/*
 * Copyright 2020 Google LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package me.zhanghai.android.beeshell

import android.graphics.Canvas
import android.graphics.Rect
import android.graphics.drawable.Drawable
import android.view.View
import androidx.core.view.forEach
import androidx.recyclerview.widget.RecyclerView
import kotlin.math.roundToInt

class DividerItemDecoration(
    var drawable: Drawable,
    var orientation: Int = VERTICAL,
    var size: Int =
        if (orientation == VERTICAL) drawable.intrinsicHeight else drawable.intrinsicWidth
) : RecyclerView.ItemDecoration() {
    private val bounds = Rect()

    override fun onDraw(canvas: Canvas, parent: RecyclerView, state: RecyclerView.State) {
        if (orientation == VERTICAL) {
            drawVertical(canvas, parent)
        } else {
            drawHorizontal(canvas, parent)
        }
    }

    private fun drawVertical(canvas: Canvas, parent: RecyclerView) {
        val left: Int
        val right: Int
        if (parent.clipToPadding) {
            left = parent.paddingLeft
            right = parent.width - parent.paddingRight
        } else {
            left = 0
            right = parent.width
        }
        parent.forEach { child ->
            if (parent.isLastItemView(child)) {
                return@forEach
            }
            parent.getDecoratedBoundsWithMargins(child, bounds)
            val bottom = bounds.bottom + child.translationY.roundToInt()
            val top = bottom - size
            drawable.setBounds(left, top, right, bottom)
            drawable.draw(canvas)
        }
    }

    private fun drawHorizontal(canvas: Canvas, parent: RecyclerView) {
        val top: Int
        val bottom: Int
        if (parent.clipToPadding) {
            top = parent.paddingTop
            bottom = parent.height - parent.paddingBottom
        } else {
            top = 0
            bottom = parent.height
        }
        parent.forEach { child ->
            if (parent.isLastItemView(child)) {
                return@forEach
            }
            parent.getDecoratedBoundsWithMargins(child, bounds)
            val right = bounds.right + child.translationX.roundToInt()
            val left = right - size
            drawable.setBounds(left, top, right, bottom)
            drawable.draw(canvas)
        }
    }

    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        if (parent.isLastItemView(view)) {
            outRect.set(0, 0, 0, 0)
            return
        }
        if (orientation == VERTICAL) {
            outRect.set(0, 0, 0, size)
        } else {
            outRect.set(0, 0, size, 0)
        }
    }

    private fun RecyclerView.isLastItemView(view: View): Boolean {
        val position = getChildAdapterPosition(view)
        if (position == RecyclerView.NO_POSITION) {
            return false
        }
        val adapter = adapter ?: return false
        return position == adapter.itemCount - 1
    }

    companion object {
        const val HORIZONTAL = RecyclerView.HORIZONTAL
        const val VERTICAL = RecyclerView.VERTICAL
    }
}
