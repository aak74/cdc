class FilterBean {
    open fun validate(history: History): Boolean {
        if ("localhost.public.products" != history.tableName) {
            return true
        }
        if (history.entityId > 5) {
            return true
        }
        return false
    }
}