package net.minecraft.world.inventory;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

public enum ContainerInput {
   PICKUP(0),
   QUICK_MOVE(1),
   SWAP(2),
   CLONE(3),
   THROW(4),
   QUICK_CRAFT(5),
   PICKUP_ALL(6);

   public static final StreamCodec<ByteBuf, ContainerInput> STREAM_CODEC = ByteBufCodecs.enumCodec(ContainerInput.class, ContainerInput::id);
   private final int id;

   private ContainerInput(final int id) {
      this.id = id;
   }

   public int id() {
      return this.id;
   }

   // $FF: synthetic method
   private static ContainerInput[] $values() {
      return new ContainerInput[]{PICKUP, QUICK_MOVE, SWAP, CLONE, THROW, QUICK_CRAFT, PICKUP_ALL};
   }
}
