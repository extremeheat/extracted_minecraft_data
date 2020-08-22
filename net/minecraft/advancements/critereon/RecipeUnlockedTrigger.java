package net.minecraft.advancements.critereon;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.advancements.CriterionTriggerInstance;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.crafting.Recipe;

public class RecipeUnlockedTrigger extends SimpleCriterionTrigger {
   private static final ResourceLocation ID = new ResourceLocation("recipe_unlocked");

   public ResourceLocation getId() {
      return ID;
   }

   public RecipeUnlockedTrigger.TriggerInstance createInstance(JsonObject var1, JsonDeserializationContext var2) {
      ResourceLocation var3 = new ResourceLocation(GsonHelper.getAsString(var1, "recipe"));
      return new RecipeUnlockedTrigger.TriggerInstance(var3);
   }

   public void trigger(ServerPlayer var1, Recipe var2) {
      this.trigger(var1.getAdvancements(), (var1x) -> {
         return var1x.matches(var2);
      });
   }

   // $FF: synthetic method
   public CriterionTriggerInstance createInstance(JsonObject var1, JsonDeserializationContext var2) {
      return this.createInstance(var1, var2);
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

      public boolean matches(Recipe var1) {
         return this.recipe.equals(var1.getId());
      }
   }
}
