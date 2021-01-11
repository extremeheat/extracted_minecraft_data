package net.minecraft.network.play.server;

import java.io.IOException;
import net.minecraft.entity.player.PlayerCapabilities;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayClient;

public class S39PacketPlayerAbilities implements Packet<INetHandlerPlayClient> {
   private boolean field_149119_a;
   private boolean field_149117_b;
   private boolean field_149118_c;
   private boolean field_149115_d;
   private float field_149116_e;
   private float field_149114_f;

   public S39PacketPlayerAbilities() {
      super();
   }

   public S39PacketPlayerAbilities(PlayerCapabilities var1) {
      super();
      this.func_149108_a(var1.field_75102_a);
      this.func_149102_b(var1.field_75100_b);
      this.func_149109_c(var1.field_75101_c);
      this.func_149111_d(var1.field_75098_d);
      this.func_149104_a(var1.func_75093_a());
      this.func_149110_b(var1.func_75094_b());
   }

   public void func_148837_a(PacketBuffer var1) throws IOException {
      byte var2 = var1.readByte();
      this.func_149108_a((var2 & 1) > 0);
      this.func_149102_b((var2 & 2) > 0);
      this.func_149109_c((var2 & 4) > 0);
      this.func_149111_d((var2 & 8) > 0);
      this.func_149104_a(var1.readFloat());
      this.func_149110_b(var1.readFloat());
   }

   public void func_148840_b(PacketBuffer var1) throws IOException {
      byte var2 = 0;
      if (this.func_149112_c()) {
         var2 = (byte)(var2 | 1);
      }

      if (this.func_149106_d()) {
         var2 = (byte)(var2 | 2);
      }

      if (this.func_149105_e()) {
         var2 = (byte)(var2 | 4);
      }

      if (this.func_149103_f()) {
         var2 = (byte)(var2 | 8);
      }

      var1.writeByte(var2);
      var1.writeFloat(this.field_149116_e);
      var1.writeFloat(this.field_149114_f);
   }

   public void func_148833_a(INetHandlerPlayClient var1) {
      var1.func_147270_a(this);
   }

   public boolean func_149112_c() {
      return this.field_149119_a;
   }

   public void func_149108_a(boolean var1) {
      this.field_149119_a = var1;
   }

   public boolean func_149106_d() {
      return this.field_149117_b;
   }

   public void func_149102_b(boolean var1) {
      this.field_149117_b = var1;
   }

   public boolean func_149105_e() {
      return this.field_149118_c;
   }

   public void func_149109_c(boolean var1) {
      this.field_149118_c = var1;
   }

   public boolean func_149103_f() {
      return this.field_149115_d;
   }

   public void func_149111_d(boolean var1) {
      this.field_149115_d = var1;
   }

   public float func_149101_g() {
      return this.field_149116_e;
   }

   public void func_149104_a(float var1) {
      this.field_149116_e = var1;
   }

   public float func_149107_h() {
      return this.field_149114_f;
   }

   public void func_149110_b(float var1) {
      this.field_149114_f = var1;
   }
}
