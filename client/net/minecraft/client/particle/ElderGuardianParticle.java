package net.minecraft.client.particle;

import net.minecraft.client.Minecraft;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.model.monster.guardian.GuardianParticleModel;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.entity.ElderGuardianRenderer;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.util.RandomSource;

public class ElderGuardianParticle extends Particle {
   protected final GuardianParticleModel model;
   protected final RenderType renderType;

   ElderGuardianParticle(ClientLevel var1, double var2, double var4, double var6) {
      super(var1, var2, var4, var6);
      this.renderType = RenderTypes.entityTranslucent(ElderGuardianRenderer.GUARDIAN_ELDER_LOCATION);
      this.model = new GuardianParticleModel(Minecraft.getInstance().getEntityModels().bakeLayer(ModelLayers.ELDER_GUARDIAN));
      this.gravity = 0.0F;
      this.lifetime = 30;
   }

   public ParticleRenderType getGroup() {
      return ParticleRenderType.ELDER_GUARDIANS;
   }

   public static class Provider implements ParticleProvider<SimpleParticleType> {
      public Provider() {
         super();
      }

      public Particle createParticle(SimpleParticleType var1, ClientLevel var2, double var3, double var5, double var7, double var9, double var11, double var13, RandomSource var15) {
         return new ElderGuardianParticle(var2, var3, var5, var7);
      }
   }
}
