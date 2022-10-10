package net.minecraft.entity.player;

import net.minecraft.nbt.NBTTagCompound;

public class PlayerCapabilities {
   public boolean field_75102_a;
   public boolean field_75100_b;
   public boolean field_75101_c;
   public boolean field_75098_d;
   public boolean field_75099_e = true;
   private double field_75096_f = 0.05000000074505806D;
   private float field_75097_g = 0.1F;

   public PlayerCapabilities() {
      super();
   }

   public void func_75091_a(NBTTagCompound var1) {
      NBTTagCompound var2 = new NBTTagCompound();
      var2.func_74757_a("invulnerable", this.field_75102_a);
      var2.func_74757_a("flying", this.field_75100_b);
      var2.func_74757_a("mayfly", this.field_75101_c);
      var2.func_74757_a("instabuild", this.field_75098_d);
      var2.func_74757_a("mayBuild", this.field_75099_e);
      var2.func_74776_a("flySpeed", (float)this.field_75096_f);
      var2.func_74776_a("walkSpeed", this.field_75097_g);
      var1.func_74782_a("abilities", var2);
   }

   public void func_75095_b(NBTTagCompound var1) {
      if (var1.func_150297_b("abilities", 10)) {
         NBTTagCompound var2 = var1.func_74775_l("abilities");
         this.field_75102_a = var2.func_74767_n("invulnerable");
         this.field_75100_b = var2.func_74767_n("flying");
         this.field_75101_c = var2.func_74767_n("mayfly");
         this.field_75098_d = var2.func_74767_n("instabuild");
         if (var2.func_150297_b("flySpeed", 99)) {
            this.field_75096_f = (double)var2.func_74760_g("flySpeed");
            this.field_75097_g = var2.func_74760_g("walkSpeed");
         }

         if (var2.func_150297_b("mayBuild", 1)) {
            this.field_75099_e = var2.func_74767_n("mayBuild");
         }
      }

   }

   public float func_75093_a() {
      return (float)this.field_75096_f;
   }

   public void func_195931_a(double var1) {
      this.field_75096_f = var1;
   }

   public float func_75094_b() {
      return this.field_75097_g;
   }

   public void func_82877_b(float var1) {
      this.field_75097_g = var1;
   }
}
