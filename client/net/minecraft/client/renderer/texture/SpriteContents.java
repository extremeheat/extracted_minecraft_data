package net.minecraft.client.renderer.texture;

import com.mojang.blaze3d.platform.NativeImage;
import com.mojang.blaze3d.platform.TextureUtil;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.textures.GpuTexture;
import com.mojang.logging.LogUtils;
import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.stream.IntStream;
import javax.annotation.Nullable;
import net.minecraft.CrashReport;
import net.minecraft.CrashReportCategory;
import net.minecraft.CrashReportDetail;
import net.minecraft.ReportedException;
import net.minecraft.SharedConstants;
import net.minecraft.client.resources.metadata.animation.AnimationFrame;
import net.minecraft.client.resources.metadata.animation.AnimationMetadataSection;
import net.minecraft.client.resources.metadata.animation.FrameSize;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.metadata.MetadataSectionType;
import net.minecraft.util.ARGB;
import org.slf4j.Logger;

public class SpriteContents implements Stitcher.Entry, AutoCloseable {
   private static final Logger LOGGER = LogUtils.getLogger();
   private static final int MAX_ANIMATED_MIP_LEVELS = 2;
   final ResourceLocation name;
   final int width;
   final int height;
   private final NativeImage originalImage;
   NativeImage[] byMipLevel;
   @Nullable
   private final AnimatedTexture animatedTexture;
   private final List<MetadataSectionType.WithValue<?>> additionalMetadata;
   private final boolean darkenedCutoutMipmap;

   public SpriteContents(ResourceLocation var1, FrameSize var2, NativeImage var3) {
      this(var1, var2, var3, Optional.empty(), List.of(), false);
   }

   public SpriteContents(ResourceLocation var1, FrameSize var2, NativeImage var3, Optional<AnimationMetadataSection> var4, List<MetadataSectionType.WithValue<?>> var5, boolean var6) {
      super();
      this.name = var1;
      this.width = var2.width();
      this.height = var2.height();
      this.additionalMetadata = var5;
      this.animatedTexture = (AnimatedTexture)var4.map((var3x) -> this.createAnimatedTexture(var2, var3.getWidth(), var3.getHeight(), var3x)).orElse((Object)null);
      this.originalImage = var3;
      this.byMipLevel = new NativeImage[]{this.originalImage};
      this.darkenedCutoutMipmap = var6;
   }

   public void increaseMipLevel(int var1) {
      try {
         this.byMipLevel = MipmapGenerator.generateMipLevels(this.byMipLevel, var1, this.darkenedCutoutMipmap);
      } catch (Throwable var5) {
         CrashReport var3 = CrashReport.forThrowable(var5, "Generating mipmaps for frame");
         CrashReportCategory var4 = var3.addCategory("Frame being iterated");
         var4.setDetail("Sprite name", this.name);
         var4.setDetail("Sprite size", (CrashReportDetail)(() -> this.width + " x " + this.height));
         var4.setDetail("Sprite frames", (CrashReportDetail)(() -> this.getFrameCount() + " frames"));
         var4.setDetail("Mipmap levels", var1);
         var4.setDetail("Original image size", (CrashReportDetail)(() -> {
            int var10000 = this.originalImage.getWidth();
            return var10000 + "x" + this.originalImage.getHeight();
         }));
         throw new ReportedException(var3);
      }
   }

   private int getFrameCount() {
      return this.animatedTexture != null ? this.animatedTexture.frames.size() : 1;
   }

   public boolean isAnimated() {
      return this.getFrameCount() > 1;
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

   void upload(int var1, int var2, int var3, int var4, NativeImage[] var5, GpuTexture var6, int var7) {
      for(int var8 = 0; var8 < Math.min(this.byMipLevel.length, var7); ++var8) {
         RenderSystem.getDevice().createCommandEncoder().writeToTexture(var6, var5[var8], var8, 0, var1 >> var8, var2 >> var8, this.width >> var8, this.height >> var8, var3 >> var8, var4 >> var8);
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

   public <T> Optional<T> getAdditionalMetadata(MetadataSectionType<T> var1) {
      for(MetadataSectionType.WithValue var3 : this.additionalMetadata) {
         Optional var4 = var3.unwrapToType(var1);
         if (var4.isPresent()) {
            return var4;
         }
      }

      return Optional.empty();
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

   public void uploadFirstFrame(int var1, int var2, GpuTexture var3) {
      if (this.animatedTexture != null) {
         this.animatedTexture.uploadFirstFrame(var1, var2, var3);
      } else {
         this.upload(var1, var2, 0, 0, this.byMipLevel, var3, this.byMipLevel.length);
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

      void uploadInterpolatedFrame(int var1, int var2, Ticker var3, GpuTexture var4) {
         AnimatedTexture var5 = var3.animationInfo;
         List var6 = var5.frames;
         FrameInfo var7 = (FrameInfo)var6.get(var3.frame);
         float var8 = (float)var3.subFrame / (float)var7.time;
         int var9 = var7.index;
         int var10 = ((FrameInfo)var6.get((var3.frame + 1) % var6.size())).index;
         if (var9 != var10) {
            for(int var11 = 0; var11 < this.activeFrame.length; ++var11) {
               int var12 = SpriteContents.this.width >> var11;
               int var13 = SpriteContents.this.height >> var11;

               for(int var14 = 0; var14 < var13; ++var14) {
                  for(int var15 = 0; var15 < var12; ++var15) {
                     int var16 = this.getPixel(var5, var9, var11, var15, var14);
                     int var17 = this.getPixel(var5, var10, var11, var15, var14);
                     this.activeFrame[var11].setPixel(var15, var14, ARGB.lerp(var8, var16, var17));
                  }
               }
            }

            SpriteContents.this.upload(var1, var2, 0, 0, this.activeFrame, var4, 2);
            if (SharedConstants.DEBUG_DUMP_INTERPOLATED_TEXTURE_FRAMES) {
               try {
                  Path var19 = TextureUtil.getDebugTexturePath();
                  Path var20 = var19.resolve(SpriteContents.this.name.toDebugFileName());
                  Files.createDirectories(var20);

                  for(int var21 = 0; var21 < this.activeFrame.length; ++var21) {
                     this.activeFrame[var21].writeToFile(var20.resolve(SpriteContents.this.name.toDebugFileName() + "_" + var21 + "_" + var9 + "_" + var10 + ".png"));
                  }
               } catch (IOException var18) {
               }
            }
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

      void uploadFrame(int var1, int var2, int var3, GpuTexture var4, int var5) {
         int var6 = this.getFrameX(var3) * SpriteContents.this.width;
         int var7 = this.getFrameY(var3) * SpriteContents.this.height;
         SpriteContents.this.upload(var1, var2, var6, var7, SpriteContents.this.byMipLevel, var4, var5);
      }

      public SpriteTicker createTicker() {
         return SpriteContents.this.new Ticker(this, this.interpolateFrames ? SpriteContents.this.new InterpolationData() : null);
      }

      public void uploadFirstFrame(int var1, int var2, GpuTexture var3) {
         this.uploadFrame(var1, var2, ((FrameInfo)this.frames.get(0)).index, var3, var3.getMipLevels());
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

      public void tickAndUpload(int var1, int var2, GpuTexture var3) {
         ++this.subFrame;
         FrameInfo var4 = (FrameInfo)this.animationInfo.frames.get(this.frame);
         if (this.subFrame >= var4.time) {
            int var5 = var4.index;
            this.frame = (this.frame + 1) % this.animationInfo.frames.size();
            this.subFrame = 0;
            int var6 = ((FrameInfo)this.animationInfo.frames.get(this.frame)).index;
            if (var5 != var6) {
               this.animationInfo.uploadFrame(var1, var2, var6, var3, 2);
            }
         } else if (this.interpolationData != null) {
            this.interpolationData.uploadInterpolatedFrame(var1, var2, this, var3);
         }

      }

      public void close() {
         if (this.interpolationData != null) {
            this.interpolationData.close();
         }

      }
   }
}
