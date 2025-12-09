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

   public void display(DebugScreenDisplayer var1, @Nullable Level var2, @Nullable LevelChunk var3, @Nullable LevelChunk var4) {
      Minecraft var5 = Minecraft.getInstance();
      Entity var6 = var5.getCameraEntity();
      if (var6 != null && var5.level != null) {
         BlockPos var7 = var6.blockPosition();
         if (var5.level.isInsideBuildHeight(var7.getY())) {
            if (SharedConstants.DEBUG_SHOW_SERVER_DEBUG_VALUES && var2 instanceof ServerLevel) {
               Identifier var8 = GROUP;
               String var10002 = "Biome: " + printBiome(var5.level.getBiome(var7));
               Holder var10003 = var2.getBiome(var7);
               var1.addToGroup(var8, List.of(var10002, "Server Biome: " + printBiome(var10003)));
            } else {
               Holder var10001 = var5.level.getBiome(var7);
               var1.addLine("Biome: " + printBiome(var10001));
            }
         }

      }
   }

   private static String printBiome(Holder<Biome> var0) {
      return (String)var0.unwrap().map((var0x) -> var0x.identifier().toString(), (var0x) -> "[unregistered " + String.valueOf(var0x) + "]");
   }
}
