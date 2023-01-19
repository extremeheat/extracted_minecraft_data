package net.minecraft.client.gui.components.events;

import javax.annotation.Nullable;
import net.minecraft.client.gui.GuiComponent;

public abstract class AbstractContainerEventHandler extends GuiComponent implements ContainerEventHandler {
   @Nullable
   private GuiEventListener focused;
   private boolean isDragging;

   public AbstractContainerEventHandler() {
      super();
   }

   @Override
   public final boolean isDragging() {
      return this.isDragging;
   }

   @Override
   public final void setDragging(boolean var1) {
      this.isDragging = var1;
   }

   @Nullable
   @Override
   public GuiEventListener getFocused() {
      return this.focused;
   }

   @Override
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
