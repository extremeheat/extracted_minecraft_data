package net.minecraft.world.level.levelgen.material;

import com.mojang.serialization.MapCodec;
import java.util.List;
import java.util.Objects;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.HolderSet;
import net.minecraft.core.Registry;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.VerticalAnchor;
import net.minecraft.world.level.levelgen.material.condition.AbovePreliminarySurfaceCondition;
import net.minecraft.world.level.levelgen.material.condition.BiomeCondition;
import net.minecraft.world.level.levelgen.material.condition.HoleCondition;
import net.minecraft.world.level.levelgen.material.condition.MaterialCondition;
import net.minecraft.world.level.levelgen.material.condition.NoiseThresholdCondition;
import net.minecraft.world.level.levelgen.material.condition.NotCondition;
import net.minecraft.world.level.levelgen.material.condition.SteepCondition;
import net.minecraft.world.level.levelgen.material.condition.StoneDepthCondition;
import net.minecraft.world.level.levelgen.material.condition.TemperatureCondition;
import net.minecraft.world.level.levelgen.material.condition.VerticalGradientCondition;
import net.minecraft.world.level.levelgen.material.condition.WaterCondition;
import net.minecraft.world.level.levelgen.material.condition.YCondition;
import net.minecraft.world.level.levelgen.material.rule.BandlandsRule;
import net.minecraft.world.level.levelgen.material.rule.BlockRule;
import net.minecraft.world.level.levelgen.material.rule.ConditionRule;
import net.minecraft.world.level.levelgen.material.rule.MaterialRule;
import net.minecraft.world.level.levelgen.material.rule.OreVeinRule;
import net.minecraft.world.level.levelgen.material.rule.SequenceRule;
import net.minecraft.world.level.levelgen.placement.CaveSurface;
import net.minecraft.world.level.levelgen.synth.NormalNoise;

public class MaterialRules {
   public MaterialRules() {
      super();
   }

   public static MaterialRule registerAndWrap(final BootstrapContext<MaterialRule> context, final ResourceKey<MaterialRule> key, final MaterialRule rule) {
      return new MaterialRule.HolderHolder(context.register(key, rule));
   }

   public static MaterialCondition registerAndWrap(final BootstrapContext<MaterialCondition> context, final ResourceKey<MaterialCondition> key, final MaterialCondition condition) {
      return new MaterialCondition.HolderHolder(context.register(key, condition));
   }

   public static MaterialRule getRule(final HolderGetter<MaterialRule> rules, final ResourceKey<MaterialRule> key) {
      return new MaterialRule.HolderHolder(rules.getOrThrow(key));
   }

   public static MaterialCondition getCondition(final HolderGetter<MaterialCondition> conditions, final ResourceKey<MaterialCondition> key) {
      return new MaterialCondition.HolderHolder(conditions.getOrThrow(key));
   }

   public static MaterialCondition stoneDepthCheck(final int offset, final boolean addSurfaceDepth1, final CaveSurface surfaceType) {
      return new StoneDepthCondition(offset, addSurfaceDepth1, 0, surfaceType);
   }

   public static MaterialCondition stoneDepthCheck(final int offset, final boolean addSurfaceDepth1, final int secondaryDepthRange, final CaveSurface surfaceType) {
      return new StoneDepthCondition(offset, addSurfaceDepth1, secondaryDepthRange, surfaceType);
   }

   public static MaterialCondition not(final MaterialCondition target) {
      return new NotCondition(target);
   }

   public static MaterialCondition yBlockCheck(final VerticalAnchor anchor, final int surfaceDepthMultiplier) {
      return new YCondition(anchor, surfaceDepthMultiplier, false);
   }

   public static MaterialCondition yStartCheck(final VerticalAnchor anchor, final int surfaceDepthMultiplier) {
      return new YCondition(anchor, surfaceDepthMultiplier, true);
   }

   public static MaterialCondition waterBlockCheck(final int offset, final int surfaceDepthMultiplier) {
      return new WaterCondition(offset, surfaceDepthMultiplier, false);
   }

   public static MaterialCondition waterStartCheck(final int offset, final int surfaceDepthMultiplier) {
      return new WaterCondition(offset, surfaceDepthMultiplier, true);
   }

   @SafeVarargs
   public static MaterialCondition isBiome(final HolderGetter<Biome> biomes, final ResourceKey<Biome>... target) {
      Objects.requireNonNull(biomes);
      return new BiomeCondition(HolderSet.direct(biomes::getOrThrow, target));
   }

   public static MaterialCondition noiseCondition2d(final ResourceKey<NormalNoise> noise, final double minRange) {
      return noiseCondition2d(noise, minRange, 1.7976931348623157E308);
   }

   public static MaterialCondition noiseCondition2d(final ResourceKey<NormalNoise> noise, final double minRange, final double maxRange) {
      return new NoiseThresholdCondition(noise, minRange, maxRange, false);
   }

   public static MaterialCondition noiseCondition3d(final ResourceKey<NormalNoise> noise, final double minRange) {
      return noiseCondition3d(noise, minRange, 1.7976931348623157E308);
   }

   public static MaterialCondition noiseCondition3d(final ResourceKey<NormalNoise> noise, final double minRange, final double maxRange) {
      return new NoiseThresholdCondition(noise, minRange, maxRange, true);
   }

   public static MaterialCondition verticalGradient(final String randomName, final VerticalAnchor trueAtAndBelow, final VerticalAnchor falseAtAndAbove) {
      return new VerticalGradientCondition(Identifier.parse(randomName), trueAtAndBelow, falseAtAndAbove);
   }

   public static MaterialCondition steep() {
      return SteepCondition.INSTANCE;
   }

   public static MaterialCondition hole() {
      return HoleCondition.INSTANCE;
   }

   public static MaterialCondition abovePreliminarySurface() {
      return AbovePreliminarySurfaceCondition.INSTANCE;
   }

   public static MaterialCondition temperature() {
      return TemperatureCondition.INSTANCE;
   }

   public static MaterialRule ifTrue(final MaterialCondition condition, final MaterialRule next) {
      return new ConditionRule(condition, next);
   }

   public static MaterialRule sequence(final MaterialRule... rules) {
      return sequence(List.of(rules));
   }

   public static MaterialRule sequence(final List<MaterialRule> rules) {
      if (rules.isEmpty()) {
         throw new IllegalArgumentException("Need at least 1 rule for a sequence");
      } else {
         return new SequenceRule(rules);
      }
   }

   public static MaterialRule state(final BlockState state) {
      return new BlockRule(state);
   }

   public static MaterialRule bandlands() {
      return BandlandsRule.INSTANCE;
   }

   public static MapCodec<? extends MaterialRule> bootstrapRules(final Registry<MapCodec<? extends MaterialRule>> registry) {
      MapCodec<? extends MaterialRule> block = register(registry, "block", BlockRule.CODEC);
      register(registry, "bandlands", BandlandsRule.CODEC);
      register(registry, "sequence", SequenceRule.CODEC);
      register(registry, "condition", ConditionRule.CODEC);
      register(registry, "ore_vein", OreVeinRule.CODEC);
      return block;
   }

   public static MapCodec<? extends MaterialCondition> bootstrapConditions(final Registry<MapCodec<? extends MaterialCondition>> registry) {
      register(registry, "biome", BiomeCondition.CODEC);
      register(registry, "noise_threshold", NoiseThresholdCondition.CODEC);
      register(registry, "vertical_gradient", VerticalGradientCondition.CODEC);
      register(registry, "y_above", YCondition.CODEC);
      register(registry, "water", WaterCondition.CODEC);
      register(registry, "temperature", TemperatureCondition.CODEC);
      register(registry, "steep", SteepCondition.CODEC);
      register(registry, "not", NotCondition.CODEC);
      register(registry, "hole", HoleCondition.CODEC);
      register(registry, "above_preliminary_surface", AbovePreliminarySurfaceCondition.CODEC);
      return register(registry, "stone_depth", StoneDepthCondition.CODEC);
   }

   private static <A> MapCodec<? extends A> register(final Registry<MapCodec<? extends A>> registry, final String name, final MapCodec<? extends A> codec) {
      return (MapCodec)Registry.register(registry, (String)name, codec);
   }

   @FunctionalInterface
   public interface DensityGetter {
      float get();
   }
}
