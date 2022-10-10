package net.minecraft.network.login.client;

import java.io.IOException;
import javax.annotation.Nullable;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.login.INetHandlerLoginServer;

public class CPacketCustomPayloadLogin implements Packet<INetHandlerLoginServer> {
   private int field_209922_a;
   private PacketBuffer field_209923_b;

   public CPacketCustomPayloadLogin() {
      super();
   }

   public CPacketCustomPayloadLogin(int var1, @Nullable PacketBuffer var2) {
      super();
      this.field_209922_a = var1;
      this.field_209923_b = var2;
   }

   public void func_148837_a(PacketBuffer var1) throws IOException {
      this.field_209922_a = var1.func_150792_a();
      if (var1.readBoolean()) {
         int var2 = var1.readableBytes();
         if (var2 < 0 || var2 > 1048576) {
            throw new IOException("Payload may not be larger than 1048576 bytes");
         }

         this.field_209923_b = new PacketBuffer(var1.readBytes(var2));
      } else {
         this.field_209923_b = null;
      }

   }

   public void func_148840_b(PacketBuffer var1) throws IOException {
      var1.func_150787_b(this.field_209922_a);
      if (this.field_209923_b != null) {
         var1.writeBoolean(true);
         var1.writeBytes(this.field_209923_b.copy());
      } else {
         var1.writeBoolean(false);
      }

   }

   public void func_148833_a(INetHandlerLoginServer var1) {
      var1.func_209526_a(this);
   }
}
