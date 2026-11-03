package net.minecraft.world.level.block;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

public sealed interface Group<T> permits WeatheringCopperCollection, ColorCollection {
   List<T> asList();

   void forEach(final Consumer<T> consumer);

   <U> Group<U> map(final Function<T, U> mapper);
}
