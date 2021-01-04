package net.minecraft.advancements.critereon;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import net.minecraft.advancements.CriterionTrigger;
import net.minecraft.advancements.CriterionTriggerInstance;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.PlayerAdvancements;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.Tag;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.level.ItemLike;

public class InventoryChangeTrigger implements CriterionTrigger<InventoryChangeTrigger.TriggerInstance> {
   private static final ResourceLocation ID = new ResourceLocation("inventory_changed");
   private final Map<PlayerAdvancements, InventoryChangeTrigger.PlayerListeners> players = Maps.newHashMap();

   public InventoryChangeTrigger() {
      super();
   }

   public ResourceLocation getId() {
      return ID;
   }

   public void addPlayerListener(PlayerAdvancements var1, CriterionTrigger.Listener<InventoryChangeTrigger.TriggerInstance> var2) {
      InventoryChangeTrigger.PlayerListeners var3 = (InventoryChangeTrigger.PlayerListeners)this.players.get(var1);
      if (var3 == null) {
         var3 = new InventoryChangeTrigger.PlayerListeners(var1);
         this.players.put(var1, var3);
      }

      var3.addListener(var2);
   }

   public void removePlayerListener(PlayerAdvancements var1, CriterionTrigger.Listener<InventoryChangeTrigger.TriggerInstance> var2) {
      InventoryChangeTrigger.PlayerListeners var3 = (InventoryChangeTrigger.PlayerListeners)this.players.get(var1);
      if (var3 != null) {
         var3.removeListener(var2);
         if (var3.isEmpty()) {
            this.players.remove(var1);
         }
      }

   }

   public void removePlayerListeners(PlayerAdvancements var1) {
      this.players.remove(var1);
   }

   public InventoryChangeTrigger.TriggerInstance createInstance(JsonObject var1, JsonDeserializationContext var2) {
      JsonObject var3 = GsonHelper.getAsJsonObject(var1, "slots", new JsonObject());
      MinMaxBounds.Ints var4 = MinMaxBounds.Ints.fromJson(var3.get("occupied"));
      MinMaxBounds.Ints var5 = MinMaxBounds.Ints.fromJson(var3.get("full"));
      MinMaxBounds.Ints var6 = MinMaxBounds.Ints.fromJson(var3.get("empty"));
      ItemPredicate[] var7 = ItemPredicate.fromJsonArray(var1.get("items"));
      return new InventoryChangeTrigger.TriggerInstance(var4, var5, var6, var7);
   }

   public void trigger(ServerPlayer var1, Inventory var2) {
      InventoryChangeTrigger.PlayerListeners var3 = (InventoryChangeTrigger.PlayerListeners)this.players.get(var1.getAdvancements());
      if (var3 != null) {
         var3.trigger(var2);
      }

   }

   // $FF: synthetic method
   public CriterionTriggerInstance createInstance(JsonObject var1, JsonDeserializationContext var2) {
      return this.createInstance(var1, var2);
   }

   static class PlayerListeners {
      private final PlayerAdvancements player;
      private final Set<CriterionTrigger.Listener<InventoryChangeTrigger.TriggerInstance>> listeners = Sets.newHashSet();

      public PlayerListeners(PlayerAdvancements var1) {
         super();
         this.player = var1;
      }

      public boolean isEmpty() {
         return this.listeners.isEmpty();
      }

      public void addListener(CriterionTrigger.Listener<InventoryChangeTrigger.TriggerInstance> var1) {
         this.listeners.add(var1);
      }

      public void removeListener(CriterionTrigger.Listener<InventoryChangeTrigger.TriggerInstance> var1) {
         this.listeners.remove(var1);
      }

      public void trigger(Inventory var1) {
         ArrayList var2 = null;
         Iterator var3 = this.listeners.iterator();

         CriterionTrigger.Listener var4;
         while(var3.hasNext()) {
            var4 = (CriterionTrigger.Listener)var3.next();
            if (((InventoryChangeTrigger.TriggerInstance)var4.getTriggerInstance()).matches(var1)) {
               if (var2 == null) {
                  var2 = Lists.newArrayList();
               }

               var2.add(var4);
            }
         }

         if (var2 != null) {
            var3 = var2.iterator();

            while(var3.hasNext()) {
               var4 = (CriterionTrigger.Listener)var3.next();
               var4.run(this.player);
            }
         }

      }
   }

   public static class TriggerInstance extends AbstractCriterionTriggerInstance {
      private final MinMaxBounds.Ints slotsOccupied;
      private final MinMaxBounds.Ints slotsFull;
      private final MinMaxBounds.Ints slotsEmpty;
      private final ItemPredicate[] predicates;

      public TriggerInstance(MinMaxBounds.Ints var1, MinMaxBounds.Ints var2, MinMaxBounds.Ints var3, ItemPredicate[] var4) {
         super(InventoryChangeTrigger.ID);
         this.slotsOccupied = var1;
         this.slotsFull = var2;
         this.slotsEmpty = var3;
         this.predicates = var4;
      }

      public static InventoryChangeTrigger.TriggerInstance hasItem(ItemPredicate... var0) {
         return new InventoryChangeTrigger.TriggerInstance(MinMaxBounds.Ints.ANY, MinMaxBounds.Ints.ANY, MinMaxBounds.Ints.ANY, var0);
      }

      public static InventoryChangeTrigger.TriggerInstance hasItem(ItemLike... var0) {
         ItemPredicate[] var1 = new ItemPredicate[var0.length];

         for(int var2 = 0; var2 < var0.length; ++var2) {
            var1[var2] = new ItemPredicate((Tag)null, var0[var2].asItem(), MinMaxBounds.Ints.ANY, MinMaxBounds.Ints.ANY, new EnchantmentPredicate[0], (Potion)null, NbtPredicate.ANY);
         }

         return hasItem(var1);
      }

      public JsonElement serializeToJson() {
         JsonObject var1 = new JsonObject();
         if (!this.slotsOccupied.isAny() || !this.slotsFull.isAny() || !this.slotsEmpty.isAny()) {
            JsonObject var2 = new JsonObject();
            var2.add("occupied", this.slotsOccupied.serializeToJson());
            var2.add("full", this.slotsFull.serializeToJson());
            var2.add("empty", this.slotsEmpty.serializeToJson());
            var1.add("slots", var2);
         }

         if (this.predicates.length > 0) {
            JsonArray var7 = new JsonArray();
            ItemPredicate[] var3 = this.predicates;
            int var4 = var3.length;

            for(int var5 = 0; var5 < var4; ++var5) {
               ItemPredicate var6 = var3[var5];
               var7.add(var6.serializeToJson());
            }

            var1.add("items", var7);
         }

         return var1;
      }

      public boolean matches(Inventory var1) {
         int var2 = 0;
         int var3 = 0;
         int var4 = 0;
         ArrayList var5 = Lists.newArrayList(this.predicates);

         for(int var6 = 0; var6 < var1.getContainerSize(); ++var6) {
            ItemStack var7 = var1.getItem(var6);
            if (var7.isEmpty()) {
               ++var3;
            } else {
               ++var4;
               if (var7.getCount() >= var7.getMaxStackSize()) {
                  ++var2;
               }

               Iterator var8 = var5.iterator();

               while(var8.hasNext()) {
                  ItemPredicate var9 = (ItemPredicate)var8.next();
                  if (var9.matches(var7)) {
                     var8.remove();
                  }
               }
            }
         }

         if (!this.slotsFull.matches(var2)) {
            return false;
         } else if (!this.slotsEmpty.matches(var3)) {
            return false;
         } else if (!this.slotsOccupied.matches(var4)) {
            return false;
         } else if (!var5.isEmpty()) {
            return false;
         } else {
            return true;
         }
      }
   }
}
