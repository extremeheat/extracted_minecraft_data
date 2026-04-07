package net.minecraft.client.particle;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.PotentSulfurEntity;
import net.minecraft.world.phys.Vec3;
import org.jspecify.annotations.Nullable;

public class NoxiousGasCloudParticle extends NoRenderParticle {
   private static final int PARTICLE_TICKS = 2;

   protected NoxiousGasCloudParticle(final ClientLevel level, final double x, final double y, final double z) {
      super(level, x, y, z);
      this.lifetime = 20;
   }

   public void tick() {
      super.tick();
      if (this.age % 2 == 0) {
         BlockPos sourceBlock = BlockPos.containing(this.x, this.y, this.z);
         Vec3 particlePos = pickRandomParticleSpawnPoint(this.level, sourceBlock);
         if (PotentSulfurEntity.canBeReachedByNoxiousGas(this.level, sourceBlock, particlePos)) {
            spawnNoxiousGasParticle(this.level, particlePos);
         }

      }
   }

   private static Vec3 pickRandomParticleSpawnPoint(final Level level, final BlockPos centerBlock) {
      RandomSource random = level.getRandom();
      Vec3 horizontalDirection = (new Vec3((double)(random.nextFloat() - 0.5F), 0.0, (double)(random.nextFloat() - 0.5F))).normalize();
      float distance = random.nextFloat() * 3.0F;
      return centerBlock.getCenter().add(horizontalDirection.scale((double)distance)).subtract(0.0, 0.25, 0.0);
   }

   private static void spawnNoxiousGasParticle(final Level level, final Vec3 pos) {
      level.addAlwaysVisibleParticle(ParticleTypes.NOXIOUS_GAS, pos.x, pos.y, pos.z, 0.0, 0.0, 0.0);
   }

   public static class Provider implements ParticleProvider<SimpleParticleType> {
      public Provider() {
         super();
      }

      public @Nullable Particle createParticle(final SimpleParticleType options, final ClientLevel level, final double x, final double y, final double z, final double xAux, final double yAux, final double zAux, final RandomSource random) {
         return new NoxiousGasCloudParticle(level, x, y, z);
      }
   }
}
