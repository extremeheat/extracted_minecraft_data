package net.minecraft.client.gui.layouts;

import java.util.function.Consumer;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.util.Mth;

public abstract class AbstractLayout implements LayoutElement {
   private int x;
   private int y;
   protected int width;
   protected int height;

   public AbstractLayout(int var1, int var2, int var3, int var4) {
      super();
      this.x = var1;
      this.y = var2;
      this.width = var3;
      this.height = var4;
   }

   protected abstract void visitChildren(Consumer<LayoutElement> var1);

   public void arrangeElements() {
      this.visitChildren(var0 -> {
         if (var0 instanceof AbstractLayout var1) {
            var1.arrangeElements();
         }
      });
   }

   @Override
   public void visitWidgets(Consumer<AbstractWidget> var1) {
      this.visitChildren(var1x -> var1x.visitWidgets(var1));
   }

   @Override
   public void setX(int var1) {
      this.visitChildren(var2 -> {
         int var3 = var2.getX() + (var1 - this.getX());
         var2.setX(var3);
      });
      this.x = var1;
   }

   @Override
   public void setY(int var1) {
      this.visitChildren(var2 -> {
         int var3 = var2.getY() + (var1 - this.getY());
         var2.setY(var3);
      });
      this.y = var1;
   }

   @Override
   public int getX() {
      return this.x;
   }

   @Override
   public int getY() {
      return this.y;
   }

   @Override
   public int getWidth() {
      return this.width;
   }

   @Override
   public int getHeight() {
      return this.height;
   }

   protected abstract static class AbstractChildWrapper {
      public final LayoutElement child;
      public final LayoutSettings.LayoutSettingsImpl layoutSettings;

      protected AbstractChildWrapper(LayoutElement var1, LayoutSettings var2) {
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
