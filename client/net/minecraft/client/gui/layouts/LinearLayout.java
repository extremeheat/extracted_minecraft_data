package net.minecraft.client.gui.layouts;

import com.mojang.math.Divisor;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.function.Consumer;

public class LinearLayout extends AbstractLayout {
   private final LinearLayout.Orientation orientation;
   private final List<LinearLayout.ChildContainer> children = new ArrayList<>();
   private final LayoutSettings defaultChildLayoutSettings = LayoutSettings.defaults();

   public LinearLayout(int var1, int var2, LinearLayout.Orientation var3) {
      this(0, 0, var1, var2, var3);
   }

   public LinearLayout(int var1, int var2, int var3, int var4, LinearLayout.Orientation var5) {
      super(var1, var2, var3, var4);
      this.orientation = var5;
   }

   @Override
   public void arrangeElements() {
      super.arrangeElements();
      if (!this.children.isEmpty()) {
         int var1 = 0;
         int var2 = this.orientation.getSecondaryLength(this);

         for(LinearLayout.ChildContainer var4 : this.children) {
            var1 += this.orientation.getPrimaryLength(var4);
            var2 = Math.max(var2, this.orientation.getSecondaryLength(var4));
         }

         int var10 = this.orientation.getPrimaryLength(this) - var1;
         int var11 = this.orientation.getPrimaryPosition(this);
         Iterator var5 = this.children.iterator();
         LinearLayout.ChildContainer var6 = (LinearLayout.ChildContainer)var5.next();
         this.orientation.setPrimaryPosition(var6, var11);
         var11 += this.orientation.getPrimaryLength(var6);
         LinearLayout.ChildContainer var8;
         if (this.children.size() >= 2) {
            for(Divisor var7 = new Divisor(var10, this.children.size() - 1); var7.hasNext(); var11 += this.orientation.getPrimaryLength(var8)) {
               var11 += var7.nextInt();
               var8 = (LinearLayout.ChildContainer)var5.next();
               this.orientation.setPrimaryPosition(var8, var11);
            }
         }

         int var14 = this.orientation.getSecondaryPosition(this);

         for(LinearLayout.ChildContainer var9 : this.children) {
            this.orientation.setSecondaryPosition(var9, var14, var2);
         }

         switch(this.orientation) {
            case HORIZONTAL:
               this.height = var2;
               break;
            case VERTICAL:
               this.width = var2;
         }
      }
   }

   @Override
   protected void visitChildren(Consumer<LayoutElement> var1) {
      this.children.forEach(var1x -> var1.accept(var1x.child));
   }

   public LayoutSettings newChildLayoutSettings() {
      return this.defaultChildLayoutSettings.copy();
   }

   public LayoutSettings defaultChildLayoutSetting() {
      return this.defaultChildLayoutSettings;
   }

   public <T extends LayoutElement> T addChild(T var1) {
      return this.addChild((T)var1, this.newChildLayoutSettings());
   }

   public <T extends LayoutElement> T addChild(T var1, LayoutSettings var2) {
      this.children.add(new LinearLayout.ChildContainer(var1, var2));
      return (T)var1;
   }

   static class ChildContainer extends AbstractLayout.AbstractChildWrapper {
      protected ChildContainer(LayoutElement var1, LayoutSettings var2) {
         super(var1, var2);
      }
   }

   public static enum Orientation {
      HORIZONTAL,
      VERTICAL;

      private Orientation() {
      }

      int getPrimaryLength(LayoutElement var1) {
         return switch(this) {
            case HORIZONTAL -> var1.getWidth();
            case VERTICAL -> var1.getHeight();
         };
      }

      int getPrimaryLength(LinearLayout.ChildContainer var1) {
         return switch(this) {
            case HORIZONTAL -> var1.getWidth();
            case VERTICAL -> var1.getHeight();
         };
      }

      int getSecondaryLength(LayoutElement var1) {
         return switch(this) {
            case HORIZONTAL -> var1.getHeight();
            case VERTICAL -> var1.getWidth();
         };
      }

      int getSecondaryLength(LinearLayout.ChildContainer var1) {
         return switch(this) {
            case HORIZONTAL -> var1.getHeight();
            case VERTICAL -> var1.getWidth();
         };
      }

      void setPrimaryPosition(LinearLayout.ChildContainer var1, int var2) {
         switch(this) {
            case HORIZONTAL:
               var1.setX(var2, var1.getWidth());
               break;
            case VERTICAL:
               var1.setY(var2, var1.getHeight());
         }
      }

      void setSecondaryPosition(LinearLayout.ChildContainer var1, int var2, int var3) {
         switch(this) {
            case HORIZONTAL:
               var1.setY(var2, var3);
               break;
            case VERTICAL:
               var1.setX(var2, var3);
         }
      }

      int getPrimaryPosition(LayoutElement var1) {
         return switch(this) {
            case HORIZONTAL -> var1.getX();
            case VERTICAL -> var1.getY();
         };
      }

      int getSecondaryPosition(LayoutElement var1) {
         return switch(this) {
            case HORIZONTAL -> var1.getY();
            case VERTICAL -> var1.getX();
         };
      }
   }
}
