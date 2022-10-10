package net.minecraft.network.play.client;

import java.io.IOException;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayServer;

public class CPacketNBTQueryEntity implements Packet<INetHandlerPlayServer> {
   private int field_211722_a;
   private int field_211723_b;

   public CPacketNBTQueryEntity() {
      super();
   }

   public CPacketNBTQueryEntity(int var1, int var2) {
      super();
      this.field_211722_a = var1;
      this.field_211723_b = var2;
   }

   public void func_148837_a(PacketBuffer var1) throws IOException {
      this.field_211722_a = var1.func_150792_a();
      this.field_211723_b = var1.func_150792_a();
   }

   public void func_148840_b(PacketBuffer var1) throws IOException {
      var1.func_150787_b(this.field_211722_a);
      var1.func_150787_b(this.field_211723_b);
   }

   public void func_148833_a(INetHandlerPlayServer var1) {
      var1.func_211526_a(this);
   }

   public int func_211721_b() {
      return this.field_211722_a;
   }

   public int func_211720_c() {
      return this.field_211723_b;
   }
}
