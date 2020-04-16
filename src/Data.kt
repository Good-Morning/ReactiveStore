import org.bson.Document

abstract class MapDAO { abstract operator fun get(id: String): String }

class DocumentDAO(private val doc: Document): MapDAO() { override fun get(id: String) = doc[id].toString() }

class ParamDAO(private val map: Map<String, List<String>>): MapDAO() {
    override fun get(id: String) = map[id]?.get(0) ?: error("$id expected")
}

abstract class Data(mapDao: MapDAO, private val idName: String = "identifier") {
    val id = mapDao[idName]

    open fun toDocument(): Document = Document(idName, id)
}

class Currency(mapDao: MapDAO): Data(mapDao) {
    val value = mapDao["value"].toDouble()

    override fun toString(): String = "Currency {identifier='$id', value='$value'}"

    override fun toDocument(): Document = super.toDocument().append("value", value)
}

class Item(mapDao: MapDAO): Data(mapDao, "name") {
    var price = mapDao["price"].toDouble()

    override fun toString(): String = toString(1.0)

    fun toString(currency: Double): String = "Item {name='$id', price='${price / currency}'}"

    override fun toDocument(): Document = super.toDocument().append("price", price)
}

class User(mapDao: MapDAO): Data(mapDao) {
    val currency = mapDao["currency"]

    override fun toString(): String = "User {identifier='$id', currency='$currency'}"

    override fun toDocument(): Document = super.toDocument().append("currency", currency)
}
