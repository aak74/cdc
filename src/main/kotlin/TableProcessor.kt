import io.debezium.data.Envelope
import org.apache.camel.Exchange
import org.apache.camel.Processor

class TableProcessor : Processor {
    override fun process(exchange: Exchange?) {
        val history = exchange?.getIn()!!.body as History
        val tableName = getTableName(history.tableName)
        val body = when (history.operation) {
            Envelope.Operation.READ.code() -> {
                getInsertSQL(tableName, history.data)
            }
            Envelope.Operation.UPDATE.code() -> {
                getUpdateSQL(tableName, history.entityId, history.data)
            }
            Envelope.Operation.DELETE.code() -> {
                getDeleteSQL(tableName, history.entityId)
            }
            else -> ""
        }
        exchange.getIn()?.body = body
    }

    private fun getInsertSQL(tableName: String, data: Map<*, *>): String {
        var sql = "INSERT INTO $tableName ("
        var values = "VALUES("

        var i = 0
        for (field in data) {
            field.key
            if (i > 0) {
                sql += ","
                values += ","
            }
            sql += field.key
            values += getWrappedValue(field)
            i++
        }
        return """
           ${sql}) 
           ${values}) 
        """
    }

    private fun getUpdateSQL(tableName: String, id: Int, data: Map<*, *>): String {
        var i = 0
        var sql = "UPDATE $tableName SET "
        for (field in data) {
            if ("id" == field.key) {
                continue
            }
            if (i > 0) {
                sql += ","
            }
            sql += "${field.key}=${getWrappedValue(field)}"
            i++
        }
        sql += " WHERE id = $id"
        return sql
    }

    private fun getDeleteSQL(tableName: String, id: Int): String {
        return "DELETE FROM $tableName WHERE id = $id"
    }

    private fun getWrappedValue(field: Map.Entry<Any?, Any?>): Any? {
        if (field.value is String) {
            return "'${field.value}'"
        }
       return field.value
    }

    private fun getTableName(fqtn: String): String {
        val lastIndex = fqtn.lastIndexOf(".", fqtn.lastIndexOf(".") -1)
        if (lastIndex == -1) {
            return ""
        }
        return fqtn.substring(lastIndex + 1)
    }

}
