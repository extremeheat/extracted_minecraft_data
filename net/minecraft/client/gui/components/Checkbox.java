package net.minecraft.client.gui.components;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;

public class Checkbox extends AbstractButton {
   private static final ResourceLocation TEXTURE = new ResourceLocation("textures/gui/checkbox.png");
   boolean selected;

   public Checkbox(int var1, int var2, int var3, int var4, String var5, boolean var6) {
      super(var1, var2, var3, var4, var5);
      this.selected = var6;
   }

   public void onPress() {
      this.selected = !this.selected;
   }

   public boolean selected() {
      return this.selected;
   }

   public void renderButton(int var1, int var2, float var3) {
      Minecraft var4 = Minecraft.getInstance();
      var4.getTextureManager().bind(TEXTURE);
      RenderSystem.enableDepthTest();
      Font var5 = var4.font;
      RenderSystem.color4f(1.0F, 1.0F, 1.0F, this.alpha);
      RenderSystem.enableBlend();
      RenderSystem.defaultBlendFunc();
      RenderSystem.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
      blit(this.x, this.y, 0.0F, this.selected ? 20.0F : 0.0F, 20, this.height, 32, 64);
      this.renderBg(var4, var1, var2);
      int var6 = 14737632;
      this.drawString(var5, this.getMessage(), this.x + 24, this.y + (this.height - 8) / 2, 14737632 | Mth.ceil(this.alpha * 255.0F) << 24);
   }
}
