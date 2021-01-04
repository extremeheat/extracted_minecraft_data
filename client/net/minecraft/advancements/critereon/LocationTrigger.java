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
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.advancements.CriterionTrigger;
import net.minecraft.advancements.CriterionTriggerInstance;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.PlayerAdvancements;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;

public class LocationTrigger implements CriterionTrigger<LocationTrigger.TriggerInstance> {
   private final ResourceLocation id;
   private final Map<PlayerAdvancements, LocationTrigger.PlayerListeners> players = Maps.newHashMap();

   public LocationTrigger(ResourceLocation var1) {
      super();
      this.id = var1;
   }

   public ResourceLocation getId() {
      return this.id;
   }

   public void addPlayerListener(PlayerAdvancements var1, CriterionTrigger.Listener<LocationTrigger.TriggerInstance> var2) {
      LocationTrigger.PlayerListeners var3 = (LocationTrigger.PlayerListeners)this.players.get(var1);
      if (var3 == null) {
         var3 = new LocationTrigger.PlayerListeners(var1);
         this.players.put(var1, var3);
      }

      var3.addListener(var2);
   }

   public void removePlayerListener(PlayerAdvancements var1, CriterionTrigger.Listener<LocationTrigger.TriggerInstance> var2) {
      LocationTrigger.PlayerListeners var3 = (LocationTrigger.PlayerListeners)this.players.get(var1);
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

   public LocationTrigger.TriggerInstance createInstance(JsonObject var1, JsonDeserializationContext var2) {
      LocationPredicate var3 = LocationPredicate.fromJson(var1);
      return new LocationTrigger.TriggerInstance(this.id, var3);
   }

   public void trigger(ServerPlayer var1) {
      LocationTrigger.PlayerListeners var2 = (LocationTrigger.PlayerListeners)this.players.get(var1.getAdvancements());
      if (var2 != null) {
         var2.trigger(var1.getLevel(), var1.x, var1.y, var1.z);
      }

   }

   // $FF: synthetic method
   public CriterionTriggerInstance createInstance(JsonObject var1, JsonDeserializationContext var2) {
      return this.createInstance(var1, var2);
   }

   static class PlayerListeners {
      private final PlayerAdvancements player;
      private final Set<CriterionTrigger.Listener<LocationTrigger.TriggerInstance>> listeners = Sets.newHashSet();

      public PlayerListeners(PlayerAdvancements var1) {
         super();
         this.player = var1;
      }

      public boolean isEmpty() {
         return this.listeners.isEmpty();
      }

      public void addListener(CriterionTrigger.Listener<LocationTrigger.TriggerInstance> var1) {
         this.listeners.add(var1);
      }

      public void removeListener(CriterionTrigger.Listener<LocationTrigger.TriggerInstance> var1) {
         this.listeners.remove(var1);
      }

      public void trigger(ServerLevel var1, double var2, double var4, double var6) {
         ArrayList var8 = null;
         Iterator var9 = this.listeners.iterator();

         CriterionTrigger.Listener var10;
         while(var9.hasNext()) {
            var10 = (CriterionTrigger.Listener)var9.next();
            if (((LocationTrigger.TriggerInstance)var10.getTriggerInstance()).matches(var1, var2, var4, var6)) {
               if (var8 == null) {
                  var8 = Lists.newArrayList();
               }

               var8.add(var10);
            }
         }

         if (var8 != null) {
            var9 = var8.iterator();

            while(var9.hasNext()) {
               var10 = (CriterionTrigger.Listener)var9.next();
               var10.run(this.player);
            }
         }

      }
   }

   public static class TriggerInstance extends AbstractCriterionTriggerInstance {
      private final LocationPredicate location;

      public TriggerInstance(ResourceLocation var1, LocationPredicate var2) {
         super(var1);
         this.location = var2;
      }

      public static LocationTrigger.TriggerInstance located(LocationPredicate var0) {
         return new LocationTrigger.TriggerInstance(CriteriaTriggers.LOCATION.id, var0);
      }

      public static LocationTrigger.TriggerInstance sleptInBed() {
         return new LocationTrigger.TriggerInstance(CriteriaTriggers.SLEPT_IN_BED.id, LocationPredicate.ANY);
      }

      public static LocationTrigger.TriggerInstance raidWon() {
         return new LocationTrigger.TriggerInstance(CriteriaTriggers.RAID_WIN.id, LocationPredicate.ANY);
      }

      public boolean matches(ServerLevel var1, double var2, double var4, double var6) {
         return this.location.matches(var1, var2, var4, var6);
      }

      public JsonElement serializeToJson() {
         return this.location.serializeToJson();
      }
   }
}
