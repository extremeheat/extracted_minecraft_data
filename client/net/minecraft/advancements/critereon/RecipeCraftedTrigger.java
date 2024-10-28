package net.minecraft.advancements.critereon;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.advancements.Criterion;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;

public class RecipeCraftedTrigger extends SimpleCriterionTrigger<TriggerInstance> {
   public RecipeCraftedTrigger() {
      super();
   }

   public Codec<TriggerInstance> codec() {
      return RecipeCraftedTrigger.TriggerInstance.CODEC;
   }

   public void trigger(ServerPlayer var1, ResourceLocation var2, List<ItemStack> var3) {
      this.trigger(var1, (var2x) -> {
         return var2x.matches(var2, var3);
      });
   }

   public static record TriggerInstance(Optional<ContextAwarePredicate> player, ResourceLocation recipeId, List<ItemPredicate> ingredients) implements SimpleCriterionTrigger.SimpleInstance {
      public static final Codec<TriggerInstance> CODEC = RecordCodecBuilder.create((var0) -> {
         return var0.group(EntityPredicate.ADVANCEMENT_CODEC.optionalFieldOf("player").forGetter(TriggerInstance::player), ResourceLocation.CODEC.fieldOf("recipe_id").forGetter(TriggerInstance::recipeId), ItemPredicate.CODEC.listOf().optionalFieldOf("ingredients", List.of()).forGetter(TriggerInstance::ingredients)).apply(var0, TriggerInstance::new);
      });

      public TriggerInstance(Optional<ContextAwarePredicate> player, ResourceLocation recipeId, List<ItemPredicate> ingredients) {
         super();
         this.player = player;
         this.recipeId = recipeId;
         this.ingredients = ingredients;
      }

      public static Criterion<TriggerInstance> craftedItem(ResourceLocation var0, List<ItemPredicate.Builder> var1) {
         return CriteriaTriggers.RECIPE_CRAFTED.createCriterion(new TriggerInstance(Optional.empty(), var0, var1.stream().map(ItemPredicate.Builder::build).toList()));
      }

      public static Criterion<TriggerInstance> craftedItem(ResourceLocation var0) {
         return CriteriaTriggers.RECIPE_CRAFTED.createCriterion(new TriggerInstance(Optional.empty(), var0, List.of()));
      }

      public static Criterion<TriggerInstance> crafterCraftedItem(ResourceLocation var0) {
         return CriteriaTriggers.CRAFTER_RECIPE_CRAFTED.createCriterion(new TriggerInstance(Optional.empty(), var0, List.of()));
      }

      boolean matches(ResourceLocation var1, List<ItemStack> var2) {
         if (!var1.equals(this.recipeId)) {
            return false;
         } else {
            ArrayList var3 = new ArrayList(var2);
            Iterator var4 = this.ingredients.iterator();

            boolean var6;
            do {
               if (!var4.hasNext()) {
                  return true;
               }

               ItemPredicate var5 = (ItemPredicate)var4.next();
               var6 = false;
               Iterator var7 = var3.iterator();

               while(var7.hasNext()) {
                  if (var5.test((ItemStack)var7.next())) {
                     var7.remove();
                     var6 = true;
                     break;
                  }
               }
            } while(var6);

            return false;
         }
      }

      public Optional<ContextAwarePredicate> player() {
         return this.player;
      }

      public ResourceLocation recipeId() {
         return this.recipeId;
      }

      public List<ItemPredicate> ingredients() {
         return this.ingredients;
      }
   }
}
