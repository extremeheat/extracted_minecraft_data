package net.minecraft.client.renderer.entity.layers;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.ColorableHierarchicalModel;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.TropicalFishModelA;
import net.minecraft.client.model.TropicalFishModelB;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.world.entity.animal.TropicalFish;

public class TropicalFishPatternLayer extends RenderLayer<TropicalFish, ColorableHierarchicalModel<TropicalFish>> {
   private final TropicalFishModelA<TropicalFish> modelA;
   private final TropicalFishModelB<TropicalFish> modelB;

   public TropicalFishPatternLayer(RenderLayerParent<TropicalFish, ColorableHierarchicalModel<TropicalFish>> var1, EntityModelSet var2) {
      super(var1);
      this.modelA = new TropicalFishModelA(var2.bakeLayer(ModelLayers.TROPICAL_FISH_SMALL_PATTERN));
      this.modelB = new TropicalFishModelB(var2.bakeLayer(ModelLayers.TROPICAL_FISH_LARGE_PATTERN));
   }

   public void render(PoseStack var1, MultiBufferSource var2, int var3, TropicalFish var4, float var5, float var6, float var7, float var8, float var9, float var10) {
      Object var11 = var4.getBaseVariant() == 0 ? this.modelA : this.modelB;
      float[] var12 = var4.getPatternColor();
      coloredCutoutModelCopyLayerRender(this.getParentModel(), (EntityModel)var11, var4.getPatternTextureLocation(), var1, var2, var3, var4, var5, var6, var8, var9, var10, var7, var12[0], var12[1], var12[2]);
   }
}
