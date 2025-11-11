package net.minecraft.client.gui.screens.inventory;

import com.mojang.blaze3d.platform.Lighting;
import com.mojang.blaze3d.platform.cursor.CursorTypes;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.model.BannerFlagModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.LoomMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.BannerItem;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.DyeItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BannerPattern;
import net.minecraft.world.level.block.entity.BannerPatternLayers;
import org.jspecify.annotations.Nullable;

public class LoomScreen extends AbstractContainerScreen<LoomMenu> {
   private static final Identifier BANNER_SLOT_SPRITE = Identifier.withDefaultNamespace("container/slot/banner");
   private static final Identifier DYE_SLOT_SPRITE = Identifier.withDefaultNamespace("container/slot/dye");
   private static final Identifier PATTERN_SLOT_SPRITE = Identifier.withDefaultNamespace("container/slot/banner_pattern");
   private static final Identifier SCROLLER_SPRITE = Identifier.withDefaultNamespace("container/loom/scroller");
   private static final Identifier SCROLLER_DISABLED_SPRITE = Identifier.withDefaultNamespace("container/loom/scroller_disabled");
   private static final Identifier PATTERN_SELECTED_SPRITE = Identifier.withDefaultNamespace("container/loom/pattern_selected");
   private static final Identifier PATTERN_HIGHLIGHTED_SPRITE = Identifier.withDefaultNamespace("container/loom/pattern_highlighted");
   private static final Identifier PATTERN_SPRITE = Identifier.withDefaultNamespace("container/loom/pattern");
   private static final Identifier ERROR_SPRITE = Identifier.withDefaultNamespace("container/loom/error");
   private static final Identifier BG_LOCATION = Identifier.withDefaultNamespace("textures/gui/container/loom.png");
   private static final int PATTERN_COLUMNS = 4;
   private static final int PATTERN_ROWS = 4;
   private static final int SCROLLER_WIDTH = 12;
   private static final int SCROLLER_HEIGHT = 15;
   private static final int PATTERN_IMAGE_SIZE = 14;
   private static final int SCROLLER_FULL_HEIGHT = 56;
   private static final int PATTERNS_X = 60;
   private static final int PATTERNS_Y = 13;
   private static final float BANNER_PATTERN_TEXTURE_SIZE = 64.0F;
   private static final float BANNER_PATTERN_WIDTH = 21.0F;
   private static final float BANNER_PATTERN_HEIGHT = 40.0F;
   private BannerFlagModel flag;
   private @Nullable BannerPatternLayers resultBannerPatterns;
   private ItemStack bannerStack;
   private ItemStack dyeStack;
   private ItemStack patternStack;
   private boolean displayPatterns;
   private boolean hasMaxPatterns;
   private float scrollOffs;
   private boolean scrolling;
   private int startRow;

   public LoomScreen(LoomMenu var1, Inventory var2, Component var3) {
      super(var1, var2, var3);
      this.bannerStack = ItemStack.EMPTY;
      this.dyeStack = ItemStack.EMPTY;
      this.patternStack = ItemStack.EMPTY;
      var1.registerUpdateListener(this::containerChanged);
      this.titleLabelY -= 2;
   }

   protected void init() {
      super.init();
      ModelPart var1 = this.minecraft.getEntityModels().bakeLayer(ModelLayers.STANDING_BANNER_FLAG);
      this.flag = new BannerFlagModel(var1);
   }

   public void render(GuiGraphics var1, int var2, int var3, float var4) {
      super.render(var1, var2, var3, var4);
      this.renderTooltip(var1, var2, var3);
   }

   private int totalRowCount() {
      return Mth.positiveCeilDiv(((LoomMenu)this.menu).getSelectablePatterns().size(), 4);
   }

   protected void renderBg(GuiGraphics var1, float var2, int var3, int var4) {
      int var5 = this.leftPos;
      int var6 = this.topPos;
      var1.blit(RenderPipelines.GUI_TEXTURED, BG_LOCATION, var5, var6, 0.0F, 0.0F, this.imageWidth, this.imageHeight, 256, 256);
      Slot var7 = ((LoomMenu)this.menu).getBannerSlot();
      Slot var8 = ((LoomMenu)this.menu).getDyeSlot();
      Slot var9 = ((LoomMenu)this.menu).getPatternSlot();
      Slot var10 = ((LoomMenu)this.menu).getResultSlot();
      if (!var7.hasItem()) {
         var1.blitSprite(RenderPipelines.GUI_TEXTURED, (Identifier)BANNER_SLOT_SPRITE, var5 + var7.x, var6 + var7.y, 16, 16);
      }

      if (!var8.hasItem()) {
         var1.blitSprite(RenderPipelines.GUI_TEXTURED, (Identifier)DYE_SLOT_SPRITE, var5 + var8.x, var6 + var8.y, 16, 16);
      }

      if (!var9.hasItem()) {
         var1.blitSprite(RenderPipelines.GUI_TEXTURED, (Identifier)PATTERN_SLOT_SPRITE, var5 + var9.x, var6 + var9.y, 16, 16);
      }

      int var11 = (int)(41.0F * this.scrollOffs);
      Identifier var12 = this.displayPatterns ? SCROLLER_SPRITE : SCROLLER_DISABLED_SPRITE;
      int var13 = var5 + 119;
      int var14 = var6 + 13 + var11;
      var1.blitSprite(RenderPipelines.GUI_TEXTURED, (Identifier)var12, var13, var14, 12, 15);
      if (var3 >= var13 && var3 < var13 + 12 && var4 >= var14 && var4 < var14 + 15) {
         var1.requestCursor(this.scrolling ? CursorTypes.RESIZE_NS : CursorTypes.POINTING_HAND);
      }

      if (this.resultBannerPatterns != null && !this.hasMaxPatterns) {
         DyeColor var15 = ((BannerItem)var10.getItem().getItem()).getColor();
         int var16 = var5 + 141;
         int var17 = var6 + 8;
         var1.submitBannerPatternRenderState(this.flag, var15, this.resultBannerPatterns, var16, var17, var16 + 20, var17 + 40);
      } else if (this.hasMaxPatterns) {
         var1.blitSprite(RenderPipelines.GUI_TEXTURED, (Identifier)ERROR_SPRITE, var5 + var10.x - 5, var6 + var10.y - 5, 26, 26);
      }

      if (this.displayPatterns) {
         int var28 = var5 + 60;
         int var29 = var6 + 13;
         List var30 = (this.menu).getSelectablePatterns();

         label79:
         for(int var18 = 0; var18 < 4; ++var18) {
            for(int var19 = 0; var19 < 4; ++var19) {
               int var20 = var18 + this.startRow;
               int var21 = var20 * 4 + var19;
               if (var21 >= var30.size()) {
                  break label79;
               }

               int var22 = var28 + var19 * 14;
               int var23 = var29 + var18 * 14;
               Holder var24 = (Holder)var30.get(var21);
               boolean var25 = var3 >= var22 && var4 >= var23 && var3 < var22 + 14 && var4 < var23 + 14;
               Identifier var26;
               if (var21 == ((LoomMenu)this.menu).getSelectedBannerPatternIndex()) {
                  var26 = PATTERN_SELECTED_SPRITE;
               } else if (var25) {
                  var26 = PATTERN_HIGHLIGHTED_SPRITE;
                  DyeColor var27 = ((DyeItem)this.dyeStack.getItem()).getDyeColor();
                  String var10001 = ((BannerPattern)var24.value()).translationKey();
                  var1.setTooltipForNextFrame(Component.translatable(var10001 + "." + var27.getName()), var3, var4);
                  var1.requestCursor(CursorTypes.POINTING_HAND);
               } else {
                  var26 = PATTERN_SPRITE;
               }

               var1.blitSprite(RenderPipelines.GUI_TEXTURED, (Identifier)var26, var22, var23, 14, 14);
               TextureAtlasSprite var31 = var1.getSprite(Sheets.getBannerMaterial(var24));
               this.renderBannerOnButton(var1, var22, var23, var31);
            }
         }
      }

      Minecraft.getInstance().gameRenderer.getLighting().setupFor(Lighting.Entry.ITEMS_3D);
   }

   private void renderBannerOnButton(GuiGraphics var1, int var2, int var3, TextureAtlasSprite var4) {
      var1.pose().pushMatrix();
      var1.pose().translate((float)(var2 + 4), (float)(var3 + 2));
      float var5 = var4.getU0();
      float var6 = var5 + (var4.getU1() - var4.getU0()) * 21.0F / 64.0F;
      float var7 = var4.getV1() - var4.getV0();
      float var8 = var4.getV0() + var7 / 64.0F;
      float var9 = var8 + var7 * 40.0F / 64.0F;
      boolean var10 = true;
      boolean var11 = true;
      var1.fill(0, 0, 5, 10, DyeColor.GRAY.getTextureDiffuseColor());
      var1.blit(var4.atlasLocation(), 0, 0, 5, 10, var5, var6, var8, var9);
      var1.pose().popMatrix();
   }

   public boolean mouseClicked(MouseButtonEvent var1, boolean var2) {
      if (this.displayPatterns) {
         int var3 = this.leftPos + 60;
         int var4 = this.topPos + 13;

         for(int var5 = 0; var5 < 4; ++var5) {
            for(int var6 = 0; var6 < 4; ++var6) {
               double var7 = var1.x() - (double)(var3 + var6 * 14);
               double var9 = var1.y() - (double)(var4 + var5 * 14);
               int var11 = var5 + this.startRow;
               int var12 = var11 * 4 + var6;
               if (var7 >= 0.0 && var9 >= 0.0 && var7 < 14.0 && var9 < 14.0 && ((LoomMenu)this.menu).clickMenuButton(this.minecraft.player, var12)) {
                  Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_LOOM_SELECT_PATTERN, 1.0F));
                  this.minecraft.gameMode.handleInventoryButtonClick((this.menu).containerId, var12);
                  return true;
               }
            }
         }

         var3 = this.leftPos + 119;
         var4 = this.topPos + 9;
         if (var1.x() >= (double)var3 && var1.x() < (double)(var3 + 12) && var1.y() >= (double)var4 && var1.y() < (double)(var4 + 56)) {
            this.scrolling = true;
         }
      }

      return super.mouseClicked(var1, var2);
   }

   public boolean mouseDragged(MouseButtonEvent var1, double var2, double var4) {
      int var6 = this.totalRowCount() - 4;
      if (this.scrolling && this.displayPatterns && var6 > 0) {
         int var7 = this.topPos + 13;
         int var8 = var7 + 56;
         this.scrollOffs = ((float)var1.y() - (float)var7 - 7.5F) / ((float)(var8 - var7) - 15.0F);
         this.scrollOffs = Mth.clamp(this.scrollOffs, 0.0F, 1.0F);
         this.startRow = Math.max((int)((double)(this.scrollOffs * (float)var6) + 0.5), 0);
         return true;
      } else {
         return super.mouseDragged(var1, var2, var4);
      }
   }

   public boolean mouseReleased(MouseButtonEvent var1) {
      this.scrolling = false;
      return super.mouseReleased(var1);
   }

   public boolean mouseScrolled(double var1, double var3, double var5, double var7) {
      if (super.mouseScrolled(var1, var3, var5, var7)) {
         return true;
      } else {
         int var9 = this.totalRowCount() - 4;
         if (this.displayPatterns && var9 > 0) {
            float var10 = (float)var7 / (float)var9;
            this.scrollOffs = Mth.clamp(this.scrollOffs - var10, 0.0F, 1.0F);
            this.startRow = Math.max((int)(this.scrollOffs * (float)var9 + 0.5F), 0);
         }

         return true;
      }
   }

   protected boolean hasClickedOutside(double var1, double var3, int var5, int var6) {
      return var1 < (double)var5 || var3 < (double)var6 || var1 >= (double)(var5 + this.imageWidth) || var3 >= (double)(var6 + this.imageHeight);
   }

   private void containerChanged() {
      ItemStack var1 = ((LoomMenu)this.menu).getResultSlot().getItem();
      if (var1.isEmpty()) {
         this.resultBannerPatterns = null;
      } else {
         this.resultBannerPatterns = (BannerPatternLayers)var1.getOrDefault(DataComponents.BANNER_PATTERNS, BannerPatternLayers.EMPTY);
      }

      ItemStack var2 = ((LoomMenu)this.menu).getBannerSlot().getItem();
      ItemStack var3 = ((LoomMenu)this.menu).getDyeSlot().getItem();
      ItemStack var4 = ((LoomMenu)this.menu).getPatternSlot().getItem();
      BannerPatternLayers var5 = (BannerPatternLayers)var2.getOrDefault(DataComponents.BANNER_PATTERNS, BannerPatternLayers.EMPTY);
      this.hasMaxPatterns = var5.layers().size() >= 6;
      if (this.hasMaxPatterns) {
         this.resultBannerPatterns = null;
      }

      if (!ItemStack.matches(var2, this.bannerStack) || !ItemStack.matches(var3, this.dyeStack) || !ItemStack.matches(var4, this.patternStack)) {
         this.displayPatterns = !var2.isEmpty() && !var3.isEmpty() && !this.hasMaxPatterns && !((LoomMenu)this.menu).getSelectablePatterns().isEmpty();
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
