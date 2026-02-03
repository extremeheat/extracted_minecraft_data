package net.minecraft.client.gui.screens.options;

import java.util.Arrays;
import java.util.Objects;
import java.util.stream.Stream;
import net.minecraft.client.OptionInstance;
import net.minecraft.client.Options;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundSource;

public class SoundOptionsScreen extends OptionsSubScreen {
   private static final Component TITLE = Component.translatable("options.sounds.title");

   public SoundOptionsScreen(final Screen lastScreen, final Options options) {
      super(lastScreen, options, TITLE);
   }

   protected void addOptions() {
      this.list.addBig(this.options.getSoundSourceOptionInstance(SoundSource.MASTER));
      this.list.addSmall(this.getAllSoundOptionsExceptMaster());
      this.list.addBig(this.options.soundDevice());
      this.list.addSmall(this.options.showSubtitles(), this.options.directionalAudio());
      this.list.addSmall(this.options.musicFrequency(), this.options.musicToast());
   }

   private OptionInstance<?>[] getAllSoundOptionsExceptMaster() {
      Stream var10000 = Arrays.stream(SoundSource.values()).filter((s) -> s != SoundSource.MASTER);
      Options var10001 = this.options;
      Objects.requireNonNull(var10001);
      return (OptionInstance[])var10000.map(var10001::getSoundSourceOptionInstance).toArray((x$0) -> new OptionInstance[x$0]);
   }
}
