package net.minecraft.client.gui.screens;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.Options;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.ObjectSelectionList;
import net.minecraft.client.resources.language.LanguageInfo;
import net.minecraft.client.resources.language.LanguageManager;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;

public class LanguageSelectScreen extends OptionsSubScreen {
   private static final Component WARNING_LABEL = Component.literal("(")
      .append(Component.translatable("options.languageWarning"))
      .append(")")
      .withStyle(ChatFormatting.GRAY);
   private LanguageSelectScreen.LanguageSelectionList packSelectionList;
   final LanguageManager languageManager;

   public LanguageSelectScreen(Screen var1, Options var2, LanguageManager var3) {
      super(var1, var2, Component.translatable("options.language"));
      this.languageManager = var3;
   }

   @Override
   protected void init() {
      this.packSelectionList = new LanguageSelectScreen.LanguageSelectionList(this.minecraft);
      this.addWidget(this.packSelectionList);
      this.addRenderableWidget(this.options.forceUnicodeFont().createButton(this.options, this.width / 2 - 155, this.height - 38, 150));
      this.addRenderableWidget(new Button(this.width / 2 - 155 + 160, this.height - 38, 150, 20, CommonComponents.GUI_DONE, var1 -> {
         LanguageSelectScreen.LanguageSelectionList.Entry var2 = this.packSelectionList.getSelected();
         if (var2 != null && !var2.language.getCode().equals(this.languageManager.getSelected().getCode())) {
            this.languageManager.setSelected(var2.language);
            this.options.languageCode = var2.language.getCode();
            this.minecraft.reloadResourcePacks();
            this.options.save();
         }

         this.minecraft.setScreen(this.lastScreen);
      }));
      super.init();
   }

   @Override
   public void render(PoseStack var1, int var2, int var3, float var4) {
      this.packSelectionList.render(var1, var2, var3, var4);
      drawCenteredString(var1, this.font, this.title, this.width / 2, 16, 16777215);
      drawCenteredString(var1, this.font, WARNING_LABEL, this.width / 2, this.height - 56, 8421504);
      super.render(var1, var2, var3, var4);
   }

   class LanguageSelectionList extends ObjectSelectionList<LanguageSelectScreen.LanguageSelectionList.Entry> {
      public LanguageSelectionList(Minecraft var2) {
         super(var2, LanguageSelectScreen.this.width, LanguageSelectScreen.this.height, 32, LanguageSelectScreen.this.height - 65 + 4, 18);

         for(LanguageInfo var4 : LanguageSelectScreen.this.languageManager.getLanguages()) {
            LanguageSelectScreen.LanguageSelectionList.Entry var5 = new LanguageSelectScreen.LanguageSelectionList.Entry(var4);
            this.addEntry(var5);
            if (LanguageSelectScreen.this.languageManager.getSelected().getCode().equals(var4.getCode())) {
               this.setSelected(var5);
            }
         }

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

      @Override
      protected void renderBackground(PoseStack var1) {
         LanguageSelectScreen.this.renderBackground(var1);
      }

      @Override
      protected boolean isFocused() {
         return LanguageSelectScreen.this.getFocused() == this;
      }

      public class Entry extends ObjectSelectionList.Entry<LanguageSelectScreen.LanguageSelectionList.Entry> {
         final LanguageInfo language;

         public Entry(LanguageInfo var2) {
            super();
            this.language = var2;
         }

         @Override
         public void render(PoseStack var1, int var2, int var3, int var4, int var5, int var6, int var7, int var8, boolean var9, float var10) {
            String var11 = this.language.toString();
            LanguageSelectScreen.this.font
               .drawShadow(
                  var1,
                  var11,
                  (float)(LanguageSelectionList.this.width / 2 - LanguageSelectScreen.this.font.width(var11) / 2),
                  (float)(var3 + 1),
                  16777215,
                  true
               );
         }

         @Override
         public boolean mouseClicked(double var1, double var3, int var5) {
            if (var5 == 0) {
               this.select();
               return true;
            } else {
               return false;
            }
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
