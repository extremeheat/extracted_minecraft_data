package net.minecraft.block;

public class BlockEventData {
   private int field_151348_a;
   private int field_151346_b;
   private int field_151347_c;
   private Block field_151344_d;
   private int field_151345_e;
   private int field_151343_f;

   public BlockEventData(int var1, int var2, int var3, Block var4, int var5, int var6) {
      super();
      this.field_151348_a = var1;
      this.field_151346_b = var2;
      this.field_151347_c = var3;
      this.field_151345_e = var5;
      this.field_151343_f = var6;
      this.field_151344_d = var4;
   }

   public int func_151340_a() {
      return this.field_151348_a;
   }

   public int func_151342_b() {
      return this.field_151346_b;
   }

   public int func_151341_c() {
      return this.field_151347_c;
   }

   public int func_151339_d() {
      return this.field_151345_e;
   }

   public int func_151338_e() {
      return this.field_151343_f;
   }

   public Block func_151337_f() {
      return this.field_151344_d;
   }

   @Override
   public boolean equals(Object var1) {
      if (!(var1 instanceof BlockEventData)) {
         return false;
      } else {
         BlockEventData var2 = (BlockEventData)var1;
         return this.field_151348_a == var2.field_151348_a
            && this.field_151346_b == var2.field_151346_b
            && this.field_151347_c == var2.field_151347_c
            && this.field_151345_e == var2.field_151345_e
            && this.field_151343_f == var2.field_151343_f
            && this.field_151344_d == var2.field_151344_d;
      }
   }

   @Override
   public String toString() {
      return "TE("
         + this.field_151348_a
         + ","
         + this.field_151346_b
         + ","
         + this.field_151347_c
         + "),"
         + this.field_151345_e
         + ","
         + this.field_151343_f
         + ","
         + this.field_151344_d;
   }
}
