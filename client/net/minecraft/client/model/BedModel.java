package net.minecraft.client.model;

import net.minecraft.client.model.geom.ModelPart;

public class BedModel extends Model {
   private final ModelPart headPiece;
   private final ModelPart footPiece;
   private final ModelPart[] legs = new ModelPart[4];

   public BedModel() {
      super();
      this.texWidth = 64;
      this.texHeight = 64;
      this.headPiece = new ModelPart(this, 0, 0);
      this.headPiece.addBox(0.0F, 0.0F, 0.0F, 16, 16, 6, 0.0F);
      this.footPiece = new ModelPart(this, 0, 22);
      this.footPiece.addBox(0.0F, 0.0F, 0.0F, 16, 16, 6, 0.0F);
      this.legs[0] = new ModelPart(this, 50, 0);
      this.legs[1] = new ModelPart(this, 50, 6);
      this.legs[2] = new ModelPart(this, 50, 12);
      this.legs[3] = new ModelPart(this, 50, 18);
      this.legs[0].addBox(0.0F, 6.0F, -16.0F, 3, 3, 3);
      this.legs[1].addBox(0.0F, 6.0F, 0.0F, 3, 3, 3);
      this.legs[2].addBox(-16.0F, 6.0F, -16.0F, 3, 3, 3);
      this.legs[3].addBox(-16.0F, 6.0F, 0.0F, 3, 3, 3);
      this.legs[0].xRot = 1.5707964F;
      this.legs[1].xRot = 1.5707964F;
      this.legs[2].xRot = 1.5707964F;
      this.legs[3].xRot = 1.5707964F;
      this.legs[0].zRot = 0.0F;
      this.legs[1].zRot = 1.5707964F;
      this.legs[2].zRot = 4.712389F;
      this.legs[3].zRot = 3.1415927F;
   }

   public void render() {
      this.headPiece.render(0.0625F);
      this.footPiece.render(0.0625F);
      this.legs[0].render(0.0625F);
      this.legs[1].render(0.0625F);
      this.legs[2].render(0.0625F);
      this.legs[3].render(0.0625F);
   }

   public void preparePiece(boolean var1) {
      this.headPiece.visible = var1;
      this.footPiece.visible = !var1;
      this.legs[0].visible = !var1;
      this.legs[1].visible = var1;
      this.legs[2].visible = !var1;
      this.legs[3].visible = var1;
   }
}
