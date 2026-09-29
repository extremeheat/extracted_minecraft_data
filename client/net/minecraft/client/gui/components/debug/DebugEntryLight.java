package net.minecraft.client.gui.components.debug;

import net.minecraft.SharedConstants;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.lighting.LevelLightEngine;
import org.jspecify.annotations.Nullable;

public class DebugEntryLight implements DebugScreenEntry {
   public DebugEntryLight() {
      super();
   }

   public void display(final DebugScreenDisplayer displayer, final @Nullable Level serverOrClientLevel, final @Nullable LevelChunk clientChunk, final @Nullable LevelChunk serverChunk) {
      Minecraft minecraft = Minecraft.getInstance();
      Entity entity = minecraft.getCameraEntity();
      if (entity != null && minecraft.level != null) {
         BlockPos feetPos = entity.blockPosition();
         if (SharedConstants.DEBUG_SHOW_SERVER_DEBUG_VALUES) {
            if (serverChunk != null) {
               LevelLightEngine lightEngine = serverChunk.getLevel().getLightEngine();
               displayer.addFactToGroup(DebugGroups.LIGHT, "Server", (fact) -> fact.value(lightEngine.getLayerListener(LightLayer.SKY).getLightValue(feetPos)).text("sky, ").value(lightEngine.getLayerListener(LightLayer.BLOCK).getLightValue(feetPos)).text(" block)"));
            } else {
               displayer.addFactToGroup(DebugGroups.LIGHT, "Server", (fact) -> fact.value("Unavailable"));
            }

            displayer.addFactToGroup(DebugGroups.LIGHT, "Client", (fact) -> populateClientLightFact(fact, minecraft, feetPos));
         } else {
            displayer.addFactToGroup(DebugGroups.MISC, "Light", (fact) -> populateClientLightFact(fact, minecraft, feetPos));
         }

      }
   }

   private static DebugFact populateClientLightFact(final DebugFact fact, final Minecraft minecraft, final BlockPos feetPos) {
      return fact.value(minecraft.level.getChunkSource().getLightEngine().getRawBrightness(feetPos, 0)).text(" (").value(minecraft.level.getBrightness(LightLayer.SKY, feetPos)).text(" sky, ").value(minecraft.level.getBrightness(LightLayer.BLOCK, feetPos)).text(" block)");
   }
}
