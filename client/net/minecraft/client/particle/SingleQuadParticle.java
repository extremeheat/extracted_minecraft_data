package net.minecraft.client.particle;

import com.mojang.blaze3d.pipeline.RenderPipeline;
import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.QuadParticleRenderState;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.ARGB;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import org.joml.Quaternionf;

public abstract class SingleQuadParticle extends Particle {
   protected float quadSize;
   protected float rCol = 1.0F;
   protected float gCol = 1.0F;
   protected float bCol = 1.0F;
   protected float alpha = 1.0F;
   protected float roll;
   protected float oRoll;
   protected TextureAtlasSprite sprite;

   protected SingleQuadParticle(ClientLevel var1, double var2, double var4, double var6, TextureAtlasSprite var8) {
      super(var1, var2, var4, var6);
      this.sprite = var8;
      this.quadSize = 0.1F * (this.random.nextFloat() * 0.5F + 0.5F) * 2.0F;
   }

   protected SingleQuadParticle(ClientLevel var1, double var2, double var4, double var6, double var8, double var10, double var12, TextureAtlasSprite var14) {
      super(var1, var2, var4, var6, var8, var10, var12);
      this.sprite = var14;
      this.quadSize = 0.1F * (this.random.nextFloat() * 0.5F + 0.5F) * 2.0F;
   }

   public FacingCameraMode getFacingCameraMode() {
      return SingleQuadParticle.FacingCameraMode.LOOKAT_XYZ;
   }

   public void extract(QuadParticleRenderState var1, Camera var2, float var3) {
      Quaternionf var4 = new Quaternionf();
      this.getFacingCameraMode().setRotation(var4, var2, var3);
      if (this.roll != 0.0F) {
         var4.rotateZ(Mth.lerp(var3, this.oRoll, this.roll));
      }

      this.extractRotatedQuad(var1, var2, var4, var3);
   }

   protected void extractRotatedQuad(QuadParticleRenderState var1, Camera var2, Quaternionf var3, float var4) {
      Vec3 var5 = var2.getPosition();
      float var6 = (float)(Mth.lerp((double)var4, this.xo, this.x) - var5.x());
      float var7 = (float)(Mth.lerp((double)var4, this.yo, this.y) - var5.y());
      float var8 = (float)(Mth.lerp((double)var4, this.zo, this.z) - var5.z());
      this.extractRotatedQuad(var1, var3, var6, var7, var8, var4);
   }

   protected void extractRotatedQuad(QuadParticleRenderState var1, Quaternionf var2, float var3, float var4, float var5, float var6) {
      var1.add(this.getLayer(), var3, var4, var5, var2.x, var2.y, var2.z, var2.w, this.getQuadSize(var6), this.getU0(), this.getU1(), this.getV0(), this.getV1(), ARGB.colorFromFloat(this.alpha, this.rCol, this.gCol, this.bCol), this.getLightColor(var6));
   }

   public float getQuadSize(float var1) {
      return this.quadSize;
   }

   public Particle scale(float var1) {
      this.quadSize *= var1;
      return super.scale(var1);
   }

   public ParticleRenderType getGroup() {
      return ParticleRenderType.SINGLE_QUADS;
   }

   public void setSpriteFromAge(SpriteSet var1) {
      if (!this.removed) {
         this.setSprite(var1.get(this.age, this.lifetime));
      }

   }

   protected void setSprite(TextureAtlasSprite var1) {
      this.sprite = var1;
   }

   protected float getU0() {
      return this.sprite.getU0();
   }

   protected float getU1() {
      return this.sprite.getU1();
   }

   protected float getV0() {
      return this.sprite.getV0();
   }

   protected float getV1() {
      return this.sprite.getV1();
   }

   protected abstract Layer getLayer();

   public void setColor(float var1, float var2, float var3) {
      this.rCol = var1;
      this.gCol = var2;
      this.bCol = var3;
   }

   protected void setAlpha(float var1) {
      this.alpha = var1;
   }

   public String toString() {
      String var10000 = this.getClass().getSimpleName();
      return var10000 + ", Pos (" + this.x + "," + this.y + "," + this.z + "), RGBA (" + this.rCol + "," + this.gCol + "," + this.bCol + "," + this.alpha + "), Age " + this.age;
   }

   public interface FacingCameraMode {
      FacingCameraMode LOOKAT_XYZ = (var0, var1, var2) -> var0.set(var1.rotation());
      FacingCameraMode LOOKAT_Y = (var0, var1, var2) -> var0.set(0.0F, var1.rotation().y, 0.0F, var1.rotation().w);

      void setRotation(Quaternionf var1, Camera var2, float var3);
   }

   public static record Layer(boolean translucent, ResourceLocation textureAtlasLocation, RenderPipeline pipeline) {
      public static final Layer TERRAIN;
      public static final Layer OPAQUE;
      public static final Layer TRANSLUCENT;

      public Layer(boolean var1, ResourceLocation var2, RenderPipeline var3) {
         super();
         this.translucent = var1;
         this.textureAtlasLocation = var2;
         this.pipeline = var3;
      }

      static {
         TERRAIN = new Layer(true, TextureAtlas.LOCATION_BLOCKS, RenderPipelines.TRANSLUCENT_PARTICLE);
         OPAQUE = new Layer(false, TextureAtlas.LOCATION_PARTICLES, RenderPipelines.OPAQUE_PARTICLE);
         TRANSLUCENT = new Layer(true, TextureAtlas.LOCATION_PARTICLES, RenderPipelines.TRANSLUCENT_PARTICLE);
      }
   }
}
