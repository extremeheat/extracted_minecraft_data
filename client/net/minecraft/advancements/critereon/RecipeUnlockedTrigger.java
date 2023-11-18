package net.minecraft.advancements.critereon;

import com.google.gson.JsonObject;
import java.util.Optional;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.advancements.Criterion;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.crafting.RecipeHolder;

public class RecipeUnlockedTrigger extends SimpleCriterionTrigger<RecipeUnlockedTrigger.TriggerInstance> {
   public RecipeUnlockedTrigger() {
      super();
   }

   public RecipeUnlockedTrigger.TriggerInstance createInstance(JsonObject var1, Optional<ContextAwarePredicate> var2, DeserializationContext var3) {
      ResourceLocation var4 = new ResourceLocation(GsonHelper.getAsString(var1, "recipe"));
      return new RecipeUnlockedTrigger.TriggerInstance(var2, var4);
   }

   public void trigger(ServerPlayer var1, RecipeHolder<?> var2) {
      this.trigger(var1, var1x -> var1x.matches(var2));
   }

   public static Criterion<RecipeUnlockedTrigger.TriggerInstance> unlocked(ResourceLocation var0) {
      return CriteriaTriggers.RECIPE_UNLOCKED.createCriterion(new RecipeUnlockedTrigger.TriggerInstance(Optional.empty(), var0));
   }

   public static class TriggerInstance extends AbstractCriterionTriggerInstance {
      private final ResourceLocation recipe;

      public TriggerInstance(Optional<ContextAwarePredicate> var1, ResourceLocation var2) {
         super(var1);
         this.recipe = var2;
      }

      @Override
      public JsonObject serializeToJson() {
         JsonObject var1 = super.serializeToJson();
         var1.addProperty("recipe", this.recipe.toString());
         return var1;
      }

      public boolean matches(RecipeHolder<?> var1) {
         return this.recipe.equals(var1.id());
      }
   }
}
