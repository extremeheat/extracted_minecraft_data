package net.minecraft.world.level.levelgen;

import com.google.common.base.Suppliers;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.KeyDispatchDataCodec;
import net.minecraft.util.Mth;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.levelgen.placement.CaveSurface;
import net.minecraft.world.level.levelgen.synth.NormalNoise;

public class SurfaceRules {
   public static final SurfaceRules.ConditionSource ON_FLOOR = stoneDepthCheck(0, false, CaveSurface.FLOOR);
   public static final SurfaceRules.ConditionSource UNDER_FLOOR = stoneDepthCheck(0, true, CaveSurface.FLOOR);
   public static final SurfaceRules.ConditionSource DEEP_UNDER_FLOOR = stoneDepthCheck(0, true, 6, CaveSurface.FLOOR);
   public static final SurfaceRules.ConditionSource VERY_DEEP_UNDER_FLOOR = stoneDepthCheck(0, true, 30, CaveSurface.FLOOR);
   public static final SurfaceRules.ConditionSource ON_CEILING = stoneDepthCheck(0, false, CaveSurface.CEILING);
   public static final SurfaceRules.ConditionSource UNDER_CEILING = stoneDepthCheck(0, true, CaveSurface.CEILING);

   public SurfaceRules() {
      super();
   }

   public static SurfaceRules.ConditionSource stoneDepthCheck(int var0, boolean var1, CaveSurface var2) {
      return new SurfaceRules.StoneDepthCheck(var0, var1, 0, var2);
   }

   public static SurfaceRules.ConditionSource stoneDepthCheck(int var0, boolean var1, int var2, CaveSurface var3) {
      return new SurfaceRules.StoneDepthCheck(var0, var1, var2, var3);
   }

   public static SurfaceRules.ConditionSource not(SurfaceRules.ConditionSource var0) {
      return new SurfaceRules.NotConditionSource(var0);
   }

   public static SurfaceRules.ConditionSource yBlockCheck(VerticalAnchor var0, int var1) {
      return new SurfaceRules.YConditionSource(var0, var1, false);
   }

   public static SurfaceRules.ConditionSource yStartCheck(VerticalAnchor var0, int var1) {
      return new SurfaceRules.YConditionSource(var0, var1, true);
   }

   public static SurfaceRules.ConditionSource waterBlockCheck(int var0, int var1) {
      return new SurfaceRules.WaterConditionSource(var0, var1, false);
   }

   public static SurfaceRules.ConditionSource waterStartCheck(int var0, int var1) {
      return new SurfaceRules.WaterConditionSource(var0, var1, true);
   }

   @SafeVarargs
   public static SurfaceRules.ConditionSource isBiome(ResourceKey<Biome>... var0) {
      return isBiome(List.of(var0));
   }

   private static SurfaceRules.BiomeConditionSource isBiome(List<ResourceKey<Biome>> var0) {
      return new SurfaceRules.BiomeConditionSource(var0);
   }

   public static SurfaceRules.ConditionSource noiseCondition(ResourceKey<NormalNoise.NoiseParameters> var0, double var1) {
      return noiseCondition(var0, var1, 1.7976931348623157E308);
   }

   public static SurfaceRules.ConditionSource noiseCondition(ResourceKey<NormalNoise.NoiseParameters> var0, double var1, double var3) {
      return new SurfaceRules.NoiseThresholdConditionSource(var0, var1, var3);
   }

   public static SurfaceRules.ConditionSource verticalGradient(String var0, VerticalAnchor var1, VerticalAnchor var2) {
      return new SurfaceRules.VerticalGradientConditionSource(ResourceLocation.parse(var0), var1, var2);
   }

   public static SurfaceRules.ConditionSource steep() {
      return SurfaceRules.Steep.INSTANCE;
   }

   public static SurfaceRules.ConditionSource hole() {
      return SurfaceRules.Hole.INSTANCE;
   }

   public static SurfaceRules.ConditionSource abovePreliminarySurface() {
      return SurfaceRules.AbovePreliminarySurface.INSTANCE;
   }

   public static SurfaceRules.ConditionSource temperature() {
      return SurfaceRules.Temperature.INSTANCE;
   }

   public static SurfaceRules.RuleSource ifTrue(SurfaceRules.ConditionSource var0, SurfaceRules.RuleSource var1) {
      return new SurfaceRules.TestRuleSource(var0, var1);
   }

   public static SurfaceRules.RuleSource sequence(SurfaceRules.RuleSource... var0) {
      if (var0.length == 0) {
         throw new IllegalArgumentException("Need at least 1 rule for a sequence");
      } else {
         return new SurfaceRules.SequenceRuleSource(Arrays.asList(var0));
      }
   }

   public static SurfaceRules.RuleSource state(BlockState var0) {
      return new SurfaceRules.BlockRuleSource(var0);
   }

   public static SurfaceRules.RuleSource bandlands() {
      return SurfaceRules.Bandlands.INSTANCE;
   }

   static <A> MapCodec<? extends A> register(Registry<MapCodec<? extends A>> var0, String var1, KeyDispatchDataCodec<? extends A> var2) {
      return Registry.register(var0, var1, var2.codec());
   }

   static enum AbovePreliminarySurface implements SurfaceRules.ConditionSource {
      INSTANCE;

      static final KeyDispatchDataCodec<SurfaceRules.AbovePreliminarySurface> CODEC = KeyDispatchDataCodec.of(MapCodec.unit(INSTANCE));

      private AbovePreliminarySurface() {
      }

      @Override
      public KeyDispatchDataCodec<? extends SurfaceRules.ConditionSource> codec() {
         return CODEC;
      }

      public SurfaceRules.Condition apply(SurfaceRules.Context var1) {
         return var1.abovePreliminarySurface;
      }
   }

   static enum Bandlands implements SurfaceRules.RuleSource {
      INSTANCE;

      static final KeyDispatchDataCodec<SurfaceRules.Bandlands> CODEC = KeyDispatchDataCodec.of(MapCodec.unit(INSTANCE));

      private Bandlands() {
      }

      @Override
      public KeyDispatchDataCodec<? extends SurfaceRules.RuleSource> codec() {
         return CODEC;
      }

      public SurfaceRules.SurfaceRule apply(SurfaceRules.Context var1) {
         return var1.system::getBand;
      }
   }

   static final class BiomeConditionSource implements SurfaceRules.ConditionSource {
      static final KeyDispatchDataCodec<SurfaceRules.BiomeConditionSource> CODEC = KeyDispatchDataCodec.of(
         ResourceKey.codec(Registries.BIOME).listOf().fieldOf("biome_is").xmap(SurfaceRules::isBiome, var0 -> var0.biomes)
      );
      private final List<ResourceKey<Biome>> biomes;
      final Predicate<ResourceKey<Biome>> biomeNameTest;

      BiomeConditionSource(List<ResourceKey<Biome>> var1) {
         super();
         this.biomes = var1;
         this.biomeNameTest = Set.copyOf(var1)::contains;
      }

      @Override
      public KeyDispatchDataCodec<? extends SurfaceRules.ConditionSource> codec() {
         return CODEC;
      }

      public SurfaceRules.Condition apply(final SurfaceRules.Context var1) {
         class 1BiomeCondition extends SurfaceRules.LazyYCondition {
            _BiomeCondition/* $VF was: 1BiomeCondition*/() {
               super(var1);
            }

            @Override
            protected boolean compute() {
               return this.context.biome.get().is(BiomeConditionSource.this.biomeNameTest);
            }
         }

         return new 1BiomeCondition();
      }

      @Override
      public boolean equals(Object var1) {
         if (this == var1) {
            return true;
         } else {
            return var1 instanceof SurfaceRules.BiomeConditionSource var2 ? this.biomes.equals(var2.biomes) : false;
         }
      }

      @Override
      public int hashCode() {
         return this.biomes.hashCode();
      }

      @Override
      public String toString() {
         return "BiomeConditionSource[biomes=" + this.biomes + "]";
      }
   }

// $VF: Couldn't be decompiled
// Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
// java.lang.NullPointerException
//   at org.jetbrains.java.decompiler.main.InitializerProcessor.isExprentIndependent(InitializerProcessor.java:423)
//   at org.jetbrains.java.decompiler.main.InitializerProcessor.extractDynamicInitializers(InitializerProcessor.java:335)
//   at org.jetbrains.java.decompiler.main.InitializerProcessor.extractInitializers(InitializerProcessor.java:44)
//   at org.jetbrains.java.decompiler.main.ClassWriter.invokeProcessors(ClassWriter.java:97)
//   at org.jetbrains.java.decompiler.main.ClassWriter.writeClass(ClassWriter.java:348)
//   at org.jetbrains.java.decompiler.main.ClassWriter.writeClass(ClassWriter.java:492)
//   at org.jetbrains.java.decompiler.main.ClassesProcessor.writeClass(ClassesProcessor.java:474)
//   at org.jetbrains.java.decompiler.main.Fernflower.getClassContent(Fernflower.java:191)
//   at org.jetbrains.java.decompiler.struct.ContextUnit.lambda$save$3(ContextUnit.java:187)

   interface Condition {
      boolean test();
   }

   public interface ConditionSource extends Function<SurfaceRules.Context, SurfaceRules.Condition> {
      Codec<SurfaceRules.ConditionSource> CODEC = BuiltInRegistries.MATERIAL_CONDITION
         .byNameCodec()
         .dispatch(var0 -> var0.codec().codec(), Function.identity());

      static MapCodec<? extends SurfaceRules.ConditionSource> bootstrap(Registry<MapCodec<? extends SurfaceRules.ConditionSource>> var0) {
         SurfaceRules.register(var0, "biome", SurfaceRules.BiomeConditionSource.CODEC);
         SurfaceRules.register(var0, "noise_threshold", SurfaceRules.NoiseThresholdConditionSource.CODEC);
         SurfaceRules.register(var0, "vertical_gradient", SurfaceRules.VerticalGradientConditionSource.CODEC);
         SurfaceRules.register(var0, "y_above", SurfaceRules.YConditionSource.CODEC);
         SurfaceRules.register(var0, "water", SurfaceRules.WaterConditionSource.CODEC);
         SurfaceRules.register(var0, "temperature", SurfaceRules.Temperature.CODEC);
         SurfaceRules.register(var0, "steep", SurfaceRules.Steep.CODEC);
         SurfaceRules.register(var0, "not", SurfaceRules.NotConditionSource.CODEC);
         SurfaceRules.register(var0, "hole", SurfaceRules.Hole.CODEC);
         SurfaceRules.register(var0, "above_preliminary_surface", SurfaceRules.AbovePreliminarySurface.CODEC);
         return SurfaceRules.register(var0, "stone_depth", SurfaceRules.StoneDepthCheck.CODEC);
      }

      KeyDispatchDataCodec<? extends SurfaceRules.ConditionSource> codec();
   }

   protected static final class Context {
      private static final int HOW_FAR_BELOW_PRELIMINARY_SURFACE_LEVEL_TO_BUILD_SURFACE = 8;
      private static final int SURFACE_CELL_BITS = 4;
      private static final int SURFACE_CELL_SIZE = 16;
      private static final int SURFACE_CELL_MASK = 15;
      final SurfaceSystem system;
      final SurfaceRules.Condition temperature = new SurfaceRules.Context.TemperatureHelperCondition(this);
      final SurfaceRules.Condition steep = new SurfaceRules.Context.SteepMaterialCondition(this);
      final SurfaceRules.Condition hole = new SurfaceRules.Context.HoleCondition(this);
      final SurfaceRules.Condition abovePreliminarySurface = new SurfaceRules.Context.AbovePreliminarySurfaceCondition();
      final RandomState randomState;
      final ChunkAccess chunk;
      private final NoiseChunk noiseChunk;
      private final Function<BlockPos, Holder<Biome>> biomeGetter;
      final WorldGenerationContext context;
      private long lastPreliminarySurfaceCellOrigin = 9223372036854775807L;
      private final int[] preliminarySurfaceCache = new int[4];
      long lastUpdateXZ = -9223372036854775807L;
      int blockX;
      int blockZ;
      int surfaceDepth;
      private long lastSurfaceDepth2Update = this.lastUpdateXZ - 1L;
      private double surfaceSecondary;
      private long lastMinSurfaceLevelUpdate = this.lastUpdateXZ - 1L;
      private int minSurfaceLevel;
      long lastUpdateY = -9223372036854775807L;
      final BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos();
      Supplier<Holder<Biome>> biome;
      int blockY;
      int waterHeight;
      int stoneDepthBelow;
      int stoneDepthAbove;

      protected Context(
         SurfaceSystem var1,
         RandomState var2,
         ChunkAccess var3,
         NoiseChunk var4,
         Function<BlockPos, Holder<Biome>> var5,
         Registry<Biome> var6,
         WorldGenerationContext var7
      ) {
         super();
         this.system = var1;
         this.randomState = var2;
         this.chunk = var3;
         this.noiseChunk = var4;
         this.biomeGetter = var5;
         this.context = var7;
      }

      protected void updateXZ(int var1, int var2) {
         this.lastUpdateXZ++;
         this.lastUpdateY++;
         this.blockX = var1;
         this.blockZ = var2;
         this.surfaceDepth = this.system.getSurfaceDepth(var1, var2);
      }

      protected void updateY(int var1, int var2, int var3, int var4, int var5, int var6) {
         this.lastUpdateY++;
         this.biome = Suppliers.memoize(() -> this.biomeGetter.apply(this.pos.set(var4, var5, var6)));
         this.blockY = var5;
         this.waterHeight = var3;
         this.stoneDepthBelow = var2;
         this.stoneDepthAbove = var1;
      }

      protected double getSurfaceSecondary() {
         if (this.lastSurfaceDepth2Update != this.lastUpdateXZ) {
            this.lastSurfaceDepth2Update = this.lastUpdateXZ;
            this.surfaceSecondary = this.system.getSurfaceSecondary(this.blockX, this.blockZ);
         }

         return this.surfaceSecondary;
      }

      public int getSeaLevel() {
         return this.system.getSeaLevel();
      }

      private static int blockCoordToSurfaceCell(int var0) {
         return var0 >> 4;
      }

      private static int surfaceCellToBlockCoord(int var0) {
         return var0 << 4;
      }

      protected int getMinSurfaceLevel() {
         if (this.lastMinSurfaceLevelUpdate != this.lastUpdateXZ) {
            this.lastMinSurfaceLevelUpdate = this.lastUpdateXZ;
            int var1 = blockCoordToSurfaceCell(this.blockX);
            int var2 = blockCoordToSurfaceCell(this.blockZ);
            long var3 = ChunkPos.asLong(var1, var2);
            if (this.lastPreliminarySurfaceCellOrigin != var3) {
               this.lastPreliminarySurfaceCellOrigin = var3;
               this.preliminarySurfaceCache[0] = this.noiseChunk.preliminarySurfaceLevel(surfaceCellToBlockCoord(var1), surfaceCellToBlockCoord(var2));
               this.preliminarySurfaceCache[1] = this.noiseChunk.preliminarySurfaceLevel(surfaceCellToBlockCoord(var1 + 1), surfaceCellToBlockCoord(var2));
               this.preliminarySurfaceCache[2] = this.noiseChunk.preliminarySurfaceLevel(surfaceCellToBlockCoord(var1), surfaceCellToBlockCoord(var2 + 1));
               this.preliminarySurfaceCache[3] = this.noiseChunk.preliminarySurfaceLevel(surfaceCellToBlockCoord(var1 + 1), surfaceCellToBlockCoord(var2 + 1));
            }

            int var5 = Mth.floor(
               Mth.lerp2(
                  (double)((float)(this.blockX & 15) / 16.0F),
                  (double)((float)(this.blockZ & 15) / 16.0F),
                  (double)this.preliminarySurfaceCache[0],
                  (double)this.preliminarySurfaceCache[1],
                  (double)this.preliminarySurfaceCache[2],
                  (double)this.preliminarySurfaceCache[3]
               )
            );
            this.minSurfaceLevel = var5 + this.surfaceDepth - 8;
         }

         return this.minSurfaceLevel;
      }

      final class AbovePreliminarySurfaceCondition implements SurfaceRules.Condition {
         AbovePreliminarySurfaceCondition() {
            super();
         }

         @Override
         public boolean test() {
            return Context.this.blockY >= Context.this.getMinSurfaceLevel();
         }
      }

      static final class HoleCondition extends SurfaceRules.LazyXZCondition {
         HoleCondition(SurfaceRules.Context var1) {
            super(var1);
         }

         @Override
         protected boolean compute() {
            return this.context.surfaceDepth <= 0;
         }
      }

      static class SteepMaterialCondition extends SurfaceRules.LazyXZCondition {
         SteepMaterialCondition(SurfaceRules.Context var1) {
            super(var1);
         }

         @Override
         protected boolean compute() {
            int var1 = this.context.blockX & 15;
            int var2 = this.context.blockZ & 15;
            int var3 = Math.max(var2 - 1, 0);
            int var4 = Math.min(var2 + 1, 15);
            ChunkAccess var5 = this.context.chunk;
            int var6 = var5.getHeight(Heightmap.Types.WORLD_SURFACE_WG, var1, var3);
            int var7 = var5.getHeight(Heightmap.Types.WORLD_SURFACE_WG, var1, var4);
            if (var7 >= var6 + 4) {
               return true;
            } else {
               int var8 = Math.max(var1 - 1, 0);
               int var9 = Math.min(var1 + 1, 15);
               int var10 = var5.getHeight(Heightmap.Types.WORLD_SURFACE_WG, var8, var2);
               int var11 = var5.getHeight(Heightmap.Types.WORLD_SURFACE_WG, var9, var2);
               return var10 >= var11 + 4;
            }
         }
      }

      static class TemperatureHelperCondition extends SurfaceRules.LazyYCondition {
         TemperatureHelperCondition(SurfaceRules.Context var1) {
            super(var1);
         }

         @Override
         protected boolean compute() {
            return this.context
               .biome
               .get()
               .value()
               .coldEnoughToSnow(this.context.pos.set(this.context.blockX, this.context.blockY, this.context.blockZ), this.context.getSeaLevel());
         }
      }
   }

   static enum Hole implements SurfaceRules.ConditionSource {
      INSTANCE;

      static final KeyDispatchDataCodec<SurfaceRules.Hole> CODEC = KeyDispatchDataCodec.of(MapCodec.unit(INSTANCE));

      private Hole() {
      }

      @Override
      public KeyDispatchDataCodec<? extends SurfaceRules.ConditionSource> codec() {
         return CODEC;
      }

      public SurfaceRules.Condition apply(SurfaceRules.Context var1) {
         return var1.hole;
      }
   }

   abstract static class LazyCondition implements SurfaceRules.Condition {
      protected final SurfaceRules.Context context;
      private long lastUpdate;
      @Nullable
      Boolean result;

      protected LazyCondition(SurfaceRules.Context var1) {
         super();
         this.context = var1;
         this.lastUpdate = this.getContextLastUpdate() - 1L;
      }

      @Override
      public boolean test() {
         long var1 = this.getContextLastUpdate();
         if (var1 == this.lastUpdate) {
            if (this.result == null) {
               throw new IllegalStateException("Update triggered but the result is null");
            } else {
               return this.result;
            }
         } else {
            this.lastUpdate = var1;
            this.result = this.compute();
            return this.result;
         }
      }

      protected abstract long getContextLastUpdate();

      protected abstract boolean compute();
   }

   abstract static class LazyXZCondition extends SurfaceRules.LazyCondition {
      protected LazyXZCondition(SurfaceRules.Context var1) {
         super(var1);
      }

      @Override
      protected long getContextLastUpdate() {
         return this.context.lastUpdateXZ;
      }
   }

   abstract static class LazyYCondition extends SurfaceRules.LazyCondition {
      protected LazyYCondition(SurfaceRules.Context var1) {
         super(var1);
      }

      @Override
      protected long getContextLastUpdate() {
         return this.context.lastUpdateY;
      }
   }

// $VF: Couldn't be decompiled
// Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
// java.lang.NullPointerException
//   at org.jetbrains.java.decompiler.main.InitializerProcessor.isExprentIndependent(InitializerProcessor.java:423)
//   at org.jetbrains.java.decompiler.main.InitializerProcessor.extractDynamicInitializers(InitializerProcessor.java:335)
//   at org.jetbrains.java.decompiler.main.InitializerProcessor.extractInitializers(InitializerProcessor.java:44)
//   at org.jetbrains.java.decompiler.main.ClassWriter.invokeProcessors(ClassWriter.java:97)
//   at org.jetbrains.java.decompiler.main.ClassWriter.writeClass(ClassWriter.java:348)
//   at org.jetbrains.java.decompiler.main.ClassWriter.writeClass(ClassWriter.java:492)
//   at org.jetbrains.java.decompiler.main.ClassesProcessor.writeClass(ClassesProcessor.java:474)
//   at org.jetbrains.java.decompiler.main.Fernflower.getClassContent(Fernflower.java:191)
//   at org.jetbrains.java.decompiler.struct.ContextUnit.lambda$save$3(ContextUnit.java:187)

// $VF: Couldn't be decompiled
// Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
// java.lang.NullPointerException
//   at org.jetbrains.java.decompiler.main.InitializerProcessor.isExprentIndependent(InitializerProcessor.java:423)
//   at org.jetbrains.java.decompiler.main.InitializerProcessor.extractDynamicInitializers(InitializerProcessor.java:335)
//   at org.jetbrains.java.decompiler.main.InitializerProcessor.extractInitializers(InitializerProcessor.java:44)
//   at org.jetbrains.java.decompiler.main.ClassWriter.invokeProcessors(ClassWriter.java:97)
//   at org.jetbrains.java.decompiler.main.ClassWriter.writeClass(ClassWriter.java:348)
//   at org.jetbrains.java.decompiler.main.ClassWriter.writeClass(ClassWriter.java:492)
//   at org.jetbrains.java.decompiler.main.ClassesProcessor.writeClass(ClassesProcessor.java:474)
//   at org.jetbrains.java.decompiler.main.Fernflower.getClassContent(Fernflower.java:191)
//   at org.jetbrains.java.decompiler.struct.ContextUnit.lambda$save$3(ContextUnit.java:187)

// $VF: Couldn't be decompiled
// Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
// java.lang.NullPointerException
//   at org.jetbrains.java.decompiler.main.InitializerProcessor.isExprentIndependent(InitializerProcessor.java:423)
//   at org.jetbrains.java.decompiler.main.InitializerProcessor.extractDynamicInitializers(InitializerProcessor.java:335)
//   at org.jetbrains.java.decompiler.main.InitializerProcessor.extractInitializers(InitializerProcessor.java:44)
//   at org.jetbrains.java.decompiler.main.ClassWriter.invokeProcessors(ClassWriter.java:97)
//   at org.jetbrains.java.decompiler.main.ClassWriter.writeClass(ClassWriter.java:348)
//   at org.jetbrains.java.decompiler.main.ClassWriter.writeClass(ClassWriter.java:492)
//   at org.jetbrains.java.decompiler.main.ClassesProcessor.writeClass(ClassesProcessor.java:474)
//   at org.jetbrains.java.decompiler.main.Fernflower.getClassContent(Fernflower.java:191)
//   at org.jetbrains.java.decompiler.struct.ContextUnit.lambda$save$3(ContextUnit.java:187)

   public interface RuleSource extends Function<SurfaceRules.Context, SurfaceRules.SurfaceRule> {
      Codec<SurfaceRules.RuleSource> CODEC = BuiltInRegistries.MATERIAL_RULE.byNameCodec().dispatch(var0 -> var0.codec().codec(), Function.identity());

      static MapCodec<? extends SurfaceRules.RuleSource> bootstrap(Registry<MapCodec<? extends SurfaceRules.RuleSource>> var0) {
         SurfaceRules.register(var0, "bandlands", SurfaceRules.Bandlands.CODEC);
         SurfaceRules.register(var0, "block", SurfaceRules.BlockRuleSource.CODEC);
         SurfaceRules.register(var0, "sequence", SurfaceRules.SequenceRuleSource.CODEC);
         return SurfaceRules.register(var0, "condition", SurfaceRules.TestRuleSource.CODEC);
      }

      KeyDispatchDataCodec<? extends SurfaceRules.RuleSource> codec();
   }

// $VF: Couldn't be decompiled
// Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
// java.lang.NullPointerException
//   at org.jetbrains.java.decompiler.main.InitializerProcessor.isExprentIndependent(InitializerProcessor.java:423)
//   at org.jetbrains.java.decompiler.main.InitializerProcessor.extractDynamicInitializers(InitializerProcessor.java:335)
//   at org.jetbrains.java.decompiler.main.InitializerProcessor.extractInitializers(InitializerProcessor.java:44)
//   at org.jetbrains.java.decompiler.main.ClassWriter.invokeProcessors(ClassWriter.java:97)
//   at org.jetbrains.java.decompiler.main.ClassWriter.writeClass(ClassWriter.java:348)
//   at org.jetbrains.java.decompiler.main.ClassWriter.writeClass(ClassWriter.java:492)
//   at org.jetbrains.java.decompiler.main.ClassesProcessor.writeClass(ClassesProcessor.java:474)
//   at org.jetbrains.java.decompiler.main.Fernflower.getClassContent(Fernflower.java:191)
//   at org.jetbrains.java.decompiler.struct.ContextUnit.lambda$save$3(ContextUnit.java:187)

// $VF: Couldn't be decompiled
// Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
// java.lang.NullPointerException
//   at org.jetbrains.java.decompiler.main.InitializerProcessor.isExprentIndependent(InitializerProcessor.java:423)
//   at org.jetbrains.java.decompiler.main.InitializerProcessor.extractDynamicInitializers(InitializerProcessor.java:335)
//   at org.jetbrains.java.decompiler.main.InitializerProcessor.extractInitializers(InitializerProcessor.java:44)
//   at org.jetbrains.java.decompiler.main.ClassWriter.invokeProcessors(ClassWriter.java:97)
//   at org.jetbrains.java.decompiler.main.ClassWriter.writeClass(ClassWriter.java:348)
//   at org.jetbrains.java.decompiler.main.ClassWriter.writeClass(ClassWriter.java:492)
//   at org.jetbrains.java.decompiler.main.ClassesProcessor.writeClass(ClassesProcessor.java:474)
//   at org.jetbrains.java.decompiler.main.Fernflower.getClassContent(Fernflower.java:191)
//   at org.jetbrains.java.decompiler.struct.ContextUnit.lambda$save$3(ContextUnit.java:187)

// $VF: Couldn't be decompiled
// Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
// java.lang.NullPointerException
//   at org.jetbrains.java.decompiler.main.InitializerProcessor.isExprentIndependent(InitializerProcessor.java:423)
//   at org.jetbrains.java.decompiler.main.InitializerProcessor.extractDynamicInitializers(InitializerProcessor.java:335)
//   at org.jetbrains.java.decompiler.main.InitializerProcessor.extractInitializers(InitializerProcessor.java:44)
//   at org.jetbrains.java.decompiler.main.ClassWriter.invokeProcessors(ClassWriter.java:97)
//   at org.jetbrains.java.decompiler.main.ClassWriter.writeClass(ClassWriter.java:348)
//   at org.jetbrains.java.decompiler.main.ClassWriter.writeClass(ClassWriter.java:492)
//   at org.jetbrains.java.decompiler.main.ClassesProcessor.writeClass(ClassesProcessor.java:474)
//   at org.jetbrains.java.decompiler.main.Fernflower.getClassContent(Fernflower.java:191)
//   at org.jetbrains.java.decompiler.struct.ContextUnit.lambda$save$3(ContextUnit.java:187)

   static enum Steep implements SurfaceRules.ConditionSource {
      INSTANCE;

      static final KeyDispatchDataCodec<SurfaceRules.Steep> CODEC = KeyDispatchDataCodec.of(MapCodec.unit(INSTANCE));

      private Steep() {
      }

      @Override
      public KeyDispatchDataCodec<? extends SurfaceRules.ConditionSource> codec() {
         return CODEC;
      }

      public SurfaceRules.Condition apply(SurfaceRules.Context var1) {
         return var1.steep;
      }
   }

// $VF: Couldn't be decompiled
// Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
// java.lang.NullPointerException
//   at org.jetbrains.java.decompiler.main.InitializerProcessor.isExprentIndependent(InitializerProcessor.java:423)
//   at org.jetbrains.java.decompiler.main.InitializerProcessor.extractDynamicInitializers(InitializerProcessor.java:335)
//   at org.jetbrains.java.decompiler.main.InitializerProcessor.extractInitializers(InitializerProcessor.java:44)
//   at org.jetbrains.java.decompiler.main.ClassWriter.invokeProcessors(ClassWriter.java:97)
//   at org.jetbrains.java.decompiler.main.ClassWriter.writeClass(ClassWriter.java:348)
//   at org.jetbrains.java.decompiler.main.ClassWriter.writeClass(ClassWriter.java:492)
//   at org.jetbrains.java.decompiler.main.ClassesProcessor.writeClass(ClassesProcessor.java:474)
//   at org.jetbrains.java.decompiler.main.Fernflower.getClassContent(Fernflower.java:191)
//   at org.jetbrains.java.decompiler.struct.ContextUnit.lambda$save$3(ContextUnit.java:187)

   protected interface SurfaceRule {
      @Nullable
      BlockState tryApply(int var1, int var2, int var3);
   }

   static enum Temperature implements SurfaceRules.ConditionSource {
      INSTANCE;

      static final KeyDispatchDataCodec<SurfaceRules.Temperature> CODEC = KeyDispatchDataCodec.of(MapCodec.unit(INSTANCE));

      private Temperature() {
      }

      @Override
      public KeyDispatchDataCodec<? extends SurfaceRules.ConditionSource> codec() {
         return CODEC;
      }

      public SurfaceRules.Condition apply(SurfaceRules.Context var1) {
         return var1.temperature;
      }
   }

// $VF: Couldn't be decompiled
// Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
// java.lang.NullPointerException
//   at org.jetbrains.java.decompiler.main.InitializerProcessor.isExprentIndependent(InitializerProcessor.java:423)
//   at org.jetbrains.java.decompiler.main.InitializerProcessor.extractDynamicInitializers(InitializerProcessor.java:335)
//   at org.jetbrains.java.decompiler.main.InitializerProcessor.extractInitializers(InitializerProcessor.java:44)
//   at org.jetbrains.java.decompiler.main.ClassWriter.invokeProcessors(ClassWriter.java:97)
//   at org.jetbrains.java.decompiler.main.ClassWriter.writeClass(ClassWriter.java:348)
//   at org.jetbrains.java.decompiler.main.ClassWriter.writeClass(ClassWriter.java:492)
//   at org.jetbrains.java.decompiler.main.ClassesProcessor.writeClass(ClassesProcessor.java:474)
//   at org.jetbrains.java.decompiler.main.Fernflower.getClassContent(Fernflower.java:191)
//   at org.jetbrains.java.decompiler.struct.ContextUnit.lambda$save$3(ContextUnit.java:187)

// $VF: Couldn't be decompiled
// Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
// java.lang.NullPointerException
//   at org.jetbrains.java.decompiler.main.InitializerProcessor.isExprentIndependent(InitializerProcessor.java:423)
//   at org.jetbrains.java.decompiler.main.InitializerProcessor.extractDynamicInitializers(InitializerProcessor.java:335)
//   at org.jetbrains.java.decompiler.main.InitializerProcessor.extractInitializers(InitializerProcessor.java:44)
//   at org.jetbrains.java.decompiler.main.ClassWriter.invokeProcessors(ClassWriter.java:97)
//   at org.jetbrains.java.decompiler.main.ClassWriter.writeClass(ClassWriter.java:348)
//   at org.jetbrains.java.decompiler.main.ClassWriter.writeClass(ClassWriter.java:492)
//   at org.jetbrains.java.decompiler.main.ClassesProcessor.writeClass(ClassesProcessor.java:474)
//   at org.jetbrains.java.decompiler.main.Fernflower.getClassContent(Fernflower.java:191)
//   at org.jetbrains.java.decompiler.struct.ContextUnit.lambda$save$3(ContextUnit.java:187)

// $VF: Couldn't be decompiled
// Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
// java.lang.NullPointerException
//   at org.jetbrains.java.decompiler.main.InitializerProcessor.isExprentIndependent(InitializerProcessor.java:423)
//   at org.jetbrains.java.decompiler.main.InitializerProcessor.extractDynamicInitializers(InitializerProcessor.java:335)
//   at org.jetbrains.java.decompiler.main.InitializerProcessor.extractInitializers(InitializerProcessor.java:44)
//   at org.jetbrains.java.decompiler.main.ClassWriter.invokeProcessors(ClassWriter.java:97)
//   at org.jetbrains.java.decompiler.main.ClassWriter.writeClass(ClassWriter.java:348)
//   at org.jetbrains.java.decompiler.main.ClassWriter.writeClass(ClassWriter.java:492)
//   at org.jetbrains.java.decompiler.main.ClassesProcessor.writeClass(ClassesProcessor.java:474)
//   at org.jetbrains.java.decompiler.main.Fernflower.getClassContent(Fernflower.java:191)
//   at org.jetbrains.java.decompiler.struct.ContextUnit.lambda$save$3(ContextUnit.java:187)

// $VF: Couldn't be decompiled
// Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
// java.lang.NullPointerException
//   at org.jetbrains.java.decompiler.main.InitializerProcessor.isExprentIndependent(InitializerProcessor.java:423)
//   at org.jetbrains.java.decompiler.main.InitializerProcessor.extractDynamicInitializers(InitializerProcessor.java:335)
//   at org.jetbrains.java.decompiler.main.InitializerProcessor.extractInitializers(InitializerProcessor.java:44)
//   at org.jetbrains.java.decompiler.main.ClassWriter.invokeProcessors(ClassWriter.java:97)
//   at org.jetbrains.java.decompiler.main.ClassWriter.writeClass(ClassWriter.java:348)
//   at org.jetbrains.java.decompiler.main.ClassWriter.writeClass(ClassWriter.java:492)
//   at org.jetbrains.java.decompiler.main.ClassesProcessor.writeClass(ClassesProcessor.java:474)
//   at org.jetbrains.java.decompiler.main.Fernflower.getClassContent(Fernflower.java:191)
//   at org.jetbrains.java.decompiler.struct.ContextUnit.lambda$save$3(ContextUnit.java:187)

// $VF: Couldn't be decompiled
// Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
// java.lang.NullPointerException
//   at org.jetbrains.java.decompiler.main.InitializerProcessor.isExprentIndependent(InitializerProcessor.java:423)
//   at org.jetbrains.java.decompiler.main.InitializerProcessor.extractDynamicInitializers(InitializerProcessor.java:335)
//   at org.jetbrains.java.decompiler.main.InitializerProcessor.extractInitializers(InitializerProcessor.java:44)
//   at org.jetbrains.java.decompiler.main.ClassWriter.invokeProcessors(ClassWriter.java:97)
//   at org.jetbrains.java.decompiler.main.ClassWriter.writeClass(ClassWriter.java:348)
//   at org.jetbrains.java.decompiler.main.ClassWriter.writeClass(ClassWriter.java:492)
//   at org.jetbrains.java.decompiler.main.ClassesProcessor.writeClass(ClassesProcessor.java:474)
//   at org.jetbrains.java.decompiler.main.Fernflower.getClassContent(Fernflower.java:191)
//   at org.jetbrains.java.decompiler.struct.ContextUnit.lambda$save$3(ContextUnit.java:187)
}
