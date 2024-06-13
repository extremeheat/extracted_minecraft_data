package net.minecraft.world.item.enchantment;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.util.Mth;

public interface LevelBasedValue {
   Codec<LevelBasedValue> DISPATCH_CODEC = BuiltInRegistries.ENCHANTMENT_LEVEL_BASED_VALUE_TYPE.byNameCodec().dispatch(LevelBasedValue::codec, var0 -> var0);
   Codec<LevelBasedValue> CODEC = Codec.either(LevelBasedValue.Constant.CODEC, DISPATCH_CODEC)
      .xmap(
         var0 -> (LevelBasedValue)var0.map(var0x -> var0x, var0x -> var0x),
         var0 -> var0 instanceof LevelBasedValue.Constant var1 ? Either.left(var1) : Either.right(var0)
      );

   static MapCodec<? extends LevelBasedValue> bootstrap(Registry<MapCodec<? extends LevelBasedValue>> var0) {
      Registry.register(var0, "clamped", LevelBasedValue.Clamped.CODEC);
      Registry.register(var0, "fraction", LevelBasedValue.Fraction.CODEC);
      Registry.register(var0, "levels_squared", LevelBasedValue.LevelsSquared.CODEC);
      return Registry.register(var0, "linear", LevelBasedValue.Linear.CODEC);
   }

   static LevelBasedValue.Constant constant(float var0) {
      return new LevelBasedValue.Constant(var0);
   }

   static LevelBasedValue.Linear perLevel(float var0, float var1) {
      return new LevelBasedValue.Linear(var0, var1);
   }

   static LevelBasedValue.Linear perLevel(float var0) {
      return perLevel(var0, var0);
   }

   float calculate(int var1);

   MapCodec<? extends LevelBasedValue> codec();

   public static record Clamped(LevelBasedValue value, float min, float max) implements LevelBasedValue {
      public static final MapCodec<LevelBasedValue.Clamped> CODEC = RecordCodecBuilder.mapCodec(
            var0 -> var0.group(
                     LevelBasedValue.CODEC.fieldOf("value").forGetter(LevelBasedValue.Clamped::value),
                     Codec.FLOAT.fieldOf("min").forGetter(LevelBasedValue.Clamped::min),
                     Codec.FLOAT.fieldOf("max").forGetter(LevelBasedValue.Clamped::max)
                  )
                  .apply(var0, LevelBasedValue.Clamped::new)
         )
         .validate(
            var0 -> var0.max <= var0.min
                  ? DataResult.error(() -> "Max must be larger than min, min: " + var0.min + ", max: " + var0.max)
                  : DataResult.success(var0)
         );

      public Clamped(LevelBasedValue value, float min, float max) {
         super();
         this.value = value;
         this.min = min;
         this.max = max;
      }

      @Override
      public float calculate(int var1) {
         return Mth.clamp(this.value.calculate(var1), this.min, this.max);
      }

      @Override
      public MapCodec<LevelBasedValue.Clamped> codec() {
         return CODEC;
      }
   }

   public static record Constant(float value) implements LevelBasedValue {
      public static final Codec<LevelBasedValue.Constant> CODEC = Codec.FLOAT.xmap(LevelBasedValue.Constant::new, LevelBasedValue.Constant::value);
      public static final MapCodec<LevelBasedValue.Constant> TYPED_CODEC = RecordCodecBuilder.mapCodec(
         var0 -> var0.group(Codec.FLOAT.fieldOf("value").forGetter(LevelBasedValue.Constant::value)).apply(var0, LevelBasedValue.Constant::new)
      );

      public Constant(float value) {
         super();
         this.value = value;
      }

      @Override
      public float calculate(int var1) {
         return this.value;
      }

      @Override
      public MapCodec<LevelBasedValue.Constant> codec() {
         return TYPED_CODEC;
      }
   }

   public static record Fraction(LevelBasedValue numerator, LevelBasedValue denominator) implements LevelBasedValue {
      public static final MapCodec<LevelBasedValue.Fraction> CODEC = RecordCodecBuilder.mapCodec(
         var0 -> var0.group(
                  LevelBasedValue.CODEC.fieldOf("numerator").forGetter(LevelBasedValue.Fraction::numerator),
                  LevelBasedValue.CODEC.fieldOf("denominator").forGetter(LevelBasedValue.Fraction::denominator)
               )
               .apply(var0, LevelBasedValue.Fraction::new)
      );

      public Fraction(LevelBasedValue numerator, LevelBasedValue denominator) {
         super();
         this.numerator = numerator;
         this.denominator = denominator;
      }

      @Override
      public float calculate(int var1) {
         float var2 = this.denominator.calculate(var1);
         return var2 == 0.0F ? 0.0F : this.numerator.calculate(var1) / var2;
      }

      @Override
      public MapCodec<LevelBasedValue.Fraction> codec() {
         return CODEC;
      }
   }

   public static record LevelsSquared(float added) implements LevelBasedValue {
      public static final MapCodec<LevelBasedValue.LevelsSquared> CODEC = RecordCodecBuilder.mapCodec(
         var0 -> var0.group(Codec.FLOAT.fieldOf("added").forGetter(LevelBasedValue.LevelsSquared::added)).apply(var0, LevelBasedValue.LevelsSquared::new)
      );

      public LevelsSquared(float added) {
         super();
         this.added = added;
      }

      @Override
      public float calculate(int var1) {
         return (float)Mth.square(var1) + this.added;
      }

      @Override
      public MapCodec<LevelBasedValue.LevelsSquared> codec() {
         return CODEC;
      }
   }

   public static record Linear(float base, float perLevelAboveFirst) implements LevelBasedValue {
      public static final MapCodec<LevelBasedValue.Linear> CODEC = RecordCodecBuilder.mapCodec(
         var0 -> var0.group(
                  Codec.FLOAT.fieldOf("base").forGetter(LevelBasedValue.Linear::base),
                  Codec.FLOAT.fieldOf("per_level_above_first").forGetter(LevelBasedValue.Linear::perLevelAboveFirst)
               )
               .apply(var0, LevelBasedValue.Linear::new)
      );

      public Linear(float base, float perLevelAboveFirst) {
         super();
         this.base = base;
         this.perLevelAboveFirst = perLevelAboveFirst;
      }

      @Override
      public float calculate(int var1) {
         return this.base + this.perLevelAboveFirst * (float)(var1 - 1);
      }

      @Override
      public MapCodec<LevelBasedValue.Linear> codec() {
         return CODEC;
      }
   }
}
