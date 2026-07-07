package com.mojang.renderpearl.api.commands;

import java.util.OptionalLong;

public interface GpuQuery extends AutoCloseable {
   OptionalLong getValue();

   void close();
}
