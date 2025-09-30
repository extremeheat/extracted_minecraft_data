package net.minecraft.client.gui.screens.options;

import java.util.Objects;
import net.minecraft.client.Minecraft;
import net.minecraft.client.Options;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.ObjectSelectionList;
import net.minecraft.client.gui.components.StringWidget;
import net.minecraft.client.gui.layouts.LinearLayout;
import net.minecraft.client.gui.screens.AccessibilityOnboardingScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.resources.language.LanguageInfo;
import net.minecraft.client.resources.language.LanguageManager;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;

public class LanguageSelectScreen extends OptionsSubScreen {
   private static final Component WARNING_LABEL = Component.translatable("options.languageAccuracyWarning").withColor(-4539718);
   private static final int FOOTER_HEIGHT = 53;
   private LanguageSelectionList languageSelectionList;
   final LanguageManager languageManager;

   public LanguageSelectScreen(Screen var1, Options var2, LanguageManager var3) {
      super(var1, var2, Component.translatable("options.language.title"));
      this.languageManager = var3;
      this.layout.setFooterHeight(53);
   }

   protected void addContents() {
      this.languageSelectionList = (LanguageSelectionList)this.layout.addToContents(new LanguageSelectionList(this.minecraft));
   }

   protected void addOptions() {
   }

   protected void addFooter() {
      LinearLayout var1 = ((LinearLayout)this.layout.addToFooter(LinearLayout.vertical())).spacing(8);
      var1.defaultCellSetting().alignHorizontallyCenter();
      var1.addChild(new StringWidget(WARNING_LABEL, this.font));
      LinearLayout var2 = (LinearLayout)var1.addChild(LinearLayout.horizontal().spacing(8));
      var2.addChild(Button.builder(Component.translatable("options.font"), (var1x) -> this.minecraft.setScreen(new FontOptionsScreen(this, this.options))).build());
      var2.addChild(Button.builder(CommonComponents.GUI_DONE, (var1x) -> this.onDone()).build());
   }

   protected void repositionElements() {
      super.repositionElements();
      this.languageSelectionList.updateSize(this.width, this.layout);
   }

   void onDone() {
      LanguageSelectionList.Entry var1 = (LanguageSelectionList.Entry)this.languageSelectionList.getSelected();
      if (var1 != null && !var1.code.equals(this.languageManager.getSelected())) {
         this.languageManager.setSelected(var1.code);
         this.options.languageCode = var1.code;
         this.minecraft.reloadResourcePacks();
      }

      this.minecraft.setScreen(this.lastScreen);
   }

   protected boolean panoramaShouldSpin() {
      return !(this.lastScreen instanceof AccessibilityOnboardingScreen);
   }

   class LanguageSelectionList extends ObjectSelectionList<Entry> {
      public LanguageSelectionList(final Minecraft var2) {
         super(var2, LanguageSelectScreen.this.width, LanguageSelectScreen.this.height - 33 - 53, 33, 18);
         String var3 = LanguageSelectScreen.this.languageManager.getSelected();
         LanguageSelectScreen.this.languageManager.getLanguages().forEach((var2x, var3x) -> {
            Entry var4 = new Entry(var2x, var3x);
            this.addEntry(var4);
            if (var3.equals(var2x)) {
               this.setSelected(var4);
            }

         });
         if (this.getSelected() != null) {
            this.centerScrollOn((Entry)this.getSelected());
         }

      }

      public int getRowWidth() {
         return super.getRowWidth() + 50;
      }

      public class Entry extends ObjectSelectionList.Entry<Entry> {
         final String code;
         private final Component language;

         public Entry(final String var2, final LanguageInfo var3) {
            super();
            this.code = var2;
            this.language = var3.toComponent();
         }

         public void renderContent(GuiGraphics var1, int var2, int var3, boolean var4, float var5) {
            Font var10001 = LanguageSelectScreen.this.font;
            Component var10002 = this.language;
            int var10003 = LanguageSelectionList.this.width / 2;
            int var10004 = this.getContentYMiddle();
            Objects.requireNonNull(LanguageSelectScreen.this.font);
            var1.drawCenteredString(var10001, (Component)var10002, var10003, var10004 - 9 / 2, -1);
         }

         public boolean keyPressed(KeyEvent var1) {
            if (var1.isSelection()) {
               this.select();
               LanguageSelectScreen.this.onDone();
               return true;
            } else {
               return super.keyPressed(var1);
            }
         }

         public boolean mouseClicked(MouseButtonEvent var1, boolean var2) {
            this.select();
            if (var2) {
               LanguageSelectScreen.this.onDone();
            }

            return super.mouseClicked(var1, var2);
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
