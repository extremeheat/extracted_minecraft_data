package net.minecraft.world.level.storage.loot.functions;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.mojang.serialization.codecs.RecordCodecBuilder.Instance;
import java.util.List;
import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;

public class SetComponentsFunction extends LootItemConditionalFunction {
   public static final Codec<SetComponentsFunction> CODEC = RecordCodecBuilder.create(
      var0 -> commonFields(var0)
            .and(DataComponentPatch.CODEC.fieldOf("components").forGetter(var0x -> var0x.components))
            .apply(var0, SetComponentsFunction::new)
   );
   private final DataComponentPatch components;

   private SetComponentsFunction(List<LootItemCondition> var1, DataComponentPatch var2) {
      super(var1);
      this.components = var2;
   }

   @Override
   public LootItemFunctionType getType() {
      return LootItemFunctions.SET_COMPONENTS;
   }

   @Override
   public ItemStack run(ItemStack var1, LootContext var2) {
      var1.applyComponents(this.components);
      return var1;
   }

   public static <T> LootItemConditionalFunction.Builder<?> setComponent(DataComponentType<T> var0, T var1) {
      return simpleBuilder(var2 -> new SetComponentsFunction(var2, DataComponentPatch.builder().set(var0, (T)var1).build()));
   }
}
