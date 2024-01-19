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

package net.sharedwonder.mc.hyplookup.handlers;

import net.sharedwonder.mc.hyplookup.utils.Constants;
import net.sharedwonder.mc.hyplookup.utils.HypLookupContext;
import net.sharedwonder.mc.ptbridge.ConnectionContext;
import net.sharedwonder.mc.ptbridge.packet.HandledFlag;
import net.sharedwonder.mc.ptbridge.packet.PacketUtils;
import net.sharedwonder.mc.ptbridge.packet.S2CPacketHandler;
import net.sharedwonder.mc.ptbridge.utils.GsonInstance;
import com.google.gson.JsonObject;
import io.netty.buffer.ByteBuf;
import org.jetbrains.annotations.NotNull;

public final class SPUpdateChatMessage implements S2CPacketHandler {
    @Override
    public int getId() {
        return Constants.PID_SP_UPDATE_CHAT_MESSAGE;
    }

    @Override
    public @NotNull HandledFlag handle(@NotNull ConnectionContext connectionContext, @NotNull ByteBuf in, @NotNull ByteBuf transformed) {
        var hypLookupContext = connectionContext.getExternalContext(HypLookupContext.class);
        var message = PacketUtils.readUtf8String(in);
        var deserialized = GsonInstance.getGson().fromJson(message, JsonObject.class);
        if (hypLookupContext.getDetectedGame() == null) {
            return HandledFlag.PASSED;
        }

        if (deserialized.has("extra")) {
            var extra = deserialized.get("extra").getAsJsonArray();
            var flag = false;
            if (extra.size() > 4) {
                var string = extra.get(4).getAsJsonObject().get("text").getAsString();
                flag = string.startsWith(" has joined") || string.startsWith("加入了游戏");
            } else if (extra.size() > 1) {
                var string = extra.get(1).getAsJsonObject().get("text").getAsString();
                flag = string.startsWith("reconnected") || string.startsWith("重新连接");
            }

            if (flag) {
                hypLookupContext.playerListDisplay.startOverwriting();
            }
        }

        return HandledFlag.PASSED;
    }
}
