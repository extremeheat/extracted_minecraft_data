package net.minecraft.state;

import java.util.Collection;
import java.util.Optional;

public interface IProperty<T extends Comparable<T>> {
   String func_177701_a();

   Collection<T> func_177700_c();

   Class<T> func_177699_b();

   Optional<T> func_185929_b(String var1);

   String func_177702_a(T var1);
}
