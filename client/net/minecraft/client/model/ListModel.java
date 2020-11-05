package net.minecraft.client.model;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import java.util.function.Function;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;

public abstract class ListModel<E extends Entity> extends EntityModel<E> {
   public ListModel() {
      this(RenderType::entityCutoutNoCull);
   }

   public ListModel(Function<ResourceLocation, RenderType> var1) {
      super(var1);
   }

   public void renderToBuffer(PoseStack var1, VertexConsumer var2, int var3, int var4, float var5, float var6, float var7, float var8) {
      this.parts().forEach((var8x) -> {
         var8x.render(var1, var2, var3, var4, var5, var6, var7, var8);
      });
   }

   public abstract Iterable<ModelPart> parts();
}
