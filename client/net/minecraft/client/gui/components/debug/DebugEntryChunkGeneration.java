package net.minecraft.client.gui.components.debug;

import java.util.ArrayList;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerChunkCache;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.BiomeSource;
import net.minecraft.world.level.biome.Climate;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.levelgen.RandomState;
import org.jspecify.annotations.Nullable;

public class DebugEntryChunkGeneration implements DebugScreenEntry {
   private static final Identifier GROUP = Identifier.withDefaultNamespace("chunk_generation");

   public DebugEntryChunkGeneration() {
      super();
   }

   public void display(DebugScreenDisplayer var1, @Nullable Level var2, @Nullable LevelChunk var3, @Nullable LevelChunk var4) {
      Minecraft var5 = Minecraft.getInstance();
      Entity var6 = var5.getCameraEntity();
      ServerLevel var7 = var2 instanceof ServerLevel ? (ServerLevel)var2 : null;
      if (var6 != null && var7 != null) {
         BlockPos var8 = var6.blockPosition();
         ServerChunkCache var9 = var7.getChunkSource();
         ArrayList var10 = new ArrayList();
         ChunkGenerator var11 = var9.getGenerator();
         RandomState var12 = var9.randomState();
         var11.addDebugScreenInfo(var10, var12, var8);
         Climate.Sampler var13 = var12.sampler();
         BiomeSource var14 = var11.getBiomeSource();
         var14.addDebugInfo(var10, var8, var13);
         if (var4 != null && var4.isOldNoiseGeneration()) {
            var10.add("Blending: Old");
         }

         var1.addToGroup(GROUP, var10);
      }
   }
}
