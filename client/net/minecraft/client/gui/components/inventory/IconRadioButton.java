package net.minecraft.client.gui.components.inventory;

import java.util.Objects;
import java.util.function.BooleanSupplier;
import java.util.function.Supplier;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.GuiPipeline;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.components.WidgetSprites;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.renderer.blockentity.BannerRenderer;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.sprite.SpriteId;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.client.sounds.SoundManager;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.item.ItemStack;

public final class IconRadioButton extends Button {
   private final WidgetSprites sprites;
   private final Icon icon;
   private final SoundEvent selectSound;
   private final Supplier<Tooltip> tooltip;
   private final BooleanSupplier selected;

   public IconRadioButton(final int width, final int height, final Component message, final WidgetSprites sprites, final Icon icon, final SoundEvent selectSound, final Button.OnPress onPress, final Button.CreateNarration createNarration, final Supplier<Tooltip> tooltip, final BooleanSupplier selected) {
      super(0, 0, width, height, message, onPress, createNarration);
      this.sprites = sprites;
      this.icon = icon;
      this.selectSound = selectSound;
      this.tooltip = tooltip;
      this.selected = selected;
   }

   protected void extractContents(final GuiGraphicsExtractor graphics) {
      graphics.blitSprite(GuiPipeline.TEXTURED, this.sprites.get(this.selected.getAsBoolean(), this.isHighlighted()), this.getX(), this.getY(), this.width, this.height);
      ItemIcon var10000 = this.icon;
      Objects.requireNonNull(var10000);
      Icon var2 = var10000;
      byte var3 = 0;
      //$FF: var3->value
      //0->net/minecraft/client/gui/components/inventory/IconRadioButton$ItemIcon
      //1->net/minecraft/client/gui/components/inventory/IconRadioButton$BannerPatternIcon
      switch (var2.typeSwitch<invokedynamic>(var2, var3)) {
         case 0:
            ItemIcon var4 = (ItemIcon)var2;
            var10000 = var4;

            try {
               var10 = var10000.stack();
            } catch (Throwable var7) {
               throw new MatchException(var7.toString(), var7);
            }

            ItemStack stack = var10;
            graphics.item(stack, this.getX(), this.getY() + 1);
            break;
         case 1:
            BannerPatternIcon bannerPatternIcon = (BannerPatternIcon)var2;
            bannerPatternIcon.extractContents(graphics, this.getX(), this.getY());
            break;
         default:
            throw new MatchException((String)null, (Throwable)null);
      }

   }

   protected void extractTooltipForNextRenderPass(final GuiGraphicsExtractor graphics) {
      if (this.isHovered() || this.isFocused() && Minecraft.getInstance().getLastInputType().isKeyboard()) {
         this.setTooltip((Tooltip)this.tooltip.get());
      }

      super.extractTooltipForNextRenderPass(graphics);
   }

   public void playDownSound(final SoundManager soundManager) {
      if (!this.selected.getAsBoolean()) {
         soundManager.play(SimpleSoundInstance.forUI(this.selectSound, 1.0F));
      }

   }

   protected MutableComponent createNarrationMessage() {
      MutableComponent narration = super.createNarrationMessage();
      return this.selected.getAsBoolean() ? Component.translatable("narrator.select", narration) : narration;
   }

   protected void updateTooltipNarration(final NarrationElementOutput output) {
   }

   public static record ItemIcon(ItemStack stack) implements Icon {
      public ItemIcon {
         super();
      }
   }

   public static record BannerPatternIcon(SpriteId sprite) implements Icon {
      private static final float TEXTURE_SIZE = 64.0F;
      private static final float PATTERN_WIDTH = 21.0F;
      private static final float PATTERN_HEIGHT = 40.0F;

      public BannerPatternIcon {
         super();
      }

      private void extractContents(final GuiGraphicsExtractor graphics, final int posX, final int posY) {
         TextureAtlasSprite bannerPatternSprite = graphics.getSprite(this.sprite);
         graphics.pose().pushMatrix();
         graphics.pose().translate((float)(posX + 4), (float)(posY + 2));
         float patternU0 = bannerPatternSprite.getU0();
         float patternU1 = patternU0 + (bannerPatternSprite.getU1() - bannerPatternSprite.getU0()) * 21.0F / 64.0F;
         float patternVSpan = bannerPatternSprite.getV1() - bannerPatternSprite.getV0();
         float patternV0 = bannerPatternSprite.getV0() + patternVSpan / 64.0F;
         float patternV1 = patternV0 + patternVSpan * 40.0F / 64.0F;
         int bannerWidth = 5;
         int bannerHeight = 10;
         graphics.fill(0, 0, 5, 10, (Integer)BannerRenderer.DYED_PATTERN_COLOR.gray());
         graphics.blit(bannerPatternSprite.atlasLocation(), 0, 0, 5, 10, patternU0, patternU1, patternV0, patternV1);
         graphics.pose().popMatrix();
      }
   }

   public sealed interface Icon permits IconRadioButton.ItemIcon, IconRadioButton.BannerPatternIcon {
   }
}
