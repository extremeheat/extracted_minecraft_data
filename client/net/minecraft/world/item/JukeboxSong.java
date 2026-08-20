package net.minecraft.world.item;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Optional;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.Registries;
import net.minecraft.core.registries.codec.RegistryCodecs;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.util.Mth;

public record JukeboxSong(Holder<SoundEvent> soundEvent, Component description, float lengthInSeconds, int comparatorOutput) {
   public static final Codec<JukeboxSong> DIRECT_CODEC = RecordCodecBuilder.create((i) -> i.group(SoundEvent.CODEC.fieldOf("sound_event").forGetter(JukeboxSong::soundEvent), ComponentSerialization.CODEC.fieldOf("description").forGetter(JukeboxSong::description), ExtraCodecs.POSITIVE_FLOAT.fieldOf("length_in_seconds").forGetter(JukeboxSong::lengthInSeconds), ExtraCodecs.intRange(0, 15).fieldOf("comparator_output").forGetter(JukeboxSong::comparatorOutput)).apply(i, JukeboxSong::new));
   public static final StreamCodec<RegistryFriendlyByteBuf, JukeboxSong> DIRECT_STREAM_CODEC;
   public static final Codec<Holder<JukeboxSong>> CODEC;
   public static final StreamCodec<RegistryFriendlyByteBuf, Holder<JukeboxSong>> STREAM_CODEC;
   private static final int SONG_END_PADDING_TICKS = 20;

   public JukeboxSong {
      super();
   }

   public int lengthInTicks() {
      return Mth.ceil(this.lengthInSeconds * 20.0F);
   }

   public boolean hasFinished(final long ticksElapsed) {
      return ticksElapsed >= (long)(this.lengthInTicks() + 20);
   }

   public static Optional<Holder<JukeboxSong>> fromStack(final ItemStack stack) {
      JukeboxPlayable jukeboxPlayable = (JukeboxPlayable)stack.get(DataComponents.JUKEBOX_PLAYABLE);
      return jukeboxPlayable != null ? Optional.of(jukeboxPlayable.song()) : Optional.empty();
   }

   static {
      DIRECT_STREAM_CODEC = StreamCodec.composite(SoundEvent.STREAM_CODEC, JukeboxSong::soundEvent, ComponentSerialization.STREAM_CODEC, JukeboxSong::description, ByteBufCodecs.FLOAT, JukeboxSong::lengthInSeconds, ByteBufCodecs.VAR_INT, JukeboxSong::comparatorOutput, JukeboxSong::new);
      CODEC = RegistryCodecs.holder(Registries.JUKEBOX_SONG);
      STREAM_CODEC = ByteBufCodecs.holder(Registries.JUKEBOX_SONG, DIRECT_STREAM_CODEC);
   }
}
