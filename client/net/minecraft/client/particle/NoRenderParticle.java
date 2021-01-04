package net.minecraft.client.particle;

import com.mojang.blaze3d.vertex.BufferBuilder;
import net.minecraft.client.Camera;
import net.minecraft.world.level.Level;

public class NoRenderParticle extends Particle {
   protected NoRenderParticle(Level var1, double var2, double var4, double var6) {
      super(var1, var2, var4, var6);
   }

   protected NoRenderParticle(Level var1, double var2, double var4, double var6, double var8, double var10, double var12) {
      super(var1, var2, var4, var6, var8, var10, var12);
   }

   public final void render(BufferBuilder var1, Camera var2, float var3, float var4, float var5, float var6, float var7, float var8) {
   }

   public ParticleRenderType getRenderType() {
      return ParticleRenderType.NO_RENDER;
   }
}
