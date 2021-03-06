package org.cqfn.diktat.ruleset.chapter1

import org.cqfn.diktat.common.config.rules.RulesConfig
import org.cqfn.diktat.ruleset.constants.Warnings.*
import org.cqfn.diktat.ruleset.rules.DIKTAT_RULE_SET_ID
import org.cqfn.diktat.ruleset.rules.PackageNaming
import org.cqfn.diktat.util.LintTestBase
import org.cqfn.diktat.util.testFileName

import com.pinterest.ktlint.core.LintError
import generated.WarningNames
import org.junit.jupiter.api.Tag
import org.junit.jupiter.api.Test

class PackageNamingWarnTest : LintTestBase(::PackageNaming) {
    private val ruleId: String = "$DIKTAT_RULE_SET_ID:package-naming"
    private val rulesConfigList: List<RulesConfig> = listOf(
            RulesConfig("DIKTAT_COMMON", true, mapOf("domainName" to "org.cqfn.diktat"))
    )

    @Test
    @Tag(WarningNames.PACKAGE_NAME_MISSING)
    fun `missing package name (check)`() {
        lintMethod(
                """
                import org.cqfn.diktat.a.b.c

                /**
                 * testComment
                 */
                class TestPackageName {  }

            """.trimIndent(),
                LintError(1, 1, ruleId, "${PACKAGE_NAME_MISSING.warnText()} $testFileName", true),
                rulesConfigList = rulesConfigList
        )
    }

    @Test
    @Tag(WarningNames.PACKAGE_NAME_INCORRECT_CASE)
    fun `package name should be in a lower case (check)`() {
        lintMethod(

                """
                package /* AAA */ org.cqfn.diktat.SPECIALTEST.test

                import org.cqfn.diktat.a.b.c

                /**
                 * testComment
                 */
                class TestPackageName {  }

            """.trimIndent(),
                LintError(1, 35, ruleId, "${PACKAGE_NAME_INCORRECT_CASE.warnText()} SPECIALTEST", true),
                rulesConfigList = rulesConfigList
        )
    }

    @Test
    @Tag(WarningNames.PACKAGE_NAME_INCORRECT_PREFIX)
    fun `package name should start from domain name (check)`() {
        lintMethod(

                """
                package some.incorrect.domain.test

                import org.cqfn.diktat.a.b.c

                /**
                 * testComment
                 */
                class TestPackageName {  }

            """.trimIndent(),
                LintError(1, 9, ruleId, "${PACKAGE_NAME_INCORRECT_PREFIX.warnText()} org.cqfn.diktat", true),
                rulesConfigList = rulesConfigList
        )
    }

    @Test
    @Tag(WarningNames.INCORRECT_PACKAGE_SEPARATOR)
    fun `underscore exceptions - incorrect underscore case`() {
        lintMethod(

                """
                package org.cqfn.diktat.domain.test_

                import org.cqfn.diktat.a.b.c

                /**
                 * testComment
                 */
                class TestPackageName {  }

            """.trimIndent(),
                LintError(1, 32, ruleId, "${INCORRECT_PACKAGE_SEPARATOR.warnText()} test_", true),
                rulesConfigList = rulesConfigList
        )
    }

    @Test
    @Tag(WarningNames.PACKAGE_NAME_INCORRECT_SYMBOLS)
    fun `incorrect symbol in package name`() {
        lintMethod(

                """
                package org.cqfn.diktat.domain.testш

                import org.cqfn.diktat.a.b.c

                /**
                 * testComment
                 */
                class TestPackageName {  }

            """.trimIndent(),
                LintError(1, 32, ruleId, "${PACKAGE_NAME_INCORRECT_SYMBOLS.warnText()} testш"),
                rulesConfigList = rulesConfigList
        )
    }

    @Test
    @Tag(WarningNames.PACKAGE_NAME_INCORRECT_SYMBOLS)
    fun `underscore exceptions - positive case - keyword`() {
        lintMethod(

                """
                package org.cqfn.diktat.domain.int_

                import org.cqfn.diktat.a.b.c

                /**
                 * testComment
                 */
                class TestPackageName {  }

            """.trimIndent(),
                rulesConfigList = rulesConfigList
        )
    }

    @Test
    @Tag(WarningNames.PACKAGE_NAME_INCORRECT_PATH)
    fun `regression - incorrect warning on file under test directory`() {
        lintMethod(
                """
                    package org.cqfn.diktat.ruleset.chapter1
                """.trimIndent(),
                fileName = "~/diktat/diktat-rules/src/test/kotlin/org/cqfn/diktat/ruleset/chapter1/EnumValueCaseTest.kt",
                rulesConfigList = rulesConfigList
        )

        lintMethod(
                """
                    package org.cqfn.diktat.chapter1
                """.trimIndent(),
                LintError(1, 9, ruleId, "${PACKAGE_NAME_INCORRECT_PATH.warnText()} org.cqfn.diktat.ruleset.chapter1", true),
                fileName = "~/diktat/diktat-rules/src/test/kotlin/org/cqfn/diktat/ruleset/chapter1/EnumValueCaseTest.kt",
                rulesConfigList = rulesConfigList
        )
    }

    @Test
    @Tag(WarningNames.PACKAGE_NAME_INCORRECT_PATH)
    fun `regression - should not remove special words from file path`() {
        lintMethod(
                """
                    |package org.cqfn.diktat.test.processing
                """.trimMargin(),
                fileName = "/home/testu/project/module/src/test/kotlin/org/cqfn/diktat/test/processing/SpecialPackageNaming.kt",
                rulesConfigList = rulesConfigList
        )

        lintMethod(
                """
                    |package kotlin.collections
                """.trimMargin(),
                fileName = "/home/testu/project/module/src/main/kotlin/kotlin/collections/Collections.kt",
                rulesConfigList = listOf(
                        RulesConfig("DIKTAT_COMMON", true, mapOf("domainName" to "kotlin"))
                )
        )
    }
}
