package net.minecraft.client.renderer.blockentity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import it.unimi.dsi.fastutil.floats.Float2FloatFunction;
import it.unimi.dsi.fastutil.ints.Int2IntFunction;
import javax.annotation.Nullable;
import net.minecraft.client.model.ChestModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.client.renderer.blockentity.state.ChestRenderState;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.state.CameraRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.Material;
import net.minecraft.client.resources.model.MaterialSet;
import net.minecraft.core.Direction;
import net.minecraft.util.SpecialDates;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.ChestBlock;
import net.minecraft.world.level.block.CopperChestBlock;
import net.minecraft.world.level.block.DoubleBlockCombiner;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.EnderChestBlockEntity;
import net.minecraft.world.level.block.entity.LidBlockEntity;
import net.minecraft.world.level.block.entity.TrappedChestBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.ChestType;
import net.minecraft.world.phys.Vec3;
import org.joml.Quaternionfc;

public class ChestRenderer<T extends BlockEntity & LidBlockEntity> implements BlockEntityRenderer<T, ChestRenderState> {
   private final MaterialSet materials;
   private final ChestModel singleModel;
   private final ChestModel doubleLeftModel;
   private final ChestModel doubleRightModel;
   private final boolean xmasTextures;

   public ChestRenderer(BlockEntityRendererProvider.Context var1) {
      super();
      this.materials = var1.materials();
      this.xmasTextures = xmasTextures();
      this.singleModel = new ChestModel(var1.bakeLayer(ModelLayers.CHEST));
      this.doubleLeftModel = new ChestModel(var1.bakeLayer(ModelLayers.DOUBLE_CHEST_LEFT));
      this.doubleRightModel = new ChestModel(var1.bakeLayer(ModelLayers.DOUBLE_CHEST_RIGHT));
   }

   public static boolean xmasTextures() {
      return SpecialDates.isExtendedChrismas();
   }

   public ChestRenderState createRenderState() {
      return new ChestRenderState();
   }

   public void extractRenderState(T var1, ChestRenderState var2, float var3, Vec3 var4, @Nullable ModelFeatureRenderer.CrumblingOverlay var5) {
      DoubleBlockCombiner.NeighborCombineResult var8;
      label30: {
         BlockEntityRenderer.super.extractRenderState(var1, var2, var3, var4, var5);
         boolean var6 = var1.getLevel() != null;
         BlockState var7 = var6 ? var1.getBlockState() : (BlockState)Blocks.CHEST.defaultBlockState().setValue(ChestBlock.FACING, Direction.SOUTH);
         var2.type = var7.hasProperty(ChestBlock.TYPE) ? (ChestType)var7.getValue(ChestBlock.TYPE) : ChestType.SINGLE;
         var2.angle = ((Direction)var7.getValue(ChestBlock.FACING)).toYRot();
         var2.material = this.getChestMaterial(var1, this.xmasTextures);
         if (var6) {
            Block var10 = var7.getBlock();
            if (var10 instanceof ChestBlock) {
               ChestBlock var9 = (ChestBlock)var10;
               var8 = var9.combine(var7, var1.getLevel(), var1.getBlockPos(), true);
               break label30;
            }
         }

         var8 = DoubleBlockCombiner.Combiner::acceptNone;
      }

      var2.open = ((Float2FloatFunction)var8.apply(ChestBlock.opennessCombiner((LidBlockEntity)var1))).get(var3);
      if (var2.type != ChestType.SINGLE) {
         var2.lightCoords = ((Int2IntFunction)var8.apply(new BrightnessCombiner())).applyAsInt(var2.lightCoords);
      }

   }

   public void submit(ChestRenderState var1, PoseStack var2, SubmitNodeCollector var3, CameraRenderState var4) {
      var2.pushPose();
      var2.translate(0.5F, 0.5F, 0.5F);
      var2.mulPose((Quaternionfc)Axis.YP.rotationDegrees(-var1.angle));
      var2.translate(-0.5F, -0.5F, -0.5F);
      float var5 = var1.open;
      var5 = 1.0F - var5;
      var5 = 1.0F - var5 * var5 * var5;
      Material var6 = Sheets.chooseMaterial(var1.material, var1.type);
      RenderType var7 = var6.renderType(RenderTypes::entityCutout);
      TextureAtlasSprite var8 = this.materials.get(var6);
      if (var1.type != ChestType.SINGLE) {
         if (var1.type == ChestType.LEFT) {
            var3.submitModel(this.doubleLeftModel, var5, var2, var7, var1.lightCoords, OverlayTexture.NO_OVERLAY, -1, var8, 0, var1.breakProgress);
         } else {
            var3.submitModel(this.doubleRightModel, var5, var2, var7, var1.lightCoords, OverlayTexture.NO_OVERLAY, -1, var8, 0, var1.breakProgress);
         }
      } else {
         var3.submitModel(this.singleModel, var5, var2, var7, var1.lightCoords, OverlayTexture.NO_OVERLAY, -1, var8, 0, var1.breakProgress);
      }

      var2.popPose();
   }

   private ChestRenderState.ChestMaterialType getChestMaterial(BlockEntity var1, boolean var2) {
      if (var1 instanceof EnderChestBlockEntity) {
         return ChestRenderState.ChestMaterialType.ENDER_CHEST;
      } else if (var2) {
         return ChestRenderState.ChestMaterialType.CHRISTMAS;
      } else if (var1 instanceof TrappedChestBlockEntity) {
         return ChestRenderState.ChestMaterialType.TRAPPED;
      } else {
         Block var4 = var1.getBlockState().getBlock();
         if (var4 instanceof CopperChestBlock) {
            CopperChestBlock var3 = (CopperChestBlock)var4;
            ChestRenderState.ChestMaterialType var10000;
            switch (var3.getState()) {
               case UNAFFECTED -> var10000 = ChestRenderState.ChestMaterialType.COPPER_UNAFFECTED;
               case EXPOSED -> var10000 = ChestRenderState.ChestMaterialType.COPPER_EXPOSED;
               case WEATHERED -> var10000 = ChestRenderState.ChestMaterialType.COPPER_WEATHERED;
               case OXIDIZED -> var10000 = ChestRenderState.ChestMaterialType.COPPER_OXIDIZED;
               default -> throw new MatchException((String)null, (Throwable)null);
            }

            return var10000;
         } else {
            return ChestRenderState.ChestMaterialType.REGULAR;
         }
      }
   }

   // $FF: synthetic method
   public BlockEntityRenderState createRenderState() {
      return this.createRenderState();
   }
}
