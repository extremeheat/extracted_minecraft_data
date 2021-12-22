package net.minecraft.world.level.levelgen;

import com.google.common.collect.Lists;
import it.unimi.dsi.fastutil.longs.Long2IntMap;
import it.unimi.dsi.fastutil.longs.Long2IntOpenHashMap;
import java.util.List;
import java.util.function.Supplier;
import javax.annotation.Nullable;
import net.minecraft.core.QuartPos;
import net.minecraft.util.Mth;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.levelgen.blending.Blender;

public class NoiseChunk {
   private final NoiseSampler sampler;
   final NoiseSettings noiseSettings;
   final int cellCountXZ;
   final int cellCountY;
   final int cellNoiseMinY;
   final int firstCellX;
   final int firstCellZ;
   private final int firstNoiseX;
   private final int firstNoiseZ;
   final List<NoiseChunk.NoiseInterpolator> interpolators;
   private final NoiseSampler.FlatNoiseData[][] noiseData;
   private final Long2IntMap preliminarySurfaceLevel = new Long2IntOpenHashMap();
   private final Aquifer aquifer;
   private final NoiseChunk.BlockStateFiller baseNoise;
   private final NoiseChunk.BlockStateFiller oreVeins;
   private final Blender blender;

   public static NoiseChunk forChunk(ChunkAccess var0, NoiseSampler var1, Supplier<NoiseChunk.NoiseFiller> var2, NoiseGeneratorSettings var3, Aquifer.FluidPicker var4, Blender var5) {
      ChunkPos var6 = var0.getPos();
      NoiseSettings var7 = var3.noiseSettings();
      int var8 = Math.max(var7.minY(), var0.getMinBuildHeight());
      int var9 = Math.min(var7.minY() + var7.height(), var0.getMaxBuildHeight());
      int var10 = Mth.intFloorDiv(var8, var7.getCellHeight());
      int var11 = Mth.intFloorDiv(var9 - var8, var7.getCellHeight());
      return new NoiseChunk(16 / var7.getCellWidth(), var11, var10, var1, var6.getMinBlockX(), var6.getMinBlockZ(), (NoiseChunk.NoiseFiller)var2.get(), var3, var4, var5);
   }

   public static NoiseChunk forColumn(int var0, int var1, int var2, int var3, NoiseSampler var4, NoiseGeneratorSettings var5, Aquifer.FluidPicker var6) {
      return new NoiseChunk(1, var3, var2, var4, var0, var1, (var0x, var1x, var2x) -> {
         return 0.0D;
      }, var5, var6, Blender.empty());
   }

   private NoiseChunk(int var1, int var2, int var3, NoiseSampler var4, int var5, int var6, NoiseChunk.NoiseFiller var7, NoiseGeneratorSettings var8, Aquifer.FluidPicker var9, Blender var10) {
      super();
      this.noiseSettings = var8.noiseSettings();
      this.cellCountXZ = var1;
      this.cellCountY = var2;
      this.cellNoiseMinY = var3;
      this.sampler = var4;
      int var11 = this.noiseSettings.getCellWidth();
      this.firstCellX = Math.floorDiv(var5, var11);
      this.firstCellZ = Math.floorDiv(var6, var11);
      this.interpolators = Lists.newArrayList();
      this.firstNoiseX = QuartPos.fromBlock(var5);
      this.firstNoiseZ = QuartPos.fromBlock(var6);
      int var12 = QuartPos.fromBlock(var1 * var11);
      this.noiseData = new NoiseSampler.FlatNoiseData[var12 + 1][];
      this.blender = var10;

      for(int var13 = 0; var13 <= var12; ++var13) {
         int var14 = this.firstNoiseX + var13;
         this.noiseData[var13] = new NoiseSampler.FlatNoiseData[var12 + 1];

         for(int var15 = 0; var15 <= var12; ++var15) {
            int var16 = this.firstNoiseZ + var15;
            this.noiseData[var13][var15] = var4.noiseData(var14, var16, var10);
         }
      }

      this.aquifer = var4.createAquifer(this, var5, var6, var3, var2, var9, var8.isAquifersEnabled());
      this.baseNoise = var4.makeBaseNoiseFiller(this, var7, var8.isNoodleCavesEnabled());
      this.oreVeins = var4.makeOreVeinifier(this, var8.isOreVeinsEnabled());
   }

   public NoiseSampler.FlatNoiseData noiseData(int var1, int var2) {
      return this.noiseData[var1 - this.firstNoiseX][var2 - this.firstNoiseZ];
   }

   public int preliminarySurfaceLevel(int var1, int var2) {
      return this.preliminarySurfaceLevel.computeIfAbsent(ChunkPos.asLong(QuartPos.fromBlock(var1), QuartPos.fromBlock(var2)), this::computePreliminarySurfaceLevel);
   }

   private int computePreliminarySurfaceLevel(long var1) {
      int var3 = ChunkPos.getX(var1);
      int var4 = ChunkPos.getZ(var1);
      int var5 = var3 - this.firstNoiseX;
      int var6 = var4 - this.firstNoiseZ;
      int var7 = this.noiseData.length;
      TerrainInfo var8;
      if (var5 >= 0 && var6 >= 0 && var5 < var7 && var6 < var7) {
         var8 = this.noiseData[var5][var6].terrainInfo();
      } else {
         var8 = this.sampler.noiseData(var3, var4, this.blender).terrainInfo();
      }

      return this.sampler.getPreliminarySurfaceLevel(QuartPos.toBlock(var3), QuartPos.toBlock(var4), var8);
   }

   protected NoiseChunk.NoiseInterpolator createNoiseInterpolator(NoiseChunk.NoiseFiller var1) {
      return new NoiseChunk.NoiseInterpolator(var1);
   }

   public Blender getBlender() {
      return this.blender;
   }

   public void initializeForFirstCellX() {
      this.interpolators.forEach((var0) -> {
         var0.initializeForFirstCellX();
      });
   }

   public void advanceCellX(int var1) {
      this.interpolators.forEach((var1x) -> {
         var1x.advanceCellX(var1);
      });
   }

   public void selectCellYZ(int var1, int var2) {
      this.interpolators.forEach((var2x) -> {
         var2x.selectCellYZ(var1, var2);
      });
   }

   public void updateForY(double var1) {
      this.interpolators.forEach((var2) -> {
         var2.updateForY(var1);
      });
   }

   public void updateForX(double var1) {
      this.interpolators.forEach((var2) -> {
         var2.updateForX(var1);
      });
   }

   public void updateForZ(double var1) {
      this.interpolators.forEach((var2) -> {
         var2.updateForZ(var1);
      });
   }

   public void swapSlices() {
      this.interpolators.forEach(NoiseChunk.NoiseInterpolator::swapSlices);
   }

   public Aquifer aquifer() {
      return this.aquifer;
   }

   @Nullable
   protected BlockState updateNoiseAndGenerateBaseState(int var1, int var2, int var3) {
      return this.baseNoise.calculate(var1, var2, var3);
   }

   @Nullable
   protected BlockState oreVeinify(int var1, int var2, int var3) {
      return this.oreVeins.calculate(var1, var2, var3);
   }

   @FunctionalInterface
   public interface NoiseFiller {
      double calculateNoise(int var1, int var2, int var3);
   }

   @FunctionalInterface
   public interface BlockStateFiller {
      @Nullable
      BlockState calculate(int var1, int var2, int var3);
   }

   public class NoiseInterpolator implements NoiseChunk.Sampler {
      private double[][] slice0;
      private double[][] slice1;
      private final NoiseChunk.NoiseFiller noiseFiller;
      private double noise000;
      private double noise001;
      private double noise100;
      private double noise101;
      private double noise010;
      private double noise011;
      private double noise110;
      private double noise111;
      private double valueXZ00;
      private double valueXZ10;
      private double valueXZ01;
      private double valueXZ11;
      private double valueZ0;
      private double valueZ1;
      private double value;

      NoiseInterpolator(NoiseChunk.NoiseFiller var2) {
         super();
         this.noiseFiller = var2;
         this.slice0 = this.allocateSlice(NoiseChunk.this.cellCountY, NoiseChunk.this.cellCountXZ);
         this.slice1 = this.allocateSlice(NoiseChunk.this.cellCountY, NoiseChunk.this.cellCountXZ);
         NoiseChunk.this.interpolators.add(this);
      }

      private double[][] allocateSlice(int var1, int var2) {
         int var3 = var2 + 1;
         int var4 = var1 + 1;
         double[][] var5 = new double[var3][var4];

         for(int var6 = 0; var6 < var3; ++var6) {
            var5[var6] = new double[var4];
         }

         return var5;
      }

      void initializeForFirstCellX() {
         this.fillSlice(this.slice0, NoiseChunk.this.firstCellX);
      }

      void advanceCellX(int var1) {
         this.fillSlice(this.slice1, NoiseChunk.this.firstCellX + var1 + 1);
      }

      private void fillSlice(double[][] var1, int var2) {
         int var3 = NoiseChunk.this.noiseSettings.getCellWidth();
         int var4 = NoiseChunk.this.noiseSettings.getCellHeight();

         for(int var5 = 0; var5 < NoiseChunk.this.cellCountXZ + 1; ++var5) {
            int var6 = NoiseChunk.this.firstCellZ + var5;

            for(int var7 = 0; var7 < NoiseChunk.this.cellCountY + 1; ++var7) {
               int var8 = var7 + NoiseChunk.this.cellNoiseMinY;
               int var9 = var8 * var4;
               double var10 = this.noiseFiller.calculateNoise(var2 * var3, var9, var6 * var3);
               var1[var5][var7] = var10;
            }
         }

      }

      void selectCellYZ(int var1, int var2) {
         this.noise000 = this.slice0[var2][var1];
         this.noise001 = this.slice0[var2 + 1][var1];
         this.noise100 = this.slice1[var2][var1];
         this.noise101 = this.slice1[var2 + 1][var1];
         this.noise010 = this.slice0[var2][var1 + 1];
         this.noise011 = this.slice0[var2 + 1][var1 + 1];
         this.noise110 = this.slice1[var2][var1 + 1];
         this.noise111 = this.slice1[var2 + 1][var1 + 1];
      }

      void updateForY(double var1) {
         this.valueXZ00 = Mth.lerp(var1, this.noise000, this.noise010);
         this.valueXZ10 = Mth.lerp(var1, this.noise100, this.noise110);
         this.valueXZ01 = Mth.lerp(var1, this.noise001, this.noise011);
         this.valueXZ11 = Mth.lerp(var1, this.noise101, this.noise111);
      }

      void updateForX(double var1) {
         this.valueZ0 = Mth.lerp(var1, this.valueXZ00, this.valueXZ10);
         this.valueZ1 = Mth.lerp(var1, this.valueXZ01, this.valueXZ11);
      }

      void updateForZ(double var1) {
         this.value = Mth.lerp(var1, this.valueZ0, this.valueZ1);
      }

      public double sample() {
         return this.value;
      }

      private void swapSlices() {
         double[][] var1 = this.slice0;
         this.slice0 = this.slice1;
         this.slice1 = var1;
      }
   }

   @FunctionalInterface
   public interface Sampler {
      double sample();
   }

   @FunctionalInterface
   public interface InterpolatableNoise {
      NoiseChunk.Sampler instantiate(NoiseChunk var1);
   }
}
