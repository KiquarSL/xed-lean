package io.kiquar.plugin.lean

import android.app.Activity
import android.content.Context
import com.rk.exec.isTerminalInstalled
import com.rk.file.child
import com.rk.file.sandboxHomeDir
import com.rk.icons.Icon
import com.rk.file.BuiltinFileType
import com.rk.lsp.LspConnectionConfig
import com.rk.lsp.ScriptedLspServer
import java.io.File

class LeanServer(
    override val icon: Icon? = null,
    override val supportedExtensions: List<String> = listOf("lean"),
    override val installScript: File
) : ScriptedLspServer() {

    override val id = "lean"
    override val languageName = "Lean"
    override val serverName = "lean-lsp"
    override val installId = "Lean and Lean LSP"

    private val latestVersion = "4.34.1"

    override suspend fun isInstalled(context: Context): Boolean {
        if (!isTerminalInstalled()) return false
        return sandboxHomeDir().child(".elan/bin/lean").exists()
    }

    override suspend fun hasUpdate(context: Context): Boolean {
        return isUpdatable(context)
    }

    override fun install(activity: Activity) {
        launchInstaller(activity, latestVersion)
    }

    override fun uninstall(activity: Activity) {
        launchInstaller(activity, "--uninstall", latestVersion)
    }

    override fun update(activity: Activity) {
        launchInstaller(activity, "--update", latestVersion)
    }

    override suspend fun isUpdatable(context: Context): Boolean {
        val versionFile = sandboxHomeDir().child(".elan/lean_version.txt")
        val currentVersion = runCatching { versionFile.readText().trim() }.getOrNull()
            ?: return false
        return currentVersion != latestVersion
    }

    override fun getConnectionConfig(): LspConnectionConfig {
        return LspConnectionConfig.Process(arrayOf(
            sandboxHomeDir().child(".elan/bin/lean").absolutePath,
            "--server"
        ))
    }
}