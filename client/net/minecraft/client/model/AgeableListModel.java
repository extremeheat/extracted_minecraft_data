package net.minecraft.client.model;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import java.util.function.Function;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;

public abstract class AgeableListModel<E extends Entity> extends EntityModel<E> {
   private final boolean scaleHead;
   private final float babyYHeadOffset;
   private final float babyZHeadOffset;
   private final float babyHeadScale;
   private final float babyBodyScale;
   private final float bodyYOffset;

   protected AgeableListModel(boolean var1, float var2, float var3) {
      this(var1, var2, var3, 2.0F, 2.0F, 24.0F);
   }

   protected AgeableListModel(boolean var1, float var2, float var3, float var4, float var5, float var6) {
      this(RenderType::entityCutoutNoCull, var1, var2, var3, var4, var5, var6);
   }

   protected AgeableListModel(Function<ResourceLocation, RenderType> var1, boolean var2, float var3, float var4, float var5, float var6, float var7) {
      super(var1);
      this.scaleHead = var2;
      this.babyYHeadOffset = var3;
      this.babyZHeadOffset = var4;
      this.babyHeadScale = var5;
      this.babyBodyScale = var6;
      this.bodyYOffset = var7;
   }

   protected AgeableListModel() {
      this(false, 5.0F, 2.0F);
   }

   public void renderToBuffer(PoseStack var1, VertexConsumer var2, int var3, int var4, float var5, float var6, float var7, float var8) {
      if (this.young) {
         var1.pushPose();
         float var9;
         if (this.scaleHead) {
            var9 = 1.5F / this.babyHeadScale;
            var1.scale(var9, var9, var9);
         }

         var1.translate(0.0D, (double)(this.babyYHeadOffset / 16.0F), (double)(this.babyZHeadOffset / 16.0F));
         this.headParts().forEach((var8x) -> {
            var8x.render(var1, var2, var3, var4, var5, var6, var7, var8);
         });
         var1.popPose();
         var1.pushPose();
         var9 = 1.0F / this.babyBodyScale;
         var1.scale(var9, var9, var9);
         var1.translate(0.0D, (double)(this.bodyYOffset / 16.0F), 0.0D);
         this.bodyParts().forEach((var8x) -> {
            var8x.render(var1, var2, var3, var4, var5, var6, var7, var8);
         });
         var1.popPose();
      } else {
         this.headParts().forEach((var8x) -> {
            var8x.render(var1, var2, var3, var4, var5, var6, var7, var8);
         });
         this.bodyParts().forEach((var8x) -> {
            var8x.render(var1, var2, var3, var4, var5, var6, var7, var8);
         });
      }

   }

   protected abstract Iterable<ModelPart> headParts();

   protected abstract Iterable<ModelPart> bodyParts();
}
