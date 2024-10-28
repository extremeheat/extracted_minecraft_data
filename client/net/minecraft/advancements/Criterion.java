package net.minecraft.advancements;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import net.minecraft.util.ExtraCodecs;

public record Criterion<T extends CriterionTriggerInstance>(CriterionTrigger<T> trigger, T triggerInstance) {
   private static final MapCodec<Criterion<?>> MAP_CODEC;
   public static final Codec<Criterion<?>> CODEC;

   public Criterion(CriterionTrigger<T> var1, T var2) {
      super();
      this.trigger = var1;
      this.triggerInstance = var2;
   }

   private static <T extends CriterionTriggerInstance> Codec<Criterion<T>> criterionCodec(CriterionTrigger<T> var0) {
      return var0.codec().xmap((var1) -> {
         return new Criterion(var0, var1);
      }, Criterion::triggerInstance);
   }

   public CriterionTrigger<T> trigger() {
      return this.trigger;
   }

   public T triggerInstance() {
      return this.triggerInstance;
   }

   static {
      MAP_CODEC = ExtraCodecs.dispatchOptionalValue("trigger", "conditions", CriteriaTriggers.CODEC, Criterion::trigger, Criterion::criterionCodec);
      CODEC = MAP_CODEC.codec();
   }
}
