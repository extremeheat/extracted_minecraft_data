package net.minecraft.client.particle;

import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;

public class NoRenderParticle extends Particle {
   protected NoRenderParticle(ClientLevel var1, double var2, double var4, double var6) {
      super(var1, var2, var4, var6);
   }

   protected NoRenderParticle(ClientLevel var1, double var2, double var4, double var6, double var8, double var10, double var12) {
      super(var1, var2, var4, var6, var8, var10, var12);
   }

   public final void render(VertexConsumer var1, Camera var2, float var3) {
   }

   public ParticleRenderType getRenderType() {
      return ParticleRenderType.NO_RENDER;
   }
}
