package net.minecraft.client.model;

import javax.annotation.Nullable;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;

public class BellModel extends Model<State> {
   private static final String BELL_BODY = "bell_body";
   private final ModelPart bellBody;

   public BellModel(ModelPart var1) {
      super(var1, RenderTypes::entitySolid);
      this.bellBody = var1.getChild("bell_body");
   }

   public static LayerDefinition createBodyLayer() {
      MeshDefinition var0 = new MeshDefinition();
      PartDefinition var1 = var0.getRoot();
      PartDefinition var2 = var1.addOrReplaceChild("bell_body", CubeListBuilder.create().texOffs(0, 0).addBox(-3.0F, -6.0F, -3.0F, 6.0F, 7.0F, 6.0F), PartPose.offset(8.0F, 12.0F, 8.0F));
      var2.addOrReplaceChild("bell_base", CubeListBuilder.create().texOffs(0, 13).addBox(4.0F, 4.0F, 4.0F, 8.0F, 2.0F, 8.0F), PartPose.offset(-8.0F, -12.0F, -8.0F));
      return LayerDefinition.create(var0, 32, 32);
   }

   public void setupAnim(State var1) {
      super.setupAnim(var1);
      float var2 = 0.0F;
      float var3 = 0.0F;
      if (var1.shakeDirection != null) {
         float var4 = Mth.sin(var1.ticks / 3.1415927F) / (4.0F + var1.ticks / 3.0F);
         switch (var1.shakeDirection) {
            case NORTH -> var2 = -var4;
            case SOUTH -> var2 = var4;
            case EAST -> var3 = -var4;
            case WEST -> var3 = var4;
         }
      }

      this.bellBody.xRot = var2;
      this.bellBody.zRot = var3;
   }

   public static record State(float ticks, @Nullable Direction shakeDirection) {
      final float ticks;
      @Nullable
      final Direction shakeDirection;

      public State(float var1, @Nullable Direction var2) {
         super();
         this.ticks = var1;
         this.shakeDirection = var2;
      }
   }
}
