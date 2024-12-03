package net.minecraft.advancements.critereon;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Optional;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.RegistryCodecs;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.JukeboxPlayable;
import net.minecraft.world.item.JukeboxSong;

public record ItemJukeboxPlayablePredicate(Optional<HolderSet<JukeboxSong>> song) implements SingleComponentItemPredicate<JukeboxPlayable> {
   public static final Codec<ItemJukeboxPlayablePredicate> CODEC = RecordCodecBuilder.create((var0) -> var0.group(RegistryCodecs.homogeneousList(Registries.JUKEBOX_SONG).optionalFieldOf("song").forGetter(ItemJukeboxPlayablePredicate::song)).apply(var0, ItemJukeboxPlayablePredicate::new));

   public ItemJukeboxPlayablePredicate(Optional<HolderSet<JukeboxSong>> var1) {
      super();
      this.song = var1;
   }

   public DataComponentType<JukeboxPlayable> componentType() {
      return DataComponents.JUKEBOX_PLAYABLE;
   }

   public boolean matches(ItemStack var1, JukeboxPlayable var2) {
      if (!this.song.isPresent()) {
         return true;
      } else {
         boolean var3 = false;

         for(Holder var5 : (HolderSet)this.song.get()) {
            Optional var6 = var5.unwrapKey();
            if (!var6.isEmpty() && var6.get() == var2.song().key()) {
               var3 = true;
               break;
            }
         }

         return var3;
      }
   }

   public static ItemJukeboxPlayablePredicate any() {
      return new ItemJukeboxPlayablePredicate(Optional.empty());
   }
}
