package io.kiquar.plugin.lean

import androidx.annotation.Keep
import com.rk.extension.ExtensionAPI
import com.rk.extension.ExtensionContext
import com.rk.file.FileTypeManager
import com.rk.lsp.LspRegistry
import com.rk.utils.getTempDir
import com.rk.file.child
import io.github.rosemoe.sora.langs.textmate.registry.FileProviderRegistry
import io.github.rosemoe.sora.langs.textmate.registry.GrammarRegistry
import io.github.rosemoe.sora.langs.textmate.registry.provider.AssetsFileResolver
import java.io.File

@Keep
@Suppress("unused")
class Main(context: ExtensionContext) : ExtensionAPI(context) {

    private var fileResolver: AssetsFileResolver? = null
    private var leanLanguage: LeanLanguage? = null
    private var leanServer: LeanServer? = null

    override fun onLoad() {
        loadLanguages()
        loadLsp()
    }

    override fun onDispose() {
        dispose()
    }

    private fun loadLanguages() {
        val fileProviderRegistry = FileProviderRegistry.getInstance()
        fileResolver = AssetsFileResolver(context.assets)
        fileProviderRegistry.addFileProvider(fileResolver)

        val grammarRegistry = GrammarRegistry.getInstance()
        grammarRegistry.loadGrammars("languages.json")

        leanLanguage = LeanLanguage(context.resources).also {
            FileTypeManager.register(it)
        }
    }

    private fun loadLsp() {
        leanServer = LeanServer(
            icon = leanLanguage?.icon,
            installScript = acquireLspInstallScript()
        ).also {
            LspRegistry.registerServer(it)
        }
    }

    private fun acquireLspInstallScript(): File {
        val stream = context.assets.open("lean-lsp-install.sh")
        val content = stream.bufferedReader().use { it.readText() }
        return getTempDir().child("lean-lsp-install.sh").also {
            it.writeText(content)
        }
    }

    private fun dispose() {
        fileResolver?.let {
            FileProviderRegistry.getInstance().removeFileProvider(it)
        }

        leanLanguage?.let {
            FileTypeManager.unregister(it)
        }

        leanServer?.let {
            LspRegistry.unregisterServer(it)
        }
    }
}