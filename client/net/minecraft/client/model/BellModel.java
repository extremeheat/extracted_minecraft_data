package net.minecraft.client.model;

import net.minecraft.client.model.geom.ModelPart;

public class BellModel extends Model {
   private final ModelPart bellBody;
   private final ModelPart bellBase;

   public BellModel() {
      super();
      this.texWidth = 32;
      this.texHeight = 32;
      this.bellBody = new ModelPart(this, 0, 0);
      this.bellBody.addBox(-3.0F, -6.0F, -3.0F, 6, 7, 6);
      this.bellBody.setPos(8.0F, 12.0F, 8.0F);
      this.bellBase = new ModelPart(this, 0, 13);
      this.bellBase.addBox(4.0F, 4.0F, 4.0F, 8, 2, 8);
      this.bellBase.setPos(-8.0F, -12.0F, -8.0F);
      this.bellBody.addChild(this.bellBase);
   }

   public void render(float var1, float var2, float var3) {
      this.bellBody.xRot = var1;
      this.bellBody.zRot = var2;
      this.bellBody.render(var3);
   }
}
