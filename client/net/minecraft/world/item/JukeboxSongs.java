package net.minecraft.world.item;

import net.minecraft.Util;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;

public interface JukeboxSongs {
   ResourceKey<JukeboxSong> THIRTEEN = create("13");
   ResourceKey<JukeboxSong> CAT = create("cat");
   ResourceKey<JukeboxSong> BLOCKS = create("blocks");
   ResourceKey<JukeboxSong> CHIRP = create("chirp");
   ResourceKey<JukeboxSong> FAR = create("far");
   ResourceKey<JukeboxSong> MALL = create("mall");
   ResourceKey<JukeboxSong> MELLOHI = create("mellohi");
   ResourceKey<JukeboxSong> STAL = create("stal");
   ResourceKey<JukeboxSong> STRAD = create("strad");
   ResourceKey<JukeboxSong> WARD = create("ward");
   ResourceKey<JukeboxSong> ELEVEN = create("11");
   ResourceKey<JukeboxSong> WAIT = create("wait");
   ResourceKey<JukeboxSong> PIGSTEP = create("pigstep");
   ResourceKey<JukeboxSong> OTHERSIDE = create("otherside");
   ResourceKey<JukeboxSong> FIVE = create("5");
   ResourceKey<JukeboxSong> RELIC = create("relic");
   ResourceKey<JukeboxSong> PRECIPICE = create("precipice");
   ResourceKey<JukeboxSong> CREATOR = create("creator");
   ResourceKey<JukeboxSong> CREATOR_MUSIC_BOX = create("creator_music_box");

   private static ResourceKey<JukeboxSong> create(String var0) {
      return ResourceKey.create(Registries.JUKEBOX_SONG, ResourceLocation.withDefaultNamespace(var0));
   }

   private static void register(BootstrapContext<JukeboxSong> var0, ResourceKey<JukeboxSong> var1, Holder.Reference<SoundEvent> var2, int var3, int var4) {
      var0.register(var1, new JukeboxSong(var2, Component.translatable(Util.makeDescriptionId("jukebox_song", var1.location())), (float)var3, var4));
   }

   static void bootstrap(BootstrapContext<JukeboxSong> var0) {
      register(var0, THIRTEEN, SoundEvents.MUSIC_DISC_13, 178, 1);
      register(var0, CAT, SoundEvents.MUSIC_DISC_CAT, 185, 2);
      register(var0, BLOCKS, SoundEvents.MUSIC_DISC_BLOCKS, 345, 3);
      register(var0, CHIRP, SoundEvents.MUSIC_DISC_CHIRP, 185, 4);
      register(var0, FAR, SoundEvents.MUSIC_DISC_FAR, 174, 5);
      register(var0, MALL, SoundEvents.MUSIC_DISC_MALL, 197, 6);
      register(var0, MELLOHI, SoundEvents.MUSIC_DISC_MELLOHI, 96, 7);
      register(var0, STAL, SoundEvents.MUSIC_DISC_STAL, 150, 8);
      register(var0, STRAD, SoundEvents.MUSIC_DISC_STRAD, 188, 9);
      register(var0, WARD, SoundEvents.MUSIC_DISC_WARD, 251, 10);
      register(var0, ELEVEN, SoundEvents.MUSIC_DISC_11, 71, 11);
      register(var0, WAIT, SoundEvents.MUSIC_DISC_WAIT, 238, 12);
      register(var0, PIGSTEP, SoundEvents.MUSIC_DISC_PIGSTEP, 149, 13);
      register(var0, OTHERSIDE, SoundEvents.MUSIC_DISC_OTHERSIDE, 195, 14);
      register(var0, FIVE, SoundEvents.MUSIC_DISC_5, 178, 15);
      register(var0, RELIC, SoundEvents.MUSIC_DISC_RELIC, 218, 14);
      register(var0, PRECIPICE, SoundEvents.MUSIC_DISC_PRECIPICE, 299, 13);
      register(var0, CREATOR, SoundEvents.MUSIC_DISC_CREATOR, 176, 12);
      register(var0, CREATOR_MUSIC_BOX, SoundEvents.MUSIC_DISC_CREATOR_MUSIC_BOX, 73, 11);
   }
}
