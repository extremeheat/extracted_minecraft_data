package net.minecraft.entity.projectile;

import java.util.Iterator;
import java.util.List;
import net.minecraft.entity.EntityAreaEffectCloud;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.EntityType;
import net.minecraft.init.MobEffects;
import net.minecraft.init.Particles;
import net.minecraft.particles.IParticleData;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;

public class EntityDragonFireball extends EntityFireball {
   public EntityDragonFireball(World var1) {
      super(EntityType.field_200799_m, var1, 1.0F, 1.0F);
   }

   public EntityDragonFireball(World var1, double var2, double var4, double var6, double var8, double var10, double var12) {
      super(EntityType.field_200799_m, var2, var4, var6, var8, var10, var12, var1, 1.0F, 1.0F);
   }

   public EntityDragonFireball(World var1, EntityLivingBase var2, double var3, double var5, double var7) {
      super(EntityType.field_200799_m, var2, var3, var5, var7, var1, 1.0F, 1.0F);
   }

   protected void func_70227_a(RayTraceResult var1) {
      if (var1.field_72308_g == null || !var1.field_72308_g.func_70028_i(this.field_70235_a)) {
         if (!this.field_70170_p.field_72995_K) {
            List var2 = this.field_70170_p.func_72872_a(EntityLivingBase.class, this.func_174813_aQ().func_72314_b(4.0D, 2.0D, 4.0D));
            EntityAreaEffectCloud var3 = new EntityAreaEffectCloud(this.field_70170_p, this.field_70165_t, this.field_70163_u, this.field_70161_v);
            var3.func_184481_a(this.field_70235_a);
            var3.func_195059_a(Particles.field_197616_i);
            var3.func_184483_a(3.0F);
            var3.func_184486_b(600);
            var3.func_184487_c((7.0F - var3.func_184490_j()) / (float)var3.func_184489_o());
            var3.func_184496_a(new PotionEffect(MobEffects.field_76433_i, 1, 1));
            if (!var2.isEmpty()) {
               Iterator var4 = var2.iterator();

               while(var4.hasNext()) {
                  EntityLivingBase var5 = (EntityLivingBase)var4.next();
                  double var6 = this.func_70068_e(var5);
                  if (var6 < 16.0D) {
                     var3.func_70107_b(var5.field_70165_t, var5.field_70163_u, var5.field_70161_v);
                     break;
                  }
               }
            }

            this.field_70170_p.func_175718_b(2006, new BlockPos(this.field_70165_t, this.field_70163_u, this.field_70161_v), 0);
            this.field_70170_p.func_72838_d(var3);
            this.func_70106_y();
         }

      }
   }

   public boolean func_70067_L() {
      return false;
   }

   public boolean func_70097_a(DamageSource var1, float var2) {
      return false;
   }

   protected IParticleData func_195057_f() {
      return Particles.field_197616_i;
   }

   protected boolean func_184564_k() {
      return false;
   }
}
