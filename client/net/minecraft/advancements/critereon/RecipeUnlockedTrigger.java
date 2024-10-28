package net.minecraft.advancements.critereon;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Optional;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.advancements.Criterion;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.crafting.RecipeHolder;

public class RecipeUnlockedTrigger extends SimpleCriterionTrigger<TriggerInstance> {
   public RecipeUnlockedTrigger() {
      super();
   }

   public Codec<TriggerInstance> codec() {
      return RecipeUnlockedTrigger.TriggerInstance.CODEC;
   }

   public void trigger(ServerPlayer var1, RecipeHolder<?> var2) {
      this.trigger(var1, (var1x) -> {
         return var1x.matches(var2);
      });
   }

   public static Criterion<TriggerInstance> unlocked(ResourceLocation var0) {
      return CriteriaTriggers.RECIPE_UNLOCKED.createCriterion(new TriggerInstance(Optional.empty(), var0));
   }

   public static record TriggerInstance(Optional<ContextAwarePredicate> player, ResourceLocation recipe) implements SimpleCriterionTrigger.SimpleInstance {
      public static final Codec<TriggerInstance> CODEC = RecordCodecBuilder.create((var0) -> {
         return var0.group(EntityPredicate.ADVANCEMENT_CODEC.optionalFieldOf("player").forGetter(TriggerInstance::player), ResourceLocation.CODEC.fieldOf("recipe").forGetter(TriggerInstance::recipe)).apply(var0, TriggerInstance::new);
      });

      public TriggerInstance(Optional<ContextAwarePredicate> var1, ResourceLocation var2) {
         super();
         this.player = var1;
         this.recipe = var2;
      }

      public boolean matches(RecipeHolder<?> var1) {
         return this.recipe.equals(var1.id());
      }

      public Optional<ContextAwarePredicate> player() {
         return this.player;
      }

      public ResourceLocation recipe() {
         return this.recipe;
      }
   }
}
