package net.minecraft.advancements.triggers;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;
import net.minecraft.advancements.predicates.DataComponentMatchers;
import net.minecraft.advancements.predicates.ItemPredicate;
import net.minecraft.advancements.predicates.MinMaxBounds;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemInstance;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;

public class InventoryChangeTrigger extends SimpleCriterionTrigger<TriggerInstance> {
   public InventoryChangeTrigger() {
      super();
   }

   public Codec<TriggerInstance> codec() {
      return InventoryChangeTrigger.TriggerInstance.CODEC;
   }

   public void trigger(final ServerPlayer player, final Inventory inventory, final ItemStack changedItem) {
      int slotsFull = 0;
      int slotsEmpty = 0;
      int slotsOccupied = 0;

      for(int slot = 0; slot < inventory.getContainerSize(); ++slot) {
         ItemStack itemStack = inventory.getItem(slot);
         if (itemStack.isEmpty()) {
            ++slotsEmpty;
         } else {
            ++slotsOccupied;
            if (itemStack.getCount() >= itemStack.getMaxStackSize()) {
               ++slotsFull;
            }
         }
      }

      this.trigger(player, inventory, changedItem, slotsFull, slotsEmpty, slotsOccupied);
   }

   private void trigger(final ServerPlayer player, final Inventory inventory, final ItemStack changedItem, final int slotsFull, final int slotsEmpty, final int slotsOccupied) {
      this.trigger(player, (t) -> t.matches(inventory, changedItem, slotsFull, slotsEmpty, slotsOccupied));
   }

   public static record TriggerInstance(Optional<Holder<LootItemCondition>> player, Slots slots, List<ItemPredicate> items) implements SimpleCriterionTrigger.SimpleInstance {
      public static final Codec<TriggerInstance> CODEC = RecordCodecBuilder.create((i) -> i.group(LootItemCondition.CODEC.optionalFieldOf("player").forGetter(TriggerInstance::player), InventoryChangeTrigger.TriggerInstance.Slots.CODEC.optionalFieldOf("slots", InventoryChangeTrigger.TriggerInstance.Slots.ANY).forGetter(TriggerInstance::slots), ItemPredicate.CODEC.listOf().optionalFieldOf("items", List.of()).forGetter(TriggerInstance::items)).apply(i, TriggerInstance::new));

      public TriggerInstance {
         super();
      }

      public static Criterion<TriggerInstance> hasItems(final ItemPredicate.Builder... items) {
         return hasItems((ItemPredicate[])Stream.of(items).map(ItemPredicate.Builder::build).toArray((x$0) -> new ItemPredicate[x$0]));
      }

      public static Criterion<TriggerInstance> hasItems(final ItemPredicate... items) {
         return CriteriaTriggers.INVENTORY_CHANGED.createCriterion(new TriggerInstance(Optional.empty(), InventoryChangeTrigger.TriggerInstance.Slots.ANY, List.of(items)));
      }

      public static Criterion<TriggerInstance> hasItems(final ItemLike... items) {
         ItemPredicate[] predicates = new ItemPredicate[items.length];

         for(int i = 0; i < items.length; ++i) {
            predicates[i] = new ItemPredicate(Optional.of(HolderSet.direct(items[i].asItem().builtInRegistryHolder())), MinMaxBounds.Ints.ANY, DataComponentMatchers.ANY);
         }

         return hasItems(predicates);
      }

      public boolean matches(final Inventory inventory, final ItemStack changedItem, final int slotsFull, final int slotsEmpty, final int slotsOccupied) {
         if (!this.slots.matches(slotsFull, slotsEmpty, slotsOccupied)) {
            return false;
         } else if (this.items.isEmpty()) {
            return true;
         } else if (this.items.size() != 1) {
            List<ItemPredicate> predicates = new ObjectArrayList(this.items);
            int count = inventory.getContainerSize();

            for(int slot = 0; slot < count; ++slot) {
               if (predicates.isEmpty()) {
                  return true;
               }

               ItemStack itemStack = inventory.getItem(slot);
               if (!itemStack.isEmpty()) {
                  predicates.removeIf((predicate) -> predicate.test((ItemInstance)itemStack));
               }
            }

            return predicates.isEmpty();
         } else {
            return !changedItem.isEmpty() && ((ItemPredicate)this.items.get(0)).test((ItemInstance)changedItem);
         }
      }

      public static record Slots(MinMaxBounds.Ints occupied, MinMaxBounds.Ints full, MinMaxBounds.Ints empty) {
         public static final Codec<Slots> CODEC = RecordCodecBuilder.create((i) -> i.group(MinMaxBounds.Ints.CODEC.optionalFieldOf("occupied", MinMaxBounds.Ints.ANY).forGetter(Slots::occupied), MinMaxBounds.Ints.CODEC.optionalFieldOf("full", MinMaxBounds.Ints.ANY).forGetter(Slots::full), MinMaxBounds.Ints.CODEC.optionalFieldOf("empty", MinMaxBounds.Ints.ANY).forGetter(Slots::empty)).apply(i, Slots::new));
         public static final Slots ANY;

         public Slots {
            super();
         }

         public boolean matches(final int slotsFull, final int slotsEmpty, final int slotsOccupied) {
            if (!this.full.matches(slotsFull)) {
               return false;
            } else if (!this.empty.matches(slotsEmpty)) {
               return false;
            } else {
               return this.occupied.matches(slotsOccupied);
            }
         }

         static {
            ANY = new Slots(MinMaxBounds.Ints.ANY, MinMaxBounds.Ints.ANY, MinMaxBounds.Ints.ANY);
         }
      }
   }
}
