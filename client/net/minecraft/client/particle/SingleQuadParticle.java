package net.minecraft.client.particle;

import com.mojang.blaze3d.pipeline.RenderPipeline;
import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.renderer.state.QuadParticleRenderState;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.resources.Identifier;
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

   protected SingleQuadParticle(final ClientLevel level, final double x, final double y, final double z, final TextureAtlasSprite sprite) {
      super(level, x, y, z);
      this.sprite = sprite;
      this.quadSize = 0.1F * (this.random.nextFloat() * 0.5F + 0.5F) * 2.0F;
   }

   protected SingleQuadParticle(final ClientLevel level, final double x, final double y, final double z, final double xa, final double ya, final double za, final TextureAtlasSprite sprite) {
      super(level, x, y, z, xa, ya, za);
      this.sprite = sprite;
      this.quadSize = 0.1F * (this.random.nextFloat() * 0.5F + 0.5F) * 2.0F;
   }

   public FacingCameraMode getFacingCameraMode() {
      return SingleQuadParticle.FacingCameraMode.LOOKAT_XYZ;
   }

   public void extract(final QuadParticleRenderState particleTypeRenderState, final Camera camera, final float partialTickTime) {
      Quaternionf rotation = new Quaternionf();
      this.getFacingCameraMode().setRotation(rotation, camera, partialTickTime);
      if (this.roll != 0.0F) {
         rotation.rotateZ(Mth.lerp(partialTickTime, this.oRoll, this.roll));
      }

      this.extractRotatedQuad(particleTypeRenderState, camera, rotation, partialTickTime);
   }

   protected void extractRotatedQuad(final QuadParticleRenderState particleTypeRenderState, final Camera camera, final Quaternionf rotation, final float partialTickTime) {
      Vec3 pos = camera.position();
      float x = (float)(Mth.lerp((double)partialTickTime, this.xo, this.x) - pos.x());
      float y = (float)(Mth.lerp((double)partialTickTime, this.yo, this.y) - pos.y());
      float z = (float)(Mth.lerp((double)partialTickTime, this.zo, this.z) - pos.z());
      this.extractRotatedQuad(particleTypeRenderState, rotation, x, y, z, partialTickTime);
   }

   protected void extractRotatedQuad(final QuadParticleRenderState particleTypeRenderState, final Quaternionf rotation, final float x, final float y, final float z, final float partialTickTime) {
      particleTypeRenderState.add(this.getLayer(), x, y, z, rotation.x, rotation.y, rotation.z, rotation.w, this.getQuadSize(partialTickTime), this.getU0(), this.getU1(), this.getV0(), this.getV1(), ARGB.colorFromFloat(this.alpha, this.rCol, this.gCol, this.bCol), this.getLightCoords(partialTickTime));
   }

   public float getQuadSize(final float a) {
      return this.quadSize;
   }

   public Particle scale(final float scale) {
      this.quadSize *= scale;
      return super.scale(scale);
   }

   public ParticleRenderType getGroup() {
      return ParticleRenderType.SINGLE_QUADS;
   }

   public void setSpriteFromAge(final SpriteSet sprites) {
      if (!this.removed) {
         this.setSprite(sprites.get(this.age, this.lifetime));
      }

   }

   protected void setSprite(final TextureAtlasSprite icon) {
      this.sprite = icon;
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

   public void setColor(final float r, final float g, final float b) {
      this.rCol = r;
      this.gCol = g;
      this.bCol = b;
   }

   protected void setAlpha(final float alpha) {
      this.alpha = alpha;
   }

   public String toString() {
      String var10000 = this.getClass().getSimpleName();
      return var10000 + ", Pos (" + this.x + "," + this.y + "," + this.z + "), RGBA (" + this.rCol + "," + this.gCol + "," + this.bCol + "," + this.alpha + "), Age " + this.age;
   }

   public interface FacingCameraMode {
      FacingCameraMode LOOKAT_XYZ = (target, camera, partialTickTime) -> target.set(camera.rotation());
      FacingCameraMode LOOKAT_Y = (target, camera, partialTickTime) -> target.set(0.0F, camera.rotation().y, 0.0F, camera.rotation().w);

      void setRotation(final Quaternionf target, final Camera camera, final float partialTickTime);
   }

   public static record Layer(boolean translucent, Identifier textureAtlasLocation, RenderPipeline pipeline) {
      public static final Layer OPAQUE_TERRAIN;
      public static final Layer TRANSLUCENT_TERRAIN;
      public static final Layer OPAQUE_ITEMS;
      public static final Layer TRANSLUCENT_ITEMS;
      public static final Layer OPAQUE;
      public static final Layer TRANSLUCENT;

      public Layer {
         super();
      }

      public static Layer bySprite(final TextureAtlasSprite sprite) {
         boolean translucent = sprite.transparency().hasTranslucent();
         if (sprite.atlasLocation().equals(TextureAtlas.LOCATION_BLOCKS)) {
            return translucent ? TRANSLUCENT_TERRAIN : OPAQUE_TERRAIN;
         } else if (sprite.atlasLocation().equals(TextureAtlas.LOCATION_ITEMS)) {
            return translucent ? TRANSLUCENT_ITEMS : OPAQUE_ITEMS;
         } else {
            return translucent ? TRANSLUCENT : OPAQUE;
         }
      }

      static {
         OPAQUE_TERRAIN = new Layer(false, TextureAtlas.LOCATION_BLOCKS, RenderPipelines.OPAQUE_PARTICLE);
         TRANSLUCENT_TERRAIN = new Layer(true, TextureAtlas.LOCATION_BLOCKS, RenderPipelines.TRANSLUCENT_PARTICLE);
         OPAQUE_ITEMS = new Layer(false, TextureAtlas.LOCATION_ITEMS, RenderPipelines.OPAQUE_PARTICLE);
         TRANSLUCENT_ITEMS = new Layer(true, TextureAtlas.LOCATION_ITEMS, RenderPipelines.TRANSLUCENT_PARTICLE);
         OPAQUE = new Layer(false, TextureAtlas.LOCATION_PARTICLES, RenderPipelines.OPAQUE_PARTICLE);
         TRANSLUCENT = new Layer(true, TextureAtlas.LOCATION_PARTICLES, RenderPipelines.TRANSLUCENT_PARTICLE);
      }
   }
}
