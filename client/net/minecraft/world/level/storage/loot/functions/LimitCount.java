package net.minecraft.world.level.storage.loot.functions;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import java.util.Set;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.IntRange;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParam;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;

public class LimitCount extends LootItemConditionalFunction {
   public static final MapCodec<LimitCount> CODEC = RecordCodecBuilder.mapCodec((var0) -> {
      return commonFields(var0).and(IntRange.CODEC.fieldOf("limit").forGetter((var0x) -> {
         return var0x.limiter;
      })).apply(var0, LimitCount::new);
   });
   private final IntRange limiter;

   private LimitCount(List<LootItemCondition> var1, IntRange var2) {
      super(var1);
      this.limiter = var2;
   }

   public LootItemFunctionType<LimitCount> getType() {
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
}
