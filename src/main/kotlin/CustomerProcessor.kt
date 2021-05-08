import org.apache.camel.Exchange
import org.apache.camel.Processor

class CustomerProcessor: Processor {
    override fun process(exchange: Exchange?) {
        val customer = exchange?.getIn()!!.body as Customer
        val insertSQL = getInsertSQL(customer)
        exchange?.getIn()!!.body = insertSQL
    }

    private fun getInsertSQL(customer: Customer): String {
        return """
           INSERT INTO customers 
           (id, first_name, last_name, email, updated_by_user) 
           VALUES(${customer.id}, '${customer.firstName}', '${customer.lastName}', '${customer.email}', ${customer.updatedByUser}) 
        """
    }

}