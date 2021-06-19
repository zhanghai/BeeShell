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

import android.app.ActivityManager
import android.content.ClipData
import android.content.ClipboardManager
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isInvisible
import androidx.core.view.isVisible
import androidx.core.view.marginTop
import androidx.core.view.updateLayoutParams
import androidx.core.view.updateMarginsRelative
import androidx.core.view.updatePaddingRelative
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import dev.chrisbanes.insetter.applyInsetter
import me.zhanghai.android.beeshell.databinding.ShellFragmentBinding

class ShellFragment : Fragment(), ShellAdapter.Listener {
    private val getContentLauncher = registerForActivityResult(
        ActivityResultContracts.GetContent(), this::onGetContentResult
    )

    private val viewModel: ShellViewModel by viewModels()

    private lateinit var binding: ShellFragmentBinding

    private lateinit var adapter: ShellAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setHasOptionsMenu(true)

        lifecycleScope.launchWhenStarted {
            viewModel.items.observe(viewLifecycleOwner) { onItemsChanged(it) }
            viewModel.isExecuting.observe(viewLifecycleOwner) { onIsExecutingChanged(it) }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = ShellFragmentBinding.inflate(inflater, container, false)
        .also { binding = it }
        .root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val activity = requireActivity() as AppCompatActivity
        activity.lifecycleScope.launchWhenCreated {
            @Suppress("DEPRECATION")
            activity.setTaskDescription(
                ActivityManager.TaskDescription(
                    null, null, activity.getColorByAttr(R.attr.colorSurface)
                )
            )
            activity.window.setDecorFitsSystemWindowsCompat(false)
            activity.setSupportActionBar(binding.toolbar)

            viewModel.setActivity(activity)
        }

        binding.toolbar.applyInsetter {
            type(statusBars = true, navigationBars = true) {
                margin(left = true, top = true, right = true)
            }
        }
        adapter = ShellAdapter(this)
        adapter.registerAdapterDataObserver(object : SimpleAdapterDataObserver() {
            override fun onChanged() {
                binding.emptyLayout.isInvisible = adapter.itemCount != 0
            }
        })
        binding.recycler.apply {
            updatePaddingRelative(top = paddingTop + marginTop)
            updateLayoutParams<ViewGroup.MarginLayoutParams> { updateMarginsRelative(top = 0) }
            applyInsetter {
                type(statusBars = true, navigationBars = true) {
                    padding(left = true, top = true, right = true)
                }
            }
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(activity)
            addItemDecoration(
                DividerItemDecoration(
                    ColorDrawable(Color.TRANSPARENT), size = dpToDimensionPixelSize(16)
                )
            )
            ItemTouchHelper(
                object : ItemTouchHelper.SimpleCallback(
                    0, ItemTouchHelper.START or ItemTouchHelper.END
                ) {
                    override fun onMove(
                        recyclerView: RecyclerView,
                        viewHolder: RecyclerView.ViewHolder,
                        target: RecyclerView.ViewHolder
                    ): Boolean = false

                    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                        onRemoveItem(viewHolder.bindingAdapterPosition)
                    }
                }
            ).attachToRecyclerView(this)
            adapter = this@ShellFragment.adapter
            // Cannot just use an on scroll listener due to RecyclerView has animations.
            viewTreeObserver.addOnPreDrawListener {
                onItemsPreDraw()
                true
            }
        }
        val itemAnimator = binding.recycler.itemAnimator!!
        binding.emptyLayout.layoutTransition.setDuration(
            (itemAnimator.addDuration + itemAnimator.removeDuration) / 2
        )
        binding.bottomLayout.applyInsetter {
            type(statusBars = true, navigationBars = true) {
                padding(left = true, right = true, bottom = true)
            }
            type(ime = true) {
                padding(left = true, right = true, bottom = true, animated = true)
            }
            syncTranslationTo(binding.syncTranslationView)
        }
        binding.inputEdit.setOnKeyListener { _, keyCode, event ->
            if (event.action == KeyEvent.ACTION_DOWN && (keyCode == KeyEvent.KEYCODE_DPAD_CENTER
                        || keyCode == KeyEvent.KEYCODE_ENTER
                        || keyCode == KeyEvent.KEYCODE_NUMPAD_ENTER) && event.isCtrlPressed) {
                onExecute()
                true
            } else {
                false
            }
        }
        binding.loadButton.setOnClickListener { onLoad() }
        binding.interruptButton.setOnClickListener { onInterrupt() }
        binding.executeButton.setOnClickListener { onExecute() }
    }

    override fun onDetach() {
        super.onDetach()

        viewModel.setActivity(null)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)

        inflater.inflate(R.menu.shell, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean =
        when (item.itemId) {
            R.id.action_clear -> {
                onClear()
                true
            }
            R.id.action_about -> {
                onAbout()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }

    private fun onClear() {
        viewModel.clear()
    }

    private fun onAbout() {
        AboutDialogFragment.show(this)
    }

    // This method is called on every pre-draw, so avoid any allocations/heavy work here!
    private fun onItemsPreDraw() {
        val layoutManager = binding.recycler.layoutManager!!
        val firstItemView = layoutManager.findViewByPosition(0)
        binding.toolbar.elevation = if (
            if (firstItemView != null) {
                firstItemView.y < binding.recycler.paddingTop
            } else {
                layoutManager.childCount != 0
            }
        ) -1f else 0f
        binding.toolbar.isInvisible = if (firstItemView != null) {
            firstItemView.y <= binding.toolbar.top
        } else {
            layoutManager.childCount != 0
        }
    }

    private fun onItemsChanged(items: List<ShellItem>) {
        adapter.replace(items)
        if (items.isNotEmpty()) {
            binding.recycler.scrollToPosition(items.size - 1)
        }
    }

    private fun onRemoveItem(index: Int) {
        viewModel.removeAt(index)
    }

    override fun onCopyText(text: String) {
        val clipboardManager = requireActivity()
            .getSystemServiceCompat(ClipboardManager::class.java)
        clipboardManager.setPrimaryClip(ClipData.newPlainText(null, text))
    }

    override fun onSelectText(text: String) {
        SelectTextDialogFragment.show(text, this)
    }

    override fun onEditText(text: String) {
        binding.inputEdit.apply {
            setText(text)
            setSelection(text.length)
        }
    }

    private fun onLoad() {
        if (viewModel.isExecuting.valueCompat) {
            return
        }
        getContentLauncher.launch("*/*")
    }

    private fun onGetContentResult(uri: Uri?) {
        uri ?: return
        val text = try {
            val inputStream = requireActivity().contentResolver.openInputStream(uri) ?: return
            inputStream.use { it.bufferedReader().readText() }
        } catch (e: Exception) {
            showToast(e.toString())
            return
        }
        binding.inputEdit.apply {
            setText(text)
            setSelection(text.length)
        }
    }

    private fun onExecute() {
        if (viewModel.isExecuting.valueCompat) {
            return
        }
        val input = binding.inputEdit.text.toString()
        if (input.isBlank()) {
            binding.inputEdit.text = null
            return
        }
        viewModel.execute(input)
    }

    private fun onIsExecutingChanged(isExecuting: Boolean) {
        binding.inputEdit.apply {
            isEditable = !isExecuting
            if (!isExecuting) {
                text = null
            }
        }
        binding.progress.apply {
            isInvisible = true
            isIndeterminate = isExecuting
            isVisible = true
        }
        binding.loadButton.isVisible = !isExecuting
        binding.interruptButton.isVisible = isExecuting
        binding.executeButton.setText(if (isExecuting) R.string.executing else R.string.execute)
    }

    private fun onInterrupt() {
        viewModel.interrupt()
    }

    fun onBackPressed(): Boolean {
        // TODO
        return false
    }
}
