package net.minecraft.util;

import java.util.function.BiConsumer;
import javax.annotation.Nullable;

public interface ITaskType<K, T extends ITaskType<K, T>> {
   @Nullable
   T func_201497_a_();

   void func_201492_a_(K var1, BiConsumer<K, T> var2);
}
