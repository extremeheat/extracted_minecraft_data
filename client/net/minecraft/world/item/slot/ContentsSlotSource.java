package net.minecraft.world.item.slot;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Objects;
import net.minecraft.world.level.storage.loot.ContainerComponentManipulator;
import net.minecraft.world.level.storage.loot.ContainerComponentManipulators;

public class ContentsSlotSource extends TransformedSlotSource {
   public static final MapCodec<ContentsSlotSource> MAP_CODEC = RecordCodecBuilder.mapCodec((i) -> commonFields(i).and(ContainerComponentManipulators.CODEC.fieldOf("component").forGetter((t) -> t.component)).apply(i, ContentsSlotSource::new));
   private final ContainerComponentManipulator<?> component;

   private ContentsSlotSource(final SlotSource slotSource, final ContainerComponentManipulator<?> component) {
      super(slotSource);
      this.component = component;
   }

   public MapCodec<ContentsSlotSource> codec() {
      return MAP_CODEC;
   }

   protected SlotCollection transform(final SlotCollection slots) {
      ContainerComponentManipulator var10001 = this.component;
      Objects.requireNonNull(var10001);
      return slots.flatMap(var10001::getSlots);
   }
}
