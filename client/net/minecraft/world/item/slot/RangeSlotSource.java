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
   public static final MapCodec<RangeSlotSource> MAP_CODEC = RecordCodecBuilder.mapCodec((var0) -> var0.group(LootContextArg.ENTITY_OR_BLOCK.fieldOf("source").forGetter((var0x) -> var0x.source), SlotRanges.CODEC.fieldOf("slots").forGetter((var0x) -> var0x.slotRange)).apply(var0, RangeSlotSource::new));
   private final LootContextArg<Object> source;
   private final SlotRange slotRange;

   private RangeSlotSource(LootContextArg<Object> var1, SlotRange var2) {
      super();
      this.source = var1;
      this.slotRange = var2;
   }

   public MapCodec<RangeSlotSource> codec() {
      return MAP_CODEC;
   }

   public Set<ContextKey<?>> getReferencedContextParams() {
      return Set.of(this.source.contextParam());
   }

   public final SlotCollection provide(LootContext var1) {
      Object var2 = this.source.get(var1);
      if (var2 instanceof SlotProvider var3) {
         return var3.getSlotsFromRange(this.slotRange.slots());
      } else {
         return SlotCollection.EMPTY;
      }
   }
}
