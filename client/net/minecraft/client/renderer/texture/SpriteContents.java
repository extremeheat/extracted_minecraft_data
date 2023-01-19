package net.minecraft.client.renderer.texture;

import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.platform.NativeImage;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.logging.LogUtils;
import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.stream.IntStream;
import javax.annotation.Nullable;
import net.minecraft.CrashReport;
import net.minecraft.CrashReportCategory;
import net.minecraft.ReportedException;
import net.minecraft.client.resources.metadata.animation.AnimationMetadataSection;
import net.minecraft.client.resources.metadata.animation.FrameSize;
import net.minecraft.resources.ResourceLocation;
import org.slf4j.Logger;

public class SpriteContents implements Stitcher.Entry, AutoCloseable {
   private static final Logger LOGGER = LogUtils.getLogger();
   private final ResourceLocation name;
   final int width;
   final int height;
   private final NativeImage originalImage;
   NativeImage[] byMipLevel;
   @Nullable
   private final SpriteContents.AnimatedTexture animatedTexture;

   public SpriteContents(ResourceLocation var1, FrameSize var2, NativeImage var3, AnimationMetadataSection var4) {
      super();
      this.name = var1;
      this.width = var2.width();
      this.height = var2.height();
      this.animatedTexture = this.createAnimatedTexture(var2, var3.getWidth(), var3.getHeight(), var4);
      this.originalImage = var3;
      this.byMipLevel = new NativeImage[]{this.originalImage};
   }

   public void increaseMipLevel(int var1) {
      try {
         this.byMipLevel = MipmapGenerator.generateMipLevels(this.byMipLevel, var1);
      } catch (Throwable var6) {
         CrashReport var3 = CrashReport.forThrowable(var6, "Generating mipmaps for frame");
         CrashReportCategory var4 = var3.addCategory("Sprite being mipmapped");
         var4.setDetail("First frame", () -> {
            StringBuilder var1x = new StringBuilder();
            if (var1x.length() > 0) {
               var1x.append(", ");
            }

            var1x.append(this.originalImage.getWidth()).append("x").append(this.originalImage.getHeight());
            return var1x.toString();
         });
         CrashReportCategory var5 = var3.addCategory("Frame being iterated");
         var5.setDetail("Sprite name", this.name);
         var5.setDetail("Sprite size", () -> this.width + " x " + this.height);
         var5.setDetail("Sprite frames", () -> this.getFrameCount() + " frames");
         var5.setDetail("Mipmap levels", var1);
         throw new ReportedException(var3);
      }
   }

   private int getFrameCount() {
      return this.animatedTexture != null ? this.animatedTexture.frames.size() : 1;
   }

   @Nullable
   private SpriteContents.AnimatedTexture createAnimatedTexture(FrameSize var1, int var2, int var3, AnimationMetadataSection var4) {
      int var5 = var2 / var1.width();
      int var6 = var3 / var1.height();
      int var7 = var5 * var6;
      ArrayList var8 = new ArrayList();
      var4.forEachFrame((var1x, var2x) -> var8.add(new SpriteContents.FrameInfo(var1x, var2x)));
      if (var8.isEmpty()) {
         for(int var9 = 0; var9 < var7; ++var9) {
            var8.add(new SpriteContents.FrameInfo(var9, var4.getDefaultFrameTime()));
         }
      } else {
         int var14 = 0;
         IntOpenHashSet var10 = new IntOpenHashSet();

         for(Iterator var11 = var8.iterator(); var11.hasNext(); ++var14) {
            SpriteContents.FrameInfo var12 = (SpriteContents.FrameInfo)var11.next();
            boolean var13 = true;
            if (var12.time <= 0) {
               LOGGER.warn("Invalid frame duration on sprite {} frame {}: {}", new Object[]{this.name, var14, var12.time});
               var13 = false;
            }

            if (var12.index < 0 || var12.index >= var7) {
               LOGGER.warn("Invalid frame index on sprite {} frame {}: {}", new Object[]{this.name, var14, var12.index});
               var13 = false;
            }

            if (var13) {
               var10.add(var12.index);
            } else {
               var11.remove();
            }
         }

         int[] var15 = IntStream.range(0, var7).filter(var1x -> !var10.contains(var1x)).toArray();
         if (var15.length > 0) {
            LOGGER.warn("Unused frames in sprite {}: {}", this.name, Arrays.toString(var15));
         }
      }

      return var8.size() <= 1 ? null : new SpriteContents.AnimatedTexture(ImmutableList.copyOf(var8), var5, var4.isInterpolatedFrames());
   }

   void upload(int var1, int var2, int var3, int var4, NativeImage[] var5) {
      for(int var6 = 0; var6 < this.byMipLevel.length; ++var6) {
         var5[var6]
            .upload(var6, var1 >> var6, var2 >> var6, var3 >> var6, var4 >> var6, this.width >> var6, this.height >> var6, this.byMipLevel.length > 1, false);
      }
   }

   @Override
   public int width() {
      return this.width;
   }

   @Override
   public int height() {
      return this.height;
   }

   @Override
   public ResourceLocation name() {
      return this.name;
   }

   public IntStream getUniqueFrames() {
      return this.animatedTexture != null ? this.animatedTexture.getUniqueFrames() : IntStream.of(1);
   }

   @Nullable
   public SpriteTicker createTicker() {
      return this.animatedTexture != null ? this.animatedTexture.createTicker() : null;
   }

   @Override
   public void close() {
      for(NativeImage var4 : this.byMipLevel) {
         var4.close();
      }
   }

   @Override
   public String toString() {
      return "SpriteContents{name=" + this.name + ", frameCount=" + this.getFrameCount() + ", height=" + this.height + ", width=" + this.width + "}";
   }

   public boolean isTransparent(int var1, int var2, int var3) {
      int var4 = var2;
      int var5 = var3;
      if (this.animatedTexture != null) {
         var4 = var2 + this.animatedTexture.getFrameX(var1) * this.width;
         var5 = var3 + this.animatedTexture.getFrameY(var1) * this.height;
      }

      return (this.originalImage.getPixelRGBA(var4, var5) >> 24 & 0xFF) == 0;
   }

   public void uploadFirstFrame(int var1, int var2) {
      if (this.animatedTexture != null) {
         this.animatedTexture.uploadFirstFrame(var1, var2);
      } else {
         this.upload(var1, var2, 0, 0, this.byMipLevel);
      }
   }

   class AnimatedTexture {
      final List<SpriteContents.FrameInfo> frames;
      private final int frameRowSize;
      private final boolean interpolateFrames;

      AnimatedTexture(List<SpriteContents.FrameInfo> var2, int var3, boolean var4) {
         super();
         this.frames = var2;
         this.frameRowSize = var3;
         this.interpolateFrames = var4;
      }

      int getFrameX(int var1) {
         return var1 % this.frameRowSize;
      }

      int getFrameY(int var1) {
         return var1 / this.frameRowSize;
      }

      void uploadFrame(int var1, int var2, int var3) {
         int var4 = this.getFrameX(var3) * SpriteContents.this.width;
         int var5 = this.getFrameY(var3) * SpriteContents.this.height;
         SpriteContents.this.upload(var1, var2, var4, var5, SpriteContents.this.byMipLevel);
      }

      public SpriteTicker createTicker() {
         return SpriteContents.this.new Ticker(this, this.interpolateFrames ? SpriteContents.this.new InterpolationData() : null);
      }

      public void uploadFirstFrame(int var1, int var2) {
         this.uploadFrame(var1, var2, this.frames.get(0).index);
      }

      public IntStream getUniqueFrames() {
         return this.frames.stream().mapToInt(var0 -> var0.index).distinct();
      }
   }

   static class FrameInfo {
      final int index;
      final int time;

      FrameInfo(int var1, int var2) {
         super();
         this.index = var1;
         this.time = var2;
      }
   }

   final class InterpolationData implements AutoCloseable {
      private final NativeImage[] activeFrame = new NativeImage[SpriteContents.this.byMipLevel.length];

      InterpolationData() {
         super();

         for(int var2 = 0; var2 < this.activeFrame.length; ++var2) {
            int var3 = SpriteContents.this.width >> var2;
            int var4 = SpriteContents.this.height >> var2;
            this.activeFrame[var2] = new NativeImage(var3, var4, false);
         }
      }

      void uploadInterpolatedFrame(int var1, int var2, SpriteContents.Ticker var3) {
         SpriteContents.AnimatedTexture var4 = var3.animationInfo;
         List var5 = var4.frames;
         SpriteContents.FrameInfo var6 = (SpriteContents.FrameInfo)var5.get(var3.frame);
         double var7 = 1.0 - (double)var3.subFrame / (double)var6.time;
         int var9 = var6.index;
         int var10 = ((SpriteContents.FrameInfo)var5.get((var3.frame + 1) % var5.size())).index;
         if (var9 != var10) {
            for(int var11 = 0; var11 < this.activeFrame.length; ++var11) {
               int var12 = SpriteContents.this.width >> var11;
               int var13 = SpriteContents.this.height >> var11;

               for(int var14 = 0; var14 < var13; ++var14) {
                  for(int var15 = 0; var15 < var12; ++var15) {
                     int var16 = this.getPixel(var4, var9, var11, var15, var14);
                     int var17 = this.getPixel(var4, var10, var11, var15, var14);
                     int var18 = this.mix(var7, var16 >> 16 & 0xFF, var17 >> 16 & 0xFF);
                     int var19 = this.mix(var7, var16 >> 8 & 0xFF, var17 >> 8 & 0xFF);
                     int var20 = this.mix(var7, var16 & 0xFF, var17 & 0xFF);
                     this.activeFrame[var11].setPixelRGBA(var15, var14, var16 & 0xFF000000 | var18 << 16 | var19 << 8 | var20);
                  }
               }
            }

            SpriteContents.this.upload(var1, var2, 0, 0, this.activeFrame);
         }
      }

      private int getPixel(SpriteContents.AnimatedTexture var1, int var2, int var3, int var4, int var5) {
         return SpriteContents.this.byMipLevel[var3]
            .getPixelRGBA(
               var4 + (var1.getFrameX(var2) * SpriteContents.this.width >> var3), var5 + (var1.getFrameY(var2) * SpriteContents.this.height >> var3)
            );
      }

      private int mix(double var1, int var3, int var4) {
         return (int)(var1 * (double)var3 + (1.0 - var1) * (double)var4);
      }

      @Override
      public void close() {
         for(NativeImage var4 : this.activeFrame) {
            var4.close();
         }
      }
   }

   class Ticker implements SpriteTicker {
      int frame;
      int subFrame;
      final SpriteContents.AnimatedTexture animationInfo;
      @Nullable
      private final SpriteContents.InterpolationData interpolationData;

      Ticker(SpriteContents.AnimatedTexture var2, @Nullable SpriteContents.InterpolationData var3) {
         super();
         this.animationInfo = var2;
         this.interpolationData = var3;
      }

      @Override
      public void tickAndUpload(int var1, int var2) {
         ++this.subFrame;
         SpriteContents.FrameInfo var3 = this.animationInfo.frames.get(this.frame);
         if (this.subFrame >= var3.time) {
            int var4 = var3.index;
            this.frame = (this.frame + 1) % this.animationInfo.frames.size();
            this.subFrame = 0;
            int var5 = this.animationInfo.frames.get(this.frame).index;
            if (var4 != var5) {
               this.animationInfo.uploadFrame(var1, var2, var5);
            }
         } else if (this.interpolationData != null) {
            if (!RenderSystem.isOnRenderThread()) {
               RenderSystem.recordRenderCall(() -> this.interpolationData.uploadInterpolatedFrame(var1, var2, this));
            } else {
               this.interpolationData.uploadInterpolatedFrame(var1, var2, this);
            }
         }
      }

      @Override
      public void close() {
         if (this.interpolationData != null) {
            this.interpolationData.close();
         }
      }
   }
}
