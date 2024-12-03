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
   Codec<LevelBasedValue> DISPATCH_CODEC = BuiltInRegistries.ENCHANTMENT_LEVEL_BASED_VALUE_TYPE.byNameCodec().dispatch(LevelBasedValue::codec, (var0) -> var0);
   Codec<LevelBasedValue> CODEC = Codec.either(LevelBasedValue.Constant.CODEC, DISPATCH_CODEC).xmap((var0) -> (LevelBasedValue)var0.map((var0x) -> var0x, (var0x) -> var0x), (var0) -> {
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
      Registry.register(var0, (String)"linear", LevelBasedValue.Linear.CODEC);
      return (MapCodec)Registry.register(var0, (String)"lookup", LevelBasedValue.Lookup.CODEC);
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

   static Lookup lookup(List<Float> var0, LevelBasedValue var1) {
      return new Lookup(var0, var1);
   }

   float calculate(int var1);

   MapCodec<? extends LevelBasedValue> codec();

   public static record Constant(float value) implements LevelBasedValue {
      public static final Codec<Constant> CODEC;
      public static final MapCodec<Constant> TYPED_CODEC;

      public Constant(float var1) {
         super();
         this.value = var1;
      }

      public float calculate(int var1) {
         return this.value;
      }

      public MapCodec<Constant> codec() {
         return TYPED_CODEC;
      }

      static {
         CODEC = Codec.FLOAT.xmap(Constant::new, Constant::value);
         TYPED_CODEC = RecordCodecBuilder.mapCodec((var0) -> var0.group(Codec.FLOAT.fieldOf("value").forGetter(Constant::value)).apply(var0, Constant::new));
      }
   }

   public static record Lookup(List<Float> values, LevelBasedValue fallback) implements LevelBasedValue {
      public static final MapCodec<Lookup> CODEC = RecordCodecBuilder.mapCodec((var0) -> var0.group(Codec.FLOAT.listOf().fieldOf("values").forGetter(Lookup::values), LevelBasedValue.CODEC.fieldOf("fallback").forGetter(Lookup::fallback)).apply(var0, Lookup::new));

      public Lookup(List<Float> var1, LevelBasedValue var2) {
         super();
         this.values = var1;
         this.fallback = var2;
      }

      public float calculate(int var1) {
         return var1 <= this.values.size() ? (Float)this.values.get(var1 - 1) : this.fallback.calculate(var1);
      }

      public MapCodec<Lookup> codec() {
         return CODEC;
      }
   }

   public static record Linear(float base, float perLevelAboveFirst) implements LevelBasedValue {
      public static final MapCodec<Linear> CODEC = RecordCodecBuilder.mapCodec((var0) -> var0.group(Codec.FLOAT.fieldOf("base").forGetter(Linear::base), Codec.FLOAT.fieldOf("per_level_above_first").forGetter(Linear::perLevelAboveFirst)).apply(var0, Linear::new));

      public Linear(float var1, float var2) {
         super();
         this.base = var1;
         this.perLevelAboveFirst = var2;
      }

      public float calculate(int var1) {
         return this.base + this.perLevelAboveFirst * (float)(var1 - 1);
      }

      public MapCodec<Linear> codec() {
         return CODEC;
      }
   }

   public static record Clamped(LevelBasedValue value, float min, float max) implements LevelBasedValue {
      public static final MapCodec<Clamped> CODEC = RecordCodecBuilder.mapCodec((var0) -> var0.group(LevelBasedValue.CODEC.fieldOf("value").forGetter(Clamped::value), Codec.FLOAT.fieldOf("min").forGetter(Clamped::min), Codec.FLOAT.fieldOf("max").forGetter(Clamped::max)).apply(var0, Clamped::new)).validate((var0) -> var0.max <= var0.min ? DataResult.error(() -> "Max must be larger than min, min: " + var0.min + ", max: " + var0.max) : DataResult.success(var0));

      public Clamped(LevelBasedValue var1, float var2, float var3) {
         super();
         this.value = var1;
         this.min = var2;
         this.max = var3;
      }

      public float calculate(int var1) {
         return Mth.clamp(this.value.calculate(var1), this.min, this.max);
      }

      public MapCodec<Clamped> codec() {
         return CODEC;
      }
   }

   public static record Fraction(LevelBasedValue numerator, LevelBasedValue denominator) implements LevelBasedValue {
      public static final MapCodec<Fraction> CODEC = RecordCodecBuilder.mapCodec((var0) -> var0.group(LevelBasedValue.CODEC.fieldOf("numerator").forGetter(Fraction::numerator), LevelBasedValue.CODEC.fieldOf("denominator").forGetter(Fraction::denominator)).apply(var0, Fraction::new));

      public Fraction(LevelBasedValue var1, LevelBasedValue var2) {
         super();
         this.numerator = var1;
         this.denominator = var2;
      }

      public float calculate(int var1) {
         float var2 = this.denominator.calculate(var1);
         return var2 == 0.0F ? 0.0F : this.numerator.calculate(var1) / var2;
      }

      public MapCodec<Fraction> codec() {
         return CODEC;
      }
   }

   public static record LevelsSquared(float added) implements LevelBasedValue {
      public static final MapCodec<LevelsSquared> CODEC = RecordCodecBuilder.mapCodec((var0) -> var0.group(Codec.FLOAT.fieldOf("added").forGetter(LevelsSquared::added)).apply(var0, LevelsSquared::new));

      public LevelsSquared(float var1) {
         super();
         this.added = var1;
      }

      public float calculate(int var1) {
         return (float)Mth.square(var1) + this.added;
      }

      public MapCodec<LevelsSquared> codec() {
         return CODEC;
      }
   }
}
