package net.minecraft.client.renderer.entity.layers;

import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.TropicalFishModelA;
import net.minecraft.client.model.TropicalFishModelB;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.world.entity.animal.TropicalFish;

public class TropicalFishPatternLayer extends RenderLayer<TropicalFish, EntityModel<TropicalFish>> {
   private final TropicalFishModelA<TropicalFish> modelA = new TropicalFishModelA(0.008F);
   private final TropicalFishModelB<TropicalFish> modelB = new TropicalFishModelB(0.008F);

   public TropicalFishPatternLayer(RenderLayerParent<TropicalFish, EntityModel<TropicalFish>> var1) {
      super(var1);
   }

   public void render(TropicalFish var1, float var2, float var3, float var4, float var5, float var6, float var7, float var8) {
      if (!var1.isInvisible()) {
         Object var9 = var1.getBaseVariant() == 0 ? this.modelA : this.modelB;
         this.bindTexture(var1.getPatternTextureLocation());
         float[] var10 = var1.getPatternColor();
         GlStateManager.color3f(var10[0], var10[1], var10[2]);
         this.getParentModel().copyPropertiesTo((EntityModel)var9);
         ((EntityModel)var9).prepareMobModel(var1, var2, var3, var4);
         ((EntityModel)var9).render(var1, var2, var3, var5, var6, var7, var8);
      }
   }

   public boolean colorsOnDamage() {
      return true;
   }
}
