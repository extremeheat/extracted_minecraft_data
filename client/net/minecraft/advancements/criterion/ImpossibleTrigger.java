package net.minecraft.advancements.criterion;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import net.minecraft.advancements.CriterionTrigger;
import net.minecraft.advancements.CriterionTriggerInstance;
import net.minecraft.server.PlayerAdvancements;
import net.minecraft.world.level.storage.loot.ValidationContextSource;

public class ImpossibleTrigger implements CriterionTrigger<TriggerInstance> {
   public ImpossibleTrigger() {
      super();
   }

   public void addPlayerListener(final PlayerAdvancements player, final CriterionTrigger.Listener<TriggerInstance> listener) {
   }

   public void removePlayerListener(final PlayerAdvancements player, final CriterionTrigger.Listener<TriggerInstance> listener) {
   }

   public void removePlayerListeners(final PlayerAdvancements player) {
   }

   public Codec<TriggerInstance> codec() {
      return ImpossibleTrigger.TriggerInstance.CODEC;
   }

   public static record TriggerInstance() implements CriterionTriggerInstance {
      public static final Codec<TriggerInstance> CODEC = MapCodec.unitCodec(new TriggerInstance());

      public TriggerInstance() {
         super();
      }

      public void validate(final ValidationContextSource validator) {
      }
   }
}
