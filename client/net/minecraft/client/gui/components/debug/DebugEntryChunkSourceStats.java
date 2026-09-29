package net.minecraft.client.gui.components.debug;

import net.minecraft.client.Minecraft;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.entity.TransientEntitySectionManager;
import org.jspecify.annotations.Nullable;

public class DebugEntryChunkSourceStats implements DebugScreenEntry {
   public DebugEntryChunkSourceStats() {
      super();
   }

   public void display(final DebugScreenDisplayer displayer, final @Nullable Level serverOrClientLevel, final @Nullable LevelChunk clientChunk, final @Nullable LevelChunk serverChunk) {
      Minecraft minecraft = Minecraft.getInstance();
      if (minecraft.level != null) {
         displayer.addFactToGroup(DebugGroups.PERFORMANCE_IMPACTORS, "Client Chunks", (fact) -> fact.value(minecraft.level.getChunkSource().getLoadedChunksCount()).text(" loaded (").value(minecraft.level.getChunkSource().getMaxChunksCount()).text(" max)"));
         TransientEntitySectionManager<Entity> entityStorage = minecraft.level.getEntityStorage();
         displayer.addFactToGroup(DebugGroups.PERFORMANCE_IMPACTORS, "Client Entities", (fact) -> fact.value(entityStorage.count()).text(" in ").value(entityStorage.sectionCount()).text(" sections (").value(entityStorage.tickingCount()).text(" ticking chunks)"));
      }

      if (serverOrClientLevel instanceof ServerLevel serverLevel) {
         displayer.addFactToGroup(DebugGroups.PERFORMANCE_IMPACTORS, "Server Chunks", (fact) -> fact.value(serverLevel.getChunkSource().getLoadedChunksCount()).value(" loaded"));
         displayer.addFactToGroup(DebugGroups.PERFORMANCE_IMPACTORS, "Server Entities", (fact) -> fact.value(serverLevel.getEntityManager().gatherStats()));
      }

   }

   public boolean isAllowed(final boolean reducedDebugInfo) {
      return true;
   }
}
