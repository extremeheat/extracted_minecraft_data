package net.minecraft.world.item;

import java.util.Iterator;
import java.util.List;
import net.minecraft.nbt.CompoundTag;

public interface DyeableLeatherItem {
   String TAG_COLOR = "color";
   String TAG_DISPLAY = "display";
   int DEFAULT_LEATHER_COLOR = 10511680;

   default boolean hasCustomColor(ItemStack var1) {
      CompoundTag var2 = var1.getTagElement("display");
      return var2 != null && var2.contains("color", 99);
   }

   default int getColor(ItemStack var1) {
      CompoundTag var2 = var1.getTagElement("display");
      return var2 != null && var2.contains("color", 99) ? var2.getInt("color") : 10511680;
   }

   default void clearColor(ItemStack var1) {
      CompoundTag var2 = var1.getTagElement("display");
      if (var2 != null && var2.contains("color")) {
         var2.remove("color");
      }

   }

   default void setColor(ItemStack var1, int var2) {
      var1.getOrCreateTagElement("display").putInt("color", var2);
   }

   static ItemStack dyeArmor(ItemStack var0, List<DyeItem> var1) {
      ItemStack var2 = ItemStack.EMPTY;
      int[] var3 = new int[3];
      int var4 = 0;
      int var5 = 0;
      DyeableLeatherItem var6 = null;
      Item var7 = var0.getItem();
      int var8;
      float var11;
      int var13;
      if (var7 instanceof DyeableLeatherItem) {
         var6 = (DyeableLeatherItem)var7;
         var2 = var0.copy();
         var2.setCount(1);
         if (var6.hasCustomColor(var0)) {
            var8 = var6.getColor(var2);
            float var9 = (float)(var8 >> 16 & 255) / 255.0F;
            float var10 = (float)(var8 >> 8 & 255) / 255.0F;
            var11 = (float)(var8 & 255) / 255.0F;
            var4 = (int)((float)var4 + Math.max(var9, Math.max(var10, var11)) * 255.0F);
            var3[0] = (int)((float)var3[0] + var9 * 255.0F);
            var3[1] = (int)((float)var3[1] + var10 * 255.0F);
            var3[2] = (int)((float)var3[2] + var11 * 255.0F);
            ++var5;
         }

         for(Iterator var14 = var1.iterator(); var14.hasNext(); ++var5) {
            DyeItem var15 = (DyeItem)var14.next();
            float[] var17 = var15.getDyeColor().getTextureDiffuseColors();
            int var19 = (int)(var17[0] * 255.0F);
            int var12 = (int)(var17[1] * 255.0F);
            var13 = (int)(var17[2] * 255.0F);
            var4 += Math.max(var19, Math.max(var12, var13));
            var3[0] += var19;
            var3[1] += var12;
            var3[2] += var13;
         }
      }

      if (var6 == null) {
         return ItemStack.EMPTY;
      } else {
         var8 = var3[0] / var5;
         int var16 = var3[1] / var5;
         int var18 = var3[2] / var5;
         var11 = (float)var4 / (float)var5;
         float var20 = (float)Math.max(var8, Math.max(var16, var18));
         var8 = (int)((float)var8 * var11 / var20);
         var16 = (int)((float)var16 * var11 / var20);
         var18 = (int)((float)var18 * var11 / var20);
         var13 = (var8 << 8) + var16;
         var13 = (var13 << 8) + var18;
         var6.setColor(var2, var13);
         return var2;
      }
   }
}
