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

   public int getX() {
      return this.xPos;
   }

   public int getY() {
      return this.yPos;
   }

   public int getWidth() {
      return this.width;
   }

   public int getHeight() {
      return this.height;
   }

   public boolean contains(int var1, int var2) {
      return var1 >= this.xPos && var1 <= this.xPos + this.width && var2 >= this.yPos && var2 <= this.yPos + this.height;
   }
}
