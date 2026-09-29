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

   public void display(final DebugScreenDisplayer displayer, final @Nullable Level serverOrClientLevel, final @Nullable LevelChunk clientChunk, final @Nullable LevelChunk serverChunk) {
      Minecraft minecraft = Minecraft.getInstance();
      IntegratedServer server = minecraft.getSingleplayerServer();
      ClientPacketListener connectionListener = minecraft.getConnection();
      if (connectionListener != null && serverOrClientLevel != null) {
         Connection connection = connectionListener.getConnection();
         float averageSentPackets = connection.getAverageSentPackets();
         float averageReceivedPackets = connection.getAverageReceivedPackets();
         displayer.addFactToGroup(DebugGroups.MISC, "Server", (fact) -> {
            TickRateManager tickRateManager = serverOrClientLevel.tickRateManager();
            String runStatus;
            if (tickRateManager.isSteppingForward()) {
               runStatus = "frozen - stepping";
            } else if (tickRateManager.isFrozen()) {
               runStatus = "frozen";
            } else {
               runStatus = "";
            }

            if (server != null) {
               ServerTickRateManager serverTickRateManager = server.tickRateManager();
               boolean isSpriting = serverTickRateManager.isSprinting();
               if (isSpriting) {
                  runStatus = "sprinting";
               }

               String tpsTarget = isSpriting ? "-" : String.format(Locale.ROOT, "%.1f", tickRateManager.millisecondsPerTick());
               fact.value("Integrated").text(" @ ").formattedValue("%.1f", server.getCurrentSmoothedTickTime()).text("/").value(tpsTarget).text(" ms");
            } else {
               fact.text("\"").value(connectionListener.serverBrand()).text("\"");
            }

            if (!runStatus.isEmpty()) {
               fact.text(" (").value(runStatus).text(")");
            }

         });
         displayer.addFactToGroup(DebugGroups.MISC, "Packets", (fact) -> fact.formattedValue("%.0f", averageSentPackets).text(" tx, ").formattedValue("%.0f", averageReceivedPackets).text(" rx"));
      }
   }

   public boolean isAllowed(final boolean reducedDebugInfo) {
      return true;
   }
}
