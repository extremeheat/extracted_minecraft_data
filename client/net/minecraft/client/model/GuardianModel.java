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
   private static final float[] SPIKE_X;
   private static final float[] SPIKE_Y;
   private static final float SPIKE_Y_BASE = 16.0F;
   private static final float[] SPIKE_Z;
   private static final float SPIKE_LENGTH = 9.4F;
   private static final float A2;
   private static final float A12;
   private static final float[] SPIKE_X_ROT_SLAB;
   private static final float[] SPIKE_Y_ROT_SLAB;
   private static final float[] SPIKE_Z_ROT_TOP_SLAB;
   private static final float[] SPIKE_Z_ROT_BOTTOM_SLAB;
   private static final float[] SPIKE_X_SLAB_OFFSET;
   private static final float[] SPIKE_X_SLAB;
   private static final float[] SPIKE_Y_SLAB;
   private static final float SPIKE_Y_BASE_SLAB = 19.0F;
   private static final float[] SPIKE_Z_SLAB;
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

   public static LayerDefinition createBodyLayer(boolean var0) {
      MeshDefinition var1 = new MeshDefinition();
      PartDefinition var2 = var1.getRoot();
      int var3 = var0 ? 3 : 0;
      PartDefinition var4 = var2.addOrReplaceChild(
         "head",
         CubeListBuilder.create()
            .texOffs(0, 0)
            .addBox(-6.0F, (float)(10 + var3 * 2), -8.0F, 12.0F, (float)(12 - var3 * 2), 16.0F)
            .texOffs(0, 28)
            .addBox(-8.0F, (float)(10 + var3 * 2), -6.0F, 2.0F, (float)(12 - var3 * 2), 12.0F)
            .texOffs(0, 28)
            .addBox(6.0F, (float)(10 + var3 * 2), -6.0F, 2.0F, (float)(12 - var3 * 2), 12.0F, true)
            .texOffs(16, 40)
            .addBox(-6.0F, (float)(8 + var3 * 2), -6.0F, 12.0F, 2.0F, 12.0F)
            .texOffs(16, 40)
            .addBox(-6.0F, 22.0F, -6.0F, 12.0F, 2.0F, 12.0F),
         PartPose.ZERO
      );
      CubeListBuilder var5 = CubeListBuilder.create().texOffs(0, 0).addBox(-1.0F, -4.5F, -1.0F, 2.0F, 9.0F, 2.0F);

      for(int var6 = 0; var6 < 12; ++var6) {
         if (var0) {
            float var7 = SPIKE_X_SLAB[var6] + SPIKE_X_SLAB_OFFSET[var6];
            float var8 = 19.0F + SPIKE_Y_SLAB[var6];
            float var9 = SPIKE_Z_SLAB[var6];
            float var10 = SPIKE_X_ROT_SLAB[var6];
            float var11 = SPIKE_Y_ROT_SLAB[var6];
            float var12 = SPIKE_Z_ROT_TOP_SLAB[var6];
            var4.addOrReplaceChild(createSpikeName(var6), var5, PartPose.offsetAndRotation(var7, var8, var9, var10, var11, var12));
         } else {
            float var14 = SPIKE_X[var6];
            float var16 = 16.0F + SPIKE_Y[var6];
            float var17 = SPIKE_Z[var6];
            float var18 = SPIKE_X_ROT[var6];
            float var19 = SPIKE_Y_ROT[var6];
            float var20 = SPIKE_Z_ROT[var6];
            var4.addOrReplaceChild(createSpikeName(var6), var5, PartPose.offsetAndRotation(var14, var16, var17, var18, var19, var20));
         }
      }

      var4.addOrReplaceChild(
         "eye", CubeListBuilder.create().texOffs(8, 0).addBox(-1.0F, (float)(15 + var3), 0.0F, 2.0F, 2.0F, 1.0F), PartPose.offset(0.0F, 0.0F, -8.25F)
      );
      PartDefinition var13 = var4.addOrReplaceChild(
         "tail0", CubeListBuilder.create().texOffs(40, 0).addBox(-2.0F, (float)(14 + var3), 7.0F, 4.0F, 4.0F, 8.0F), PartPose.ZERO
      );
      PartDefinition var15 = var13.addOrReplaceChild(
         "tail1", CubeListBuilder.create().texOffs(0, 54).addBox(0.0F, (float)(14 + var3), 0.0F, 3.0F, 3.0F, 7.0F), PartPose.offset(-1.5F, 0.5F, 14.0F)
      );
      var15.addOrReplaceChild(
         "tail2",
         CubeListBuilder.create()
            .texOffs(41, 32)
            .addBox(0.0F, (float)(14 + var3), 0.0F, 2.0F, 2.0F, 6.0F)
            .texOffs(25, 19)
            .addBox(1.0F, 10.5F + (float)var3, 3.0F, 1.0F, 9.0F, 9.0F),
         PartPose.offset(0.5F, 0.5F, 6.0F)
      );
      return LayerDefinition.create(var1, 64, 64);
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
      if (var1.isToxic()) {
         this.setupSpikesToxic(var4, var8, var1.isVehicle(), var1.isPassenger());
      } else {
         this.setupSpikes(var4, var8);
      }

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
         this.spikeParts[var3].x = SPIKE_X[var3] * getSpikeOffset(var3, var1, var2);
         this.spikeParts[var3].y = 16.0F + SPIKE_Y[var3] * getSpikeOffset(var3, var1, var2);
         this.spikeParts[var3].z = SPIKE_Z[var3] * getSpikeOffset(var3, var1, var2);
         this.spikeParts[var3].zRot = SPIKE_Z_ROT[var3];
      }

      for(int var4 = 0; var4 < 4; ++var4) {
         this.spikeParts[var4].visible = true;
      }
   }

   private void setupSpikesToxic(float var1, float var2, boolean var3, boolean var4) {
      int var5 = var3 ? -1 : 1;
      float[] var6 = var3 ? SPIKE_Z_ROT_BOTTOM_SLAB : SPIKE_Z_ROT_TOP_SLAB;

      for(int var7 = 0; var7 < 12; ++var7) {
         this.spikeParts[var7].x = SPIKE_X_SLAB[var7] * getSpikeOffset(var7, var1, var2) + SPIKE_X_SLAB_OFFSET[var7];
         this.spikeParts[var7].y = 19.0F + (float)var5 * SPIKE_Y_SLAB[var7] * getSpikeOffset(var7, var1, var2);
         this.spikeParts[var7].z = SPIKE_Z_SLAB[var7] * getSpikeOffset(var7, var1, var2);
         this.spikeParts[var7].zRot = var6[var7];
      }

      if (var3 && var4) {
         for(int var9 = 0; var9 < 4; ++var9) {
            this.spikeParts[var9].visible = false;
         }
      } else {
         for(int var8 = 0; var8 < 4; ++var8) {
            this.spikeParts[var8].visible = true;
         }
      }
   }

   private static float getSpikeOffset(int var0, float var1, float var2) {
      return 1.0F + Mth.cos(var1 * 1.5F + (float)var0) * 0.01F - var2;
   }

   static {
      for(int var0 = 0; var0 < 12; ++var0) {
         SPIKE_X_ROT[var0] = 3.1415927F * SPIKE_X_ROT[var0];
         SPIKE_Y_ROT[var0] = 3.1415927F * SPIKE_Y_ROT[var0];
         SPIKE_Z_ROT[var0] = 3.1415927F * SPIKE_Z_ROT[var0];
      }

      SPIKE_X = new float[]{0.0F, 0.0F, 8.0F, -8.0F, -8.0F, 8.0F, 8.0F, -8.0F, 0.0F, 0.0F, 8.0F, -8.0F};
      SPIKE_Y = new float[]{-8.0F, -8.0F, -8.0F, -8.0F, 0.0F, 0.0F, 0.0F, 0.0F, 8.0F, 8.0F, 8.0F, 8.0F};
      SPIKE_Z = new float[]{8.0F, -8.0F, 0.0F, 0.0F, -8.0F, -8.0F, 8.0F, 8.0F, 8.0F, -8.0F, 0.0F, 0.0F};
      A2 = (float)Math.atan2(2.0, 1.0);
      A12 = (float)Math.atan2(1.0, 2.0);
      SPIKE_X_ROT_SLAB = new float[]{A2, A12, -A12, -A2, A2, A12, -A12, -A2, A2, A12, -A12, -A2};
      SPIKE_Y_ROT_SLAB = new float[]{0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F};
      SPIKE_Z_ROT_TOP_SLAB = new float[]{0.0F, 0.0F, 0.0F, 0.0F, 0.5F, 0.5F, 0.5F, 0.5F, -0.5F, -0.5F, -0.5F, -0.5F};
      SPIKE_Z_ROT_BOTTOM_SLAB = new float[]{1.0F, 1.0F, 1.0F, 1.0F, 0.5F, 0.5F, 0.5F, 0.5F, -0.5F, -0.5F, -0.5F, -0.5F};
      SPIKE_X_SLAB_OFFSET = new float[]{0.0F, 0.0F, 0.0F, 0.0F, 3.0F, 3.0F, 3.0F, 3.0F, -3.0F, -3.0F, -3.0F, -3.0F};
      SPIKE_X_SLAB = new float[]{
         0.0F, 0.0F, 0.0F, 0.0F, Mth.cos(A2), Mth.cos(A12), Mth.cos(A12), Mth.cos(A2), -Mth.cos(A2), -Mth.cos(A12), -Mth.cos(A12), -Mth.cos(A2)
      };
      SPIKE_Y_SLAB = new float[]{-Mth.cos(A2), -Mth.cos(A12), -Mth.cos(A12), -Mth.cos(A2), 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F};
      SPIKE_Z_SLAB = new float[]{
         -Mth.sin(A2),
         -Mth.sin(A12),
         Mth.sin(A12),
         Mth.sin(A2),
         -Mth.sin(A2),
         -Mth.sin(A12),
         Mth.sin(A12),
         Mth.sin(A2),
         -Mth.sin(A2),
         -Mth.sin(A12),
         Mth.sin(A12),
         Mth.sin(A2)
      };

      for(int var1 = 0; var1 < 12; ++var1) {
         SPIKE_Z_ROT_TOP_SLAB[var1] *= 3.1415927F;
         SPIKE_Z_ROT_BOTTOM_SLAB[var1] *= 3.1415927F;
         SPIKE_X_SLAB[var1] *= 9.4F;
         SPIKE_Y_SLAB[var1] *= 9.4F;
         SPIKE_Z_SLAB[var1] *= 9.4F;
      }
   }
}
