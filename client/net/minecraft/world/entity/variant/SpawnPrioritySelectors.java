package net.minecraft.world.entity.variant;

import com.mojang.serialization.Codec;
import java.util.List;

public record SpawnPrioritySelectors(List<PriorityProvider.Selector<SpawnContext, SpawnCondition>> selectors) {
   public static final SpawnPrioritySelectors EMPTY = new SpawnPrioritySelectors(List.of());
   public static final Codec<SpawnPrioritySelectors> CODEC;

   public SpawnPrioritySelectors(List<PriorityProvider.Selector<SpawnContext, SpawnCondition>> var1) {
      super();
      this.selectors = var1;
   }

   public static SpawnPrioritySelectors single(SpawnCondition var0, int var1) {
      return new SpawnPrioritySelectors(PriorityProvider.single(var0, var1));
   }

   public static SpawnPrioritySelectors fallback(int var0) {
      return new SpawnPrioritySelectors(PriorityProvider.alwaysTrue(var0));
   }

   static {
      CODEC = PriorityProvider.Selector.codec(SpawnCondition.CODEC).listOf().xmap(SpawnPrioritySelectors::new, SpawnPrioritySelectors::selectors);
   }
}
