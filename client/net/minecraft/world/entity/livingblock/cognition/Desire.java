package net.minecraft.world.entity.livingblock.cognition;

public record Desire<V>(int index) {
   private static int nextIndex;

   public Desire {
      super();
   }

   public static <V> Desire<V> of() {
      return new Desire<V>(nextIndex++);
   }
}
