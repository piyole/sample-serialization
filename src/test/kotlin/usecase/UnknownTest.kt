package usecase

import J
import JACKSON_SAMPLE
import KOTLIN_SERIALIZATION_SAMPLE
import S
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import kotlinx.serialization.Serializable
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json

class UnknownTest : FunSpec({
  context("未定義のプロパティは無視したい") {
    test(KOTLIN_SERIALIZATION_SAMPLE) {
      @Serializable
      data class User(
        val id: Int,
        val name: String,
      )

      // 未定義の active プロパティが存在
      val json = """{"id":1000,"name":"John","active":true}"""

      shouldThrow<Throwable> {
        // User クラスに active プロパティがないのでエラーとなる
        S.deserialize<User>(json)
      }

      @Suppress("JSON_FORMAT_REDUNDANT")
      Json {
        ignoreUnknownKeys = true // クラスではなく Json オブジェクトの構築時に指定する
      }.decodeFromString<User>(json) shouldBe User(1_000, "John")
    }

    test(JACKSON_SAMPLE) {
      @JsonIgnoreProperties(ignoreUnknown = true)
      data class User(
        val id: Int,
        val name: String,
      )

      // 未定義の active プロパティが存在
      val json = """{"id":1000,"name":"John","active":true}"""

      J.deserialize<User>(json) shouldBe User(1_000, "John")

      // ObjectMapper オブジェクトで設定することも可能
      jacksonObjectMapper()
        .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
        .readValue(json, User::class.java)
        .shouldBe(User(1_000, "John"))
    }
  }
})
