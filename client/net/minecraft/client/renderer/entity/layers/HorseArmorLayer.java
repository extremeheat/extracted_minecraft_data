package net.minecraft.client.renderer.entity.layers;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.HorseModel;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.FastColor;
import net.minecraft.world.entity.animal.horse.AbstractHorse;
import net.minecraft.world.entity.animal.horse.Horse;
import net.minecraft.world.item.AnimalArmorItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.DyedItemColor;

public class HorseArmorLayer extends RenderLayer<Horse, HorseModel<Horse>> {
   private final HorseModel<Horse> model;

   public HorseArmorLayer(RenderLayerParent<Horse, HorseModel<Horse>> var1, EntityModelSet var2) {
      super(var1);
      this.model = new HorseModel(var2.bakeLayer(ModelLayers.HORSE_ARMOR));
   }

   public void render(PoseStack var1, MultiBufferSource var2, int var3, Horse var4, float var5, float var6, float var7, float var8, float var9, float var10) {
      ItemStack var11 = var4.getBodyArmorItem();
      Item var13 = var11.getItem();
      if (var13 instanceof AnimalArmorItem var12) {
         if (var12.getBodyType() == AnimalArmorItem.BodyType.EQUESTRIAN) {
            ((HorseModel)this.getParentModel()).copyPropertiesTo(this.model);
            this.model.prepareMobModel((AbstractHorse)var4, var5, var6, var7);
            this.model.setupAnim((AbstractHorse)var4, var5, var6, var8, var9, var10);
            int var15;
            if (var11.is(ItemTags.DYEABLE)) {
               var15 = FastColor.ARGB32.opaque(DyedItemColor.getOrDefault(var11, -6265536));
            } else {
               var15 = -1;
            }

            VertexConsumer var14 = var2.getBuffer(RenderType.entityCutoutNoCull(var12.getTexture()));
            this.model.renderToBuffer(var1, var14, var3, OverlayTexture.NO_OVERLAY, var15);
            return;
         }
      }

   }
}
