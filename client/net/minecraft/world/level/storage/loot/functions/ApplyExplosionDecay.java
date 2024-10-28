package net.minecraft.world.level.storage.loot.functions;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;

public class ApplyExplosionDecay extends LootItemConditionalFunction {
   public static final MapCodec<ApplyExplosionDecay> CODEC = RecordCodecBuilder.mapCodec((var0) -> {
      return commonFields(var0).apply(var0, ApplyExplosionDecay::new);
   });

   private ApplyExplosionDecay(List<LootItemCondition> var1) {
      super(var1);
   }

   public LootItemFunctionType<ApplyExplosionDecay> getType() {
      return LootItemFunctions.EXPLOSION_DECAY;
   }

   public ItemStack run(ItemStack var1, LootContext var2) {
      Float var3 = (Float)var2.getParamOrNull(LootContextParams.EXPLOSION_RADIUS);
      if (var3 != null) {
         RandomSource var4 = var2.getRandom();
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
}
