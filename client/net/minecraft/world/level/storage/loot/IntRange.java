package net.minecraft.world.level.storage.loot;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.ImmutableSet.Builder;
import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.mojang.serialization.codecs.RecordCodecBuilder.Instance;
import java.util.Objects;
import java.util.Optional;
import java.util.OptionalInt;
import java.util.Set;
import java.util.function.Function;
import javax.annotation.Nullable;
import net.minecraft.util.Mth;
import net.minecraft.world.level.storage.loot.parameters.LootContextParam;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import net.minecraft.world.level.storage.loot.providers.number.NumberProvider;
import net.minecraft.world.level.storage.loot.providers.number.NumberProviders;

public class IntRange {
   private static final Codec<IntRange> RECORD_CODEC = RecordCodecBuilder.create(
      var0 -> var0.group(
               NumberProviders.CODEC.optionalFieldOf("min").forGetter(var0x -> Optional.ofNullable(var0x.min)),
               NumberProviders.CODEC.optionalFieldOf("max").forGetter(var0x -> Optional.ofNullable(var0x.max))
            )
            .apply(var0, IntRange::new)
   );
   public static final Codec<IntRange> CODEC = Codec.either(Codec.INT, RECORD_CODEC)
      .xmap(var0 -> (IntRange)var0.map(IntRange::exact, Function.identity()), var0 -> {
         OptionalInt var1 = var0.unpackExact();
         return var1.isPresent() ? Either.left(var1.getAsInt()) : Either.right(var0);
      });
   @Nullable
   private final NumberProvider min;
   @Nullable
   private final NumberProvider max;
   private final IntRange.IntLimiter limiter;
   private final IntRange.IntChecker predicate;

   public Set<LootContextParam<?>> getReferencedContextParams() {
      Builder var1 = ImmutableSet.builder();
      if (this.min != null) {
         var1.addAll(this.min.getReferencedContextParams());
      }

      if (this.max != null) {
         var1.addAll(this.max.getReferencedContextParams());
      }

      return var1.build();
   }

   private IntRange(Optional<NumberProvider> var1, Optional<NumberProvider> var2) {
      this(var1.orElse(null), var2.orElse(null));
   }

   private IntRange(@Nullable NumberProvider var1, @Nullable NumberProvider var2) {
      super();
      this.min = var1;
      this.max = var2;
      if (var1 == null) {
         if (var2 == null) {
            this.limiter = (var0, var1x) -> var1x;
            this.predicate = (var0, var1x) -> true;
         } else {
            this.limiter = (var1x, var2x) -> Math.min(var2.getInt(var1x), var2x);
            this.predicate = (var1x, var2x) -> var2x <= var2.getInt(var1x);
         }
      } else if (var2 == null) {
         this.limiter = (var1x, var2x) -> Math.max(var1.getInt(var1x), var2x);
         this.predicate = (var1x, var2x) -> var2x >= var1.getInt(var1x);
      } else {
         this.limiter = (var2x, var3) -> Mth.clamp(var3, var1.getInt(var2x), var2.getInt(var2x));
         this.predicate = (var2x, var3) -> var3 >= var1.getInt(var2x) && var3 <= var2.getInt(var2x);
      }
   }

   public static IntRange exact(int var0) {
      ConstantValue var1 = ConstantValue.exactly((float)var0);
      return new IntRange(Optional.of(var1), Optional.of(var1));
   }

   public static IntRange range(int var0, int var1) {
      return new IntRange(Optional.of(ConstantValue.exactly((float)var0)), Optional.of(ConstantValue.exactly((float)var1)));
   }

   public static IntRange lowerBound(int var0) {
      return new IntRange(Optional.of(ConstantValue.exactly((float)var0)), Optional.empty());
   }

   public static IntRange upperBound(int var0) {
      return new IntRange(Optional.empty(), Optional.of(ConstantValue.exactly((float)var0)));
   }

   public int clamp(LootContext var1, int var2) {
      return this.limiter.apply(var1, var2);
   }

   public boolean test(LootContext var1, int var2) {
      return this.predicate.test(var1, var2);
   }

   // $VF: Could not properly define all variable types!
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   private OptionalInt unpackExact() {
      if (Objects.equals(this.min, this.max)) {
         NumberProvider var2 = this.min;
         if (var2 instanceof ConstantValue var1 && Math.floor((double)var1.value()) == (double)var1.value()) {
            return OptionalInt.of((int)var1.value());
         }
      }

      return OptionalInt.empty();
   }

   @FunctionalInterface
   interface IntChecker {
      boolean test(LootContext var1, int var2);
   }

   @FunctionalInterface
   interface IntLimiter {
      int apply(LootContext var1, int var2);
   }
}
