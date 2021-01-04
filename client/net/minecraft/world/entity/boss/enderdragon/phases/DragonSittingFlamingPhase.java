package net.minecraft.world.entity.boss.enderdragon.phases;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.AreaEffectCloud;
import net.minecraft.world.entity.boss.enderdragon.EnderDragon;
import net.minecraft.world.phys.Vec3;

public class DragonSittingFlamingPhase extends AbstractDragonSittingPhase {
   private int flameTicks;
   private int flameCount;
   private AreaEffectCloud flame;

   public DragonSittingFlamingPhase(EnderDragon var1) {
      super(var1);
   }

   public void doClientTick() {
      ++this.flameTicks;
      if (this.flameTicks % 2 == 0 && this.flameTicks < 10) {
         Vec3 var1 = this.dragon.getHeadLookVector(1.0F).normalize();
         var1.yRot(-0.7853982F);
         double var2 = this.dragon.head.x;
         double var4 = this.dragon.head.y + (double)(this.dragon.head.getBbHeight() / 2.0F);
         double var6 = this.dragon.head.z;

         for(int var8 = 0; var8 < 8; ++var8) {
            double var9 = var2 + this.dragon.getRandom().nextGaussian() / 2.0D;
            double var11 = var4 + this.dragon.getRandom().nextGaussian() / 2.0D;
            double var13 = var6 + this.dragon.getRandom().nextGaussian() / 2.0D;

            for(int var15 = 0; var15 < 6; ++var15) {
               this.dragon.level.addParticle(ParticleTypes.DRAGON_BREATH, var9, var11, var13, -var1.x * 0.07999999821186066D * (double)var15, -var1.y * 0.6000000238418579D, -var1.z * 0.07999999821186066D * (double)var15);
            }

            var1.yRot(0.19634955F);
         }
      }

   }

   public void doServerTick() {
      ++this.flameTicks;
      if (this.flameTicks >= 200) {
         if (this.flameCount >= 4) {
            this.dragon.getPhaseManager().setPhase(EnderDragonPhase.TAKEOFF);
         } else {
            this.dragon.getPhaseManager().setPhase(EnderDragonPhase.SITTING_SCANNING);
         }
      } else if (this.flameTicks == 10) {
         Vec3 var1 = (new Vec3(this.dragon.head.x - this.dragon.x, 0.0D, this.dragon.head.z - this.dragon.z)).normalize();
         float var2 = 5.0F;
         double var3 = this.dragon.head.x + var1.x * 5.0D / 2.0D;
         double var5 = this.dragon.head.z + var1.z * 5.0D / 2.0D;
         double var7 = this.dragon.head.y + (double)(this.dragon.head.getBbHeight() / 2.0F);
         BlockPos.MutableBlockPos var9 = new BlockPos.MutableBlockPos(var3, var7, var5);

         while(this.dragon.level.isEmptyBlock(var9)) {
            --var7;
            var9.set(var3, var7, var5);
         }

         var7 = (double)(Mth.floor(var7) + 1);
         this.flame = new AreaEffectCloud(this.dragon.level, var3, var7, var5);
         this.flame.setOwner(this.dragon);
         this.flame.setRadius(5.0F);
         this.flame.setDuration(200);
         this.flame.setParticle(ParticleTypes.DRAGON_BREATH);
         this.flame.addEffect(new MobEffectInstance(MobEffects.HARM));
         this.dragon.level.addFreshEntity(this.flame);
      }

   }

   public void begin() {
      this.flameTicks = 0;
      ++this.flameCount;
   }

   public void end() {
      if (this.flame != null) {
         this.flame.remove();
         this.flame = null;
      }

   }

   public EnderDragonPhase<DragonSittingFlamingPhase> getPhase() {
      return EnderDragonPhase.SITTING_FLAMING;
   }

   public void resetFlameCount() {
      this.flameCount = 0;
   }
}
