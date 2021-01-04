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
import net.minecraft.world.level.ItemLike;

public class UsedTotemTrigger implements CriterionTrigger<UsedTotemTrigger.TriggerInstance> {
   private static final ResourceLocation ID = new ResourceLocation("used_totem");
   private final Map<PlayerAdvancements, UsedTotemTrigger.PlayerListeners> players = Maps.newHashMap();

   public UsedTotemTrigger() {
      super();
   }

   public ResourceLocation getId() {
      return ID;
   }

   public void addPlayerListener(PlayerAdvancements var1, CriterionTrigger.Listener<UsedTotemTrigger.TriggerInstance> var2) {
      UsedTotemTrigger.PlayerListeners var3 = (UsedTotemTrigger.PlayerListeners)this.players.get(var1);
      if (var3 == null) {
         var3 = new UsedTotemTrigger.PlayerListeners(var1);
         this.players.put(var1, var3);
      }

      var3.addListener(var2);
   }

   public void removePlayerListener(PlayerAdvancements var1, CriterionTrigger.Listener<UsedTotemTrigger.TriggerInstance> var2) {
      UsedTotemTrigger.PlayerListeners var3 = (UsedTotemTrigger.PlayerListeners)this.players.get(var1);
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

   public UsedTotemTrigger.TriggerInstance createInstance(JsonObject var1, JsonDeserializationContext var2) {
      ItemPredicate var3 = ItemPredicate.fromJson(var1.get("item"));
      return new UsedTotemTrigger.TriggerInstance(var3);
   }

   public void trigger(ServerPlayer var1, ItemStack var2) {
      UsedTotemTrigger.PlayerListeners var3 = (UsedTotemTrigger.PlayerListeners)this.players.get(var1.getAdvancements());
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
      private final Set<CriterionTrigger.Listener<UsedTotemTrigger.TriggerInstance>> listeners = Sets.newHashSet();

      public PlayerListeners(PlayerAdvancements var1) {
         super();
         this.player = var1;
      }

      public boolean isEmpty() {
         return this.listeners.isEmpty();
      }

      public void addListener(CriterionTrigger.Listener<UsedTotemTrigger.TriggerInstance> var1) {
         this.listeners.add(var1);
      }

      public void removeListener(CriterionTrigger.Listener<UsedTotemTrigger.TriggerInstance> var1) {
         this.listeners.remove(var1);
      }

      public void trigger(ItemStack var1) {
         ArrayList var2 = null;
         Iterator var3 = this.listeners.iterator();

         CriterionTrigger.Listener var4;
         while(var3.hasNext()) {
            var4 = (CriterionTrigger.Listener)var3.next();
            if (((UsedTotemTrigger.TriggerInstance)var4.getTriggerInstance()).matches(var1)) {
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
      private final ItemPredicate item;

      public TriggerInstance(ItemPredicate var1) {
         super(UsedTotemTrigger.ID);
         this.item = var1;
      }

      public static UsedTotemTrigger.TriggerInstance usedTotem(ItemLike var0) {
         return new UsedTotemTrigger.TriggerInstance(ItemPredicate.Builder.item().of(var0).build());
      }

      public boolean matches(ItemStack var1) {
         return this.item.matches(var1);
      }

      public JsonElement serializeToJson() {
         JsonObject var1 = new JsonObject();
         var1.add("item", this.item.serializeToJson());
         return var1;
      }
   }
}
