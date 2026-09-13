package net.minecraft.entity.projectile;

import net.minecraft.block.Block;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Blocks;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.Explosion;
import net.minecraft.world.World;

public class EntityWitherSkull extends EntityFireball {
   public EntityWitherSkull(World var1) {
      super(var1);
      this.func_70105_a(0.3125F, 0.3125F);
   }

   public EntityWitherSkull(World var1, EntityLivingBase var2, double var3, double var5, double var7) {
      super(var1, var2, var3, var5, var7);
      this.func_70105_a(0.3125F, 0.3125F);
   }

   @Override
   protected float func_82341_c() {
      return this.func_82342_d() ? 0.73F : super.func_82341_c();
   }

   public EntityWitherSkull(World var1, double var2, double var4, double var6, double var8, double var10, double var12) {
      super(var1, var2, var4, var6, var8, var10, var12);
      this.func_70105_a(0.3125F, 0.3125F);
   }

   @Override
   public boolean func_70027_ad() {
      return false;
   }

   @Override
   public float func_145772_a(Explosion var1, World var2, int var3, int var4, int var5, Block var6) {
      float var7 = super.func_145772_a(var1, var2, var3, var4, var5, var6);
      if (this.func_82342_d()
         && var6 != Blocks.field_150357_h
         && var6 != Blocks.field_150384_bq
         && var6 != Blocks.field_150378_br
         && var6 != Blocks.field_150483_bI) {
         var7 = Math.min(0.8F, var7);
      }

      return var7;
   }

   @Override
   protected void func_70227_a(MovingObjectPosition var1) {
      if (!this.field_70170_p.field_72995_K) {
         if (var1.field_72308_g != null) {
            if (this.field_70235_a != null) {
               if (var1.field_72308_g.func_70097_a(DamageSource.func_76358_a(this.field_70235_a), 8.0F) && !var1.field_72308_g.func_70089_S()) {
                  this.field_70235_a.func_70691_i(5.0F);
               }
            } else {
               var1.field_72308_g.func_70097_a(DamageSource.field_76376_m, 5.0F);
            }

            if (var1.field_72308_g instanceof EntityLivingBase) {
               byte var2 = 0;
               if (this.field_70170_p.field_73013_u == EnumDifficulty.NORMAL) {
                  var2 = 10;
               } else if (this.field_70170_p.field_73013_u == EnumDifficulty.HARD) {
                  var2 = 40;
               }

               if (var2 > 0) {
                  ((EntityLivingBase)var1.field_72308_g).func_70690_d(new PotionEffect(Potion.field_82731_v.field_76415_H, 20 * var2, 1));
               }
            }
         }

         this.field_70170_p
            .func_72885_a(
               this, this.field_70165_t, this.field_70163_u, this.field_70161_v, 1.0F, false, this.field_70170_p.func_82736_K().func_82766_b("mobGriefing")
            );
         this.func_70106_y();
      }
   }

   @Override
   public boolean func_70067_L() {
      return false;
   }

   @Override
   public boolean func_70097_a(DamageSource var1, float var2) {
      return false;
   }

   @Override
   protected void func_70088_a() {
      this.field_70180_af.func_75682_a(10, (byte)0);
   }

   public boolean func_82342_d() {
      return this.field_70180_af.func_75683_a(10) == 1;
   }

   public void func_82343_e(boolean var1) {
      this.field_70180_af.func_75692_b(10, Byte.valueOf((byte)(var1 ? 1 : 0)));
   }
}
