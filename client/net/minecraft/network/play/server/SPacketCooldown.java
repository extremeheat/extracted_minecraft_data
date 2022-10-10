package net.minecraft.network.play.server;

import java.io.IOException;
import net.minecraft.item.Item;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayClient;

public class SPacketCooldown implements Packet<INetHandlerPlayClient> {
   private Item field_186923_a;
   private int field_186924_b;

   public SPacketCooldown() {
      super();
   }

   public SPacketCooldown(Item var1, int var2) {
      super();
      this.field_186923_a = var1;
      this.field_186924_b = var2;
   }

   public void func_148837_a(PacketBuffer var1) throws IOException {
      this.field_186923_a = Item.func_150899_d(var1.func_150792_a());
      this.field_186924_b = var1.func_150792_a();
   }

   public void func_148840_b(PacketBuffer var1) throws IOException {
      var1.func_150787_b(Item.func_150891_b(this.field_186923_a));
      var1.func_150787_b(this.field_186924_b);
   }

   public void func_148833_a(INetHandlerPlayClient var1) {
      var1.func_184324_a(this);
   }

   public Item func_186920_a() {
      return this.field_186923_a;
   }

   public int func_186922_b() {
      return this.field_186924_b;
   }
}
