package com.example.mangiabastakotlin.Model

import com.example.mangiabastakotlin.Exceptions.IncompleteProfileException
import com.example.mangiabastakotlin.Exceptions.OngoingOrderException
import kotlinx.serialization.Serializable
import java.time.LocalDate

@Serializable
data class CreatedUser(
    val sid: String,
    val uid: Int
)

@Serializable
data class ResponseError(val message: String)

@Serializable
data class Location(
    val lat: Double = 0.0,
    val lng: Double = 0.0
)

@Serializable
data class ImageResponse(
    val base64: String
)

@Serializable
data class MenuShortDetails(
    val mid: Int = -1,
    val name: String = "",
    val price: Double = 0.0,
    val location: Location = Location(),
    val imageVersion: Int = -1,
    val shortDescription: String = "",
    val deliveryTime: Int = -1,
)

@Serializable
data class MenuLongDetails(
    val mid: Int = -1,
    val name: String = "",
    val price: Double = 0.0,
    val location: Location = Location(),
    val imageVersion: Int = -1,
    val shortDescription: String = "",
    val deliveryTime: Int = -1,
    val longDescription: String = "",
)

@Serializable
data class BuyMenuRequest(
    val sid: String,
    val deliveryLocation: Location
)

@Serializable
data class OrderDetails(
    val oid:Int=-1,
    val mid: Int=-1,
    val uid: Int=-1,
    val creationTimestamp: String="",
    val status: String="",
    val deliveryLocation: Location =Location(),
    val deliveryTimestamp: String = "", //Questo campo c'è solo a ordine completato.
    val expectedDeliveryTimestamp: String = "",
    val currentPosition: Location=Location(),
)

@Serializable
data class UserDetailsResponse(
    val firstName: String?,
    val lastName: String?,
    val cardFullName: String?,
    val cardNumber: String?,
    val cardExpireMonth: Int?,
    val cardExpireYear: Int?,
    val cardCVV: String?,
    val uid: Int?,
    val lastOid: Int?,
    val orderStatus: String?
)

data class UserDetails(
    var firstName: String="",
    var lastName: String="",
    var cardFullName: String="",
    var cardNumber: String="",
    var cardExpireMonth: Int=-1,
    var cardExpireYear: Int=-1,
    var cardCVV: String="",
    val uid: Int=-1,
    var lastOid: Int=-1,
    val orderStatus: String=""
){
    constructor(
        userDetailsResponse: UserDetailsResponse,
    ): this(
        firstName=userDetailsResponse.firstName?:"",
        lastName=userDetailsResponse.lastName?:"",
        cardFullName=userDetailsResponse.cardFullName?:"",
        cardNumber=userDetailsResponse.cardNumber?:"",
        cardExpireMonth=userDetailsResponse.cardExpireMonth?:-1,
        cardExpireYear=userDetailsResponse.cardExpireYear?:-1,
        cardCVV=userDetailsResponse.cardCVV?:"",
        uid=userDetailsResponse.uid?:-1,
        lastOid=userDetailsResponse.lastOid?:-1,
        orderStatus=userDetailsResponse.orderStatus?:"",
    );

    fun isComplete(){
        if((this.firstName.length !in 1..15) || (!isValidString(this.firstName))){       //Esclude automaticamente le stringhe "".
            throw IncompleteProfileException(wrongField="First name");
        }
        if((this.lastName.length !in 1..15) || (!isValidString(this.lastName))){
            throw IncompleteProfileException(wrongField="Last name");
        }
        if((this.cardFullName.length!in 1..31) || (!isValidString(this.cardFullName))){
            throw IncompleteProfileException(wrongField="Card full name");
        }
        if(this.cardNumber.length!=16 || (!isValidCode(this.cardNumber))){
            throw IncompleteProfileException(wrongField="Card number");
        }
        if(this.cardCVV.length!=3 || (!isValidCode(this.cardCVV))){
            throw IncompleteProfileException(wrongField="Card CVV");
        }
        if(this.cardExpireMonth !in 1..12){     //Esclude automaticamente il caso valore=-1
            throw IncompleteProfileException(wrongField="Card expire month");
        }
        if(this.cardExpireYear !in LocalDate.now().year..2100){  //Esclude automaticamente il caso valore=-1
            throw IncompleteProfileException(wrongField="Card expire year");
        }
    }

    fun canOrder(){
        isComplete();
        if(orderStatus=="ON_DELIVERY"){
            throw OngoingOrderException();
        }
    }

    private fun isValidString(str: String):Boolean{
        return str.all { it.isLetter() || it.isWhitespace() };
    }

    private fun isValidCode(str: String):Boolean{
        return str.all { it.isDigit() };
    }
}

@Serializable
data class UserDetailsUpdateRequest(
    var firstName: String="",
    var lastName: String="",
    var cardFullName: String="",
    var cardNumber: String="",
    var cardExpireMonth: Int=-1,
    var cardExpireYear: Int=-1,
    var cardCVV: String="",
    var sid: String=""
){
    constructor(userDetails:UserDetails,sid:String):this(
        firstName=userDetails.firstName,
        lastName=userDetails.lastName,
        cardFullName=userDetails.cardFullName,
        cardNumber=userDetails.cardNumber,
        cardExpireMonth=userDetails.cardExpireMonth,
        cardExpireYear=userDetails.cardExpireYear,
        cardCVV=userDetails.cardCVV,
        sid=sid
    )
}

data class AppRequest(
    val requestState: String="Idle",    //Idle, Waiting, Received
    val title: String = "",
    val message: String = "",
    val orderId: Int = -1
)




