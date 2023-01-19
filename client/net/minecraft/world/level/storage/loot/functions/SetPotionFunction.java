package net.minecraft.world.level.storage.loot.functions;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSyntaxException;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;

public class SetPotionFunction extends LootItemConditionalFunction {
   final Potion potion;

   SetPotionFunction(LootItemCondition[] var1, Potion var2) {
      super(var1);
      this.potion = var2;
   }

   @Override
   public LootItemFunctionType getType() {
      return LootItemFunctions.SET_POTION;
   }

   @Override
   public ItemStack run(ItemStack var1, LootContext var2) {
      PotionUtils.setPotion(var1, this.potion);
      return var1;
   }

   public static LootItemConditionalFunction.Builder<?> setPotion(Potion var0) {
      return simpleBuilder(var1 -> new SetPotionFunction(var1, var0));
   }

   public static class Serializer extends LootItemConditionalFunction.Serializer<SetPotionFunction> {
      public Serializer() {
         super();
      }

      public void serialize(JsonObject var1, SetPotionFunction var2, JsonSerializationContext var3) {
         super.serialize(var1, var2, var3);
         var1.addProperty("id", BuiltInRegistries.POTION.getKey(var2.potion).toString());
      }

      public SetPotionFunction deserialize(JsonObject var1, JsonDeserializationContext var2, LootItemCondition[] var3) {
         String var4 = GsonHelper.getAsString(var1, "id");
         Potion var5 = BuiltInRegistries.POTION
            .getOptional(ResourceLocation.tryParse(var4))
            .orElseThrow(() -> new JsonSyntaxException("Unknown potion '" + var4 + "'"));
         return new SetPotionFunction(var3, var5);
      }
   }
}
