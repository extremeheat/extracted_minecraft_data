package net.minecraft.world.item.crafting;

import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.SuspiciousStewItem;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SuspiciousEffectHolder;

public class SuspiciousStewRecipe extends CustomRecipe {
   public SuspiciousStewRecipe(ResourceLocation var1, CraftingBookCategory var2) {
      super(var1, var2);
   }

   public boolean matches(CraftingContainer var1, Level var2) {
      boolean var3 = false;
      boolean var4 = false;
      boolean var5 = false;
      boolean var6 = false;

      for(int var7 = 0; var7 < var1.getContainerSize(); ++var7) {
         ItemStack var8 = var1.getItem(var7);
         if (!var8.isEmpty()) {
            if (var8.is(Blocks.BROWN_MUSHROOM.asItem()) && !var5) {
               var5 = true;
            } else if (var8.is(Blocks.RED_MUSHROOM.asItem()) && !var4) {
               var4 = true;
            } else if (var8.is(ItemTags.SMALL_FLOWERS) && !var3) {
               var3 = true;
            } else {
               if (!var8.is(Items.BOWL) || var6) {
                  return false;
               }

               var6 = true;
            }
         }
      }

      return var3 && var5 && var4 && var6;
   }

   public ItemStack assemble(CraftingContainer var1, RegistryAccess var2) {
      ItemStack var3 = new ItemStack(Items.SUSPICIOUS_STEW, 1);

      for(int var4 = 0; var4 < var1.getContainerSize(); ++var4) {
         ItemStack var5 = var1.getItem(var4);
         if (!var5.isEmpty()) {
            SuspiciousEffectHolder var6 = SuspiciousEffectHolder.tryGet(var5.getItem());
            if (var6 != null) {
               SuspiciousStewItem.saveMobEffect(var3, var6.getSuspiciousEffect(), var6.getEffectDuration());
               break;
            }
         }
      }

      return var3;
   }

   @Override
   public boolean canCraftInDimensions(int var1, int var2) {
      return var1 >= 2 && var2 >= 2;
   }

   @Override
   public RecipeSerializer<?> getSerializer() {
      return RecipeSerializer.SUSPICIOUS_STEW;
   }
}
