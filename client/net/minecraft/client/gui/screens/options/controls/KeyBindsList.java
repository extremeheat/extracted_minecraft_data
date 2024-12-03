package net.minecraft.client.gui.screens.options.controls;

import com.google.common.collect.ImmutableList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import javax.annotation.Nullable;
import net.minecraft.ChatFormatting;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ComponentPath;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.ContainerObjectSelectionList;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarratableEntry;
import net.minecraft.client.gui.narration.NarratedElementType;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.navigation.FocusNavigationEvent;
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
      String var4 = null;

      for(KeyMapping var8 : var3) {
         String var9 = var8.getCategory();
         if (!var9.equals(var4)) {
            var4 = var9;
            this.addEntry(new CategoryEntry(Component.translatable(var9)));
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
      final Component name;
      private final int width;

      public CategoryEntry(final Component var2) {
         super();
         this.name = var2;
         this.width = KeyBindsList.this.minecraft.font.width((FormattedText)this.name);
      }

      public void render(GuiGraphics var1, int var2, int var3, int var4, int var5, int var6, int var7, int var8, boolean var9, float var10) {
         Font var10001 = KeyBindsList.this.minecraft.font;
         Component var10002 = this.name;
         int var10003 = KeyBindsList.this.width / 2 - this.width / 2;
         int var10004 = var3 + var6;
         Objects.requireNonNull(KeyBindsList.this.minecraft.font);
         var1.drawString(var10001, (Component)var10002, var10003, var10004 - 9 - 1, -1);
      }

      @Nullable
      public ComponentPath nextFocusPath(FocusNavigationEvent var1) {
         return null;
      }

      public List<? extends GuiEventListener> children() {
         return Collections.emptyList();
      }

      public List<? extends NarratableEntry> narratables() {
         return ImmutableList.of(new NarratableEntry() {
            public NarratableEntry.NarrationPriority narrationPriority() {
               return NarratableEntry.NarrationPriority.HOVERED;
            }

            public void updateNarration(NarrationElementOutput var1) {
               var1.add(NarratedElementType.TITLE, CategoryEntry.this.name);
            }
         });
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

      public void render(GuiGraphics var1, int var2, int var3, int var4, int var5, int var6, int var7, int var8, boolean var9, float var10) {
         int var11 = KeyBindsList.this.scrollBarX() - this.resetButton.getWidth() - 10;
         int var12 = var3 - 2;
         this.resetButton.setPosition(var11, var12);
         this.resetButton.render(var1, var7, var8, var10);
         int var13 = var11 - 5 - this.changeButton.getWidth();
         this.changeButton.setPosition(var13, var12);
         this.changeButton.render(var1, var7, var8, var10);
         Font var10001 = KeyBindsList.this.minecraft.font;
         Component var10002 = this.name;
         int var10004 = var3 + var6 / 2;
         Objects.requireNonNull(KeyBindsList.this.minecraft.font);
         var1.drawString(var10001, (Component)var10002, var4, var10004 - 9 / 2, -1);
         if (this.hasCollision) {
            boolean var14 = true;
            int var15 = this.changeButton.getX() - 6;
            var1.fill(var15, var3 - 1, var15 + 3, var3 + var6, -65536);
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
               if (var5 != this.key && this.key.same(var5)) {
                  if (this.hasCollision) {
                     var1.append(", ");
                  }

                  this.hasCollision = true;
                  var1.append((Component)Component.translatable(var5.getName()));
               }
            }
         }

         if (this.hasCollision) {
            this.changeButton.setMessage(Component.literal("[ ").append((Component)this.changeButton.getMessage().copy().withStyle(ChatFormatting.WHITE)).append(" ]").withStyle(ChatFormatting.RED));
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
