package com.mojang.blaze3d.buffers;

import com.mojang.blaze3d.DontObfuscate;
import net.minecraft.util.Mth;

@DontObfuscate
public class Std140SizeCalculator {
   private int size;

   public Std140SizeCalculator() {
      super();
   }

   public int get() {
      return this.size;
   }

   public Std140SizeCalculator align(int var1) {
      this.size = Mth.roundToward(this.size, var1);
      return this;
   }

   public Std140SizeCalculator putFloat() {
      this.align(4);
      this.size += 4;
      return this;
   }

   public Std140SizeCalculator putInt() {
      this.align(4);
      this.size += 4;
      return this;
   }

   public Std140SizeCalculator putVec2() {
      this.align(8);
      this.size += 8;
      return this;
   }

   public Std140SizeCalculator putIVec2() {
      this.align(8);
      this.size += 8;
      return this;
   }

   public Std140SizeCalculator putVec3() {
      this.align(16);
      this.size += 16;
      return this;
   }

   public Std140SizeCalculator putIVec3() {
      this.align(16);
      this.size += 16;
      return this;
   }

   public Std140SizeCalculator putVec4() {
      this.align(16);
      this.size += 16;
      return this;
   }

   public Std140SizeCalculator putIVec4() {
      this.align(16);
      this.size += 16;
      return this;
   }

   public Std140SizeCalculator putMat4f() {
      this.align(16);
      this.size += 64;
      return this;
   }
}
