package net.minecraft.world.level.storage.loot;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.ImmutableSet.Builder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import java.lang.reflect.Type;
import java.util.Objects;
import java.util.Set;
import javax.annotation.Nullable;
import net.minecraft.util.GsonHelper;
import net.minecraft.util.Mth;
import net.minecraft.world.level.storage.loot.parameters.LootContextParam;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import net.minecraft.world.level.storage.loot.providers.number.NumberProvider;

public class IntRange {
   @Nullable
   final NumberProvider min;
   @Nullable
   final NumberProvider max;
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

   IntRange(@Nullable NumberProvider var1, @Nullable NumberProvider var2) {
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
      return new IntRange(var1, var1);
   }

   public static IntRange range(int var0, int var1) {
      return new IntRange(ConstantValue.exactly((float)var0), ConstantValue.exactly((float)var1));
   }

   public static IntRange lowerBound(int var0) {
      return new IntRange(ConstantValue.exactly((float)var0), null);
   }

   public static IntRange upperBound(int var0) {
      return new IntRange(null, ConstantValue.exactly((float)var0));
   }

   public int clamp(LootContext var1, int var2) {
      return this.limiter.apply(var1, var2);
   }

   public boolean test(LootContext var1, int var2) {
      return this.predicate.test(var1, var2);
   }

   @FunctionalInterface
   interface IntChecker {
      boolean test(LootContext var1, int var2);
   }

   @FunctionalInterface
   interface IntLimiter {
      int apply(LootContext var1, int var2);
   }

   public static class Serializer implements JsonDeserializer<IntRange>, JsonSerializer<IntRange> {
      public Serializer() {
         super();
      }

      public IntRange deserialize(JsonElement var1, Type var2, JsonDeserializationContext var3) {
         if (var1.isJsonPrimitive()) {
            return IntRange.exact(var1.getAsInt());
         } else {
            JsonObject var4 = GsonHelper.convertToJsonObject(var1, "value");
            NumberProvider var5 = var4.has("min") ? GsonHelper.getAsObject(var4, "min", var3, NumberProvider.class) : null;
            NumberProvider var6 = var4.has("max") ? GsonHelper.getAsObject(var4, "max", var3, NumberProvider.class) : null;
            return new IntRange(var5, var6);
         }
      }

      public JsonElement serialize(IntRange var1, Type var2, JsonSerializationContext var3) {
         JsonObject var4 = new JsonObject();
         if (Objects.equals(var1.max, var1.min)) {
            return var3.serialize(var1.min);
         } else {
            if (var1.max != null) {
               var4.add("max", var3.serialize(var1.max));
            }

            if (var1.min != null) {
               var4.add("min", var3.serialize(var1.min));
            }

            return var4;
         }
      }
   }
}
