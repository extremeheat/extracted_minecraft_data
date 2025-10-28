package net.minecraft.world.item.slot;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.advancements.critereon.ItemPredicate;

public class FilteredSlotSource extends TransformedSlotSource {
   public static final MapCodec<FilteredSlotSource> MAP_CODEC = RecordCodecBuilder.mapCodec((var0) -> commonFields(var0).and(ItemPredicate.CODEC.fieldOf("item_filter").forGetter((var0x) -> var0x.filter)).apply(var0, FilteredSlotSource::new));
   private final ItemPredicate filter;

   private FilteredSlotSource(SlotSource var1, ItemPredicate var2) {
      super(var1);
      this.filter = var2;
   }

   public MapCodec<FilteredSlotSource> codec() {
      return MAP_CODEC;
   }

   protected SlotCollection transform(SlotCollection var1) {
      return var1.filter(this.filter);
   }
}
