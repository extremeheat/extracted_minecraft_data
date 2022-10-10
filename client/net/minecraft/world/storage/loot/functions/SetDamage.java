package net.minecraft.world.storage.loot.functions;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import java.util.Random;
import net.minecraft.item.ItemStack;
import net.minecraft.util.JsonUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.storage.loot.LootContext;
import net.minecraft.world.storage.loot.RandomValueRange;
import net.minecraft.world.storage.loot.conditions.LootCondition;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class SetDamage extends LootFunction {
   private static final Logger field_186565_a = LogManager.getLogger();
   private final RandomValueRange field_186566_b;

   public SetDamage(LootCondition[] var1, RandomValueRange var2) {
      super(var1);
      this.field_186566_b = var2;
   }

   public ItemStack func_186553_a(ItemStack var1, Random var2, LootContext var3) {
      if (var1.func_77984_f()) {
         float var4 = 1.0F - this.field_186566_b.func_186507_b(var2);
         var1.func_196085_b(MathHelper.func_76141_d(var4 * (float)var1.func_77958_k()));
      } else {
         field_186565_a.warn("Couldn't set damage of loot item {}", var1);
      }

      return var1;
   }

   public static class Serializer extends LootFunction.Serializer<SetDamage> {
      protected Serializer() {
         super(new ResourceLocation("set_damage"), SetDamage.class);
      }

      public void func_186532_a(JsonObject var1, SetDamage var2, JsonSerializationContext var3) {
         var1.add("damage", var3.serialize(var2.field_186566_b));
      }

      public SetDamage func_186530_b(JsonObject var1, JsonDeserializationContext var2, LootCondition[] var3) {
         return new SetDamage(var3, (RandomValueRange)JsonUtils.func_188174_a(var1, "damage", var2, RandomValueRange.class));
      }

      // $FF: synthetic method
      public LootFunction func_186530_b(JsonObject var1, JsonDeserializationContext var2, LootCondition[] var3) {
         return this.func_186530_b(var1, var2, var3);
      }
   }
}
