package net.minecraft.client.gui.components.toasts;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.unlocks.PlayerUnlocksScreen;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Items;

public class LevelUpToast implements Toast {
   private static final ResourceLocation BACKGROUND_SPRITE = ResourceLocation.withDefaultNamespace("toast/advancement");
   public static final int DISPLAY_TIME = 5000;
   private int level;
   private Toast.Visibility visibility;

   public LevelUpToast(int var1) {
      super();
      this.level = var1;
      this.visibility = Toast.Visibility.SHOW;
   }

   public Toast.Visibility getWantedVisibility() {
      return this.visibility;
   }

   public void update(ToastManager var1, long var2) {
      Minecraft var4 = Minecraft.getInstance();
      LocalPlayer var5 = var4.player;
      if (var5 != null) {
         this.level = var5.experienceLevel;
      }

      if (this.visibility == Toast.Visibility.SHOW) {
         if (var4.screen instanceof PlayerUnlocksScreen) {
            this.visibility = Toast.Visibility.HIDE;
         } else {
            this.visibility = (double)var2 >= 5000.0 * var1.getNotificationDisplayTimeMultiplier() ? Toast.Visibility.HIDE : Toast.Visibility.SHOW;
         }
      }

   }

   public int width() {
      return 240;
   }

   public void render(GuiGraphics var1, Font var2, long var3) {
      var1.blitSprite(RenderType::guiTextured, (ResourceLocation)BACKGROUND_SPRITE, 0, 0, this.width(), this.height());
      var1.drawString(var2, (Component)Component.translatableEscape("level.gained", this.level), 30, 7, -256, false);
      var1.drawString(var2, (Component)Component.translatable("level.unlock_hint", Component.keybind("key.unlocks")), 30, 18, -1, false);
      var1.renderFakeItem(Items.EXPERIENCE_BOTTLE.getDefaultInstance(), 8, 8);
   }
}
