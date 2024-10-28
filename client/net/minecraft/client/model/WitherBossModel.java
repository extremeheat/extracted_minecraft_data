package net.minecraft.client.model;

import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.boss.wither.WitherBoss;

public class WitherBossModel<T extends WitherBoss> extends HierarchicalModel<T> {
   private static final String RIBCAGE = "ribcage";
   private static final String CENTER_HEAD = "center_head";
   private static final String RIGHT_HEAD = "right_head";
   private static final String LEFT_HEAD = "left_head";
   private static final float RIBCAGE_X_ROT_OFFSET = 0.065F;
   private static final float TAIL_X_ROT_OFFSET = 0.265F;
   private final ModelPart root;
   private final ModelPart centerHead;
   private final ModelPart rightHead;
   private final ModelPart leftHead;
   private final ModelPart ribcage;
   private final ModelPart tail;

   public WitherBossModel(ModelPart var1) {
      super();
      this.root = var1;
      this.ribcage = var1.getChild("ribcage");
      this.tail = var1.getChild("tail");
      this.centerHead = var1.getChild("center_head");
      this.rightHead = var1.getChild("right_head");
      this.leftHead = var1.getChild("left_head");
   }

   public static LayerDefinition createBodyLayer(CubeDeformation var0) {
      MeshDefinition var1 = new MeshDefinition();
      PartDefinition var2 = var1.getRoot();
      var2.addOrReplaceChild("shoulders", CubeListBuilder.create().texOffs(0, 16).addBox(-10.0F, 3.9F, -0.5F, 20.0F, 3.0F, 3.0F, var0), PartPose.ZERO);
      float var3 = 0.20420352F;
      var2.addOrReplaceChild("ribcage", CubeListBuilder.create().texOffs(0, 22).addBox(0.0F, 0.0F, 0.0F, 3.0F, 10.0F, 3.0F, var0).texOffs(24, 22).addBox(-4.0F, 1.5F, 0.5F, 11.0F, 2.0F, 2.0F, var0).texOffs(24, 22).addBox(-4.0F, 4.0F, 0.5F, 11.0F, 2.0F, 2.0F, var0).texOffs(24, 22).addBox(-4.0F, 6.5F, 0.5F, 11.0F, 2.0F, 2.0F, var0), PartPose.offsetAndRotation(-2.0F, 6.9F, -0.5F, 0.20420352F, 0.0F, 0.0F));
      var2.addOrReplaceChild("tail", CubeListBuilder.create().texOffs(12, 22).addBox(0.0F, 0.0F, 0.0F, 3.0F, 6.0F, 3.0F, var0), PartPose.offsetAndRotation(-2.0F, 6.9F + Mth.cos(0.20420352F) * 10.0F, -0.5F + Mth.sin(0.20420352F) * 10.0F, 0.83252203F, 0.0F, 0.0F));
      var2.addOrReplaceChild("center_head", CubeListBuilder.create().texOffs(0, 0).addBox(-4.0F, -4.0F, -4.0F, 8.0F, 8.0F, 8.0F, var0), PartPose.ZERO);
      CubeListBuilder var4 = CubeListBuilder.create().texOffs(32, 0).addBox(-4.0F, -4.0F, -4.0F, 6.0F, 6.0F, 6.0F, var0);
      var2.addOrReplaceChild("right_head", var4, PartPose.offset(-8.0F, 4.0F, 0.0F));
      var2.addOrReplaceChild("left_head", var4, PartPose.offset(10.0F, 4.0F, 0.0F));
      return LayerDefinition.create(var1, 64, 64);
   }

   public ModelPart root() {
      return this.root;
   }

   public void setupAnim(T var1, float var2, float var3, float var4, float var5, float var6) {
      float var7 = Mth.cos(var4 * 0.1F);
      this.ribcage.xRot = (0.065F + 0.05F * var7) * 3.1415927F;
      this.tail.setPos(-2.0F, 6.9F + Mth.cos(this.ribcage.xRot) * 10.0F, -0.5F + Mth.sin(this.ribcage.xRot) * 10.0F);
      this.tail.xRot = (0.265F + 0.1F * var7) * 3.1415927F;
      this.centerHead.yRot = var5 * 0.017453292F;
      this.centerHead.xRot = var6 * 0.017453292F;
   }

   public void prepareMobModel(T var1, float var2, float var3, float var4) {
      setupHeadRotation(var1, this.rightHead, 0);
      setupHeadRotation(var1, this.leftHead, 1);
   }

   private static <T extends WitherBoss> void setupHeadRotation(T var0, ModelPart var1, int var2) {
      var1.yRot = (var0.getHeadYRot(var2) - var0.yBodyRot) * 0.017453292F;
      var1.xRot = var0.getHeadXRot(var2) * 0.017453292F;
   }
}
