package net.minecraft.client.renderer.blockentity;

import com.mojang.blaze3d.vertex.PoseStack;
import java.util.HashMap;
import java.util.Map;
import javax.annotation.Nullable;
import net.minecraft.client.model.CopperGolemStatueModel;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.animal.coppergolem.CopperGolemOxidationLevels;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.CopperGolemStatueBlock;
import net.minecraft.world.level.block.entity.CopperGolemStatueBlockEntity;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.phys.Vec3;

public class CopperGolemStatueBlockRenderer implements BlockEntityRenderer<CopperGolemStatueBlockEntity> {
   private final Map<CopperGolemStatueBlock.Pose, CopperGolemStatueModel> models = new HashMap();

   public CopperGolemStatueBlockRenderer(BlockEntityRendererProvider.Context var1) {
      super();
      EntityModelSet var2 = var1.entityModelSet();
      this.models.put(CopperGolemStatueBlock.Pose.STANDING, new CopperGolemStatueModel(var2.bakeLayer(ModelLayers.COPPER_GOLEM)));
      this.models.put(CopperGolemStatueBlock.Pose.RUNNING, new CopperGolemStatueModel(var2.bakeLayer(ModelLayers.COPPER_GOLEM_RUNNING)));
      this.models.put(CopperGolemStatueBlock.Pose.SITTING, new CopperGolemStatueModel(var2.bakeLayer(ModelLayers.COPPER_GOLEM_SITTING)));
      this.models.put(CopperGolemStatueBlock.Pose.STAR, new CopperGolemStatueModel(var2.bakeLayer(ModelLayers.COPPER_GOLEM_STAR)));
   }

   public void submit(CopperGolemStatueBlockEntity var1, float var2, PoseStack var3, int var4, int var5, Vec3 var6, @Nullable ModelFeatureRenderer.CrumblingOverlay var7, SubmitNodeCollector var8) {
      Block var10 = var1.getBlockState().getBlock();
      if (var10 instanceof CopperGolemStatueBlock var9) {
         var3.pushPose();
         var3.translate(0.5F, 0.0F, 0.5F);
         CopperGolemStatueModel var13 = (CopperGolemStatueModel)this.models.get(var1.getBlockState().getValue(BlockStateProperties.COPPER_GOLEM_POSE));
         Direction var11 = (Direction)var1.getBlockState().getValue(CopperGolemStatueBlock.FACING);
         RenderType var12 = RenderType.entityCutoutNoCull(CopperGolemOxidationLevels.getOxidationLevel(var9.getWeatheringState()).texture());
         var8.submitModel(var13, var11, var3, var12, var4, OverlayTexture.NO_OVERLAY, 0, var7);
         var3.popPose();
      }

   }
}
