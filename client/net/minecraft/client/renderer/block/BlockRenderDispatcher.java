package net.minecraft.client.renderer.block;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.QuadInstance;
import com.mojang.blaze3d.vertex.SheetedDecalTextureGenerator;
import com.mojang.blaze3d.vertex.VertexConsumer;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import net.minecraft.client.color.block.BlockColors;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.BlockModelPart;
import net.minecraft.client.renderer.block.model.BlockStateModel;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.resources.model.ModelBakery;
import net.minecraft.client.resources.model.ModelManager;
import net.minecraft.client.resources.model.SpriteGetter;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.ResourceManagerReloadListener;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.state.BlockState;
import org.jspecify.annotations.Nullable;

public class BlockRenderDispatcher implements ResourceManagerReloadListener {
   private static final Direction[] DIRECTIONS = Direction.values();
   private final ModelManager modelManager;
   private final SpriteGetter sprites;
   private @Nullable LiquidBlockRenderer liquidBlockRenderer;
   private final RandomSource singleThreadRandom = RandomSource.create();
   private final List<BlockModelPart> singleThreadPartList = new ArrayList();

   public BlockRenderDispatcher(final ModelManager modelManager, final SpriteGetter sprites, final BlockColors blockColors) {
      super();
      this.modelManager = modelManager;
      this.sprites = sprites;
   }

   public void renderBreakingTexture(final BlockState state, final BlockPos pos, final PoseStack poseStack, final MultiBufferSource bufferSource, final int progress) {
      if (state.getRenderShape() == RenderShape.MODEL) {
         PoseStack.Pose pose = poseStack.last();
         VertexConsumer buffer = new SheetedDecalTextureGenerator(bufferSource.getBuffer((RenderType)ModelBakery.DESTROY_TYPES.get(progress)), pose, 1.0F);
         BlockStateModel model = this.modelManager.getBlockModelSet().get(state);
         this.singleThreadRandom.setSeed(state.getSeed(pos));
         this.singleThreadPartList.clear();
         model.collectParts(this.singleThreadRandom, this.singleThreadPartList);
         QuadInstance instance = new QuadInstance();

         for(BlockModelPart part : this.singleThreadPartList) {
            for(Direction direction : DIRECTIONS) {
               for(BakedQuad quad : part.getQuads(direction)) {
                  buffer.putBakedQuad(pose, quad, instance);
               }
            }

            for(BakedQuad quad : part.getQuads((Direction)null)) {
               buffer.putBakedQuad(pose, quad, instance);
            }
         }

      }
   }

   public BlockStateModelSet getModelSet() {
      return this.modelManager.getBlockModelSet();
   }

   public LiquidBlockRenderer getLiquidRenderer() {
      return (LiquidBlockRenderer)Objects.requireNonNull(this.liquidBlockRenderer, "Liquid renderer not initialized");
   }

   public BlockStateModel getBlockModel(final BlockState state) {
      return this.modelManager.getBlockModelSet().get(state);
   }

   public void onResourceManagerReload(final ResourceManager resourceManager) {
      this.liquidBlockRenderer = new LiquidBlockRenderer(this.sprites);
   }
}
