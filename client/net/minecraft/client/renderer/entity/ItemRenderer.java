package net.minecraft.client.renderer.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.SheetedDecalTextureGenerator;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.blaze3d.vertex.VertexMultiConsumer;
import com.mojang.math.MatrixUtil;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.ARGB;
import net.minecraft.world.item.ItemDisplayContext;

public class ItemRenderer {
   public static final ResourceLocation ENCHANTED_GLINT_ARMOR = ResourceLocation.withDefaultNamespace("textures/misc/enchanted_glint_armor.png");
   public static final ResourceLocation ENCHANTED_GLINT_ITEM = ResourceLocation.withDefaultNamespace("textures/misc/enchanted_glint_item.png");
   public static final float SPECIAL_FOIL_UI_SCALE = 0.5F;
   public static final float SPECIAL_FOIL_FIRST_PERSON_SCALE = 0.75F;
   public static final float SPECIAL_FOIL_TEXTURE_SCALE = 0.0078125F;
   public static final int NO_TINT = -1;

   public ItemRenderer() {
      super();
   }

   public static void renderItem(ItemDisplayContext var0, PoseStack var1, MultiBufferSource var2, int var3, int var4, int[] var5, List<BakedQuad> var6, RenderType var7, ItemStackRenderState.FoilType var8) {
      VertexConsumer var9;
      if (var8 == ItemStackRenderState.FoilType.SPECIAL) {
         PoseStack.Pose var10 = var1.last().copy();
         if (var0 == ItemDisplayContext.GUI) {
            MatrixUtil.mulComponentWise(var10.pose(), 0.5F);
         } else if (var0.firstPerson()) {
            MatrixUtil.mulComponentWise(var10.pose(), 0.75F);
         }

         var9 = getSpecialFoilBuffer(var2, var7, var10);
      } else {
         var9 = getFoilBuffer(var2, var7, true, var8 != ItemStackRenderState.FoilType.NONE);
      }

      renderQuadList(var1, var9, var6, var5, var3, var4);
   }

   private static VertexConsumer getSpecialFoilBuffer(MultiBufferSource var0, RenderType var1, PoseStack.Pose var2) {
      return VertexMultiConsumer.create(new SheetedDecalTextureGenerator(var0.getBuffer(useTransparentGlint(var1) ? RenderType.glintTranslucent() : RenderType.glint()), var2, 0.0078125F), var0.getBuffer(var1));
   }

   public static VertexConsumer getFoilBuffer(MultiBufferSource var0, RenderType var1, boolean var2, boolean var3) {
      if (var3) {
         return useTransparentGlint(var1) ? VertexMultiConsumer.create(var0.getBuffer(RenderType.glintTranslucent()), var0.getBuffer(var1)) : VertexMultiConsumer.create(var0.getBuffer(var2 ? RenderType.glint() : RenderType.entityGlint()), var0.getBuffer(var1));
      } else {
         return var0.getBuffer(var1);
      }
   }

   public static List<RenderType> getFoilRenderTypes(RenderType var0, boolean var1, boolean var2) {
      if (var2) {
         return useTransparentGlint(var0) ? List.of(var0, RenderType.glintTranslucent()) : List.of(var0, var1 ? RenderType.glint() : RenderType.entityGlint());
      } else {
         return List.of(var0);
      }
   }

   private static boolean useTransparentGlint(RenderType var0) {
      return Minecraft.useShaderTransparency() && var0 == Sheets.translucentItemSheet();
   }

   private static int getLayerColorSafe(int[] var0, int var1) {
      return var1 >= 0 && var1 < var0.length ? var0[var1] : -1;
   }

   private static void renderQuadList(PoseStack var0, VertexConsumer var1, List<BakedQuad> var2, int[] var3, int var4, int var5) {
      PoseStack.Pose var6 = var0.last();

      for(BakedQuad var8 : var2) {
         float var9;
         float var10;
         float var11;
         float var12;
         if (var8.isTinted()) {
            int var13 = getLayerColorSafe(var3, var8.tintIndex());
            var9 = (float)ARGB.alpha(var13) / 255.0F;
            var10 = (float)ARGB.red(var13) / 255.0F;
            var11 = (float)ARGB.green(var13) / 255.0F;
            var12 = (float)ARGB.blue(var13) / 255.0F;
         } else {
            var9 = 1.0F;
            var10 = 1.0F;
            var11 = 1.0F;
            var12 = 1.0F;
         }

         var1.putBulkData(var6, var8, var10, var11, var12, var9, var4, var5);
      }

   }
}
