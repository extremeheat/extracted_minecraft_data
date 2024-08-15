package net.minecraft.client.renderer.entity.layers;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.HorseModel;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.state.HorseRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.ARGB;
import net.minecraft.world.item.AnimalArmorItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.DyedItemColor;

public class HorseArmorLayer extends RenderLayer<HorseRenderState, HorseModel> {
   private final HorseModel adultModel;
   private final HorseModel babyModel;

   public HorseArmorLayer(RenderLayerParent<HorseRenderState, HorseModel> var1, EntityModelSet var2) {
      super(var1);
      this.adultModel = new HorseModel(var2.bakeLayer(ModelLayers.HORSE_ARMOR));
      this.babyModel = new HorseModel(var2.bakeLayer(ModelLayers.HORSE_BABY_ARMOR));
   }

   public void render(PoseStack var1, MultiBufferSource var2, int var3, HorseRenderState var4, float var5, float var6) {
      ItemStack var7 = var4.bodyArmorItem;
      if (var7.getItem() instanceof AnimalArmorItem var8 && var8.getBodyType() == AnimalArmorItem.BodyType.EQUESTRIAN) {
         HorseModel var12 = var4.isBaby ? this.babyModel : this.adultModel;
         var12.setupAnim(var4);
         int var10;
         if (var7.is(ItemTags.DYEABLE)) {
            var10 = ARGB.opaque(DyedItemColor.getOrDefault(var7, -6265536));
         } else {
            var10 = -1;
         }

         VertexConsumer var11 = var2.getBuffer(RenderType.entityCutoutNoCull(var8.getTexture()));
         var12.renderToBuffer(var1, var11, var3, OverlayTexture.NO_OVERLAY, var10);
         return;
      }
   }
}
