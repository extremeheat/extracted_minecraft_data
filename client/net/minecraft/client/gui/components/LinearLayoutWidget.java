package net.minecraft.client.gui.components;

import com.google.common.collect.Lists;
import com.mojang.math.Divisor;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import net.minecraft.network.chat.Component;

public class LinearLayoutWidget extends AbstractContainerWidget {
   private final LinearLayoutWidget.Orientation orientation;
   private final List<LinearLayoutWidget.ChildContainer> children = new ArrayList<>();
   private final List<AbstractWidget> containedChildrenView = Collections.unmodifiableList(Lists.transform(this.children, var0 -> var0.child));
   private final LayoutSettings defaultChildLayoutSettings = LayoutSettings.defaults();

   public LinearLayoutWidget(int var1, int var2, LinearLayoutWidget.Orientation var3) {
      this(0, 0, var1, var2, var3);
   }

   public LinearLayoutWidget(int var1, int var2, int var3, int var4, LinearLayoutWidget.Orientation var5) {
      super(var1, var2, var3, var4, Component.empty());
      this.orientation = var5;
   }

   public void pack() {
      if (!this.children.isEmpty()) {
         int var1 = 0;
         int var2 = this.orientation.getSecondaryLength(this);

         for(LinearLayoutWidget.ChildContainer var4 : this.children) {
            var1 += this.orientation.getPrimaryLength(var4);
            var2 = Math.max(var2, this.orientation.getSecondaryLength(var4));
         }

         int var10 = this.orientation.getPrimaryLength(this) - var1;
         int var11 = this.orientation.getPrimaryPosition(this);
         Iterator var5 = this.children.iterator();
         LinearLayoutWidget.ChildContainer var6 = (LinearLayoutWidget.ChildContainer)var5.next();
         this.orientation.setPrimaryPosition(var6, var11);
         var11 += this.orientation.getPrimaryLength(var6);
         LinearLayoutWidget.ChildContainer var8;
         if (this.children.size() >= 2) {
            for(Divisor var7 = new Divisor(var10, this.children.size() - 1); var7.hasNext(); var11 += this.orientation.getPrimaryLength(var8)) {
               var11 += var7.nextInt();
               var8 = (LinearLayoutWidget.ChildContainer)var5.next();
               this.orientation.setPrimaryPosition(var8, var11);
            }
         }

         int var14 = this.orientation.getSecondaryPosition(this);

         for(LinearLayoutWidget.ChildContainer var9 : this.children) {
            this.orientation.setSecondaryPosition(var9, var14, var2);
         }

         this.orientation.setSecondaryLength(this, var2);
      }
   }

   @Override
   protected List<? extends AbstractWidget> getContainedChildren() {
      return this.containedChildrenView;
   }

   public LayoutSettings newChildLayoutSettings() {
      return this.defaultChildLayoutSettings.copy();
   }

   public LayoutSettings defaultChildLayoutSetting() {
      return this.defaultChildLayoutSettings;
   }

   public <T extends AbstractWidget> T addChild(T var1) {
      return this.addChild((T)var1, this.newChildLayoutSettings());
   }

   public <T extends AbstractWidget> T addChild(T var1, LayoutSettings var2) {
      this.children.add(new LinearLayoutWidget.ChildContainer(var1, var2));
      return (T)var1;
   }

   static class ChildContainer extends AbstractContainerWidget.AbstractChildWrapper {
      protected ChildContainer(AbstractWidget var1, LayoutSettings var2) {
         super(var1, var2);
      }
   }

   public static enum Orientation {
      HORIZONTAL,
      VERTICAL;

      private Orientation() {
      }

      int getPrimaryLength(AbstractWidget var1) {
         return switch(this) {
            case HORIZONTAL -> var1.getWidth();
            case VERTICAL -> var1.getHeight();
         };
      }

      int getPrimaryLength(LinearLayoutWidget.ChildContainer var1) {
         return switch(this) {
            case HORIZONTAL -> var1.getWidth();
            case VERTICAL -> var1.getHeight();
         };
      }

      int getSecondaryLength(AbstractWidget var1) {
         return switch(this) {
            case HORIZONTAL -> var1.getHeight();
            case VERTICAL -> var1.getWidth();
         };
      }

      int getSecondaryLength(LinearLayoutWidget.ChildContainer var1) {
         return switch(this) {
            case HORIZONTAL -> var1.getHeight();
            case VERTICAL -> var1.getWidth();
         };
      }

      void setPrimaryPosition(LinearLayoutWidget.ChildContainer var1, int var2) {
         switch(this) {
            case HORIZONTAL:
               var1.setX(var2, var1.getWidth());
               break;
            case VERTICAL:
               var1.setY(var2, var1.getHeight());
         }
      }

      void setSecondaryPosition(LinearLayoutWidget.ChildContainer var1, int var2, int var3) {
         switch(this) {
            case HORIZONTAL:
               var1.setY(var2, var3);
               break;
            case VERTICAL:
               var1.setX(var2, var3);
         }
      }

      int getPrimaryPosition(AbstractWidget var1) {
         return switch(this) {
            case HORIZONTAL -> var1.getX();
            case VERTICAL -> var1.getY();
         };
      }

      int getSecondaryPosition(AbstractWidget var1) {
         return switch(this) {
            case HORIZONTAL -> var1.getY();
            case VERTICAL -> var1.getX();
         };
      }

      void setSecondaryLength(AbstractWidget var1, int var2) {
         switch(this) {
            case HORIZONTAL:
               var1.height = var2;
               break;
            case VERTICAL:
               var1.width = var2;
         }
      }
   }
}
