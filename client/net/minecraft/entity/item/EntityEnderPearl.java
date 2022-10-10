package net.minecraft.entity.item;

import javax.annotation.Nullable;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.monster.EntityEndermite;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.projectile.EntityThrowable;
import net.minecraft.init.Particles;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityEndGateway;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import net.minecraft.world.dimension.DimensionType;

public class EntityEnderPearl extends EntityThrowable {
   private EntityLivingBase field_181555_c;

   public EntityEnderPearl(World var1) {
      super(EntityType.field_200752_ar, var1);
   }

   public EntityEnderPearl(World var1, EntityLivingBase var2) {
      super(EntityType.field_200752_ar, var2, var1);
      this.field_181555_c = var2;
   }

   public EntityEnderPearl(World var1, double var2, double var4, double var6) {
      super(EntityType.field_200752_ar, var2, var4, var6, var1);
   }

   protected void func_70184_a(RayTraceResult var1) {
      EntityLivingBase var2 = this.func_85052_h();
      if (var1.field_72308_g != null) {
         if (var1.field_72308_g == this.field_181555_c) {
            return;
         }

         var1.field_72308_g.func_70097_a(DamageSource.func_76356_a(this, var2), 0.0F);
      }

      if (var1.field_72313_a == RayTraceResult.Type.BLOCK) {
         BlockPos var3 = var1.func_178782_a();
         TileEntity var4 = this.field_70170_p.func_175625_s(var3);
         if (var4 instanceof TileEntityEndGateway) {
            TileEntityEndGateway var5 = (TileEntityEndGateway)var4;
            if (var2 != null) {
               if (var2 instanceof EntityPlayerMP) {
                  CriteriaTriggers.field_192124_d.func_192193_a((EntityPlayerMP)var2, this.field_70170_p.func_180495_p(var3));
               }

               var5.func_195496_a(var2);
               this.func_70106_y();
               return;
            }

            var5.func_195496_a(this);
            return;
         }
      }

      for(int var6 = 0; var6 < 32; ++var6) {
         this.field_70170_p.func_195594_a(Particles.field_197599_J, this.field_70165_t, this.field_70163_u + this.field_70146_Z.nextDouble() * 2.0D, this.field_70161_v, this.field_70146_Z.nextGaussian(), 0.0D, this.field_70146_Z.nextGaussian());
      }

      if (!this.field_70170_p.field_72995_K) {
         if (var2 instanceof EntityPlayerMP) {
            EntityPlayerMP var7 = (EntityPlayerMP)var2;
            if (var7.field_71135_a.func_147362_b().func_150724_d() && var7.field_70170_p == this.field_70170_p && !var7.func_70608_bn()) {
               if (this.field_70146_Z.nextFloat() < 0.05F && this.field_70170_p.func_82736_K().func_82766_b("doMobSpawning")) {
                  EntityEndermite var8 = new EntityEndermite(this.field_70170_p);
                  var8.func_175496_a(true);
                  var8.func_70012_b(var2.field_70165_t, var2.field_70163_u, var2.field_70161_v, var2.field_70177_z, var2.field_70125_A);
                  this.field_70170_p.func_72838_d(var8);
               }

               if (var2.func_184218_aH()) {
                  var2.func_184210_p();
               }

               var2.func_70634_a(this.field_70165_t, this.field_70163_u, this.field_70161_v);
               var2.field_70143_R = 0.0F;
               var2.func_70097_a(DamageSource.field_76379_h, 5.0F);
            }
         } else if (var2 != null) {
            var2.func_70634_a(this.field_70165_t, this.field_70163_u, this.field_70161_v);
            var2.field_70143_R = 0.0F;
         }

         this.func_70106_y();
      }

   }

   public void func_70071_h_() {
      EntityLivingBase var1 = this.func_85052_h();
      if (var1 != null && var1 instanceof EntityPlayer && !var1.func_70089_S()) {
         this.func_70106_y();
      } else {
         super.func_70071_h_();
      }

   }

   @Nullable
   public Entity func_212321_a(DimensionType var1) {
      if (this.field_70192_c.field_71093_bK != var1) {
         this.field_70192_c = null;
      }

      return super.func_212321_a(var1);
   }
}
