package net.minecraft.world.item;

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
      if (var7 instanceof DyeableLeatherItem) {
         var6 = (DyeableLeatherItem)var7;
         var2 = var0.copy();
         var2.setCount(1);
         if (var6.hasCustomColor(var0)) {
            int var8 = var6.getColor(var2);
            float var9 = (float)(var8 >> 16 & 0xFF) / 255.0F;
            float var10 = (float)(var8 >> 8 & 0xFF) / 255.0F;
            float var11 = (float)(var8 & 0xFF) / 255.0F;
            var4 += (int)(Math.max(var9, Math.max(var10, var11)) * 255.0F);
            var3[0] += (int)(var9 * 255.0F);
            var3[1] += (int)(var10 * 255.0F);
            var3[2] += (int)(var11 * 255.0F);
            ++var5;
         }

         for(DyeItem var17 : var1) {
            float[] var20 = var17.getDyeColor().getTextureDiffuseColors();
            int var23 = (int)(var20[0] * 255.0F);
            int var12 = (int)(var20[1] * 255.0F);
            int var13 = (int)(var20[2] * 255.0F);
            var4 += Math.max(var23, Math.max(var12, var13));
            var3[0] += var23;
            var3[1] += var12;
            var3[2] += var13;
            ++var5;
         }
      }

      if (var6 == null) {
         return ItemStack.EMPTY;
      } else {
         int var15 = var3[0] / var5;
         int var18 = var3[1] / var5;
         int var21 = var3[2] / var5;
         float var24 = (float)var4 / (float)var5;
         float var25 = (float)Math.max(var15, Math.max(var18, var21));
         var15 = (int)((float)var15 * var24 / var25);
         var18 = (int)((float)var18 * var24 / var25);
         var21 = (int)((float)var21 * var24 / var25);
         int var26 = (var15 << 8) + var18;
         var26 = (var26 << 8) + var21;
         var6.setColor(var2, var26);
         return var2;
      }
   }
}
