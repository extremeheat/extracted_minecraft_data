package net.minecraft.world.level.storage.loot.functions;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import java.util.Random;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;

public class ApplyExplosionDecay extends LootItemConditionalFunction {
   private ApplyExplosionDecay(LootItemCondition[] var1) {
      super(var1);
   }

   public LootItemFunctionType getType() {
      return LootItemFunctions.EXPLOSION_DECAY;
   }

   public ItemStack run(ItemStack var1, LootContext var2) {
      Float var3 = (Float)var2.getParamOrNull(LootContextParams.EXPLOSION_RADIUS);
      if (var3 != null) {
         Random var4 = var2.getRandom();
         float var5 = 1.0F / var3;
         int var6 = var1.getCount();
         int var7 = 0;

         for(int var8 = 0; var8 < var6; ++var8) {
            if (var4.nextFloat() <= var5) {
               ++var7;
            }
         }

         var1.setCount(var7);
      }

      return var1;
   }

   public static LootItemConditionalFunction.Builder<?> explosionDecay() {
      return simpleBuilder(ApplyExplosionDecay::new);
   }

   // $FF: synthetic method
   ApplyExplosionDecay(LootItemCondition[] var1, Object var2) {
      this(var1);
   }

   public static class Serializer extends LootItemConditionalFunction.Serializer<ApplyExplosionDecay> {
      public Serializer() {
         super();
      }

      public ApplyExplosionDecay deserialize(JsonObject var1, JsonDeserializationContext var2, LootItemCondition[] var3) {
         return new ApplyExplosionDecay(var3);
      }

      // $FF: synthetic method
      public LootItemConditionalFunction deserialize(JsonObject var1, JsonDeserializationContext var2, LootItemCondition[] var3) {
         return this.deserialize(var1, var2, var3);
      }
   }
}
