package net.minecraft.entity.item;

import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.MoverType;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Enchantments;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

public class EntityXPOrb extends Entity {
   public int field_70533_a;
   public int field_70531_b;
   public int field_70532_c;
   private int field_70529_d = 5;
   private int field_70530_e;
   private EntityPlayer field_80001_f;
   private int field_80002_g;

   public EntityXPOrb(World var1, double var2, double var4, double var6, int var8) {
      super(EntityType.field_200807_u, var1);
      this.func_70105_a(0.5F, 0.5F);
      this.func_70107_b(var2, var4, var6);
      this.field_70177_z = (float)(Math.random() * 360.0D);
      this.field_70159_w = (double)((float)(Math.random() * 0.20000000298023224D - 0.10000000149011612D) * 2.0F);
      this.field_70181_x = (double)((float)(Math.random() * 0.2D) * 2.0F);
      this.field_70179_y = (double)((float)(Math.random() * 0.20000000298023224D - 0.10000000149011612D) * 2.0F);
      this.field_70530_e = var8;
   }

   public EntityXPOrb(World var1) {
      super(EntityType.field_200807_u, var1);
      this.func_70105_a(0.25F, 0.25F);
   }

   protected boolean func_70041_e_() {
      return false;
   }

   protected void func_70088_a() {
   }

   public int func_70070_b() {
      float var1 = 0.5F;
      var1 = MathHelper.func_76131_a(var1, 0.0F, 1.0F);
      int var2 = super.func_70070_b();
      int var3 = var2 & 255;
      int var4 = var2 >> 16 & 255;
      var3 += (int)(var1 * 15.0F * 16.0F);
      if (var3 > 240) {
         var3 = 240;
      }

      return var3 | var4 << 16;
   }

   public void func_70071_h_() {
      super.func_70071_h_();
      if (this.field_70532_c > 0) {
         --this.field_70532_c;
      }

      this.field_70169_q = this.field_70165_t;
      this.field_70167_r = this.field_70163_u;
      this.field_70166_s = this.field_70161_v;
      if (this.func_208600_a(FluidTags.field_206959_a)) {
         this.func_205711_k();
      } else if (!this.func_189652_ae()) {
         this.field_70181_x -= 0.029999999329447746D;
      }

      if (this.field_70170_p.func_204610_c(new BlockPos(this)).func_206884_a(FluidTags.field_206960_b)) {
         this.field_70181_x = 0.20000000298023224D;
         this.field_70159_w = (double)((this.field_70146_Z.nextFloat() - this.field_70146_Z.nextFloat()) * 0.2F);
         this.field_70179_y = (double)((this.field_70146_Z.nextFloat() - this.field_70146_Z.nextFloat()) * 0.2F);
         this.func_184185_a(SoundEvents.field_187658_bx, 0.4F, 2.0F + this.field_70146_Z.nextFloat() * 0.4F);
      }

      this.func_145771_j(this.field_70165_t, (this.func_174813_aQ().field_72338_b + this.func_174813_aQ().field_72337_e) / 2.0D, this.field_70161_v);
      double var1 = 8.0D;
      if (this.field_80002_g < this.field_70533_a - 20 + this.func_145782_y() % 100) {
         if (this.field_80001_f == null || this.field_80001_f.func_70068_e(this) > 64.0D) {
            this.field_80001_f = this.field_70170_p.func_72890_a(this, 8.0D);
         }

         this.field_80002_g = this.field_70533_a;
      }

      if (this.field_80001_f != null && this.field_80001_f.func_175149_v()) {
         this.field_80001_f = null;
      }

      if (this.field_80001_f != null) {
         double var3 = (this.field_80001_f.field_70165_t - this.field_70165_t) / 8.0D;
         double var5 = (this.field_80001_f.field_70163_u + (double)this.field_80001_f.func_70047_e() / 2.0D - this.field_70163_u) / 8.0D;
         double var7 = (this.field_80001_f.field_70161_v - this.field_70161_v) / 8.0D;
         double var9 = Math.sqrt(var3 * var3 + var5 * var5 + var7 * var7);
         double var11 = 1.0D - var9;
         if (var11 > 0.0D) {
            var11 *= var11;
            this.field_70159_w += var3 / var9 * var11 * 0.1D;
            this.field_70181_x += var5 / var9 * var11 * 0.1D;
            this.field_70179_y += var7 / var9 * var11 * 0.1D;
         }
      }

      this.func_70091_d(MoverType.SELF, this.field_70159_w, this.field_70181_x, this.field_70179_y);
      float var13 = 0.98F;
      if (this.field_70122_E) {
         var13 = this.field_70170_p.func_180495_p(new BlockPos(MathHelper.func_76128_c(this.field_70165_t), MathHelper.func_76128_c(this.func_174813_aQ().field_72338_b) - 1, MathHelper.func_76128_c(this.field_70161_v))).func_177230_c().func_208618_m() * 0.98F;
      }

      this.field_70159_w *= (double)var13;
      this.field_70181_x *= 0.9800000190734863D;
      this.field_70179_y *= (double)var13;
      if (this.field_70122_E) {
         this.field_70181_x *= -0.8999999761581421D;
      }

      ++this.field_70533_a;
      ++this.field_70531_b;
      if (this.field_70531_b >= 6000) {
         this.func_70106_y();
      }

   }

   private void func_205711_k() {
      this.field_70181_x += 5.000000237487257E-4D;
      this.field_70181_x = Math.min(this.field_70181_x, 0.05999999865889549D);
      this.field_70159_w *= 0.9900000095367432D;
      this.field_70179_y *= 0.9900000095367432D;
   }

   protected void func_71061_d_() {
   }

   protected void func_70081_e(int var1) {
      this.func_70097_a(DamageSource.field_76372_a, (float)var1);
   }

   public boolean func_70097_a(DamageSource var1, float var2) {
      if (this.func_180431_b(var1)) {
         return false;
      } else {
         this.func_70018_K();
         this.field_70529_d = (int)((float)this.field_70529_d - var2);
         if (this.field_70529_d <= 0) {
            this.func_70106_y();
         }

         return false;
      }
   }

   public void func_70014_b(NBTTagCompound var1) {
      var1.func_74777_a("Health", (short)this.field_70529_d);
      var1.func_74777_a("Age", (short)this.field_70531_b);
      var1.func_74777_a("Value", (short)this.field_70530_e);
   }

   public void func_70037_a(NBTTagCompound var1) {
      this.field_70529_d = var1.func_74765_d("Health");
      this.field_70531_b = var1.func_74765_d("Age");
      this.field_70530_e = var1.func_74765_d("Value");
   }

   public void func_70100_b_(EntityPlayer var1) {
      if (!this.field_70170_p.field_72995_K) {
         if (this.field_70532_c == 0 && var1.field_71090_bL == 0) {
            var1.field_71090_bL = 2;
            var1.func_71001_a(this, 1);
            ItemStack var2 = EnchantmentHelper.func_92099_a(Enchantments.field_185296_A, var1);
            if (!var2.func_190926_b() && var2.func_77951_h()) {
               int var3 = Math.min(this.func_184514_c(this.field_70530_e), var2.func_77952_i());
               this.field_70530_e -= this.func_184515_b(var3);
               var2.func_196085_b(var2.func_77952_i() - var3);
            }

            if (this.field_70530_e > 0) {
               var1.func_195068_e(this.field_70530_e);
            }

            this.func_70106_y();
         }

      }
   }

   private int func_184515_b(int var1) {
      return var1 / 2;
   }

   private int func_184514_c(int var1) {
      return var1 * 2;
   }

   public int func_70526_d() {
      return this.field_70530_e;
   }

   public int func_70528_g() {
      if (this.field_70530_e >= 2477) {
         return 10;
      } else if (this.field_70530_e >= 1237) {
         return 9;
      } else if (this.field_70530_e >= 617) {
         return 8;
      } else if (this.field_70530_e >= 307) {
         return 7;
      } else if (this.field_70530_e >= 149) {
         return 6;
      } else if (this.field_70530_e >= 73) {
         return 5;
      } else if (this.field_70530_e >= 37) {
         return 4;
      } else if (this.field_70530_e >= 17) {
         return 3;
      } else if (this.field_70530_e >= 7) {
         return 2;
      } else {
         return this.field_70530_e >= 3 ? 1 : 0;
      }
   }

   public static int func_70527_a(int var0) {
      if (var0 >= 2477) {
         return 2477;
      } else if (var0 >= 1237) {
         return 1237;
      } else if (var0 >= 617) {
         return 617;
      } else if (var0 >= 307) {
         return 307;
      } else if (var0 >= 149) {
         return 149;
      } else if (var0 >= 73) {
         return 73;
      } else if (var0 >= 37) {
         return 37;
      } else if (var0 >= 17) {
         return 17;
      } else if (var0 >= 7) {
         return 7;
      } else {
         return var0 >= 3 ? 3 : 1;
      }
   }

   public boolean func_70075_an() {
      return false;
   }
}
