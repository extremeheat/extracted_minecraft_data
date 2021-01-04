package net.minecraft.client.resources.metadata.animation;

import com.google.common.collect.Sets;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

public class AnimationMetadataSection {
   public static final AnimationMetadataSectionSerializer SERIALIZER = new AnimationMetadataSectionSerializer();
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

   public int getFrameHeight() {
      return this.frameHeight;
   }

   public int getFrameWidth() {
      return this.frameWidth;
   }

   public int getFrameCount() {
      return this.frames.size();
   }

   public int getDefaultFrameTime() {
      return this.defaultFrameTime;
   }

   public boolean isInterpolatedFrames() {
      return this.interpolatedFrames;
   }

   private AnimationFrame getFrame(int var1) {
      return (AnimationFrame)this.frames.get(var1);
   }

   public int getFrameTime(int var1) {
      AnimationFrame var2 = this.getFrame(var1);
      return var2.isTimeUnknown() ? this.defaultFrameTime : var2.getTime();
   }

   public int getFrameIndex(int var1) {
      return ((AnimationFrame)this.frames.get(var1)).getIndex();
   }

   public Set<Integer> getUniqueFrameIndices() {
      HashSet var1 = Sets.newHashSet();
      Iterator var2 = this.frames.iterator();

      while(var2.hasNext()) {
         AnimationFrame var3 = (AnimationFrame)var2.next();
         var1.add(var3.getIndex());
      }

      return var1;
   }
}
