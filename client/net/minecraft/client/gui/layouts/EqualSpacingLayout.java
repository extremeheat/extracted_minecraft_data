package net.minecraft.client.gui.layouts;

import com.mojang.math.Divisor;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.function.Consumer;
import net.minecraft.Util;

public class EqualSpacingLayout extends AbstractLayout {
   private final Orientation orientation;
   private final List<ChildContainer> children;
   private final LayoutSettings defaultChildLayoutSettings;

   public EqualSpacingLayout(int var1, int var2, Orientation var3) {
      this(0, 0, var1, var2, var3);
   }

   public EqualSpacingLayout(int var1, int var2, int var3, int var4, Orientation var5) {
      super(var1, var2, var3, var4);
      this.children = new ArrayList();
      this.defaultChildLayoutSettings = LayoutSettings.defaults();
      this.orientation = var5;
   }

   public void arrangeElements() {
      super.arrangeElements();
      if (!this.children.isEmpty()) {
         int var1 = 0;
         int var2 = this.orientation.getSecondaryLength((LayoutElement)this);

         for(ChildContainer var4 : this.children) {
            var1 += this.orientation.getPrimaryLength(var4);
            var2 = Math.max(var2, this.orientation.getSecondaryLength(var4));
         }

         int var10 = this.orientation.getPrimaryLength((LayoutElement)this) - var1;
         int var11 = this.orientation.getPrimaryPosition(this);
         Iterator var5 = this.children.iterator();
         ChildContainer var6 = (ChildContainer)var5.next();
         this.orientation.setPrimaryPosition(var6, var11);
         var11 += this.orientation.getPrimaryLength(var6);
         ChildContainer var8;
         if (this.children.size() >= 2) {
            for(Divisor var7 = new Divisor(var10, this.children.size() - 1); var7.hasNext(); var11 += this.orientation.getPrimaryLength(var8)) {
               var11 += var7.nextInt();
               var8 = (ChildContainer)var5.next();
               this.orientation.setPrimaryPosition(var8, var11);
            }
         }

         int var14 = this.orientation.getSecondaryPosition(this);

         for(ChildContainer var9 : this.children) {
            this.orientation.setSecondaryPosition(var9, var14, var2);
         }

         switch (this.orientation.ordinal()) {
            case 0 -> this.height = var2;
            case 1 -> this.width = var2;
         }

      }
   }

   public void visitChildren(Consumer<LayoutElement> var1) {
      this.children.forEach((var1x) -> var1.accept(var1x.child));
   }

   public LayoutSettings newChildLayoutSettings() {
      return this.defaultChildLayoutSettings.copy();
   }

   public LayoutSettings defaultChildLayoutSetting() {
      return this.defaultChildLayoutSettings;
   }

   public <T extends LayoutElement> T addChild(T var1) {
      return (T)this.addChild(var1, this.newChildLayoutSettings());
   }

   public <T extends LayoutElement> T addChild(T var1, LayoutSettings var2) {
      this.children.add(new ChildContainer(var1, var2));
      return (T)var1;
   }

   public <T extends LayoutElement> T addChild(T var1, Consumer<LayoutSettings> var2) {
      return (T)this.addChild(var1, (LayoutSettings)Util.make(this.newChildLayoutSettings(), var2));
   }

   public static enum Orientation {
      HORIZONTAL,
      VERTICAL;

      private Orientation() {
      }

      int getPrimaryLength(LayoutElement var1) {
         int var10000;
         switch (this.ordinal()) {
            case 0 -> var10000 = var1.getWidth();
            case 1 -> var10000 = var1.getHeight();
            default -> throw new MatchException((String)null, (Throwable)null);
         }

         return var10000;
      }

      int getPrimaryLength(ChildContainer var1) {
         int var10000;
         switch (this.ordinal()) {
            case 0 -> var10000 = var1.getWidth();
            case 1 -> var10000 = var1.getHeight();
            default -> throw new MatchException((String)null, (Throwable)null);
         }

         return var10000;
      }

      int getSecondaryLength(LayoutElement var1) {
         int var10000;
         switch (this.ordinal()) {
            case 0 -> var10000 = var1.getHeight();
            case 1 -> var10000 = var1.getWidth();
            default -> throw new MatchException((String)null, (Throwable)null);
         }

         return var10000;
      }

      int getSecondaryLength(ChildContainer var1) {
         int var10000;
         switch (this.ordinal()) {
            case 0 -> var10000 = var1.getHeight();
            case 1 -> var10000 = var1.getWidth();
            default -> throw new MatchException((String)null, (Throwable)null);
         }

         return var10000;
      }

      void setPrimaryPosition(ChildContainer var1, int var2) {
         switch (this.ordinal()) {
            case 0 -> var1.setX(var2, var1.getWidth());
            case 1 -> var1.setY(var2, var1.getHeight());
         }

      }

      void setSecondaryPosition(ChildContainer var1, int var2, int var3) {
         switch (this.ordinal()) {
            case 0 -> var1.setY(var2, var3);
            case 1 -> var1.setX(var2, var3);
         }

      }

      int getPrimaryPosition(LayoutElement var1) {
         int var10000;
         switch (this.ordinal()) {
            case 0 -> var10000 = var1.getX();
            case 1 -> var10000 = var1.getY();
            default -> throw new MatchException((String)null, (Throwable)null);
         }

         return var10000;
      }

      int getSecondaryPosition(LayoutElement var1) {
         int var10000;
         switch (this.ordinal()) {
            case 0 -> var10000 = var1.getY();
            case 1 -> var10000 = var1.getX();
            default -> throw new MatchException((String)null, (Throwable)null);
         }

         return var10000;
      }

      // $FF: synthetic method
      private static Orientation[] $values() {
         return new Orientation[]{HORIZONTAL, VERTICAL};
      }
   }

   static class ChildContainer extends AbstractLayout.AbstractChildWrapper {
      protected ChildContainer(LayoutElement var1, LayoutSettings var2) {
         super(var1, var2);
      }
   }
}
