package net.minecraft.world.level.validation;

import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.PathMatcher;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import net.minecraft.util.Util;
import org.jspecify.annotations.Nullable;

public class DirectoryValidator {
   private static final int FILE_ATTRIBUTE_REPARSE_POINT = 1024;
   private static final boolean IS_WINDOWS;
   private final PathMatcher symlinkTargetAllowList;

   public DirectoryValidator(final PathMatcher symlinkTargetAllowList) {
      super();
      this.symlinkTargetAllowList = symlinkTargetAllowList;
   }

   public @Nullable Path validateAndResolveLink(final Path path) throws IOException, ContentValidationException {
      return this.validateAndResolveLink(path, Files.readAttributes(path, BasicFileAttributes.class, LinkOption.NOFOLLOW_LINKS));
   }

   public @Nullable Path validateAndResolveLink(final Path path, final BasicFileAttributes attributes) throws IOException, ContentValidationException {
      if (attributes.isSymbolicLink()) {
         Path target = Files.readSymbolicLink(path);
         if (!this.symlinkTargetAllowList.matches(target)) {
            throw new ContentValidationException(path, List.of(new ForbiddenLinkInfo(path, target)));
         } else {
            return target;
         }
      } else {
         if (IS_WINDOWS && attributes.isOther()) {
            Map<String, Object> dosAttributes = Files.readAttributes(path, "dos:attributes", LinkOption.NOFOLLOW_LINKS);
            int flags = (Integer)dosAttributes.get("attributes");
            if ((flags & 1024) != 0) {
               throw new ContentValidationException(path, List.of(new ForbiddenLinkInfo(path, path.toRealPath(), true)));
            }
         }

         return null;
      }
   }

   public void validateDirectory(Path directory, final boolean allowTopSymlink) throws IOException, ContentValidationException {
      BasicFileAttributes targetAttributes;
      try {
         targetAttributes = Files.readAttributes(directory, BasicFileAttributes.class, LinkOption.NOFOLLOW_LINKS);
      } catch (NoSuchFileException var5) {
         return;
      }

      if (targetAttributes.isRegularFile()) {
         throw new IOException("Path " + String.valueOf(directory) + " is not a directory");
      } else {
         if (allowTopSymlink) {
            if (targetAttributes.isSymbolicLink()) {
               directory = Files.readSymbolicLink(directory);
            }
         } else {
            this.validateAndResolveLink(directory, targetAttributes);
         }

         this.validateKnownDirectory(directory);
      }
   }

   public void validateKnownDirectory(final Path directory) throws IOException, ContentValidationException {
      final List<ForbiddenLinkInfo> issues = new ArrayList();
      Files.walkFileTree(directory, new SimpleFileVisitor<Path>() {
         {
            Objects.requireNonNull(DirectoryValidator.this);
         }

         private void validateSymlink(final Path path, final BasicFileAttributes attrs) throws IOException {
            try {
               DirectoryValidator.this.validateAndResolveLink(path, attrs);
            } catch (ContentValidationException e) {
               issues.addAll(e.getIssues());
            }

         }

         public FileVisitResult preVisitDirectory(final Path dir, final BasicFileAttributes attrs) throws IOException {
            this.validateSymlink(dir, attrs);
            return super.preVisitDirectory(dir, attrs);
         }

         public FileVisitResult visitFile(final Path file, final BasicFileAttributes attrs) throws IOException {
            this.validateSymlink(file, attrs);
            return super.visitFile(file, attrs);
         }
      });
      if (!issues.isEmpty()) {
         throw new ContentValidationException(directory, issues);
      }
   }

   static {
      IS_WINDOWS = Util.getPlatform() == Util.OS.WINDOWS;
   }
}
