package net.minecraft.network.protocol.common.custom;

import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;

public record PoiRemovedDebugPayload(BlockPos b) implements CustomPacketPayload {
   private final BlockPos pos;
   public static final ResourceLocation ID = new ResourceLocation("debug/poi_removed");

   public PoiRemovedDebugPayload(FriendlyByteBuf var1) {
      this(var1.readBlockPos());
   }

   public PoiRemovedDebugPayload(BlockPos var1) {
      super();
      this.pos = var1;
   }

   @Override
   public void write(FriendlyByteBuf var1) {
      var1.writeBlockPos(this.pos);
   }

   @Override
   public ResourceLocation id() {
      return ID;
   }
}
