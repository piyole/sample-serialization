package usecase

import JACKSON_SAMPLE
import KOTLIN_SERIALIZATION_SAMPLE
import com.fasterxml.jackson.annotation.JsonSubTypes
import com.fasterxml.jackson.annotation.JsonTypeInfo
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.polymorphic
import kotlinx.serialization.modules.subclass

interface Content

class PolymorphismTest : FunSpec({
  context("ポリモーフィズム") {
    test(KOTLIN_SERIALIZATION_SAMPLE) {

      @Serializable
      @SerialName("article") // 指定しない場合は完全修飾クラス名となる
      class Article(val title: String) : Content

      @Serializable
      @SerialName("image")
      class Image(val url: String) : Content

      @Serializable
      @SerialName("book")
      class Book(val articles: List<Article>) : Content

      val json = Json {
        // デフォルトだとプロパティ名が type になる
        // classDiscriminator で任意のプロパティ名に変更可能
        classDiscriminator = "@type"
        serializersModule = SerializersModule {
          polymorphic(Content::class) {
            subclass(Article::class)
            subclass(Image::class)
            subclass(Book::class)
          }
        }
      }

      // polymorphic であることを明示するために型パラメータが必要
      json.encodeToString<Content>(Article("タイトル"))
        .shouldBe("""{"@type":"article","title":"タイトル"}""")
      // 型パラメータがない場合は単なる具象クラスとして扱われるので @type が含まれない
      json.encodeToString(Article("タイトル"))
        .shouldBe("""{"title":"タイトル"}""")
      json.encodeToString<Content>(Image("https://newspicks.com/images/123"))
        .shouldBe("""{"@type":"image","url":"https://newspicks.com/images/123"}""")
      // Book のもつ articles は Content インタフェースではなく具象クラスの Article なので　@type は含まれない
      json.encodeToString<Content>(Book(listOf(Article("タイトル"))))
        .shouldBe("""{"@type":"book","articles":[{"title":"タイトル"}]}""")
    }

    test(JACKSON_SAMPLE) {

      class Article(val title: String) : Content

      class Image(val url: String) : Content

      class Book(val articles: List<Article>) : Content

      @JsonTypeInfo(use = JsonTypeInfo.Id.NAME)
      @JsonSubTypes(
        JsonSubTypes.Type(Article::class, name = "article"),
        JsonSubTypes.Type(Image::class, name = "image"),
        JsonSubTypes.Type(Book::class, name = "book")
      )
      class ContentMixIn

      val mapper = jacksonObjectMapper().addMixIn(Content::class.java, ContentMixIn::class.java)

      mapper.writeValueAsString(Article("タイトル"))
        .shouldBe("""{"@type":"article","title":"タイトル"}""")
      mapper.writeValueAsString(Image("https://newspicks.com/images/123"))
        .shouldBe("""{"@type":"image","url":"https://newspicks.com/images/123"}""")
      mapper.writeValueAsString(Book(listOf(Article("タイトル"))))
        .shouldBe("""{"@type":"book","articles":[{"@type":"article","title":"タイトル"}]}""")
    }
  }
})
