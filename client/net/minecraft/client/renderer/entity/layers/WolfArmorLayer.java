package net.minecraft.client.renderer.entity.layers;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import java.util.Map;
import net.minecraft.client.model.WolfModel;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.FastColor;
import net.minecraft.world.entity.Crackiness;
import net.minecraft.world.entity.animal.Wolf;
import net.minecraft.world.item.AnimalArmorItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.DyedItemColor;

public class WolfArmorLayer extends RenderLayer<Wolf, WolfModel<Wolf>> {
   private final WolfModel<Wolf> model;
   private static final Map<Crackiness.Level, ResourceLocation> ARMOR_CRACK_LOCATIONS;

   public WolfArmorLayer(RenderLayerParent<Wolf, WolfModel<Wolf>> var1, EntityModelSet var2) {
      super(var1);
      this.model = new WolfModel(var2.bakeLayer(ModelLayers.WOLF_ARMOR));
   }

   public void render(PoseStack var1, MultiBufferSource var2, int var3, Wolf var4, float var5, float var6, float var7, float var8, float var9, float var10) {
      if (var4.hasArmor()) {
         ItemStack var11 = var4.getBodyArmorItem();
         Item var13 = var11.getItem();
         if (var13 instanceof AnimalArmorItem) {
            AnimalArmorItem var12 = (AnimalArmorItem)var13;
            if (var12.getBodyType() == AnimalArmorItem.BodyType.CANINE) {
               ((WolfModel)this.getParentModel()).copyPropertiesTo(this.model);
               this.model.prepareMobModel(var4, var5, var6, var7);
               this.model.setupAnim(var4, var5, var6, var8, var9, var10);
               VertexConsumer var14 = var2.getBuffer(RenderType.entityCutoutNoCull(var12.getTexture()));
               this.model.renderToBuffer(var1, var14, var3, OverlayTexture.NO_OVERLAY);
               this.maybeRenderColoredLayer(var1, var2, var3, var11, var12);
               this.maybeRenderCracks(var1, var2, var3, var11);
               return;
            }
         }

      }
   }

   private void maybeRenderColoredLayer(PoseStack var1, MultiBufferSource var2, int var3, ItemStack var4, AnimalArmorItem var5) {
      if (var4.is(ItemTags.DYEABLE)) {
         int var6 = DyedItemColor.getOrDefault(var4, 0);
         if (FastColor.ARGB32.alpha(var6) == 0) {
            return;
         }

         ResourceLocation var7 = var5.getOverlayTexture();
         if (var7 == null) {
            return;
         }

         this.model.renderToBuffer(var1, var2.getBuffer(RenderType.entityCutoutNoCull(var7)), var3, OverlayTexture.NO_OVERLAY, FastColor.ARGB32.opaque(var6));
      }

   }

   private void maybeRenderCracks(PoseStack var1, MultiBufferSource var2, int var3, ItemStack var4) {
      Crackiness.Level var5 = Crackiness.WOLF_ARMOR.byDamage(var4);
      if (var5 != Crackiness.Level.NONE) {
         ResourceLocation var6 = (ResourceLocation)ARMOR_CRACK_LOCATIONS.get(var5);
         VertexConsumer var7 = var2.getBuffer(RenderType.entityTranslucent(var6));
         this.model.renderToBuffer(var1, var7, var3, OverlayTexture.NO_OVERLAY);
      }
   }

   static {
      ARMOR_CRACK_LOCATIONS = Map.of(Crackiness.Level.LOW, ResourceLocation.withDefaultNamespace("textures/entity/wolf/wolf_armor_crackiness_low.png"), Crackiness.Level.MEDIUM, ResourceLocation.withDefaultNamespace("textures/entity/wolf/wolf_armor_crackiness_medium.png"), Crackiness.Level.HIGH, ResourceLocation.withDefaultNamespace("textures/entity/wolf/wolf_armor_crackiness_high.png"));
   }
}
