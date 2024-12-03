package net.minecraft.world.level.storage.loot.functions;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import java.util.Set;
import net.minecraft.util.context.ContextKey;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.IntRange;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;

public class LimitCount extends LootItemConditionalFunction {
   public static final MapCodec<LimitCount> CODEC = RecordCodecBuilder.mapCodec((var0) -> commonFields(var0).and(IntRange.CODEC.fieldOf("limit").forGetter((var0x) -> var0x.limiter)).apply(var0, LimitCount::new));
   private final IntRange limiter;

   private LimitCount(List<LootItemCondition> var1, IntRange var2) {
      super(var1);
      this.limiter = var2;
   }

   public LootItemFunctionType<LimitCount> getType() {
      return LootItemFunctions.LIMIT_COUNT;
   }

   public Set<ContextKey<?>> getReferencedContextParams() {
      return this.limiter.getReferencedContextParams();
   }

   public ItemStack run(ItemStack var1, LootContext var2) {
      int var3 = this.limiter.clamp(var2, var1.getCount());
      var1.setCount(var3);
      return var1;
   }

   public static LootItemConditionalFunction.Builder<?> limitCount(IntRange var0) {
      return simpleBuilder((var1) -> new LimitCount(var1, var0));
   }
}
