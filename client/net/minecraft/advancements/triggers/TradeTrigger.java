package net.minecraft.advancements.triggers;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Optional;
import net.minecraft.advancements.predicates.ItemPredicate;
import net.minecraft.advancements.predicates.entity.EntityPredicate;
import net.minecraft.core.Holder;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.npc.villager.AbstractVillager;
import net.minecraft.world.item.ItemInstance;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.Validatable;
import net.minecraft.world.level.storage.loot.ValidationContextSource;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;

public class TradeTrigger extends SimpleCriterionTrigger<TriggerInstance> {
   public TradeTrigger() {
      super();
   }

   public Codec<TriggerInstance> codec() {
      return TradeTrigger.TriggerInstance.CODEC;
   }

   public void trigger(final ServerPlayer player, final AbstractVillager villager, final ItemStack itemStack) {
      LootContext villagerContext = EntityPredicate.createContext(player, villager);
      this.trigger(player, (t) -> t.matches(villagerContext, itemStack));
   }

   public static record TriggerInstance(Optional<Holder<LootItemCondition>> player, Optional<Holder<LootItemCondition>> villager, Optional<ItemPredicate> item) implements SimpleCriterionTrigger.SimpleInstance {
      public static final Codec<TriggerInstance> CODEC = RecordCodecBuilder.create((i) -> i.group(LootItemCondition.CODEC.optionalFieldOf("player").forGetter(TriggerInstance::player), LootItemCondition.CODEC.optionalFieldOf("villager").forGetter(TriggerInstance::villager), ItemPredicate.CODEC.optionalFieldOf("item").forGetter(TriggerInstance::item)).apply(i, TriggerInstance::new));

      public TriggerInstance {
         super();
      }

      public static Criterion<TriggerInstance> tradedWithVillager() {
         return CriteriaTriggers.TRADE.createCriterion(new TriggerInstance(Optional.empty(), Optional.empty(), Optional.empty()));
      }

      public static Criterion<TriggerInstance> tradedWithVillager(final EntityPredicate.Builder player) {
         return CriteriaTriggers.TRADE.createCriterion(new TriggerInstance(Optional.of(EntityPredicate.wrap(player)), Optional.empty(), Optional.empty()));
      }

      public boolean matches(final LootContext villager, final ItemStack itemStack) {
         if (this.villager.isPresent() && !((LootItemCondition)((Holder)this.villager.get()).value()).test(villager)) {
            return false;
         } else {
            return !this.item.isPresent() || ((ItemPredicate)this.item.get()).test((ItemInstance)itemStack);
         }
      }

      public void validate(final ValidationContextSource validator) {
         SimpleCriterionTrigger.SimpleInstance.super.validate(validator);
         Validatable.validateHolder(validator.entityContext(), "villager", this.villager);
      }
   }
}
