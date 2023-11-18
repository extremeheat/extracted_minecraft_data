package net.minecraft.advancements.critereon;

import com.google.gson.JsonObject;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.advancements.Criterion;
import net.minecraft.core.HolderSet;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;

public class InventoryChangeTrigger extends SimpleCriterionTrigger<InventoryChangeTrigger.TriggerInstance> {
   public InventoryChangeTrigger() {
      super();
   }

   public InventoryChangeTrigger.TriggerInstance createInstance(JsonObject var1, Optional<ContextAwarePredicate> var2, DeserializationContext var3) {
      JsonObject var4 = GsonHelper.getAsJsonObject(var1, "slots", new JsonObject());
      MinMaxBounds.Ints var5 = MinMaxBounds.Ints.fromJson(var4.get("occupied"));
      MinMaxBounds.Ints var6 = MinMaxBounds.Ints.fromJson(var4.get("full"));
      MinMaxBounds.Ints var7 = MinMaxBounds.Ints.fromJson(var4.get("empty"));
      List var8 = ItemPredicate.fromJsonArray(var1.get("items"));
      return new InventoryChangeTrigger.TriggerInstance(var2, var5, var6, var7, var8);
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

   public static class TriggerInstance extends AbstractCriterionTriggerInstance {
      private final MinMaxBounds.Ints slotsOccupied;
      private final MinMaxBounds.Ints slotsFull;
      private final MinMaxBounds.Ints slotsEmpty;
      private final List<ItemPredicate> predicates;

      public TriggerInstance(
         Optional<ContextAwarePredicate> var1, MinMaxBounds.Ints var2, MinMaxBounds.Ints var3, MinMaxBounds.Ints var4, List<ItemPredicate> var5
      ) {
         super(var1);
         this.slotsOccupied = var2;
         this.slotsFull = var3;
         this.slotsEmpty = var4;
         this.predicates = var5;
      }

      public static Criterion<InventoryChangeTrigger.TriggerInstance> hasItems(ItemPredicate.Builder... var0) {
         return hasItems(Stream.of(var0).map(ItemPredicate.Builder::build).toArray(var0x -> new ItemPredicate[var0x]));
      }

      public static Criterion<InventoryChangeTrigger.TriggerInstance> hasItems(ItemPredicate... var0) {
         return CriteriaTriggers.INVENTORY_CHANGED
            .createCriterion(
               new InventoryChangeTrigger.TriggerInstance(Optional.empty(), MinMaxBounds.Ints.ANY, MinMaxBounds.Ints.ANY, MinMaxBounds.Ints.ANY, List.of(var0))
            );
      }

      public static Criterion<InventoryChangeTrigger.TriggerInstance> hasItems(ItemLike... var0) {
         ItemPredicate[] var1 = new ItemPredicate[var0.length];

         for(int var2 = 0; var2 < var0.length; ++var2) {
            var1[var2] = new ItemPredicate(
               Optional.empty(),
               Optional.of(HolderSet.direct(var0[var2].asItem().builtInRegistryHolder())),
               MinMaxBounds.Ints.ANY,
               MinMaxBounds.Ints.ANY,
               List.of(),
               List.of(),
               Optional.empty(),
               Optional.empty()
            );
         }

         return hasItems(var1);
      }

      @Override
      public JsonObject serializeToJson() {
         JsonObject var1 = super.serializeToJson();
         if (!this.slotsOccupied.isAny() || !this.slotsFull.isAny() || !this.slotsEmpty.isAny()) {
            JsonObject var2 = new JsonObject();
            var2.add("occupied", this.slotsOccupied.serializeToJson());
            var2.add("full", this.slotsFull.serializeToJson());
            var2.add("empty", this.slotsEmpty.serializeToJson());
            var1.add("slots", var2);
         }

         if (!this.predicates.isEmpty()) {
            var1.add("items", ItemPredicate.serializeToJsonArray(this.predicates));
         }

         return var1;
      }

      public boolean matches(Inventory var1, ItemStack var2, int var3, int var4, int var5) {
         if (!this.slotsFull.matches(var3)) {
            return false;
         } else if (!this.slotsEmpty.matches(var4)) {
            return false;
         } else if (!this.slotsOccupied.matches(var5)) {
            return false;
         } else if (this.predicates.isEmpty()) {
            return true;
         } else if (this.predicates.size() != 1) {
            ObjectArrayList var6 = new ObjectArrayList(this.predicates);
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
            return !var2.isEmpty() && this.predicates.get(0).matches(var2);
         }
      }
   }
}
