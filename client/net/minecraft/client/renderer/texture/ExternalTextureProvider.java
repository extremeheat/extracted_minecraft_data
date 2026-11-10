package net.minecraft.client.renderer.texture;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Objects;
import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.resources.ResourceManager;

public class ExternalTextureProvider implements TextureProvider<ExternalTexture, TextureHandle> {
   private final TextureHandle handle;
   private final Dumper dumper;

   public ExternalTextureProvider(final TextureHandle handle, final Dumper dumper) {
      super();
      this.handle = handle;
      this.dumper = dumper;
   }

   public ExternalTextureProvider(final TextureResources resources) {
      Objects.requireNonNull(resources);
      this(resources, resources::dumpContents);
   }

   public ExternalTexture prepareState(final ResourceManager resourceManager, final Identifier identifier) throws IOException {
      return new ExternalTexture(this.handle);
   }

   public ExternalTexture missingState() {
      return new ExternalTexture(this.handle);
   }

   public TextureHandle createTexture(final ExternalTexture state, final Identifier identifier) {
      return state.handle;
   }

   public void dumpContents(final Identifier identifier, final Path outputDirectory, final TextureHandle texture) {
      this.dumper.dumpContents(identifier, outputDirectory);
   }

   public void releaseTexture(final TextureHandle texture) {
   }

   public static record ExternalTexture(TextureHandle handle) implements TextureProvider.IntermediaryResources {
      public ExternalTexture {
         super();
      }

      public void close() {
      }
   }

   @FunctionalInterface
   public interface Dumper {
      void dumpContents(Identifier identifier, Path outputDirectory);
   }
}
