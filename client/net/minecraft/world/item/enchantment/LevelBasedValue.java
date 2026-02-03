package net.minecraft.world.item.enchantment;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.util.Mth;

public interface LevelBasedValue {
   Codec<LevelBasedValue> DISPATCH_CODEC = BuiltInRegistries.ENCHANTMENT_LEVEL_BASED_VALUE_TYPE.byNameCodec().dispatch(LevelBasedValue::codec, (c) -> c);
   Codec<LevelBasedValue> CODEC = Codec.either(LevelBasedValue.Constant.CODEC, DISPATCH_CODEC).xmap((either) -> (LevelBasedValue)either.map((l) -> l, (r) -> r), (levelBasedValue) -> {
      Either var10000;
      if (levelBasedValue instanceof Constant constant) {
         var10000 = Either.left(constant);
      } else {
         var10000 = Either.right(levelBasedValue);
      }

      return var10000;
   });

   static MapCodec<? extends LevelBasedValue> bootstrap(final Registry<MapCodec<? extends LevelBasedValue>> registry) {
      Registry.register(registry, (String)"clamped", LevelBasedValue.Clamped.CODEC);
      Registry.register(registry, (String)"fraction", LevelBasedValue.Fraction.CODEC);
      Registry.register(registry, (String)"levels_squared", LevelBasedValue.LevelsSquared.CODEC);
      Registry.register(registry, (String)"linear", LevelBasedValue.Linear.CODEC);
      Registry.register(registry, (String)"exponent", LevelBasedValue.Exponent.CODEC);
      return (MapCodec)Registry.register(registry, (String)"lookup", LevelBasedValue.Lookup.CODEC);
   }

   static Constant constant(final float value) {
      return new Constant(value);
   }

   static Linear perLevel(final float base, final float perLevelAboveFirst) {
      return new Linear(base, perLevelAboveFirst);
   }

   static Linear perLevel(final float perLevel) {
      return perLevel(perLevel, perLevel);
   }

   static Lookup lookup(final List<Float> values, final LevelBasedValue fallback) {
      return new Lookup(values, fallback);
   }

   float calculate(int level);

   MapCodec<? extends LevelBasedValue> codec();

   public static record Constant(float value) implements LevelBasedValue {
      public static final Codec<Constant> CODEC;
      public static final MapCodec<Constant> TYPED_CODEC;

      public Constant {
         super();
      }

      public float calculate(final int level) {
         return this.value;
      }

      public MapCodec<Constant> codec() {
         return TYPED_CODEC;
      }

      static {
         CODEC = Codec.FLOAT.xmap(Constant::new, Constant::value);
         TYPED_CODEC = RecordCodecBuilder.mapCodec((i) -> i.group(Codec.FLOAT.fieldOf("value").forGetter(Constant::value)).apply(i, Constant::new));
      }
   }

   public static record Lookup(List<Float> values, LevelBasedValue fallback) implements LevelBasedValue {
      public static final MapCodec<Lookup> CODEC = RecordCodecBuilder.mapCodec((i) -> i.group(Codec.FLOAT.listOf().fieldOf("values").forGetter(Lookup::values), LevelBasedValue.CODEC.fieldOf("fallback").forGetter(Lookup::fallback)).apply(i, Lookup::new));

      public Lookup {
         super();
      }

      public float calculate(final int level) {
         return level <= this.values.size() ? (Float)this.values.get(level - 1) : this.fallback.calculate(level);
      }

      public MapCodec<Lookup> codec() {
         return CODEC;
      }
   }

   public static record Linear(float base, float perLevelAboveFirst) implements LevelBasedValue {
      public static final MapCodec<Linear> CODEC = RecordCodecBuilder.mapCodec((i) -> i.group(Codec.FLOAT.fieldOf("base").forGetter(Linear::base), Codec.FLOAT.fieldOf("per_level_above_first").forGetter(Linear::perLevelAboveFirst)).apply(i, Linear::new));

      public Linear {
         super();
      }

      public float calculate(final int level) {
         return this.base + this.perLevelAboveFirst * (float)(level - 1);
      }

      public MapCodec<Linear> codec() {
         return CODEC;
      }
   }

   public static record Clamped(LevelBasedValue value, float min, float max) implements LevelBasedValue {
      public static final MapCodec<Clamped> CODEC = RecordCodecBuilder.mapCodec((i) -> i.group(LevelBasedValue.CODEC.fieldOf("value").forGetter(Clamped::value), Codec.FLOAT.fieldOf("min").forGetter(Clamped::min), Codec.FLOAT.fieldOf("max").forGetter(Clamped::max)).apply(i, Clamped::new)).validate((u) -> u.max <= u.min ? DataResult.error(() -> "Max must be larger than min, min: " + u.min + ", max: " + u.max) : DataResult.success(u));

      public Clamped {
         super();
      }

      public float calculate(final int level) {
         return Mth.clamp(this.value.calculate(level), this.min, this.max);
      }

      public MapCodec<Clamped> codec() {
         return CODEC;
      }
   }

   public static record Fraction(LevelBasedValue numerator, LevelBasedValue denominator) implements LevelBasedValue {
      public static final MapCodec<Fraction> CODEC = RecordCodecBuilder.mapCodec((i) -> i.group(LevelBasedValue.CODEC.fieldOf("numerator").forGetter(Fraction::numerator), LevelBasedValue.CODEC.fieldOf("denominator").forGetter(Fraction::denominator)).apply(i, Fraction::new));

      public Fraction {
         super();
      }

      public float calculate(final int level) {
         float denominator = this.denominator.calculate(level);
         return denominator == 0.0F ? 0.0F : this.numerator.calculate(level) / denominator;
      }

      public MapCodec<Fraction> codec() {
         return CODEC;
      }
   }

   public static record Exponent(LevelBasedValue base, LevelBasedValue power) implements LevelBasedValue {
      public static final MapCodec<Exponent> CODEC = RecordCodecBuilder.mapCodec((i) -> i.group(LevelBasedValue.CODEC.fieldOf("base").forGetter(Exponent::base), LevelBasedValue.CODEC.fieldOf("power").forGetter(Exponent::power)).apply(i, Exponent::new));

      public Exponent {
         super();
      }

      public float calculate(final int level) {
         return (float)Math.pow((double)this.base.calculate(level), (double)this.power.calculate(level));
      }

      public MapCodec<Exponent> codec() {
         return CODEC;
      }
   }

   public static record LevelsSquared(float added) implements LevelBasedValue {
      public static final MapCodec<LevelsSquared> CODEC = RecordCodecBuilder.mapCodec((i) -> i.group(Codec.FLOAT.fieldOf("added").forGetter(LevelsSquared::added)).apply(i, LevelsSquared::new));

      public LevelsSquared {
         super();
      }

      public float calculate(final int level) {
         return (float)Mth.square(level) + this.added;
      }

      public MapCodec<LevelsSquared> codec() {
         return CODEC;
      }
   }
}
