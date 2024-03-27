package net.minecraft.advancements.critereon;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.mojang.serialization.codecs.RecordCodecBuilder.Instance;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.advancements.Criterion;
import net.minecraft.core.HolderSet;
import net.minecraft.core.component.DataComponentPredicate;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;

public class InventoryChangeTrigger extends SimpleCriterionTrigger<InventoryChangeTrigger.TriggerInstance> {
   public InventoryChangeTrigger() {
      super();
   }

   @Override
   public Codec<InventoryChangeTrigger.TriggerInstance> codec() {
      return InventoryChangeTrigger.TriggerInstance.CODEC;
   }

   public void trigger(ServerPlayer var1, Inventory var2, ItemStack var3) {
      int var4 = 0;
      int var5 = 0;
      int var6 = 0;

      for(int var7 = 0; var7 < var2.getContainerSize(); ++var7) {
         ItemStack var8 = var2.getItem(var7);
         if (var8.isEmpty()) {
            ++var5;
         } else {
            ++var6;
            if (var8.getCount() >= var8.getMaxStackSize()) {
               ++var4;
            }
         }
      }

      this.trigger(var1, var2, var3, var4, var5, var6);
   }

   private void trigger(ServerPlayer var1, Inventory var2, ItemStack var3, int var4, int var5, int var6) {
      this.trigger(var1, var5x -> var5x.matches(var2, var3, var4, var5, var6));
   }

   public static record TriggerInstance(Optional<ContextAwarePredicate> b, InventoryChangeTrigger.TriggerInstance.Slots c, List<ItemPredicate> d)
      implements SimpleCriterionTrigger.SimpleInstance {
      private final Optional<ContextAwarePredicate> player;
      private final InventoryChangeTrigger.TriggerInstance.Slots slots;
      private final List<ItemPredicate> items;
      public static final Codec<InventoryChangeTrigger.TriggerInstance> CODEC = RecordCodecBuilder.create(
         var0 -> var0.group(
                  EntityPredicate.ADVANCEMENT_CODEC.optionalFieldOf("player").forGetter(InventoryChangeTrigger.TriggerInstance::player),
                  InventoryChangeTrigger.TriggerInstance.Slots.CODEC
                     .optionalFieldOf("slots", InventoryChangeTrigger.TriggerInstance.Slots.ANY)
                     .forGetter(InventoryChangeTrigger.TriggerInstance::slots),
                  ItemPredicate.CODEC.listOf().optionalFieldOf("items", List.of()).forGetter(InventoryChangeTrigger.TriggerInstance::items)
               )
               .apply(var0, InventoryChangeTrigger.TriggerInstance::new)
      );

      public TriggerInstance(Optional<ContextAwarePredicate> var1, InventoryChangeTrigger.TriggerInstance.Slots var2, List<ItemPredicate> var3) {
         super();
         this.player = var1;
         this.slots = var2;
         this.items = var3;
      }

      public static Criterion<InventoryChangeTrigger.TriggerInstance> hasItems(ItemPredicate.Builder... var0) {
         return hasItems(Stream.of(var0).map(ItemPredicate.Builder::build).toArray(var0x -> new ItemPredicate[var0x]));
      }

      public static Criterion<InventoryChangeTrigger.TriggerInstance> hasItems(ItemPredicate... var0) {
         return CriteriaTriggers.INVENTORY_CHANGED
            .createCriterion(new InventoryChangeTrigger.TriggerInstance(Optional.empty(), InventoryChangeTrigger.TriggerInstance.Slots.ANY, List.of(var0)));
      }

      public static Criterion<InventoryChangeTrigger.TriggerInstance> hasItems(ItemLike... var0) {
         ItemPredicate[] var1 = new ItemPredicate[var0.length];

         for(int var2 = 0; var2 < var0.length; ++var2) {
            var1[var2] = new ItemPredicate(
               Optional.of(HolderSet.direct(var0[var2].asItem().builtInRegistryHolder())), MinMaxBounds.Ints.ANY, DataComponentPredicate.EMPTY, Map.of()
            );
         }

         return hasItems(var1);
      }

      public boolean matches(Inventory var1, ItemStack var2, int var3, int var4, int var5) {
         if (!this.slots.matches(var3, var4, var5)) {
            return false;
         } else if (this.items.isEmpty()) {
            return true;
         } else if (this.items.size() != 1) {
            ObjectArrayList var6 = new ObjectArrayList(this.items);
            int var7 = var1.getContainerSize();

            for(int var8 = 0; var8 < var7; ++var8) {
               if (var6.isEmpty()) {
                  return true;
               }

               ItemStack var9 = var1.getItem(var8);
               if (!var9.isEmpty()) {
                  var6.removeIf(var1x -> var1x.matches(var9));
               }
            }

            return var6.isEmpty();
         } else {
            return !var2.isEmpty() && ((ItemPredicate)this.items.get(0)).matches(var2);
         }
      }

      public static record Slots(MinMaxBounds.Ints c, MinMaxBounds.Ints d, MinMaxBounds.Ints e) {
         private final MinMaxBounds.Ints occupied;
         private final MinMaxBounds.Ints full;
         private final MinMaxBounds.Ints empty;
         public static final Codec<InventoryChangeTrigger.TriggerInstance.Slots> CODEC = RecordCodecBuilder.create(
            var0 -> var0.group(
                     MinMaxBounds.Ints.CODEC
                        .optionalFieldOf("occupied", MinMaxBounds.Ints.ANY)
                        .forGetter(InventoryChangeTrigger.TriggerInstance.Slots::occupied),
                     MinMaxBounds.Ints.CODEC.optionalFieldOf("full", MinMaxBounds.Ints.ANY).forGetter(InventoryChangeTrigger.TriggerInstance.Slots::full),
                     MinMaxBounds.Ints.CODEC.optionalFieldOf("empty", MinMaxBounds.Ints.ANY).forGetter(InventoryChangeTrigger.TriggerInstance.Slots::empty)
                  )
                  .apply(var0, InventoryChangeTrigger.TriggerInstance.Slots::new)
         );
         public static final InventoryChangeTrigger.TriggerInstance.Slots ANY = new InventoryChangeTrigger.TriggerInstance.Slots(
            MinMaxBounds.Ints.ANY, MinMaxBounds.Ints.ANY, MinMaxBounds.Ints.ANY
         );

         public Slots(MinMaxBounds.Ints var1, MinMaxBounds.Ints var2, MinMaxBounds.Ints var3) {
            super();
            this.occupied = var1;
            this.full = var2;
            this.empty = var3;
         }

         public boolean matches(int var1, int var2, int var3) {
            if (!this.full.matches(var1)) {
               return false;
            } else if (!this.empty.matches(var2)) {
               return false;
            } else {
               return this.occupied.matches(var3);
            }
         }
      }
   }
}
