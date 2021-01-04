package net.minecraft.client.gui.screens.inventory;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.platform.GlStateManager;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.banner.BannerTextures;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.nbt.CompoundTag;
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
import net.minecraft.world.level.block.entity.BannerBlockEntity;
import net.minecraft.world.level.block.entity.BannerPattern;

public class LoomScreen extends AbstractContainerScreen<LoomMenu> {
   private static final ResourceLocation BG_LOCATION = new ResourceLocation("textures/gui/container/loom.png");
   private static final int TOTAL_PATTERN_ROWS;
   private static final DyeColor PATTERN_BASE_COLOR;
   private static final DyeColor PATTERN_OVERLAY_COLOR;
   private static final List<DyeColor> PATTERN_COLORS;
   private ResourceLocation resultBannerTexture;
   private ItemStack bannerStack;
   private ItemStack dyeStack;
   private ItemStack patternStack;
   private final ResourceLocation[] patternTextures;
   private boolean displayPatterns;
   private boolean displaySpecialPattern;
   private boolean hasMaxPatterns;
   private float scrollOffs;
   private boolean scrolling;
   private int startIndex;
   private int loadNextTextureIndex;

   public LoomScreen(LoomMenu var1, Inventory var2, Component var3) {
      super(var1, var2, var3);
      this.bannerStack = ItemStack.EMPTY;
      this.dyeStack = ItemStack.EMPTY;
      this.patternStack = ItemStack.EMPTY;
      this.patternTextures = new ResourceLocation[BannerPattern.COUNT];
      this.startIndex = 1;
      this.loadNextTextureIndex = 1;
      var1.registerUpdateListener(this::containerChanged);
   }

   public void tick() {
      super.tick();
      if (this.loadNextTextureIndex < BannerPattern.COUNT) {
         BannerPattern var1 = BannerPattern.values()[this.loadNextTextureIndex];
         String var2 = "b" + PATTERN_BASE_COLOR.getId();
         String var3 = var1.getHashname() + PATTERN_OVERLAY_COLOR.getId();
         this.patternTextures[this.loadNextTextureIndex] = BannerTextures.BANNER_CACHE.getTextureLocation(var2 + var3, Lists.newArrayList(new BannerPattern[]{BannerPattern.BASE, var1}), PATTERN_COLORS);
         ++this.loadNextTextureIndex;
      }

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
      GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
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
      if (this.resultBannerTexture != null && !this.hasMaxPatterns) {
         this.minecraft.getTextureManager().bind(this.resultBannerTexture);
         blit(var4 + 141, var5 + 8, 20, 40, 1.0F, 1.0F, 20, 40, 64, 64);
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

         for(int var14 = this.startIndex; var14 < var13 && var14 < this.patternTextures.length - 5; ++var14) {
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
            if (this.patternTextures[var14] != null) {
               this.minecraft.getTextureManager().bind(this.patternTextures[var14]);
               blit(var16 + 4, var17 + 2, 5, 10, 1.0F, 1.0F, 20, 40, 64, 64);
            }
         }
      } else if (this.displaySpecialPattern) {
         var11 = var4 + 60;
         var12 = var5 + 13;
         this.minecraft.getTextureManager().bind(BG_LOCATION);
         this.blit(var11, var12, 0, this.imageHeight, 14, 14);
         var13 = ((LoomMenu)this.menu).getSelectedBannerPatternIndex();
         if (this.patternTextures[var13] != null) {
            this.minecraft.getTextureManager().bind(this.patternTextures[var13]);
            blit(var11 + 4, var12 + 2, 5, 10, 1.0F, 1.0F, 20, 40, 64, 64);
         }
      }

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
         this.resultBannerTexture = null;
      } else {
         BannerBlockEntity var2 = new BannerBlockEntity();
         var2.fromItem(var1, ((BannerItem)var1.getItem()).getColor());
         this.resultBannerTexture = BannerTextures.BANNER_CACHE.getTextureLocation(var2.getTextureHashName(), var2.getPatterns(), var2.getColors());
      }

      ItemStack var6 = ((LoomMenu)this.menu).getBannerSlot().getItem();
      ItemStack var3 = ((LoomMenu)this.menu).getDyeSlot().getItem();
      ItemStack var4 = ((LoomMenu)this.menu).getPatternSlot().getItem();
      CompoundTag var5 = var6.getOrCreateTagElement("BlockEntityTag");
      this.hasMaxPatterns = var5.contains("Patterns", 9) && !var6.isEmpty() && var5.getList("Patterns", 10).size() >= 6;
      if (this.hasMaxPatterns) {
         this.resultBannerTexture = null;
      }

      if (!ItemStack.matches(var6, this.bannerStack) || !ItemStack.matches(var3, this.dyeStack) || !ItemStack.matches(var4, this.patternStack)) {
         this.displayPatterns = !var6.isEmpty() && !var3.isEmpty() && var4.isEmpty() && !this.hasMaxPatterns;
         this.displaySpecialPattern = !this.hasMaxPatterns && !var4.isEmpty() && !var6.isEmpty() && !var3.isEmpty();
      }

      this.bannerStack = var6.copy();
      this.dyeStack = var3.copy();
      this.patternStack = var4.copy();
   }

   static {
      TOTAL_PATTERN_ROWS = (BannerPattern.COUNT - 5 - 1 + 4 - 1) / 4;
      PATTERN_BASE_COLOR = DyeColor.GRAY;
      PATTERN_OVERLAY_COLOR = DyeColor.WHITE;
      PATTERN_COLORS = Lists.newArrayList(new DyeColor[]{PATTERN_BASE_COLOR, PATTERN_OVERLAY_COLOR});
   }
}
