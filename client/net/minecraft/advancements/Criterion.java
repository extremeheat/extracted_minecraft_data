package net.minecraft.advancements;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import net.minecraft.util.ExtraCodecs;

public record Criterion<T extends CriterionTriggerInstance>(CriterionTrigger<T> trigger, T triggerInstance) {
   private static final MapCodec<Criterion<?>> MAP_CODEC = ExtraCodecs.dispatchOptionalValue(
      "trigger", "conditions", (Codec<CriterionTrigger<T>>)CriteriaTriggers.CODEC, Criterion::trigger, Criterion::criterionCodec
   );
   public static final Codec<Criterion<?>> CODEC = MAP_CODEC.codec();

   public Criterion(CriterionTrigger<T> trigger, T triggerInstance) {
      super();
      this.trigger = trigger;
      this.triggerInstance = (T)triggerInstance;
   }

   private static <T extends CriterionTriggerInstance> Codec<Criterion<T>> criterionCodec(CriterionTrigger<T> var0) {
      return var0.codec().xmap(var1 -> new Criterion<>(var0, var1), Criterion::triggerInstance);
   }
}
