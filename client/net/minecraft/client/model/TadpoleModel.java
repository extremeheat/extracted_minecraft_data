package net.minecraft.client.model;

import com.google.common.collect.ImmutableList;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.animal.frog.Tadpole;

public class TadpoleModel<T extends Tadpole> extends AgeableListModel<T> {
   private final ModelPart root;
   private final ModelPart tail;

   public TadpoleModel(ModelPart var1) {
      super(true, 8.0F, 3.35F);
      this.root = var1;
      this.tail = var1.getChild("tail");
   }

   public static LayerDefinition createBodyLayer() {
      MeshDefinition var0 = new MeshDefinition();
      PartDefinition var1 = var0.getRoot();
      float var2 = 0.0F;
      float var3 = 22.0F;
      float var4 = -3.0F;
      var1.addOrReplaceChild("body", CubeListBuilder.create().texOffs(0, 0).addBox(-1.5F, -1.0F, 0.0F, 3.0F, 2.0F, 3.0F), PartPose.offset(0.0F, 22.0F, -3.0F));
      var1.addOrReplaceChild("tail", CubeListBuilder.create().texOffs(0, 0).addBox(0.0F, -1.0F, 0.0F, 0.0F, 2.0F, 7.0F), PartPose.offset(0.0F, 22.0F, 0.0F));
      return LayerDefinition.create(var0, 16, 16);
   }

   protected Iterable<ModelPart> headParts() {
      return ImmutableList.of(this.root);
   }

   protected Iterable<ModelPart> bodyParts() {
      return ImmutableList.of(this.tail);
   }

   public void setupAnim(T var1, float var2, float var3, float var4, float var5, float var6) {
      float var7 = var1.isInWater() ? 1.0F : 1.5F;
      this.tail.yRot = -var7 * 0.25F * Mth.sin(0.3F * var4);
   }
}
