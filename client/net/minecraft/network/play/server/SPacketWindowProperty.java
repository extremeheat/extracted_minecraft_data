package net.minecraft.network.play.server;

import java.io.IOException;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayClient;

public class SPacketWindowProperty implements Packet<INetHandlerPlayClient> {
   private int field_149186_a;
   private int field_149184_b;
   private int field_149185_c;

   public SPacketWindowProperty() {
      super();
   }

   public SPacketWindowProperty(int var1, int var2, int var3) {
      super();
      this.field_149186_a = var1;
      this.field_149184_b = var2;
      this.field_149185_c = var3;
   }

   public void func_148833_a(INetHandlerPlayClient var1) {
      var1.func_147245_a(this);
   }

   public void func_148837_a(PacketBuffer var1) throws IOException {
      this.field_149186_a = var1.readUnsignedByte();
      this.field_149184_b = var1.readShort();
      this.field_149185_c = var1.readShort();
   }

   public void func_148840_b(PacketBuffer var1) throws IOException {
      var1.writeByte(this.field_149186_a);
      var1.writeShort(this.field_149184_b);
      var1.writeShort(this.field_149185_c);
   }

   public int func_149182_c() {
      return this.field_149186_a;
   }

   public int func_149181_d() {
      return this.field_149184_b;
   }

   public int func_149180_e() {
      return this.field_149185_c;
   }
}
