package net.minecraft.advancements.critereon;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Optional;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.advancements.Criterion;
import net.minecraft.core.Holder;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.alchemy.Potion;

public class BrewedPotionTrigger extends SimpleCriterionTrigger<BrewedPotionTrigger.TriggerInstance> {
   public BrewedPotionTrigger() {
      super();
   }

   @Override
   public Codec<BrewedPotionTrigger.TriggerInstance> codec() {
      return BrewedPotionTrigger.TriggerInstance.CODEC;
   }

   public void trigger(ServerPlayer var1, Holder<Potion> var2) {
      this.trigger(var1, var1x -> var1x.matches(var2));
   }

   public static record TriggerInstance(Optional<ContextAwarePredicate> player, Optional<Holder<Potion>> potion)
      implements SimpleCriterionTrigger.SimpleInstance {
      public static final Codec<BrewedPotionTrigger.TriggerInstance> CODEC = RecordCodecBuilder.create(
         var0 -> var0.group(
                  EntityPredicate.ADVANCEMENT_CODEC.optionalFieldOf("player").forGetter(BrewedPotionTrigger.TriggerInstance::player),
                  Potion.CODEC.optionalFieldOf("potion").forGetter(BrewedPotionTrigger.TriggerInstance::potion)
               )
               .apply(var0, BrewedPotionTrigger.TriggerInstance::new)
      );

      public TriggerInstance(Optional<ContextAwarePredicate> player, Optional<Holder<Potion>> potion) {
         super();
         this.player = player;
         this.potion = potion;
      }

      public static Criterion<BrewedPotionTrigger.TriggerInstance> brewedPotion() {
         return CriteriaTriggers.BREWED_POTION.createCriterion(new BrewedPotionTrigger.TriggerInstance(Optional.empty(), Optional.empty()));
      }

      public boolean matches(Holder<Potion> var1) {
         return !this.potion.isPresent() || this.potion.get().equals(var1);
      }
   }
}
