package usecase

import J
import JACKSON_SAMPLE
import KOTLIN_SERIALIZATION_SAMPLE
import S
import com.fasterxml.jackson.annotation.JsonInclude
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import kotlinx.serialization.Serializable

class IgnoreNullTest : FunSpec({
  context("null の場合は JSON に含めたくない") {
    test(KOTLIN_SERIALIZATION_SAMPLE) {
      @Serializable
      data class Pick(
        val userId: Int,
        val newsId: Int,
        // デフォルト値をもつプロパティはデフォルト値と異なる場合のみ出力される
        val comment: String? = null,
      )

      S.serialize(Pick(1_000, 2_000))
        .shouldBe("""{"userId":1000,"newsId":2000}""")
    }

    test(JACKSON_SAMPLE) {
      data class Pick(
        val userId: Int,
        val newsId: Int,
        @JsonInclude(JsonInclude.Include.NON_NULL)
        val comment: String?,
      )

      J.serialize(Pick(1_000, 2_000, null))
        .shouldBe("""{"userId":1000,"newsId":2000}""")
    }
  }
})
