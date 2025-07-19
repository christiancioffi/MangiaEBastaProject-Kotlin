package com.example.mangiabastakotlin.Model.DataSources

import android.net.Uri
import android.util.Log
import com.example.mangiabastakotlin.Exceptions.InvalidCardException
import com.example.mangiabastakotlin.Exceptions.NetworkErrorException
import com.example.mangiabastakotlin.Exceptions.NotFoundException
import com.example.mangiabastakotlin.Model.ResponseError
import com.example.mangiabastakotlin.Model.CreatedUser
import com.example.mangiabastakotlin.Model.ImageResponse
import com.example.mangiabastakotlin.Model.Location
import com.example.mangiabastakotlin.Model.MenuLongDetails
import com.example.mangiabastakotlin.Model.MenuShortDetails
import com.example.mangiabastakotlin.Model.OrderDetails
import com.example.mangiabastakotlin.Model.BuyMenuRequest
import com.example.mangiabastakotlin.Model.UserDetails
import com.example.mangiabastakotlin.Model.UserDetailsResponse
import com.example.mangiabastakotlin.Model.UserDetailsUpdateRequest
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.android.Android
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.HttpRequestBuilder
import io.ktor.client.request.delete
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.put
import io.ktor.client.request.setBody
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import kotlin.coroutines.cancellation.CancellationException

class CommunicationController {
    private val TAG = CommunicationController::class.simpleName
    private val BASE_URL = "https://develop.ewlab.di.unimi.it/mc/2425"
    //private val SID : String = "3KI8yHzHYS7Vz2RtLdPTSwyk23AIEZVPJibqyYrfp8NXZTWBgHnSp5TQ1dvtw775";
    //private val UID : Int = 36893;

    companion object{
        private var SID:String="";
        private var UID: Int=-1;

        fun initializedSidUid(sid: String, uid: Int){
            SID=sid;
            UID=uid;
        }
    }

    private val client = HttpClient(Android) {
        install(ContentNegotiation) {
            json(Json {
                ignoreUnknownKeys = true
            })
        }
        install(HttpTimeout) {
            connectTimeoutMillis = 15_000 // Timeout per la connessione
            requestTimeoutMillis = 15_000 // Timeout per l'intera richiesta
        }
    }

    enum class HttpMethod {
        GET,
        POST,
        DELETE,
        PUT
    }


    suspend fun genericRequest(url: String, method: HttpMethod,
                               queryParameters: Map<String, Any> = emptyMap(),
                               requestBody: Any? = null) : HttpResponse {
        //Crea un URI (oggetto)
        val urlUri = Uri.parse(url)
        //Crea un URL (oggetto)
        val urlBuilder = urlUri.buildUpon()
        //Aggiunge i parametri passati in ingresso alla funzione
        queryParameters.forEach { (key, value) ->
            urlBuilder.appendQueryParameter(key, value.toString())
        }
        //Definire l'URL completo sottoforma di stringa
        val completeUrlString = urlBuilder.build().toString()
        Log.d(TAG, "Request to $completeUrlString");

        //Costruzione della richiesta HTTP
        //La notazione sotto rappresenta il fatto che nel contesto di HTTPRequestBuilder
        // si definisce la funzione specificata (che non prende nessun parametro in ingresso
        //e non ritorna nulla (Unit)
        val request: HttpRequestBuilder.() -> Unit = {
            requestBody?.let {
                contentType(ContentType.Application.Json)
                setBody(requestBody)
            }
        }

        if(method!=HttpMethod.GET && requestBody!=null){
            Log.d(TAG, "Sending to the server "+requestBody.toString());
        }
        //Effettua la richiesta sulla base del metodo (perchè ci sono differenti metodi
        //del client a seconda del metodo)

        try{
            val response: HttpResponse = when (method) {
                HttpMethod.GET -> client.get(completeUrlString, request)
                HttpMethod.POST -> client.post(completeUrlString, request)
                HttpMethod.DELETE -> client.delete(completeUrlString, request)
                HttpMethod.PUT -> client.put(completeUrlString, request)
            }
            val status=response.status.value

            if(status==200){
                val body:String=response.bodyAsText();
                val message="$url returned HTTP status $status, body: $body";
                Log.d(TAG, message);
            }else if(status==204){
                val message="$url returned HTTP status $status";
                Log.d(TAG, message);
            }else if(status==404){
                val error: ResponseError =response.body();
                val message=error.message;
                Log.d(TAG,"Error message from the server. HTTP Status: $status, Message: $message");
                throw NotFoundException(message);
            }else if(status==403){
                val error: ResponseError =response.body();
                val message=error.message;
                Log.d(TAG,"Error message from the server. HTTP Status: $status, Message: $message");
                throw InvalidCardException();
            }else{
                val error: ResponseError =response.body();
                val message=error.message;
                Log.d(TAG,"Error message from the server. HTTP Status: $status, Message: $message");
                throw NetworkErrorException("Request not accepted");
            }

            //Ritorna il risultato della richiesta effettuata dal client
            return response;
        }catch(nee: NetworkErrorException){
            throw NetworkErrorException(nee.message);
        }catch(ice: InvalidCardException){
            throw InvalidCardException();
        }catch(nfe: NotFoundException){
            throw NotFoundException(nfe.message);
        }catch(ce: CancellationException){
            throw CancellationException(ce.message);
        }catch(e: Exception){
            Log.d(TAG, e.message ?: "Error");
            throw NetworkErrorException();
        }

    }

    suspend fun createUser(): CreatedUser {
        val endpoint: String="/user";
        Log.d(TAG, "Request to '$endpoint' endpoint");

        val url = BASE_URL +endpoint;

        val httpResponse = genericRequest (url, HttpMethod.POST)
        val result : CreatedUser = httpResponse.body()
        return result
    }

    suspend fun getUser(): UserDetails {
        val endpoint: String="/user/${CommunicationController.UID}";
        Log.d(TAG, "Request to '$endpoint' endpoint");

        val url = BASE_URL +endpoint;
        val queryParams=mapOf("sid" to CommunicationController.SID);

        val httpResponse = genericRequest (url, HttpMethod.GET,queryParams)
        val serializedResult : UserDetailsResponse = httpResponse.body()
        val result=UserDetails(serializedResult)
        return result
    }

    suspend fun getMenuImage(mid:Int): ImageResponse {
        val endpoint: String="/menu/$mid/image";
        Log.d(TAG, "Request to '$endpoint' endpoint");

        val url = BASE_URL +endpoint;
        val queryParams=mapOf("sid" to CommunicationController.SID);

        val httpResponse = genericRequest (url, HttpMethod.GET,queryParams);
        val result : ImageResponse = httpResponse.body();
        return result;
    }

    suspend fun getMenuByLocation(location: Location): List<MenuShortDetails> {
        val endpoint: String="/menu";
        Log.d(TAG, "Request to '$endpoint' endpoint");

        val url = BASE_URL +endpoint;
        val queryParams=mapOf(
            "sid" to CommunicationController.SID,
            "lat" to location.lat,
            "lng" to location.lng,
            );

        val httpResponse = genericRequest (url, HttpMethod.GET,queryParams);
        val result : List<MenuShortDetails> = httpResponse.body();
        return result;
    }

    suspend fun getMenuDetails(mid: Int, location: Location): MenuLongDetails {
        val endpoint: String="/menu/$mid";
        Log.d(TAG, "Request to '$endpoint' endpoint");

        val url = BASE_URL +endpoint;
        val queryParams=mapOf(
            "sid" to CommunicationController.SID,
            "lat" to location.lat,
            "lng" to location.lng,
        );

        val httpResponse = genericRequest (url, HttpMethod.GET,queryParams);
        val result : MenuLongDetails = httpResponse.body();
        return result;
    }

    suspend fun buyMenu(mid: Int, location: Location): OrderDetails {
        val endpoint: String="/menu/$mid/buy";
        Log.d(TAG, "Request to '$endpoint' endpoint");

        val url = BASE_URL +endpoint;
        val requestBody = BuyMenuRequest(CommunicationController.SID,location)

        val httpResponse = genericRequest (url, HttpMethod.POST,requestBody=requestBody);
        val result : OrderDetails = httpResponse.body();
        return result;
    }

    suspend fun updateUser(userDetails: UserDetails){
        val endpoint: String="/user/${CommunicationController.UID}";
        Log.d(TAG, "Request to '$endpoint' endpoint");

        val url = BASE_URL +endpoint;
        val requestBody = UserDetailsUpdateRequest(userDetails,CommunicationController.SID)

        val httpResponse = genericRequest (url, HttpMethod.PUT,requestBody=requestBody);
    }

    suspend fun getOrderDetails(oid: Int):OrderDetails{
        val endpoint: String="/order/$oid";
        Log.d(TAG, "Request to '$endpoint' endpoint");

        val url = BASE_URL +endpoint;
        val queryParams=mapOf(
            "sid" to CommunicationController.SID,
        );

        val httpResponse = genericRequest (url, HttpMethod.GET,queryParams);
        val result : OrderDetails = httpResponse.body();
        return result;
    }

}

