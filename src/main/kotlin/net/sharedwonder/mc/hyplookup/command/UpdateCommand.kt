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

package net.sharedwonder.mc.hyplookup.command

import net.sharedwonder.mc.hyplookup.utils.HypLookupContext
import net.sharedwonder.mc.ptbridge.ConnectionContext

object UpdateCommand : Command {
    override val expressions: Array<String> = arrayOf("update", "u", "up")

    override val description: String = "Clears cache to update player data"

    override fun run(connectionContext: ConnectionContext, hypLookupContext: HypLookupContext, args: List<String>): String {
        hypLookupContext.playerListDisplay.fullUpdate()
        return "Cache has been cleared, player data will be updated."
    }
}
