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
import net.minecraft.world.entity.Entity;

public class SummonedEntityTrigger implements CriterionTrigger<SummonedEntityTrigger.TriggerInstance> {
   private static final ResourceLocation ID = new ResourceLocation("summoned_entity");
   private final Map<PlayerAdvancements, SummonedEntityTrigger.PlayerListeners> players = Maps.newHashMap();

   public SummonedEntityTrigger() {
      super();
   }

   public ResourceLocation getId() {
      return ID;
   }

   public void addPlayerListener(PlayerAdvancements var1, CriterionTrigger.Listener<SummonedEntityTrigger.TriggerInstance> var2) {
      SummonedEntityTrigger.PlayerListeners var3 = (SummonedEntityTrigger.PlayerListeners)this.players.get(var1);
      if (var3 == null) {
         var3 = new SummonedEntityTrigger.PlayerListeners(var1);
         this.players.put(var1, var3);
      }

      var3.addListener(var2);
   }

   public void removePlayerListener(PlayerAdvancements var1, CriterionTrigger.Listener<SummonedEntityTrigger.TriggerInstance> var2) {
      SummonedEntityTrigger.PlayerListeners var3 = (SummonedEntityTrigger.PlayerListeners)this.players.get(var1);
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

   public SummonedEntityTrigger.TriggerInstance createInstance(JsonObject var1, JsonDeserializationContext var2) {
      EntityPredicate var3 = EntityPredicate.fromJson(var1.get("entity"));
      return new SummonedEntityTrigger.TriggerInstance(var3);
   }

   public void trigger(ServerPlayer var1, Entity var2) {
      SummonedEntityTrigger.PlayerListeners var3 = (SummonedEntityTrigger.PlayerListeners)this.players.get(var1.getAdvancements());
      if (var3 != null) {
         var3.trigger(var1, var2);
      }

   }

   // $FF: synthetic method
   public CriterionTriggerInstance createInstance(JsonObject var1, JsonDeserializationContext var2) {
      return this.createInstance(var1, var2);
   }

   static class PlayerListeners {
      private final PlayerAdvancements player;
      private final Set<CriterionTrigger.Listener<SummonedEntityTrigger.TriggerInstance>> listeners = Sets.newHashSet();

      public PlayerListeners(PlayerAdvancements var1) {
         super();
         this.player = var1;
      }

      public boolean isEmpty() {
         return this.listeners.isEmpty();
      }

      public void addListener(CriterionTrigger.Listener<SummonedEntityTrigger.TriggerInstance> var1) {
         this.listeners.add(var1);
      }

      public void removeListener(CriterionTrigger.Listener<SummonedEntityTrigger.TriggerInstance> var1) {
         this.listeners.remove(var1);
      }

      public void trigger(ServerPlayer var1, Entity var2) {
         ArrayList var3 = null;
         Iterator var4 = this.listeners.iterator();

         CriterionTrigger.Listener var5;
         while(var4.hasNext()) {
            var5 = (CriterionTrigger.Listener)var4.next();
            if (((SummonedEntityTrigger.TriggerInstance)var5.getTriggerInstance()).matches(var1, var2)) {
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
      private final EntityPredicate entity;

      public TriggerInstance(EntityPredicate var1) {
         super(SummonedEntityTrigger.ID);
         this.entity = var1;
      }

      public static SummonedEntityTrigger.TriggerInstance summonedEntity(EntityPredicate.Builder var0) {
         return new SummonedEntityTrigger.TriggerInstance(var0.build());
      }

      public boolean matches(ServerPlayer var1, Entity var2) {
         return this.entity.matches(var1, var2);
      }

      public JsonElement serializeToJson() {
         JsonObject var1 = new JsonObject();
         var1.add("entity", this.entity.serializeToJson());
         return var1;
      }
   }
}
