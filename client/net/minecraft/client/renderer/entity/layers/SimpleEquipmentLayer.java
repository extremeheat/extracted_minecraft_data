package net.minecraft.client.renderer.entity.layers;

import com.mojang.blaze3d.vertex.PoseStack;
import java.util.function.Function;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.client.resources.model.EquipmentClientInfo;
import net.minecraft.core.component.DataComponents;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.equipment.Equippable;

public class SimpleEquipmentLayer<S extends LivingEntityRenderState, RM extends EntityModel<? super S>, EM extends EntityModel<? super S>> extends RenderLayer<S, RM> {
   private final EquipmentLayerRenderer equipmentRenderer;
   private final EquipmentClientInfo.LayerType layer;
   private final Function<S, ItemStack> itemGetter;
   private final EM adultModel;
   private final EM babyModel;

   public SimpleEquipmentLayer(RenderLayerParent<S, RM> var1, EquipmentLayerRenderer var2, EquipmentClientInfo.LayerType var3, Function<S, ItemStack> var4, EM var5, EM var6) {
      super(var1);
      this.equipmentRenderer = var2;
      this.layer = var3;
      this.itemGetter = var4;
      this.adultModel = var5;
      this.babyModel = var6;
   }

   public SimpleEquipmentLayer(RenderLayerParent<S, RM> var1, EquipmentLayerRenderer var2, EM var3, EquipmentClientInfo.LayerType var4, Function<S, ItemStack> var5) {
      this(var1, var2, var4, var5, var3, var3);
   }

   public void submit(PoseStack var1, SubmitNodeCollector var2, int var3, S var4, float var5, float var6) {
      ItemStack var7 = (ItemStack)this.itemGetter.apply(var4);
      Equippable var8 = (Equippable)var7.get(DataComponents.EQUIPPABLE);
      if (var8 != null && !var8.assetId().isEmpty()) {
         EntityModel var9 = var4.isBaby ? this.babyModel : this.adultModel;
         this.equipmentRenderer.renderLayers(this.layer, (ResourceKey)var8.assetId().get(), var9, var4, var7, var1, var2, var3, var4.outlineColor);
      }
   }
}
