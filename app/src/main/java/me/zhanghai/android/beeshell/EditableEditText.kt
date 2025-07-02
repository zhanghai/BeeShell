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
import android.text.InputType
import android.util.AttributeSet
import androidx.annotation.AttrRes
import androidx.appcompat.widget.AppCompatEditText

class EditableEditText : AppCompatEditText {
    private var editableInputType =
        inputType.let { if (it != InputType.TYPE_NULL) it else InputType.TYPE_CLASS_TEXT }

    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)

    constructor(
        context: Context,
        attrs: AttributeSet?,
        @AttrRes defStyleAttr: Int,
    ) : super(context, attrs, defStyleAttr)

    var isEditable: Boolean
        get() = keyListener != null && inputType != InputType.TYPE_NULL
        set(value) {
            if (isEditable == value) {
                return
            }
            if (value) {
                inputType = editableInputType
            } else {
                editableInputType = inputType
                keyListener = null
            }
        }
}
