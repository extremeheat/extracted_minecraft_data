package net.minecraft.world.level.storage.loot.functions;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.mojang.logging.LogUtils;
import java.util.Optional;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.SmeltingRecipe;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import org.slf4j.Logger;

public class SmeltItemFunction extends LootItemConditionalFunction {
   private static final Logger LOGGER = LogUtils.getLogger();

   SmeltItemFunction(LootItemCondition[] var1) {
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
            ItemStack var4 = ((SmeltingRecipe)var3.get()).getResultItem();
            if (!var4.isEmpty()) {
               ItemStack var5 = var4.copy();
               var5.setCount(var1.getCount());
               return var5;
            }
         }

         LOGGER.warn("Couldn't smelt {} because there is no smelting recipe", var1);
         return var1;
      }
   }

   public static LootItemConditionalFunction.Builder<?> smelted() {
      return simpleBuilder(SmeltItemFunction::new);
   }

   public static class Serializer extends LootItemConditionalFunction.Serializer<SmeltItemFunction> {
      public Serializer() {
         super();
      }

      public SmeltItemFunction deserialize(JsonObject var1, JsonDeserializationContext var2, LootItemCondition[] var3) {
         return new SmeltItemFunction(var3);
      }
   }
}
