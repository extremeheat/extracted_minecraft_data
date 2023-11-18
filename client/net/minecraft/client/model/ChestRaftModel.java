package net.minecraft.client.model;

import com.google.common.collect.ImmutableList.Builder;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;

public class ChestRaftModel extends RaftModel {
   private static final String CHEST_BOTTOM = "chest_bottom";
   private static final String CHEST_LID = "chest_lid";
   private static final String CHEST_LOCK = "chest_lock";

   public ChestRaftModel(ModelPart var1) {
      super(var1);
   }

   @Override
   protected Builder<ModelPart> createPartsBuilder(ModelPart var1) {
      Builder var2 = super.createPartsBuilder(var1);
      var2.add(var1.getChild("chest_bottom"));
      var2.add(var1.getChild("chest_lid"));
      var2.add(var1.getChild("chest_lock"));
      return var2;
   }

   public static LayerDefinition createBodyModel() {
      MeshDefinition var0 = new MeshDefinition();
      PartDefinition var1 = var0.getRoot();
      RaftModel.createChildren(var1);
      var1.addOrReplaceChild(
         "chest_bottom",
         CubeListBuilder.create().texOffs(0, 76).addBox(0.0F, 0.0F, 0.0F, 12.0F, 8.0F, 12.0F),
         PartPose.offsetAndRotation(-2.0F, -10.1F, -6.0F, 0.0F, -1.5707964F, 0.0F)
      );
      var1.addOrReplaceChild(
         "chest_lid",
         CubeListBuilder.create().texOffs(0, 59).addBox(0.0F, 0.0F, 0.0F, 12.0F, 4.0F, 12.0F),
         PartPose.offsetAndRotation(-2.0F, -14.1F, -6.0F, 0.0F, -1.5707964F, 0.0F)
      );
      var1.addOrReplaceChild(
         "chest_lock",
         CubeListBuilder.create().texOffs(0, 59).addBox(0.0F, 0.0F, 0.0F, 2.0F, 4.0F, 1.0F),
         PartPose.offsetAndRotation(-1.0F, -11.1F, -1.0F, 0.0F, -1.5707964F, 0.0F)
      );
      return LayerDefinition.create(var0, 128, 128);
   }
}
