package net.minecraft.client.renderer.entity;

import com.google.common.collect.Sets;
import com.mojang.blaze3d.platform.Lighting;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.SheetedDecalTextureGenerator;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.blaze3d.vertex.VertexMultiConsumer;
import com.mojang.math.MatrixUtil;
import java.util.List;
import java.util.Set;
import javax.annotation.Nullable;
import net.minecraft.CrashReport;
import net.minecraft.CrashReportCategory;
import net.minecraft.ReportedException;
import net.minecraft.client.Minecraft;
import net.minecraft.client.color.item.ItemColors;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.ItemModelShaper;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.ModelManager;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.ResourceManagerReloadListener;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.HalfTransparentBlock;
import net.minecraft.world.level.block.StainedGlassPaneBlock;
import org.joml.Matrix4f;

public class ItemRenderer implements ResourceManagerReloadListener {
   public static final ResourceLocation ENCHANTED_GLINT_ENTITY = new ResourceLocation("textures/misc/enchanted_glint_entity.png");
   public static final ResourceLocation ENCHANTED_GLINT_ITEM = new ResourceLocation("textures/misc/enchanted_glint_item.png");
   private static final Set<Item> IGNORED = Sets.newHashSet(new Item[]{Items.AIR});
   private static final int GUI_SLOT_CENTER_X = 8;
   private static final int GUI_SLOT_CENTER_Y = 8;
   public static final int ITEM_COUNT_BLIT_OFFSET = 200;
   public static final float COMPASS_FOIL_UI_SCALE = 0.5F;
   public static final float COMPASS_FOIL_FIRST_PERSON_SCALE = 0.75F;
   public static final float COMPASS_FOIL_TEXTURE_SCALE = 0.0078125F;
   private static final ModelResourceLocation TRIDENT_MODEL = ModelResourceLocation.vanilla("trident", "inventory");
   public static final ModelResourceLocation TRIDENT_IN_HAND_MODEL = ModelResourceLocation.vanilla("trident_in_hand", "inventory");
   private static final ModelResourceLocation SPYGLASS_MODEL = ModelResourceLocation.vanilla("spyglass", "inventory");
   public static final ModelResourceLocation SPYGLASS_IN_HAND_MODEL = ModelResourceLocation.vanilla("spyglass_in_hand", "inventory");
   private final Minecraft minecraft;
   private final ItemModelShaper itemModelShaper;
   private final TextureManager textureManager;
   private final ItemColors itemColors;
   private final BlockEntityWithoutLevelRenderer blockEntityRenderer;

   public ItemRenderer(Minecraft var1, TextureManager var2, ModelManager var3, ItemColors var4, BlockEntityWithoutLevelRenderer var5) {
      super();
      this.minecraft = var1;
      this.textureManager = var2;
      this.itemModelShaper = new ItemModelShaper(var3);
      this.blockEntityRenderer = var5;

      for(Item var7 : BuiltInRegistries.ITEM) {
         if (!IGNORED.contains(var7)) {
            this.itemModelShaper.register(var7, new ModelResourceLocation(BuiltInRegistries.ITEM.getKey(var7), "inventory"));
         }
      }

      this.itemColors = var4;
   }

   public ItemModelShaper getItemModelShaper() {
      return this.itemModelShaper;
   }

   private void renderModelLists(BakedModel var1, ItemStack var2, int var3, int var4, PoseStack var5, VertexConsumer var6) {
      RandomSource var7 = RandomSource.create();
      long var8 = 42L;

      for(Direction var13 : Direction.values()) {
         var7.setSeed(42L);
         this.renderQuadList(var5, var6, var1.getQuads(null, var13, var7), var2, var3, var4);
      }

      var7.setSeed(42L);
      this.renderQuadList(var5, var6, var1.getQuads(null, null, var7), var2, var3, var4);
   }

   public void render(ItemStack var1, ItemDisplayContext var2, boolean var3, PoseStack var4, MultiBufferSource var5, int var6, int var7, BakedModel var8) {
      if (!var1.isEmpty()) {
         var4.pushPose();
         boolean var9 = var2 == ItemDisplayContext.GUI || var2 == ItemDisplayContext.GROUND || var2 == ItemDisplayContext.FIXED;
         if (var9) {
            if (var1.is(Items.TRIDENT)) {
               var8 = this.itemModelShaper.getModelManager().getModel(TRIDENT_MODEL);
            } else if (var1.is(Items.SPYGLASS)) {
               var8 = this.itemModelShaper.getModelManager().getModel(SPYGLASS_MODEL);
            }
         }

         var8.getTransforms().getTransform(var2).apply(var3, var4);
         var4.translate(-0.5F, -0.5F, -0.5F);
         if (!var8.isCustomRenderer() && (!var1.is(Items.TRIDENT) || var9)) {
            boolean var10;
            if (var2 != ItemDisplayContext.GUI && !var2.firstPerson() && var1.getItem() instanceof BlockItem) {
               Block var11 = ((BlockItem)var1.getItem()).getBlock();
               var10 = !(var11 instanceof HalfTransparentBlock) && !(var11 instanceof StainedGlassPaneBlock);
            } else {
               var10 = true;
            }

            RenderType var14 = ItemBlockRenderTypes.getRenderType(var1, var10);
            VertexConsumer var12;
            if (var1.is(ItemTags.COMPASSES) && var1.hasFoil()) {
               var4.pushPose();
               PoseStack.Pose var13 = var4.last();
               if (var2 == ItemDisplayContext.GUI) {
                  MatrixUtil.mulComponentWise(var13.pose(), 0.5F);
               } else if (var2.firstPerson()) {
                  MatrixUtil.mulComponentWise(var13.pose(), 0.75F);
               }

               if (var10) {
                  var12 = getCompassFoilBufferDirect(var5, var14, var13);
               } else {
                  var12 = getCompassFoilBuffer(var5, var14, var13);
               }

               var4.popPose();
            } else if (var10) {
               var12 = getFoilBufferDirect(var5, var14, true, var1.hasFoil());
            } else {
               var12 = getFoilBuffer(var5, var14, true, var1.hasFoil());
            }

            this.renderModelLists(var8, var1, var6, var7, var4, var12);
         } else {
            this.blockEntityRenderer.renderByItem(var1, var2, var4, var5, var6, var7);
         }

         var4.popPose();
      }
   }

   public static VertexConsumer getArmorFoilBuffer(MultiBufferSource var0, RenderType var1, boolean var2, boolean var3) {
      return var3
         ? VertexMultiConsumer.create(var0.getBuffer(var2 ? RenderType.armorGlint() : RenderType.armorEntityGlint()), var0.getBuffer(var1))
         : var0.getBuffer(var1);
   }

   public static VertexConsumer getCompassFoilBuffer(MultiBufferSource var0, RenderType var1, PoseStack.Pose var2) {
      return VertexMultiConsumer.create(
         new SheetedDecalTextureGenerator(var0.getBuffer(RenderType.glint()), var2.pose(), var2.normal(), 0.0078125F), var0.getBuffer(var1)
      );
   }

   public static VertexConsumer getCompassFoilBufferDirect(MultiBufferSource var0, RenderType var1, PoseStack.Pose var2) {
      return VertexMultiConsumer.create(
         new SheetedDecalTextureGenerator(var0.getBuffer(RenderType.glintDirect()), var2.pose(), var2.normal(), 0.0078125F), var0.getBuffer(var1)
      );
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

   public static VertexConsumer getFoilBufferDirect(MultiBufferSource var0, RenderType var1, boolean var2, boolean var3) {
      return var3
         ? VertexMultiConsumer.create(var0.getBuffer(var2 ? RenderType.glintDirect() : RenderType.entityGlintDirect()), var0.getBuffer(var1))
         : var0.getBuffer(var1);
   }

   private void renderQuadList(PoseStack var1, VertexConsumer var2, List<BakedQuad> var3, ItemStack var4, int var5, int var6) {
      boolean var7 = !var4.isEmpty();
      PoseStack.Pose var8 = var1.last();

      for(BakedQuad var10 : var3) {
         int var11 = -1;
         if (var7 && var10.isTinted()) {
            var11 = this.itemColors.getColor(var4, var10.getTintIndex());
         }

         float var12 = (float)(var11 >> 16 & 0xFF) / 255.0F;
         float var13 = (float)(var11 >> 8 & 0xFF) / 255.0F;
         float var14 = (float)(var11 & 0xFF) / 255.0F;
         var2.putBulkData(var8, var10, var12, var13, var14, var5, var6);
      }
   }

   public BakedModel getModel(ItemStack var1, @Nullable Level var2, @Nullable LivingEntity var3, int var4) {
      BakedModel var5;
      if (var1.is(Items.TRIDENT)) {
         var5 = this.itemModelShaper.getModelManager().getModel(TRIDENT_IN_HAND_MODEL);
      } else if (var1.is(Items.SPYGLASS)) {
         var5 = this.itemModelShaper.getModelManager().getModel(SPYGLASS_IN_HAND_MODEL);
      } else {
         var5 = this.itemModelShaper.getItemModel(var1);
      }

      ClientLevel var6 = var2 instanceof ClientLevel ? (ClientLevel)var2 : null;
      BakedModel var7 = var5.getOverrides().resolve(var5, var1, var6, var3, var4);
      return var7 == null ? this.itemModelShaper.getModelManager().getMissingModel() : var7;
   }

   public void renderStatic(
      ItemStack var1, ItemDisplayContext var2, int var3, int var4, PoseStack var5, MultiBufferSource var6, @Nullable Level var7, int var8
   ) {
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

   public void renderGuiItem(PoseStack var1, ItemStack var2, int var3, int var4) {
      this.renderGuiItem(var1, var2, var3, var4, this.getModel(var2, null, null, 0));
   }

   protected void renderGuiItem(PoseStack var1, ItemStack var2, int var3, int var4, BakedModel var5) {
      var1.pushPose();
      var1.translate((float)var3, (float)var4, 100.0F);
      var1.translate(8.0F, 8.0F, 0.0F);
      var1.mulPoseMatrix(new Matrix4f().scaling(1.0F, -1.0F, 1.0F));
      var1.scale(16.0F, 16.0F, 16.0F);
      MultiBufferSource.BufferSource var6 = this.minecraft.renderBuffers().bufferSource();
      boolean var7 = !var5.usesBlockLight();
      if (var7) {
         Lighting.setupForFlatItems();
      }

      PoseStack var8 = RenderSystem.getModelViewStack();
      var8.pushPose();
      var8.mulPoseMatrix(var1.last().pose());
      RenderSystem.applyModelViewMatrix();
      this.render(var2, ItemDisplayContext.GUI, false, new PoseStack(), var6, 15728880, OverlayTexture.NO_OVERLAY, var5);
      var6.endBatch();
      RenderSystem.enableDepthTest();
      if (var7) {
         Lighting.setupFor3DItems();
      }

      var1.popPose();
      var8.popPose();
      RenderSystem.applyModelViewMatrix();
   }

   public void renderAndDecorateItem(PoseStack var1, ItemStack var2, int var3, int var4) {
      this.tryRenderGuiItem(var1, this.minecraft.player, this.minecraft.level, var2, var3, var4, 0);
   }

   public void renderAndDecorateItem(PoseStack var1, ItemStack var2, int var3, int var4, int var5) {
      this.tryRenderGuiItem(var1, this.minecraft.player, this.minecraft.level, var2, var3, var4, var5);
   }

   public void renderAndDecorateItem(PoseStack var1, ItemStack var2, int var3, int var4, int var5, int var6) {
      this.tryRenderGuiItem(var1, this.minecraft.player, this.minecraft.level, var2, var3, var4, var5, var6);
   }

   public void renderAndDecorateFakeItem(PoseStack var1, ItemStack var2, int var3, int var4) {
      this.tryRenderGuiItem(var1, null, this.minecraft.level, var2, var3, var4, 0);
   }

   public void renderAndDecorateItem(PoseStack var1, LivingEntity var2, ItemStack var3, int var4, int var5, int var6) {
      this.tryRenderGuiItem(var1, var2, var2.level, var3, var4, var5, var6);
   }

   private void tryRenderGuiItem(PoseStack var1, @Nullable LivingEntity var2, @Nullable Level var3, ItemStack var4, int var5, int var6, int var7) {
      this.tryRenderGuiItem(var1, var2, var3, var4, var5, var6, var7, 0);
   }

   private void tryRenderGuiItem(PoseStack var1, @Nullable LivingEntity var2, @Nullable Level var3, ItemStack var4, int var5, int var6, int var7, int var8) {
      if (!var4.isEmpty()) {
         BakedModel var9 = this.getModel(var4, var3, var2, var7);
         var1.pushPose();
         var1.translate(0.0F, 0.0F, (float)(50 + (var9.isGui3d() ? var8 : 0)));

         try {
            this.renderGuiItem(var1, var4, var5, var6, var9);
         } catch (Throwable var13) {
            CrashReport var11 = CrashReport.forThrowable(var13, "Rendering item");
            CrashReportCategory var12 = var11.addCategory("Item being rendered");
            var12.setDetail("Item Type", () -> String.valueOf(var4.getItem()));
            var12.setDetail("Item Damage", () -> String.valueOf(var4.getDamageValue()));
            var12.setDetail("Item NBT", () -> String.valueOf(var4.getTag()));
            var12.setDetail("Item Foil", () -> String.valueOf(var4.hasFoil()));
            throw new ReportedException(var11);
         }

         var1.popPose();
      }
   }

   public void renderGuiItemDecorations(PoseStack var1, Font var2, ItemStack var3, int var4, int var5) {
      this.renderGuiItemDecorations(var1, var2, var3, var4, var5, null);
   }

   public void renderGuiItemDecorations(PoseStack var1, Font var2, ItemStack var3, int var4, int var5, @Nullable String var6) {
      if (!var3.isEmpty()) {
         var1.pushPose();
         if (var3.getCount() != 1 || var6 != null) {
            String var7 = var6 == null ? String.valueOf(var3.getCount()) : var6;
            var1.translate(0.0F, 0.0F, 200.0F);
            MultiBufferSource.BufferSource var8 = MultiBufferSource.immediate(Tesselator.getInstance().getBuilder());
            var2.drawInBatch(
               var7,
               (float)(var4 + 19 - 2 - var2.width(var7)),
               (float)(var5 + 6 + 3),
               16777215,
               true,
               var1.last().pose(),
               var8,
               Font.DisplayMode.NORMAL,
               0,
               15728880
            );
            var8.endBatch();
         }

         if (var3.isBarVisible()) {
            RenderSystem.disableDepthTest();
            int var11 = var3.getBarWidth();
            int var13 = var3.getBarColor();
            int var9 = var4 + 2;
            int var10 = var5 + 13;
            GuiComponent.fill(var1, var9, var10, var9 + 13, var10 + 2, -16777216);
            GuiComponent.fill(var1, var9, var10, var9 + var11, var10 + 1, var13 | 0xFF000000);
            RenderSystem.enableDepthTest();
         }

         LocalPlayer var12 = this.minecraft.player;
         float var14 = var12 == null ? 0.0F : var12.getCooldowns().getCooldownPercent(var3.getItem(), this.minecraft.getFrameTime());
         if (var14 > 0.0F) {
            RenderSystem.disableDepthTest();
            int var15 = var5 + Mth.floor(16.0F * (1.0F - var14));
            int var16 = var15 + Mth.ceil(16.0F * var14);
            GuiComponent.fill(var1, var4, var15, var4 + 16, var16, 2147483647);
            RenderSystem.enableDepthTest();
         }

         var1.popPose();
      }
   }

   @Override
   public void onResourceManagerReload(ResourceManager var1) {
      this.itemModelShaper.rebuildCache();
   }
}
