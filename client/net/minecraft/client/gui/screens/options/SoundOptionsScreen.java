package net.minecraft.client.gui.screens.options;

import java.util.Arrays;
import net.minecraft.client.OptionInstance;
import net.minecraft.client.Options;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundSource;

public class SoundOptionsScreen extends OptionsSubScreen {
   private static final Component TITLE = Component.translatable("options.sounds.title");

   private static OptionInstance<?>[] buttonOptions(Options var0) {
      return new OptionInstance[]{var0.showSubtitles(), var0.directionalAudio()};
   }

   public SoundOptionsScreen(Screen var1, Options var2) {
      super(var1, var2, TITLE);
   }

   protected void addOptions() {
      this.list.addBig(this.options.getSoundSourceOptionInstance(SoundSource.MASTER));
      this.list.addSmall(this.getAllSoundOptionsExceptMaster());
      this.list.addBig(this.options.soundDevice());
      this.list.addSmall(buttonOptions(this.options));
   }

   private OptionInstance<?>[] getAllSoundOptionsExceptMaster() {
      return (OptionInstance[])Arrays.stream(SoundSource.values()).filter((var0) -> {
         return var0 != SoundSource.MASTER;
      }).map((var1) -> {
         return this.options.getSoundSourceOptionInstance(var1);
      }).toArray((var0) -> {
         return new OptionInstance[var0];
      });
   }
}
