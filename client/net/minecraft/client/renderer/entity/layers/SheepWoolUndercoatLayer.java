package net.minecraft.client.renderer.entity.layers;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.SheepFurModel;
import net.minecraft.client.model.SheepModel;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.state.SheepRenderState;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.DyeColor;

public class SheepWoolUndercoatLayer extends RenderLayer<SheepRenderState, SheepModel> {
   private static final ResourceLocation SHEEP_WOOL_UNDERCOAT_LOCATION = ResourceLocation.withDefaultNamespace("textures/entity/sheep/sheep_wool_undercoat.png");
   private final EntityModel<SheepRenderState> adultModel;
   private final EntityModel<SheepRenderState> babyModel;

   public SheepWoolUndercoatLayer(RenderLayerParent<SheepRenderState, SheepModel> var1, EntityModelSet var2) {
      super(var1);
      this.adultModel = new SheepFurModel(var2.bakeLayer(ModelLayers.SHEEP_WOOL_UNDERCOAT));
      this.babyModel = new SheepFurModel(var2.bakeLayer(ModelLayers.SHEEP_BABY_WOOL_UNDERCOAT));
   }

   public void submit(PoseStack var1, SubmitNodeCollector var2, int var3, SheepRenderState var4, float var5, float var6) {
      if (!var4.isInvisible && (var4.isJebSheep || var4.woolColor != DyeColor.WHITE)) {
         EntityModel var7 = var4.isBaby ? this.babyModel : this.adultModel;
         coloredCutoutModelCopyLayerRender(var7, SHEEP_WOOL_UNDERCOAT_LOCATION, var1, var2, var3, var4, var4.getWoolColor(), 1);
      }
   }
}
