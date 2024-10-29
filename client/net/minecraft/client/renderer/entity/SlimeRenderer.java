package net.minecraft.client.renderer.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.SlimeModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.layers.SlimeOuterLayer;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.client.renderer.entity.state.SlimeRenderState;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.monster.Slime;

public class SlimeRenderer extends MobRenderer<Slime, SlimeRenderState, SlimeModel> {
   public static final ResourceLocation SLIME_LOCATION = ResourceLocation.withDefaultNamespace("textures/entity/slime/slime.png");

   public SlimeRenderer(EntityRendererProvider.Context var1) {
      super(var1, new SlimeModel(var1.bakeLayer(ModelLayers.SLIME)), 0.25F);
      this.addLayer(new SlimeOuterLayer(this, var1.getModelSet()));
   }

   public void render(SlimeRenderState var1, PoseStack var2, MultiBufferSource var3, int var4) {
      this.shadowRadius = 0.25F * (float)var1.size;
      super.render(var1, var2, var3, var4);
   }

   protected void scale(SlimeRenderState var1, PoseStack var2) {
      float var3 = 0.999F;
      var2.scale(0.999F, 0.999F, 0.999F);
      var2.translate(0.0F, 0.001F, 0.0F);
      float var4 = (float)var1.size;
      float var5 = var1.squish / (var4 * 0.5F + 1.0F);
      float var6 = 1.0F / (var5 + 1.0F);
      var2.scale(var6 * var4, 1.0F / var6 * var4, var6 * var4);
   }

   public ResourceLocation getTextureLocation(SlimeRenderState var1) {
      return SLIME_LOCATION;
   }

   public SlimeRenderState createRenderState() {
      return new SlimeRenderState();
   }

   public void extractRenderState(Slime var1, SlimeRenderState var2, float var3) {
      super.extractRenderState(var1, var2, var3);
      var2.squish = Mth.lerp(var3, var1.oSquish, var1.squish);
      var2.size = var1.getSize();
   }

   // $FF: synthetic method
   public ResourceLocation getTextureLocation(final LivingEntityRenderState var1) {
      return this.getTextureLocation((SlimeRenderState)var1);
   }

   // $FF: synthetic method
   public EntityRenderState createRenderState() {
      return this.createRenderState();
   }
}
