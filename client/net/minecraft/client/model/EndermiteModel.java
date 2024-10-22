package net.minecraft.client.model;

import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.util.Mth;

public class EndermiteModel extends EntityModel<EntityRenderState> {
   private static final int BODY_COUNT = 4;
   private static final int[][] BODY_SIZES = new int[][]{{4, 3, 2}, {6, 4, 5}, {3, 3, 1}, {1, 2, 1}};
   private static final int[][] BODY_TEXS = new int[][]{{0, 0}, {0, 5}, {0, 14}, {0, 18}};
   private final ModelPart[] bodyParts = new ModelPart[4];

   public EndermiteModel(ModelPart var1) {
      super(var1);

      for (int var2 = 0; var2 < 4; var2++) {
         this.bodyParts[var2] = var1.getChild(createSegmentName(var2));
      }
   }

   private static String createSegmentName(int var0) {
      return "segment" + var0;
   }

   public static LayerDefinition createBodyLayer() {
      MeshDefinition var0 = new MeshDefinition();
      PartDefinition var1 = var0.getRoot();
      float var2 = -3.5F;

      for (int var3 = 0; var3 < 4; var3++) {
         var1.addOrReplaceChild(
            createSegmentName(var3),
            CubeListBuilder.create()
               .texOffs(BODY_TEXS[var3][0], BODY_TEXS[var3][1])
               .addBox(
                  (float)BODY_SIZES[var3][0] * -0.5F,
                  0.0F,
                  (float)BODY_SIZES[var3][2] * -0.5F,
                  (float)BODY_SIZES[var3][0],
                  (float)BODY_SIZES[var3][1],
                  (float)BODY_SIZES[var3][2]
               ),
            PartPose.offset(0.0F, (float)(24 - BODY_SIZES[var3][1]), var2)
         );
         if (var3 < 3) {
            var2 += (float)(BODY_SIZES[var3][2] + BODY_SIZES[var3 + 1][2]) * 0.5F;
         }
      }

      return LayerDefinition.create(var0, 64, 32);
   }

   @Override
   public void setupAnim(EntityRenderState var1) {
      super.setupAnim(var1);

      for (int var2 = 0; var2 < this.bodyParts.length; var2++) {
         this.bodyParts[var2].yRot = Mth.cos(var1.ageInTicks * 0.9F + (float)var2 * 0.15F * 3.1415927F) * 3.1415927F * 0.01F * (float)(1 + Math.abs(var2 - 2));
         this.bodyParts[var2].x = Mth.sin(var1.ageInTicks * 0.9F + (float)var2 * 0.15F * 3.1415927F) * 3.1415927F * 0.1F * (float)Math.abs(var2 - 2);
      }
   }
}
