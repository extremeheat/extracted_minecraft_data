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
import net.minecraft.client.resources.model.EquipmentClientInfo;
import net.minecraft.core.component.DataComponents;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Crackiness;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.equipment.Equippable;

public class WolfArmorLayer extends RenderLayer<WolfRenderState, WolfModel> {
   private final WolfModel adultModel;
   private final WolfModel babyModel;
   private final EquipmentLayerRenderer equipmentRenderer;
   private static final Map<Crackiness.Level, ResourceLocation> ARMOR_CRACK_LOCATIONS;

   public WolfArmorLayer(RenderLayerParent<WolfRenderState, WolfModel> var1, EntityModelSet var2, EquipmentLayerRenderer var3) {
      super(var1);
      this.adultModel = new WolfModel(var2.bakeLayer(ModelLayers.WOLF_ARMOR));
      this.babyModel = new WolfModel(var2.bakeLayer(ModelLayers.WOLF_BABY_ARMOR));
      this.equipmentRenderer = var3;
   }

   public void render(PoseStack var1, MultiBufferSource var2, int var3, WolfRenderState var4, float var5, float var6) {
      ItemStack var7 = var4.bodyArmorItem;
      Equippable var8 = (Equippable)var7.get(DataComponents.EQUIPPABLE);
      if (var8 != null && !var8.assetId().isEmpty()) {
         WolfModel var9 = var4.isBaby ? this.babyModel : this.adultModel;
         var9.setupAnim(var4);
         this.equipmentRenderer.renderLayers(EquipmentClientInfo.LayerType.WOLF_BODY, (ResourceKey)var8.assetId().get(), var9, var7, var1, var2, var3);
         this.maybeRenderCracks(var1, var2, var3, var7, var9);
      }
   }

   private void maybeRenderCracks(PoseStack var1, MultiBufferSource var2, int var3, ItemStack var4, Model var5) {
      Crackiness.Level var6 = Crackiness.WOLF_ARMOR.byDamage(var4);
      if (var6 != Crackiness.Level.NONE) {
         ResourceLocation var7 = (ResourceLocation)ARMOR_CRACK_LOCATIONS.get(var6);
         VertexConsumer var8 = var2.getBuffer(RenderType.armorTranslucent(var7));
         var5.renderToBuffer(var1, var8, var3, OverlayTexture.NO_OVERLAY);
      }
   }

   static {
      ARMOR_CRACK_LOCATIONS = Map.of(Crackiness.Level.LOW, ResourceLocation.withDefaultNamespace("textures/entity/wolf/wolf_armor_crackiness_low.png"), Crackiness.Level.MEDIUM, ResourceLocation.withDefaultNamespace("textures/entity/wolf/wolf_armor_crackiness_medium.png"), Crackiness.Level.HIGH, ResourceLocation.withDefaultNamespace("textures/entity/wolf/wolf_armor_crackiness_high.png"));
   }
}
