package net.minecraft.network.protocol.common.custom;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;

public record GameTestClearMarkersDebugPayload() implements CustomPacketPayload {
   public static final ResourceLocation ID = new ResourceLocation("debug/game_test_clear");

   public GameTestClearMarkersDebugPayload(FriendlyByteBuf var1) {
      this();
   }

   public GameTestClearMarkersDebugPayload() {
      super();
   }

   @Override
   public void write(FriendlyByteBuf var1) {
   }

   @Override
   public ResourceLocation id() {
      return ID;
   }
}
