package net.minecraft.world.timeline;

import java.util.Optional;
import java.util.function.LongSupplier;
import net.minecraft.util.KeyframeTrack;
import net.minecraft.util.KeyframeTrackSampler;
import net.minecraft.world.attribute.EnvironmentAttributeLayer;
import net.minecraft.world.attribute.LerpFunction;
import net.minecraft.world.attribute.modifier.AttributeModifier;
import org.jspecify.annotations.Nullable;

public class AttributeTrackSampler<Value, Argument> implements EnvironmentAttributeLayer.TimeBased<Value> {
   private final AttributeModifier<Value, Argument> modifier;
   private final KeyframeTrackSampler<Argument> argumentSampler;
   private final LongSupplier dayTimeGetter;
   private int cachedTickId;
   private @Nullable Argument cachedArgument;

   public AttributeTrackSampler(Optional<Integer> var1, AttributeModifier<Value, Argument> var2, KeyframeTrack<Argument> var3, LerpFunction<Argument> var4, LongSupplier var5) {
      super();
      this.modifier = var2;
      this.dayTimeGetter = var5;
      this.argumentSampler = var3.bakeSampler(var1, var4);
   }

   public Value applyTimeBased(Value var1, int var2) {
      if (this.cachedArgument == null || var2 != this.cachedTickId) {
         this.cachedTickId = var2;
         this.cachedArgument = this.argumentSampler.sample(this.dayTimeGetter.getAsLong());
      }

      return this.modifier.apply(var1, this.cachedArgument);
   }
}
