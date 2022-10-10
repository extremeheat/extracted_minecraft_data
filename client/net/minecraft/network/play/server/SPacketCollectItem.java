package net.minecraft.network.play.server;

import java.io.IOException;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayClient;

public class SPacketCollectItem implements Packet<INetHandlerPlayClient> {
   private int field_149357_a;
   private int field_149356_b;
   private int field_191209_c;

   public SPacketCollectItem() {
      super();
   }

   public SPacketCollectItem(int var1, int var2, int var3) {
      super();
      this.field_149357_a = var1;
      this.field_149356_b = var2;
      this.field_191209_c = var3;
   }

   public void func_148837_a(PacketBuffer var1) throws IOException {
      this.field_149357_a = var1.func_150792_a();
      this.field_149356_b = var1.func_150792_a();
      this.field_191209_c = var1.func_150792_a();
   }

   public void func_148840_b(PacketBuffer var1) throws IOException {
      var1.func_150787_b(this.field_149357_a);
      var1.func_150787_b(this.field_149356_b);
      var1.func_150787_b(this.field_191209_c);
   }

   public void func_148833_a(INetHandlerPlayClient var1) {
      var1.func_147246_a(this);
   }

   public int func_149354_c() {
      return this.field_149357_a;
   }

   public int func_149353_d() {
      return this.field_149356_b;
   }

   public int func_191208_c() {
      return this.field_191209_c;
   }
}
