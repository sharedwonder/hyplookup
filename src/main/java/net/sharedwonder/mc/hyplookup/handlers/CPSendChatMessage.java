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

import io.netty.buffer.ByteBuf;
import net.sharedwonder.mc.hyplookup.Constants;
import net.sharedwonder.mc.hyplookup.HypLookupContext;
import net.sharedwonder.mc.hyplookup.command.CommandParser;
import net.sharedwonder.mc.ptbridge.ConnectionContext;
import net.sharedwonder.mc.ptbridge.packet.C2SPacketHandler;
import net.sharedwonder.mc.ptbridge.packet.HandledFlag;
import net.sharedwonder.mc.ptbridge.packet.PacketUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public final class CPSendChatMessage implements C2SPacketHandler {
    @Override
    public int getId() {
        return Constants.PID_CP_SEND_CHAT_MESSAGE;
    }

    @Override
    public HandledFlag handle(ConnectionContext context, ByteBuf in, ByteBuf transformed) {
        var message = PacketUtils.readUtf8String(in);

        if (message.charAt(0) == Constants.COMMAND_PREFIX) {
            var hypLookupContext = context.getExternalContext(HypLookupContext.class);
            var parser = new CommandParser(message, hypLookupContext);
            if (parser.isMatched()) {
                LOGGER.info(context.getPlayerUsername() + " issued a HypLookup command: " + message);
                try {
                    hypLookupContext.runCommand(parser);
                } catch (IllegalStateException exception) {
                    LOGGER.error("Failed to run HypLookup command: " + message, exception);
                }
                return HandledFlag.BLOCKED;
            }
        }

        return HandledFlag.PASSED;
    }

    private static final Logger LOGGER = LogManager.getLogger(CPSendChatMessage.class);
}
