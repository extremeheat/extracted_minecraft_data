package net.minecraft.client.renderer.texture;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;
import net.minecraft.util.Mth;

public class Stitcher {
   private static final Comparator<Stitcher.Holder> HOLDER_COMPARATOR = Comparator.<Stitcher.Holder, Integer>comparing(var0 -> -var0.height)
      .thenComparing(var0 -> -var0.width)
      .thenComparing(var0 -> var0.spriteInfo.name());
   private final int mipLevel;
   private final Set<Stitcher.Holder> texturesToBeStitched = Sets.newHashSetWithExpectedSize(256);
   private final List<Stitcher.Region> storage = Lists.newArrayListWithCapacity(256);
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

   public void registerSprite(TextureAtlasSprite.Info var1) {
      Stitcher.Holder var2 = new Stitcher.Holder(var1, this.mipLevel);
      this.texturesToBeStitched.add(var2);
   }

   public void stitch() {
      ArrayList var1 = Lists.newArrayList(this.texturesToBeStitched);
      var1.sort(HOLDER_COMPARATOR);

      for(Stitcher.Holder var3 : var1) {
         if (!this.addToStorage(var3)) {
            throw new StitcherException(var3.spriteInfo, var1.stream().map(var0 -> var0.spriteInfo).collect(ImmutableList.toImmutableList()));
         }
      }

      this.storageX = Mth.smallestEncompassingPowerOfTwo(this.storageX);
      this.storageY = Mth.smallestEncompassingPowerOfTwo(this.storageY);
   }

   public void gatherSprites(Stitcher.SpriteLoader var1) {
      for(Stitcher.Region var3 : this.storage) {
         var3.walk(var2 -> {
            Stitcher.Holder var3x = var2.getHolder();
            TextureAtlasSprite.Info var4 = var3x.spriteInfo;
            var1.load(var4, this.storageX, this.storageY, var2.getX(), var2.getY());
         });
      }
   }

   static int smallestFittingMinTexel(int var0, int var1) {
      return (var0 >> var1) + ((var0 & (1 << var1) - 1) == 0 ? 0 : 1) << var1;
   }

   private boolean addToStorage(Stitcher.Holder var1) {
      for(Stitcher.Region var3 : this.storage) {
         if (var3.add(var1)) {
            return true;
         }
      }

      return this.expand(var1);
   }

   private boolean expand(Stitcher.Holder var1) {
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

         Stitcher.Region var11;
         if (var2) {
            if (this.storageY == 0) {
               this.storageY = var1.height;
            }

            var11 = new Stitcher.Region(this.storageX, 0, var1.width, this.storageY);
            this.storageX += var1.width;
         } else {
            var11 = new Stitcher.Region(0, this.storageY, this.storageX, var1.height);
            this.storageY += var1.height;
         }

         var11.add(var1);
         this.storage.add(var11);
         return true;
      }
   }

   static class Holder {
      public final TextureAtlasSprite.Info spriteInfo;
      public final int width;
      public final int height;

      public Holder(TextureAtlasSprite.Info var1, int var2) {
         super();
         this.spriteInfo = var1;
         this.width = Stitcher.smallestFittingMinTexel(var1.width(), var2);
         this.height = Stitcher.smallestFittingMinTexel(var1.height(), var2);
      }

      @Override
      public String toString() {
         return "Holder{width=" + this.width + ", height=" + this.height + "}";
      }
   }

   public static class Region {
      private final int originX;
      private final int originY;
      private final int width;
      private final int height;
      private List<Stitcher.Region> subSlots;
      private Stitcher.Holder holder;

      public Region(int var1, int var2, int var3, int var4) {
         super();
         this.originX = var1;
         this.originY = var2;
         this.width = var3;
         this.height = var4;
      }

      public Stitcher.Holder getHolder() {
         return this.holder;
      }

      public int getX() {
         return this.originX;
      }

      public int getY() {
         return this.originY;
      }

      public boolean add(Stitcher.Holder var1) {
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
                     this.subSlots = Lists.newArrayListWithCapacity(1);
                     this.subSlots.add(new Stitcher.Region(this.originX, this.originY, var2, var3));
                     int var4 = this.width - var2;
                     int var5 = this.height - var3;
                     if (var5 > 0 && var4 > 0) {
                        int var6 = Math.max(this.height, var4);
                        int var7 = Math.max(this.width, var5);
                        if (var6 >= var7) {
                           this.subSlots.add(new Stitcher.Region(this.originX, this.originY + var3, var2, var5));
                           this.subSlots.add(new Stitcher.Region(this.originX + var2, this.originY, var4, this.height));
                        } else {
                           this.subSlots.add(new Stitcher.Region(this.originX + var2, this.originY, var4, var3));
                           this.subSlots.add(new Stitcher.Region(this.originX, this.originY + var3, this.width, var5));
                        }
                     } else if (var4 == 0) {
                        this.subSlots.add(new Stitcher.Region(this.originX, this.originY + var3, var2, var5));
                     } else if (var5 == 0) {
                        this.subSlots.add(new Stitcher.Region(this.originX + var2, this.originY, var4, var3));
                     }
                  }

                  for(Stitcher.Region var9 : this.subSlots) {
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

      public void walk(Consumer<Stitcher.Region> var1) {
         if (this.holder != null) {
            var1.accept(this);
         } else if (this.subSlots != null) {
            for(Stitcher.Region var3 : this.subSlots) {
               var3.walk(var1);
            }
         }
      }

      @Override
      public String toString() {
         return "Slot{originX="
            + this.originX
            + ", originY="
            + this.originY
            + ", width="
            + this.width
            + ", height="
            + this.height
            + ", texture="
            + this.holder
            + ", subSlots="
            + this.subSlots
            + "}";
      }
   }

   public interface SpriteLoader {
      void load(TextureAtlasSprite.Info var1, int var2, int var3, int var4, int var5);
   }
}
