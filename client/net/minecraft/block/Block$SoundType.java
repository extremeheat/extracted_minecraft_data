package net.minecraft.block;

public class Block$SoundType {
   public final String field_150501_a;
   public final float field_150499_b;
   public final float field_150500_c;

   public Block$SoundType(String var1, float var2, float var3) {
      super();
      this.field_150501_a = var1;
      this.field_150499_b = var2;
      this.field_150500_c = var3;
   }

   public float func_150497_c() {
      return this.field_150499_b;
   }

   public float func_150494_d() {
      return this.field_150500_c;
   }

   public String func_150495_a() {
      return "dig." + this.field_150501_a;
   }

   public String func_150498_e() {
      return "step." + this.field_150501_a;
   }

   public String func_150496_b() {
      return this.func_150495_a();
   }
}
