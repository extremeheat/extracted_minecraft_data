package net.minecraft.client.gui.screens.inventory;

import com.google.common.collect.Lists;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.Message;
import com.mojang.brigadier.ParseResults;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.context.CommandContextBuilder;
import com.mojang.brigadier.context.SuggestionContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.Suggestion;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.tree.LiteralCommandNode;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.CompletableFuture;
import javax.annotation.Nullable;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.gui.chat.NarratorChatListener;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.ChatScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.Rect2i;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentUtils;
import net.minecraft.util.Mth;
import net.minecraft.world.level.BaseCommandBlock;
import net.minecraft.world.phys.Vec2;

public abstract class AbstractCommandBlockEditScreen extends Screen {
   protected EditBox commandEdit;
   protected EditBox previousEdit;
   protected Button doneButton;
   protected Button cancelButton;
   protected Button outputButton;
   protected boolean trackOutput;
   protected final List<String> commandUsage = Lists.newArrayList();
   protected int commandUsagePosition;
   protected int commandUsageWidth;
   protected ParseResults<SharedSuggestionProvider> currentParse;
   protected CompletableFuture<Suggestions> pendingSuggestions;
   protected AbstractCommandBlockEditScreen.SuggestionsList suggestions;
   private boolean keepSuggestions;

   public AbstractCommandBlockEditScreen() {
      super(NarratorChatListener.NO_TITLE);
   }

   public void tick() {
      this.commandEdit.tick();
   }

   abstract BaseCommandBlock getCommandBlock();

   abstract int getPreviousY();

   protected void init() {
      this.minecraft.keyboardHandler.setSendRepeatsToGui(true);
      this.doneButton = (Button)this.addButton(new Button(this.width / 2 - 4 - 150, this.height / 4 + 120 + 12, 150, 20, I18n.get("gui.done"), (var1) -> {
         this.onDone();
      }));
      this.cancelButton = (Button)this.addButton(new Button(this.width / 2 + 4, this.height / 4 + 120 + 12, 150, 20, I18n.get("gui.cancel"), (var1) -> {
         this.onClose();
      }));
      this.outputButton = (Button)this.addButton(new Button(this.width / 2 + 150 - 20, this.getPreviousY(), 20, 20, "O", (var1) -> {
         BaseCommandBlock var2 = this.getCommandBlock();
         var2.setTrackOutput(!var2.isTrackOutput());
         this.updateCommandOutput();
      }));
      this.commandEdit = new EditBox(this.font, this.width / 2 - 150, 50, 300, 20, I18n.get("advMode.command"));
      this.commandEdit.setMaxLength(32500);
      this.commandEdit.setFormatter(this::formatChat);
      this.commandEdit.setResponder(this::onEdited);
      this.children.add(this.commandEdit);
      this.previousEdit = new EditBox(this.font, this.width / 2 - 150, this.getPreviousY(), 276, 20, I18n.get("advMode.previousOutput"));
      this.previousEdit.setMaxLength(32500);
      this.previousEdit.setEditable(false);
      this.previousEdit.setValue("-");
      this.children.add(this.previousEdit);
      this.setInitialFocus(this.commandEdit);
      this.commandEdit.setFocus(true);
      this.updateCommandInfo();
   }

   public void resize(Minecraft var1, int var2, int var3) {
      String var4 = this.commandEdit.getValue();
      this.init(var1, var2, var3);
      this.setChatLine(var4);
      this.updateCommandInfo();
   }

   protected void updateCommandOutput() {
      if (this.getCommandBlock().isTrackOutput()) {
         this.outputButton.setMessage("O");
         this.previousEdit.setValue(this.getCommandBlock().getLastOutput().getString());
      } else {
         this.outputButton.setMessage("X");
         this.previousEdit.setValue("-");
      }

   }

   protected void onDone() {
      BaseCommandBlock var1 = this.getCommandBlock();
      this.populateAndSendPacket(var1);
      if (!var1.isTrackOutput()) {
         var1.setLastOutput((Component)null);
      }

      this.minecraft.setScreen((Screen)null);
   }

   public void removed() {
      this.minecraft.keyboardHandler.setSendRepeatsToGui(false);
   }

   protected abstract void populateAndSendPacket(BaseCommandBlock var1);

   public void onClose() {
      this.getCommandBlock().setTrackOutput(this.trackOutput);
      this.minecraft.setScreen((Screen)null);
   }

   private void onEdited(String var1) {
      this.updateCommandInfo();
   }

   public boolean keyPressed(int var1, int var2, int var3) {
      if (this.suggestions != null && this.suggestions.keyPressed(var1, var2, var3)) {
         return true;
      } else if (this.getFocused() == this.commandEdit && var1 == 258) {
         this.showSuggestions();
         return true;
      } else if (super.keyPressed(var1, var2, var3)) {
         return true;
      } else if (var1 != 257 && var1 != 335) {
         if (var1 == 258 && this.getFocused() == this.commandEdit) {
            this.showSuggestions();
         }

         return false;
      } else {
         this.onDone();
         return true;
      }
   }

   public boolean mouseScrolled(double var1, double var3, double var5) {
      return this.suggestions != null && this.suggestions.mouseScrolled(Mth.clamp(var5, -1.0D, 1.0D)) ? true : super.mouseScrolled(var1, var3, var5);
   }

   public boolean mouseClicked(double var1, double var3, int var5) {
      return this.suggestions != null && this.suggestions.mouseClicked((int)var1, (int)var3, var5) ? true : super.mouseClicked(var1, var3, var5);
   }

   protected void updateCommandInfo() {
      String var1 = this.commandEdit.getValue();
      if (this.currentParse != null && !this.currentParse.getReader().getString().equals(var1)) {
         this.currentParse = null;
      }

      if (!this.keepSuggestions) {
         this.commandEdit.setSuggestion((String)null);
         this.suggestions = null;
      }

      this.commandUsage.clear();
      CommandDispatcher var2 = this.minecraft.player.connection.getCommands();
      StringReader var3 = new StringReader(var1);
      if (var3.canRead() && var3.peek() == '/') {
         var3.skip();
      }

      int var4 = var3.getCursor();
      if (this.currentParse == null) {
         this.currentParse = var2.parse(var3, this.minecraft.player.connection.getSuggestionsProvider());
      }

      int var5 = this.commandEdit.getCursorPosition();
      if (var5 >= var4 && (this.suggestions == null || !this.keepSuggestions)) {
         this.pendingSuggestions = var2.getCompletionSuggestions(this.currentParse, var5);
         this.pendingSuggestions.thenRun(() -> {
            if (this.pendingSuggestions.isDone()) {
               this.updateUsageInfo();
            }
         });
      }

   }

   private void updateUsageInfo() {
      if (((Suggestions)this.pendingSuggestions.join()).isEmpty() && !this.currentParse.getExceptions().isEmpty() && this.commandEdit.getCursorPosition() == this.commandEdit.getValue().length()) {
         int var1 = 0;
         Iterator var2 = this.currentParse.getExceptions().entrySet().iterator();

         while(var2.hasNext()) {
            Entry var3 = (Entry)var2.next();
            CommandSyntaxException var4 = (CommandSyntaxException)var3.getValue();
            if (var4.getType() == CommandSyntaxException.BUILT_IN_EXCEPTIONS.literalIncorrect()) {
               ++var1;
            } else {
               this.commandUsage.add(var4.getMessage());
            }
         }

         if (var1 > 0) {
            this.commandUsage.add(CommandSyntaxException.BUILT_IN_EXCEPTIONS.dispatcherUnknownCommand().create().getMessage());
         }
      }

      this.commandUsagePosition = 0;
      this.commandUsageWidth = this.width;
      if (this.commandUsage.isEmpty()) {
         this.fillNodeUsage(ChatFormatting.GRAY);
      }

      this.suggestions = null;
      if (this.minecraft.options.autoSuggestions) {
         this.showSuggestions();
      }

   }

   private String formatChat(String var1, int var2) {
      return this.currentParse != null ? ChatScreen.formatText(this.currentParse, var1, var2) : var1;
   }

   private void fillNodeUsage(ChatFormatting var1) {
      CommandContextBuilder var2 = this.currentParse.getContext();
      SuggestionContext var3 = var2.findSuggestionContext(this.commandEdit.getCursorPosition());
      Map var4 = this.minecraft.player.connection.getCommands().getSmartUsage(var3.parent, this.minecraft.player.connection.getSuggestionsProvider());
      ArrayList var5 = Lists.newArrayList();
      int var6 = 0;
      Iterator var7 = var4.entrySet().iterator();

      while(var7.hasNext()) {
         Entry var8 = (Entry)var7.next();
         if (!(var8.getKey() instanceof LiteralCommandNode)) {
            var5.add(var1 + (String)var8.getValue());
            var6 = Math.max(var6, this.font.width((String)var8.getValue()));
         }
      }

      if (!var5.isEmpty()) {
         this.commandUsage.addAll(var5);
         this.commandUsagePosition = Mth.clamp(this.commandEdit.getScreenX(var3.startPos), 0, this.commandEdit.getScreenX(0) + this.commandEdit.getInnerWidth() - var6);
         this.commandUsageWidth = var6;
      }

   }

   public void render(int var1, int var2, float var3) {
      this.renderBackground();
      this.drawCenteredString(this.font, I18n.get("advMode.setCommand"), this.width / 2, 20, 16777215);
      this.drawString(this.font, I18n.get("advMode.command"), this.width / 2 - 150, 40, 10526880);
      this.commandEdit.render(var1, var2, var3);
      byte var4 = 75;
      int var7;
      if (!this.previousEdit.getValue().isEmpty()) {
         this.font.getClass();
         var7 = var4 + (5 * 9 + 1 + this.getPreviousY() - 135);
         this.drawString(this.font, I18n.get("advMode.previousOutput"), this.width / 2 - 150, var7 + 4, 10526880);
         this.previousEdit.render(var1, var2, var3);
      }

      super.render(var1, var2, var3);
      if (this.suggestions != null) {
         this.suggestions.render(var1, var2);
      } else {
         var7 = 0;

         for(Iterator var5 = this.commandUsage.iterator(); var5.hasNext(); ++var7) {
            String var6 = (String)var5.next();
            fill(this.commandUsagePosition - 1, 72 + 12 * var7, this.commandUsagePosition + this.commandUsageWidth + 1, 84 + 12 * var7, -2147483648);
            this.font.drawShadow(var6, (float)this.commandUsagePosition, (float)(74 + 12 * var7), -1);
         }
      }

   }

   public void showSuggestions() {
      if (this.pendingSuggestions != null && this.pendingSuggestions.isDone()) {
         Suggestions var1 = (Suggestions)this.pendingSuggestions.join();
         if (!var1.isEmpty()) {
            int var2 = 0;

            Suggestion var4;
            for(Iterator var3 = var1.getList().iterator(); var3.hasNext(); var2 = Math.max(var2, this.font.width(var4.getText()))) {
               var4 = (Suggestion)var3.next();
            }

            int var5 = Mth.clamp(this.commandEdit.getScreenX(var1.getRange().getStart()), 0, this.commandEdit.getScreenX(0) + this.commandEdit.getInnerWidth() - var2);
            this.suggestions = new AbstractCommandBlockEditScreen.SuggestionsList(var5, 72, var2, var1);
         }
      }

   }

   protected void setChatLine(String var1) {
      this.commandEdit.setValue(var1);
   }

   @Nullable
   private static String calculateSuggestionSuffix(String var0, String var1) {
      return var1.startsWith(var0) ? var1.substring(var0.length()) : null;
   }

   class SuggestionsList {
      private final Rect2i rect;
      private final Suggestions suggestions;
      private final String originalContents;
      private int offset;
      private int current;
      private Vec2 lastMouse;
      private boolean tabCycles;

      private SuggestionsList(int var2, int var3, int var4, Suggestions var5) {
         super();
         this.lastMouse = Vec2.ZERO;
         this.rect = new Rect2i(var2 - 1, var3, var4 + 1, Math.min(var5.getList().size(), 7) * 12);
         this.suggestions = var5;
         this.originalContents = AbstractCommandBlockEditScreen.this.commandEdit.getValue();
         this.select(0);
      }

      public void render(int var1, int var2) {
         int var3 = Math.min(this.suggestions.getList().size(), 7);
         int var4 = -2147483648;
         int var5 = -5592406;
         boolean var6 = this.offset > 0;
         boolean var7 = this.suggestions.getList().size() > this.offset + var3;
         boolean var8 = var6 || var7;
         boolean var9 = this.lastMouse.x != (float)var1 || this.lastMouse.y != (float)var2;
         if (var9) {
            this.lastMouse = new Vec2((float)var1, (float)var2);
         }

         if (var8) {
            GuiComponent.fill(this.rect.getX(), this.rect.getY() - 1, this.rect.getX() + this.rect.getWidth(), this.rect.getY(), -2147483648);
            GuiComponent.fill(this.rect.getX(), this.rect.getY() + this.rect.getHeight(), this.rect.getX() + this.rect.getWidth(), this.rect.getY() + this.rect.getHeight() + 1, -2147483648);
            int var10;
            if (var6) {
               for(var10 = 0; var10 < this.rect.getWidth(); ++var10) {
                  if (var10 % 2 == 0) {
                     GuiComponent.fill(this.rect.getX() + var10, this.rect.getY() - 1, this.rect.getX() + var10 + 1, this.rect.getY(), -1);
                  }
               }
            }

            if (var7) {
               for(var10 = 0; var10 < this.rect.getWidth(); ++var10) {
                  if (var10 % 2 == 0) {
                     GuiComponent.fill(this.rect.getX() + var10, this.rect.getY() + this.rect.getHeight(), this.rect.getX() + var10 + 1, this.rect.getY() + this.rect.getHeight() + 1, -1);
                  }
               }
            }
         }

         boolean var13 = false;

         for(int var11 = 0; var11 < var3; ++var11) {
            Suggestion var12 = (Suggestion)this.suggestions.getList().get(var11 + this.offset);
            GuiComponent.fill(this.rect.getX(), this.rect.getY() + 12 * var11, this.rect.getX() + this.rect.getWidth(), this.rect.getY() + 12 * var11 + 12, -2147483648);
            if (var1 > this.rect.getX() && var1 < this.rect.getX() + this.rect.getWidth() && var2 > this.rect.getY() + 12 * var11 && var2 < this.rect.getY() + 12 * var11 + 12) {
               if (var9) {
                  this.select(var11 + this.offset);
               }

               var13 = true;
            }

            AbstractCommandBlockEditScreen.this.font.drawShadow(var12.getText(), (float)(this.rect.getX() + 1), (float)(this.rect.getY() + 2 + 12 * var11), var11 + this.offset == this.current ? -256 : -5592406);
         }

         if (var13) {
            Message var14 = ((Suggestion)this.suggestions.getList().get(this.current)).getTooltip();
            if (var14 != null) {
               AbstractCommandBlockEditScreen.this.renderTooltip(ComponentUtils.fromMessage(var14).getColoredString(), var1, var2);
            }
         }

      }

      public boolean mouseClicked(int var1, int var2, int var3) {
         if (!this.rect.contains(var1, var2)) {
            return false;
         } else {
            int var4 = (var2 - this.rect.getY()) / 12 + this.offset;
            if (var4 >= 0 && var4 < this.suggestions.getList().size()) {
               this.select(var4);
               this.useSuggestion();
            }

            return true;
         }
      }

      public boolean mouseScrolled(double var1) {
         int var3 = (int)(AbstractCommandBlockEditScreen.this.minecraft.mouseHandler.xpos() * (double)AbstractCommandBlockEditScreen.this.minecraft.window.getGuiScaledWidth() / (double)AbstractCommandBlockEditScreen.this.minecraft.window.getScreenWidth());
         int var4 = (int)(AbstractCommandBlockEditScreen.this.minecraft.mouseHandler.ypos() * (double)AbstractCommandBlockEditScreen.this.minecraft.window.getGuiScaledHeight() / (double)AbstractCommandBlockEditScreen.this.minecraft.window.getScreenHeight());
         if (this.rect.contains(var3, var4)) {
            this.offset = Mth.clamp((int)((double)this.offset - var1), 0, Math.max(this.suggestions.getList().size() - 7, 0));
            return true;
         } else {
            return false;
         }
      }

      public boolean keyPressed(int var1, int var2, int var3) {
         if (var1 == 265) {
            this.cycle(-1);
            this.tabCycles = false;
            return true;
         } else if (var1 == 264) {
            this.cycle(1);
            this.tabCycles = false;
            return true;
         } else if (var1 == 258) {
            if (this.tabCycles) {
               this.cycle(Screen.hasShiftDown() ? -1 : 1);
            }

            this.useSuggestion();
            return true;
         } else if (var1 == 256) {
            this.hide();
            return true;
         } else {
            return false;
         }
      }

      public void cycle(int var1) {
         this.select(this.current + var1);
         int var2 = this.offset;
         int var3 = this.offset + 7 - 1;
         if (this.current < var2) {
            this.offset = Mth.clamp(this.current, 0, Math.max(this.suggestions.getList().size() - 7, 0));
         } else if (this.current > var3) {
            this.offset = Mth.clamp(this.current - 7, 0, Math.max(this.suggestions.getList().size() - 7, 0));
         }

      }

      public void select(int var1) {
         this.current = var1;
         if (this.current < 0) {
            this.current += this.suggestions.getList().size();
         }

         if (this.current >= this.suggestions.getList().size()) {
            this.current -= this.suggestions.getList().size();
         }

         Suggestion var2 = (Suggestion)this.suggestions.getList().get(this.current);
         AbstractCommandBlockEditScreen.this.commandEdit.setSuggestion(AbstractCommandBlockEditScreen.calculateSuggestionSuffix(AbstractCommandBlockEditScreen.this.commandEdit.getValue(), var2.apply(this.originalContents)));
      }

      public void useSuggestion() {
         Suggestion var1 = (Suggestion)this.suggestions.getList().get(this.current);
         AbstractCommandBlockEditScreen.this.keepSuggestions = true;
         AbstractCommandBlockEditScreen.this.setChatLine(var1.apply(this.originalContents));
         int var2 = var1.getRange().getStart() + var1.getText().length();
         AbstractCommandBlockEditScreen.this.commandEdit.setCursorPosition(var2);
         AbstractCommandBlockEditScreen.this.commandEdit.setHighlightPos(var2);
         this.select(this.current);
         AbstractCommandBlockEditScreen.this.keepSuggestions = false;
         this.tabCycles = true;
      }

      public void hide() {
         AbstractCommandBlockEditScreen.this.suggestions = null;
      }

      // $FF: synthetic method
      SuggestionsList(int var2, int var3, int var4, Suggestions var5, Object var6) {
         this(var2, var3, var4, var5);
      }
   }
}
