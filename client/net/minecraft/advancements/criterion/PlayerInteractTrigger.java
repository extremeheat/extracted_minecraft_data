package net.minecraft.advancements.criterion;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Optional;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.advancements.Criterion;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemInstance;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.Validatable;
import net.minecraft.world.level.storage.loot.ValidationContextSource;

public class PlayerInteractTrigger extends SimpleCriterionTrigger<TriggerInstance> {
   public PlayerInteractTrigger() {
      super();
   }

   public Codec<TriggerInstance> codec() {
      return PlayerInteractTrigger.TriggerInstance.CODEC;
   }

   public void trigger(final ServerPlayer player, final ItemStack itemStack, final Entity interactedWith) {
      LootContext context = EntityPredicate.createContext(player, interactedWith);
      this.trigger(player, (t) -> t.matches(itemStack, context));
   }

   public static record TriggerInstance(Optional<ContextAwarePredicate> player, Optional<ItemPredicate> item, Optional<ContextAwarePredicate> entity) implements SimpleCriterionTrigger.SimpleInstance {
      public static final Codec<TriggerInstance> CODEC = RecordCodecBuilder.create((i) -> i.group(EntityPredicate.ADVANCEMENT_CODEC.optionalFieldOf("player").forGetter(TriggerInstance::player), ItemPredicate.CODEC.optionalFieldOf("item").forGetter(TriggerInstance::item), EntityPredicate.ADVANCEMENT_CODEC.optionalFieldOf("entity").forGetter(TriggerInstance::entity)).apply(i, TriggerInstance::new));

      public TriggerInstance {
         super();
      }

      public static Criterion<TriggerInstance> itemUsedOnEntity(final Optional<ContextAwarePredicate> player, final ItemPredicate.Builder item, final Optional<ContextAwarePredicate> entity) {
         return CriteriaTriggers.PLAYER_INTERACTED_WITH_ENTITY.createCriterion(new TriggerInstance(player, Optional.of(item.build()), entity));
      }

      public static Criterion<TriggerInstance> equipmentSheared(final Optional<ContextAwarePredicate> player, final ItemPredicate.Builder item, final Optional<ContextAwarePredicate> entity) {
         return CriteriaTriggers.PLAYER_SHEARED_EQUIPMENT.createCriterion(new TriggerInstance(player, Optional.of(item.build()), entity));
      }

      public static Criterion<TriggerInstance> equipmentSheared(final ItemPredicate.Builder item, final Optional<ContextAwarePredicate> entity) {
         return CriteriaTriggers.PLAYER_SHEARED_EQUIPMENT.createCriterion(new TriggerInstance(Optional.empty(), Optional.of(item.build()), entity));
      }

      public static Criterion<TriggerInstance> itemUsedOnEntity(final ItemPredicate.Builder item, final Optional<ContextAwarePredicate> entity) {
         return itemUsedOnEntity(Optional.empty(), item, entity);
      }

      public boolean matches(final ItemStack itemStack, final LootContext interactedWith) {
         if (this.item.isPresent() && !((ItemPredicate)this.item.get()).test((ItemInstance)itemStack)) {
            return false;
         } else {
            return this.entity.isEmpty() || ((ContextAwarePredicate)this.entity.get()).matches(interactedWith);
         }
      }

      public void validate(final ValidationContextSource validator) {
         SimpleCriterionTrigger.SimpleInstance.super.validate(validator);
         Validatable.validate(validator.entityContext(), "entity", this.entity);
      }
   }
}
