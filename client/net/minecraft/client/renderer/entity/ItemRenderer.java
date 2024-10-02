package net.minecraft.client.renderer.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.SheetedDecalTextureGenerator;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.blaze3d.vertex.VertexMultiConsumer;
import com.mojang.math.MatrixUtil;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.color.item.ItemColors;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.ItemModelShaper;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.ModelManager;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.ResourceManagerReloadListener;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.ARGB;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.BundleItem;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;

public class ItemRenderer implements ResourceManagerReloadListener {
   public static final ResourceLocation ENCHANTED_GLINT_ENTITY = ResourceLocation.withDefaultNamespace("textures/misc/enchanted_glint_entity.png");
   public static final ResourceLocation ENCHANTED_GLINT_ITEM = ResourceLocation.withDefaultNamespace("textures/misc/enchanted_glint_item.png");
   public static final int GUI_SLOT_CENTER_X = 8;
   public static final int GUI_SLOT_CENTER_Y = 8;
   public static final int ITEM_DECORATION_BLIT_OFFSET = 200;
   public static final float COMPASS_FOIL_UI_SCALE = 0.5F;
   public static final float COMPASS_FOIL_FIRST_PERSON_SCALE = 0.75F;
   public static final float COMPASS_FOIL_TEXTURE_SCALE = 0.0078125F;
   public static final ModelResourceLocation TRIDENT_MODEL = ModelResourceLocation.inventory(ResourceLocation.withDefaultNamespace("trident"));
   public static final ModelResourceLocation SPYGLASS_MODEL = ModelResourceLocation.inventory(ResourceLocation.withDefaultNamespace("spyglass"));
   private final ModelManager modelManager;
   private final ItemModelShaper itemModelShaper;
   private final ItemColors itemColors;
   private final BlockEntityWithoutLevelRenderer blockEntityRenderer;

   public ItemRenderer(ModelManager var1, ItemColors var2, BlockEntityWithoutLevelRenderer var3) {
      super();
      this.modelManager = var1;
      this.itemModelShaper = new ItemModelShaper(var1);
      this.blockEntityRenderer = var3;
      this.itemColors = var2;
   }

   private void renderModelLists(BakedModel var1, ItemStack var2, int var3, int var4, PoseStack var5, VertexConsumer var6) {
      RandomSource var7 = RandomSource.create();
      long var8 = 42L;

      for (Direction var13 : Direction.values()) {
         var7.setSeed(42L);
         this.renderQuadList(var5, var6, var1.getQuads(null, var13, var7), var2, var3, var4);
      }

      var7.setSeed(42L);
      this.renderQuadList(var5, var6, var1.getQuads(null, null, var7), var2, var3, var4);
   }

   public void render(ItemStack var1, ItemDisplayContext var2, boolean var3, PoseStack var4, MultiBufferSource var5, int var6, int var7, BakedModel var8) {
      if (!var1.isEmpty()) {
         this.renderSimpleItemModel(var1, var2, var3, var4, var5, var6, var7, var8, shouldRenderItemFlat(var2));
      }
   }

   public void renderBundleItem(
      ItemStack var1,
      ItemDisplayContext var2,
      boolean var3,
      PoseStack var4,
      MultiBufferSource var5,
      int var6,
      int var7,
      BakedModel var8,
      @Nullable Level var9,
      @Nullable LivingEntity var10,
      int var11
   ) {
      if (var1.getItem() instanceof BundleItem var12) {
         if (BundleItem.hasSelectedItem(var1)) {
            boolean var18 = shouldRenderItemFlat(var2);
            BakedModel var14 = this.resolveModelOverride(this.itemModelShaper.getItemModel(var12.openBackModel()), var1, var9, var10, var11);
            this.renderItemModelRaw(var1, var2, var3, var4, var5, var6, var7, var14, var18, -1.5F);
            ItemStack var15 = BundleItem.getSelectedItemStack(var1);
            BakedModel var16 = this.getModel(var15, var9, var10, var11);
            this.renderSimpleItemModel(var15, var2, var3, var4, var5, var6, var7, var16, var18);
            BakedModel var17 = this.resolveModelOverride(this.itemModelShaper.getItemModel(var12.openFrontModel()), var1, var9, var10, var11);
            this.renderItemModelRaw(var1, var2, var3, var4, var5, var6, var7, var17, var18, 0.5F);
         } else {
            this.render(var1, var2, var3, var4, var5, var6, var7, var8);
         }
      }
   }

   private void renderSimpleItemModel(
      ItemStack var1, ItemDisplayContext var2, boolean var3, PoseStack var4, MultiBufferSource var5, int var6, int var7, BakedModel var8, boolean var9
   ) {
      if (var9) {
         if (var1.is(Items.TRIDENT)) {
            var8 = this.modelManager.getModel(TRIDENT_MODEL);
         } else if (var1.is(Items.SPYGLASS)) {
            var8 = this.modelManager.getModel(SPYGLASS_MODEL);
         }
      }

      this.renderItemModelRaw(var1, var2, var3, var4, var5, var6, var7, var8, var9, -0.5F);
   }

   private void renderItemModelRaw(
      ItemStack var1,
      ItemDisplayContext var2,
      boolean var3,
      PoseStack var4,
      MultiBufferSource var5,
      int var6,
      int var7,
      BakedModel var8,
      boolean var9,
      float var10
   ) {
      var4.pushPose();
      var8.getTransforms().getTransform(var2).apply(var3, var4);
      var4.translate(-0.5F, -0.5F, var10);
      this.renderItem(var1, var2, var4, var5, var6, var7, var8, var9);
      var4.popPose();
   }

   private void renderItem(ItemStack var1, ItemDisplayContext var2, PoseStack var3, MultiBufferSource var4, int var5, int var6, BakedModel var7, boolean var8) {
      if (!var7.isCustomRenderer() && (!var1.is(Items.TRIDENT) || var8)) {
         RenderType var9 = ItemBlockRenderTypes.getRenderType(var1);
         VertexConsumer var10;
         if (hasAnimatedTexture(var1) && var1.hasFoil()) {
            PoseStack.Pose var11 = var3.last().copy();
            if (var2 == ItemDisplayContext.GUI) {
               MatrixUtil.mulComponentWise(var11.pose(), 0.5F);
            } else if (var2.firstPerson()) {
               MatrixUtil.mulComponentWise(var11.pose(), 0.75F);
            }

            var10 = getCompassFoilBuffer(var4, var9, var11);
         } else {
            var10 = getFoilBuffer(var4, var9, true, var1.hasFoil());
         }

         this.renderModelLists(var7, var1, var5, var6, var3, var10);
      } else {
         this.blockEntityRenderer.renderByItem(var1, var2, var3, var4, var5, var6);
      }
   }

   private static boolean shouldRenderItemFlat(ItemDisplayContext var0) {
      return var0 == ItemDisplayContext.GUI || var0 == ItemDisplayContext.GROUND || var0 == ItemDisplayContext.FIXED;
   }

   private static boolean hasAnimatedTexture(ItemStack var0) {
      return var0.is(ItemTags.COMPASSES) || var0.is(Items.CLOCK);
   }

   public static VertexConsumer getArmorFoilBuffer(MultiBufferSource var0, RenderType var1, boolean var2) {
      return var2 ? VertexMultiConsumer.create(var0.getBuffer(RenderType.armorEntityGlint()), var0.getBuffer(var1)) : var0.getBuffer(var1);
   }

   public static VertexConsumer getCompassFoilBuffer(MultiBufferSource var0, RenderType var1, PoseStack.Pose var2) {
      return VertexMultiConsumer.create(new SheetedDecalTextureGenerator(var0.getBuffer(RenderType.glint()), var2, 0.0078125F), var0.getBuffer(var1));
   }

   public static VertexConsumer getFoilBuffer(MultiBufferSource var0, RenderType var1, boolean var2, boolean var3) {
      if (var3) {
         return Minecraft.useShaderTransparency() && var1 == Sheets.translucentItemSheet()
            ? VertexMultiConsumer.create(var0.getBuffer(RenderType.glintTranslucent()), var0.getBuffer(var1))
            : VertexMultiConsumer.create(var0.getBuffer(var2 ? RenderType.glint() : RenderType.entityGlint()), var0.getBuffer(var1));
      } else {
         return var0.getBuffer(var1);
      }
   }

   private void renderQuadList(PoseStack var1, VertexConsumer var2, List<BakedQuad> var3, ItemStack var4, int var5, int var6) {
      boolean var7 = !var4.isEmpty();
      PoseStack.Pose var8 = var1.last();

      for (BakedQuad var10 : var3) {
         int var11 = -1;
         if (var7 && var10.isTinted()) {
            var11 = this.itemColors.getColor(var4, var10.getTintIndex());
         }

         float var12 = (float)ARGB.alpha(var11) / 255.0F;
         float var13 = (float)ARGB.red(var11) / 255.0F;
         float var14 = (float)ARGB.green(var11) / 255.0F;
         float var15 = (float)ARGB.blue(var11) / 255.0F;
         var2.putBulkData(var8, var10, var13, var14, var15, var12, var5, var6);
      }
   }

   public BakedModel getModel(ItemStack var1, @Nullable Level var2, @Nullable LivingEntity var3, int var4) {
      BakedModel var5 = this.itemModelShaper.getItemModel(var1);
      return this.resolveModelOverride(var5, var1, var2, var3, var4);
   }

   public void renderStatic(ItemStack var1, ItemDisplayContext var2, int var3, int var4, PoseStack var5, MultiBufferSource var6, @Nullable Level var7, int var8) {
      this.renderStatic(null, var1, var2, false, var5, var6, var7, var3, var4, var8);
   }

   public void renderStatic(
      @Nullable LivingEntity var1,
      ItemStack var2,
      ItemDisplayContext var3,
      boolean var4,
      PoseStack var5,
      MultiBufferSource var6,
      @Nullable Level var7,
      int var8,
      int var9,
      int var10
   ) {
      if (!var2.isEmpty()) {
         BakedModel var11 = this.getModel(var2, var7, var1, var10);
         this.render(var2, var3, var4, var5, var6, var8, var9, var11);
      }
   }

   @Override
   public void onResourceManagerReload(ResourceManager var1) {
      this.itemModelShaper.invalidateCache();
   }

   @Nullable
   public BakedModel resolveItemModel(ItemStack var1, LivingEntity var2, ItemDisplayContext var3) {
      return var1.isEmpty() ? null : this.getModel(var1, var2.level(), var2, var2.getId() + var3.ordinal());
   }

   private BakedModel resolveModelOverride(BakedModel var1, ItemStack var2, @Nullable Level var3, @Nullable LivingEntity var4, int var5) {
      ClientLevel var6 = var3 instanceof ClientLevel ? (ClientLevel)var3 : null;
      BakedModel var7 = var1.overrides().findOverride(var2, var6, var4, var5);
      return var7 == null ? var1 : var7;
   }
}
