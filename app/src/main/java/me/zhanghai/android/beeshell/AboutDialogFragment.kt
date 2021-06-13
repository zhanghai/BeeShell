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

import android.app.Dialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import bsh.Interpreter
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import me.zhanghai.android.beeshell.databinding.AboutDialogBinding

class AboutDialogFragment : DialogFragment() {
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog =
        MaterialAlertDialogBuilder(requireContext(), theme)
            .apply {
                val context = context
                val binding = AboutDialogBinding.inflate(context.layoutInflater)
                binding.poweredBeanShellText.text = context.getString(
                    R.string.powered_by_beanshell_format, Interpreter.VERSION
                )
                setView(binding.root)
                setPositiveButton(android.R.string.ok) { dialog, _ -> dialog.dismiss() }
                setNeutralButton(R.string.github) { _, _ ->
                    context.startActivitySafe(Intent(Intent.ACTION_VIEW, GITHUB_URI))
                }
            }
            .create()

    companion object {
        private val GITHUB_URI = Uri.parse("https://github.com/zhanghai/BeeShell")

        fun show(fragment: Fragment) {
            AboutDialogFragment().show(fragment)
        }
    }
}
