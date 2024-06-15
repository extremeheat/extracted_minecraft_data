package net.minecraft.client.gui.screens;

import java.util.Arrays;
import net.minecraft.client.OptionInstance;
import net.minecraft.client.Options;
import net.minecraft.client.gui.components.OptionsList;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundSource;

public class SoundOptionsScreen extends OptionsSubScreen {
   private static final Component TITLE = Component.translatable("options.sounds.title");
   private OptionsList list;

   private static OptionInstance<?>[] buttonOptions(Options var0) {
      return new OptionInstance[]{var0.showSubtitles(), var0.directionalAudio()};
   }

   public SoundOptionsScreen(Screen var1, Options var2) {
      super(var1, var2, TITLE);
   }

   @Override
   protected void init() {
      this.list = this.addRenderableWidget(new OptionsList(this.minecraft, this.width, this.height, this));
      this.list.addBig(this.options.getSoundSourceOptionInstance(SoundSource.MASTER));
      this.list.addSmall(this.getAllSoundOptionsExceptMaster());
      this.list.addBig(this.options.soundDevice());
      this.list.addSmall(buttonOptions(this.options));
      super.init();
   }

   @Override
   protected void repositionElements() {
      super.repositionElements();
      this.list.updateSize(this.width, this.layout);
   }

   private OptionInstance<?>[] getAllSoundOptionsExceptMaster() {
      return Arrays.stream(SoundSource.values())
         .filter(var0 -> var0 != SoundSource.MASTER)
         .map(var1 -> this.options.getSoundSourceOptionInstance(var1))
         .toArray(OptionInstance[]::new);
   }
}
