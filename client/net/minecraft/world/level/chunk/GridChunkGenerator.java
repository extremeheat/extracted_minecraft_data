package net.minecraft.world.level.chunk;

import com.google.common.annotations.VisibleForTesting;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.server.level.WorldGenRegion;
import net.minecraft.world.level.LevelHeightAccessor;
import net.minecraft.world.level.NoiseColumn;
import net.minecraft.world.level.StructureManager;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeManager;
import net.minecraft.world.level.biome.BiomeSource;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.Aquifer;
import net.minecraft.world.level.levelgen.Beardifier;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.NoiseChunk;
import net.minecraft.world.level.levelgen.NoiseGeneratorSettings;
import net.minecraft.world.level.levelgen.RandomState;
import net.minecraft.world.level.levelgen.WorldGenerationContext;
import net.minecraft.world.level.levelgen.blending.Blender;

public class GridChunkGenerator extends ChunkGenerator {
   public static final MapCodec<GridChunkGenerator> CODEC = RecordCodecBuilder.mapCodec((var0) -> var0.group(BiomeSource.CODEC.fieldOf("biome_source").forGetter((var0x) -> var0x.biomeSource), NoiseGeneratorSettings.CODEC.fieldOf("settings").forGetter((var0x) -> var0x.settings), Codec.INT.fieldOf("spacing").forGetter((var0x) -> var0x.spacing), Codec.INT.fieldOf("width").forGetter((var0x) -> var0x.width), Codec.INT.fieldOf("y").forGetter((var0x) -> var0x.y), Codec.BOOL.fieldOf("lines").forGetter((var0x) -> var0x.lines)).apply(var0, var0.stable(GridChunkGenerator::new)));
   private final Holder<NoiseGeneratorSettings> settings;
   private final int spacing;
   private final int width;
   private final int y;
   private final boolean lines;

   public GridChunkGenerator(BiomeSource var1, Holder<NoiseGeneratorSettings> var2, int var3, int var4, int var5, boolean var6) {
      super(var1);
      this.settings = var2;
      this.spacing = var3;
      this.width = var4;
      this.y = var5;
      this.lines = var6;
   }

   protected MapCodec<? extends ChunkGenerator> codec() {
      return CODEC;
   }

   public NoiseGeneratorSettings getNoiseGeneratorSettings() {
      return this.settings.value();
   }

   public void applyCarvers(WorldGenRegion var1, long var2, RandomState var4, BiomeManager var5, StructureManager var6, ChunkAccess var7) {
   }

   public void buildSurface(WorldGenRegion var1, StructureManager var2, RandomState var3, ChunkAccess var4) {
      WorldGenerationContext var5 = new WorldGenerationContext(this, var1);
      this.buildSurface(var4, var5, var3, var2, var1.getBiomeManager(), var1.registryAccess().lookupOrThrow(Registries.BIOME), Blender.of(var1));
   }

   @VisibleForTesting
   public void buildSurface(ChunkAccess var1, WorldGenerationContext var2, RandomState var3, StructureManager var4, BiomeManager var5, Registry<Biome> var6, Blender var7) {
      NoiseChunk var8 = var1.getOrCreateNoiseChunk((var4x) -> this.createNoiseChunk(var4x, var4, var7, var3));
      NoiseGeneratorSettings var9 = this.settings.value();
      var3.surfaceSystem().buildSurface(var3, var5, var6, var9.useLegacyRandomSource(), var2, var1, var8, var9.surfaceRule());
   }

   public void spawnOriginalMobs(WorldGenRegion var1) {
   }

   public int getGenDepth() {
      return 384;
   }

   public CompletableFuture<ChunkAccess> fillFromNoise(Blender var1, RandomState var2, StructureManager var3, ChunkAccess var4) {
      BlockPos.MutableBlockPos var5 = new BlockPos.MutableBlockPos();
      Heightmap var6 = var4.getOrCreateHeightmapUnprimed(Heightmap.Types.OCEAN_FLOOR_WG);
      Heightmap var7 = var4.getOrCreateHeightmapUnprimed(Heightmap.Types.WORLD_SURFACE_WG);
      BlockState var8 = ((NoiseGeneratorSettings)this.settings.value()).defaultBlock();

      for(int var9 = 0; var9 < 16; ++var9) {
         for(int var10 = 0; var10 < 16; ++var10) {
            if (this.isSolid(var9, var10)) {
               var4.setBlockState(var5.set(var9, this.y, var10), var8);
               var6.update(var9, this.y, var10, var8);
               var7.update(var9, this.y, var10, var8);
            }
         }
      }

      return CompletableFuture.completedFuture(var4);
   }

   private boolean isSolid(int var1, int var2) {
      boolean var3 = var1 % this.spacing < this.width;
      boolean var4 = var2 % this.spacing < this.width;
      return this.lines ? var3 || var4 : var3 && var4;
   }

   private NoiseChunk createNoiseChunk(ChunkAccess var1, StructureManager var2, Blender var3, RandomState var4) {
      return NoiseChunk.forChunk(var1, var4, Beardifier.forStructuresInChunk(var2, var1.getPos()), this.settings.value(), (var0, var1x, var2x) -> new Aquifer.FluidStatus(-64, Blocks.AIR.defaultBlockState()), var3);
   }

   public int getSeaLevel() {
      return -63;
   }

   public int getMinY() {
      return -64;
   }

   public int getBaseHeight(int var1, int var2, Heightmap.Types var3, LevelHeightAccessor var4, RandomState var5) {
      return this.isSolid(var1, var2) ? this.y : this.getMinY();
   }

   public NoiseColumn getBaseColumn(int var1, int var2, LevelHeightAccessor var3, RandomState var4) {
      return this.isSolid(var1, var2) ? this.hitColumn() : this.missColumn();
   }

   private NoiseColumn hitColumn() {
      NoiseColumn var1 = this.missColumn();
      var1.setBlock(this.y, ((NoiseGeneratorSettings)this.settings.value()).defaultBlock());
      return var1;
   }

   private NoiseColumn missColumn() {
      BlockState[] var1 = new BlockState[this.getGenDepth()];
      Arrays.fill(var1, Blocks.AIR.defaultBlockState());
      return new NoiseColumn(this.getMinY(), var1);
   }

   public void addDebugScreenInfo(List<String> var1, RandomState var2, BlockPos var3) {
   }
}
