import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import kotlinx.serialization.Serializable
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class SampleTest : FunSpec({

  @Serializable // コンパイラプラグインがシリアライザを自動生成するために必要
  data class User(val id: Int, val name: String) // data class である必要はないけどログ出力のため

  test("シリアライズ") {
    val user = User(1_000, "John")
    Json.encodeToString(user) shouldBe """{"id":1000,"name":"John"}"""
  }

  test("デシリアライズ") {
    val json = """{"id":1001,"name":"Mike"}"""
    // デシリアライズ後の型を指定する
    Json.decodeFromString<User>(json) shouldBe User(1_001, "Mike")
  }

})
