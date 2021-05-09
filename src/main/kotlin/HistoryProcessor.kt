import org.apache.camel.Exchange
import org.apache.camel.Processor
import org.apache.camel.component.debezium.DebeziumConstants
import org.apache.kafka.connect.data.Struct

class HistoryProcessor : Processor {
    override fun process(exchange: Exchange?) {
        val struct: Struct?
        val before: Struct?
        val after: Struct?
        val headers = exchange?.getIn()?.headers as Map<*, *>

        if (null == exchange?.getIn()?.body) {
            struct = headers[DebeziumConstants.HEADER_BEFORE] as Struct
            before = struct
            after = null
        } else {
            struct = exchange.getIn().body as Struct
            before = null
            after = struct
        }

        val history = History(
            null,
            headers[DebeziumConstants.HEADER_TIMESTAMP] as Long,
            headers[DebeziumConstants.HEADER_IDENTIFIER] as String,
            struct.getInt32("id"),
            struct.getInt32("user_id"),
            headers[DebeziumConstants.HEADER_OPERATION] as String,
            before,
            after
        )
        exchange?.getIn()?.body = history
    }
}