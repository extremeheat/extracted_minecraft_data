package net.minecraft.advancements;

import com.google.common.collect.Lists;
import java.util.Iterator;
import java.util.List;
import javax.annotation.Nullable;

public class AdvancementTreeNode {
   private final Advancement field_192328_a;
   private final AdvancementTreeNode field_192329_b;
   private final AdvancementTreeNode field_192330_c;
   private final int field_192331_d;
   private final List<AdvancementTreeNode> field_192332_e = Lists.newArrayList();
   private AdvancementTreeNode field_192333_f;
   private AdvancementTreeNode field_192334_g;
   private int field_192335_h;
   private float field_192336_i;
   private float field_192337_j;
   private float field_192338_k;
   private float field_192339_l;

   public AdvancementTreeNode(Advancement var1, @Nullable AdvancementTreeNode var2, @Nullable AdvancementTreeNode var3, int var4, int var5) {
      super();
      if (var1.func_192068_c() == null) {
         throw new IllegalArgumentException("Can't position an invisible advancement!");
      } else {
         this.field_192328_a = var1;
         this.field_192329_b = var2;
         this.field_192330_c = var3;
         this.field_192331_d = var4;
         this.field_192333_f = this;
         this.field_192335_h = var5;
         this.field_192336_i = -1.0F;
         AdvancementTreeNode var6 = null;

         Advancement var8;
         for(Iterator var7 = var1.func_192069_e().iterator(); var7.hasNext(); var6 = this.func_192322_a(var8, var6)) {
            var8 = (Advancement)var7.next();
         }

      }
   }

   @Nullable
   private AdvancementTreeNode func_192322_a(Advancement var1, @Nullable AdvancementTreeNode var2) {
      Advancement var4;
      if (var1.func_192068_c() != null) {
         var2 = new AdvancementTreeNode(var1, this, var2, this.field_192332_e.size() + 1, this.field_192335_h + 1);
         this.field_192332_e.add(var2);
      } else {
         for(Iterator var3 = var1.func_192069_e().iterator(); var3.hasNext(); var2 = this.func_192322_a(var4, var2)) {
            var4 = (Advancement)var3.next();
         }
      }

      return var2;
   }

   private void func_192320_a() {
      if (this.field_192332_e.isEmpty()) {
         if (this.field_192330_c != null) {
            this.field_192336_i = this.field_192330_c.field_192336_i + 1.0F;
         } else {
            this.field_192336_i = 0.0F;
         }

      } else {
         AdvancementTreeNode var1 = null;

         AdvancementTreeNode var3;
         for(Iterator var2 = this.field_192332_e.iterator(); var2.hasNext(); var1 = var3.func_192324_a(var1 == null ? var3 : var1)) {
            var3 = (AdvancementTreeNode)var2.next();
            var3.func_192320_a();
         }

         this.func_192325_b();
         float var4 = (((AdvancementTreeNode)this.field_192332_e.get(0)).field_192336_i + ((AdvancementTreeNode)this.field_192332_e.get(this.field_192332_e.size() - 1)).field_192336_i) / 2.0F;
         if (this.field_192330_c != null) {
            this.field_192336_i = this.field_192330_c.field_192336_i + 1.0F;
            this.field_192337_j = this.field_192336_i - var4;
         } else {
            this.field_192336_i = var4;
         }

      }
   }

   private float func_192319_a(float var1, int var2, float var3) {
      this.field_192336_i += var1;
      this.field_192335_h = var2;
      if (this.field_192336_i < var3) {
         var3 = this.field_192336_i;
      }

      AdvancementTreeNode var5;
      for(Iterator var4 = this.field_192332_e.iterator(); var4.hasNext(); var3 = var5.func_192319_a(var1 + this.field_192337_j, var2 + 1, var3)) {
         var5 = (AdvancementTreeNode)var4.next();
      }

      return var3;
   }

   private void func_192318_a(float var1) {
      this.field_192336_i += var1;
      Iterator var2 = this.field_192332_e.iterator();

      while(var2.hasNext()) {
         AdvancementTreeNode var3 = (AdvancementTreeNode)var2.next();
         var3.func_192318_a(var1);
      }

   }

   private void func_192325_b() {
      float var1 = 0.0F;
      float var2 = 0.0F;

      for(int var3 = this.field_192332_e.size() - 1; var3 >= 0; --var3) {
         AdvancementTreeNode var4 = (AdvancementTreeNode)this.field_192332_e.get(var3);
         var4.field_192336_i += var1;
         var4.field_192337_j += var1;
         var2 += var4.field_192338_k;
         var1 += var4.field_192339_l + var2;
      }

   }

   @Nullable
   private AdvancementTreeNode func_192321_c() {
      if (this.field_192334_g != null) {
         return this.field_192334_g;
      } else {
         return !this.field_192332_e.isEmpty() ? (AdvancementTreeNode)this.field_192332_e.get(0) : null;
      }
   }

   @Nullable
   private AdvancementTreeNode func_192317_d() {
      if (this.field_192334_g != null) {
         return this.field_192334_g;
      } else {
         return !this.field_192332_e.isEmpty() ? (AdvancementTreeNode)this.field_192332_e.get(this.field_192332_e.size() - 1) : null;
      }
   }

   private AdvancementTreeNode func_192324_a(AdvancementTreeNode var1) {
      if (this.field_192330_c == null) {
         return var1;
      } else {
         AdvancementTreeNode var2 = this;
         AdvancementTreeNode var3 = this;
         AdvancementTreeNode var4 = this.field_192330_c;
         AdvancementTreeNode var5 = (AdvancementTreeNode)this.field_192329_b.field_192332_e.get(0);
         float var6 = this.field_192337_j;
         float var7 = this.field_192337_j;
         float var8 = var4.field_192337_j;

         float var9;
         for(var9 = var5.field_192337_j; var4.func_192317_d() != null && var2.func_192321_c() != null; var7 += var3.field_192337_j) {
            var4 = var4.func_192317_d();
            var2 = var2.func_192321_c();
            var5 = var5.func_192321_c();
            var3 = var3.func_192317_d();
            var3.field_192333_f = this;
            float var10 = var4.field_192336_i + var8 - (var2.field_192336_i + var6) + 1.0F;
            if (var10 > 0.0F) {
               var4.func_192326_a(this, var1).func_192316_a(this, var10);
               var6 += var10;
               var7 += var10;
            }

            var8 += var4.field_192337_j;
            var6 += var2.field_192337_j;
            var9 += var5.field_192337_j;
         }

         if (var4.func_192317_d() != null && var3.func_192317_d() == null) {
            var3.field_192334_g = var4.func_192317_d();
            var3.field_192337_j += var8 - var7;
         } else {
            if (var2.func_192321_c() != null && var5.func_192321_c() == null) {
               var5.field_192334_g = var2.func_192321_c();
               var5.field_192337_j += var6 - var9;
            }

            var1 = this;
         }

         return var1;
      }
   }

   private void func_192316_a(AdvancementTreeNode var1, float var2) {
      float var3 = (float)(var1.field_192331_d - this.field_192331_d);
      if (var3 != 0.0F) {
         var1.field_192338_k -= var2 / var3;
         this.field_192338_k += var2 / var3;
      }

      var1.field_192339_l += var2;
      var1.field_192336_i += var2;
      var1.field_192337_j += var2;
   }

   private AdvancementTreeNode func_192326_a(AdvancementTreeNode var1, AdvancementTreeNode var2) {
      return this.field_192333_f != null && var1.field_192329_b.field_192332_e.contains(this.field_192333_f) ? this.field_192333_f : var2;
   }

   private void func_192327_e() {
      if (this.field_192328_a.func_192068_c() != null) {
         this.field_192328_a.func_192068_c().func_192292_a((float)this.field_192335_h, this.field_192336_i);
      }

      if (!this.field_192332_e.isEmpty()) {
         Iterator var1 = this.field_192332_e.iterator();

         while(var1.hasNext()) {
            AdvancementTreeNode var2 = (AdvancementTreeNode)var1.next();
            var2.func_192327_e();
         }
      }

   }

   public static void func_192323_a(Advancement var0) {
      if (var0.func_192068_c() == null) {
         throw new IllegalArgumentException("Can't position children of an invisible root!");
      } else {
         AdvancementTreeNode var1 = new AdvancementTreeNode(var0, (AdvancementTreeNode)null, (AdvancementTreeNode)null, 1, 0);
         var1.func_192320_a();
         float var2 = var1.func_192319_a(0.0F, 0, var1.field_192336_i);
         if (var2 < 0.0F) {
            var1.func_192318_a(-var2);
         }

         var1.func_192327_e();
      }
   }
}
