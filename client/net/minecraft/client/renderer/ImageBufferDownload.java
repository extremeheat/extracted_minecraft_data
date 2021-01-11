package net.minecraft.client.renderer;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.awt.image.ImageObserver;

public class ImageBufferDownload implements IImageBuffer {
   private int[] field_78438_a;
   private int field_78436_b;
   private int field_78437_c;

   public ImageBufferDownload() {
      super();
   }

   public BufferedImage func_78432_a(BufferedImage var1) {
      if (var1 == null) {
         return null;
      } else {
         this.field_78436_b = 64;
         this.field_78437_c = 64;
         BufferedImage var2 = new BufferedImage(this.field_78436_b, this.field_78437_c, 2);
         Graphics var3 = var2.getGraphics();
         var3.drawImage(var1, 0, 0, (ImageObserver)null);
         if (var1.getHeight() == 32) {
            var3.drawImage(var2, 24, 48, 20, 52, 4, 16, 8, 20, (ImageObserver)null);
            var3.drawImage(var2, 28, 48, 24, 52, 8, 16, 12, 20, (ImageObserver)null);
            var3.drawImage(var2, 20, 52, 16, 64, 8, 20, 12, 32, (ImageObserver)null);
            var3.drawImage(var2, 24, 52, 20, 64, 4, 20, 8, 32, (ImageObserver)null);
            var3.drawImage(var2, 28, 52, 24, 64, 0, 20, 4, 32, (ImageObserver)null);
            var3.drawImage(var2, 32, 52, 28, 64, 12, 20, 16, 32, (ImageObserver)null);
            var3.drawImage(var2, 40, 48, 36, 52, 44, 16, 48, 20, (ImageObserver)null);
            var3.drawImage(var2, 44, 48, 40, 52, 48, 16, 52, 20, (ImageObserver)null);
            var3.drawImage(var2, 36, 52, 32, 64, 48, 20, 52, 32, (ImageObserver)null);
            var3.drawImage(var2, 40, 52, 36, 64, 44, 20, 48, 32, (ImageObserver)null);
            var3.drawImage(var2, 44, 52, 40, 64, 40, 20, 44, 32, (ImageObserver)null);
            var3.drawImage(var2, 48, 52, 44, 64, 52, 20, 56, 32, (ImageObserver)null);
         }

         var3.dispose();
         this.field_78438_a = ((DataBufferInt)var2.getRaster().getDataBuffer()).getData();
         this.func_78433_b(0, 0, 32, 16);
         this.func_78434_a(32, 0, 64, 32);
         this.func_78433_b(0, 16, 64, 32);
         this.func_78434_a(0, 32, 16, 48);
         this.func_78434_a(16, 32, 40, 48);
         this.func_78434_a(40, 32, 56, 48);
         this.func_78434_a(0, 48, 16, 64);
         this.func_78433_b(16, 48, 48, 64);
         this.func_78434_a(48, 48, 64, 64);
         return var2;
      }
   }

   public void func_152634_a() {
   }

   private void func_78434_a(int var1, int var2, int var3, int var4) {
      if (!this.func_78435_c(var1, var2, var3, var4)) {
         for(int var5 = var1; var5 < var3; ++var5) {
            for(int var6 = var2; var6 < var4; ++var6) {
               int[] var10000 = this.field_78438_a;
               int var10001 = var5 + var6 * this.field_78436_b;
               var10000[var10001] &= 16777215;
            }
         }

      }
   }

   private void func_78433_b(int var1, int var2, int var3, int var4) {
      for(int var5 = var1; var5 < var3; ++var5) {
         for(int var6 = var2; var6 < var4; ++var6) {
            int[] var10000 = this.field_78438_a;
            int var10001 = var5 + var6 * this.field_78436_b;
            var10000[var10001] |= -16777216;
         }
      }

   }

   private boolean func_78435_c(int var1, int var2, int var3, int var4) {
      for(int var5 = var1; var5 < var3; ++var5) {
         for(int var6 = var2; var6 < var4; ++var6) {
            int var7 = this.field_78438_a[var5 + var6 * this.field_78436_b];
            if ((var7 >> 24 & 255) < 128) {
               return true;
            }
         }
      }

      return false;
   }
}
