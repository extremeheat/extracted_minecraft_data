package net.minecraft.world.level.storage.loot.functions;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.mojang.serialization.codecs.RecordCodecBuilder.Instance;
import java.util.List;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.providers.number.NumberProvider;
import net.minecraft.world.level.storage.loot.providers.number.NumberProviders;

public class SetOminousBottleAmplifierFunction extends LootItemConditionalFunction {
   static final MapCodec<SetOminousBottleAmplifierFunction> CODEC = RecordCodecBuilder.mapCodec(
      var0 -> commonFields(var0)
            .and(NumberProviders.CODEC.fieldOf("amplifier").forGetter(var0x -> var0x.amplifierGenerator))
            .apply(var0, SetOminousBottleAmplifierFunction::new)
   );
   private final NumberProvider amplifierGenerator;

   private SetOminousBottleAmplifierFunction(List<LootItemCondition> var1, NumberProvider var2) {
      super(var1);
      this.amplifierGenerator = var2;
   }

   @Override
   public LootItemFunctionType getType() {
      return LootItemFunctions.SET_OMINOUS_BOTTLE_AMPLIFIER;
   }

   @Override
   public ItemStack run(ItemStack var1, LootContext var2) {
      var1.set(DataComponents.OMINOUS_BOTTLE_AMPLIFIER, this.amplifierGenerator.getInt(var2));
      return var1;
   }

   public NumberProvider amplifier() {
      return this.amplifierGenerator;
   }

   public static LootItemConditionalFunction.Builder<?> setAmplifier(NumberProvider var0) {
      return simpleBuilder(var1 -> new SetOminousBottleAmplifierFunction(var1, var0));
   }
}
