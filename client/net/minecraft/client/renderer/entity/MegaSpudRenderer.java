package net.minecraft.client.renderer.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.MegaSpudModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.layers.MegaSpudArmorLayer;
import net.minecraft.client.renderer.entity.layers.MegaSpudOuterLayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.monster.MegaSpud;

public class MegaSpudRenderer extends MobRenderer<MegaSpud, MegaSpudModel<MegaSpud>> {
   private static final ResourceLocation SLIME_LOCATION = new ResourceLocation("textures/entity/slime/mega_spud.png");

   public MegaSpudRenderer(EntityRendererProvider.Context var1) {
      super(var1, new MegaSpudModel<>(var1.bakeLayer(ModelLayers.MEGA_SPUD)), 0.25F);
      this.addLayer(new MegaSpudOuterLayer<>(this, var1.getModelSet()));
      this.addLayer(new MegaSpudArmorLayer(this, var1.getModelSet()));
   }

   public void render(MegaSpud var1, float var2, float var3, PoseStack var4, MultiBufferSource var5, int var6) {
      this.shadowRadius = 0.25F * (float)var1.getSize();
      super.render(var1, var2, var3, var4, var5, var6);
   }

   protected void scale(MegaSpud var1, PoseStack var2, float var3) {
      float var4 = 0.999F;
      var2.scale(0.999F, 0.999F, 0.999F);
      var2.translate(0.0F, 0.001F, 0.0F);
      float var5 = (float)var1.getSize();
      float var6 = Mth.lerp(var3, var1.oSquish, var1.squish) / (var5 * 0.5F + 1.0F);
      float var7 = 1.0F / (var6 + 1.0F);
      var2.scale(var7 * var5, 1.5F / var7 * var5, var7 * var5);
   }

   public ResourceLocation getTextureLocation(MegaSpud var1) {
      return SLIME_LOCATION;
   }
}
