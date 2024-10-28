package net.minecraft.client.renderer.entity.layers;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.SkeletonModel;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.monster.RangedAttackMob;

public class SkeletonClothingLayer<T extends Mob & RangedAttackMob, M extends EntityModel<T>> extends RenderLayer<T, M> {
   private final SkeletonModel<T> layerModel;
   private final ResourceLocation clothesLocation;

   public SkeletonClothingLayer(RenderLayerParent<T, M> var1, EntityModelSet var2, ModelLayerLocation var3, ResourceLocation var4) {
      super(var1);
      this.clothesLocation = var4;
      this.layerModel = new SkeletonModel(var2.bakeLayer(var3));
   }

   public void render(PoseStack var1, MultiBufferSource var2, int var3, T var4, float var5, float var6, float var7, float var8, float var9, float var10) {
      coloredCutoutModelCopyLayerRender(this.getParentModel(), this.layerModel, this.clothesLocation, var1, var2, var3, var4, var5, var6, var8, var9, var10, var7, -1);
   }
}
