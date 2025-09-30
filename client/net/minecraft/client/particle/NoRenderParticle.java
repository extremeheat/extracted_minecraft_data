package net.minecraft.client.particle;

import net.minecraft.client.multiplayer.ClientLevel;

public class NoRenderParticle extends Particle {
   protected NoRenderParticle(ClientLevel var1, double var2, double var4, double var6) {
      super(var1, var2, var4, var6);
   }

   protected NoRenderParticle(ClientLevel var1, double var2, double var4, double var6, double var8, double var10, double var12) {
      super(var1, var2, var4, var6, var8, var10, var12);
   }

   public ParticleRenderType getGroup() {
      return ParticleRenderType.NO_RENDER;
   }
}
