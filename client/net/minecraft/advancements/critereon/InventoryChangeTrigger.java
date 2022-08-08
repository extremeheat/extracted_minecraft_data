package net.minecraft.advancements.critereon;

import com.google.common.collect.ImmutableSet;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.TagKey;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.level.ItemLike;

public class InventoryChangeTrigger extends SimpleCriterionTrigger<TriggerInstance> {
   static final ResourceLocation ID = new ResourceLocation("inventory_changed");

   public InventoryChangeTrigger() {
      super();
   }

   public ResourceLocation getId() {
      return ID;
   }

   public TriggerInstance createInstance(JsonObject var1, EntityPredicate.Composite var2, DeserializationContext var3) {
      JsonObject var4 = GsonHelper.getAsJsonObject(var1, "slots", new JsonObject());
      MinMaxBounds.Ints var5 = MinMaxBounds.Ints.fromJson(var4.get("occupied"));
      MinMaxBounds.Ints var6 = MinMaxBounds.Ints.fromJson(var4.get("full"));
      MinMaxBounds.Ints var7 = MinMaxBounds.Ints.fromJson(var4.get("empty"));
      ItemPredicate[] var8 = ItemPredicate.fromJsonArray(var1.get("items"));
      return new TriggerInstance(var2, var5, var6, var7, var8);
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
      this.trigger(var1, (var5x) -> {
         return var5x.matches(var2, var3, var4, var5, var6);
      });
   }

   // $FF: synthetic method
   public AbstractCriterionTriggerInstance createInstance(JsonObject var1, EntityPredicate.Composite var2, DeserializationContext var3) {
      return this.createInstance(var1, var2, var3);
   }

   public static class TriggerInstance extends AbstractCriterionTriggerInstance {
      private final MinMaxBounds.Ints slotsOccupied;
      private final MinMaxBounds.Ints slotsFull;
      private final MinMaxBounds.Ints slotsEmpty;
      private final ItemPredicate[] predicates;

      public TriggerInstance(EntityPredicate.Composite var1, MinMaxBounds.Ints var2, MinMaxBounds.Ints var3, MinMaxBounds.Ints var4, ItemPredicate[] var5) {
         super(InventoryChangeTrigger.ID, var1);
         this.slotsOccupied = var2;
         this.slotsFull = var3;
         this.slotsEmpty = var4;
         this.predicates = var5;
      }

      public static TriggerInstance hasItems(ItemPredicate... var0) {
         return new TriggerInstance(EntityPredicate.Composite.ANY, MinMaxBounds.Ints.ANY, MinMaxBounds.Ints.ANY, MinMaxBounds.Ints.ANY, var0);
      }

      public static TriggerInstance hasItems(ItemLike... var0) {
         ItemPredicate[] var1 = new ItemPredicate[var0.length];

         for(int var2 = 0; var2 < var0.length; ++var2) {
            var1[var2] = new ItemPredicate((TagKey)null, ImmutableSet.of(var0[var2].asItem()), MinMaxBounds.Ints.ANY, MinMaxBounds.Ints.ANY, EnchantmentPredicate.NONE, EnchantmentPredicate.NONE, (Potion)null, NbtPredicate.ANY);
         }

         return hasItems(var1);
      }

      public JsonObject serializeToJson(SerializationContext var1) {
         JsonObject var2 = super.serializeToJson(var1);
         if (!this.slotsOccupied.isAny() || !this.slotsFull.isAny() || !this.slotsEmpty.isAny()) {
            JsonObject var3 = new JsonObject();
            var3.add("occupied", this.slotsOccupied.serializeToJson());
            var3.add("full", this.slotsFull.serializeToJson());
            var3.add("empty", this.slotsEmpty.serializeToJson());
            var2.add("slots", var3);
         }

         if (this.predicates.length > 0) {
            JsonArray var8 = new JsonArray();
            ItemPredicate[] var4 = this.predicates;
            int var5 = var4.length;

            for(int var6 = 0; var6 < var5; ++var6) {
               ItemPredicate var7 = var4[var6];
               var8.add(var7.serializeToJson());
            }

            var2.add("items", var8);
         }

         return var2;
      }

      public boolean matches(Inventory var1, ItemStack var2, int var3, int var4, int var5) {
         if (!this.slotsFull.matches(var3)) {
            return false;
         } else if (!this.slotsEmpty.matches(var4)) {
            return false;
         } else if (!this.slotsOccupied.matches(var5)) {
            return false;
         } else {
            int var6 = this.predicates.length;
            if (var6 == 0) {
               return true;
            } else if (var6 != 1) {
               ObjectArrayList var7 = new ObjectArrayList(this.predicates);
               int var8 = var1.getContainerSize();

               for(int var9 = 0; var9 < var8; ++var9) {
                  if (var7.isEmpty()) {
                     return true;
                  }

                  ItemStack var10 = var1.getItem(var9);
                  if (!var10.isEmpty()) {
                     var7.removeIf((var1x) -> {
                        return var1x.matches(var10);
                     });
                  }
               }

               return var7.isEmpty();
            } else {
               return !var2.isEmpty() && this.predicates[0].matches(var2);
            }
         }
      }
   }
}
