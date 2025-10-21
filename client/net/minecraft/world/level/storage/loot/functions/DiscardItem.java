package net.minecraft.world.level.storage.loot.functions;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;

public class DiscardItem extends LootItemConditionalFunction {
   public static final MapCodec<DiscardItem> CODEC = RecordCodecBuilder.mapCodec((var0) -> commonFields(var0).apply(var0, DiscardItem::new));

   protected DiscardItem(List<LootItemCondition> var1) {
      super(var1);
   }

   public LootItemFunctionType<DiscardItem> getType() {
      return LootItemFunctions.DISCARD;
   }

   protected ItemStack run(ItemStack var1, LootContext var2) {
      return ItemStack.EMPTY;
   }

   public static LootItemConditionalFunction.Builder<?> discardItem() {
      return simpleBuilder(DiscardItem::new);
   }
}
