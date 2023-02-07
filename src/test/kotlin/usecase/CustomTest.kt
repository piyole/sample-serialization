package usecase

import J
import JACKSON_SAMPLE
import KOTLIN_SERIALIZATION_SAMPLE
import S
import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.databind.JsonSerializer
import com.fasterxml.jackson.databind.SerializerProvider
import com.fasterxml.jackson.databind.annotation.JsonSerialize
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

data class PickId(val userId: Int, val newsId: Int)

// ローカルで定義したオブジェクト（クラス）は使用できない
object PickIdSerializer : KSerializer<PickId> {
  override val descriptor: SerialDescriptor =
    PrimitiveSerialDescriptor("PickId", PrimitiveKind.STRING)

  override fun deserialize(decoder: Decoder): PickId {
    val (userId, newsId) = decoder.decodeString().split("-")
    return PickId(userId.toInt(), newsId.toInt())
  }

  override fun serialize(encoder: Encoder, value: PickId) {
    encoder.encodeString("${value.userId}-${value.newsId}")
  }
}

class CustomTest : FunSpec({
  context("独自の方法でシリアライズしたい") {
    test(KOTLIN_SERIALIZATION_SAMPLE) {
      @Serializable
      data class Pick(
        @Serializable(with = PickIdSerializer::class)
        val pickId: PickId,
        val comment: String?
      )

      S.serialize(Pick(PickId(1_000, 2_000), "test"))
        .shouldBe("""{"pickId":"1000-2000","comment":"test"}""")
    }

    test(JACKSON_SAMPLE) {
      class PickIdSerializer : JsonSerializer<PickId>() {
        override fun serialize(value: PickId, gen: JsonGenerator, serializers: SerializerProvider) {
          gen.writeString("${value.userId}-${value.newsId}")
        }
      }

      data class Pick(
        @JsonSerialize(using = PickIdSerializer::class)
        val pickId: PickId,
        val comment: String?
      )

      J.serialize(Pick(PickId(1_000, 2_000), "test"))
        .shouldBe("""{"pickId":"1000-2000","comment":"test"}""")
    }
  }
})
