package com.mojang.blaze3d.systems;

import com.mojang.blaze3d.DontObfuscate;

@DontObfuscate
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

   public boolean isEnabled() {
      return this.enabled;
   }

   public int getX() {
      return this.x;
   }

   public int getY() {
      return this.y;
   }

   public int getWidth() {
      return this.width;
   }

   public int getHeight() {
      return this.height;
   }

   public void copyFrom(ScissorState var1) {
      this.enabled = var1.enabled;
      this.x = var1.x;
      this.y = var1.y;
      this.width = var1.width;
      this.height = var1.height;
   }
}
