package net.minecraft.advancements.critereon;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Optional;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.advancements.Criterion;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;

public class RecipeUnlockedTrigger extends SimpleCriterionTrigger<TriggerInstance> {
   public RecipeUnlockedTrigger() {
      super();
   }

   public Codec<TriggerInstance> codec() {
      return RecipeUnlockedTrigger.TriggerInstance.CODEC;
   }

   public void trigger(ServerPlayer var1, RecipeHolder<?> var2) {
      this.trigger(var1, (var1x) -> var1x.matches(var2));
   }

   public static Criterion<TriggerInstance> unlocked(ResourceKey<Recipe<?>> var0) {
      return CriteriaTriggers.RECIPE_UNLOCKED.createCriterion(new TriggerInstance(Optional.empty(), var0));
   }

   public static record TriggerInstance(Optional<ContextAwarePredicate> player, ResourceKey<Recipe<?>> recipe) implements SimpleCriterionTrigger.SimpleInstance {
      public static final Codec<TriggerInstance> CODEC = RecordCodecBuilder.create((var0) -> var0.group(EntityPredicate.ADVANCEMENT_CODEC.optionalFieldOf("player").forGetter(TriggerInstance::player), ResourceKey.codec(Registries.RECIPE).fieldOf("recipe").forGetter(TriggerInstance::recipe)).apply(var0, TriggerInstance::new));

      public TriggerInstance(Optional<ContextAwarePredicate> var1, ResourceKey<Recipe<?>> var2) {
         super();
         this.player = var1;
         this.recipe = var2;
      }

      public boolean matches(RecipeHolder<?> var1) {
         return this.recipe == var1.id();
      }
   }
}
