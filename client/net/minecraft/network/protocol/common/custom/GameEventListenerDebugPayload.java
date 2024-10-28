package net.minecraft.network.protocol.common.custom;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.level.gameevent.PositionSource;

public record GameEventListenerDebugPayload(PositionSource listenerPos, int listenerRange) implements CustomPacketPayload {
   public static final StreamCodec<RegistryFriendlyByteBuf, GameEventListenerDebugPayload> STREAM_CODEC;
   public static final CustomPacketPayload.Type<GameEventListenerDebugPayload> TYPE;

   public GameEventListenerDebugPayload(PositionSource listenerPos, int listenerRange) {
      super();
      this.listenerPos = listenerPos;
      this.listenerRange = listenerRange;
   }

   public CustomPacketPayload.Type<GameEventListenerDebugPayload> type() {
      return TYPE;
   }

   public PositionSource listenerPos() {
      return this.listenerPos;
   }

   public int listenerRange() {
      return this.listenerRange;
   }

   static {
      STREAM_CODEC = StreamCodec.composite(PositionSource.STREAM_CODEC, GameEventListenerDebugPayload::listenerPos, ByteBufCodecs.VAR_INT, GameEventListenerDebugPayload::listenerRange, GameEventListenerDebugPayload::new);
      TYPE = CustomPacketPayload.createType("debug/game_event_listeners");
   }
}
