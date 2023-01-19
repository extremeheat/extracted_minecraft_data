package net.minecraft.realms;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.gui.components.Renderable;
import net.minecraft.network.chat.Component;

public class RealmsLabel implements Renderable {
   private final Component text;
   private final int x;
   private final int y;
   private final int color;

   public RealmsLabel(Component var1, int var2, int var3, int var4) {
      super();
      this.text = var1;
      this.x = var2;
      this.y = var3;
      this.color = var4;
   }

   @Override
   public void render(PoseStack var1, int var2, int var3, float var4) {
      GuiComponent.drawCenteredString(var1, Minecraft.getInstance().font, this.text, this.x, this.y, this.color);
   }

   public Component getText() {
      return this.text;
   }
}
