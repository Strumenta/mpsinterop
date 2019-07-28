import com.strumenta.mpsinterop.logicalmodel.Language
import com.strumenta.mpsinterop.logicalmodel.Concept
import com.strumenta.mpsinterop.logicalmodel.InterfaceConcept
import com.strumenta.mpsinterop.registries.LanguageRegistry
import java.util.*

fun injectIValidIdentifier(languageRegistry: LanguageRegistry) {
    val iNamedConcept = languageRegistry.getConcept("jetbrains.mps.lang.core.structure.INamedConcept")!!
    val baseLang = Language(UUID.fromString("00000000-0000-4000-0000-011c895902ca"),
            "jetbrains.mps.baseLanguage")
    val iValidIndentifier = InterfaceConcept(1212170275853L,
            "IValidIdentifier")
    iValidIndentifier.extended.add(iNamedConcept as InterfaceConcept)
    baseLang.add(iValidIndentifier)
    languageRegistry.add(baseLang)
}