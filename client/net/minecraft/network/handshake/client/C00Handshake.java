package net.minecraft.network.handshake.client;

import java.io.IOException;
import net.minecraft.network.EnumConnectionState;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.handshake.INetHandlerHandshakeServer;

public class C00Handshake implements Packet<INetHandlerHandshakeServer> {
   private int field_149600_a;
   private String field_149598_b;
   private int field_149599_c;
   private EnumConnectionState field_149597_d;

   public C00Handshake() {
      super();
   }

   public C00Handshake(int var1, String var2, int var3, EnumConnectionState var4) {
      super();
      this.field_149600_a = var1;
      this.field_149598_b = var2;
      this.field_149599_c = var3;
      this.field_149597_d = var4;
   }

   public void func_148837_a(PacketBuffer var1) throws IOException {
      this.field_149600_a = var1.func_150792_a();
      this.field_149598_b = var1.func_150789_c(255);
      this.field_149599_c = var1.readUnsignedShort();
      this.field_149597_d = EnumConnectionState.func_150760_a(var1.func_150792_a());
   }

   public void func_148840_b(PacketBuffer var1) throws IOException {
      var1.func_150787_b(this.field_149600_a);
      var1.func_180714_a(this.field_149598_b);
      var1.writeShort(this.field_149599_c);
      var1.func_150787_b(this.field_149597_d.func_150759_c());
   }

   public void func_148833_a(INetHandlerHandshakeServer var1) {
      var1.func_147383_a(this);
   }

   public EnumConnectionState func_149594_c() {
      return this.field_149597_d;
   }

   public int func_149595_d() {
      return this.field_149600_a;
   }
}
