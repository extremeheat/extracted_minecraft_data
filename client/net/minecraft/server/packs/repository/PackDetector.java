package net.minecraft.server.packs.repository;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.world.level.validation.DirectoryValidator;
import net.minecraft.world.level.validation.ForbiddenSymlinkInfo;

public abstract class PackDetector<T> {
   private final DirectoryValidator validator;

   protected PackDetector(DirectoryValidator var1) {
      super();
      this.validator = var1;
   }

   @Nullable
   public T detectPackResources(Path var1, List<ForbiddenSymlinkInfo> var2) throws IOException {
      Path var3 = var1;

      BasicFileAttributes var4;
      try {
         var4 = Files.readAttributes(var1, BasicFileAttributes.class, LinkOption.NOFOLLOW_LINKS);
      } catch (NoSuchFileException var6) {
         return null;
      }

      if (var4.isSymbolicLink()) {
         this.validator.validateSymlink(var1, var2);
         if (!var2.isEmpty()) {
            return null;
         }

         var3 = Files.readSymbolicLink(var1);
         var4 = Files.readAttributes(var3, BasicFileAttributes.class, LinkOption.NOFOLLOW_LINKS);
      }

      if (var4.isDirectory()) {
         this.validator.validateKnownDirectory(var3, var2);
         if (!var2.isEmpty()) {
            return null;
         } else {
            return !Files.isRegularFile(var3.resolve("pack.mcmeta"), new LinkOption[0]) ? null : this.createDirectoryPack(var3);
         }
      } else {
         return var4.isRegularFile() && var3.getFileName().toString().endsWith(".zip") ? this.createZipPack(var3) : null;
      }
   }

   @Nullable
   protected abstract T createZipPack(Path var1) throws IOException;

   @Nullable
   protected abstract T createDirectoryPack(Path var1) throws IOException;
}
