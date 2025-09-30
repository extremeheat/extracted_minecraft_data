package net.minecraft.client.gui.font;

import com.mojang.blaze3d.vertex.VertexConsumer;
import org.joml.Matrix4f;

public interface PlainTextRenderable extends TextRenderable {
   default void render(Matrix4f var1, VertexConsumer var2, int var3, boolean var4) {
      float var5 = 0.0F;
      if (this.shadowColor() != 0) {
         this.renderSprite(var1, var2, var3, this.x() + this.shadowOffset(), this.y() + this.shadowOffset(), 0.0F, this.shadowColor());
         if (!var4) {
            var5 += 0.03F;
         }
      }

      this.renderSprite(var1, var2, var3, this.x(), this.y(), var5, this.color());
   }

   void renderSprite(Matrix4f var1, VertexConsumer var2, int var3, float var4, float var5, float var6, int var7);

   float x();

   float y();

   int color();

   int shadowColor();

   float shadowOffset();
}
