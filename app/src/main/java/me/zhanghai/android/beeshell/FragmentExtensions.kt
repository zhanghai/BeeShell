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

import android.widget.Toast
import androidx.annotation.Dimension
import androidx.fragment.app.Fragment

@Dimension
fun Fragment.dpToDimensionPixelSize(@Dimension(unit = Dimension.DP) dp: Float) =
    requireContext().dpToDimensionPixelSize(dp)

@Dimension
fun Fragment.dpToDimensionPixelSize(@Dimension(unit = Dimension.DP) dp: Int) =
    requireContext().dpToDimensionPixelSize(dp)

fun Fragment.showToast(textRes: Int, duration: Int = Toast.LENGTH_SHORT) =
    requireContext().showToast(textRes, duration)

fun Fragment.showToast(text: CharSequence, duration: Int = Toast.LENGTH_SHORT) =
    requireContext().showToast(text, duration)
