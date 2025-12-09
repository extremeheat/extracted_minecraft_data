package net.minecraft.client.gui.font;

import com.mojang.blaze3d.vertex.VertexConsumer;
import org.joml.Matrix4f;

public interface PlainTextRenderable extends TextRenderable.Styled {
   float DEFAULT_WIDTH = 8.0F;
   float DEFAULT_HEIGHT = 8.0F;
   float DEFUAULT_ASCENT = 8.0F;

   default void render(Matrix4f var1, VertexConsumer var2, int var3, boolean var4) {
      float var5 = 0.0F;
      if (this.shadowColor() != 0) {
         this.renderSprite(var1, var2, var3, this.shadowOffset(), this.shadowOffset(), 0.0F, this.shadowColor());
         if (!var4) {
            var5 += 0.03F;
         }
      }

      this.renderSprite(var1, var2, var3, 0.0F, 0.0F, var5, this.color());
   }

   void renderSprite(Matrix4f var1, VertexConsumer var2, int var3, float var4, float var5, float var6, int var7);

   float x();

   float y();

   int color();

   int shadowColor();

   float shadowOffset();

   default float width() {
      return 8.0F;
   }

   default float height() {
      return 8.0F;
   }

   default float ascent() {
      return 8.0F;
   }

   default float left() {
      return this.x();
   }

   default float right() {
      return this.left() + this.width();
   }

   default float top() {
      return this.y() + 7.0F - this.ascent();
   }

   default float bottom() {
      return this.activeTop() + this.height();
   }
}
