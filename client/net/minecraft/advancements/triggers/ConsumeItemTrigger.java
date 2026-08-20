package net.minecraft.advancements.triggers;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Optional;
import net.minecraft.advancements.predicates.ItemPredicate;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderGetter;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemInstance;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;

public class ConsumeItemTrigger extends SimpleCriterionTrigger<TriggerInstance> {
   public ConsumeItemTrigger() {
      super();
   }

   public Codec<TriggerInstance> codec() {
      return ConsumeItemTrigger.TriggerInstance.CODEC;
   }

   public void trigger(final ServerPlayer player, final ItemStack itemStack) {
      this.trigger(player, (t) -> t.matches(itemStack));
   }

   public static record TriggerInstance(Optional<Holder<LootItemCondition>> player, Optional<ItemPredicate> item) implements SimpleCriterionTrigger.SimpleInstance {
      public static final Codec<TriggerInstance> CODEC = RecordCodecBuilder.create((i) -> i.group(LootItemCondition.CODEC.optionalFieldOf("player").forGetter(TriggerInstance::player), ItemPredicate.CODEC.optionalFieldOf("item").forGetter(TriggerInstance::item)).apply(i, TriggerInstance::new));

      public TriggerInstance {
         super();
      }

      public static Criterion<TriggerInstance> usedItem() {
         return CriteriaTriggers.CONSUME_ITEM.createCriterion(new TriggerInstance(Optional.empty(), Optional.empty()));
      }

      public static Criterion<TriggerInstance> usedItem(final HolderGetter<Item> items, final ItemLike item) {
         return usedItem(ItemPredicate.Builder.item().of(items, item.asItem()));
      }

      public static Criterion<TriggerInstance> usedItem(final ItemPredicate.Builder predicate) {
         return CriteriaTriggers.CONSUME_ITEM.createCriterion(new TriggerInstance(Optional.empty(), Optional.of(predicate.build())));
      }

      public boolean matches(final ItemStack itemStack) {
         return this.item.isEmpty() || ((ItemPredicate)this.item.get()).test((ItemInstance)itemStack);
      }
   }
}
