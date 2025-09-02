package net.minecraft.client.particle;

import com.mojang.blaze3d.vertex.PoseStack;
import java.util.List;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ParticleGroupRenderState;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;

public class ItemPickupParticleGroup extends ParticleGroup<ItemPickupParticle> {
   public ItemPickupParticleGroup(ParticleEngine var1) {
      super(var1);
   }

   public ParticleGroupRenderState extractRenderState(Frustum var1, Camera var2, float var3) {
      return new State(this.particles.stream().map((var2x) -> ItemPickupParticleGroup.ParticleInstance.fromParticle(var2x, var2, var3)).toList());
   }

   static record State(List<ParticleInstance> instances) implements ParticleGroupRenderState {
      State(List<ParticleInstance> var1) {
         super();
         this.instances = var1;
      }

      public void submit(SubmitNodeCollector var1) {
         PoseStack var2 = new PoseStack();
         EntityRenderDispatcher var3 = Minecraft.getInstance().getEntityRenderDispatcher();

         for(ParticleInstance var5 : this.instances) {
            var3.submit(var5.itemRenderState, var5.xOffset, var5.yOffset, var5.zOffset, var2, var1);
         }

      }
   }

   static record ParticleInstance(EntityRenderState itemRenderState, double xOffset, double yOffset, double zOffset) {
      final EntityRenderState itemRenderState;
      final double xOffset;
      final double yOffset;
      final double zOffset;

      private ParticleInstance(EntityRenderState var1, double var2, double var4, double var6) {
         super();
         this.itemRenderState = var1;
         this.xOffset = var2;
         this.yOffset = var4;
         this.zOffset = var6;
      }

      public static ParticleInstance fromParticle(ItemPickupParticle var0, Camera var1, float var2) {
         float var3 = ((float)var0.life + var2) / 3.0F;
         var3 *= var3;
         double var4 = Mth.lerp((double)var2, var0.targetXOld, var0.targetX);
         double var6 = Mth.lerp((double)var2, var0.targetYOld, var0.targetY);
         double var8 = Mth.lerp((double)var2, var0.targetZOld, var0.targetZ);
         double var10 = Mth.lerp((double)var3, var0.itemRenderState.x, var4);
         double var12 = Mth.lerp((double)var3, var0.itemRenderState.y, var6);
         double var14 = Mth.lerp((double)var3, var0.itemRenderState.z, var8);
         Vec3 var16 = var1.getPosition();
         return new ParticleInstance(var0.itemRenderState, var10 - var16.x(), var12 - var16.y(), var14 - var16.z());
      }
   }
}
