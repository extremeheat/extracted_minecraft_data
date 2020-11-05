package net.minecraft.client.model;

import com.google.common.collect.ImmutableList;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.monster.Guardian;
import net.minecraft.world.phys.Vec3;

public class GuardianModel extends ListModel<Guardian> {
   private static final float[] SPIKE_X_ROT = new float[]{1.75F, 0.25F, 0.0F, 0.0F, 0.5F, 0.5F, 0.5F, 0.5F, 1.25F, 0.75F, 0.0F, 0.0F};
   private static final float[] SPIKE_Y_ROT = new float[]{0.0F, 0.0F, 0.0F, 0.0F, 0.25F, 1.75F, 1.25F, 0.75F, 0.0F, 0.0F, 0.0F, 0.0F};
   private static final float[] SPIKE_Z_ROT = new float[]{0.0F, 0.0F, 0.25F, 1.75F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.75F, 1.25F};
   private static final float[] SPIKE_X = new float[]{0.0F, 0.0F, 8.0F, -8.0F, -8.0F, 8.0F, 8.0F, -8.0F, 0.0F, 0.0F, 8.0F, -8.0F};
   private static final float[] SPIKE_Y = new float[]{-8.0F, -8.0F, -8.0F, -8.0F, 0.0F, 0.0F, 0.0F, 0.0F, 8.0F, 8.0F, 8.0F, 8.0F};
   private static final float[] SPIKE_Z = new float[]{8.0F, -8.0F, 0.0F, 0.0F, -8.0F, -8.0F, 8.0F, 8.0F, 8.0F, -8.0F, 0.0F, 0.0F};
   private final ModelPart head;
   private final ModelPart eye;
   private final ModelPart[] spikeParts;
   private final ModelPart[] tailParts;

   public GuardianModel() {
      super();
      this.texWidth = 64;
      this.texHeight = 64;
      this.spikeParts = new ModelPart[12];
      this.head = new ModelPart(this);
      this.head.texOffs(0, 0).addBox(-6.0F, 10.0F, -8.0F, 12.0F, 12.0F, 16.0F);
      this.head.texOffs(0, 28).addBox(-8.0F, 10.0F, -6.0F, 2.0F, 12.0F, 12.0F);
      this.head.texOffs(0, 28).addBox(6.0F, 10.0F, -6.0F, 2.0F, 12.0F, 12.0F, true);
      this.head.texOffs(16, 40).addBox(-6.0F, 8.0F, -6.0F, 12.0F, 2.0F, 12.0F);
      this.head.texOffs(16, 40).addBox(-6.0F, 22.0F, -6.0F, 12.0F, 2.0F, 12.0F);

      for(int var1 = 0; var1 < this.spikeParts.length; ++var1) {
         this.spikeParts[var1] = new ModelPart(this, 0, 0);
         this.spikeParts[var1].addBox(-1.0F, -4.5F, -1.0F, 2.0F, 9.0F, 2.0F);
         this.head.addChild(this.spikeParts[var1]);
      }

      this.eye = new ModelPart(this, 8, 0);
      this.eye.addBox(-1.0F, 15.0F, 0.0F, 2.0F, 2.0F, 1.0F);
      this.head.addChild(this.eye);
      this.tailParts = new ModelPart[3];
      this.tailParts[0] = new ModelPart(this, 40, 0);
      this.tailParts[0].addBox(-2.0F, 14.0F, 7.0F, 4.0F, 4.0F, 8.0F);
      this.tailParts[1] = new ModelPart(this, 0, 54);
      this.tailParts[1].addBox(0.0F, 14.0F, 0.0F, 3.0F, 3.0F, 7.0F);
      this.tailParts[2] = new ModelPart(this);
      this.tailParts[2].texOffs(41, 32).addBox(0.0F, 14.0F, 0.0F, 2.0F, 2.0F, 6.0F);
      this.tailParts[2].texOffs(25, 19).addBox(1.0F, 10.5F, 3.0F, 1.0F, 9.0F, 9.0F);
      this.head.addChild(this.tailParts[0]);
      this.tailParts[0].addChild(this.tailParts[1]);
      this.tailParts[1].addChild(this.tailParts[2]);
      this.setupSpikes(0.0F, 0.0F);
   }

   public Iterable<ModelPart> parts() {
      return ImmutableList.of(this.head);
   }

   public void setupAnim(Guardian var1, float var2, float var3, float var4, float var5, float var6) {
      float var7 = var4 - (float)var1.tickCount;
      this.head.yRot = var5 * 0.017453292F;
      this.head.xRot = var6 * 0.017453292F;
      float var8 = (1.0F - var1.getSpikesAnimation(var7)) * 0.55F;
      this.setupSpikes(var4, var8);
      this.eye.z = -8.25F;
      Object var9 = Minecraft.getInstance().getCameraEntity();
      if (var1.hasActiveAttackTarget()) {
         var9 = var1.getActiveAttackTarget();
      }

      if (var9 != null) {
         Vec3 var10 = ((Entity)var9).getEyePosition(0.0F);
         Vec3 var11 = var1.getEyePosition(0.0F);
         double var12 = var10.y - var11.y;
         if (var12 > 0.0D) {
            this.eye.y = 0.0F;
         } else {
            this.eye.y = 1.0F;
         }

         Vec3 var14 = var1.getViewVector(0.0F);
         var14 = new Vec3(var14.x, 0.0D, var14.z);
         Vec3 var15 = (new Vec3(var11.x - var10.x, 0.0D, var11.z - var10.z)).normalize().yRot(1.5707964F);
         double var16 = var14.dot(var15);
         this.eye.x = Mth.sqrt((float)Math.abs(var16)) * 2.0F * (float)Math.signum(var16);
      }

      this.eye.visible = true;
      float var18 = var1.getTailAnimation(var7);
      this.tailParts[0].yRot = Mth.sin(var18) * 3.1415927F * 0.05F;
      this.tailParts[1].yRot = Mth.sin(var18) * 3.1415927F * 0.1F;
      this.tailParts[1].x = -1.5F;
      this.tailParts[1].y = 0.5F;
      this.tailParts[1].z = 14.0F;
      this.tailParts[2].yRot = Mth.sin(var18) * 3.1415927F * 0.15F;
      this.tailParts[2].x = 0.5F;
      this.tailParts[2].y = 0.5F;
      this.tailParts[2].z = 6.0F;
   }

   private void setupSpikes(float var1, float var2) {
      for(int var3 = 0; var3 < 12; ++var3) {
         this.spikeParts[var3].xRot = 3.1415927F * SPIKE_X_ROT[var3];
         this.spikeParts[var3].yRot = 3.1415927F * SPIKE_Y_ROT[var3];
         this.spikeParts[var3].zRot = 3.1415927F * SPIKE_Z_ROT[var3];
         this.spikeParts[var3].x = SPIKE_X[var3] * (1.0F + Mth.cos(var1 * 1.5F + (float)var3) * 0.01F - var2);
         this.spikeParts[var3].y = 16.0F + SPIKE_Y[var3] * (1.0F + Mth.cos(var1 * 1.5F + (float)var3) * 0.01F - var2);
         this.spikeParts[var3].z = SPIKE_Z[var3] * (1.0F + Mth.cos(var1 * 1.5F + (float)var3) * 0.01F - var2);
      }

   }
}
