package net.minecraft.client.particle;

import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix4f;

public class FootprintParticle extends Particle {
   private final TextureAtlasSprite sprite;
   private final float rot;

   protected FootprintParticle(ClientLevel var1, double var2, double var4, double var6, double var8, double var10, double var12, SpriteSet var14) {
      super(var1, var2, var4, var6);
      this.xd = 0.0;
      this.yd = 0.0;
      this.zd = 0.0;
      this.rot = (float)var8;
      this.lifetime = 200;
      this.gravity = 0.0F;
      this.hasPhysics = false;
      this.sprite = var14.get(this.random);
   }

   @Override
   public ParticleRenderType getRenderType() {
      return ParticleRenderType.PARTICLE_SHEET_TRANSLUCENT;
   }

   @Override
   public void render(VertexConsumer var1, Camera var2, float var3) {
      float var4 = ((float)this.age + var3) / (float)this.lifetime;
      var4 *= var4;
      float var5 = 2.0F - var4 * 2.0F;
      var5 *= 0.2F;
      float var6 = 0.125F;
      Vec3 var7 = var2.getPosition();
      float var8 = (float)(this.x - var7.x);
      float var9 = (float)(this.y - var7.y);
      float var10 = (float)(this.z - var7.z);
      int var11 = this.getLightColor(var3);
      float var12 = this.sprite.getU0();
      float var13 = this.sprite.getU1();
      float var14 = this.sprite.getV0();
      float var15 = this.sprite.getV1();
      Matrix4f var16 = new Matrix4f().translation(var8, var9, var10);
      var16.rotate(0.017453292F * this.rot, 0.0F, 1.0F, 0.0F);
      var1.vertex(var16, -0.125F, 0.0F, 0.125F).uv(var12, var15).color(this.rCol, this.gCol, this.bCol, var5).uv2(var11).endVertex();
      var1.vertex(var16, 0.125F, 0.0F, 0.125F).uv(var13, var15).color(this.rCol, this.gCol, this.bCol, var5).uv2(var11).endVertex();
      var1.vertex(var16, 0.125F, 0.0F, -0.125F).uv(var13, var14).color(this.rCol, this.gCol, this.bCol, var5).uv2(var11).endVertex();
      var1.vertex(var16, -0.125F, 0.0F, -0.125F).uv(var12, var14).color(this.rCol, this.gCol, this.bCol, var5).uv2(var11).endVertex();
   }

   public static class Provider implements ParticleProvider<SimpleParticleType> {
      private final SpriteSet sprites;

      public Provider(SpriteSet var1) {
         super();
         this.sprites = var1;
      }

      public Particle createParticle(SimpleParticleType var1, ClientLevel var2, double var3, double var5, double var7, double var9, double var11, double var13) {
         return new FootprintParticle(var2, var3, var5, var7, var9, var11, var13, this.sprites);
      }
   }
}
