package net.minecraft.world.level.storage.loot.functions;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.IntLimiter;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;

public class LimitCount extends LootItemConditionalFunction {
   private final IntLimiter limiter;

   private LimitCount(LootItemCondition[] var1, IntLimiter var2) {
      super(var1);
      this.limiter = var2;
   }

   public ItemStack run(ItemStack var1, LootContext var2) {
      int var3 = this.limiter.applyAsInt(var1.getCount());
      var1.setCount(var3);
      return var1;
   }

   public static LootItemConditionalFunction.Builder limitCount(IntLimiter var0) {
      return simpleBuilder((var1) -> {
         return new LimitCount(var1, var0);
      });
   }

   // $FF: synthetic method
   LimitCount(LootItemCondition[] var1, IntLimiter var2, Object var3) {
      this(var1, var2);
   }

   public static class Serializer extends LootItemConditionalFunction.Serializer {
      protected Serializer() {
         super(new ResourceLocation("limit_count"), LimitCount.class);
      }

      public void serialize(JsonObject var1, LimitCount var2, JsonSerializationContext var3) {
         super.serialize(var1, (LootItemConditionalFunction)var2, var3);
         var1.add("limit", var3.serialize(var2.limiter));
      }

      public LimitCount deserialize(JsonObject var1, JsonDeserializationContext var2, LootItemCondition[] var3) {
         IntLimiter var4 = (IntLimiter)GsonHelper.getAsObject(var1, "limit", var2, IntLimiter.class);
         return new LimitCount(var3, var4);
      }

      // $FF: synthetic method
      public LootItemConditionalFunction deserialize(JsonObject var1, JsonDeserializationContext var2, LootItemCondition[] var3) {
         return this.deserialize(var1, var2, var3);
      }
   }
}
