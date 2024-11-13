package net.minecraft.client.renderer.texture;

import com.google.common.collect.ImmutableList;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;

public class Stitcher<T extends Stitcher.Entry> {
   private static final Comparator<Holder<?>> HOLDER_COMPARATOR = Comparator.comparing((var0) -> -var0.height).thenComparing((var0) -> -var0.width).thenComparing((var0) -> var0.entry.name());
   private final int mipLevel;
   private final List<Holder<T>> texturesToBeStitched = new ArrayList();
   private final List<Region<T>> storage = new ArrayList();
   private int storageX;
   private int storageY;
   private final int maxWidth;
   private final int maxHeight;

   public Stitcher(int var1, int var2, int var3) {
      super();
      this.mipLevel = var3;
      this.maxWidth = var1;
      this.maxHeight = var2;
   }

   public int getWidth() {
      return this.storageX;
   }

   public int getHeight() {
      return this.storageY;
   }

   public void registerSprite(T var1) {
      Holder var2 = new Holder(var1, this.mipLevel);
      this.texturesToBeStitched.add(var2);
   }

   public void stitch() {
      ArrayList var1 = new ArrayList(this.texturesToBeStitched);
      var1.sort(HOLDER_COMPARATOR);

      for(Holder var3 : var1) {
         if (!this.addToStorage(var3)) {
            throw new StitcherException(var3.entry, (Collection)var1.stream().map((var0) -> var0.entry).collect(ImmutableList.toImmutableList()));
         }
      }

   }

   public void gatherSprites(SpriteLoader<T> var1) {
      for(Region var3 : this.storage) {
         var3.walk(var1);
      }

   }

   static int smallestFittingMinTexel(int var0, int var1) {
      return (var0 >> var1) + ((var0 & (1 << var1) - 1) == 0 ? 0 : 1) << var1;
   }

   private boolean addToStorage(Holder<T> var1) {
      for(Region var3 : this.storage) {
         if (var3.add(var1)) {
            return true;
         }
      }

      return this.expand(var1);
   }

   private boolean expand(Holder<T> var1) {
      int var3 = Mth.smallestEncompassingPowerOfTwo(this.storageX);
      int var4 = Mth.smallestEncompassingPowerOfTwo(this.storageY);
      int var5 = Mth.smallestEncompassingPowerOfTwo(this.storageX + var1.width);
      int var6 = Mth.smallestEncompassingPowerOfTwo(this.storageY + var1.height);
      boolean var7 = var5 <= this.maxWidth;
      boolean var8 = var6 <= this.maxHeight;
      if (!var7 && !var8) {
         return false;
      } else {
         boolean var9 = var7 && var3 != var5;
         boolean var10 = var8 && var4 != var6;
         boolean var2;
         if (var9 ^ var10) {
            var2 = var9;
         } else {
            var2 = var7 && var3 <= var4;
         }

         Region var11;
         if (var2) {
            if (this.storageY == 0) {
               this.storageY = var6;
            }

            var11 = new Region(this.storageX, 0, var5 - this.storageX, this.storageY);
            this.storageX = var5;
         } else {
            var11 = new Region(0, this.storageY, this.storageX, var6 - this.storageY);
            this.storageY = var6;
         }

         var11.add(var1);
         this.storage.add(var11);
         return true;
      }
   }

   static record Holder<T extends Entry>(T entry, int width, int height) {
      final T entry;
      final int width;
      final int height;

      public Holder(T var1, int var2) {
         this(var1, Stitcher.smallestFittingMinTexel(var1.width(), var2), Stitcher.smallestFittingMinTexel(var1.height(), var2));
      }

      private Holder(T var1, int var2, int var3) {
         super();
         this.entry = var1;
         this.width = var2;
         this.height = var3;
      }
   }

   public static class Region<T extends Entry> {
      private final int originX;
      private final int originY;
      private final int width;
      private final int height;
      @Nullable
      private List<Region<T>> subSlots;
      @Nullable
      private Holder<T> holder;

      public Region(int var1, int var2, int var3, int var4) {
         super();
         this.originX = var1;
         this.originY = var2;
         this.width = var3;
         this.height = var4;
      }

      public int getX() {
         return this.originX;
      }

      public int getY() {
         return this.originY;
      }

      public boolean add(Holder<T> var1) {
         if (this.holder != null) {
            return false;
         } else {
            int var2 = var1.width;
            int var3 = var1.height;
            if (var2 <= this.width && var3 <= this.height) {
               if (var2 == this.width && var3 == this.height) {
                  this.holder = var1;
                  return true;
               } else {
                  if (this.subSlots == null) {
                     this.subSlots = new ArrayList(1);
                     this.subSlots.add(new Region(this.originX, this.originY, var2, var3));
                     int var4 = this.width - var2;
                     int var5 = this.height - var3;
                     if (var5 > 0 && var4 > 0) {
                        int var6 = Math.max(this.height, var4);
                        int var7 = Math.max(this.width, var5);
                        if (var6 >= var7) {
                           this.subSlots.add(new Region(this.originX, this.originY + var3, var2, var5));
                           this.subSlots.add(new Region(this.originX + var2, this.originY, var4, this.height));
                        } else {
                           this.subSlots.add(new Region(this.originX + var2, this.originY, var4, var3));
                           this.subSlots.add(new Region(this.originX, this.originY + var3, this.width, var5));
                        }
                     } else if (var4 == 0) {
                        this.subSlots.add(new Region(this.originX, this.originY + var3, var2, var5));
                     } else if (var5 == 0) {
                        this.subSlots.add(new Region(this.originX + var2, this.originY, var4, var3));
                     }
                  }

                  for(Region var9 : this.subSlots) {
                     if (var9.add(var1)) {
                        return true;
                     }
                  }

                  return false;
               }
            } else {
               return false;
            }
         }
      }

      public void walk(SpriteLoader<T> var1) {
         if (this.holder != null) {
            var1.load(this.holder.entry, this.getX(), this.getY());
         } else if (this.subSlots != null) {
            for(Region var3 : this.subSlots) {
               var3.walk(var1);
            }
         }

      }

      public String toString() {
         int var10000 = this.originX;
         return "Slot{originX=" + var10000 + ", originY=" + this.originY + ", width=" + this.width + ", height=" + this.height + ", texture=" + String.valueOf(this.holder) + ", subSlots=" + String.valueOf(this.subSlots) + "}";
      }
   }

   public interface Entry {
      int width();

      int height();

      ResourceLocation name();
   }

   public interface SpriteLoader<T extends Entry> {
      void load(T var1, int var2, int var3);
   }
}
