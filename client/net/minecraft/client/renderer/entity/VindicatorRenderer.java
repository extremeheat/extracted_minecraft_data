package net.minecraft.client.renderer.entity;

import net.minecraft.client.model.IllagerModel;
import net.minecraft.client.renderer.entity.layers.ItemInHandLayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.Vindicator;

public class VindicatorRenderer extends IllagerRenderer<Vindicator> {
   private static final ResourceLocation VINDICATOR = new ResourceLocation("textures/entity/illager/vindicator.png");

   public VindicatorRenderer(EntityRenderDispatcher var1) {
      super(var1, new IllagerModel(0.0F, 0.0F, 64, 64), 0.5F);
      this.addLayer(new ItemInHandLayer<Vindicator, IllagerModel<Vindicator>>(this) {
         public void render(Vindicator var1, float var2, float var3, float var4, float var5, float var6, float var7, float var8) {
            if (var1.isAggressive()) {
               super.render((LivingEntity)var1, var2, var3, var4, var5, var6, var7, var8);
            }

         }
      });
   }

   protected ResourceLocation getTextureLocation(Vindicator var1) {
      return VINDICATOR;
   }
}
