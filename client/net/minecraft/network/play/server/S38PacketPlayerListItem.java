package net.minecraft.network.play.server;

import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayClient;

public class S38PacketPlayerListItem extends Packet {
   private String field_149126_a;
   private boolean field_149124_b;
   private int field_149125_c;

   public S38PacketPlayerListItem() {
      super();
   }

   public S38PacketPlayerListItem(String var1, boolean var2, int var3) {
      super();
      this.field_149126_a = var1;
      this.field_149124_b = var2;
      this.field_149125_c = var3;
   }

   @Override
   public void func_148837_a(PacketBuffer var1) {
      this.field_149126_a = var1.func_150789_c(16);
      this.field_149124_b = var1.readBoolean();
      this.field_149125_c = var1.readShort();
   }

   @Override
   public void func_148840_b(PacketBuffer var1) {
      var1.func_150785_a(this.field_149126_a);
      var1.writeBoolean(this.field_149124_b);
      var1.writeShort(this.field_149125_c);
   }

   public void func_148833_a(INetHandlerPlayClient var1) {
      var1.func_147256_a(this);
   }

   public String func_149122_c() {
      return this.field_149126_a;
   }

   public boolean func_149121_d() {
      return this.field_149124_b;
   }

   public int func_149120_e() {
      return this.field_149125_c;
   }
}
