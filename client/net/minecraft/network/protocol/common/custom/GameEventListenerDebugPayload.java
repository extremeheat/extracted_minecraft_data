package net.minecraft.network.protocol.common.custom;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.gameevent.PositionSource;
import net.minecraft.world.level.gameevent.PositionSourceType;

public record GameEventListenerDebugPayload(PositionSource b, int c) implements CustomPacketPayload {
   private final PositionSource listenerPos;
   private final int listenerRange;
   public static final ResourceLocation ID = new ResourceLocation("debug/game_event_listeners");

   public GameEventListenerDebugPayload(FriendlyByteBuf var1) {
      this(PositionSourceType.fromNetwork(var1), var1.readVarInt());
   }

   public GameEventListenerDebugPayload(PositionSource var1, int var2) {
      super();
      this.listenerPos = var1;
      this.listenerRange = var2;
   }

   @Override
   public void write(FriendlyByteBuf var1) {
      PositionSourceType.toNetwork(this.listenerPos, var1);
      var1.writeVarInt(this.listenerRange);
   }

   @Override
   public ResourceLocation id() {
      return ID;
   }
}
