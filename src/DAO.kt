import com.mongodb.client.model.Filters
import com.mongodb.rx.client.*
import org.bson.Document
import rx.Observable

open class DAO<T : Data>(private val collectionName: String, private val supplier: (MapDAO) -> T) {
    val data: Observable<T> get() = filteredCollection { find().toObservable() }

    fun find(id: String): Observable<T> = filteredCollection { find(Filters.eq("identifier", id)).toObservable() }

    fun add(value: T) : Observable<Success> = Mongo.getCollection(collectionName).insertOne(value.toDocument())

    private fun filteredCollection(map: MongoCollection<Document>.() -> Observable<Document>) =
        Mongo.getCollection(collectionName).map().map { supplier(DocumentDAO(it)) }
}

object CurrencyDAO: DAO<Currency>("currencies", ::Currency)

object GoodsDAO: DAO<Item>("items", ::Item)

object UserDAO: DAO<User>("users", ::User)

object Mongo {
    private val client = MongoClients.create("mongodb://localhost:27017")
    private val db: MongoDatabase get() = client.getDatabase("ReactiveStore")

    fun reset(): Observable<Success> = Observable.amb(
        db.listCollectionNames().flatMap { getCollection(it).drop() },
        db.createCollection("items"),
        db.createCollection("users"),
        db.createCollection("currencies"),
        db.getCollection("currencies").insertMany(listOf(
            Document().append("identifier", "RUB").append("value", 1.0),
            Document().append("identifier", "USD").append("value", 74.83),
            Document().append("identifier", "EUR").append("value", 80.67)
        ))
    )

    fun getCollection(name: String): MongoCollection<Document> = db.getCollection(name)
}
