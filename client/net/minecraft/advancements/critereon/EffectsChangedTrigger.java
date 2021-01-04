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
import net.minecraft.world.entity.LivingEntity;

public class EffectsChangedTrigger implements CriterionTrigger<EffectsChangedTrigger.TriggerInstance> {
   private static final ResourceLocation ID = new ResourceLocation("effects_changed");
   private final Map<PlayerAdvancements, EffectsChangedTrigger.PlayerListeners> players = Maps.newHashMap();

   public EffectsChangedTrigger() {
      super();
   }

   public ResourceLocation getId() {
      return ID;
   }

   public void addPlayerListener(PlayerAdvancements var1, CriterionTrigger.Listener<EffectsChangedTrigger.TriggerInstance> var2) {
      EffectsChangedTrigger.PlayerListeners var3 = (EffectsChangedTrigger.PlayerListeners)this.players.get(var1);
      if (var3 == null) {
         var3 = new EffectsChangedTrigger.PlayerListeners(var1);
         this.players.put(var1, var3);
      }

      var3.addListener(var2);
   }

   public void removePlayerListener(PlayerAdvancements var1, CriterionTrigger.Listener<EffectsChangedTrigger.TriggerInstance> var2) {
      EffectsChangedTrigger.PlayerListeners var3 = (EffectsChangedTrigger.PlayerListeners)this.players.get(var1);
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

   public EffectsChangedTrigger.TriggerInstance createInstance(JsonObject var1, JsonDeserializationContext var2) {
      MobEffectsPredicate var3 = MobEffectsPredicate.fromJson(var1.get("effects"));
      return new EffectsChangedTrigger.TriggerInstance(var3);
   }

   public void trigger(ServerPlayer var1) {
      EffectsChangedTrigger.PlayerListeners var2 = (EffectsChangedTrigger.PlayerListeners)this.players.get(var1.getAdvancements());
      if (var2 != null) {
         var2.trigger(var1);
      }

   }

   // $FF: synthetic method
   public CriterionTriggerInstance createInstance(JsonObject var1, JsonDeserializationContext var2) {
      return this.createInstance(var1, var2);
   }

   static class PlayerListeners {
      private final PlayerAdvancements player;
      private final Set<CriterionTrigger.Listener<EffectsChangedTrigger.TriggerInstance>> listeners = Sets.newHashSet();

      public PlayerListeners(PlayerAdvancements var1) {
         super();
         this.player = var1;
      }

      public boolean isEmpty() {
         return this.listeners.isEmpty();
      }

      public void addListener(CriterionTrigger.Listener<EffectsChangedTrigger.TriggerInstance> var1) {
         this.listeners.add(var1);
      }

      public void removeListener(CriterionTrigger.Listener<EffectsChangedTrigger.TriggerInstance> var1) {
         this.listeners.remove(var1);
      }

      public void trigger(ServerPlayer var1) {
         ArrayList var2 = null;
         Iterator var3 = this.listeners.iterator();

         CriterionTrigger.Listener var4;
         while(var3.hasNext()) {
            var4 = (CriterionTrigger.Listener)var3.next();
            if (((EffectsChangedTrigger.TriggerInstance)var4.getTriggerInstance()).matches(var1)) {
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
      private final MobEffectsPredicate effects;

      public TriggerInstance(MobEffectsPredicate var1) {
         super(EffectsChangedTrigger.ID);
         this.effects = var1;
      }

      public static EffectsChangedTrigger.TriggerInstance hasEffects(MobEffectsPredicate var0) {
         return new EffectsChangedTrigger.TriggerInstance(var0);
      }

      public boolean matches(ServerPlayer var1) {
         return this.effects.matches((LivingEntity)var1);
      }

      public JsonElement serializeToJson() {
         JsonObject var1 = new JsonObject();
         var1.add("effects", this.effects.serializeToJson());
         return var1;
      }
   }
}
