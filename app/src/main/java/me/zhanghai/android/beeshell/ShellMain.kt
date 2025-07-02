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
import android.content.Context
import android.os.Looper
import bsh.Interpreter
import me.zhanghai.android.linenoise.Linenoise
import java.io.File
import kotlin.system.exitProcess

object ShellMain {
    @JvmStatic
    @SuppressLint("UnsafeDynamicallyLoadedCode")
    fun main(args: Array<String>) {
        Thread.setDefaultUncaughtExceptionHandler { _, e ->
            e.printStackTrace()
            exitProcess(1)
        }

        @Suppress("DEPRECATION") Looper.prepareMainLooper()
        @SuppressLint("PrivateApi")
        val activityThreadClass = Class.forName("android.app.ActivityThread")
        val activityThreadSystemMainMethod = activityThreadClass.getMethod("systemMain")
        val activityThread = activityThreadSystemMainMethod.invoke(null)
        val activityThreadGetSystemContextMethod = activityThreadClass.getMethod("getSystemContext")
        val systemContext = activityThreadGetSystemContextMethod.invoke(activityThread) as Context
        val shellContext = systemContext.createPackageContext("com.android.shell", 0)
        @SuppressLint("PrivateApi") val contextImplClass = Class.forName("android.app.ContextImpl")
        @SuppressLint("DiscouragedPrivateApi")
        val contextImplPackageInfoField =
            contextImplClass.getDeclaredField("mPackageInfo").apply { isAccessible = true }
        val loadedApk = contextImplPackageInfoField.get(shellContext)
        @SuppressLint("PrivateApi") val loadedApkClass = Class.forName("android.app.LoadedApk")
        val contextImplCreateAppContextMethod =
            contextImplClass
                .getDeclaredMethod("createAppContext", activityThreadClass, loadedApkClass)
                .apply { isAccessible = true }
        val context =
            contextImplCreateAppContextMethod.invoke(null, activityThread, loadedApk) as Context
        @SuppressLint("PrivateApi")
        val ddmHandleAppNameClass = Class.forName("android.ddm.DdmHandleAppName")
        val ddmHandleAppNameSetAppName =
            ddmHandleAppNameClass.getMethod("setAppName", String::class.java, Int::class.java)
        ddmHandleAppNameSetAppName.invoke(null, "${BuildConfig.APPLICATION_ID}:shell", 0)

        val interpreter =
            Interpreter().apply {
                set("systemContext", systemContext)
                set("context", context)
            }
        val isInteractive = args.isEmpty() && System.console() != null
        if (isInteractive) {
            val packageManager = context.packageManager
            @Suppress("DEPRECATION")
            val applicationInfo = packageManager.getApplicationInfo(BuildConfig.APPLICATION_ID, 0)
            val libraryDirectory = applicationInfo.nativeLibraryDir
            val linenoiseLibraryPath =
                File(libraryDirectory).resolve("lib${Linenoise.getLibraryName()}.so").path
            System.load(linenoiseLibraryPath)

            println(
                """
                |  Welcome to BeeShell ${BuildConfig.VERSION_NAME}
                |  Powered by BeanShell ${Interpreter.VERSION}
                """
                    .trimIndent()
            )
            val interactiveInterpreter = InteractiveInterpreter(interpreter)

            Linenoise.setMultiLine(true)
            while (true) {
                val input = Linenoise.readLine("bsh> ") ?: break
                if (input.isBlank()) {
                    continue
                }
                val output = interactiveInterpreter.execute(input)
                println(output)
                Linenoise.addHistory(input)
            }
        } else {
            val fileName = args.getOrNull(0)
            val inputStream = if (fileName != null) File(fileName).inputStream() else System.`in`
            inputStream.bufferedReader().use {
                interpreter.eval(it, interpreter.nameSpace, fileName ?: "<stdin>")
            }
        }

        exitProcess(0)
    }
}
