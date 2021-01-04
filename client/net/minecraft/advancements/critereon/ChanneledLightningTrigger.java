package net.minecraft.advancements.critereon;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import net.minecraft.advancements.CriterionTrigger;
import net.minecraft.advancements.CriterionTriggerInstance;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.PlayerAdvancements;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;

public class ChanneledLightningTrigger implements CriterionTrigger<ChanneledLightningTrigger.TriggerInstance> {
   private static final ResourceLocation ID = new ResourceLocation("channeled_lightning");
   private final Map<PlayerAdvancements, ChanneledLightningTrigger.PlayerListeners> players = Maps.newHashMap();

   public ChanneledLightningTrigger() {
      super();
   }

   public ResourceLocation getId() {
      return ID;
   }

   public void addPlayerListener(PlayerAdvancements var1, CriterionTrigger.Listener<ChanneledLightningTrigger.TriggerInstance> var2) {
      ChanneledLightningTrigger.PlayerListeners var3 = (ChanneledLightningTrigger.PlayerListeners)this.players.get(var1);
      if (var3 == null) {
         var3 = new ChanneledLightningTrigger.PlayerListeners(var1);
         this.players.put(var1, var3);
      }

      var3.addListener(var2);
   }

   public void removePlayerListener(PlayerAdvancements var1, CriterionTrigger.Listener<ChanneledLightningTrigger.TriggerInstance> var2) {
      ChanneledLightningTrigger.PlayerListeners var3 = (ChanneledLightningTrigger.PlayerListeners)this.players.get(var1);
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

   public ChanneledLightningTrigger.TriggerInstance createInstance(JsonObject var1, JsonDeserializationContext var2) {
      EntityPredicate[] var3 = EntityPredicate.fromJsonArray(var1.get("victims"));
      return new ChanneledLightningTrigger.TriggerInstance(var3);
   }

   public void trigger(ServerPlayer var1, Collection<? extends Entity> var2) {
      ChanneledLightningTrigger.PlayerListeners var3 = (ChanneledLightningTrigger.PlayerListeners)this.players.get(var1.getAdvancements());
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
      private final Set<CriterionTrigger.Listener<ChanneledLightningTrigger.TriggerInstance>> listeners = Sets.newHashSet();

      public PlayerListeners(PlayerAdvancements var1) {
         super();
         this.player = var1;
      }

      public boolean isEmpty() {
         return this.listeners.isEmpty();
      }

      public void addListener(CriterionTrigger.Listener<ChanneledLightningTrigger.TriggerInstance> var1) {
         this.listeners.add(var1);
      }

      public void removeListener(CriterionTrigger.Listener<ChanneledLightningTrigger.TriggerInstance> var1) {
         this.listeners.remove(var1);
      }

      public void trigger(ServerPlayer var1, Collection<? extends Entity> var2) {
         ArrayList var3 = null;
         Iterator var4 = this.listeners.iterator();

         CriterionTrigger.Listener var5;
         while(var4.hasNext()) {
            var5 = (CriterionTrigger.Listener)var4.next();
            if (((ChanneledLightningTrigger.TriggerInstance)var5.getTriggerInstance()).matches(var1, var2)) {
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
      private final EntityPredicate[] victims;

      public TriggerInstance(EntityPredicate[] var1) {
         super(ChanneledLightningTrigger.ID);
         this.victims = var1;
      }

      public static ChanneledLightningTrigger.TriggerInstance channeledLightning(EntityPredicate... var0) {
         return new ChanneledLightningTrigger.TriggerInstance(var0);
      }

      public boolean matches(ServerPlayer var1, Collection<? extends Entity> var2) {
         EntityPredicate[] var3 = this.victims;
         int var4 = var3.length;

         for(int var5 = 0; var5 < var4; ++var5) {
            EntityPredicate var6 = var3[var5];
            boolean var7 = false;
            Iterator var8 = var2.iterator();

            while(var8.hasNext()) {
               Entity var9 = (Entity)var8.next();
               if (var6.matches(var1, var9)) {
                  var7 = true;
                  break;
               }
            }

            if (!var7) {
               return false;
            }
         }

         return true;
      }

      public JsonElement serializeToJson() {
         JsonObject var1 = new JsonObject();
         var1.add("victims", EntityPredicate.serializeArrayToJson(this.victims));
         return var1;
      }
   }
}
