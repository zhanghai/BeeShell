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

import android.content.Context
import android.util.AttributeSet
import android.widget.LinearLayout
import androidx.annotation.AttrRes
import androidx.annotation.StyleRes
import com.google.android.material.shape.MaterialShapeDrawable
import com.google.android.material.shape.MaterialShapeUtils
import com.google.android.material.shape.ShapeAppearanceModel
import com.google.android.material.shape.Shapeable
import com.google.android.material.theme.overlay.MaterialThemeOverlay

class MaterialCardLinearLayout @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    @AttrRes defStyleAttr: Int = 0,
    @StyleRes defStyleRes: Int = 0
) : LinearLayout(
    MaterialThemeOverlay.wrap(context, attrs, defStyleAttr, defStyleRes), attrs, defStyleAttr,
    defStyleRes
), Shapeable {
    private val materialShapeDrawable: MaterialShapeDrawable

    init {
        @Suppress("NAME_SHADOWING")
        val context = getContext()
        materialShapeDrawable = MaterialShapeDrawable.createWithElevationOverlay(context, elevation)
            .apply {
                shapeAppearanceModel =
                    ShapeAppearanceModel.builder(context, attrs, defStyleAttr, defStyleRes).build()
            }
        background = materialShapeDrawable
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()

        MaterialShapeUtils.setParentAbsoluteElevation(this)
    }

    override fun setElevation(elevation: Float) {
        super.setElevation(elevation)

        MaterialShapeUtils.setElevation(this, elevation)
    }

    override fun getShapeAppearanceModel(): ShapeAppearanceModel =
        materialShapeDrawable.shapeAppearanceModel

    override fun setShapeAppearanceModel(shapeAppearanceModel: ShapeAppearanceModel) {
        materialShapeDrawable.shapeAppearanceModel = shapeAppearanceModel
    }
}
