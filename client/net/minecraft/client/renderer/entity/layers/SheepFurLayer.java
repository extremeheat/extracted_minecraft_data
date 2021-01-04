package net.minecraft.client.renderer.entity.layers;

import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.model.SheepFurModel;
import net.minecraft.client.model.SheepModel;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.animal.Sheep;
import net.minecraft.world.item.DyeColor;

public class SheepFurLayer extends RenderLayer<Sheep, SheepModel<Sheep>> {
   private static final ResourceLocation SHEEP_FUR_LOCATION = new ResourceLocation("textures/entity/sheep/sheep_fur.png");
   private final SheepFurModel<Sheep> model = new SheepFurModel();

   public SheepFurLayer(RenderLayerParent<Sheep, SheepModel<Sheep>> var1) {
      super(var1);
   }

   public void render(Sheep var1, float var2, float var3, float var4, float var5, float var6, float var7, float var8) {
      if (!var1.isSheared() && !var1.isInvisible()) {
         this.bindTexture(SHEEP_FUR_LOCATION);
         if (var1.hasCustomName() && "jeb_".equals(var1.getName().getContents())) {
            boolean var17 = true;
            int var10 = var1.tickCount / 25 + var1.getId();
            int var11 = DyeColor.values().length;
            int var12 = var10 % var11;
            int var13 = (var10 + 1) % var11;
            float var14 = ((float)(var1.tickCount % 25) + var4) / 25.0F;
            float[] var15 = Sheep.getColorArray(DyeColor.byId(var12));
            float[] var16 = Sheep.getColorArray(DyeColor.byId(var13));
            GlStateManager.color3f(var15[0] * (1.0F - var14) + var16[0] * var14, var15[1] * (1.0F - var14) + var16[1] * var14, var15[2] * (1.0F - var14) + var16[2] * var14);
         } else {
            float[] var9 = Sheep.getColorArray(var1.getColor());
            GlStateManager.color3f(var9[0], var9[1], var9[2]);
         }

         ((SheepModel)this.getParentModel()).copyPropertiesTo(this.model);
         this.model.prepareMobModel(var1, var2, var3, var4);
         this.model.render(var1, var2, var3, var5, var6, var7, var8);
      }
   }

   public boolean colorsOnDamage() {
      return true;
   }
}
