package net.minecraft.world.item.crafting;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.BannerItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;

public class ShieldDecorationRecipe extends CustomRecipe {
   public ShieldDecorationRecipe(ResourceLocation var1) {
      super(var1);
   }

   public boolean matches(CraftingContainer var1, Level var2) {
      ItemStack var3 = ItemStack.EMPTY;
      ItemStack var4 = ItemStack.EMPTY;

      for(int var5 = 0; var5 < var1.getContainerSize(); ++var5) {
         ItemStack var6 = var1.getItem(var5);
         if (!var6.isEmpty()) {
            if (var6.getItem() instanceof BannerItem) {
               if (!var4.isEmpty()) {
                  return false;
               }

               var4 = var6;
            } else {
               if (!var6.is(Items.SHIELD)) {
                  return false;
               }

               if (!var3.isEmpty()) {
                  return false;
               }

               if (var6.getTagElement("BlockEntityTag") != null) {
                  return false;
               }

               var3 = var6;
            }
         }
      }

      if (!var3.isEmpty() && !var4.isEmpty()) {
         return true;
      } else {
         return false;
      }
   }

   public ItemStack assemble(CraftingContainer var1) {
      ItemStack var2 = ItemStack.EMPTY;
      ItemStack var3 = ItemStack.EMPTY;

      for(int var4 = 0; var4 < var1.getContainerSize(); ++var4) {
         ItemStack var5 = var1.getItem(var4);
         if (!var5.isEmpty()) {
            if (var5.getItem() instanceof BannerItem) {
               var2 = var5;
            } else if (var5.is(Items.SHIELD)) {
               var3 = var5.copy();
            }
         }
      }

      if (var3.isEmpty()) {
         return var3;
      } else {
         CompoundTag var6 = var2.getTagElement("BlockEntityTag");
         CompoundTag var7 = var6 == null ? new CompoundTag() : var6.copy();
         var7.putInt("Base", ((BannerItem)var2.getItem()).getColor().getId());
         var3.addTagElement("BlockEntityTag", var7);
         return var3;
      }
   }

   public boolean canCraftInDimensions(int var1, int var2) {
      return var1 * var2 >= 2;
   }

   public RecipeSerializer<?> getSerializer() {
      return RecipeSerializer.SHIELD_DECORATION;
   }
}
