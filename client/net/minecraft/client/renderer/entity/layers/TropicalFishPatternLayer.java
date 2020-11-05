package net.minecraft.client.renderer.entity.layers;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.TropicalFishModelA;
import net.minecraft.client.model.TropicalFishModelB;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.world.entity.animal.TropicalFish;

public class TropicalFishPatternLayer extends RenderLayer<TropicalFish, EntityModel<TropicalFish>> {
   private final TropicalFishModelA<TropicalFish> modelA = new TropicalFishModelA(0.008F);
   private final TropicalFishModelB<TropicalFish> modelB = new TropicalFishModelB(0.008F);

   public TropicalFishPatternLayer(RenderLayerParent<TropicalFish, EntityModel<TropicalFish>> var1) {
      super(var1);
   }

   public void render(PoseStack var1, MultiBufferSource var2, int var3, TropicalFish var4, float var5, float var6, float var7, float var8, float var9, float var10) {
      Object var11 = var4.getBaseVariant() == 0 ? this.modelA : this.modelB;
      float[] var12 = var4.getPatternColor();
      coloredCutoutModelCopyLayerRender(this.getParentModel(), (EntityModel)var11, var4.getPatternTextureLocation(), var1, var2, var3, var4, var5, var6, var8, var9, var10, var7, var12[0], var12[1], var12[2]);
   }
}
