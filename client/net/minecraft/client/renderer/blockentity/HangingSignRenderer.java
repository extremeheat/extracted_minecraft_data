package net.minecraft.client.renderer.blockentity;

import com.google.common.collect.ImmutableMap;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import java.util.Map;
import net.minecraft.client.model.Model;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.resources.model.Material;
import net.minecraft.world.level.block.CeilingHangingSignBlock;
import net.minecraft.world.level.block.SignBlock;
import net.minecraft.world.level.block.WallSignBlock;
import net.minecraft.world.level.block.entity.SignBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.RotationSegment;
import net.minecraft.world.level.block.state.properties.WoodType;
import net.minecraft.world.phys.Vec3;

public class HangingSignRenderer extends SignRenderer {
   private static final String PLANK = "plank";
   private static final String V_CHAINS = "vChains";
   public static final String NORMAL_CHAINS = "normalChains";
   public static final String CHAIN_L_1 = "chainL1";
   public static final String CHAIN_L_2 = "chainL2";
   public static final String CHAIN_R_1 = "chainR1";
   public static final String CHAIN_R_2 = "chainR2";
   public static final String BOARD = "board";
   private final Map<WoodType, HangingSignRenderer.HangingSignModel> hangingSignModels;

   public HangingSignRenderer(BlockEntityRendererProvider.Context var1) {
      super(var1);
      this.hangingSignModels = WoodType.values()
         .collect(
            ImmutableMap.toImmutableMap(
               var0 -> var0, var1x -> new HangingSignRenderer.HangingSignModel(var1.bakeLayer(ModelLayers.createHangingSignModelName(var1x)))
            )
         );
   }

   @Override
   public void render(SignBlockEntity var1, float var2, PoseStack var3, MultiBufferSource var4, int var5, int var6) {
      BlockState var7 = var1.getBlockState();
      var3.pushPose();
      WoodType var8 = SignBlock.getWoodType(var7.getBlock());
      HangingSignRenderer.HangingSignModel var9 = this.hangingSignModels.get(var8);
      boolean var10 = !(var7.getBlock() instanceof CeilingHangingSignBlock);
      boolean var11 = var7.hasProperty(BlockStateProperties.ATTACHED) && var7.getValue(BlockStateProperties.ATTACHED);
      var3.translate(0.5, 0.9375, 0.5);
      if (var11) {
         float var12 = -RotationSegment.convertToDegrees(var7.getValue(CeilingHangingSignBlock.ROTATION));
         var3.mulPose(Axis.YP.rotationDegrees(var12));
      } else {
         var3.mulPose(Axis.YP.rotationDegrees(this.getSignAngle(var7, var10)));
      }

      var3.translate(0.0F, -0.3125F, 0.0F);
      var9.evaluateVisibleParts(var7);
      float var13 = 1.0F;
      this.renderSign(var3, var4, var5, var6, 1.0F, var8, var9);
      this.renderSignText(var1, var3, var4, var5, 1.0F);
   }

   private float getSignAngle(BlockState var1, boolean var2) {
      return var2 ? -var1.getValue(WallSignBlock.FACING).toYRot() : -((float)(var1.getValue(CeilingHangingSignBlock.ROTATION) * 360) / 16.0F);
   }

   @Override
   Material getSignMaterial(WoodType var1) {
      return Sheets.getHangingSignMaterial(var1);
   }

   @Override
   void renderSignModel(PoseStack var1, int var2, int var3, Model var4, VertexConsumer var5) {
      HangingSignRenderer.HangingSignModel var6 = (HangingSignRenderer.HangingSignModel)var4;
      var6.root.render(var1, var5, var2, var3);
   }

   @Override
   Vec3 getTextOffset(float var1) {
      return new Vec3(0.0, (double)(-0.32F * var1), (double)(0.063F * var1));
   }

   public static LayerDefinition createHangingSignLayer() {
      MeshDefinition var0 = new MeshDefinition();
      PartDefinition var1 = var0.getRoot();
      var1.addOrReplaceChild("board", CubeListBuilder.create().texOffs(0, 12).addBox(-7.0F, 0.0F, -1.0F, 14.0F, 10.0F, 2.0F), PartPose.ZERO);
      var1.addOrReplaceChild("plank", CubeListBuilder.create().texOffs(0, 0).addBox(-8.0F, -6.0F, -2.0F, 16.0F, 2.0F, 4.0F), PartPose.ZERO);
      PartDefinition var2 = var1.addOrReplaceChild("normalChains", CubeListBuilder.create(), PartPose.ZERO);
      var2.addOrReplaceChild(
         "chainL1",
         CubeListBuilder.create().texOffs(0, 6).addBox(-1.5F, 0.0F, 0.0F, 3.0F, 6.0F, 0.0F),
         PartPose.offsetAndRotation(-5.0F, -6.0F, 0.0F, 0.0F, -0.7853982F, 0.0F)
      );
      var2.addOrReplaceChild(
         "chainL2",
         CubeListBuilder.create().texOffs(6, 6).addBox(-1.5F, 0.0F, 0.0F, 3.0F, 6.0F, 0.0F),
         PartPose.offsetAndRotation(-5.0F, -6.0F, 0.0F, 0.0F, 0.7853982F, 0.0F)
      );
      var2.addOrReplaceChild(
         "chainR1",
         CubeListBuilder.create().texOffs(0, 6).addBox(-1.5F, 0.0F, 0.0F, 3.0F, 6.0F, 0.0F),
         PartPose.offsetAndRotation(5.0F, -6.0F, 0.0F, 0.0F, -0.7853982F, 0.0F)
      );
      var2.addOrReplaceChild(
         "chainR2",
         CubeListBuilder.create().texOffs(6, 6).addBox(-1.5F, 0.0F, 0.0F, 3.0F, 6.0F, 0.0F),
         PartPose.offsetAndRotation(5.0F, -6.0F, 0.0F, 0.0F, 0.7853982F, 0.0F)
      );
      var1.addOrReplaceChild("vChains", CubeListBuilder.create().texOffs(14, 6).addBox(-6.0F, -6.0F, 0.0F, 12.0F, 6.0F, 0.0F), PartPose.ZERO);
      return LayerDefinition.create(var0, 64, 32);
   }

   public static final class HangingSignModel extends Model {
      public final ModelPart root;
      public final ModelPart plank;
      public final ModelPart vChains;
      public final ModelPart normalChains;

      public HangingSignModel(ModelPart var1) {
         super(RenderType::entityCutoutNoCull);
         this.root = var1;
         this.plank = var1.getChild("plank");
         this.normalChains = var1.getChild("normalChains");
         this.vChains = var1.getChild("vChains");
      }

      public void evaluateVisibleParts(BlockState var1) {
         boolean var2 = !(var1.getBlock() instanceof CeilingHangingSignBlock);
         this.plank.visible = var2;
         this.vChains.visible = false;
         this.normalChains.visible = true;
         if (!var2) {
            boolean var3 = var1.getValue(BlockStateProperties.ATTACHED);
            this.normalChains.visible = !var3;
            this.vChains.visible = var3;
         }
      }

      @Override
      public void renderToBuffer(PoseStack var1, VertexConsumer var2, int var3, int var4, float var5, float var6, float var7, float var8) {
         this.root.render(var1, var2, var3, var4, var5, var6, var7, var8);
      }
   }
}
