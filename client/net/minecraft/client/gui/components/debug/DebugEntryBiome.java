package net.minecraft.client.gui.components.debug;

import java.util.List;
import net.minecraft.SharedConstants;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.chunk.LevelChunk;
import org.jspecify.annotations.Nullable;

public class DebugEntryBiome implements DebugScreenEntry {
   private static final Identifier GROUP = Identifier.withDefaultNamespace("biome");

   public DebugEntryBiome() {
      super();
   }

   public void display(final DebugScreenDisplayer displayer, final @Nullable Level serverOrClientLevel, final @Nullable LevelChunk clientChunk, final @Nullable LevelChunk serverChunk) {
      Minecraft minecraft = Minecraft.getInstance();
      Entity entity = minecraft.getCameraEntity();
      if (entity != null && minecraft.level != null) {
         BlockPos feetPos = entity.blockPosition();
         if (minecraft.level.isInsideBuildHeight(feetPos.getY())) {
            if (SharedConstants.DEBUG_SHOW_SERVER_DEBUG_VALUES && serverOrClientLevel instanceof ServerLevel) {
               Identifier var8 = GROUP;
               String var10002 = "Biome: " + printBiome(minecraft.level.getBiome(feetPos));
               Holder var10003 = serverOrClientLevel.getBiome(feetPos);
               displayer.addToGroup(var8, List.of(var10002, "Server Biome: " + printBiome(var10003)));
            } else {
               Holder var10001 = minecraft.level.getBiome(feetPos);
               displayer.addLine("Biome: " + printBiome(var10001));
            }
         }

      }
   }

   private static String printBiome(final Holder<Biome> biome) {
      return (String)biome.unwrap().map((key) -> key.identifier().toString(), (l) -> "[unregistered " + String.valueOf(l) + "]");
   }
}
