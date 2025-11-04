package net.minecraft.client.renderer.texture;

import com.mojang.blaze3d.buffers.GpuBufferSlice;
import com.mojang.blaze3d.buffers.Std140Builder;
import com.mojang.blaze3d.textures.GpuTexture;
import com.mojang.blaze3d.vertex.VertexConsumer;
import java.nio.ByteBuffer;
import net.minecraft.client.renderer.SpriteCoordinateExpander;
import net.minecraft.resources.Identifier;
import org.joml.Matrix4f;
import org.jspecify.annotations.Nullable;
import org.lwjgl.system.MemoryUtil;

public class TextureAtlasSprite implements AutoCloseable {
   private final Identifier atlasLocation;
   private final SpriteContents contents;
   private final int x;
   private final int y;
   private final float u0;
   private final float u1;
   private final float v0;
   private final float v1;
   private final int padding;

   protected TextureAtlasSprite(Identifier var1, SpriteContents var2, int var3, int var4, int var5, int var6, int var7) {
      super();
      this.atlasLocation = var1;
      this.contents = var2;
      this.padding = var7;
      this.x = var5;
      this.y = var6;
      this.u0 = (float)(var5 + var7) / (float)var3;
      this.u1 = (float)(var5 + var7 + var2.width()) / (float)var3;
      this.v0 = (float)(var6 + var7) / (float)var4;
      this.v1 = (float)(var6 + var7 + var2.height()) / (float)var4;
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

   public SpriteContents.@Nullable AnimationState createAnimationState(GpuBufferSlice var1, int var2) {
      return this.contents.createAnimationState(var1, var2);
   }

   public float getU(float var1) {
      float var2 = this.u1 - this.u0;
      return this.u0 + var2 * var1;
   }

   public float getV0() {
      return this.v0;
   }

   public float getV1() {
      return this.v1;
   }

   public float getV(float var1) {
      float var2 = this.v1 - this.v0;
      return this.v0 + var2 * var1;
   }

   public Identifier atlasLocation() {
      return this.atlasLocation;
   }

   public String toString() {
      String var10000 = String.valueOf(this.contents);
      return "TextureAtlasSprite{contents='" + var10000 + "', u0=" + this.u0 + ", u1=" + this.u1 + ", v0=" + this.v0 + ", v1=" + this.v1 + "}";
   }

   public void uploadFirstFrame(GpuTexture var1, int var2) {
      this.contents.uploadFirstFrame(var1, var2);
   }

   public VertexConsumer wrap(VertexConsumer var1) {
      return new SpriteCoordinateExpander(var1, this);
   }

   boolean isAnimated() {
      return this.contents.isAnimated();
   }

   public void uploadSpriteUbo(ByteBuffer var1, int var2, int var3, int var4, int var5, int var6) {
      for(int var7 = 0; var7 <= var3; ++var7) {
         Std140Builder.intoBuffer(MemoryUtil.memSlice(var1, var2 + var7 * var6, var6)).putMat4f((new Matrix4f()).ortho2D(0.0F, (float)(var4 >> var7), 0.0F, (float)(var5 >> var7))).putMat4f((new Matrix4f()).translate((float)(this.x >> var7), (float)(this.y >> var7), 0.0F).scale((float)(this.contents.width() + this.padding * 2 >> var7), (float)(this.contents.height() + this.padding * 2 >> var7), 1.0F)).putFloat((float)this.padding / (float)this.contents.width()).putFloat((float)this.padding / (float)this.contents.height()).putInt(var7);
      }

   }

   public void close() {
      this.contents.close();
   }
}
