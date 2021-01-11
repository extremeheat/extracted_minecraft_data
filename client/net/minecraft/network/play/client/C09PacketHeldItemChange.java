package net.minecraft.network.play.client;

import java.io.IOException;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayServer;

public class C09PacketHeldItemChange implements Packet<INetHandlerPlayServer> {
   private int field_149615_a;

   public C09PacketHeldItemChange() {
      super();
   }

   public C09PacketHeldItemChange(int var1) {
      super();
      this.field_149615_a = var1;
   }

   public void func_148837_a(PacketBuffer var1) throws IOException {
      this.field_149615_a = var1.readShort();
   }

   public void func_148840_b(PacketBuffer var1) throws IOException {
      var1.writeShort(this.field_149615_a);
   }

   public void func_148833_a(INetHandlerPlayServer var1) {
      var1.func_147355_a(this);
   }

   public int func_149614_c() {
      return this.field_149615_a;
   }
}
