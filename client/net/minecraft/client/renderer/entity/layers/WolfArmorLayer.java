package net.minecraft.client.renderer.entity.layers;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import java.util.Map;
import net.minecraft.client.model.Model;
import net.minecraft.client.model.WolfModel;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.state.WolfRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.ARGB;
import net.minecraft.world.entity.Crackiness;
import net.minecraft.world.item.AnimalArmorItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.DyedItemColor;

public class WolfArmorLayer extends RenderLayer<WolfRenderState, WolfModel> {
   private final WolfModel adultModel;
   private final WolfModel babyModel;
   private static final Map<Crackiness.Level, ResourceLocation> ARMOR_CRACK_LOCATIONS = Map.of(
      Crackiness.Level.LOW,
      ResourceLocation.withDefaultNamespace("textures/entity/wolf/wolf_armor_crackiness_low.png"),
      Crackiness.Level.MEDIUM,
      ResourceLocation.withDefaultNamespace("textures/entity/wolf/wolf_armor_crackiness_medium.png"),
      Crackiness.Level.HIGH,
      ResourceLocation.withDefaultNamespace("textures/entity/wolf/wolf_armor_crackiness_high.png")
   );

   public WolfArmorLayer(RenderLayerParent<WolfRenderState, WolfModel> var1, EntityModelSet var2) {
      super(var1);
      this.adultModel = new WolfModel(var2.bakeLayer(ModelLayers.WOLF_ARMOR));
      this.babyModel = new WolfModel(var2.bakeLayer(ModelLayers.WOLF_BABY_ARMOR));
   }

   public void render(PoseStack var1, MultiBufferSource var2, int var3, WolfRenderState var4, float var5, float var6) {
      ItemStack var7 = var4.bodyArmorItem;
      if (var7.getItem() instanceof AnimalArmorItem var8 && var8.getBodyType() == AnimalArmorItem.BodyType.CANINE) {
         WolfModel var11 = var4.isBaby ? this.babyModel : this.adultModel;
         var11.setupAnim(var4);
         VertexConsumer var10 = var2.getBuffer(RenderType.entityCutoutNoCull(var8.getTexture()));
         var11.renderToBuffer(var1, var10, var3, OverlayTexture.NO_OVERLAY);
         this.maybeRenderColoredLayer(var1, var2, var3, var7, var8, var11);
         this.maybeRenderCracks(var1, var2, var3, var7, var11);
         return;
      }
   }

   private void maybeRenderColoredLayer(PoseStack var1, MultiBufferSource var2, int var3, ItemStack var4, AnimalArmorItem var5, Model var6) {
      if (var4.is(ItemTags.DYEABLE)) {
         int var7 = DyedItemColor.getOrDefault(var4, 0);
         if (ARGB.alpha(var7) == 0) {
            return;
         }

         ResourceLocation var8 = var5.getOverlayTexture();
         if (var8 == null) {
            return;
         }

         var6.renderToBuffer(var1, var2.getBuffer(RenderType.entityCutoutNoCull(var8)), var3, OverlayTexture.NO_OVERLAY, ARGB.opaque(var7));
      }
   }

   private void maybeRenderCracks(PoseStack var1, MultiBufferSource var2, int var3, ItemStack var4, Model var5) {
      Crackiness.Level var6 = Crackiness.WOLF_ARMOR.byDamage(var4);
      if (var6 != Crackiness.Level.NONE) {
         ResourceLocation var7 = ARMOR_CRACK_LOCATIONS.get(var6);
         VertexConsumer var8 = var2.getBuffer(RenderType.entityTranslucent(var7));
         var5.renderToBuffer(var1, var8, var3, OverlayTexture.NO_OVERLAY);
      }
   }
}
