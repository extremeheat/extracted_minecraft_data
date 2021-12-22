package net.minecraft.realms;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.gui.components.Widget;
import net.minecraft.network.chat.Component;

public class RealmsLabel implements Widget {
   private final Component text;
   // $FF: renamed from: x int
   private final int field_316;
   // $FF: renamed from: y int
   private final int field_317;
   private final int color;

   public RealmsLabel(Component var1, int var2, int var3, int var4) {
      super();
      this.text = var1;
      this.field_316 = var2;
      this.field_317 = var3;
      this.color = var4;
   }

   public void render(PoseStack var1, int var2, int var3, float var4) {
      GuiComponent.drawCenteredString(var1, Minecraft.getInstance().font, this.text, this.field_316, this.field_317, this.color);
   }

   public Component getText() {
      return this.text;
   }
}
