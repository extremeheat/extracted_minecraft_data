package net.minecraft.util.filefix.virtualfilesystem;

import com.google.common.annotations.VisibleForTesting;
import com.mojang.logging.LogUtils;
import java.io.IOException;
import java.nio.file.CopyOption;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.FileStore;
import java.nio.file.FileSystem;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.PathMatcher;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.WatchService;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.UserPrincipalLookupService;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import net.minecraft.util.Util;
import net.minecraft.util.filefix.virtualfilesystem.exception.CowFSCreationException;
import net.minecraft.util.filefix.virtualfilesystem.exception.CowFSSymlinkException;
import org.apache.commons.io.file.PathUtils;
import org.jspecify.annotations.Nullable;
import org.slf4j.Logger;

public class CopyOnWriteFileSystem extends FileSystem {
   private static final String TMP_DIRECTORY_NAME = "file_fix_tmp";
   private static final Set<String> FILE_ATTRIBUTE_VIEWS = Set.of("basic");
   private static final Logger LOGGER = LogUtils.getLogger();
   private final CopyOnWriteFileStore store;
   private final CopyOnWriteFSProvider provider;
   private final Path baseDirectory;
   private final Path tmpDirectory;
   private final CopyOnWriteFSPath rootPath;
   private final AtomicInteger tmpFileIndex = new AtomicInteger();
   private DirectoryNode fileTree;

   private CopyOnWriteFileSystem(final String name, final Path baseDirectory, final Path tmpDirectory) throws IOException {
      super();
      this.baseDirectory = baseDirectory;
      this.tmpDirectory = tmpDirectory;
      this.provider = new CopyOnWriteFSProvider(this);
      this.store = new CopyOnWriteFileStore(name, this);
      this.rootPath = this.getPath("/");
      this.fileTree = this.buildFileTreeFrom(baseDirectory);
   }

   public static CopyOnWriteFileSystem create(final String name, final Path baseDirectory) throws IOException {
      Path tmpDirectory = baseDirectory.resolve("file_fix_tmp");
      if (Files.exists(tmpDirectory, new LinkOption[0])) {
         throw new CowFSCreationException("Temporary directory already exists: " + String.valueOf(tmpDirectory));
      } else {
         CopyOnWriteFileSystem fileSystem = new CopyOnWriteFileSystem(name, baseDirectory, tmpDirectory);
         Files.createDirectory(tmpDirectory);
         return fileSystem;
      }
   }

   private DirectoryNode buildFileTreeFrom(final Path baseDirectory) throws IOException {
      final DirectoryNode fileTree = new DirectoryNode(this.rootPath);
      Files.walkFileTree(baseDirectory, new SimpleFileVisitor<Path>() {
         {
            Objects.requireNonNull(CopyOnWriteFileSystem.this);
         }

         public FileVisitResult visitFile(final Path realPath, final BasicFileAttributes attrs) throws IOException {
            checkAttributes(realPath, attrs);
            CopyOnWriteFSPath cowPath = this.toCowPath(realPath);
            DirectoryNode parentNode = fileTree.directoryByPath((CopyOnWriteFSPath)Objects.requireNonNull(cowPath.getParent()));
            parentNode.addChild(new FileNode(cowPath, realPath, false));
            return FileVisitResult.CONTINUE;
         }

         public FileVisitResult preVisitDirectory(final Path realPath, final BasicFileAttributes attrs) throws IOException {
            checkAttributes(realPath, attrs);
            if (realPath.equals(CopyOnWriteFileSystem.this.tmpDirectory)) {
               return FileVisitResult.SKIP_SUBTREE;
            } else if (realPath.equals(baseDirectory)) {
               return FileVisitResult.CONTINUE;
            } else {
               CopyOnWriteFSPath cowPath = this.toCowPath(realPath);
               DirectoryNode parentNode = fileTree.directoryByPath((CopyOnWriteFSPath)Objects.requireNonNull(cowPath.getParent()));
               parentNode.addChild(new DirectoryNode(cowPath));
               return FileVisitResult.CONTINUE;
            }
         }

         private static void checkAttributes(final Path realPath, final BasicFileAttributes attrs) throws CowFSCreationException {
            if (!attrs.isRegularFile() && !attrs.isDirectory()) {
               throw new CowFSSymlinkException("Cannot build copy-on-write file system when symlink is present: " + String.valueOf(realPath));
            } else if (!Files.isWritable(realPath)) {
               throw new CowFSCreationException("Cannot build copy-on-write file system, missing write access for file: " + String.valueOf(realPath));
            }
         }

         private CopyOnWriteFSPath toCowPath(final Path realPath) {
            return fileTree.path().resolve(baseDirectory.relativize(realPath).toString());
         }
      });
      return fileTree;
   }

   @VisibleForTesting
   protected void resetFileTreeToBaseFolderContent() throws IOException {
      this.fileTree = this.buildFileTreeFrom(this.baseDirectory);
   }

   public CopyOnWriteFSProvider provider() {
      return this.provider;
   }

   public void close() throws IOException {
      if (Files.exists(this.tmpDirectory, new LinkOption[0])) {
         PathUtils.deleteDirectory(this.tmpDirectory);
      }

   }

   public boolean isOpen() {
      return true;
   }

   public boolean isReadOnly() {
      return false;
   }

   public String getSeparator() {
      return this.backingFileSystem().getSeparator();
   }

   public Iterable<Path> getRootDirectories() {
      return List.of(this.rootPath());
   }

   public Iterable<FileStore> getFileStores() {
      return List.of(this.store);
   }

   public Set<String> supportedFileAttributeViews() {
      return FILE_ATTRIBUTE_VIEWS;
   }

   public CopyOnWriteFSPath getPath(final String first, final String... more) {
      return CopyOnWriteFSPath.of(this, first, more);
   }

   public PathMatcher getPathMatcher(final String syntaxAndPattern) {
      throw new UnsupportedOperationException();
   }

   public UserPrincipalLookupService getUserPrincipalLookupService() {
      throw new UnsupportedOperationException();
   }

   public WatchService newWatchService() {
      throw new UnsupportedOperationException();
   }

   public CopyOnWriteFileStore store() {
      return this.store;
   }

   public CopyOnWriteFSPath rootPath() {
      return this.rootPath;
   }

   DirectoryNode fileTree() {
      return this.fileTree;
   }

   public Path baseDirectory() {
      return this.baseDirectory;
   }

   public Path tmpDirectory() {
      return this.tmpDirectory;
   }

   Path createTemporaryFilePath() {
      return this.tmpDirectory.resolve("tmp_" + this.tmpFileIndex.incrementAndGet());
   }

   public FileSystem backingFileSystem() {
      return this.tmpDirectory.getFileSystem();
   }

   private List<Move> collectMoveOperations(final Path outPath, final DirectoryNode folder) {
      List<Move> result = new ArrayList();
      this.collectMoveOperations(outPath, folder, result);
      return result;
   }

   private void collectMoveOperations(final Path outPath, final DirectoryNode folder, final List<Move> result) {
      for(Node childNode : folder.children()) {
         Path target = outPath.resolve((String)Objects.requireNonNull(childNode.name()));
         Objects.requireNonNull(childNode);
         byte var8 = 0;
         //$FF: var8->value
         //0->net/minecraft/util/filefix/virtualfilesystem/FileNode
         //1->net/minecraft/util/filefix/virtualfilesystem/DirectoryNode
         switch (childNode.typeSwitch<invokedynamic>(childNode, var8)) {
            case 0:
               FileNode fileNode = (FileNode)childNode;
               result.add(new Move(fileNode.storagePath(), target));
               break;
            case 1:
               DirectoryNode directoryNode = (DirectoryNode)childNode;
               result.add(new Move((Path)null, target));
               this.collectMoveOperations(target, directoryNode, result);
               break;
            default:
               throw new MatchException((String)null, (Throwable)null);
         }
      }

   }

   public void makeHardLinkCopy(final Path outPath) throws IOException {
      for(Move move : this.collectMoveOperations(outPath, this.fileTree)) {
         if (move.from == null) {
            Files.createDirectory(move.to);
         } else if (Files.isRegularFile(move.from, new LinkOption[0])) {
            Files.createLink(move.to, move.from);
         }
      }

   }

   public void moveFilesToNewFolder(final Path outPath, final CopyOption... options) throws IOException {
      List<Move> moves = this.collectMoveOperations(outPath, this.fileTree);
      int i = 0;

      try {
         for(; i < moves.size(); ++i) {
            Move move = (Move)moves.get(i);
            if (move.from == null) {
               try {
                  Files.createDirectory(move.to);
               } catch (FileAlreadyExistsException var8) {
               }
            } else if (Files.isRegularFile(move.from, new LinkOption[0])) {
               Files.move(move.from, move.to, options);
            }
         }

      } catch (IOException e) {
         LOGGER.error("Encountered error while trying to create new world folder:", e);

         for(int j = 0; j < i; ++j) {
            Move move = (Move)moves.get(j);
            LOGGER.info("Already had moved {} to {}", move.from, move.to);
         }

         LOGGER.info("Trying to undo already run operations");
         --i;

         for(; i >= 0; --i) {
            Move move = (Move)moves.get(i);
            if (Files.isRegularFile(move.to, new LinkOption[0]) && move.from != null) {
               Util.safeMoveFile(move.to, move.from, options);
               LOGGER.info("Reverted move from {} to {}", move.from, move.to);
            } else {
               LOGGER.info("Skipping reverting {} to {} as it's not a file", move.from, move.to);
            }
         }

         LOGGER.info("Successfully reverted back to previous world state");
         throw e;
      }
   }

   public static record Move(@Nullable Path from, Path to) {
      public Move {
         super();
      }
   }
}
