import org.apache.camel.Exchange
import org.apache.camel.TypeConversionException
import org.apache.camel.support.TypeConverterSupport
import org.apache.kafka.connect.data.Struct

class CustomerConverter : TypeConverterSupport() {
    @Throws(TypeConversionException::class)
    override fun <T> convertTo(type: Class<T>, exchange: Exchange, value: Any): T {
        val struct = exchange.getIn().body as Struct
        return CustomerFactory().getFromStruct(struct) as T
    }
}
