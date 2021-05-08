import org.apache.camel.Exchange
import org.apache.camel.Processor

class DebeziumProcessor: Processor {
    override fun process(exchange: Exchange?) {
        print(exchange?.getIn()!!.headers)
    }

}