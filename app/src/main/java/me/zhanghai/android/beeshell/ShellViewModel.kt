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

import android.app.Activity
import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import bsh.Interpreter
import java.io.ByteArrayOutputStream
import java.io.PrintStream
import java.nio.charset.StandardCharsets
import java.util.concurrent.Executors
import java.util.concurrent.Future

class ShellViewModel(application: Application) : AndroidViewModel(application) {
    private val interpreterOutputStream = ByteArrayOutputStream()
    private val interpreterPrintStream =
        PrintStream(interpreterOutputStream, false, StandardCharsets.UTF_8.name())
    private val interpreter =
        InteractiveInterpreter(
            Interpreter().apply {
                out = interpreterPrintStream
                err = interpreterPrintStream
                set("context", application)
            }
        )

    private val interpreterExecutor = Executors.newFixedThreadPool(1)
    private var interpreterFuture: Future<*>? = null

    private val _items = MutableLiveData(emptyList<ShellItem>())
    val items: LiveData<List<ShellItem>> = _items

    private val _isExecuting = MutableLiveData(false)
    val isExecuting: LiveData<Boolean> = _isExecuting

    fun setActivity(activity: Activity?) {
        interpreter.interpreter.apply {
            val oldActivity = get("activity")
            set("activity", activity)
            if (activity == null) {
                for (variableName in nameSpace.variableNames) {
                    if (get(variableName) == oldActivity) {
                        set(variableName, null)
                    }
                }
            }
        }
    }

    fun clearItems() {
        _items.value = emptyList()
    }

    fun removeItemAt(index: Int) {
        _items.value = _items.valueCompat.filterIndexed { it, _ -> it != index }
    }

    fun execute(input: String) {
        if (isExecuting.valueCompat) {
            return
        }
        _isExecuting.value = true
        interpreterFuture =
            interpreterExecutor.submit {
                val interpreterOutput = interpreter.execute(input)
                interpreterPrintStream.flush()
                val printedOutput = interpreterOutputStream.toString(StandardCharsets.UTF_8.name())
                interpreterOutputStream.reset()
                val output =
                    if (printedOutput.isNotEmpty()) {
                        "$printedOutput\n$interpreterOutput"
                    } else {
                        interpreterOutput
                    }
                getApplication<Application>().mainExecutorCompat.execute {
                    interpreterFuture = null
                    _items.value = _items.valueCompat + ShellItem(input, output)
                    _isExecuting.value = false
                }
            }
    }

    fun interrupt() {
        interpreterFuture?.cancel(true)
    }
}
