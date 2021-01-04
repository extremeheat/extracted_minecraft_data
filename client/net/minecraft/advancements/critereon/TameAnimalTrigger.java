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
import net.minecraft.world.entity.animal.Animal;

public class TameAnimalTrigger implements CriterionTrigger<TameAnimalTrigger.TriggerInstance> {
   private static final ResourceLocation ID = new ResourceLocation("tame_animal");
   private final Map<PlayerAdvancements, TameAnimalTrigger.PlayerListeners> players = Maps.newHashMap();

   public TameAnimalTrigger() {
      super();
   }

   public ResourceLocation getId() {
      return ID;
   }

   public void addPlayerListener(PlayerAdvancements var1, CriterionTrigger.Listener<TameAnimalTrigger.TriggerInstance> var2) {
      TameAnimalTrigger.PlayerListeners var3 = (TameAnimalTrigger.PlayerListeners)this.players.get(var1);
      if (var3 == null) {
         var3 = new TameAnimalTrigger.PlayerListeners(var1);
         this.players.put(var1, var3);
      }

      var3.addListener(var2);
   }

   public void removePlayerListener(PlayerAdvancements var1, CriterionTrigger.Listener<TameAnimalTrigger.TriggerInstance> var2) {
      TameAnimalTrigger.PlayerListeners var3 = (TameAnimalTrigger.PlayerListeners)this.players.get(var1);
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

   public TameAnimalTrigger.TriggerInstance createInstance(JsonObject var1, JsonDeserializationContext var2) {
      EntityPredicate var3 = EntityPredicate.fromJson(var1.get("entity"));
      return new TameAnimalTrigger.TriggerInstance(var3);
   }

   public void trigger(ServerPlayer var1, Animal var2) {
      TameAnimalTrigger.PlayerListeners var3 = (TameAnimalTrigger.PlayerListeners)this.players.get(var1.getAdvancements());
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
      private final Set<CriterionTrigger.Listener<TameAnimalTrigger.TriggerInstance>> listeners = Sets.newHashSet();

      public PlayerListeners(PlayerAdvancements var1) {
         super();
         this.player = var1;
      }

      public boolean isEmpty() {
         return this.listeners.isEmpty();
      }

      public void addListener(CriterionTrigger.Listener<TameAnimalTrigger.TriggerInstance> var1) {
         this.listeners.add(var1);
      }

      public void removeListener(CriterionTrigger.Listener<TameAnimalTrigger.TriggerInstance> var1) {
         this.listeners.remove(var1);
      }

      public void trigger(ServerPlayer var1, Animal var2) {
         ArrayList var3 = null;
         Iterator var4 = this.listeners.iterator();

         CriterionTrigger.Listener var5;
         while(var4.hasNext()) {
            var5 = (CriterionTrigger.Listener)var4.next();
            if (((TameAnimalTrigger.TriggerInstance)var5.getTriggerInstance()).matches(var1, var2)) {
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
         super(TameAnimalTrigger.ID);
         this.entity = var1;
      }

      public static TameAnimalTrigger.TriggerInstance tamedAnimal() {
         return new TameAnimalTrigger.TriggerInstance(EntityPredicate.ANY);
      }

      public static TameAnimalTrigger.TriggerInstance tamedAnimal(EntityPredicate var0) {
         return new TameAnimalTrigger.TriggerInstance(var0);
      }

      public boolean matches(ServerPlayer var1, Animal var2) {
         return this.entity.matches(var1, var2);
      }

      public JsonElement serializeToJson() {
         JsonObject var1 = new JsonObject();
         var1.add("entity", this.entity.serializeToJson());
         return var1;
      }
   }
}
