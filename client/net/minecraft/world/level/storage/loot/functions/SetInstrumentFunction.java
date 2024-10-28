package net.minecraft.world.level.storage.loot.functions;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import net.minecraft.core.registries.Registries;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Instrument;
import net.minecraft.world.item.InstrumentItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;

public class SetInstrumentFunction extends LootItemConditionalFunction {
   public static final MapCodec<SetInstrumentFunction> CODEC = RecordCodecBuilder.mapCodec((var0) -> {
      return commonFields(var0).and(TagKey.hashedCodec(Registries.INSTRUMENT).fieldOf("options").forGetter((var0x) -> {
         return var0x.options;
      })).apply(var0, SetInstrumentFunction::new);
   });
   private final TagKey<Instrument> options;

   private SetInstrumentFunction(List<LootItemCondition> var1, TagKey<Instrument> var2) {
      super(var1);
      this.options = var2;
   }

   public LootItemFunctionType<SetInstrumentFunction> getType() {
      return LootItemFunctions.SET_INSTRUMENT;
   }

   public ItemStack run(ItemStack var1, LootContext var2) {
      InstrumentItem.setRandom(var1, this.options, var2.getRandom());
      return var1;
   }

   public static LootItemConditionalFunction.Builder<?> setInstrumentOptions(TagKey<Instrument> var0) {
      return simpleBuilder((var1) -> {
         return new SetInstrumentFunction(var1, var0);
      });
   }
}
