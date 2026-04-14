package net.minecraft.advancements.triggers;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Optional;
import net.minecraft.advancements.predicates.ContextAwarePredicate;
import net.minecraft.advancements.predicates.ItemPredicate;
import net.minecraft.advancements.predicates.MinMaxBounds;
import net.minecraft.advancements.predicates.entity.EntityPredicate;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemInstance;
import net.minecraft.world.item.ItemStack;

public class EnchantedItemTrigger extends SimpleCriterionTrigger<TriggerInstance> {
   public EnchantedItemTrigger() {
      super();
   }

   public Codec<TriggerInstance> codec() {
      return EnchantedItemTrigger.TriggerInstance.CODEC;
   }

   public void trigger(final ServerPlayer player, final ItemStack itemStack, final int levels) {
      this.trigger(player, (t) -> t.matches(itemStack, levels));
   }

   public static record TriggerInstance(Optional<ContextAwarePredicate> player, Optional<ItemPredicate> item, MinMaxBounds.Ints levels) implements SimpleCriterionTrigger.SimpleInstance {
      public static final Codec<TriggerInstance> CODEC = RecordCodecBuilder.create((i) -> i.group(EntityPredicate.ADVANCEMENT_CODEC.optionalFieldOf("player").forGetter(TriggerInstance::player), ItemPredicate.CODEC.optionalFieldOf("item").forGetter(TriggerInstance::item), MinMaxBounds.Ints.CODEC.optionalFieldOf("levels", MinMaxBounds.Ints.ANY).forGetter(TriggerInstance::levels)).apply(i, TriggerInstance::new));

      public TriggerInstance {
         super();
      }

      public static Criterion<TriggerInstance> enchantedItem() {
         return CriteriaTriggers.ENCHANTED_ITEM.createCriterion(new TriggerInstance(Optional.empty(), Optional.empty(), MinMaxBounds.Ints.ANY));
      }

      public boolean matches(final ItemStack itemStack, final int levels) {
         if (this.item.isPresent() && !((ItemPredicate)this.item.get()).test((ItemInstance)itemStack)) {
            return false;
         } else {
            return this.levels.matches(levels);
         }
      }
   }
}
