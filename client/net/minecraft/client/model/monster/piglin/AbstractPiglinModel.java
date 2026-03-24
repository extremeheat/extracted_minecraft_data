package net.minecraft.client.model.monster.piglin;

import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.client.model.player.PlayerModel;
import net.minecraft.client.renderer.entity.ArmorModelSet;
import net.minecraft.client.renderer.entity.state.HumanoidRenderState;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.util.Mth;

public abstract class AbstractPiglinModel<S extends HumanoidRenderState> extends HumanoidModel<S> {
   protected static final float ADULT_EAR_ANGLE_IN_DEGREES = 30.0F;
   protected static final float BABY_EAR_ANGLE_IN_DEGREES = 5.0F;
   public final ModelPart rightEar;
   public final ModelPart leftEar;

   public AbstractPiglinModel(final ModelPart root) {
      super(root, RenderTypes::entityTranslucent);
      this.rightEar = this.head.getChild("right_ear");
      this.leftEar = this.head.getChild("left_ear");
   }

   public static ArmorModelSet<MeshDefinition> createArmorMeshSet(final CubeDeformation innerDeformation, final CubeDeformation outerDeformation) {
      return PlayerModel.createArmorMeshSet(innerDeformation, outerDeformation).<MeshDefinition>map(AbstractPiglinModel::removeEars);
   }

   private static MeshDefinition removeEars(final MeshDefinition mesh) {
      PartDefinition root = mesh.getRoot();
      PartDefinition head = root.getChild("head");
      head.addOrReplaceChild("left_ear", CubeListBuilder.create(), PartPose.ZERO);
      head.addOrReplaceChild("right_ear", CubeListBuilder.create(), PartPose.ZERO);
      return mesh;
   }

   public static PartDefinition addHead(final CubeDeformation g, final MeshDefinition mesh) {
      PartDefinition root = mesh.getRoot();
      PartDefinition head = root.addOrReplaceChild("head", CubeListBuilder.create().texOffs(0, 0).addBox(-5.0F, -8.0F, -4.0F, 10.0F, 8.0F, 8.0F, g).texOffs(31, 1).addBox(-2.0F, -4.0F, -5.0F, 4.0F, 4.0F, 1.0F, g).texOffs(2, 4).addBox(2.0F, -2.0F, -5.0F, 1.0F, 2.0F, 1.0F, g).texOffs(2, 0).addBox(-3.0F, -2.0F, -5.0F, 1.0F, 2.0F, 1.0F, g), PartPose.ZERO);
      head.addOrReplaceChild("left_ear", CubeListBuilder.create().texOffs(51, 6).addBox(0.0F, 0.0F, -2.0F, 1.0F, 5.0F, 4.0F, g), PartPose.offsetAndRotation(4.5F, -6.0F, 0.0F, 0.0F, 0.0F, -0.5235988F));
      head.addOrReplaceChild("right_ear", CubeListBuilder.create().texOffs(39, 6).addBox(-1.0F, 0.0F, -2.0F, 1.0F, 5.0F, 4.0F, g), PartPose.offsetAndRotation(-4.5F, -6.0F, 0.0F, 0.0F, 0.0F, 0.5235988F));
      return head;
   }

   public static ArmorModelSet<MeshDefinition> createBabyArmorMeshSet(final CubeDeformation innerDeformation, final CubeDeformation outerDeformation, final PartPose armOffset) {
      return PlayerModel.createBabyArmorMeshSet(innerDeformation, outerDeformation, armOffset).<MeshDefinition>map(AbstractPiglinModel::removeEars);
   }

   public void setupAnim(final S state) {
      super.setupAnim(state);
      float animationPos = state.walkAnimationPos;
      float animationSpeed = state.walkAnimationSpeed;
      float defaultAngle = this.getDefaultEarAngleInDegrees() * 0.017453292F;
      float frequency = state.ageInTicks * 0.1F + animationPos * 0.5F;
      float amplitude = 0.08F + animationSpeed * 0.4F;
      this.leftEar.zRot = -defaultAngle - Mth.cos((double)(frequency * 1.2F)) * amplitude;
      this.rightEar.zRot = defaultAngle + Mth.cos((double)frequency) * amplitude;
   }

   abstract float getDefaultEarAngleInDegrees();
}
