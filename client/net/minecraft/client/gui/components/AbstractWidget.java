package net.minecraft.client.gui.components;

import com.mojang.blaze3d.platform.GlStateManager;
import java.util.Objects;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.gui.chat.NarratorChatListener;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.client.sounds.SoundManager;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;

public abstract class AbstractWidget extends GuiComponent implements Widget, GuiEventListener {
   public static final ResourceLocation WIDGETS_LOCATION = new ResourceLocation("textures/gui/widgets.png");
   private static final int NARRATE_DELAY_MOUSE = 750;
   private static final int NARRATE_DELAY_FOCUS = 200;
   protected int width;
   protected int height;
   public int x;
   public int y;
   private String message;
   private boolean wasHovered;
   protected boolean isHovered;
   public boolean active;
   public boolean visible;
   protected float alpha;
   protected long nextNarration;
   private boolean focused;

   public AbstractWidget(int var1, int var2, String var3) {
      this(var1, var2, 200, 20, var3);
   }

   public AbstractWidget(int var1, int var2, int var3, int var4, String var5) {
      super();
      this.active = true;
      this.visible = true;
      this.alpha = 1.0F;
      this.nextNarration = 9223372036854775807L;
      this.x = var1;
      this.y = var2;
      this.width = var3;
      this.height = var4;
      this.message = var5;
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

   public void render(int var1, int var2, float var3) {
      if (this.visible) {
         this.isHovered = var1 >= this.x && var2 >= this.y && var1 < this.x + this.width && var2 < this.y + this.height;
         if (this.wasHovered != this.isHovered()) {
            if (this.isHovered()) {
               if (this.focused) {
                  this.nextNarration = Util.getMillis() + 200L;
               } else {
                  this.nextNarration = Util.getMillis() + 750L;
               }
            } else {
               this.nextNarration = 9223372036854775807L;
            }
         }

         if (this.visible) {
            this.renderButton(var1, var2, var3);
         }

         this.narrate();
         this.wasHovered = this.isHovered();
      }
   }

   protected void narrate() {
      if (this.active && this.isHovered() && Util.getMillis() > this.nextNarration) {
         String var1 = this.getNarrationMessage();
         if (!var1.isEmpty()) {
            NarratorChatListener.INSTANCE.sayNow(var1);
            this.nextNarration = 9223372036854775807L;
         }
      }

   }

   protected String getNarrationMessage() {
      return this.message.isEmpty() ? "" : I18n.get("gui.narrate.button", this.getMessage());
   }

   public void renderButton(int var1, int var2, float var3) {
      Minecraft var4 = Minecraft.getInstance();
      Font var5 = var4.font;
      var4.getTextureManager().bind(WIDGETS_LOCATION);
      GlStateManager.color4f(1.0F, 1.0F, 1.0F, this.alpha);
      int var6 = this.getYImage(this.isHovered());
      GlStateManager.enableBlend();
      GlStateManager.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
      GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
      this.blit(this.x, this.y, 0, 46 + var6 * 20, this.width / 2, this.height);
      this.blit(this.x + this.width / 2, this.y, 200 - this.width / 2, 46 + var6 * 20, this.width / 2, this.height);
      this.renderBg(var4, var1, var2);
      int var7 = 14737632;
      if (!this.active) {
         var7 = 10526880;
      } else if (this.isHovered()) {
         var7 = 16777120;
      }

      this.drawCenteredString(var5, this.message, this.x + this.width / 2, this.y + (this.height - 8) / 2, var7 | Mth.ceil(this.alpha * 255.0F) << 24);
   }

   protected void renderBg(Minecraft var1, int var2, int var3) {
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

   public void renderToolTip(int var1, int var2) {
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

   public void setMessage(String var1) {
      if (!Objects.equals(var1, this.message)) {
         this.nextNarration = Util.getMillis() + 250L;
      }

      this.message = var1;
   }

   public String getMessage() {
      return this.message;
   }

   public boolean isFocused() {
      return this.focused;
   }

   protected void setFocused(boolean var1) {
      this.focused = var1;
   }
}
