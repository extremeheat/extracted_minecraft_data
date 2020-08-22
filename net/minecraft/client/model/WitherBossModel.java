package net.minecraft.client.model;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableList.Builder;
import java.util.Arrays;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.boss.wither.WitherBoss;

public class WitherBossModel extends ListModel {
   private final ModelPart[] upperBodyParts;
   private final ModelPart[] heads;
   private final ImmutableList parts;

   public WitherBossModel(float var1) {
      this.texWidth = 64;
      this.texHeight = 64;
      this.upperBodyParts = new ModelPart[3];
      this.upperBodyParts[0] = new ModelPart(this, 0, 16);
      this.upperBodyParts[0].addBox(-10.0F, 3.9F, -0.5F, 20.0F, 3.0F, 3.0F, var1);
      this.upperBodyParts[1] = (new ModelPart(this)).setTexSize(this.texWidth, this.texHeight);
      this.upperBodyParts[1].setPos(-2.0F, 6.9F, -0.5F);
      this.upperBodyParts[1].texOffs(0, 22).addBox(0.0F, 0.0F, 0.0F, 3.0F, 10.0F, 3.0F, var1);
      this.upperBodyParts[1].texOffs(24, 22).addBox(-4.0F, 1.5F, 0.5F, 11.0F, 2.0F, 2.0F, var1);
      this.upperBodyParts[1].texOffs(24, 22).addBox(-4.0F, 4.0F, 0.5F, 11.0F, 2.0F, 2.0F, var1);
      this.upperBodyParts[1].texOffs(24, 22).addBox(-4.0F, 6.5F, 0.5F, 11.0F, 2.0F, 2.0F, var1);
      this.upperBodyParts[2] = new ModelPart(this, 12, 22);
      this.upperBodyParts[2].addBox(0.0F, 0.0F, 0.0F, 3.0F, 6.0F, 3.0F, var1);
      this.heads = new ModelPart[3];
      this.heads[0] = new ModelPart(this, 0, 0);
      this.heads[0].addBox(-4.0F, -4.0F, -4.0F, 8.0F, 8.0F, 8.0F, var1);
      this.heads[1] = new ModelPart(this, 32, 0);
      this.heads[1].addBox(-4.0F, -4.0F, -4.0F, 6.0F, 6.0F, 6.0F, var1);
      this.heads[1].x = -8.0F;
      this.heads[1].y = 4.0F;
      this.heads[2] = new ModelPart(this, 32, 0);
      this.heads[2].addBox(-4.0F, -4.0F, -4.0F, 6.0F, 6.0F, 6.0F, var1);
      this.heads[2].x = 10.0F;
      this.heads[2].y = 4.0F;
      Builder var2 = ImmutableList.builder();
      var2.addAll(Arrays.asList(this.heads));
      var2.addAll(Arrays.asList(this.upperBodyParts));
      this.parts = var2.build();
   }

   public ImmutableList parts() {
      return this.parts;
   }

   public void setupAnim(WitherBoss var1, float var2, float var3, float var4, float var5, float var6) {
      float var7 = Mth.cos(var4 * 0.1F);
      this.upperBodyParts[1].xRot = (0.065F + 0.05F * var7) * 3.1415927F;
      this.upperBodyParts[2].setPos(-2.0F, 6.9F + Mth.cos(this.upperBodyParts[1].xRot) * 10.0F, -0.5F + Mth.sin(this.upperBodyParts[1].xRot) * 10.0F);
      this.upperBodyParts[2].xRot = (0.265F + 0.1F * var7) * 3.1415927F;
      this.heads[0].yRot = var5 * 0.017453292F;
      this.heads[0].xRot = var6 * 0.017453292F;
   }

   public void prepareMobModel(WitherBoss var1, float var2, float var3, float var4) {
      for(int var5 = 1; var5 < 3; ++var5) {
         this.heads[var5].yRot = (var1.getHeadYRot(var5 - 1) - var1.yBodyRot) * 0.017453292F;
         this.heads[var5].xRot = var1.getHeadXRot(var5 - 1) * 0.017453292F;
      }

   }

   // $FF: synthetic method
   public Iterable parts() {
      return this.parts();
   }
}
