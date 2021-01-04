package net.minecraft.advancements.critereon;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
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
import net.minecraft.world.item.ItemStack;

public class ItemDurabilityTrigger implements CriterionTrigger<ItemDurabilityTrigger.TriggerInstance> {
   private static final ResourceLocation ID = new ResourceLocation("item_durability_changed");
   private final Map<PlayerAdvancements, ItemDurabilityTrigger.PlayerListeners> players = Maps.newHashMap();

   public ItemDurabilityTrigger() {
      super();
   }

   public ResourceLocation getId() {
      return ID;
   }

   public void addPlayerListener(PlayerAdvancements var1, CriterionTrigger.Listener<ItemDurabilityTrigger.TriggerInstance> var2) {
      ItemDurabilityTrigger.PlayerListeners var3 = (ItemDurabilityTrigger.PlayerListeners)this.players.get(var1);
      if (var3 == null) {
         var3 = new ItemDurabilityTrigger.PlayerListeners(var1);
         this.players.put(var1, var3);
      }

      var3.addListener(var2);
   }

   public void removePlayerListener(PlayerAdvancements var1, CriterionTrigger.Listener<ItemDurabilityTrigger.TriggerInstance> var2) {
      ItemDurabilityTrigger.PlayerListeners var3 = (ItemDurabilityTrigger.PlayerListeners)this.players.get(var1);
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

   public ItemDurabilityTrigger.TriggerInstance createInstance(JsonObject var1, JsonDeserializationContext var2) {
      ItemPredicate var3 = ItemPredicate.fromJson(var1.get("item"));
      MinMaxBounds.Ints var4 = MinMaxBounds.Ints.fromJson(var1.get("durability"));
      MinMaxBounds.Ints var5 = MinMaxBounds.Ints.fromJson(var1.get("delta"));
      return new ItemDurabilityTrigger.TriggerInstance(var3, var4, var5);
   }

   public void trigger(ServerPlayer var1, ItemStack var2, int var3) {
      ItemDurabilityTrigger.PlayerListeners var4 = (ItemDurabilityTrigger.PlayerListeners)this.players.get(var1.getAdvancements());
      if (var4 != null) {
         var4.trigger(var2, var3);
      }

   }

   // $FF: synthetic method
   public CriterionTriggerInstance createInstance(JsonObject var1, JsonDeserializationContext var2) {
      return this.createInstance(var1, var2);
   }

   static class PlayerListeners {
      private final PlayerAdvancements player;
      private final Set<CriterionTrigger.Listener<ItemDurabilityTrigger.TriggerInstance>> listeners = Sets.newHashSet();

      public PlayerListeners(PlayerAdvancements var1) {
         super();
         this.player = var1;
      }

      public boolean isEmpty() {
         return this.listeners.isEmpty();
      }

      public void addListener(CriterionTrigger.Listener<ItemDurabilityTrigger.TriggerInstance> var1) {
         this.listeners.add(var1);
      }

      public void removeListener(CriterionTrigger.Listener<ItemDurabilityTrigger.TriggerInstance> var1) {
         this.listeners.remove(var1);
      }

      public void trigger(ItemStack var1, int var2) {
         ArrayList var3 = null;
         Iterator var4 = this.listeners.iterator();

         CriterionTrigger.Listener var5;
         while(var4.hasNext()) {
            var5 = (CriterionTrigger.Listener)var4.next();
            if (((ItemDurabilityTrigger.TriggerInstance)var5.getTriggerInstance()).matches(var1, var2)) {
               if (var3 == null) {
                  var3 = Lists.newArrayList();
               }

               var3.add(var5);
            }
         }

         if (var3 != null) {
            var4 = var3.iterator();

            while(var4.hasNext()) {
               var5 = (CriterionTrigger.Listener)var4.next();
               var5.run(this.player);
            }
         }

      }
   }

   public static class TriggerInstance extends AbstractCriterionTriggerInstance {
      private final ItemPredicate item;
      private final MinMaxBounds.Ints durability;
      private final MinMaxBounds.Ints delta;

      public TriggerInstance(ItemPredicate var1, MinMaxBounds.Ints var2, MinMaxBounds.Ints var3) {
         super(ItemDurabilityTrigger.ID);
         this.item = var1;
         this.durability = var2;
         this.delta = var3;
      }

      public static ItemDurabilityTrigger.TriggerInstance changedDurability(ItemPredicate var0, MinMaxBounds.Ints var1) {
         return new ItemDurabilityTrigger.TriggerInstance(var0, var1, MinMaxBounds.Ints.ANY);
      }

      public boolean matches(ItemStack var1, int var2) {
         if (!this.item.matches(var1)) {
            return false;
         } else if (!this.durability.matches(var1.getMaxDamage() - var2)) {
            return false;
         } else {
            return this.delta.matches(var1.getDamageValue() - var2);
         }
      }

      public JsonElement serializeToJson() {
         JsonObject var1 = new JsonObject();
         var1.add("item", this.item.serializeToJson());
         var1.add("durability", this.durability.serializeToJson());
         var1.add("delta", this.delta.serializeToJson());
         return var1;
      }
   }
}
