package net.minecraft.client.renderer.entity.layers;

import com.google.common.collect.Maps;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import java.util.Iterator;
import java.util.Map;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.ModelManager;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.FastColor;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.armortrim.ArmorTrim;
import net.minecraft.world.item.armortrim.TrimPattern;
import net.minecraft.world.item.component.DyedItemColor;

public class HumanoidArmorLayer<T extends LivingEntity, M extends HumanoidModel<T>, A extends HumanoidModel<T>> extends RenderLayer<T, M> {
   private static final Map<String, ResourceLocation> ARMOR_LOCATION_CACHE = Maps.newHashMap();
   private final A innerModel;
   private final A outerModel;
   private final TextureAtlas armorTrimAtlas;

   public HumanoidArmorLayer(RenderLayerParent<T, M> var1, A var2, A var3, ModelManager var4) {
      super(var1);
      this.innerModel = var2;
      this.outerModel = var3;
      this.armorTrimAtlas = var4.getAtlas(Sheets.ARMOR_TRIMS_SHEET);
   }

   public void render(PoseStack var1, MultiBufferSource var2, int var3, T var4, float var5, float var6, float var7, float var8, float var9, float var10) {
      this.renderArmorPiece(var1, var2, var4, EquipmentSlot.CHEST, var3, this.getArmorModel(EquipmentSlot.CHEST));
      this.renderArmorPiece(var1, var2, var4, EquipmentSlot.LEGS, var3, this.getArmorModel(EquipmentSlot.LEGS));
      this.renderArmorPiece(var1, var2, var4, EquipmentSlot.FEET, var3, this.getArmorModel(EquipmentSlot.FEET));
      this.renderArmorPiece(var1, var2, var4, EquipmentSlot.HEAD, var3, this.getArmorModel(EquipmentSlot.HEAD));
   }

   private void renderArmorPiece(PoseStack var1, MultiBufferSource var2, T var3, EquipmentSlot var4, int var5, A var6) {
      ItemStack var7 = var3.getItemBySlot(var4);
      Item var9 = var7.getItem();
      if (var9 instanceof ArmorItem var8) {
         if (var8.getEquipmentSlot() == var4) {
            ((HumanoidModel)this.getParentModel()).copyPropertiesTo(var6);
            this.setPartVisibility(var6, var4);
            boolean var15 = this.usesInnerModel(var4);
            ArmorMaterial var10 = (ArmorMaterial)var8.getMaterial().value();
            int var11 = var7.is(ItemTags.DYEABLE) ? FastColor.ARGB32.opaque(DyedItemColor.getOrDefault(var7, -6265536)) : -1;
            Iterator var12 = var10.layers().iterator();

            while(var12.hasNext()) {
               ArmorMaterial.Layer var13 = (ArmorMaterial.Layer)var12.next();
               int var14 = var13.dyeable() ? var11 : -1;
               this.renderModel(var1, var2, var5, var6, var14, var13.texture(var15));
            }

            ArmorTrim var16 = (ArmorTrim)var7.get(DataComponents.TRIM);
            if (var16 != null) {
               this.renderTrim(var8.getMaterial(), var1, var2, var5, var16, var6, var15);
            }

            if (var7.hasFoil()) {
               this.renderGlint(var1, var2, var5, var6);
            }

         }
      }
   }

   protected void setPartVisibility(A var1, EquipmentSlot var2) {
      var1.setAllVisible(false);
      switch (var2) {
         case HEAD:
            var1.head.visible = true;
            var1.hat.visible = true;
            break;
         case CHEST:
            var1.body.visible = true;
            var1.rightArm.visible = true;
            var1.leftArm.visible = true;
            break;
         case LEGS:
            var1.body.visible = true;
            var1.rightLeg.visible = true;
            var1.leftLeg.visible = true;
            break;
         case FEET:
            var1.rightLeg.visible = true;
            var1.leftLeg.visible = true;
      }

   }

   private void renderModel(PoseStack var1, MultiBufferSource var2, int var3, A var4, int var5, ResourceLocation var6) {
      VertexConsumer var7 = var2.getBuffer(RenderType.armorCutoutNoCull(var6));
      var4.renderToBuffer(var1, var7, var3, OverlayTexture.NO_OVERLAY, var5);
   }

   private void renderTrim(Holder<ArmorMaterial> var1, PoseStack var2, MultiBufferSource var3, int var4, ArmorTrim var5, A var6, boolean var7) {
      TextureAtlasSprite var8 = this.armorTrimAtlas.getSprite(var7 ? var5.innerTexture(var1) : var5.outerTexture(var1));
      VertexConsumer var9 = var8.wrap(var3.getBuffer(Sheets.armorTrimsSheet(((TrimPattern)var5.pattern().value()).decal())));
      var6.renderToBuffer(var2, var9, var4, OverlayTexture.NO_OVERLAY);
   }

   private void renderGlint(PoseStack var1, MultiBufferSource var2, int var3, A var4) {
      var4.renderToBuffer(var1, var2.getBuffer(RenderType.armorEntityGlint()), var3, OverlayTexture.NO_OVERLAY);
   }

   private A getArmorModel(EquipmentSlot var1) {
      return this.usesInnerModel(var1) ? this.innerModel : this.outerModel;
   }

   private boolean usesInnerModel(EquipmentSlot var1) {
      return var1 == EquipmentSlot.LEGS;
   }
}
