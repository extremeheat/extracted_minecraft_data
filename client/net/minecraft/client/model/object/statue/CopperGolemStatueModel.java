package net.minecraft.client.model.object.statue;

import net.minecraft.client.model.Model;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.core.Direction;

public class CopperGolemStatueModel extends Model<Direction> {
   public CopperGolemStatueModel(ModelPart var1) {
      super(var1, RenderTypes::entityCutoutNoCull);
   }

   public void setupAnim(Direction var1) {
      this.root.y = 0.0F;
      this.root.yRot = var1.getOpposite().toYRot() * 0.017453292F;
      this.root.zRot = 3.1415927F;
   }
}
