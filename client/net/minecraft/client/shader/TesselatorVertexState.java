package net.minecraft.client.shader;

public class TesselatorVertexState {
   private int[] field_147583_a;
   private int field_147581_b;
   private int field_147582_c;
   private boolean field_147579_d;
   private boolean field_147580_e;
   private boolean field_147577_f;
   private boolean field_147578_g;

   public TesselatorVertexState(int[] var1, int var2, int var3, boolean var4, boolean var5, boolean var6, boolean var7) {
      super();
      this.field_147583_a = var1;
      this.field_147581_b = var2;
      this.field_147582_c = var3;
      this.field_147579_d = var4;
      this.field_147580_e = var5;
      this.field_147577_f = var6;
      this.field_147578_g = var7;
   }

   public int[] func_147572_a() {
      return this.field_147583_a;
   }

   public int func_147576_b() {
      return this.field_147581_b;
   }

   public int func_147575_c() {
      return this.field_147582_c;
   }

   public boolean func_147573_d() {
      return this.field_147579_d;
   }

   public boolean func_147571_e() {
      return this.field_147580_e;
   }

   public boolean func_147570_f() {
      return this.field_147577_f;
   }

   public boolean func_147574_g() {
      return this.field_147578_g;
   }
}
