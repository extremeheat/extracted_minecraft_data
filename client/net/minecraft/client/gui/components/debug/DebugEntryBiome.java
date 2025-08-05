package net.minecraft.client.gui.components.debug;

import javax.annotation.Nullable;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.chunk.LevelChunk;

public class DebugEntryBiome implements DebugScreenEntry {
   private static final ResourceLocation GROUP = ResourceLocation.withDefaultNamespace("biome");

   public DebugEntryBiome() {
      super();
   }

   public void display(DebugScreenDisplayer var1, @Nullable Level var2, @Nullable LevelChunk var3, @Nullable LevelChunk var4) {
      Minecraft var5 = Minecraft.getInstance();
      Entity var6 = var5.getCameraEntity();
      if (var6 != null && var5.level != null) {
         BlockPos var7 = var6.blockPosition();
         if (var5.level.isInsideBuildHeight(var7.getY())) {
            Holder var10001 = var5.level.getBiome(var7);
            var1.addLine("Biome: " + printBiome(var10001));
         }

      }
   }

   private static String printBiome(Holder<Biome> var0) {
      return (String)var0.unwrap().map((var0x) -> var0x.location().toString(), (var0x) -> "[unregistered " + String.valueOf(var0x) + "]");
   }
}
