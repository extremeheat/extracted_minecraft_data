package net.minecraft.client.gui.navigation;

import javax.annotation.Nullable;
import net.minecraft.util.Mth;
import org.joml.Matrix3x2fc;
import org.joml.Vector2f;

public record ScreenRectangle(ScreenPosition position, int width, int height) {
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
      ScreenRectangle var10000;
      switch (var0) {
         case HORIZONTAL -> var10000 = new ScreenRectangle(var1, var2, var3, var4);
         case VERTICAL -> var10000 = new ScreenRectangle(var2, var1, var4, var3);
         default -> throw new MatchException((String)null, (Throwable)null);
      }

      return var10000;
   }

   public ScreenRectangle step(ScreenDirection var1) {
      return new ScreenRectangle(this.position.step(var1), this.width, this.height);
   }

   public int getLength(ScreenAxis var1) {
      int var10000;
      switch (var1) {
         case HORIZONTAL -> var10000 = this.width;
         case VERTICAL -> var10000 = this.height;
         default -> throw new MatchException((String)null, (Throwable)null);
      }

      return var10000;
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

   public boolean intersects(ScreenRectangle var1) {
      return this.left() < var1.right() && this.right() > var1.left() && this.top() < var1.bottom() && this.bottom() > var1.top();
   }

   public boolean encompasses(ScreenRectangle var1) {
      return var1.left() >= this.left() && var1.top() >= this.top() && var1.right() <= this.right() && var1.bottom() <= this.bottom();
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

   public boolean containsPoint(int var1, int var2) {
      return var1 >= this.left() && var1 < this.right() && var2 >= this.top() && var2 < this.bottom();
   }

   public ScreenRectangle transformAxisAligned(Matrix3x2fc var1) {
      Vector2f var2 = var1.transformPosition((float)this.left(), (float)this.top(), new Vector2f());
      Vector2f var3 = var1.transformPosition((float)this.right(), (float)this.bottom(), new Vector2f());
      return new ScreenRectangle(Mth.floor(var2.x), Mth.floor(var2.y), Mth.floor(var3.x - var2.x), Mth.floor(var3.y - var2.y));
   }

   public ScreenRectangle transformMaxBounds(Matrix3x2fc var1) {
      Vector2f var2 = var1.transformPosition((float)this.left(), (float)this.top(), new Vector2f());
      Vector2f var3 = var1.transformPosition((float)this.right(), (float)this.top(), new Vector2f());
      Vector2f var4 = var1.transformPosition((float)this.left(), (float)this.bottom(), new Vector2f());
      Vector2f var5 = var1.transformPosition((float)this.right(), (float)this.bottom(), new Vector2f());
      float var6 = Math.min(Math.min(var2.x(), var4.x()), Math.min(var3.x(), var5.x()));
      float var7 = Math.max(Math.max(var2.x(), var4.x()), Math.max(var3.x(), var5.x()));
      float var8 = Math.min(Math.min(var2.y(), var4.y()), Math.min(var3.y(), var5.y()));
      float var9 = Math.max(Math.max(var2.y(), var4.y()), Math.max(var3.y(), var5.y()));
      return new ScreenRectangle(Mth.floor(var6), Mth.floor(var8), Mth.ceil(var7 - var6), Mth.ceil(var9 - var8));
   }
}
