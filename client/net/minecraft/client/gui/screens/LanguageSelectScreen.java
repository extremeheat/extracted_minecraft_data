package net.minecraft.client.gui.screens;

import net.minecraft.ChatFormatting;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.Options;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.ObjectSelectionList;
import net.minecraft.client.gui.navigation.CommonInputs;
import net.minecraft.client.resources.language.LanguageInfo;
import net.minecraft.client.resources.language.LanguageManager;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;

public class LanguageSelectScreen extends OptionsSubScreen {
   private static final Component WARNING_LABEL = Component.translatable("options.languageAccuracyWarning").withStyle(ChatFormatting.GRAY);
   private LanguageSelectScreen.LanguageSelectionList packSelectionList;
   final LanguageManager languageManager;

   public LanguageSelectScreen(Screen var1, Options var2, LanguageManager var3) {
      super(var1, var2, Component.translatable("options.language.title"));
      this.languageManager = var3;
   }

   @Override
   protected void init() {
      this.packSelectionList = this.addRenderableWidget(new LanguageSelectScreen.LanguageSelectionList(this.minecraft));
      this.addRenderableWidget(this.options.forceUnicodeFont().createButton(this.options, this.width / 2 - 155, this.height - 38, 150));
      this.addRenderableWidget(
         Button.builder(CommonComponents.GUI_DONE, var1 -> this.onDone()).bounds(this.width / 2 - 155 + 160, this.height - 38, 150, 20).build()
      );
   }

   void onDone() {
      LanguageSelectScreen.LanguageSelectionList.Entry var1 = this.packSelectionList.getSelected();
      if (var1 != null && !var1.code.equals(this.languageManager.getSelected())) {
         this.languageManager.setSelected(var1.code);
         this.options.languageCode = var1.code;
         this.minecraft.reloadResourcePacks();
         this.options.save();
      }

      this.minecraft.setScreen(this.lastScreen);
   }

   @Override
   public boolean keyPressed(int var1, int var2, int var3) {
      if (CommonInputs.selected(var1)) {
         LanguageSelectScreen.LanguageSelectionList.Entry var4 = this.packSelectionList.getSelected();
         if (var4 != null) {
            var4.select();
            this.onDone();
            return true;
         }
      }

      return super.keyPressed(var1, var2, var3);
   }

   @Override
   public void render(GuiGraphics var1, int var2, int var3, float var4) {
      super.render(var1, var2, var3, var4);
      var1.drawCenteredString(this.font, this.title, this.width / 2, 16, 16777215);
      var1.drawCenteredString(this.font, WARNING_LABEL, this.width / 2, this.height - 56, -8355712);
   }

   @Override
   public void renderBackground(GuiGraphics var1, int var2, int var3, float var4) {
      this.renderDirtBackground(var1);
   }

   class LanguageSelectionList extends ObjectSelectionList<LanguageSelectScreen.LanguageSelectionList.Entry> {
      public LanguageSelectionList(Minecraft var2) {
         super(var2, LanguageSelectScreen.this.width, LanguageSelectScreen.this.height - 93, 32, 18);
         String var3 = LanguageSelectScreen.this.languageManager.getSelected();
         LanguageSelectScreen.this.languageManager.getLanguages().forEach((var2x, var3x) -> {
            LanguageSelectScreen.LanguageSelectionList.Entry var4 = new LanguageSelectScreen.LanguageSelectionList.Entry(var2x, var3x);
            this.addEntry(var4);
            if (var3.equals(var2x)) {
               this.setSelected(var4);
            }
         });
         if (this.getSelected() != null) {
            this.centerScrollOn(this.getSelected());
         }
      }

      @Override
      protected int getScrollbarPosition() {
         return super.getScrollbarPosition() + 20;
      }

      @Override
      public int getRowWidth() {
         return super.getRowWidth() + 50;
      }

      public class Entry extends ObjectSelectionList.Entry<LanguageSelectScreen.LanguageSelectionList.Entry> {
         final String code;
         private final Component language;
         private long lastClickTime;

         public Entry(String var2, LanguageInfo var3) {
            super();
            this.code = var2;
            this.language = var3.toComponent();
         }

         @Override
         public void render(GuiGraphics var1, int var2, int var3, int var4, int var5, int var6, int var7, int var8, boolean var9, float var10) {
            var1.drawCenteredString(LanguageSelectScreen.this.font, this.language, LanguageSelectionList.this.width / 2, var3 + 1, 16777215);
         }

         @Override
         public boolean mouseClicked(double var1, double var3, int var5) {
            this.select();
            if (Util.getMillis() - this.lastClickTime < 250L) {
               LanguageSelectScreen.this.onDone();
            }

            this.lastClickTime = Util.getMillis();
            return true;
         }

         void select() {
            LanguageSelectionList.this.setSelected(this);
         }

         @Override
         public Component getNarration() {
            return Component.translatable("narrator.select", this.language);
         }
      }
   }
}
