package net.minecraft.world.level.levelgen;

import com.google.common.collect.ImmutableList;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Arrays;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.DoubleSupplier;
import java.util.function.Function;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.HolderSet;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryCodecs;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.levelgen.placement.CaveSurface;
import net.minecraft.world.level.levelgen.synth.NormalNoise;
import org.jspecify.annotations.Nullable;

public class SurfaceRules {
   public static final ConditionSource ON_FLOOR;
   public static final ConditionSource UNDER_FLOOR;
   public static final ConditionSource DEEP_UNDER_FLOOR;
   public static final ConditionSource VERY_DEEP_UNDER_FLOOR;
   public static final ConditionSource ON_CEILING;
   public static final ConditionSource UNDER_CEILING;

   public SurfaceRules() {
      super();
   }

   public static ConditionSource stoneDepthCheck(final int offset, final boolean addSurfaceDepth1, final CaveSurface surfaceType) {
      return new StoneDepthCheck(offset, addSurfaceDepth1, 0, surfaceType);
   }

   public static ConditionSource stoneDepthCheck(final int offset, final boolean addSurfaceDepth1, final int secondaryDepthRange, final CaveSurface surfaceType) {
      return new StoneDepthCheck(offset, addSurfaceDepth1, secondaryDepthRange, surfaceType);
   }

   public static ConditionSource not(final ConditionSource target) {
      return new NotConditionSource(target);
   }

   public static ConditionSource yBlockCheck(final VerticalAnchor anchor, final int surfaceDepthMultiplier) {
      return new YConditionSource(anchor, surfaceDepthMultiplier, false);
   }

   public static ConditionSource yStartCheck(final VerticalAnchor anchor, final int surfaceDepthMultiplier) {
      return new YConditionSource(anchor, surfaceDepthMultiplier, true);
   }

   public static ConditionSource waterBlockCheck(final int offset, final int surfaceDepthMultiplier) {
      return new WaterConditionSource(offset, surfaceDepthMultiplier, false);
   }

   public static ConditionSource waterStartCheck(final int offset, final int surfaceDepthMultiplier) {
      return new WaterConditionSource(offset, surfaceDepthMultiplier, true);
   }

   @SafeVarargs
   public static ConditionSource isBiome(final HolderGetter<Biome> biomes, final ResourceKey<Biome>... target) {
      Objects.requireNonNull(biomes);
      return new BiomeConditionSource(HolderSet.direct(biomes::getOrThrow, target));
   }

   public static ConditionSource noiseCondition2d(final ResourceKey<NormalNoise.NoiseParameters> noise, final double minRange) {
      return noiseCondition2d(noise, minRange, 1.7976931348623157E308);
   }

   public static ConditionSource noiseCondition2d(final ResourceKey<NormalNoise.NoiseParameters> noise, final double minRange, final double maxRange) {
      return new NoiseThresholdConditionSource(noise, minRange, maxRange, false);
   }

   public static ConditionSource noiseCondition3d(final ResourceKey<NormalNoise.NoiseParameters> noise, final double minRange) {
      return noiseCondition3d(noise, minRange, 1.7976931348623157E308);
   }

   public static ConditionSource noiseCondition3d(final ResourceKey<NormalNoise.NoiseParameters> noise, final double minRange, final double maxRange) {
      return new NoiseThresholdConditionSource(noise, minRange, maxRange, true);
   }

   public static ConditionSource verticalGradient(final String randomName, final VerticalAnchor trueAtAndBelow, final VerticalAnchor falseAtAndAbove) {
      return new VerticalGradientConditionSource(Identifier.parse(randomName), trueAtAndBelow, falseAtAndAbove);
   }

   public static ConditionSource steep() {
      return SurfaceRules.Steep.INSTANCE;
   }

   public static ConditionSource hole() {
      return SurfaceRules.Hole.INSTANCE;
   }

   public static ConditionSource abovePreliminarySurface() {
      return SurfaceRules.AbovePreliminarySurface.INSTANCE;
   }

   public static ConditionSource temperature() {
      return SurfaceRules.Temperature.INSTANCE;
   }

   public static RuleSource ifTrue(final ConditionSource condition, final RuleSource next) {
      return new TestRuleSource(condition, next);
   }

   public static RuleSource sequence(final RuleSource... rules) {
      if (rules.length == 0) {
         throw new IllegalArgumentException("Need at least 1 rule for a sequence");
      } else {
         return new SequenceRuleSource(Arrays.asList(rules));
      }
   }

   public static RuleSource state(final BlockState state) {
      return new BlockRuleSource(state);
   }

   public static RuleSource bandlands() {
      return SurfaceRules.Bandlands.INSTANCE;
   }

   private static <A> MapCodec<? extends A> register(final Registry<MapCodec<? extends A>> registry, final String name, final MapCodec<? extends A> codec) {
      return (MapCodec)Registry.register(registry, (String)name, codec);
   }

   static {
      ON_FLOOR = stoneDepthCheck(0, false, CaveSurface.FLOOR);
      UNDER_FLOOR = stoneDepthCheck(0, true, CaveSurface.FLOOR);
      DEEP_UNDER_FLOOR = stoneDepthCheck(0, true, 6, CaveSurface.FLOOR);
      VERY_DEEP_UNDER_FLOOR = stoneDepthCheck(0, true, 30, CaveSurface.FLOOR);
      ON_CEILING = stoneDepthCheck(0, false, CaveSurface.CEILING);
      UNDER_CEILING = stoneDepthCheck(0, true, CaveSurface.CEILING);
   }

   protected static final class Context {
      private static final int HOW_FAR_BELOW_PRELIMINARY_SURFACE_LEVEL_TO_BUILD_SURFACE = 8;
      private static final int SURFACE_CELL_BITS = 4;
      private static final int SURFACE_CELL_SIZE = 16;
      private static final int SURFACE_CELL_MASK = 15;
      private final SurfaceSystem system;
      private final Condition temperature = new TemperatureHelperCondition(this);
      private final Condition steep = new SteepMaterialCondition(this);
      private final Condition hole = new HoleCondition(this);
      private final Condition abovePreliminarySurface = new AbovePreliminarySurfaceCondition();
      private final RandomState randomState;
      private final ChunkAccess chunk;
      private final NoiseChunk noiseChunk;
      private final Function<BlockPos, Holder<Biome>> biomeGetter;
      private final WorldGenerationContext context;
      private final @Nullable Set<Holder<Biome>> possibleBiomes;
      private long lastPreliminarySurfaceCellOrigin = 9223372036854775807L;
      private final int[] preliminarySurfaceCache = new int[4];
      private final Map<ResourceKey<NormalNoise.NoiseParameters>, DoubleSupplier> noiseSamplers2d = new IdentityHashMap();
      private final Map<ResourceKey<NormalNoise.NoiseParameters>, DoubleSupplier> noiseSamplers3d = new IdentityHashMap();
      private long lastUpdateXZ = -9223372036854775807L;
      private int blockX;
      private int blockZ;
      private int surfaceDepth;
      private long lastSurfaceDepth2Update;
      private double surfaceSecondary;
      private long lastMinSurfaceLevelUpdate;
      private int minSurfaceLevel;
      private long lastUpdateY;
      private final BlockPos.MutableBlockPos pos;
      private @Nullable Holder<Biome> biome;
      private int blockY;
      private int waterHeight;
      private int stoneDepthBelow;
      private int stoneDepthAbove;

      protected Context(final SurfaceSystem system, final RandomState randomState, final ChunkAccess chunk, final NoiseChunk noiseChunk, final Function<BlockPos, Holder<Biome>> biomeGetter, final WorldGenerationContext context, final @Nullable Set<Holder<Biome>> possibleBiomes) {
         super();
         this.lastSurfaceDepth2Update = this.lastUpdateXZ - 1L;
         this.lastMinSurfaceLevelUpdate = this.lastUpdateXZ - 1L;
         this.lastUpdateY = -9223372036854775807L;
         this.pos = new BlockPos.MutableBlockPos();
         this.system = system;
         this.randomState = randomState;
         this.chunk = chunk;
         this.noiseChunk = noiseChunk;
         this.biomeGetter = biomeGetter;
         this.context = context;
         this.possibleBiomes = possibleBiomes;
      }

      protected void updateXZ(final int blockX, final int blockZ) {
         ++this.lastUpdateXZ;
         ++this.lastUpdateY;
         this.blockX = blockX;
         this.blockZ = blockZ;
         this.surfaceDepth = this.system.getSurfaceDepth(blockX, blockZ);
      }

      protected void updateY(final int stoneDepthAbove, final int stoneDepthBelow, final int waterHeight, final int blockY) {
         ++this.lastUpdateY;
         this.biome = null;
         this.blockY = blockY;
         this.waterHeight = waterHeight;
         this.stoneDepthBelow = stoneDepthBelow;
         this.stoneDepthAbove = stoneDepthAbove;
      }

      protected double getSurfaceSecondary() {
         if (this.lastSurfaceDepth2Update != this.lastUpdateXZ) {
            this.lastSurfaceDepth2Update = this.lastUpdateXZ;
            this.surfaceSecondary = this.system.getSurfaceSecondary(this.blockX, this.blockZ);
         }

         return this.surfaceSecondary;
      }

      protected Holder<Biome> getBiome() {
         if (this.biome == null) {
            this.biome = (Holder)this.biomeGetter.apply(this.pos.set(this.blockX, this.blockY, this.blockZ));
         }

         return this.biome;
      }

      public int getSeaLevel() {
         return this.system.getSeaLevel();
      }

      private static int blockCoordToSurfaceCell(final int blockCoord) {
         return blockCoord >> 4;
      }

      private static int surfaceCellToBlockCoord(final int cellCoord) {
         return cellCoord << 4;
      }

      protected int getMinSurfaceLevel() {
         if (this.lastMinSurfaceLevelUpdate != this.lastUpdateXZ) {
            this.lastMinSurfaceLevelUpdate = this.lastUpdateXZ;
            int cornerCellX = blockCoordToSurfaceCell(this.blockX);
            int cornerCellZ = blockCoordToSurfaceCell(this.blockZ);
            long preliminarySurfaceCellOrigin = ChunkPos.pack(cornerCellX, cornerCellZ);
            if (this.lastPreliminarySurfaceCellOrigin != preliminarySurfaceCellOrigin) {
               this.lastPreliminarySurfaceCellOrigin = preliminarySurfaceCellOrigin;
               this.preliminarySurfaceCache[0] = this.noiseChunk.preliminarySurfaceLevel(surfaceCellToBlockCoord(cornerCellX), surfaceCellToBlockCoord(cornerCellZ));
               this.preliminarySurfaceCache[1] = this.noiseChunk.preliminarySurfaceLevel(surfaceCellToBlockCoord(cornerCellX + 1), surfaceCellToBlockCoord(cornerCellZ));
               this.preliminarySurfaceCache[2] = this.noiseChunk.preliminarySurfaceLevel(surfaceCellToBlockCoord(cornerCellX), surfaceCellToBlockCoord(cornerCellZ + 1));
               this.preliminarySurfaceCache[3] = this.noiseChunk.preliminarySurfaceLevel(surfaceCellToBlockCoord(cornerCellX + 1), surfaceCellToBlockCoord(cornerCellZ + 1));
            }

            int preliminarySurfaceLevel = Mth.floor(Mth.lerp2((double)((float)(this.blockX & 15) / 16.0F), (double)((float)(this.blockZ & 15) / 16.0F), (double)this.preliminarySurfaceCache[0], (double)this.preliminarySurfaceCache[1], (double)this.preliminarySurfaceCache[2], (double)this.preliminarySurfaceCache[3]));
            this.minSurfaceLevel = preliminarySurfaceLevel + this.surfaceDepth - 8;
         }

         return this.minSurfaceLevel;
      }

      protected DoubleSupplier getNoiseSampler(final ResourceKey<NormalNoise.NoiseParameters> noiseId, final boolean is3d) {
         return is3d ? (DoubleSupplier)this.noiseSamplers3d.computeIfAbsent(noiseId, this::createNoiseSampler3d) : (DoubleSupplier)this.noiseSamplers2d.computeIfAbsent(noiseId, this::createNoiseSampler2d);
      }

      private DoubleSupplier createNoiseSampler2d(final ResourceKey<NormalNoise.NoiseParameters> noiseId) {
         final NormalNoise noise = this.randomState.getOrCreateNoise(noiseId);
         return new DoubleSupplier() {
            private long lastUpdateXZ;
            private double lastNoise;

            {
               Objects.requireNonNull(Context.this);
               this.lastUpdateXZ = Context.this.lastUpdateXZ - 1L;
            }

            public double getAsDouble() {
               if (this.lastUpdateXZ != Context.this.lastUpdateXZ) {
                  this.lastNoise = noise.getValue((double)Context.this.blockX, 0.0, (double)Context.this.blockZ);
                  this.lastUpdateXZ = Context.this.lastUpdateXZ;
               }

               return this.lastNoise;
            }
         };
      }

      private DoubleSupplier createNoiseSampler3d(final ResourceKey<NormalNoise.NoiseParameters> noiseId) {
         final NormalNoise noise = this.randomState.getOrCreateNoise(noiseId);
         return new DoubleSupplier() {
            private long lastUpdateY;
            private double lastNoise;

            {
               Objects.requireNonNull(Context.this);
               this.lastUpdateY = Context.this.lastUpdateY - 1L;
            }

            public double getAsDouble() {
               if (this.lastUpdateY != Context.this.lastUpdateY) {
                  this.lastNoise = noise.getValue((double)Context.this.blockX, (double)Context.this.blockY, (double)Context.this.blockZ);
                  this.lastUpdateY = Context.this.lastUpdateY;
               }

               return this.lastNoise;
            }
         };
      }

      private static final class HoleCondition extends LazyXZCondition {
         private HoleCondition(final Context context) {
            super(context);
         }

         protected boolean compute() {
            return this.context.surfaceDepth <= 0;
         }
      }

      private final class AbovePreliminarySurfaceCondition implements Condition {
         private AbovePreliminarySurfaceCondition() {
            Objects.requireNonNull(Context.this);
            super();
         }

         public boolean test() {
            return Context.this.blockY >= Context.this.getMinSurfaceLevel();
         }
      }

      private static class TemperatureHelperCondition extends LazyYCondition {
         private TemperatureHelperCondition(final Context context) {
            super(context);
         }

         protected boolean compute() {
            return ((Biome)this.context.getBiome().value()).coldEnoughToSnow(this.context.pos.set(this.context.blockX, this.context.blockY, this.context.blockZ), this.context.getSeaLevel());
         }
      }

      private static class SteepMaterialCondition extends LazyXZCondition {
         private SteepMaterialCondition(final Context context) {
            super(context);
         }

         protected boolean compute() {
            int chunkBlockX = this.context.blockX & 15;
            int chunkBlockZ = this.context.blockZ & 15;
            int zNorth = Math.max(chunkBlockZ - 1, 0);
            int zSouth = Math.min(chunkBlockZ + 1, 15);
            ChunkAccess chunk = this.context.chunk;
            int heightNorth = chunk.getHeight(Heightmap.Types.WORLD_SURFACE_WG, chunkBlockX, zNorth);
            int heightSouth = chunk.getHeight(Heightmap.Types.WORLD_SURFACE_WG, chunkBlockX, zSouth);
            if (heightSouth >= heightNorth + 4) {
               return true;
            } else {
               int xWest = Math.max(chunkBlockX - 1, 0);
               int xEast = Math.min(chunkBlockX + 1, 15);
               int heightWest = chunk.getHeight(Heightmap.Types.WORLD_SURFACE_WG, xWest, chunkBlockZ);
               int heightEast = chunk.getHeight(Heightmap.Types.WORLD_SURFACE_WG, xEast, chunkBlockZ);
               return heightWest >= heightEast + 4;
            }
         }
      }
   }

   private abstract static class LazyCondition implements Condition {
      protected final Context context;
      private long lastUpdate;
      private @Nullable Boolean result;

      protected LazyCondition(final Context context) {
         super();
         this.context = context;
         this.lastUpdate = this.getContextLastUpdate() - 1L;
      }

      public boolean test() {
         long lastContextUpdate = this.getContextLastUpdate();
         if (lastContextUpdate == this.lastUpdate) {
            if (this.result == null) {
               throw new IllegalStateException("Update triggered but the result is null");
            } else {
               return this.result;
            }
         } else {
            this.lastUpdate = lastContextUpdate;
            this.result = this.compute();
            return this.result;
         }
      }

      protected abstract long getContextLastUpdate();

      protected abstract boolean compute();
   }

   private abstract static class LazyXZCondition extends LazyCondition {
      protected LazyXZCondition(final Context context) {
         super(context);
      }

      protected long getContextLastUpdate() {
         return this.context.lastUpdateXZ;
      }
   }

   private abstract static class LazyYCondition extends LazyCondition {
      protected LazyYCondition(final Context context) {
         super(context);
      }

      protected long getContextLastUpdate() {
         return this.context.lastUpdateY;
      }
   }

   private static record NotCondition(Condition target) implements Condition {
      private NotCondition {
         super();
      }

      public boolean test() {
         return !this.target.test();
      }
   }

   private static record StateRule(BlockState state) implements SurfaceRule {
      private StateRule {
         super();
      }

      public BlockState tryApply(final int blockX, final int blockY, final int blockZ) {
         return this.state;
      }
   }

   private static record TestRule(Condition condition, SurfaceRule followup) implements SurfaceRule {
      private TestRule {
         super();
      }

      public @Nullable BlockState tryApply(final int blockX, final int blockY, final int blockZ) {
         return !this.condition.test() ? null : this.followup.tryApply(blockX, blockY, blockZ);
      }
   }

   private static record SequenceRule(List<SurfaceRule> rules) implements SurfaceRule {
      private SequenceRule {
         super();
      }

      public @Nullable BlockState tryApply(final int blockX, final int blockY, final int blockZ) {
         for(SurfaceRule rule : this.rules) {
            BlockState state = rule.tryApply(blockX, blockY, blockZ);
            if (state != null) {
               return state;
            }
         }

         return null;
      }
   }

   public interface ConditionSource extends Function<Context, Condition> {
      Codec<ConditionSource> CODEC = BuiltInRegistries.MATERIAL_CONDITION.byNameCodec().dispatch(ConditionSource::codec, Function.identity());

      static MapCodec<? extends ConditionSource> bootstrap(final Registry<MapCodec<? extends ConditionSource>> registry) {
         SurfaceRules.register(registry, "biome", SurfaceRules.BiomeConditionSource.CODEC);
         SurfaceRules.register(registry, "noise_threshold", SurfaceRules.NoiseThresholdConditionSource.CODEC);
         SurfaceRules.register(registry, "vertical_gradient", SurfaceRules.VerticalGradientConditionSource.CODEC);
         SurfaceRules.register(registry, "y_above", SurfaceRules.YConditionSource.CODEC);
         SurfaceRules.register(registry, "water", SurfaceRules.WaterConditionSource.CODEC);
         SurfaceRules.register(registry, "temperature", SurfaceRules.Temperature.CODEC);
         SurfaceRules.register(registry, "steep", SurfaceRules.Steep.CODEC);
         SurfaceRules.register(registry, "not", SurfaceRules.NotConditionSource.CODEC);
         SurfaceRules.register(registry, "hole", SurfaceRules.Hole.CODEC);
         SurfaceRules.register(registry, "above_preliminary_surface", SurfaceRules.AbovePreliminarySurface.CODEC);
         return SurfaceRules.<ConditionSource>register(registry, "stone_depth", SurfaceRules.StoneDepthCheck.CODEC);
      }

      MapCodec<? extends ConditionSource> codec();
   }

   public interface RuleSource extends Function<Context, SurfaceRule> {
      Codec<RuleSource> CODEC = BuiltInRegistries.MATERIAL_RULE.byNameCodec().dispatch(RuleSource::codec, Function.identity());

      static MapCodec<? extends RuleSource> bootstrap(final Registry<MapCodec<? extends RuleSource>> registry) {
         SurfaceRules.register(registry, "bandlands", SurfaceRules.Bandlands.CODEC);
         SurfaceRules.register(registry, "block", SurfaceRules.BlockRuleSource.CODEC);
         SurfaceRules.register(registry, "sequence", SurfaceRules.SequenceRuleSource.CODEC);
         return SurfaceRules.<RuleSource>register(registry, "condition", SurfaceRules.TestRuleSource.CODEC);
      }

      MapCodec<? extends RuleSource> codec();
   }

   private static record NotConditionSource(ConditionSource target) implements ConditionSource {
      private static final MapCodec<NotConditionSource> CODEC;

      private NotConditionSource {
         super();
      }

      public MapCodec<NotConditionSource> codec() {
         return CODEC;
      }

      public Condition apply(final Context context) {
         return new NotCondition((Condition)this.target.apply(context));
      }

      static {
         CODEC = SurfaceRules.ConditionSource.CODEC.xmap(NotConditionSource::new, NotConditionSource::target).fieldOf("invert");
      }
   }

   private static record StoneDepthCheck(int offset, boolean addSurfaceDepth, int secondaryDepthRange, CaveSurface surfaceType) implements ConditionSource {
      private static final MapCodec<StoneDepthCheck> CODEC = RecordCodecBuilder.mapCodec((i) -> i.group(Codec.INT.fieldOf("offset").forGetter(StoneDepthCheck::offset), Codec.BOOL.fieldOf("add_surface_depth").forGetter(StoneDepthCheck::addSurfaceDepth), Codec.INT.fieldOf("secondary_depth_range").forGetter(StoneDepthCheck::secondaryDepthRange), CaveSurface.CODEC.fieldOf("surface_type").forGetter(StoneDepthCheck::surfaceType)).apply(i, StoneDepthCheck::new));

      private StoneDepthCheck {
         super();
      }

      public MapCodec<StoneDepthCheck> codec() {
         return CODEC;
      }

      public Condition apply(final Context ruleContext) {
         final boolean ceiling = this.surfaceType == CaveSurface.CEILING;

         class StoneDepthCondition extends LazyYCondition {
            private StoneDepthCondition() {
               Objects.requireNonNull(StoneDepthCheck.this);
               super(StoneDepthCheck.this);
            }

            protected boolean compute() {
               int stoneDepth = ceiling ? this.context.stoneDepthBelow : this.context.stoneDepthAbove;
               int surfaceDepth = StoneDepthCheck.this.addSurfaceDepth ? this.context.surfaceDepth : 0;
               int secondarySurfaceDepth = StoneDepthCheck.this.secondaryDepthRange == 0 ? 0 : (int)Mth.map(this.context.getSurfaceSecondary(), -1.0, 1.0, 0.0, (double)StoneDepthCheck.this.secondaryDepthRange);
               return stoneDepth <= 1 + StoneDepthCheck.this.offset + surfaceDepth + secondarySurfaceDepth;
            }
         }

         return new StoneDepthCondition();
      }
   }

   private static enum AbovePreliminarySurface implements ConditionSource {
      INSTANCE;

      private static final MapCodec<AbovePreliminarySurface> CODEC = MapCodec.unit(INSTANCE);

      private AbovePreliminarySurface() {
      }

      public MapCodec<AbovePreliminarySurface> codec() {
         return CODEC;
      }

      public Condition apply(final Context context) {
         return context.abovePreliminarySurface;
      }

      // $FF: synthetic method
      private static AbovePreliminarySurface[] $values() {
         return new AbovePreliminarySurface[]{INSTANCE};
      }
   }

   private static enum Hole implements ConditionSource {
      INSTANCE;

      private static final MapCodec<Hole> CODEC = MapCodec.unit(INSTANCE);

      private Hole() {
      }

      public MapCodec<Hole> codec() {
         return CODEC;
      }

      public Condition apply(final Context context) {
         return context.hole;
      }

      // $FF: synthetic method
      private static Hole[] $values() {
         return new Hole[]{INSTANCE};
      }
   }

   private static record YConditionSource(VerticalAnchor anchor, int surfaceDepthMultiplier, boolean addStoneDepth) implements ConditionSource {
      private static final MapCodec<YConditionSource> CODEC = RecordCodecBuilder.mapCodec((i) -> i.group(VerticalAnchor.CODEC.fieldOf("anchor").forGetter(YConditionSource::anchor), Codec.intRange(-20, 20).fieldOf("surface_depth_multiplier").forGetter(YConditionSource::surfaceDepthMultiplier), Codec.BOOL.fieldOf("add_stone_depth").forGetter(YConditionSource::addStoneDepth)).apply(i, YConditionSource::new));

      private YConditionSource {
         super();
      }

      public MapCodec<YConditionSource> codec() {
         return CODEC;
      }

      public Condition apply(final Context ruleContext) {
         class YCondition extends LazyYCondition {
            private YCondition() {
               Objects.requireNonNull(YConditionSource.this);
               super(YConditionSource.this);
            }

            protected boolean compute() {
               return this.context.blockY + (YConditionSource.this.addStoneDepth ? this.context.stoneDepthAbove : 0) >= YConditionSource.this.anchor.resolveY(this.context.context) + this.context.surfaceDepth * YConditionSource.this.surfaceDepthMultiplier;
            }
         }

         return new YCondition();
      }
   }

   private static record WaterConditionSource(int offset, int surfaceDepthMultiplier, boolean addStoneDepth) implements ConditionSource {
      private static final MapCodec<WaterConditionSource> CODEC = RecordCodecBuilder.mapCodec((i) -> i.group(Codec.INT.fieldOf("offset").forGetter(WaterConditionSource::offset), Codec.intRange(-20, 20).fieldOf("surface_depth_multiplier").forGetter(WaterConditionSource::surfaceDepthMultiplier), Codec.BOOL.fieldOf("add_stone_depth").forGetter(WaterConditionSource::addStoneDepth)).apply(i, WaterConditionSource::new));

      private WaterConditionSource {
         super();
      }

      public MapCodec<WaterConditionSource> codec() {
         return CODEC;
      }

      public Condition apply(final Context ruleContext) {
         class WaterCondition extends LazyYCondition {
            private WaterCondition() {
               Objects.requireNonNull(WaterConditionSource.this);
               super(WaterConditionSource.this);
            }

            protected boolean compute() {
               return this.context.waterHeight == -2147483648 || this.context.blockY + (WaterConditionSource.this.addStoneDepth ? this.context.stoneDepthAbove : 0) >= this.context.waterHeight + WaterConditionSource.this.offset + this.context.surfaceDepth * WaterConditionSource.this.surfaceDepthMultiplier;
            }
         }

         return new WaterCondition();
      }
   }

   private static record BiomeConditionSource(HolderSet<Biome> biomes) implements ConditionSource {
      private static final MapCodec<BiomeConditionSource> CODEC = RecordCodecBuilder.mapCodec((i) -> i.group(RegistryCodecs.homogeneousList(Registries.BIOME).fieldOf("biome_is").forGetter(BiomeConditionSource::biomes)).apply(i, BiomeConditionSource::new));

      private BiomeConditionSource {
         super();
      }

      public MapCodec<BiomeConditionSource> codec() {
         return CODEC;
      }

      public Condition apply(final Context ruleContext) {
         if (ruleContext.possibleBiomes != null) {
            if (this.canNeverMatch(ruleContext.possibleBiomes)) {
               return () -> false;
            }

            if (this.willAlwaysMatch(ruleContext.possibleBiomes)) {
               return () -> true;
            }
         }

         class BiomeCondition extends LazyYCondition {
            private BiomeCondition() {
               Objects.requireNonNull(BiomeConditionSource.this);
               super(BiomeConditionSource.this);
            }

            protected boolean compute() {
               return BiomeConditionSource.this.biomes.contains(this.context.getBiome());
            }
         }

         return new BiomeCondition();
      }

      private boolean canNeverMatch(final Set<Holder<Biome>> possibleBiomes) {
         for(Holder<Biome> biome : this.biomes) {
            if (possibleBiomes.contains(biome)) {
               return false;
            }
         }

         return true;
      }

      private boolean willAlwaysMatch(final Set<Holder<Biome>> possibleBiomes) {
         for(Holder<Biome> possibleBiome : possibleBiomes) {
            if (!this.biomes.contains(possibleBiome)) {
               return false;
            }
         }

         return true;
      }

      public String toString() {
         return "BiomeConditionSource[biomes=" + String.valueOf(this.biomes) + "]";
      }
   }

   private static record NoiseThresholdConditionSource(ResourceKey<NormalNoise.NoiseParameters> noise, double minThreshold, double maxThreshold, boolean is3d) implements ConditionSource {
      private static final MapCodec<NoiseThresholdConditionSource> CODEC = RecordCodecBuilder.mapCodec((i) -> i.group(ResourceKey.codec(Registries.NOISE).fieldOf("noise").forGetter(NoiseThresholdConditionSource::noise), Codec.DOUBLE.fieldOf("min_threshold").forGetter(NoiseThresholdConditionSource::minThreshold), Codec.DOUBLE.fieldOf("max_threshold").forGetter(NoiseThresholdConditionSource::maxThreshold), Codec.BOOL.optionalFieldOf("is_3d", false).forGetter(NoiseThresholdConditionSource::is3d)).apply(i, NoiseThresholdConditionSource::new));

      private NoiseThresholdConditionSource {
         super();
      }

      public MapCodec<NoiseThresholdConditionSource> codec() {
         return CODEC;
      }

      public Condition apply(final Context ruleContext) {
         final DoubleSupplier noise = ruleContext.getNoiseSampler(this.noise, this.is3d);

         class NoiseThresholdCondition implements Condition {
            NoiseThresholdCondition() {
               Objects.requireNonNull(NoiseThresholdConditionSource.this);
               super();
            }

            public boolean test() {
               double value = noise.getAsDouble();
               return value >= NoiseThresholdConditionSource.this.minThreshold && value <= NoiseThresholdConditionSource.this.maxThreshold;
            }
         }

         return new NoiseThresholdCondition();
      }
   }

   private static record VerticalGradientConditionSource(Identifier randomName, VerticalAnchor trueAtAndBelow, VerticalAnchor falseAtAndAbove) implements ConditionSource {
      private static final MapCodec<VerticalGradientConditionSource> CODEC = RecordCodecBuilder.mapCodec((i) -> i.group(Identifier.CODEC.fieldOf("random_name").forGetter(VerticalGradientConditionSource::randomName), VerticalAnchor.CODEC.fieldOf("true_at_and_below").forGetter(VerticalGradientConditionSource::trueAtAndBelow), VerticalAnchor.CODEC.fieldOf("false_at_and_above").forGetter(VerticalGradientConditionSource::falseAtAndAbove)).apply(i, VerticalGradientConditionSource::new));

      private VerticalGradientConditionSource {
         super();
      }

      public MapCodec<VerticalGradientConditionSource> codec() {
         return CODEC;
      }

      public Condition apply(final Context ruleContext) {
         final int trueAtAndBelow = this.trueAtAndBelow().resolveY(ruleContext.context);
         final int falseAtAndAbove = this.falseAtAndAbove().resolveY(ruleContext.context);
         final PositionalRandomFactory randomFactory = ruleContext.randomState.getOrCreateRandomFactory(this.randomName());

         class VerticalGradientCondition extends LazyYCondition {
            private VerticalGradientCondition() {
               Objects.requireNonNull(VerticalGradientConditionSource.this);
               super(VerticalGradientConditionSource.this);
            }

            protected boolean compute() {
               int blockY = this.context.blockY;
               if (blockY <= trueAtAndBelow) {
                  return true;
               } else if (blockY >= falseAtAndAbove) {
                  return false;
               } else {
                  double probability = Mth.map((double)blockY, (double)trueAtAndBelow, (double)falseAtAndAbove, 1.0, 0.0);
                  RandomSource random = randomFactory.at(this.context.blockX, blockY, this.context.blockZ);
                  return (double)random.nextFloat() < probability;
               }
            }
         }

         return new VerticalGradientCondition();
      }
   }

   private static enum Temperature implements ConditionSource {
      INSTANCE;

      private static final MapCodec<Temperature> CODEC = MapCodec.unit(INSTANCE);

      private Temperature() {
      }

      public MapCodec<Temperature> codec() {
         return CODEC;
      }

      public Condition apply(final Context context) {
         return context.temperature;
      }

      // $FF: synthetic method
      private static Temperature[] $values() {
         return new Temperature[]{INSTANCE};
      }
   }

   private static enum Steep implements ConditionSource {
      INSTANCE;

      private static final MapCodec<Steep> CODEC = MapCodec.unit(INSTANCE);

      private Steep() {
      }

      public MapCodec<Steep> codec() {
         return CODEC;
      }

      public Condition apply(final Context context) {
         return context.steep;
      }

      // $FF: synthetic method
      private static Steep[] $values() {
         return new Steep[]{INSTANCE};
      }
   }

   private static record BlockRuleSource(BlockState resultState, StateRule rule) implements RuleSource {
      private static final MapCodec<BlockRuleSource> CODEC;

      private BlockRuleSource(final BlockState state) {
         this(state, new StateRule(state));
      }

      private BlockRuleSource {
         super();
      }

      public MapCodec<BlockRuleSource> codec() {
         return CODEC;
      }

      public SurfaceRule apply(final Context context) {
         return this.rule;
      }

      static {
         CODEC = BlockState.CODEC.xmap(BlockRuleSource::new, BlockRuleSource::resultState).fieldOf("result_state");
      }
   }

   private static record TestRuleSource(ConditionSource ifTrue, RuleSource thenRun) implements RuleSource {
      private static final MapCodec<TestRuleSource> CODEC = RecordCodecBuilder.mapCodec((i) -> i.group(SurfaceRules.ConditionSource.CODEC.fieldOf("if_true").forGetter(TestRuleSource::ifTrue), SurfaceRules.RuleSource.CODEC.fieldOf("then_run").forGetter(TestRuleSource::thenRun)).apply(i, TestRuleSource::new));

      private TestRuleSource {
         super();
      }

      public MapCodec<TestRuleSource> codec() {
         return CODEC;
      }

      public SurfaceRule apply(final Context context) {
         return new TestRule((Condition)this.ifTrue.apply(context), (SurfaceRule)this.thenRun.apply(context));
      }
   }

   private static record SequenceRuleSource(List<RuleSource> sequence) implements RuleSource {
      private static final MapCodec<SequenceRuleSource> CODEC;

      private SequenceRuleSource {
         super();
      }

      public MapCodec<SequenceRuleSource> codec() {
         return CODEC;
      }

      public SurfaceRule apply(final Context context) {
         if (this.sequence.size() == 1) {
            return (SurfaceRule)((RuleSource)this.sequence.get(0)).apply(context);
         } else {
            ImmutableList.Builder<SurfaceRule> builder = ImmutableList.builder();

            for(RuleSource rule : this.sequence) {
               builder.add((SurfaceRule)rule.apply(context));
            }

            return new SequenceRule(builder.build());
         }
      }

      static {
         CODEC = SurfaceRules.RuleSource.CODEC.listOf().xmap(SequenceRuleSource::new, SequenceRuleSource::sequence).fieldOf("sequence");
      }
   }

   private static enum Bandlands implements RuleSource {
      INSTANCE;

      private static final MapCodec<Bandlands> CODEC = MapCodec.unit(INSTANCE);

      private Bandlands() {
      }

      public MapCodec<Bandlands> codec() {
         return CODEC;
      }

      public SurfaceRule apply(final Context context) {
         SurfaceSystem var10000 = context.system;
         Objects.requireNonNull(var10000);
         return var10000::getBand;
      }

      // $FF: synthetic method
      private static Bandlands[] $values() {
         return new Bandlands[]{INSTANCE};
      }
   }

   private interface Condition {
      boolean test();
   }

   protected interface SurfaceRule {
      @Nullable BlockState tryApply(final int blockX, final int blockY, final int blockZ);
   }
}
