package net.minecraft.network.protocol.game;

import io.netty.buffer.ByteBuf;
import java.io.IOException;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;
import net.minecraft.resources.ResourceLocation;

public class ServerboundCustomPayloadPacket implements Packet<ServerGamePacketListener> {
   public static final ResourceLocation BRAND = new ResourceLocation("brand");
   private ResourceLocation identifier;
   private FriendlyByteBuf data;

   public ServerboundCustomPayloadPacket() {
      super();
   }

   public ServerboundCustomPayloadPacket(ResourceLocation var1, FriendlyByteBuf var2) {
      super();
      this.identifier = var1;
      this.data = var2;
   }

   public void read(FriendlyByteBuf var1) throws IOException {
      this.identifier = var1.readResourceLocation();
      int var2 = var1.readableBytes();
      if (var2 >= 0 && var2 <= 32767) {
         this.data = new FriendlyByteBuf(var1.readBytes(var2));
      } else {
         throw new IOException("Payload may not be larger than 32767 bytes");
      }
   }

   public void write(FriendlyByteBuf var1) throws IOException {
      var1.writeResourceLocation(this.identifier);
      var1.writeBytes((ByteBuf)this.data);
   }

   public void handle(ServerGamePacketListener var1) {
      var1.handleCustomPayload(this);
      if (this.data != null) {
         this.data.release();
      }

   }
}
