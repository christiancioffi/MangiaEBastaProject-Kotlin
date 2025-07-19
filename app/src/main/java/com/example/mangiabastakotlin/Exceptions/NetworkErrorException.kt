package com.example.mangiabastakotlin.Exceptions

class NetworkErrorException(msg:String?="A network error occurred"):Exception(){
    override val message:String=msg ?:"A network error occurred"
}