package net.minecraft.client.renderer;

import java.util.function.Function;
import net.minecraft.world.level.block.state.properties.ChestType;

public record MultiblockChestResources<T>(T single, T left, T right) {
   public MultiblockChestResources {
      super();
   }

   public T select(final ChestType chestType) {
      Object var10000;
      switch (chestType) {
         case SINGLE -> var10000 = this.single;
         case LEFT -> var10000 = this.left;
         case RIGHT -> var10000 = this.right;
         default -> throw new MatchException((String)null, (Throwable)null);
      }

      return (T)var10000;
   }

   public <S> MultiblockChestResources<S> map(final Function<T, S> mapper) {
      return new MultiblockChestResources<S>(mapper.apply(this.single), mapper.apply(this.left), mapper.apply(this.right));
   }
}
