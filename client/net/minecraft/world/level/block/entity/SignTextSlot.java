package net.minecraft.world.level.block.entity;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

public enum SignTextSlot {
   BACK(0),
   FRONT(1);

   private final int id;
   public static final StreamCodec<ByteBuf, SignTextSlot> STREAM_CODEC = ByteBufCodecs.enumCodec(SignTextSlot.class, (h) -> h.id);

   private SignTextSlot(final int id) {
      this.id = id;
   }

   // $FF: synthetic method
   private static SignTextSlot[] $values() {
      return new SignTextSlot[]{BACK, FRONT};
   }
}
