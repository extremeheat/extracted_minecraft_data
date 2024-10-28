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
import net.minecraft.world.level.block.entity.SignBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.WoodType;
import net.minecraft.world.phys.Vec3;

public class HangingSignRenderer extends SignRenderer {
   private static final String PLANK = "plank";
   private static final String V_CHAINS = "vChains";
   private static final String NORMAL_CHAINS = "normalChains";
   private static final String CHAIN_L_1 = "chainL1";
   private static final String CHAIN_L_2 = "chainL2";
   private static final String CHAIN_R_1 = "chainR1";
   private static final String CHAIN_R_2 = "chainR2";
   private static final String BOARD = "board";
   private static final float MODEL_RENDER_SCALE = 1.0F;
   private static final float TEXT_RENDER_SCALE = 0.9F;
   private static final Vec3 TEXT_OFFSET = new Vec3(0.0, -0.3199999928474426, 0.0729999989271164);
   private final Map<WoodType, HangingSignModel> hangingSignModels;

   public HangingSignRenderer(BlockEntityRendererProvider.Context var1) {
      super(var1);
      this.hangingSignModels = (Map)WoodType.values().collect(ImmutableMap.toImmutableMap((var0) -> {
         return var0;
      }, (var1x) -> {
         return new HangingSignModel(var1.bakeLayer(ModelLayers.createHangingSignModelName(var1x)));
      }));
   }

   public float getSignModelRenderScale() {
      return 1.0F;
   }

   public float getSignTextRenderScale() {
      return 0.9F;
   }

   public void render(SignBlockEntity var1, float var2, PoseStack var3, MultiBufferSource var4, int var5, int var6) {
      BlockState var7 = var1.getBlockState();
      SignBlock var8 = (SignBlock)var7.getBlock();
      WoodType var9 = SignBlock.getWoodType(var8);
      HangingSignModel var10 = (HangingSignModel)this.hangingSignModels.get(var9);
      var10.evaluateVisibleParts(var7);
      this.renderSignWithText(var1, var3, var4, var5, var6, var7, var8, var9, var10);
   }

   void translateSign(PoseStack var1, float var2, BlockState var3) {
      var1.translate(0.5, 0.9375, 0.5);
      var1.mulPose(Axis.YP.rotationDegrees(var2));
      var1.translate(0.0F, -0.3125F, 0.0F);
   }

   void renderSignModel(PoseStack var1, int var2, int var3, Model var4, VertexConsumer var5) {
      HangingSignModel var6 = (HangingSignModel)var4;
      var6.root.render(var1, var5, var2, var3);
   }

   Material getSignMaterial(WoodType var1) {
      return Sheets.getHangingSignMaterial(var1);
   }

   Vec3 getTextOffset() {
      return TEXT_OFFSET;
   }

   public static LayerDefinition createHangingSignLayer() {
      MeshDefinition var0 = new MeshDefinition();
      PartDefinition var1 = var0.getRoot();
      var1.addOrReplaceChild("board", CubeListBuilder.create().texOffs(0, 12).addBox(-7.0F, 0.0F, -1.0F, 14.0F, 10.0F, 2.0F), PartPose.ZERO);
      var1.addOrReplaceChild("plank", CubeListBuilder.create().texOffs(0, 0).addBox(-8.0F, -6.0F, -2.0F, 16.0F, 2.0F, 4.0F), PartPose.ZERO);
      PartDefinition var2 = var1.addOrReplaceChild("normalChains", CubeListBuilder.create(), PartPose.ZERO);
      var2.addOrReplaceChild("chainL1", CubeListBuilder.create().texOffs(0, 6).addBox(-1.5F, 0.0F, 0.0F, 3.0F, 6.0F, 0.0F), PartPose.offsetAndRotation(-5.0F, -6.0F, 0.0F, 0.0F, -0.7853982F, 0.0F));
      var2.addOrReplaceChild("chainL2", CubeListBuilder.create().texOffs(6, 6).addBox(-1.5F, 0.0F, 0.0F, 3.0F, 6.0F, 0.0F), PartPose.offsetAndRotation(-5.0F, -6.0F, 0.0F, 0.0F, 0.7853982F, 0.0F));
      var2.addOrReplaceChild("chainR1", CubeListBuilder.create().texOffs(0, 6).addBox(-1.5F, 0.0F, 0.0F, 3.0F, 6.0F, 0.0F), PartPose.offsetAndRotation(5.0F, -6.0F, 0.0F, 0.0F, -0.7853982F, 0.0F));
      var2.addOrReplaceChild("chainR2", CubeListBuilder.create().texOffs(6, 6).addBox(-1.5F, 0.0F, 0.0F, 3.0F, 6.0F, 0.0F), PartPose.offsetAndRotation(5.0F, -6.0F, 0.0F, 0.0F, 0.7853982F, 0.0F));
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
            boolean var3 = (Boolean)var1.getValue(BlockStateProperties.ATTACHED);
            this.normalChains.visible = !var3;
            this.vChains.visible = var3;
         }

      }

      public void renderToBuffer(PoseStack var1, VertexConsumer var2, int var3, int var4, int var5) {
         this.root.render(var1, var2, var3, var4, var5);
      }
   }
}
