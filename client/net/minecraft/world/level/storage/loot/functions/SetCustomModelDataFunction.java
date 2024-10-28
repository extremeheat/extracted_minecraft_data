package net.minecraft.world.level.storage.loot.functions;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import java.util.Set;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomModelData;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParam;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.providers.number.NumberProvider;
import net.minecraft.world.level.storage.loot.providers.number.NumberProviders;

public class SetCustomModelDataFunction extends LootItemConditionalFunction {
   static final MapCodec<SetCustomModelDataFunction> CODEC = RecordCodecBuilder.mapCodec((var0) -> {
      return commonFields(var0).and(NumberProviders.CODEC.fieldOf("value").forGetter((var0x) -> {
         return var0x.valueProvider;
      })).apply(var0, SetCustomModelDataFunction::new);
   });
   private final NumberProvider valueProvider;

   private SetCustomModelDataFunction(List<LootItemCondition> var1, NumberProvider var2) {
      super(var1);
      this.valueProvider = var2;
   }

   public Set<LootContextParam<?>> getReferencedContextParams() {
      return this.valueProvider.getReferencedContextParams();
   }

   public LootItemFunctionType<SetCustomModelDataFunction> getType() {
      return LootItemFunctions.SET_CUSTOM_MODEL_DATA;
   }

   public ItemStack run(ItemStack var1, LootContext var2) {
      var1.set(DataComponents.CUSTOM_MODEL_DATA, new CustomModelData(this.valueProvider.getInt(var2)));
      return var1;
   }
}
