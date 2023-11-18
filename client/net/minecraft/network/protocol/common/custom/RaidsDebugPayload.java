package net.minecraft.network.protocol.common.custom;

import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;

public record RaidsDebugPayload(List<BlockPos> b) implements CustomPacketPayload {
   private final List<BlockPos> raidCenters;
   public static final ResourceLocation ID = new ResourceLocation("debug/raids");

   public RaidsDebugPayload(FriendlyByteBuf var1) {
      this(var1.readList(FriendlyByteBuf::readBlockPos));
   }

   public RaidsDebugPayload(List<BlockPos> var1) {
      super();
      this.raidCenters = var1;
   }

   @Override
   public void write(FriendlyByteBuf var1) {
      var1.writeCollection(this.raidCenters, FriendlyByteBuf::writeBlockPos);
   }

   @Override
   public ResourceLocation id() {
      return ID;
   }
}
