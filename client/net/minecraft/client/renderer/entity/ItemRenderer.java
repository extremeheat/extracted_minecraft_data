package net.minecraft.client.renderer.entity;

import com.google.common.collect.Sets;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.Tesselator;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.Set;
import javax.annotation.Nullable;
import net.minecraft.CrashReport;
import net.minecraft.CrashReportCategory;
import net.minecraft.ReportedException;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.color.item.ItemColors;
import net.minecraft.client.gui.Font;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.EntityBlockRenderer;
import net.minecraft.client.renderer.ItemModelShaper;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.ItemTransform;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.ModelManager;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.core.Direction;
import net.minecraft.core.Registry;
import net.minecraft.core.Vec3i;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.ResourceManagerReloadListener;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

public class ItemRenderer implements ResourceManagerReloadListener {
   public static final ResourceLocation ENCHANT_GLINT_LOCATION = new ResourceLocation("textures/misc/enchanted_item_glint.png");
   private static final Set<Item> IGNORED;
   public float blitOffset;
   private final ItemModelShaper itemModelShaper;
   private final TextureManager textureManager;
   private final ItemColors itemColors;

   public ItemRenderer(TextureManager var1, ModelManager var2, ItemColors var3) {
      super();
      this.textureManager = var1;
      this.itemModelShaper = new ItemModelShaper(var2);
      Iterator var4 = Registry.ITEM.iterator();

      while(var4.hasNext()) {
         Item var5 = (Item)var4.next();
         if (!IGNORED.contains(var5)) {
            this.itemModelShaper.register(var5, new ModelResourceLocation(Registry.ITEM.getKey(var5), "inventory"));
         }
      }

      this.itemColors = var3;
   }

   public ItemModelShaper getItemModelShaper() {
      return this.itemModelShaper;
   }

   private void renderModelLists(BakedModel var1, ItemStack var2) {
      this.renderModelLists(var1, -1, var2);
   }

   private void renderModelLists(BakedModel var1, int var2) {
      this.renderModelLists(var1, var2, ItemStack.EMPTY);
   }

   private void renderModelLists(BakedModel var1, int var2, ItemStack var3) {
      Tesselator var4 = Tesselator.getInstance();
      BufferBuilder var5 = var4.getBuilder();
      var5.begin(7, DefaultVertexFormat.BLOCK_NORMALS);
      Random var6 = new Random();
      long var7 = 42L;
      Direction[] var9 = Direction.values();
      int var10 = var9.length;

      for(int var11 = 0; var11 < var10; ++var11) {
         Direction var12 = var9[var11];
         var6.setSeed(42L);
         this.renderQuadList(var5, var1.getQuads((BlockState)null, var12, var6), var2, var3);
      }

      var6.setSeed(42L);
      this.renderQuadList(var5, var1.getQuads((BlockState)null, (Direction)null, var6), var2, var3);
      var4.end();
   }

   public void render(ItemStack var1, BakedModel var2) {
      if (!var1.isEmpty()) {
         GlStateManager.pushMatrix();
         GlStateManager.translatef(-0.5F, -0.5F, -0.5F);
         if (var2.isCustomRenderer()) {
            GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
            GlStateManager.enableRescaleNormal();
            EntityBlockRenderer.instance.renderByItem(var1);
         } else {
            this.renderModelLists(var2, var1);
            if (var1.hasFoil()) {
               renderFoilLayer(this.textureManager, () -> {
                  this.renderModelLists(var2, -8372020);
               }, 8);
            }
         }

         GlStateManager.popMatrix();
      }
   }

   public static void renderFoilLayer(TextureManager var0, Runnable var1, int var2) {
      GlStateManager.depthMask(false);
      GlStateManager.depthFunc(514);
      GlStateManager.disableLighting();
      GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_COLOR, GlStateManager.DestFactor.ONE);
      var0.bind(ENCHANT_GLINT_LOCATION);
      GlStateManager.matrixMode(5890);
      GlStateManager.pushMatrix();
      GlStateManager.scalef((float)var2, (float)var2, (float)var2);
      float var3 = (float)(Util.getMillis() % 3000L) / 3000.0F / (float)var2;
      GlStateManager.translatef(var3, 0.0F, 0.0F);
      GlStateManager.rotatef(-50.0F, 0.0F, 0.0F, 1.0F);
      var1.run();
      GlStateManager.popMatrix();
      GlStateManager.pushMatrix();
      GlStateManager.scalef((float)var2, (float)var2, (float)var2);
      float var4 = (float)(Util.getMillis() % 4873L) / 4873.0F / (float)var2;
      GlStateManager.translatef(-var4, 0.0F, 0.0F);
      GlStateManager.rotatef(10.0F, 0.0F, 0.0F, 1.0F);
      var1.run();
      GlStateManager.popMatrix();
      GlStateManager.matrixMode(5888);
      GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
      GlStateManager.enableLighting();
      GlStateManager.depthFunc(515);
      GlStateManager.depthMask(true);
      var0.bind(TextureAtlas.LOCATION_BLOCKS);
   }

   private void applyNormal(BufferBuilder var1, BakedQuad var2) {
      Vec3i var3 = var2.getDirection().getNormal();
      var1.postNormal((float)var3.getX(), (float)var3.getY(), (float)var3.getZ());
   }

   private void putQuadData(BufferBuilder var1, BakedQuad var2, int var3) {
      var1.putBulkData(var2.getVertices());
      var1.fixupQuadColor(var3);
      this.applyNormal(var1, var2);
   }

   private void renderQuadList(BufferBuilder var1, List<BakedQuad> var2, int var3, ItemStack var4) {
      boolean var5 = var3 == -1 && !var4.isEmpty();
      int var6 = 0;

      for(int var7 = var2.size(); var6 < var7; ++var6) {
         BakedQuad var8 = (BakedQuad)var2.get(var6);
         int var9 = var3;
         if (var5 && var8.isTinted()) {
            var9 = this.itemColors.getColor(var4, var8.getTintIndex());
            var9 |= -16777216;
         }

         this.putQuadData(var1, var8, var9);
      }

   }

   public boolean isGui3d(ItemStack var1) {
      BakedModel var2 = this.itemModelShaper.getItemModel(var1);
      return var2 == null ? false : var2.isGui3d();
   }

   public void renderStatic(ItemStack var1, ItemTransforms.TransformType var2) {
      if (!var1.isEmpty()) {
         BakedModel var3 = this.getModel(var1);
         this.renderStatic(var1, var3, var2, false);
      }
   }

   public BakedModel getModel(ItemStack var1, @Nullable Level var2, @Nullable LivingEntity var3) {
      BakedModel var4 = this.itemModelShaper.getItemModel(var1);
      Item var5 = var1.getItem();
      return !var5.hasProperties() ? var4 : this.resolveOverrides(var4, var1, var2, var3);
   }

   public BakedModel getInHandModel(ItemStack var1, Level var2, LivingEntity var3) {
      Item var5 = var1.getItem();
      BakedModel var4;
      if (var5 == Items.TRIDENT) {
         var4 = this.itemModelShaper.getModelManager().getModel(new ModelResourceLocation("minecraft:trident_in_hand#inventory"));
      } else {
         var4 = this.itemModelShaper.getItemModel(var1);
      }

      return !var5.hasProperties() ? var4 : this.resolveOverrides(var4, var1, var2, var3);
   }

   public BakedModel getModel(ItemStack var1) {
      return this.getModel(var1, (Level)null, (LivingEntity)null);
   }

   private BakedModel resolveOverrides(BakedModel var1, ItemStack var2, @Nullable Level var3, @Nullable LivingEntity var4) {
      BakedModel var5 = var1.getOverrides().resolve(var1, var2, var3, var4);
      return var5 == null ? this.itemModelShaper.getModelManager().getMissingModel() : var5;
   }

   public void renderWithMobState(ItemStack var1, LivingEntity var2, ItemTransforms.TransformType var3, boolean var4) {
      if (!var1.isEmpty() && var2 != null) {
         BakedModel var5 = this.getInHandModel(var1, var2.level, var2);
         this.renderStatic(var1, var5, var3, var4);
      }
   }

   protected void renderStatic(ItemStack var1, BakedModel var2, ItemTransforms.TransformType var3, boolean var4) {
      if (!var1.isEmpty()) {
         this.textureManager.bind(TextureAtlas.LOCATION_BLOCKS);
         this.textureManager.getTexture(TextureAtlas.LOCATION_BLOCKS).pushFilter(false, false);
         GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
         GlStateManager.enableRescaleNormal();
         GlStateManager.alphaFunc(516, 0.1F);
         GlStateManager.enableBlend();
         GlStateManager.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
         GlStateManager.pushMatrix();
         ItemTransforms var5 = var2.getTransforms();
         ItemTransforms.apply(var5.getTransform(var3), var4);
         if (this.needsFlip(var5.getTransform(var3))) {
            GlStateManager.cullFace(GlStateManager.CullFace.FRONT);
         }

         this.render(var1, var2);
         GlStateManager.cullFace(GlStateManager.CullFace.BACK);
         GlStateManager.popMatrix();
         GlStateManager.disableRescaleNormal();
         GlStateManager.disableBlend();
         this.textureManager.bind(TextureAtlas.LOCATION_BLOCKS);
         this.textureManager.getTexture(TextureAtlas.LOCATION_BLOCKS).popFilter();
      }
   }

   private boolean needsFlip(ItemTransform var1) {
      return var1.scale.x() < 0.0F ^ var1.scale.y() < 0.0F ^ var1.scale.z() < 0.0F;
   }

   public void renderGuiItem(ItemStack var1, int var2, int var3) {
      this.renderGuiItem(var1, var2, var3, this.getModel(var1));
   }

   protected void renderGuiItem(ItemStack var1, int var2, int var3, BakedModel var4) {
      GlStateManager.pushMatrix();
      this.textureManager.bind(TextureAtlas.LOCATION_BLOCKS);
      this.textureManager.getTexture(TextureAtlas.LOCATION_BLOCKS).pushFilter(false, false);
      GlStateManager.enableRescaleNormal();
      GlStateManager.enableAlphaTest();
      GlStateManager.alphaFunc(516, 0.1F);
      GlStateManager.enableBlend();
      GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
      GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
      this.setupGuiItem(var2, var3, var4.isGui3d());
      var4.getTransforms().apply(ItemTransforms.TransformType.GUI);
      this.render(var1, var4);
      GlStateManager.disableAlphaTest();
      GlStateManager.disableRescaleNormal();
      GlStateManager.disableLighting();
      GlStateManager.popMatrix();
      this.textureManager.bind(TextureAtlas.LOCATION_BLOCKS);
      this.textureManager.getTexture(TextureAtlas.LOCATION_BLOCKS).popFilter();
   }

   private void setupGuiItem(int var1, int var2, boolean var3) {
      GlStateManager.translatef((float)var1, (float)var2, 100.0F + this.blitOffset);
      GlStateManager.translatef(8.0F, 8.0F, 0.0F);
      GlStateManager.scalef(1.0F, -1.0F, 1.0F);
      GlStateManager.scalef(16.0F, 16.0F, 16.0F);
      if (var3) {
         GlStateManager.enableLighting();
      } else {
         GlStateManager.disableLighting();
      }

   }

   public void renderAndDecorateItem(ItemStack var1, int var2, int var3) {
      this.renderAndDecorateItem(Minecraft.getInstance().player, var1, var2, var3);
   }

   public void renderAndDecorateItem(@Nullable LivingEntity var1, ItemStack var2, int var3, int var4) {
      if (!var2.isEmpty()) {
         this.blitOffset += 50.0F;

         try {
            this.renderGuiItem(var2, var3, var4, this.getModel(var2, (Level)null, var1));
         } catch (Throwable var8) {
            CrashReport var6 = CrashReport.forThrowable(var8, "Rendering item");
            CrashReportCategory var7 = var6.addCategory("Item being rendered");
            var7.setDetail("Item Type", () -> {
               return String.valueOf(var2.getItem());
            });
            var7.setDetail("Item Damage", () -> {
               return String.valueOf(var2.getDamageValue());
            });
            var7.setDetail("Item NBT", () -> {
               return String.valueOf(var2.getTag());
            });
            var7.setDetail("Item Foil", () -> {
               return String.valueOf(var2.hasFoil());
            });
            throw new ReportedException(var6);
         }

         this.blitOffset -= 50.0F;
      }
   }

   public void renderGuiItemDecorations(Font var1, ItemStack var2, int var3, int var4) {
      this.renderGuiItemDecorations(var1, var2, var3, var4, (String)null);
   }

   public void renderGuiItemDecorations(Font var1, ItemStack var2, int var3, int var4, @Nullable String var5) {
      if (!var2.isEmpty()) {
         if (var2.getCount() != 1 || var5 != null) {
            String var6 = var5 == null ? String.valueOf(var2.getCount()) : var5;
            GlStateManager.disableLighting();
            GlStateManager.disableDepthTest();
            GlStateManager.disableBlend();
            var1.drawShadow(var6, (float)(var3 + 19 - 2 - var1.width(var6)), (float)(var4 + 6 + 3), 16777215);
            GlStateManager.enableBlend();
            GlStateManager.enableLighting();
            GlStateManager.enableDepthTest();
         }

         if (var2.isDamaged()) {
            GlStateManager.disableLighting();
            GlStateManager.disableDepthTest();
            GlStateManager.disableTexture();
            GlStateManager.disableAlphaTest();
            GlStateManager.disableBlend();
            Tesselator var13 = Tesselator.getInstance();
            BufferBuilder var7 = var13.getBuilder();
            float var8 = (float)var2.getDamageValue();
            float var9 = (float)var2.getMaxDamage();
            float var10 = Math.max(0.0F, (var9 - var8) / var9);
            int var11 = Math.round(13.0F - var8 * 13.0F / var9);
            int var12 = Mth.hsvToRgb(var10 / 3.0F, 1.0F, 1.0F);
            this.fillRect(var7, var3 + 2, var4 + 13, 13, 2, 0, 0, 0, 255);
            this.fillRect(var7, var3 + 2, var4 + 13, var11, 1, var12 >> 16 & 255, var12 >> 8 & 255, var12 & 255, 255);
            GlStateManager.enableBlend();
            GlStateManager.enableAlphaTest();
            GlStateManager.enableTexture();
            GlStateManager.enableLighting();
            GlStateManager.enableDepthTest();
         }

         LocalPlayer var14 = Minecraft.getInstance().player;
         float var15 = var14 == null ? 0.0F : var14.getCooldowns().getCooldownPercent(var2.getItem(), Minecraft.getInstance().getFrameTime());
         if (var15 > 0.0F) {
            GlStateManager.disableLighting();
            GlStateManager.disableDepthTest();
            GlStateManager.disableTexture();
            Tesselator var16 = Tesselator.getInstance();
            BufferBuilder var17 = var16.getBuilder();
            this.fillRect(var17, var3, var4 + Mth.floor(16.0F * (1.0F - var15)), 16, Mth.ceil(16.0F * var15), 255, 255, 255, 127);
            GlStateManager.enableTexture();
            GlStateManager.enableLighting();
            GlStateManager.enableDepthTest();
         }

      }
   }

   private void fillRect(BufferBuilder var1, int var2, int var3, int var4, int var5, int var6, int var7, int var8, int var9) {
      var1.begin(7, DefaultVertexFormat.POSITION_COLOR);
      var1.vertex((double)(var2 + 0), (double)(var3 + 0), 0.0D).color(var6, var7, var8, var9).endVertex();
      var1.vertex((double)(var2 + 0), (double)(var3 + var5), 0.0D).color(var6, var7, var8, var9).endVertex();
      var1.vertex((double)(var2 + var4), (double)(var3 + var5), 0.0D).color(var6, var7, var8, var9).endVertex();
      var1.vertex((double)(var2 + var4), (double)(var3 + 0), 0.0D).color(var6, var7, var8, var9).endVertex();
      Tesselator.getInstance().end();
   }

   public void onResourceManagerReload(ResourceManager var1) {
      this.itemModelShaper.rebuildCache();
   }

   static {
      IGNORED = Sets.newHashSet(new Item[]{Items.AIR});
   }
}
