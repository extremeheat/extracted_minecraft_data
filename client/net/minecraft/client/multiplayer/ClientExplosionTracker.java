package net.minecraft.client.multiplayer;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ExplosionParticleInfo;
import net.minecraft.server.level.ParticleStatus;
import net.minecraft.util.RandomSource;
import net.minecraft.util.random.WeightedList;
import net.minecraft.util.random.WeightedRandom;
import net.minecraft.world.phys.Vec3;

public class ClientExplosionTracker {
   private static final int MAX_PARTICLES_PER_TICK = 512;
   private final List<ExplosionInfo> explosions = new ArrayList();

   public ClientExplosionTracker() {
      super();
   }

   public void track(Vec3 var1, float var2, int var3, WeightedList<ExplosionParticleInfo> var4) {
      if (!var4.isEmpty()) {
         this.explosions.add(new ExplosionInfo(var1, var2, var3, var4));
      }

   }

   public void tick(ClientLevel var1) {
      if (Minecraft.getInstance().options.particles().get() != ParticleStatus.ALL) {
         this.explosions.clear();
      } else {
         int var2 = WeightedRandom.getTotalWeight(this.explosions, ExplosionInfo::blockCount);
         int var3 = Math.min(var2, 512);

         for(int var4 = 0; var4 < var3; ++var4) {
            WeightedRandom.getRandomItem(var1.getRandom(), this.explosions, var2, ExplosionInfo::blockCount).ifPresent((var2x) -> this.addParticle(var1, var2x));
         }

         this.explosions.clear();
      }
   }

   private void addParticle(ClientLevel var1, ExplosionInfo var2) {
      RandomSource var3 = var1.getRandom();
      Vec3 var4 = var2.center();
      Vec3 var5 = (new Vec3((double)(var3.nextFloat() * 2.0F - 1.0F), (double)(var3.nextFloat() * 2.0F - 1.0F), (double)(var3.nextFloat() * 2.0F - 1.0F))).normalize();
      float var6 = (float)Math.cbrt((double)var3.nextFloat()) * var2.radius();
      Vec3 var7 = var5.scale((double)var6);
      Vec3 var8 = var4.add(var7);
      if (var1.getBlockState(BlockPos.containing(var8)).isAir()) {
         float var9 = 0.5F / (var6 / var2.radius() + 0.1F) * var3.nextFloat() * var3.nextFloat() + 0.3F;
         ExplosionParticleInfo var10 = var2.blockParticles.getRandomOrThrow(var3);
         Vec3 var11 = var4.add(var7.scale((double)var10.scaling()));
         Vec3 var12 = var5.scale((double)(var9 * var10.speed()));
         var1.addParticle(var10.particle(), var11.x(), var11.y(), var11.z(), var12.x(), var12.y(), var12.z());
      }
   }

   static record ExplosionInfo(Vec3 center, float radius, int blockCount, WeightedList<ExplosionParticleInfo> blockParticles) {
      final WeightedList<ExplosionParticleInfo> blockParticles;

      ExplosionInfo(Vec3 var1, float var2, int var3, WeightedList<ExplosionParticleInfo> var4) {
         super();
         this.center = var1;
         this.radius = var2;
         this.blockCount = var3;
         this.blockParticles = var4;
      }
   }
}
