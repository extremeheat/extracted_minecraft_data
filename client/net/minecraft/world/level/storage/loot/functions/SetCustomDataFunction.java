package net.minecraft.world.level.storage.loot.functions;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.TagParser;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;

public class SetCustomDataFunction extends LootItemConditionalFunction {
   public static final MapCodec<SetCustomDataFunction> CODEC = RecordCodecBuilder.mapCodec(
      var0 -> commonFields(var0).and(TagParser.AS_CODEC.fieldOf("tag").forGetter(var0x -> var0x.tag)).apply(var0, SetCustomDataFunction::new)
   );
   private final CompoundTag tag;

   private SetCustomDataFunction(List<LootItemCondition> var1, CompoundTag var2) {
      super(var1);
      this.tag = var2;
   }

   @Override
   public LootItemFunctionType getType() {
      return LootItemFunctions.SET_CUSTOM_DATA;
   }

   @Override
   public ItemStack run(ItemStack var1, LootContext var2) {
      CustomData.update(DataComponents.CUSTOM_DATA, var1, var1x -> var1x.merge(this.tag));
      return var1;
   }

   @Deprecated
   public static LootItemConditionalFunction.Builder<?> setCustomData(CompoundTag var0) {
      return simpleBuilder(var1 -> new SetCustomDataFunction(var1, var0));
   }
}
