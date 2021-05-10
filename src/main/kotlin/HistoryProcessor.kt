import io.debezium.data.Envelope
import org.apache.camel.Exchange
import org.apache.camel.Processor
import org.apache.camel.component.debezium.DebeziumConstants

class HistoryProcessor : Processor {
    override fun process(exchange: Exchange?) {
        val headers = exchange?.getIn()?.headers as Map<*, *>
        val operation = headers[DebeziumConstants.HEADER_OPERATION] as String

        if (Envelope.Operation.DELETE.code() == operation) {
            exchange.getIn()?.body = headers[DebeziumConstants.HEADER_BEFORE]
        }
        val data = exchange.getIn().getBody(Map::class.java)

        val history = History(
            null,
            headers[DebeziumConstants.HEADER_TIMESTAMP] as Long,
            headers[DebeziumConstants.HEADER_IDENTIFIER] as String,
            data["id"] as Int,
            data["user_id"] as Int,
            operation,
            data
        )
        exchange.getIn()?.body = history
    }
}