package net.minecraft.advancements.critereon;

import com.mojang.serialization.Codec;
import net.minecraft.advancements.CriterionTrigger;
import net.minecraft.advancements.CriterionTriggerInstance;
import net.minecraft.server.PlayerAdvancements;

public class ImpossibleTrigger implements CriterionTrigger<TriggerInstance> {
   public ImpossibleTrigger() {
      super();
   }

   public void addPlayerListener(PlayerAdvancements var1, CriterionTrigger.Listener<TriggerInstance> var2) {
   }

   public void removePlayerListener(PlayerAdvancements var1, CriterionTrigger.Listener<TriggerInstance> var2) {
   }

   public void removePlayerListeners(PlayerAdvancements var1) {
   }

   public Codec<TriggerInstance> codec() {
      return ImpossibleTrigger.TriggerInstance.CODEC;
   }

   public static record TriggerInstance() implements CriterionTriggerInstance {
      public static final Codec<TriggerInstance> CODEC = Codec.unit(new TriggerInstance());

      public TriggerInstance() {
         super();
      }

      public void validate(CriterionValidator var1) {
      }
   }
}
