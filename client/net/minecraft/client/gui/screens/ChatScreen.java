package net.minecraft.client.gui.screens;

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
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.gui.chat.NarratorChatListener;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.renderer.Rect2i;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentUtils;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec2;

public class ChatScreen extends Screen {
   private static final Pattern WHITESPACE_PATTERN = Pattern.compile("(\\s+)");
   private String historyBuffer = "";
   private int historyPos = -1;
   protected EditBox input;
   private String initial = "";
   protected final List<String> commandUsage = Lists.newArrayList();
   protected int commandUsagePosition;
   protected int commandUsageWidth;
   private ParseResults<SharedSuggestionProvider> currentParse;
   private CompletableFuture<Suggestions> pendingSuggestions;
   private ChatScreen.SuggestionsList suggestions;
   private boolean hasEdits;
   private boolean keepSuggestions;

   public ChatScreen(String var1) {
      super(NarratorChatListener.NO_TITLE);
      this.initial = var1;
   }

   protected void init() {
      this.minecraft.keyboardHandler.setSendRepeatsToGui(true);
      this.historyPos = this.minecraft.gui.getChat().getRecentChat().size();
      this.input = new EditBox(this.font, 4, this.height - 12, this.width - 4, 12, I18n.get("chat.editBox"));
      this.input.setMaxLength(256);
      this.input.setBordered(false);
      this.input.setValue(this.initial);
      this.input.setFormatter(this::formatChat);
      this.input.setResponder(this::onEdited);
      this.children.add(this.input);
      this.updateCommandInfo();
      this.setInitialFocus(this.input);
   }

   public void resize(Minecraft var1, int var2, int var3) {
      String var4 = this.input.getValue();
      this.init(var1, var2, var3);
      this.setChatLine(var4);
      this.updateCommandInfo();
   }

   public void removed() {
      this.minecraft.keyboardHandler.setSendRepeatsToGui(false);
      this.minecraft.gui.getChat().resetChatScroll();
   }

   public void tick() {
      this.input.tick();
   }

   private void onEdited(String var1) {
      String var2 = this.input.getValue();
      this.hasEdits = !var2.equals(this.initial);
      this.updateCommandInfo();
   }

   public boolean keyPressed(int var1, int var2, int var3) {
      if (this.suggestions != null && this.suggestions.keyPressed(var1, var2, var3)) {
         return true;
      } else {
         if (var1 == 258) {
            this.hasEdits = true;
            this.showSuggestions();
         }

         if (super.keyPressed(var1, var2, var3)) {
            return true;
         } else if (var1 == 256) {
            this.minecraft.setScreen((Screen)null);
            return true;
         } else if (var1 != 257 && var1 != 335) {
            if (var1 == 265) {
               this.moveInHistory(-1);
               return true;
            } else if (var1 == 264) {
               this.moveInHistory(1);
               return true;
            } else if (var1 == 266) {
               this.minecraft.gui.getChat().scrollChat((double)(this.minecraft.gui.getChat().getLinesPerPage() - 1));
               return true;
            } else if (var1 == 267) {
               this.minecraft.gui.getChat().scrollChat((double)(-this.minecraft.gui.getChat().getLinesPerPage() + 1));
               return true;
            } else {
               return false;
            }
         } else {
            String var4 = this.input.getValue().trim();
            if (!var4.isEmpty()) {
               this.sendMessage(var4);
            }

            this.minecraft.setScreen((Screen)null);
            return true;
         }
      }
   }

   public void showSuggestions() {
      if (this.pendingSuggestions != null && this.pendingSuggestions.isDone()) {
         int var1 = 0;
         Suggestions var2 = (Suggestions)this.pendingSuggestions.join();
         if (!var2.getList().isEmpty()) {
            Suggestion var4;
            for(Iterator var3 = var2.getList().iterator(); var3.hasNext(); var1 = Math.max(var1, this.font.width(var4.getText()))) {
               var4 = (Suggestion)var3.next();
            }

            int var5 = Mth.clamp(this.input.getScreenX(var2.getRange().getStart()), 0, this.width - var1);
            this.suggestions = new ChatScreen.SuggestionsList(var5, this.height - 12, var1, var2);
         }
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

   private void updateCommandInfo() {
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
      int var4;
      if (var2.canRead() && var2.peek() == '/') {
         var2.skip();
         CommandDispatcher var3 = this.minecraft.player.connection.getCommands();
         if (this.currentParse == null) {
            this.currentParse = var3.parse(var2, this.minecraft.player.connection.getSuggestionsProvider());
         }

         var4 = this.input.getCursorPosition();
         if (var4 >= 1 && (this.suggestions == null || !this.keepSuggestions)) {
            this.pendingSuggestions = var3.getCompletionSuggestions(this.currentParse, var4);
            this.pendingSuggestions.thenRun(() -> {
               if (this.pendingSuggestions.isDone()) {
                  this.updateUsageInfo();
               }
            });
         }
      } else {
         var4 = getLastWordIndex(var1);
         Collection var5 = this.minecraft.player.connection.getSuggestionsProvider().getOnlinePlayerNames();
         this.pendingSuggestions = SharedSuggestionProvider.suggest((Iterable)var5, new SuggestionsBuilder(var1, var4));
      }

   }

   private void updateUsageInfo() {
      if (((Suggestions)this.pendingSuggestions.join()).isEmpty() && !this.currentParse.getExceptions().isEmpty() && this.input.getCursorPosition() == this.input.getValue().length()) {
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
      if (this.hasEdits && this.minecraft.options.autoSuggestions) {
         this.showSuggestions();
      }

   }

   private String formatChat(String var1, int var2) {
      return this.currentParse != null ? formatText(this.currentParse, var1, var2) : var1;
   }

   public static String formatText(ParseResults<SharedSuggestionProvider> var0, String var1, int var2) {
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

   public boolean mouseScrolled(double var1, double var3, double var5) {
      if (var5 > 1.0D) {
         var5 = 1.0D;
      }

      if (var5 < -1.0D) {
         var5 = -1.0D;
      }

      if (this.suggestions != null && this.suggestions.mouseScrolled(var5)) {
         return true;
      } else {
         if (!hasShiftDown()) {
            var5 *= 7.0D;
         }

         this.minecraft.gui.getChat().scrollChat(var5);
         return true;
      }
   }

   public boolean mouseClicked(double var1, double var3, int var5) {
      if (this.suggestions != null && this.suggestions.mouseClicked((int)var1, (int)var3, var5)) {
         return true;
      } else {
         if (var5 == 0) {
            Component var6 = this.minecraft.gui.getChat().getClickedComponentAt(var1, var3);
            if (var6 != null && this.handleComponentClicked(var6)) {
               return true;
            }
         }

         return this.input.mouseClicked(var1, var3, var5) ? true : super.mouseClicked(var1, var3, var5);
      }
   }

   protected void insertText(String var1, boolean var2) {
      if (var2) {
         this.input.setValue(var1);
      } else {
         this.input.insertText(var1);
      }

   }

   public void moveInHistory(int var1) {
      int var2 = this.historyPos + var1;
      int var3 = this.minecraft.gui.getChat().getRecentChat().size();
      var2 = Mth.clamp(var2, 0, var3);
      if (var2 != this.historyPos) {
         if (var2 == var3) {
            this.historyPos = var3;
            this.input.setValue(this.historyBuffer);
         } else {
            if (this.historyPos == var3) {
               this.historyBuffer = this.input.getValue();
            }

            this.input.setValue((String)this.minecraft.gui.getChat().getRecentChat().get(var2));
            this.suggestions = null;
            this.historyPos = var2;
            this.hasEdits = false;
         }
      }
   }

   public void render(int var1, int var2, float var3) {
      this.setFocused(this.input);
      this.input.setFocus(true);
      fill(2, this.height - 14, this.width - 2, this.height - 2, this.minecraft.options.getBackgroundColor(-2147483648));
      this.input.render(var1, var2, var3);
      if (this.suggestions != null) {
         this.suggestions.render(var1, var2);
      } else {
         int var4 = 0;

         for(Iterator var5 = this.commandUsage.iterator(); var5.hasNext(); ++var4) {
            String var6 = (String)var5.next();
            fill(this.commandUsagePosition - 1, this.height - 14 - 13 - 12 * var4, this.commandUsagePosition + this.commandUsageWidth + 1, this.height - 2 - 13 - 12 * var4, -16777216);
            this.font.drawShadow(var6, (float)this.commandUsagePosition, (float)(this.height - 14 - 13 + 2 - 12 * var4), -1);
         }
      }

      Component var7 = this.minecraft.gui.getChat().getClickedComponentAt((double)var1, (double)var2);
      if (var7 != null && var7.getStyle().getHoverEvent() != null) {
         this.renderComponentHoverEffect(var7, var1, var2);
      }

      super.render(var1, var2, var3);
   }

   public boolean isPauseScreen() {
      return false;
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
         this.commandUsagePosition = Mth.clamp(this.input.getScreenX(var3.startPos), 0, this.width - var6);
         this.commandUsageWidth = var6;
      }

   }

   @Nullable
   private static String calculateSuggestionSuffix(String var0, String var1) {
      return var1.startsWith(var0) ? var1.substring(var0.length()) : null;
   }

   private void setChatLine(String var1) {
      this.input.setValue(var1);
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
         this.rect = new Rect2i(var2 - 1, var3 - 3 - Math.min(var5.getList().size(), 10) * 12, var4 + 1, Math.min(var5.getList().size(), 10) * 12);
         this.suggestions = var5;
         this.originalContents = ChatScreen.this.input.getValue();
         this.select(0);
      }

      public void render(int var1, int var2) {
         int var3 = Math.min(this.suggestions.getList().size(), 10);
         int var4 = -5592406;
         boolean var5 = this.offset > 0;
         boolean var6 = this.suggestions.getList().size() > this.offset + var3;
         boolean var7 = var5 || var6;
         boolean var8 = this.lastMouse.x != (float)var1 || this.lastMouse.y != (float)var2;
         if (var8) {
            this.lastMouse = new Vec2((float)var1, (float)var2);
         }

         if (var7) {
            GuiComponent.fill(this.rect.getX(), this.rect.getY() - 1, this.rect.getX() + this.rect.getWidth(), this.rect.getY(), -805306368);
            GuiComponent.fill(this.rect.getX(), this.rect.getY() + this.rect.getHeight(), this.rect.getX() + this.rect.getWidth(), this.rect.getY() + this.rect.getHeight() + 1, -805306368);
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
            GuiComponent.fill(this.rect.getX(), this.rect.getY() + 12 * var10, this.rect.getX() + this.rect.getWidth(), this.rect.getY() + 12 * var10 + 12, -805306368);
            if (var1 > this.rect.getX() && var1 < this.rect.getX() + this.rect.getWidth() && var2 > this.rect.getY() + 12 * var10 && var2 < this.rect.getY() + 12 * var10 + 12) {
               if (var8) {
                  this.select(var10 + this.offset);
               }

               var12 = true;
            }

            ChatScreen.this.font.drawShadow(var11.getText(), (float)(this.rect.getX() + 1), (float)(this.rect.getY() + 2 + 12 * var10), var10 + this.offset == this.current ? -256 : -5592406);
         }

         if (var12) {
            Message var13 = ((Suggestion)this.suggestions.getList().get(this.current)).getTooltip();
            if (var13 != null) {
               ChatScreen.this.renderTooltip(ComponentUtils.fromMessage(var13).getColoredString(), var1, var2);
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
         int var3 = (int)(ChatScreen.this.minecraft.mouseHandler.xpos() * (double)ChatScreen.this.minecraft.window.getGuiScaledWidth() / (double)ChatScreen.this.minecraft.window.getScreenWidth());
         int var4 = (int)(ChatScreen.this.minecraft.mouseHandler.ypos() * (double)ChatScreen.this.minecraft.window.getGuiScaledHeight() / (double)ChatScreen.this.minecraft.window.getScreenHeight());
         if (this.rect.contains(var3, var4)) {
            this.offset = Mth.clamp((int)((double)this.offset - var1), 0, Math.max(this.suggestions.getList().size() - 10, 0));
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
         int var3 = this.offset + 10 - 1;
         if (this.current < var2) {
            this.offset = Mth.clamp(this.current, 0, Math.max(this.suggestions.getList().size() - 10, 0));
         } else if (this.current > var3) {
            this.offset = Mth.clamp(this.current + 1 - 10, 0, Math.max(this.suggestions.getList().size() - 10, 0));
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
         ChatScreen.this.input.setSuggestion(ChatScreen.calculateSuggestionSuffix(ChatScreen.this.input.getValue(), var2.apply(this.originalContents)));
      }

      public void useSuggestion() {
         Suggestion var1 = (Suggestion)this.suggestions.getList().get(this.current);
         ChatScreen.this.keepSuggestions = true;
         ChatScreen.this.setChatLine(var1.apply(this.originalContents));
         int var2 = var1.getRange().getStart() + var1.getText().length();
         ChatScreen.this.input.setCursorPosition(var2);
         ChatScreen.this.input.setHighlightPos(var2);
         this.select(this.current);
         ChatScreen.this.keepSuggestions = false;
         this.tabCycles = true;
      }

      public void hide() {
         ChatScreen.this.suggestions = null;
      }

      // $FF: synthetic method
      SuggestionsList(int var2, int var3, int var4, Suggestions var5, Object var6) {
         this(var2, var3, var4, var5);
      }
   }
}
