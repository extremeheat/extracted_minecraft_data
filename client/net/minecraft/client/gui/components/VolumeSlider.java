package net.minecraft.client.gui.components;

import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.sounds.SoundSource;

public class VolumeSlider extends AbstractOptionSliderButton {
   private final SoundSource source;

   public VolumeSlider(Minecraft var1, int var2, int var3, SoundSource var4, int var5) {
      super(var1.options, var2, var3, var5, 20, (double)var1.options.getSoundSourceVolume(var4));
      this.source = var4;
      this.updateMessage();
   }

   protected void updateMessage() {
      Object var1 = (float)this.value == (float)this.getYImage(false) ? CommonComponents.OPTION_OFF : new TextComponent((int)(this.value * 100.0D) + "%");
      this.setMessage((new TranslatableComponent("soundCategory." + this.source.getName())).append(": ").append((Component)var1));
   }

   protected void applyValue() {
      this.options.setSoundCategoryVolume(this.source, (float)this.value);
      this.options.save();
   }
}
