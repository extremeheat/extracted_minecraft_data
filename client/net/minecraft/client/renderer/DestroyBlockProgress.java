package net.minecraft.client.renderer;

public class DestroyBlockProgress {
   private final int field_73115_a;
   private final int field_73113_b;
   private final int field_73114_c;
   private final int field_73111_d;
   private int field_73112_e;
   private int field_82745_f;

   public DestroyBlockProgress(int var1, int var2, int var3, int var4) {
      super();
      this.field_73115_a = var1;
      this.field_73113_b = var2;
      this.field_73114_c = var3;
      this.field_73111_d = var4;
   }

   public int func_73110_b() {
      return this.field_73113_b;
   }

   public int func_73109_c() {
      return this.field_73114_c;
   }

   public int func_73108_d() {
      return this.field_73111_d;
   }

   public void func_73107_a(int var1) {
      if (var1 > 10) {
         var1 = 10;
      }

      this.field_73112_e = var1;
   }

   public int func_73106_e() {
      return this.field_73112_e;
   }

   public void func_82744_b(int var1) {
      this.field_82745_f = var1;
   }

   public int func_82743_f() {
      return this.field_82745_f;
   }
}
