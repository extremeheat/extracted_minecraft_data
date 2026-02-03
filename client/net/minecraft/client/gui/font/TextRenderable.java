package net.minecraft.client.gui.font;

import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.textures.GpuTextureView;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.rendertype.RenderType;
import org.joml.Matrix4f;

public interface TextRenderable {
   void render(Matrix4f pose, VertexConsumer buffer, int packedLightCoords, boolean flat);

   RenderType renderType(Font.DisplayMode displayMode);

   GpuTextureView textureView();

   RenderPipeline guiPipeline();

   float left();

   float top();

   float right();

   float bottom();

   public interface Styled extends TextRenderable, ActiveArea {
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
