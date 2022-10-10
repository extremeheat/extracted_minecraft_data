package net.minecraft.network.login.server;

import java.io.IOException;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.login.INetHandlerLoginClient;
import net.minecraft.util.ResourceLocation;

public class SPacketCustomPayloadLogin implements Packet<INetHandlerLoginClient> {
   private int field_209919_a;
   private ResourceLocation field_209920_b;
   private PacketBuffer field_209921_c;

   public SPacketCustomPayloadLogin() {
      super();
   }

   public void func_148837_a(PacketBuffer var1) throws IOException {
      this.field_209919_a = var1.func_150792_a();
      this.field_209920_b = var1.func_192575_l();
      int var2 = var1.readableBytes();
      if (var2 >= 0 && var2 <= 1048576) {
         this.field_209921_c = new PacketBuffer(var1.readBytes(var2));
      } else {
         throw new IOException("Payload may not be larger than 1048576 bytes");
      }
   }

   public void func_148840_b(PacketBuffer var1) throws IOException {
      var1.func_150787_b(this.field_209919_a);
      var1.func_192572_a(this.field_209920_b);
      var1.writeBytes(this.field_209921_c.copy());
   }

   public void func_148833_a(INetHandlerLoginClient var1) {
      var1.func_209521_a(this);
   }

   public int func_209918_a() {
      return this.field_209919_a;
   }
}
