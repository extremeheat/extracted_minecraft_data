package net.minecraft.world.level.storage.loot.functions;

import com.mojang.logging.LogUtils;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import java.util.Optional;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.SingleRecipeInput;
import net.minecraft.world.item.crafting.SmeltingRecipe;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import org.slf4j.Logger;

public class SmeltItemFunction extends LootItemConditionalFunction {
   private static final Logger LOGGER = LogUtils.getLogger();
   public static final MapCodec<SmeltItemFunction> CODEC = RecordCodecBuilder.mapCodec((var0) -> commonFields(var0).apply(var0, SmeltItemFunction::new));

   private SmeltItemFunction(List<LootItemCondition> var1) {
      super(var1);
   }

   public LootItemFunctionType<SmeltItemFunction> getType() {
      return LootItemFunctions.FURNACE_SMELT;
   }

   public ItemStack run(ItemStack var1, LootContext var2) {
      if (var1.isEmpty()) {
         return var1;
      } else {
         SingleRecipeInput var3 = new SingleRecipeInput(var1);
         Optional var4 = var2.getLevel().recipeAccess().getRecipeFor(RecipeType.SMELTING, var3, var2.getLevel());
         if (var4.isPresent()) {
            ItemStack var5 = ((SmeltingRecipe)((RecipeHolder)var4.get()).value()).assemble(var3, var2.getLevel().registryAccess());
            if (!var5.isEmpty()) {
               return var5.copyWithCount(var1.getCount());
            }
         }

         LOGGER.warn("Couldn't smelt {} because there is no smelting recipe", var1);
         return var1;
      }
   }

   public static LootItemConditionalFunction.Builder<?> smelted() {
      return simpleBuilder(SmeltItemFunction::new);
   }
}
