package net.minecraft.network.protocol.common.custom;

import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;

public record PoiAddedDebugPayload(BlockPos b, String c, int d) implements CustomPacketPayload {
   private final BlockPos pos;
   private final String type;
   private final int freeTicketCount;
   public static final ResourceLocation ID = new ResourceLocation("debug/poi_added");

   public PoiAddedDebugPayload(FriendlyByteBuf var1) {
      this(var1.readBlockPos(), var1.readUtf(), var1.readInt());
   }

   public PoiAddedDebugPayload(BlockPos var1, String var2, int var3) {
      super();
      this.pos = var1;
      this.type = var2;
      this.freeTicketCount = var3;
   }

   @Override
   public void write(FriendlyByteBuf var1) {
      var1.writeBlockPos(this.pos);
      var1.writeUtf(this.type);
      var1.writeInt(this.freeTicketCount);
   }

   @Override
   public ResourceLocation id() {
      return ID;
   }
}
