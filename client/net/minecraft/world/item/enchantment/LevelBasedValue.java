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
   Codec<LevelBasedValue> DISPATCH_CODEC = BuiltInRegistries.ENCHANTMENT_LEVEL_BASED_VALUE_TYPE.byNameCodec().dispatch(LevelBasedValue::codec, (var0) -> {
      return var0;
   });
   Codec<LevelBasedValue> CODEC = Codec.either(LevelBasedValue.Constant.CODEC, DISPATCH_CODEC).xmap((var0) -> {
      return (LevelBasedValue)var0.map((var0x) -> {
         return var0x;
      }, (var0x) -> {
         return var0x;
      });
   }, (var0) -> {
      Either var10000;
      if (var0 instanceof Constant var1) {
         var10000 = Either.left(var1);
      } else {
         var10000 = Either.right(var0);
      }

      return var10000;
   });

   static MapCodec<? extends LevelBasedValue> bootstrap(Registry<MapCodec<? extends LevelBasedValue>> var0) {
      Registry.register(var0, (String)"clamped", LevelBasedValue.Clamped.CODEC);
      Registry.register(var0, (String)"fraction", LevelBasedValue.Fraction.CODEC);
      Registry.register(var0, (String)"levels_squared", LevelBasedValue.LevelsSquared.CODEC);
      return (MapCodec)Registry.register(var0, (String)"linear", LevelBasedValue.Linear.CODEC);
   }

   static Constant constant(float var0) {
      return new Constant(var0);
   }

   static Linear perLevel(float var0, float var1) {
      return new Linear(var0, var1);
   }

   static Linear perLevel(float var0) {
      return perLevel(var0, var0);
   }

   float calculate(int var1);

   MapCodec<? extends LevelBasedValue> codec();

   public static record Clamped(LevelBasedValue value, float min, float max) implements LevelBasedValue {
      public static final MapCodec<Clamped> CODEC = RecordCodecBuilder.mapCodec((var0) -> {
         return var0.group(LevelBasedValue.CODEC.fieldOf("value").forGetter(Clamped::value), Codec.FLOAT.fieldOf("min").forGetter(Clamped::min), Codec.FLOAT.fieldOf("max").forGetter(Clamped::max)).apply(var0, Clamped::new);
      }).validate((var0) -> {
         return var0.max <= var0.min ? DataResult.error(() -> {
            return "Max must be larger than min, min: " + var0.min + ", max: " + var0.max;
         }) : DataResult.success(var0);
      });

      public Clamped(LevelBasedValue value, float min, float max) {
         super();
         this.value = value;
         this.min = min;
         this.max = max;
      }

      public float calculate(int var1) {
         return Mth.clamp(this.value.calculate(var1), this.min, this.max);
      }

      public MapCodec<Clamped> codec() {
         return CODEC;
      }

      public LevelBasedValue value() {
         return this.value;
      }

      public float min() {
         return this.min;
      }

      public float max() {
         return this.max;
      }
   }

   public static record Fraction(LevelBasedValue numerator, LevelBasedValue denominator) implements LevelBasedValue {
      public static final MapCodec<Fraction> CODEC = RecordCodecBuilder.mapCodec((var0) -> {
         return var0.group(LevelBasedValue.CODEC.fieldOf("numerator").forGetter(Fraction::numerator), LevelBasedValue.CODEC.fieldOf("denominator").forGetter(Fraction::denominator)).apply(var0, Fraction::new);
      });

      public Fraction(LevelBasedValue numerator, LevelBasedValue denominator) {
         super();
         this.numerator = numerator;
         this.denominator = denominator;
      }

      public float calculate(int var1) {
         return this.numerator.calculate(var1) / this.denominator.calculate(var1);
      }

      public MapCodec<Fraction> codec() {
         return CODEC;
      }

      public LevelBasedValue numerator() {
         return this.numerator;
      }

      public LevelBasedValue denominator() {
         return this.denominator;
      }
   }

   public static record LevelsSquared(float added) implements LevelBasedValue {
      public static final MapCodec<LevelsSquared> CODEC = RecordCodecBuilder.mapCodec((var0) -> {
         return var0.group(Codec.FLOAT.fieldOf("added").forGetter(LevelsSquared::added)).apply(var0, LevelsSquared::new);
      });

      public LevelsSquared(float added) {
         super();
         this.added = added;
      }

      public float calculate(int var1) {
         return (float)Mth.square(var1) + this.added;
      }

      public MapCodec<LevelsSquared> codec() {
         return CODEC;
      }

      public float added() {
         return this.added;
      }
   }

   public static record Linear(float base, float perLevelAboveFirst) implements LevelBasedValue {
      public static final MapCodec<Linear> CODEC = RecordCodecBuilder.mapCodec((var0) -> {
         return var0.group(Codec.FLOAT.fieldOf("base").forGetter(Linear::base), Codec.FLOAT.fieldOf("per_level_above_first").forGetter(Linear::perLevelAboveFirst)).apply(var0, Linear::new);
      });

      public Linear(float base, float perLevelAboveFirst) {
         super();
         this.base = base;
         this.perLevelAboveFirst = perLevelAboveFirst;
      }

      public float calculate(int var1) {
         return this.base + this.perLevelAboveFirst * (float)(var1 - 1);
      }

      public MapCodec<Linear> codec() {
         return CODEC;
      }

      public float base() {
         return this.base;
      }

      public float perLevelAboveFirst() {
         return this.perLevelAboveFirst;
      }
   }

   public static record Constant(float value) implements LevelBasedValue {
      public static final Codec<Constant> CODEC;
      public static final MapCodec<Constant> TYPED_CODEC;

      public Constant(float value) {
         super();
         this.value = value;
      }

      public float calculate(int var1) {
         return this.value;
      }

      public MapCodec<Constant> codec() {
         return TYPED_CODEC;
      }

      public float value() {
         return this.value;
      }

      static {
         CODEC = Codec.FLOAT.xmap(Constant::new, Constant::value);
         TYPED_CODEC = RecordCodecBuilder.mapCodec((var0) -> {
            return var0.group(Codec.FLOAT.fieldOf("value").forGetter(Constant::value)).apply(var0, Constant::new);
         });
      }
   }
}
