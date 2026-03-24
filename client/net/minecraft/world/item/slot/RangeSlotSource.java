package net.minecraft.world.item.slot;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Set;
import net.minecraft.util.context.ContextKey;
import net.minecraft.world.entity.SlotProvider;
import net.minecraft.world.inventory.SlotRange;
import net.minecraft.world.inventory.SlotRanges;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.LootContextArg;

public class RangeSlotSource implements SlotSource {
   public static final MapCodec<RangeSlotSource> MAP_CODEC = RecordCodecBuilder.mapCodec((i) -> i.group(LootContextArg.ENTITY_OR_BLOCK.fieldOf("source").forGetter((t) -> t.source), SlotRanges.CODEC.fieldOf("slots").forGetter((t) -> t.slotRange)).apply(i, RangeSlotSource::new));
   private final LootContextArg<Object> source;
   private final SlotRange slotRange;

   private RangeSlotSource(final LootContextArg<Object> source, final SlotRange slotRange) {
      super();
      this.source = source;
      this.slotRange = slotRange;
   }

   public MapCodec<RangeSlotSource> codec() {
      return MAP_CODEC;
   }

   public Set<ContextKey<?>> getReferencedContextParams() {
      return Set.of(this.source.contextParam());
   }

   public final SlotCollection provide(final LootContext context) {
      Object maybeProvider = this.source.get(context);
      if (maybeProvider instanceof SlotProvider slotProvider) {
         return slotProvider.getSlotsFromRange(this.slotRange.slots());
      } else {
         return SlotCollection.EMPTY;
      }
   }
}
