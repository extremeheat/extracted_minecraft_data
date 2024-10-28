package net.minecraft.world.level.storage.loot.functions;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import net.minecraft.advancements.critereon.ItemPredicate;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.ValidationContext;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;

public class FilteredFunction extends LootItemConditionalFunction {
   public static final MapCodec<FilteredFunction> CODEC = RecordCodecBuilder.mapCodec((var0) -> {
      return commonFields(var0).and(var0.group(ItemPredicate.CODEC.fieldOf("item_filter").forGetter((var0x) -> {
         return var0x.filter;
      }), LootItemFunctions.ROOT_CODEC.fieldOf("modifier").forGetter((var0x) -> {
         return var0x.modifier;
      }))).apply(var0, FilteredFunction::new);
   });
   private final ItemPredicate filter;
   private final LootItemFunction modifier;

   private FilteredFunction(List<LootItemCondition> var1, ItemPredicate var2, LootItemFunction var3) {
      super(var1);
      this.filter = var2;
      this.modifier = var3;
   }

   public LootItemFunctionType<FilteredFunction> getType() {
      return LootItemFunctions.FILTERED;
   }

   public ItemStack run(ItemStack var1, LootContext var2) {
      return this.filter.test(var1) ? (ItemStack)this.modifier.apply(var1, var2) : var1;
   }

   public void validate(ValidationContext var1) {
      super.validate(var1);
      this.modifier.validate(var1.forChild(".modifier"));
   }
}
