package net.minecraft.client.renderer.blockentity;

import com.google.common.collect.ImmutableMap;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import java.util.Arrays;
import java.util.Map;
import java.util.stream.Stream;
import net.minecraft.client.model.Model;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.resources.model.Material;
import net.minecraft.util.StringRepresentable;
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
   private final Map<ModelKey, Model> hangingSignModels;

   public HangingSignRenderer(BlockEntityRendererProvider.Context var1) {
      super(var1);
      Stream var2 = WoodType.values().flatMap((var0) -> {
         return Arrays.stream(HangingSignRenderer.AttachmentType.values()).map((var1) -> {
            return new ModelKey(var0, var1);
         });
      });
      this.hangingSignModels = (Map)var2.collect(ImmutableMap.toImmutableMap((var0) -> {
         return var0;
      }, (var1x) -> {
         return new Model.Simple(var1.bakeLayer(ModelLayers.createHangingSignModelName(var1x.woodType, var1x.attachmentType)), RenderType::entityCutoutNoCull);
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
      AttachmentType var10 = HangingSignRenderer.AttachmentType.byBlockState(var7);
      Model var11 = (Model)this.hangingSignModels.get(new ModelKey(var9, var10));
      this.renderSignWithText(var1, var3, var4, var5, var6, var7, var8, var9, var11);
   }

   void translateSign(PoseStack var1, float var2, BlockState var3) {
      var1.translate(0.5, 0.9375, 0.5);
      var1.mulPose(Axis.YP.rotationDegrees(var2));
      var1.translate(0.0F, -0.3125F, 0.0F);
   }

   Material getSignMaterial(WoodType var1) {
      return Sheets.getHangingSignMaterial(var1);
   }

   Vec3 getTextOffset() {
      return TEXT_OFFSET;
   }

   public static LayerDefinition createHangingSignLayer(AttachmentType var0) {
      MeshDefinition var1 = new MeshDefinition();
      PartDefinition var2 = var1.getRoot();
      var2.addOrReplaceChild("board", CubeListBuilder.create().texOffs(0, 12).addBox(-7.0F, 0.0F, -1.0F, 14.0F, 10.0F, 2.0F), PartPose.ZERO);
      if (var0 == HangingSignRenderer.AttachmentType.WALL) {
         var2.addOrReplaceChild("plank", CubeListBuilder.create().texOffs(0, 0).addBox(-8.0F, -6.0F, -2.0F, 16.0F, 2.0F, 4.0F), PartPose.ZERO);
      }

      if (var0 == HangingSignRenderer.AttachmentType.WALL || var0 == HangingSignRenderer.AttachmentType.CEILING) {
         PartDefinition var3 = var2.addOrReplaceChild("normalChains", CubeListBuilder.create(), PartPose.ZERO);
         var3.addOrReplaceChild("chainL1", CubeListBuilder.create().texOffs(0, 6).addBox(-1.5F, 0.0F, 0.0F, 3.0F, 6.0F, 0.0F), PartPose.offsetAndRotation(-5.0F, -6.0F, 0.0F, 0.0F, -0.7853982F, 0.0F));
         var3.addOrReplaceChild("chainL2", CubeListBuilder.create().texOffs(6, 6).addBox(-1.5F, 0.0F, 0.0F, 3.0F, 6.0F, 0.0F), PartPose.offsetAndRotation(-5.0F, -6.0F, 0.0F, 0.0F, 0.7853982F, 0.0F));
         var3.addOrReplaceChild("chainR1", CubeListBuilder.create().texOffs(0, 6).addBox(-1.5F, 0.0F, 0.0F, 3.0F, 6.0F, 0.0F), PartPose.offsetAndRotation(5.0F, -6.0F, 0.0F, 0.0F, -0.7853982F, 0.0F));
         var3.addOrReplaceChild("chainR2", CubeListBuilder.create().texOffs(6, 6).addBox(-1.5F, 0.0F, 0.0F, 3.0F, 6.0F, 0.0F), PartPose.offsetAndRotation(5.0F, -6.0F, 0.0F, 0.0F, 0.7853982F, 0.0F));
      }

      if (var0 == HangingSignRenderer.AttachmentType.CEILING_MIDDLE) {
         var2.addOrReplaceChild("vChains", CubeListBuilder.create().texOffs(14, 6).addBox(-6.0F, -6.0F, 0.0F, 12.0F, 6.0F, 0.0F), PartPose.ZERO);
      }

      return LayerDefinition.create(var1, 64, 32);
   }

   public static enum AttachmentType implements StringRepresentable {
      WALL("wall"),
      CEILING("ceiling"),
      CEILING_MIDDLE("ceiling_middle");

      private final String name;

      private AttachmentType(final String var3) {
         this.name = var3;
      }

      public static AttachmentType byBlockState(BlockState var0) {
         if (var0.getBlock() instanceof CeilingHangingSignBlock) {
            return (Boolean)var0.getValue(BlockStateProperties.ATTACHED) ? CEILING_MIDDLE : CEILING;
         } else {
            return WALL;
         }
      }

      public String getSerializedName() {
         return this.name;
      }

      // $FF: synthetic method
      private static AttachmentType[] $values() {
         return new AttachmentType[]{WALL, CEILING, CEILING_MIDDLE};
      }
   }

   public static record ModelKey(WoodType woodType, AttachmentType attachmentType) {
      final WoodType woodType;
      final AttachmentType attachmentType;

      public ModelKey(WoodType var1, AttachmentType var2) {
         super();
         this.woodType = var1;
         this.attachmentType = var2;
      }

      public WoodType woodType() {
         return this.woodType;
      }

      public AttachmentType attachmentType() {
         return this.attachmentType;
      }
   }
}
