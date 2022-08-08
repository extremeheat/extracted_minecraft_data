package net.minecraft.network.protocol.game;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;
import net.minecraft.resources.ResourceLocation;

public class ServerboundCustomPayloadPacket implements Packet<ServerGamePacketListener> {
   private static final int MAX_PAYLOAD_SIZE = 32767;
   public static final ResourceLocation BRAND = new ResourceLocation("brand");
   private final ResourceLocation identifier;
   private final FriendlyByteBuf data;

   public ServerboundCustomPayloadPacket(ResourceLocation var1, FriendlyByteBuf var2) {
      super();
      this.identifier = var1;
      this.data = var2;
   }

   public ServerboundCustomPayloadPacket(FriendlyByteBuf var1) {
      super();
      this.identifier = var1.readResourceLocation();
      int var2 = var1.readableBytes();
      if (var2 >= 0 && var2 <= 32767) {
         this.data = new FriendlyByteBuf(var1.readBytes(var2));
      } else {
         throw new IllegalArgumentException("Payload may not be larger than 32767 bytes");
      }
   }

   public void write(FriendlyByteBuf var1) {
      var1.writeResourceLocation(this.identifier);
      var1.writeBytes((ByteBuf)this.data);
   }

   public void handle(ServerGamePacketListener var1) {
      var1.handleCustomPayload(this);
      this.data.release();
   }

   public ResourceLocation getIdentifier() {
      return this.identifier;
   }

   public FriendlyByteBuf getData() {
      return this.data;
   }
}
