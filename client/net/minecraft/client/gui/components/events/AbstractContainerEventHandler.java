package net.minecraft.client.gui.components.events;

import javax.annotation.Nullable;

public abstract class AbstractContainerEventHandler implements ContainerEventHandler {
   @Nullable
   private GuiEventListener focused;
   private boolean isDragging;

   public AbstractContainerEventHandler() {
      super();
   }

   public final boolean isDragging() {
      return this.isDragging;
   }

   public final void setDragging(boolean var1) {
      this.isDragging = var1;
   }

   @Nullable
   public GuiEventListener getFocused() {
      return this.focused;
   }

   public void setFocused(@Nullable GuiEventListener var1) {
      if (this.focused != null) {
         this.focused.setFocused(false);
      }

      if (var1 != null) {
         var1.setFocused(true);
      }

      this.focused = var1;
   }
}
