package net.minecraft.client.model;

import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.resources.ResourceLocation;

public class TridentModel extends Model {
   public static final ResourceLocation TEXTURE = new ResourceLocation("textures/entity/trident.png");
   private final ModelPart pole;

   public TridentModel() {
      super();
      this.texWidth = 32;
      this.texHeight = 32;
      this.pole = new ModelPart(this, 0, 0);
      this.pole.addBox(-0.5F, -4.0F, -0.5F, 1, 31, 1, 0.0F);
      ModelPart var1 = new ModelPart(this, 4, 0);
      var1.addBox(-1.5F, 0.0F, -0.5F, 3, 2, 1);
      this.pole.addChild(var1);
      ModelPart var2 = new ModelPart(this, 4, 3);
      var2.addBox(-2.5F, -3.0F, -0.5F, 1, 4, 1);
      this.pole.addChild(var2);
      ModelPart var3 = new ModelPart(this, 4, 3);
      var3.mirror = true;
      var3.addBox(1.5F, -3.0F, -0.5F, 1, 4, 1);
      this.pole.addChild(var3);
   }

   public void render() {
      this.pole.render(0.0625F);
   }
}
