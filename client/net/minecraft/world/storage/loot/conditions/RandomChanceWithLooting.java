package net.minecraft.world.storage.loot.conditions;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import java.util.Random;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.JsonUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.storage.loot.LootContext;

public class RandomChanceWithLooting implements LootCondition {
   private final float field_186627_a;
   private final float field_186628_b;

   public RandomChanceWithLooting(float var1, float var2) {
      super();
      this.field_186627_a = var1;
      this.field_186628_b = var2;
   }

   public boolean func_186618_a(Random var1, LootContext var2) {
      int var3 = 0;
      if (var2.func_186492_c() instanceof EntityLivingBase) {
         var3 = EnchantmentHelper.func_185283_h((EntityLivingBase)var2.func_186492_c());
      }

      return var1.nextFloat() < this.field_186627_a + (float)var3 * this.field_186628_b;
   }

   public static class Serializer extends LootCondition.Serializer<RandomChanceWithLooting> {
      protected Serializer() {
         super(new ResourceLocation("random_chance_with_looting"), RandomChanceWithLooting.class);
      }

      public void func_186605_a(JsonObject var1, RandomChanceWithLooting var2, JsonSerializationContext var3) {
         var1.addProperty("chance", var2.field_186627_a);
         var1.addProperty("looting_multiplier", var2.field_186628_b);
      }

      public RandomChanceWithLooting func_186603_b(JsonObject var1, JsonDeserializationContext var2) {
         return new RandomChanceWithLooting(JsonUtils.func_151217_k(var1, "chance"), JsonUtils.func_151217_k(var1, "looting_multiplier"));
      }

      // $FF: synthetic method
      public LootCondition func_186603_b(JsonObject var1, JsonDeserializationContext var2) {
         return this.func_186603_b(var1, var2);
      }
   }
}
