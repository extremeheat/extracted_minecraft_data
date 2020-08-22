package net.minecraft.client.gui.components.events;

import javax.annotation.Nullable;
import net.minecraft.client.gui.GuiComponent;

public abstract class AbstractContainerEventHandler extends GuiComponent implements ContainerEventHandler {
   @Nullable
   private GuiEventListener focused;
   private boolean isDragging;

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
      this.focused = var1;
   }
}
