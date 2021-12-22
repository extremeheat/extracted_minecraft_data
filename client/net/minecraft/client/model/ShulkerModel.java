package net.minecraft.client.model;

import com.google.common.collect.ImmutableList;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.monster.Shulker;

public class ShulkerModel<T extends Shulker> extends ListModel<T> {
   private static final String LID = "lid";
   private static final String BASE = "base";
   private final ModelPart base;
   private final ModelPart lid;
   private final ModelPart head;

   public ShulkerModel(ModelPart var1) {
      super(RenderType::entityCutoutNoCullZOffset);
      this.lid = var1.getChild("lid");
      this.base = var1.getChild("base");
      this.head = var1.getChild("head");
   }

   public static LayerDefinition createBodyLayer() {
      MeshDefinition var0 = new MeshDefinition();
      PartDefinition var1 = var0.getRoot();
      var1.addOrReplaceChild("lid", CubeListBuilder.create().texOffs(0, 0).addBox(-8.0F, -16.0F, -8.0F, 16.0F, 12.0F, 16.0F), PartPose.offset(0.0F, 24.0F, 0.0F));
      var1.addOrReplaceChild("base", CubeListBuilder.create().texOffs(0, 28).addBox(-8.0F, -8.0F, -8.0F, 16.0F, 8.0F, 16.0F), PartPose.offset(0.0F, 24.0F, 0.0F));
      var1.addOrReplaceChild("head", CubeListBuilder.create().texOffs(0, 52).addBox(-3.0F, 0.0F, -3.0F, 6.0F, 6.0F, 6.0F), PartPose.offset(0.0F, 12.0F, 0.0F));
      return LayerDefinition.create(var0, 64, 64);
   }

   public void setupAnim(T var1, float var2, float var3, float var4, float var5, float var6) {
      float var7 = var4 - (float)var1.tickCount;
      float var8 = (0.5F + var1.getClientPeekAmount(var7)) * 3.1415927F;
      float var9 = -1.0F + Mth.sin(var8);
      float var10 = 0.0F;
      if (var8 > 3.1415927F) {
         var10 = Mth.sin(var4 * 0.1F) * 0.7F;
      }

      this.lid.setPos(0.0F, 16.0F + Mth.sin(var8) * 8.0F + var10, 0.0F);
      if (var1.getClientPeekAmount(var7) > 0.3F) {
         this.lid.yRot = var9 * var9 * var9 * var9 * 3.1415927F * 0.125F;
      } else {
         this.lid.yRot = 0.0F;
      }

      this.head.xRot = var6 * 0.017453292F;
      this.head.yRot = (var1.yHeadRot - 180.0F - var1.yBodyRot) * 0.017453292F;
   }

   public Iterable<ModelPart> parts() {
      return ImmutableList.of(this.base, this.lid);
   }

   public ModelPart getLid() {
      return this.lid;
   }

   public ModelPart getHead() {
      return this.head;
   }
}
