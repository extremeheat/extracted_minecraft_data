package net.minecraft.client.renderer.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.model.monster.slime.SulfurCubeModel;
import net.minecraft.client.renderer.block.BlockModelResolver;
import net.minecraft.client.renderer.block.model.BlockDisplayContext;
import net.minecraft.client.renderer.entity.layers.SulfurCubeInnerLayer;
import net.minecraft.client.renderer.entity.state.SulfurCubeRenderState;
import net.minecraft.core.component.DataComponents;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.monster.cubemob.SulfurCube;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.BlockItemStateProperties;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

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
      this.downscaleSlightly(poseStack);
      super.scale(state, poseStack);
      float fuse = state.fuseRemainingTicks;
      if (fuse < 10.0F && fuse > 0.0F) {
         float s = 1.0F + TntRenderer.getSwellAmount(fuse);
         poseStack.scale(s, s, s);
      }

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
      state.fuseRemainingTicks = entity.isPrimed() ? (float)entity.getFuse() - partialTicks + 1.0F : 0.0F;
      ItemStack containedBlock = entity.getBodyArmorItem();
      if (!containedBlock.isEmpty()) {
         BlockItemStateProperties blockItemState = (BlockItemStateProperties)containedBlock.getOrDefault(DataComponents.BLOCK_STATE, BlockItemStateProperties.EMPTY);
         BlockState blockState = blockItemState.apply(Block.byItem(containedBlock.getItem()).defaultBlockState());
         this.blockModelResolver.update(state.containedBlock, blockState, BLOCK_DISPLAY_CONTEXT);
      }

   }
}
