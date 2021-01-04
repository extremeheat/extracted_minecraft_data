package net.minecraft.client.renderer.block;

import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.Tesselator;
import java.util.Random;
import net.minecraft.CrashReport;
import net.minecraft.CrashReportCategory;
import net.minecraft.ReportedException;
import net.minecraft.client.color.block.BlockColors;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.SimpleBakedModel;
import net.minecraft.core.BlockPos;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.ResourceManagerReloadListener;
import net.minecraft.world.level.BlockAndBiomeGetter;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;

public class BlockRenderDispatcher implements ResourceManagerReloadListener {
   private final BlockModelShaper blockModelShaper;
   private final ModelBlockRenderer modelRenderer;
   private final AnimatedEntityBlockRenderer entityBlockRenderer = new AnimatedEntityBlockRenderer();
   private final LiquidBlockRenderer liquidBlockRenderer;
   private final Random random = new Random();

   public BlockRenderDispatcher(BlockModelShaper var1, BlockColors var2) {
      super();
      this.blockModelShaper = var1;
      this.modelRenderer = new ModelBlockRenderer(var2);
      this.liquidBlockRenderer = new LiquidBlockRenderer();
   }

   public BlockModelShaper getBlockModelShaper() {
      return this.blockModelShaper;
   }

   public void renderBreakingTexture(BlockState var1, BlockPos var2, TextureAtlasSprite var3, BlockAndBiomeGetter var4) {
      if (var1.getRenderShape() == RenderShape.MODEL) {
         BakedModel var5 = this.blockModelShaper.getBlockModel(var1);
         long var6 = var1.getSeed(var2);
         BakedModel var8 = (new SimpleBakedModel.Builder(var1, var5, var3, this.random, var6)).build();
         this.modelRenderer.tesselateBlock(var4, var8, var1, var2, Tesselator.getInstance().getBuilder(), true, this.random, var6);
      }
   }

   public boolean renderBatched(BlockState var1, BlockPos var2, BlockAndBiomeGetter var3, BufferBuilder var4, Random var5) {
      try {
         RenderShape var6 = var1.getRenderShape();
         if (var6 == RenderShape.INVISIBLE) {
            return false;
         } else {
            switch(var6) {
            case MODEL:
               return this.modelRenderer.tesselateBlock(var3, this.getBlockModel(var1), var1, var2, var4, true, var5, var1.getSeed(var2));
            case ENTITYBLOCK_ANIMATED:
               return false;
            default:
               return false;
            }
         }
      } catch (Throwable var9) {
         CrashReport var7 = CrashReport.forThrowable(var9, "Tesselating block in world");
         CrashReportCategory var8 = var7.addCategory("Block being tesselated");
         CrashReportCategory.populateBlockDetails(var8, var2, var1);
         throw new ReportedException(var7);
      }
   }

   public boolean renderLiquid(BlockPos var1, BlockAndBiomeGetter var2, BufferBuilder var3, FluidState var4) {
      try {
         return this.liquidBlockRenderer.tesselate(var2, var1, var3, var4);
      } catch (Throwable var8) {
         CrashReport var6 = CrashReport.forThrowable(var8, "Tesselating liquid in world");
         CrashReportCategory var7 = var6.addCategory("Block being tesselated");
         CrashReportCategory.populateBlockDetails(var7, var1, (BlockState)null);
         throw new ReportedException(var6);
      }
   }

   public ModelBlockRenderer getModelRenderer() {
      return this.modelRenderer;
   }

   public BakedModel getBlockModel(BlockState var1) {
      return this.blockModelShaper.getBlockModel(var1);
   }

   public void renderSingleBlock(BlockState var1, float var2) {
      RenderShape var3 = var1.getRenderShape();
      if (var3 != RenderShape.INVISIBLE) {
         switch(var3) {
         case MODEL:
            BakedModel var4 = this.getBlockModel(var1);
            this.modelRenderer.renderSingleBlock(var4, var1, var2, true);
            break;
         case ENTITYBLOCK_ANIMATED:
            this.entityBlockRenderer.renderSingleBlock(var1.getBlock(), var2);
         }

      }
   }

   public void onResourceManagerReload(ResourceManager var1) {
      this.liquidBlockRenderer.setupSprites();
   }
}
