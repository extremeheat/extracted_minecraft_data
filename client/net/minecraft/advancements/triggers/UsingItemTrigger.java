package net.minecraft.advancements.triggers;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Optional;
import net.minecraft.advancements.predicates.ContextAwarePredicate;
import net.minecraft.advancements.predicates.ItemPredicate;
import net.minecraft.advancements.predicates.entity.EntityPredicate;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemInstance;
import net.minecraft.world.item.ItemStack;

public class UsingItemTrigger extends SimpleCriterionTrigger<TriggerInstance> {
   public UsingItemTrigger() {
      super();
   }

   public Codec<TriggerInstance> codec() {
      return UsingItemTrigger.TriggerInstance.CODEC;
   }

   public void trigger(final ServerPlayer player, final ItemStack item) {
      this.trigger(player, (t) -> t.matches(item));
   }

   public static record TriggerInstance(Optional<ContextAwarePredicate> player, Optional<ItemPredicate> item) implements SimpleCriterionTrigger.SimpleInstance {
      public static final Codec<TriggerInstance> CODEC = RecordCodecBuilder.create((i) -> i.group(EntityPredicate.ADVANCEMENT_CODEC.optionalFieldOf("player").forGetter(TriggerInstance::player), ItemPredicate.CODEC.optionalFieldOf("item").forGetter(TriggerInstance::item)).apply(i, TriggerInstance::new));

      public TriggerInstance {
         super();
      }

      public static Criterion<TriggerInstance> lookingAt(final EntityPredicate.Builder player, final ItemPredicate.Builder with) {
         return CriteriaTriggers.USING_ITEM.createCriterion(new TriggerInstance(Optional.of(EntityPredicate.wrap(player)), Optional.of(with.build())));
      }

      public boolean matches(final ItemStack item) {
         return !this.item.isPresent() || ((ItemPredicate)this.item.get()).test((ItemInstance)item);
      }
   }
}
