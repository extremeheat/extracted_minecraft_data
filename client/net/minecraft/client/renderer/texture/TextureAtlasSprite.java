package net.minecraft.client.renderer.texture;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.mojang.blaze3d.platform.NativeImage;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.VertexConsumer;
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
import net.minecraft.client.renderer.SpriteCoordinateExpander;
import net.minecraft.client.resources.metadata.animation.AnimationMetadataSection;
import net.minecraft.resources.ResourceLocation;
import org.slf4j.Logger;

public class TextureAtlasSprite implements AutoCloseable {
   private static final Logger LOGGER = LogUtils.getLogger();
   private final TextureAtlas atlas;
   private final ResourceLocation name;
   final int width;
   final int height;
   protected final NativeImage[] mainImage;
   @Nullable
   private final TextureAtlasSprite.AnimatedTexture animatedTexture;
   private final int x;
   private final int y;
   private final float u0;
   private final float u1;
   private final float v0;
   private final float v1;

   protected TextureAtlasSprite(TextureAtlas var1, TextureAtlasSprite.Info var2, int var3, int var4, int var5, int var6, int var7, NativeImage var8) {
      super();
      this.atlas = var1;
      this.width = var2.width;
      this.height = var2.height;
      this.name = var2.name;
      this.x = var6;
      this.y = var7;
      this.u0 = (float)var6 / (float)var4;
      this.u1 = (float)(var6 + this.width) / (float)var4;
      this.v0 = (float)var7 / (float)var5;
      this.v1 = (float)(var7 + this.height) / (float)var5;
      this.animatedTexture = this.createTicker(var2, var8.getWidth(), var8.getHeight(), var3);

      try {
         try {
            this.mainImage = MipmapGenerator.generateMipLevels(var8, var3);
         } catch (Throwable var12) {
            CrashReport var14 = CrashReport.forThrowable(var12, "Generating mipmaps for frame");
            CrashReportCategory var15 = var14.addCategory("Frame being iterated");
            var15.setDetail("First frame", () -> {
               StringBuilder var1x = new StringBuilder();
               if (var1x.length() > 0) {
                  var1x.append(", ");
               }

               var1x.append(var8.getWidth()).append("x").append(var8.getHeight());
               return var1x.toString();
            });
            throw new ReportedException(var14);
         }
      } catch (Throwable var13) {
         CrashReport var10 = CrashReport.forThrowable(var13, "Applying mipmap");
         CrashReportCategory var11 = var10.addCategory("Sprite being mipmapped");
         var11.setDetail("Sprite name", this.name::toString);
         var11.setDetail("Sprite size", () -> this.width + " x " + this.height);
         var11.setDetail("Sprite frames", () -> this.getFrameCount() + " frames");
         var11.setDetail("Mipmap levels", var3);
         throw new ReportedException(var10);
      }
   }

   private int getFrameCount() {
      return this.animatedTexture != null ? this.animatedTexture.frames.size() : 1;
   }

   @Nullable
   private TextureAtlasSprite.AnimatedTexture createTicker(TextureAtlasSprite.Info var1, int var2, int var3, int var4) {
      AnimationMetadataSection var5 = var1.metadata;
      int var6 = var2 / var5.getFrameWidth(var1.width);
      int var7 = var3 / var5.getFrameHeight(var1.height);
      int var8 = var6 * var7;
      ArrayList var9 = Lists.newArrayList();
      var5.forEachFrame((var1x, var2x) -> var9.add(new TextureAtlasSprite.FrameInfo(var1x, var2x)));
      if (var9.isEmpty()) {
         for(int var10 = 0; var10 < var8; ++var10) {
            var9.add(new TextureAtlasSprite.FrameInfo(var10, var5.getDefaultFrameTime()));
         }
      } else {
         int var15 = 0;
         IntOpenHashSet var11 = new IntOpenHashSet();

         for(Iterator var12 = var9.iterator(); var12.hasNext(); ++var15) {
            TextureAtlasSprite.FrameInfo var13 = (TextureAtlasSprite.FrameInfo)var12.next();
            boolean var14 = true;
            if (var13.time <= 0) {
               LOGGER.warn("Invalid frame duration on sprite {} frame {}: {}", new Object[]{this.name, var15, var13.time});
               var14 = false;
            }

            if (var13.index < 0 || var13.index >= var8) {
               LOGGER.warn("Invalid frame index on sprite {} frame {}: {}", new Object[]{this.name, var15, var13.index});
               var14 = false;
            }

            if (var14) {
               var11.add(var13.index);
            } else {
               var12.remove();
            }
         }

         int[] var17 = IntStream.range(0, var8).filter(var1x -> !var11.contains(var1x)).toArray();
         if (var17.length > 0) {
            LOGGER.warn("Unused frames in sprite {}: {}", this.name, Arrays.toString(var17));
         }
      }

      if (var9.size() <= 1) {
         return null;
      } else {
         TextureAtlasSprite.InterpolationData var16 = var5.isInterpolatedFrames() ? new TextureAtlasSprite.InterpolationData(var1, var4) : null;
         return new TextureAtlasSprite.AnimatedTexture(ImmutableList.copyOf(var9), var6, var16);
      }
   }

   void upload(int var1, int var2, NativeImage[] var3) {
      for(int var4 = 0; var4 < this.mainImage.length; ++var4) {
         var3[var4]
            .upload(
               var4, this.x >> var4, this.y >> var4, var1 >> var4, var2 >> var4, this.width >> var4, this.height >> var4, this.mainImage.length > 1, false
            );
      }
   }

   public int getX() {
      return this.x;
   }

   public int getY() {
      return this.y;
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

   public TextureAtlas atlas() {
      return this.atlas;
   }

   public IntStream getUniqueFrames() {
      return this.animatedTexture != null ? this.animatedTexture.getUniqueFrames() : IntStream.of(1);
   }

   @Override
   public void close() {
      for(NativeImage var4 : this.mainImage) {
         if (var4 != null) {
            var4.close();
         }
      }

      if (this.animatedTexture != null) {
         this.animatedTexture.close();
      }
   }

   @Override
   public String toString() {
      return "TextureAtlasSprite{name='"
         + this.name
         + "', frameCount="
         + this.getFrameCount()
         + ", x="
         + this.x
         + ", y="
         + this.y
         + ", height="
         + this.height
         + ", width="
         + this.width
         + ", u0="
         + this.u0
         + ", u1="
         + this.u1
         + ", v0="
         + this.v0
         + ", v1="
         + this.v1
         + "}";
   }

   public boolean isTransparent(int var1, int var2, int var3) {
      int var4 = var2;
      int var5 = var3;
      if (this.animatedTexture != null) {
         var4 = var2 + this.animatedTexture.getFrameX(var1) * this.width;
         var5 = var3 + this.animatedTexture.getFrameY(var1) * this.height;
      }

      return (this.mainImage[0].getPixelRGBA(var4, var5) >> 24 & 0xFF) == 0;
   }

   public void uploadFirstFrame() {
      if (this.animatedTexture != null) {
         this.animatedTexture.uploadFirstFrame();
      } else {
         this.upload(0, 0, this.mainImage);
      }
   }

   private float atlasSize() {
      float var1 = (float)this.width / (this.u1 - this.u0);
      float var2 = (float)this.height / (this.v1 - this.v0);
      return Math.max(var2, var1);
   }

   public float uvShrinkRatio() {
      return 4.0F / this.atlasSize();
   }

   @Nullable
   public Tickable getAnimationTicker() {
      return this.animatedTexture;
   }

   public VertexConsumer wrap(VertexConsumer var1) {
      return new SpriteCoordinateExpander(var1, this);
   }

   class AnimatedTexture implements Tickable, AutoCloseable {
      int frame;
      int subFrame;
      final List<TextureAtlasSprite.FrameInfo> frames;
      private final int frameRowSize;
      @Nullable
      private final TextureAtlasSprite.InterpolationData interpolationData;

      AnimatedTexture(List<TextureAtlasSprite.FrameInfo> var2, int var3, @Nullable TextureAtlasSprite.InterpolationData var4) {
         super();
         this.frames = var2;
         this.frameRowSize = var3;
         this.interpolationData = var4;
      }

      int getFrameX(int var1) {
         return var1 % this.frameRowSize;
      }

      int getFrameY(int var1) {
         return var1 / this.frameRowSize;
      }

      private void uploadFrame(int var1) {
         int var2 = this.getFrameX(var1) * TextureAtlasSprite.this.width;
         int var3 = this.getFrameY(var1) * TextureAtlasSprite.this.height;
         TextureAtlasSprite.this.upload(var2, var3, TextureAtlasSprite.this.mainImage);
      }

      @Override
      public void close() {
         if (this.interpolationData != null) {
            this.interpolationData.close();
         }
      }

      @Override
      public void tick() {
         ++this.subFrame;
         TextureAtlasSprite.FrameInfo var1 = this.frames.get(this.frame);
         if (this.subFrame >= var1.time) {
            int var2 = var1.index;
            this.frame = (this.frame + 1) % this.frames.size();
            this.subFrame = 0;
            int var3 = this.frames.get(this.frame).index;
            if (var2 != var3) {
               this.uploadFrame(var3);
            }
         } else if (this.interpolationData != null) {
            if (!RenderSystem.isOnRenderThread()) {
               RenderSystem.recordRenderCall(() -> this.interpolationData.uploadInterpolatedFrame(this));
            } else {
               this.interpolationData.uploadInterpolatedFrame(this);
            }
         }
      }

      public void uploadFirstFrame() {
         this.uploadFrame(this.frames.get(0).index);
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

   public static final class Info {
      final ResourceLocation name;
      final int width;
      final int height;
      final AnimationMetadataSection metadata;

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

   final class InterpolationData implements AutoCloseable {
      private final NativeImage[] activeFrame;

      InterpolationData(TextureAtlasSprite.Info var2, int var3) {
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

      void uploadInterpolatedFrame(TextureAtlasSprite.AnimatedTexture var1) {
         TextureAtlasSprite.FrameInfo var2 = var1.frames.get(var1.frame);
         double var3 = 1.0 - (double)var1.subFrame / (double)var2.time;
         int var5 = var2.index;
         int var6 = var1.frames.get((var1.frame + 1) % var1.frames.size()).index;
         if (var5 != var6) {
            for(int var7 = 0; var7 < this.activeFrame.length; ++var7) {
               int var8 = TextureAtlasSprite.this.width >> var7;
               int var9 = TextureAtlasSprite.this.height >> var7;

               for(int var10 = 0; var10 < var9; ++var10) {
                  for(int var11 = 0; var11 < var8; ++var11) {
                     int var12 = this.getPixel(var1, var5, var7, var11, var10);
                     int var13 = this.getPixel(var1, var6, var7, var11, var10);
                     int var14 = this.mix(var3, var12 >> 16 & 0xFF, var13 >> 16 & 0xFF);
                     int var15 = this.mix(var3, var12 >> 8 & 0xFF, var13 >> 8 & 0xFF);
                     int var16 = this.mix(var3, var12 & 0xFF, var13 & 0xFF);
                     this.activeFrame[var7].setPixelRGBA(var11, var10, var12 & 0xFF000000 | var14 << 16 | var15 << 8 | var16);
                  }
               }
            }

            TextureAtlasSprite.this.upload(0, 0, this.activeFrame);
         }
      }

      private int getPixel(TextureAtlasSprite.AnimatedTexture var1, int var2, int var3, int var4, int var5) {
         return TextureAtlasSprite.this.mainImage[var3]
            .getPixelRGBA(
               var4 + (var1.getFrameX(var2) * TextureAtlasSprite.this.width >> var3), var5 + (var1.getFrameY(var2) * TextureAtlasSprite.this.height >> var3)
            );
      }

      private int mix(double var1, int var3, int var4) {
         return (int)(var1 * (double)var3 + (1.0 - var1) * (double)var4);
      }

      @Override
      public void close() {
         for(NativeImage var4 : this.activeFrame) {
            if (var4 != null) {
               var4.close();
            }
         }
      }
   }
}
