package net.minecraft.world.level.storage;

import javax.annotation.Nullable;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;

public interface PlayerIO {
   void save(Player var1);

   @Nullable
   CompoundTag load(Player var1);
}
