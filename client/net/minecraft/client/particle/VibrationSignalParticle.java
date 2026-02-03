package net.minecraft.client.particle;

import java.util.Optional;
import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.state.QuadParticleRenderState;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.particles.VibrationParticleOption;
import net.minecraft.util.LightCoordsUtil;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.gameevent.PositionSource;
import net.minecraft.world.phys.Vec3;
import org.joml.Quaternionf;

public class VibrationSignalParticle extends SingleQuadParticle {
   private final PositionSource target;
   private float rot;
   private float rotO;
   private float pitch;
   private float pitchO;

   private VibrationSignalParticle(final ClientLevel level, final double x, final double y, final double z, final PositionSource target, final int arrivalInTicks, final TextureAtlasSprite sprite) {
      super(level, x, y, z, 0.0, 0.0, 0.0, sprite);
      this.quadSize = 0.3F;
      this.target = target;
      this.lifetime = arrivalInTicks;
      Optional<Vec3> position = target.getPosition(level);
      if (position.isPresent()) {
         Vec3 destination = (Vec3)position.get();
         double dx = x - destination.x();
         double dy = y - destination.y();
         double dz = z - destination.z();
         this.rotO = this.rot = (float)Mth.atan2(dx, dz);
         this.pitchO = this.pitch = (float)Mth.atan2(dy, Math.sqrt(dx * dx + dz * dz));
      }

   }

   public void extract(final QuadParticleRenderState particleTypeRenderState, final Camera camera, final float partialTickTime) {
      float randomSway = Mth.sin((double)(((float)this.age + partialTickTime - 6.2831855F) * 0.05F)) * 2.0F;
      float lerpedRotation = Mth.lerp(partialTickTime, this.rotO, this.rot);
      float lerpedPitch = Mth.lerp(partialTickTime, this.pitchO, this.pitch) + 1.5707964F;
      Quaternionf rotation = new Quaternionf();
      rotation.rotationY(lerpedRotation).rotateX(-lerpedPitch).rotateY(randomSway);
      this.extractRotatedQuad(particleTypeRenderState, camera, rotation, partialTickTime);
      rotation.rotationY(-3.1415927F + lerpedRotation).rotateX(lerpedPitch).rotateY(randomSway);
      this.extractRotatedQuad(particleTypeRenderState, camera, rotation, partialTickTime);
   }

   public int getLightCoords(final float a) {
      return LightCoordsUtil.withBlock(super.getLightCoords(a), 15);
   }

   public SingleQuadParticle.Layer getLayer() {
      return SingleQuadParticle.Layer.TRANSLUCENT;
   }

   public void tick() {
      this.xo = this.x;
      this.yo = this.y;
      this.zo = this.z;
      if (this.age++ >= this.lifetime) {
         this.remove();
      } else {
         Optional<Vec3> position = this.target.getPosition(this.level);
         if (position.isEmpty()) {
            this.remove();
         } else {
            int ticksRemaining = this.lifetime - this.age;
            double alpha = 1.0 / (double)ticksRemaining;
            Vec3 destination = (Vec3)position.get();
            this.x = Mth.lerp(alpha, this.x, destination.x());
            this.y = Mth.lerp(alpha, this.y, destination.y());
            this.z = Mth.lerp(alpha, this.z, destination.z());
            double dx = this.x - destination.x();
            double dy = this.y - destination.y();
            double dz = this.z - destination.z();
            this.rotO = this.rot;
            this.rot = (float)Mth.atan2(dx, dz);
            this.pitchO = this.pitch;
            this.pitch = (float)Mth.atan2(dy, Math.sqrt(dx * dx + dz * dz));
         }
      }
   }

   public static class Provider implements ParticleProvider<VibrationParticleOption> {
      private final SpriteSet sprite;

      public Provider(final SpriteSet sprite) {
         super();
         this.sprite = sprite;
      }

      public Particle createParticle(final VibrationParticleOption options, final ClientLevel level, final double x, final double y, final double z, final double xAux, final double yAux, final double zAux, final RandomSource random) {
         VibrationSignalParticle particle = new VibrationSignalParticle(level, x, y, z, options.getDestination(), options.getArrivalInTicks(), this.sprite.get(random));
         particle.setAlpha(1.0F);
         return particle;
      }
   }
}
