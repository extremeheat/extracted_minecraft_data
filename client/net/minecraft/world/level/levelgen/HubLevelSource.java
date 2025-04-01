package net.minecraft.world.level.levelgen;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.resources.RegistryOps;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.WorldGenRegion;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.LevelHeightAccessor;
import net.minecraft.world.level.NoiseColumn;
import net.minecraft.world.level.StructureManager;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeManager;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.biome.FixedBiomeSource;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.blending.Blender;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.templatesystem.AlwaysTrueTest;
import net.minecraft.world.level.levelgen.structure.templatesystem.BlockMatchTest;
import net.minecraft.world.level.levelgen.structure.templatesystem.ProcessorRule;
import net.minecraft.world.level.levelgen.structure.templatesystem.RuleProcessor;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessor;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplateManager;

public class HubLevelSource extends ChunkGenerator {
   public static final MapCodec<HubLevelSource> CODEC = RecordCodecBuilder.mapCodec((var0) -> var0.group(RegistryOps.retrieveElement(Biomes.HUB)).apply(var0, var0.stable(HubLevelSource::new)));
   public static final ResourceLocation CENTER_BASE_STRUCTURE = ResourceLocation.withDefaultNamespace("hub/center_base");
   public static final ResourceLocation CENTER_TOP_STRUCTURE = ResourceLocation.withDefaultNamespace("hub/center_hat");
   public static final ResourceLocation CORRIDOR_BASE_STRUCTURE = ResourceLocation.withDefaultNamespace("hub/corridor_base");
   public static final ResourceLocation CORRIDOR_TOP_STRUCTURE = ResourceLocation.withDefaultNamespace("hub/corridor_hat");
   private static final int BOTTOM_SECTION = 0;
   public static final StructureProcessor SKYIFIER;

   public HubLevelSource(Holder.Reference<Biome> var1) {
      super(new FixedBiomeSource(var1));
   }

   protected MapCodec<HubLevelSource> codec() {
      return CODEC;
   }

   public void applyBiomeDecoration(WorldGenLevel var1, ChunkAccess var2, StructureManager var3) {
      ChunkPos var4 = var2.getPos();
      StructureTemplateManager var5 = var1.getLevel().getStructureManager();
      if (var4.z >= -1 && var4.z <= 1) {
         LevelHeightAccessor var6 = var2.getHeightAccessorForGeneration();
         BoundingBox var7 = new BoundingBox(var4.getMinBlockX(), var6.getMinY(), var4.getMinBlockZ(), var4.getMaxBlockX(), var6.getMaxY(), var4.getMaxBlockZ());
         if (var4.x >= -1 && var4.x <= 1) {
            BlockPos.MutableBlockPos var9 = new BlockPos.MutableBlockPos(-16, 0, -16);
            this.plaseChunk(var1, var5, var7, var9, CENTER_BASE_STRUCTURE, CENTER_TOP_STRUCTURE);
         } else if (var4.x < -1) {
            BlockPos.MutableBlockPos var8 = new BlockPos.MutableBlockPos(var4.x * 16, 0, -16);
            this.plaseChunk(var1, var5, var7, var8, CORRIDOR_BASE_STRUCTURE, CORRIDOR_TOP_STRUCTURE);
         }
      }

   }

   private void plaseChunk(WorldGenLevel var1, StructureTemplateManager var2, BoundingBox var3, BlockPos.MutableBlockPos var4, ResourceLocation var5, ResourceLocation var6) {
      for(int var7 = 0; var7 < var1.getMaxY() / 16; ++var7) {
         ResourceLocation var8 = var7 == 0 ? var5 : var6;
         placeStructure(var1, var4, var3, var2, var8);
         var4.move(Direction.UP, 16);
      }

   }

   private static void placeStructure(WorldGenLevel var0, BlockPos var1, BoundingBox var2, StructureTemplateManager var3, ResourceLocation var4) {
      var3.get(var4).ifPresent((var3x) -> {
         WorldgenRandom var4 = new WorldgenRandom(new XoroshiroRandomSource(RandomSupport.generateUniqueSeed()));
         var4.setDecorationSeed(var0.getSeed(), var1.getX(), var1.getZ());
         var3x.placeInWorld(var0, var1, var1, (new StructurePlaceSettings()).setBoundingBox(var2).addProcessor(SKYIFIER), var4, 0);
      });
   }

   public void applyCarvers(WorldGenRegion var1, long var2, RandomState var4, BiomeManager var5, StructureManager var6, ChunkAccess var7) {
   }

   public void buildSurface(WorldGenRegion var1, StructureManager var2, RandomState var3, ChunkAccess var4) {
   }

   public void spawnOriginalMobs(WorldGenRegion var1) {
   }

   public int getGenDepth() {
      return 0;
   }

   public CompletableFuture<ChunkAccess> fillFromNoise(Blender var1, RandomState var2, StructureManager var3, ChunkAccess var4) {
      return CompletableFuture.completedFuture(var4);
   }

   public int getSeaLevel() {
      return 0;
   }

   public int getMinY() {
      return 0;
   }

   public int getBaseHeight(int var1, int var2, Heightmap.Types var3, LevelHeightAccessor var4, RandomState var5) {
      return 0;
   }

   public NoiseColumn getBaseColumn(int var1, int var2, LevelHeightAccessor var3, RandomState var4) {
      return new NoiseColumn(0, new BlockState[0]);
   }

   public void addDebugScreenInfo(List<String> var1, RandomState var2, BlockPos var3) {
   }

   static {
      SKYIFIER = new RuleProcessor(List.of(new ProcessorRule(new BlockMatchTest(Blocks.BEDROCK), AlwaysTrueTest.INSTANCE, Blocks.SKY.defaultBlockState())));
   }
}
