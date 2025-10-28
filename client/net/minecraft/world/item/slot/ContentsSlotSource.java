package net.minecraft.world.item.slot;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Objects;
import net.minecraft.world.level.storage.loot.ContainerComponentManipulator;
import net.minecraft.world.level.storage.loot.ContainerComponentManipulators;

public class ContentsSlotSource extends TransformedSlotSource {
   public static final MapCodec<ContentsSlotSource> MAP_CODEC = RecordCodecBuilder.mapCodec((var0) -> commonFields(var0).and(ContainerComponentManipulators.CODEC.fieldOf("component").forGetter((var0x) -> var0x.component)).apply(var0, ContentsSlotSource::new));
   private final ContainerComponentManipulator<?> component;

   private ContentsSlotSource(SlotSource var1, ContainerComponentManipulator<?> var2) {
      super(var1);
      this.component = var2;
   }

   public MapCodec<ContentsSlotSource> codec() {
      return MAP_CODEC;
   }

   protected SlotCollection transform(SlotCollection var1) {
      ContainerComponentManipulator var10001 = this.component;
      Objects.requireNonNull(var10001);
      return var1.flatMap(var10001::getSlots);
   }
}
