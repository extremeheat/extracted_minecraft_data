package net.minecraft.client.renderer.entity.layers;

import com.mojang.blaze3d.vertex.PoseStack;
import java.util.Map;
import net.minecraft.client.model.Model;
import net.minecraft.client.model.animal.wolf.WolfModel;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.state.WolfRenderState;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.resources.model.EquipmentClientInfo;
import net.minecraft.core.component.DataComponents;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.Crackiness;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.equipment.Equippable;

public class WolfArmorLayer extends RenderLayer<WolfRenderState, WolfModel> {
   private final WolfModel adultModel;
   private final WolfModel babyModel;
   private final EquipmentLayerRenderer equipmentRenderer;
   private static final Map<Crackiness.Level, Identifier> ARMOR_CRACK_LOCATIONS;

   public WolfArmorLayer(RenderLayerParent<WolfRenderState, WolfModel> var1, EntityModelSet var2, EquipmentLayerRenderer var3) {
      super(var1);
      this.adultModel = new WolfModel(var2.bakeLayer(ModelLayers.WOLF_ARMOR));
      this.babyModel = new WolfModel(var2.bakeLayer(ModelLayers.WOLF_BABY_ARMOR));
      this.equipmentRenderer = var3;
   }

   public void submit(PoseStack var1, SubmitNodeCollector var2, int var3, WolfRenderState var4, float var5, float var6) {
      ItemStack var7 = var4.bodyArmorItem;
      Equippable var8 = (Equippable)var7.get(DataComponents.EQUIPPABLE);
      if (var8 != null && !var8.assetId().isEmpty()) {
         WolfModel var9 = var4.isBaby ? this.babyModel : this.adultModel;
         this.equipmentRenderer.renderLayers(EquipmentClientInfo.LayerType.WOLF_BODY, (ResourceKey)var8.assetId().get(), var9, var4, var7, var1, var2, var3, var4.outlineColor);
         this.maybeRenderCracks(var1, var2, var3, var7, var9, var4);
      }
   }

   private void maybeRenderCracks(PoseStack var1, SubmitNodeCollector var2, int var3, ItemStack var4, Model<WolfRenderState> var5, WolfRenderState var6) {
      Crackiness.Level var7 = Crackiness.WOLF_ARMOR.byDamage(var4);
      if (var7 != Crackiness.Level.NONE) {
         Identifier var8 = (Identifier)ARMOR_CRACK_LOCATIONS.get(var7);
         var2.submitModel(var5, var6, var1, RenderTypes.armorTranslucent(var8), var3, OverlayTexture.NO_OVERLAY, var6.outlineColor, (ModelFeatureRenderer.CrumblingOverlay)null);
      }
   }

   static {
      ARMOR_CRACK_LOCATIONS = Map.of(Crackiness.Level.LOW, Identifier.withDefaultNamespace("textures/entity/wolf/wolf_armor_crackiness_low.png"), Crackiness.Level.MEDIUM, Identifier.withDefaultNamespace("textures/entity/wolf/wolf_armor_crackiness_medium.png"), Crackiness.Level.HIGH, Identifier.withDefaultNamespace("textures/entity/wolf/wolf_armor_crackiness_high.png"));
   }
}
