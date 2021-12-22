package net.minecraft.client.gui.screens;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Option;
import net.minecraft.client.Options;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.VolumeSlider;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.sounds.SoundSource;

public class SoundOptionsScreen extends OptionsSubScreen {
   public SoundOptionsScreen(Screen var1, Options var2) {
      super(var1, var2, new TranslatableComponent("options.sounds.title"));
   }

   protected void init() {
      int var1 = this.height / 6 - 12;
      boolean var2 = true;
      byte var3 = 0;
      this.addRenderableWidget(new VolumeSlider(this.minecraft, this.width / 2 - 155 + var3 % 2 * 160, var1 + 22 * (var3 >> 1), SoundSource.MASTER, 310));
      int var8 = var3 + 2;
      SoundSource[] var4 = SoundSource.values();
      int var5 = var4.length;

      for(int var6 = 0; var6 < var5; ++var6) {
         SoundSource var7 = var4[var6];
         if (var7 != SoundSource.MASTER) {
            this.addRenderableWidget(new VolumeSlider(this.minecraft, this.width / 2 - 155 + var8 % 2 * 160, var1 + 22 * (var8 >> 1), var7, 150));
            ++var8;
         }
      }

      if (var8 % 2 == 1) {
         ++var8;
      }

      this.addRenderableWidget(Option.AUDIO_DEVICE.createButton(this.options, this.width / 2 - 155, var1 + 22 * (var8 >> 1), 310));
      var8 += 2;
      this.addRenderableWidget(Option.SHOW_SUBTITLES.createButton(this.options, this.width / 2 - 75, var1 + 22 * (var8 >> 1), 150));
      var8 += 2;
      this.addRenderableWidget(new Button(this.width / 2 - 100, var1 + 22 * (var8 >> 1), 200, 20, CommonComponents.GUI_DONE, (var1x) -> {
         this.minecraft.setScreen(this.lastScreen);
      }));
   }

   public void render(PoseStack var1, int var2, int var3, float var4) {
      this.renderBackground(var1);
      drawCenteredString(var1, this.font, this.title, this.width / 2, 15, 16777215);
      super.render(var1, var2, var3, var4);
   }
}
