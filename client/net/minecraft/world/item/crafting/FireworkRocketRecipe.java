package net.minecraft.world.item.crafting;

import net.minecraft.core.RegistryAccess;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;

public class FireworkRocketRecipe extends CustomRecipe {
   private static final Ingredient PAPER_INGREDIENT = Ingredient.of(Items.PAPER);
   private static final Ingredient GUNPOWDER_INGREDIENT = Ingredient.of(Items.GUNPOWDER);
   private static final Ingredient STAR_INGREDIENT = Ingredient.of(Items.FIREWORK_STAR);

   public FireworkRocketRecipe(ResourceLocation var1, CraftingBookCategory var2) {
      super(var1, var2);
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
               if (++var4 > 3) {
                  return false;
               }
            } else if (!STAR_INGREDIENT.test(var6)) {
               return false;
            }
         }
      }

      return var3 && var4 >= 1;
   }

   public ItemStack assemble(CraftingContainer var1, RegistryAccess var2) {
      ItemStack var3 = new ItemStack(Items.FIREWORK_ROCKET, 3);
      CompoundTag var4 = var3.getOrCreateTagElement("Fireworks");
      ListTag var5 = new ListTag();
      int var6 = 0;

      for(int var7 = 0; var7 < var1.getContainerSize(); ++var7) {
         ItemStack var8 = var1.getItem(var7);
         if (!var8.isEmpty()) {
            if (GUNPOWDER_INGREDIENT.test(var8)) {
               ++var6;
            } else if (STAR_INGREDIENT.test(var8)) {
               CompoundTag var9 = var8.getTagElement("Explosion");
               if (var9 != null) {
                  var5.add(var9);
               }
            }
         }
      }

      var4.putByte("Flight", (byte)var6);
      if (!var5.isEmpty()) {
         var4.put("Explosions", var5);
      }

      return var3;
   }

   @Override
   public boolean canCraftInDimensions(int var1, int var2) {
      return var1 * var2 >= 2;
   }

   @Override
   public ItemStack getResultItem(RegistryAccess var1) {
      return new ItemStack(Items.FIREWORK_ROCKET);
   }

   @Override
   public RecipeSerializer<?> getSerializer() {
      return RecipeSerializer.FIREWORK_ROCKET;
   }
}
