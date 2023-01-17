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

import android.annotation.SuppressLint
import androidx.recyclerview.widget.RecyclerView

abstract class SimpleAdapter<T, VH : RecyclerView.ViewHolder> : RecyclerView.Adapter<VH>() {
    private val _list = mutableListOf<T>()
    val list: List<T>
        get() = _list

    protected abstract val hasStableIds: Boolean

    init {
        setHasStableIds(hasStableIds)
    }

    fun addAll(collection: Collection<T>) {
        val oldSize = _list.size
        _list.addAll(collection)
        notifyItemRangeInserted(oldSize, collection.size)
    }

    @SuppressLint("NotifyDataSetChanged")
    fun replace(collection: Collection<T>) {
        _list.clear()
        _list.addAll(collection)
        notifyDataSetChanged()
    }

    fun add(position: Int, item: T) {
        _list.add(position, item)
        notifyItemInserted(position)
    }

    fun add(item: T) {
        add(_list.size, item)
    }

    operator fun set(position: Int, item: T) {
        _list[position] = item
        notifyItemChanged(position)
    }

    fun remove(position: Int): T {
        val item = _list.removeAt(position)
        notifyItemRemoved(position)
        return item
    }

    fun clear() {
        val oldSize = _list.size
        _list.clear()
        notifyItemRangeRemoved(0, oldSize)
    }

    fun findPositionById(id: Long): Int {
        val count = itemCount
        for (index in 0 until count) {
            if (getItemId(index) == id) {
                return index
            }
        }
        return RecyclerView.NO_POSITION
    }

    fun notifyItemChangedById(id: Long) {
        val position = findPositionById(id)
        if (position != RecyclerView.NO_POSITION) {
            notifyItemChanged(position)
        }
    }

    fun removeById(id: Long): T? {
        val position = findPositionById(id)
        return if (position != RecyclerView.NO_POSITION) {
            remove(position)
        } else {
            null
        }
    }

    fun getItem(position: Int): T {
        return _list[position]
    }

    override fun getItemCount(): Int {
        return _list.size
    }
}
