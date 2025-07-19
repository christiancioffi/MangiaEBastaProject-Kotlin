package com.example.mangiabastakotlin.Exceptions

class NotFoundException(msg:String?="The item was not found"):Exception(){
    override val message:String=msg ?:"The item was not found"
}