package net.minecraft.client.gui.components.debug;

import java.util.function.BiConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerChunkCache;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.BiomeSource;
import net.minecraft.world.level.biome.Climate;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.levelgen.RandomState;
import net.minecraft.world.level.levelgen.densityfunction.SamplerContext;
import org.jspecify.annotations.Nullable;

public class DebugEntryChunkGeneration implements DebugScreenEntry {
   public DebugEntryChunkGeneration() {
      super();
   }

   public void display(final DebugScreenDisplayer displayer, final @Nullable Level serverOrClientLevel, final @Nullable LevelChunk clientChunk, final @Nullable LevelChunk serverChunk) {
      Minecraft minecraft = Minecraft.getInstance();
      Entity entity = minecraft.getCameraEntity();
      ServerLevel var10000;
      if (serverOrClientLevel instanceof ServerLevel level) {
         var10000 = level;
      } else {
         var10000 = null;
      }

      ServerLevel serverLevel = var10000;
      if (entity != null && serverLevel != null) {
         BlockPos feetPos = entity.blockPosition();
         this.update(serverChunk, feetPos, serverLevel, displayer);
      }
   }

   private void update(final @Nullable LevelChunk serverChunk, final BlockPos feetPos, final ServerLevel serverLevel, final DebugScreenDisplayer displayer) {
      ServerChunkCache chunkSource = serverLevel.getChunkSource();
      SamplerContext samplerContext = SamplerContext.builder().enableCaches().build();
      ChunkGenerator generator = chunkSource.getGenerator();
      RandomState randomState = chunkSource.randomState();
      BiConsumer<String, String> addFact = (key, value) -> displayer.addFactToGroup(DebugGroups.CHUNK_GENERATION, key, (fact) -> fact.value(value));
      generator.addDebugScreenInfo(addFact, randomState, feetPos, samplerContext);
      Climate.Sampler sampler = randomState.createClimateSampler(samplerContext);
      BiomeSource biomeSource = generator.getBiomeSource();
      biomeSource.addDebugInfo(addFact, feetPos, sampler);
      if (serverChunk != null && serverChunk.isOldNoiseGeneration()) {
         addFact.accept("Blending", "Old");
      }

   }
}
