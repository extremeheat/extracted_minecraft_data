package net.minecraft.network.play.client;

import java.io.IOException;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayServer;

public class C11PacketEnchantItem implements Packet<INetHandlerPlayServer> {
   private int field_149541_a;
   private int field_149540_b;

   public C11PacketEnchantItem() {
      super();
   }

   public C11PacketEnchantItem(int var1, int var2) {
      super();
      this.field_149541_a = var1;
      this.field_149540_b = var2;
   }

   public void func_148833_a(INetHandlerPlayServer var1) {
      var1.func_147338_a(this);
   }

   public void func_148837_a(PacketBuffer var1) throws IOException {
      this.field_149541_a = var1.readByte();
      this.field_149540_b = var1.readByte();
   }

   public void func_148840_b(PacketBuffer var1) throws IOException {
      var1.writeByte(this.field_149541_a);
      var1.writeByte(this.field_149540_b);
   }

   public int func_149539_c() {
      return this.field_149541_a;
   }

   public int func_149537_d() {
      return this.field_149540_b;
   }
}
