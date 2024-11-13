package net.minecraft.client.renderer.texture;

import com.mojang.blaze3d.platform.NativeImage;
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
import net.minecraft.CrashReportDetail;
import net.minecraft.ReportedException;
import net.minecraft.client.resources.metadata.animation.AnimationFrame;
import net.minecraft.client.resources.metadata.animation.AnimationMetadataSection;
import net.minecraft.client.resources.metadata.animation.FrameSize;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceMetadata;
import net.minecraft.util.ARGB;
import org.slf4j.Logger;

public class SpriteContents implements Stitcher.Entry, AutoCloseable {
   private static final Logger LOGGER = LogUtils.getLogger();
   private final ResourceLocation name;
   final int width;
   final int height;
   private final NativeImage originalImage;
   NativeImage[] byMipLevel;
   @Nullable
   private final AnimatedTexture animatedTexture;
   private final ResourceMetadata metadata;

   public SpriteContents(ResourceLocation var1, FrameSize var2, NativeImage var3, ResourceMetadata var4) {
      super();
      this.name = var1;
      this.width = var2.width();
      this.height = var2.height();
      this.metadata = var4;
      this.animatedTexture = (AnimatedTexture)var4.getSection(AnimationMetadataSection.TYPE).map((var3x) -> this.createAnimatedTexture(var2, var3.getWidth(), var3.getHeight(), var3x)).orElse((Object)null);
      this.originalImage = var3;
      this.byMipLevel = new NativeImage[]{this.originalImage};
   }

   public void increaseMipLevel(int var1) {
      try {
         this.byMipLevel = MipmapGenerator.generateMipLevels(this.byMipLevel, var1);
      } catch (Throwable var6) {
         CrashReport var3 = CrashReport.forThrowable(var6, "Generating mipmaps for frame");
         CrashReportCategory var4 = var3.addCategory("Sprite being mipmapped");
         var4.setDetail("First frame", (CrashReportDetail)(() -> {
            StringBuilder var1 = new StringBuilder();
            if (var1.length() > 0) {
               var1.append(", ");
            }

            var1.append(this.originalImage.getWidth()).append("x").append(this.originalImage.getHeight());
            return var1.toString();
         }));
         CrashReportCategory var5 = var3.addCategory("Frame being iterated");
         var5.setDetail("Sprite name", this.name);
         var5.setDetail("Sprite size", (CrashReportDetail)(() -> this.width + " x " + this.height));
         var5.setDetail("Sprite frames", (CrashReportDetail)(() -> this.getFrameCount() + " frames"));
         var5.setDetail("Mipmap levels", var1);
         throw new ReportedException(var3);
      }
   }

   private int getFrameCount() {
      return this.animatedTexture != null ? this.animatedTexture.frames.size() : 1;
   }

   @Nullable
   private AnimatedTexture createAnimatedTexture(FrameSize var1, int var2, int var3, AnimationMetadataSection var4) {
      int var5 = var2 / var1.width();
      int var6 = var3 / var1.height();
      int var7 = var5 * var6;
      int var8 = var4.defaultFrameTime();
      ArrayList var9;
      if (var4.frames().isEmpty()) {
         var9 = new ArrayList(var7);

         for(int var10 = 0; var10 < var7; ++var10) {
            var9.add(new FrameInfo(var10, var8));
         }
      } else {
         List var16 = (List)var4.frames().get();
         var9 = new ArrayList(var16.size());

         for(AnimationFrame var12 : var16) {
            var9.add(new FrameInfo(var12.index(), var12.timeOr(var8)));
         }

         int var17 = 0;
         IntOpenHashSet var18 = new IntOpenHashSet();

         for(Iterator var13 = var9.iterator(); var13.hasNext(); ++var17) {
            FrameInfo var14 = (FrameInfo)var13.next();
            boolean var15 = true;
            if (var14.time <= 0) {
               LOGGER.warn("Invalid frame duration on sprite {} frame {}: {}", new Object[]{this.name, var17, var14.time});
               var15 = false;
            }

            if (var14.index < 0 || var14.index >= var7) {
               LOGGER.warn("Invalid frame index on sprite {} frame {}: {}", new Object[]{this.name, var17, var14.index});
               var15 = false;
            }

            if (var15) {
               var18.add(var14.index);
            } else {
               var13.remove();
            }
         }

         int[] var19 = IntStream.range(0, var7).filter((var1x) -> !var18.contains(var1x)).toArray();
         if (var19.length > 0) {
            LOGGER.warn("Unused frames in sprite {}: {}", this.name, Arrays.toString(var19));
         }
      }

      return var9.size() <= 1 ? null : new AnimatedTexture(List.copyOf(var9), var5, var4.interpolatedFrames());
   }

   void upload(int var1, int var2, int var3, int var4, NativeImage[] var5) {
      for(int var6 = 0; var6 < this.byMipLevel.length; ++var6) {
         var5[var6].upload(var6, var1 >> var6, var2 >> var6, var3 >> var6, var4 >> var6, this.width >> var6, this.height >> var6, this.byMipLevel.length > 1, false);
      }

   }

   public int width() {
      return this.width;
   }

   public int height() {
      return this.height;
   }

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

   public ResourceMetadata metadata() {
      return this.metadata;
   }

   public void close() {
      for(NativeImage var4 : this.byMipLevel) {
         var4.close();
      }

   }

   public String toString() {
      String var10000 = String.valueOf(this.name);
      return "SpriteContents{name=" + var10000 + ", frameCount=" + this.getFrameCount() + ", height=" + this.height + ", width=" + this.width + "}";
   }

   public boolean isTransparent(int var1, int var2, int var3) {
      int var4 = var2;
      int var5 = var3;
      if (this.animatedTexture != null) {
         var4 = var2 + this.animatedTexture.getFrameX(var1) * this.width;
         var5 = var3 + this.animatedTexture.getFrameY(var1) * this.height;
      }

      return ARGB.alpha(this.originalImage.getPixel(var4, var5)) == 0;
   }

   public void uploadFirstFrame(int var1, int var2) {
      if (this.animatedTexture != null) {
         this.animatedTexture.uploadFirstFrame(var1, var2);
      } else {
         this.upload(var1, var2, 0, 0, this.byMipLevel);
      }

   }

   final class InterpolationData implements AutoCloseable {
      private final NativeImage[] activeFrame;

      InterpolationData() {
         super();
         this.activeFrame = new NativeImage[SpriteContents.this.byMipLevel.length];

         for(int var2 = 0; var2 < this.activeFrame.length; ++var2) {
            int var3 = SpriteContents.this.width >> var2;
            int var4 = SpriteContents.this.height >> var2;
            this.activeFrame[var2] = new NativeImage(var3, var4, false);
         }

      }

      void uploadInterpolatedFrame(int var1, int var2, Ticker var3) {
         AnimatedTexture var4 = var3.animationInfo;
         List var5 = var4.frames;
         FrameInfo var6 = (FrameInfo)var5.get(var3.frame);
         float var7 = (float)var3.subFrame / (float)var6.time;
         int var8 = var6.index;
         int var9 = ((FrameInfo)var5.get((var3.frame + 1) % var5.size())).index;
         if (var8 != var9) {
            for(int var10 = 0; var10 < this.activeFrame.length; ++var10) {
               int var11 = SpriteContents.this.width >> var10;
               int var12 = SpriteContents.this.height >> var10;

               for(int var13 = 0; var13 < var12; ++var13) {
                  for(int var14 = 0; var14 < var11; ++var14) {
                     int var15 = this.getPixel(var4, var8, var10, var14, var13);
                     int var16 = this.getPixel(var4, var9, var10, var14, var13);
                     this.activeFrame[var10].setPixel(var14, var13, ARGB.lerp(var7, var15, var16));
                  }
               }
            }

            SpriteContents.this.upload(var1, var2, 0, 0, this.activeFrame);
         }

      }

      private int getPixel(AnimatedTexture var1, int var2, int var3, int var4, int var5) {
         return SpriteContents.this.byMipLevel[var3].getPixel(var4 + (var1.getFrameX(var2) * SpriteContents.this.width >> var3), var5 + (var1.getFrameY(var2) * SpriteContents.this.height >> var3));
      }

      public void close() {
         for(NativeImage var4 : this.activeFrame) {
            var4.close();
         }

      }
   }

   static record FrameInfo(int index, int time) {
      final int index;
      final int time;

      FrameInfo(int var1, int var2) {
         super();
         this.index = var1;
         this.time = var2;
      }
   }

   class AnimatedTexture {
      final List<FrameInfo> frames;
      private final int frameRowSize;
      private final boolean interpolateFrames;

      AnimatedTexture(final List<FrameInfo> var2, final int var3, final boolean var4) {
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
         this.uploadFrame(var1, var2, ((FrameInfo)this.frames.get(0)).index);
      }

      public IntStream getUniqueFrames() {
         return this.frames.stream().mapToInt((var0) -> var0.index).distinct();
      }
   }

   class Ticker implements SpriteTicker {
      int frame;
      int subFrame;
      final AnimatedTexture animationInfo;
      @Nullable
      private final InterpolationData interpolationData;

      Ticker(final AnimatedTexture var2, @Nullable final InterpolationData var3) {
         super();
         this.animationInfo = var2;
         this.interpolationData = var3;
      }

      public void tickAndUpload(int var1, int var2) {
         ++this.subFrame;
         FrameInfo var3 = (FrameInfo)this.animationInfo.frames.get(this.frame);
         if (this.subFrame >= var3.time) {
            int var4 = var3.index;
            this.frame = (this.frame + 1) % this.animationInfo.frames.size();
            this.subFrame = 0;
            int var5 = ((FrameInfo)this.animationInfo.frames.get(this.frame)).index;
            if (var4 != var5) {
               this.animationInfo.uploadFrame(var1, var2, var5);
            }
         } else if (this.interpolationData != null) {
            this.interpolationData.uploadInterpolatedFrame(var1, var2, this);
         }

      }

      public void close() {
         if (this.interpolationData != null) {
            this.interpolationData.close();
         }

      }
   }
}
