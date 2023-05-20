package net.minecraft.world.item.crafting;

import java.util.List;
import net.minecraft.core.RegistryAccess;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.DecoratedPotBlockEntity;

public class DecoratedPotRecipe extends CustomRecipe {
   public DecoratedPotRecipe(ResourceLocation var1, CraftingBookCategory var2) {
      super(var1, var2);
   }

   public boolean matches(CraftingContainer var1, Level var2) {
      if (!this.canCraftInDimensions(var1.getWidth(), var1.getHeight())) {
         return false;
      } else {
         for(int var3 = 0; var3 < var1.getContainerSize(); ++var3) {
            ItemStack var4 = var1.getItem(var3);
            switch(var3) {
               case 1:
               case 3:
               case 5:
               case 7:
                  if (!var4.is(ItemTags.DECORATED_POT_SHARDS)) {
                     return false;
                  }
                  break;
               case 2:
               case 4:
               case 6:
               default:
                  if (!var4.is(Items.AIR)) {
                     return false;
                  }
            }
         }

         return true;
      }
   }

   public ItemStack assemble(CraftingContainer var1, RegistryAccess var2) {
      ItemStack var3 = Items.DECORATED_POT.getDefaultInstance();
      CompoundTag var4 = new CompoundTag();
      DecoratedPotBlockEntity.saveShards(
         List.of(var1.getItem(1).getItem(), var1.getItem(3).getItem(), var1.getItem(5).getItem(), var1.getItem(7).getItem()), var4
      );
      BlockItem.setBlockEntityData(var3, BlockEntityType.DECORATED_POT, var4);
      return var3;
   }

   @Override
   public boolean canCraftInDimensions(int var1, int var2) {
      return var1 == 3 && var2 == 3;
   }

   @Override
   public RecipeSerializer<?> getSerializer() {
      return RecipeSerializer.DECORATED_POT_RECIPE;
   }
}
