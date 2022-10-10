package net.minecraft.entity.ai;

import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.pathfinding.Path;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;

public class EntityAIAttackMelee extends EntityAIBase {
   protected EntityCreature field_75441_b;
   protected int field_75439_d;
   private final double field_75440_e;
   private final boolean field_75437_f;
   private Path field_75438_g;
   private int field_75445_i;
   private double field_151497_i;
   private double field_151495_j;
   private double field_151496_k;
   protected final int field_188493_g = 20;

   public EntityAIAttackMelee(EntityCreature var1, double var2, boolean var4) {
      super();
      this.field_75441_b = var1;
      this.field_75440_e = var2;
      this.field_75437_f = var4;
      this.func_75248_a(3);
   }

   public boolean func_75250_a() {
      EntityLivingBase var1 = this.field_75441_b.func_70638_az();
      if (var1 == null) {
         return false;
      } else if (!var1.func_70089_S()) {
         return false;
      } else {
         this.field_75438_g = this.field_75441_b.func_70661_as().func_75494_a(var1);
         if (this.field_75438_g != null) {
            return true;
         } else {
            return this.func_179512_a(var1) >= this.field_75441_b.func_70092_e(var1.field_70165_t, var1.func_174813_aQ().field_72338_b, var1.field_70161_v);
         }
      }
   }

   public boolean func_75253_b() {
      EntityLivingBase var1 = this.field_75441_b.func_70638_az();
      if (var1 == null) {
         return false;
      } else if (!var1.func_70089_S()) {
         return false;
      } else if (!this.field_75437_f) {
         return !this.field_75441_b.func_70661_as().func_75500_f();
      } else if (!this.field_75441_b.func_180485_d(new BlockPos(var1))) {
         return false;
      } else {
         return !(var1 instanceof EntityPlayer) || !((EntityPlayer)var1).func_175149_v() && !((EntityPlayer)var1).func_184812_l_();
      }
   }

   public void func_75249_e() {
      this.field_75441_b.func_70661_as().func_75484_a(this.field_75438_g, this.field_75440_e);
      this.field_75445_i = 0;
   }

   public void func_75251_c() {
      EntityLivingBase var1 = this.field_75441_b.func_70638_az();
      if (var1 instanceof EntityPlayer && (((EntityPlayer)var1).func_175149_v() || ((EntityPlayer)var1).func_184812_l_())) {
         this.field_75441_b.func_70624_b((EntityLivingBase)null);
      }

      this.field_75441_b.func_70661_as().func_75499_g();
   }

   public void func_75246_d() {
      EntityLivingBase var1 = this.field_75441_b.func_70638_az();
      this.field_75441_b.func_70671_ap().func_75651_a(var1, 30.0F, 30.0F);
      double var2 = this.field_75441_b.func_70092_e(var1.field_70165_t, var1.func_174813_aQ().field_72338_b, var1.field_70161_v);
      --this.field_75445_i;
      if ((this.field_75437_f || this.field_75441_b.func_70635_at().func_75522_a(var1)) && this.field_75445_i <= 0 && (this.field_151497_i == 0.0D && this.field_151495_j == 0.0D && this.field_151496_k == 0.0D || var1.func_70092_e(this.field_151497_i, this.field_151495_j, this.field_151496_k) >= 1.0D || this.field_75441_b.func_70681_au().nextFloat() < 0.05F)) {
         this.field_151497_i = var1.field_70165_t;
         this.field_151495_j = var1.func_174813_aQ().field_72338_b;
         this.field_151496_k = var1.field_70161_v;
         this.field_75445_i = 4 + this.field_75441_b.func_70681_au().nextInt(7);
         if (var2 > 1024.0D) {
            this.field_75445_i += 10;
         } else if (var2 > 256.0D) {
            this.field_75445_i += 5;
         }

         if (!this.field_75441_b.func_70661_as().func_75497_a(var1, this.field_75440_e)) {
            this.field_75445_i += 15;
         }
      }

      this.field_75439_d = Math.max(this.field_75439_d - 1, 0);
      this.func_190102_a(var1, var2);
   }

   protected void func_190102_a(EntityLivingBase var1, double var2) {
      double var4 = this.func_179512_a(var1);
      if (var2 <= var4 && this.field_75439_d <= 0) {
         this.field_75439_d = 20;
         this.field_75441_b.func_184609_a(EnumHand.MAIN_HAND);
         this.field_75441_b.func_70652_k(var1);
      }

   }

   protected double func_179512_a(EntityLivingBase var1) {
      return (double)(this.field_75441_b.field_70130_N * 2.0F * this.field_75441_b.field_70130_N * 2.0F + var1.field_70130_N);
   }
}
