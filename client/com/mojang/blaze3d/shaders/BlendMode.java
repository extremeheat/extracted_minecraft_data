package com.mojang.blaze3d.shaders;

import com.mojang.blaze3d.systems.RenderSystem;
import java.util.Locale;
import javax.annotation.Nullable;

public class BlendMode {
   @Nullable
   private static BlendMode lastApplied;
   private final int srcColorFactor;
   private final int srcAlphaFactor;
   private final int dstColorFactor;
   private final int dstAlphaFactor;
   private final int blendFunc;
   private final boolean separateBlend;
   private final boolean opaque;

   private BlendMode(boolean var1, boolean var2, int var3, int var4, int var5, int var6, int var7) {
      super();
      this.separateBlend = var1;
      this.srcColorFactor = var3;
      this.dstColorFactor = var4;
      this.srcAlphaFactor = var5;
      this.dstAlphaFactor = var6;
      this.opaque = var2;
      this.blendFunc = var7;
   }

   public BlendMode() {
      this(false, true, 1, 0, 1, 0, 32774);
   }

   public BlendMode(int var1, int var2, int var3) {
      this(false, false, var1, var2, var1, var2, var3);
   }

   public BlendMode(int var1, int var2, int var3, int var4, int var5) {
      this(true, false, var1, var2, var3, var4, var5);
   }

   public void apply() {
      if (!this.equals(lastApplied)) {
         if (lastApplied == null || this.opaque != lastApplied.isOpaque()) {
            lastApplied = this;
            if (this.opaque) {
               RenderSystem.disableBlend();
               return;
            }

            RenderSystem.enableBlend();
         }

         RenderSystem.blendEquation(this.blendFunc);
         if (this.separateBlend) {
            RenderSystem.blendFuncSeparate(this.srcColorFactor, this.dstColorFactor, this.srcAlphaFactor, this.dstAlphaFactor);
         } else {
            RenderSystem.blendFunc(this.srcColorFactor, this.dstColorFactor);
         }

      }
   }

   public boolean equals(Object var1) {
      if (this == var1) {
         return true;
      } else if (!(var1 instanceof BlendMode)) {
         return false;
      } else {
         BlendMode var2 = (BlendMode)var1;
         if (this.blendFunc != var2.blendFunc) {
            return false;
         } else if (this.dstAlphaFactor != var2.dstAlphaFactor) {
            return false;
         } else if (this.dstColorFactor != var2.dstColorFactor) {
            return false;
         } else if (this.opaque != var2.opaque) {
            return false;
         } else if (this.separateBlend != var2.separateBlend) {
            return false;
         } else if (this.srcAlphaFactor != var2.srcAlphaFactor) {
            return false;
         } else {
            return this.srcColorFactor == var2.srcColorFactor;
         }
      }
   }

   public int hashCode() {
      int var1 = this.srcColorFactor;
      var1 = 31 * var1 + this.srcAlphaFactor;
      var1 = 31 * var1 + this.dstColorFactor;
      var1 = 31 * var1 + this.dstAlphaFactor;
      var1 = 31 * var1 + this.blendFunc;
      var1 = 31 * var1 + (this.separateBlend ? 1 : 0);
      var1 = 31 * var1 + (this.opaque ? 1 : 0);
      return var1;
   }

   public boolean isOpaque() {
      return this.opaque;
   }

   public static int stringToBlendFunc(String var0) {
      String var1 = var0.trim().toLowerCase(Locale.ROOT);
      if ("add".equals(var1)) {
         return 32774;
      } else if ("subtract".equals(var1)) {
         return 32778;
      } else if ("reversesubtract".equals(var1)) {
         return 32779;
      } else if ("reverse_subtract".equals(var1)) {
         return 32779;
      } else if ("min".equals(var1)) {
         return 32775;
      } else {
         return "max".equals(var1) ? '\u8008' : '\u8006';
      }
   }

   public static int stringToBlendFactor(String var0) {
      String var1 = var0.trim().toLowerCase(Locale.ROOT);
      var1 = var1.replaceAll("_", "");
      var1 = var1.replaceAll("one", "1");
      var1 = var1.replaceAll("zero", "0");
      var1 = var1.replaceAll("minus", "-");
      if ("0".equals(var1)) {
         return 0;
      } else if ("1".equals(var1)) {
         return 1;
      } else if ("srccolor".equals(var1)) {
         return 768;
      } else if ("1-srccolor".equals(var1)) {
         return 769;
      } else if ("dstcolor".equals(var1)) {
         return 774;
      } else if ("1-dstcolor".equals(var1)) {
         return 775;
      } else if ("srcalpha".equals(var1)) {
         return 770;
      } else if ("1-srcalpha".equals(var1)) {
         return 771;
      } else if ("dstalpha".equals(var1)) {
         return 772;
      } else {
         return "1-dstalpha".equals(var1) ? 773 : -1;
      }
   }
}
