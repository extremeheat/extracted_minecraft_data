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
import net.minecraft.world.entity.npc.AbstractVillager;
import net.minecraft.world.item.ItemStack;

public class TradeTrigger implements CriterionTrigger<TradeTrigger.TriggerInstance> {
   private static final ResourceLocation ID = new ResourceLocation("villager_trade");
   private final Map<PlayerAdvancements, TradeTrigger.PlayerListeners> players = Maps.newHashMap();

   public TradeTrigger() {
      super();
   }

   public ResourceLocation getId() {
      return ID;
   }

   public void addPlayerListener(PlayerAdvancements var1, CriterionTrigger.Listener<TradeTrigger.TriggerInstance> var2) {
      TradeTrigger.PlayerListeners var3 = (TradeTrigger.PlayerListeners)this.players.get(var1);
      if (var3 == null) {
         var3 = new TradeTrigger.PlayerListeners(var1);
         this.players.put(var1, var3);
      }

      var3.addListener(var2);
   }

   public void removePlayerListener(PlayerAdvancements var1, CriterionTrigger.Listener<TradeTrigger.TriggerInstance> var2) {
      TradeTrigger.PlayerListeners var3 = (TradeTrigger.PlayerListeners)this.players.get(var1);
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

   public TradeTrigger.TriggerInstance createInstance(JsonObject var1, JsonDeserializationContext var2) {
      EntityPredicate var3 = EntityPredicate.fromJson(var1.get("villager"));
      ItemPredicate var4 = ItemPredicate.fromJson(var1.get("item"));
      return new TradeTrigger.TriggerInstance(var3, var4);
   }

   public void trigger(ServerPlayer var1, AbstractVillager var2, ItemStack var3) {
      TradeTrigger.PlayerListeners var4 = (TradeTrigger.PlayerListeners)this.players.get(var1.getAdvancements());
      if (var4 != null) {
         var4.trigger(var1, var2, var3);
      }

   }

   // $FF: synthetic method
   public CriterionTriggerInstance createInstance(JsonObject var1, JsonDeserializationContext var2) {
      return this.createInstance(var1, var2);
   }

   static class PlayerListeners {
      private final PlayerAdvancements player;
      private final Set<CriterionTrigger.Listener<TradeTrigger.TriggerInstance>> listeners = Sets.newHashSet();

      public PlayerListeners(PlayerAdvancements var1) {
         super();
         this.player = var1;
      }

      public boolean isEmpty() {
         return this.listeners.isEmpty();
      }

      public void addListener(CriterionTrigger.Listener<TradeTrigger.TriggerInstance> var1) {
         this.listeners.add(var1);
      }

      public void removeListener(CriterionTrigger.Listener<TradeTrigger.TriggerInstance> var1) {
         this.listeners.remove(var1);
      }

      public void trigger(ServerPlayer var1, AbstractVillager var2, ItemStack var3) {
         ArrayList var4 = null;
         Iterator var5 = this.listeners.iterator();

         CriterionTrigger.Listener var6;
         while(var5.hasNext()) {
            var6 = (CriterionTrigger.Listener)var5.next();
            if (((TradeTrigger.TriggerInstance)var6.getTriggerInstance()).matches(var1, var2, var3)) {
               if (var4 == null) {
                  var4 = Lists.newArrayList();
               }

               var4.add(var6);
            }
         }

         if (var4 != null) {
            var5 = var4.iterator();

            while(var5.hasNext()) {
               var6 = (CriterionTrigger.Listener)var5.next();
               var6.run(this.player);
            }
         }

      }
   }

   public static class TriggerInstance extends AbstractCriterionTriggerInstance {
      private final EntityPredicate villager;
      private final ItemPredicate item;

      public TriggerInstance(EntityPredicate var1, ItemPredicate var2) {
         super(TradeTrigger.ID);
         this.villager = var1;
         this.item = var2;
      }

      public static TradeTrigger.TriggerInstance tradedWithVillager() {
         return new TradeTrigger.TriggerInstance(EntityPredicate.ANY, ItemPredicate.ANY);
      }

      public boolean matches(ServerPlayer var1, AbstractVillager var2, ItemStack var3) {
         if (!this.villager.matches(var1, var2)) {
            return false;
         } else {
            return this.item.matches(var3);
         }
      }

      public JsonElement serializeToJson() {
         JsonObject var1 = new JsonObject();
         var1.add("item", this.item.serializeToJson());
         var1.add("villager", this.villager.serializeToJson());
         return var1;
      }
   }
}
