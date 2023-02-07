package usecase;

import J
import JACKSON_SAMPLE
import KOTLIN_SERIALIZATION_SAMPLE
import S
import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

class IgnoreTest : FunSpec({
  context("とあるプロパティは JSON に含めたくない") {
    test(KOTLIN_SERIALIZATION_SAMPLE) {
      @Serializable
      data class User(
        val id: Int,
        val name: String,
        @Transient // デフォルト値は必須
        val objectMapper: ObjectMapper = jacksonObjectMapper(),
      )

      S.serialize(User(1_000, "John"))
        .shouldBe("""{"id":1000,"name":"John"}""")
    }

    test(JACKSON_SAMPLE) {
      data class User(
        val id: Int,
        val name: String,
        @JsonIgnore
        val objectMapper: ObjectMapper = jacksonObjectMapper(),
      )

      J.serialize(User(1_000, "John"))
        .shouldBe("""{"id":1000,"name":"John"}""")
    }
  }

  context("とあるプロパティは JSON に含めたくない - デフォルト値がない場合") {
    test(KOTLIN_SERIALIZATION_SAMPLE) {
      @Serializable
      data class User(
        val id: Int,
        val name: String,
        // @Transient <- これはデフォルト値がないのでコンパイルエラー
        val password: String,
      )

      // テストは失敗する
      // S.serialize(User(1_000, "John", "test")) shouldBe """{"id":1000,"name":"John"}"""

      // 実現する方法はなさそう？ただ実際の業務においてこのユースケースはなさそう
    }

    test(JACKSON_SAMPLE) {
      data class User(
        val id: Int,
        val name: String,
        @JsonIgnore
        val password: String,
      )

      val json = J.serialize(User(1_000, "John", "password"))
      json shouldBe """{"id":1000,"name":"John"}"""

      shouldThrow<Throwable> {
        J.deserialize<User>(json) // non-null な password プロパティがないのでエラー
      }
    }
  }
})
