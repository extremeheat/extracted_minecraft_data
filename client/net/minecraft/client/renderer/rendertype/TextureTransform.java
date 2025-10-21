package net.minecraft.client.renderer.rendertype;

import java.util.function.Supplier;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import org.joml.Matrix4f;

public class TextureTransform {
   public static final double MAX_ENCHANTMENT_GLINT_SPEED_MILLIS = 8.0;
   private final String name;
   private final Supplier<Matrix4f> supplier;
   public static final TextureTransform DEFAULT_TEXTURING = new TextureTransform("default_texturing", Matrix4f::new);
   public static final TextureTransform GLINT_TEXTURING = new TextureTransform("glint_texturing", () -> setupGlintTexturing(8.0F));
   public static final TextureTransform ENTITY_GLINT_TEXTURING = new TextureTransform("entity_glint_texturing", () -> setupGlintTexturing(0.5F));
   public static final TextureTransform ARMOR_ENTITY_GLINT_TEXTURING = new TextureTransform("armor_entity_glint_texturing", () -> setupGlintTexturing(0.16F));

   public TextureTransform(String var1, Supplier<Matrix4f> var2) {
      super();
      this.name = var1;
      this.supplier = var2;
   }

   public Matrix4f getMatrix() {
      return (Matrix4f)this.supplier.get();
   }

   public String toString() {
      return "TexturingStateShard[" + this.name + "]";
   }

   private static Matrix4f setupGlintTexturing(float var0) {
      long var1 = (long)((double)Util.getMillis() * (Double)Minecraft.getInstance().options.glintSpeed().get() * 8.0);
      float var3 = (float)(var1 % 110000L) / 110000.0F;
      float var4 = (float)(var1 % 30000L) / 30000.0F;
      Matrix4f var5 = (new Matrix4f()).translation(-var3, var4, 0.0F);
      var5.rotateZ(0.17453292F).scale(var0);
      return var5;
   }

   public static final class OffsetTextureTransform extends TextureTransform {
      public OffsetTextureTransform(float var1, float var2) {
         super("offset_texturing", () -> (new Matrix4f()).translation(var1, var2, 0.0F));
      }
   }
}
