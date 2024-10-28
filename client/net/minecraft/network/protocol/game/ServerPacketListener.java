package net.minecraft.network.protocol.game;

import com.mojang.logging.LogUtils;
import net.minecraft.ReportedException;
import net.minecraft.network.ServerboundPacketListener;
import net.minecraft.network.protocol.Packet;
import org.slf4j.Logger;

public interface ServerPacketListener extends ServerboundPacketListener {
   Logger LOGGER = LogUtils.getLogger();

   default void onPacketError(Packet var1, Exception var2) throws ReportedException {
      LOGGER.error("Failed to handle packet {}, suppressing error", var1, var2);
   }
}
