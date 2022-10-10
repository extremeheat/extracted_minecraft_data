package net.minecraft.network.play.client;

import java.io.IOException;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayServer;

public class CPacketSteerBoat implements Packet<INetHandlerPlayServer> {
   private boolean field_187015_a;
   private boolean field_187016_b;

   public CPacketSteerBoat() {
      super();
   }

   public CPacketSteerBoat(boolean var1, boolean var2) {
      super();
      this.field_187015_a = var1;
      this.field_187016_b = var2;
   }

   public void func_148837_a(PacketBuffer var1) throws IOException {
      this.field_187015_a = var1.readBoolean();
      this.field_187016_b = var1.readBoolean();
   }

   public void func_148840_b(PacketBuffer var1) throws IOException {
      var1.writeBoolean(this.field_187015_a);
      var1.writeBoolean(this.field_187016_b);
   }

   public void func_148833_a(INetHandlerPlayServer var1) {
      var1.func_184340_a(this);
   }

   public boolean func_187012_a() {
      return this.field_187015_a;
   }

   public boolean func_187014_b() {
      return this.field_187016_b;
   }
}
