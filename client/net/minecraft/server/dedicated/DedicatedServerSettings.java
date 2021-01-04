package net.minecraft.server.dedicated;

import java.nio.file.Path;
import java.util.function.UnaryOperator;

public class DedicatedServerSettings {
   private final Path source;
   private DedicatedServerProperties properties;

   public DedicatedServerSettings(Path var1) {
      super();
      this.source = var1;
      this.properties = DedicatedServerProperties.fromFile(var1);
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
