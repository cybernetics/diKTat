package org.cqfn.diktat.ruleset.chapter6

import com.pinterest.ktlint.core.LintError
import generated.WarningNames
import org.cqfn.diktat.ruleset.constants.Warnings
import org.cqfn.diktat.ruleset.rules.DIKTAT_RULE_SET_ID
import org.cqfn.diktat.ruleset.rules.ImplicitBackingPropertyRule
import org.cqfn.diktat.util.LintTestBase
import org.junit.jupiter.api.Tag
import org.junit.jupiter.api.Test

class ImplicitBackingPropertyWarnTest: LintTestBase(::ImplicitBackingPropertyRule) {
    private val ruleId = "$DIKTAT_RULE_SET_ID:implicit-backing-property"

    @Test
    @Tag("")
    fun `not trigger on backing property`() {
        lintMethod(
                """
                    |class Some(val a: Int = 5) {
                    |   private var _table: Map<String, Int>? = null
                    |   val table:Map<String, Int>
                    |       get() {
                    |           if (_table == null) {
                    |               _table = HashMap()
                    |           }
                    |           return _table ?: throw AssertionError("Set to null by another thread")
                    |       }
                    |       set(value) {field = value}
                    |}
                """.trimMargin()
        )
    }

    @Test
    @Tag("")
    fun `trigger on backing property`() {
        lintMethod(
                """
                    |class Some(val a: Int = 5) {
                    |   private var _a: Map<String, Int>? = null
                    |   val table:Map<String, Int>
                    |       get() {
                    |           if (_a == null) {
                    |               _a = HashMap()
                    |           }
                    |           return _a ?: throw AssertionError("Set to null by another thread")
                    |       }
                    |       set(value) {field = value}
                    |}
                """.trimMargin()
        )
    }

    @Test
    @Tag("")
    fun `don't trigger on regular backing property`() {
        lintMethod(
                """
                    |class Some(val a: Int = 5) {
                    |   private var _a: Map<String, Int>? = null
                    |   private val _some:Int? = null
                    |}
                """.trimMargin()
        )
    }

    @Test
    @Tag("")
    fun `don't trigger on regular property`() {
        lintMethod(
                """
                    |class Some(val a: Int = 5) {
                    |   private var a: Map<String, Int>? = null
                    |   private val some:Int? = null
                    |   private val _prop: String? = null
                    |}
                """.trimMargin()
        )
    }

    @Test
    @Tag("")
    fun `should not trigger if property has field in accessor`() {
        lintMethod(
                """
                    |class Some(val a: Int = 5) {
                    |   val table:Map<String, Int>
                    |       set(value) { field = value }
                    |   val _table: Map<String,Int>? = null
                    |}
                """.trimMargin()
        )
    }
}