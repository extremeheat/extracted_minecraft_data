package net.minecraft.client.model;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import java.util.function.Function;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;

public abstract class Model {
   protected final Function<ResourceLocation, RenderType> renderType;

   public Model(Function<ResourceLocation, RenderType> var1) {
      super();
      this.renderType = var1;
   }

   public final RenderType renderType(ResourceLocation var1) {
      return (RenderType)this.renderType.apply(var1);
   }

   public abstract void renderToBuffer(PoseStack var1, VertexConsumer var2, int var3, int var4, int var5);

   public final void renderToBuffer(PoseStack var1, VertexConsumer var2, int var3, int var4) {
      this.renderToBuffer(var1, var2, var3, var4, -1);
   }
}
