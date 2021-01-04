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
import javax.annotation.Nullable;
import net.minecraft.advancements.CriterionTrigger;
import net.minecraft.advancements.CriterionTriggerInstance;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.PlayerAdvancements;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.AgableMob;
import net.minecraft.world.entity.animal.Animal;

public class BredAnimalsTrigger implements CriterionTrigger<BredAnimalsTrigger.TriggerInstance> {
   private static final ResourceLocation ID = new ResourceLocation("bred_animals");
   private final Map<PlayerAdvancements, BredAnimalsTrigger.PlayerListeners> players = Maps.newHashMap();

   public BredAnimalsTrigger() {
      super();
   }

   public ResourceLocation getId() {
      return ID;
   }

   public void addPlayerListener(PlayerAdvancements var1, CriterionTrigger.Listener<BredAnimalsTrigger.TriggerInstance> var2) {
      BredAnimalsTrigger.PlayerListeners var3 = (BredAnimalsTrigger.PlayerListeners)this.players.get(var1);
      if (var3 == null) {
         var3 = new BredAnimalsTrigger.PlayerListeners(var1);
         this.players.put(var1, var3);
      }

      var3.addListener(var2);
   }

   public void removePlayerListener(PlayerAdvancements var1, CriterionTrigger.Listener<BredAnimalsTrigger.TriggerInstance> var2) {
      BredAnimalsTrigger.PlayerListeners var3 = (BredAnimalsTrigger.PlayerListeners)this.players.get(var1);
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

   public BredAnimalsTrigger.TriggerInstance createInstance(JsonObject var1, JsonDeserializationContext var2) {
      EntityPredicate var3 = EntityPredicate.fromJson(var1.get("parent"));
      EntityPredicate var4 = EntityPredicate.fromJson(var1.get("partner"));
      EntityPredicate var5 = EntityPredicate.fromJson(var1.get("child"));
      return new BredAnimalsTrigger.TriggerInstance(var3, var4, var5);
   }

   public void trigger(ServerPlayer var1, Animal var2, @Nullable Animal var3, @Nullable AgableMob var4) {
      BredAnimalsTrigger.PlayerListeners var5 = (BredAnimalsTrigger.PlayerListeners)this.players.get(var1.getAdvancements());
      if (var5 != null) {
         var5.trigger(var1, var2, var3, var4);
      }

   }

   // $FF: synthetic method
   public CriterionTriggerInstance createInstance(JsonObject var1, JsonDeserializationContext var2) {
      return this.createInstance(var1, var2);
   }

   static class PlayerListeners {
      private final PlayerAdvancements player;
      private final Set<CriterionTrigger.Listener<BredAnimalsTrigger.TriggerInstance>> listeners = Sets.newHashSet();

      public PlayerListeners(PlayerAdvancements var1) {
         super();
         this.player = var1;
      }

      public boolean isEmpty() {
         return this.listeners.isEmpty();
      }

      public void addListener(CriterionTrigger.Listener<BredAnimalsTrigger.TriggerInstance> var1) {
         this.listeners.add(var1);
      }

      public void removeListener(CriterionTrigger.Listener<BredAnimalsTrigger.TriggerInstance> var1) {
         this.listeners.remove(var1);
      }

      public void trigger(ServerPlayer var1, Animal var2, @Nullable Animal var3, @Nullable AgableMob var4) {
         ArrayList var5 = null;
         Iterator var6 = this.listeners.iterator();

         CriterionTrigger.Listener var7;
         while(var6.hasNext()) {
            var7 = (CriterionTrigger.Listener)var6.next();
            if (((BredAnimalsTrigger.TriggerInstance)var7.getTriggerInstance()).matches(var1, var2, var3, var4)) {
               if (var5 == null) {
                  var5 = Lists.newArrayList();
               }

               var5.add(var7);
            }
         }

         if (var5 != null) {
            var6 = var5.iterator();

            while(var6.hasNext()) {
               var7 = (CriterionTrigger.Listener)var6.next();
               var7.run(this.player);
            }
         }

      }
   }

   public static class TriggerInstance extends AbstractCriterionTriggerInstance {
      private final EntityPredicate parent;
      private final EntityPredicate partner;
      private final EntityPredicate child;

      public TriggerInstance(EntityPredicate var1, EntityPredicate var2, EntityPredicate var3) {
         super(BredAnimalsTrigger.ID);
         this.parent = var1;
         this.partner = var2;
         this.child = var3;
      }

      public static BredAnimalsTrigger.TriggerInstance bredAnimals() {
         return new BredAnimalsTrigger.TriggerInstance(EntityPredicate.ANY, EntityPredicate.ANY, EntityPredicate.ANY);
      }

      public static BredAnimalsTrigger.TriggerInstance bredAnimals(EntityPredicate.Builder var0) {
         return new BredAnimalsTrigger.TriggerInstance(var0.build(), EntityPredicate.ANY, EntityPredicate.ANY);
      }

      public boolean matches(ServerPlayer var1, Animal var2, @Nullable Animal var3, @Nullable AgableMob var4) {
         if (!this.child.matches(var1, var4)) {
            return false;
         } else {
            return this.parent.matches(var1, var2) && this.partner.matches(var1, var3) || this.parent.matches(var1, var3) && this.partner.matches(var1, var2);
         }
      }

      public JsonElement serializeToJson() {
         JsonObject var1 = new JsonObject();
         var1.add("parent", this.parent.serializeToJson());
         var1.add("partner", this.partner.serializeToJson());
         var1.add("child", this.child.serializeToJson());
         return var1;
      }
   }
}
