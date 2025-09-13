import kotlinx.serialization.Serializable

@Serializable
data class Rule(
    val rule: Boolean,
    val quantity: Int? = null
)
