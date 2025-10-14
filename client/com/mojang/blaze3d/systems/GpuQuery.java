package com.mojang.blaze3d.systems;

import com.mojang.blaze3d.DontObfuscate;
import java.util.OptionalLong;

@DontObfuscate
public interface GpuQuery extends AutoCloseable {
   OptionalLong getValue();

   void close();
}
