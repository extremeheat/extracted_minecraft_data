package net.minecraft.world.level.storage.loot.predicates;

import com.google.common.collect.ImmutableSet;
import com.mojang.serialization.MapCodec;
import java.util.Set;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParam;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;

public class ExplosionCondition implements LootItemCondition {
   private static final ExplosionCondition INSTANCE = new ExplosionCondition();
   public static final MapCodec<ExplosionCondition> CODEC = MapCodec.unit(INSTANCE);

   private ExplosionCondition() {
      super();
   }

   @Override
   public LootItemConditionType getType() {
      return LootItemConditions.SURVIVES_EXPLOSION;
   }

   @Override
   public Set<LootContextParam<?>> getReferencedContextParams() {
      return ImmutableSet.of(LootContextParams.EXPLOSION_RADIUS);
   }

   public boolean test(LootContext var1) {
      Float var2 = var1.getParamOrNull(LootContextParams.EXPLOSION_RADIUS);
      if (var2 != null) {
         RandomSource var3 = var1.getRandom();
         float var4 = 1.0F / var2;
         return var3.nextFloat() <= var4;
      } else {
         return true;
      }
   }

   public static LootItemCondition.Builder survivesExplosion() {
      return () -> INSTANCE;
   }
}
