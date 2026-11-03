package net.minecraft.client.gui.narration;

import java.util.Collection;
import java.util.List;
import net.minecraft.client.gui.components.TabOrderedElement;

public interface NarratableEntry extends NarrationSupplier, TabOrderedElement {
   NarrationPriority narrationPriority();

   default boolean isActive() {
      return true;
   }

   default Collection<? extends NarratableEntry> getNarratables() {
      return List.of(this);
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

      public static NarrationPriority of(final boolean focused, final boolean hovered) {
         return focused ? FOCUSED : (hovered ? HOVERED : NONE);
      }

      // $FF: synthetic method
      private static NarrationPriority[] $values() {
         return new NarrationPriority[]{NONE, HOVERED, FOCUSED};
      }
   }
}
