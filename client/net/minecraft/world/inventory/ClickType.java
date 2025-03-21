package net.minecraft.world.inventory;

import io.netty.buffer.ByteBuf;
import java.util.function.IntFunction;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.ByIdMap;

public enum ClickType {
   PICKUP(0),
   QUICK_MOVE(1),
   SWAP(2),
   CLONE(3),
   THROW(4),
   QUICK_CRAFT(5),
   PICKUP_ALL(6);

   private static final IntFunction<ClickType> BY_ID = ByIdMap.<ClickType>continuous(ClickType::id, values(), ByIdMap.OutOfBoundsStrategy.ZERO);
   public static final StreamCodec<ByteBuf, ClickType> STREAM_CODEC = ByteBufCodecs.idMapper(BY_ID, ClickType::id);
   private final int id;

   private ClickType(final int var3) {
      this.id = var3;
   }

   public int id() {
      return this.id;
   }

   // $FF: synthetic method
   private static ClickType[] $values() {
      return new ClickType[]{PICKUP, QUICK_MOVE, SWAP, CLONE, THROW, QUICK_CRAFT, PICKUP_ALL};
   }
}
