package net.minecraft.world.inventory;

import java.util.Collections;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;

public interface RecipeCraftingHolder {
   void setRecipeUsed(@Nullable RecipeHolder<?> var1);

   @Nullable
   RecipeHolder<?> getRecipeUsed();

   default void awardUsedRecipes(Player var1, List<ItemStack> var2) {
      RecipeHolder var3 = this.getRecipeUsed();
      if (var3 != null) {
         var1.triggerRecipeCrafted(var3, var2);
         if (!var3.value().isSpecial()) {
            var1.awardRecipes(Collections.singleton(var3));
            this.setRecipeUsed((RecipeHolder)null);
         }
      }

   }

   default boolean setRecipeUsed(Level var1, ServerPlayer var2, RecipeHolder<?> var3) {
      if (!var3.value().isSpecial() && var1.getGameRules().getBoolean(GameRules.RULE_LIMITED_CRAFTING) && !var2.getRecipeBook().contains(var3)) {
         return false;
      } else {
         this.setRecipeUsed(var3);
         return true;
      }
   }
}
