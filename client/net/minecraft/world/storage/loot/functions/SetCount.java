package net.minecraft.world.storage.loot.functions;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import java.util.Random;
import net.minecraft.item.ItemStack;
import net.minecraft.util.JsonUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.storage.loot.LootContext;
import net.minecraft.world.storage.loot.RandomValueRange;
import net.minecraft.world.storage.loot.conditions.LootCondition;

public class SetCount extends LootFunction {
   private final RandomValueRange field_186568_a;

   public SetCount(LootCondition[] var1, RandomValueRange var2) {
      super(var1);
      this.field_186568_a = var2;
   }

   public ItemStack func_186553_a(ItemStack var1, Random var2, LootContext var3) {
      var1.func_190920_e(this.field_186568_a.func_186511_a(var2));
      return var1;
   }

   public static class Serializer extends LootFunction.Serializer<SetCount> {
      protected Serializer() {
         super(new ResourceLocation("set_count"), SetCount.class);
      }

      public void func_186532_a(JsonObject var1, SetCount var2, JsonSerializationContext var3) {
         var1.add("count", var3.serialize(var2.field_186568_a));
      }

      public SetCount func_186530_b(JsonObject var1, JsonDeserializationContext var2, LootCondition[] var3) {
         return new SetCount(var3, (RandomValueRange)JsonUtils.func_188174_a(var1, "count", var2, RandomValueRange.class));
      }

      // $FF: synthetic method
      public LootFunction func_186530_b(JsonObject var1, JsonDeserializationContext var2, LootCondition[] var3) {
         return this.func_186530_b(var1, var2, var3);
      }
   }
}
