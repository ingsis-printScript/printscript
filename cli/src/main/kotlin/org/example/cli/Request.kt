package org.example.cli

data class Request(
    val inputSource: String,
    val version: String,
    val configSource: String
) {}
