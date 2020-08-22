package net.minecraft.client.gui.components;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.Message;
import com.mojang.brigadier.ParseResults;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.context.CommandContextBuilder;
import com.mojang.brigadier.context.ParsedArgument;
import com.mojang.brigadier.context.SuggestionContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.Suggestion;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import com.mojang.brigadier.tree.LiteralCommandNode;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.CompletableFuture;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.annotation.Nullable;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.gui.chat.NarratorChatListener;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.Rect2i;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.commands.Commands;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.network.chat.ComponentUtils;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec2;

public class CommandSuggestions {
   private static final Pattern WHITESPACE_PATTERN = Pattern.compile("(\\s+)");
   private final Minecraft minecraft;
   private final Screen screen;
   private final EditBox input;
   private final Font font;
   private final boolean commandsOnly;
   private final boolean onlyShowIfCursorPastError;
   private final int lineStartOffset;
   private final int suggestionLineLimit;
   private final boolean anchorToBottom;
   private final int fillColor;
   private final List commandUsage = Lists.newArrayList();
   private int commandUsagePosition;
   private int commandUsageWidth;
   private ParseResults currentParse;
   private CompletableFuture pendingSuggestions;
   private CommandSuggestions.SuggestionsList suggestions;
   private boolean allowSuggestions;
   private boolean keepSuggestions;

   public CommandSuggestions(Minecraft var1, Screen var2, EditBox var3, Font var4, boolean var5, boolean var6, int var7, int var8, boolean var9, int var10) {
      this.minecraft = var1;
      this.screen = var2;
      this.input = var3;
      this.font = var4;
      this.commandsOnly = var5;
      this.onlyShowIfCursorPastError = var6;
      this.lineStartOffset = var7;
      this.suggestionLineLimit = var8;
      this.anchorToBottom = var9;
      this.fillColor = var10;
      var3.setFormatter(this::formatChat);
   }

   public void setAllowSuggestions(boolean var1) {
      this.allowSuggestions = var1;
      if (!var1) {
         this.suggestions = null;
      }

   }

   public boolean keyPressed(int var1, int var2, int var3) {
      if (this.suggestions != null && this.suggestions.keyPressed(var1, var2, var3)) {
         return true;
      } else if (this.screen.getFocused() == this.input && var1 == 258) {
         this.showSuggestions(true);
         return true;
      } else {
         return false;
      }
   }

   public boolean mouseScrolled(double var1) {
      return this.suggestions != null && this.suggestions.mouseScrolled(Mth.clamp(var1, -1.0D, 1.0D));
   }

   public boolean mouseClicked(double var1, double var3, int var5) {
      return this.suggestions != null && this.suggestions.mouseClicked((int)var1, (int)var3, var5);
   }

   public void showSuggestions(boolean var1) {
      if (this.pendingSuggestions != null && this.pendingSuggestions.isDone()) {
         Suggestions var2 = (Suggestions)this.pendingSuggestions.join();
         if (!var2.isEmpty()) {
            int var3 = 0;

            Suggestion var5;
            for(Iterator var4 = var2.getList().iterator(); var4.hasNext(); var3 = Math.max(var3, this.font.width(var5.getText()))) {
               var5 = (Suggestion)var4.next();
            }

            int var6 = Mth.clamp(this.input.getScreenX(var2.getRange().getStart()), 0, this.input.getScreenX(0) + this.input.getInnerWidth() - var3);
            int var7 = this.anchorToBottom ? this.screen.height - 12 : 72;
            this.suggestions = new CommandSuggestions.SuggestionsList(var6, var7, var3, var2, var1);
         }
      }

   }

   public void updateCommandInfo() {
      String var1 = this.input.getValue();
      if (this.currentParse != null && !this.currentParse.getReader().getString().equals(var1)) {
         this.currentParse = null;
      }

      if (!this.keepSuggestions) {
         this.input.setSuggestion((String)null);
         this.suggestions = null;
      }

      this.commandUsage.clear();
      StringReader var2 = new StringReader(var1);
      boolean var3 = var2.canRead() && var2.peek() == '/';
      if (var3) {
         var2.skip();
      }

      boolean var4 = this.commandsOnly || var3;
      int var5 = this.input.getCursorPosition();
      int var7;
      if (var4) {
         CommandDispatcher var6 = this.minecraft.player.connection.getCommands();
         if (this.currentParse == null) {
            this.currentParse = var6.parse(var2, this.minecraft.player.connection.getSuggestionsProvider());
         }

         var7 = this.onlyShowIfCursorPastError ? var2.getCursor() : 1;
         if (var5 >= var7 && (this.suggestions == null || !this.keepSuggestions)) {
            this.pendingSuggestions = var6.getCompletionSuggestions(this.currentParse, var5);
            this.pendingSuggestions.thenRun(() -> {
               if (this.pendingSuggestions.isDone()) {
                  this.updateUsageInfo();
               }
            });
         }
      } else {
         String var9 = var1.substring(0, var5);
         var7 = getLastWordIndex(var9);
         Collection var8 = this.minecraft.player.connection.getSuggestionsProvider().getOnlinePlayerNames();
         this.pendingSuggestions = SharedSuggestionProvider.suggest((Iterable)var8, new SuggestionsBuilder(var9, var7));
      }

   }

   private static int getLastWordIndex(String var0) {
      if (Strings.isNullOrEmpty(var0)) {
         return 0;
      } else {
         int var1 = 0;

         for(Matcher var2 = WHITESPACE_PATTERN.matcher(var0); var2.find(); var1 = var2.end()) {
         }

         return var1;
      }
   }

   public void updateUsageInfo() {
      if (this.input.getCursorPosition() == this.input.getValue().length()) {
         if (((Suggestions)this.pendingSuggestions.join()).isEmpty() && !this.currentParse.getExceptions().isEmpty()) {
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
         } else if (this.currentParse.getReader().canRead()) {
            this.commandUsage.add(Commands.getParseException(this.currentParse).getMessage());
         }
      }

      this.commandUsagePosition = 0;
      this.commandUsageWidth = this.screen.width;
      if (this.commandUsage.isEmpty()) {
         this.fillNodeUsage(ChatFormatting.GRAY);
      }

      this.suggestions = null;
      if (this.allowSuggestions && this.minecraft.options.autoSuggestions) {
         this.showSuggestions(false);
      }

   }

   private void fillNodeUsage(ChatFormatting var1) {
      CommandContextBuilder var2 = this.currentParse.getContext();
      SuggestionContext var3 = var2.findSuggestionContext(this.input.getCursorPosition());
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
         this.commandUsagePosition = Mth.clamp(this.input.getScreenX(var3.startPos), 0, this.input.getScreenX(0) + this.input.getInnerWidth() - var6);
         this.commandUsageWidth = var6;
      }

   }

   private String formatChat(String var1, int var2) {
      return this.currentParse != null ? formatText(this.currentParse, var1, var2) : var1;
   }

   @Nullable
   private static String calculateSuggestionSuffix(String var0, String var1) {
      return var1.startsWith(var0) ? var1.substring(var0.length()) : null;
   }

   public static String formatText(ParseResults var0, String var1, int var2) {
      ChatFormatting[] var3 = new ChatFormatting[]{ChatFormatting.AQUA, ChatFormatting.YELLOW, ChatFormatting.GREEN, ChatFormatting.LIGHT_PURPLE, ChatFormatting.GOLD};
      String var4 = ChatFormatting.GRAY.toString();
      StringBuilder var5 = new StringBuilder(var4);
      int var6 = 0;
      int var7 = -1;
      CommandContextBuilder var8 = var0.getContext().getLastChild();
      Iterator var9 = var8.getArguments().values().iterator();

      while(var9.hasNext()) {
         ParsedArgument var10 = (ParsedArgument)var9.next();
         ++var7;
         if (var7 >= var3.length) {
            var7 = 0;
         }

         int var11 = Math.max(var10.getRange().getStart() - var2, 0);
         if (var11 >= var1.length()) {
            break;
         }

         int var12 = Math.min(var10.getRange().getEnd() - var2, var1.length());
         if (var12 > 0) {
            var5.append(var1, var6, var11);
            var5.append(var3[var7]);
            var5.append(var1, var11, var12);
            var5.append(var4);
            var6 = var12;
         }
      }

      if (var0.getReader().canRead()) {
         int var13 = Math.max(var0.getReader().getCursor() - var2, 0);
         if (var13 < var1.length()) {
            int var14 = Math.min(var13 + var0.getReader().getRemainingLength(), var1.length());
            var5.append(var1, var6, var13);
            var5.append(ChatFormatting.RED);
            var5.append(var1, var13, var14);
            var6 = var14;
         }
      }

      var5.append(var1, var6, var1.length());
      return var5.toString();
   }

   public void render(int var1, int var2) {
      if (this.suggestions != null) {
         this.suggestions.render(var1, var2);
      } else {
         int var3 = 0;

         for(Iterator var4 = this.commandUsage.iterator(); var4.hasNext(); ++var3) {
            String var5 = (String)var4.next();
            int var6 = this.anchorToBottom ? this.screen.height - 14 - 13 - 12 * var3 : 72 + 12 * var3;
            GuiComponent.fill(this.commandUsagePosition - 1, var6, this.commandUsagePosition + this.commandUsageWidth + 1, var6 + 12, this.fillColor);
            this.font.drawShadow(var5, (float)this.commandUsagePosition, (float)(var6 + 2), -1);
         }
      }

   }

   public String getNarrationMessage() {
      return this.suggestions != null ? "\n" + this.suggestions.getNarrationMessage() : "";
   }

   public class SuggestionsList {
      private final Rect2i rect;
      private final Suggestions suggestions;
      private final String originalContents;
      private int offset;
      private int current;
      private Vec2 lastMouse;
      private boolean tabCycles;
      private int lastNarratedEntry;

      private SuggestionsList(int var2, int var3, int var4, Suggestions var5, boolean var6) {
         this.lastMouse = Vec2.ZERO;
         int var7 = var2 - 1;
         int var8 = CommandSuggestions.this.anchorToBottom ? var3 - 3 - Math.min(var5.getList().size(), CommandSuggestions.this.suggestionLineLimit) * 12 : var3;
         this.rect = new Rect2i(var7, var8, var4 + 1, Math.min(var5.getList().size(), CommandSuggestions.this.suggestionLineLimit) * 12);
         this.suggestions = var5;
         this.originalContents = CommandSuggestions.this.input.getValue();
         this.lastNarratedEntry = var6 ? -1 : 0;
         this.select(0);
      }

      public void render(int var1, int var2) {
         int var3 = Math.min(this.suggestions.getList().size(), CommandSuggestions.this.suggestionLineLimit);
         int var4 = -5592406;
         boolean var5 = this.offset > 0;
         boolean var6 = this.suggestions.getList().size() > this.offset + var3;
         boolean var7 = var5 || var6;
         boolean var8 = this.lastMouse.x != (float)var1 || this.lastMouse.y != (float)var2;
         if (var8) {
            this.lastMouse = new Vec2((float)var1, (float)var2);
         }

         if (var7) {
            GuiComponent.fill(this.rect.getX(), this.rect.getY() - 1, this.rect.getX() + this.rect.getWidth(), this.rect.getY(), CommandSuggestions.this.fillColor);
            GuiComponent.fill(this.rect.getX(), this.rect.getY() + this.rect.getHeight(), this.rect.getX() + this.rect.getWidth(), this.rect.getY() + this.rect.getHeight() + 1, CommandSuggestions.this.fillColor);
            int var9;
            if (var5) {
               for(var9 = 0; var9 < this.rect.getWidth(); ++var9) {
                  if (var9 % 2 == 0) {
                     GuiComponent.fill(this.rect.getX() + var9, this.rect.getY() - 1, this.rect.getX() + var9 + 1, this.rect.getY(), -1);
                  }
               }
            }

            if (var6) {
               for(var9 = 0; var9 < this.rect.getWidth(); ++var9) {
                  if (var9 % 2 == 0) {
                     GuiComponent.fill(this.rect.getX() + var9, this.rect.getY() + this.rect.getHeight(), this.rect.getX() + var9 + 1, this.rect.getY() + this.rect.getHeight() + 1, -1);
                  }
               }
            }
         }

         boolean var12 = false;

         for(int var10 = 0; var10 < var3; ++var10) {
            Suggestion var11 = (Suggestion)this.suggestions.getList().get(var10 + this.offset);
            GuiComponent.fill(this.rect.getX(), this.rect.getY() + 12 * var10, this.rect.getX() + this.rect.getWidth(), this.rect.getY() + 12 * var10 + 12, CommandSuggestions.this.fillColor);
            if (var1 > this.rect.getX() && var1 < this.rect.getX() + this.rect.getWidth() && var2 > this.rect.getY() + 12 * var10 && var2 < this.rect.getY() + 12 * var10 + 12) {
               if (var8) {
                  this.select(var10 + this.offset);
               }

               var12 = true;
            }

            CommandSuggestions.this.font.drawShadow(var11.getText(), (float)(this.rect.getX() + 1), (float)(this.rect.getY() + 2 + 12 * var10), var10 + this.offset == this.current ? -256 : -5592406);
         }

         if (var12) {
            Message var13 = ((Suggestion)this.suggestions.getList().get(this.current)).getTooltip();
            if (var13 != null) {
               CommandSuggestions.this.screen.renderTooltip(ComponentUtils.fromMessage(var13).getColoredString(), var1, var2);
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
         int var3 = (int)(CommandSuggestions.this.minecraft.mouseHandler.xpos() * (double)CommandSuggestions.this.minecraft.getWindow().getGuiScaledWidth() / (double)CommandSuggestions.this.minecraft.getWindow().getScreenWidth());
         int var4 = (int)(CommandSuggestions.this.minecraft.mouseHandler.ypos() * (double)CommandSuggestions.this.minecraft.getWindow().getGuiScaledHeight() / (double)CommandSuggestions.this.minecraft.getWindow().getScreenHeight());
         if (this.rect.contains(var3, var4)) {
            this.offset = Mth.clamp((int)((double)this.offset - var1), 0, Math.max(this.suggestions.getList().size() - CommandSuggestions.this.suggestionLineLimit, 0));
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
         int var3 = this.offset + CommandSuggestions.this.suggestionLineLimit - 1;
         if (this.current < var2) {
            this.offset = Mth.clamp(this.current, 0, Math.max(this.suggestions.getList().size() - CommandSuggestions.this.suggestionLineLimit, 0));
         } else if (this.current > var3) {
            this.offset = Mth.clamp(this.current + CommandSuggestions.this.lineStartOffset - CommandSuggestions.this.suggestionLineLimit, 0, Math.max(this.suggestions.getList().size() - CommandSuggestions.this.suggestionLineLimit, 0));
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
         CommandSuggestions.this.input.setSuggestion(CommandSuggestions.calculateSuggestionSuffix(CommandSuggestions.this.input.getValue(), var2.apply(this.originalContents)));
         if (NarratorChatListener.INSTANCE.isActive() && this.lastNarratedEntry != this.current) {
            NarratorChatListener.INSTANCE.sayNow(this.getNarrationMessage());
         }

      }

      public void useSuggestion() {
         Suggestion var1 = (Suggestion)this.suggestions.getList().get(this.current);
         CommandSuggestions.this.keepSuggestions = true;
         CommandSuggestions.this.input.setValue(var1.apply(this.originalContents));
         int var2 = var1.getRange().getStart() + var1.getText().length();
         CommandSuggestions.this.input.setCursorPosition(var2);
         CommandSuggestions.this.input.setHighlightPos(var2);
         this.select(this.current);
         CommandSuggestions.this.keepSuggestions = false;
         this.tabCycles = true;
      }

      private String getNarrationMessage() {
         this.lastNarratedEntry = this.current;
         List var1 = this.suggestions.getList();
         Suggestion var2 = (Suggestion)var1.get(this.current);
         Message var3 = var2.getTooltip();
         return var3 != null ? I18n.get("narration.suggestion.tooltip", this.current + 1, var1.size(), var2.getText(), var3.getString()) : I18n.get("narration.suggestion", this.current + 1, var1.size(), var2.getText());
      }

      public void hide() {
         CommandSuggestions.this.suggestions = null;
      }

      // $FF: synthetic method
      SuggestionsList(int var2, int var3, int var4, Suggestions var5, boolean var6, Object var7) {
         this(var2, var3, var4, var5, var6);
      }
   }
}
