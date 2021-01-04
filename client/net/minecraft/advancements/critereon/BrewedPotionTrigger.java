package net.minecraft.advancements.critereon;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import javax.annotation.Nullable;
import net.minecraft.advancements.CriterionTrigger;
import net.minecraft.advancements.CriterionTriggerInstance;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.PlayerAdvancements;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.alchemy.Potion;

public class BrewedPotionTrigger implements CriterionTrigger<BrewedPotionTrigger.TriggerInstance> {
   private static final ResourceLocation ID = new ResourceLocation("brewed_potion");
   private final Map<PlayerAdvancements, BrewedPotionTrigger.PlayerListeners> players = Maps.newHashMap();

   public BrewedPotionTrigger() {
      super();
   }

   public ResourceLocation getId() {
      return ID;
   }

   public void addPlayerListener(PlayerAdvancements var1, CriterionTrigger.Listener<BrewedPotionTrigger.TriggerInstance> var2) {
      BrewedPotionTrigger.PlayerListeners var3 = (BrewedPotionTrigger.PlayerListeners)this.players.get(var1);
      if (var3 == null) {
         var3 = new BrewedPotionTrigger.PlayerListeners(var1);
         this.players.put(var1, var3);
      }

      var3.addListener(var2);
   }

   public void removePlayerListener(PlayerAdvancements var1, CriterionTrigger.Listener<BrewedPotionTrigger.TriggerInstance> var2) {
      BrewedPotionTrigger.PlayerListeners var3 = (BrewedPotionTrigger.PlayerListeners)this.players.get(var1);
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

   public BrewedPotionTrigger.TriggerInstance createInstance(JsonObject var1, JsonDeserializationContext var2) {
      Potion var3 = null;
      if (var1.has("potion")) {
         ResourceLocation var4 = new ResourceLocation(GsonHelper.getAsString(var1, "potion"));
         var3 = (Potion)Registry.POTION.getOptional(var4).orElseThrow(() -> {
            return new JsonSyntaxException("Unknown potion '" + var4 + "'");
         });
      }

      return new BrewedPotionTrigger.TriggerInstance(var3);
   }

   public void trigger(ServerPlayer var1, Potion var2) {
      BrewedPotionTrigger.PlayerListeners var3 = (BrewedPotionTrigger.PlayerListeners)this.players.get(var1.getAdvancements());
      if (var3 != null) {
         var3.trigger(var2);
      }

   }

   // $FF: synthetic method
   public CriterionTriggerInstance createInstance(JsonObject var1, JsonDeserializationContext var2) {
      return this.createInstance(var1, var2);
   }

   static class PlayerListeners {
      private final PlayerAdvancements player;
      private final Set<CriterionTrigger.Listener<BrewedPotionTrigger.TriggerInstance>> listeners = Sets.newHashSet();

      public PlayerListeners(PlayerAdvancements var1) {
         super();
         this.player = var1;
      }

      public boolean isEmpty() {
         return this.listeners.isEmpty();
      }

      public void addListener(CriterionTrigger.Listener<BrewedPotionTrigger.TriggerInstance> var1) {
         this.listeners.add(var1);
      }

      public void removeListener(CriterionTrigger.Listener<BrewedPotionTrigger.TriggerInstance> var1) {
         this.listeners.remove(var1);
      }

      public void trigger(Potion var1) {
         ArrayList var2 = null;
         Iterator var3 = this.listeners.iterator();

         CriterionTrigger.Listener var4;
         while(var3.hasNext()) {
            var4 = (CriterionTrigger.Listener)var3.next();
            if (((BrewedPotionTrigger.TriggerInstance)var4.getTriggerInstance()).matches(var1)) {
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
      private final Potion potion;

      public TriggerInstance(@Nullable Potion var1) {
         super(BrewedPotionTrigger.ID);
         this.potion = var1;
      }

      public static BrewedPotionTrigger.TriggerInstance brewedPotion() {
         return new BrewedPotionTrigger.TriggerInstance((Potion)null);
      }

      public boolean matches(Potion var1) {
         return this.potion == null || this.potion == var1;
      }

      public JsonElement serializeToJson() {
         JsonObject var1 = new JsonObject();
         if (this.potion != null) {
            var1.addProperty("potion", Registry.POTION.getKey(this.potion).toString());
         }

         return var1;
      }
   }
}
