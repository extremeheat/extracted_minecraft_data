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
import net.minecraft.world.entity.animal.horse.Horse;
import net.minecraft.world.item.AnimalArmorItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.DyedItemColor;

public class HorseArmorLayer extends RenderLayer<Horse, HorseModel<Horse>> {
   private final HorseModel<Horse> model;

   public HorseArmorLayer(RenderLayerParent<Horse, HorseModel<Horse>> var1, EntityModelSet var2) {
      super(var1);
      this.model = new HorseModel<>(var2.bakeLayer(ModelLayers.HORSE_ARMOR));
   }

   // $VF: Could not properly define all variable types!
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   public void render(PoseStack var1, MultiBufferSource var2, int var3, Horse var4, float var5, float var6, float var7, float var8, float var9, float var10) {
      ItemStack var11 = var4.getBodyArmorItem();
      Item var13 = var11.getItem();
      if (var13 instanceof AnimalArmorItem var12 && var12.getBodyType() == AnimalArmorItem.BodyType.EQUESTRIAN) {
         this.getParentModel().copyPropertiesTo(this.model);
         this.model.prepareMobModel(var4, var5, var6, var7);
         this.model.setupAnim(var4, var5, var6, var8, var9, var10);
         float var14;
         float var15;
         float var17;
         if (var11.is(ItemTags.DYEABLE)) {
            int var16 = DyedItemColor.getOrDefault(var11, -6265536);
            var17 = (float)FastColor.ARGB32.red(var16) / 255.0F;
            var14 = (float)FastColor.ARGB32.green(var16) / 255.0F;
            var15 = (float)FastColor.ARGB32.blue(var16) / 255.0F;
         } else {
            var17 = 1.0F;
            var14 = 1.0F;
            var15 = 1.0F;
         }

         VertexConsumer var18 = var2.getBuffer(RenderType.entityCutoutNoCull(var12.getTexture()));
         this.model.renderToBuffer(var1, var18, var3, OverlayTexture.NO_OVERLAY, var17, var14, var15, 1.0F);
         return;
      }
   }
}
