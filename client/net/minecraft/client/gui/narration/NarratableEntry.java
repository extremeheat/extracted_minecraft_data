package net.minecraft.client.gui.narration;

public interface NarratableEntry extends NarrationSupplier {
   NarratableEntry.NarrationPriority narrationPriority();

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
   }
}
