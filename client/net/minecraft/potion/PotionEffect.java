package net.minecraft.potion;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.nbt.NBTTagCompound;

public class PotionEffect {
   private int field_76462_a;
   private int field_76460_b;
   private int field_76461_c;
   private boolean field_82723_d;
   private boolean field_82724_e;
   private boolean field_100013_f;

   public PotionEffect(int var1, int var2) {
      this(var1, var2, 0);
   }

   public PotionEffect(int var1, int var2, int var3) {
      this(var1, var2, var3, false);
   }

   public PotionEffect(int var1, int var2, int var3, boolean var4) {
      super();
      this.field_76462_a = var1;
      this.field_76460_b = var2;
      this.field_76461_c = var3;
      this.field_82724_e = var4;
   }

   public PotionEffect(PotionEffect var1) {
      super();
      this.field_76462_a = var1.field_76462_a;
      this.field_76460_b = var1.field_76460_b;
      this.field_76461_c = var1.field_76461_c;
   }

   public void func_76452_a(PotionEffect var1) {
      if (this.field_76462_a != var1.field_76462_a) {
         System.err.println("This method should only be called for matching effects!");
      }

      if (var1.field_76461_c > this.field_76461_c) {
         this.field_76461_c = var1.field_76461_c;
         this.field_76460_b = var1.field_76460_b;
      } else if (var1.field_76461_c == this.field_76461_c && this.field_76460_b < var1.field_76460_b) {
         this.field_76460_b = var1.field_76460_b;
      } else if (!var1.field_82724_e && this.field_82724_e) {
         this.field_82724_e = var1.field_82724_e;
      }
   }

   public int func_76456_a() {
      return this.field_76462_a;
   }

   public int func_76459_b() {
      return this.field_76460_b;
   }

   public int func_76458_c() {
      return this.field_76461_c;
   }

   public void func_82721_a(boolean var1) {
      this.field_82723_d = var1;
   }

   public boolean func_82720_e() {
      return this.field_82724_e;
   }

   public boolean func_76455_a(EntityLivingBase var1) {
      if (this.field_76460_b > 0) {
         if (Potion.field_76425_a[this.field_76462_a].func_76397_a(this.field_76460_b, this.field_76461_c)) {
            this.func_76457_b(var1);
         }

         this.func_76454_e();
      }

      return this.field_76460_b > 0;
   }

   private int func_76454_e() {
      return --this.field_76460_b;
   }

   public void func_76457_b(EntityLivingBase var1) {
      if (this.field_76460_b > 0) {
         Potion.field_76425_a[this.field_76462_a].func_76394_a(var1, this.field_76461_c);
      }
   }

   public String func_76453_d() {
      return Potion.field_76425_a[this.field_76462_a].func_76393_a();
   }

   @Override
   public int hashCode() {
      return this.field_76462_a;
   }

   @Override
   public String toString() {
      String var1 = "";
      if (this.func_76458_c() > 0) {
         var1 = this.func_76453_d() + " x " + (this.func_76458_c() + 1) + ", Duration: " + this.func_76459_b();
      } else {
         var1 = this.func_76453_d() + ", Duration: " + this.func_76459_b();
      }

      if (this.field_82723_d) {
         var1 = var1 + ", Splash: true";
      }

      return Potion.field_76425_a[this.field_76462_a].func_76395_i() ? "(" + var1 + ")" : var1;
   }

   @Override
   public boolean equals(Object var1) {
      if (!(var1 instanceof PotionEffect)) {
         return false;
      } else {
         PotionEffect var2 = (PotionEffect)var1;
         return this.field_76462_a == var2.field_76462_a
            && this.field_76461_c == var2.field_76461_c
            && this.field_76460_b == var2.field_76460_b
            && this.field_82723_d == var2.field_82723_d
            && this.field_82724_e == var2.field_82724_e;
      }
   }

   public NBTTagCompound func_82719_a(NBTTagCompound var1) {
      var1.func_74774_a("Id", (byte)this.func_76456_a());
      var1.func_74774_a("Amplifier", (byte)this.func_76458_c());
      var1.func_74768_a("Duration", this.func_76459_b());
      var1.func_74757_a("Ambient", this.func_82720_e());
      return var1;
   }

   public static PotionEffect func_82722_b(NBTTagCompound var0) {
      byte var1 = var0.func_74771_c("Id");
      if (var1 >= 0 && var1 < Potion.field_76425_a.length && Potion.field_76425_a[var1] != null) {
         byte var2 = var0.func_74771_c("Amplifier");
         int var3 = var0.func_74762_e("Duration");
         boolean var4 = var0.func_74767_n("Ambient");
         return new PotionEffect(var1, var3, var2, var4);
      } else {
         return null;
      }
   }

   public void func_100012_b(boolean var1) {
      this.field_100013_f = var1;
   }

   public boolean func_100011_g() {
      return this.field_100013_f;
   }
}
