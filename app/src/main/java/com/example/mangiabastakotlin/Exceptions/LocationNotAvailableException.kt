package com.example.mangiabastakotlin.Exceptions

class LocationNotAvailableException(msg:String?="Location not available"):Exception(){
    override val message:String=msg ?:"Location not available"
}