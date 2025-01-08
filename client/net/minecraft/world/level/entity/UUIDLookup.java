package net.minecraft.world.level.entity;

import java.util.UUID;

public interface UUIDLookup<IdentifiedType extends UniquelyIdentifyable> {
   IdentifiedType getEntity(UUID var1);
}
