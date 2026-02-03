package net.minecraft.client.renderer.rendertype;

import java.util.function.Supplier;
import net.minecraft.client.Minecraft;
import net.minecraft.util.Util;
import org.joml.Matrix4f;

public class TextureTransform {
   public static final double MAX_ENCHANTMENT_GLINT_SPEED_MILLIS = 8.0;
   private final String name;
   private final Supplier<Matrix4f> supplier;
   public static final TextureTransform DEFAULT_TEXTURING = new TextureTransform("default_texturing", Matrix4f::new);
   public static final TextureTransform GLINT_TEXTURING = new TextureTransform("glint_texturing", () -> setupGlintTexturing(8.0F));
   public static final TextureTransform ENTITY_GLINT_TEXTURING = new TextureTransform("entity_glint_texturing", () -> setupGlintTexturing(0.5F));
   public static final TextureTransform ARMOR_ENTITY_GLINT_TEXTURING = new TextureTransform("armor_entity_glint_texturing", () -> setupGlintTexturing(0.16F));

   public TextureTransform(final String name, final Supplier<Matrix4f> matrix) {
      super();
      this.name = name;
      this.supplier = matrix;
   }

   public Matrix4f getMatrix() {
      return (Matrix4f)this.supplier.get();
   }

   public String toString() {
      return "TexturingStateShard[" + this.name + "]";
   }

   private static Matrix4f setupGlintTexturing(final float scale) {
      long millis = (long)((double)Util.getMillis() * (Double)Minecraft.getInstance().options.glintSpeed().get() * 8.0);
      float layerOffset0 = (float)(millis % 110000L) / 110000.0F;
      float layerOffset1 = (float)(millis % 30000L) / 30000.0F;
      Matrix4f matrix = (new Matrix4f()).translation(-layerOffset0, layerOffset1, 0.0F);
      matrix.rotateZ(0.17453292F).scale(scale);
      return matrix;
   }

   public static final class OffsetTextureTransform extends TextureTransform {
      public OffsetTextureTransform(final float uOffset, final float vOffset) {
         super("offset_texturing", () -> (new Matrix4f()).translation(uOffset, vOffset, 0.0F));
      }
   }
}
