package net.minecraft.client.gui.components;

import javax.annotation.Nullable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.events.ContainerEventHandler;
import net.minecraft.client.gui.components.events.GuiEventListener;

public abstract class ContainerObjectSelectionList<E extends ContainerObjectSelectionList.Entry<E>> extends AbstractSelectionList<E> {
   public ContainerObjectSelectionList(Minecraft var1, int var2, int var3, int var4, int var5, int var6) {
      super(var1, var2, var3, var4, var5, var6);
   }

   public boolean changeFocus(boolean var1) {
      boolean var2 = super.changeFocus(var1);
      if (var2) {
         this.ensureVisible(this.getFocused());
      }

      return var2;
   }

   protected boolean isSelectedItem(int var1) {
      return false;
   }

   public abstract static class Entry<E extends ContainerObjectSelectionList.Entry<E>> extends AbstractSelectionList.Entry<E> implements ContainerEventHandler {
      @Nullable
      private GuiEventListener focused;
      private boolean dragging;

      public Entry() {
         super();
      }

      public boolean isDragging() {
         return this.dragging;
      }

      public void setDragging(boolean var1) {
         this.dragging = var1;
      }

      public void setFocused(@Nullable GuiEventListener var1) {
         this.focused = var1;
      }

      @Nullable
      public GuiEventListener getFocused() {
         return this.focused;
      }
   }
}
