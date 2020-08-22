package net.minecraft.client.gui.screens.inventory;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import javax.annotation.Nullable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BannerRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderDispatcher;
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

public class LoomScreen extends AbstractContainerScreen {
   private static final ResourceLocation BG_LOCATION = new ResourceLocation("textures/gui/container/loom.png");
   private static final int TOTAL_PATTERN_ROWS;
   private final ModelPart flag;
   @Nullable
   private BannerBlockEntity resultBanner;
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
   }

   public void render(int var1, int var2, float var3) {
      super.render(var1, var2, var3);
      this.renderTooltip(var1, var2);
   }

   protected void renderLabels(int var1, int var2) {
      this.font.draw(this.title.getColoredString(), 8.0F, 4.0F, 4210752);
      this.font.draw(this.inventory.getDisplayName().getColoredString(), 8.0F, (float)(this.imageHeight - 96 + 2), 4210752);
   }

   protected void renderBg(float var1, int var2, int var3) {
      this.renderBackground();
      this.minecraft.getTextureManager().bind(BG_LOCATION);
      int var4 = this.leftPos;
      int var5 = this.topPos;
      this.blit(var4, var5, 0, 0, this.imageWidth, this.imageHeight);
      Slot var6 = ((LoomMenu)this.menu).getBannerSlot();
      Slot var7 = ((LoomMenu)this.menu).getDyeSlot();
      Slot var8 = ((LoomMenu)this.menu).getPatternSlot();
      Slot var9 = ((LoomMenu)this.menu).getResultSlot();
      if (!var6.hasItem()) {
         this.blit(var4 + var6.x, var5 + var6.y, this.imageWidth, 0, 16, 16);
      }

      if (!var7.hasItem()) {
         this.blit(var4 + var7.x, var5 + var7.y, this.imageWidth + 16, 0, 16, 16);
      }

      if (!var8.hasItem()) {
         this.blit(var4 + var8.x, var5 + var8.y, this.imageWidth + 32, 0, 16, 16);
      }

      int var10 = (int)(41.0F * this.scrollOffs);
      this.blit(var4 + 119, var5 + 13 + var10, 232 + (this.displayPatterns ? 0 : 12), 0, 12, 15);
      if (this.resultBanner != null && !this.hasMaxPatterns) {
         RenderSystem.pushMatrix();
         RenderSystem.translatef((float)(var4 + 139), (float)(var5 + 52), 0.0F);
         RenderSystem.scalef(24.0F, -24.0F, 1.0F);
         this.resultBanner.setOnlyRenderPattern(true);
         BlockEntityRenderDispatcher.instance.renderItem(this.resultBanner, new PoseStack());
         this.resultBanner.setOnlyRenderPattern(false);
         RenderSystem.popMatrix();
      } else if (this.hasMaxPatterns) {
         this.blit(var4 + var9.x - 2, var5 + var9.y - 2, this.imageWidth, 17, 17, 16);
      }

      int var11;
      int var12;
      int var13;
      if (this.displayPatterns) {
         var11 = var4 + 60;
         var12 = var5 + 13;
         var13 = this.startIndex + 16;

         for(int var14 = this.startIndex; var14 < var13 && var14 < BannerPattern.COUNT - 5; ++var14) {
            int var15 = var14 - this.startIndex;
            int var16 = var11 + var15 % 4 * 14;
            int var17 = var12 + var15 / 4 * 14;
            this.minecraft.getTextureManager().bind(BG_LOCATION);
            int var18 = this.imageHeight;
            if (var14 == ((LoomMenu)this.menu).getSelectedBannerPatternIndex()) {
               var18 += 14;
            } else if (var2 >= var16 && var3 >= var17 && var2 < var16 + 14 && var3 < var17 + 14) {
               var18 += 28;
            }

            this.blit(var16, var17, 0, var18, 14, 14);
            this.renderPattern(var14, var16, var17);
         }
      } else if (this.displaySpecialPattern) {
         var11 = var4 + 60;
         var12 = var5 + 13;
         this.minecraft.getTextureManager().bind(BG_LOCATION);
         this.blit(var11, var12, 0, this.imageHeight, 14, 14);
         var13 = ((LoomMenu)this.menu).getSelectedBannerPatternIndex();
         this.renderPattern(var13, var11, var12);
      }

   }

   private void renderPattern(int var1, int var2, int var3) {
      BannerBlockEntity var4 = new BannerBlockEntity();
      var4.setOnlyRenderPattern(true);
      ItemStack var5 = new ItemStack(Items.GRAY_BANNER);
      CompoundTag var6 = var5.getOrCreateTagElement("BlockEntityTag");
      ListTag var7 = (new BannerPattern.Builder()).addPattern(BannerPattern.BASE, DyeColor.GRAY).addPattern(BannerPattern.values()[var1], DyeColor.WHITE).toListTag();
      var6.put("Patterns", var7);
      var4.fromItem(var5, DyeColor.GRAY);
      PoseStack var8 = new PoseStack();
      var8.pushPose();
      var8.translate((double)((float)var2 + 0.5F), (double)(var3 + 16), 0.0D);
      var8.scale(6.0F, -6.0F, 1.0F);
      var8.translate(0.5D, 0.5D, 0.0D);
      float var9 = 0.6666667F;
      var8.translate(0.5D, 0.5D, 0.5D);
      var8.scale(0.6666667F, -0.6666667F, -0.6666667F);
      MultiBufferSource.BufferSource var10 = this.minecraft.renderBuffers().bufferSource();
      this.flag.xRot = 0.0F;
      this.flag.y = -32.0F;
      BannerRenderer.renderPatterns(var4, var8, var10, 15728880, OverlayTexture.NO_OVERLAY, this.flag, ModelBakery.BANNER_BASE, true);
      var8.popPose();
      var10.endBatch();
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
         this.resultBanner = null;
      } else {
         this.resultBanner = new BannerBlockEntity();
         this.resultBanner.fromItem(var1, ((BannerItem)var1.getItem()).getColor());
      }

      ItemStack var2 = ((LoomMenu)this.menu).getBannerSlot().getItem();
      ItemStack var3 = ((LoomMenu)this.menu).getDyeSlot().getItem();
      ItemStack var4 = ((LoomMenu)this.menu).getPatternSlot().getItem();
      CompoundTag var5 = var2.getOrCreateTagElement("BlockEntityTag");
      this.hasMaxPatterns = var5.contains("Patterns", 9) && !var2.isEmpty() && var5.getList("Patterns", 10).size() >= 6;
      if (this.hasMaxPatterns) {
         this.resultBanner = null;
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
      TOTAL_PATTERN_ROWS = (BannerPattern.COUNT - 5 - 1 + 4 - 1) / 4;
   }
}
