package org.example.cli

data class Request(
    val operation: String,
    val inputSource: String,
    val version: String,
    val configSource: String?
)
