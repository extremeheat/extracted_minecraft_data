package net.minecraft.advancements.triggers;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Optional;
import net.minecraft.core.Holder;
import net.minecraft.core.component.predicates.PotionsPredicate;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;

public class BrewedPotionTrigger extends SimpleCriterionTrigger<TriggerInstance> {
   public BrewedPotionTrigger() {
      super();
   }

   public Codec<TriggerInstance> codec() {
      return BrewedPotionTrigger.TriggerInstance.CODEC;
   }

   public void trigger(final ServerPlayer player, final PotionContents potion) {
      this.trigger(player, (t) -> t.matches(potion));
   }

   public static record TriggerInstance(Optional<Holder<LootItemCondition>> player, Optional<PotionsPredicate> potion) implements SimpleCriterionTrigger.SimpleInstance {
      public static final Codec<TriggerInstance> CODEC = RecordCodecBuilder.create((i) -> i.group(LootItemCondition.CODEC.optionalFieldOf("player").forGetter(TriggerInstance::player), PotionsPredicate.CODEC.optionalFieldOf("potion").forGetter(TriggerInstance::potion)).apply(i, TriggerInstance::new));

      public TriggerInstance {
         super();
      }

      public static Criterion<TriggerInstance> brewedPotion() {
         return CriteriaTriggers.BREWED_POTION.createCriterion(new TriggerInstance(Optional.empty(), Optional.empty()));
      }

      public boolean matches(final PotionContents potion) {
         return !this.potion.isPresent() || ((PotionsPredicate)this.potion.get()).matches(potion);
      }
   }
}
