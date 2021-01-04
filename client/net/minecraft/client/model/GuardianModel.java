package net.minecraft.client.model;

import net.minecraft.client.Minecraft;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.monster.Guardian;
import net.minecraft.world.phys.Vec3;

public class GuardianModel extends EntityModel<Guardian> {
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
      this.head.texOffs(0, 0).addBox(-6.0F, 10.0F, -8.0F, 12, 12, 16);
      this.head.texOffs(0, 28).addBox(-8.0F, 10.0F, -6.0F, 2, 12, 12);
      this.head.texOffs(0, 28).addBox(6.0F, 10.0F, -6.0F, 2, 12, 12, true);
      this.head.texOffs(16, 40).addBox(-6.0F, 8.0F, -6.0F, 12, 2, 12);
      this.head.texOffs(16, 40).addBox(-6.0F, 22.0F, -6.0F, 12, 2, 12);

      for(int var1 = 0; var1 < this.spikeParts.length; ++var1) {
         this.spikeParts[var1] = new ModelPart(this, 0, 0);
         this.spikeParts[var1].addBox(-1.0F, -4.5F, -1.0F, 2, 9, 2);
         this.head.addChild(this.spikeParts[var1]);
      }

      this.eye = new ModelPart(this, 8, 0);
      this.eye.addBox(-1.0F, 15.0F, 0.0F, 2, 2, 1);
      this.head.addChild(this.eye);
      this.tailParts = new ModelPart[3];
      this.tailParts[0] = new ModelPart(this, 40, 0);
      this.tailParts[0].addBox(-2.0F, 14.0F, 7.0F, 4, 4, 8);
      this.tailParts[1] = new ModelPart(this, 0, 54);
      this.tailParts[1].addBox(0.0F, 14.0F, 0.0F, 3, 3, 7);
      this.tailParts[2] = new ModelPart(this);
      this.tailParts[2].texOffs(41, 32).addBox(0.0F, 14.0F, 0.0F, 2, 2, 6);
      this.tailParts[2].texOffs(25, 19).addBox(1.0F, 10.5F, 3.0F, 1, 9, 9);
      this.head.addChild(this.tailParts[0]);
      this.tailParts[0].addChild(this.tailParts[1]);
      this.tailParts[1].addChild(this.tailParts[2]);
   }

   public void render(Guardian var1, float var2, float var3, float var4, float var5, float var6, float var7) {
      this.setupAnim(var1, var2, var3, var4, var5, var6, var7);
      this.head.render(var7);
   }

   public void setupAnim(Guardian var1, float var2, float var3, float var4, float var5, float var6, float var7) {
      float var8 = var4 - (float)var1.tickCount;
      this.head.yRot = var5 * 0.017453292F;
      this.head.xRot = var6 * 0.017453292F;
      float var9 = (1.0F - var1.getSpikesAnimation(var8)) * 0.55F;

      for(int var10 = 0; var10 < 12; ++var10) {
         this.spikeParts[var10].xRot = 3.1415927F * SPIKE_X_ROT[var10];
         this.spikeParts[var10].yRot = 3.1415927F * SPIKE_Y_ROT[var10];
         this.spikeParts[var10].zRot = 3.1415927F * SPIKE_Z_ROT[var10];
         this.spikeParts[var10].x = SPIKE_X[var10] * (1.0F + Mth.cos(var4 * 1.5F + (float)var10) * 0.01F - var9);
         this.spikeParts[var10].y = 16.0F + SPIKE_Y[var10] * (1.0F + Mth.cos(var4 * 1.5F + (float)var10) * 0.01F - var9);
         this.spikeParts[var10].z = SPIKE_Z[var10] * (1.0F + Mth.cos(var4 * 1.5F + (float)var10) * 0.01F - var9);
      }

      this.eye.z = -8.25F;
      Object var19 = Minecraft.getInstance().getCameraEntity();
      if (var1.hasActiveAttackTarget()) {
         var19 = var1.getActiveAttackTarget();
      }

      if (var19 != null) {
         Vec3 var11 = ((Entity)var19).getEyePosition(0.0F);
         Vec3 var12 = var1.getEyePosition(0.0F);
         double var13 = var11.y - var12.y;
         if (var13 > 0.0D) {
            this.eye.y = 0.0F;
         } else {
            this.eye.y = 1.0F;
         }

         Vec3 var15 = var1.getViewVector(0.0F);
         var15 = new Vec3(var15.x, 0.0D, var15.z);
         Vec3 var16 = (new Vec3(var12.x - var11.x, 0.0D, var12.z - var11.z)).normalize().yRot(1.5707964F);
         double var17 = var15.dot(var16);
         this.eye.x = Mth.sqrt((float)Math.abs(var17)) * 2.0F * (float)Math.signum(var17);
      }

      this.eye.visible = true;
      float var20 = var1.getTailAnimation(var8);
      this.tailParts[0].yRot = Mth.sin(var20) * 3.1415927F * 0.05F;
      this.tailParts[1].yRot = Mth.sin(var20) * 3.1415927F * 0.1F;
      this.tailParts[1].x = -1.5F;
      this.tailParts[1].y = 0.5F;
      this.tailParts[1].z = 14.0F;
      this.tailParts[2].yRot = Mth.sin(var20) * 3.1415927F * 0.15F;
      this.tailParts[2].x = 0.5F;
      this.tailParts[2].y = 0.5F;
      this.tailParts[2].z = 6.0F;
   }

   // $FF: synthetic method
   public void setupAnim(Entity var1, float var2, float var3, float var4, float var5, float var6, float var7) {
      this.setupAnim((Guardian)var1, var2, var3, var4, var5, var6, var7);
   }

   // $FF: synthetic method
   public void render(Entity var1, float var2, float var3, float var4, float var5, float var6, float var7) {
      this.render((Guardian)var1, var2, var3, var4, var5, var6, var7);
   }
}
