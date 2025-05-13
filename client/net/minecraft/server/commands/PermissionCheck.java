package net.minecraft.server.commands;

import java.util.function.Predicate;

public interface PermissionCheck<T> extends Predicate<T> {
   int requiredLevel();
}
