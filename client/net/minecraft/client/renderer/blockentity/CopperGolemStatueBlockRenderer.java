package net.minecraft.client.renderer.blockentity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import java.util.HashMap;
import java.util.Map;
import net.minecraft.client.model.CopperGolemModel;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.animal.coppergolem.CopperGolemOxidationLevels;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.CopperGolemStatueBlock;
import net.minecraft.world.level.block.entity.CopperGolemStatueBlockEntity;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.phys.Vec3;

public class CopperGolemStatueBlockRenderer implements BlockEntityRenderer<CopperGolemStatueBlockEntity> {
   private final Map<CopperGolemStatueBlock.Pose, CopperGolemModel> models = new HashMap();

   public CopperGolemStatueBlockRenderer(BlockEntityRendererProvider.Context var1) {
      super();
      EntityModelSet var2 = var1.entityModelSet();
      this.models.put(CopperGolemStatueBlock.Pose.STANDING, new CopperGolemModel(var2.bakeLayer(ModelLayers.COPPER_GOLEM)));
      this.models.put(CopperGolemStatueBlock.Pose.RUNNING, new CopperGolemModel(var2.bakeLayer(ModelLayers.COPPER_GOLEM_RUNNING)));
      this.models.put(CopperGolemStatueBlock.Pose.SITTING, new CopperGolemModel(var2.bakeLayer(ModelLayers.COPPER_GOLEM_SITTING)));
      this.models.put(CopperGolemStatueBlock.Pose.STAR, new CopperGolemModel(var2.bakeLayer(ModelLayers.COPPER_GOLEM_STAR)));
   }

   public void render(CopperGolemStatueBlockEntity var1, float var2, PoseStack var3, MultiBufferSource var4, int var5, int var6, Vec3 var7) {
      Block var9 = var1.getBlockState().getBlock();
      if (var9 instanceof CopperGolemStatueBlock var8) {
         var3.pushPose();
         var3.translate(0.5F, 0.0F, 0.5F);
         CopperGolemModel var11 = (CopperGolemModel)this.models.get(var1.getBlockState().getValue(BlockStateProperties.COPPER_GOLEM_POSE));
         var11.poseStatue((Direction)var1.getBlockState().getValue(CopperGolemStatueBlock.FACING));
         VertexConsumer var10 = var4.getBuffer(RenderType.entityCutoutNoCull(CopperGolemOxidationLevels.getOxidationLevel(var8.getWeatheringState()).texture()));
         var11.renderToBuffer(var3, var10, var5, OverlayTexture.NO_OVERLAY);
         var3.popPose();
      }

   }
}
