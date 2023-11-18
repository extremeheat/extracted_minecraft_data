package net.minecraft.network.protocol.common.custom;

import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;

public record GameTestAddMarkerDebugPayload(BlockPos b, int c, String d, int e) implements CustomPacketPayload {
   private final BlockPos pos;
   private final int color;
   private final String text;
   private final int durationMs;
   public static final ResourceLocation ID = new ResourceLocation("debug/game_test_add_marker");

   public GameTestAddMarkerDebugPayload(FriendlyByteBuf var1) {
      this(var1.readBlockPos(), var1.readInt(), var1.readUtf(), var1.readInt());
   }

   public GameTestAddMarkerDebugPayload(BlockPos var1, int var2, String var3, int var4) {
      super();
      this.pos = var1;
      this.color = var2;
      this.text = var3;
      this.durationMs = var4;
   }

   @Override
   public void write(FriendlyByteBuf var1) {
      var1.writeBlockPos(this.pos);
      var1.writeInt(this.color);
      var1.writeUtf(this.text);
      var1.writeInt(this.durationMs);
   }

   @Override
   public ResourceLocation id() {
      return ID;
   }
}
