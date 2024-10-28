package net.minecraft.world.level.storage.loot.functions;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import java.util.Set;
import net.minecraft.core.component.DataComponents;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParam;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.providers.number.NumberProvider;
import net.minecraft.world.level.storage.loot.providers.number.NumberProviders;

public class SetOminousBottleAmplifierFunction extends LootItemConditionalFunction {
   static final MapCodec<SetOminousBottleAmplifierFunction> CODEC = RecordCodecBuilder.mapCodec((var0) -> {
      return commonFields(var0).and(NumberProviders.CODEC.fieldOf("amplifier").forGetter((var0x) -> {
         return var0x.amplifierGenerator;
      })).apply(var0, SetOminousBottleAmplifierFunction::new);
   });
   private final NumberProvider amplifierGenerator;

   private SetOminousBottleAmplifierFunction(List<LootItemCondition> var1, NumberProvider var2) {
      super(var1);
      this.amplifierGenerator = var2;
   }

   public Set<LootContextParam<?>> getReferencedContextParams() {
      return this.amplifierGenerator.getReferencedContextParams();
   }

   public LootItemFunctionType<SetOminousBottleAmplifierFunction> getType() {
      return LootItemFunctions.SET_OMINOUS_BOTTLE_AMPLIFIER;
   }

   public ItemStack run(ItemStack var1, LootContext var2) {
      int var3 = Mth.clamp(this.amplifierGenerator.getInt(var2), 0, 4);
      var1.set(DataComponents.OMINOUS_BOTTLE_AMPLIFIER, var3);
      return var1;
   }

   public NumberProvider amplifier() {
      return this.amplifierGenerator;
   }

   public static LootItemConditionalFunction.Builder<?> setAmplifier(NumberProvider var0) {
      return simpleBuilder((var1) -> {
         return new SetOminousBottleAmplifierFunction(var1, var0);
      });
   }
}
