package net.minecraft.client.renderer.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.model.monster.slime.SulfurCubeModel;
import net.minecraft.client.renderer.block.BlockModelResolver;
import net.minecraft.client.renderer.block.model.BlockDisplayContext;
import net.minecraft.client.renderer.entity.layers.SulfurCubeInnerLayer;
import net.minecraft.client.renderer.entity.state.SulfurCubeRenderState;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.monster.cubemob.SulfurCube;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;

public class SulfurCubeRenderer extends AbstractCubeMobRenderer<SulfurCube, SulfurCubeRenderState, SulfurCubeModel> {
   private static final Identifier SULFUR_CUBE_LOCATION = Identifier.withDefaultNamespace("textures/entity/sulfur_cube/sulfur_cube_outer.png");
   public static final BlockDisplayContext BLOCK_DISPLAY_CONTEXT = BlockDisplayContext.create();
   private final BlockModelResolver blockModelResolver;

   public SulfurCubeRenderer(final EntityRendererProvider.Context context) {
      super(context, new SulfurCubeModel(context.bakeLayer(ModelLayers.SULFUR_CUBE)));
      this.blockModelResolver = context.getBlockModelResolver();
      this.addLayer(new SulfurCubeInnerLayer(this, context.getModelSet()));
   }

   protected void scale(final SulfurCubeRenderState state, final PoseStack poseStack) {
      super.scale(state, poseStack);
      this.downscaleSlightly(poseStack);
      poseStack.scale(0.5F, 0.5F, 0.5F);
      poseStack.translate(-0.0F, 0.98F, -0.0F);
   }

   public Identifier getTextureLocation(final SulfurCubeRenderState state) {
      return SULFUR_CUBE_LOCATION;
   }

   public SulfurCubeRenderState createRenderState() {
      return new SulfurCubeRenderState();
   }

   public void extractRenderState(final SulfurCube entity, final SulfurCubeRenderState state, final float partialTicks) {
      super.extractRenderState(entity, state, partialTicks);
      ItemStack containedBlock = entity.getBodyArmorItem();
      if (!containedBlock.isEmpty()) {
         this.blockModelResolver.update(state.containedBlock, Block.byItem(containedBlock.getItem()).defaultBlockState(), BLOCK_DISPLAY_CONTEXT);
      }

   }
}
