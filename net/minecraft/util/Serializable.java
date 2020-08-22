package net.minecraft.util;

import com.mojang.datafixers.types.DynamicOps;

public interface Serializable {
   Object serialize(DynamicOps var1);
}
