package net.minecraft.advancements.critereon;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.mojang.serialization.codecs.RecordCodecBuilder.Instance;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.advancements.Criterion;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;

public class RecipeCraftedTrigger extends SimpleCriterionTrigger<RecipeCraftedTrigger.TriggerInstance> {
   public RecipeCraftedTrigger() {
      super();
   }

   @Override
   public Codec<RecipeCraftedTrigger.TriggerInstance> codec() {
      return RecipeCraftedTrigger.TriggerInstance.CODEC;
   }

   public void trigger(ServerPlayer var1, ResourceLocation var2, List<ItemStack> var3) {
      this.trigger(var1, var2x -> var2x.matches(var2, var3));
   }

   public static record TriggerInstance(Optional<ContextAwarePredicate> b, ResourceLocation c, List<ItemPredicate> d)
      implements SimpleCriterionTrigger.SimpleInstance {
      private final Optional<ContextAwarePredicate> player;
      private final ResourceLocation recipeId;
      private final List<ItemPredicate> ingredients;
      public static final Codec<RecipeCraftedTrigger.TriggerInstance> CODEC = RecordCodecBuilder.create(
         var0 -> var0.group(
                  EntityPredicate.ADVANCEMENT_CODEC.optionalFieldOf("player").forGetter(RecipeCraftedTrigger.TriggerInstance::player),
                  ResourceLocation.CODEC.fieldOf("recipe_id").forGetter(RecipeCraftedTrigger.TriggerInstance::recipeId),
                  ItemPredicate.CODEC.listOf().optionalFieldOf("ingredients", List.of()).forGetter(RecipeCraftedTrigger.TriggerInstance::ingredients)
               )
               .apply(var0, RecipeCraftedTrigger.TriggerInstance::new)
      );

      public TriggerInstance(Optional<ContextAwarePredicate> var1, ResourceLocation var2, List<ItemPredicate> var3) {
         super();
         this.player = var1;
         this.recipeId = var2;
         this.ingredients = var3;
      }

      public static Criterion<RecipeCraftedTrigger.TriggerInstance> craftedItem(ResourceLocation var0, List<ItemPredicate.Builder> var1) {
         return CriteriaTriggers.RECIPE_CRAFTED
            .createCriterion(new RecipeCraftedTrigger.TriggerInstance(Optional.empty(), var0, var1.stream().map(ItemPredicate.Builder::build).toList()));
      }

      public static Criterion<RecipeCraftedTrigger.TriggerInstance> craftedItem(ResourceLocation var0) {
         return CriteriaTriggers.RECIPE_CRAFTED.createCriterion(new RecipeCraftedTrigger.TriggerInstance(Optional.empty(), var0, List.of()));
      }

      public static Criterion<RecipeCraftedTrigger.TriggerInstance> crafterCraftedItem(ResourceLocation var0) {
         return CriteriaTriggers.CRAFTER_RECIPE_CRAFTED.createCriterion(new RecipeCraftedTrigger.TriggerInstance(Optional.empty(), var0, List.of()));
      }

      boolean matches(ResourceLocation var1, List<ItemStack> var2) {
         if (!var1.equals(this.recipeId)) {
            return false;
         } else {
            ArrayList var3 = new ArrayList(var2);

            for(ItemPredicate var5 : this.ingredients) {
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
   }
}
