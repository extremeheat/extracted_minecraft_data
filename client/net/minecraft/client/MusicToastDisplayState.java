package net.minecraft.client;

import com.mojang.serialization.Codec;
import net.minecraft.network.chat.Component;
import net.minecraft.util.StringRepresentable;

public enum MusicToastDisplayState implements StringRepresentable {
   NEVER("never", "options.musicToast.never"),
   PAUSE("pause", "options.musicToast.pauseMenu"),
   PAUSE_AND_TOAST("pause_and_toast", "options.musicToast.pauseMenuAndToast");

   public static final Codec<MusicToastDisplayState> CODEC = StringRepresentable.<MusicToastDisplayState>fromEnum(MusicToastDisplayState::values);
   private final String name;
   private final Component text;
   private final Component tooltip;

   private MusicToastDisplayState(final String var3, final String var4) {
      this.name = var3;
      this.text = Component.translatable(var4);
      this.tooltip = Component.translatable(var4 + ".tooltip");
   }

   public Component text() {
      return this.text;
   }

   public Component tooltip() {
      return this.tooltip;
   }

   public String getSerializedName() {
      return this.name;
   }

   public boolean renderInPauseScreen() {
      return this != NEVER;
   }

   public boolean renderToast() {
      return this == PAUSE_AND_TOAST;
   }

   // $FF: synthetic method
   private static MusicToastDisplayState[] $values() {
      return new MusicToastDisplayState[]{NEVER, PAUSE, PAUSE_AND_TOAST};
   }
}
