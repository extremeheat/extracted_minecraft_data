package com.mojang.blaze3d.systems;

public class ScissorState {
   private boolean enabled;
   private int x;
   private int y;
   private int width;
   private int height;

   public ScissorState() {
      super();
   }

   public void enable(int var1, int var2, int var3, int var4) {
      this.enabled = true;
      this.x = var1;
      this.y = var2;
      this.width = var3;
      this.height = var4;
   }

   public void disable() {
      this.enabled = false;
   }

   public boolean enabled() {
      return this.enabled;
   }

   public int x() {
      return this.x;
   }

   public int y() {
      return this.y;
   }

   public int width() {
      return this.width;
   }

   public int height() {
      return this.height;
   }
}
