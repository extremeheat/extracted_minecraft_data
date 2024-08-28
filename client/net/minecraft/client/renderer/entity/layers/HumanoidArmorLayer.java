package net.minecraft.client.renderer.entity.layers;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.state.HumanoidRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.ModelManager;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.ARGB;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.armortrim.ArmorTrim;
import net.minecraft.world.item.component.DyedItemColor;

public class HumanoidArmorLayer<S extends HumanoidRenderState, M extends HumanoidModel<S>, A extends HumanoidModel<S>> extends RenderLayer<S, M> {
   private final A innerModel;
   private final A outerModel;
   private final A innerModelBaby;
   private final A outerModelBaby;
   private final TextureAtlas armorTrimAtlas;

   public HumanoidArmorLayer(RenderLayerParent<S, M> var1, A var2, A var3, ModelManager var4) {
      this(var1, (A)var2, (A)var3, (A)var2, (A)var3, var4);
   }

   public HumanoidArmorLayer(RenderLayerParent<S, M> var1, A var2, A var3, A var4, A var5, ModelManager var6) {
      super(var1);
      this.innerModel = (A)var2;
      this.outerModel = (A)var3;
      this.innerModelBaby = (A)var4;
      this.outerModelBaby = (A)var5;
      this.armorTrimAtlas = var6.getAtlas(Sheets.ARMOR_TRIMS_SHEET);
   }

   public void render(PoseStack var1, MultiBufferSource var2, int var3, S var4, float var5, float var6) {
      this.renderArmorPiece(var1, var2, (S)var4, var4.chestItem, EquipmentSlot.CHEST, var3, this.getArmorModel((S)var4, EquipmentSlot.CHEST));
      this.renderArmorPiece(var1, var2, (S)var4, var4.legsItem, EquipmentSlot.LEGS, var3, this.getArmorModel((S)var4, EquipmentSlot.LEGS));
      this.renderArmorPiece(var1, var2, (S)var4, var4.feetItem, EquipmentSlot.FEET, var3, this.getArmorModel((S)var4, EquipmentSlot.FEET));
      this.renderArmorPiece(var1, var2, (S)var4, var4.headItem, EquipmentSlot.HEAD, var3, this.getArmorModel((S)var4, EquipmentSlot.HEAD));
   }

   private void renderArmorPiece(PoseStack var1, MultiBufferSource var2, S var3, ItemStack var4, EquipmentSlot var5, int var6, A var7) {
      if (var4.getItem() instanceof ArmorItem var8) {
         if (var8.getEquipmentSlot() == var5) {
            var7.setupAnim(var3);
            this.setPartVisibility((A)var7, var5);
            boolean var15 = this.usesInnerModel(var5);
            ArmorMaterial var10 = var8.getMaterial().value();
            int var11 = var4.is(ItemTags.DYEABLE) ? ARGB.opaque(DyedItemColor.getOrDefault(var4, -6265536)) : -1;

            for (ArmorMaterial.Layer var13 : var10.layers()) {
               int var14 = var13.dyeable() ? var11 : -1;
               this.renderModel(var1, var2, var6, (A)var7, var14, var13.texture(var15));
            }

            ArmorTrim var16 = var4.get(DataComponents.TRIM);
            if (var16 != null) {
               this.renderTrim(var8.getMaterial(), var1, var2, var6, var16, (A)var7, var15);
            }

            if (var4.hasFoil()) {
               this.renderGlint(var1, var2, var6, (A)var7);
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
      VertexConsumer var9 = var8.wrap(var3.getBuffer(Sheets.armorTrimsSheet(var5.pattern().value().decal())));
      var6.renderToBuffer(var2, var9, var4, OverlayTexture.NO_OVERLAY);
   }

   private void renderGlint(PoseStack var1, MultiBufferSource var2, int var3, A var4) {
      var4.renderToBuffer(var1, var2.getBuffer(RenderType.armorEntityGlint()), var3, OverlayTexture.NO_OVERLAY);
   }

   private A getArmorModel(S var1, EquipmentSlot var2) {
      if (this.usesInnerModel(var2)) {
         return var1.isBaby ? this.innerModelBaby : this.innerModel;
      } else {
         return var1.isBaby ? this.outerModelBaby : this.outerModel;
      }
   }

   private boolean usesInnerModel(EquipmentSlot var1) {
      return var1 == EquipmentSlot.LEGS;
   }
}
