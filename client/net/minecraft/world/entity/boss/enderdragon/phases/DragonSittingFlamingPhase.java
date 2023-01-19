package net.minecraft.world.entity.boss.enderdragon.phases;

import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.AreaEffectCloud;
import net.minecraft.world.entity.boss.enderdragon.EnderDragon;
import net.minecraft.world.phys.Vec3;

public class DragonSittingFlamingPhase extends AbstractDragonSittingPhase {
   private static final int FLAME_DURATION = 200;
   private static final int SITTING_FLAME_ATTACKS_COUNT = 4;
   private static final int WARMUP_TIME = 10;
   private int flameTicks;
   private int flameCount;
   @Nullable
   private AreaEffectCloud flame;

   public DragonSittingFlamingPhase(EnderDragon var1) {
      super(var1);
   }

   @Override
   public void doClientTick() {
      ++this.flameTicks;
      if (this.flameTicks % 2 == 0 && this.flameTicks < 10) {
         Vec3 var1 = this.dragon.getHeadLookVector(1.0F).normalize();
         var1.yRot(-0.7853982F);
         double var2 = this.dragon.head.getX();
         double var4 = this.dragon.head.getY(0.5);
         double var6 = this.dragon.head.getZ();

         for(int var8 = 0; var8 < 8; ++var8) {
            double var9 = var2 + this.dragon.getRandom().nextGaussian() / 2.0;
            double var11 = var4 + this.dragon.getRandom().nextGaussian() / 2.0;
            double var13 = var6 + this.dragon.getRandom().nextGaussian() / 2.0;

            for(int var15 = 0; var15 < 6; ++var15) {
               this.dragon
                  .level
                  .addParticle(
                     ParticleTypes.DRAGON_BREATH,
                     var9,
                     var11,
                     var13,
                     -var1.x * 0.07999999821186066 * (double)var15,
                     -var1.y * 0.6000000238418579,
                     -var1.z * 0.07999999821186066 * (double)var15
                  );
            }

            var1.yRot(0.19634955F);
         }
      }
   }

   @Override
   public void doServerTick() {
      ++this.flameTicks;
      if (this.flameTicks >= 200) {
         if (this.flameCount >= 4) {
            this.dragon.getPhaseManager().setPhase(EnderDragonPhase.TAKEOFF);
         } else {
            this.dragon.getPhaseManager().setPhase(EnderDragonPhase.SITTING_SCANNING);
         }
      } else if (this.flameTicks == 10) {
         Vec3 var1 = new Vec3(this.dragon.head.getX() - this.dragon.getX(), 0.0, this.dragon.head.getZ() - this.dragon.getZ()).normalize();
         float var2 = 5.0F;
         double var3 = this.dragon.head.getX() + var1.x * 5.0 / 2.0;
         double var5 = this.dragon.head.getZ() + var1.z * 5.0 / 2.0;
         double var7 = this.dragon.head.getY(0.5);
         double var9 = var7;
         BlockPos.MutableBlockPos var11 = new BlockPos.MutableBlockPos(var3, var7, var5);

         while(this.dragon.level.isEmptyBlock(var11)) {
            if (--var9 < 0.0) {
               var9 = var7;
               break;
            }

            var11.set(var3, var9, var5);
         }

         var9 = (double)(Mth.floor(var9) + 1);
         this.flame = new AreaEffectCloud(this.dragon.level, var3, var9, var5);
         this.flame.setOwner(this.dragon);
         this.flame.setRadius(5.0F);
         this.flame.setDuration(200);
         this.flame.setParticle(ParticleTypes.DRAGON_BREATH);
         this.flame.addEffect(new MobEffectInstance(MobEffects.HARM));
         this.dragon.level.addFreshEntity(this.flame);
      }
   }

   @Override
   public void begin() {
      this.flameTicks = 0;
      ++this.flameCount;
   }

   @Override
   public void end() {
      if (this.flame != null) {
         this.flame.discard();
         this.flame = null;
      }
   }

   @Override
   public EnderDragonPhase<DragonSittingFlamingPhase> getPhase() {
      return EnderDragonPhase.SITTING_FLAMING;
   }

   public void resetFlameCount() {
      this.flameCount = 0;
   }
}
