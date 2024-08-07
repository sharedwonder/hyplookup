/*
 * Copyright (C) 2024 sharedwonder (Liu Baihao).
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package net.sharedwonder.mc.hyplookup.utils

import java.io.Serial
import java.util.UUID
import com.google.gson.Gson
import net.sharedwonder.mc.hyplookup.HypLookup
import net.sharedwonder.mc.hyplookup.data.HypixelPlayerData
import net.sharedwonder.mc.hyplookup.data.NicknamePlayer
import net.sharedwonder.mc.hyplookup.data.RealPlayerData
import net.sharedwonder.mc.ptbridge.http.HTTPRequestUtils

object HypixelAPI {
    private val GSON = Gson()

    private val baseUrl = HypLookup.CONFIG.hypixelApiBaseUrl

    private val key = HypLookup.CONFIG.hypixelApiKey

    private val userAgent = HypLookup.CONFIG.hypixelApiUserAgent

    @JvmStatic
    fun fetchPlayerData(uuid: UUID): HypixelPlayerData {
        val map = (HTTPRequestUtils.request("$baseUrl/player?uuid=$uuid") {
            if (key != null) {
                setRequestProperty("API-Key", key)
            }
            if (userAgent != null) {
                setRequestProperty("User-Agent", userAgent)
            }
        }.ifErrorResponse {
            throw if (response.status == 429) ThrottlingException() else buildException("Failed to access Hypixel API")
        }.ifInterruptedByException {
            throw exception
        }.response).let {
            @Suppress("UNCHECKED_CAST")
            GSON.fromJson(it.contentAsUtf8String, Map::class.java)["player"] as Map<String, Any>?
        }

        return if (map == null) NicknamePlayer else RealPlayerData.build(map)
    }

    class ThrottlingException : RuntimeException() {
        private companion object {
            @Serial private const val serialVersionUID: Long = -5299607313553938526L
        }
    }
}
