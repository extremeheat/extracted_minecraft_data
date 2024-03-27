package net.minecraft.advancements.critereon;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.mojang.serialization.codecs.RecordCodecBuilder.Instance;
import java.util.Optional;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.advancements.Criterion;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
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

   public static record TriggerInstance(Optional<ContextAwarePredicate> b, Optional<Holder<Potion>> c) implements SimpleCriterionTrigger.SimpleInstance {
      private final Optional<ContextAwarePredicate> player;
      private final Optional<Holder<Potion>> potion;
      public static final Codec<BrewedPotionTrigger.TriggerInstance> CODEC = RecordCodecBuilder.create(
         var0 -> var0.group(
                  EntityPredicate.ADVANCEMENT_CODEC.optionalFieldOf("player").forGetter(BrewedPotionTrigger.TriggerInstance::player),
                  BuiltInRegistries.POTION.holderByNameCodec().optionalFieldOf("potion").forGetter(BrewedPotionTrigger.TriggerInstance::potion)
               )
               .apply(var0, BrewedPotionTrigger.TriggerInstance::new)
      );

      public TriggerInstance(Optional<ContextAwarePredicate> var1, Optional<Holder<Potion>> var2) {
         super();
         this.player = var1;
         this.potion = var2;
      }

      public static Criterion<BrewedPotionTrigger.TriggerInstance> brewedPotion() {
         return CriteriaTriggers.BREWED_POTION.createCriterion(new BrewedPotionTrigger.TriggerInstance(Optional.empty(), Optional.empty()));
      }

      public boolean matches(Holder<Potion> var1) {
         return !this.potion.isPresent() || this.potion.get().equals(var1);
      }
   }
}
