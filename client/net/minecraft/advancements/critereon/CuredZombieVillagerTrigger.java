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
import net.minecraft.world.entity.monster.Zombie;
import net.minecraft.world.entity.npc.Villager;

public class CuredZombieVillagerTrigger implements CriterionTrigger<CuredZombieVillagerTrigger.TriggerInstance> {
   private static final ResourceLocation ID = new ResourceLocation("cured_zombie_villager");
   private final Map<PlayerAdvancements, CuredZombieVillagerTrigger.PlayerListeners> players = Maps.newHashMap();

   public CuredZombieVillagerTrigger() {
      super();
   }

   public ResourceLocation getId() {
      return ID;
   }

   public void addPlayerListener(PlayerAdvancements var1, CriterionTrigger.Listener<CuredZombieVillagerTrigger.TriggerInstance> var2) {
      CuredZombieVillagerTrigger.PlayerListeners var3 = (CuredZombieVillagerTrigger.PlayerListeners)this.players.get(var1);
      if (var3 == null) {
         var3 = new CuredZombieVillagerTrigger.PlayerListeners(var1);
         this.players.put(var1, var3);
      }

      var3.addListener(var2);
   }

   public void removePlayerListener(PlayerAdvancements var1, CriterionTrigger.Listener<CuredZombieVillagerTrigger.TriggerInstance> var2) {
      CuredZombieVillagerTrigger.PlayerListeners var3 = (CuredZombieVillagerTrigger.PlayerListeners)this.players.get(var1);
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

   public CuredZombieVillagerTrigger.TriggerInstance createInstance(JsonObject var1, JsonDeserializationContext var2) {
      EntityPredicate var3 = EntityPredicate.fromJson(var1.get("zombie"));
      EntityPredicate var4 = EntityPredicate.fromJson(var1.get("villager"));
      return new CuredZombieVillagerTrigger.TriggerInstance(var3, var4);
   }

   public void trigger(ServerPlayer var1, Zombie var2, Villager var3) {
      CuredZombieVillagerTrigger.PlayerListeners var4 = (CuredZombieVillagerTrigger.PlayerListeners)this.players.get(var1.getAdvancements());
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
      private final Set<CriterionTrigger.Listener<CuredZombieVillagerTrigger.TriggerInstance>> listeners = Sets.newHashSet();

      public PlayerListeners(PlayerAdvancements var1) {
         super();
         this.player = var1;
      }

      public boolean isEmpty() {
         return this.listeners.isEmpty();
      }

      public void addListener(CriterionTrigger.Listener<CuredZombieVillagerTrigger.TriggerInstance> var1) {
         this.listeners.add(var1);
      }

      public void removeListener(CriterionTrigger.Listener<CuredZombieVillagerTrigger.TriggerInstance> var1) {
         this.listeners.remove(var1);
      }

      public void trigger(ServerPlayer var1, Zombie var2, Villager var3) {
         ArrayList var4 = null;
         Iterator var5 = this.listeners.iterator();

         CriterionTrigger.Listener var6;
         while(var5.hasNext()) {
            var6 = (CriterionTrigger.Listener)var5.next();
            if (((CuredZombieVillagerTrigger.TriggerInstance)var6.getTriggerInstance()).matches(var1, var2, var3)) {
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
      private final EntityPredicate zombie;
      private final EntityPredicate villager;

      public TriggerInstance(EntityPredicate var1, EntityPredicate var2) {
         super(CuredZombieVillagerTrigger.ID);
         this.zombie = var1;
         this.villager = var2;
      }

      public static CuredZombieVillagerTrigger.TriggerInstance curedZombieVillager() {
         return new CuredZombieVillagerTrigger.TriggerInstance(EntityPredicate.ANY, EntityPredicate.ANY);
      }

      public boolean matches(ServerPlayer var1, Zombie var2, Villager var3) {
         if (!this.zombie.matches(var1, var2)) {
            return false;
         } else {
            return this.villager.matches(var1, var3);
         }
      }

      public JsonElement serializeToJson() {
         JsonObject var1 = new JsonObject();
         var1.add("zombie", this.zombie.serializeToJson());
         var1.add("villager", this.villager.serializeToJson());
         return var1;
      }
   }
}
