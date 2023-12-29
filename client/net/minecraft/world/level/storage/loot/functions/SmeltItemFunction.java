package net.minecraft.world.level.storage.loot.functions;

import com.mojang.logging.LogUtils;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.mojang.serialization.codecs.RecordCodecBuilder.Instance;
import java.util.List;
import java.util.Optional;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.SmeltingRecipe;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import org.slf4j.Logger;

public class SmeltItemFunction extends LootItemConditionalFunction {
   private static final Logger LOGGER = LogUtils.getLogger();
   public static final Codec<SmeltItemFunction> CODEC = RecordCodecBuilder.create(var0 -> commonFields(var0).apply(var0, SmeltItemFunction::new));

   private SmeltItemFunction(List<LootItemCondition> var1) {
      super(var1);
   }

   @Override
   public LootItemFunctionType getType() {
      return LootItemFunctions.FURNACE_SMELT;
   }

   @Override
   public ItemStack run(ItemStack var1, LootContext var2) {
      if (var1.isEmpty()) {
         return var1;
      } else {
         Optional var3 = var2.getLevel().getRecipeManager().getRecipeFor(RecipeType.SMELTING, new SimpleContainer(var1), var2.getLevel());
         if (var3.isPresent()) {
            ItemStack var4 = ((SmeltingRecipe)((RecipeHolder)var3.get()).value()).getResultItem(var2.getLevel().registryAccess());
            if (!var4.isEmpty()) {
               return var4.copyWithCount(var1.getCount());
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
