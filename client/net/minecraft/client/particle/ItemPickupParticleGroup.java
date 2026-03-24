package net.minecraft.client.particle;

import com.mojang.blaze3d.vertex.PoseStack;
import java.util.List;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.client.renderer.state.level.ParticleGroupRenderState;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;

public class ItemPickupParticleGroup extends ParticleGroup<ItemPickupParticle> {
   public ItemPickupParticleGroup(final ParticleEngine engine) {
      super(engine);
   }

   public ParticleGroupRenderState extractRenderState(final Frustum frustum, final Camera camera, final float partialTickTime) {
      return new State(this.particles.stream().map((particle) -> ItemPickupParticleGroup.ParticleInstance.fromParticle(particle, camera, partialTickTime)).toList());
   }

   private static record State(List<ParticleInstance> instances) implements ParticleGroupRenderState {
      private State {
         super();
      }

      public void submit(final SubmitNodeCollector submitNodeCollector, final CameraRenderState camera) {
         PoseStack poseStack = new PoseStack();
         EntityRenderDispatcher entityRenderDispatcher = Minecraft.getInstance().getEntityRenderDispatcher();

         for(ParticleInstance instance : this.instances) {
            entityRenderDispatcher.submit(instance.itemRenderState, camera, instance.xOffset, instance.yOffset, instance.zOffset, poseStack, submitNodeCollector);
         }

      }
   }

   private static record ParticleInstance(EntityRenderState itemRenderState, double xOffset, double yOffset, double zOffset) {
      private ParticleInstance {
         super();
      }

      public static ParticleInstance fromParticle(final ItemPickupParticle particle, final Camera camera, final float partialTickTime) {
         float time = ((float)particle.life + partialTickTime) / 3.0F;
         time *= time;
         double xt = Mth.lerp((double)partialTickTime, particle.targetXOld, particle.targetX);
         double yt = Mth.lerp((double)partialTickTime, particle.targetYOld, particle.targetY);
         double zt = Mth.lerp((double)partialTickTime, particle.targetZOld, particle.targetZ);
         double xx = Mth.lerp((double)time, particle.itemRenderState.x, xt);
         double yy = Mth.lerp((double)time, particle.itemRenderState.y, yt);
         double zz = Mth.lerp((double)time, particle.itemRenderState.z, zt);
         Vec3 pos = camera.position();
         return new ParticleInstance(particle.itemRenderState, xx - pos.x(), yy - pos.y(), zz - pos.z());
      }
   }
}
