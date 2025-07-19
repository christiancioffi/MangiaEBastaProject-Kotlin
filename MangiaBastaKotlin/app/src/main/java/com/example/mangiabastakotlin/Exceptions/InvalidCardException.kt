package com.example.mangiabastakotlin.Exceptions

class InvalidCardException(msg:String?="You have an invalid card. Please add a new one."):Exception(){
    override val message:String=msg ?:"You have an invalid card. Please add a new one."
}