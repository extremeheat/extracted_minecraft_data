package net.minecraft.client.model;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import java.util.List;
import java.util.function.Function;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;

public abstract class Model {
   protected final ModelPart root;
   protected final Function<ResourceLocation, RenderType> renderType;
   private final List<ModelPart> allParts;

   public Model(ModelPart var1, Function<ResourceLocation, RenderType> var2) {
      super();
      this.root = var1;
      this.renderType = var2;
      this.allParts = var1.getAllParts();
   }

   public final RenderType renderType(ResourceLocation var1) {
      return (RenderType)this.renderType.apply(var1);
   }

   public final void renderToBuffer(PoseStack var1, VertexConsumer var2, int var3, int var4, int var5) {
      this.root().render(var1, var2, var3, var4, var5);
   }

   public final void renderToBuffer(PoseStack var1, VertexConsumer var2, int var3, int var4) {
      this.renderToBuffer(var1, var2, var3, var4, -1);
   }

   public final ModelPart root() {
      return this.root;
   }

   public final List<ModelPart> allParts() {
      return this.allParts;
   }

   public final void resetPose() {
      for(ModelPart var2 : this.allParts) {
         var2.resetPose();
      }

   }

   public static class Simple extends Model {
      public Simple(ModelPart var1, Function<ResourceLocation, RenderType> var2) {
         super(var1, var2);
      }
   }
}
