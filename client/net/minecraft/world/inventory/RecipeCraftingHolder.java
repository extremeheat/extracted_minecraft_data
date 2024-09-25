package net.minecraft.world.inventory;

import java.util.Collections;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.GameRules;

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
            this.setRecipeUsed(null);
         }
      }
   }

   default boolean setRecipeUsed(ServerPlayer var1, RecipeHolder<?> var2) {
      if (!var2.value().isSpecial() && var1.serverLevel().getGameRules().getBoolean(GameRules.RULE_LIMITED_CRAFTING) && !var1.getRecipeBook().contains(var2)) {
         return false;
      } else {
         this.setRecipeUsed(var2);
         return true;
      }
   }
}
