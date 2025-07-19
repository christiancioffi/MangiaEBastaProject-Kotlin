package com.example.mangiabastakotlin.Exceptions

class IncompleteProfileException(override val message: String="Profile is not completed", val wrongField:String = ""):Exception(){

}