package net.minecraft.client.model;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import java.util.function.Function;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;

public abstract class AgeableHierarchicalModel<E extends Entity> extends HierarchicalModel<E> {
   private final float youngScaleFactor;
   private final float bodyYOffset;

   public AgeableHierarchicalModel(float var1, float var2) {
      this(var1, var2, RenderType::entityCutoutNoCull);
   }

   public AgeableHierarchicalModel(float var1, float var2, Function<ResourceLocation, RenderType> var3) {
      super(var3);
      this.bodyYOffset = var2;
      this.youngScaleFactor = var1;
   }

   public void renderToBuffer(PoseStack var1, VertexConsumer var2, int var3, int var4, int var5) {
      if (this.young) {
         var1.pushPose();
         var1.scale(this.youngScaleFactor, this.youngScaleFactor, this.youngScaleFactor);
         var1.translate(0.0F, this.bodyYOffset / 16.0F, 0.0F);
         this.root().render(var1, var2, var3, var4, var5);
         var1.popPose();
      } else {
         this.root().render(var1, var2, var3, var4, var5);
      }

   }
}
