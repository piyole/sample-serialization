import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

const val KOTLIN_SERIALIZATION_SAMPLE = "Kotlin Serialization Sample"
const val JACKSON_SAMPLE = "Jackson Sample"

object S {
  inline fun <reified T> serialize(value: T) = Json.encodeToString(value)
  inline fun <reified T> deserialize(value: String) = Json.decodeFromString<T>(value)
}

object J {
  inline fun <reified T> serialize(value: T): String = jacksonObjectMapper().writeValueAsString(value)
  inline fun <reified T> deserialize(value: String): T = jacksonObjectMapper().readValue(value, T::class.java)
}
