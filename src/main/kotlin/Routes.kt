import org.apache.camel.LoggingLevel
import org.apache.camel.builder.RouteBuilder
import javax.sql.DataSource
import org.apache.commons.dbcp2.BasicDataSource


class Routes : RouteBuilder() {
    private val DATABASE_READER = ("debezium-postgres:{{database.hostname}}?"
            + "databaseHostname={{database.hostname}}"
            + "&databasePort={{database.port}}"
            + "&databaseUser={{database.user}}"
            + "&databasePassword={{database.password}}"
            + "&databaseDbname={{database.dbname}}"
            + "&databaseServerName={{database.hostname}}"
            + "&schemaWhitelist=public"
            + "&tableWhitelist=public.*"
            + "&offsetStorageFileName=/tmp/offset.dat"
            + "&offsetFlushIntervalMs=10000"
            + "&pluginName=pgoutput")
    private val DB_WRITER = "direct:db-writer"
    private val DB_HISTORY = "direct:db-history"

    override fun configure() {
//        val prop = context.propertiesComponent
//        context.propertiesComponent.setLocation("classpath:application.properties")
        prepareRegistry()

//        val isCRUEvent = header(DebeziumConstants.HEADER_OPERATION).`in`(
//            constant(Envelope.Operation.CREATE.code()),
//            constant(Envelope.Operation.READ.code()),
//            constant(Envelope.Operation.UPDATE.code())
//        )

        from(DATABASE_READER)
            .routeId("DatabaseReader")
            .to("log://DatabaseReader")
            .log(LoggingLevel.DEBUG, "Incoming message \nBODY: \${body} \nHEADERS: \${headers}")
            .process(HistoryProcessor())
//            .convertBodyTo(History::class.java)
            .multicast().streaming().parallelProcessing()
                .stopOnException().to(DB_WRITER, DB_HISTORY)
            .end()


        from(DB_WRITER)
            .routeId("DbWriter")
//            .process(CustomerProcessor())
            .to("log://dbwriter")
            .filter()
                .method(FilterBean::class.java, "validate")
                .to("log://dbwriter-filtered")
                .process(TableProcessor())
                .to("jdbc:outDataSource?useHeadersAsParameters=true")
            .end()

        from(DB_HISTORY)
            .routeId("DbHistory")
            .to("log://dbhistory")

    }

    private fun prepareRegistry() {
//        context.typeConverterRegistry
//            .addTypeConverter(History::class.java, Struct::class.java, HistoryConverter())

//        context.typeConverterRegistry
//            .addTypeConverter(Customer::class.java, Struct::class.java, CustomerConverter())

        context.registry.bind(
            "outDataSource",
            DataSource::class.java,
            getDataSource("jdbc:postgresql://localhost:15432/dest_db")
        )
    }

    private fun getDataSource(connectURI: String): DataSource {
        val ds = BasicDataSource()
        ds.driverClassName = "org.postgresql.Driver"
        ds.url = connectURI
        ds.username = "postgres"
        ds.password = "postgres"
        return ds
    }

}
