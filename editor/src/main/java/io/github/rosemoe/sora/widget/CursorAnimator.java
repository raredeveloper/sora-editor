/*
 *    CodeEditor - the awesome code editor for Android
 *    Copyright (C) 2020-2021  Rosemoe
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
 */
package io.github.rosemoe.sora.widget;

import android.animation.ValueAnimator;

class CursorAnimator implements ValueAnimator.AnimatorUpdateListener {

    ValueAnimator animatorX;
    ValueAnimator animatorY;
    private final CodeEditor editor;
    private float startX, startY;

    public CursorAnimator(CodeEditor editor) {
        this.editor = editor;
        animatorX = new ValueAnimator();
        animatorY = new ValueAnimator();
    }

    public void markStartPos() {
        float[] pos = editor.mLayout.getCharLayoutOffset(editor.getCursor().getLeftLine(), editor.getCursor().getLeftColumn());
        startX = editor.measureTextRegionOffset() + pos[1];
        startY = pos[0];
    }

    public boolean isRunning() {
        return animatorX.isRunning() || animatorY.isRunning();
    }

    public void cancel() {
        animatorX.cancel();
        animatorY.cancel();
    }

    public void markEndPosAndStart() {
        cancel();
        animatorX.removeAllUpdateListeners();
        animatorY.removeAllUpdateListeners();
        float[] pos = editor.mLayout.getCharLayoutOffset(editor.getCursor().getLeftLine(), editor.getCursor().getLeftColumn());
        animatorX = ValueAnimator.ofFloat(startX, (pos[1] + editor.measureTextRegionOffset()));
        animatorY = ValueAnimator.ofFloat(startY, pos[0]);
        animatorX.addUpdateListener(this);
        animatorX.setDuration(120);
        animatorY.setDuration(120);
        animatorX.start();
        animatorY.start();
    }

    @Override
    public void onAnimationUpdate(ValueAnimator animation) {
        editor.postInvalidate();
    }
}
