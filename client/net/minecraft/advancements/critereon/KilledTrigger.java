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
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;

public class KilledTrigger implements CriterionTrigger<KilledTrigger.TriggerInstance> {
   private final Map<PlayerAdvancements, KilledTrigger.PlayerListeners> players = Maps.newHashMap();
   private final ResourceLocation id;

   public KilledTrigger(ResourceLocation var1) {
      super();
      this.id = var1;
   }

   public ResourceLocation getId() {
      return this.id;
   }

   public void addPlayerListener(PlayerAdvancements var1, CriterionTrigger.Listener<KilledTrigger.TriggerInstance> var2) {
      KilledTrigger.PlayerListeners var3 = (KilledTrigger.PlayerListeners)this.players.get(var1);
      if (var3 == null) {
         var3 = new KilledTrigger.PlayerListeners(var1);
         this.players.put(var1, var3);
      }

      var3.addListener(var2);
   }

   public void removePlayerListener(PlayerAdvancements var1, CriterionTrigger.Listener<KilledTrigger.TriggerInstance> var2) {
      KilledTrigger.PlayerListeners var3 = (KilledTrigger.PlayerListeners)this.players.get(var1);
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

   public KilledTrigger.TriggerInstance createInstance(JsonObject var1, JsonDeserializationContext var2) {
      return new KilledTrigger.TriggerInstance(this.id, EntityPredicate.fromJson(var1.get("entity")), DamageSourcePredicate.fromJson(var1.get("killing_blow")));
   }

   public void trigger(ServerPlayer var1, Entity var2, DamageSource var3) {
      KilledTrigger.PlayerListeners var4 = (KilledTrigger.PlayerListeners)this.players.get(var1.getAdvancements());
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
      private final Set<CriterionTrigger.Listener<KilledTrigger.TriggerInstance>> listeners = Sets.newHashSet();

      public PlayerListeners(PlayerAdvancements var1) {
         super();
         this.player = var1;
      }

      public boolean isEmpty() {
         return this.listeners.isEmpty();
      }

      public void addListener(CriterionTrigger.Listener<KilledTrigger.TriggerInstance> var1) {
         this.listeners.add(var1);
      }

      public void removeListener(CriterionTrigger.Listener<KilledTrigger.TriggerInstance> var1) {
         this.listeners.remove(var1);
      }

      public void trigger(ServerPlayer var1, Entity var2, DamageSource var3) {
         ArrayList var4 = null;
         Iterator var5 = this.listeners.iterator();

         CriterionTrigger.Listener var6;
         while(var5.hasNext()) {
            var6 = (CriterionTrigger.Listener)var5.next();
            if (((KilledTrigger.TriggerInstance)var6.getTriggerInstance()).matches(var1, var2, var3)) {
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
      private final EntityPredicate entityPredicate;
      private final DamageSourcePredicate killingBlow;

      public TriggerInstance(ResourceLocation var1, EntityPredicate var2, DamageSourcePredicate var3) {
         super(var1);
         this.entityPredicate = var2;
         this.killingBlow = var3;
      }

      public static KilledTrigger.TriggerInstance playerKilledEntity(EntityPredicate.Builder var0) {
         return new KilledTrigger.TriggerInstance(CriteriaTriggers.PLAYER_KILLED_ENTITY.id, var0.build(), DamageSourcePredicate.ANY);
      }

      public static KilledTrigger.TriggerInstance playerKilledEntity() {
         return new KilledTrigger.TriggerInstance(CriteriaTriggers.PLAYER_KILLED_ENTITY.id, EntityPredicate.ANY, DamageSourcePredicate.ANY);
      }

      public static KilledTrigger.TriggerInstance playerKilledEntity(EntityPredicate.Builder var0, DamageSourcePredicate.Builder var1) {
         return new KilledTrigger.TriggerInstance(CriteriaTriggers.PLAYER_KILLED_ENTITY.id, var0.build(), var1.build());
      }

      public static KilledTrigger.TriggerInstance entityKilledPlayer() {
         return new KilledTrigger.TriggerInstance(CriteriaTriggers.ENTITY_KILLED_PLAYER.id, EntityPredicate.ANY, DamageSourcePredicate.ANY);
      }

      public boolean matches(ServerPlayer var1, Entity var2, DamageSource var3) {
         return !this.killingBlow.matches(var1, var3) ? false : this.entityPredicate.matches(var1, var2);
      }

      public JsonElement serializeToJson() {
         JsonObject var1 = new JsonObject();
         var1.add("entity", this.entityPredicate.serializeToJson());
         var1.add("killing_blow", this.killingBlow.serializeToJson());
         return var1;
      }
   }
}
