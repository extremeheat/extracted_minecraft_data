package net.minecraft.client.renderer.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.LavaSlimeModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.client.renderer.entity.state.SlimeRenderState;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.monster.MagmaCube;

public class MagmaCubeRenderer extends MobRenderer<MagmaCube, SlimeRenderState, LavaSlimeModel> {
   private static final ResourceLocation MAGMACUBE_LOCATION = ResourceLocation.withDefaultNamespace("textures/entity/slime/magmacube.png");

   public MagmaCubeRenderer(EntityRendererProvider.Context var1) {
      super(var1, new LavaSlimeModel(var1.bakeLayer(ModelLayers.MAGMA_CUBE)), 0.25F);
   }

   protected int getBlockLightLevel(MagmaCube var1, BlockPos var2) {
      return 15;
   }

   public ResourceLocation getTextureLocation(SlimeRenderState var1) {
      return MAGMACUBE_LOCATION;
   }

   public SlimeRenderState createRenderState() {
      return new SlimeRenderState();
   }

   public void extractRenderState(MagmaCube var1, SlimeRenderState var2, float var3) {
      super.extractRenderState(var1, var2, var3);
      var2.squish = Mth.lerp(var3, var1.oSquish, var1.squish);
      var2.size = var1.getSize();
   }

   public void render(SlimeRenderState var1, PoseStack var2, MultiBufferSource var3, int var4) {
      this.shadowRadius = 0.25F * (float)var1.size;
      super.render(var1, var2, var3, var4);
   }

   protected void scale(SlimeRenderState var1, PoseStack var2) {
      int var3 = var1.size;
      float var4 = var1.squish / ((float)var3 * 0.5F + 1.0F);
      float var5 = 1.0F / (var4 + 1.0F);
      var2.scale(var5 * (float)var3, 1.0F / var5 * (float)var3, var5 * (float)var3);
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
