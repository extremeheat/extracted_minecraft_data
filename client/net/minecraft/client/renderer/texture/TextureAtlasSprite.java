package net.minecraft.client.renderer.texture;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.platform.NativeImage;
import com.mojang.blaze3d.platform.PngInfo;
import com.mojang.datafixers.util.Pair;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import javax.annotation.Nullable;
import net.minecraft.CrashReport;
import net.minecraft.CrashReportCategory;
import net.minecraft.ReportedException;
import net.minecraft.Util;
import net.minecraft.client.resources.metadata.animation.AnimationFrame;
import net.minecraft.client.resources.metadata.animation.AnimationMetadataSection;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;

public class TextureAtlasSprite {
   private final ResourceLocation name;
   protected final int width;
   protected final int height;
   protected NativeImage[] mainImage;
   @Nullable
   protected int[] framesX;
   @Nullable
   protected int[] framesY;
   protected NativeImage[] activeFrame;
   private AnimationMetadataSection metadata;
   protected int x;
   protected int y;
   private float u0;
   private float u1;
   private float v0;
   private float v1;
   protected int frame;
   protected int subFrame;
   private static final float[] POW22 = (float[])Util.make(new float[256], (var0) -> {
      for(int var1 = 0; var1 < var0.length; ++var1) {
         var0[var1] = (float)Math.pow((double)((float)var1 / 255.0F), 2.2D);
      }

   });

   protected TextureAtlasSprite(ResourceLocation var1, int var2, int var3) {
      super();
      this.name = var1;
      this.width = var2;
      this.height = var3;
   }

   protected TextureAtlasSprite(ResourceLocation var1, PngInfo var2, @Nullable AnimationMetadataSection var3) {
      super();
      this.name = var1;
      if (var3 != null) {
         Pair var4 = getFrameSize(var3.getFrameWidth(), var3.getFrameHeight(), var2.width, var2.height);
         this.width = (Integer)var4.getFirst();
         this.height = (Integer)var4.getSecond();
         if (!isDivisionInteger(var2.width, this.width) || !isDivisionInteger(var2.height, this.height)) {
            throw new IllegalArgumentException(String.format("Image size %s,%s is not multiply of frame size %s,%s", this.width, this.height, var2.width, var2.height));
         }
      } else {
         this.width = var2.width;
         this.height = var2.height;
      }

      this.metadata = var3;
   }

   private static Pair<Integer, Integer> getFrameSize(int var0, int var1, int var2, int var3) {
      if (var0 != -1) {
         return var1 != -1 ? Pair.of(var0, var1) : Pair.of(var0, var3);
      } else if (var1 != -1) {
         return Pair.of(var2, var1);
      } else {
         int var4 = Math.min(var2, var3);
         return Pair.of(var4, var4);
      }
   }

   private static boolean isDivisionInteger(int var0, int var1) {
      return var0 / var1 * var1 == var0;
   }

   private void generateMipLevels(int var1) {
      NativeImage[] var2 = new NativeImage[var1 + 1];
      var2[0] = this.mainImage[0];
      if (var1 > 0) {
         boolean var3 = false;

         int var4;
         label71:
         for(var4 = 0; var4 < this.mainImage[0].getWidth(); ++var4) {
            for(int var5 = 0; var5 < this.mainImage[0].getHeight(); ++var5) {
               if (this.mainImage[0].getPixelRGBA(var4, var5) >> 24 == 0) {
                  var3 = true;
                  break label71;
               }
            }
         }

         for(var4 = 1; var4 <= var1; ++var4) {
            if (this.mainImage.length > var4 && this.mainImage[var4] != null) {
               var2[var4] = this.mainImage[var4];
            } else {
               NativeImage var11 = var2[var4 - 1];
               NativeImage var6 = new NativeImage(var11.getWidth() >> 1, var11.getHeight() >> 1, false);
               int var7 = var6.getWidth();
               int var8 = var6.getHeight();

               for(int var9 = 0; var9 < var7; ++var9) {
                  for(int var10 = 0; var10 < var8; ++var10) {
                     var6.setPixelRGBA(var9, var10, alphaBlend(var11.getPixelRGBA(var9 * 2 + 0, var10 * 2 + 0), var11.getPixelRGBA(var9 * 2 + 1, var10 * 2 + 0), var11.getPixelRGBA(var9 * 2 + 0, var10 * 2 + 1), var11.getPixelRGBA(var9 * 2 + 1, var10 * 2 + 1), var3));
                  }
               }

               var2[var4] = var6;
            }
         }

         for(var4 = var1 + 1; var4 < this.mainImage.length; ++var4) {
            if (this.mainImage[var4] != null) {
               this.mainImage[var4].close();
            }
         }
      }

      this.mainImage = var2;
   }

   private static int alphaBlend(int var0, int var1, int var2, int var3, boolean var4) {
      if (var4) {
         float var13 = 0.0F;
         float var14 = 0.0F;
         float var15 = 0.0F;
         float var16 = 0.0F;
         if (var0 >> 24 != 0) {
            var13 += getPow22(var0 >> 24);
            var14 += getPow22(var0 >> 16);
            var15 += getPow22(var0 >> 8);
            var16 += getPow22(var0 >> 0);
         }

         if (var1 >> 24 != 0) {
            var13 += getPow22(var1 >> 24);
            var14 += getPow22(var1 >> 16);
            var15 += getPow22(var1 >> 8);
            var16 += getPow22(var1 >> 0);
         }

         if (var2 >> 24 != 0) {
            var13 += getPow22(var2 >> 24);
            var14 += getPow22(var2 >> 16);
            var15 += getPow22(var2 >> 8);
            var16 += getPow22(var2 >> 0);
         }

         if (var3 >> 24 != 0) {
            var13 += getPow22(var3 >> 24);
            var14 += getPow22(var3 >> 16);
            var15 += getPow22(var3 >> 8);
            var16 += getPow22(var3 >> 0);
         }

         var13 /= 4.0F;
         var14 /= 4.0F;
         var15 /= 4.0F;
         var16 /= 4.0F;
         int var9 = (int)(Math.pow((double)var13, 0.45454545454545453D) * 255.0D);
         int var10 = (int)(Math.pow((double)var14, 0.45454545454545453D) * 255.0D);
         int var11 = (int)(Math.pow((double)var15, 0.45454545454545453D) * 255.0D);
         int var12 = (int)(Math.pow((double)var16, 0.45454545454545453D) * 255.0D);
         if (var9 < 96) {
            var9 = 0;
         }

         return var9 << 24 | var10 << 16 | var11 << 8 | var12;
      } else {
         int var5 = gammaBlend(var0, var1, var2, var3, 24);
         int var6 = gammaBlend(var0, var1, var2, var3, 16);
         int var7 = gammaBlend(var0, var1, var2, var3, 8);
         int var8 = gammaBlend(var0, var1, var2, var3, 0);
         return var5 << 24 | var6 << 16 | var7 << 8 | var8;
      }
   }

   private static int gammaBlend(int var0, int var1, int var2, int var3, int var4) {
      float var5 = getPow22(var0 >> var4);
      float var6 = getPow22(var1 >> var4);
      float var7 = getPow22(var2 >> var4);
      float var8 = getPow22(var3 >> var4);
      float var9 = (float)((double)((float)Math.pow((double)(var5 + var6 + var7 + var8) * 0.25D, 0.45454545454545453D)));
      return (int)((double)var9 * 255.0D);
   }

   private static float getPow22(int var0) {
      return POW22[var0 & 255];
   }

   private void upload(int var1) {
      int var2 = 0;
      int var3 = 0;
      if (this.framesX != null) {
         var2 = this.framesX[var1] * this.width;
         var3 = this.framesY[var1] * this.height;
      }

      this.upload(var2, var3, this.mainImage);
   }

   private void upload(int var1, int var2, NativeImage[] var3) {
      for(int var4 = 0; var4 < this.mainImage.length; ++var4) {
         var3[var4].upload(var4, this.x >> var4, this.y >> var4, var1 >> var4, var2 >> var4, this.width >> var4, this.height >> var4, this.mainImage.length > 1);
      }

   }

   public void init(int var1, int var2, int var3, int var4) {
      this.x = var3;
      this.y = var4;
      this.u0 = (float)var3 / (float)var1;
      this.u1 = (float)(var3 + this.width) / (float)var1;
      this.v0 = (float)var4 / (float)var2;
      this.v1 = (float)(var4 + this.height) / (float)var2;
   }

   public int getWidth() {
      return this.width;
   }

   public int getHeight() {
      return this.height;
   }

   public float getU0() {
      return this.u0;
   }

   public float getU1() {
      return this.u1;
   }

   public float getU(double var1) {
      float var3 = this.u1 - this.u0;
      return this.u0 + var3 * (float)var1 / 16.0F;
   }

   public float getUOffset(float var1) {
      float var2 = this.u1 - this.u0;
      return (var1 - this.u0) / var2 * 16.0F;
   }

   public float getV0() {
      return this.v0;
   }

   public float getV1() {
      return this.v1;
   }

   public float getV(double var1) {
      float var3 = this.v1 - this.v0;
      return this.v0 + var3 * (float)var1 / 16.0F;
   }

   public float getVOffset(float var1) {
      float var2 = this.v1 - this.v0;
      return (var1 - this.v0) / var2 * 16.0F;
   }

   public ResourceLocation getName() {
      return this.name;
   }

   public void cycleFrames() {
      ++this.subFrame;
      if (this.subFrame >= this.metadata.getFrameTime(this.frame)) {
         int var1 = this.metadata.getFrameIndex(this.frame);
         int var2 = this.metadata.getFrameCount() == 0 ? this.getFrameCount() : this.metadata.getFrameCount();
         this.frame = (this.frame + 1) % var2;
         this.subFrame = 0;
         int var3 = this.metadata.getFrameIndex(this.frame);
         if (var1 != var3 && var3 >= 0 && var3 < this.getFrameCount()) {
            this.upload(var3);
         }
      } else if (this.metadata.isInterpolatedFrames()) {
         this.uploadInterpolatedFrame();
      }

   }

   private void uploadInterpolatedFrame() {
      double var1 = 1.0D - (double)this.subFrame / (double)this.metadata.getFrameTime(this.frame);
      int var3 = this.metadata.getFrameIndex(this.frame);
      int var4 = this.metadata.getFrameCount() == 0 ? this.getFrameCount() : this.metadata.getFrameCount();
      int var5 = this.metadata.getFrameIndex((this.frame + 1) % var4);
      if (var3 != var5 && var5 >= 0 && var5 < this.getFrameCount()) {
         int var7;
         int var8;
         if (this.activeFrame == null || this.activeFrame.length != this.mainImage.length) {
            if (this.activeFrame != null) {
               NativeImage[] var6 = this.activeFrame;
               var7 = var6.length;

               for(var8 = 0; var8 < var7; ++var8) {
                  NativeImage var9 = var6[var8];
                  if (var9 != null) {
                     var9.close();
                  }
               }
            }

            this.activeFrame = new NativeImage[this.mainImage.length];
         }

         for(int var16 = 0; var16 < this.mainImage.length; ++var16) {
            var7 = this.width >> var16;
            var8 = this.height >> var16;
            if (this.activeFrame[var16] == null) {
               this.activeFrame[var16] = new NativeImage(var7, var8, false);
            }

            for(int var17 = 0; var17 < var8; ++var17) {
               for(int var10 = 0; var10 < var7; ++var10) {
                  int var11 = this.getPixel(var3, var16, var10, var17);
                  int var12 = this.getPixel(var5, var16, var10, var17);
                  int var13 = this.mix(var1, var11 >> 16 & 255, var12 >> 16 & 255);
                  int var14 = this.mix(var1, var11 >> 8 & 255, var12 >> 8 & 255);
                  int var15 = this.mix(var1, var11 & 255, var12 & 255);
                  this.activeFrame[var16].setPixelRGBA(var10, var17, var11 & -16777216 | var13 << 16 | var14 << 8 | var15);
               }
            }
         }

         this.upload(0, 0, this.activeFrame);
      }

   }

   private int mix(double var1, int var3, int var4) {
      return (int)(var1 * (double)var3 + (1.0D - var1) * (double)var4);
   }

   public int getFrameCount() {
      return this.framesX == null ? 0 : this.framesX.length;
   }

   public void loadData(Resource var1, int var2) throws IOException {
      NativeImage var3 = NativeImage.read(var1.getInputStream());
      this.mainImage = new NativeImage[var2];
      this.mainImage[0] = var3;
      int var4;
      if (this.metadata != null && this.metadata.getFrameWidth() != -1) {
         var4 = var3.getWidth() / this.metadata.getFrameWidth();
      } else {
         var4 = var3.getWidth() / this.width;
      }

      int var5;
      if (this.metadata != null && this.metadata.getFrameHeight() != -1) {
         var5 = var3.getHeight() / this.metadata.getFrameHeight();
      } else {
         var5 = var3.getHeight() / this.height;
      }

      int var8;
      int var9;
      int var10;
      if (this.metadata != null && this.metadata.getFrameCount() > 0) {
         int var11 = (Integer)this.metadata.getUniqueFrameIndices().stream().max(Integer::compareTo).get() + 1;
         this.framesX = new int[var11];
         this.framesY = new int[var11];
         Arrays.fill(this.framesX, -1);
         Arrays.fill(this.framesY, -1);

         for(Iterator var12 = this.metadata.getUniqueFrameIndices().iterator(); var12.hasNext(); this.framesY[var8] = var9) {
            var8 = (Integer)var12.next();
            if (var8 >= var4 * var5) {
               throw new RuntimeException("invalid frameindex " + var8);
            }

            var9 = var8 / var4;
            var10 = var8 % var4;
            this.framesX[var8] = var10;
         }
      } else {
         ArrayList var6 = Lists.newArrayList();
         int var7 = var4 * var5;
         this.framesX = new int[var7];
         this.framesY = new int[var7];

         for(var8 = 0; var8 < var5; ++var8) {
            for(var9 = 0; var9 < var4; ++var9) {
               var10 = var8 * var4 + var9;
               this.framesX[var10] = var9;
               this.framesY[var10] = var8;
               var6.add(new AnimationFrame(var10, -1));
            }
         }

         var8 = 1;
         boolean var13 = false;
         if (this.metadata != null) {
            var8 = this.metadata.getDefaultFrameTime();
            var13 = this.metadata.isInterpolatedFrames();
         }

         this.metadata = new AnimationMetadataSection(var6, this.width, this.height, var8, var13);
      }

   }

   public void applyMipmapping(int var1) {
      try {
         this.generateMipLevels(var1);
      } catch (Throwable var5) {
         CrashReport var3 = CrashReport.forThrowable(var5, "Generating mipmaps for frame");
         CrashReportCategory var4 = var3.addCategory("Frame being iterated");
         var4.setDetail("Frame sizes", () -> {
            StringBuilder var1 = new StringBuilder();
            NativeImage[] var2 = this.mainImage;
            int var3 = var2.length;

            for(int var4 = 0; var4 < var3; ++var4) {
               NativeImage var5 = var2[var4];
               if (var1.length() > 0) {
                  var1.append(", ");
               }

               var1.append(var5 == null ? "null" : var5.getWidth() + "x" + var5.getHeight());
            }

            return var1.toString();
         });
         throw new ReportedException(var3);
      }
   }

   public void wipeFrameData() {
      NativeImage[] var1;
      int var2;
      int var3;
      NativeImage var4;
      if (this.mainImage != null) {
         var1 = this.mainImage;
         var2 = var1.length;

         for(var3 = 0; var3 < var2; ++var3) {
            var4 = var1[var3];
            if (var4 != null) {
               var4.close();
            }
         }
      }

      this.mainImage = null;
      if (this.activeFrame != null) {
         var1 = this.activeFrame;
         var2 = var1.length;

         for(var3 = 0; var3 < var2; ++var3) {
            var4 = var1[var3];
            if (var4 != null) {
               var4.close();
            }
         }
      }

      this.activeFrame = null;
   }

   public boolean isAnimation() {
      return this.metadata != null && this.metadata.getFrameCount() > 1;
   }

   public String toString() {
      int var1 = this.framesX == null ? 0 : this.framesX.length;
      return "TextureAtlasSprite{name='" + this.name + '\'' + ", frameCount=" + var1 + ", x=" + this.x + ", y=" + this.y + ", height=" + this.height + ", width=" + this.width + ", u0=" + this.u0 + ", u1=" + this.u1 + ", v0=" + this.v0 + ", v1=" + this.v1 + '}';
   }

   private int getPixel(int var1, int var2, int var3, int var4) {
      return this.mainImage[var2].getPixelRGBA(var3 + (this.framesX[var1] * this.width >> var2), var4 + (this.framesY[var1] * this.height >> var2));
   }

   public boolean isTransparent(int var1, int var2, int var3) {
      return (this.mainImage[0].getPixelRGBA(var2 + this.framesX[var1] * this.width, var3 + this.framesY[var1] * this.height) >> 24 & 255) == 0;
   }

   public void uploadFirstFrame() {
      this.upload(0);
   }
}
