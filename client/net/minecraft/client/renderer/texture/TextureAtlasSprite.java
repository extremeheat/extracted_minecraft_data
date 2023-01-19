package net.minecraft.client.renderer.texture;

import com.mojang.blaze3d.vertex.VertexConsumer;
import javax.annotation.Nullable;
import net.minecraft.client.renderer.SpriteCoordinateExpander;
import net.minecraft.resources.ResourceLocation;

public class TextureAtlasSprite {
   private final ResourceLocation atlasLocation;
   private final SpriteContents contents;
   final int x;
   final int y;
   private final float u0;
   private final float u1;
   private final float v0;
   private final float v1;

   protected TextureAtlasSprite(ResourceLocation var1, SpriteContents var2, int var3, int var4, int var5, int var6) {
      super();
      this.atlasLocation = var1;
      this.contents = var2;
      this.x = var5;
      this.y = var6;
      this.u0 = (float)var5 / (float)var3;
      this.u1 = (float)(var5 + var2.width()) / (float)var3;
      this.v0 = (float)var6 / (float)var4;
      this.v1 = (float)(var6 + var2.height()) / (float)var4;
   }

   public int getX() {
      return this.x;
   }

   public int getY() {
      return this.y;
   }

   public float getU0() {
      return this.u0;
   }

   public float getU1() {
      return this.u1;
   }

   public SpriteContents contents() {
      return this.contents;
   }

   @Nullable
   public TextureAtlasSprite.Ticker createTicker() {
      final SpriteTicker var1 = this.contents.createTicker();
      return var1 != null ? new TextureAtlasSprite.Ticker() {
         @Override
         public void tickAndUpload() {
            var1.tickAndUpload(TextureAtlasSprite.this.x, TextureAtlasSprite.this.y);
         }

         @Override
         public void close() {
            var1.close();
         }
      } : null;
   }

   public float getU(double var1) {
      float var3 = this.u1 - this.u0;
      return this.u0 + var3 * (float)var1 / 16.0F;
   }

   public float getUOffset(float var1) {
      float var2 = this.u1 - this.u0;
      return (var1 - this.u0) / var2 * 16.0F;
   }

   public float getV0() {
      return this.v0;
   }

   public float getV1() {
      return this.v1;
   }

   public float getV(double var1) {
      float var3 = this.v1 - this.v0;
      return this.v0 + var3 * (float)var1 / 16.0F;
   }

   public float getVOffset(float var1) {
      float var2 = this.v1 - this.v0;
      return (var1 - this.v0) / var2 * 16.0F;
   }

   public ResourceLocation atlasLocation() {
      return this.atlasLocation;
   }

   @Override
   public String toString() {
      return "TextureAtlasSprite{contents='" + this.contents + "', u0=" + this.u0 + ", u1=" + this.u1 + ", v0=" + this.v0 + ", v1=" + this.v1 + "}";
   }

   public void uploadFirstFrame() {
      this.contents.uploadFirstFrame(this.x, this.y);
   }

   private float atlasSize() {
      float var1 = (float)this.contents.width() / (this.u1 - this.u0);
      float var2 = (float)this.contents.height() / (this.v1 - this.v0);
      return Math.max(var2, var1);
   }

   public float uvShrinkRatio() {
      return 4.0F / this.atlasSize();
   }

   public VertexConsumer wrap(VertexConsumer var1) {
      return new SpriteCoordinateExpander(var1, this);
   }

   public interface Ticker extends AutoCloseable {
      void tickAndUpload();

      @Override
      void close();
   }
}
