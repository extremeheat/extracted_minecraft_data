package net.minecraft.world.level.storage.loot.functions;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import java.util.Set;
import net.minecraft.util.GsonHelper;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParam;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.providers.number.NumberProvider;

public class SetItemCountFunction extends LootItemConditionalFunction {
   final NumberProvider value;
   final boolean add;

   SetItemCountFunction(LootItemCondition[] var1, NumberProvider var2, boolean var3) {
      super(var1);
      this.value = var2;
      this.add = var3;
   }

   @Override
   public LootItemFunctionType getType() {
      return LootItemFunctions.SET_COUNT;
   }

   @Override
   public Set<LootContextParam<?>> getReferencedContextParams() {
      return this.value.getReferencedContextParams();
   }

   @Override
   public ItemStack run(ItemStack var1, LootContext var2) {
      int var3 = this.add ? var1.getCount() : 0;
      var1.setCount(Mth.clamp(var3 + this.value.getInt(var2), 0, var1.getMaxStackSize()));
      return var1;
   }

   public static LootItemConditionalFunction.Builder<?> setCount(NumberProvider var0) {
      return simpleBuilder(var1 -> new SetItemCountFunction(var1, var0, false));
   }

   public static LootItemConditionalFunction.Builder<?> setCount(NumberProvider var0, boolean var1) {
      return simpleBuilder(var2 -> new SetItemCountFunction(var2, var0, var1));
   }

   public static class Serializer extends LootItemConditionalFunction.Serializer<SetItemCountFunction> {
      public Serializer() {
         super();
      }

      public void serialize(JsonObject var1, SetItemCountFunction var2, JsonSerializationContext var3) {
         super.serialize(var1, var2, var3);
         var1.add("count", var3.serialize(var2.value));
         var1.addProperty("add", var2.add);
      }

      public SetItemCountFunction deserialize(JsonObject var1, JsonDeserializationContext var2, LootItemCondition[] var3) {
         NumberProvider var4 = GsonHelper.getAsObject(var1, "count", var2, NumberProvider.class);
         boolean var5 = GsonHelper.getAsBoolean(var1, "add", false);
         return new SetItemCountFunction(var3, var4, var5);
      }
   }
}
