package net.minecraft.client.resources.metadata.animation;

import com.google.common.collect.Lists;
import java.util.List;

public class AnimationMetadataSection {
   public static final AnimationMetadataSectionSerializer SERIALIZER = new AnimationMetadataSectionSerializer();
   public static final String SECTION_NAME = "animation";
   public static final int DEFAULT_FRAME_TIME = 1;
   public static final int UNKNOWN_SIZE = -1;
   public static final AnimationMetadataSection EMPTY = new AnimationMetadataSection(Lists.newArrayList(), -1, -1, 1, false) {
      @Override
      public FrameSize calculateFrameSize(int var1, int var2) {
         return new FrameSize(var1, var2);
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

   public FrameSize calculateFrameSize(int var1, int var2) {
      if (this.frameWidth != -1) {
         return this.frameHeight != -1 ? new FrameSize(this.frameWidth, this.frameHeight) : new FrameSize(this.frameWidth, var2);
      } else if (this.frameHeight != -1) {
         return new FrameSize(var1, this.frameHeight);
      } else {
         int var3 = Math.min(var1, var2);
         return new FrameSize(var3, var3);
      }
   }

   public int getDefaultFrameTime() {
      return this.defaultFrameTime;
   }

   public boolean isInterpolatedFrames() {
      return this.interpolatedFrames;
   }

   public void forEachFrame(AnimationMetadataSection.FrameOutput var1) {
      for (AnimationFrame var3 : this.frames) {
         var1.accept(var3.getIndex(), var3.getTime(this.defaultFrameTime));
      }
   }

   @FunctionalInterface
   public interface FrameOutput {
      void accept(int var1, int var2);
   }
}
