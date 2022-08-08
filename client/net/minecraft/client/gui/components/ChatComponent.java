package net.minecraft.client.gui.components;

import com.google.common.collect.Lists;
import com.google.common.collect.Queues;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.logging.LogUtils;
import java.util.Deque;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import javax.annotation.Nullable;
import net.minecraft.client.GuiMessage;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.gui.screens.ChatScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.ChatVisiblity;
import org.slf4j.Logger;

public class ChatComponent extends GuiComponent {
   private static final Logger LOGGER = LogUtils.getLogger();
   private static final int MAX_CHAT_HISTORY = 100;
   private final Minecraft minecraft;
   private final List<String> recentChat = Lists.newArrayList();
   private final List<GuiMessage<Component>> allMessages = Lists.newArrayList();
   private final List<GuiMessage<FormattedCharSequence>> trimmedMessages = Lists.newArrayList();
   private final Deque<Component> chatQueue = Queues.newArrayDeque();
   private int chatScrollbarPos;
   private boolean newMessageSinceScroll;
   private long lastMessage;

   public ChatComponent(Minecraft var1) {
      super();
      this.minecraft = var1;
   }

   public void render(PoseStack var1, int var2) {
      if (!this.isChatHidden()) {
         this.processPendingMessages();
         int var3 = this.getLinesPerPage();
         int var4 = this.trimmedMessages.size();
         if (var4 > 0) {
            boolean var5 = this.isChatFocused();
            float var6 = (float)this.getScale();
            int var7 = Mth.ceil((float)this.getWidth() / var6);
            var1.pushPose();
            var1.translate(4.0, 8.0, 0.0);
            var1.scale(var6, var6, 1.0F);
            double var8 = (Double)this.minecraft.options.chatOpacity().get() * 0.8999999761581421 + 0.10000000149011612;
            double var10 = (Double)this.minecraft.options.textBackgroundOpacity().get();
            double var12 = (Double)this.minecraft.options.chatLineSpacing().get();
            double var14 = 9.0 * (var12 + 1.0);
            double var16 = -8.0 * (var12 + 1.0) + 4.0 * var12;
            int var18 = 0;

            int var19;
            int var21;
            int var24;
            int var25;
            for(var19 = 0; var19 + this.chatScrollbarPos < this.trimmedMessages.size() && var19 < var3; ++var19) {
               GuiMessage var20 = (GuiMessage)this.trimmedMessages.get(var19 + this.chatScrollbarPos);
               if (var20 != null) {
                  var21 = var2 - var20.getAddedTime();
                  if (var21 < 200 || var5) {
                     double var22 = var5 ? 1.0 : getTimeFactor(var21);
                     var24 = (int)(255.0 * var22 * var8);
                     var25 = (int)(255.0 * var22 * var10);
                     ++var18;
                     if (var24 > 3) {
                        boolean var26 = false;
                        double var27 = (double)(-var19) * var14;
                        var1.pushPose();
                        var1.translate(0.0, 0.0, 50.0);
                        fill(var1, -4, (int)(var27 - var14), 0 + var7 + 4, (int)var27, var25 << 24);
                        RenderSystem.enableBlend();
                        var1.translate(0.0, 0.0, 50.0);
                        this.minecraft.font.drawShadow(var1, (FormattedCharSequence)var20.getMessage(), 0.0F, (float)((int)(var27 + var16)), 16777215 + (var24 << 24));
                        RenderSystem.disableBlend();
                        var1.popPose();
                     }
                  }
               }
            }

            int var30;
            if (!this.chatQueue.isEmpty()) {
               var19 = (int)(128.0 * var8);
               var30 = (int)(255.0 * var10);
               var1.pushPose();
               var1.translate(0.0, 0.0, 50.0);
               fill(var1, -2, 0, var7 + 4, 9, var30 << 24);
               RenderSystem.enableBlend();
               var1.translate(0.0, 0.0, 50.0);
               this.minecraft.font.drawShadow(var1, (Component)Component.translatable("chat.queue", this.chatQueue.size()), 0.0F, 1.0F, 16777215 + (var19 << 24));
               var1.popPose();
               RenderSystem.disableBlend();
            }

            if (var5) {
               Objects.requireNonNull(this.minecraft.font);
               byte var29 = 9;
               var30 = var4 * var29;
               var21 = var18 * var29;
               int var31 = this.chatScrollbarPos * var21 / var4;
               int var23 = var21 * var21 / var30;
               if (var30 != var21) {
                  var24 = var31 > 0 ? 170 : 96;
                  var25 = this.newMessageSinceScroll ? 13382451 : 3355562;
                  var1.translate(-4.0, 0.0, 0.0);
                  fill(var1, 0, -var31, 2, -var31 - var23, var25 + (var24 << 24));
                  fill(var1, 2, -var31, 1, -var31 - var23, 13421772 + (var24 << 24));
               }
            }

            var1.popPose();
         }
      }
   }

   private boolean isChatHidden() {
      return this.minecraft.options.chatVisibility().get() == ChatVisiblity.HIDDEN;
   }

   private static double getTimeFactor(int var0) {
      double var1 = (double)var0 / 200.0;
      var1 = 1.0 - var1;
      var1 *= 10.0;
      var1 = Mth.clamp(var1, 0.0, 1.0);
      var1 *= var1;
      return var1;
   }

   public void clearMessages(boolean var1) {
      this.chatQueue.clear();
      this.trimmedMessages.clear();
      this.allMessages.clear();
      if (var1) {
         this.recentChat.clear();
      }

   }

   public void addMessage(Component var1) {
      this.addMessage(var1, 0);
   }

   private void addMessage(Component var1, int var2) {
      this.addMessage(var1, var2, this.minecraft.gui.getGuiTicks(), false);
      LOGGER.info("[CHAT] {}", var1.getString().replaceAll("\r", "\\\\r").replaceAll("\n", "\\\\n"));
   }

   private void addMessage(Component var1, int var2, int var3, boolean var4) {
      if (var2 != 0) {
         this.removeById(var2);
      }

      int var5 = Mth.floor((double)this.getWidth() / this.getScale());
      List var6 = ComponentRenderUtils.wrapComponents(var1, var5, this.minecraft.font);
      boolean var7 = this.isChatFocused();

      FormattedCharSequence var9;
      for(Iterator var8 = var6.iterator(); var8.hasNext(); this.trimmedMessages.add(0, new GuiMessage(var3, var9, var2))) {
         var9 = (FormattedCharSequence)var8.next();
         if (var7 && this.chatScrollbarPos > 0) {
            this.newMessageSinceScroll = true;
            this.scrollChat(1);
         }
      }

      while(this.trimmedMessages.size() > 100) {
         this.trimmedMessages.remove(this.trimmedMessages.size() - 1);
      }

      if (!var4) {
         this.allMessages.add(0, new GuiMessage(var3, var1, var2));

         while(this.allMessages.size() > 100) {
            this.allMessages.remove(this.allMessages.size() - 1);
         }
      }

   }

   public void rescaleChat() {
      this.trimmedMessages.clear();
      this.resetChatScroll();

      for(int var1 = this.allMessages.size() - 1; var1 >= 0; --var1) {
         GuiMessage var2 = (GuiMessage)this.allMessages.get(var1);
         this.addMessage((Component)var2.getMessage(), var2.getId(), var2.getAddedTime(), true);
      }

   }

   public List<String> getRecentChat() {
      return this.recentChat;
   }

   public void addRecentChat(String var1) {
      if (this.recentChat.isEmpty() || !((String)this.recentChat.get(this.recentChat.size() - 1)).equals(var1)) {
         this.recentChat.add(var1);
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

   public boolean handleChatQueueClicked(double var1, double var3) {
      if (this.isChatFocused() && !this.minecraft.options.hideGui && !this.isChatHidden() && !this.chatQueue.isEmpty()) {
         double var5 = var1 - 2.0;
         double var7 = (double)this.minecraft.getWindow().getGuiScaledHeight() - var3 - 40.0;
         if (var5 <= (double)Mth.floor((double)this.getWidth() / this.getScale()) && var7 < 0.0 && var7 > (double)Mth.floor(-9.0 * this.getScale())) {
            this.addMessage((Component)this.chatQueue.remove());
            this.lastMessage = System.currentTimeMillis();
            return true;
         } else {
            return false;
         }
      } else {
         return false;
      }
   }

   @Nullable
   public Style getClickedComponentStyleAt(double var1, double var3) {
      if (this.isChatFocused() && !this.minecraft.options.hideGui && !this.isChatHidden()) {
         double var5 = var1 - 2.0;
         double var7 = (double)this.minecraft.getWindow().getGuiScaledHeight() - var3 - 40.0;
         var5 = (double)Mth.floor(var5 / this.getScale());
         var7 = (double)Mth.floor(var7 / (this.getScale() * ((Double)this.minecraft.options.chatLineSpacing().get() + 1.0)));
         if (!(var5 < 0.0) && !(var7 < 0.0)) {
            int var9 = Math.min(this.getLinesPerPage(), this.trimmedMessages.size());
            if (var5 <= (double)Mth.floor((double)this.getWidth() / this.getScale())) {
               Objects.requireNonNull(this.minecraft.font);
               if (var7 < (double)(9 * var9 + var9)) {
                  Objects.requireNonNull(this.minecraft.font);
                  int var10 = (int)(var7 / 9.0 + (double)this.chatScrollbarPos);
                  if (var10 >= 0 && var10 < this.trimmedMessages.size()) {
                     GuiMessage var11 = (GuiMessage)this.trimmedMessages.get(var10);
                     return this.minecraft.font.getSplitter().componentStyleAtWidth((FormattedCharSequence)var11.getMessage(), (int)var5);
                  }
               }
            }

            return null;
         } else {
            return null;
         }
      } else {
         return null;
      }
   }

   @Nullable
   public ChatScreen getFocusedChat() {
      Screen var2 = this.minecraft.screen;
      if (var2 instanceof ChatScreen var1) {
         return var1;
      } else {
         return null;
      }
   }

   private boolean isChatFocused() {
      return this.getFocusedChat() != null;
   }

   private void removeById(int var1) {
      this.trimmedMessages.removeIf((var1x) -> {
         return var1x.getId() == var1;
      });
      this.allMessages.removeIf((var1x) -> {
         return var1x.getId() == var1;
      });
   }

   public int getWidth() {
      return getWidth((Double)this.minecraft.options.chatWidth().get());
   }

   public int getHeight() {
      return getHeight(this.isChatFocused() ? (Double)this.minecraft.options.chatHeightFocused().get() : (Double)this.minecraft.options.chatHeightUnfocused().get() / ((Double)this.minecraft.options.chatLineSpacing().get() + 1.0));
   }

   public double getScale() {
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
      return this.getHeight() / 9;
   }

   private long getChatRateMillis() {
      return (long)((Double)this.minecraft.options.chatDelay().get() * 1000.0);
   }

   private void processPendingMessages() {
      if (!this.chatQueue.isEmpty()) {
         long var1 = System.currentTimeMillis();
         if (var1 - this.lastMessage >= this.getChatRateMillis()) {
            this.addMessage((Component)this.chatQueue.remove());
            this.lastMessage = var1;
         }

      }
   }

   public void enqueueMessage(Component var1) {
      if ((Double)this.minecraft.options.chatDelay().get() <= 0.0) {
         this.addMessage(var1);
      } else {
         long var2 = System.currentTimeMillis();
         if (var2 - this.lastMessage >= this.getChatRateMillis()) {
            this.addMessage(var1);
            this.lastMessage = var2;
         } else {
            this.chatQueue.add(var1);
         }
      }

   }
}
