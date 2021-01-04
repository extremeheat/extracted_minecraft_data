package net.minecraft.client.gui.screens;

import java.util.Iterator;
import javax.annotation.Nullable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.Option;
import net.minecraft.client.Options;
import net.minecraft.client.gui.chat.NarratorChatListener;
import net.minecraft.client.gui.components.AbstractSelectionList;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.ObjectSelectionList;
import net.minecraft.client.gui.components.OptionButton;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.client.resources.language.Language;
import net.minecraft.client.resources.language.LanguageManager;
import net.minecraft.network.chat.TranslatableComponent;

public class LanguageSelectScreen extends Screen {
   protected final Screen lastScreen;
   private LanguageSelectScreen.LanguageSelectionList packSelectionList;
   private final Options options;
   private final LanguageManager languageManager;
   private OptionButton forceUnicodeButton;
   private Button doneButton;

   public LanguageSelectScreen(Screen var1, Options var2, LanguageManager var3) {
      super(new TranslatableComponent("options.language", new Object[0]));
      this.lastScreen = var1;
      this.options = var2;
      this.languageManager = var3;
   }

   protected void init() {
      this.packSelectionList = new LanguageSelectScreen.LanguageSelectionList(this.minecraft);
      this.children.add(this.packSelectionList);
      this.forceUnicodeButton = (OptionButton)this.addButton(new OptionButton(this.width / 2 - 155, this.height - 38, 150, 20, Option.FORCE_UNICODE_FONT, Option.FORCE_UNICODE_FONT.getMessage(this.options), (var1) -> {
         Option.FORCE_UNICODE_FONT.toggle(this.options);
         this.options.save();
         var1.setMessage(Option.FORCE_UNICODE_FONT.getMessage(this.options));
         this.minecraft.resizeDisplay();
      }));
      this.doneButton = (Button)this.addButton(new Button(this.width / 2 - 155 + 160, this.height - 38, 150, 20, I18n.get("gui.done"), (var1) -> {
         LanguageSelectScreen.LanguageSelectionList.Entry var2 = (LanguageSelectScreen.LanguageSelectionList.Entry)this.packSelectionList.getSelected();
         if (var2 != null && !var2.language.getCode().equals(this.languageManager.getSelected().getCode())) {
            this.languageManager.setSelected(var2.language);
            this.options.languageCode = var2.language.getCode();
            this.minecraft.reloadResourcePacks();
            this.font.setBidirectional(this.languageManager.isBidirectional());
            this.doneButton.setMessage(I18n.get("gui.done"));
            this.forceUnicodeButton.setMessage(Option.FORCE_UNICODE_FONT.getMessage(this.options));
            this.options.save();
         }

         this.minecraft.setScreen(this.lastScreen);
      }));
      super.init();
   }

   public void render(int var1, int var2, float var3) {
      this.packSelectionList.render(var1, var2, var3);
      this.drawCenteredString(this.font, this.title.getColoredString(), this.width / 2, 16, 16777215);
      this.drawCenteredString(this.font, "(" + I18n.get("options.languageWarning") + ")", this.width / 2, this.height - 56, 8421504);
      super.render(var1, var2, var3);
   }

   class LanguageSelectionList extends ObjectSelectionList<LanguageSelectScreen.LanguageSelectionList.Entry> {
      public LanguageSelectionList(Minecraft var2) {
         super(var2, LanguageSelectScreen.this.width, LanguageSelectScreen.this.height, 32, LanguageSelectScreen.this.height - 65 + 4, 18);
         Iterator var3 = LanguageSelectScreen.this.languageManager.getLanguages().iterator();

         while(var3.hasNext()) {
            Language var4 = (Language)var3.next();
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

      protected int getScrollbarPosition() {
         return super.getScrollbarPosition() + 20;
      }

      public int getRowWidth() {
         return super.getRowWidth() + 50;
      }

      public void setSelected(@Nullable LanguageSelectScreen.LanguageSelectionList.Entry var1) {
         super.setSelected(var1);
         if (var1 != null) {
            NarratorChatListener.INSTANCE.sayNow((new TranslatableComponent("narrator.select", new Object[]{var1.language})).getString());
         }

      }

      protected void renderBackground() {
         LanguageSelectScreen.this.renderBackground();
      }

      protected boolean isFocused() {
         return LanguageSelectScreen.this.getFocused() == this;
      }

      // $FF: synthetic method
      public void setSelected(@Nullable AbstractSelectionList.Entry var1) {
         this.setSelected((LanguageSelectScreen.LanguageSelectionList.Entry)var1);
      }

      public class Entry extends ObjectSelectionList.Entry<LanguageSelectScreen.LanguageSelectionList.Entry> {
         private final Language language;

         public Entry(Language var2) {
            super();
            this.language = var2;
         }

         public void render(int var1, int var2, int var3, int var4, int var5, int var6, int var7, boolean var8, float var9) {
            LanguageSelectScreen.this.font.setBidirectional(true);
            LanguageSelectionList.this.drawCenteredString(LanguageSelectScreen.this.font, this.language.toString(), LanguageSelectionList.this.width / 2, var2 + 1, 16777215);
            LanguageSelectScreen.this.font.setBidirectional(LanguageSelectScreen.this.languageManager.getSelected().isBidirectional());
         }

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
      }
   }
}
