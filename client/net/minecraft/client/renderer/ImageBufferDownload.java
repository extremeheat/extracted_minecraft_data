package net.minecraft.client.renderer;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;

public class ImageBufferDownload implements IImageBuffer {
   private int[] field_78438_a;
   private int field_78436_b;
   private int field_78437_c;

   public ImageBufferDownload() {
      super();
   }

   @Override
   public BufferedImage func_78432_a(BufferedImage var1) {
      if (var1 == null) {
         return null;
      } else {
         this.field_78436_b = 64;
         this.field_78437_c = 32;
         BufferedImage var2 = new BufferedImage(this.field_78436_b, this.field_78437_c, 2);
         Graphics var3 = var2.getGraphics();
         var3.drawImage(var1, 0, 0, null);
         var3.dispose();
         this.field_78438_a = ((DataBufferInt)var2.getRaster().getDataBuffer()).getData();
         this.func_78433_b(0, 0, 32, 16);
         this.func_78434_a(32, 0, 64, 32);
         this.func_78433_b(0, 16, 64, 32);
         return var2;
      }
   }

   @Override
   public void func_152634_a() {
   }

   private void func_78434_a(int var1, int var2, int var3, int var4) {
      if (!this.func_78435_c(var1, var2, var3, var4)) {
         for(int var5 = var1; var5 < var3; ++var5) {
            for(int var6 = var2; var6 < var4; ++var6) {
               this.field_78438_a[var5 + var6 * this.field_78436_b] &= 16777215;
            }
         }
      }
   }

   private void func_78433_b(int var1, int var2, int var3, int var4) {
      for(int var5 = var1; var5 < var3; ++var5) {
         for(int var6 = var2; var6 < var4; ++var6) {
            this.field_78438_a[var5 + var6 * this.field_78436_b] |= -16777216;
         }
      }
   }

   private boolean func_78435_c(int var1, int var2, int var3, int var4) {
      for(int var5 = var1; var5 < var3; ++var5) {
         for(int var6 = var2; var6 < var4; ++var6) {
            int var7 = this.field_78438_a[var5 + var6 * this.field_78436_b];
            if ((var7 >> 24 & 0xFF) < 128) {
               return true;
            }
         }
      }

      return false;
   }
}
