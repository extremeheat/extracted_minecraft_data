package net.minecraft.client.renderer.entity;

import com.mojang.blaze3d.platform.GlStateManager;
import javax.annotation.Nullable;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.TropicalFishModelA;
import net.minecraft.client.model.TropicalFishModelB;
import net.minecraft.client.renderer.entity.layers.TropicalFishPatternLayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.animal.TropicalFish;

public class TropicalFishRenderer extends MobRenderer<TropicalFish, EntityModel<TropicalFish>> {
   private final TropicalFishModelA<TropicalFish> modelA = new TropicalFishModelA();
   private final TropicalFishModelB<TropicalFish> modelB = new TropicalFishModelB();

   public TropicalFishRenderer(EntityRenderDispatcher var1) {
      super(var1, new TropicalFishModelA(), 0.15F);
      this.addLayer(new TropicalFishPatternLayer(this));
   }

   @Nullable
   protected ResourceLocation getTextureLocation(TropicalFish var1) {
      return var1.getBaseTextureLocation();
   }

   public void render(TropicalFish var1, double var2, double var4, double var6, float var8, float var9) {
      this.model = (EntityModel)(var1.getBaseVariant() == 0 ? this.modelA : this.modelB);
      float[] var10 = var1.getBaseColor();
      GlStateManager.color3f(var10[0], var10[1], var10[2]);
      super.render((Mob)var1, var2, var4, var6, var8, var9);
   }

   protected void setupRotations(TropicalFish var1, float var2, float var3, float var4) {
      super.setupRotations(var1, var2, var3, var4);
      float var5 = 4.3F * Mth.sin(0.6F * var2);
      GlStateManager.rotatef(var5, 0.0F, 1.0F, 0.0F);
      if (!var1.isInWater()) {
         GlStateManager.translatef(0.2F, 0.1F, 0.0F);
         GlStateManager.rotatef(90.0F, 0.0F, 0.0F, 1.0F);
      }

   }
}
