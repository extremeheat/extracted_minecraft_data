package net.minecraft.advancements.critereon;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import net.minecraft.advancements.CriterionTrigger;
import net.minecraft.advancements.CriterionTriggerInstance;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.PlayerAdvancements;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.fishing.FishingHook;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;

public class FishingRodHookedTrigger implements CriterionTrigger<FishingRodHookedTrigger.TriggerInstance> {
   private static final ResourceLocation ID = new ResourceLocation("fishing_rod_hooked");
   private final Map<PlayerAdvancements, FishingRodHookedTrigger.PlayerListeners> players = Maps.newHashMap();

   public FishingRodHookedTrigger() {
      super();
   }

   public ResourceLocation getId() {
      return ID;
   }

   public void addPlayerListener(PlayerAdvancements var1, CriterionTrigger.Listener<FishingRodHookedTrigger.TriggerInstance> var2) {
      FishingRodHookedTrigger.PlayerListeners var3 = (FishingRodHookedTrigger.PlayerListeners)this.players.get(var1);
      if (var3 == null) {
         var3 = new FishingRodHookedTrigger.PlayerListeners(var1);
         this.players.put(var1, var3);
      }

      var3.addListener(var2);
   }

   public void removePlayerListener(PlayerAdvancements var1, CriterionTrigger.Listener<FishingRodHookedTrigger.TriggerInstance> var2) {
      FishingRodHookedTrigger.PlayerListeners var3 = (FishingRodHookedTrigger.PlayerListeners)this.players.get(var1);
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

   public FishingRodHookedTrigger.TriggerInstance createInstance(JsonObject var1, JsonDeserializationContext var2) {
      ItemPredicate var3 = ItemPredicate.fromJson(var1.get("rod"));
      EntityPredicate var4 = EntityPredicate.fromJson(var1.get("entity"));
      ItemPredicate var5 = ItemPredicate.fromJson(var1.get("item"));
      return new FishingRodHookedTrigger.TriggerInstance(var3, var4, var5);
   }

   public void trigger(ServerPlayer var1, ItemStack var2, FishingHook var3, Collection<ItemStack> var4) {
      FishingRodHookedTrigger.PlayerListeners var5 = (FishingRodHookedTrigger.PlayerListeners)this.players.get(var1.getAdvancements());
      if (var5 != null) {
         var5.trigger(var1, var2, var3, var4);
      }

   }

   // $FF: synthetic method
   public CriterionTriggerInstance createInstance(JsonObject var1, JsonDeserializationContext var2) {
      return this.createInstance(var1, var2);
   }

   static class PlayerListeners {
      private final PlayerAdvancements player;
      private final Set<CriterionTrigger.Listener<FishingRodHookedTrigger.TriggerInstance>> listeners = Sets.newHashSet();

      public PlayerListeners(PlayerAdvancements var1) {
         super();
         this.player = var1;
      }

      public boolean isEmpty() {
         return this.listeners.isEmpty();
      }

      public void addListener(CriterionTrigger.Listener<FishingRodHookedTrigger.TriggerInstance> var1) {
         this.listeners.add(var1);
      }

      public void removeListener(CriterionTrigger.Listener<FishingRodHookedTrigger.TriggerInstance> var1) {
         this.listeners.remove(var1);
      }

      public void trigger(ServerPlayer var1, ItemStack var2, FishingHook var3, Collection<ItemStack> var4) {
         ArrayList var5 = null;
         Iterator var6 = this.listeners.iterator();

         CriterionTrigger.Listener var7;
         while(var6.hasNext()) {
            var7 = (CriterionTrigger.Listener)var6.next();
            if (((FishingRodHookedTrigger.TriggerInstance)var7.getTriggerInstance()).matches(var1, var2, var3, var4)) {
               if (var5 == null) {
                  var5 = Lists.newArrayList();
               }

               var5.add(var7);
            }
         }

         if (var5 != null) {
            var6 = var5.iterator();

            while(var6.hasNext()) {
               var7 = (CriterionTrigger.Listener)var6.next();
               var7.run(this.player);
            }
         }

      }
   }

   public static class TriggerInstance extends AbstractCriterionTriggerInstance {
      private final ItemPredicate rod;
      private final EntityPredicate entity;
      private final ItemPredicate item;

      public TriggerInstance(ItemPredicate var1, EntityPredicate var2, ItemPredicate var3) {
         super(FishingRodHookedTrigger.ID);
         this.rod = var1;
         this.entity = var2;
         this.item = var3;
      }

      public static FishingRodHookedTrigger.TriggerInstance fishedItem(ItemPredicate var0, EntityPredicate var1, ItemPredicate var2) {
         return new FishingRodHookedTrigger.TriggerInstance(var0, var1, var2);
      }

      public boolean matches(ServerPlayer var1, ItemStack var2, FishingHook var3, Collection<ItemStack> var4) {
         if (!this.rod.matches(var2)) {
            return false;
         } else if (!this.entity.matches(var1, var3.hookedIn)) {
            return false;
         } else {
            if (this.item != ItemPredicate.ANY) {
               boolean var5 = false;
               if (var3.hookedIn instanceof ItemEntity) {
                  ItemEntity var6 = (ItemEntity)var3.hookedIn;
                  if (this.item.matches(var6.getItem())) {
                     var5 = true;
                  }
               }

               Iterator var8 = var4.iterator();

               while(var8.hasNext()) {
                  ItemStack var7 = (ItemStack)var8.next();
                  if (this.item.matches(var7)) {
                     var5 = true;
                     break;
                  }
               }

               if (!var5) {
                  return false;
               }
            }

            return true;
         }
      }

      public JsonElement serializeToJson() {
         JsonObject var1 = new JsonObject();
         var1.add("rod", this.rod.serializeToJson());
         var1.add("entity", this.entity.serializeToJson());
         var1.add("item", this.item.serializeToJson());
         return var1;
      }
   }
}
