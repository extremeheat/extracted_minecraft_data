package net.minecraft.world.level.levelgen;

import com.google.common.base.Suppliers;
import com.google.common.collect.ImmutableList;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
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
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.levelgen.placement.CaveSurface;
import net.minecraft.world.level.levelgen.synth.NormalNoise;

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

   public static ConditionSource stoneDepthCheck(int var0, boolean var1, CaveSurface var2) {
      return new StoneDepthCheck(var0, var1, 0, var2);
   }

   public static ConditionSource stoneDepthCheck(int var0, boolean var1, int var2, CaveSurface var3) {
      return new StoneDepthCheck(var0, var1, var2, var3);
   }

   public static ConditionSource not(ConditionSource var0) {
      return new NotConditionSource(var0);
   }

   public static ConditionSource yBlockCheck(VerticalAnchor var0, int var1) {
      return new YConditionSource(var0, var1, false);
   }

   public static ConditionSource yStartCheck(VerticalAnchor var0, int var1) {
      return new YConditionSource(var0, var1, true);
   }

   public static ConditionSource waterBlockCheck(int var0, int var1) {
      return new WaterConditionSource(var0, var1, false);
   }

   public static ConditionSource waterStartCheck(int var0, int var1) {
      return new WaterConditionSource(var0, var1, true);
   }

   @SafeVarargs
   public static ConditionSource isBiome(ResourceKey<Biome>... var0) {
      return isBiome(List.of(var0));
   }

   private static BiomeConditionSource isBiome(List<ResourceKey<Biome>> var0) {
      return new BiomeConditionSource(var0);
   }

   public static ConditionSource noiseCondition(ResourceKey<NormalNoise.NoiseParameters> var0, double var1) {
      return noiseCondition(var0, var1, 1.7976931348623157E308);
   }

   public static ConditionSource noiseCondition(ResourceKey<NormalNoise.NoiseParameters> var0, double var1, double var3) {
      return new NoiseThresholdConditionSource(var0, var1, var3);
   }

   public static ConditionSource verticalGradient(String var0, VerticalAnchor var1, VerticalAnchor var2) {
      return new VerticalGradientConditionSource(ResourceLocation.parse(var0), var1, var2);
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

   public static RuleSource ifTrue(ConditionSource var0, RuleSource var1) {
      return new TestRuleSource(var0, var1);
   }

   public static RuleSource sequence(RuleSource... var0) {
      if (var0.length == 0) {
         throw new IllegalArgumentException("Need at least 1 rule for a sequence");
      } else {
         return new SequenceRuleSource(Arrays.asList(var0));
      }
   }

   public static RuleSource state(BlockState var0) {
      return new BlockRuleSource(var0);
   }

   public static RuleSource bandlands() {
      return SurfaceRules.Bandlands.INSTANCE;
   }

   static <A> MapCodec<? extends A> register(Registry<MapCodec<? extends A>> var0, String var1, KeyDispatchDataCodec<? extends A> var2) {
      return (MapCodec)Registry.register(var0, (String)var1, var2.codec());
   }

   static {
      ON_FLOOR = stoneDepthCheck(0, false, CaveSurface.FLOOR);
      UNDER_FLOOR = stoneDepthCheck(0, true, CaveSurface.FLOOR);
      DEEP_UNDER_FLOOR = stoneDepthCheck(0, true, 6, CaveSurface.FLOOR);
      VERY_DEEP_UNDER_FLOOR = stoneDepthCheck(0, true, 30, CaveSurface.FLOOR);
      ON_CEILING = stoneDepthCheck(0, false, CaveSurface.CEILING);
      UNDER_CEILING = stoneDepthCheck(0, true, CaveSurface.CEILING);
   }

   private static record StoneDepthCheck(int offset, boolean addSurfaceDepth, int secondaryDepthRange, CaveSurface surfaceType) implements ConditionSource {
      final int offset;
      final boolean addSurfaceDepth;
      final int secondaryDepthRange;
      static final KeyDispatchDataCodec<StoneDepthCheck> CODEC = KeyDispatchDataCodec.of(RecordCodecBuilder.mapCodec((var0) -> {
         return var0.group(Codec.INT.fieldOf("offset").forGetter(StoneDepthCheck::offset), Codec.BOOL.fieldOf("add_surface_depth").forGetter(StoneDepthCheck::addSurfaceDepth), Codec.INT.fieldOf("secondary_depth_range").forGetter(StoneDepthCheck::secondaryDepthRange), CaveSurface.CODEC.fieldOf("surface_type").forGetter(StoneDepthCheck::surfaceType)).apply(var0, StoneDepthCheck::new);
      }));

      StoneDepthCheck(int var1, boolean var2, int var3, CaveSurface var4) {
         super();
         this.offset = var1;
         this.addSurfaceDepth = var2;
         this.secondaryDepthRange = var3;
         this.surfaceType = var4;
      }

      public KeyDispatchDataCodec<? extends ConditionSource> codec() {
         return CODEC;
      }

      public Condition apply(final Context var1) {
         final boolean var2 = this.surfaceType == CaveSurface.CEILING;

         class 1StoneDepthCondition extends LazyYCondition {
            _StoneDepthCondition/* $FF was: 1StoneDepthCondition*/() {
               super(var1);
            }

            protected boolean compute() {
               int var1x = var2 ? this.context.stoneDepthBelow : this.context.stoneDepthAbove;
               int var2x = StoneDepthCheck.this.addSurfaceDepth ? this.context.surfaceDepth : 0;
               int var3 = StoneDepthCheck.this.secondaryDepthRange == 0 ? 0 : (int)Mth.map(this.context.getSurfaceSecondary(), -1.0, 1.0, 0.0, (double)StoneDepthCheck.this.secondaryDepthRange);
               return var1x <= 1 + StoneDepthCheck.this.offset + var2x + var3;
            }
         }

         return new 1StoneDepthCondition();
      }

      public int offset() {
         return this.offset;
      }

      public boolean addSurfaceDepth() {
         return this.addSurfaceDepth;
      }

      public int secondaryDepthRange() {
         return this.secondaryDepthRange;
      }

      public CaveSurface surfaceType() {
         return this.surfaceType;
      }

      // $FF: synthetic method
      public Object apply(final Object var1) {
         return this.apply((Context)var1);
      }
   }

   static record NotConditionSource(ConditionSource target) implements ConditionSource {
      static final KeyDispatchDataCodec<NotConditionSource> CODEC;

      NotConditionSource(ConditionSource var1) {
         super();
         this.target = var1;
      }

      public KeyDispatchDataCodec<? extends ConditionSource> codec() {
         return CODEC;
      }

      public Condition apply(Context var1) {
         return new NotCondition((Condition)this.target.apply(var1));
      }

      public ConditionSource target() {
         return this.target;
      }

      // $FF: synthetic method
      public Object apply(final Object var1) {
         return this.apply((Context)var1);
      }

      static {
         CODEC = KeyDispatchDataCodec.of(SurfaceRules.ConditionSource.CODEC.xmap(NotConditionSource::new, NotConditionSource::target).fieldOf("invert"));
      }
   }

   public interface ConditionSource extends Function<Context, Condition> {
      Codec<ConditionSource> CODEC = BuiltInRegistries.MATERIAL_CONDITION.byNameCodec().dispatch((var0) -> {
         return var0.codec().codec();
      }, Function.identity());

      static MapCodec<? extends ConditionSource> bootstrap(Registry<MapCodec<? extends ConditionSource>> var0) {
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

      KeyDispatchDataCodec<? extends ConditionSource> codec();
   }

   private static record YConditionSource(VerticalAnchor anchor, int surfaceDepthMultiplier, boolean addStoneDepth) implements ConditionSource {
      final VerticalAnchor anchor;
      final int surfaceDepthMultiplier;
      final boolean addStoneDepth;
      static final KeyDispatchDataCodec<YConditionSource> CODEC = KeyDispatchDataCodec.of(RecordCodecBuilder.mapCodec((var0) -> {
         return var0.group(VerticalAnchor.CODEC.fieldOf("anchor").forGetter(YConditionSource::anchor), Codec.intRange(-20, 20).fieldOf("surface_depth_multiplier").forGetter(YConditionSource::surfaceDepthMultiplier), Codec.BOOL.fieldOf("add_stone_depth").forGetter(YConditionSource::addStoneDepth)).apply(var0, YConditionSource::new);
      }));

      YConditionSource(VerticalAnchor var1, int var2, boolean var3) {
         super();
         this.anchor = var1;
         this.surfaceDepthMultiplier = var2;
         this.addStoneDepth = var3;
      }

      public KeyDispatchDataCodec<? extends ConditionSource> codec() {
         return CODEC;
      }

      public Condition apply(final Context var1) {
         class 1YCondition extends LazyYCondition {
            _YCondition/* $FF was: 1YCondition*/() {
               super(var1);
            }

            protected boolean compute() {
               return this.context.blockY + (YConditionSource.this.addStoneDepth ? this.context.stoneDepthAbove : 0) >= YConditionSource.this.anchor.resolveY(this.context.context) + this.context.surfaceDepth * YConditionSource.this.surfaceDepthMultiplier;
            }
         }

         return new 1YCondition();
      }

      public VerticalAnchor anchor() {
         return this.anchor;
      }

      public int surfaceDepthMultiplier() {
         return this.surfaceDepthMultiplier;
      }

      public boolean addStoneDepth() {
         return this.addStoneDepth;
      }

      // $FF: synthetic method
      public Object apply(final Object var1) {
         return this.apply((Context)var1);
      }
   }

   private static record WaterConditionSource(int offset, int surfaceDepthMultiplier, boolean addStoneDepth) implements ConditionSource {
      final int offset;
      final int surfaceDepthMultiplier;
      final boolean addStoneDepth;
      static final KeyDispatchDataCodec<WaterConditionSource> CODEC = KeyDispatchDataCodec.of(RecordCodecBuilder.mapCodec((var0) -> {
         return var0.group(Codec.INT.fieldOf("offset").forGetter(WaterConditionSource::offset), Codec.intRange(-20, 20).fieldOf("surface_depth_multiplier").forGetter(WaterConditionSource::surfaceDepthMultiplier), Codec.BOOL.fieldOf("add_stone_depth").forGetter(WaterConditionSource::addStoneDepth)).apply(var0, WaterConditionSource::new);
      }));

      WaterConditionSource(int var1, int var2, boolean var3) {
         super();
         this.offset = var1;
         this.surfaceDepthMultiplier = var2;
         this.addStoneDepth = var3;
      }

      public KeyDispatchDataCodec<? extends ConditionSource> codec() {
         return CODEC;
      }

      public Condition apply(final Context var1) {
         class 1WaterCondition extends LazyYCondition {
            _WaterCondition/* $FF was: 1WaterCondition*/() {
               super(var1);
            }

            protected boolean compute() {
               return this.context.waterHeight == -2147483648 || this.context.blockY + (WaterConditionSource.this.addStoneDepth ? this.context.stoneDepthAbove : 0) >= this.context.waterHeight + WaterConditionSource.this.offset + this.context.surfaceDepth * WaterConditionSource.this.surfaceDepthMultiplier;
            }
         }

         return new 1WaterCondition();
      }

      public int offset() {
         return this.offset;
      }

      public int surfaceDepthMultiplier() {
         return this.surfaceDepthMultiplier;
      }

      public boolean addStoneDepth() {
         return this.addStoneDepth;
      }

      // $FF: synthetic method
      public Object apply(final Object var1) {
         return this.apply((Context)var1);
      }
   }

   static final class BiomeConditionSource implements ConditionSource {
      static final KeyDispatchDataCodec<BiomeConditionSource> CODEC;
      private final List<ResourceKey<Biome>> biomes;
      final Predicate<ResourceKey<Biome>> biomeNameTest;

      BiomeConditionSource(List<ResourceKey<Biome>> var1) {
         super();
         this.biomes = var1;
         Set var10001 = Set.copyOf(var1);
         Objects.requireNonNull(var10001);
         this.biomeNameTest = var10001::contains;
      }

      public KeyDispatchDataCodec<? extends ConditionSource> codec() {
         return CODEC;
      }

      public Condition apply(final Context var1) {
         class 1BiomeCondition extends LazyYCondition {
            _BiomeCondition/* $FF was: 1BiomeCondition*/() {
               super(var1);
            }

            protected boolean compute() {
               return ((Holder)this.context.biome.get()).is(BiomeConditionSource.this.biomeNameTest);
            }
         }

         return new 1BiomeCondition();
      }

      public boolean equals(Object var1) {
         if (this == var1) {
            return true;
         } else if (var1 instanceof BiomeConditionSource) {
            BiomeConditionSource var2 = (BiomeConditionSource)var1;
            return this.biomes.equals(var2.biomes);
         } else {
            return false;
         }
      }

      public int hashCode() {
         return this.biomes.hashCode();
      }

      public String toString() {
         return "BiomeConditionSource[biomes=" + String.valueOf(this.biomes) + "]";
      }

      // $FF: synthetic method
      public Object apply(final Object var1) {
         return this.apply((Context)var1);
      }

      static {
         CODEC = KeyDispatchDataCodec.of(ResourceKey.codec(Registries.BIOME).listOf().fieldOf("biome_is").xmap(SurfaceRules::isBiome, (var0) -> {
            return var0.biomes;
         }));
      }
   }

   private static record NoiseThresholdConditionSource(ResourceKey<NormalNoise.NoiseParameters> noise, double minThreshold, double maxThreshold) implements ConditionSource {
      final double minThreshold;
      final double maxThreshold;
      static final KeyDispatchDataCodec<NoiseThresholdConditionSource> CODEC = KeyDispatchDataCodec.of(RecordCodecBuilder.mapCodec((var0) -> {
         return var0.group(ResourceKey.codec(Registries.NOISE).fieldOf("noise").forGetter(NoiseThresholdConditionSource::noise), Codec.DOUBLE.fieldOf("min_threshold").forGetter(NoiseThresholdConditionSource::minThreshold), Codec.DOUBLE.fieldOf("max_threshold").forGetter(NoiseThresholdConditionSource::maxThreshold)).apply(var0, NoiseThresholdConditionSource::new);
      }));

      NoiseThresholdConditionSource(ResourceKey<NormalNoise.NoiseParameters> var1, double var2, double var4) {
         super();
         this.noise = var1;
         this.minThreshold = var2;
         this.maxThreshold = var4;
      }

      public KeyDispatchDataCodec<? extends ConditionSource> codec() {
         return CODEC;
      }

      public Condition apply(final Context var1) {
         final NormalNoise var2 = var1.randomState.getOrCreateNoise(this.noise);

         class 1NoiseThresholdCondition extends LazyXZCondition {
            _NoiseThresholdCondition/* $FF was: 1NoiseThresholdCondition*/() {
               super(var1);
            }

            protected boolean compute() {
               double var1x = var2.getValue((double)this.context.blockX, 0.0, (double)this.context.blockZ);
               return var1x >= NoiseThresholdConditionSource.this.minThreshold && var1x <= NoiseThresholdConditionSource.this.maxThreshold;
            }
         }

         return new 1NoiseThresholdCondition();
      }

      public ResourceKey<NormalNoise.NoiseParameters> noise() {
         return this.noise;
      }

      public double minThreshold() {
         return this.minThreshold;
      }

      public double maxThreshold() {
         return this.maxThreshold;
      }

      // $FF: synthetic method
      public Object apply(final Object var1) {
         return this.apply((Context)var1);
      }
   }

   static record VerticalGradientConditionSource(ResourceLocation randomName, VerticalAnchor trueAtAndBelow, VerticalAnchor falseAtAndAbove) implements ConditionSource {
      static final KeyDispatchDataCodec<VerticalGradientConditionSource> CODEC = KeyDispatchDataCodec.of(RecordCodecBuilder.mapCodec((var0) -> {
         return var0.group(ResourceLocation.CODEC.fieldOf("random_name").forGetter(VerticalGradientConditionSource::randomName), VerticalAnchor.CODEC.fieldOf("true_at_and_below").forGetter(VerticalGradientConditionSource::trueAtAndBelow), VerticalAnchor.CODEC.fieldOf("false_at_and_above").forGetter(VerticalGradientConditionSource::falseAtAndAbove)).apply(var0, VerticalGradientConditionSource::new);
      }));

      VerticalGradientConditionSource(ResourceLocation var1, VerticalAnchor var2, VerticalAnchor var3) {
         super();
         this.randomName = var1;
         this.trueAtAndBelow = var2;
         this.falseAtAndAbove = var3;
      }

      public KeyDispatchDataCodec<? extends ConditionSource> codec() {
         return CODEC;
      }

      public Condition apply(final Context var1) {
         final int var2 = this.trueAtAndBelow().resolveY(var1.context);
         final int var3 = this.falseAtAndAbove().resolveY(var1.context);
         final PositionalRandomFactory var4 = var1.randomState.getOrCreateRandomFactory(this.randomName());

         class 1VerticalGradientCondition extends LazyYCondition {
            _VerticalGradientCondition/* $FF was: 1VerticalGradientCondition*/(final VerticalGradientConditionSource var1x) {
               super(var1);
            }

            protected boolean compute() {
               int var1x = this.context.blockY;
               if (var1x <= var2) {
                  return true;
               } else if (var1x >= var3) {
                  return false;
               } else {
                  double var2x = Mth.map((double)var1x, (double)var2, (double)var3, 1.0, 0.0);
                  RandomSource var4x = var4.at(this.context.blockX, var1x, this.context.blockZ);
                  return (double)var4x.nextFloat() < var2x;
               }
            }
         }

         return new 1VerticalGradientCondition(this);
      }

      public ResourceLocation randomName() {
         return this.randomName;
      }

      public VerticalAnchor trueAtAndBelow() {
         return this.trueAtAndBelow;
      }

      public VerticalAnchor falseAtAndAbove() {
         return this.falseAtAndAbove;
      }

      // $FF: synthetic method
      public Object apply(final Object var1) {
         return this.apply((Context)var1);
      }
   }

   static enum Steep implements ConditionSource {
      INSTANCE;

      static final KeyDispatchDataCodec<Steep> CODEC = KeyDispatchDataCodec.of(MapCodec.unit(INSTANCE));

      private Steep() {
      }

      public KeyDispatchDataCodec<? extends ConditionSource> codec() {
         return CODEC;
      }

      public Condition apply(Context var1) {
         return var1.steep;
      }

      // $FF: synthetic method
      public Object apply(final Object var1) {
         return this.apply((Context)var1);
      }

      // $FF: synthetic method
      private static Steep[] $values() {
         return new Steep[]{INSTANCE};
      }
   }

   static enum Hole implements ConditionSource {
      INSTANCE;

      static final KeyDispatchDataCodec<Hole> CODEC = KeyDispatchDataCodec.of(MapCodec.unit(INSTANCE));

      private Hole() {
      }

      public KeyDispatchDataCodec<? extends ConditionSource> codec() {
         return CODEC;
      }

      public Condition apply(Context var1) {
         return var1.hole;
      }

      // $FF: synthetic method
      public Object apply(final Object var1) {
         return this.apply((Context)var1);
      }

      // $FF: synthetic method
      private static Hole[] $values() {
         return new Hole[]{INSTANCE};
      }
   }

   static enum AbovePreliminarySurface implements ConditionSource {
      INSTANCE;

      static final KeyDispatchDataCodec<AbovePreliminarySurface> CODEC = KeyDispatchDataCodec.of(MapCodec.unit(INSTANCE));

      private AbovePreliminarySurface() {
      }

      public KeyDispatchDataCodec<? extends ConditionSource> codec() {
         return CODEC;
      }

      public Condition apply(Context var1) {
         return var1.abovePreliminarySurface;
      }

      // $FF: synthetic method
      public Object apply(final Object var1) {
         return this.apply((Context)var1);
      }

      // $FF: synthetic method
      private static AbovePreliminarySurface[] $values() {
         return new AbovePreliminarySurface[]{INSTANCE};
      }
   }

   static enum Temperature implements ConditionSource {
      INSTANCE;

      static final KeyDispatchDataCodec<Temperature> CODEC = KeyDispatchDataCodec.of(MapCodec.unit(INSTANCE));

      private Temperature() {
      }

      public KeyDispatchDataCodec<? extends ConditionSource> codec() {
         return CODEC;
      }

      public Condition apply(Context var1) {
         return var1.temperature;
      }

      // $FF: synthetic method
      public Object apply(final Object var1) {
         return this.apply((Context)var1);
      }

      // $FF: synthetic method
      private static Temperature[] $values() {
         return new Temperature[]{INSTANCE};
      }
   }

   private static record TestRuleSource(ConditionSource ifTrue, RuleSource thenRun) implements RuleSource {
      static final KeyDispatchDataCodec<TestRuleSource> CODEC = KeyDispatchDataCodec.of(RecordCodecBuilder.mapCodec((var0) -> {
         return var0.group(SurfaceRules.ConditionSource.CODEC.fieldOf("if_true").forGetter(TestRuleSource::ifTrue), SurfaceRules.RuleSource.CODEC.fieldOf("then_run").forGetter(TestRuleSource::thenRun)).apply(var0, TestRuleSource::new);
      }));

      TestRuleSource(ConditionSource var1, RuleSource var2) {
         super();
         this.ifTrue = var1;
         this.thenRun = var2;
      }

      public KeyDispatchDataCodec<? extends RuleSource> codec() {
         return CODEC;
      }

      public SurfaceRule apply(Context var1) {
         return new TestRule((Condition)this.ifTrue.apply(var1), (SurfaceRule)this.thenRun.apply(var1));
      }

      public ConditionSource ifTrue() {
         return this.ifTrue;
      }

      public RuleSource thenRun() {
         return this.thenRun;
      }

      // $FF: synthetic method
      public Object apply(final Object var1) {
         return this.apply((Context)var1);
      }
   }

   public interface RuleSource extends Function<Context, SurfaceRule> {
      Codec<RuleSource> CODEC = BuiltInRegistries.MATERIAL_RULE.byNameCodec().dispatch((var0) -> {
         return var0.codec().codec();
      }, Function.identity());

      static MapCodec<? extends RuleSource> bootstrap(Registry<MapCodec<? extends RuleSource>> var0) {
         SurfaceRules.register(var0, "bandlands", SurfaceRules.Bandlands.CODEC);
         SurfaceRules.register(var0, "block", SurfaceRules.BlockRuleSource.CODEC);
         SurfaceRules.register(var0, "sequence", SurfaceRules.SequenceRuleSource.CODEC);
         return SurfaceRules.register(var0, "condition", SurfaceRules.TestRuleSource.CODEC);
      }

      KeyDispatchDataCodec<? extends RuleSource> codec();
   }

   private static record SequenceRuleSource(List<RuleSource> sequence) implements RuleSource {
      static final KeyDispatchDataCodec<SequenceRuleSource> CODEC;

      SequenceRuleSource(List<RuleSource> var1) {
         super();
         this.sequence = var1;
      }

      public KeyDispatchDataCodec<? extends RuleSource> codec() {
         return CODEC;
      }

      public SurfaceRule apply(Context var1) {
         if (this.sequence.size() == 1) {
            return (SurfaceRule)((RuleSource)this.sequence.get(0)).apply(var1);
         } else {
            ImmutableList.Builder var2 = ImmutableList.builder();
            Iterator var3 = this.sequence.iterator();

            while(var3.hasNext()) {
               RuleSource var4 = (RuleSource)var3.next();
               var2.add((SurfaceRule)var4.apply(var1));
            }

            return new SequenceRule(var2.build());
         }
      }

      public List<RuleSource> sequence() {
         return this.sequence;
      }

      // $FF: synthetic method
      public Object apply(final Object var1) {
         return this.apply((Context)var1);
      }

      static {
         CODEC = KeyDispatchDataCodec.of(SurfaceRules.RuleSource.CODEC.listOf().xmap(SequenceRuleSource::new, SequenceRuleSource::sequence).fieldOf("sequence"));
      }
   }

   static record BlockRuleSource(BlockState resultState, StateRule rule) implements RuleSource {
      static final KeyDispatchDataCodec<BlockRuleSource> CODEC;

      BlockRuleSource(BlockState var1) {
         this(var1, new StateRule(var1));
      }

      private BlockRuleSource(BlockState var1, StateRule var2) {
         super();
         this.resultState = var1;
         this.rule = var2;
      }

      public KeyDispatchDataCodec<? extends RuleSource> codec() {
         return CODEC;
      }

      public SurfaceRule apply(Context var1) {
         return this.rule;
      }

      public BlockState resultState() {
         return this.resultState;
      }

      public StateRule rule() {
         return this.rule;
      }

      // $FF: synthetic method
      public Object apply(final Object var1) {
         return this.apply((Context)var1);
      }

      static {
         CODEC = KeyDispatchDataCodec.of(BlockState.CODEC.xmap(BlockRuleSource::new, BlockRuleSource::resultState).fieldOf("result_state"));
      }
   }

   private static enum Bandlands implements RuleSource {
      INSTANCE;

      static final KeyDispatchDataCodec<Bandlands> CODEC = KeyDispatchDataCodec.of(MapCodec.unit(INSTANCE));

      private Bandlands() {
      }

      public KeyDispatchDataCodec<? extends RuleSource> codec() {
         return CODEC;
      }

      public SurfaceRule apply(Context var1) {
         SurfaceSystem var10000 = var1.system;
         Objects.requireNonNull(var10000);
         return var10000::getBand;
      }

      // $FF: synthetic method
      public Object apply(final Object var1) {
         return this.apply((Context)var1);
      }

      // $FF: synthetic method
      private static Bandlands[] $values() {
         return new Bandlands[]{INSTANCE};
      }
   }

   private static record SequenceRule(List<SurfaceRule> rules) implements SurfaceRule {
      SequenceRule(List<SurfaceRule> var1) {
         super();
         this.rules = var1;
      }

      @Nullable
      public BlockState tryApply(int var1, int var2, int var3) {
         Iterator var4 = this.rules.iterator();

         BlockState var6;
         do {
            if (!var4.hasNext()) {
               return null;
            }

            SurfaceRule var5 = (SurfaceRule)var4.next();
            var6 = var5.tryApply(var1, var2, var3);
         } while(var6 == null);

         return var6;
      }

      public List<SurfaceRule> rules() {
         return this.rules;
      }
   }

   private static record TestRule(Condition condition, SurfaceRule followup) implements SurfaceRule {
      TestRule(Condition var1, SurfaceRule var2) {
         super();
         this.condition = var1;
         this.followup = var2;
      }

      @Nullable
      public BlockState tryApply(int var1, int var2, int var3) {
         return !this.condition.test() ? null : this.followup.tryApply(var1, var2, var3);
      }

      public Condition condition() {
         return this.condition;
      }

      public SurfaceRule followup() {
         return this.followup;
      }
   }

   static record StateRule(BlockState state) implements SurfaceRule {
      StateRule(BlockState var1) {
         super();
         this.state = var1;
      }

      public BlockState tryApply(int var1, int var2, int var3) {
         return this.state;
      }

      public BlockState state() {
         return this.state;
      }
   }

   protected interface SurfaceRule {
      @Nullable
      BlockState tryApply(int var1, int var2, int var3);
   }

   private static record NotCondition(Condition target) implements Condition {
      NotCondition(Condition var1) {
         super();
         this.target = var1;
      }

      public boolean test() {
         return !this.target.test();
      }

      public Condition target() {
         return this.target;
      }
   }

   private abstract static class LazyYCondition extends LazyCondition {
      protected LazyYCondition(Context var1) {
         super(var1);
      }

      protected long getContextLastUpdate() {
         return this.context.lastUpdateY;
      }
   }

   private abstract static class LazyXZCondition extends LazyCondition {
      protected LazyXZCondition(Context var1) {
         super(var1);
      }

      protected long getContextLastUpdate() {
         return this.context.lastUpdateXZ;
      }
   }

   private abstract static class LazyCondition implements Condition {
      protected final Context context;
      private long lastUpdate;
      @Nullable
      Boolean result;

      protected LazyCondition(Context var1) {
         super();
         this.context = var1;
         this.lastUpdate = this.getContextLastUpdate() - 1L;
      }

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

   private interface Condition {
      boolean test();
   }

   protected static final class Context {
      private static final int HOW_FAR_BELOW_PRELIMINARY_SURFACE_LEVEL_TO_BUILD_SURFACE = 8;
      private static final int SURFACE_CELL_BITS = 4;
      private static final int SURFACE_CELL_SIZE = 16;
      private static final int SURFACE_CELL_MASK = 15;
      final SurfaceSystem system;
      final Condition temperature = new TemperatureHelperCondition(this);
      final Condition steep = new SteepMaterialCondition(this);
      final Condition hole = new HoleCondition(this);
      final Condition abovePreliminarySurface = new AbovePreliminarySurfaceCondition();
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
      private long lastSurfaceDepth2Update;
      private double surfaceSecondary;
      private long lastMinSurfaceLevelUpdate;
      private int minSurfaceLevel;
      long lastUpdateY;
      final BlockPos.MutableBlockPos pos;
      Supplier<Holder<Biome>> biome;
      int blockY;
      int waterHeight;
      int stoneDepthBelow;
      int stoneDepthAbove;

      protected Context(SurfaceSystem var1, RandomState var2, ChunkAccess var3, NoiseChunk var4, Function<BlockPos, Holder<Biome>> var5, Registry<Biome> var6, WorldGenerationContext var7) {
         super();
         this.lastSurfaceDepth2Update = this.lastUpdateXZ - 1L;
         this.lastMinSurfaceLevelUpdate = this.lastUpdateXZ - 1L;
         this.lastUpdateY = -9223372036854775807L;
         this.pos = new BlockPos.MutableBlockPos();
         this.system = var1;
         this.randomState = var2;
         this.chunk = var3;
         this.noiseChunk = var4;
         this.biomeGetter = var5;
         this.context = var7;
      }

      protected void updateXZ(int var1, int var2) {
         ++this.lastUpdateXZ;
         ++this.lastUpdateY;
         this.blockX = var1;
         this.blockZ = var2;
         this.surfaceDepth = this.system.getSurfaceDepth(var1, var2);
      }

      protected void updateY(int var1, int var2, int var3, int var4, int var5, int var6) {
         ++this.lastUpdateY;
         this.biome = Suppliers.memoize(() -> {
            return (Holder)this.biomeGetter.apply(this.pos.set(var4, var5, var6));
         });
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

            int var5 = Mth.floor(Mth.lerp2((double)((float)(this.blockX & 15) / 16.0F), (double)((float)(this.blockZ & 15) / 16.0F), (double)this.preliminarySurfaceCache[0], (double)this.preliminarySurfaceCache[1], (double)this.preliminarySurfaceCache[2], (double)this.preliminarySurfaceCache[3]));
            this.minSurfaceLevel = var5 + this.surfaceDepth - 8;
         }

         return this.minSurfaceLevel;
      }

      private static class TemperatureHelperCondition extends LazyYCondition {
         TemperatureHelperCondition(Context var1) {
            super(var1);
         }

         protected boolean compute() {
            return ((Biome)((Holder)this.context.biome.get()).value()).coldEnoughToSnow(this.context.pos.set(this.context.blockX, this.context.blockY, this.context.blockZ));
         }
      }

      static class SteepMaterialCondition extends LazyXZCondition {
         SteepMaterialCondition(Context var1) {
            super(var1);
         }

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

      private static final class HoleCondition extends LazyXZCondition {
         HoleCondition(Context var1) {
            super(var1);
         }

         protected boolean compute() {
            return this.context.surfaceDepth <= 0;
         }
      }

      final class AbovePreliminarySurfaceCondition implements Condition {
         AbovePreliminarySurfaceCondition() {
            super();
         }

         public boolean test() {
            return Context.this.blockY >= Context.this.getMinSurfaceLevel();
         }
      }
   }
}
