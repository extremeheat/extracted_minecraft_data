package net.minecraft.client.gui.screens.inventory;

import com.mojang.blaze3d.platform.Lighting;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.datafixers.util.Pair;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BannerRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.resources.model.ModelBakery;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
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
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.entity.BannerBlockEntity;
import net.minecraft.world.level.block.entity.BannerPattern;

public class LoomScreen extends AbstractContainerScreen<LoomMenu> {
   private static final ResourceLocation BG_LOCATION = new ResourceLocation("textures/gui/container/loom.png");
   private static final int TOTAL_PATTERN_ROWS;
   private final ModelPart flag;
   @Nullable
   private List<Pair<BannerPattern, DyeColor>> resultBannerPatterns;
   private ItemStack bannerStack;
   private ItemStack dyeStack;
   private ItemStack patternStack;
   private boolean displayPatterns;
   private boolean displaySpecialPattern;
   private boolean hasMaxPatterns;
   private float scrollOffs;
   private boolean scrolling;
   private int startIndex;

   public LoomScreen(LoomMenu var1, Inventory var2, Component var3) {
      super(var1, var2, var3);
      this.bannerStack = ItemStack.EMPTY;
      this.dyeStack = ItemStack.EMPTY;
      this.patternStack = ItemStack.EMPTY;
      this.startIndex = 1;
      this.flag = BannerRenderer.makeFlag();
      var1.registerUpdateListener(this::containerChanged);
      this.titleLabelY -= 2;
   }

   public void render(PoseStack var1, int var2, int var3, float var4) {
      super.render(var1, var2, var3, var4);
      this.renderTooltip(var1, var2, var3);
   }

   protected void renderBg(PoseStack var1, float var2, int var3, int var4) {
      this.renderBackground(var1);
      this.minecraft.getTextureManager().bind(BG_LOCATION);
      int var5 = this.leftPos;
      int var6 = this.topPos;
      this.blit(var1, var5, var6, 0, 0, this.imageWidth, this.imageHeight);
      Slot var7 = ((LoomMenu)this.menu).getBannerSlot();
      Slot var8 = ((LoomMenu)this.menu).getDyeSlot();
      Slot var9 = ((LoomMenu)this.menu).getPatternSlot();
      Slot var10 = ((LoomMenu)this.menu).getResultSlot();
      if (!var7.hasItem()) {
         this.blit(var1, var5 + var7.x, var6 + var7.y, this.imageWidth, 0, 16, 16);
      }

      if (!var8.hasItem()) {
         this.blit(var1, var5 + var8.x, var6 + var8.y, this.imageWidth + 16, 0, 16, 16);
      }

      if (!var9.hasItem()) {
         this.blit(var1, var5 + var9.x, var6 + var9.y, this.imageWidth + 32, 0, 16, 16);
      }

      int var11 = (int)(41.0F * this.scrollOffs);
      this.blit(var1, var5 + 119, var6 + 13 + var11, 232 + (this.displayPatterns ? 0 : 12), 0, 12, 15);
      Lighting.setupForFlatItems();
      if (this.resultBannerPatterns != null && !this.hasMaxPatterns) {
         MultiBufferSource.BufferSource var12 = this.minecraft.renderBuffers().bufferSource();
         var1.pushPose();
         var1.translate((double)(var5 + 139), (double)(var6 + 52), 0.0D);
         var1.scale(24.0F, -24.0F, 1.0F);
         var1.translate(0.5D, 0.5D, 0.5D);
         float var13 = 0.6666667F;
         var1.scale(0.6666667F, -0.6666667F, -0.6666667F);
         this.flag.xRot = 0.0F;
         this.flag.y = -32.0F;
         BannerRenderer.renderPatterns(var1, var12, 15728880, OverlayTexture.NO_OVERLAY, this.flag, ModelBakery.BANNER_BASE, true, this.resultBannerPatterns);
         var1.popPose();
         var12.endBatch();
      } else if (this.hasMaxPatterns) {
         this.blit(var1, var5 + var10.x - 2, var6 + var10.y - 2, this.imageWidth, 17, 17, 16);
      }

      int var14;
      int var20;
      int var21;
      if (this.displayPatterns) {
         var20 = var5 + 60;
         var21 = var6 + 13;
         var14 = this.startIndex + 16;

         for(int var15 = this.startIndex; var15 < var14 && var15 < BannerPattern.COUNT - BannerPattern.PATTERN_ITEM_COUNT; ++var15) {
            int var16 = var15 - this.startIndex;
            int var17 = var20 + var16 % 4 * 14;
            int var18 = var21 + var16 / 4 * 14;
            this.minecraft.getTextureManager().bind(BG_LOCATION);
            int var19 = this.imageHeight;
            if (var15 == ((LoomMenu)this.menu).getSelectedBannerPatternIndex()) {
               var19 += 14;
            } else if (var3 >= var17 && var4 >= var18 && var3 < var17 + 14 && var4 < var18 + 14) {
               var19 += 28;
            }

            this.blit(var1, var17, var18, 0, var19, 14, 14);
            this.renderPattern(var15, var17, var18);
         }
      } else if (this.displaySpecialPattern) {
         var20 = var5 + 60;
         var21 = var6 + 13;
         this.minecraft.getTextureManager().bind(BG_LOCATION);
         this.blit(var1, var20, var21, 0, this.imageHeight, 14, 14);
         var14 = ((LoomMenu)this.menu).getSelectedBannerPatternIndex();
         this.renderPattern(var14, var20, var21);
      }

      Lighting.setupFor3DItems();
   }

   private void renderPattern(int var1, int var2, int var3) {
      ItemStack var4 = new ItemStack(Items.GRAY_BANNER);
      CompoundTag var5 = var4.getOrCreateTagElement("BlockEntityTag");
      ListTag var6 = (new BannerPattern.Builder()).addPattern(BannerPattern.BASE, DyeColor.GRAY).addPattern(BannerPattern.values()[var1], DyeColor.WHITE).toListTag();
      var5.put("Patterns", var6);
      PoseStack var7 = new PoseStack();
      var7.pushPose();
      var7.translate((double)((float)var2 + 0.5F), (double)(var3 + 16), 0.0D);
      var7.scale(6.0F, -6.0F, 1.0F);
      var7.translate(0.5D, 0.5D, 0.0D);
      var7.translate(0.5D, 0.5D, 0.5D);
      float var8 = 0.6666667F;
      var7.scale(0.6666667F, -0.6666667F, -0.6666667F);
      MultiBufferSource.BufferSource var9 = this.minecraft.renderBuffers().bufferSource();
      this.flag.xRot = 0.0F;
      this.flag.y = -32.0F;
      List var10 = BannerBlockEntity.createPatterns(DyeColor.GRAY, BannerBlockEntity.getItemPatterns(var4));
      BannerRenderer.renderPatterns(var7, var9, 15728880, OverlayTexture.NO_OVERLAY, this.flag, ModelBakery.BANNER_BASE, true, var10);
      var7.popPose();
      var9.endBatch();
   }

   public boolean mouseClicked(double var1, double var3, int var5) {
      this.scrolling = false;
      if (this.displayPatterns) {
         int var6 = this.leftPos + 60;
         int var7 = this.topPos + 13;
         int var8 = this.startIndex + 16;

         for(int var9 = this.startIndex; var9 < var8; ++var9) {
            int var10 = var9 - this.startIndex;
            double var11 = var1 - (double)(var6 + var10 % 4 * 14);
            double var13 = var3 - (double)(var7 + var10 / 4 * 14);
            if (var11 >= 0.0D && var13 >= 0.0D && var11 < 14.0D && var13 < 14.0D && ((LoomMenu)this.menu).clickMenuButton(this.minecraft.player, var9)) {
               Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_LOOM_SELECT_PATTERN, 1.0F));
               this.minecraft.gameMode.handleInventoryButtonClick(((LoomMenu)this.menu).containerId, var9);
               return true;
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

   public boolean mouseDragged(double var1, double var3, int var5, double var6, double var8) {
      if (this.scrolling && this.displayPatterns) {
         int var10 = this.topPos + 13;
         int var11 = var10 + 56;
         this.scrollOffs = ((float)var3 - (float)var10 - 7.5F) / ((float)(var11 - var10) - 15.0F);
         this.scrollOffs = Mth.clamp(this.scrollOffs, 0.0F, 1.0F);
         int var12 = TOTAL_PATTERN_ROWS - 4;
         int var13 = (int)((double)(this.scrollOffs * (float)var12) + 0.5D);
         if (var13 < 0) {
            var13 = 0;
         }

         this.startIndex = 1 + var13 * 4;
         return true;
      } else {
         return super.mouseDragged(var1, var3, var5, var6, var8);
      }
   }

   public boolean mouseScrolled(double var1, double var3, double var5) {
      if (this.displayPatterns) {
         int var7 = TOTAL_PATTERN_ROWS - 4;
         this.scrollOffs = (float)((double)this.scrollOffs - var5 / (double)var7);
         this.scrollOffs = Mth.clamp(this.scrollOffs, 0.0F, 1.0F);
         this.startIndex = 1 + (int)((double)(this.scrollOffs * (float)var7) + 0.5D) * 4;
      }

      return true;
   }

   protected boolean hasClickedOutside(double var1, double var3, int var5, int var6, int var7) {
      return var1 < (double)var5 || var3 < (double)var6 || var1 >= (double)(var5 + this.imageWidth) || var3 >= (double)(var6 + this.imageHeight);
   }

   private void containerChanged() {
      ItemStack var1 = ((LoomMenu)this.menu).getResultSlot().getItem();
      if (var1.isEmpty()) {
         this.resultBannerPatterns = null;
      } else {
         this.resultBannerPatterns = BannerBlockEntity.createPatterns(((BannerItem)var1.getItem()).getColor(), BannerBlockEntity.getItemPatterns(var1));
      }

      ItemStack var2 = ((LoomMenu)this.menu).getBannerSlot().getItem();
      ItemStack var3 = ((LoomMenu)this.menu).getDyeSlot().getItem();
      ItemStack var4 = ((LoomMenu)this.menu).getPatternSlot().getItem();
      CompoundTag var5 = var2.getOrCreateTagElement("BlockEntityTag");
      this.hasMaxPatterns = var5.contains("Patterns", 9) && !var2.isEmpty() && var5.getList("Patterns", 10).size() >= 6;
      if (this.hasMaxPatterns) {
         this.resultBannerPatterns = null;
      }

      if (!ItemStack.matches(var2, this.bannerStack) || !ItemStack.matches(var3, this.dyeStack) || !ItemStack.matches(var4, this.patternStack)) {
         this.displayPatterns = !var2.isEmpty() && !var3.isEmpty() && var4.isEmpty() && !this.hasMaxPatterns;
         this.displaySpecialPattern = !this.hasMaxPatterns && !var4.isEmpty() && !var2.isEmpty() && !var3.isEmpty();
      }

      this.bannerStack = var2.copy();
      this.dyeStack = var3.copy();
      this.patternStack = var4.copy();
   }

   static {
      TOTAL_PATTERN_ROWS = (BannerPattern.COUNT - BannerPattern.PATTERN_ITEM_COUNT - 1 + 4 - 1) / 4;
   }
}
