package net.minecraft.client.gui.components;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import java.util.Objects;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.gui.chat.NarratorChatListener;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.client.sounds.SoundManager;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;

public abstract class AbstractWidget extends GuiComponent implements Widget, GuiEventListener {
   public static final ResourceLocation WIDGETS_LOCATION = new ResourceLocation("textures/gui/widgets.png");
   protected int width;
   protected int height;
   public int x;
   public int y;
   private Component message;
   private boolean wasHovered;
   protected boolean isHovered;
   public boolean active = true;
   public boolean visible = true;
   protected float alpha = 1.0F;
   protected long nextNarration = 9223372036854775807L;
   private boolean focused;

   public AbstractWidget(int var1, int var2, int var3, int var4, Component var5) {
      super();
      this.x = var1;
      this.y = var2;
      this.width = var3;
      this.height = var4;
      this.message = var5;
   }

   public int getHeight() {
      return this.height;
   }

   protected int getYImage(boolean var1) {
      byte var2 = 1;
      if (!this.active) {
         var2 = 0;
      } else if (var1) {
         var2 = 2;
      }

      return var2;
   }

   public void render(PoseStack var1, int var2, int var3, float var4) {
      if (this.visible) {
         this.isHovered = var2 >= this.x && var3 >= this.y && var2 < this.x + this.width && var3 < this.y + this.height;
         if (this.wasHovered != this.isHovered()) {
            if (this.isHovered()) {
               if (this.focused) {
                  this.queueNarration(200);
               } else {
                  this.queueNarration(750);
               }
            } else {
               this.nextNarration = 9223372036854775807L;
            }
         }

         if (this.visible) {
            this.renderButton(var1, var2, var3, var4);
         }

         this.narrate();
         this.wasHovered = this.isHovered();
      }
   }

   protected void narrate() {
      if (this.active && this.isHovered() && Util.getMillis() > this.nextNarration) {
         String var1 = this.createNarrationMessage().getString();
         if (!var1.isEmpty()) {
            NarratorChatListener.INSTANCE.sayNow(var1);
            this.nextNarration = 9223372036854775807L;
         }
      }

   }

   protected MutableComponent createNarrationMessage() {
      return new TranslatableComponent("gui.narrate.button", new Object[]{this.getMessage()});
   }

   public void renderButton(PoseStack var1, int var2, int var3, float var4) {
      Minecraft var5 = Minecraft.getInstance();
      Font var6 = var5.font;
      var5.getTextureManager().bind(WIDGETS_LOCATION);
      RenderSystem.color4f(1.0F, 1.0F, 1.0F, this.alpha);
      int var7 = this.getYImage(this.isHovered());
      RenderSystem.enableBlend();
      RenderSystem.defaultBlendFunc();
      RenderSystem.enableDepthTest();
      this.blit(var1, this.x, this.y, 0, 46 + var7 * 20, this.width / 2, this.height);
      this.blit(var1, this.x + this.width / 2, this.y, 200 - this.width / 2, 46 + var7 * 20, this.width / 2, this.height);
      this.renderBg(var1, var5, var2, var3);
      int var8 = this.active ? 16777215 : 10526880;
      drawCenteredString(var1, var6, this.getMessage(), this.x + this.width / 2, this.y + (this.height - 8) / 2, var8 | Mth.ceil(this.alpha * 255.0F) << 24);
   }

   protected void renderBg(PoseStack var1, Minecraft var2, int var3, int var4) {
   }

   public void onClick(double var1, double var3) {
   }

   public void onRelease(double var1, double var3) {
   }

   protected void onDrag(double var1, double var3, double var5, double var7) {
   }

   public boolean mouseClicked(double var1, double var3, int var5) {
      if (this.active && this.visible) {
         if (this.isValidClickButton(var5)) {
            boolean var6 = this.clicked(var1, var3);
            if (var6) {
               this.playDownSound(Minecraft.getInstance().getSoundManager());
               this.onClick(var1, var3);
               return true;
            }
         }

         return false;
      } else {
         return false;
      }
   }

   public boolean mouseReleased(double var1, double var3, int var5) {
      if (this.isValidClickButton(var5)) {
         this.onRelease(var1, var3);
         return true;
      } else {
         return false;
      }
   }

   protected boolean isValidClickButton(int var1) {
      return var1 == 0;
   }

   public boolean mouseDragged(double var1, double var3, int var5, double var6, double var8) {
      if (this.isValidClickButton(var5)) {
         this.onDrag(var1, var3, var6, var8);
         return true;
      } else {
         return false;
      }
   }

   protected boolean clicked(double var1, double var3) {
      return this.active && this.visible && var1 >= (double)this.x && var3 >= (double)this.y && var1 < (double)(this.x + this.width) && var3 < (double)(this.y + this.height);
   }

   public boolean isHovered() {
      return this.isHovered || this.focused;
   }

   public boolean changeFocus(boolean var1) {
      if (this.active && this.visible) {
         this.focused = !this.focused;
         this.onFocusedChanged(this.focused);
         return this.focused;
      } else {
         return false;
      }
   }

   protected void onFocusedChanged(boolean var1) {
   }

   public boolean isMouseOver(double var1, double var3) {
      return this.active && this.visible && var1 >= (double)this.x && var3 >= (double)this.y && var1 < (double)(this.x + this.width) && var3 < (double)(this.y + this.height);
   }

   public void renderToolTip(PoseStack var1, int var2, int var3) {
   }

   public void playDownSound(SoundManager var1) {
      var1.play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1.0F));
   }

   public int getWidth() {
      return this.width;
   }

   public void setWidth(int var1) {
      this.width = var1;
   }

   public void setAlpha(float var1) {
      this.alpha = var1;
   }

   public void setMessage(Component var1) {
      if (!Objects.equals(var1.getString(), this.message.getString())) {
         this.queueNarration(250);
      }

      this.message = var1;
   }

   public void queueNarration(int var1) {
      this.nextNarration = Util.getMillis() + (long)var1;
   }

   public Component getMessage() {
      return this.message;
   }

   public boolean isFocused() {
      return this.focused;
   }

   protected void setFocused(boolean var1) {
      this.focused = var1;
   }
}
