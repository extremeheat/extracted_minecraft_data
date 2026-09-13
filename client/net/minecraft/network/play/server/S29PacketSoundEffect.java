package net.minecraft.network.play.server;

import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayClient;
import org.apache.commons.lang3.Validate;

public class S29PacketSoundEffect extends Packet {
   private String field_149219_a;
   private int field_149217_b;
   private int field_149218_c = 2147483647;
   private int field_149215_d;
   private float field_149216_e;
   private int field_149214_f;

   public S29PacketSoundEffect() {
      super();
   }

   public S29PacketSoundEffect(String var1, double var2, double var4, double var6, float var8, float var9) {
      super();
      Validate.notNull(var1, "name", new Object[0]);
      this.field_149219_a = var1;
      this.field_149217_b = (int)(var2 * 8.0);
      this.field_149218_c = (int)(var4 * 8.0);
      this.field_149215_d = (int)(var6 * 8.0);
      this.field_149216_e = var8;
      this.field_149214_f = (int)(var9 * 63.0F);
      if (this.field_149214_f < 0) {
         this.field_149214_f = 0;
      }

      if (this.field_149214_f > 255) {
         this.field_149214_f = 255;
      }
   }

   @Override
   public void func_148837_a(PacketBuffer var1) {
      this.field_149219_a = var1.func_150789_c(256);
      this.field_149217_b = var1.readInt();
      this.field_149218_c = var1.readInt();
      this.field_149215_d = var1.readInt();
      this.field_149216_e = var1.readFloat();
      this.field_149214_f = var1.readUnsignedByte();
   }

   @Override
   public void func_148840_b(PacketBuffer var1) {
      var1.func_150785_a(this.field_149219_a);
      var1.writeInt(this.field_149217_b);
      var1.writeInt(this.field_149218_c);
      var1.writeInt(this.field_149215_d);
      var1.writeFloat(this.field_149216_e);
      var1.writeByte(this.field_149214_f);
   }

   public String func_149212_c() {
      return this.field_149219_a;
   }

   public double func_149207_d() {
      return (double)((float)this.field_149217_b / 8.0F);
   }

   public double func_149211_e() {
      return (double)((float)this.field_149218_c / 8.0F);
   }

   public double func_149210_f() {
      return (double)((float)this.field_149215_d / 8.0F);
   }

   public float func_149208_g() {
      return this.field_149216_e;
   }

   public float func_149209_h() {
      return (float)this.field_149214_f / 63.0F;
   }

   public void func_148833_a(INetHandlerPlayClient var1) {
      var1.func_147255_a(this);
   }
}
