package net.minecraft.client.gui.fonts;

import javax.annotation.Nullable;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.util.ResourceLocation;

public class EmptyGlyph extends TexturedGlyph {
   public EmptyGlyph() {
      super(new ResourceLocation(""), 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F);
   }

   public void func_211234_a(TextureManager var1, boolean var2, float var3, float var4, BufferBuilder var5, float var6, float var7, float var8, float var9) {
   }

   @Nullable
   public ResourceLocation func_211233_b() {
      return null;
   }
}
