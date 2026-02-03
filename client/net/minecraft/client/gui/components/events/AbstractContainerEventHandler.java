package net.minecraft.client.gui.components.events;

import org.jspecify.annotations.Nullable;

public abstract class AbstractContainerEventHandler implements ContainerEventHandler {
   private @Nullable GuiEventListener focused;
   private boolean isDragging;

   public AbstractContainerEventHandler() {
      super();
   }

   public final boolean isDragging() {
      return this.isDragging;
   }

   public final void setDragging(final boolean dragging) {
      this.isDragging = dragging;
   }

   public @Nullable GuiEventListener getFocused() {
      return this.focused;
   }

   public void setFocused(final @Nullable GuiEventListener focused) {
      if (this.focused != focused) {
         if (this.focused != null) {
            this.focused.setFocused(false);
         }

         if (focused != null) {
            focused.setFocused(true);
         }

         this.focused = focused;
      }
   }
}
