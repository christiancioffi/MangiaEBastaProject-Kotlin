package com.example.mangiabastakotlin.Exceptions

class OngoingOrderException(override val message: String="An order is still ongoing"):Exception() {
}