package net.minecraft.network.play.client;

import io.netty.buffer.ByteBuf;
import java.io.IOException;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayServer;
import net.minecraft.util.ResourceLocation;

public class CPacketCustomPayload implements Packet<INetHandlerPlayServer> {
   public static final ResourceLocation field_210344_a = new ResourceLocation("minecraft:brand");
   private ResourceLocation field_149562_a;
   private PacketBuffer field_149561_c;

   public CPacketCustomPayload() {
      super();
   }

   public CPacketCustomPayload(ResourceLocation var1, PacketBuffer var2) {
      super();
      this.field_149562_a = var1;
      this.field_149561_c = var2;
   }

   public void func_148837_a(PacketBuffer var1) throws IOException {
      this.field_149562_a = var1.func_192575_l();
      int var2 = var1.readableBytes();
      if (var2 >= 0 && var2 <= 32767) {
         this.field_149561_c = new PacketBuffer(var1.readBytes(var2));
      } else {
         throw new IOException("Payload may not be larger than 32767 bytes");
      }
   }

   public void func_148840_b(PacketBuffer var1) throws IOException {
      var1.func_192572_a(this.field_149562_a);
      var1.writeBytes((ByteBuf)this.field_149561_c);
   }

   public void func_148833_a(INetHandlerPlayServer var1) {
      var1.func_147349_a(this);
      if (this.field_149561_c != null) {
         this.field_149561_c.release();
      }

   }
}
