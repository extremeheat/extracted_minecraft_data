package net.minecraft.client.renderer.blockentity;

import com.mojang.blaze3d.vertex.PoseStack;
import java.util.HashMap;
import java.util.Map;
import javax.annotation.Nullable;
import net.minecraft.client.model.CopperGolemStatueModel;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.client.renderer.blockentity.state.CopperGolemStatueRenderState;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.state.CameraRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.animal.coppergolem.CopperGolemOxidationLevels;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.CopperGolemStatueBlock;
import net.minecraft.world.level.block.entity.CopperGolemStatueBlockEntity;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.phys.Vec3;

public class CopperGolemStatueBlockRenderer implements BlockEntityRenderer<CopperGolemStatueBlockEntity, CopperGolemStatueRenderState> {
   private final Map<CopperGolemStatueBlock.Pose, CopperGolemStatueModel> models = new HashMap();

   public CopperGolemStatueBlockRenderer(BlockEntityRendererProvider.Context var1) {
      super();
      EntityModelSet var2 = var1.entityModelSet();
      this.models.put(CopperGolemStatueBlock.Pose.STANDING, new CopperGolemStatueModel(var2.bakeLayer(ModelLayers.COPPER_GOLEM)));
      this.models.put(CopperGolemStatueBlock.Pose.RUNNING, new CopperGolemStatueModel(var2.bakeLayer(ModelLayers.COPPER_GOLEM_RUNNING)));
      this.models.put(CopperGolemStatueBlock.Pose.SITTING, new CopperGolemStatueModel(var2.bakeLayer(ModelLayers.COPPER_GOLEM_SITTING)));
      this.models.put(CopperGolemStatueBlock.Pose.STAR, new CopperGolemStatueModel(var2.bakeLayer(ModelLayers.COPPER_GOLEM_STAR)));
   }

   public CopperGolemStatueRenderState createRenderState() {
      return new CopperGolemStatueRenderState();
   }

   public void extractRenderState(CopperGolemStatueBlockEntity var1, CopperGolemStatueRenderState var2, float var3, Vec3 var4, @Nullable ModelFeatureRenderer.CrumblingOverlay var5) {
      BlockEntityRenderer.super.extractRenderState(var1, var2, var3, var4, var5);
      var2.direction = (Direction)var1.getBlockState().getValue(CopperGolemStatueBlock.FACING);
      var2.pose = (CopperGolemStatueBlock.Pose)var1.getBlockState().getValue(BlockStateProperties.COPPER_GOLEM_POSE);
   }

   public void submit(CopperGolemStatueRenderState var1, PoseStack var2, SubmitNodeCollector var3, CameraRenderState var4) {
      Block var6 = var1.blockState.getBlock();
      if (var6 instanceof CopperGolemStatueBlock var5) {
         var2.pushPose();
         var2.translate(0.5F, 0.0F, 0.5F);
         CopperGolemStatueModel var9 = (CopperGolemStatueModel)this.models.get(var1.pose);
         Direction var7 = var1.direction;
         RenderType var8 = RenderTypes.entityCutoutNoCull(CopperGolemOxidationLevels.getOxidationLevel(var5.getWeatheringState()).texture());
         var3.submitModel(var9, var7, var2, var8, var1.lightCoords, OverlayTexture.NO_OVERLAY, 0, var1.breakProgress);
         var2.popPose();
      }

   }

   // $FF: synthetic method
   public BlockEntityRenderState createRenderState() {
      return this.createRenderState();
   }
}
