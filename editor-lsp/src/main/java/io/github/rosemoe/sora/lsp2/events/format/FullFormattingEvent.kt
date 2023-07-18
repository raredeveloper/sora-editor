/*******************************************************************************
 *    sora-editor - the awesome code editor for Android
 *    https://github.com/Rosemoe/sora-editor
 *    Copyright (C) 2020-2023  Rosemoe
 *
 *     This library is free software; you can redistribute it and/or
 *     modify it under the terms of the GNU Lesser General Public
 *     License as published by the Free Software Foundation; either
 *     version 2.1 of the License, or (at your option) any later version.
 *
 *     This library is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *     Lesser General Public License for more details.
 *
 *     You should have received a copy of the GNU Lesser General Public
 *     License along with this library; if not, write to the Free Software
 *     Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301
 *     USA
 *
 *     Please contact Rosemoe by email 2073412493@qq.com if you need
 *     additional information or have any questions
 ******************************************************************************/

package io.github.rosemoe.sora.lsp2.events.format

import io.github.rosemoe.sora.lsp2.editor.LspEditor
import io.github.rosemoe.sora.lsp2.editor.getOption
import io.github.rosemoe.sora.lsp2.events.AsyncEventListener
import io.github.rosemoe.sora.lsp2.events.EventContext
import io.github.rosemoe.sora.lsp2.events.EventListener
import io.github.rosemoe.sora.lsp2.events.EventType
import io.github.rosemoe.sora.lsp2.events.document.applyEdits
import io.github.rosemoe.sora.lsp2.events.getByClass
import io.github.rosemoe.sora.lsp2.requests.Timeout
import io.github.rosemoe.sora.lsp2.requests.Timeouts
import io.github.rosemoe.sora.lsp2.utils.LSPException
import io.github.rosemoe.sora.lsp2.utils.createTextDocumentIdentifier
import io.github.rosemoe.sora.text.Content
import kotlinx.coroutines.future.await
import kotlinx.coroutines.withTimeout
import org.eclipse.lsp4j.DocumentFormattingParams
import org.eclipse.lsp4j.FormattingOptions


class FullFormattingEvent : AsyncEventListener() {
    override val eventName = "textDocument/formatting"

    override suspend fun handleAsync(context: EventContext) {
        val editor = context.get<LspEditor>("lsp-editor")

        val content = context.getByClass<Content>() ?: return

        val requestManager = editor.requestManager ?: return

        val formattingParams = DocumentFormattingParams()

        formattingParams.options = editor.eventManager.getOption<FormattingOptions>()

        formattingParams.textDocument =
            editor.uri.createTextDocumentIdentifier()

        val formattingFuture = requestManager.formatting(formattingParams) ?: return

        try {
            withTimeout(Timeout[Timeouts.FORMATTING].toLong()) {
                val textEditList = formattingFuture.await() ?: listOf()

                editor.eventManager.emit(EventType.applyEdits) {
                    put("edits", textEditList)
                    put("content", content)
                }
            }

        } catch (exception: Exception) {
            throw LSPException("Formatting code timeout", exception)
        }
    }

}

val EventType.fullFormatting: String
    get() = "textDocument/formatting"