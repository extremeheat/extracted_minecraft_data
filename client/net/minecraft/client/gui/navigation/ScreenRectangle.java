package net.minecraft.client.gui.navigation;

import javax.annotation.Nullable;

public record ScreenRectangle(ScreenPosition a, int b, int c) {
   private final ScreenPosition position;
   private final int width;
   private final int height;
   private static final ScreenRectangle EMPTY = new ScreenRectangle(0, 0, 0, 0);

   public ScreenRectangle(int var1, int var2, int var3, int var4) {
      this(new ScreenPosition(var1, var2), var3, var4);
   }

   public ScreenRectangle(ScreenPosition var1, int var2, int var3) {
      super();
      this.position = var1;
      this.width = var2;
      this.height = var3;
   }

   public static ScreenRectangle empty() {
      return EMPTY;
   }

   public static ScreenRectangle of(ScreenAxis var0, int var1, int var2, int var3, int var4) {
      return switch(var0) {
         case HORIZONTAL -> new ScreenRectangle(var1, var2, var3, var4);
         case VERTICAL -> new ScreenRectangle(var2, var1, var4, var3);
      };
   }

   public ScreenRectangle step(ScreenDirection var1) {
      return new ScreenRectangle(this.position.step(var1), this.width, this.height);
   }

   public int getLength(ScreenAxis var1) {
      return switch(var1) {
         case HORIZONTAL -> this.width;
         case VERTICAL -> this.height;
      };
   }

   public int getBoundInDirection(ScreenDirection var1) {
      ScreenAxis var2 = var1.getAxis();
      return var1.isPositive() ? this.position.getCoordinate(var2) + this.getLength(var2) - 1 : this.position.getCoordinate(var2);
   }

   public ScreenRectangle getBorder(ScreenDirection var1) {
      int var2 = this.getBoundInDirection(var1);
      ScreenAxis var3 = var1.getAxis().orthogonal();
      int var4 = this.getBoundInDirection(var3.getNegative());
      int var5 = this.getLength(var3);
      return of(var1.getAxis(), var2, var4, 1, var5).step(var1);
   }

   public boolean overlaps(ScreenRectangle var1) {
      return this.overlapsInAxis(var1, ScreenAxis.HORIZONTAL) && this.overlapsInAxis(var1, ScreenAxis.VERTICAL);
   }

   public boolean overlapsInAxis(ScreenRectangle var1, ScreenAxis var2) {
      int var3 = this.getBoundInDirection(var2.getNegative());
      int var4 = var1.getBoundInDirection(var2.getNegative());
      int var5 = this.getBoundInDirection(var2.getPositive());
      int var6 = var1.getBoundInDirection(var2.getPositive());
      return Math.max(var3, var4) <= Math.min(var5, var6);
   }

   public int getCenterInAxis(ScreenAxis var1) {
      return (this.getBoundInDirection(var1.getPositive()) + this.getBoundInDirection(var1.getNegative())) / 2;
   }

   @Nullable
   public ScreenRectangle intersection(ScreenRectangle var1) {
      int var2 = Math.max(this.left(), var1.left());
      int var3 = Math.max(this.top(), var1.top());
      int var4 = Math.min(this.right(), var1.right());
      int var5 = Math.min(this.bottom(), var1.bottom());
      return var2 < var4 && var3 < var5 ? new ScreenRectangle(var2, var3, var4 - var2, var5 - var3) : null;
   }

   public int top() {
      return this.position.y();
   }

   public int bottom() {
      return this.position.y() + this.height;
   }

   public int left() {
      return this.position.x();
   }

   public int right() {
      return this.position.x() + this.width;
   }
}
