package net.minecraft.client.gui.components.debug;

import java.util.Locale;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.client.server.IntegratedServer;
import net.minecraft.network.Connection;
import net.minecraft.server.ServerTickRateManager;
import net.minecraft.world.TickRateManager;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.chunk.LevelChunk;
import org.jspecify.annotations.Nullable;

public class DebugEntryTps implements DebugScreenEntry {
   public DebugEntryTps() {
      super();
   }

   public void display(DebugScreenDisplayer var1, @Nullable Level var2, @Nullable LevelChunk var3, @Nullable LevelChunk var4) {
      Minecraft var5 = Minecraft.getInstance();
      IntegratedServer var6 = var5.getSingleplayerServer();
      ClientPacketListener var7 = var5.getConnection();
      if (var7 != null && var2 != null) {
         Connection var8 = var7.getConnection();
         float var9 = var8.getAverageSentPackets();
         float var10 = var8.getAverageReceivedPackets();
         TickRateManager var12 = var2.tickRateManager();
         String var11;
         if (var12.isSteppingForward()) {
            var11 = " (frozen - stepping)";
         } else if (var12.isFrozen()) {
            var11 = " (frozen)";
         } else {
            var11 = "";
         }

         String var13;
         if (var6 != null) {
            ServerTickRateManager var14 = var6.tickRateManager();
            boolean var15 = var14.isSprinting();
            if (var15) {
               var11 = " (sprinting)";
            }

            String var16 = var15 ? "-" : String.format(Locale.ROOT, "%.1f", var12.millisecondsPerTick());
            var13 = String.format(Locale.ROOT, "Integrated server @ %.1f/%s ms%s, %.0f tx, %.0f rx", var6.getCurrentSmoothedTickTime(), var16, var11, var9, var10);
         } else {
            var13 = String.format(Locale.ROOT, "\"%s\" server%s, %.0f tx, %.0f rx", var7.serverBrand(), var11, var9, var10);
         }

         var1.addLine(var13);
      }
   }

   public boolean isAllowed(boolean var1) {
      return true;
   }
}
