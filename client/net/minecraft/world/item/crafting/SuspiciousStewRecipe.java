package net.minecraft.world.item.crafting;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.Tag;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.SuspiciousStewItem;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.FlowerBlock;

public class SuspiciousStewRecipe extends CustomRecipe {
   public SuspiciousStewRecipe(ResourceLocation var1) {
      super(var1);
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
            } else if (var8.is((Tag)ItemTags.SMALL_FLOWERS) && !var3) {
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

   public ItemStack assemble(CraftingContainer var1) {
      ItemStack var2 = ItemStack.EMPTY;

      for(int var3 = 0; var3 < var1.getContainerSize(); ++var3) {
         ItemStack var4 = var1.getItem(var3);
         if (!var4.isEmpty() && var4.is((Tag)ItemTags.SMALL_FLOWERS)) {
            var2 = var4;
            break;
         }
      }

      ItemStack var6 = new ItemStack(Items.SUSPICIOUS_STEW, 1);
      if (var2.getItem() instanceof BlockItem && ((BlockItem)var2.getItem()).getBlock() instanceof FlowerBlock) {
         FlowerBlock var7 = (FlowerBlock)((BlockItem)var2.getItem()).getBlock();
         MobEffect var5 = var7.getSuspiciousStewEffect();
         SuspiciousStewItem.saveMobEffect(var6, var5, var7.getEffectDuration());
      }

      return var6;
   }

   public boolean canCraftInDimensions(int var1, int var2) {
      return var1 >= 2 && var2 >= 2;
   }

   public RecipeSerializer<?> getSerializer() {
      return RecipeSerializer.SUSPICIOUS_STEW;
   }
}
