package net.minecraft.network.protocol.common.custom;

import io.netty.buffer.ByteBuf;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.level.redstone.Orientation;

public record RedstoneWireOrientationsDebugPayload(long time, List<Wire> wires) implements CustomPacketPayload {
   public static final CustomPacketPayload.Type<RedstoneWireOrientationsDebugPayload> TYPE = CustomPacketPayload.createType("debug/redstone_update_order");
   public static final StreamCodec<FriendlyByteBuf, RedstoneWireOrientationsDebugPayload> STREAM_CODEC;

   public RedstoneWireOrientationsDebugPayload(long var1, List<Wire> var3) {
      super();
      this.time = var1;
      this.wires = var3;
   }

   public CustomPacketPayload.Type<RedstoneWireOrientationsDebugPayload> type() {
      return TYPE;
   }

   public long time() {
      return this.time;
   }

   public List<Wire> wires() {
      return this.wires;
   }

   static {
      STREAM_CODEC = StreamCodec.composite(ByteBufCodecs.VAR_LONG, RedstoneWireOrientationsDebugPayload::time, RedstoneWireOrientationsDebugPayload.Wire.STREAM_CODEC.apply(ByteBufCodecs.list()), RedstoneWireOrientationsDebugPayload::wires, RedstoneWireOrientationsDebugPayload::new);
   }

   public static record Wire(BlockPos pos, Orientation orientation) {
      public static final StreamCodec<ByteBuf, Wire> STREAM_CODEC;

      public Wire(BlockPos var1, Orientation var2) {
         super();
         this.pos = var1;
         this.orientation = var2;
      }

      public BlockPos pos() {
         return this.pos;
      }

      public Orientation orientation() {
         return this.orientation;
      }

      static {
         STREAM_CODEC = StreamCodec.composite(BlockPos.STREAM_CODEC, Wire::pos, Orientation.STREAM_CODEC, Wire::orientation, Wire::new);
      }
   }
}
