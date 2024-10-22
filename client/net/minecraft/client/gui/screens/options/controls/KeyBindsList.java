package net.minecraft.client.gui.screens.options.controls;

import com.google.common.collect.ImmutableList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.ChatFormatting;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ComponentPath;
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
import net.minecraft.network.chat.MutableComponent;
import org.apache.commons.lang3.ArrayUtils;

public class KeyBindsList extends ContainerObjectSelectionList<KeyBindsList.Entry> {
   private static final int ITEM_HEIGHT = 20;
   final KeyBindsScreen keyBindsScreen;
   private int maxNameWidth;

   public KeyBindsList(KeyBindsScreen var1, Minecraft var2) {
      super(var2, var1.width, var1.layout.getContentHeight(), var1.layout.getHeaderHeight(), 20);
      this.keyBindsScreen = var1;
      KeyMapping[] var3 = (KeyMapping[])ArrayUtils.clone(var2.options.keyMappings);
      Arrays.sort((Object[])var3);
      String var4 = null;

      for (KeyMapping var8 : var3) {
         String var9 = var8.getCategory();
         if (!var9.equals(var4)) {
            var4 = var9;
            this.addEntry(new KeyBindsList.CategoryEntry(Component.translatable(var9)));
         }

         MutableComponent var10 = Component.translatable(var8.getName());
         int var11 = var2.font.width(var10);
         if (var11 > this.maxNameWidth) {
            this.maxNameWidth = var11;
         }

         this.addEntry(new KeyBindsList.KeyEntry(var8, var10));
      }
   }

   public void resetMappingAndUpdateButtons() {
      KeyMapping.resetMapping();
      this.refreshEntries();
   }

   public void refreshEntries() {
      this.children().forEach(KeyBindsList.Entry::refreshEntry);
   }

   @Override
   public int getRowWidth() {
      return 340;
   }

   public class CategoryEntry extends KeyBindsList.Entry {
      final Component name;
      private final int width;

      public CategoryEntry(final Component nullx) {
         super();
         this.name = nullx;
         this.width = KeyBindsList.this.minecraft.font.width(this.name);
      }

      @Override
      public void render(GuiGraphics var1, int var2, int var3, int var4, int var5, int var6, int var7, int var8, boolean var9, float var10) {
         var1.drawString(KeyBindsList.this.minecraft.font, this.name, KeyBindsList.this.width / 2 - this.width / 2, var3 + var6 - 9 - 1, -1, false);
      }

      @Nullable
      @Override
      public ComponentPath nextFocusPath(FocusNavigationEvent var1) {
         return null;
      }

      @Override
      public List<? extends GuiEventListener> children() {
         return Collections.emptyList();
      }

      @Override
      public List<? extends NarratableEntry> narratables() {
         return ImmutableList.of(new NarratableEntry() {
            @Override
            public NarratableEntry.NarrationPriority narrationPriority() {
               return NarratableEntry.NarrationPriority.HOVERED;
            }

            @Override
            public void updateNarration(NarrationElementOutput var1) {
               var1.add(NarratedElementType.TITLE, CategoryEntry.this.name);
            }
         });
      }

      @Override
      protected void refreshEntry() {
      }
   }

   public abstract static class Entry extends ContainerObjectSelectionList.Entry<KeyBindsList.Entry> {
      public Entry() {
         super();
      }

      abstract void refreshEntry();
   }

   public class KeyEntry extends KeyBindsList.Entry {
      private static final Component RESET_BUTTON_TITLE = Component.translatable("controls.reset");
      private static final int PADDING = 10;
      private final KeyMapping key;
      private final Component name;
      private final Button changeButton;
      private final Button resetButton;
      private boolean hasCollision = false;

      KeyEntry(final KeyMapping nullx, final Component nullxx) {
         super();
         this.key = nullx;
         this.name = nullxx;
         this.changeButton = Button.builder(nullxx, var2 -> {
               KeyBindsList.this.keyBindsScreen.selectedKey = nullx;
               KeyBindsList.this.resetMappingAndUpdateButtons();
            })
            .bounds(0, 0, 75, 20)
            .createNarration(
               var2 -> nullx.isUnbound()
                     ? Component.translatable("narrator.controls.unbound", nullxx)
                     : Component.translatable("narrator.controls.bound", nullxx, var2.get())
            )
            .build();
         this.resetButton = Button.builder(RESET_BUTTON_TITLE, var2 -> {
            nullx.setKey(nullx.getDefaultKey());
            KeyBindsList.this.resetMappingAndUpdateButtons();
         }).bounds(0, 0, 50, 20).createNarration(var1 -> Component.translatable("narrator.controls.reset", nullxx)).build();
         this.refreshEntry();
      }

      @Override
      public void render(GuiGraphics var1, int var2, int var3, int var4, int var5, int var6, int var7, int var8, boolean var9, float var10) {
         int var11 = KeyBindsList.this.getScrollbarPosition() - this.resetButton.getWidth() - 10;
         int var12 = var3 - 2;
         this.resetButton.setPosition(var11, var12);
         this.resetButton.render(var1, var7, var8, var10);
         int var13 = var11 - 5 - this.changeButton.getWidth();
         this.changeButton.setPosition(var13, var12);
         this.changeButton.render(var1, var7, var8, var10);
         var1.drawString(KeyBindsList.this.minecraft.font, this.name, var4, var3 + var6 / 2 - 9 / 2, -1);
         if (this.hasCollision) {
            byte var14 = 3;
            int var15 = this.changeButton.getX() - 6;
            var1.fill(var15, var3 - 1, var15 + 3, var3 + var6, -65536);
         }
      }

      @Override
      public List<? extends GuiEventListener> children() {
         return ImmutableList.of(this.changeButton, this.resetButton);
      }

      @Override
      public List<? extends NarratableEntry> narratables() {
         return ImmutableList.of(this.changeButton, this.resetButton);
      }

      @Override
      protected void refreshEntry() {
         this.changeButton.setMessage(this.key.getTranslatedKeyMessage());
         this.resetButton.active = !this.key.isDefault();
         this.hasCollision = false;
         MutableComponent var1 = Component.empty();
         if (!this.key.isUnbound()) {
            for (KeyMapping var5 : KeyBindsList.this.minecraft.options.keyMappings) {
               if (var5 != this.key && this.key.same(var5)) {
                  if (this.hasCollision) {
                     var1.append(", ");
                  }

                  this.hasCollision = true;
                  var1.append(Component.translatable(var5.getName()));
               }
            }
         }

         if (this.hasCollision) {
            this.changeButton
               .setMessage(
                  Component.literal("[ ")
                     .append(this.changeButton.getMessage().copy().withStyle(ChatFormatting.WHITE))
                     .append(" ]")
                     .withStyle(ChatFormatting.RED)
               );
            this.changeButton.setTooltip(Tooltip.create(Component.translatable("controls.keybinds.duplicateKeybinds", var1)));
         } else {
            this.changeButton.setTooltip(null);
         }

         if (KeyBindsList.this.keyBindsScreen.selectedKey == this.key) {
            this.changeButton
               .setMessage(
                  Component.literal("> ")
                     .append(this.changeButton.getMessage().copy().withStyle(ChatFormatting.WHITE, ChatFormatting.UNDERLINE))
                     .append(" <")
                     .withStyle(ChatFormatting.YELLOW)
               );
         }
      }
   }
}
