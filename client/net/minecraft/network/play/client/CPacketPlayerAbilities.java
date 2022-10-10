package net.minecraft.network.play.client;

import java.io.IOException;
import net.minecraft.entity.player.PlayerCapabilities;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayServer;

public class CPacketPlayerAbilities implements Packet<INetHandlerPlayServer> {
   private boolean field_149500_a;
   private boolean field_149498_b;
   private boolean field_149499_c;
   private boolean field_149496_d;
   private float field_149497_e;
   private float field_149495_f;

   public CPacketPlayerAbilities() {
      super();
   }

   public CPacketPlayerAbilities(PlayerCapabilities var1) {
      super();
      this.func_149490_a(var1.field_75102_a);
      this.func_149483_b(var1.field_75100_b);
      this.func_149491_c(var1.field_75101_c);
      this.func_149493_d(var1.field_75098_d);
      this.func_149485_a(var1.func_75093_a());
      this.func_149492_b(var1.func_75094_b());
   }

   public void func_148837_a(PacketBuffer var1) throws IOException {
      byte var2 = var1.readByte();
      this.func_149490_a((var2 & 1) > 0);
      this.func_149483_b((var2 & 2) > 0);
      this.func_149491_c((var2 & 4) > 0);
      this.func_149493_d((var2 & 8) > 0);
      this.func_149485_a(var1.readFloat());
      this.func_149492_b(var1.readFloat());
   }

   public void func_148840_b(PacketBuffer var1) throws IOException {
      byte var2 = 0;
      if (this.func_149494_c()) {
         var2 = (byte)(var2 | 1);
      }

      if (this.func_149488_d()) {
         var2 = (byte)(var2 | 2);
      }

      if (this.func_149486_e()) {
         var2 = (byte)(var2 | 4);
      }

      if (this.func_149484_f()) {
         var2 = (byte)(var2 | 8);
      }

      var1.writeByte(var2);
      var1.writeFloat(this.field_149497_e);
      var1.writeFloat(this.field_149495_f);
   }

   public void func_148833_a(INetHandlerPlayServer var1) {
      var1.func_147348_a(this);
   }

   public boolean func_149494_c() {
      return this.field_149500_a;
   }

   public void func_149490_a(boolean var1) {
      this.field_149500_a = var1;
   }

   public boolean func_149488_d() {
      return this.field_149498_b;
   }

   public void func_149483_b(boolean var1) {
      this.field_149498_b = var1;
   }

   public boolean func_149486_e() {
      return this.field_149499_c;
   }

   public void func_149491_c(boolean var1) {
      this.field_149499_c = var1;
   }

   public boolean func_149484_f() {
      return this.field_149496_d;
   }

   public void func_149493_d(boolean var1) {
      this.field_149496_d = var1;
   }

   public void func_149485_a(float var1) {
      this.field_149497_e = var1;
   }

   public void func_149492_b(float var1) {
      this.field_149495_f = var1;
   }
}
