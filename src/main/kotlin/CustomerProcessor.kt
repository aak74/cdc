import io.debezium.data.Envelope
import org.apache.camel.Exchange
import org.apache.camel.Processor

class CustomerProcessor: Processor {
    override fun process(exchange: Exchange?) {
        val history = exchange?.getIn()!!.body as History
        val sql: String
        val customer = history.after?.let { CustomerFactory().getFromStruct(it) }
        sql = if (null == customer) {
            getDeleteSQL(history.entityId)
        } else {
            when (history.operation) {
                Envelope.Operation.UPDATE.code() -> {
                    getUpdateSQL(customer)
                }
                else -> {
                    getInsertSQL(customer)
                }
            }

        }
        exchange?.getIn()!!.body = sql
    }

    private fun getUpdateSQL(customer: Customer): String {
        return """
           UPDATE customers
           SET 
               first_name = '${customer.firstName}',
               last_name = '${customer.lastName}',
               email = '${customer.email}', 
               user_id =${customer.userId}
           WHERE id = ${customer.id} 
        """
    }

    private fun getDeleteSQL(id: Int): String {
        return """
           DELETE FROM customers
           WHERE id = $id 
        """
    }

    private fun getInsertSQL(customer: Customer): String {
        return """
           INSERT INTO customers 
           (id, first_name, last_name, email, user_id) 
           VALUES(${customer.id}, '${customer.firstName}', '${customer.lastName}', '${customer.email}', ${customer.userId}) 
        """
    }

}