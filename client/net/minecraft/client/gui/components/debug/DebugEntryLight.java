package net.minecraft.client.gui.components.debug;

import java.util.List;
import net.minecraft.SharedConstants;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.lighting.LevelLightEngine;
import org.jspecify.annotations.Nullable;

public class DebugEntryLight implements DebugScreenEntry {
   public static final Identifier GROUP = Identifier.withDefaultNamespace("light");

   public DebugEntryLight() {
      super();
   }

   public void display(DebugScreenDisplayer var1, @Nullable Level var2, @Nullable LevelChunk var3, @Nullable LevelChunk var4) {
      Minecraft var5 = Minecraft.getInstance();
      Entity var6 = var5.getCameraEntity();
      if (var6 != null && var5.level != null) {
         BlockPos var7 = var6.blockPosition();
         int var8 = var5.level.getChunkSource().getLightEngine().getRawBrightness(var7, 0);
         int var9 = var5.level.getBrightness(LightLayer.SKY, var7);
         int var10 = var5.level.getBrightness(LightLayer.BLOCK, var7);
         String var11 = "Client Light: " + var8 + " (" + var9 + " sky, " + var10 + " block)";
         if (SharedConstants.DEBUG_SHOW_SERVER_DEBUG_VALUES) {
            String var12;
            if (var4 != null) {
               LevelLightEngine var13 = var4.getLevel().getLightEngine();
               var12 = "Server Light: (" + var13.getLayerListener(LightLayer.SKY).getLightValue(var7) + " sky, " + var13.getLayerListener(LightLayer.BLOCK).getLightValue(var7) + " block)";
            } else {
               var12 = "Server Light: (?? sky, ?? block)";
            }

            var1.addToGroup(GROUP, List.of(var11, var12));
         } else {
            var1.addToGroup(GROUP, var11);
         }

      }
   }
}
