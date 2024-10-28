package net.minecraft.client.renderer.entity.layers;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.SheepFurModel;
import net.minecraft.client.model.SheepModel;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FastColor;
import net.minecraft.world.entity.animal.Sheep;
import net.minecraft.world.item.DyeColor;

public class SheepFurLayer extends RenderLayer<Sheep, SheepModel<Sheep>> {
   private static final ResourceLocation SHEEP_FUR_LOCATION = ResourceLocation.withDefaultNamespace("textures/entity/sheep/sheep_fur.png");
   private final SheepFurModel<Sheep> model;

   public SheepFurLayer(RenderLayerParent<Sheep, SheepModel<Sheep>> var1, EntityModelSet var2) {
      super(var1);
      this.model = new SheepFurModel(var2.bakeLayer(ModelLayers.SHEEP_FUR));
   }

   public void render(PoseStack var1, MultiBufferSource var2, int var3, Sheep var4, float var5, float var6, float var7, float var8, float var9, float var10) {
      if (!var4.isSheared()) {
         boolean var12;
         if (var4.isInvisible()) {
            Minecraft var20 = Minecraft.getInstance();
            var12 = var20.shouldEntityAppearGlowing(var4);
            if (var12) {
               ((SheepModel)this.getParentModel()).copyPropertiesTo(this.model);
               this.model.prepareMobModel(var4, var5, var6, var7);
               this.model.setupAnim(var4, var5, var6, var8, var9, var10);
               VertexConsumer var21 = var2.getBuffer(RenderType.outline(SHEEP_FUR_LOCATION));
               this.model.renderToBuffer(var1, var21, var3, LivingEntityRenderer.getOverlayCoords(var4, 0.0F), -16777216);
            }

         } else {
            int var11;
            if (var4.hasCustomName() && "jeb_".equals(var4.getName().getString())) {
               var12 = true;
               int var13 = var4.tickCount / 25 + var4.getId();
               int var14 = DyeColor.values().length;
               int var15 = var13 % var14;
               int var16 = (var13 + 1) % var14;
               float var17 = ((float)(var4.tickCount % 25) + var7) / 25.0F;
               int var18 = Sheep.getColor(DyeColor.byId(var15));
               int var19 = Sheep.getColor(DyeColor.byId(var16));
               var11 = FastColor.ARGB32.lerp(var17, var18, var19);
            } else {
               var11 = Sheep.getColor(var4.getColor());
            }

            coloredCutoutModelCopyLayerRender(this.getParentModel(), this.model, SHEEP_FUR_LOCATION, var1, var2, var3, var4, var5, var6, var8, var9, var10, var7, var11);
         }
      }
   }
}
