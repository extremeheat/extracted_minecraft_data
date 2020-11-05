package net.minecraft.client.gui.components;

import com.google.common.base.Strings;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.mojang.blaze3d.vertex.PoseStack;
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
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.CompletableFuture;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;
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
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentUtils;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec2;

public class CommandSuggestions {
   private static final Pattern WHITESPACE_PATTERN = Pattern.compile("(\\s+)");
   private static final Style UNPARSED_STYLE;
   private static final Style LITERAL_STYLE;
   private static final List<Style> ARGUMENT_STYLES;
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
   private final List<FormattedCharSequence> commandUsage = Lists.newArrayList();
   private int commandUsagePosition;
   private int commandUsageWidth;
   private ParseResults<SharedSuggestionProvider> currentParse;
   private CompletableFuture<Suggestions> pendingSuggestions;
   private CommandSuggestions.SuggestionsList suggestions;
   private boolean allowSuggestions;
   private boolean keepSuggestions;

   public CommandSuggestions(Minecraft var1, Screen var2, EditBox var3, Font var4, boolean var5, boolean var6, int var7, int var8, boolean var9, int var10) {
      super();
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
            this.suggestions = new CommandSuggestions.SuggestionsList(var6, var7, var3, this.sortSuggestions(var2), var1);
         }
      }

   }

   private List<Suggestion> sortSuggestions(Suggestions var1) {
      String var2 = this.input.getValue().substring(0, this.input.getCursorPosition());
      int var3 = getLastWordIndex(var2);
      String var4 = var2.substring(var3).toLowerCase(Locale.ROOT);
      ArrayList var5 = Lists.newArrayList();
      ArrayList var6 = Lists.newArrayList();
      Iterator var7 = var1.getList().iterator();

      while(true) {
         while(var7.hasNext()) {
            Suggestion var8 = (Suggestion)var7.next();
            if (!var8.getText().startsWith(var4) && !var8.getText().startsWith("minecraft:" + var4)) {
               var6.add(var8);
            } else {
               var5.add(var8);
            }
         }

         var5.addAll(var6);
         return var5;
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

   private static FormattedCharSequence getExceptionMessage(CommandSyntaxException var0) {
      Component var1 = ComponentUtils.fromMessage(var0.getRawMessage());
      String var2 = var0.getContext();
      return var2 == null ? var1.getVisualOrderText() : (new TranslatableComponent("command.context.parse_error", new Object[]{var1, var0.getCursor(), var2})).getVisualOrderText();
   }

   private void updateUsageInfo() {
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
                  this.commandUsage.add(getExceptionMessage(var4));
               }
            }

            if (var1 > 0) {
               this.commandUsage.add(getExceptionMessage(CommandSyntaxException.BUILT_IN_EXCEPTIONS.dispatcherUnknownCommand().create()));
            }
         } else if (this.currentParse.getReader().canRead()) {
            this.commandUsage.add(getExceptionMessage(Commands.getParseException(this.currentParse)));
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
      Style var7 = Style.EMPTY.withColor(var1);
      Iterator var8 = var4.entrySet().iterator();

      while(var8.hasNext()) {
         Entry var9 = (Entry)var8.next();
         if (!(var9.getKey() instanceof LiteralCommandNode)) {
            var5.add(FormattedCharSequence.forward((String)var9.getValue(), var7));
            var6 = Math.max(var6, this.font.width((String)var9.getValue()));
         }
      }

      if (!var5.isEmpty()) {
         this.commandUsage.addAll(var5);
         this.commandUsagePosition = Mth.clamp(this.input.getScreenX(var3.startPos), 0, this.input.getScreenX(0) + this.input.getInnerWidth() - var6);
         this.commandUsageWidth = var6;
      }

   }

   private FormattedCharSequence formatChat(String var1, int var2) {
      return this.currentParse != null ? formatText(this.currentParse, var1, var2) : FormattedCharSequence.forward(var1, Style.EMPTY);
   }

   @Nullable
   private static String calculateSuggestionSuffix(String var0, String var1) {
      return var1.startsWith(var0) ? var1.substring(var0.length()) : null;
   }

   private static FormattedCharSequence formatText(ParseResults<SharedSuggestionProvider> var0, String var1, int var2) {
      ArrayList var3 = Lists.newArrayList();
      int var4 = 0;
      int var5 = -1;
      CommandContextBuilder var6 = var0.getContext().getLastChild();
      Iterator var7 = var6.getArguments().values().iterator();

      while(var7.hasNext()) {
         ParsedArgument var8 = (ParsedArgument)var7.next();
         ++var5;
         if (var5 >= ARGUMENT_STYLES.size()) {
            var5 = 0;
         }

         int var9 = Math.max(var8.getRange().getStart() - var2, 0);
         if (var9 >= var1.length()) {
            break;
         }

         int var10 = Math.min(var8.getRange().getEnd() - var2, var1.length());
         if (var10 > 0) {
            var3.add(FormattedCharSequence.forward(var1.substring(var4, var9), LITERAL_STYLE));
            var3.add(FormattedCharSequence.forward(var1.substring(var9, var10), (Style)ARGUMENT_STYLES.get(var5)));
            var4 = var10;
         }
      }

      if (var0.getReader().canRead()) {
         int var11 = Math.max(var0.getReader().getCursor() - var2, 0);
         if (var11 < var1.length()) {
            int var12 = Math.min(var11 + var0.getReader().getRemainingLength(), var1.length());
            var3.add(FormattedCharSequence.forward(var1.substring(var4, var11), LITERAL_STYLE));
            var3.add(FormattedCharSequence.forward(var1.substring(var11, var12), UNPARSED_STYLE));
            var4 = var12;
         }
      }

      var3.add(FormattedCharSequence.forward(var1.substring(var4), LITERAL_STYLE));
      return FormattedCharSequence.composite(var3);
   }

   public void render(PoseStack var1, int var2, int var3) {
      if (this.suggestions != null) {
         this.suggestions.render(var1, var2, var3);
      } else {
         int var4 = 0;

         for(Iterator var5 = this.commandUsage.iterator(); var5.hasNext(); ++var4) {
            FormattedCharSequence var6 = (FormattedCharSequence)var5.next();
            int var7 = this.anchorToBottom ? this.screen.height - 14 - 13 - 12 * var4 : 72 + 12 * var4;
            GuiComponent.fill(var1, this.commandUsagePosition - 1, var7, this.commandUsagePosition + this.commandUsageWidth + 1, var7 + 12, this.fillColor);
            this.font.drawShadow(var1, (FormattedCharSequence)var6, (float)this.commandUsagePosition, (float)(var7 + 2), -1);
         }
      }

   }

   public String getNarrationMessage() {
      return this.suggestions != null ? "\n" + this.suggestions.getNarrationMessage() : "";
   }

   static {
      UNPARSED_STYLE = Style.EMPTY.withColor(ChatFormatting.RED);
      LITERAL_STYLE = Style.EMPTY.withColor(ChatFormatting.GRAY);
      Stream var10000 = Stream.of(ChatFormatting.AQUA, ChatFormatting.YELLOW, ChatFormatting.GREEN, ChatFormatting.LIGHT_PURPLE, ChatFormatting.GOLD);
      Style var10001 = Style.EMPTY;
      var10001.getClass();
      ARGUMENT_STYLES = (List)var10000.map(var10001::withColor).collect(ImmutableList.toImmutableList());
   }

   public class SuggestionsList {
      private final Rect2i rect;
      private final String originalContents;
      private final List<Suggestion> suggestionList;
      private int offset;
      private int current;
      private Vec2 lastMouse;
      private boolean tabCycles;
      private int lastNarratedEntry;

      private SuggestionsList(int var2, int var3, int var4, List<Suggestion> var5, boolean var6) {
         super();
         this.lastMouse = Vec2.ZERO;
         int var7 = var2 - 1;
         int var8 = CommandSuggestions.this.anchorToBottom ? var3 - 3 - Math.min(var5.size(), CommandSuggestions.this.suggestionLineLimit) * 12 : var3;
         this.rect = new Rect2i(var7, var8, var4 + 1, Math.min(var5.size(), CommandSuggestions.this.suggestionLineLimit) * 12);
         this.originalContents = CommandSuggestions.this.input.getValue();
         this.lastNarratedEntry = var6 ? -1 : 0;
         this.suggestionList = var5;
         this.select(0);
      }

      public void render(PoseStack var1, int var2, int var3) {
         int var4 = Math.min(this.suggestionList.size(), CommandSuggestions.this.suggestionLineLimit);
         int var5 = -5592406;
         boolean var6 = this.offset > 0;
         boolean var7 = this.suggestionList.size() > this.offset + var4;
         boolean var8 = var6 || var7;
         boolean var9 = this.lastMouse.x != (float)var2 || this.lastMouse.y != (float)var3;
         if (var9) {
            this.lastMouse = new Vec2((float)var2, (float)var3);
         }

         if (var8) {
            GuiComponent.fill(var1, this.rect.getX(), this.rect.getY() - 1, this.rect.getX() + this.rect.getWidth(), this.rect.getY(), CommandSuggestions.this.fillColor);
            GuiComponent.fill(var1, this.rect.getX(), this.rect.getY() + this.rect.getHeight(), this.rect.getX() + this.rect.getWidth(), this.rect.getY() + this.rect.getHeight() + 1, CommandSuggestions.this.fillColor);
            int var10;
            if (var6) {
               for(var10 = 0; var10 < this.rect.getWidth(); ++var10) {
                  if (var10 % 2 == 0) {
                     GuiComponent.fill(var1, this.rect.getX() + var10, this.rect.getY() - 1, this.rect.getX() + var10 + 1, this.rect.getY(), -1);
                  }
               }
            }

            if (var7) {
               for(var10 = 0; var10 < this.rect.getWidth(); ++var10) {
                  if (var10 % 2 == 0) {
                     GuiComponent.fill(var1, this.rect.getX() + var10, this.rect.getY() + this.rect.getHeight(), this.rect.getX() + var10 + 1, this.rect.getY() + this.rect.getHeight() + 1, -1);
                  }
               }
            }
         }

         boolean var13 = false;

         for(int var11 = 0; var11 < var4; ++var11) {
            Suggestion var12 = (Suggestion)this.suggestionList.get(var11 + this.offset);
            GuiComponent.fill(var1, this.rect.getX(), this.rect.getY() + 12 * var11, this.rect.getX() + this.rect.getWidth(), this.rect.getY() + 12 * var11 + 12, CommandSuggestions.this.fillColor);
            if (var2 > this.rect.getX() && var2 < this.rect.getX() + this.rect.getWidth() && var3 > this.rect.getY() + 12 * var11 && var3 < this.rect.getY() + 12 * var11 + 12) {
               if (var9) {
                  this.select(var11 + this.offset);
               }

               var13 = true;
            }

            CommandSuggestions.this.font.drawShadow(var1, var12.getText(), (float)(this.rect.getX() + 1), (float)(this.rect.getY() + 2 + 12 * var11), var11 + this.offset == this.current ? -256 : -5592406);
         }

         if (var13) {
            Message var14 = ((Suggestion)this.suggestionList.get(this.current)).getTooltip();
            if (var14 != null) {
               CommandSuggestions.this.screen.renderTooltip(var1, ComponentUtils.fromMessage(var14), var2, var3);
            }
         }

      }

      public boolean mouseClicked(int var1, int var2, int var3) {
         if (!this.rect.contains(var1, var2)) {
            return false;
         } else {
            int var4 = (var2 - this.rect.getY()) / 12 + this.offset;
            if (var4 >= 0 && var4 < this.suggestionList.size()) {
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
            this.offset = Mth.clamp((int)((double)this.offset - var1), 0, Math.max(this.suggestionList.size() - CommandSuggestions.this.suggestionLineLimit, 0));
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
            this.offset = Mth.clamp(this.current, 0, Math.max(this.suggestionList.size() - CommandSuggestions.this.suggestionLineLimit, 0));
         } else if (this.current > var3) {
            this.offset = Mth.clamp(this.current + CommandSuggestions.this.lineStartOffset - CommandSuggestions.this.suggestionLineLimit, 0, Math.max(this.suggestionList.size() - CommandSuggestions.this.suggestionLineLimit, 0));
         }

      }

      public void select(int var1) {
         this.current = var1;
         if (this.current < 0) {
            this.current += this.suggestionList.size();
         }

         if (this.current >= this.suggestionList.size()) {
            this.current -= this.suggestionList.size();
         }

         Suggestion var2 = (Suggestion)this.suggestionList.get(this.current);
         CommandSuggestions.this.input.setSuggestion(CommandSuggestions.calculateSuggestionSuffix(CommandSuggestions.this.input.getValue(), var2.apply(this.originalContents)));
         if (NarratorChatListener.INSTANCE.isActive() && this.lastNarratedEntry != this.current) {
            NarratorChatListener.INSTANCE.sayNow(this.getNarrationMessage());
         }

      }

      public void useSuggestion() {
         Suggestion var1 = (Suggestion)this.suggestionList.get(this.current);
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
         Suggestion var1 = (Suggestion)this.suggestionList.get(this.current);
         Message var2 = var1.getTooltip();
         return var2 != null ? I18n.get("narration.suggestion.tooltip", this.current + 1, this.suggestionList.size(), var1.getText(), var2.getString()) : I18n.get("narration.suggestion", this.current + 1, this.suggestionList.size(), var1.getText());
      }

      public void hide() {
         CommandSuggestions.this.suggestions = null;
      }

      // $FF: synthetic method
      SuggestionsList(int var2, int var3, int var4, List var5, boolean var6, Object var7) {
         this(var2, var3, var4, var5, var6);
      }
   }
}
