package net.minecraft.world.attribute;

import com.google.common.collect.ImmutableBiMap;
import com.mojang.serialization.Codec;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.util.Util;
import net.minecraft.world.attribute.modifier.AttributeModifier;

public record AttributeType<Value>(Codec<Value> valueCodec, Map<AttributeModifier.OperationId, AttributeModifier<Value, ?>> modifierLibrary, Codec<AttributeModifier<Value, ?>> modifierCodec, LerpFunction<Value> keyframeLerp, LerpFunction<Value> stateChangeLerp, LerpFunction<Value> spatialLerp, LerpFunction<Value> partialTickLerp) {
   public AttributeType(Codec<Value> var1, Map<AttributeModifier.OperationId, AttributeModifier<Value, ?>> var2, Codec<AttributeModifier<Value, ?>> var3, LerpFunction<Value> var4, LerpFunction<Value> var5, LerpFunction<Value> var6, LerpFunction<Value> var7) {
      super();
      this.valueCodec = var1;
      this.modifierLibrary = var2;
      this.modifierCodec = var3;
      this.keyframeLerp = var4;
      this.stateChangeLerp = var5;
      this.spatialLerp = var6;
      this.partialTickLerp = var7;
   }

   public static <Value> AttributeType<Value> ofInterpolated(Codec<Value> var0, Map<AttributeModifier.OperationId, AttributeModifier<Value, ?>> var1, LerpFunction<Value> var2) {
      return ofInterpolated(var0, var1, var2, var2);
   }

   public static <Value> AttributeType<Value> ofInterpolated(Codec<Value> var0, Map<AttributeModifier.OperationId, AttributeModifier<Value, ?>> var1, LerpFunction<Value> var2, LerpFunction<Value> var3) {
      return new AttributeType<Value>(var0, var1, createModifierCodec(var1), var2, var2, var2, var3);
   }

   public static <Value> AttributeType<Value> ofNotInterpolated(Codec<Value> var0, Map<AttributeModifier.OperationId, AttributeModifier<Value, ?>> var1) {
      return new AttributeType<Value>(var0, var1, createModifierCodec(var1), LerpFunction.ofStep(1.0F), LerpFunction.ofStep(0.0F), LerpFunction.ofStep(0.5F), LerpFunction.ofStep(0.0F));
   }

   public static <Value> AttributeType<Value> ofNotInterpolated(Codec<Value> var0) {
      return ofNotInterpolated(var0, Map.of());
   }

   private static <Value> Codec<AttributeModifier<Value, ?>> createModifierCodec(Map<AttributeModifier.OperationId, AttributeModifier<Value, ?>> var0) {
      ImmutableBiMap var1 = ImmutableBiMap.builder().put(AttributeModifier.OperationId.OVERRIDE, AttributeModifier.override()).putAll(var0).buildOrThrow();
      Codec var10000 = AttributeModifier.OperationId.CODEC;
      Objects.requireNonNull(var1);
      Function var10001 = var1::get;
      ImmutableBiMap var10002 = var1.inverse();
      Objects.requireNonNull(var10002);
      return ExtraCodecs.idResolverCodec(var10000, var10001, var10002::get);
   }

   public void checkAllowedModifier(AttributeModifier<Value, ?> var1) {
      if (var1 != AttributeModifier.override() && !this.modifierLibrary.containsValue(var1)) {
         String var10002 = String.valueOf(var1);
         throw new IllegalArgumentException("Modifier " + var10002 + " is not valid for " + String.valueOf(this));
      }
   }

   public String toString() {
      return Util.getRegisteredName(BuiltInRegistries.ATTRIBUTE_TYPE, this);
   }
}
