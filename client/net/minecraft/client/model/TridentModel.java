package net.minecraft.client.model;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;

public class TridentModel extends Model {
   public static final ResourceLocation TEXTURE = new ResourceLocation("textures/entity/trident.png");
   private final ModelPart pole = new ModelPart(32, 32, 0, 6);

   public TridentModel() {
      super(RenderType::entitySolid);
      this.pole.addBox(-0.5F, 2.0F, -0.5F, 1.0F, 25.0F, 1.0F, 0.0F);
      ModelPart var1 = new ModelPart(32, 32, 4, 0);
      var1.addBox(-1.5F, 0.0F, -0.5F, 3.0F, 2.0F, 1.0F);
      this.pole.addChild(var1);
      ModelPart var2 = new ModelPart(32, 32, 4, 3);
      var2.addBox(-2.5F, -3.0F, -0.5F, 1.0F, 4.0F, 1.0F);
      this.pole.addChild(var2);
      ModelPart var3 = new ModelPart(32, 32, 0, 0);
      var3.addBox(-0.5F, -4.0F, -0.5F, 1.0F, 4.0F, 1.0F, 0.0F);
      this.pole.addChild(var3);
      ModelPart var4 = new ModelPart(32, 32, 4, 3);
      var4.mirror = true;
      var4.addBox(1.5F, -3.0F, -0.5F, 1.0F, 4.0F, 1.0F);
      this.pole.addChild(var4);
   }

   public void renderToBuffer(PoseStack var1, VertexConsumer var2, int var3, int var4, float var5, float var6, float var7, float var8) {
      this.pole.render(var1, var2, var3, var4, var5, var6, var7, var8);
   }
}
