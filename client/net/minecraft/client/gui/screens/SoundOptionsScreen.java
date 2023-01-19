package net.minecraft.client.gui.screens;

import com.mojang.blaze3d.vertex.PoseStack;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.client.Options;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.TooltipAccessor;
import net.minecraft.client.gui.components.VolumeSlider;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundSource;

public class SoundOptionsScreen extends OptionsSubScreen {
   @Nullable
   private AbstractWidget directionalAudioButton;

   public SoundOptionsScreen(Screen var1, Options var2) {
      super(var1, var2, Component.translatable("options.sounds.title"));
   }

   @Override
   protected void init() {
      int var1 = this.height / 6 - 12;
      boolean var2 = true;
      int var3 = 0;
      this.addRenderableWidget(new VolumeSlider(this.minecraft, this.width / 2 - 155 + var3 % 2 * 160, var1 + 22 * (var3 >> 1), SoundSource.MASTER, 310));
      var3 += 2;

      for(SoundSource var7 : SoundSource.values()) {
         if (var7 != SoundSource.MASTER) {
            this.addRenderableWidget(new VolumeSlider(this.minecraft, this.width / 2 - 155 + var3 % 2 * 160, var1 + 22 * (var3 >> 1), var7, 150));
            ++var3;
         }
      }

      if (var3 % 2 == 1) {
         ++var3;
      }

      this.addRenderableWidget(this.options.soundDevice().createButton(this.options, this.width / 2 - 155, var1 + 22 * (var3 >> 1), 310));
      var3 += 2;
      this.addRenderableWidget(this.options.showSubtitles().createButton(this.options, this.width / 2 - 155, var1 + 22 * (var3 >> 1), 150));
      this.directionalAudioButton = this.options.directionalAudio().createButton(this.options, this.width / 2 + 5, var1 + 22 * (var3 >> 1), 150);
      this.addRenderableWidget(this.directionalAudioButton);
      var3 += 2;
      this.addRenderableWidget(
         new Button(this.width / 2 - 100, var1 + 22 * (var3 >> 1), 200, 20, CommonComponents.GUI_DONE, var1x -> this.minecraft.setScreen(this.lastScreen))
      );
   }

   @Override
   public void render(PoseStack var1, int var2, int var3, float var4) {
      this.renderBackground(var1);
      drawCenteredString(var1, this.font, this.title, this.width / 2, 15, 16777215);
      super.render(var1, var2, var3, var4);
      if (this.directionalAudioButton != null && this.directionalAudioButton.isMouseOver((double)var2, (double)var3)) {
         List var5 = ((TooltipAccessor)this.directionalAudioButton).getTooltip();
         this.renderTooltip(var1, var5, var2, var3);
      }
   }
}
