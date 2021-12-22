package net.minecraft.client.renderer.entity;

import com.google.common.collect.Sets;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.platform.Lighting;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.BufferUploader;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.SheetedDecalTextureGenerator;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.mojang.blaze3d.vertex.VertexMultiConsumer;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.Set;
import javax.annotation.Nullable;
import net.minecraft.CrashReport;
import net.minecraft.CrashReportCategory;
import net.minecraft.ReportedException;
import net.minecraft.client.Minecraft;
import net.minecraft.client.color.item.ItemColors;
import net.minecraft.client.gui.Font;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.ItemModelShaper;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.ModelManager;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.core.Direction;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.ResourceManagerReloadListener;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.HalfTransparentBlock;
import net.minecraft.world.level.block.StainedGlassPaneBlock;
import net.minecraft.world.level.block.state.BlockState;

public class ItemRenderer implements ResourceManagerReloadListener {
   public static final ResourceLocation ENCHANT_GLINT_LOCATION = new ResourceLocation("textures/misc/enchanted_item_glint.png");
   private static final Set<Item> IGNORED;
   private static final int GUI_SLOT_CENTER_X = 8;
   private static final int GUI_SLOT_CENTER_Y = 8;
   public static final int ITEM_COUNT_BLIT_OFFSET = 200;
   public static final float COMPASS_FOIL_UI_SCALE = 0.5F;
   public static final float COMPASS_FOIL_FIRST_PERSON_SCALE = 0.75F;
   public float blitOffset;
   private final ItemModelShaper itemModelShaper;
   private final TextureManager textureManager;
   private final ItemColors itemColors;
   private final BlockEntityWithoutLevelRenderer blockEntityRenderer;

   public ItemRenderer(TextureManager var1, ModelManager var2, ItemColors var3, BlockEntityWithoutLevelRenderer var4) {
      super();
      this.textureManager = var1;
      this.itemModelShaper = new ItemModelShaper(var2);
      this.blockEntityRenderer = var4;
      Iterator var5 = Registry.ITEM.iterator();

      while(var5.hasNext()) {
         Item var6 = (Item)var5.next();
         if (!IGNORED.contains(var6)) {
            this.itemModelShaper.register(var6, new ModelResourceLocation(Registry.ITEM.getKey(var6), "inventory"));
         }
      }

      this.itemColors = var3;
   }

   public ItemModelShaper getItemModelShaper() {
      return this.itemModelShaper;
   }

   private void renderModelLists(BakedModel var1, ItemStack var2, int var3, int var4, PoseStack var5, VertexConsumer var6) {
      Random var7 = new Random();
      long var8 = 42L;
      Direction[] var10 = Direction.values();
      int var11 = var10.length;

      for(int var12 = 0; var12 < var11; ++var12) {
         Direction var13 = var10[var12];
         var7.setSeed(42L);
         this.renderQuadList(var5, var6, var1.getQuads((BlockState)null, var13, var7), var2, var3, var4);
      }

      var7.setSeed(42L);
      this.renderQuadList(var5, var6, var1.getQuads((BlockState)null, (Direction)null, var7), var2, var3, var4);
   }

   public void render(ItemStack var1, ItemTransforms.TransformType var2, boolean var3, PoseStack var4, MultiBufferSource var5, int var6, int var7, BakedModel var8) {
      if (!var1.isEmpty()) {
         var4.pushPose();
         boolean var9 = var2 == ItemTransforms.TransformType.GUI || var2 == ItemTransforms.TransformType.GROUND || var2 == ItemTransforms.TransformType.FIXED;
         if (var9) {
            if (var1.method_87(Items.TRIDENT)) {
               var8 = this.itemModelShaper.getModelManager().getModel(new ModelResourceLocation("minecraft:trident#inventory"));
            } else if (var1.method_87(Items.SPYGLASS)) {
               var8 = this.itemModelShaper.getModelManager().getModel(new ModelResourceLocation("minecraft:spyglass#inventory"));
            }
         }

         var8.getTransforms().getTransform(var2).apply(var3, var4);
         var4.translate(-0.5D, -0.5D, -0.5D);
         if (!var8.isCustomRenderer() && (!var1.method_87(Items.TRIDENT) || var9)) {
            boolean var10;
            if (var2 != ItemTransforms.TransformType.GUI && !var2.firstPerson() && var1.getItem() instanceof BlockItem) {
               Block var11 = ((BlockItem)var1.getItem()).getBlock();
               var10 = !(var11 instanceof HalfTransparentBlock) && !(var11 instanceof StainedGlassPaneBlock);
            } else {
               var10 = true;
            }

            RenderType var14 = ItemBlockRenderTypes.getRenderType(var1, var10);
            VertexConsumer var12;
            if (var1.method_87(Items.COMPASS) && var1.hasFoil()) {
               var4.pushPose();
               PoseStack.Pose var13 = var4.last();
               if (var2 == ItemTransforms.TransformType.GUI) {
                  var13.pose().multiply(0.5F);
               } else if (var2.firstPerson()) {
                  var13.pose().multiply(0.75F);
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
      return var3 ? VertexMultiConsumer.create(var0.getBuffer(var2 ? RenderType.armorGlint() : RenderType.armorEntityGlint()), var0.getBuffer(var1)) : var0.getBuffer(var1);
   }

   public static VertexConsumer getCompassFoilBuffer(MultiBufferSource var0, RenderType var1, PoseStack.Pose var2) {
      return VertexMultiConsumer.create(new SheetedDecalTextureGenerator(var0.getBuffer(RenderType.glint()), var2.pose(), var2.normal()), var0.getBuffer(var1));
   }

   public static VertexConsumer getCompassFoilBufferDirect(MultiBufferSource var0, RenderType var1, PoseStack.Pose var2) {
      return VertexMultiConsumer.create(new SheetedDecalTextureGenerator(var0.getBuffer(RenderType.glintDirect()), var2.pose(), var2.normal()), var0.getBuffer(var1));
   }

   public static VertexConsumer getFoilBuffer(MultiBufferSource var0, RenderType var1, boolean var2, boolean var3) {
      if (var3) {
         return Minecraft.useShaderTransparency() && var1 == Sheets.translucentItemSheet() ? VertexMultiConsumer.create(var0.getBuffer(RenderType.glintTranslucent()), var0.getBuffer(var1)) : VertexMultiConsumer.create(var0.getBuffer(var2 ? RenderType.glint() : RenderType.entityGlint()), var0.getBuffer(var1));
      } else {
         return var0.getBuffer(var1);
      }
   }

   public static VertexConsumer getFoilBufferDirect(MultiBufferSource var0, RenderType var1, boolean var2, boolean var3) {
      return var3 ? VertexMultiConsumer.create(var0.getBuffer(var2 ? RenderType.glintDirect() : RenderType.entityGlintDirect()), var0.getBuffer(var1)) : var0.getBuffer(var1);
   }

   private void renderQuadList(PoseStack var1, VertexConsumer var2, List<BakedQuad> var3, ItemStack var4, int var5, int var6) {
      boolean var7 = !var4.isEmpty();
      PoseStack.Pose var8 = var1.last();
      Iterator var9 = var3.iterator();

      while(var9.hasNext()) {
         BakedQuad var10 = (BakedQuad)var9.next();
         int var11 = -1;
         if (var7 && var10.isTinted()) {
            var11 = this.itemColors.getColor(var4, var10.getTintIndex());
         }

         float var12 = (float)(var11 >> 16 & 255) / 255.0F;
         float var13 = (float)(var11 >> 8 & 255) / 255.0F;
         float var14 = (float)(var11 & 255) / 255.0F;
         var2.putBulkData(var8, var10, var12, var13, var14, var5, var6);
      }

   }

   public BakedModel getModel(ItemStack var1, @Nullable Level var2, @Nullable LivingEntity var3, int var4) {
      BakedModel var5;
      if (var1.method_87(Items.TRIDENT)) {
         var5 = this.itemModelShaper.getModelManager().getModel(new ModelResourceLocation("minecraft:trident_in_hand#inventory"));
      } else if (var1.method_87(Items.SPYGLASS)) {
         var5 = this.itemModelShaper.getModelManager().getModel(new ModelResourceLocation("minecraft:spyglass_in_hand#inventory"));
      } else {
         var5 = this.itemModelShaper.getItemModel(var1);
      }

      ClientLevel var6 = var2 instanceof ClientLevel ? (ClientLevel)var2 : null;
      BakedModel var7 = var5.getOverrides().resolve(var5, var1, var6, var3, var4);
      return var7 == null ? this.itemModelShaper.getModelManager().getMissingModel() : var7;
   }

   public void renderStatic(ItemStack var1, ItemTransforms.TransformType var2, int var3, int var4, PoseStack var5, MultiBufferSource var6, int var7) {
      this.renderStatic((LivingEntity)null, var1, var2, false, var5, var6, (Level)null, var3, var4, var7);
   }

   public void renderStatic(@Nullable LivingEntity var1, ItemStack var2, ItemTransforms.TransformType var3, boolean var4, PoseStack var5, MultiBufferSource var6, @Nullable Level var7, int var8, int var9, int var10) {
      if (!var2.isEmpty()) {
         BakedModel var11 = this.getModel(var2, var7, var1, var10);
         this.render(var2, var3, var4, var5, var6, var8, var9, var11);
      }
   }

   public void renderGuiItem(ItemStack var1, int var2, int var3) {
      this.renderGuiItem(var1, var2, var3, this.getModel(var1, (Level)null, (LivingEntity)null, 0));
   }

   protected void renderGuiItem(ItemStack var1, int var2, int var3, BakedModel var4) {
      this.textureManager.getTexture(TextureAtlas.LOCATION_BLOCKS).setFilter(false, false);
      RenderSystem.setShaderTexture(0, TextureAtlas.LOCATION_BLOCKS);
      RenderSystem.enableBlend();
      RenderSystem.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
      RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
      PoseStack var5 = RenderSystem.getModelViewStack();
      var5.pushPose();
      var5.translate((double)var2, (double)var3, (double)(100.0F + this.blitOffset));
      var5.translate(8.0D, 8.0D, 0.0D);
      var5.scale(1.0F, -1.0F, 1.0F);
      var5.scale(16.0F, 16.0F, 16.0F);
      RenderSystem.applyModelViewMatrix();
      PoseStack var6 = new PoseStack();
      MultiBufferSource.BufferSource var7 = Minecraft.getInstance().renderBuffers().bufferSource();
      boolean var8 = !var4.usesBlockLight();
      if (var8) {
         Lighting.setupForFlatItems();
      }

      this.render(var1, ItemTransforms.TransformType.GUI, false, var6, var7, 15728880, OverlayTexture.NO_OVERLAY, var4);
      var7.endBatch();
      RenderSystem.enableDepthTest();
      if (var8) {
         Lighting.setupFor3DItems();
      }

      var5.popPose();
      RenderSystem.applyModelViewMatrix();
   }

   public void renderAndDecorateItem(ItemStack var1, int var2, int var3) {
      this.tryRenderGuiItem(Minecraft.getInstance().player, var1, var2, var3, 0);
   }

   public void renderAndDecorateItem(ItemStack var1, int var2, int var3, int var4) {
      this.tryRenderGuiItem(Minecraft.getInstance().player, var1, var2, var3, var4);
   }

   public void renderAndDecorateItem(ItemStack var1, int var2, int var3, int var4, int var5) {
      this.tryRenderGuiItem(Minecraft.getInstance().player, var1, var2, var3, var4, var5);
   }

   public void renderAndDecorateFakeItem(ItemStack var1, int var2, int var3) {
      this.tryRenderGuiItem((LivingEntity)null, var1, var2, var3, 0);
   }

   public void renderAndDecorateItem(LivingEntity var1, ItemStack var2, int var3, int var4, int var5) {
      this.tryRenderGuiItem(var1, var2, var3, var4, var5);
   }

   private void tryRenderGuiItem(@Nullable LivingEntity var1, ItemStack var2, int var3, int var4, int var5) {
      this.tryRenderGuiItem(var1, var2, var3, var4, var5, 0);
   }

   private void tryRenderGuiItem(@Nullable LivingEntity var1, ItemStack var2, int var3, int var4, int var5, int var6) {
      if (!var2.isEmpty()) {
         BakedModel var7 = this.getModel(var2, (Level)null, var1, var5);
         this.blitOffset = var7.isGui3d() ? this.blitOffset + 50.0F + (float)var6 : this.blitOffset + 50.0F;

         try {
            this.renderGuiItem(var2, var3, var4, var7);
         } catch (Throwable var11) {
            CrashReport var9 = CrashReport.forThrowable(var11, "Rendering item");
            CrashReportCategory var10 = var9.addCategory("Item being rendered");
            var10.setDetail("Item Type", () -> {
               return String.valueOf(var2.getItem());
            });
            var10.setDetail("Item Damage", () -> {
               return String.valueOf(var2.getDamageValue());
            });
            var10.setDetail("Item NBT", () -> {
               return String.valueOf(var2.getTag());
            });
            var10.setDetail("Item Foil", () -> {
               return String.valueOf(var2.hasFoil());
            });
            throw new ReportedException(var9);
         }

         this.blitOffset = var7.isGui3d() ? this.blitOffset - 50.0F - (float)var6 : this.blitOffset - 50.0F;
      }
   }

   public void renderGuiItemDecorations(Font var1, ItemStack var2, int var3, int var4) {
      this.renderGuiItemDecorations(var1, var2, var3, var4, (String)null);
   }

   public void renderGuiItemDecorations(Font var1, ItemStack var2, int var3, int var4, @Nullable String var5) {
      if (!var2.isEmpty()) {
         PoseStack var6 = new PoseStack();
         if (var2.getCount() != 1 || var5 != null) {
            String var7 = var5 == null ? String.valueOf(var2.getCount()) : var5;
            var6.translate(0.0D, 0.0D, (double)(this.blitOffset + 200.0F));
            MultiBufferSource.BufferSource var8 = MultiBufferSource.immediate(Tesselator.getInstance().getBuilder());
            var1.drawInBatch((String)var7, (float)(var3 + 19 - 2 - var1.width(var7)), (float)(var4 + 6 + 3), 16777215, true, var6.last().pose(), var8, false, 0, 15728880);
            var8.endBatch();
         }

         if (var2.isBarVisible()) {
            RenderSystem.disableDepthTest();
            RenderSystem.disableTexture();
            RenderSystem.disableBlend();
            Tesselator var11 = Tesselator.getInstance();
            BufferBuilder var13 = var11.getBuilder();
            int var9 = var2.getBarWidth();
            int var10 = var2.getBarColor();
            this.fillRect(var13, var3 + 2, var4 + 13, 13, 2, 0, 0, 0, 255);
            this.fillRect(var13, var3 + 2, var4 + 13, var9, 1, var10 >> 16 & 255, var10 >> 8 & 255, var10 & 255, 255);
            RenderSystem.enableBlend();
            RenderSystem.enableTexture();
            RenderSystem.enableDepthTest();
         }

         LocalPlayer var12 = Minecraft.getInstance().player;
         float var14 = var12 == null ? 0.0F : var12.getCooldowns().getCooldownPercent(var2.getItem(), Minecraft.getInstance().getFrameTime());
         if (var14 > 0.0F) {
            RenderSystem.disableDepthTest();
            RenderSystem.disableTexture();
            RenderSystem.enableBlend();
            RenderSystem.defaultBlendFunc();
            Tesselator var15 = Tesselator.getInstance();
            BufferBuilder var16 = var15.getBuilder();
            this.fillRect(var16, var3, var4 + Mth.floor(16.0F * (1.0F - var14)), 16, Mth.ceil(16.0F * var14), 255, 255, 255, 127);
            RenderSystem.enableTexture();
            RenderSystem.enableDepthTest();
         }

      }
   }

   private void fillRect(BufferBuilder var1, int var2, int var3, int var4, int var5, int var6, int var7, int var8, int var9) {
      RenderSystem.setShader(GameRenderer::getPositionColorShader);
      var1.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR);
      var1.vertex((double)(var2 + 0), (double)(var3 + 0), 0.0D).color(var6, var7, var8, var9).endVertex();
      var1.vertex((double)(var2 + 0), (double)(var3 + var5), 0.0D).color(var6, var7, var8, var9).endVertex();
      var1.vertex((double)(var2 + var4), (double)(var3 + var5), 0.0D).color(var6, var7, var8, var9).endVertex();
      var1.vertex((double)(var2 + var4), (double)(var3 + 0), 0.0D).color(var6, var7, var8, var9).endVertex();
      var1.end();
      BufferUploader.end(var1);
   }

   public void onResourceManagerReload(ResourceManager var1) {
      this.itemModelShaper.rebuildCache();
   }

   static {
      IGNORED = Sets.newHashSet(new Item[]{Items.AIR});
   }
}
