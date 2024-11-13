package net.minecraft.client.renderer.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.SheetedDecalTextureGenerator;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.blaze3d.vertex.VertexMultiConsumer;
import com.mojang.math.MatrixUtil;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.item.ItemModelResolver;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.ARGB;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

public class ItemRenderer {
   public static final ResourceLocation ENCHANTED_GLINT_ENTITY = ResourceLocation.withDefaultNamespace("textures/misc/enchanted_glint_entity.png");
   public static final ResourceLocation ENCHANTED_GLINT_ITEM = ResourceLocation.withDefaultNamespace("textures/misc/enchanted_glint_item.png");
   public static final int GUI_SLOT_CENTER_X = 8;
   public static final int GUI_SLOT_CENTER_Y = 8;
   public static final int ITEM_DECORATION_BLIT_OFFSET = 200;
   public static final float COMPASS_FOIL_UI_SCALE = 0.5F;
   public static final float COMPASS_FOIL_FIRST_PERSON_SCALE = 0.75F;
   public static final float COMPASS_FOIL_TEXTURE_SCALE = 0.0078125F;
   public static final int NO_TINT = -1;
   private final ItemModelResolver resolver;
   private final ItemStackRenderState scratchItemStackRenderState = new ItemStackRenderState();

   public ItemRenderer(ItemModelResolver var1) {
      super();
      this.resolver = var1;
   }

   private static void renderModelLists(BakedModel var0, int[] var1, int var2, int var3, PoseStack var4, VertexConsumer var5) {
      RandomSource var6 = RandomSource.create();
      long var7 = 42L;

      for(Direction var12 : Direction.values()) {
         var6.setSeed(42L);
         renderQuadList(var4, var5, var0.getQuads((BlockState)null, var12, var6), var1, var2, var3);
      }

      var6.setSeed(42L);
      renderQuadList(var4, var5, var0.getQuads((BlockState)null, (Direction)null, var6), var1, var2, var3);
   }

   public static void renderItem(ItemDisplayContext var0, PoseStack var1, MultiBufferSource var2, int var3, int var4, int[] var5, BakedModel var6, RenderType var7, ItemStackRenderState.FoilType var8) {
      VertexConsumer var9;
      if (var8 == ItemStackRenderState.FoilType.SPECIAL) {
         PoseStack.Pose var10 = var1.last().copy();
         if (var0 == ItemDisplayContext.GUI) {
            MatrixUtil.mulComponentWise(var10.pose(), 0.5F);
         } else if (var0.firstPerson()) {
            MatrixUtil.mulComponentWise(var10.pose(), 0.75F);
         }

         var9 = getCompassFoilBuffer(var2, var7, var10);
      } else {
         var9 = getFoilBuffer(var2, var7, true, var8 != ItemStackRenderState.FoilType.NONE);
      }

      renderModelLists(var6, var5, var3, var4, var1, var9);
   }

   public static VertexConsumer getArmorFoilBuffer(MultiBufferSource var0, RenderType var1, boolean var2) {
      return var2 ? VertexMultiConsumer.create(var0.getBuffer(RenderType.armorEntityGlint()), var0.getBuffer(var1)) : var0.getBuffer(var1);
   }

   private static VertexConsumer getCompassFoilBuffer(MultiBufferSource var0, RenderType var1, PoseStack.Pose var2) {
      return VertexMultiConsumer.create(new SheetedDecalTextureGenerator(var0.getBuffer(RenderType.glint()), var2, 0.0078125F), var0.getBuffer(var1));
   }

   public static VertexConsumer getFoilBuffer(MultiBufferSource var0, RenderType var1, boolean var2, boolean var3) {
      if (var3) {
         return Minecraft.useShaderTransparency() && var1 == Sheets.translucentItemSheet() ? VertexMultiConsumer.create(var0.getBuffer(RenderType.glintTranslucent()), var0.getBuffer(var1)) : VertexMultiConsumer.create(var0.getBuffer(var2 ? RenderType.glint() : RenderType.entityGlint()), var0.getBuffer(var1));
      } else {
         return var0.getBuffer(var1);
      }
   }

   private static int getLayerColorSafe(int[] var0, int var1) {
      return var1 >= var0.length ? -1 : var0[var1];
   }

   private static void renderQuadList(PoseStack var0, VertexConsumer var1, List<BakedQuad> var2, int[] var3, int var4, int var5) {
      PoseStack.Pose var6 = var0.last();

      for(BakedQuad var8 : var2) {
         float var9;
         float var10;
         float var11;
         float var12;
         if (var8.isTinted()) {
            int var13 = getLayerColorSafe(var3, var8.getTintIndex());
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

   public void renderStatic(ItemStack var1, ItemDisplayContext var2, int var3, int var4, PoseStack var5, MultiBufferSource var6, @Nullable Level var7, int var8) {
      this.renderStatic((LivingEntity)null, var1, var2, false, var5, var6, var7, var3, var4, var8);
   }

   public void renderStatic(@Nullable LivingEntity var1, ItemStack var2, ItemDisplayContext var3, boolean var4, PoseStack var5, MultiBufferSource var6, @Nullable Level var7, int var8, int var9, int var10) {
      this.resolver.updateForTopItem(this.scratchItemStackRenderState, var2, var3, var4, var7, var1, var10);
      this.scratchItemStackRenderState.render(var5, var6, var8, var9);
   }
}
