package net.minecraft.core;

import com.mojang.serialization.Lifecycle;
import java.util.Optional;
import net.minecraft.server.packs.repository.KnownPack;

public record RegistrationInfo(Optional<KnownPack> knownPackInfo, Lifecycle lifecycle) {
   public static final RegistrationInfo BUILT_IN = new RegistrationInfo(Optional.empty(), Lifecycle.stable());

   public RegistrationInfo(Optional<KnownPack> var1, Lifecycle var2) {
      super();
      this.knownPackInfo = var1;
      this.lifecycle = var2;
   }
}
