package io.kiquar.plugin.lean

import android.content.res.Resources
import com.rk.file.FileType
import com.rk.icons.Icon

class LeanLanguage(resources: Resources) : FileType {
    override val extensions = listOf("lean")
    override val textmateScope = "source.lean"
    override val name = "lean"
    override val title = "Lean"
    override val icon = null
}
