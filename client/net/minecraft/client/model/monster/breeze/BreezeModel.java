package net.minecraft.client.model.monster.breeze;

import java.util.Set;
import net.minecraft.client.animation.KeyframeAnimation;
import net.minecraft.client.animation.definitions.BreezeAnimation;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.client.renderer.entity.state.BreezeRenderState;
import net.minecraft.client.renderer.rendertype.RenderTypes;

public class BreezeModel extends EntityModel<BreezeRenderState> {
   private static final float WIND_TOP_SPEED = 0.6F;
   private static final float WIND_MIDDLE_SPEED = 0.8F;
   private static final float WIND_BOTTOM_SPEED = 1.0F;
   private final ModelPart head;
   private final ModelPart eyes;
   private final ModelPart wind;
   private final ModelPart windTop;
   private final ModelPart windMid;
   private final ModelPart windBottom;
   private final ModelPart rods;
   private final KeyframeAnimation idleAnimation;
   private final KeyframeAnimation shootAnimation;
   private final KeyframeAnimation slideAnimation;
   private final KeyframeAnimation slideBackAnimation;
   private final KeyframeAnimation inhaleAnimation;
   private final KeyframeAnimation jumpAnimation;

   public BreezeModel(ModelPart var1) {
      super(var1, RenderTypes::entityTranslucent);
      this.wind = var1.getChild("wind_body");
      this.windBottom = this.wind.getChild("wind_bottom");
      this.windMid = this.windBottom.getChild("wind_mid");
      this.windTop = this.windMid.getChild("wind_top");
      this.head = var1.getChild("body").getChild("head");
      this.eyes = this.head.getChild("eyes");
      this.rods = var1.getChild("body").getChild("rods");
      this.idleAnimation = BreezeAnimation.IDLE.bake(var1);
      this.shootAnimation = BreezeAnimation.SHOOT.bake(var1);
      this.slideAnimation = BreezeAnimation.SLIDE.bake(var1);
      this.slideBackAnimation = BreezeAnimation.SLIDE_BACK.bake(var1);
      this.inhaleAnimation = BreezeAnimation.INHALE.bake(var1);
      this.jumpAnimation = BreezeAnimation.JUMP.bake(var1);
   }

   private static MeshDefinition createBaseMesh() {
      MeshDefinition var0 = new MeshDefinition();
      PartDefinition var1 = var0.getRoot();
      PartDefinition var2 = var1.addOrReplaceChild("body", CubeListBuilder.create(), PartPose.offset(0.0F, 0.0F, 0.0F));
      PartDefinition var3 = var2.addOrReplaceChild("rods", CubeListBuilder.create(), PartPose.offset(0.0F, 8.0F, 0.0F));
      var3.addOrReplaceChild("rod_1", CubeListBuilder.create().texOffs(0, 17).addBox(-1.0F, 0.0F, -3.0F, 2.0F, 8.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(2.5981F, -3.0F, 1.5F, -2.7489F, -1.0472F, 3.1416F));
      var3.addOrReplaceChild("rod_2", CubeListBuilder.create().texOffs(0, 17).addBox(-1.0F, 0.0F, -3.0F, 2.0F, 8.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-2.5981F, -3.0F, 1.5F, -2.7489F, 1.0472F, 3.1416F));
      var3.addOrReplaceChild("rod_3", CubeListBuilder.create().texOffs(0, 17).addBox(-1.0F, 0.0F, -3.0F, 2.0F, 8.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -3.0F, -3.0F, 0.3927F, 0.0F, 0.0F));
      PartDefinition var4 = var2.addOrReplaceChild("head", CubeListBuilder.create().texOffs(4, 24).addBox(-5.0F, -5.0F, -4.2F, 10.0F, 3.0F, 4.0F, new CubeDeformation(0.0F)).texOffs(0, 0).addBox(-4.0F, -8.0F, -4.0F, 8.0F, 8.0F, 8.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 4.0F, 0.0F));
      var4.addOrReplaceChild("eyes", CubeListBuilder.create().texOffs(4, 24).addBox(-5.0F, -5.0F, -4.2F, 10.0F, 3.0F, 4.0F, new CubeDeformation(0.0F)).texOffs(0, 0).addBox(-4.0F, -8.0F, -4.0F, 8.0F, 8.0F, 8.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 0.0F));
      PartDefinition var5 = var1.addOrReplaceChild("wind_body", CubeListBuilder.create(), PartPose.offset(0.0F, 0.0F, 0.0F));
      PartDefinition var6 = var5.addOrReplaceChild("wind_bottom", CubeListBuilder.create().texOffs(1, 83).addBox(-2.5F, -7.0F, -2.5F, 5.0F, 7.0F, 5.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 24.0F, 0.0F));
      PartDefinition var7 = var6.addOrReplaceChild("wind_mid", CubeListBuilder.create().texOffs(74, 28).addBox(-6.0F, -6.0F, -6.0F, 12.0F, 6.0F, 12.0F, new CubeDeformation(0.0F)).texOffs(78, 32).addBox(-4.0F, -6.0F, -4.0F, 8.0F, 6.0F, 8.0F, new CubeDeformation(0.0F)).texOffs(49, 71).addBox(-2.5F, -6.0F, -2.5F, 5.0F, 6.0F, 5.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -7.0F, 0.0F));
      var7.addOrReplaceChild("wind_top", CubeListBuilder.create().texOffs(0, 0).addBox(-9.0F, -8.0F, -9.0F, 18.0F, 8.0F, 18.0F, new CubeDeformation(0.0F)).texOffs(6, 6).addBox(-6.0F, -8.0F, -6.0F, 12.0F, 8.0F, 12.0F, new CubeDeformation(0.0F)).texOffs(105, 57).addBox(-2.5F, -8.0F, -2.5F, 5.0F, 8.0F, 5.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -6.0F, 0.0F));
      return var0;
   }

   public static LayerDefinition createBodyLayer() {
      MeshDefinition var0 = createBaseMesh();
      var0.getRoot().retainPartsAndChildren(Set.of("head", "rods"));
      return LayerDefinition.create(var0, 32, 32);
   }

   public static LayerDefinition createWindLayer() {
      MeshDefinition var0 = createBaseMesh();
      var0.getRoot().retainPartsAndChildren(Set.of("wind_body"));
      return LayerDefinition.create(var0, 128, 128);
   }

   public static LayerDefinition createEyesLayer() {
      MeshDefinition var0 = createBaseMesh();
      var0.getRoot().retainPartsAndChildren(Set.of("eyes"));
      return LayerDefinition.create(var0, 32, 32);
   }

   public void setupAnim(BreezeRenderState var1) {
      super.setupAnim(var1);
      this.idleAnimation.apply(var1.idle, var1.ageInTicks);
      this.shootAnimation.apply(var1.shoot, var1.ageInTicks);
      this.slideAnimation.apply(var1.slide, var1.ageInTicks);
      this.slideBackAnimation.apply(var1.slideBack, var1.ageInTicks);
      this.inhaleAnimation.apply(var1.inhale, var1.ageInTicks);
      this.jumpAnimation.apply(var1.longJump, var1.ageInTicks);
   }

   public ModelPart head() {
      return this.head;
   }

   public ModelPart eyes() {
      return this.eyes;
   }

   public ModelPart rods() {
      return this.rods;
   }

   public ModelPart wind() {
      return this.wind;
   }
}
