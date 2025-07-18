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
import android.view.View

class SyncTranslationView
@JvmOverloads
constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) :
    View(context, attrs, defStyleAttr) {
    override fun setTranslationX(translationX: Float) {
        super.setTranslationX(translationX)

        (parent as View).translationX = translationX / 2
    }

    override fun setTranslationY(translationY: Float) {
        super.setTranslationY(translationY)

        (parent as View).translationY = translationY / 2
    }
}
