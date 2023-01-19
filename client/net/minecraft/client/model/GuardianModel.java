package net.minecraft.client.model;

import net.minecraft.client.Minecraft;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.monster.Guardian;
import net.minecraft.world.phys.Vec3;

public class GuardianModel extends HierarchicalModel<Guardian> {
   private static final float[] SPIKE_X_ROT = new float[]{1.75F, 0.25F, 0.0F, 0.0F, 0.5F, 0.5F, 0.5F, 0.5F, 1.25F, 0.75F, 0.0F, 0.0F};
   private static final float[] SPIKE_Y_ROT = new float[]{0.0F, 0.0F, 0.0F, 0.0F, 0.25F, 1.75F, 1.25F, 0.75F, 0.0F, 0.0F, 0.0F, 0.0F};
   private static final float[] SPIKE_Z_ROT = new float[]{0.0F, 0.0F, 0.25F, 1.75F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.75F, 1.25F};
   private static final float[] SPIKE_X = new float[]{0.0F, 0.0F, 8.0F, -8.0F, -8.0F, 8.0F, 8.0F, -8.0F, 0.0F, 0.0F, 8.0F, -8.0F};
   private static final float[] SPIKE_Y = new float[]{-8.0F, -8.0F, -8.0F, -8.0F, 0.0F, 0.0F, 0.0F, 0.0F, 8.0F, 8.0F, 8.0F, 8.0F};
   private static final float[] SPIKE_Z = new float[]{8.0F, -8.0F, 0.0F, 0.0F, -8.0F, -8.0F, 8.0F, 8.0F, 8.0F, -8.0F, 0.0F, 0.0F};
   private static final String EYE = "eye";
   private static final String TAIL_0 = "tail0";
   private static final String TAIL_1 = "tail1";
   private static final String TAIL_2 = "tail2";
   private final ModelPart root;
   private final ModelPart head;
   private final ModelPart eye;
   private final ModelPart[] spikeParts;
   private final ModelPart[] tailParts;

   public GuardianModel(ModelPart var1) {
      super();
      this.root = var1;
      this.spikeParts = new ModelPart[12];
      this.head = var1.getChild("head");

      for(int var2 = 0; var2 < this.spikeParts.length; ++var2) {
         this.spikeParts[var2] = this.head.getChild(createSpikeName(var2));
      }

      this.eye = this.head.getChild("eye");
      this.tailParts = new ModelPart[3];
      this.tailParts[0] = this.head.getChild("tail0");
      this.tailParts[1] = this.tailParts[0].getChild("tail1");
      this.tailParts[2] = this.tailParts[1].getChild("tail2");
   }

   private static String createSpikeName(int var0) {
      return "spike" + var0;
   }

   public static LayerDefinition createBodyLayer() {
      MeshDefinition var0 = new MeshDefinition();
      PartDefinition var1 = var0.getRoot();
      PartDefinition var2 = var1.addOrReplaceChild(
         "head",
         CubeListBuilder.create()
            .texOffs(0, 0)
            .addBox(-6.0F, 10.0F, -8.0F, 12.0F, 12.0F, 16.0F)
            .texOffs(0, 28)
            .addBox(-8.0F, 10.0F, -6.0F, 2.0F, 12.0F, 12.0F)
            .texOffs(0, 28)
            .addBox(6.0F, 10.0F, -6.0F, 2.0F, 12.0F, 12.0F, true)
            .texOffs(16, 40)
            .addBox(-6.0F, 8.0F, -6.0F, 12.0F, 2.0F, 12.0F)
            .texOffs(16, 40)
            .addBox(-6.0F, 22.0F, -6.0F, 12.0F, 2.0F, 12.0F),
         PartPose.ZERO
      );
      CubeListBuilder var3 = CubeListBuilder.create().texOffs(0, 0).addBox(-1.0F, -4.5F, -1.0F, 2.0F, 9.0F, 2.0F);

      for(int var4 = 0; var4 < 12; ++var4) {
         float var5 = getSpikeX(var4, 0.0F, 0.0F);
         float var6 = getSpikeY(var4, 0.0F, 0.0F);
         float var7 = getSpikeZ(var4, 0.0F, 0.0F);
         float var8 = 3.1415927F * SPIKE_X_ROT[var4];
         float var9 = 3.1415927F * SPIKE_Y_ROT[var4];
         float var10 = 3.1415927F * SPIKE_Z_ROT[var4];
         var2.addOrReplaceChild(createSpikeName(var4), var3, PartPose.offsetAndRotation(var5, var6, var7, var8, var9, var10));
      }

      var2.addOrReplaceChild("eye", CubeListBuilder.create().texOffs(8, 0).addBox(-1.0F, 15.0F, 0.0F, 2.0F, 2.0F, 1.0F), PartPose.offset(0.0F, 0.0F, -8.25F));
      PartDefinition var11 = var2.addOrReplaceChild(
         "tail0", CubeListBuilder.create().texOffs(40, 0).addBox(-2.0F, 14.0F, 7.0F, 4.0F, 4.0F, 8.0F), PartPose.ZERO
      );
      PartDefinition var12 = var11.addOrReplaceChild(
         "tail1", CubeListBuilder.create().texOffs(0, 54).addBox(0.0F, 14.0F, 0.0F, 3.0F, 3.0F, 7.0F), PartPose.offset(-1.5F, 0.5F, 14.0F)
      );
      var12.addOrReplaceChild(
         "tail2",
         CubeListBuilder.create().texOffs(41, 32).addBox(0.0F, 14.0F, 0.0F, 2.0F, 2.0F, 6.0F).texOffs(25, 19).addBox(1.0F, 10.5F, 3.0F, 1.0F, 9.0F, 9.0F),
         PartPose.offset(0.5F, 0.5F, 6.0F)
      );
      return LayerDefinition.create(var0, 64, 64);
   }

   @Override
   public ModelPart root() {
      return this.root;
   }

   public void setupAnim(Guardian var1, float var2, float var3, float var4, float var5, float var6) {
      float var7 = var4 - (float)var1.tickCount;
      this.head.yRot = var5 * 0.017453292F;
      this.head.xRot = var6 * 0.017453292F;
      float var8 = (1.0F - var1.getSpikesAnimation(var7)) * 0.55F;
      this.setupSpikes(var4, var8);
      Object var9 = Minecraft.getInstance().getCameraEntity();
      if (var1.hasActiveAttackTarget()) {
         var9 = var1.getActiveAttackTarget();
      }

      if (var9 != null) {
         Vec3 var10 = ((Entity)var9).getEyePosition(0.0F);
         Vec3 var11 = var1.getEyePosition(0.0F);
         double var12 = var10.y - var11.y;
         if (var12 > 0.0) {
            this.eye.y = 0.0F;
         } else {
            this.eye.y = 1.0F;
         }

         Vec3 var14 = var1.getViewVector(0.0F);
         var14 = new Vec3(var14.x, 0.0, var14.z);
         Vec3 var15 = new Vec3(var11.x - var10.x, 0.0, var11.z - var10.z).normalize().yRot(1.5707964F);
         double var16 = var14.dot(var15);
         this.eye.x = Mth.sqrt((float)Math.abs(var16)) * 2.0F * (float)Math.signum(var16);
      }

      this.eye.visible = true;
      float var18 = var1.getTailAnimation(var7);
      this.tailParts[0].yRot = Mth.sin(var18) * 3.1415927F * 0.05F;
      this.tailParts[1].yRot = Mth.sin(var18) * 3.1415927F * 0.1F;
      this.tailParts[2].yRot = Mth.sin(var18) * 3.1415927F * 0.15F;
   }

   private void setupSpikes(float var1, float var2) {
      for(int var3 = 0; var3 < 12; ++var3) {
         this.spikeParts[var3].x = getSpikeX(var3, var1, var2);
         this.spikeParts[var3].y = getSpikeY(var3, var1, var2);
         this.spikeParts[var3].z = getSpikeZ(var3, var1, var2);
      }
   }

   private static float getSpikeOffset(int var0, float var1, float var2) {
      return 1.0F + Mth.cos(var1 * 1.5F + (float)var0) * 0.01F - var2;
   }

   private static float getSpikeX(int var0, float var1, float var2) {
      return SPIKE_X[var0] * getSpikeOffset(var0, var1, var2);
   }

   private static float getSpikeY(int var0, float var1, float var2) {
      return 16.0F + SPIKE_Y[var0] * getSpikeOffset(var0, var1, var2);
   }

   private static float getSpikeZ(int var0, float var1, float var2) {
      return SPIKE_Z[var0] * getSpikeOffset(var0, var1, var2);
   }
}
