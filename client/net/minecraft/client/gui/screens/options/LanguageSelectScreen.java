package net.minecraft.client.gui.screens.options;

import net.minecraft.ChatFormatting;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.Options;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.ObjectSelectionList;
import net.minecraft.client.gui.components.StringWidget;
import net.minecraft.client.gui.layouts.LinearLayout;
import net.minecraft.client.gui.navigation.CommonInputs;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.resources.language.LanguageInfo;
import net.minecraft.client.resources.language.LanguageManager;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;

public class LanguageSelectScreen extends OptionsSubScreen {
   private static final Component WARNING_LABEL = Component.translatable("options.languageAccuracyWarning").withStyle(ChatFormatting.GRAY);
   private static final int FOOTER_HEIGHT = 53;
   private LanguageSelectScreen.LanguageSelectionList languageSelectionList;
   final LanguageManager languageManager;

   public LanguageSelectScreen(Screen var1, Options var2, LanguageManager var3) {
      super(var1, var2, Component.translatable("options.language.title"));
      this.languageManager = var3;
      this.layout.setFooterHeight(53);
   }

   @Override
   protected void addContents() {
      this.languageSelectionList = this.layout.addToContents(new LanguageSelectScreen.LanguageSelectionList(this.minecraft));
   }

   @Override
   protected void addOptions() {
   }

   @Override
   protected void addFooter() {
      LinearLayout var1 = this.layout.addToFooter(LinearLayout.vertical()).spacing(8);
      var1.defaultCellSetting().alignHorizontallyCenter();
      var1.addChild(new StringWidget(WARNING_LABEL, this.font));
      LinearLayout var2 = var1.addChild(LinearLayout.horizontal().spacing(8));
      var2.addChild(
         Button.builder(Component.translatable("options.font"), var1x -> this.minecraft.setScreen(new FontOptionsScreen(this, this.options))).build()
      );
      var2.addChild(Button.builder(CommonComponents.GUI_DONE, var1x -> this.onDone()).build());
   }

   @Override
   protected void repositionElements() {
      super.repositionElements();
      this.languageSelectionList.updateSize(this.width, this.layout);
   }

   void onDone() {
      LanguageSelectScreen.LanguageSelectionList.Entry var1 = this.languageSelectionList.getSelected();
      if (var1 != null && !var1.code.equals(this.languageManager.getSelected())) {
         this.languageManager.setSelected(var1.code);
         this.options.languageCode = var1.code;
         this.minecraft.reloadResourcePacks();
      }

      this.minecraft.setScreen(this.lastScreen);
   }

   class LanguageSelectionList extends ObjectSelectionList<LanguageSelectScreen.LanguageSelectionList.Entry> {
      public LanguageSelectionList(final Minecraft nullx) {
         super(nullx, LanguageSelectScreen.this.width, LanguageSelectScreen.this.height - 33 - 53, 33, 18);
         String var3 = LanguageSelectScreen.this.languageManager.getSelected();
         LanguageSelectScreen.this.languageManager.getLanguages().forEach((var2, var3x) -> {
            LanguageSelectScreen.LanguageSelectionList.Entry var4 = new LanguageSelectScreen.LanguageSelectionList.Entry(var2, var3x);
            this.addEntry(var4);
            if (var3.equals(var2)) {
               this.setSelected(var4);
            }
         });
         if (this.getSelected() != null) {
            this.centerScrollOn(this.getSelected());
         }
      }

      @Override
      public int getRowWidth() {
         return super.getRowWidth() + 50;
      }

      public class Entry extends ObjectSelectionList.Entry<LanguageSelectScreen.LanguageSelectionList.Entry> {
         final String code;
         private final Component language;
         private long lastClickTime;

         public Entry(final String nullx, final LanguageInfo nullxx) {
            super();
            this.code = nullx;
            this.language = nullxx.toComponent();
         }

         @Override
         public void render(GuiGraphics var1, int var2, int var3, int var4, int var5, int var6, int var7, int var8, boolean var9, float var10) {
            var1.drawCenteredString(LanguageSelectScreen.this.font, this.language, LanguageSelectionList.this.width / 2, var3 + 1, -1);
         }

         @Override
         public boolean keyPressed(int var1, int var2, int var3) {
            if (CommonInputs.selected(var1)) {
               this.select();
               LanguageSelectScreen.this.onDone();
               return true;
            } else {
               return super.keyPressed(var1, var2, var3);
            }
         }

         @Override
         public boolean mouseClicked(double var1, double var3, int var5) {
            this.select();
            if (Util.getMillis() - this.lastClickTime < 250L) {
               LanguageSelectScreen.this.onDone();
            }

            this.lastClickTime = Util.getMillis();
            return super.mouseClicked(var1, var3, var5);
         }

         private void select() {
            LanguageSelectionList.this.setSelected(this);
         }

         @Override
         public Component getNarration() {
            return Component.translatable("narrator.select", this.language);
         }
      }
   }
}
