package net.minecraft.client.resources.metadata.animation;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.mojang.datafixers.util.Pair;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

public class AnimationMetadataSection {
   public static final AnimationMetadataSectionSerializer SERIALIZER = new AnimationMetadataSectionSerializer();
   public static final AnimationMetadataSection EMPTY = new AnimationMetadataSection(Lists.newArrayList(), -1, -1, 1, false) {
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
      int var4 = (Integer)var3.getFirst();
      int var5 = (Integer)var3.getSecond();
      if (isDivisionInteger(var1, var4) && isDivisionInteger(var2, var5)) {
         return var3;
      } else {
         throw new IllegalArgumentException(String.format("Image size %s,%s is not multiply of frame size %s,%s", var1, var2, var4, var5));
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
