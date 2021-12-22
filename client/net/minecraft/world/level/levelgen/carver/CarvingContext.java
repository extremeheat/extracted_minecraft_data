package net.minecraft.world.level.levelgen.carver;

import java.util.Optional;
import java.util.function.Function;
import net.minecraft.core.BlockPos;
import net.minecraft.core.RegistryAccess;
import net.minecraft.world.level.LevelHeightAccessor;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.levelgen.NoiseBasedChunkGenerator;
import net.minecraft.world.level.levelgen.NoiseChunk;
import net.minecraft.world.level.levelgen.WorldGenerationContext;

public class CarvingContext extends WorldGenerationContext {
   private final NoiseBasedChunkGenerator generator;
   private final RegistryAccess registryAccess;
   private final NoiseChunk noiseChunk;

   public CarvingContext(NoiseBasedChunkGenerator var1, RegistryAccess var2, LevelHeightAccessor var3, NoiseChunk var4) {
      super(var1, var3);
      this.generator = var1;
      this.registryAccess = var2;
      this.noiseChunk = var4;
   }

   /** @deprecated */
   @Deprecated
   public Optional<BlockState> topMaterial(Function<BlockPos, Biome> var1, ChunkAccess var2, BlockPos var3, boolean var4) {
      return this.generator.topMaterial(this, var1, var2, this.noiseChunk, var3, var4);
   }

   /** @deprecated */
   @Deprecated
   public RegistryAccess registryAccess() {
      return this.registryAccess;
   }
}
