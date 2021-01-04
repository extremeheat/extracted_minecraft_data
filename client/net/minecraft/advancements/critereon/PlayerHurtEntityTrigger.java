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
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;

public class PlayerHurtEntityTrigger implements CriterionTrigger<PlayerHurtEntityTrigger.TriggerInstance> {
   private static final ResourceLocation ID = new ResourceLocation("player_hurt_entity");
   private final Map<PlayerAdvancements, PlayerHurtEntityTrigger.PlayerListeners> players = Maps.newHashMap();

   public PlayerHurtEntityTrigger() {
      super();
   }

   public ResourceLocation getId() {
      return ID;
   }

   public void addPlayerListener(PlayerAdvancements var1, CriterionTrigger.Listener<PlayerHurtEntityTrigger.TriggerInstance> var2) {
      PlayerHurtEntityTrigger.PlayerListeners var3 = (PlayerHurtEntityTrigger.PlayerListeners)this.players.get(var1);
      if (var3 == null) {
         var3 = new PlayerHurtEntityTrigger.PlayerListeners(var1);
         this.players.put(var1, var3);
      }

      var3.addListener(var2);
   }

   public void removePlayerListener(PlayerAdvancements var1, CriterionTrigger.Listener<PlayerHurtEntityTrigger.TriggerInstance> var2) {
      PlayerHurtEntityTrigger.PlayerListeners var3 = (PlayerHurtEntityTrigger.PlayerListeners)this.players.get(var1);
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

   public PlayerHurtEntityTrigger.TriggerInstance createInstance(JsonObject var1, JsonDeserializationContext var2) {
      DamagePredicate var3 = DamagePredicate.fromJson(var1.get("damage"));
      EntityPredicate var4 = EntityPredicate.fromJson(var1.get("entity"));
      return new PlayerHurtEntityTrigger.TriggerInstance(var3, var4);
   }

   public void trigger(ServerPlayer var1, Entity var2, DamageSource var3, float var4, float var5, boolean var6) {
      PlayerHurtEntityTrigger.PlayerListeners var7 = (PlayerHurtEntityTrigger.PlayerListeners)this.players.get(var1.getAdvancements());
      if (var7 != null) {
         var7.trigger(var1, var2, var3, var4, var5, var6);
      }

   }

   // $FF: synthetic method
   public CriterionTriggerInstance createInstance(JsonObject var1, JsonDeserializationContext var2) {
      return this.createInstance(var1, var2);
   }

   static class PlayerListeners {
      private final PlayerAdvancements player;
      private final Set<CriterionTrigger.Listener<PlayerHurtEntityTrigger.TriggerInstance>> listeners = Sets.newHashSet();

      public PlayerListeners(PlayerAdvancements var1) {
         super();
         this.player = var1;
      }

      public boolean isEmpty() {
         return this.listeners.isEmpty();
      }

      public void addListener(CriterionTrigger.Listener<PlayerHurtEntityTrigger.TriggerInstance> var1) {
         this.listeners.add(var1);
      }

      public void removeListener(CriterionTrigger.Listener<PlayerHurtEntityTrigger.TriggerInstance> var1) {
         this.listeners.remove(var1);
      }

      public void trigger(ServerPlayer var1, Entity var2, DamageSource var3, float var4, float var5, boolean var6) {
         ArrayList var7 = null;
         Iterator var8 = this.listeners.iterator();

         CriterionTrigger.Listener var9;
         while(var8.hasNext()) {
            var9 = (CriterionTrigger.Listener)var8.next();
            if (((PlayerHurtEntityTrigger.TriggerInstance)var9.getTriggerInstance()).matches(var1, var2, var3, var4, var5, var6)) {
               if (var7 == null) {
                  var7 = Lists.newArrayList();
               }

               var7.add(var9);
            }
         }

         if (var7 != null) {
            var8 = var7.iterator();

            while(var8.hasNext()) {
               var9 = (CriterionTrigger.Listener)var8.next();
               var9.run(this.player);
            }
         }

      }
   }

   public static class TriggerInstance extends AbstractCriterionTriggerInstance {
      private final DamagePredicate damage;
      private final EntityPredicate entity;

      public TriggerInstance(DamagePredicate var1, EntityPredicate var2) {
         super(PlayerHurtEntityTrigger.ID);
         this.damage = var1;
         this.entity = var2;
      }

      public static PlayerHurtEntityTrigger.TriggerInstance playerHurtEntity(DamagePredicate.Builder var0) {
         return new PlayerHurtEntityTrigger.TriggerInstance(var0.build(), EntityPredicate.ANY);
      }

      public boolean matches(ServerPlayer var1, Entity var2, DamageSource var3, float var4, float var5, boolean var6) {
         if (!this.damage.matches(var1, var3, var4, var5, var6)) {
            return false;
         } else {
            return this.entity.matches(var1, var2);
         }
      }

      public JsonElement serializeToJson() {
         JsonObject var1 = new JsonObject();
         var1.add("damage", this.damage.serializeToJson());
         var1.add("entity", this.entity.serializeToJson());
         return var1;
      }
   }
}
