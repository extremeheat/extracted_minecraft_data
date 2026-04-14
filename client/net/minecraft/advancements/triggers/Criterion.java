package net.minecraft.advancements.triggers;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import net.minecraft.advancements.CriterionTriggerInstance;
import net.minecraft.util.ExtraCodecs;

public record Criterion<T extends CriterionTriggerInstance>(CriterionTrigger<T> trigger, T triggerInstance) {
   private static final MapCodec<Criterion<?>> MAP_CODEC;
   public static final Codec<Criterion<?>> CODEC;

   public Criterion {
      super();
   }

   private static <T extends CriterionTriggerInstance> Codec<Criterion<T>> criterionCodec(final CriterionTrigger<T> trigger) {
      return trigger.codec().xmap((instance) -> new Criterion(trigger, instance), Criterion::triggerInstance);
   }

   static {
      MAP_CODEC = ExtraCodecs.dispatchOptionalValue("trigger", "conditions", CriteriaTriggers.CODEC, Criterion::trigger, Criterion::criterionCodec);
      CODEC = MAP_CODEC.codec();
   }
}
