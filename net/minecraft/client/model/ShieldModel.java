package net.minecraft.client.model;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.RenderType;

public class ShieldModel extends Model {
   private final ModelPart plate;
   private final ModelPart handle;

   public ShieldModel() {
      super(RenderType::entitySolid);
      this.texWidth = 64;
      this.texHeight = 64;
      this.plate = new ModelPart(this, 0, 0);
      this.plate.addBox(-6.0F, -11.0F, -2.0F, 12.0F, 22.0F, 1.0F, 0.0F);
      this.handle = new ModelPart(this, 26, 0);
      this.handle.addBox(-1.0F, -3.0F, -1.0F, 2.0F, 6.0F, 6.0F, 0.0F);
   }

   public ModelPart plate() {
      return this.plate;
   }

   public ModelPart handle() {
      return this.handle;
   }

   public void renderToBuffer(PoseStack var1, VertexConsumer var2, int var3, int var4, float var5, float var6, float var7, float var8) {
      this.plate.render(var1, var2, var3, var4, var5, var6, var7, var8);
      this.handle.render(var1, var2, var3, var4, var5, var6, var7, var8);
   }
}
