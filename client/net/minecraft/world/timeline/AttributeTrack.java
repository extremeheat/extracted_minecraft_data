package net.minecraft.world.timeline;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.MapCodec;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.LongSupplier;
import net.minecraft.util.KeyframeTrack;
import net.minecraft.util.Util;
import net.minecraft.world.attribute.EnvironmentAttribute;
import net.minecraft.world.attribute.modifier.AttributeModifier;

public record AttributeTrack<Value, Argument>(AttributeModifier<Value, Argument> modifier, KeyframeTrack<Argument> argumentTrack) {
   public AttributeTrack(AttributeModifier<Value, Argument> var1, KeyframeTrack<Argument> var2) {
      super();
      this.modifier = var1;
      this.argumentTrack = var2;
   }

   public static <Value> Codec<AttributeTrack<Value, ?>> createCodec(EnvironmentAttribute<Value> var0) {
      MapCodec var1 = var0.type().modifierCodec().optionalFieldOf("modifier", AttributeModifier.override());
      return var1.dispatch(AttributeTrack::modifier, Util.memoize((Function)((var1x) -> createCodecWithModifier(var0, var1x))));
   }

   private static <Value, Argument> MapCodec<AttributeTrack<Value, Argument>> createCodecWithModifier(EnvironmentAttribute<Value> var0, AttributeModifier<Value, Argument> var1) {
      return KeyframeTrack.mapCodec(var1.argumentCodec(var0)).xmap((var1x) -> new AttributeTrack(var1, var1x), AttributeTrack::argumentTrack);
   }

   public AttributeTrackSampler<Value, Argument> bakeSampler(EnvironmentAttribute<Value> var1, Optional<Integer> var2, LongSupplier var3) {
      return new AttributeTrackSampler<Value, Argument>(var2, this.modifier, this.argumentTrack, this.modifier.argumentKeyframeLerp(var1), var3);
   }

   public static DataResult<AttributeTrack<?, ?>> validatePeriod(AttributeTrack<?, ?> var0, int var1) {
      return KeyframeTrack.validatePeriod(var0.argumentTrack(), var1).map((var1x) -> var0);
   }
}
