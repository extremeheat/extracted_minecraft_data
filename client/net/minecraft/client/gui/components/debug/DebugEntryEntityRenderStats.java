package net.minecraft.client.gui.components.debug;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.extract.LevelExtractor;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.chunk.LevelChunk;
import org.jspecify.annotations.Nullable;

public class DebugEntryEntityRenderStats implements DebugScreenEntry {
   public DebugEntryEntityRenderStats() {
      super();
   }

   public void display(final DebugScreenDisplayer displayer, final @Nullable Level serverOrClientLevel, final @Nullable LevelChunk clientChunk, final @Nullable LevelChunk serverChunk) {
      ClientLevel clientLevel = Minecraft.getInstance().level;
      if (clientLevel != null) {
         LevelExtractor levelExtractor = Minecraft.getInstance().levelExtractor;
         displayer.addFactToGroup(DebugGroups.MISC, "Entities", (fact) -> fact.value(levelExtractor.getRenderedEntityCount()).text(" / ").value(levelExtractor.getTotalEntityCount()));
         displayer.addFactToGroup(DebugGroups.MISC, "Simulation Distance", (fact) -> fact.value(clientLevel.getServerSimulationDistance()));
      }
   }

   public boolean isAllowed(final boolean reducedDebugInfo) {
      return true;
   }
}
