package net.minecraft.world.inventory;

import java.util.Collections;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;

public interface RecipeHolder {
   void setRecipeUsed(@Nullable Recipe<?> var1);

   @Nullable
   Recipe<?> getRecipeUsed();

   default void awardUsedRecipes(Player var1, List<ItemStack> var2) {
      Recipe var3 = this.getRecipeUsed();
      if (var3 != null) {
         var1.triggerRecipeCrafted(var3, var2);
         if (!var3.isSpecial()) {
            var1.awardRecipes(Collections.singleton(var3));
            this.setRecipeUsed(null);
         }
      }
   }

   default boolean setRecipeUsed(Level var1, ServerPlayer var2, Recipe<?> var3) {
      if (!var3.isSpecial() && var1.getGameRules().getBoolean(GameRules.RULE_LIMITED_CRAFTING) && !var2.getRecipeBook().contains(var3)) {
         return false;
      } else {
         this.setRecipeUsed(var3);
         return true;
      }
   }
}
