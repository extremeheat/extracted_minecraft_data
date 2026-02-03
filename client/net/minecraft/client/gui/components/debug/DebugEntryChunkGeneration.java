package net.minecraft.client.gui.components.debug;

import java.util.ArrayList;
import java.util.List;
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

   public void display(final DebugScreenDisplayer displayer, final @Nullable Level serverOrClientLevel, final @Nullable LevelChunk clientChunk, final @Nullable LevelChunk serverChunk) {
      Minecraft minecraft = Minecraft.getInstance();
      Entity entity = minecraft.getCameraEntity();
      ServerLevel serverLevel = serverOrClientLevel instanceof ServerLevel ? (ServerLevel)serverOrClientLevel : null;
      if (entity != null && serverLevel != null) {
         BlockPos feetPos = entity.blockPosition();
         ServerChunkCache chunkSource = serverLevel.getChunkSource();
         List<String> result = new ArrayList();
         ChunkGenerator generator = chunkSource.getGenerator();
         RandomState randomState = chunkSource.randomState();
         generator.addDebugScreenInfo(result, randomState, feetPos);
         Climate.Sampler sampler = randomState.sampler();
         BiomeSource biomeSource = generator.getBiomeSource();
         biomeSource.addDebugInfo(result, feetPos, sampler);
         if (serverChunk != null && serverChunk.isOldNoiseGeneration()) {
            result.add("Blending: Old");
         }

         displayer.addToGroup(GROUP, result);
      }
   }
}
