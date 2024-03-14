package net.minecraft.world.level.storage.loot.functions;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.mojang.serialization.codecs.RecordCodecBuilder.Instance;
import java.util.List;
import java.util.Set;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParam;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.providers.number.NumberProvider;
import net.minecraft.world.level.storage.loot.providers.number.NumberProviders;

public class SetItemCountFunction extends LootItemConditionalFunction {
   public static final Codec<SetItemCountFunction> CODEC = RecordCodecBuilder.create(
      var0 -> commonFields(var0)
            .and(
               var0.group(
                  NumberProviders.CODEC.fieldOf("count").forGetter(var0x -> var0x.value), Codec.BOOL.fieldOf("add").orElse(false).forGetter(var0x -> var0x.add)
               )
            )
            .apply(var0, SetItemCountFunction::new)
   );
   private final NumberProvider value;
   private final boolean add;

   private SetItemCountFunction(List<LootItemCondition> var1, NumberProvider var2, boolean var3) {
      super(var1);
      this.value = var2;
      this.add = var3;
   }

   @Override
   public LootItemFunctionType getType() {
      return LootItemFunctions.SET_COUNT;
   }

   @Override
   public Set<LootContextParam<?>> getReferencedContextParams() {
      return this.value.getReferencedContextParams();
   }

   @Override
   public ItemStack run(ItemStack var1, LootContext var2) {
      int var3 = this.add ? var1.getCount() : 0;
      var1.setCount(Mth.clamp(var3 + this.value.getInt(var2), 0, var1.getMaxStackSize()));
      return var1;
   }

   public static LootItemConditionalFunction.Builder<?> setCount(NumberProvider var0) {
      return simpleBuilder(var1 -> new SetItemCountFunction(var1, var0, false));
   }

   public static LootItemConditionalFunction.Builder<?> setCount(NumberProvider var0, boolean var1) {
      return simpleBuilder(var2 -> new SetItemCountFunction(var2, var0, var1));
   }
}
