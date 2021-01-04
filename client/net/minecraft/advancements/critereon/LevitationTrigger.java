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
import net.minecraft.world.phys.Vec3;

public class LevitationTrigger implements CriterionTrigger<LevitationTrigger.TriggerInstance> {
   private static final ResourceLocation ID = new ResourceLocation("levitation");
   private final Map<PlayerAdvancements, LevitationTrigger.PlayerListeners> players = Maps.newHashMap();

   public LevitationTrigger() {
      super();
   }

   public ResourceLocation getId() {
      return ID;
   }

   public void addPlayerListener(PlayerAdvancements var1, CriterionTrigger.Listener<LevitationTrigger.TriggerInstance> var2) {
      LevitationTrigger.PlayerListeners var3 = (LevitationTrigger.PlayerListeners)this.players.get(var1);
      if (var3 == null) {
         var3 = new LevitationTrigger.PlayerListeners(var1);
         this.players.put(var1, var3);
      }

      var3.addListener(var2);
   }

   public void removePlayerListener(PlayerAdvancements var1, CriterionTrigger.Listener<LevitationTrigger.TriggerInstance> var2) {
      LevitationTrigger.PlayerListeners var3 = (LevitationTrigger.PlayerListeners)this.players.get(var1);
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

   public LevitationTrigger.TriggerInstance createInstance(JsonObject var1, JsonDeserializationContext var2) {
      DistancePredicate var3 = DistancePredicate.fromJson(var1.get("distance"));
      MinMaxBounds.Ints var4 = MinMaxBounds.Ints.fromJson(var1.get("duration"));
      return new LevitationTrigger.TriggerInstance(var3, var4);
   }

   public void trigger(ServerPlayer var1, Vec3 var2, int var3) {
      LevitationTrigger.PlayerListeners var4 = (LevitationTrigger.PlayerListeners)this.players.get(var1.getAdvancements());
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
      private final Set<CriterionTrigger.Listener<LevitationTrigger.TriggerInstance>> listeners = Sets.newHashSet();

      public PlayerListeners(PlayerAdvancements var1) {
         super();
         this.player = var1;
      }

      public boolean isEmpty() {
         return this.listeners.isEmpty();
      }

      public void addListener(CriterionTrigger.Listener<LevitationTrigger.TriggerInstance> var1) {
         this.listeners.add(var1);
      }

      public void removeListener(CriterionTrigger.Listener<LevitationTrigger.TriggerInstance> var1) {
         this.listeners.remove(var1);
      }

      public void trigger(ServerPlayer var1, Vec3 var2, int var3) {
         ArrayList var4 = null;
         Iterator var5 = this.listeners.iterator();

         CriterionTrigger.Listener var6;
         while(var5.hasNext()) {
            var6 = (CriterionTrigger.Listener)var5.next();
            if (((LevitationTrigger.TriggerInstance)var6.getTriggerInstance()).matches(var1, var2, var3)) {
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
      private final DistancePredicate distance;
      private final MinMaxBounds.Ints duration;

      public TriggerInstance(DistancePredicate var1, MinMaxBounds.Ints var2) {
         super(LevitationTrigger.ID);
         this.distance = var1;
         this.duration = var2;
      }

      public static LevitationTrigger.TriggerInstance levitated(DistancePredicate var0) {
         return new LevitationTrigger.TriggerInstance(var0, MinMaxBounds.Ints.ANY);
      }

      public boolean matches(ServerPlayer var1, Vec3 var2, int var3) {
         if (!this.distance.matches(var2.x, var2.y, var2.z, var1.x, var1.y, var1.z)) {
            return false;
         } else {
            return this.duration.matches(var3);
         }
      }

      public JsonElement serializeToJson() {
         JsonObject var1 = new JsonObject();
         var1.add("distance", this.distance.serializeToJson());
         var1.add("duration", this.duration.serializeToJson());
         return var1;
      }
   }
}
