package net.minecraft.client.model;

import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.world.level.block.entity.BellBlockEntity;

public class BellModel extends Model {
   private static final String BELL_BODY = "bell_body";
   private final ModelPart bellBody;

   public BellModel(ModelPart var1) {
      super(var1, RenderType::entitySolid);
      this.bellBody = var1.getChild("bell_body");
   }

   public static LayerDefinition createBodyLayer() {
      MeshDefinition var0 = new MeshDefinition();
      PartDefinition var1 = var0.getRoot();
      PartDefinition var2 = var1.addOrReplaceChild("bell_body", CubeListBuilder.create().texOffs(0, 0).addBox(-3.0F, -6.0F, -3.0F, 6.0F, 7.0F, 6.0F), PartPose.offset(8.0F, 12.0F, 8.0F));
      var2.addOrReplaceChild("bell_base", CubeListBuilder.create().texOffs(0, 13).addBox(4.0F, 4.0F, 4.0F, 8.0F, 2.0F, 8.0F), PartPose.offset(-8.0F, -12.0F, -8.0F));
      return LayerDefinition.create(var0, 32, 32);
   }

   public void setupAnim(BellBlockEntity var1, float var2) {
      float var3 = (float)var1.ticks + var2;
      float var4 = 0.0F;
      float var5 = 0.0F;
      if (var1.shaking) {
         float var6 = Mth.sin(var3 / 3.1415927F) / (4.0F + var3 / 3.0F);
         if (var1.clickDirection == Direction.NORTH) {
            var4 = -var6;
         } else if (var1.clickDirection == Direction.SOUTH) {
            var4 = var6;
         } else if (var1.clickDirection == Direction.EAST) {
            var5 = -var6;
         } else if (var1.clickDirection == Direction.WEST) {
            var5 = var6;
         }
      }

      this.bellBody.xRot = var4;
      this.bellBody.zRot = var5;
   }
}
