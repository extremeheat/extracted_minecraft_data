package com.mojang.renderpearl.api.commands;

import com.mojang.renderpearl.util.UncheckedAutoCloseable;
import java.util.OptionalLong;

public interface GpuQueryPool extends UncheckedAutoCloseable {
   int size();

   OptionalLong getValue(int index);

   OptionalLong[] getValues(int index, int count);
}
