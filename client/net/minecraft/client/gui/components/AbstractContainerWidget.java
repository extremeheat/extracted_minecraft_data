package net.minecraft.client.gui.components;

import com.mojang.blaze3d.vertex.PoseStack;
import java.util.List;
import java.util.Optional;
import javax.annotation.Nullable;
import net.minecraft.client.gui.components.events.ContainerEventHandler;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarratableEntry;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.narration.NarrationSupplier;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;

public abstract class AbstractContainerWidget extends AbstractWidget implements ContainerEventHandler {
   @Nullable
   private GuiEventListener focused;
   private boolean dragging;

   public AbstractContainerWidget(int var1, int var2, int var3, int var4, Component var5) {
      super(var1, var2, var3, var4, var5);
   }

   @Override
   public void renderButton(PoseStack var1, int var2, int var3, float var4) {
      for(AbstractWidget var6 : this.getContainedChildren()) {
         var6.render(var1, var2, var3, var4);
      }
   }

   @Override
   public boolean isMouseOver(double var1, double var3) {
      for(AbstractWidget var6 : this.getContainedChildren()) {
         if (var6.isMouseOver(var1, var3)) {
            return true;
         }
      }

      return false;
   }

   @Override
   public void mouseMoved(double var1, double var3) {
      this.getContainedChildren().forEach(var4 -> var4.mouseMoved(var1, var3));
   }

   @Override
   public List<? extends GuiEventListener> children() {
      return this.getContainedChildren();
   }

   protected abstract List<? extends AbstractWidget> getContainedChildren();

   @Override
   public boolean isDragging() {
      return this.dragging;
   }

   @Override
   public void setDragging(boolean var1) {
      this.dragging = var1;
   }

   @Override
   public boolean mouseScrolled(double var1, double var3, double var5) {
      boolean var7 = false;

      for(AbstractWidget var9 : this.getContainedChildren()) {
         if (var9.isMouseOver(var1, var3) && var9.mouseScrolled(var1, var3, var5)) {
            var7 = true;
         }
      }

      return var7 || super.mouseScrolled(var1, var3, var5);
   }

   @Override
   public boolean changeFocus(boolean var1) {
      return ContainerEventHandler.super.changeFocus(var1);
   }

   @Nullable
   protected GuiEventListener getHovered() {
      for(AbstractWidget var2 : this.getContainedChildren()) {
         if (var2.isHovered) {
            return var2;
         }
      }

      return null;
   }

   @Nullable
   @Override
   public GuiEventListener getFocused() {
      return this.focused;
   }

   @Override
   public void setFocused(@Nullable GuiEventListener var1) {
      this.focused = var1;
   }

   // $QF: Could not properly define all variable types!
   // Please report this to the Quiltflower issue tracker, at https://github.com/QuiltMC/quiltflower/issues with a copy of the class file (if you have the rights to distribute it!)
   @Override
   public void updateWidgetNarration(NarrationElementOutput var1) {
      GuiEventListener var2 = this.getHovered();
      if (var2 != null) {
         if (var2 instanceof NarrationSupplier var3) {
            var3.updateNarration(var1.nest());
         }
      } else {
         GuiEventListener var5 = this.getFocused();
         if (var5 != null && var5 instanceof NarrationSupplier var4) {
            var4.updateNarration(var1.nest());
         }
      }
   }

   @Override
   public NarratableEntry.NarrationPriority narrationPriority() {
      if (this.isHovered || this.getHovered() != null) {
         return NarratableEntry.NarrationPriority.HOVERED;
      } else {
         return this.focused != null ? NarratableEntry.NarrationPriority.FOCUSED : super.narrationPriority();
      }
   }

   @Override
   public void setX(int var1) {
      for(AbstractWidget var3 : this.getContainedChildren()) {
         int var4 = var3.getX() + (var1 - this.getX());
         var3.setX(var4);
      }

      super.setX(var1);
   }

   @Override
   public void setY(int var1) {
      for(AbstractWidget var3 : this.getContainedChildren()) {
         int var4 = var3.getY() + (var1 - this.getY());
         var3.setY(var4);
      }

      super.setY(var1);
   }

   @Override
   public Optional<GuiEventListener> getChildAt(double var1, double var3) {
      return ContainerEventHandler.super.getChildAt(var1, var3);
   }

   @Override
   public boolean mouseClicked(double var1, double var3, int var5) {
      return ContainerEventHandler.super.mouseClicked(var1, var3, var5);
   }

   @Override
   public boolean mouseReleased(double var1, double var3, int var5) {
      return ContainerEventHandler.super.mouseReleased(var1, var3, var5);
   }

   @Override
   public boolean mouseDragged(double var1, double var3, int var5, double var6, double var8) {
      return ContainerEventHandler.super.mouseDragged(var1, var3, var5, var6, var8);
   }

   protected abstract static class AbstractChildWrapper {
      public final AbstractWidget child;
      public final LayoutSettings.LayoutSettingsImpl layoutSettings;

      protected AbstractChildWrapper(AbstractWidget var1, LayoutSettings var2) {
         super();
         this.child = var1;
         this.layoutSettings = var2.getExposed();
      }

      public int getHeight() {
         return this.child.getHeight() + this.layoutSettings.paddingTop + this.layoutSettings.paddingBottom;
      }

      public int getWidth() {
         return this.child.getWidth() + this.layoutSettings.paddingLeft + this.layoutSettings.paddingRight;
      }

      public void setX(int var1, int var2) {
         float var3 = (float)this.layoutSettings.paddingLeft;
         float var4 = (float)(var2 - this.child.getWidth() - this.layoutSettings.paddingRight);
         int var5 = (int)Mth.lerp(this.layoutSettings.xAlignment, var3, var4);
         this.child.setX(var5 + var1);
      }

      public void setY(int var1, int var2) {
         float var3 = (float)this.layoutSettings.paddingTop;
         float var4 = (float)(var2 - this.child.getHeight() - this.layoutSettings.paddingBottom);
         int var5 = (int)Mth.lerp(this.layoutSettings.yAlignment, var3, var4);
         this.child.setY(var5 + var1);
      }
   }
}
