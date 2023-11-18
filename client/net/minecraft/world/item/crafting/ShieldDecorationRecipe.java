package net.minecraft.world.item.crafting;

import net.minecraft.core.RegistryAccess;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.BannerItem;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntityType;

public class ShieldDecorationRecipe extends CustomRecipe {
   public ShieldDecorationRecipe(CraftingBookCategory var1) {
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

               if (BlockItem.getBlockEntityData(var6) != null) {
                  return false;
               }

               var3 = var6;
            }
         }
      }

      return !var3.isEmpty() && !var4.isEmpty();
   }

   public ItemStack assemble(CraftingContainer var1, RegistryAccess var2) {
      ItemStack var3 = ItemStack.EMPTY;
      ItemStack var4 = ItemStack.EMPTY;

      for(int var5 = 0; var5 < var1.getContainerSize(); ++var5) {
         ItemStack var6 = var1.getItem(var5);
         if (!var6.isEmpty()) {
            if (var6.getItem() instanceof BannerItem) {
               var3 = var6;
            } else if (var6.is(Items.SHIELD)) {
               var4 = var6.copy();
            }
         }
      }

      if (var4.isEmpty()) {
         return var4;
      } else {
         CompoundTag var7 = BlockItem.getBlockEntityData(var3);
         CompoundTag var8 = var7 == null ? new CompoundTag() : var7.copy();
         var8.putInt("Base", ((BannerItem)var3.getItem()).getColor().getId());
         BlockItem.setBlockEntityData(var4, BlockEntityType.BANNER, var8);
         return var4;
      }
   }

   @Override
   public boolean canCraftInDimensions(int var1, int var2) {
      return var1 * var2 >= 2;
   }

   @Override
   public RecipeSerializer<?> getSerializer() {
      return RecipeSerializer.SHIELD_DECORATION;
   }
}
