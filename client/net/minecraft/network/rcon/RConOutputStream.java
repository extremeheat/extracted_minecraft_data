package net.minecraft.network.rcon;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class RConOutputStream {
   private final ByteArrayOutputStream field_72674_a;
   private final DataOutputStream field_72673_b;

   public RConOutputStream(int var1) {
      super();
      this.field_72674_a = new ByteArrayOutputStream(var1);
      this.field_72673_b = new DataOutputStream(this.field_72674_a);
   }

   public void func_72670_a(byte[] var1) throws IOException {
      this.field_72673_b.write(var1, 0, var1.length);
   }

   public void func_72671_a(String var1) throws IOException {
      this.field_72673_b.writeBytes(var1);
      this.field_72673_b.write(0);
   }

   public void func_72667_a(int var1) throws IOException {
      this.field_72673_b.write(var1);
   }

   public void func_72668_a(short var1) throws IOException {
      this.field_72673_b.writeShort(Short.reverseBytes(var1));
   }

   public byte[] func_72672_a() {
      return this.field_72674_a.toByteArray();
   }

   public void func_72669_b() {
      this.field_72674_a.reset();
   }
}
