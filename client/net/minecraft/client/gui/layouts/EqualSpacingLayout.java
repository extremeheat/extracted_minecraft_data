package net.minecraft.client.gui.layouts;

import com.mojang.math.Divisor;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.function.Consumer;
import net.minecraft.util.Util;

public class EqualSpacingLayout extends AbstractLayout {
   private final Orientation orientation;
   private final List<ChildContainer> children;
   private final LayoutSettings defaultChildLayoutSettings;

   public EqualSpacingLayout(final int width, final int height, final Orientation orientation) {
      this(0, 0, width, height, orientation);
   }

   public EqualSpacingLayout(final int x, final int y, final int width, final int height, final Orientation orientation) {
      super(x, y, width, height);
      this.children = new ArrayList();
      this.defaultChildLayoutSettings = LayoutSettings.defaults();
      this.orientation = orientation;
   }

   public void arrangeElements() {
      super.arrangeElements();
      if (!this.children.isEmpty()) {
         int totalChildPrimaryLength = 0;
         int maxChildSecondaryLength = this.orientation.getSecondaryLength((LayoutElement)this);

         for(ChildContainer child : this.children) {
            totalChildPrimaryLength += this.orientation.getPrimaryLength(child);
            maxChildSecondaryLength = Math.max(maxChildSecondaryLength, this.orientation.getSecondaryLength(child));
         }

         int remainingSpace = this.orientation.getPrimaryLength((LayoutElement)this) - totalChildPrimaryLength;
         int position = this.orientation.getPrimaryPosition(this);
         Iterator<ChildContainer> childIterator = this.children.iterator();
         ChildContainer firstChild = (ChildContainer)childIterator.next();
         this.orientation.setPrimaryPosition(firstChild, position);
         position += this.orientation.getPrimaryLength(firstChild);
         ChildContainer child;
         if (this.children.size() >= 2) {
            for(Divisor divisor = new Divisor(remainingSpace, this.children.size() - 1); divisor.hasNext(); position += this.orientation.getPrimaryLength(child)) {
               position += divisor.nextInt();
               child = (ChildContainer)childIterator.next();
               this.orientation.setPrimaryPosition(child, position);
            }
         }

         int thisSecondaryPosition = this.orientation.getSecondaryPosition(this);

         for(ChildContainer child : this.children) {
            this.orientation.setSecondaryPosition(child, thisSecondaryPosition, maxChildSecondaryLength);
         }

         switch (this.orientation.ordinal()) {
            case 0 -> this.height = maxChildSecondaryLength;
            case 1 -> this.width = maxChildSecondaryLength;
         }

      }
   }

   public void visitChildren(final Consumer<LayoutElement> layoutElementVisitor) {
      this.children.forEach((wrapper) -> layoutElementVisitor.accept(wrapper.child));
   }

   public void removeChildren() {
      this.children.clear();
   }

   public LayoutSettings newChildLayoutSettings() {
      return this.defaultChildLayoutSettings.copy();
   }

   public LayoutSettings defaultChildLayoutSetting() {
      return this.defaultChildLayoutSettings;
   }

   public <T extends LayoutElement> T addChild(final T child) {
      return (T)this.addChild(child, this.newChildLayoutSettings());
   }

   public <T extends LayoutElement> T addChild(final T child, final LayoutSettings layoutSettings) {
      this.children.add(new ChildContainer(child, layoutSettings));
      return child;
   }

   public <T extends LayoutElement> T addChild(final T child, final Consumer<LayoutSettings> layoutSettingsAdjustments) {
      return (T)this.addChild(child, (LayoutSettings)Util.make(this.newChildLayoutSettings(), layoutSettingsAdjustments));
   }

   public static enum Orientation {
      HORIZONTAL,
      VERTICAL;

      private Orientation() {
      }

      private int getPrimaryLength(final LayoutElement widget) {
         int var10000;
         switch (this.ordinal()) {
            case 0 -> var10000 = widget.getWidth();
            case 1 -> var10000 = widget.getHeight();
            default -> throw new MatchException((String)null, (Throwable)null);
         }

         return var10000;
      }

      private int getPrimaryLength(final ChildContainer childContainer) {
         int var10000;
         switch (this.ordinal()) {
            case 0 -> var10000 = childContainer.getWidth();
            case 1 -> var10000 = childContainer.getHeight();
            default -> throw new MatchException((String)null, (Throwable)null);
         }

         return var10000;
      }

      private int getSecondaryLength(final LayoutElement widget) {
         int var10000;
         switch (this.ordinal()) {
            case 0 -> var10000 = widget.getHeight();
            case 1 -> var10000 = widget.getWidth();
            default -> throw new MatchException((String)null, (Throwable)null);
         }

         return var10000;
      }

      private int getSecondaryLength(final ChildContainer childContainer) {
         int var10000;
         switch (this.ordinal()) {
            case 0 -> var10000 = childContainer.getHeight();
            case 1 -> var10000 = childContainer.getWidth();
            default -> throw new MatchException((String)null, (Throwable)null);
         }

         return var10000;
      }

      private void setPrimaryPosition(final ChildContainer childContainer, final int position) {
         switch (this.ordinal()) {
            case 0 -> childContainer.setX(position, childContainer.getWidth());
            case 1 -> childContainer.setY(position, childContainer.getHeight());
         }

      }

      private void setSecondaryPosition(final ChildContainer childContainer, final int position, final int availableSpace) {
         switch (this.ordinal()) {
            case 0 -> childContainer.setY(position, availableSpace);
            case 1 -> childContainer.setX(position, availableSpace);
         }

      }

      private int getPrimaryPosition(final LayoutElement widget) {
         int var10000;
         switch (this.ordinal()) {
            case 0 -> var10000 = widget.getX();
            case 1 -> var10000 = widget.getY();
            default -> throw new MatchException((String)null, (Throwable)null);
         }

         return var10000;
      }

      private int getSecondaryPosition(final LayoutElement widget) {
         int var10000;
         switch (this.ordinal()) {
            case 0 -> var10000 = widget.getY();
            case 1 -> var10000 = widget.getX();
            default -> throw new MatchException((String)null, (Throwable)null);
         }

         return var10000;
      }

      // $FF: synthetic method
      private static Orientation[] $values() {
         return new Orientation[]{HORIZONTAL, VERTICAL};
      }
   }

   private static class ChildContainer extends AbstractLayout.AbstractChildWrapper {
      protected ChildContainer(final LayoutElement child, final LayoutSettings layoutSettings) {
         super(child, layoutSettings);
      }
   }
}
