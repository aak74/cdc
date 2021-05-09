import org.apache.camel.Exchange
import org.apache.camel.TypeConversionException
import org.apache.camel.component.debezium.DebeziumConstants
import org.apache.camel.support.TypeConverterSupport
import org.apache.kafka.connect.data.Struct

class HistoryConverter : TypeConverterSupport() {
    @Throws(TypeConversionException::class)
    override fun <T> convertTo(type: Class<T>, exchange: Exchange, value: Any): T {
        val struct = exchange.getIn().body as Struct
        val headers = exchange.getIn().headers as Map<*, *>
        return History(
            null,
            headers[DebeziumConstants.HEADER_TIMESTAMP] as Long,
            headers[DebeziumConstants.HEADER_IDENTIFIER] as String,
            struct.getInt32("id"),
            struct.getInt32("user_id"),
            headers[DebeziumConstants.HEADER_OPERATION] as String,
            headers[DebeziumConstants.HEADER_BEFORE] as? Struct,
            struct
        ) as T
    }

    override fun allowNull(): Boolean {
        return true
    }
}