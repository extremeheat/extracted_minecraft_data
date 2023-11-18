package net.minecraft.client.gui.screens.inventory;

import com.mojang.blaze3d.platform.Lighting;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.datafixers.util.Pair;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.blockentity.BannerRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.resources.model.ModelBakery;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.core.Holder;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.LoomMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.BannerItem;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.entity.BannerBlockEntity;
import net.minecraft.world.level.block.entity.BannerPattern;
import net.minecraft.world.level.block.entity.BannerPatterns;
import net.minecraft.world.level.block.entity.BlockEntityType;

public class LoomScreen extends AbstractContainerScreen<LoomMenu> {
   private static final ResourceLocation BG_LOCATION = new ResourceLocation("textures/gui/container/loom.png");
   private static final int PATTERN_COLUMNS = 4;
   private static final int PATTERN_ROWS = 4;
   private static final int SCROLLER_WIDTH = 12;
   private static final int SCROLLER_HEIGHT = 15;
   private static final int PATTERN_IMAGE_SIZE = 14;
   private static final int SCROLLER_FULL_HEIGHT = 56;
   private static final int PATTERNS_X = 60;
   private static final int PATTERNS_Y = 13;
   private ModelPart flag;
   @Nullable
   private List<Pair<Holder<BannerPattern>, DyeColor>> resultBannerPatterns;
   private ItemStack bannerStack = ItemStack.EMPTY;
   private ItemStack dyeStack = ItemStack.EMPTY;
   private ItemStack patternStack = ItemStack.EMPTY;
   private boolean displayPatterns;
   private boolean hasMaxPatterns;
   private float scrollOffs;
   private boolean scrolling;
   private int startRow;

   public LoomScreen(LoomMenu var1, Inventory var2, Component var3) {
      super(var1, var2, var3);
      var1.registerUpdateListener(this::containerChanged);
      this.titleLabelY -= 2;
   }

   @Override
   protected void init() {
      super.init();
      this.flag = this.minecraft.getEntityModels().bakeLayer(ModelLayers.BANNER).getChild("flag");
   }

   @Override
   public void render(GuiGraphics var1, int var2, int var3, float var4) {
      super.render(var1, var2, var3, var4);
      this.renderTooltip(var1, var2, var3);
   }

   private int totalRowCount() {
      return Mth.positiveCeilDiv(this.menu.getSelectablePatterns().size(), 4);
   }

   @Override
   protected void renderBg(GuiGraphics var1, float var2, int var3, int var4) {
      this.renderBackground(var1);
      int var5 = this.leftPos;
      int var6 = this.topPos;
      var1.blit(BG_LOCATION, var5, var6, 0, 0, this.imageWidth, this.imageHeight);
      Slot var7 = this.menu.getBannerSlot();
      Slot var8 = this.menu.getDyeSlot();
      Slot var9 = this.menu.getPatternSlot();
      Slot var10 = this.menu.getResultSlot();
      if (!var7.hasItem()) {
         var1.blit(BG_LOCATION, var5 + var7.x, var6 + var7.y, this.imageWidth, 0, 16, 16);
      }

      if (!var8.hasItem()) {
         var1.blit(BG_LOCATION, var5 + var8.x, var6 + var8.y, this.imageWidth + 16, 0, 16, 16);
      }

      if (!var9.hasItem()) {
         var1.blit(BG_LOCATION, var5 + var9.x, var6 + var9.y, this.imageWidth + 32, 0, 16, 16);
      }

      int var11 = (int)(41.0F * this.scrollOffs);
      var1.blit(BG_LOCATION, var5 + 119, var6 + 13 + var11, 232 + (this.displayPatterns ? 0 : 12), 0, 12, 15);
      Lighting.setupForFlatItems();
      if (this.resultBannerPatterns != null && !this.hasMaxPatterns) {
         var1.pose().pushPose();
         var1.pose().translate((float)(var5 + 139), (float)(var6 + 52), 0.0F);
         var1.pose().scale(24.0F, -24.0F, 1.0F);
         var1.pose().translate(0.5F, 0.5F, 0.5F);
         float var12 = 0.6666667F;
         var1.pose().scale(0.6666667F, -0.6666667F, -0.6666667F);
         this.flag.xRot = 0.0F;
         this.flag.y = -32.0F;
         BannerRenderer.renderPatterns(
            var1.pose(), var1.bufferSource(), 15728880, OverlayTexture.NO_OVERLAY, this.flag, ModelBakery.BANNER_BASE, true, this.resultBannerPatterns
         );
         var1.pose().popPose();
         var1.flush();
      } else if (this.hasMaxPatterns) {
         var1.blit(BG_LOCATION, var5 + var10.x - 2, var6 + var10.y - 2, this.imageWidth, 17, 17, 16);
      }

      if (this.displayPatterns) {
         int var23 = var5 + 60;
         int var13 = var6 + 13;
         List var14 = this.menu.getSelectablePatterns();

         label64:
         for(int var15 = 0; var15 < 4; ++var15) {
            for(int var16 = 0; var16 < 4; ++var16) {
               int var17 = var15 + this.startRow;
               int var18 = var17 * 4 + var16;
               if (var18 >= var14.size()) {
                  break label64;
               }

               int var19 = var23 + var16 * 14;
               int var20 = var13 + var15 * 14;
               boolean var21 = var3 >= var19 && var4 >= var20 && var3 < var19 + 14 && var4 < var20 + 14;
               int var22;
               if (var18 == this.menu.getSelectedBannerPatternIndex()) {
                  var22 = this.imageHeight + 14;
               } else if (var21) {
                  var22 = this.imageHeight + 28;
               } else {
                  var22 = this.imageHeight;
               }

               var1.blit(BG_LOCATION, var19, var20, 0, var22, 14, 14);
               this.renderPattern(var1, (Holder<BannerPattern>)var14.get(var18), var19, var20);
            }
         }
      }

      Lighting.setupFor3DItems();
   }

   private void renderPattern(GuiGraphics var1, Holder<BannerPattern> var2, int var3, int var4) {
      CompoundTag var5 = new CompoundTag();
      ListTag var6 = new BannerPattern.Builder().addPattern(BannerPatterns.BASE, DyeColor.GRAY).addPattern(var2, DyeColor.WHITE).toListTag();
      var5.put("Patterns", var6);
      ItemStack var7 = new ItemStack(Items.GRAY_BANNER);
      BlockItem.setBlockEntityData(var7, BlockEntityType.BANNER, var5);
      PoseStack var8 = new PoseStack();
      var8.pushPose();
      var8.translate((float)var3 + 0.5F, (float)(var4 + 16), 0.0F);
      var8.scale(6.0F, -6.0F, 1.0F);
      var8.translate(0.5F, 0.5F, 0.0F);
      var8.translate(0.5F, 0.5F, 0.5F);
      float var9 = 0.6666667F;
      var8.scale(0.6666667F, -0.6666667F, -0.6666667F);
      this.flag.xRot = 0.0F;
      this.flag.y = -32.0F;
      List var10 = BannerBlockEntity.createPatterns(DyeColor.GRAY, BannerBlockEntity.getItemPatterns(var7));
      BannerRenderer.renderPatterns(var8, var1.bufferSource(), 15728880, OverlayTexture.NO_OVERLAY, this.flag, ModelBakery.BANNER_BASE, true, var10);
      var8.popPose();
      var1.flush();
   }

   @Override
   public boolean mouseClicked(double var1, double var3, int var5) {
      this.scrolling = false;
      if (this.displayPatterns) {
         int var6 = this.leftPos + 60;
         int var7 = this.topPos + 13;

         for(int var8 = 0; var8 < 4; ++var8) {
            for(int var9 = 0; var9 < 4; ++var9) {
               double var10 = var1 - (double)(var6 + var9 * 14);
               double var12 = var3 - (double)(var7 + var8 * 14);
               int var14 = var8 + this.startRow;
               int var15 = var14 * 4 + var9;
               if (var10 >= 0.0 && var12 >= 0.0 && var10 < 14.0 && var12 < 14.0 && this.menu.clickMenuButton(this.minecraft.player, var15)) {
                  Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_LOOM_SELECT_PATTERN, 1.0F));
                  this.minecraft.gameMode.handleInventoryButtonClick(this.menu.containerId, var15);
                  return true;
               }
            }
         }

         var6 = this.leftPos + 119;
         var7 = this.topPos + 9;
         if (var1 >= (double)var6 && var1 < (double)(var6 + 12) && var3 >= (double)var7 && var3 < (double)(var7 + 56)) {
            this.scrolling = true;
         }
      }

      return super.mouseClicked(var1, var3, var5);
   }

   @Override
   public boolean mouseDragged(double var1, double var3, int var5, double var6, double var8) {
      int var10 = this.totalRowCount() - 4;
      if (this.scrolling && this.displayPatterns && var10 > 0) {
         int var11 = this.topPos + 13;
         int var12 = var11 + 56;
         this.scrollOffs = ((float)var3 - (float)var11 - 7.5F) / ((float)(var12 - var11) - 15.0F);
         this.scrollOffs = Mth.clamp(this.scrollOffs, 0.0F, 1.0F);
         this.startRow = Math.max((int)((double)(this.scrollOffs * (float)var10) + 0.5), 0);
         return true;
      } else {
         return super.mouseDragged(var1, var3, var5, var6, var8);
      }
   }

   @Override
   public boolean mouseScrolled(double var1, double var3, double var5) {
      int var7 = this.totalRowCount() - 4;
      if (this.displayPatterns && var7 > 0) {
         float var8 = (float)var5 / (float)var7;
         this.scrollOffs = Mth.clamp(this.scrollOffs - var8, 0.0F, 1.0F);
         this.startRow = Math.max((int)(this.scrollOffs * (float)var7 + 0.5F), 0);
      }

      return true;
   }

   @Override
   protected boolean hasClickedOutside(double var1, double var3, int var5, int var6, int var7) {
      return var1 < (double)var5 || var3 < (double)var6 || var1 >= (double)(var5 + this.imageWidth) || var3 >= (double)(var6 + this.imageHeight);
   }

   private void containerChanged() {
      ItemStack var1 = this.menu.getResultSlot().getItem();
      if (var1.isEmpty()) {
         this.resultBannerPatterns = null;
      } else {
         this.resultBannerPatterns = BannerBlockEntity.createPatterns(((BannerItem)var1.getItem()).getColor(), BannerBlockEntity.getItemPatterns(var1));
      }

      ItemStack var2 = this.menu.getBannerSlot().getItem();
      ItemStack var3 = this.menu.getDyeSlot().getItem();
      ItemStack var4 = this.menu.getPatternSlot().getItem();
      CompoundTag var5 = BlockItem.getBlockEntityData(var2);
      this.hasMaxPatterns = var5 != null && var5.contains("Patterns", 9) && !var2.isEmpty() && var5.getList("Patterns", 10).size() >= 6;
      if (this.hasMaxPatterns) {
         this.resultBannerPatterns = null;
      }

      if (!ItemStack.matches(var2, this.bannerStack) || !ItemStack.matches(var3, this.dyeStack) || !ItemStack.matches(var4, this.patternStack)) {
         this.displayPatterns = !var2.isEmpty() && !var3.isEmpty() && !this.hasMaxPatterns && !this.menu.getSelectablePatterns().isEmpty();
      }

      if (this.startRow >= this.totalRowCount()) {
         this.startRow = 0;
         this.scrollOffs = 0.0F;
      }

      this.bannerStack = var2.copy();
      this.dyeStack = var3.copy();
      this.patternStack = var4.copy();
   }
}
