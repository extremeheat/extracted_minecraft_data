package net.minecraft.world.level.storage.loot.predicates;

import com.mojang.serialization.MapCodec;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.ValidationContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import org.jspecify.annotations.Nullable;

public class ExplosionCondition implements LootItemCondition {
   private static final ExplosionCondition INSTANCE = new ExplosionCondition();
   public static final MapCodec<ExplosionCondition> MAP_CODEC;

   private ExplosionCondition() {
      super();
   }

   public MapCodec<ExplosionCondition> codec() {
      return MAP_CODEC;
   }

   public @Nullable Boolean constantValue(final LootItemCondition.ConstantConditionEvaluationContext context) {
      return !context.contextKeySet().allowed().contains(LootContextParams.EXPLOSION_RADIUS) ? true : null;
   }

   public void validate(final ValidationContext context) {
   }

   public boolean test(final LootContext context) {
      Float explosionRadius = (Float)context.getOptional(LootContextParams.EXPLOSION_RADIUS);
      if (explosionRadius != null) {
         RandomSource random = context.getRandom();
         float probability = 1.0F / explosionRadius;
         return random.nextFloat() <= probability;
      } else {
         return true;
      }
   }

   public static LootItemCondition.Builder survivesExplosion() {
      return () -> INSTANCE;
   }

   static {
      MAP_CODEC = MapCodec.unit(INSTANCE);
   }
}
