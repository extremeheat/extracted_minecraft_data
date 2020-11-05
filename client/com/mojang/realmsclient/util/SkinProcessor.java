package com.mojang.realmsclient.util;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.awt.image.ImageObserver;
import javax.annotation.Nullable;

public class SkinProcessor {
   private int[] pixels;
   private int width;
   private int height;

   public SkinProcessor() {
      super();
   }

   @Nullable
   public BufferedImage process(BufferedImage var1) {
      if (var1 == null) {
         return null;
      } else {
         this.width = 64;
         this.height = 64;
         BufferedImage var2 = new BufferedImage(this.width, this.height, 2);
         Graphics var3 = var2.getGraphics();
         var3.drawImage(var1, 0, 0, (ImageObserver)null);
         boolean var4 = var1.getHeight() == 32;
         if (var4) {
            var3.setColor(new Color(0, 0, 0, 0));
            var3.fillRect(0, 32, 64, 32);
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
         this.pixels = ((DataBufferInt)var2.getRaster().getDataBuffer()).getData();
         this.setNoAlpha(0, 0, 32, 16);
         if (var4) {
            this.doLegacyTransparencyHack(32, 0, 64, 32);
         }

         this.setNoAlpha(0, 16, 64, 32);
         this.setNoAlpha(16, 48, 48, 64);
         return var2;
      }
   }

   private void doLegacyTransparencyHack(int var1, int var2, int var3, int var4) {
      int var5;
      int var6;
      for(var5 = var1; var5 < var3; ++var5) {
         for(var6 = var2; var6 < var4; ++var6) {
            int var7 = this.pixels[var5 + var6 * this.width];
            if ((var7 >> 24 & 255) < 128) {
               return;
            }
         }
      }

      for(var5 = var1; var5 < var3; ++var5) {
         for(var6 = var2; var6 < var4; ++var6) {
            int[] var10000 = this.pixels;
            int var10001 = var5 + var6 * this.width;
            var10000[var10001] &= 16777215;
         }
      }

   }

   private void setNoAlpha(int var1, int var2, int var3, int var4) {
      for(int var5 = var1; var5 < var3; ++var5) {
         for(int var6 = var2; var6 < var4; ++var6) {
            int[] var10000 = this.pixels;
            int var10001 = var5 + var6 * this.width;
            var10000[var10001] |= -16777216;
         }
      }

   }
}
