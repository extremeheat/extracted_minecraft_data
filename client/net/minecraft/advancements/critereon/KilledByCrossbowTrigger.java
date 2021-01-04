package net.minecraft.advancements.critereon;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import net.minecraft.advancements.CriterionTrigger;
import net.minecraft.advancements.CriterionTriggerInstance;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.PlayerAdvancements;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;

public class KilledByCrossbowTrigger implements CriterionTrigger<KilledByCrossbowTrigger.TriggerInstance> {
   private static final ResourceLocation ID = new ResourceLocation("killed_by_crossbow");
   private final Map<PlayerAdvancements, KilledByCrossbowTrigger.PlayerListeners> players = Maps.newHashMap();

   public KilledByCrossbowTrigger() {
      super();
   }

   public ResourceLocation getId() {
      return ID;
   }

   public void addPlayerListener(PlayerAdvancements var1, CriterionTrigger.Listener<KilledByCrossbowTrigger.TriggerInstance> var2) {
      KilledByCrossbowTrigger.PlayerListeners var3 = (KilledByCrossbowTrigger.PlayerListeners)this.players.get(var1);
      if (var3 == null) {
         var3 = new KilledByCrossbowTrigger.PlayerListeners(var1);
         this.players.put(var1, var3);
      }

      var3.addListener(var2);
   }

   public void removePlayerListener(PlayerAdvancements var1, CriterionTrigger.Listener<KilledByCrossbowTrigger.TriggerInstance> var2) {
      KilledByCrossbowTrigger.PlayerListeners var3 = (KilledByCrossbowTrigger.PlayerListeners)this.players.get(var1);
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

   public KilledByCrossbowTrigger.TriggerInstance createInstance(JsonObject var1, JsonDeserializationContext var2) {
      EntityPredicate[] var3 = EntityPredicate.fromJsonArray(var1.get("victims"));
      MinMaxBounds.Ints var4 = MinMaxBounds.Ints.fromJson(var1.get("unique_entity_types"));
      return new KilledByCrossbowTrigger.TriggerInstance(var3, var4);
   }

   public void trigger(ServerPlayer var1, Collection<Entity> var2, int var3) {
      KilledByCrossbowTrigger.PlayerListeners var4 = (KilledByCrossbowTrigger.PlayerListeners)this.players.get(var1.getAdvancements());
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
      private final Set<CriterionTrigger.Listener<KilledByCrossbowTrigger.TriggerInstance>> listeners = Sets.newHashSet();

      public PlayerListeners(PlayerAdvancements var1) {
         super();
         this.player = var1;
      }

      public boolean isEmpty() {
         return this.listeners.isEmpty();
      }

      public void addListener(CriterionTrigger.Listener<KilledByCrossbowTrigger.TriggerInstance> var1) {
         this.listeners.add(var1);
      }

      public void removeListener(CriterionTrigger.Listener<KilledByCrossbowTrigger.TriggerInstance> var1) {
         this.listeners.remove(var1);
      }

      public void trigger(ServerPlayer var1, Collection<Entity> var2, int var3) {
         ArrayList var4 = null;
         Iterator var5 = this.listeners.iterator();

         CriterionTrigger.Listener var6;
         while(var5.hasNext()) {
            var6 = (CriterionTrigger.Listener)var5.next();
            if (((KilledByCrossbowTrigger.TriggerInstance)var6.getTriggerInstance()).matches(var1, var2, var3)) {
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
      private final EntityPredicate[] victims;
      private final MinMaxBounds.Ints uniqueEntityTypes;

      public TriggerInstance(EntityPredicate[] var1, MinMaxBounds.Ints var2) {
         super(KilledByCrossbowTrigger.ID);
         this.victims = var1;
         this.uniqueEntityTypes = var2;
      }

      public static KilledByCrossbowTrigger.TriggerInstance crossbowKilled(EntityPredicate.Builder... var0) {
         EntityPredicate[] var1 = new EntityPredicate[var0.length];

         for(int var2 = 0; var2 < var0.length; ++var2) {
            EntityPredicate.Builder var3 = var0[var2];
            var1[var2] = var3.build();
         }

         return new KilledByCrossbowTrigger.TriggerInstance(var1, MinMaxBounds.Ints.ANY);
      }

      public static KilledByCrossbowTrigger.TriggerInstance crossbowKilled(MinMaxBounds.Ints var0) {
         EntityPredicate[] var1 = new EntityPredicate[0];
         return new KilledByCrossbowTrigger.TriggerInstance(var1, var0);
      }

      public boolean matches(ServerPlayer var1, Collection<Entity> var2, int var3) {
         if (this.victims.length > 0) {
            ArrayList var4 = Lists.newArrayList(var2);
            EntityPredicate[] var5 = this.victims;
            int var6 = var5.length;

            for(int var7 = 0; var7 < var6; ++var7) {
               EntityPredicate var8 = var5[var7];
               boolean var9 = false;
               Iterator var10 = var4.iterator();

               while(var10.hasNext()) {
                  Entity var11 = (Entity)var10.next();
                  if (var8.matches(var1, var11)) {
                     var10.remove();
                     var9 = true;
                     break;
                  }
               }

               if (!var9) {
                  return false;
               }
            }
         }

         if (this.uniqueEntityTypes == MinMaxBounds.Ints.ANY) {
            return true;
         } else {
            HashSet var12 = Sets.newHashSet();
            Iterator var13 = var2.iterator();

            while(var13.hasNext()) {
               Entity var14 = (Entity)var13.next();
               var12.add(var14.getType());
            }

            return this.uniqueEntityTypes.matches(var12.size()) && this.uniqueEntityTypes.matches(var3);
         }
      }

      public JsonElement serializeToJson() {
         JsonObject var1 = new JsonObject();
         var1.add("victims", EntityPredicate.serializeArrayToJson(this.victims));
         var1.add("unique_entity_types", this.uniqueEntityTypes.serializeToJson());
         return var1;
      }
   }
}
