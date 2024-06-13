package net.minecraft.world.level.storage.loot.functions;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;

public class SetPotionFunction extends LootItemConditionalFunction {
   public static final MapCodec<SetPotionFunction> CODEC = RecordCodecBuilder.mapCodec(
      var0 -> commonFields(var0)
            .and(BuiltInRegistries.POTION.holderByNameCodec().fieldOf("id").forGetter(var0x -> var0x.potion))
            .apply(var0, SetPotionFunction::new)
   );
   private final Holder<Potion> potion;

   private SetPotionFunction(List<LootItemCondition> var1, Holder<Potion> var2) {
      super(var1);
      this.potion = var2;
   }

   @Override
   public LootItemFunctionType getType() {
      return LootItemFunctions.SET_POTION;
   }

   @Override
   public ItemStack run(ItemStack var1, LootContext var2) {
      var1.update(DataComponents.POTION_CONTENTS, PotionContents.EMPTY, this.potion, PotionContents::withPotion);
      return var1;
   }

   public static LootItemConditionalFunction.Builder<?> setPotion(Holder<Potion> var0) {
      return simpleBuilder(var1 -> new SetPotionFunction(var1, var0));
   }
}
