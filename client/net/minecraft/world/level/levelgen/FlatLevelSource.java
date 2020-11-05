package net.minecraft.world.level.levelgen;

import com.mojang.serialization.Codec;
import java.util.Arrays;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.WorldGenRegion;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.NoiseColumn;
import net.minecraft.world.level.StructureFeatureManager;
import net.minecraft.world.level.biome.FixedBiomeSource;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.flat.FlatLevelGeneratorSettings;

public class FlatLevelSource extends ChunkGenerator {
   public static final Codec<FlatLevelSource> CODEC;
   private final FlatLevelGeneratorSettings settings;

   public FlatLevelSource(FlatLevelGeneratorSettings var1) {
      super(new FixedBiomeSource(var1.getBiomeFromSettings()), new FixedBiomeSource(var1.getBiome()), var1.structureSettings(), 0L);
      this.settings = var1;
   }

   protected Codec<? extends ChunkGenerator> codec() {
      return CODEC;
   }

   public ChunkGenerator withSeed(long var1) {
      return this;
   }

   public FlatLevelGeneratorSettings settings() {
      return this.settings;
   }

   public void buildSurfaceAndBedrock(WorldGenRegion var1, ChunkAccess var2) {
   }

   public int getSpawnHeight() {
      BlockState[] var1 = this.settings.getLayers();

      for(int var2 = 0; var2 < var1.length; ++var2) {
         BlockState var3 = var1[var2] == null ? Blocks.AIR.defaultBlockState() : var1[var2];
         if (!Heightmap.Types.MOTION_BLOCKING.isOpaque().test(var3)) {
            return var2 - 1;
         }
      }

      return var1.length;
   }

   public void fillFromNoise(LevelAccessor var1, StructureFeatureManager var2, ChunkAccess var3) {
      BlockState[] var4 = this.settings.getLayers();
      BlockPos.MutableBlockPos var5 = new BlockPos.MutableBlockPos();
      Heightmap var6 = var3.getOrCreateHeightmapUnprimed(Heightmap.Types.OCEAN_FLOOR_WG);
      Heightmap var7 = var3.getOrCreateHeightmapUnprimed(Heightmap.Types.WORLD_SURFACE_WG);

      for(int var8 = 0; var8 < var4.length; ++var8) {
         BlockState var9 = var4[var8];
         if (var9 != null) {
            for(int var10 = 0; var10 < 16; ++var10) {
               for(int var11 = 0; var11 < 16; ++var11) {
                  var3.setBlockState(var5.set(var10, var8, var11), var9, false);
                  var6.update(var10, var8, var11, var9);
                  var7.update(var10, var8, var11, var9);
               }
            }
         }
      }

   }

   public int getBaseHeight(int var1, int var2, Heightmap.Types var3) {
      BlockState[] var4 = this.settings.getLayers();

      for(int var5 = var4.length - 1; var5 >= 0; --var5) {
         BlockState var6 = var4[var5];
         if (var6 != null && var3.isOpaque().test(var6)) {
            return var5 + 1;
         }
      }

      return 0;
   }

   public BlockGetter getBaseColumn(int var1, int var2) {
      return new NoiseColumn((BlockState[])Arrays.stream(this.settings.getLayers()).map((var0) -> {
         return var0 == null ? Blocks.AIR.defaultBlockState() : var0;
      }).toArray((var0) -> {
         return new BlockState[var0];
      }));
   }

   static {
      CODEC = FlatLevelGeneratorSettings.CODEC.fieldOf("settings").xmap(FlatLevelSource::new, FlatLevelSource::settings).codec();
   }
}
