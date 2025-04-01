package net.minecraft.client.gui.screens.inventory;

import com.mojang.blaze3d.platform.Lighting;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.SpriteIconButton;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.inventory.GhettoSpline;
import net.minecraft.world.inventory.MineCraftingDrawerSlot;
import net.minecraft.world.inventory.MineCraftingMenu;
import net.minecraft.world.inventory.MineCraftingResultSlot;
import net.minecraft.world.inventory.MineCraftingSlot;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.level.block.entity.MineCrafterBlockEntity;
import net.minecraft.world.phys.Vec2;

public class MineCraftingScreen extends AbstractContainerScreen<MineCraftingMenu> {
   private static final ResourceLocation CONTAINER_SLOT_SPRITE = ResourceLocation.withDefaultNamespace("container/slot");
   private static final ResourceLocation CONTAINER_SLOT_DISABLED_SPRITE = ResourceLocation.withDefaultNamespace("container/crafter/disabled_slot");
   private static final ResourceLocation BANNER_SLOT_SPRITE = ResourceLocation.withDefaultNamespace("container/slot/level");
   private static final ResourceLocation SCROLLER_SPRITE = ResourceLocation.withDefaultNamespace("container/loom/scroller");
   private static final ResourceLocation SCROLLER_DISABLED_SPRITE = ResourceLocation.withDefaultNamespace("container/loom/scroller_disabled");
   private static final ResourceLocation DONATE_BG_LOCATION = ResourceLocation.withDefaultNamespace("textures/gui/container/mine_crafter_donate.png");
   private static final ResourceLocation BG_LOCATION = ResourceLocation.withDefaultNamespace("textures/gui/container/mine_crafter.png");
   private static final ResourceLocation BG_HINTS_LOCATION = ResourceLocation.withDefaultNamespace("textures/gui/container/mine_crafter_hints.png");
   private static final ResourceLocation BG_BOSS_LOCATION = ResourceLocation.withDefaultNamespace("textures/gui/container/mine_crafter_boss.png");
   private static final ResourceLocation BG_BOSS_ACTIVE_LOCATION = ResourceLocation.withDefaultNamespace("textures/gui/container/mine_crafter_boss_active.png");
   private static final ResourceLocation BG_ACTIVE_LOCATION = ResourceLocation.withDefaultNamespace("textures/gui/container/mine_crafter_active.png");
   private static final ResourceLocation BG_WON_LOCATION = ResourceLocation.withDefaultNamespace("textures/gui/container/mine_crafter_won.png");
   private static final ResourceLocation BG_FAIL_LOCATION = ResourceLocation.withDefaultNamespace("textures/gui/container/mine_crafter_fail.png");
   private static final ResourceLocation EXPERIENCE_BAR_BACKGROUND_SPRITE = ResourceLocation.withDefaultNamespace("hud/experience_bar_background");
   private static final ResourceLocation EXPERIENCE_BAR_PROGRESS_SPRITE = ResourceLocation.withDefaultNamespace("hud/experience_bar_progress");
   private static final ResourceLocation NO_HINTS_SPRITE = ResourceLocation.withDefaultNamespace("container/crafter/all_unlocked");
   private static final int SCROLLER_FULL_HEIGHT = 70;
   public static final int SCROLL_X_POS = 174;
   public static final int SCROLL_Y_POS = 124;
   private static final int SCROLLER_WIDTH = 12;
   private static final int SCROLLER_HEIGHT = 15;
   private final SpriteIconButton donateButton = SpriteIconButton.builder(Component.empty(), this::donateExperienceToMineCrafter, true).sprite(ResourceLocation.withDefaultNamespace("icon/donate_experience"), 24, 24).size(24, 24).build();
   private float scrollOffs;
   private boolean scrolling;
   private int startRow;
   private final float[] rot = new float[]{0.0F, 0.0F};
   private final float[] mapRot = new float[]{0.0F, 0.0F};
   private int ticks = 0;
   private final int speed = 240;

   public MineCraftingScreen(MineCraftingMenu var1, Inventory var2, Component var3) {
      super(var1, var2, var3);
      this.donateButton.setTooltip(Tooltip.create(Component.translatable("container.mine_crafter.donate", 20)));
   }

   private void donateExperienceToMineCrafter(Button var1) {
      this.minecraft.player.donateExperienceToMineCrafter();
   }

   protected void init() {
      this.imageWidth = 193;
      this.imageHeight = 205;
      super.init();
      this.addWidget(this.donateButton);
      this.startRow = 0;
      this.scrollTo(this.startRow);
   }

   protected void slotClicked(Slot var1, int var2, int var3, ClickType var4) {
      super.slotClicked(var1, var2, var3, var4);
   }

   protected void renderLabels(GuiGraphics var1, int var2, int var3) {
      if (((MineCraftingMenu)this.menu).isMineCompleted()) {
         if (((MineCraftingMenu)this.menu).wasMineSuccess()) {
            var1.drawString(this.font, (Component)Component.translatable("container.mine_crafter.won"), this.titleLabelX, this.titleLabelY, 4210752, false);
         } else {
            var1.drawString(this.font, (Component)Component.translatable("container.mine_crafter.fail"), this.titleLabelX, this.titleLabelY, 4210752, false);
         }
      }

      if (!((MineCraftingMenu)this.menu).isBossMine() || ((MineCraftingMenu)this.menu).isMineCompleted()) {
         if (((MineCraftingMenu)this.menu).isMineActive()) {
            var1.drawString(this.font, (Component)Component.translatable("container.mine_crafter.active"), this.titleLabelX, this.titleLabelY, 4210752, false);
         } else if (!((MineCraftingMenu)this.menu).isMineCompleted()) {
            var1.drawString(this.font, this.title, this.titleLabelX, this.titleLabelY, 4210752, false);
         }

         var1.drawString(this.font, (Component)Component.translatable("container.mine_crafter.drawer"), this.inventoryLabelX, this.inventoryLabelY + 39, 4210752, false);
         if (((MineCraftingMenu)this.menu).allEffectsAreUnlocked()) {
            MutableComponent var4 = Component.translatable("container.mine_crafter.no_hints");
            int var5 = this.getFont().width((FormattedText)var4);
            var1.drawString(this.font, (Component)var4, this.titleLabelX + 250 - var5 / 2, this.titleLabelY, 4210752, false);
         } else {
            MutableComponent var6 = Component.translatable("container.mine_crafter.hints");
            int var7 = this.getFont().width((FormattedText)var6);
            var1.drawString(this.font, (Component)var6, this.titleLabelX + 250 - var7 / 2, this.titleLabelY, 4210752, false);
         }
      }

   }

   public void render(GuiGraphics var1, int var2, int var3, float var4) {
      super.render(var1, var2, var3, var4);
      if (!((MineCraftingMenu)this.menu).isBossMine() || ((MineCraftingMenu)this.menu).isMineCompleted()) {
         this.renderExperienceBar(var1, var2, var3, var4);
         this.renderExperienceLevel(var1);
      }

      this.renderTooltip(var1, var2, var3);
   }

   private void renderExperienceBar(GuiGraphics var1, int var2, int var3, float var4) {
      boolean var5 = true;
      int var6 = this.leftPos + 4;
      int var7 = MineCrafterBlockEntity.experienceRequiredForLevel(((MineCraftingMenu)this.menu).getCurrentLevel());
      if (var7 > 0) {
         int var8 = (int)((float)((MineCraftingMenu)this.menu).getCurrentExp() / (float)var7 * 161.0F);
         int var9 = this.topPos - 16;
         var1.blitSprite(RenderType::guiTextured, (ResourceLocation)EXPERIENCE_BAR_BACKGROUND_SPRITE, var6, var9, 160, 11);
         if (var8 > 0) {
            var1.blitSprite(RenderType::guiTextured, EXPERIENCE_BAR_PROGRESS_SPRITE, 182, 11, 0, 0, var6, var9, var8, 11);
         }
      }

      this.donateButton.setPosition(this.leftPos + this.imageWidth - 28, this.topPos - 23);
      this.donateButton.render(var1, var2, var3, var4);
   }

   private void renderExperienceLevel(GuiGraphics var1) {
      int var2 = ((MineCraftingMenu)this.menu).getCurrentLevel() + 1;
      String var3 = Component.translatable("container.mine_crafter.level", var2, ((MineCraftingMenu)this.menu).getCurrentExp(), MineCrafterBlockEntity.experienceRequiredForLevel(((MineCraftingMenu)this.menu).getCurrentLevel())).getString();
      int var4 = this.leftPos + this.font.width(var3) / 2;
      int var5 = this.topPos - 15;
      var1.drawString(this.font, (String)var3, var4 + 1, var5, 0, false);
      var1.drawString(this.font, (String)var3, var4 - 1, var5, 0, false);
      var1.drawString(this.font, (String)var3, var4, var5 + 1, 0, false);
      var1.drawString(this.font, (String)var3, var4, var5 - 1, 0, false);
      var1.drawString(this.font, var3, var4, var5, 8453920, false);
   }

   protected void containerTick() {
      super.containerTick();
      this.ticks = (this.ticks + 1) % 240;
      float[] var10000 = this.mapRot;
      var10000[1] += 0.5F;
      var10000 = this.rot;
      var10000[1] += 0.3F;
      long var1 = ((MineCraftingMenu)this.menu).getCraftingSlots().stream().filter(Slot::hasItem).count();

      for(Slot var4 : (this.menu).slots) {
         if (var4.shouldMove()) {
            this.moveSlot(var4, var1);
         }
      }

      this.donateButton.active = this.minecraft.player.canDonateExperienceToMineCrafter();
      this.donateButton.setFocused(false);
   }

   private void moveSlot(Slot var1, long var2) {
      GhettoSpline var4 = ((MineCraftingMenu)this.menu).getSpline();
      double var5 = 0.5 / (double)var2 * (double)var1.getContainerSlot();
      double var7 = (double)this.ticks / 240.0 + var5;
      Vec2 var9 = var4.interpolate((float)(var7 + var5));
      var1.x0 = var9.x;
      var1.y0 = var9.y;
   }

   protected void renderSlot(GuiGraphics var1, Slot var2) {
      float var3 = var1.getDeltaTracker().getRealtimeDeltaTicks();
      if (var2.shouldMove()) {
         var2.x = (int)Mth.lerp(var3, (float)var2.x, var2.x0);
         var2.y = (int)Mth.lerp(var3, (float)var2.y, var2.y0);
      }

      if (var2.shouldRotate()) {
         if (var2 instanceof MineCraftingSlot) {
            var1.rotation = this.mapRot[0] = Mth.lerp(var3, this.mapRot[0], this.mapRot[1]);
         } else {
            var1.rotation = this.rot[0] = Mth.lerp(var3, this.rot[0], this.rot[1]);
         }
      }

      super.renderSlot(var1, var2);
      var1.rotation = 0.0F;
   }

   protected void renderBg(GuiGraphics var1, float var2, int var3, int var4) {
      int var5 = this.leftPos;
      int var6 = this.topPos;
      if (!((MineCraftingMenu)this.menu).isBossMine() || ((MineCraftingMenu)this.menu).isMineCompleted()) {
         var1.blit(RenderType::guiTextured, DONATE_BG_LOCATION, var5, var6 - 20, 0.0F, 0.0F, this.imageWidth, this.imageHeight, 256, 256);
      }

      if (((MineCraftingMenu)this.menu).isMineActive()) {
         var1.blit(RenderType::guiTextured, ((MineCraftingMenu)this.menu).isBossMine() ? BG_BOSS_ACTIVE_LOCATION : BG_ACTIVE_LOCATION, var5, var6, 0.0F, 0.0F, this.imageWidth, this.imageHeight, 256, 256);
      } else if (((MineCraftingMenu)this.menu).isMineCompleted()) {
         if (((MineCraftingMenu)this.menu).wasMineSuccess()) {
            var1.blit(RenderType::guiTextured, BG_WON_LOCATION, var5, var6, 0.0F, 0.0F, this.imageWidth, this.imageHeight, 256, 256);
         } else {
            var1.blit(RenderType::guiTextured, BG_FAIL_LOCATION, var5, var6, 0.0F, 0.0F, this.imageWidth, this.imageHeight, 256, 256);
         }
      } else {
         var1.blit(RenderType::guiTextured, ((MineCraftingMenu)this.menu).isBossMine() ? BG_BOSS_LOCATION : BG_LOCATION, var5, var6, 0.0F, 0.0F, this.imageWidth, this.imageHeight, 256, 256);
      }

      if (!((MineCraftingMenu)this.menu).isMineActive() && !((MineCraftingMenu)this.menu).isMineCompleted()) {
         for(MineCraftingSlot var8 : (this.menu).getCraftingSlots()) {
            if (var8.isActive()) {
               if (!var8.randomSlot && !((MineCraftingMenu)this.menu).isBossMine()) {
                  var1.blitSprite(RenderType::guiTextured, (ResourceLocation)CONTAINER_SLOT_SPRITE, var5 + var8.x - 1, var6 + var8.y - 1, 18, 18);
               } else {
                  var1.blitSprite(RenderType::guiTextured, (ResourceLocation)CONTAINER_SLOT_DISABLED_SPRITE, var5 + var8.x - 1, var6 + var8.y - 1, 18, 18);
               }
            }
         }

         for(MineCraftingDrawerSlot var12 : (this.menu).getDrawerSlots()) {
            if (var12.isActive() && (!var12.couldBeAddedToMine() || ((MineCraftingMenu)this.menu).isBossMine())) {
               var1.blitSprite(RenderType::guiTextured, (ResourceLocation)CONTAINER_SLOT_DISABLED_SPRITE, var5 + var12.x - 1, var6 + var12.y - 1, 18, 18);
            }
         }
      }

      MineCraftingResultSlot var11 = ((MineCraftingMenu)this.menu).getResultSlot();
      var1.blitSprite(RenderType::guiTextured, (ResourceLocation)BANNER_SLOT_SPRITE, var5 + var11.x, var6 + var11.y, 16, 16);
      if (!((MineCraftingMenu)this.menu).isBossMine() || ((MineCraftingMenu)this.menu).isMineCompleted()) {
         var1.blit(RenderType::guiTextured, BG_HINTS_LOCATION, var5 + 200, var6, 0.0F, 0.0F, this.imageWidth, this.imageHeight, 256, 256);
         if (((MineCraftingMenu)this.menu).allEffectsAreUnlocked()) {
            var1.blitSprite(RenderType::guiTextured, (ResourceLocation)NO_HINTS_SPRITE, var5 + 230, var6 + 70, 50, 50);
         }

         int var13 = (int)(55.0F * this.scrollOffs);
         ResourceLocation var9 = SCROLLER_DISABLED_SPRITE;
         var1.blitSprite(RenderType::guiTextured, (ResourceLocation)var9, var5 + 174, var6 + 124 + var13, 12, 15);
      }

      var1.flush();
      Lighting.setupForFlatItems();
      var1.flush();
      Lighting.setupFor3DItems();
   }

   public boolean mouseClicked(double var1, double var3, int var5) {
      this.scrolling = false;
      int var6 = this.leftPos + 174;
      int var7 = this.topPos + 124;
      if (var1 >= (double)var6 && var1 < (double)(var6 + 12) && var3 >= (double)var7 && var3 < (double)(var7 + 70)) {
         this.scrolling = this.canScroll();
         return true;
      } else {
         return super.mouseClicked(var1, var3, var5);
      }
   }

   private boolean canScroll() {
      return this.drawerItems() > 36;
   }

   private int drawerItems() {
      return (int)((MineCraftingMenu)this.menu).getDrawerSlots().stream().filter((var0) -> var0.hasItem()).count();
   }

   public boolean mouseDragged(double var1, double var3, int var5, double var6, double var8) {
      int var10 = Mth.ceil((float)this.drawerItems() / 9.0F) - 4;
      if (this.scrolling && var10 > 0) {
         int var11 = this.topPos + 124;
         int var12 = var11 + 70;
         this.scrollOffs = ((float)var3 - (float)var11 - 7.5F) / ((float)(var12 - var11) - 15.0F);
         this.scrollOffs = Mth.clamp(this.scrollOffs, 0.0F, 1.0F);
         this.startRow = Math.max((int)((double)(this.scrollOffs * (float)var10) + 0.5), 0);
         this.scrollTo(this.startRow);
         return true;
      } else {
         return super.mouseDragged(var1, var3, var5, var6, var8);
      }
   }

   public boolean mouseScrolled(double var1, double var3, double var5, double var7) {
      if (super.mouseScrolled(var1, var3, var5, var7)) {
         return true;
      } else if (!this.canScroll()) {
         return false;
      } else {
         int var9 = Mth.ceil((float)this.drawerItems() / 9.0F) - 4;
         if (var9 > 0) {
            float var10 = (float)var7 / (float)var9;
            this.scrollOffs = Mth.clamp(this.scrollOffs - var10, 0.0F, 1.0F);
            this.startRow = Math.max((int)(this.scrollOffs * (float)var9 + 0.5F), 0);
            this.scrollTo(this.startRow);
         }

         return true;
      }
   }

   protected boolean hasClickedOutside(double var1, double var3, int var5, int var6, int var7) {
      return var1 < (double)var5 || var3 < (double)var6 || var1 >= (double)(var5 + this.imageWidth) || var3 >= (double)(var6 + this.imageHeight);
   }

   private void scrollTo(int var1) {
      for(int var2 = 0; var2 < ((MineCraftingMenu)this.menu).getDrawerSlots().size(); ++var2) {
         MineCraftingDrawerSlot var3 = (MineCraftingDrawerSlot)((MineCraftingMenu)this.menu).getDrawerSlots().get(var2);
         int var4 = Mth.floor((float)var2 / 9.0F) - var1;
         if (var4 < 0) {
            var3.setActive(false);
         } else if (var4 < 4) {
            var3.setActive(true);
            var3.y = 124 + var4 * 18;
         } else {
            var3.setActive(false);
         }
      }

   }
}
