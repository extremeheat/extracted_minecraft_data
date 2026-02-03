package net.minecraft.world.level.levelgen.structure.templatesystem.loader;

import java.nio.file.Path;
import java.util.List;
import net.minecraft.IdentifierException;
import net.minecraft.resources.FileToIdConverter;
import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.PackType;
import net.minecraft.util.FileUtil;

public class TemplatePathFactory {
   private final Path sourceDir;

   public TemplatePathFactory(final Path sourceDir, final PackType packType) {
      this(sourceDir.resolve(packType.getDirectory()));
   }

   public TemplatePathFactory(final Path sourceDir) {
      super();
      this.sourceDir = sourceDir;
   }

   public Path createAndValidatePathToStructure(final Identifier id, final FileToIdConverter converter) {
      return this.createAndValidatePathToStructure(converter.idToFile(id));
   }

   public Path createAndValidatePathToStructure(final Identifier resourceLocation) {
      Path namespacePath = this.sourceDir.resolve(resourceLocation.getNamespace());
      List<String> decomposedPath = (List)FileUtil.decomposePath(resourceLocation.getPath()).getOrThrow((msg) -> {
         String var10002 = String.valueOf(resourceLocation);
         return new IdentifierException("Invalid file path '" + var10002 + "': " + msg);
      });
      if (!decomposedPath.stream().allMatch(FileUtil::isPathPartPortable)) {
         throw new IdentifierException("Resource path '" + String.valueOf(resourceLocation) + "' is not portable");
      } else {
         return FileUtil.resolvePath(namespacePath, decomposedPath);
      }
   }
}
