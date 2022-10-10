package net.minecraft.potion;

import com.google.common.collect.ComparisonChain;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.nbt.NBTTagCompound;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class PotionEffect implements Comparable<PotionEffect> {
   private static final Logger field_180155_a = LogManager.getLogger();
   private final Potion field_188420_b;
   private int field_76460_b;
   private int field_76461_c;
   private boolean field_82723_d;
   private boolean field_82724_e;
   private boolean field_100013_f;
   private boolean field_188421_h;
   private boolean field_205349_i;

   public PotionEffect(Potion var1) {
      this(var1, 0, 0);
   }

   public PotionEffect(Potion var1, int var2) {
      this(var1, var2, 0);
   }

   public PotionEffect(Potion var1, int var2, int var3) {
      this(var1, var2, var3, false, true);
   }

   public PotionEffect(Potion var1, int var2, int var3, boolean var4, boolean var5) {
      this(var1, var2, var3, var4, var5, var5);
   }

   public PotionEffect(Potion var1, int var2, int var3, boolean var4, boolean var5, boolean var6) {
      super();
      this.field_188420_b = var1;
      this.field_76460_b = var2;
      this.field_76461_c = var3;
      this.field_82724_e = var4;
      this.field_188421_h = var5;
      this.field_205349_i = var6;
   }

   public PotionEffect(PotionEffect var1) {
      super();
      this.field_188420_b = var1.field_188420_b;
      this.field_76460_b = var1.field_76460_b;
      this.field_76461_c = var1.field_76461_c;
      this.field_82724_e = var1.field_82724_e;
      this.field_188421_h = var1.field_188421_h;
      this.field_205349_i = var1.field_205349_i;
   }

   public boolean func_199308_a(PotionEffect var1) {
      if (this.field_188420_b != var1.field_188420_b) {
         field_180155_a.warn("This method should only be called for matching effects!");
      }

      boolean var2 = false;
      if (var1.field_76461_c > this.field_76461_c) {
         this.field_76461_c = var1.field_76461_c;
         this.field_76460_b = var1.field_76460_b;
         var2 = true;
      } else if (var1.field_76461_c == this.field_76461_c && this.field_76460_b < var1.field_76460_b) {
         this.field_76460_b = var1.field_76460_b;
         var2 = true;
      }

      if (!var1.field_82724_e && this.field_82724_e || var2) {
         this.field_82724_e = var1.field_82724_e;
         var2 = true;
      }

      if (var1.field_188421_h != this.field_188421_h) {
         this.field_188421_h = var1.field_188421_h;
         var2 = true;
      }

      if (var1.field_205349_i != this.field_205349_i) {
         this.field_205349_i = var1.field_205349_i;
         var2 = true;
      }

      return var2;
   }

   public Potion func_188419_a() {
      return this.field_188420_b;
   }

   public int func_76459_b() {
      return this.field_76460_b;
   }

   public int func_76458_c() {
      return this.field_76461_c;
   }

   public boolean func_82720_e() {
      return this.field_82724_e;
   }

   public boolean func_188418_e() {
      return this.field_188421_h;
   }

   public boolean func_205348_f() {
      return this.field_205349_i;
   }

   public boolean func_76455_a(EntityLivingBase var1) {
      if (this.field_76460_b > 0) {
         if (this.field_188420_b.func_76397_a(this.field_76460_b, this.field_76461_c)) {
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
         this.field_188420_b.func_76394_a(var1, this.field_76461_c);
      }

   }

   public String func_76453_d() {
      return this.field_188420_b.func_76393_a();
   }

   public String toString() {
      String var1;
      if (this.field_76461_c > 0) {
         var1 = this.func_76453_d() + " x " + (this.field_76461_c + 1) + ", Duration: " + this.field_76460_b;
      } else {
         var1 = this.func_76453_d() + ", Duration: " + this.field_76460_b;
      }

      if (this.field_82723_d) {
         var1 = var1 + ", Splash: true";
      }

      if (!this.field_188421_h) {
         var1 = var1 + ", Particles: false";
      }

      if (!this.field_205349_i) {
         var1 = var1 + ", Show Icon: false";
      }

      return var1;
   }

   public boolean equals(Object var1) {
      if (this == var1) {
         return true;
      } else if (!(var1 instanceof PotionEffect)) {
         return false;
      } else {
         PotionEffect var2 = (PotionEffect)var1;
         return this.field_76460_b == var2.field_76460_b && this.field_76461_c == var2.field_76461_c && this.field_82723_d == var2.field_82723_d && this.field_82724_e == var2.field_82724_e && this.field_188420_b.equals(var2.field_188420_b);
      }
   }

   public int hashCode() {
      int var1 = this.field_188420_b.hashCode();
      var1 = 31 * var1 + this.field_76460_b;
      var1 = 31 * var1 + this.field_76461_c;
      var1 = 31 * var1 + (this.field_82723_d ? 1 : 0);
      var1 = 31 * var1 + (this.field_82724_e ? 1 : 0);
      return var1;
   }

   public NBTTagCompound func_82719_a(NBTTagCompound var1) {
      var1.func_74774_a("Id", (byte)Potion.func_188409_a(this.func_188419_a()));
      var1.func_74774_a("Amplifier", (byte)this.func_76458_c());
      var1.func_74768_a("Duration", this.func_76459_b());
      var1.func_74757_a("Ambient", this.func_82720_e());
      var1.func_74757_a("ShowParticles", this.func_188418_e());
      var1.func_74757_a("ShowIcon", this.func_205348_f());
      return var1;
   }

   public static PotionEffect func_82722_b(NBTTagCompound var0) {
      byte var1 = var0.func_74771_c("Id");
      Potion var2 = Potion.func_188412_a(var1);
      if (var2 == null) {
         return null;
      } else {
         byte var3 = var0.func_74771_c("Amplifier");
         int var4 = var0.func_74762_e("Duration");
         boolean var5 = var0.func_74767_n("Ambient");
         boolean var6 = true;
         if (var0.func_150297_b("ShowParticles", 1)) {
            var6 = var0.func_74767_n("ShowParticles");
         }

         boolean var7 = var6;
         if (var0.func_150297_b("ShowIcon", 1)) {
            var7 = var0.func_74767_n("ShowIcon");
         }

         return new PotionEffect(var2, var4, var3 < 0 ? 0 : var3, var5, var6, var7);
      }
   }

   public void func_100012_b(boolean var1) {
      this.field_100013_f = var1;
   }

   public boolean func_100011_g() {
      return this.field_100013_f;
   }

   public int compareTo(PotionEffect var1) {
      boolean var2 = true;
      return (this.func_76459_b() <= 32147 || var1.func_76459_b() <= 32147) && (!this.func_82720_e() || !var1.func_82720_e()) ? ComparisonChain.start().compare(this.func_82720_e(), var1.func_82720_e()).compare(this.func_76459_b(), var1.func_76459_b()).compare(this.func_188419_a().func_76401_j(), var1.func_188419_a().func_76401_j()).result() : ComparisonChain.start().compare(this.func_82720_e(), var1.func_82720_e()).compare(this.func_188419_a().func_76401_j(), var1.func_188419_a().func_76401_j()).result();
   }

   // $FF: synthetic method
   public int compareTo(Object var1) {
      return this.compareTo((PotionEffect)var1);
   }
}
