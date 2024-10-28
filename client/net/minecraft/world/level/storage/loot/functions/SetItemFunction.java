package net.minecraft.world.level.storage.loot.functions;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.RegistryFixedCodec;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;

public class SetItemFunction extends LootItemConditionalFunction {
   public static final MapCodec<SetItemFunction> CODEC = RecordCodecBuilder.mapCodec((var0) -> {
      return commonFields(var0).and(RegistryFixedCodec.create(Registries.ITEM).fieldOf("item").forGetter((var0x) -> {
         return var0x.item;
      })).apply(var0, SetItemFunction::new);
   });
   private final Holder<Item> item;

   private SetItemFunction(List<LootItemCondition> var1, Holder<Item> var2) {
      super(var1);
      this.item = var2;
   }

   public LootItemFunctionType<SetItemFunction> getType() {
      return LootItemFunctions.SET_ITEM;
   }

   public ItemStack run(ItemStack var1, LootContext var2) {
      return var1.transmuteCopy((ItemLike)this.item.value());
   }
}
