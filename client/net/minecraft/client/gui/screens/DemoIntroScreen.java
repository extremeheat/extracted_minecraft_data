package net.minecraft.client.gui.screens;

import java.util.Objects;
import net.minecraft.Util;
import net.minecraft.client.Options;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.MultiLineLabel;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.CommonLinks;

public class DemoIntroScreen extends Screen {
   private static final ResourceLocation DEMO_BACKGROUND_LOCATION = ResourceLocation.withDefaultNamespace("textures/gui/demo_background.png");
   private MultiLineLabel movementMessage;
   private MultiLineLabel durationMessage;

   public DemoIntroScreen() {
      super(Component.translatable("demo.help.title"));
      this.movementMessage = MultiLineLabel.EMPTY;
      this.durationMessage = MultiLineLabel.EMPTY;
   }

   protected void init() {
      boolean var1 = true;
      this.addRenderableWidget(Button.builder(Component.translatable("demo.help.buy"), (var0) -> {
         var0.active = false;
         Util.getPlatform().openUri(CommonLinks.BUY_MINECRAFT_JAVA);
      }).bounds(this.width / 2 - 116, this.height / 2 + 62 + -16, 114, 20).build());
      this.addRenderableWidget(Button.builder(Component.translatable("demo.help.later"), (var1x) -> {
         this.minecraft.setScreen((Screen)null);
         this.minecraft.mouseHandler.grabMouse();
      }).bounds(this.width / 2 + 2, this.height / 2 + 62 + -16, 114, 20).build());
      Options var2 = this.minecraft.options;
      this.movementMessage = MultiLineLabel.create(this.font, Component.translatable("demo.help.movementShort", var2.keyUp.getTranslatedKeyMessage(), var2.keyLeft.getTranslatedKeyMessage(), var2.keyDown.getTranslatedKeyMessage(), var2.keyRight.getTranslatedKeyMessage()), Component.translatable("demo.help.movementMouse"), Component.translatable("demo.help.jump", var2.keyJump.getTranslatedKeyMessage()), Component.translatable("demo.help.inventory", var2.keyInventory.getTranslatedKeyMessage()));
      this.durationMessage = MultiLineLabel.create(this.font, Component.translatable("demo.help.fullWrapped"), 218);
   }

   public void renderBackground(GuiGraphics var1, int var2, int var3, float var4) {
      super.renderBackground(var1, var2, var3, var4);
      int var5 = (this.width - 248) / 2;
      int var6 = (this.height - 166) / 2;
      var1.blit(DEMO_BACKGROUND_LOCATION, var5, var6, 0, 0, 248, 166);
   }

   public void render(GuiGraphics var1, int var2, int var3, float var4) {
      super.render(var1, var2, var3, var4);
      int var5 = (this.width - 248) / 2 + 10;
      int var6 = (this.height - 166) / 2 + 8;
      var1.drawString(this.font, this.title, var5, var6, 2039583, false);
      var6 = this.movementMessage.renderLeftAlignedNoShadow(var1, var5, var6 + 12, 12, 5197647);
      MultiLineLabel var10000 = this.durationMessage;
      int var10003 = var6 + 20;
      Objects.requireNonNull(this.font);
      var10000.renderLeftAlignedNoShadow(var1, var5, var10003, 9, 2039583);
   }
}
