package com.nursyai.backend

import android.content.Context
import io.appwrite.Client
import io.appwrite.ID
import io.appwrite.models.Session
import io.appwrite.models.User
import io.appwrite.services.Account

object AppwriteBackend {
    lateinit var client: Client
        private set
    lateinit var account: Account
        private set

    fun init(context: Context) {
        if (::client.isInitialized) return

        client = Client(context.applicationContext)
            .setEndpoint(AppwriteConfig.endpoint)
            .setProject(AppwriteConfig.projectId)

        account = Account(client)
    }

    suspend fun ping() {
        client.ping()
    }

    suspend fun register(email: String, password: String): User<Map<String, Any>> {
        return account.create(
            userId = ID.unique(),
            email = email,
            password = password
        )
    }

    suspend fun login(email: String, password: String): Session {
        return account.createEmailPasswordSession(
            email = email,
            password = password
        )
    }

    suspend fun currentUser(): User<Map<String, Any>> {
        return account.get()
    }

    suspend fun logout() {
        account.deleteSession("current")
    }
}
