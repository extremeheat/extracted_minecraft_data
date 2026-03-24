package com.mojang.blaze3d.systems;

import java.util.OptionalLong;

public interface GpuQuery extends AutoCloseable {
   OptionalLong getValue();

   void close();
}
