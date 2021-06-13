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

import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import me.zhanghai.android.beeshell.databinding.ShellItemBinding

class ShellAdapter(
    private val listener: Listener
) : SimpleAdapter<ShellItem, ShellAdapter.ViewHolder>() {
    override val hasStableIds: Boolean
        get() = true

    override fun getItemId(position: Int): Long = getItem(position).id

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder =
        ViewHolder(ShellItemBinding.inflate(parent.context.layoutInflater, parent, false))

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = getItem(position)
        val binding = holder.binding
        binding.inputText.setTextAndContextMenu(item.input)
        binding.outputText.setTextAndContextMenu(item.output)
    }

    private fun TextView.setTextAndContextMenu(text: String) {
        this.text = text
        setOnCreateContextMenuListener { menu, _, _ ->
            menu.apply {
                add(R.string.copy).setOnMenuItemClickListener {
                    listener.onCopyText(text)
                    true
                }
                add(R.string.select).setOnMenuItemClickListener {
                    listener.onSelectText(text)
                    true
                }
                add(R.string.edit).setOnMenuItemClickListener {
                    listener.onEditText(text)
                    true
                }
            }
        }
    }

    interface Listener {
        fun onCopyText(text: String)
        fun onSelectText(text: String)
        fun onEditText(text: String)
    }

    class ViewHolder(val binding: ShellItemBinding) : RecyclerView.ViewHolder(binding.root)
}
