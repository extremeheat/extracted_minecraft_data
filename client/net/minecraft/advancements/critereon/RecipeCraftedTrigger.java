package net.minecraft.advancements.critereon;

import com.google.gson.JsonObject;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.advancements.Criterion;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.ItemStack;

public class RecipeCraftedTrigger extends SimpleCriterionTrigger<RecipeCraftedTrigger.TriggerInstance> {
   public RecipeCraftedTrigger() {
      super();
   }

   protected RecipeCraftedTrigger.TriggerInstance createInstance(JsonObject var1, Optional<ContextAwarePredicate> var2, DeserializationContext var3) {
      ResourceLocation var4 = new ResourceLocation(GsonHelper.getAsString(var1, "recipe_id"));
      List var5 = ItemPredicate.fromJsonArray(var1.get("ingredients"));
      return new RecipeCraftedTrigger.TriggerInstance(var2, var4, var5);
   }

   public void trigger(ServerPlayer var1, ResourceLocation var2, List<ItemStack> var3) {
      this.trigger(var1, var2x -> var2x.matches(var2, var3));
   }

   public static class TriggerInstance extends AbstractCriterionTriggerInstance {
      private final ResourceLocation recipeId;
      private final List<ItemPredicate> predicates;

      public TriggerInstance(Optional<ContextAwarePredicate> var1, ResourceLocation var2, List<ItemPredicate> var3) {
         super(var1);
         this.recipeId = var2;
         this.predicates = var3;
      }

      public static Criterion<RecipeCraftedTrigger.TriggerInstance> craftedItem(ResourceLocation var0, List<ItemPredicate.Builder> var1) {
         return CriteriaTriggers.RECIPE_CRAFTED
            .createCriterion(new RecipeCraftedTrigger.TriggerInstance(Optional.empty(), var0, var1.stream().map(ItemPredicate.Builder::build).toList()));
      }

      public static Criterion<RecipeCraftedTrigger.TriggerInstance> craftedItem(ResourceLocation var0) {
         return CriteriaTriggers.RECIPE_CRAFTED.createCriterion(new RecipeCraftedTrigger.TriggerInstance(Optional.empty(), var0, List.of()));
      }

      boolean matches(ResourceLocation var1, List<ItemStack> var2) {
         if (!var1.equals(this.recipeId)) {
            return false;
         } else {
            ArrayList var3 = new ArrayList(var2);

            for(ItemPredicate var5 : this.predicates) {
               boolean var6 = false;
               Iterator var7 = var3.iterator();

               while(var7.hasNext()) {
                  if (var5.matches((ItemStack)var7.next())) {
                     var7.remove();
                     var6 = true;
                     break;
                  }
               }

               if (!var6) {
                  return false;
               }
            }

            return true;
         }
      }

      @Override
      public JsonObject serializeToJson() {
         JsonObject var1 = super.serializeToJson();
         var1.addProperty("recipe_id", this.recipeId.toString());
         if (!this.predicates.isEmpty()) {
            var1.add("ingredients", ItemPredicate.serializeToJsonArray(this.predicates));
         }

         return var1;
      }
   }
}
