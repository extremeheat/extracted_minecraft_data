package net.minecraft.client.renderer.blockentity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import it.unimi.dsi.fastutil.floats.Float2FloatFunction;
import it.unimi.dsi.fastutil.ints.Int2IntFunction;
import java.util.Calendar;
import javax.annotation.Nullable;
import net.minecraft.client.model.ChestModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.Material;
import net.minecraft.client.resources.model.MaterialSet;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.AbstractChestBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.ChestBlock;
import net.minecraft.world.level.block.DoubleBlockCombiner;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.LidBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.ChestType;
import net.minecraft.world.phys.Vec3;
import org.joml.Quaternionfc;

public class ChestRenderer<T extends BlockEntity & LidBlockEntity> implements BlockEntityRenderer<T> {
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
      Calendar var0 = Calendar.getInstance();
      return var0.get(2) + 1 == 12 && var0.get(5) >= 24 && var0.get(5) <= 26;
   }

   public void submit(T var1, float var2, PoseStack var3, int var4, int var5, Vec3 var6, @Nullable ModelFeatureRenderer.CrumblingOverlay var7, SubmitNodeCollector var8) {
      Level var9 = var1.getLevel();
      boolean var10 = var9 != null;
      BlockState var11 = var10 ? var1.getBlockState() : (BlockState)Blocks.CHEST.defaultBlockState().setValue(ChestBlock.FACING, Direction.SOUTH);
      ChestType var12 = var11.hasProperty(ChestBlock.TYPE) ? (ChestType)var11.getValue(ChestBlock.TYPE) : ChestType.SINGLE;
      Block var13 = var11.getBlock();
      if (var13 instanceof AbstractChestBlock var14) {
         boolean var15 = var12 != ChestType.SINGLE;
         var3.pushPose();
         float var16 = ((Direction)var11.getValue(ChestBlock.FACING)).toYRot();
         var3.translate(0.5F, 0.5F, 0.5F);
         var3.mulPose((Quaternionfc)Axis.YP.rotationDegrees(-var16));
         var3.translate(-0.5F, -0.5F, -0.5F);
         DoubleBlockCombiner.NeighborCombineResult var17;
         if (var10) {
            var17 = var14.combine(var11, var9, var1.getBlockPos(), true);
         } else {
            var17 = DoubleBlockCombiner.Combiner::acceptNone;
         }

         float var18 = ((Float2FloatFunction)var17.apply(ChestBlock.opennessCombiner((LidBlockEntity)var1))).get(var2);
         var18 = 1.0F - var18;
         var18 = 1.0F - var18 * var18 * var18;
         int var19 = ((Int2IntFunction)var17.apply(new BrightnessCombiner())).applyAsInt(var4);
         Material var20 = Sheets.chooseMaterial(var1, var12, this.xmasTextures);
         RenderType var21 = var20.renderType(RenderType::entityCutout);
         TextureAtlasSprite var22 = this.materials.get(var20);
         if (var15) {
            if (var12 == ChestType.LEFT) {
               var8.submitModel(this.doubleLeftModel, var18, var3, var21, var4, var5, -1, var22, 0, var7);
            } else {
               var8.submitModel(this.doubleRightModel, var18, var3, var21, var4, var5, -1, var22, 0, var7);
            }
         } else {
            var8.submitModel(this.singleModel, var18, var3, var21, var19, var5, -1, var22, 0, var7);
         }

         var3.popPose();
      }
   }
}
