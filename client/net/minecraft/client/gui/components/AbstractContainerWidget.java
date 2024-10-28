package net.minecraft.client.gui.components;

import javax.annotation.Nullable;
import net.minecraft.client.gui.ComponentPath;
import net.minecraft.client.gui.components.events.ContainerEventHandler;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.navigation.FocusNavigationEvent;
import net.minecraft.network.chat.Component;

public abstract class AbstractContainerWidget extends AbstractWidget implements ContainerEventHandler {
   @Nullable
   private GuiEventListener focused;
   private boolean isDragging;

   public AbstractContainerWidget(int var1, int var2, int var3, int var4, Component var5) {
      super(var1, var2, var3, var4, var5);
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

   @Nullable
   public ComponentPath nextFocusPath(FocusNavigationEvent var1) {
      return ContainerEventHandler.super.nextFocusPath(var1);
   }

   public boolean mouseClicked(double var1, double var3, int var5) {
      return ContainerEventHandler.super.mouseClicked(var1, var3, var5);
   }

   public boolean mouseReleased(double var1, double var3, int var5) {
      return ContainerEventHandler.super.mouseReleased(var1, var3, var5);
   }

   public boolean mouseDragged(double var1, double var3, int var5, double var6, double var8) {
      return ContainerEventHandler.super.mouseDragged(var1, var3, var5, var6, var8);
   }

   public boolean isFocused() {
      return ContainerEventHandler.super.isFocused();
   }

   public void setFocused(boolean var1) {
      ContainerEventHandler.super.setFocused(var1);
   }
}
