package net.minecraft.world.ticks;

import java.util.function.Function;
import net.minecraft.nbt.Tag;

public interface SerializableTickContainer<T> {
   Tag save(long var1, Function<T, String> var3);
}
