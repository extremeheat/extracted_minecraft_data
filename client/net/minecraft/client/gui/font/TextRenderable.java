package net.minecraft.client.gui.font;

import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.textures.GpuTextureView;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.rendertype.RenderType;
import org.joml.Matrix4f;

public interface TextRenderable {
   void render(Matrix4f var1, VertexConsumer var2, int var3, boolean var4);

   RenderType renderType(Font.DisplayMode var1);

   GpuTextureView textureView();

   RenderPipeline guiPipeline();

   float left();

   float top();

   float right();

   float bottom();

   public interface Styled extends ActiveArea, TextRenderable {
      default float activeLeft() {
         return this.left();
      }

      default float activeTop() {
         return this.top();
      }

      default float activeRight() {
         return this.right();
      }

      default float activeBottom() {
         return this.bottom();
      }
   }
}
