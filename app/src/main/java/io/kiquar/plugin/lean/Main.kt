package io.kiquar.plugin.lean

import androidx.annotation.Keep
import com.rk.extension.ExtensionAPI
import com.rk.extension.ExtensionContext
import com.rk.file.FileTypeManager
import com.rk.file.child
import com.rk.lsp.LspRegistry
import com.rk.utils.getTempDir
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

        val lang = LeanLanguage(context.resources)
        leanLanguage = lang
        FileTypeManager.register(lang)
    }

    private fun loadLsp() {
        val lang = leanLanguage
        if (lang == null) return

        val server = LeanServer(
            icon = lang.icon,
            installScript = acquireLspInstallScript()
        )
        leanServer = server
        LspRegistry.registerServer(server)
    }

    private fun acquireLspInstallScript(): File {
        val stream = context.assets.open("lean-lsp-install.sh")
        val content = stream.bufferedReader().use { it.readText() }
        val script = getTempDir().child("lean-lsp-install.sh")
        script.writeText(content)
        return script
    }

    private fun dispose() {
        val resolver = fileResolver
        if (resolver != null) {
            FileProviderRegistry.getInstance().removeFileProvider(resolver)
        }

        val lang = leanLanguage
        if (lang != null) {
            FileTypeManager.unregister(lang)
        }

        val server = leanServer
        if (server != null) {
            LspRegistry.unregisterServer(server)
        }
    }
}