package net.minecraft.client.gui.components.inventory;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.GuiPipeline;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.WidgetSprites;
import net.minecraft.client.gui.screens.inventory.EnchantmentNames;
import net.minecraft.client.sounds.SoundManager;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.resources.Identifier;
import net.minecraft.world.inventory.EnchantmentMenu;

public class EnchantmentOptionButton extends Button {
   private static final Identifier ENCHANTMENT_SLOT_DISABLED_SPRITE = Identifier.withDefaultNamespace("container/enchanting_table/enchantment_slot_disabled");
   private static final Identifier ENCHANTMENT_SLOT_HIGHLIGHTED_SPRITE = Identifier.withDefaultNamespace("container/enchanting_table/enchantment_slot_highlighted");
   private static final Identifier ENCHANTMENT_SLOT_SPRITE = Identifier.withDefaultNamespace("container/enchanting_table/enchantment_slot");
   private static final WidgetSprites SPRITES;
   private static final Identifier[] ENABLED_LEVEL_SPRITES;
   private static final Identifier[] DISABLED_LEVEL_SPRITES;
   private static final int WIDTH = 108;
   public static final int HEIGHT = 19;
   private static final int TEXT_X_OFFSET = 20;
   private static final int TEXT_Y_OFFSET = 2;
   private static final int TEXT_WIDTH = 86;
   private static final int COST_OFFSET = 7;
   private static final int ACTIVE_TEXT_COLOR = -9937334;
   private static final int NOT_ACTIVE_XP_TEXT_COLOR = -12550384;
   private static final int ACTIVE_HOVERED_TEXT_COLOR = -128;
   private static final int ACTIVE_XP_TEXT_COLOR = -8323296;
   private static final int NOT_ACTIVE_TEXT_COLOR = -13357275;
   private static final int LEVEL_IMAGE_SIZE = 16;
   private final EnchantmentMenu menu;
   private final int index;

   public EnchantmentOptionButton(final int x, final int y, final EnchantmentMenu menu, final int index, final Button.OnPress onPress) {
      super(x, y, 108, 19, CommonComponents.EMPTY, onPress, DEFAULT_NARRATION);
      this.menu = menu;
      this.index = index;
   }

   public void playDownSound(final SoundManager soundManager) {
   }

   protected void extractContents(final GuiGraphicsExtractor graphics) {
      graphics.blitSprite(GuiPipeline.TEXTURED, SPRITES.get(this.isActive(), this.isHighlighted()), this.getX(), this.getY(), this.width, this.height);
      int cost = this.menu.costs[this.index];
      if (cost != 0) {
         Minecraft minecraft = Minecraft.getInstance();
         Font font = minecraft.font;
         int leftPosText = this.getX() + 20;
         int topPosText = this.getY() + 2;
         String costText = "" + cost;
         int textWidth = 86 - font.width(costText);
         FormattedText message = EnchantmentNames.getInstance().getRandomName(font, textWidth);
         graphics.blitSprite(GuiPipeline.TEXTURED, (Identifier)(this.isActive() ? ENABLED_LEVEL_SPRITES[this.index] : DISABLED_LEVEL_SPRITES[this.index]), this.getX() + 1, this.getY(), 16, 16);
         graphics.textWithWordWrap(font, message, leftPosText, topPosText, textWidth, this.isActive() ? (this.isHighlighted() ? -128 : -9937334) : -13357275, false);
         graphics.text(font, costText, leftPosText + textWidth, topPosText + 7, this.isActive() ? -8323296 : -12550384);
      }
   }

   static {
      SPRITES = new WidgetSprites(ENCHANTMENT_SLOT_SPRITE, ENCHANTMENT_SLOT_DISABLED_SPRITE, ENCHANTMENT_SLOT_HIGHLIGHTED_SPRITE);
      ENABLED_LEVEL_SPRITES = new Identifier[]{Identifier.withDefaultNamespace("container/enchanting_table/level_1"), Identifier.withDefaultNamespace("container/enchanting_table/level_2"), Identifier.withDefaultNamespace("container/enchanting_table/level_3")};
      DISABLED_LEVEL_SPRITES = new Identifier[]{Identifier.withDefaultNamespace("container/enchanting_table/level_1_disabled"), Identifier.withDefaultNamespace("container/enchanting_table/level_2_disabled"), Identifier.withDefaultNamespace("container/enchanting_table/level_3_disabled")};
   }
}
