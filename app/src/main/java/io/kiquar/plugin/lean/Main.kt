package io.kiquar.plugin.lean

import androidx.annotation.Keep
import com.rk.extension.ExtensionAPI
import com.rk.extension.ExtensionContext
import com.rk.file.FileTypeManager
import io.github.rosemoe.sora.langs.textmate.registry.FileProviderRegistry
import io.github.rosemoe.sora.langs.textmate.registry.GrammarRegistry
import io.github.rosemoe.sora.langs.textmate.registry.provider.AssetsFileResolver

@Keep
@Suppress("unused")
class Main(context: ExtensionContext) : ExtensionAPI(context) {

    private var fileResolver: AssetsFileResolver? = null
    private var leanLanguage: LeanLanguage? = null

    override fun onLoad() {
        loadLanguages()
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

    private fun dispose() {
        fileResolver?.let {
            FileProviderRegistry.getInstance().removeFileProvider(it)
        }

        leanLanguage?.let {
            FileTypeManager.unregister(it)
        }
    }
}