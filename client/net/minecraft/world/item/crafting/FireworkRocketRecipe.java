package net.minecraft.world.item.crafting;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;

public class FireworkRocketRecipe extends CustomRecipe {
   private static final Ingredient PAPER_INGREDIENT;
   private static final Ingredient GUNPOWDER_INGREDIENT;
   private static final Ingredient STAR_INGREDIENT;

   public FireworkRocketRecipe(ResourceLocation var1) {
      super(var1);
   }

   public boolean matches(CraftingContainer var1, Level var2) {
      boolean var3 = false;
      int var4 = 0;

      for(int var5 = 0; var5 < var1.getContainerSize(); ++var5) {
         ItemStack var6 = var1.getItem(var5);
         if (!var6.isEmpty()) {
            if (PAPER_INGREDIENT.test(var6)) {
               if (var3) {
                  return false;
               }

               var3 = true;
            } else if (GUNPOWDER_INGREDIENT.test(var6)) {
               ++var4;
               if (var4 > 3) {
                  return false;
               }
            } else if (!STAR_INGREDIENT.test(var6)) {
               return false;
            }
         }
      }

      return var3 && var4 >= 1;
   }

   public ItemStack assemble(CraftingContainer var1) {
      ItemStack var2 = new ItemStack(Items.FIREWORK_ROCKET, 3);
      CompoundTag var3 = var2.getOrCreateTagElement("Fireworks");
      ListTag var4 = new ListTag();
      int var5 = 0;

      for(int var6 = 0; var6 < var1.getContainerSize(); ++var6) {
         ItemStack var7 = var1.getItem(var6);
         if (!var7.isEmpty()) {
            if (GUNPOWDER_INGREDIENT.test(var7)) {
               ++var5;
            } else if (STAR_INGREDIENT.test(var7)) {
               CompoundTag var8 = var7.getTagElement("Explosion");
               if (var8 != null) {
                  var4.add(var8);
               }
            }
         }
      }

      var3.putByte("Flight", (byte)var5);
      if (!var4.isEmpty()) {
         var3.put("Explosions", var4);
      }

      return var2;
   }

   public boolean canCraftInDimensions(int var1, int var2) {
      return var1 * var2 >= 2;
   }

   public ItemStack getResultItem() {
      return new ItemStack(Items.FIREWORK_ROCKET);
   }

   public RecipeSerializer<?> getSerializer() {
      return RecipeSerializer.FIREWORK_ROCKET;
   }

   static {
      PAPER_INGREDIENT = Ingredient.of(Items.PAPER);
      GUNPOWDER_INGREDIENT = Ingredient.of(Items.GUNPOWDER);
      STAR_INGREDIENT = Ingredient.of(Items.FIREWORK_STAR);
   }
}
