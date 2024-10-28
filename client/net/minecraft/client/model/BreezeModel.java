package net.minecraft.client.model;

import net.minecraft.client.animation.definitions.BreezeAnimation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.monster.breeze.Breeze;

public class BreezeModel<T extends Breeze> extends HierarchicalModel<T> {
   private static final float WIND_TOP_SPEED = 0.6F;
   private static final float WIND_MIDDLE_SPEED = 0.8F;
   private static final float WIND_BOTTOM_SPEED = 1.0F;
   private final ModelPart root;
   private final ModelPart head;
   private final ModelPart eyes;
   private final ModelPart wind;
   private final ModelPart windTop;
   private final ModelPart windMid;
   private final ModelPart windBottom;
   private final ModelPart rods;

   public BreezeModel(ModelPart var1) {
      super(RenderType::entityTranslucent);
      this.root = var1;
      this.wind = var1.getChild("wind_body");
      this.windBottom = this.wind.getChild("wind_bottom");
      this.windMid = this.windBottom.getChild("wind_mid");
      this.windTop = this.windMid.getChild("wind_top");
      this.head = var1.getChild("body").getChild("head");
      this.eyes = this.head.getChild("eyes");
      this.rods = var1.getChild("body").getChild("rods");
   }

   public static LayerDefinition createBodyLayer(int var0, int var1) {
      MeshDefinition var2 = new MeshDefinition();
      PartDefinition var3 = var2.getRoot();
      PartDefinition var4 = var3.addOrReplaceChild("body", CubeListBuilder.create(), PartPose.offset(0.0F, 0.0F, 0.0F));
      PartDefinition var5 = var4.addOrReplaceChild("rods", CubeListBuilder.create(), PartPose.offset(0.0F, 8.0F, 0.0F));
      var5.addOrReplaceChild("rod_1", CubeListBuilder.create().texOffs(0, 17).addBox(-1.0F, 0.0F, -3.0F, 2.0F, 8.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(2.5981F, -3.0F, 1.5F, -2.7489F, -1.0472F, 3.1416F));
      var5.addOrReplaceChild("rod_2", CubeListBuilder.create().texOffs(0, 17).addBox(-1.0F, 0.0F, -3.0F, 2.0F, 8.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-2.5981F, -3.0F, 1.5F, -2.7489F, 1.0472F, 3.1416F));
      var5.addOrReplaceChild("rod_3", CubeListBuilder.create().texOffs(0, 17).addBox(-1.0F, 0.0F, -3.0F, 2.0F, 8.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -3.0F, -3.0F, 0.3927F, 0.0F, 0.0F));
      PartDefinition var6 = var4.addOrReplaceChild("head", CubeListBuilder.create().texOffs(4, 24).addBox(-5.0F, -5.0F, -4.2F, 10.0F, 3.0F, 4.0F, new CubeDeformation(0.0F)).texOffs(0, 0).addBox(-4.0F, -8.0F, -4.0F, 8.0F, 8.0F, 8.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 4.0F, 0.0F));
      var6.addOrReplaceChild("eyes", CubeListBuilder.create().texOffs(4, 24).addBox(-5.0F, -5.0F, -4.2F, 10.0F, 3.0F, 4.0F, new CubeDeformation(0.0F)).texOffs(0, 0).addBox(-4.0F, -8.0F, -4.0F, 8.0F, 8.0F, 8.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 0.0F));
      PartDefinition var7 = var3.addOrReplaceChild("wind_body", CubeListBuilder.create(), PartPose.offset(0.0F, 0.0F, 0.0F));
      PartDefinition var8 = var7.addOrReplaceChild("wind_bottom", CubeListBuilder.create().texOffs(1, 83).addBox(-2.5F, -7.0F, -2.5F, 5.0F, 7.0F, 5.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 24.0F, 0.0F));
      PartDefinition var9 = var8.addOrReplaceChild("wind_mid", CubeListBuilder.create().texOffs(74, 28).addBox(-6.0F, -6.0F, -6.0F, 12.0F, 6.0F, 12.0F, new CubeDeformation(0.0F)).texOffs(78, 32).addBox(-4.0F, -6.0F, -4.0F, 8.0F, 6.0F, 8.0F, new CubeDeformation(0.0F)).texOffs(49, 71).addBox(-2.5F, -6.0F, -2.5F, 5.0F, 6.0F, 5.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -7.0F, 0.0F));
      var9.addOrReplaceChild("wind_top", CubeListBuilder.create().texOffs(0, 0).addBox(-9.0F, -8.0F, -9.0F, 18.0F, 8.0F, 18.0F, new CubeDeformation(0.0F)).texOffs(6, 6).addBox(-6.0F, -8.0F, -6.0F, 12.0F, 8.0F, 12.0F, new CubeDeformation(0.0F)).texOffs(105, 57).addBox(-2.5F, -8.0F, -2.5F, 5.0F, 8.0F, 5.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -6.0F, 0.0F));
      return LayerDefinition.create(var2, var0, var1);
   }

   public void setupAnim(T var1, float var2, float var3, float var4, float var5, float var6) {
      this.root().getAllParts().forEach(ModelPart::resetPose);
      float var7 = var4 * 3.1415927F * -0.1F;
      this.windTop.x = Mth.cos(var7) * 1.0F * 0.6F;
      this.windTop.z = Mth.sin(var7) * 1.0F * 0.6F;
      this.windMid.x = Mth.sin(var7) * 0.5F * 0.8F;
      this.windMid.z = Mth.cos(var7) * 0.8F;
      this.windBottom.x = Mth.cos(var7) * -0.25F * 1.0F;
      this.windBottom.z = Mth.sin(var7) * -0.25F * 1.0F;
      this.head.y = 4.0F + Mth.cos(var7) / 4.0F;
      this.rods.yRot = var4 * 3.1415927F * 0.1F;
      this.animate(var1.shoot, BreezeAnimation.SHOOT, var4);
      this.animate(var1.slide, BreezeAnimation.SLIDE, var4);
      this.animate(var1.slideBack, BreezeAnimation.SLIDE_BACK, var4);
      this.animate(var1.longJump, BreezeAnimation.JUMP, var4);
   }

   public ModelPart root() {
      return this.root;
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
