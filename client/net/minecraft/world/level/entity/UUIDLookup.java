package net.minecraft.world.level.entity;

import java.util.UUID;
import javax.annotation.Nullable;

public interface UUIDLookup<IdentifiedType extends UniquelyIdentifyable> {
   @Nullable
   IdentifiedType lookup(UUID var1);
}
