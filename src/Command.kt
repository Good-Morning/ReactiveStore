import rx.Observable

fun withUserCurrency(params: Map<String, List<String>>, func: Currency.() -> Observable<String>): Observable<String> =
    UserDAO.find(params.getOrElse("identifier") {
        return@withUserCurrency Observable.just("identifier expected")
    }[0]).flatMap { user: User -> CurrencyDAO.find(user.currency).flatMap { it.func() } }

fun addUser(params: Map<String, List<String>>): Observable<String> = User(ParamDAO(params)).run {
    return CurrencyDAO.find(currency).flatMap { UserDAO.add(this).map { "done" } }
}

fun listUsers(): Observable<String> = UserDAO.data.map { "$it\n" }

fun listCurrencies(): Observable<String> = CurrencyDAO.data.map { "$it\n" }

fun listItems(params: Map<String, List<String>>): Observable<String> = withUserCurrency(params) {
    GoodsDAO.data.map { "${it.toString(value)}\n" }
}

fun addItem(params: Map<String, List<String>>): Observable<String> = withUserCurrency(params) {
    Item(ParamDAO(params)).let { item: Item ->
        item.price *= value
        GoodsDAO.add(item).map { "done" }
    }
}

fun reset(): Observable<String> = Mongo.reset().map { "done" }
