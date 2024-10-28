package net.minecraft.network.protocol.common.custom;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;

public record GameTestClearMarkersDebugPayload() implements CustomPacketPayload {
   public static final StreamCodec<FriendlyByteBuf, GameTestClearMarkersDebugPayload> STREAM_CODEC = CustomPacketPayload.codec(GameTestClearMarkersDebugPayload::write, GameTestClearMarkersDebugPayload::new);
   public static final CustomPacketPayload.Type<GameTestClearMarkersDebugPayload> TYPE = CustomPacketPayload.createType("debug/game_test_clear");

   private GameTestClearMarkersDebugPayload(FriendlyByteBuf var1) {
      this();
   }

   public GameTestClearMarkersDebugPayload() {
      super();
   }

   private void write(FriendlyByteBuf var1) {
   }

   public CustomPacketPayload.Type<GameTestClearMarkersDebugPayload> type() {
      return TYPE;
   }
}
