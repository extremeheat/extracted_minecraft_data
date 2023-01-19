package net.minecraft.client.gui.components;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import java.util.function.Consumer;
import javax.annotation.Nullable;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ComponentPath;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.layouts.LayoutElement;
import net.minecraft.client.gui.narration.NarratableEntry;
import net.minecraft.client.gui.narration.NarratedElementType;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.navigation.FocusNavigationEvent;
import net.minecraft.client.gui.navigation.ScreenRectangle;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.tooltip.BelowOrAboveWidgetTooltipPositioner;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipPositioner;
import net.minecraft.client.gui.screens.inventory.tooltip.DefaultTooltipPositioner;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.client.sounds.SoundManager;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;

public abstract class AbstractWidget extends GuiComponent implements Renderable, GuiEventListener, LayoutElement, NarratableEntry {
   public static final ResourceLocation WIDGETS_LOCATION = new ResourceLocation("textures/gui/widgets.png");
   protected static final int BUTTON_TEXTURE_Y_OFFSET = 46;
   protected int width;
   protected int height;
   private int x;
   private int y;
   private Component message;
   protected boolean isHovered;
   public boolean active = true;
   public boolean visible = true;
   protected float alpha = 1.0F;
   private boolean focused;
   @Nullable
   private Tooltip tooltip;
   private int tooltipMsDelay;
   private long hoverOrFocusedStartTime;
   private boolean wasHoveredOrFocused;

   public AbstractWidget(int var1, int var2, int var3, int var4, Component var5) {
      super();
      this.x = var1;
      this.y = var2;
      this.width = var3;
      this.height = var4;
      this.message = var5;
   }

   @Override
   public int getHeight() {
      return this.height;
   }

   protected ResourceLocation getTextureLocation() {
      return WIDGETS_LOCATION;
   }

   protected int getTextureY() {
      byte var1 = 1;
      if (!this.active) {
         var1 = 0;
      } else if (this.isHoveredOrFocused()) {
         var1 = 2;
      }

      return 46 + var1 * 20;
   }

   @Override
   public void render(PoseStack var1, int var2, int var3, float var4) {
      if (this.visible) {
         this.isHovered = var2 >= this.getX() && var3 >= this.getY() && var2 < this.getX() + this.width && var3 < this.getY() + this.height;
         this.renderButton(var1, var2, var3, var4);
         this.updateTooltip();
      }
   }

   private void updateTooltip() {
      if (this.tooltip != null) {
         boolean var1 = this.isHovered || this.isFocused() && Minecraft.getInstance().getLastInputType().isKeyboard();
         if (var1 != this.wasHoveredOrFocused) {
            if (var1) {
               this.hoverOrFocusedStartTime = Util.getMillis();
            }

            this.wasHoveredOrFocused = var1;
         }

         if (var1 && Util.getMillis() - this.hoverOrFocusedStartTime > (long)this.tooltipMsDelay) {
            Screen var2 = Minecraft.getInstance().screen;
            if (var2 != null) {
               var2.setTooltipForNextRenderPass(this.tooltip, this.createTooltipPositioner(), this.isFocused());
            }
         }
      }
   }

   protected ClientTooltipPositioner createTooltipPositioner() {
      return (ClientTooltipPositioner)(!this.isHovered && this.isFocused() && Minecraft.getInstance().getLastInputType().isKeyboard()
         ? new BelowOrAboveWidgetTooltipPositioner(this)
         : DefaultTooltipPositioner.INSTANCE);
   }

   public void setTooltip(@Nullable Tooltip var1) {
      this.tooltip = var1;
   }

   public void setTooltipDelay(int var1) {
      this.tooltipMsDelay = var1;
   }

   protected MutableComponent createNarrationMessage() {
      return wrapDefaultNarrationMessage(this.getMessage());
   }

   public static MutableComponent wrapDefaultNarrationMessage(Component var0) {
      return Component.translatable("gui.narrate.button", var0);
   }

   public void renderButton(PoseStack var1, int var2, int var3, float var4) {
      Minecraft var5 = Minecraft.getInstance();
      Font var6 = var5.font;
      RenderSystem.setShader(GameRenderer::getPositionTexShader);
      RenderSystem.setShaderTexture(0, this.getTextureLocation());
      RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, this.alpha);
      RenderSystem.enableBlend();
      RenderSystem.defaultBlendFunc();
      RenderSystem.enableDepthTest();
      int var7 = this.width / 2;
      int var8 = this.width - var7;
      int var9 = this.getTextureY();
      this.blit(var1, this.getX(), this.getY(), 0, var9, var7, this.height);
      this.blit(var1, this.getX() + var7, this.getY(), 200 - var8, var9, var8, this.height);
      this.renderBg(var1, var5, var2, var3);
      RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
      int var10 = this.active ? 16777215 : 10526880;
      drawCenteredString(var1, var6, this.getMessage(), this.getX() + var7, this.getY() + (this.height - 8) / 2, var10 | Mth.ceil(this.alpha * 255.0F) << 24);
   }

   protected void renderBg(PoseStack var1, Minecraft var2, int var3, int var4) {
   }

   public void onClick(double var1, double var3) {
   }

   public void onRelease(double var1, double var3) {
   }

   protected void onDrag(double var1, double var3, double var5, double var7) {
   }

   @Override
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

   @Override
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

   @Override
   public boolean mouseDragged(double var1, double var3, int var5, double var6, double var8) {
      if (this.isValidClickButton(var5)) {
         this.onDrag(var1, var3, var6, var8);
         return true;
      } else {
         return false;
      }
   }

   protected boolean clicked(double var1, double var3) {
      return this.active
         && this.visible
         && var1 >= (double)this.getX()
         && var3 >= (double)this.getY()
         && var1 < (double)(this.getX() + this.width)
         && var3 < (double)(this.getY() + this.height);
   }

   public boolean isHoveredOrFocused() {
      return this.isHovered || this.isFocused();
   }

   @Nullable
   @Override
   public ComponentPath nextFocusPath(FocusNavigationEvent var1) {
      if (!this.active || !this.visible) {
         return null;
      } else {
         return !this.isFocused() ? ComponentPath.leaf(this) : null;
      }
   }

   @Override
   public boolean isMouseOver(double var1, double var3) {
      return this.active
         && this.visible
         && var1 >= (double)this.getX()
         && var3 >= (double)this.getY()
         && var1 < (double)(this.getX() + this.width)
         && var3 < (double)(this.getY() + this.height);
   }

   public void playDownSound(SoundManager var1) {
      var1.play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1.0F));
   }

   @Override
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
      this.message = var1;
   }

   public Component getMessage() {
      return this.message;
   }

   @Override
   public boolean isFocused() {
      return this.focused;
   }

   @Override
   public boolean isActive() {
      return this.visible && this.active;
   }

   @Override
   public void setFocused(boolean var1) {
      this.focused = var1;
   }

   @Override
   public NarratableEntry.NarrationPriority narrationPriority() {
      if (this.isFocused()) {
         return NarratableEntry.NarrationPriority.FOCUSED;
      } else {
         return this.isHovered ? NarratableEntry.NarrationPriority.HOVERED : NarratableEntry.NarrationPriority.NONE;
      }
   }

   @Override
   public final void updateNarration(NarrationElementOutput var1) {
      this.updateWidgetNarration(var1);
      if (this.tooltip != null) {
         this.tooltip.updateNarration(var1);
      }
   }

   protected abstract void updateWidgetNarration(NarrationElementOutput var1);

   protected void defaultButtonNarrationText(NarrationElementOutput var1) {
      var1.add(NarratedElementType.TITLE, this.createNarrationMessage());
      if (this.active) {
         if (this.isFocused()) {
            var1.add(NarratedElementType.USAGE, Component.translatable("narration.button.usage.focused"));
         } else {
            var1.add(NarratedElementType.USAGE, Component.translatable("narration.button.usage.hovered"));
         }
      }
   }

   @Override
   public int getX() {
      return this.x;
   }

   @Override
   public void setX(int var1) {
      this.x = var1;
   }

   @Override
   public int getY() {
      return this.y;
   }

   @Override
   public void setY(int var1) {
      this.y = var1;
   }

   @Override
   public void visitWidgets(Consumer<AbstractWidget> var1) {
      var1.accept(this);
   }

   @Override
   public ScreenRectangle getRectangle() {
      return new ScreenRectangle(this.getX(), this.getY(), this.getWidth(), this.getHeight());
   }
}
