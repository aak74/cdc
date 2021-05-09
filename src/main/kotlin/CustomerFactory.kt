import org.apache.kafka.connect.data.Struct

class CustomerFactory {
    fun getFromStruct(struct: Struct): Customer {
        return Customer(
            struct.getInt32("id"),
            struct.getString("first_name"),
            struct.getString("last_name"),
            struct.getString("email"),
            struct.getInt32("user_id")
        )
    }
}
