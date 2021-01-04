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
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.crafting.Recipe;

public class RecipeUnlockedTrigger implements CriterionTrigger<RecipeUnlockedTrigger.TriggerInstance> {
   private static final ResourceLocation ID = new ResourceLocation("recipe_unlocked");
   private final Map<PlayerAdvancements, RecipeUnlockedTrigger.PlayerListeners> players = Maps.newHashMap();

   public RecipeUnlockedTrigger() {
      super();
   }

   public ResourceLocation getId() {
      return ID;
   }

   public void addPlayerListener(PlayerAdvancements var1, CriterionTrigger.Listener<RecipeUnlockedTrigger.TriggerInstance> var2) {
      RecipeUnlockedTrigger.PlayerListeners var3 = (RecipeUnlockedTrigger.PlayerListeners)this.players.get(var1);
      if (var3 == null) {
         var3 = new RecipeUnlockedTrigger.PlayerListeners(var1);
         this.players.put(var1, var3);
      }

      var3.addListener(var2);
   }

   public void removePlayerListener(PlayerAdvancements var1, CriterionTrigger.Listener<RecipeUnlockedTrigger.TriggerInstance> var2) {
      RecipeUnlockedTrigger.PlayerListeners var3 = (RecipeUnlockedTrigger.PlayerListeners)this.players.get(var1);
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

   public RecipeUnlockedTrigger.TriggerInstance createInstance(JsonObject var1, JsonDeserializationContext var2) {
      ResourceLocation var3 = new ResourceLocation(GsonHelper.getAsString(var1, "recipe"));
      return new RecipeUnlockedTrigger.TriggerInstance(var3);
   }

   public void trigger(ServerPlayer var1, Recipe<?> var2) {
      RecipeUnlockedTrigger.PlayerListeners var3 = (RecipeUnlockedTrigger.PlayerListeners)this.players.get(var1.getAdvancements());
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
      private final Set<CriterionTrigger.Listener<RecipeUnlockedTrigger.TriggerInstance>> listeners = Sets.newHashSet();

      public PlayerListeners(PlayerAdvancements var1) {
         super();
         this.player = var1;
      }

      public boolean isEmpty() {
         return this.listeners.isEmpty();
      }

      public void addListener(CriterionTrigger.Listener<RecipeUnlockedTrigger.TriggerInstance> var1) {
         this.listeners.add(var1);
      }

      public void removeListener(CriterionTrigger.Listener<RecipeUnlockedTrigger.TriggerInstance> var1) {
         this.listeners.remove(var1);
      }

      public void trigger(Recipe<?> var1) {
         ArrayList var2 = null;
         Iterator var3 = this.listeners.iterator();

         CriterionTrigger.Listener var4;
         while(var3.hasNext()) {
            var4 = (CriterionTrigger.Listener)var3.next();
            if (((RecipeUnlockedTrigger.TriggerInstance)var4.getTriggerInstance()).matches(var1)) {
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
      private final ResourceLocation recipe;

      public TriggerInstance(ResourceLocation var1) {
         super(RecipeUnlockedTrigger.ID);
         this.recipe = var1;
      }

      public JsonElement serializeToJson() {
         JsonObject var1 = new JsonObject();
         var1.addProperty("recipe", this.recipe.toString());
         return var1;
      }

      public boolean matches(Recipe<?> var1) {
         return this.recipe.equals(var1.getId());
      }
   }
}
