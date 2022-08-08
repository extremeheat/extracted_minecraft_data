package net.minecraft.client.gui.narration;

public interface NarratableEntry extends NarrationSupplier {
   NarrationPriority narrationPriority();

   default boolean isActive() {
      return true;
   }

   public static enum NarrationPriority {
      NONE,
      HOVERED,
      FOCUSED;

      private NarrationPriority() {
      }

      public boolean isTerminal() {
         return this == FOCUSED;
      }

      // $FF: synthetic method
      private static NarrationPriority[] $values() {
         return new NarrationPriority[]{NONE, HOVERED, FOCUSED};
      }
   }
}
