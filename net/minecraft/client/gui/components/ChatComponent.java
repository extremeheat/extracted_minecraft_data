package net.minecraft.client.gui.components;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.math.Matrix4f;
import java.util.Iterator;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.client.GuiMessage;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.gui.screens.ChatScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.ChatVisiblity;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ChatComponent extends GuiComponent {
   private static final Logger LOGGER = LogManager.getLogger();
   private final Minecraft minecraft;
   private final List recentChat = Lists.newArrayList();
   private final List allMessages = Lists.newArrayList();
   private final List trimmedMessages = Lists.newArrayList();
   private int chatScrollbarPos;
   private boolean newMessageSinceScroll;

   public ChatComponent(Minecraft var1) {
      this.minecraft = var1;
   }

   public void render(int var1) {
      if (this.isChatVisible()) {
         int var2 = this.getLinesPerPage();
         int var3 = this.trimmedMessages.size();
         if (var3 > 0) {
            boolean var4 = false;
            if (this.isChatFocused()) {
               var4 = true;
            }

            double var5 = this.getScale();
            int var7 = Mth.ceil((double)this.getWidth() / var5);
            RenderSystem.pushMatrix();
            RenderSystem.translatef(2.0F, 8.0F, 0.0F);
            RenderSystem.scaled(var5, var5, 1.0D);
            double var8 = this.minecraft.options.chatOpacity * 0.8999999761581421D + 0.10000000149011612D;
            double var10 = this.minecraft.options.textBackgroundOpacity;
            int var12 = 0;
            Matrix4f var13 = Matrix4f.createTranslateMatrix(0.0F, 0.0F, -100.0F);

            int var16;
            int var19;
            int var20;
            for(int var14 = 0; var14 + this.chatScrollbarPos < this.trimmedMessages.size() && var14 < var2; ++var14) {
               GuiMessage var15 = (GuiMessage)this.trimmedMessages.get(var14 + this.chatScrollbarPos);
               if (var15 != null) {
                  var16 = var1 - var15.getAddedTime();
                  if (var16 < 200 || var4) {
                     double var17 = var4 ? 1.0D : getTimeFactor(var16);
                     var19 = (int)(255.0D * var17 * var8);
                     var20 = (int)(255.0D * var17 * var10);
                     ++var12;
                     if (var19 > 3) {
                        boolean var21 = false;
                        int var22 = -var14 * 9;
                        fill(var13, -2, var22 - 9, 0 + var7 + 4, var22, var20 << 24);
                        String var23 = var15.getMessage().getColoredString();
                        RenderSystem.enableBlend();
                        this.minecraft.font.drawShadow(var23, 0.0F, (float)(var22 - 8), 16777215 + (var19 << 24));
                        RenderSystem.disableAlphaTest();
                        RenderSystem.disableBlend();
                     }
                  }
               }
            }

            if (var4) {
               this.minecraft.font.getClass();
               byte var24 = 9;
               RenderSystem.translatef(-3.0F, 0.0F, 0.0F);
               int var25 = var3 * var24 + var3;
               var16 = var12 * var24 + var12;
               int var26 = this.chatScrollbarPos * var16 / var3;
               int var18 = var16 * var16 / var25;
               if (var25 != var16) {
                  var19 = var26 > 0 ? 170 : 96;
                  var20 = this.newMessageSinceScroll ? 13382451 : 3355562;
                  fill(0, -var26, 2, -var26 - var18, var20 + (var19 << 24));
                  fill(2, -var26, 1, -var26 - var18, 13421772 + (var19 << 24));
               }
            }

            RenderSystem.popMatrix();
         }
      }
   }

   private boolean isChatVisible() {
      return this.minecraft.options.chatVisibility != ChatVisiblity.HIDDEN;
   }

   private static double getTimeFactor(int var0) {
      double var1 = (double)var0 / 200.0D;
      var1 = 1.0D - var1;
      var1 *= 10.0D;
      var1 = Mth.clamp(var1, 0.0D, 1.0D);
      var1 *= var1;
      return var1;
   }

   public void clearMessages(boolean var1) {
      this.trimmedMessages.clear();
      this.allMessages.clear();
      if (var1) {
         this.recentChat.clear();
      }

   }

   public void addMessage(Component var1) {
      this.addMessage(var1, 0);
   }

   public void addMessage(Component var1, int var2) {
      this.addMessage(var1, var2, this.minecraft.gui.getGuiTicks(), false);
      LOGGER.info("[CHAT] {}", var1.getString().replaceAll("\r", "\\\\r").replaceAll("\n", "\\\\n"));
   }

   private void addMessage(Component var1, int var2, int var3, boolean var4) {
      if (var2 != 0) {
         this.removeById(var2);
      }

      int var5 = Mth.floor((double)this.getWidth() / this.getScale());
      List var6 = ComponentRenderUtils.wrapComponents(var1, var5, this.minecraft.font, false, false);
      boolean var7 = this.isChatFocused();

      Component var9;
      for(Iterator var8 = var6.iterator(); var8.hasNext(); this.trimmedMessages.add(0, new GuiMessage(var3, var9, var2))) {
         var9 = (Component)var8.next();
         if (var7 && this.chatScrollbarPos > 0) {
            this.newMessageSinceScroll = true;
            this.scrollChat(1.0D);
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
         this.addMessage(var2.getMessage(), var2.getId(), var2.getAddedTime(), true);
      }

   }

   public List getRecentChat() {
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

   public void scrollChat(double var1) {
      this.chatScrollbarPos = (int)((double)this.chatScrollbarPos + var1);
      int var3 = this.trimmedMessages.size();
      if (this.chatScrollbarPos > var3 - this.getLinesPerPage()) {
         this.chatScrollbarPos = var3 - this.getLinesPerPage();
      }

      if (this.chatScrollbarPos <= 0) {
         this.chatScrollbarPos = 0;
         this.newMessageSinceScroll = false;
      }

   }

   @Nullable
   public Component getClickedComponentAt(double var1, double var3) {
      if (this.isChatFocused() && !this.minecraft.options.hideGui && this.isChatVisible()) {
         double var5 = this.getScale();
         double var7 = var1 - 2.0D;
         double var9 = (double)this.minecraft.getWindow().getGuiScaledHeight() - var3 - 40.0D;
         var7 = (double)Mth.floor(var7 / var5);
         var9 = (double)Mth.floor(var9 / var5);
         if (var7 >= 0.0D && var9 >= 0.0D) {
            int var11 = Math.min(this.getLinesPerPage(), this.trimmedMessages.size());
            if (var7 <= (double)Mth.floor((double)this.getWidth() / this.getScale())) {
               this.minecraft.font.getClass();
               if (var9 < (double)(9 * var11 + var11)) {
                  this.minecraft.font.getClass();
                  int var12 = (int)(var9 / 9.0D + (double)this.chatScrollbarPos);
                  if (var12 >= 0 && var12 < this.trimmedMessages.size()) {
                     GuiMessage var13 = (GuiMessage)this.trimmedMessages.get(var12);
                     int var14 = 0;
                     Iterator var15 = var13.getMessage().iterator();

                     while(var15.hasNext()) {
                        Component var16 = (Component)var15.next();
                        if (var16 instanceof TextComponent) {
                           var14 += this.minecraft.font.width(ComponentRenderUtils.stripColor(((TextComponent)var16).getText(), false));
                           if ((double)var14 > var7) {
                              return var16;
                           }
                        }
                     }
                  }

                  return null;
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

   public boolean isChatFocused() {
      return this.minecraft.screen instanceof ChatScreen;
   }

   public void removeById(int var1) {
      Iterator var2 = this.trimmedMessages.iterator();

      GuiMessage var3;
      while(var2.hasNext()) {
         var3 = (GuiMessage)var2.next();
         if (var3.getId() == var1) {
            var2.remove();
         }
      }

      var2 = this.allMessages.iterator();

      while(var2.hasNext()) {
         var3 = (GuiMessage)var2.next();
         if (var3.getId() == var1) {
            var2.remove();
            break;
         }
      }

   }

   public int getWidth() {
      return getWidth(this.minecraft.options.chatWidth);
   }

   public int getHeight() {
      return getHeight(this.isChatFocused() ? this.minecraft.options.chatHeightFocused : this.minecraft.options.chatHeightUnfocused);
   }

   public double getScale() {
      return this.minecraft.options.chatScale;
   }

   public static int getWidth(double var0) {
      boolean var2 = true;
      boolean var3 = true;
      return Mth.floor(var0 * 280.0D + 40.0D);
   }

   public static int getHeight(double var0) {
      boolean var2 = true;
      boolean var3 = true;
      return Mth.floor(var0 * 160.0D + 20.0D);
   }

   public int getLinesPerPage() {
      return this.getHeight() / 9;
   }
}
