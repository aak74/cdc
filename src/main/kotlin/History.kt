import org.apache.kafka.connect.data.Struct

data class History(
    val id: Int? = 0,
    val createdAt: Long,
    val tableName: String,
    val entityId: Int,
    val user_id: Int,
    val operation: String,
    val before: Struct? = null,
    val after: Struct? = null
)
