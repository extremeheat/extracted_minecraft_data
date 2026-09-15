package net.minecraft.world.item.slot;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.HolderSet;

public class GroupSlotSource extends CompositeSlotSource {
   public static final MapCodec<GroupSlotSource> MAP_CODEC = createCodec(GroupSlotSource::new);
   public static final Codec<GroupSlotSource> INLINE_CODEC = createInlineCodec(GroupSlotSource::new);

   private GroupSlotSource(final HolderSet<SlotSource> terms) {
      super(terms);
   }

   public MapCodec<GroupSlotSource> codec() {
      return MAP_CODEC;
   }
}
