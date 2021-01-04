package net.minecraft.server.packs.repository;

import java.util.Map;

public interface RepositorySource {
   <T extends UnopenedPack> void loadPacks(Map<String, T> var1, UnopenedPack.UnopenedPackConstructor<T> var2);
}
