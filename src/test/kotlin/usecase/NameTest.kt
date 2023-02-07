package usecase

import J
import JACKSON_SAMPLE
import KOTLIN_SERIALIZATION_SAMPLE
import S
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.databind.*
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import kotlinx.serialization.*


class NameTest : FunSpec({

  context("プロパティ名を変更したい") {
    test(KOTLIN_SERIALIZATION_SAMPLE) {
      @Serializable
      data class User(
        @SerialName("userId")
        val id: Int,
        val name: String,
      )

      S.serialize(User(1_000, "John"))
        .shouldBe("""{"userId":1000,"name":"John"}""")
    }

    test(JACKSON_SAMPLE) {
      data class User(
        @JsonProperty("userId")
        val id: Int,
        val name: String,
      )
      J.serialize(User(1_000, "John"))
        .shouldBe("""{"userId":1000,"name":"John"}""")
    }
  }
})
