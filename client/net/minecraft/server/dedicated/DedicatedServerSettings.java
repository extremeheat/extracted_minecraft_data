package net.minecraft.server.dedicated;

import java.nio.file.Path;
import java.util.function.UnaryOperator;
import net.minecraft.core.RegistryAccess;

public class DedicatedServerSettings {
   private final Path source;
   private DedicatedServerProperties properties;

   public DedicatedServerSettings(RegistryAccess var1, Path var2) {
      super();
      this.source = var2;
      this.properties = DedicatedServerProperties.fromFile(var1, var2);
   }

   public DedicatedServerProperties getProperties() {
      return this.properties;
   }

   public void forceSave() {
      this.properties.store(this.source);
   }

   public DedicatedServerSettings update(UnaryOperator<DedicatedServerProperties> var1) {
      (this.properties = (DedicatedServerProperties)var1.apply(this.properties)).store(this.source);
      return this;
   }
}
