package net.minecraft.advancements.critereon;

import com.google.gson.JsonObject;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.crafting.Recipe;

public class RecipeUnlockedTrigger extends SimpleCriterionTrigger<RecipeUnlockedTrigger.TriggerInstance> {
   static final ResourceLocation ID = new ResourceLocation("recipe_unlocked");

   public RecipeUnlockedTrigger() {
      super();
   }

   @Override
   public ResourceLocation getId() {
      return ID;
   }

   public RecipeUnlockedTrigger.TriggerInstance createInstance(JsonObject var1, EntityPredicate.Composite var2, DeserializationContext var3) {
      ResourceLocation var4 = new ResourceLocation(GsonHelper.getAsString(var1, "recipe"));
      return new RecipeUnlockedTrigger.TriggerInstance(var2, var4);
   }

   public void trigger(ServerPlayer var1, Recipe<?> var2) {
      this.trigger(var1, var1x -> var1x.matches(var2));
   }

   public static RecipeUnlockedTrigger.TriggerInstance unlocked(ResourceLocation var0) {
      return new RecipeUnlockedTrigger.TriggerInstance(EntityPredicate.Composite.ANY, var0);
   }

   public static class TriggerInstance extends AbstractCriterionTriggerInstance {
      private final ResourceLocation recipe;

      public TriggerInstance(EntityPredicate.Composite var1, ResourceLocation var2) {
         super(RecipeUnlockedTrigger.ID, var1);
         this.recipe = var2;
      }

      @Override
      public JsonObject serializeToJson(SerializationContext var1) {
         JsonObject var2 = super.serializeToJson(var1);
         var2.addProperty("recipe", this.recipe.toString());
         return var2;
      }

      public boolean matches(Recipe<?> var1) {
         return this.recipe.equals(var1.getId());
      }
   }
}
