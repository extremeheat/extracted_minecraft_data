package net.minecraft.advancements.critereon;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import net.minecraft.advancements.CriterionTrigger;
import net.minecraft.advancements.CriterionTriggerInstance;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.PlayerAdvancements;
import net.minecraft.server.level.ServerPlayer;

public class UsedEnderEyeTrigger implements CriterionTrigger<UsedEnderEyeTrigger.TriggerInstance> {
   private static final ResourceLocation ID = new ResourceLocation("used_ender_eye");
   private final Map<PlayerAdvancements, UsedEnderEyeTrigger.PlayerListeners> players = Maps.newHashMap();

   public UsedEnderEyeTrigger() {
      super();
   }

   public ResourceLocation getId() {
      return ID;
   }

   public void addPlayerListener(PlayerAdvancements var1, CriterionTrigger.Listener<UsedEnderEyeTrigger.TriggerInstance> var2) {
      UsedEnderEyeTrigger.PlayerListeners var3 = (UsedEnderEyeTrigger.PlayerListeners)this.players.get(var1);
      if (var3 == null) {
         var3 = new UsedEnderEyeTrigger.PlayerListeners(var1);
         this.players.put(var1, var3);
      }

      var3.addListener(var2);
   }

   public void removePlayerListener(PlayerAdvancements var1, CriterionTrigger.Listener<UsedEnderEyeTrigger.TriggerInstance> var2) {
      UsedEnderEyeTrigger.PlayerListeners var3 = (UsedEnderEyeTrigger.PlayerListeners)this.players.get(var1);
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

   public UsedEnderEyeTrigger.TriggerInstance createInstance(JsonObject var1, JsonDeserializationContext var2) {
      MinMaxBounds.Floats var3 = MinMaxBounds.Floats.fromJson(var1.get("distance"));
      return new UsedEnderEyeTrigger.TriggerInstance(var3);
   }

   public void trigger(ServerPlayer var1, BlockPos var2) {
      UsedEnderEyeTrigger.PlayerListeners var3 = (UsedEnderEyeTrigger.PlayerListeners)this.players.get(var1.getAdvancements());
      if (var3 != null) {
         double var4 = var1.x - (double)var2.getX();
         double var6 = var1.z - (double)var2.getZ();
         var3.trigger(var4 * var4 + var6 * var6);
      }

   }

   // $FF: synthetic method
   public CriterionTriggerInstance createInstance(JsonObject var1, JsonDeserializationContext var2) {
      return this.createInstance(var1, var2);
   }

   static class PlayerListeners {
      private final PlayerAdvancements player;
      private final Set<CriterionTrigger.Listener<UsedEnderEyeTrigger.TriggerInstance>> listeners = Sets.newHashSet();

      public PlayerListeners(PlayerAdvancements var1) {
         super();
         this.player = var1;
      }

      public boolean isEmpty() {
         return this.listeners.isEmpty();
      }

      public void addListener(CriterionTrigger.Listener<UsedEnderEyeTrigger.TriggerInstance> var1) {
         this.listeners.add(var1);
      }

      public void removeListener(CriterionTrigger.Listener<UsedEnderEyeTrigger.TriggerInstance> var1) {
         this.listeners.remove(var1);
      }

      public void trigger(double var1) {
         ArrayList var3 = null;
         Iterator var4 = this.listeners.iterator();

         CriterionTrigger.Listener var5;
         while(var4.hasNext()) {
            var5 = (CriterionTrigger.Listener)var4.next();
            if (((UsedEnderEyeTrigger.TriggerInstance)var5.getTriggerInstance()).matches(var1)) {
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
      private final MinMaxBounds.Floats level;

      public TriggerInstance(MinMaxBounds.Floats var1) {
         super(UsedEnderEyeTrigger.ID);
         this.level = var1;
      }

      public boolean matches(double var1) {
         return this.level.matchesSqr(var1);
      }
   }
}
