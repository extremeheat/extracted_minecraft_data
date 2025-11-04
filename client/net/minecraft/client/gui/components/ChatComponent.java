package net.minecraft.client.gui.components;

import com.google.common.collect.Lists;
import com.mojang.logging.LogUtils;
import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;
import net.minecraft.ChatFormatting;
import net.minecraft.Optionull;
import net.minecraft.client.GuiMessage;
import net.minecraft.client.GuiMessageTag;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ActiveTextCollector;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.TextAlignment;
import net.minecraft.client.gui.screens.ChatScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.HoverEvent;
import net.minecraft.network.chat.MessageSignature;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.Identifier;
import net.minecraft.util.ARGB;
import net.minecraft.util.ArrayListDeque;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.util.Mth;
import net.minecraft.util.profiling.Profiler;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.entity.player.ChatVisiblity;
import org.joml.Matrix3x2f;
import org.joml.Vector2f;
import org.jspecify.annotations.Nullable;
import org.slf4j.Logger;

public class ChatComponent {
   private static final Logger LOGGER = LogUtils.getLogger();
   private static final int MAX_CHAT_HISTORY = 100;
   private static final int MESSAGE_INDENT = 4;
   private static final int BOTTOM_MARGIN = 40;
   private static final int TOOLTIP_MAX_WIDTH = 210;
   private static final int TIME_BEFORE_MESSAGE_DELETION = 60;
   private static final Component DELETED_CHAT_MESSAGE;
   public static final int MESSAGE_BOTTOM_TO_MESSAGE_TOP = 8;
   public static final Identifier QUEUE_EXPAND_ID;
   private static final Style QUEUE_EXPAND_TEXT_STYLE;
   final Minecraft minecraft;
   private final ArrayListDeque<String> recentChat = new ArrayListDeque<String>(100);
   private final List<GuiMessage> allMessages = Lists.newArrayList();
   private final List<GuiMessage.Line> trimmedMessages = Lists.newArrayList();
   private int chatScrollbarPos;
   private boolean newMessageSinceScroll;
   private @Nullable Draft latestDraft;
   private @Nullable ChatScreen preservedScreen;
   private final List<DelayedMessageDeletion> messageDeletionQueue = new ArrayList();

   public ChatComponent(Minecraft var1) {
      super();
      this.minecraft = var1;
      this.recentChat.addAll(var1.commandHistory().history());
   }

   public void tick() {
      if (!this.messageDeletionQueue.isEmpty()) {
         this.processMessageDeletionQueue();
      }

   }

   private int forEachLine(AlphaCalculator var1, LineConsumer var2) {
      int var3 = this.getLinesPerPage();
      int var4 = 0;

      for(int var5 = Math.min(this.trimmedMessages.size() - this.chatScrollbarPos, var3) - 1; var5 >= 0; --var5) {
         int var6 = var5 + this.chatScrollbarPos;
         GuiMessage.Line var7 = (GuiMessage.Line)this.trimmedMessages.get(var6);
         float var8 = var1.calculate(var7);
         if (var8 > 1.0E-5F) {
            ++var4;
            var2.accept(var7, var5, var8);
         }
      }

      return var4;
   }

   public void render(GuiGraphics var1, Font var2, int var3, int var4, int var5, boolean var6) {
      var1.pose().pushMatrix();
      this.render((ChatGraphicsAccess)(var6 ? new DrawingFocusedGraphicsAccess(var1, var2, var4, var5) : new DrawingBackgroundGraphicsAccess(var1)), var1.guiHeight(), var3, var6);
      var1.pose().popMatrix();
   }

   public void captureClickableText(ActiveTextCollector var1, int var2, int var3, boolean var4) {
      this.render(new ClickableTextOnlyGraphicsAccess(var1), var2, var3, var4);
   }

   private void render(final ChatGraphicsAccess var1, int var2, int var3, boolean var4) {
      if (!this.isChatHidden()) {
         int var5 = this.trimmedMessages.size();
         if (var5 > 0) {
            ProfilerFiller var6 = Profiler.get();
            var6.push("chat");
            float var7 = (float)this.getScale();
            int var8 = Mth.ceil((float)this.getWidth() / var7);
            final int var9 = Mth.floor((float)(var2 - 40) / var7);
            final float var10 = ((Double)this.minecraft.options.chatOpacity().get()).floatValue() * 0.9F + 0.1F;
            float var11 = ((Double)this.minecraft.options.textBackgroundOpacity().get()).floatValue();
            Objects.requireNonNull(this.minecraft.font);
            final byte var12 = 9;
            boolean var13 = true;
            double var14 = (Double)this.minecraft.options.chatLineSpacing().get();
            final int var16 = (int)((double)var12 * (var14 + 1.0));
            final int var17 = (int)Math.round(8.0 * (var14 + 1.0) - 4.0 * var14);
            long var18 = this.minecraft.getChatListener().queueSize();
            AlphaCalculator var20 = var4 ? ChatComponent.AlphaCalculator.FULLY_VISIBLE : ChatComponent.AlphaCalculator.timeBased(var3);
            var1.updatePose((var1x) -> {
               var1x.scale(var7, var7);
               var1x.translate(4.0F, 0.0F);
            });
            this.forEachLine(var20, (var5x, var6x, var7x) -> {
               int var8x = var9 - var6x * var16;
               int var9x = var8x - var16;
               var1.fill(-4, var9x, var8 + 4 + 4, var8x, ARGB.black(var7x * var11));
            });
            if (var18 > 0L) {
               var1.fill(-2, var9, var8 + 4, var9 + var12, ARGB.black(var11));
            }

            int var21 = this.forEachLine(var20, new LineConsumer() {
               boolean hoveredOverCurrentMessage;

               public void accept(GuiMessage.Line var1x, int var2, float var3) {
                  int var4 = var9 - var2 * var16;
                  int var5 = var4 - var16;
                  int var6 = var4 - var17;
                  boolean var7 = var1.handleMessage(var6, var3 * var10, var1x.content());
                  this.hoveredOverCurrentMessage |= var7;
                  boolean var8;
                  if (var1x.endOfEntry()) {
                     var8 = this.hoveredOverCurrentMessage;
                     this.hoveredOverCurrentMessage = false;
                  } else {
                     var8 = false;
                  }

                  GuiMessageTag var9x = var1x.tag();
                  if (var9x != null) {
                     var1.handleTag(-4, var5, -2, var4, var3 * var10, var9x);
                     if (var9x.icon() != null) {
                        int var10x = var1x.getTagIconLeft(ChatComponent.this.minecraft.font);
                        int var11 = var6 + var12;
                        var1.handleTagIcon(var10x, var11, var8, var9x, var9x.icon());
                     }
                  }

               }
            });
            if (var18 > 0L) {
               int var22 = var9 + var12;
               MutableComponent var23 = Component.translatable("chat.queue", var18).setStyle(QUEUE_EXPAND_TEXT_STYLE);
               var1.handleMessage(var22 - 8, 0.5F * var10, var23.getVisualOrderText());
            }

            if (var4) {
               int var29 = var5 * var16;
               int var30 = var21 * var16;
               int var24 = this.chatScrollbarPos * var30 / var5 - var9;
               int var25 = var30 * var30 / var29;
               if (var29 != var30) {
                  int var26 = var24 > 0 ? 170 : 96;
                  int var27 = this.newMessageSinceScroll ? 13382451 : 3355562;
                  int var28 = var8 + 4;
                  var1.fill(var28, -var24, var28 + 2, -var24 - var25, ARGB.color(var26, var27));
                  var1.fill(var28 + 2, -var24, var28 + 1, -var24 - var25, ARGB.color(var26, 13421772));
               }
            }

            var6.pop();
         }
      }
   }

   private boolean isChatHidden() {
      return this.minecraft.options.chatVisibility().get() == ChatVisiblity.HIDDEN;
   }

   public void clearMessages(boolean var1) {
      this.minecraft.getChatListener().flushQueue();
      this.messageDeletionQueue.clear();
      this.trimmedMessages.clear();
      this.allMessages.clear();
      if (var1) {
         this.recentChat.clear();
         this.recentChat.addAll(this.minecraft.commandHistory().history());
      }

   }

   public void addMessage(Component var1) {
      this.addMessage(var1, (MessageSignature)null, this.minecraft.isSingleplayer() ? GuiMessageTag.systemSinglePlayer() : GuiMessageTag.system());
   }

   public void addMessage(Component var1, @Nullable MessageSignature var2, @Nullable GuiMessageTag var3) {
      GuiMessage var4 = new GuiMessage(this.minecraft.gui.getGuiTicks(), var1, var2, var3);
      this.logChatMessage(var4);
      this.addMessageToDisplayQueue(var4);
      this.addMessageToQueue(var4);
   }

   private void logChatMessage(GuiMessage var1) {
      String var2 = var1.content().getString().replaceAll("\r", "\\\\r").replaceAll("\n", "\\\\n");
      String var3 = (String)Optionull.map(var1.tag(), GuiMessageTag::logTag);
      if (var3 != null) {
         LOGGER.info("[{}] [CHAT] {}", var3, var2);
      } else {
         LOGGER.info("[CHAT] {}", var2);
      }

   }

   private void addMessageToDisplayQueue(GuiMessage var1) {
      int var2 = Mth.floor((double)this.getWidth() / this.getScale());
      List var3 = var1.splitLines(this.minecraft.font, var2);
      boolean var4 = this.isChatFocused();

      for(int var5 = 0; var5 < var3.size(); ++var5) {
         FormattedCharSequence var6 = (FormattedCharSequence)var3.get(var5);
         if (var4 && this.chatScrollbarPos > 0) {
            this.newMessageSinceScroll = true;
            this.scrollChat(1);
         }

         boolean var7 = var5 == var3.size() - 1;
         this.trimmedMessages.addFirst(new GuiMessage.Line(var1.addedTime(), var6, var1.tag(), var7));
      }

      while(this.trimmedMessages.size() > 100) {
         this.trimmedMessages.removeLast();
      }

   }

   private void addMessageToQueue(GuiMessage var1) {
      this.allMessages.addFirst(var1);

      while(this.allMessages.size() > 100) {
         this.allMessages.removeLast();
      }

   }

   private void processMessageDeletionQueue() {
      int var1 = this.minecraft.gui.getGuiTicks();
      this.messageDeletionQueue.removeIf((var2) -> {
         if (var1 >= var2.deletableAfter()) {
            return this.deleteMessageOrDelay(var2.signature()) == null;
         } else {
            return false;
         }
      });
   }

   public void deleteMessage(MessageSignature var1) {
      DelayedMessageDeletion var2 = this.deleteMessageOrDelay(var1);
      if (var2 != null) {
         this.messageDeletionQueue.add(var2);
      }

   }

   private @Nullable DelayedMessageDeletion deleteMessageOrDelay(MessageSignature var1) {
      int var2 = this.minecraft.gui.getGuiTicks();
      ListIterator var3 = this.allMessages.listIterator();

      while(var3.hasNext()) {
         GuiMessage var4 = (GuiMessage)var3.next();
         if (var1.equals(var4.signature())) {
            int var5 = var4.addedTime() + 60;
            if (var2 >= var5) {
               var3.set(this.createDeletedMarker(var4));
               this.refreshTrimmedMessages();
               return null;
            }

            return new DelayedMessageDeletion(var1, var5);
         }
      }

      return null;
   }

   private GuiMessage createDeletedMarker(GuiMessage var1) {
      return new GuiMessage(var1.addedTime(), DELETED_CHAT_MESSAGE, (MessageSignature)null, GuiMessageTag.system());
   }

   public void rescaleChat() {
      this.resetChatScroll();
      this.refreshTrimmedMessages();
   }

   private void refreshTrimmedMessages() {
      this.trimmedMessages.clear();

      for(GuiMessage var2 : Lists.reverse(this.allMessages)) {
         this.addMessageToDisplayQueue(var2);
      }

   }

   public ArrayListDeque<String> getRecentChat() {
      return this.recentChat;
   }

   public void addRecentChat(String var1) {
      if (!var1.equals(this.recentChat.peekLast())) {
         if (this.recentChat.size() >= 100) {
            this.recentChat.removeFirst();
         }

         this.recentChat.addLast(var1);
      }

      if (var1.startsWith("/")) {
         this.minecraft.commandHistory().addCommand(var1);
      }

   }

   public void resetChatScroll() {
      this.chatScrollbarPos = 0;
      this.newMessageSinceScroll = false;
   }

   public void scrollChat(int var1) {
      this.chatScrollbarPos += var1;
      int var2 = this.trimmedMessages.size();
      if (this.chatScrollbarPos > var2 - this.getLinesPerPage()) {
         this.chatScrollbarPos = var2 - this.getLinesPerPage();
      }

      if (this.chatScrollbarPos <= 0) {
         this.chatScrollbarPos = 0;
         this.newMessageSinceScroll = false;
      }

   }

   public boolean isChatFocused() {
      return this.minecraft.screen instanceof ChatScreen;
   }

   private int getWidth() {
      return getWidth((Double)this.minecraft.options.chatWidth().get());
   }

   private int getHeight() {
      return getHeight(this.isChatFocused() ? (Double)this.minecraft.options.chatHeightFocused().get() : (Double)this.minecraft.options.chatHeightUnfocused().get());
   }

   private double getScale() {
      return (Double)this.minecraft.options.chatScale().get();
   }

   public static int getWidth(double var0) {
      boolean var2 = true;
      boolean var3 = true;
      return Mth.floor(var0 * 280.0 + 40.0);
   }

   public static int getHeight(double var0) {
      boolean var2 = true;
      boolean var3 = true;
      return Mth.floor(var0 * 160.0 + 20.0);
   }

   public static double defaultUnfocusedPct() {
      boolean var0 = true;
      boolean var1 = true;
      return 70.0 / (double)(getHeight(1.0) - 20);
   }

   public int getLinesPerPage() {
      return this.getHeight() / this.getLineHeight();
   }

   private int getLineHeight() {
      Objects.requireNonNull(this.minecraft.font);
      return (int)(9.0 * ((Double)this.minecraft.options.chatLineSpacing().get() + 1.0));
   }

   public void saveAsDraft(String var1) {
      boolean var2 = var1.startsWith("/");
      this.latestDraft = new Draft(var1, var2 ? ChatComponent.ChatMethod.COMMAND : ChatComponent.ChatMethod.MESSAGE);
   }

   public void discardDraft() {
      this.latestDraft = null;
   }

   public <T extends ChatScreen> T createScreen(ChatMethod var1, ChatScreen.ChatConstructor<T> var2) {
      return (T)(this.latestDraft != null && var1.isDraftRestorable(this.latestDraft) ? var2.create(this.latestDraft.text(), true) : var2.create(var1.prefix(), false));
   }

   public void openScreen(ChatMethod var1, ChatScreen.ChatConstructor<?> var2) {
      this.minecraft.setScreen(this.createScreen(var1, var2));
   }

   public void preserveCurrentChatScreen() {
      Screen var2 = this.minecraft.screen;
      if (var2 instanceof ChatScreen var1) {
         this.preservedScreen = var1;
      }

   }

   public @Nullable ChatScreen restoreChatScreen() {
      ChatScreen var1 = this.preservedScreen;
      this.preservedScreen = null;
      return var1;
   }

   public State storeState() {
      return new State(List.copyOf(this.allMessages), List.copyOf(this.recentChat), List.copyOf(this.messageDeletionQueue));
   }

   public void restoreState(State var1) {
      this.recentChat.clear();
      this.recentChat.addAll(var1.history);
      this.messageDeletionQueue.clear();
      this.messageDeletionQueue.addAll(var1.delayedMessageDeletions);
      this.allMessages.clear();
      this.allMessages.addAll(var1.messages);
      this.refreshTrimmedMessages();
   }

   static {
      DELETED_CHAT_MESSAGE = Component.translatable("chat.deleted_marker").withStyle(ChatFormatting.GRAY, ChatFormatting.ITALIC);
      QUEUE_EXPAND_ID = Identifier.withDefaultNamespace("internal/expand_chat_queue");
      QUEUE_EXPAND_TEXT_STYLE = Style.EMPTY.withClickEvent(new ClickEvent.Custom(QUEUE_EXPAND_ID, Optional.empty())).withHoverEvent(new HoverEvent.ShowText(Component.translatable("chat.queue.tooltip")));
   }

   static record DelayedMessageDeletion(MessageSignature signature, int deletableAfter) {
      DelayedMessageDeletion(MessageSignature var1, int var2) {
         super();
         this.signature = var1;
         this.deletableAfter = var2;
      }
   }

   public static class State {
      final List<GuiMessage> messages;
      final List<String> history;
      final List<DelayedMessageDeletion> delayedMessageDeletions;

      public State(List<GuiMessage> var1, List<String> var2, List<DelayedMessageDeletion> var3) {
         super();
         this.messages = var1;
         this.history = var2;
         this.delayedMessageDeletions = var3;
      }
   }

   public static record Draft(String text, ChatMethod chatMethod) {
      final ChatMethod chatMethod;

      public Draft(String var1, ChatMethod var2) {
         super();
         this.text = var1;
         this.chatMethod = var2;
      }
   }

   public static enum ChatMethod {
      MESSAGE("") {
         public boolean isDraftRestorable(Draft var1) {
            return true;
         }
      },
      COMMAND("/") {
         public boolean isDraftRestorable(Draft var1) {
            return this == var1.chatMethod;
         }
      };

      private final String prefix;

      ChatMethod(final String var3) {
         this.prefix = var3;
      }

      public String prefix() {
         return this.prefix;
      }

      public abstract boolean isDraftRestorable(Draft var1);

      // $FF: synthetic method
      private static ChatMethod[] $values() {
         return new ChatMethod[]{MESSAGE, COMMAND};
      }
   }

   @FunctionalInterface
   interface AlphaCalculator {
      AlphaCalculator FULLY_VISIBLE = (var0) -> 1.0F;

      static AlphaCalculator timeBased(int var0) {
         return (var1) -> {
            int var2 = var0 - var1.addedTime();
            double var3 = (double)var2 / 200.0;
            var3 = 1.0 - var3;
            var3 *= 10.0;
            var3 = Mth.clamp(var3, 0.0, 1.0);
            var3 *= var3;
            return (float)var3;
         };
      }

      float calculate(GuiMessage.Line var1);
   }

   static class DrawingBackgroundGraphicsAccess implements ChatGraphicsAccess {
      private final GuiGraphics graphics;
      private final ActiveTextCollector textRenderer;
      private ActiveTextCollector.Parameters parameters;

      public DrawingBackgroundGraphicsAccess(GuiGraphics var1) {
         super();
         this.graphics = var1;
         this.textRenderer = var1.textRenderer(GuiGraphics.HoveredTextEffects.NONE, (Consumer)null);
         this.parameters = this.textRenderer.defaultParameters();
      }

      public void updatePose(Consumer<Matrix3x2f> var1) {
         var1.accept(this.graphics.pose());
         this.parameters = this.parameters.withPose(new Matrix3x2f(this.graphics.pose()));
      }

      public void fill(int var1, int var2, int var3, int var4, int var5) {
         this.graphics.fill(var1, var2, var3, var4, var5);
      }

      public boolean handleMessage(int var1, float var2, FormattedCharSequence var3) {
         this.textRenderer.accept(TextAlignment.LEFT, 0, var1, this.parameters.withOpacity(var2), (FormattedCharSequence)var3);
         return false;
      }

      public void handleTag(int var1, int var2, int var3, int var4, float var5, GuiMessageTag var6) {
         int var7 = ARGB.color(var5, var6.indicatorColor());
         this.graphics.fill(var1, var2, var3, var4, var7);
      }

      public void handleTagIcon(int var1, int var2, boolean var3, GuiMessageTag var4, GuiMessageTag.Icon var5) {
      }
   }

   static class DrawingFocusedGraphicsAccess implements ChatGraphicsAccess, Consumer<Style> {
      private final GuiGraphics graphics;
      private final Font font;
      private final ActiveTextCollector textRenderer;
      private ActiveTextCollector.Parameters parameters;
      private final int globalMouseX;
      private final int globalMouseY;
      private final Vector2f localMousePos = new Vector2f();
      private @Nullable Style hoveredStyle;

      public DrawingFocusedGraphicsAccess(GuiGraphics var1, Font var2, int var3, int var4) {
         super();
         this.graphics = var1;
         this.font = var2;
         this.textRenderer = var1.textRenderer(GuiGraphics.HoveredTextEffects.TOOLTIP_AND_CURSOR, this);
         this.globalMouseX = var3;
         this.globalMouseY = var4;
         this.parameters = this.textRenderer.defaultParameters();
         this.updateLocalMousePos();
      }

      private void updateLocalMousePos() {
         this.graphics.pose().invert(new Matrix3x2f()).transformPosition((float)this.globalMouseX, (float)this.globalMouseY, this.localMousePos);
      }

      public void updatePose(Consumer<Matrix3x2f> var1) {
         var1.accept(this.graphics.pose());
         this.parameters = this.parameters.withPose(new Matrix3x2f(this.graphics.pose()));
         this.updateLocalMousePos();
      }

      public void fill(int var1, int var2, int var3, int var4, int var5) {
         this.graphics.fill(var1, var2, var3, var4, var5);
      }

      public void accept(Style var1) {
         this.hoveredStyle = var1;
      }

      public boolean handleMessage(int var1, float var2, FormattedCharSequence var3) {
         this.hoveredStyle = null;
         this.textRenderer.accept(TextAlignment.LEFT, 0, var1, this.parameters.withOpacity(var2), (FormattedCharSequence)var3);
         return this.hoveredStyle != null;
      }

      private boolean isMouseOver(int var1, int var2, int var3, int var4) {
         return ActiveTextCollector.isPointInRectangle(this.localMousePos.x, this.localMousePos.y, (float)var1, (float)var2, (float)var3, (float)var4);
      }

      public void handleTag(int var1, int var2, int var3, int var4, float var5, GuiMessageTag var6) {
         int var7 = ARGB.color(var5, var6.indicatorColor());
         this.graphics.fill(var1, var2, var3, var4, var7);
         if (this.isMouseOver(var1, var2, var3, var4)) {
            this.showTooltip(var6);
         }

      }

      public void handleTagIcon(int var1, int var2, boolean var3, GuiMessageTag var4, GuiMessageTag.Icon var5) {
         int var6 = var2 - var5.height - 1;
         int var7 = var1 + var5.width;
         boolean var8 = this.isMouseOver(var1, var6, var7, var2);
         if (var8) {
            this.showTooltip(var4);
         }

         if (var3 || var8) {
            var5.draw(this.graphics, var1, var6);
         }

      }

      private void showTooltip(GuiMessageTag var1) {
         if (var1.text() != null) {
            this.graphics.setTooltipForNextFrame(this.font, this.font.split(var1.text(), 210), this.globalMouseX, this.globalMouseY);
         }

      }

      // $FF: synthetic method
      public void accept(final Object var1) {
         this.accept((Style)var1);
      }
   }

   static class ClickableTextOnlyGraphicsAccess implements ChatGraphicsAccess {
      private final ActiveTextCollector output;

      public ClickableTextOnlyGraphicsAccess(ActiveTextCollector var1) {
         super();
         this.output = var1;
      }

      public void updatePose(Consumer<Matrix3x2f> var1) {
         ActiveTextCollector.Parameters var2 = this.output.defaultParameters();
         Matrix3x2f var3 = new Matrix3x2f(var2.pose());
         var1.accept(var3);
         this.output.defaultParameters(var2.withPose(var3));
      }

      public void fill(int var1, int var2, int var3, int var4, int var5) {
      }

      public boolean handleMessage(int var1, float var2, FormattedCharSequence var3) {
         this.output.accept(TextAlignment.LEFT, 0, var1, (FormattedCharSequence)var3);
         return false;
      }

      public void handleTag(int var1, int var2, int var3, int var4, float var5, GuiMessageTag var6) {
      }

      public void handleTagIcon(int var1, int var2, boolean var3, GuiMessageTag var4, GuiMessageTag.Icon var5) {
      }
   }

   public interface ChatGraphicsAccess {
      void updatePose(Consumer<Matrix3x2f> var1);

      void fill(int var1, int var2, int var3, int var4, int var5);

      boolean handleMessage(int var1, float var2, FormattedCharSequence var3);

      void handleTag(int var1, int var2, int var3, int var4, float var5, GuiMessageTag var6);

      void handleTagIcon(int var1, int var2, boolean var3, GuiMessageTag var4, GuiMessageTag.Icon var5);
   }

   @FunctionalInterface
   interface LineConsumer {
      void accept(GuiMessage.Line var1, int var2, float var3);
   }
}
