package net.minecraft.world.level.levelgen.placement;

import java.util.BitSet;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.LevelHeightAccessor;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.chunk.ProtoChunk;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.Heightmap;

public class DecorationContext implements LevelHeightAccessor {
   private final WorldGenLevel level;
   private final ChunkGenerator generator;

   public DecorationContext(WorldGenLevel var1, ChunkGenerator var2) {
      super();
      this.level = var1;
      this.generator = var2;
   }

   public int getHeight(Heightmap.Types var1, int var2, int var3) {
      return this.level.getHeight(var1, var2, var3);
   }

   public int getGenDepth() {
      return this.generator.getGenDepth();
   }

   public int getSeaLevel() {
      return this.generator.getSeaLevel();
   }

   public BitSet getCarvingMask(ChunkPos var1, GenerationStep.Carving var2) {
      return ((ProtoChunk)this.level.getChunk(var1.x, var1.z)).getOrCreateCarvingMask(var2);
   }

   public BlockState getBlockState(BlockPos var1) {
      return this.level.getBlockState(var1);
   }

   public int getSectionsCount() {
      return this.level.getSectionsCount();
   }

   public int getMinSection() {
      return this.level.getMinSection();
   }
}
