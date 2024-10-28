package net.minecraft.client.gui.layouts;

import java.util.function.Consumer;
import net.minecraft.Util;

public class LinearLayout implements Layout {
   private final GridLayout wrapped;
   private final Orientation orientation;
   private int nextChildIndex;

   private LinearLayout(Orientation var1) {
      this(0, 0, var1);
   }

   public LinearLayout(int var1, int var2, Orientation var3) {
      super();
      this.nextChildIndex = 0;
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
      return this.orientation.addChild(this.wrapped, var1, this.nextChildIndex++, var2);
   }

   public <T extends LayoutElement> T addChild(T var1) {
      return this.addChild(var1, this.newCellSettings());
   }

   public <T extends LayoutElement> T addChild(T var1, Consumer<LayoutSettings> var2) {
      return this.orientation.addChild(this.wrapped, var1, this.nextChildIndex++, (LayoutSettings)Util.make(this.newCellSettings(), var2));
   }

   public void visitChildren(Consumer<LayoutElement> var1) {
      this.wrapped.visitChildren(var1);
   }

   public void arrangeElements() {
      this.wrapped.arrangeElements();
   }

   public int getWidth() {
      return this.wrapped.getWidth();
   }

   public int getHeight() {
      return this.wrapped.getHeight();
   }

   public void setX(int var1) {
      this.wrapped.setX(var1);
   }

   public void setY(int var1) {
      this.wrapped.setY(var1);
   }

   public int getX() {
      return this.wrapped.getX();
   }

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
         switch (this.ordinal()) {
            case 0 -> var1.columnSpacing(var2);
            case 1 -> var1.rowSpacing(var2);
         }

      }

      public <T extends LayoutElement> T addChild(GridLayout var1, T var2, int var3, LayoutSettings var4) {
         LayoutElement var10000;
         switch (this.ordinal()) {
            case 0 -> var10000 = (LayoutElement)var1.addChild(var2, 0, var3, (LayoutSettings)var4);
            case 1 -> var10000 = (LayoutElement)var1.addChild(var2, var3, 0, (LayoutSettings)var4);
            default -> throw new MatchException((String)null, (Throwable)null);
         }

         return var10000;
      }

      // $FF: synthetic method
      private static Orientation[] $values() {
         return new Orientation[]{HORIZONTAL, VERTICAL};
      }
   }
}
