package net.minecraft.state;

import com.google.common.collect.ImmutableMap;
import java.util.Collection;

public interface IStateHolder<C> {
   Collection<IProperty<?>> func_206869_a();

   <T extends Comparable<T>> boolean func_196959_b(IProperty<T> var1);

   <T extends Comparable<T>> T func_177229_b(IProperty<T> var1);

   <T extends Comparable<T>, V extends T> C func_206870_a(IProperty<T> var1, V var2);

   <T extends Comparable<T>> C func_177231_a(IProperty<T> var1);

   ImmutableMap<IProperty<?>, Comparable<?>> func_206871_b();
}
