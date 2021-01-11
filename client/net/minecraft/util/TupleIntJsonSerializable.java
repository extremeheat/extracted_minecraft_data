package net.minecraft.util;

public class TupleIntJsonSerializable {
   private int field_151192_a;
   private IJsonSerializable field_151191_b;

   public TupleIntJsonSerializable() {
      super();
   }

   public int func_151189_a() {
      return this.field_151192_a;
   }

   public void func_151188_a(int var1) {
      this.field_151192_a = var1;
   }

   public <T extends IJsonSerializable> T func_151187_b() {
      return this.field_151191_b;
   }

   public void func_151190_a(IJsonSerializable var1) {
      this.field_151191_b = var1;
   }
}
