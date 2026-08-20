package net.minecraft.world.level.block.entity;

import io.netty.buffer.ByteBuf;
import java.util.function.IntFunction;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.ByIdMap;

public enum SignTextSlot {
   BACK(0),
   FRONT(1);

   private final int id;
   private static final IntFunction<SignTextSlot> BY_ID = ByIdMap.<SignTextSlot>continuous((h) -> h.id, values(), ByIdMap.OutOfBoundsStrategy.ZERO);
   public static final StreamCodec<ByteBuf, SignTextSlot> STREAM_CODEC = ByteBufCodecs.idMapper(BY_ID, (h) -> h.id);

   private SignTextSlot(final int id) {
      this.id = id;
   }

   // $FF: synthetic method
   private static SignTextSlot[] $values() {
      return new SignTextSlot[]{BACK, FRONT};
   }
}
