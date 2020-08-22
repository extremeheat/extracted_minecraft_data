package net.minecraft.client.gui.components;

import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.sounds.SoundSource;

public class VolumeSlider extends AbstractSliderButton {
   private final SoundSource source;

   public VolumeSlider(Minecraft var1, int var2, int var3, SoundSource var4, int var5) {
      super(var1.options, var2, var3, var5, 20, (double)var1.options.getSoundSourceVolume(var4));
      this.source = var4;
      this.updateMessage();
   }

   protected void updateMessage() {
      String var1 = (float)this.value == (float)this.getYImage(false) ? I18n.get("options.off") : (int)((float)this.value * 100.0F) + "%";
      this.setMessage(I18n.get("soundCategory." + this.source.getName()) + ": " + var1);
   }

   protected void applyValue() {
      this.options.setSoundCategoryVolume(this.source, (float)this.value);
      this.options.save();
   }
}
