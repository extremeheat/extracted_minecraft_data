package net.minecraft.client.gui.screens;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Option;
import net.minecraft.client.Options;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.OptionButton;
import net.minecraft.client.gui.components.VolumeSlider;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.sounds.SoundSource;

public class SoundOptionsScreen extends OptionsSubScreen {
   public SoundOptionsScreen(Screen var1, Options var2) {
      super(var1, var2, new TranslatableComponent("options.sounds.title"));
   }

   protected void init() {
      byte var1 = 0;
      this.addButton(new VolumeSlider(this.minecraft, this.width / 2 - 155 + var1 % 2 * 160, this.height / 6 - 12 + 24 * (var1 >> 1), SoundSource.MASTER, 310));
      int var6 = var1 + 2;
      SoundSource[] var2 = SoundSource.values();
      int var3 = var2.length;

      for(int var4 = 0; var4 < var3; ++var4) {
         SoundSource var5 = var2[var4];
         if (var5 != SoundSource.MASTER) {
            this.addButton(new VolumeSlider(this.minecraft, this.width / 2 - 155 + var6 % 2 * 160, this.height / 6 - 12 + 24 * (var6 >> 1), var5, 150));
            ++var6;
         }
      }

      int var10003 = this.width / 2 - 75;
      int var10004 = this.height / 6 - 12;
      ++var6;
      this.addButton(new OptionButton(var10003, var10004 + 24 * (var6 >> 1), 150, 20, Option.SHOW_SUBTITLES, Option.SHOW_SUBTITLES.getMessage(this.options), (var1x) -> {
         Option.SHOW_SUBTITLES.toggle(this.minecraft.options);
         var1x.setMessage(Option.SHOW_SUBTITLES.getMessage(this.minecraft.options));
         this.minecraft.options.save();
      }));
      this.addButton(new Button(this.width / 2 - 100, this.height / 6 + 168, 200, 20, CommonComponents.GUI_DONE, (var1x) -> {
         this.minecraft.setScreen(this.lastScreen);
      }));
   }

   public void render(PoseStack var1, int var2, int var3, float var4) {
      this.renderBackground(var1);
      drawCenteredString(var1, this.font, this.title, this.width / 2, 15, 16777215);
      super.render(var1, var2, var3, var4);
   }
}
