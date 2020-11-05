package net.minecraft.client.renderer.texture;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.platform.NativeImage;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.VertexConsumer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import javax.annotation.Nullable;
import net.minecraft.CrashReport;
import net.minecraft.CrashReportCategory;
import net.minecraft.ReportedException;
import net.minecraft.client.renderer.SpriteCoordinateExpander;
import net.minecraft.client.resources.metadata.animation.AnimationFrame;
import net.minecraft.client.resources.metadata.animation.AnimationMetadataSection;
import net.minecraft.resources.ResourceLocation;

public class TextureAtlasSprite implements AutoCloseable {
   private final TextureAtlas atlas;
   private final TextureAtlasSprite.Info info;
   private final AnimationMetadataSection metadata;
   protected final NativeImage[] mainImage;
   private final int[] framesX;
   private final int[] framesY;
   @Nullable
   private final TextureAtlasSprite.InterpolationData interpolationData;
   private final int x;
   private final int y;
   private final float u0;
   private final float u1;
   private final float v0;
   private final float v1;
   private int frame;
   private int subFrame;

   protected TextureAtlasSprite(TextureAtlas var1, TextureAtlasSprite.Info var2, int var3, int var4, int var5, int var6, int var7, NativeImage var8) {
      super();
      this.atlas = var1;
      AnimationMetadataSection var9 = var2.metadata;
      int var10 = var2.width;
      int var11 = var2.height;
      this.x = var6;
      this.y = var7;
      this.u0 = (float)var6 / (float)var4;
      this.u1 = (float)(var6 + var10) / (float)var4;
      this.v0 = (float)var7 / (float)var5;
      this.v1 = (float)(var7 + var11) / (float)var5;
      int var12 = var8.getWidth() / var9.getFrameWidth(var10);
      int var13 = var8.getHeight() / var9.getFrameHeight(var11);
      int var16;
      int var17;
      int var18;
      if (var9.getFrameCount() > 0) {
         int var14 = (Integer)var9.getUniqueFrameIndices().stream().max(Integer::compareTo).get() + 1;
         this.framesX = new int[var14];
         this.framesY = new int[var14];
         Arrays.fill(this.framesX, -1);
         Arrays.fill(this.framesY, -1);

         for(Iterator var15 = var9.getUniqueFrameIndices().iterator(); var15.hasNext(); this.framesY[var16] = var17) {
            var16 = (Integer)var15.next();
            if (var16 >= var12 * var13) {
               throw new RuntimeException("invalid frameindex " + var16);
            }

            var17 = var16 / var12;
            var18 = var16 % var12;
            this.framesX[var16] = var18;
         }
      } else {
         ArrayList var21 = Lists.newArrayList();
         int var22 = var12 * var13;
         this.framesX = new int[var22];
         this.framesY = new int[var22];

         for(var16 = 0; var16 < var13; ++var16) {
            for(var17 = 0; var17 < var12; ++var17) {
               var18 = var16 * var12 + var17;
               this.framesX[var18] = var17;
               this.framesY[var18] = var16;
               var21.add(new AnimationFrame(var18, -1));
            }
         }

         var9 = new AnimationMetadataSection(var21, var10, var11, var9.getDefaultFrameTime(), var9.isInterpolatedFrames());
      }

      this.info = new TextureAtlasSprite.Info(var2.name, var10, var11, var9);
      this.metadata = var9;

      CrashReport var23;
      CrashReportCategory var24;
      try {
         try {
            this.mainImage = MipmapGenerator.generateMipLevels(var8, var3);
         } catch (Throwable var19) {
            var23 = CrashReport.forThrowable(var19, "Generating mipmaps for frame");
            var24 = var23.addCategory("Frame being iterated");
            var24.setDetail("First frame", () -> {
               StringBuilder var1 = new StringBuilder();
               if (var1.length() > 0) {
                  var1.append(", ");
               }

               var1.append(var8.getWidth()).append("x").append(var8.getHeight());
               return var1.toString();
            });
            throw new ReportedException(var23);
         }
      } catch (Throwable var20) {
         var23 = CrashReport.forThrowable(var20, "Applying mipmap");
         var24 = var23.addCategory("Sprite being mipmapped");
         var24.setDetail("Sprite name", () -> {
            return this.getName().toString();
         });
         var24.setDetail("Sprite size", () -> {
            return this.getWidth() + " x " + this.getHeight();
         });
         var24.setDetail("Sprite frames", () -> {
            return this.getFrameCount() + " frames";
         });
         var24.setDetail("Mipmap levels", (Object)var3);
         throw new ReportedException(var23);
      }

      if (var9.isInterpolatedFrames()) {
         this.interpolationData = new TextureAtlasSprite.InterpolationData(var2, var3);
      } else {
         this.interpolationData = null;
      }

   }

   private void upload(int var1) {
      int var2 = this.framesX[var1] * this.info.width;
      int var3 = this.framesY[var1] * this.info.height;
      this.upload(var2, var3, this.mainImage);
   }

   private void upload(int var1, int var2, NativeImage[] var3) {
      for(int var4 = 0; var4 < this.mainImage.length; ++var4) {
         var3[var4].upload(var4, this.x >> var4, this.y >> var4, var1 >> var4, var2 >> var4, this.info.width >> var4, this.info.height >> var4, this.mainImage.length > 1, false);
      }

   }

   public int getWidth() {
      return this.info.width;
   }

   public int getHeight() {
      return this.info.height;
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

   public ResourceLocation getName() {
      return this.info.name;
   }

   public TextureAtlas atlas() {
      return this.atlas;
   }

   public int getFrameCount() {
      return this.framesX.length;
   }

   public void close() {
      NativeImage[] var1 = this.mainImage;
      int var2 = var1.length;

      for(int var3 = 0; var3 < var2; ++var3) {
         NativeImage var4 = var1[var3];
         if (var4 != null) {
            var4.close();
         }
      }

      if (this.interpolationData != null) {
         this.interpolationData.close();
      }

   }

   public String toString() {
      int var1 = this.framesX.length;
      return "TextureAtlasSprite{name='" + this.info.name + '\'' + ", frameCount=" + var1 + ", x=" + this.x + ", y=" + this.y + ", height=" + this.info.height + ", width=" + this.info.width + ", u0=" + this.u0 + ", u1=" + this.u1 + ", v0=" + this.v0 + ", v1=" + this.v1 + '}';
   }

   public boolean isTransparent(int var1, int var2, int var3) {
      return (this.mainImage[0].getPixelRGBA(var2 + this.framesX[var1] * this.info.width, var3 + this.framesY[var1] * this.info.height) >> 24 & 255) == 0;
   }

   public void uploadFirstFrame() {
      this.upload(0);
   }

   private float atlasSize() {
      float var1 = (float)this.info.width / (this.u1 - this.u0);
      float var2 = (float)this.info.height / (this.v1 - this.v0);
      return Math.max(var2, var1);
   }

   public float uvShrinkRatio() {
      return 4.0F / this.atlasSize();
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
      } else if (this.interpolationData != null) {
         if (!RenderSystem.isOnRenderThread()) {
            RenderSystem.recordRenderCall(() -> {
               var0.uploadInterpolatedFrame();
            });
         } else {
            this.interpolationData.uploadInterpolatedFrame();
         }
      }

   }

   public boolean isAnimation() {
      return this.metadata.getFrameCount() > 1;
   }

   public VertexConsumer wrap(VertexConsumer var1) {
      return new SpriteCoordinateExpander(var1, this);
   }

   final class InterpolationData implements AutoCloseable {
      private final NativeImage[] activeFrame;

      private InterpolationData(TextureAtlasSprite.Info var2, int var3) {
         super();
         this.activeFrame = new NativeImage[var3 + 1];

         for(int var4 = 0; var4 < this.activeFrame.length; ++var4) {
            int var5 = var2.width >> var4;
            int var6 = var2.height >> var4;
            if (this.activeFrame[var4] == null) {
               this.activeFrame[var4] = new NativeImage(var5, var6, false);
            }
         }

      }

      private void uploadInterpolatedFrame() {
         double var1 = 1.0D - (double)TextureAtlasSprite.this.subFrame / (double)TextureAtlasSprite.this.metadata.getFrameTime(TextureAtlasSprite.this.frame);
         int var3 = TextureAtlasSprite.this.metadata.getFrameIndex(TextureAtlasSprite.this.frame);
         int var4 = TextureAtlasSprite.this.metadata.getFrameCount() == 0 ? TextureAtlasSprite.this.getFrameCount() : TextureAtlasSprite.this.metadata.getFrameCount();
         int var5 = TextureAtlasSprite.this.metadata.getFrameIndex((TextureAtlasSprite.this.frame + 1) % var4);
         if (var3 != var5 && var5 >= 0 && var5 < TextureAtlasSprite.this.getFrameCount()) {
            for(int var6 = 0; var6 < this.activeFrame.length; ++var6) {
               int var7 = TextureAtlasSprite.this.info.width >> var6;
               int var8 = TextureAtlasSprite.this.info.height >> var6;

               for(int var9 = 0; var9 < var8; ++var9) {
                  for(int var10 = 0; var10 < var7; ++var10) {
                     int var11 = this.getPixel(var3, var6, var10, var9);
                     int var12 = this.getPixel(var5, var6, var10, var9);
                     int var13 = this.mix(var1, var11 >> 16 & 255, var12 >> 16 & 255);
                     int var14 = this.mix(var1, var11 >> 8 & 255, var12 >> 8 & 255);
                     int var15 = this.mix(var1, var11 & 255, var12 & 255);
                     this.activeFrame[var6].setPixelRGBA(var10, var9, var11 & -16777216 | var13 << 16 | var14 << 8 | var15);
                  }
               }
            }

            TextureAtlasSprite.this.upload(0, 0, this.activeFrame);
         }

      }

      private int getPixel(int var1, int var2, int var3, int var4) {
         return TextureAtlasSprite.this.mainImage[var2].getPixelRGBA(var3 + (TextureAtlasSprite.this.framesX[var1] * TextureAtlasSprite.this.info.width >> var2), var4 + (TextureAtlasSprite.this.framesY[var1] * TextureAtlasSprite.this.info.height >> var2));
      }

      private int mix(double var1, int var3, int var4) {
         return (int)(var1 * (double)var3 + (1.0D - var1) * (double)var4);
      }

      public void close() {
         NativeImage[] var1 = this.activeFrame;
         int var2 = var1.length;

         for(int var3 = 0; var3 < var2; ++var3) {
            NativeImage var4 = var1[var3];
            if (var4 != null) {
               var4.close();
            }
         }

      }

      // $FF: synthetic method
      InterpolationData(TextureAtlasSprite.Info var2, int var3, Object var4) {
         this(var2, var3);
      }
   }

   public static final class Info {
      private final ResourceLocation name;
      private final int width;
      private final int height;
      private final AnimationMetadataSection metadata;

      public Info(ResourceLocation var1, int var2, int var3, AnimationMetadataSection var4) {
         super();
         this.name = var1;
         this.width = var2;
         this.height = var3;
         this.metadata = var4;
      }

      public ResourceLocation name() {
         return this.name;
      }

      public int width() {
         return this.width;
      }

      public int height() {
         return this.height;
      }
   }
}
