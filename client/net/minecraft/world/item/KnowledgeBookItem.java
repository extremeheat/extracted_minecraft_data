package net.minecraft.world.item;

import com.mojang.logging.LogUtils;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import net.minecraft.core.component.DataComponents;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.level.Level;
import org.slf4j.Logger;

public class KnowledgeBookItem extends Item {
   private static final Logger LOGGER = LogUtils.getLogger();

   public KnowledgeBookItem(Item.Properties var1) {
      super(var1);
   }

   public InteractionResultHolder<ItemStack> use(Level var1, Player var2, InteractionHand var3) {
      ItemStack var4 = var2.getItemInHand(var3);
      List var5 = (List)var4.getOrDefault(DataComponents.RECIPES, List.of());
      var4.consume(1, var2);
      if (var5.isEmpty()) {
         return InteractionResultHolder.fail(var4);
      } else {
         if (!var1.isClientSide) {
            RecipeManager var6 = var1.getServer().getRecipeManager();
            ArrayList var7 = new ArrayList(var5.size());
            Iterator var8 = var5.iterator();

            while(var8.hasNext()) {
               ResourceLocation var9 = (ResourceLocation)var8.next();
               Optional var10 = var6.byKey(var9);
               if (!var10.isPresent()) {
                  LOGGER.error("Invalid recipe: {}", var9);
                  return InteractionResultHolder.fail(var4);
               }

               var7.add((RecipeHolder)var10.get());
            }

            var2.awardRecipes(var7);
            var2.awardStat(Stats.ITEM_USED.get(this));
         }

         return InteractionResultHolder.sidedSuccess(var4, var1.isClientSide());
      }
   }
}
