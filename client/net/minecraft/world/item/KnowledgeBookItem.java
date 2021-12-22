package net.minecraft.world.item;

import com.google.common.collect.Lists;
import java.util.ArrayList;
import java.util.Optional;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.level.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class KnowledgeBookItem extends Item {
   private static final String RECIPE_TAG = "Recipes";
   private static final Logger LOGGER = LogManager.getLogger();

   public KnowledgeBookItem(Item.Properties var1) {
      super(var1);
   }

   public InteractionResultHolder<ItemStack> use(Level var1, Player var2, InteractionHand var3) {
      ItemStack var4 = var2.getItemInHand(var3);
      CompoundTag var5 = var4.getTag();
      if (!var2.getAbilities().instabuild) {
         var2.setItemInHand(var3, ItemStack.EMPTY);
      }

      if (var5 != null && var5.contains("Recipes", 9)) {
         if (!var1.isClientSide) {
            ListTag var6 = var5.getList("Recipes", 8);
            ArrayList var7 = Lists.newArrayList();
            RecipeManager var8 = var1.getServer().getRecipeManager();

            for(int var9 = 0; var9 < var6.size(); ++var9) {
               String var10 = var6.getString(var9);
               Optional var11 = var8.byKey(new ResourceLocation(var10));
               if (!var11.isPresent()) {
                  LOGGER.error("Invalid recipe: {}", var10);
                  return InteractionResultHolder.fail(var4);
               }

               var7.add((Recipe)var11.get());
            }

            var2.awardRecipes(var7);
            var2.awardStat(Stats.ITEM_USED.get(this));
         }

         return InteractionResultHolder.sidedSuccess(var4, var1.isClientSide());
      } else {
         LOGGER.error("Tag not valid: {}", var5);
         return InteractionResultHolder.fail(var4);
      }
   }
}
