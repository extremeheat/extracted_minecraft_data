package net.minecraft.client.model;

import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.animal.horse.AbstractChestedHorse;

public class LlamaModel extends EntityModel {
   private final ModelPart head;
   private final ModelPart body;
   private final ModelPart leg0;
   private final ModelPart leg1;
   private final ModelPart leg2;
   private final ModelPart leg3;
   private final ModelPart chest1;
   private final ModelPart chest2;

   public LlamaModel(float var1) {
      this.texWidth = 128;
      this.texHeight = 64;
      this.head = new ModelPart(this, 0, 0);
      this.head.addBox(-2.0F, -14.0F, -10.0F, 4.0F, 4.0F, 9.0F, var1);
      this.head.setPos(0.0F, 7.0F, -6.0F);
      this.head.texOffs(0, 14).addBox(-4.0F, -16.0F, -6.0F, 8.0F, 18.0F, 6.0F, var1);
      this.head.texOffs(17, 0).addBox(-4.0F, -19.0F, -4.0F, 3.0F, 3.0F, 2.0F, var1);
      this.head.texOffs(17, 0).addBox(1.0F, -19.0F, -4.0F, 3.0F, 3.0F, 2.0F, var1);
      this.body = new ModelPart(this, 29, 0);
      this.body.addBox(-6.0F, -10.0F, -7.0F, 12.0F, 18.0F, 10.0F, var1);
      this.body.setPos(0.0F, 5.0F, 2.0F);
      this.chest1 = new ModelPart(this, 45, 28);
      this.chest1.addBox(-3.0F, 0.0F, 0.0F, 8.0F, 8.0F, 3.0F, var1);
      this.chest1.setPos(-8.5F, 3.0F, 3.0F);
      this.chest1.yRot = 1.5707964F;
      this.chest2 = new ModelPart(this, 45, 41);
      this.chest2.addBox(-3.0F, 0.0F, 0.0F, 8.0F, 8.0F, 3.0F, var1);
      this.chest2.setPos(5.5F, 3.0F, 3.0F);
      this.chest2.yRot = 1.5707964F;
      boolean var2 = true;
      boolean var3 = true;
      this.leg0 = new ModelPart(this, 29, 29);
      this.leg0.addBox(-2.0F, 0.0F, -2.0F, 4.0F, 14.0F, 4.0F, var1);
      this.leg0.setPos(-2.5F, 10.0F, 6.0F);
      this.leg1 = new ModelPart(this, 29, 29);
      this.leg1.addBox(-2.0F, 0.0F, -2.0F, 4.0F, 14.0F, 4.0F, var1);
      this.leg1.setPos(2.5F, 10.0F, 6.0F);
      this.leg2 = new ModelPart(this, 29, 29);
      this.leg2.addBox(-2.0F, 0.0F, -2.0F, 4.0F, 14.0F, 4.0F, var1);
      this.leg2.setPos(-2.5F, 10.0F, -4.0F);
      this.leg3 = new ModelPart(this, 29, 29);
      this.leg3.addBox(-2.0F, 0.0F, -2.0F, 4.0F, 14.0F, 4.0F, var1);
      this.leg3.setPos(2.5F, 10.0F, -4.0F);
      --this.leg0.x;
      ++this.leg1.x;
      ModelPart var10000 = this.leg0;
      var10000.z += 0.0F;
      var10000 = this.leg1;
      var10000.z += 0.0F;
      --this.leg2.x;
      ++this.leg3.x;
      --this.leg2.z;
      --this.leg3.z;
   }

   public void setupAnim(AbstractChestedHorse var1, float var2, float var3, float var4, float var5, float var6) {
      this.head.xRot = var6 * 0.017453292F;
      this.head.yRot = var5 * 0.017453292F;
      this.body.xRot = 1.5707964F;
      this.leg0.xRot = Mth.cos(var2 * 0.6662F) * 1.4F * var3;
      this.leg1.xRot = Mth.cos(var2 * 0.6662F + 3.1415927F) * 1.4F * var3;
      this.leg2.xRot = Mth.cos(var2 * 0.6662F + 3.1415927F) * 1.4F * var3;
      this.leg3.xRot = Mth.cos(var2 * 0.6662F) * 1.4F * var3;
      boolean var7 = !var1.isBaby() && var1.hasChest();
      this.chest1.visible = var7;
      this.chest2.visible = var7;
   }

   public void renderToBuffer(PoseStack var1, VertexConsumer var2, int var3, int var4, float var5, float var6, float var7, float var8) {
      if (this.young) {
         float var9 = 2.0F;
         var1.pushPose();
         float var10 = 0.7F;
         var1.scale(0.71428573F, 0.64935064F, 0.7936508F);
         var1.translate(0.0D, 1.3125D, 0.2199999988079071D);
         this.head.render(var1, var2, var3, var4, var5, var6, var7, var8);
         var1.popPose();
         var1.pushPose();
         float var11 = 1.1F;
         var1.scale(0.625F, 0.45454544F, 0.45454544F);
         var1.translate(0.0D, 2.0625D, 0.0D);
         this.body.render(var1, var2, var3, var4, var5, var6, var7, var8);
         var1.popPose();
         var1.pushPose();
         var1.scale(0.45454544F, 0.41322312F, 0.45454544F);
         var1.translate(0.0D, 2.0625D, 0.0D);
         ImmutableList.of(this.leg0, this.leg1, this.leg2, this.leg3, this.chest1, this.chest2).forEach((var8x) -> {
            var8x.render(var1, var2, var3, var4, var5, var6, var7, var8);
         });
         var1.popPose();
      } else {
         ImmutableList.of(this.head, this.body, this.leg0, this.leg1, this.leg2, this.leg3, this.chest1, this.chest2).forEach((var8x) -> {
            var8x.render(var1, var2, var3, var4, var5, var6, var7, var8);
         });
      }

   }
}
