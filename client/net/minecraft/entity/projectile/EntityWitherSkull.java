package net.minecraft.entity.projectile;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.boss.EntityWither;
import net.minecraft.fluid.IFluidState;
import net.minecraft.init.MobEffects;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.Explosion;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;

public class EntityWitherSkull extends EntityFireball {
   private static final DataParameter<Boolean> field_184565_e;

   public EntityWitherSkull(World var1) {
      super(EntityType.field_200723_aB, var1, 0.3125F, 0.3125F);
   }

   public EntityWitherSkull(World var1, EntityLivingBase var2, double var3, double var5, double var7) {
      super(EntityType.field_200723_aB, var2, var3, var5, var7, var1, 0.3125F, 0.3125F);
   }

   public EntityWitherSkull(World var1, double var2, double var4, double var6, double var8, double var10, double var12) {
      super(EntityType.field_200723_aB, var2, var4, var6, var8, var10, var12, var1, 0.3125F, 0.3125F);
   }

   protected float func_82341_c() {
      return this.func_82342_d() ? 0.73F : super.func_82341_c();
   }

   public boolean func_70027_ad() {
      return false;
   }

   public float func_180428_a(Explosion var1, IBlockReader var2, BlockPos var3, IBlockState var4, IFluidState var5, float var6) {
      return this.func_82342_d() && EntityWither.func_181033_a(var4.func_177230_c()) ? Math.min(0.8F, var6) : var6;
   }

   protected void func_70227_a(RayTraceResult var1) {
      if (!this.field_70170_p.field_72995_K) {
         if (var1.field_72308_g != null) {
            if (this.field_70235_a != null) {
               if (var1.field_72308_g.func_70097_a(DamageSource.func_76358_a(this.field_70235_a), 8.0F)) {
                  if (var1.field_72308_g.func_70089_S()) {
                     this.func_174815_a(this.field_70235_a, var1.field_72308_g);
                  } else {
                     this.field_70235_a.func_70691_i(5.0F);
                  }
               }
            } else {
               var1.field_72308_g.func_70097_a(DamageSource.field_76376_m, 5.0F);
            }

            if (var1.field_72308_g instanceof EntityLivingBase) {
               byte var2 = 0;
               if (this.field_70170_p.func_175659_aa() == EnumDifficulty.NORMAL) {
                  var2 = 10;
               } else if (this.field_70170_p.func_175659_aa() == EnumDifficulty.HARD) {
                  var2 = 40;
               }

               if (var2 > 0) {
                  ((EntityLivingBase)var1.field_72308_g).func_195064_c(new PotionEffect(MobEffects.field_82731_v, 20 * var2, 1));
               }
            }
         }

         this.field_70170_p.func_72885_a(this, this.field_70165_t, this.field_70163_u, this.field_70161_v, 1.0F, false, this.field_70170_p.func_82736_K().func_82766_b("mobGriefing"));
         this.func_70106_y();
      }

   }

   public boolean func_70067_L() {
      return false;
   }

   public boolean func_70097_a(DamageSource var1, float var2) {
      return false;
   }

   protected void func_70088_a() {
      this.field_70180_af.func_187214_a(field_184565_e, false);
   }

   public boolean func_82342_d() {
      return (Boolean)this.field_70180_af.func_187225_a(field_184565_e);
   }

   public void func_82343_e(boolean var1) {
      this.field_70180_af.func_187227_b(field_184565_e, var1);
   }

   protected boolean func_184564_k() {
      return false;
   }

   static {
      field_184565_e = EntityDataManager.func_187226_a(EntityWitherSkull.class, DataSerializers.field_187198_h);
   }
}
