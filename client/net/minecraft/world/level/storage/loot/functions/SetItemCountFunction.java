package net.minecraft.world.level.storage.loot.functions;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.RandomIntGenerator;
import net.minecraft.world.level.storage.loot.RandomIntGenerators;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;

public class SetItemCountFunction extends LootItemConditionalFunction {
   private final RandomIntGenerator value;

   private SetItemCountFunction(LootItemCondition[] var1, RandomIntGenerator var2) {
      super(var1);
      this.value = var2;
   }

   public ItemStack run(ItemStack var1, LootContext var2) {
      var1.setCount(this.value.getInt(var2.getRandom()));
      return var1;
   }

   public static LootItemConditionalFunction.Builder<?> setCount(RandomIntGenerator var0) {
      return simpleBuilder((var1) -> {
         return new SetItemCountFunction(var1, var0);
      });
   }

   // $FF: synthetic method
   SetItemCountFunction(LootItemCondition[] var1, RandomIntGenerator var2, Object var3) {
      this(var1, var2);
   }

   public static class Serializer extends LootItemConditionalFunction.Serializer<SetItemCountFunction> {
      protected Serializer() {
         super(new ResourceLocation("set_count"), SetItemCountFunction.class);
      }

      public void serialize(JsonObject var1, SetItemCountFunction var2, JsonSerializationContext var3) {
         super.serialize(var1, (LootItemConditionalFunction)var2, var3);
         var1.add("count", RandomIntGenerators.serialize(var2.value, var3));
      }

      public SetItemCountFunction deserialize(JsonObject var1, JsonDeserializationContext var2, LootItemCondition[] var3) {
         RandomIntGenerator var4 = RandomIntGenerators.deserialize(var1.get("count"), var2);
         return new SetItemCountFunction(var3, var4);
      }

      // $FF: synthetic method
      public LootItemConditionalFunction deserialize(JsonObject var1, JsonDeserializationContext var2, LootItemCondition[] var3) {
         return this.deserialize(var1, var2, var3);
      }
   }
}
