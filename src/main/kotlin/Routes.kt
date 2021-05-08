import org.apache.camel.LoggingLevel
import org.apache.camel.builder.RouteBuilder
import org.apache.kafka.connect.data.Struct
import javax.sql.DataSource
import org.apache.commons.dbcp2.BasicDataSource


class Routes: RouteBuilder() {
    private val DATABASE_READER = ("debezium-postgres:{{database.hostname}}?"
            + "databaseHostname={{database.hostname}}"
            + "&databasePort={{database.port}}"
            + "&databaseUser={{database.user}}"
            + "&databasePassword={{database.password}}"
            + "&databaseDbname={{database.dbname}}"
            + "&databaseServerName={{database.hostname}}"
            + "&schemaWhitelist=public"
            + "&tableWhitelist=public.customers"
            + "&offsetStorageFileName=/tmp/offset.dat"
            + "&offsetFlushIntervalMs=10000"
            + "&pluginName=pgoutput")
    private val DB_WRITER = "direct:db-insert"

    override fun configure() {
//        val prop = context.propertiesComponent
//        context.propertiesComponent.setLocation("classpath:application.properties")
        prepareRegistry()

//        val isCRUEvent = header(DebeziumConstants.HEADER_OPERATION).`in`(
//            constant(Envelope.Operation.CREATE.code()),
//            constant(Envelope.Operation.READ.code()),
//            constant(Envelope.Operation.UPDATE.code())
//        )

//        from(DATABASE_READER)
//            .routeId("DatabaseReader")
//            .to("log://DatabaseReader")
//            .log(LoggingLevel.DEBUG, "Incoming message \nBODY: \${body} \nHEADERS: \${headers}")
//            .process(DebeziumProcessor())
//            .filter(isCRUEvent)
//                .convertBodyTo(Customer::class.java)
//                .multicast().streaming().parallelProcessing()
//                    .stopOnException().to(DB_WRITER)
//                .end()
//            .end()

        from(DATABASE_READER)
            .routeId("DatabaseReader")
            .to("log://DatabaseReader")
            .log(LoggingLevel.DEBUG, "Incoming message \nBODY: \${body} \nHEADERS: \${headers}")
            .convertBodyTo(Customer::class.java)
            .to(DB_WRITER)


        from(DB_WRITER)
            .routeId("DbWriter")
            .process(CustomerProcessor())
            .to("log://dbwriter")
            .to("jdbc:outDataSource?useHeadersAsParameters=true")

    }

    private fun prepareRegistry() {
        context.typeConverterRegistry
            .addTypeConverter(Customer::class.java, Struct::class.java, CustomerConverter())

        context.registry.bind("outDataSource", DataSource::class.java, getDataSource("jdbc:postgresql://localhost:15432/dest_db"))
//        context.registry.bind("outDataSource", DataSource::class.java, getDataSource("jdbc:postgresql://{{database_out.hostname}}:{{database_out.port}}/some_db"))
    }

    private fun getDataSource(connectURI: String): DataSource? {
        val ds = BasicDataSource()
        ds.setDriverClassName("org.postgresql.Driver")
        ds.setUsername("postgres")
        ds.setPassword("postgres")
        ds.setUrl(connectURI)
        return ds
    }

}
