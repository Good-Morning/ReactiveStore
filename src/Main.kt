import io.netty.buffer.ByteBuf
import io.reactivex.netty.protocol.http.server.HttpServer
import io.reactivex.netty.protocol.http.server.HttpServerRequest
import io.reactivex.netty.protocol.http.server.HttpServerResponse
import rx.Observable

fun main() {
    HttpServer
        .newServer(80)
        .start { request: HttpServerRequest<ByteBuf?>, response: HttpServerResponse<ByteBuf?> ->
            response.writeString(process(request.decodedPath, request.queryParameters))
        }
        .awaitShutdown()
}

private fun process(name: String, params: Map<String, List<String>>): Observable<String> = try { when (name) {
    "/add_user"        -> addUser(params)
    "/list_users"      -> listUsers()
    "/list_items"      -> listItems(params)
    "/add_item"        -> addItem(params)
    "/list_currencies" -> listCurrencies()
    "/reset"           -> reset()
    else               -> Observable.just("""
        usage:
        http://[host]:80/[command]?[key0]=[value0]&[key1]=[value1]&...&[keyn]=[valuen]
        
        commands:
        /reset           - init/reset db 
                           no argument
        /list_currencies - list registered currencies is rubbles
                           no argument
                         
        /add_user        - register a new user
                           identifier = [user_identifier]
                           currency = [RUB | EUR | USD]
        /list_users      - list registered users
                           no argument
                           
        /list_items      - show cost of the selected item
                           identifier = [user_identifier]
        /add_item        - register a new item
                           identifier = [user_identifier]
                           name = [item_identifier]
                           price = [price_in_rubbles]
                         
        e.g. http://localhost:80/add_user?identifier=guest&currency=EUR
             http://localhost:80/list_currency
    """.trimIndent())
}} catch (t: Throwable) {Observable.just("an error occured: $t")}
