package net.minecraft.client.gui.screens.unlocks;

import com.google.common.collect.Lists;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.ChatFormatting;
import net.minecraft.advancements.AdvancementType;
import net.minecraft.advancements.DisplayInfo;
import net.minecraft.client.Minecraft;
import net.minecraft.client.StringSplitter;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.advancements.AdvancementWidgetType;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.Holder;
import net.minecraft.locale.Language;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentUtils;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.players.PlayerUnlock;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

public class PlayerUnlockWidget {
   private static final ResourceLocation TITLE_BOX_SPRITE = ResourceLocation.withDefaultNamespace("advancements/title_box");
   private static final int HEIGHT = 26;
   private static final int BOX_X = 0;
   private static final int BOX_WIDTH = 200;
   private static final int FRAME_WIDTH = 26;
   private static final int ICON_X = 8;
   private static final int ICON_Y = 5;
   private static final int ICON_WIDTH = 26;
   private static final int TITLE_PADDING_LEFT = 3;
   private static final int TITLE_PADDING_RIGHT = 5;
   private static final int TITLE_X = 32;
   private static final int TITLE_PADDING_TOP = 9;
   private static final int TITLE_PADDING_BOTTOM = 8;
   private static final int TITLE_MAX_WIDTH = 163;
   private static final int TITLE_MIN_WIDTH = 80;
   private static final int[] TEST_SPLIT_OFFSETS = new int[]{0, 10, -10, 25, -25};
   private static final List<FormattedCharSequence> MYSTERY_TITLE;
   private final PlayerUnlocksTab tab;
   private final Holder<PlayerUnlock> node;
   final DisplayInfo display;
   final Minecraft minecraft;
   private final int cost;
   @Nullable
   private PlayerUnlockWidget parent;
   private final List<PlayerUnlockWidget> children = Lists.newArrayList();
   private final boolean hasExclusiveKey;
   private boolean activeExclusive;
   private PlayerUnlock.UnlockVisibility visibility;
   private boolean unlocked;
   @Nullable
   private TooltipInfo tooltipInfo;

   public PlayerUnlockWidget(PlayerUnlocksTab var1, Minecraft var2, Holder<PlayerUnlock> var3, DisplayInfo var4, PlayerUnlock.UnlockVisibility var5, boolean var6, int var7, boolean var8, boolean var9) {
      super();
      this.tab = var1;
      this.node = var3;
      this.display = var4;
      this.minecraft = var2;
      this.hasExclusiveKey = var8;
      this.activeExclusive = var9;
      this.visibility = var5;
      this.unlocked = var6;
      this.cost = var7;
   }

   protected int getX() {
      return Mth.floor(this.display.getX() * 28.0F);
   }

   protected int getY() {
      return Mth.floor(this.display.getY() * 27.0F);
   }

   int getMaxStatusWidth() {
      boolean var1 = true;
      MutableComponent var2 = Component.translatable("unlocks.screen.unlocked");
      MutableComponent var3 = Component.translatable("unlocks.screen.locked");
      if (this.hasExclusiveKey) {
         MutableComponent var4 = Component.translatable("unlocks.screen.active");
         MutableComponent var5 = Component.translatable("unlocks.screen.inactive");
         return Math.max(Math.max(this.minecraft.font.width((FormattedText)var4), this.minecraft.font.width((FormattedText)var5)), Math.max(this.minecraft.font.width((FormattedText)var2), this.minecraft.font.width((FormattedText)var3))) + 8;
      } else {
         return Math.max(this.minecraft.font.width((FormattedText)var2), this.minecraft.font.width((FormattedText)var3)) + 8;
      }
   }

   private static float getMaxWidth(StringSplitter var0, List<FormattedText> var1) {
      Stream var10000 = var1.stream();
      Objects.requireNonNull(var0);
      return (float)var10000.mapToDouble(var0::stringWidth).max().orElse(0.0);
   }

   List<FormattedText> findOptimalLines(Component var1, int var2) {
      StringSplitter var3 = this.minecraft.font.getSplitter();
      List var4 = null;
      float var5 = 3.4028235E38F;

      for(int var9 : TEST_SPLIT_OFFSETS) {
         List var10 = var3.splitLines((FormattedText)var1, var2 - var9, Style.EMPTY);
         float var11 = Math.abs(getMaxWidth(var3, var10) - (float)var2);
         if (var11 <= 10.0F) {
            return var10;
         }

         if (var11 < var5) {
            var5 = var11;
            var4 = var10;
         }
      }

      return var4;
   }

   private PlayerUnlockWidget getFirstVisibleParent(Holder<PlayerUnlock> var1) {
      Optional var2;
      for(var2 = ((PlayerUnlock)var1.value()).parent(); var2.isPresent(); var2 = ((PlayerUnlock)((Holder)var2.get()).value()).parent()) {
         PlayerUnlockWidget var3 = this.tab.getWidget((Holder)var2.get());
         if (var3 != null && var3.isVisibleAtAll()) {
            break;
         }
      }

      return this.tab.getWidget((Holder)var2.orElse(var1));
   }

   public void drawConnectivity(GuiGraphics var1, int var2, int var3, boolean var4) {
      if (this.parent != null) {
         int var5 = var2 + this.parent.getX() + 13;
         int var6 = var2 + this.parent.getX() + 26 + 4;
         int var7 = var3 + this.parent.getY() + 13;
         int var8 = var2 + this.getX() + 13;
         int var9 = var3 + this.getY() + 13;
         int var10 = var4 ? -16777216 : -1;
         if (var4) {
            var1.hLine(var6, var5, var7 - 1, var10);
            var1.hLine(var6 + 1, var5, var7, var10);
            var1.hLine(var6, var5, var7 + 1, var10);
            var1.hLine(var8, var6 - 1, var9 - 1, var10);
            var1.hLine(var8, var6 - 1, var9, var10);
            var1.hLine(var8, var6 - 1, var9 + 1, var10);
            var1.vLine(var6 - 1, var9, var7, var10);
            var1.vLine(var6 + 1, var9, var7, var10);
         } else {
            var1.hLine(var6, var5, var7, var10);
            var1.hLine(var8, var6, var9, var10);
            var1.vLine(var6, var9, var7, var10);
         }
      }

      for(PlayerUnlockWidget var12 : this.children) {
         if (var12.isVisibleAtAll()) {
            var12.drawConnectivity(var1, var2, var3, var4);
         }
      }

   }

   public void draw(GuiGraphics var1, int var2, int var3) {
      if (!this.display.isHidden() || this.unlocked) {
         ResourceLocation var4 = this.getSpriteType().frameSprite(this.getFrameShape());
         var1.blitSprite(RenderType::guiTextured, (ResourceLocation)var4, var2 + this.getX() + 3, var3 + this.getY(), 26, 26);
         ItemStack var5 = this.getIconItem();
         if (var5 != null) {
            var1.renderFakeItem(var5, var2 + this.getX() + 8, var3 + this.getY() + 5);
         }
      }

      for(PlayerUnlockWidget var7 : this.children) {
         if (var7.isVisibleAtAll()) {
            var7.draw(var1, var2, var3);
         }
      }

   }

   private AdvancementType getFrameShape() {
      return this.visibility == PlayerUnlock.UnlockVisibility.MYSTERY ? AdvancementType.CHALLENGE : this.display.getType();
   }

   @Nullable
   private ItemStack getIconItem() {
      if (this.visibility == PlayerUnlock.UnlockVisibility.MYSTERY) {
         return Items.POISONOUS_POTATO.getDefaultInstance();
      } else {
         return this.visibility == PlayerUnlock.UnlockVisibility.VISIBLE ? this.display.getIcon() : null;
      }
   }

   private TooltipInfo tooltipInfo() {
      if (this.tooltipInfo == null) {
         this.tooltipInfo = new TooltipInfo();
      }

      return this.tooltipInfo;
   }

   public int getWidth() {
      return this.tooltipInfo().width;
   }

   public void unlock(boolean var1) {
      this.unlocked = var1;
   }

   public void visibility(PlayerUnlock.UnlockVisibility var1) {
      this.visibility = var1;
   }

   public void isActiveExclusive(boolean var1) {
      this.activeExclusive = var1;
   }

   public void addChild(PlayerUnlockWidget var1) {
      this.children.add(var1);
   }

   public void drawHover(GuiGraphics var1, int var2, int var3, float var4, int var5, int var6, int var7, int var8) {
      if (!this.isVisibleAtAll()) {
         var1.blitSprite(RenderType::guiTextured, (ResourceLocation)AdvancementWidgetType.UNOBTAINED.frameSprite(this.getFrameShape()), var2 + this.getX() + 3, var3 + this.getY(), 26, 26);
      } else {
         TooltipInfo var9 = this.tooltipInfo();
         Font var10 = this.minecraft.font;
         Objects.requireNonNull(var10);
         int var11 = 9 * var9.titleLines.size() + 9 + 8;
         int var12 = var3 + this.getY() + (26 - var11) / 2;
         int var13 = var12 + var11;
         int var10000 = this.getDescriptionText().size();
         Objects.requireNonNull(var10);
         int var14 = var10000 * 9;
         int var15 = 6 + var14;
         boolean var16 = var5 + var2 + this.getX() + var9.width + 26 >= this.tab.getScreen().width;
         MutableComponent var17 = Component.empty();
         if (this.visibility == PlayerUnlock.UnlockVisibility.VISIBLE) {
            if (this.unlocked) {
               if (this.hasExclusiveKey) {
                  var17 = this.activeExclusive ? Component.translatable("unlocks.screen.active") : Component.translatable("unlocks.screen.inactive");
               } else {
                  var17 = Component.translatable("unlocks.screen.unlocked");
               }
            } else {
               boolean var18 = this.minecraft.player.experienceLevel >= this.cost;
               ChatFormatting var19 = var18 ? ChatFormatting.GREEN : ChatFormatting.RED;
               var17 = Component.translatable("unlocks.screen.locked", Component.literal(String.valueOf(this.cost)).withStyle(var19));
            }
         }

         int var25 = var10.width((FormattedText)var17);
         boolean var26 = var13 + var15 >= var8;
         int var20;
         if (var16) {
            var20 = var2 + this.getX() - var9.width + 26 + 6;
         } else {
            var20 = var2 + this.getX();
         }

         int var21 = var11 + var15;
         if (!var9.description.isEmpty()) {
            if (var26) {
               var1.blitSprite(RenderType::guiTextured, TITLE_BOX_SPRITE, var20, var13 - var21, var9.width, var21);
            } else {
               var1.blitSprite(RenderType::guiTextured, TITLE_BOX_SPRITE, var20, var12, var9.width, var21);
            }
         }

         AdvancementWidgetType var22 = this.getSpriteType();
         var1.blitSprite(RenderType::guiTextured, var22.boxSprite(), var20, var12, var9.width, var11);
         var1.blitSprite(RenderType::guiTextured, (ResourceLocation)var22.frameSprite(this.display.getType()), var2 + this.getX() + 3, var3 + this.getY(), 26, 26);
         int var23 = var20 + 5;
         if (var16) {
            this.drawMultilineText(var1, this.getTitleText(), var23, var12 + 9, -1);
            var1.drawString(var10, (Component)var17, var2 + this.getX() - var25, var12 + 9, -1);
         } else {
            this.drawMultilineText(var1, this.getTitleText(), var2 + this.getX() + 32, var12 + 9, -1);
            var1.drawString(var10, (Component)var17, var2 + this.getX() + var9.width - var25 - 5, var12 + 9, -1);
         }

         if (var26) {
            this.drawMultilineText(var1, this.getDescriptionText(), var23, var12 - var14 + 1, -16711936);
         } else {
            this.drawMultilineText(var1, this.getDescriptionText(), var23, var13, -16711936);
         }

         ItemStack var24 = this.getIconItem();
         if (var24 != null) {
            var1.renderFakeItem(var24, var2 + this.getX() + 8, var3 + this.getY() + 5);
         }

      }
   }

   private AdvancementWidgetType getSpriteType() {
      if (this.unlocked) {
         return this.activeExclusive ? AdvancementWidgetType.OBTAINED_ACTIVE : AdvancementWidgetType.OBTAINED;
      } else {
         boolean var1 = this.parent == null || this.parent.unlocked;
         return var1 && this.visibility == PlayerUnlock.UnlockVisibility.VISIBLE ? AdvancementWidgetType.UNOBTAINED : AdvancementWidgetType.UNOBTAINED_LOCKED;
      }
   }

   private List<FormattedCharSequence> getTitleText() {
      return this.visibility == PlayerUnlock.UnlockVisibility.VISIBLE ? this.tooltipInfo().titleLines : MYSTERY_TITLE;
   }

   private List<FormattedCharSequence> getDescriptionText() {
      return this.visibility == PlayerUnlock.UnlockVisibility.VISIBLE ? this.tooltipInfo().description : this.tooltipInfo().hint;
   }

   private void drawMultilineText(GuiGraphics var1, List<FormattedCharSequence> var2, int var3, int var4, int var5) {
      Font var6 = this.minecraft.font;

      for(int var7 = 0; var7 < var2.size(); ++var7) {
         FormattedCharSequence var10002 = (FormattedCharSequence)var2.get(var7);
         Objects.requireNonNull(var6);
         var1.drawString(var6, var10002, var3, var4 + var7 * 9, var5);
      }

   }

   public boolean isMouseOver(int var1, int var2, int var3, int var4) {
      if (this.display.isHidden() && !this.unlocked) {
         return false;
      } else if (!this.isVisibleAtAll()) {
         return false;
      } else {
         int var5 = var1 + this.getX();
         int var6 = var5 + 26;
         int var7 = var2 + this.getY();
         int var8 = var7 + 26;
         return var3 >= var5 && var3 <= var6 && var4 >= var7 && var4 <= var8;
      }
   }

   private boolean isVisibleAtAll() {
      return this.minecraft.player.connection.getUnlocks().isVisibleAtAll(this.node);
   }

   public void attachToParent() {
      if (this.parent == null && ((PlayerUnlock)this.node.value()).parent().isPresent()) {
         this.parent = this.getFirstVisibleParent(this.node);
         if (this.parent != null) {
            this.parent.addChild(this);
         }
      }

   }

   public void onClicked() {
      if (!this.unlocked) {
         this.minecraft.player.buyUnlock(this.node);
      } else {
         this.minecraft.player.reactivateUnlock(this.node);
      }

   }

   static {
      MYSTERY_TITLE = List.of(FormattedCharSequence.forward("much mystery", Style.EMPTY.withObfuscated(true)));
   }

   class TooltipInfo {
      final List<FormattedCharSequence> titleLines;
      final int width;
      final List<FormattedCharSequence> description;
      final List<FormattedCharSequence> hint;

      public TooltipInfo() {
         super();
         this.titleLines = PlayerUnlockWidget.this.minecraft.font.split(PlayerUnlockWidget.this.display.getTitle(), 163);
         Stream var10000 = this.titleLines.stream();
         Font var10001 = PlayerUnlockWidget.this.minecraft.font;
         Objects.requireNonNull(var10001);
         int var2 = Math.max(var10000.mapToInt(var10001::width).max().orElse(0), 80);
         int var3 = PlayerUnlockWidget.this.getMaxStatusWidth();
         int var4 = 29 + var2 + var3;
         this.description = Language.getInstance().getVisualOrder(PlayerUnlockWidget.this.findOptimalLines(ComponentUtils.mergeStyles(PlayerUnlockWidget.this.display.getDescription().copy(), Style.EMPTY.withColor(PlayerUnlockWidget.this.display.getType().getChatColor())), var4));

         for(FormattedCharSequence var6 : this.description) {
            var4 = Math.max(var4, PlayerUnlockWidget.this.minecraft.font.width(var6));
         }

         MutableComponent var8 = Component.translatable("unlocks.screen.hint", ComponentUtils.mergeStyles(PlayerUnlockWidget.this.display.getHint().copy(), Style.EMPTY.withColor(ChatFormatting.GRAY)));
         this.hint = Language.getInstance().getVisualOrder(PlayerUnlockWidget.this.findOptimalLines(var8, var4));

         for(FormattedCharSequence var7 : this.hint) {
            var4 = Math.max(var4, PlayerUnlockWidget.this.minecraft.font.width(var7));
         }

         this.width = var4 + 3 + 5;
      }
   }
}
