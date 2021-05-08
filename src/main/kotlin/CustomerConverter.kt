import org.apache.camel.Exchange
import org.apache.camel.TypeConversionException
import org.apache.camel.support.TypeConverterSupport
import org.apache.kafka.connect.data.Struct

class CustomerConverter : TypeConverterSupport() {
    @Throws(TypeConversionException::class)
    override fun <T> convertTo(type: Class<T>, exchange: Exchange, value: Any): T {
        val struct = exchange.getIn().body as Struct
        return Customer(
            struct.getInt32("id"),
            struct.getString("first_name"),
            struct.getString("last_name"),
            struct.getString("email"),
            struct.getInt32("updated_by_user")
        ) as T
    }
}
