package net.minecraft.client.renderer.blockentity;

import com.google.common.collect.ImmutableMap;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import java.util.Arrays;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Stream;
import net.minecraft.client.model.Model;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.resources.model.Material;
import net.minecraft.client.resources.model.MaterialSet;
import net.minecraft.util.StringRepresentable;
import net.minecraft.util.Unit;
import net.minecraft.world.level.block.CeilingHangingSignBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.WoodType;
import net.minecraft.world.phys.Vec3;
import org.joml.Quaternionfc;

public class HangingSignRenderer extends AbstractSignRenderer {
   private static final String PLANK = "plank";
   private static final String V_CHAINS = "vChains";
   private static final String NORMAL_CHAINS = "normalChains";
   private static final String CHAIN_L_1 = "chainL1";
   private static final String CHAIN_L_2 = "chainL2";
   private static final String CHAIN_R_1 = "chainR1";
   private static final String CHAIN_R_2 = "chainR2";
   private static final String BOARD = "board";
   public static final float MODEL_RENDER_SCALE = 1.0F;
   private static final float TEXT_RENDER_SCALE = 0.9F;
   private static final Vec3 TEXT_OFFSET = new Vec3(0.0, -0.3199999928474426, 0.0729999989271164);
   private final Map<ModelKey, Model.Simple> hangingSignModels;

   public HangingSignRenderer(final BlockEntityRendererProvider.Context context) {
      super(context);
      Stream<ModelKey> modelKeys = WoodType.values().flatMap((woodType) -> Arrays.stream(HangingSignRenderer.AttachmentType.values()).map((attachmentType) -> new ModelKey(woodType, attachmentType)));
      this.hangingSignModels = (Map)modelKeys.collect(ImmutableMap.toImmutableMap((type) -> type, (type) -> createSignModel(context.entityModelSet(), type.woodType, type.attachmentType)));
   }

   public static Model.Simple createSignModel(final EntityModelSet entityModelSet, final WoodType woodType, final AttachmentType attachmentType) {
      return new Model.Simple(entityModelSet.bakeLayer(ModelLayers.createHangingSignModelName(woodType, attachmentType)), RenderTypes::entityCutout);
   }

   protected float getSignModelRenderScale() {
      return 1.0F;
   }

   protected float getSignTextRenderScale() {
      return 0.9F;
   }

   public static void translateBase(final PoseStack poseStack, final float angle) {
      poseStack.translate(0.5, 0.9375, 0.5);
      poseStack.mulPose((Quaternionfc)Axis.YP.rotationDegrees(angle));
      poseStack.translate(0.0F, -0.3125F, 0.0F);
   }

   protected void translateSign(final PoseStack poseStack, final float angle, final BlockState blockState) {
      translateBase(poseStack, angle);
   }

   protected Model.Simple getSignModel(final BlockState blockState, final WoodType type) {
      AttachmentType attachmentType = HangingSignRenderer.AttachmentType.byBlockState(blockState);
      return (Model.Simple)this.hangingSignModels.get(new ModelKey(type, attachmentType));
   }

   protected Material getSignMaterial(final WoodType type) {
      return Sheets.getHangingSignMaterial(type);
   }

   protected Vec3 getTextOffset() {
      return TEXT_OFFSET;
   }

   public static void submitSpecial(final MaterialSet materials, final PoseStack poseStack, final SubmitNodeCollector submitNodeCollector, final int lightCoords, final int overlayCoords, final Model.Simple model, final Material material) {
      poseStack.pushPose();
      translateBase(poseStack, 0.0F);
      poseStack.scale(1.0F, -1.0F, -1.0F);
      Unit var10002 = Unit.INSTANCE;
      Objects.requireNonNull(model);
      submitNodeCollector.submitModel(model, var10002, poseStack, material.renderType(model::renderType), lightCoords, overlayCoords, -1, materials.get(material), 0, (ModelFeatureRenderer.CrumblingOverlay)null);
      poseStack.popPose();
   }

   public static LayerDefinition createHangingSignLayer(final AttachmentType type) {
      MeshDefinition mesh = new MeshDefinition();
      PartDefinition root = mesh.getRoot();
      root.addOrReplaceChild("board", CubeListBuilder.create().texOffs(0, 12).addBox(-7.0F, 0.0F, -1.0F, 14.0F, 10.0F, 2.0F), PartPose.ZERO);
      if (type == HangingSignRenderer.AttachmentType.WALL) {
         root.addOrReplaceChild("plank", CubeListBuilder.create().texOffs(0, 0).addBox(-8.0F, -6.0F, -2.0F, 16.0F, 2.0F, 4.0F), PartPose.ZERO);
      }

      if (type == HangingSignRenderer.AttachmentType.WALL || type == HangingSignRenderer.AttachmentType.CEILING) {
         PartDefinition normalChains = root.addOrReplaceChild("normalChains", CubeListBuilder.create(), PartPose.ZERO);
         normalChains.addOrReplaceChild("chainL1", CubeListBuilder.create().texOffs(0, 6).addBox(-1.5F, 0.0F, 0.0F, 3.0F, 6.0F, 0.0F), PartPose.offsetAndRotation(-5.0F, -6.0F, 0.0F, 0.0F, -0.7853982F, 0.0F));
         normalChains.addOrReplaceChild("chainL2", CubeListBuilder.create().texOffs(6, 6).addBox(-1.5F, 0.0F, 0.0F, 3.0F, 6.0F, 0.0F), PartPose.offsetAndRotation(-5.0F, -6.0F, 0.0F, 0.0F, 0.7853982F, 0.0F));
         normalChains.addOrReplaceChild("chainR1", CubeListBuilder.create().texOffs(0, 6).addBox(-1.5F, 0.0F, 0.0F, 3.0F, 6.0F, 0.0F), PartPose.offsetAndRotation(5.0F, -6.0F, 0.0F, 0.0F, -0.7853982F, 0.0F));
         normalChains.addOrReplaceChild("chainR2", CubeListBuilder.create().texOffs(6, 6).addBox(-1.5F, 0.0F, 0.0F, 3.0F, 6.0F, 0.0F), PartPose.offsetAndRotation(5.0F, -6.0F, 0.0F, 0.0F, 0.7853982F, 0.0F));
      }

      if (type == HangingSignRenderer.AttachmentType.CEILING_MIDDLE) {
         root.addOrReplaceChild("vChains", CubeListBuilder.create().texOffs(14, 6).addBox(-6.0F, -6.0F, 0.0F, 12.0F, 6.0F, 0.0F), PartPose.ZERO);
      }

      return LayerDefinition.create(mesh, 64, 32);
   }

   public static enum AttachmentType implements StringRepresentable {
      WALL("wall"),
      CEILING("ceiling"),
      CEILING_MIDDLE("ceiling_middle");

      private final String name;

      private AttachmentType(final String name) {
         this.name = name;
      }

      public static AttachmentType byBlockState(final BlockState blockState) {
         if (blockState.getBlock() instanceof CeilingHangingSignBlock) {
            return (Boolean)blockState.getValue(BlockStateProperties.ATTACHED) ? CEILING_MIDDLE : CEILING;
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
      public ModelKey {
         super();
      }
   }
}
