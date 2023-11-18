package net.minecraft.client.gui.layouts;

import java.util.function.Consumer;
import net.minecraft.Util;

public class LinearLayout implements Layout {
   private final GridLayout wrapped;
   private final LinearLayout.Orientation orientation;
   private int nextChildIndex = 0;

   private LinearLayout(LinearLayout.Orientation var1) {
      this(0, 0, var1);
   }

   public LinearLayout(int var1, int var2, LinearLayout.Orientation var3) {
      super();
      this.wrapped = new GridLayout(var1, var2);
      this.orientation = var3;
   }

   public LinearLayout spacing(int var1) {
      this.orientation.setSpacing(this.wrapped, var1);
      return this;
   }

   public LayoutSettings newCellSettings() {
      return this.wrapped.newCellSettings();
   }

   public LayoutSettings defaultCellSetting() {
      return this.wrapped.defaultCellSetting();
   }

   public <T extends LayoutElement> T addChild(T var1, LayoutSettings var2) {
      return this.orientation.addChild(this.wrapped, (T)var1, this.nextChildIndex++, var2);
   }

   public <T extends LayoutElement> T addChild(T var1) {
      return this.addChild((T)var1, this.newCellSettings());
   }

   public <T extends LayoutElement> T addChild(T var1, Consumer<LayoutSettings> var2) {
      return this.orientation.addChild(this.wrapped, (T)var1, this.nextChildIndex++, Util.make(this.newCellSettings(), var2));
   }

   @Override
   public void visitChildren(Consumer<LayoutElement> var1) {
      this.wrapped.visitChildren(var1);
   }

   @Override
   public void arrangeElements() {
      this.wrapped.arrangeElements();
   }

   @Override
   public int getWidth() {
      return this.wrapped.getWidth();
   }

   @Override
   public int getHeight() {
      return this.wrapped.getHeight();
   }

   @Override
   public void setX(int var1) {
      this.wrapped.setX(var1);
   }

   @Override
   public void setY(int var1) {
      this.wrapped.setY(var1);
   }

   @Override
   public int getX() {
      return this.wrapped.getX();
   }

   @Override
   public int getY() {
      return this.wrapped.getY();
   }

   public static LinearLayout vertical() {
      return new LinearLayout(LinearLayout.Orientation.VERTICAL);
   }

   public static LinearLayout horizontal() {
      return new LinearLayout(LinearLayout.Orientation.HORIZONTAL);
   }

   public static enum Orientation {
      HORIZONTAL,
      VERTICAL;

      private Orientation() {
      }

      void setSpacing(GridLayout var1, int var2) {
         switch(this) {
            case HORIZONTAL:
               var1.columnSpacing(var2);
               break;
            case VERTICAL:
               var1.rowSpacing(var2);
         }
      }

      public <T extends LayoutElement> T addChild(GridLayout var1, T var2, int var3, LayoutSettings var4) {
         return (T)(switch(this) {
            case HORIZONTAL -> var1.addChild(var2, 0, var3, var4);
            case VERTICAL -> var1.addChild(var2, var3, 0, var4);
         });
      }
   }
}
