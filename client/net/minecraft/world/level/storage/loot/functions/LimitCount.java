package net.minecraft.world.level.storage.loot.functions;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import java.util.Set;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.IntRange;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParam;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;

public class LimitCount extends LootItemConditionalFunction {
   final IntRange limiter;

   LimitCount(LootItemCondition[] var1, IntRange var2) {
      super(var1);
      this.limiter = var2;
   }

   public LootItemFunctionType getType() {
      return LootItemFunctions.LIMIT_COUNT;
   }

   public Set<LootContextParam<?>> getReferencedContextParams() {
      return this.limiter.getReferencedContextParams();
   }

   public ItemStack run(ItemStack var1, LootContext var2) {
      int var3 = this.limiter.clamp(var2, var1.getCount());
      var1.setCount(var3);
      return var1;
   }

   public static LootItemConditionalFunction.Builder<?> limitCount(IntRange var0) {
      return simpleBuilder((var1) -> {
         return new LimitCount(var1, var0);
      });
   }

   public static class Serializer extends LootItemConditionalFunction.Serializer<LimitCount> {
      public Serializer() {
         super();
      }

      public void serialize(JsonObject var1, LimitCount var2, JsonSerializationContext var3) {
         super.serialize(var1, (LootItemConditionalFunction)var2, var3);
         var1.add("limit", var3.serialize(var2.limiter));
      }

      public LimitCount deserialize(JsonObject var1, JsonDeserializationContext var2, LootItemCondition[] var3) {
         IntRange var4 = (IntRange)GsonHelper.getAsObject(var1, "limit", var2, IntRange.class);
         return new LimitCount(var3, var4);
      }

      // $FF: synthetic method
      public LootItemConditionalFunction deserialize(JsonObject var1, JsonDeserializationContext var2, LootItemCondition[] var3) {
         return this.deserialize(var1, var2, var3);
      }
   }
}
