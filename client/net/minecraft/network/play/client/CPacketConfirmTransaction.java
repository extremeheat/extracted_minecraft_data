package net.minecraft.network.play.client;

import java.io.IOException;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayServer;

public class CPacketConfirmTransaction implements Packet<INetHandlerPlayServer> {
   private int field_149536_a;
   private short field_149534_b;
   private boolean field_149535_c;

   public CPacketConfirmTransaction() {
      super();
   }

   public CPacketConfirmTransaction(int var1, short var2, boolean var3) {
      super();
      this.field_149536_a = var1;
      this.field_149534_b = var2;
      this.field_149535_c = var3;
   }

   public void func_148833_a(INetHandlerPlayServer var1) {
      var1.func_147339_a(this);
   }

   public void func_148837_a(PacketBuffer var1) throws IOException {
      this.field_149536_a = var1.readByte();
      this.field_149534_b = var1.readShort();
      this.field_149535_c = var1.readByte() != 0;
   }

   public void func_148840_b(PacketBuffer var1) throws IOException {
      var1.writeByte(this.field_149536_a);
      var1.writeShort(this.field_149534_b);
      var1.writeByte(this.field_149535_c ? 1 : 0);
   }

   public int func_149532_c() {
      return this.field_149536_a;
   }

   public short func_149533_d() {
      return this.field_149534_b;
   }
}
