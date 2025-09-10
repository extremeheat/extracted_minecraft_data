package net.minecraft.client.gui.screens.options.controls;

import com.google.common.collect.ImmutableList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import net.minecraft.ChatFormatting;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.ContainerObjectSelectionList;
import net.minecraft.client.gui.components.FocusableTextWidget;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarratableEntry;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.network.chat.MutableComponent;
import org.apache.commons.lang3.ArrayUtils;

public class KeyBindsList extends ContainerObjectSelectionList<Entry> {
   private static final int ITEM_HEIGHT = 20;
   final KeyBindsScreen keyBindsScreen;
   private int maxNameWidth;

   public KeyBindsList(KeyBindsScreen var1, Minecraft var2) {
      super(var2, var1.width, var1.layout.getContentHeight(), var1.layout.getHeaderHeight(), 20);
      this.keyBindsScreen = var1;
      KeyMapping[] var3 = (KeyMapping[])ArrayUtils.clone(var2.options.keyMappings);
      Arrays.sort(var3);
      KeyMapping.Category var4 = null;

      for(KeyMapping var8 : var3) {
         KeyMapping.Category var9 = var8.getCategory();
         if (var9 != var4) {
            var4 = var9;
            this.addEntry(new CategoryEntry(var9));
         }

         MutableComponent var10 = Component.translatable(var8.getName());
         int var11 = var2.font.width((FormattedText)var10);
         if (var11 > this.maxNameWidth) {
            this.maxNameWidth = var11;
         }

         this.addEntry(new KeyEntry(var8, var10));
      }

   }

   public void resetMappingAndUpdateButtons() {
      KeyMapping.resetMapping();
      this.refreshEntries();
   }

   public void refreshEntries() {
      this.children().forEach(Entry::refreshEntry);
   }

   public int getRowWidth() {
      return 340;
   }

   public abstract static class Entry extends ContainerObjectSelectionList.Entry<Entry> {
      public Entry() {
         super();
      }

      abstract void refreshEntry();
   }

   public class CategoryEntry extends Entry {
      private final FocusableTextWidget categoryName;

      public CategoryEntry(final KeyMapping.Category var2) {
         super();
         this.categoryName = new FocusableTextWidget(KeyBindsList.this.getRowWidth(), var2.label(), KeyBindsList.this.minecraft.font, false, FocusableTextWidget.BackgroundFill.ON_FOCUS, 4);
      }

      public void renderContent(GuiGraphics var1, int var2, int var3, boolean var4, float var5) {
         FocusableTextWidget var10000 = this.categoryName;
         int var10001 = KeyBindsList.this.width / 2 - this.categoryName.getWidth() / 2;
         int var10002 = this.getContentBottom();
         Objects.requireNonNull(KeyBindsList.this.minecraft.font);
         var10000.setPosition(var10001, var10002 - 9 - 1);
         this.categoryName.render(var1, var2, var3, var5);
      }

      public List<? extends GuiEventListener> children() {
         return List.of(this.categoryName);
      }

      public List<? extends NarratableEntry> narratables() {
         return List.of(this.categoryName);
      }

      protected void refreshEntry() {
      }
   }

   public class KeyEntry extends Entry {
      private static final Component RESET_BUTTON_TITLE = Component.translatable("controls.reset");
      private static final int PADDING = 10;
      private final KeyMapping key;
      private final Component name;
      private final Button changeButton;
      private final Button resetButton;
      private boolean hasCollision = false;

      KeyEntry(final KeyMapping var2, final Component var3) {
         super();
         this.key = var2;
         this.name = var3;
         this.changeButton = Button.builder(var3, (var2x) -> {
            KeyBindsList.this.keyBindsScreen.selectedKey = var2;
            KeyBindsList.this.resetMappingAndUpdateButtons();
         }).bounds(0, 0, 75, 20).createNarration((var2x) -> var2.isUnbound() ? Component.translatable("narrator.controls.unbound", var3) : Component.translatable("narrator.controls.bound", var3, var2x.get())).build();
         this.resetButton = Button.builder(RESET_BUTTON_TITLE, (var2x) -> {
            var2.setKey(var2.getDefaultKey());
            KeyBindsList.this.resetMappingAndUpdateButtons();
         }).bounds(0, 0, 50, 20).createNarration((var1x) -> Component.translatable("narrator.controls.reset", var3)).build();
         this.refreshEntry();
      }

      public void renderContent(GuiGraphics var1, int var2, int var3, boolean var4, float var5) {
         int var6 = KeyBindsList.this.scrollBarX() - this.resetButton.getWidth() - 10;
         int var7 = this.getContentY() - 2;
         this.resetButton.setPosition(var6, var7);
         this.resetButton.render(var1, var2, var3, var5);
         int var8 = var6 - 5 - this.changeButton.getWidth();
         this.changeButton.setPosition(var8, var7);
         this.changeButton.render(var1, var2, var3, var5);
         Font var10001 = KeyBindsList.this.minecraft.font;
         Component var10002 = this.name;
         int var10003 = this.getContentX();
         int var10004 = this.getContentYMiddle();
         Objects.requireNonNull(KeyBindsList.this.minecraft.font);
         var1.drawString(var10001, (Component)var10002, var10003, var10004 - 9 / 2, -1);
         if (this.hasCollision) {
            boolean var9 = true;
            int var10 = this.changeButton.getX() - 6;
            var1.fill(var10, this.getContentY() - 1, var10 + 3, this.getContentBottom(), -256);
         }

      }

      public List<? extends GuiEventListener> children() {
         return ImmutableList.of(this.changeButton, this.resetButton);
      }

      public List<? extends NarratableEntry> narratables() {
         return ImmutableList.of(this.changeButton, this.resetButton);
      }

      protected void refreshEntry() {
         this.changeButton.setMessage(this.key.getTranslatedKeyMessage());
         this.resetButton.active = !this.key.isDefault();
         this.hasCollision = false;
         MutableComponent var1 = Component.empty();
         if (!this.key.isUnbound()) {
            for(KeyMapping var5 : KeyBindsList.this.minecraft.options.keyMappings) {
               if (var5 != this.key && this.key.same(var5) && (!var5.isDefault() || !this.key.isDefault())) {
                  if (this.hasCollision) {
                     var1.append(", ");
                  }

                  this.hasCollision = true;
                  var1.append((Component)Component.translatable(var5.getName()));
               }
            }
         }

         if (this.hasCollision) {
            this.changeButton.setMessage(Component.literal("[ ").append((Component)this.changeButton.getMessage().copy().withStyle(ChatFormatting.WHITE)).append(" ]").withStyle(ChatFormatting.YELLOW));
            this.changeButton.setTooltip(Tooltip.create(Component.translatable("controls.keybinds.duplicateKeybinds", var1)));
         } else {
            this.changeButton.setTooltip((Tooltip)null);
         }

         if (KeyBindsList.this.keyBindsScreen.selectedKey == this.key) {
            this.changeButton.setMessage(Component.literal("> ").append((Component)this.changeButton.getMessage().copy().withStyle(ChatFormatting.WHITE, ChatFormatting.UNDERLINE)).append(" <").withStyle(ChatFormatting.YELLOW));
         }

      }
   }
}
