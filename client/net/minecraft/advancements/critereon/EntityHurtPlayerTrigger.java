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

public class EntityHurtPlayerTrigger implements CriterionTrigger<EntityHurtPlayerTrigger.TriggerInstance> {
   private static final ResourceLocation ID = new ResourceLocation("entity_hurt_player");
   private final Map<PlayerAdvancements, EntityHurtPlayerTrigger.PlayerListeners> players = Maps.newHashMap();

   public EntityHurtPlayerTrigger() {
      super();
   }

   public ResourceLocation getId() {
      return ID;
   }

   public void addPlayerListener(PlayerAdvancements var1, CriterionTrigger.Listener<EntityHurtPlayerTrigger.TriggerInstance> var2) {
      EntityHurtPlayerTrigger.PlayerListeners var3 = (EntityHurtPlayerTrigger.PlayerListeners)this.players.get(var1);
      if (var3 == null) {
         var3 = new EntityHurtPlayerTrigger.PlayerListeners(var1);
         this.players.put(var1, var3);
      }

      var3.addListener(var2);
   }

   public void removePlayerListener(PlayerAdvancements var1, CriterionTrigger.Listener<EntityHurtPlayerTrigger.TriggerInstance> var2) {
      EntityHurtPlayerTrigger.PlayerListeners var3 = (EntityHurtPlayerTrigger.PlayerListeners)this.players.get(var1);
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

   public EntityHurtPlayerTrigger.TriggerInstance createInstance(JsonObject var1, JsonDeserializationContext var2) {
      DamagePredicate var3 = DamagePredicate.fromJson(var1.get("damage"));
      return new EntityHurtPlayerTrigger.TriggerInstance(var3);
   }

   public void trigger(ServerPlayer var1, DamageSource var2, float var3, float var4, boolean var5) {
      EntityHurtPlayerTrigger.PlayerListeners var6 = (EntityHurtPlayerTrigger.PlayerListeners)this.players.get(var1.getAdvancements());
      if (var6 != null) {
         var6.trigger(var1, var2, var3, var4, var5);
      }

   }

   // $FF: synthetic method
   public CriterionTriggerInstance createInstance(JsonObject var1, JsonDeserializationContext var2) {
      return this.createInstance(var1, var2);
   }

   static class PlayerListeners {
      private final PlayerAdvancements player;
      private final Set<CriterionTrigger.Listener<EntityHurtPlayerTrigger.TriggerInstance>> listeners = Sets.newHashSet();

      public PlayerListeners(PlayerAdvancements var1) {
         super();
         this.player = var1;
      }

      public boolean isEmpty() {
         return this.listeners.isEmpty();
      }

      public void addListener(CriterionTrigger.Listener<EntityHurtPlayerTrigger.TriggerInstance> var1) {
         this.listeners.add(var1);
      }

      public void removeListener(CriterionTrigger.Listener<EntityHurtPlayerTrigger.TriggerInstance> var1) {
         this.listeners.remove(var1);
      }

      public void trigger(ServerPlayer var1, DamageSource var2, float var3, float var4, boolean var5) {
         ArrayList var6 = null;
         Iterator var7 = this.listeners.iterator();

         CriterionTrigger.Listener var8;
         while(var7.hasNext()) {
            var8 = (CriterionTrigger.Listener)var7.next();
            if (((EntityHurtPlayerTrigger.TriggerInstance)var8.getTriggerInstance()).matches(var1, var2, var3, var4, var5)) {
               if (var6 == null) {
                  var6 = Lists.newArrayList();
               }

               var6.add(var8);
            }
         }

         if (var6 != null) {
            var7 = var6.iterator();

            while(var7.hasNext()) {
               var8 = (CriterionTrigger.Listener)var7.next();
               var8.run(this.player);
            }
         }

      }
   }

   public static class TriggerInstance extends AbstractCriterionTriggerInstance {
      private final DamagePredicate damage;

      public TriggerInstance(DamagePredicate var1) {
         super(EntityHurtPlayerTrigger.ID);
         this.damage = var1;
      }

      public static EntityHurtPlayerTrigger.TriggerInstance entityHurtPlayer(DamagePredicate.Builder var0) {
         return new EntityHurtPlayerTrigger.TriggerInstance(var0.build());
      }

      public boolean matches(ServerPlayer var1, DamageSource var2, float var3, float var4, boolean var5) {
         return this.damage.matches(var1, var2, var3, var4, var5);
      }

      public JsonElement serializeToJson() {
         JsonObject var1 = new JsonObject();
         var1.add("damage", this.damage.serializeToJson());
         return var1;
      }
   }
}
