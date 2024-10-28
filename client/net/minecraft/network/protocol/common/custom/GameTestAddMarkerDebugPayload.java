package net.minecraft.network.protocol.common.custom;

import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;

public record GameTestAddMarkerDebugPayload(BlockPos pos, int color, String text, int durationMs) implements CustomPacketPayload {
   public static final StreamCodec<FriendlyByteBuf, GameTestAddMarkerDebugPayload> STREAM_CODEC = CustomPacketPayload.codec(GameTestAddMarkerDebugPayload::write, GameTestAddMarkerDebugPayload::new);
   public static final CustomPacketPayload.Type<GameTestAddMarkerDebugPayload> TYPE = CustomPacketPayload.createType("debug/game_test_add_marker");

   private GameTestAddMarkerDebugPayload(FriendlyByteBuf var1) {
      this(var1.readBlockPos(), var1.readInt(), var1.readUtf(), var1.readInt());
   }

   public GameTestAddMarkerDebugPayload(BlockPos var1, int var2, String var3, int var4) {
      super();
      this.pos = var1;
      this.color = var2;
      this.text = var3;
      this.durationMs = var4;
   }

   private void write(FriendlyByteBuf var1) {
      var1.writeBlockPos(this.pos);
      var1.writeInt(this.color);
      var1.writeUtf(this.text);
      var1.writeInt(this.durationMs);
   }

   public CustomPacketPayload.Type<GameTestAddMarkerDebugPayload> type() {
      return TYPE;
   }

   public BlockPos pos() {
      return this.pos;
   }

   public int color() {
      return this.color;
   }

   public String text() {
      return this.text;
   }

   public int durationMs() {
      return this.durationMs;
   }
}
