package net.minecraft.client.renderer;

public class Rect2i {
   private int xPos;
   private int yPos;
   private int width;
   private int height;

   public Rect2i(int var1, int var2, int var3, int var4) {
      super();
      this.xPos = var1;
      this.yPos = var2;
      this.width = var3;
      this.height = var4;
   }

   public Rect2i intersect(Rect2i var1) {
      int var2 = this.xPos;
      int var3 = this.yPos;
      int var4 = this.xPos + this.width;
      int var5 = this.yPos + this.height;
      int var6 = var1.getX();
      int var7 = var1.getY();
      int var8 = var6 + var1.getWidth();
      int var9 = var7 + var1.getHeight();
      this.xPos = Math.max(var2, var6);
      this.yPos = Math.max(var3, var7);
      this.width = Math.max(0, Math.min(var4, var8) - this.xPos);
      this.height = Math.max(0, Math.min(var5, var9) - this.yPos);
      return this;
   }

   public int getX() {
      return this.xPos;
   }

   public int getY() {
      return this.yPos;
   }

   public void setX(int var1) {
      this.xPos = var1;
   }

   public void setY(int var1) {
      this.yPos = var1;
   }

   public int getWidth() {
      return this.width;
   }

   public int getHeight() {
      return this.height;
   }

   public void setWidth(int var1) {
      this.width = var1;
   }

   public void setHeight(int var1) {
      this.height = var1;
   }

   public void setPosition(int var1, int var2) {
      this.xPos = var1;
      this.yPos = var2;
   }

   public boolean contains(int var1, int var2) {
      return var1 >= this.xPos && var1 <= this.xPos + this.width && var2 >= this.yPos && var2 <= this.yPos + this.height;
   }
}
