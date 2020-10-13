package com.appsci.panda.sdk.data.subscriptions.google

class InvalidIntentException(
        action: String,
        packageName: String
) : Exception("Invalid Intent params found:" +
        "\naction=$action\npackage=$packageName")
