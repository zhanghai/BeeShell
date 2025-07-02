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

import bsh.EvalError
import bsh.Interpreter
import bsh.ParseException
import bsh.TargetError

class InteractiveInterpreter(val interpreter: Interpreter) {
    init {
        interpreter.nameSpace.apply {
            importPackage("android.app")
            importPackage("android.content")
            importPackage("android.content.pm")
            importPackage("android.content.res")
            importPackage("android.graphics")
            importPackage("android.graphics.drawable")
            importPackage("android.net")
            importPackage("android.os")
            importPackage("android.provider")
            importPackage("android.system")
            importPackage("android.util")
            importPackage("android.view")
            importPackage("android.widget")
            importPackage("java.lang.reflect")
            importPackage("java.nio")
            importPackage("java.nio.charset")
            importPackage("java.nio.file")
            importPackage("java.nio.file.attribute")
            importPackage("java.time")
            importPackage("java.time.format")
            importPackage("java.util.concurrent")
            importPackage("java.util.function")
            importPackage("java.util.regex")
        }
    }

    private var outputIndex = 0

    fun execute(input: String): String {
        ++outputIndex
        val outputValue: Any? =
            try {
                interpreter.eval(input)
            } catch (e: ParseException) {
                return e.message!!.trim()
            } catch (e: TargetError) {
                return e.printedStackTrace
            } catch (e: EvalError) {
                return e.message!!.trim()
            }
        val outputName = "\$$outputIndex"
        interpreter.set(outputName, outputValue)
        @Suppress("PLATFORM_CLASS_MAPPED_TO_KOTLIN")
        val outputText =
            when (outputValue) {
                is Array<*> -> outputValue.contentDeepToString()
                is Character -> "'$outputValue'"
                is String -> "\"$outputValue\""
                else -> outputValue.toString()
            }
        return "$outputName = $outputText"
    }
}
