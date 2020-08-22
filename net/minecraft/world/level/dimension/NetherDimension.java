package net.minecraft.world.level.dimension;

import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.BiomeSourceType;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.biome.FixedBiomeSourceSettings;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.border.WorldBorder;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.chunk.ChunkGeneratorType;
import net.minecraft.world.level.levelgen.NetherGeneratorSettings;
import net.minecraft.world.phys.Vec3;

public class NetherDimension extends Dimension {
   private static final Vec3 NETHER_FOG_COLOR = new Vec3(0.20000000298023224D, 0.029999999329447746D, 0.029999999329447746D);

   public NetherDimension(Level var1, DimensionType var2) {
      super(var1, var2, 0.1F);
      this.ultraWarm = true;
      this.hasCeiling = true;
   }

   public Vec3 getFogColor(float var1, float var2) {
      return NETHER_FOG_COLOR;
   }

   public ChunkGenerator createRandomLevelGenerator() {
      NetherGeneratorSettings var1 = (NetherGeneratorSettings)ChunkGeneratorType.CAVES.createSettings();
      var1.setDefaultBlock(Blocks.NETHERRACK.defaultBlockState());
      var1.setDefaultFluid(Blocks.LAVA.defaultBlockState());
      return ChunkGeneratorType.CAVES.create(this.level, BiomeSourceType.FIXED.create(((FixedBiomeSourceSettings)BiomeSourceType.FIXED.createSettings(this.level.getLevelData())).setBiome(Biomes.NETHER)), var1);
   }

   public boolean isNaturalDimension() {
      return false;
   }

   @Nullable
   public BlockPos getSpawnPosInChunk(ChunkPos var1, boolean var2) {
      return null;
   }

   @Nullable
   public BlockPos getValidSpawnPosition(int var1, int var2, boolean var3) {
      return null;
   }

   public float getTimeOfDay(long var1, float var3) {
      return 0.5F;
   }

   public boolean mayRespawn() {
      return false;
   }

   public boolean isFoggyAt(int var1, int var2) {
      return true;
   }

   public WorldBorder createWorldBorder() {
      return new WorldBorder() {
         public double getCenterX() {
            return super.getCenterX() / 8.0D;
         }

         public double getCenterZ() {
            return super.getCenterZ() / 8.0D;
         }
      };
   }

   public DimensionType getType() {
      return DimensionType.NETHER;
   }
}
