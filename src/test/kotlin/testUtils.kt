import com.strumenta.mpsinterop.logicalmodel.Language
import com.strumenta.mpsinterop.logicalmodel.SConcept
import com.strumenta.mpsinterop.logicalmodel.SConceptId
import com.strumenta.mpsinterop.registries.LanguageRegistry
import java.util.*

fun injectIValidIdentifier(languageRegistry: LanguageRegistry) {
    val iNamedConcept = languageRegistry.getConcept("jetbrains.mps.lang.core.structure.INamedConcept")!!
    val baseLang = Language(UUID.fromString("00000000-0000-4000-0000-011c895902ca"),
            "jetbrains.mps.baseLanguage")
    val iValidIndentifier = SConcept(1212170275853L,
            "IValidIdentifier",
            isInterface = true)
    iValidIndentifier.extended = iNamedConcept
    baseLang.add(iValidIndentifier)
    languageRegistry.add(baseLang)
}