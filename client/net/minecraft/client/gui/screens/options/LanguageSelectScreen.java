package net.minecraft.client.gui.screens.options;

import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.SortedMap;
import net.minecraft.client.Minecraft;
import net.minecraft.client.Options;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.ObjectSelectionList;
import net.minecraft.client.gui.components.StringWidget;
import net.minecraft.client.gui.layouts.HeaderAndFooterLayout;
import net.minecraft.client.gui.layouts.LinearLayout;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.resources.language.LanguageInfo;
import net.minecraft.client.resources.language.LanguageManager;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import org.jspecify.annotations.Nullable;

public class LanguageSelectScreen extends OptionsSubScreen {
   private static final Component WARNING_LABEL = Component.translatable("options.languageAccuracyWarning").withColor(-4539718);
   private static final int FOOTER_HEIGHT = 53;
   private static final Component SEARCH_HINT;
   private static final int SEARCH_BOX_HEIGHT = 15;
   private final LanguageManager languageManager;
   private @Nullable LanguageSelectionList languageSelectionList;
   private @Nullable EditBox search;

   public LanguageSelectScreen(final Screen lastScreen, final Options options, final LanguageManager languageManager) {
      super(lastScreen, options, Component.translatable("options.language.title"));
      this.languageManager = languageManager;
      this.layout.setFooterHeight(53);
   }

   protected void addTitle() {
      LinearLayout header = (LinearLayout)this.layout.addToHeader(LinearLayout.vertical().spacing(4));
      header.defaultCellSetting().alignHorizontallyCenter();
      header.addChild(new StringWidget(this.title, this.font));
      this.search = (EditBox)header.addChild(new EditBox(this.font, 0, 0, 200, 15, Component.empty()));
      this.search.setHint(SEARCH_HINT);
      this.search.setResponder((string) -> {
         if (this.languageSelectionList != null) {
            this.languageSelectionList.filterEntries(string);
         }

      });
      HeaderAndFooterLayout var10000 = this.layout;
      Objects.requireNonNull(this.font);
      var10000.setHeaderHeight((int)(12.0 + 9.0 + 15.0));
   }

   protected void setInitialFocus() {
      if (this.search != null) {
         this.setInitialFocus(this.search);
      } else {
         super.setInitialFocus();
      }

   }

   protected void addContents() {
      this.languageSelectionList = (LanguageSelectionList)this.layout.addToContents(new LanguageSelectionList(this.minecraft));
   }

   protected void addOptions() {
   }

   protected void addFooter() {
      LinearLayout footer = ((LinearLayout)this.layout.addToFooter(LinearLayout.vertical())).spacing(8);
      footer.defaultCellSetting().alignHorizontallyCenter();
      footer.addChild(new StringWidget(WARNING_LABEL, this.font));
      LinearLayout bottomButtons = (LinearLayout)footer.addChild(LinearLayout.horizontal().spacing(8));
      bottomButtons.addChild(Button.builder(Component.translatable("options.font"), (var1) -> this.minecraft.gui.setScreen(new FontOptionsScreen(this, this.options))).build());
      bottomButtons.addChild(Button.builder(CommonComponents.GUI_DONE, (button) -> this.onDone()).build());
   }

   protected void repositionElements() {
      super.repositionElements();
      if (this.languageSelectionList != null) {
         this.languageSelectionList.updateSize(this.width, this.layout);
      }

   }

   private void onDone() {
      if (this.languageSelectionList != null && this.languageSelectionList.getSelected() != null) {
         LanguageSelectionList.Entry selectedEntry = (LanguageSelectionList.Entry)this.languageSelectionList.getSelected();
         if (!selectedEntry.code.equals(this.languageManager.getSelected())) {
            this.languageManager.setSelected(selectedEntry.code);
            this.options.languageCode = selectedEntry.code;
            this.minecraft.reloadResourcePacks();
         }
      }

      this.minecraft.gui.setScreen(this.lastScreen);
   }

   static {
      SEARCH_HINT = Component.translatable("gui.language.search").withStyle(EditBox.SEARCH_HINT_STYLE);
   }

   private class LanguageSelectionList extends ObjectSelectionList<Entry> {
      public LanguageSelectionList(final Minecraft minecraft) {
         Objects.requireNonNull(LanguageSelectScreen.this);
         super(minecraft, LanguageSelectScreen.this.width, LanguageSelectScreen.this.height - 33 - 53, 33, 18);
         String selectedLanguage = LanguageSelectScreen.this.languageManager.getSelected();
         LanguageSelectScreen.this.languageManager.getLanguages().forEach((code, info) -> {
            Entry entry = new Entry(code, info);
            this.addEntry(entry);
            if (selectedLanguage.equals(code)) {
               this.setSelected(entry);
            }

         });
         if (this.getSelected() != null) {
            this.centerScrollOn((Entry)this.getSelected());
         }

      }

      private void filterEntries(final String filter) {
         SortedMap<String, LanguageInfo> languages = LanguageSelectScreen.this.languageManager.getLanguages();
         List<Entry> filteredEntries = languages.entrySet().stream().filter((entry) -> filter.isEmpty() || ((LanguageInfo)entry.getValue()).name().toLowerCase(Locale.ROOT).contains(filter.toLowerCase(Locale.ROOT)) || ((LanguageInfo)entry.getValue()).region().toLowerCase(Locale.ROOT).contains(filter.toLowerCase(Locale.ROOT))).map((entry) -> new Entry((String)entry.getKey(), (LanguageInfo)entry.getValue())).toList();
         this.replaceEntries(filteredEntries);
         this.refreshScrollAmount();
      }

      public int getRowWidth() {
         return super.getRowWidth() + 50;
      }

      public class Entry extends ObjectSelectionList.Entry<Entry> {
         private final String code;
         private final Component language;

         public Entry(final String code, final LanguageInfo language) {
            Objects.requireNonNull(LanguageSelectionList.this);
            super();
            this.code = code;
            this.language = language.toComponent();
         }

         public void extractContent(final GuiGraphicsExtractor graphics, final int mouseX, final int mouseY, final boolean hovered, final float a) {
            Font var10001 = LanguageSelectScreen.this.font;
            Component var10002 = this.language;
            int var10003 = LanguageSelectionList.this.width / 2;
            int var10004 = this.getContentYMiddle();
            Objects.requireNonNull(LanguageSelectScreen.this.font);
            graphics.centeredText(var10001, (Component)var10002, var10003, var10004 - 9 / 2, -1);
         }

         public boolean keyPressed(final KeyEvent event) {
            if (event.isSelection()) {
               this.select();
               LanguageSelectScreen.this.onDone();
               return true;
            } else {
               return super.keyPressed(event);
            }
         }

         public boolean mouseClicked(final MouseButtonEvent event, final boolean doubleClick) {
            this.select();
            if (doubleClick) {
               LanguageSelectScreen.this.onDone();
            }

            return super.mouseClicked(event, doubleClick);
         }

         private void select() {
            LanguageSelectionList.this.setSelected(this);
         }

         public Component getNarration() {
            return Component.translatable("narrator.select", this.language);
         }
      }
   }
}
