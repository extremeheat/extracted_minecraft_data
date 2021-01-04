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
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.phys.Vec3;

public class NetherTravelTrigger implements CriterionTrigger<NetherTravelTrigger.TriggerInstance> {
   private static final ResourceLocation ID = new ResourceLocation("nether_travel");
   private final Map<PlayerAdvancements, NetherTravelTrigger.PlayerListeners> players = Maps.newHashMap();

   public NetherTravelTrigger() {
      super();
   }

   public ResourceLocation getId() {
      return ID;
   }

   public void addPlayerListener(PlayerAdvancements var1, CriterionTrigger.Listener<NetherTravelTrigger.TriggerInstance> var2) {
      NetherTravelTrigger.PlayerListeners var3 = (NetherTravelTrigger.PlayerListeners)this.players.get(var1);
      if (var3 == null) {
         var3 = new NetherTravelTrigger.PlayerListeners(var1);
         this.players.put(var1, var3);
      }

      var3.addListener(var2);
   }

   public void removePlayerListener(PlayerAdvancements var1, CriterionTrigger.Listener<NetherTravelTrigger.TriggerInstance> var2) {
      NetherTravelTrigger.PlayerListeners var3 = (NetherTravelTrigger.PlayerListeners)this.players.get(var1);
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

   public NetherTravelTrigger.TriggerInstance createInstance(JsonObject var1, JsonDeserializationContext var2) {
      LocationPredicate var3 = LocationPredicate.fromJson(var1.get("entered"));
      LocationPredicate var4 = LocationPredicate.fromJson(var1.get("exited"));
      DistancePredicate var5 = DistancePredicate.fromJson(var1.get("distance"));
      return new NetherTravelTrigger.TriggerInstance(var3, var4, var5);
   }

   public void trigger(ServerPlayer var1, Vec3 var2) {
      NetherTravelTrigger.PlayerListeners var3 = (NetherTravelTrigger.PlayerListeners)this.players.get(var1.getAdvancements());
      if (var3 != null) {
         var3.trigger(var1.getLevel(), var2, var1.x, var1.y, var1.z);
      }

   }

   // $FF: synthetic method
   public CriterionTriggerInstance createInstance(JsonObject var1, JsonDeserializationContext var2) {
      return this.createInstance(var1, var2);
   }

   static class PlayerListeners {
      private final PlayerAdvancements player;
      private final Set<CriterionTrigger.Listener<NetherTravelTrigger.TriggerInstance>> listeners = Sets.newHashSet();

      public PlayerListeners(PlayerAdvancements var1) {
         super();
         this.player = var1;
      }

      public boolean isEmpty() {
         return this.listeners.isEmpty();
      }

      public void addListener(CriterionTrigger.Listener<NetherTravelTrigger.TriggerInstance> var1) {
         this.listeners.add(var1);
      }

      public void removeListener(CriterionTrigger.Listener<NetherTravelTrigger.TriggerInstance> var1) {
         this.listeners.remove(var1);
      }

      public void trigger(ServerLevel var1, Vec3 var2, double var3, double var5, double var7) {
         ArrayList var9 = null;
         Iterator var10 = this.listeners.iterator();

         CriterionTrigger.Listener var11;
         while(var10.hasNext()) {
            var11 = (CriterionTrigger.Listener)var10.next();
            if (((NetherTravelTrigger.TriggerInstance)var11.getTriggerInstance()).matches(var1, var2, var3, var5, var7)) {
               if (var9 == null) {
                  var9 = Lists.newArrayList();
               }

               var9.add(var11);
            }
         }

         if (var9 != null) {
            var10 = var9.iterator();

            while(var10.hasNext()) {
               var11 = (CriterionTrigger.Listener)var10.next();
               var11.run(this.player);
            }
         }

      }
   }

   public static class TriggerInstance extends AbstractCriterionTriggerInstance {
      private final LocationPredicate entered;
      private final LocationPredicate exited;
      private final DistancePredicate distance;

      public TriggerInstance(LocationPredicate var1, LocationPredicate var2, DistancePredicate var3) {
         super(NetherTravelTrigger.ID);
         this.entered = var1;
         this.exited = var2;
         this.distance = var3;
      }

      public static NetherTravelTrigger.TriggerInstance travelledThroughNether(DistancePredicate var0) {
         return new NetherTravelTrigger.TriggerInstance(LocationPredicate.ANY, LocationPredicate.ANY, var0);
      }

      public boolean matches(ServerLevel var1, Vec3 var2, double var3, double var5, double var7) {
         if (!this.entered.matches(var1, var2.x, var2.y, var2.z)) {
            return false;
         } else if (!this.exited.matches(var1, var3, var5, var7)) {
            return false;
         } else {
            return this.distance.matches(var2.x, var2.y, var2.z, var3, var5, var7);
         }
      }

      public JsonElement serializeToJson() {
         JsonObject var1 = new JsonObject();
         var1.add("entered", this.entered.serializeToJson());
         var1.add("exited", this.exited.serializeToJson());
         var1.add("distance", this.distance.serializeToJson());
         return var1;
      }
   }
}
