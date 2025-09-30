package net.minecraft.client.gui.render.state;

import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.vertex.VertexConsumer;
import javax.annotation.Nullable;
import net.minecraft.client.gui.navigation.ScreenRectangle;
import net.minecraft.client.gui.render.TextureSetup;

public interface GuiElementRenderState extends ScreenArea {
   void buildVertices(VertexConsumer var1);

   RenderPipeline pipeline();

   TextureSetup textureSetup();

   @Nullable
   ScreenRectangle scissorArea();
}
