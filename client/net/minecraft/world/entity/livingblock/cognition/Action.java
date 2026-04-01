package net.minecraft.world.entity.livingblock.cognition;

import java.util.function.Function;
import net.minecraft.world.entity.livingblock.LivingBlock;

public record Action<V>(int index, Function<LivingBlock, Intent<V>> intent) {
   private static int nextIndex;

   public Action {
      super();
   }

   public static <V> Action<V> of(final Function<LivingBlock, Intent<V>> intent) {
      return new Action<V>(nextIndex++, intent);
   }
}
