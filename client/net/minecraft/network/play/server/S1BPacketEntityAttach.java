package net.minecraft.network.play.server;

import java.io.IOException;
import net.minecraft.entity.Entity;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayClient;

public class S1BPacketEntityAttach implements Packet<INetHandlerPlayClient> {
   private int field_149408_a;
   private int field_149406_b;
   private int field_149407_c;

   public S1BPacketEntityAttach() {
      super();
   }

   public S1BPacketEntityAttach(int var1, Entity var2, Entity var3) {
      super();
      this.field_149408_a = var1;
      this.field_149406_b = var2.func_145782_y();
      this.field_149407_c = var3 != null ? var3.func_145782_y() : -1;
   }

   public void func_148837_a(PacketBuffer var1) throws IOException {
      this.field_149406_b = var1.readInt();
      this.field_149407_c = var1.readInt();
      this.field_149408_a = var1.readUnsignedByte();
   }

   public void func_148840_b(PacketBuffer var1) throws IOException {
      var1.writeInt(this.field_149406_b);
      var1.writeInt(this.field_149407_c);
      var1.writeByte(this.field_149408_a);
   }

   public void func_148833_a(INetHandlerPlayClient var1) {
      var1.func_147243_a(this);
   }

   public int func_149404_c() {
      return this.field_149408_a;
   }

   public int func_149403_d() {
      return this.field_149406_b;
   }

   public int func_149402_e() {
      return this.field_149407_c;
   }
}
