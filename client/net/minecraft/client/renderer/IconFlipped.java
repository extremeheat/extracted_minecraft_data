package net.minecraft.client.renderer;

import net.minecraft.util.IIcon;

public class IconFlipped implements IIcon {
   private final IIcon field_96454_a;
   private final boolean field_96452_b;
   private final boolean field_96453_c;

   public IconFlipped(IIcon var1, boolean var2, boolean var3) {
      super();
      this.field_96454_a = var1;
      this.field_96452_b = var2;
      this.field_96453_c = var3;
   }

   @Override
   public int func_94211_a() {
      return this.field_96454_a.func_94211_a();
   }

   @Override
   public int func_94216_b() {
      return this.field_96454_a.func_94216_b();
   }

   @Override
   public float func_94209_e() {
      return this.field_96452_b ? this.field_96454_a.func_94212_f() : this.field_96454_a.func_94209_e();
   }

   @Override
   public float func_94212_f() {
      return this.field_96452_b ? this.field_96454_a.func_94209_e() : this.field_96454_a.func_94212_f();
   }

   @Override
   public float func_94214_a(double var1) {
      float var3 = this.func_94212_f() - this.func_94209_e();
      return this.func_94209_e() + var3 * ((float)var1 / 16.0F);
   }

   @Override
   public float func_94206_g() {
      return this.field_96453_c ? this.field_96454_a.func_94206_g() : this.field_96454_a.func_94206_g();
   }

   @Override
   public float func_94210_h() {
      return this.field_96453_c ? this.field_96454_a.func_94206_g() : this.field_96454_a.func_94210_h();
   }

   @Override
   public float func_94207_b(double var1) {
      float var3 = this.func_94210_h() - this.func_94206_g();
      return this.func_94206_g() + var3 * ((float)var1 / 16.0F);
   }

   @Override
   public String func_94215_i() {
      return this.field_96454_a.func_94215_i();
   }
}
