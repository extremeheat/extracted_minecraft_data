package net.minecraft.client.gui.components;

import com.google.common.base.Strings;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.mojang.blaze3d.platform.cursor.CursorTypes;
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
import com.mojang.brigadier.tree.CommandNode;
import com.mojang.brigadier.tree.LiteralCommandNode;
import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.multiplayer.ClientSuggestionProvider;
import net.minecraft.client.renderer.Rect2i;
import net.minecraft.commands.Commands;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentUtils;
import net.minecraft.network.chat.Style;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec2;
import org.jspecify.annotations.Nullable;

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
   private @Nullable ParseResults<ClientSuggestionProvider> currentParse;
   private @Nullable CompletableFuture<Suggestions> pendingSuggestions;
   private @Nullable SuggestionsList suggestions;
   private boolean allowSuggestions;
   private boolean keepSuggestions;
   private boolean allowHiding = true;

   public CommandSuggestions(final Minecraft minecraft, final Screen screen, final EditBox input, final Font font, final boolean commandsOnly, final boolean onlyShowIfCursorPastError, final int lineStartOffset, final int suggestionLineLimit, final boolean anchorToBottom, final int fillColor) {
      super();
      this.minecraft = minecraft;
      this.screen = screen;
      this.input = input;
      this.font = font;
      this.commandsOnly = commandsOnly;
      this.onlyShowIfCursorPastError = onlyShowIfCursorPastError;
      this.lineStartOffset = lineStartOffset;
      this.suggestionLineLimit = suggestionLineLimit;
      this.anchorToBottom = anchorToBottom;
      this.fillColor = fillColor;
      input.addFormatter(this::formatChat);
   }

   public void setAllowSuggestions(final boolean allowSuggestions) {
      this.allowSuggestions = allowSuggestions;
      if (!allowSuggestions) {
         this.suggestions = null;
      }

   }

   public void setAllowHiding(final boolean allowHiding) {
      this.allowHiding = allowHiding;
   }

   public boolean keyPressed(final KeyEvent event) {
      boolean isVisible = this.suggestions != null;
      if (isVisible && this.suggestions.keyPressed(event)) {
         return true;
      } else if (this.screen.getFocused() != this.input || !event.isCycleFocus() || this.allowHiding && !isVisible) {
         return false;
      } else {
         this.showSuggestions(true);
         return true;
      }
   }

   public boolean mouseScrolled(final double scroll) {
      return this.suggestions != null && this.suggestions.mouseScrolled(Mth.clamp(scroll, -1.0, 1.0));
   }

   public boolean mouseClicked(final MouseButtonEvent event) {
      return this.suggestions != null && this.suggestions.mouseClicked((int)event.x(), (int)event.y());
   }

   public void showSuggestions(final boolean immediateNarration) {
      if (this.pendingSuggestions != null && this.pendingSuggestions.isDone()) {
         Suggestions suggestions = (Suggestions)this.pendingSuggestions.join();
         if (!suggestions.isEmpty()) {
            int maxSuggestionWidth = 0;

            for(Suggestion suggestion : suggestions.getList()) {
               maxSuggestionWidth = Math.max(maxSuggestionWidth, this.font.width(suggestion.getText()));
            }

            int x = Mth.clamp(this.input.getScreenX(suggestions.getRange().getStart()), 0, this.input.getScreenX(0) + this.input.getInnerWidth() - maxSuggestionWidth);
            int y = this.anchorToBottom ? this.screen.height - 12 : 72;
            this.suggestions = new SuggestionsList(x, y, maxSuggestionWidth, this.sortSuggestions(suggestions), immediateNarration);
         }
      }

   }

   public boolean isVisible() {
      return this.suggestions != null;
   }

   public Component getUsageNarration() {
      if (this.suggestions != null && this.suggestions.tabCycles) {
         return this.allowHiding ? Component.translatable("narration.suggestion.usage.cycle.hidable") : Component.translatable("narration.suggestion.usage.cycle.fixed");
      } else {
         return this.allowHiding ? Component.translatable("narration.suggestion.usage.fill.hidable") : Component.translatable("narration.suggestion.usage.fill.fixed");
      }
   }

   public void hide() {
      this.suggestions = null;
   }

   private List<Suggestion> sortSuggestions(final Suggestions suggestions) {
      String partialCommand = this.input.getValue().substring(0, this.input.getCursorPosition());
      int lastWordIndex = getLastWordIndex(partialCommand);
      String lastWord = partialCommand.substring(lastWordIndex).toLowerCase(Locale.ROOT);
      List<Suggestion> suggestionList = Lists.newArrayList();
      List<Suggestion> partial = Lists.newArrayList();

      for(Suggestion suggestion : suggestions.getList()) {
         if (!suggestion.getText().startsWith(lastWord) && !suggestion.getText().startsWith("minecraft:" + lastWord)) {
            partial.add(suggestion);
         } else {
            suggestionList.add(suggestion);
         }
      }

      suggestionList.addAll(partial);
      return suggestionList;
   }

   public void updateCommandInfo() {
      String command = this.input.getValue();
      if (this.currentParse != null && !this.currentParse.getReader().getString().equals(command)) {
         this.currentParse = null;
      }

      if (!this.keepSuggestions) {
         this.input.setSuggestion((String)null);
         this.suggestions = null;
      }

      this.commandUsage.clear();
      StringReader reader = new StringReader(command);
      boolean startsWithSlash = reader.canRead() && reader.peek() == '/';
      if (startsWithSlash) {
         reader.skip();
      }

      boolean isCommand = this.commandsOnly || startsWithSlash;
      int cursorPosition = this.input.getCursorPosition();
      if (isCommand) {
         CommandDispatcher<ClientSuggestionProvider> commands = this.minecraft.player.connection.getCommands();
         if (this.currentParse == null) {
            this.currentParse = commands.parse(reader, this.minecraft.player.connection.getSuggestionsProvider());
         }

         int parseStart = this.onlyShowIfCursorPastError ? reader.getCursor() : 1;
         if (cursorPosition >= parseStart && (this.suggestions == null || !this.keepSuggestions)) {
            this.pendingSuggestions = commands.getCompletionSuggestions(this.currentParse, cursorPosition);
            this.pendingSuggestions.thenRun(() -> {
               if (this.pendingSuggestions.isDone()) {
                  this.updateUsageInfo();
               }
            });
         }
      } else {
         String partialCommand = command.substring(0, cursorPosition);
         int lastWord = getLastWordIndex(partialCommand);
         Collection<String> nonCommandSuggestions = this.minecraft.player.connection.getSuggestionsProvider().getCustomTabSugggestions();
         this.pendingSuggestions = SharedSuggestionProvider.suggest(nonCommandSuggestions, new SuggestionsBuilder(partialCommand, lastWord));
      }

   }

   private static int getLastWordIndex(final String text) {
      if (Strings.isNullOrEmpty(text)) {
         return 0;
      } else {
         int result = 0;

         for(Matcher matcher = WHITESPACE_PATTERN.matcher(text); matcher.find(); result = matcher.end()) {
         }

         return result;
      }
   }

   private static FormattedCharSequence getExceptionMessage(final CommandSyntaxException e) {
      Component message = ComponentUtils.fromMessage(e.getRawMessage());
      String context = e.getContext();
      return context == null ? message.getVisualOrderText() : Component.translatable("command.context.parse_error", message, e.getCursor(), context).getVisualOrderText();
   }

   private void updateUsageInfo() {
      boolean trailingCharacters = false;
      if (this.input.getCursorPosition() == this.input.getValue().length()) {
         if (((Suggestions)this.pendingSuggestions.join()).isEmpty() && !this.currentParse.getExceptions().isEmpty()) {
            int literals = 0;

            for(Map.Entry<CommandNode<ClientSuggestionProvider>, CommandSyntaxException> entry : this.currentParse.getExceptions().entrySet()) {
               CommandSyntaxException exception = (CommandSyntaxException)entry.getValue();
               if (exception.getType() == CommandSyntaxException.BUILT_IN_EXCEPTIONS.literalIncorrect()) {
                  ++literals;
               } else {
                  this.commandUsage.add(getExceptionMessage(exception));
               }
            }

            if (literals > 0) {
               this.commandUsage.add(getExceptionMessage(CommandSyntaxException.BUILT_IN_EXCEPTIONS.dispatcherUnknownArgument().createWithContext(this.currentParse.getReader())));
            }
         } else if (this.currentParse.getReader().canRead()) {
            trailingCharacters = true;
         }
      }

      this.commandUsagePosition = 0;
      this.commandUsageWidth = this.screen.width;
      if (this.commandUsage.isEmpty() && !this.fillNodeUsage(ChatFormatting.GRAY) && trailingCharacters) {
         this.commandUsage.add(getExceptionMessage(Commands.getParseException(this.currentParse)));
      }

      this.suggestions = null;
      if (this.allowSuggestions && (Boolean)this.minecraft.options.autoSuggestions().get()) {
         this.showSuggestions(false);
      }

   }

   private boolean fillNodeUsage(final ChatFormatting color) {
      CommandContextBuilder<ClientSuggestionProvider> rootContext = this.currentParse.getContext();
      SuggestionContext<ClientSuggestionProvider> suggestionContext = rootContext.findSuggestionContext(this.input.getCursorPosition());
      Map<CommandNode<ClientSuggestionProvider>, String> usage = this.minecraft.player.connection.getCommands().getSmartUsage(suggestionContext.parent, this.minecraft.player.connection.getSuggestionsProvider());
      List<FormattedCharSequence> lines = Lists.newArrayList();
      int longest = 0;
      Style usageFormat = Style.EMPTY.withColor(color);

      for(Map.Entry<CommandNode<ClientSuggestionProvider>, String> entry : usage.entrySet()) {
         if (!(entry.getKey() instanceof LiteralCommandNode)) {
            lines.add(FormattedCharSequence.forward((String)entry.getValue(), usageFormat));
            longest = Math.max(longest, this.font.width((String)entry.getValue()));
         }
      }

      if (!lines.isEmpty()) {
         this.commandUsage.addAll(lines);
         this.commandUsagePosition = Mth.clamp(this.input.getScreenX(suggestionContext.startPos), 0, this.input.getScreenX(0) + this.input.getInnerWidth() - longest);
         this.commandUsageWidth = longest;
         return true;
      } else {
         return false;
      }
   }

   private @Nullable FormattedCharSequence formatChat(final String text, final int offset) {
      return this.currentParse != null ? formatText(this.currentParse, text, offset) : null;
   }

   private static @Nullable String calculateSuggestionSuffix(final String contents, final String suggestion) {
      return suggestion.startsWith(contents) ? suggestion.substring(contents.length()) : null;
   }

   private static FormattedCharSequence formatText(final ParseResults<ClientSuggestionProvider> currentParse, final String text, final int offset) {
      List<FormattedCharSequence> parts = Lists.newArrayList();
      int unformattedStart = 0;
      int nextColor = -1;
      CommandContextBuilder<ClientSuggestionProvider> context = currentParse.getContext().getLastChild();

      for(ParsedArgument<ClientSuggestionProvider, ?> argument : context.getArguments().values()) {
         ++nextColor;
         if (nextColor >= ARGUMENT_STYLES.size()) {
            nextColor = 0;
         }

         int start = Math.max(argument.getRange().getStart() - offset, 0);
         if (start >= text.length()) {
            break;
         }

         int end = Math.min(argument.getRange().getEnd() - offset, text.length());
         if (end > 0) {
            parts.add(FormattedCharSequence.forward(text.substring(unformattedStart, start), LITERAL_STYLE));
            parts.add(FormattedCharSequence.forward(text.substring(start, end), (Style)ARGUMENT_STYLES.get(nextColor)));
            unformattedStart = end;
         }
      }

      if (currentParse.getReader().canRead()) {
         int start = Math.max(currentParse.getReader().getCursor() - offset, 0);
         if (start < text.length()) {
            int end = Math.min(start + currentParse.getReader().getRemainingLength(), text.length());
            parts.add(FormattedCharSequence.forward(text.substring(unformattedStart, start), LITERAL_STYLE));
            parts.add(FormattedCharSequence.forward(text.substring(start, end), UNPARSED_STYLE));
            unformattedStart = end;
         }
      }

      parts.add(FormattedCharSequence.forward(text.substring(unformattedStart), LITERAL_STYLE));
      return FormattedCharSequence.composite(parts);
   }

   public void render(final GuiGraphics graphics, final int mouseX, final int mouseY) {
      if (!this.renderSuggestions(graphics, mouseX, mouseY)) {
         this.renderUsage(graphics);
      }

   }

   public boolean renderSuggestions(final GuiGraphics graphics, final int mouseX, final int mouseY) {
      if (this.suggestions != null) {
         this.suggestions.render(graphics, mouseX, mouseY);
         return true;
      } else {
         return false;
      }
   }

   public void renderUsage(final GuiGraphics graphics) {
      int y = 0;

      for(FormattedCharSequence line : this.commandUsage) {
         int lineY = this.anchorToBottom ? this.screen.height - 14 - 13 - 12 * y : 72 + 12 * y;
         graphics.fill(this.commandUsagePosition - 1, lineY, this.commandUsagePosition + this.commandUsageWidth + 1, lineY + 12, this.fillColor);
         graphics.drawString(this.font, (FormattedCharSequence)line, this.commandUsagePosition, lineY + 2, -1);
         ++y;
      }

   }

   public Component getNarrationMessage() {
      return (Component)(this.suggestions != null ? CommonComponents.NEW_LINE.copy().append(this.suggestions.getNarrationMessage()) : CommonComponents.EMPTY);
   }

   static {
      UNPARSED_STYLE = Style.EMPTY.withColor(ChatFormatting.RED);
      LITERAL_STYLE = Style.EMPTY.withColor(ChatFormatting.GRAY);
      Stream var10000 = Stream.of(ChatFormatting.AQUA, ChatFormatting.YELLOW, ChatFormatting.GREEN, ChatFormatting.LIGHT_PURPLE, ChatFormatting.GOLD);
      Style var10001 = Style.EMPTY;
      Objects.requireNonNull(var10001);
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

      private SuggestionsList(final int x, final int y, final int width, final List<Suggestion> suggestionList, final boolean immediateNarration) {
         Objects.requireNonNull(CommandSuggestions.this);
         super();
         this.lastMouse = Vec2.ZERO;
         int listX = x - (CommandSuggestions.this.input.isBordered() ? 0 : 1);
         int listY = CommandSuggestions.this.anchorToBottom ? y - 3 - Math.min(suggestionList.size(), CommandSuggestions.this.suggestionLineLimit) * 12 : y - (CommandSuggestions.this.input.isBordered() ? 1 : 0);
         this.rect = new Rect2i(listX, listY, width + 1, Math.min(suggestionList.size(), CommandSuggestions.this.suggestionLineLimit) * 12);
         this.originalContents = CommandSuggestions.this.input.getValue();
         this.lastNarratedEntry = immediateNarration ? -1 : 0;
         this.suggestionList = suggestionList;
         this.select(0);
      }

      public void render(final GuiGraphics graphics, final int mouseX, final int mouseY) {
         int limit = Math.min(this.suggestionList.size(), CommandSuggestions.this.suggestionLineLimit);
         int unselectedColor = -5592406;
         boolean hasPrevious = this.offset > 0;
         boolean hasNext = this.suggestionList.size() > this.offset + limit;
         boolean limited = hasPrevious || hasNext;
         boolean mouseMoved = this.lastMouse.x != (float)mouseX || this.lastMouse.y != (float)mouseY;
         if (mouseMoved) {
            this.lastMouse = new Vec2((float)mouseX, (float)mouseY);
         }

         if (limited) {
            graphics.fill(this.rect.getX(), this.rect.getY() - 1, this.rect.getX() + this.rect.getWidth(), this.rect.getY(), CommandSuggestions.this.fillColor);
            graphics.fill(this.rect.getX(), this.rect.getY() + this.rect.getHeight(), this.rect.getX() + this.rect.getWidth(), this.rect.getY() + this.rect.getHeight() + 1, CommandSuggestions.this.fillColor);
            if (hasPrevious) {
               for(int x = 0; x < this.rect.getWidth(); ++x) {
                  if (x % 2 == 0) {
                     graphics.fill(this.rect.getX() + x, this.rect.getY() - 1, this.rect.getX() + x + 1, this.rect.getY(), -1);
                  }
               }
            }

            if (hasNext) {
               for(int x = 0; x < this.rect.getWidth(); ++x) {
                  if (x % 2 == 0) {
                     graphics.fill(this.rect.getX() + x, this.rect.getY() + this.rect.getHeight(), this.rect.getX() + x + 1, this.rect.getY() + this.rect.getHeight() + 1, -1);
                  }
               }
            }
         }

         boolean hovered = false;

         for(int i = 0; i < limit; ++i) {
            Suggestion suggestion = (Suggestion)this.suggestionList.get(i + this.offset);
            graphics.fill(this.rect.getX(), this.rect.getY() + 12 * i, this.rect.getX() + this.rect.getWidth(), this.rect.getY() + 12 * i + 12, CommandSuggestions.this.fillColor);
            if (mouseX > this.rect.getX() && mouseX < this.rect.getX() + this.rect.getWidth() && mouseY > this.rect.getY() + 12 * i && mouseY < this.rect.getY() + 12 * i + 12) {
               if (mouseMoved) {
                  this.select(i + this.offset);
               }

               hovered = true;
            }

            graphics.drawString(CommandSuggestions.this.font, suggestion.getText(), this.rect.getX() + 1, this.rect.getY() + 2 + 12 * i, i + this.offset == this.current ? -256 : -5592406);
         }

         if (hovered) {
            Message tooltip = ((Suggestion)this.suggestionList.get(this.current)).getTooltip();
            if (tooltip != null) {
               graphics.setTooltipForNextFrame(CommandSuggestions.this.font, ComponentUtils.fromMessage(tooltip), mouseX, mouseY);
            }
         }

         if (this.rect.contains(mouseX, mouseY)) {
            graphics.requestCursor(CursorTypes.POINTING_HAND);
         }

      }

      public boolean mouseClicked(final int x, final int y) {
         if (!this.rect.contains(x, y)) {
            return false;
         } else {
            int line = (y - this.rect.getY()) / 12 + this.offset;
            if (line >= 0 && line < this.suggestionList.size()) {
               this.select(line);
               this.useSuggestion();
            }

            return true;
         }
      }

      public boolean mouseScrolled(final double scroll) {
         int mouseX = (int)CommandSuggestions.this.minecraft.mouseHandler.getScaledXPos(CommandSuggestions.this.minecraft.getWindow());
         int mouseY = (int)CommandSuggestions.this.minecraft.mouseHandler.getScaledYPos(CommandSuggestions.this.minecraft.getWindow());
         if (this.rect.contains(mouseX, mouseY)) {
            this.offset = Mth.clamp((int)((double)this.offset - scroll), 0, Math.max(this.suggestionList.size() - CommandSuggestions.this.suggestionLineLimit, 0));
            return true;
         } else {
            return false;
         }
      }

      public boolean keyPressed(final KeyEvent event) {
         if (event.isUp()) {
            this.cycle(-1);
            this.tabCycles = false;
            return true;
         } else if (event.isDown()) {
            this.cycle(1);
            this.tabCycles = false;
            return true;
         } else if (event.isCycleFocus()) {
            if (this.tabCycles) {
               this.cycle(event.hasShiftDown() ? -1 : 1);
            }

            this.useSuggestion();
            return true;
         } else if (event.isEscape()) {
            CommandSuggestions.this.hide();
            CommandSuggestions.this.input.setSuggestion((String)null);
            return true;
         } else {
            return false;
         }
      }

      public void cycle(final int direction) {
         this.select(this.current + direction);
         int first = this.offset;
         int last = this.offset + CommandSuggestions.this.suggestionLineLimit - 1;
         if (this.current < first) {
            this.offset = Mth.clamp(this.current, 0, Math.max(this.suggestionList.size() - CommandSuggestions.this.suggestionLineLimit, 0));
         } else if (this.current > last) {
            this.offset = Mth.clamp(this.current + CommandSuggestions.this.lineStartOffset - CommandSuggestions.this.suggestionLineLimit, 0, Math.max(this.suggestionList.size() - CommandSuggestions.this.suggestionLineLimit, 0));
         }

      }

      public void select(final int index) {
         this.current = index;
         if (this.current < 0) {
            this.current += this.suggestionList.size();
         }

         if (this.current >= this.suggestionList.size()) {
            this.current -= this.suggestionList.size();
         }

         Suggestion suggestion = (Suggestion)this.suggestionList.get(this.current);
         CommandSuggestions.this.input.setSuggestion(CommandSuggestions.calculateSuggestionSuffix(CommandSuggestions.this.input.getValue(), suggestion.apply(this.originalContents)));
         if (this.lastNarratedEntry != this.current) {
            CommandSuggestions.this.minecraft.getNarrator().saySystemNow(this.getNarrationMessage());
         }

      }

      public void useSuggestion() {
         Suggestion suggestion = (Suggestion)this.suggestionList.get(this.current);
         CommandSuggestions.this.keepSuggestions = true;
         CommandSuggestions.this.input.setValue(suggestion.apply(this.originalContents));
         int end = suggestion.getRange().getStart() + suggestion.getText().length();
         CommandSuggestions.this.input.setCursorPosition(end);
         CommandSuggestions.this.input.setHighlightPos(end);
         this.select(this.current);
         CommandSuggestions.this.keepSuggestions = false;
         this.tabCycles = true;
      }

      private Component getNarrationMessage() {
         this.lastNarratedEntry = this.current;
         Suggestion suggestion = (Suggestion)this.suggestionList.get(this.current);
         Message tooltip = suggestion.getTooltip();
         return tooltip != null ? Component.translatable("narration.suggestion.tooltip", this.current + 1, this.suggestionList.size(), suggestion.getText(), Component.translationArg(tooltip)) : Component.translatable("narration.suggestion", this.current + 1, this.suggestionList.size(), suggestion.getText());
      }
   }
}
