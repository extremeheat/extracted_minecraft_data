package net.minecraft.client.renderer.entity.layers;

import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.SlimeModel;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.world.entity.Entity;

public class SlimeOuterLayer<T extends Entity> extends RenderLayer<T, SlimeModel<T>> {
   private final EntityModel<T> model = new SlimeModel(0);

   public SlimeOuterLayer(RenderLayerParent<T, SlimeModel<T>> var1) {
      super(var1);
   }

   public void render(T var1, float var2, float var3, float var4, float var5, float var6, float var7, float var8) {
      if (!var1.isInvisible()) {
         GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
         GlStateManager.enableNormalize();
         GlStateManager.enableBlend();
         GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
         ((SlimeModel)this.getParentModel()).copyPropertiesTo(this.model);
         this.model.render(var1, var2, var3, var5, var6, var7, var8);
         GlStateManager.disableBlend();
         GlStateManager.disableNormalize();
      }
   }

   public boolean colorsOnDamage() {
      return true;
   }
}
