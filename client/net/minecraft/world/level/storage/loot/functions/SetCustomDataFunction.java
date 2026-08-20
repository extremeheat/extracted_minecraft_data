package net.minecraft.world.level.storage.loot.functions;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Optional;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.TagParser;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;

public class SetCustomDataFunction extends LootItemConditionalFunction {
   public static final MapCodec<SetCustomDataFunction> MAP_CODEC = RecordCodecBuilder.mapCodec((i) -> commonFields(i).and(TagParser.LENIENT_CODEC.fieldOf("tag").forGetter((f) -> f.tag)).apply(i, SetCustomDataFunction::new));
   private final CompoundTag tag;

   private SetCustomDataFunction(final Optional<Holder<LootItemCondition>> condition, final CompoundTag tag) {
      super(condition);
      this.tag = tag;
   }

   public MapCodec<SetCustomDataFunction> codec() {
      return MAP_CODEC;
   }

   public ItemStack run(final ItemStack itemStack, final LootContext context) {
      CustomData.update(DataComponents.CUSTOM_DATA, itemStack, (tag) -> tag.merge(this.tag));
      return itemStack;
   }

   /** @deprecated */
   @Deprecated
   public static LootItemConditionalFunction.Builder<?> setCustomData(final CompoundTag value) {
      return simpleBuilder((conditions) -> new SetCustomDataFunction(conditions, value));
   }
}
