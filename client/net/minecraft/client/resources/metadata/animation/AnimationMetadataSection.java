package net.minecraft.client.resources.metadata.animation;

import com.google.common.collect.Lists;
import com.mojang.datafixers.util.Pair;
import java.util.List;
import java.util.Locale;

public class AnimationMetadataSection {
   public static final AnimationMetadataSectionSerializer SERIALIZER = new AnimationMetadataSectionSerializer();
   public static final String SECTION_NAME = "animation";
   public static final int DEFAULT_FRAME_TIME = 1;
   public static final int UNKNOWN_SIZE = -1;
   public static final AnimationMetadataSection EMPTY = new AnimationMetadataSection(Lists.newArrayList(), -1, -1, 1, false) {
      @Override
      public Pair<Integer, Integer> getFrameSize(int var1, int var2) {
         return Pair.of(var1, var2);
      }
   };
   private final List<AnimationFrame> frames;
   private final int frameWidth;
   private final int frameHeight;
   private final int defaultFrameTime;
   private final boolean interpolatedFrames;

   public AnimationMetadataSection(List<AnimationFrame> var1, int var2, int var3, int var4, boolean var5) {
      super();
      this.frames = var1;
      this.frameWidth = var2;
      this.frameHeight = var3;
      this.defaultFrameTime = var4;
      this.interpolatedFrames = var5;
   }

   private static boolean isDivisionInteger(int var0, int var1) {
      return var0 / var1 * var1 == var0;
   }

   public Pair<Integer, Integer> getFrameSize(int var1, int var2) {
      Pair var3 = this.calculateFrameSize(var1, var2);
      int var4 = var3.getFirst();
      int var5 = var3.getSecond();
      if (isDivisionInteger(var1, var4) && isDivisionInteger(var2, var5)) {
         return var3;
      } else {
         throw new IllegalArgumentException(String.format(Locale.ROOT, "Image size %s,%s is not multiply of frame size %s,%s", var1, var2, var4, var5));
      }
   }

   private Pair<Integer, Integer> calculateFrameSize(int var1, int var2) {
      if (this.frameWidth != -1) {
         return this.frameHeight != -1 ? Pair.of(this.frameWidth, this.frameHeight) : Pair.of(this.frameWidth, var2);
      } else if (this.frameHeight != -1) {
         return Pair.of(var1, this.frameHeight);
      } else {
         int var3 = Math.min(var1, var2);
         return Pair.of(var3, var3);
      }
   }

   public int getFrameHeight(int var1) {
      return this.frameHeight == -1 ? var1 : this.frameHeight;
   }

   public int getFrameWidth(int var1) {
      return this.frameWidth == -1 ? var1 : this.frameWidth;
   }

   public int getDefaultFrameTime() {
      return this.defaultFrameTime;
   }

   public boolean isInterpolatedFrames() {
      return this.interpolatedFrames;
   }

   public void forEachFrame(AnimationMetadataSection.FrameOutput var1) {
      for(AnimationFrame var3 : this.frames) {
         var1.accept(var3.getIndex(), var3.getTime(this.defaultFrameTime));
      }
   }

   @FunctionalInterface
   public interface FrameOutput {
      void accept(int var1, int var2);
   }
}
