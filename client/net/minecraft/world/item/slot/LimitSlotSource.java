package net.minecraft.world.item.slot;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.ExtraCodecs;

public class LimitSlotSource extends TransformedSlotSource {
   public static final MapCodec<LimitSlotSource> MAP_CODEC = RecordCodecBuilder.mapCodec((var0) -> commonFields(var0).and(ExtraCodecs.POSITIVE_INT.fieldOf("limit").forGetter((var0x) -> var0x.limit)).apply(var0, LimitSlotSource::new));
   private final int limit;

   private LimitSlotSource(SlotSource var1, int var2) {
      super(var1);
      this.limit = var2;
   }

   public MapCodec<LimitSlotSource> codec() {
      return MAP_CODEC;
   }

   protected SlotCollection transform(SlotCollection var1) {
      return var1.limit(this.limit);
   }
}
