package net.minecraft.network.protocol.game;

import net.minecraft.network.ServerboundPacketListener;

public interface ServerPacketListener extends ServerboundPacketListener {
   default boolean shouldPropagateHandlingExceptions() {
      return false;
   }
}
