package net.minecraft.world.level.storage.loot.functions;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import java.util.Set;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParam;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.providers.number.NumberProvider;
import net.minecraft.world.level.storage.loot.providers.number.NumberProviders;

public class SetItemCountFunction extends LootItemConditionalFunction {
   public static final MapCodec<SetItemCountFunction> CODEC = RecordCodecBuilder.mapCodec((var0) -> {
      return commonFields(var0).and(var0.group(NumberProviders.CODEC.fieldOf("count").forGetter((var0x) -> {
         return var0x.value;
      }), Codec.BOOL.fieldOf("add").orElse(false).forGetter((var0x) -> {
         return var0x.add;
      }))).apply(var0, SetItemCountFunction::new);
   });
   private final NumberProvider value;
   private final boolean add;

   private SetItemCountFunction(List<LootItemCondition> var1, NumberProvider var2, boolean var3) {
      super(var1);
      this.value = var2;
      this.add = var3;
   }

   public LootItemFunctionType<SetItemCountFunction> getType() {
      return LootItemFunctions.SET_COUNT;
   }

   public Set<LootContextParam<?>> getReferencedContextParams() {
      return this.value.getReferencedContextParams();
   }

   public ItemStack run(ItemStack var1, LootContext var2) {
      int var3 = this.add ? var1.getCount() : 0;
      var1.setCount(var3 + this.value.getInt(var2));
      return var1;
   }

   public static LootItemConditionalFunction.Builder<?> setCount(NumberProvider var0) {
      return simpleBuilder((var1) -> {
         return new SetItemCountFunction(var1, var0, false);
      });
   }

   public static LootItemConditionalFunction.Builder<?> setCount(NumberProvider var0, boolean var1) {
      return simpleBuilder((var2) -> {
         return new SetItemCountFunction(var2, var0, var1);
      });
   }
}
