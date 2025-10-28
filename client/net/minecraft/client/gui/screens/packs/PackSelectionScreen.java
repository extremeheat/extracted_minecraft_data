package net.minecraft.client.gui.screens.packs;

import com.google.common.collect.Maps;
import com.google.common.hash.Hashing;
import com.mojang.blaze3d.platform.NativeImage;
import com.mojang.logging.LogUtils;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import net.minecraft.ChatFormatting;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.StringWidget;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.components.toasts.SystemToast;
import net.minecraft.client.gui.layouts.HeaderAndFooterLayout;
import net.minecraft.client.gui.layouts.LinearLayout;
import net.minecraft.client.gui.screens.AlertScreen;
import net.minecraft.client.gui.screens.ConfirmScreen;
import net.minecraft.client.gui.screens.NoticeWithLinkScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackResources;
import net.minecraft.server.packs.repository.Pack;
import net.minecraft.server.packs.repository.PackDetector;
import net.minecraft.server.packs.repository.PackRepository;
import net.minecraft.server.packs.resources.IoSupplier;
import org.apache.commons.lang3.mutable.MutableBoolean;
import org.jspecify.annotations.Nullable;
import org.slf4j.Logger;

public class PackSelectionScreen extends Screen {
   static final Logger LOGGER = LogUtils.getLogger();
   private static final Component AVAILABLE_TITLE = Component.translatable("pack.available.title");
   private static final Component SELECTED_TITLE = Component.translatable("pack.selected.title");
   private static final Component OPEN_PACK_FOLDER_TITLE = Component.translatable("pack.openFolder");
   private static final Component SEARCH;
   private static final int LIST_WIDTH = 200;
   private static final int HEADER_ELEMENT_SPACING = 4;
   private static final int SEARCH_BOX_HEIGHT = 15;
   private static final Component DRAG_AND_DROP;
   private static final Component DIRECTORY_BUTTON_TOOLTIP;
   private static final int RELOAD_COOLDOWN = 20;
   private static final ResourceLocation DEFAULT_ICON;
   private final HeaderAndFooterLayout layout = new HeaderAndFooterLayout(this);
   private final PackSelectionModel model;
   private @Nullable Watcher watcher;
   private long ticksToReload;
   private @Nullable TransferableSelectionList availablePackList;
   private @Nullable TransferableSelectionList selectedPackList;
   private @Nullable EditBox search;
   private final Path packDir;
   private @Nullable Button doneButton;
   private final Map<String, ResourceLocation> packIcons = Maps.newHashMap();

   public PackSelectionScreen(PackRepository var1, Consumer<PackRepository> var2, Path var3, Component var4) {
      super(var4);
      this.model = new PackSelectionModel(this::populateLists, this::getPackIcon, var1, var2);
      this.packDir = var3;
      this.watcher = PackSelectionScreen.Watcher.create(var3);
   }

   public void onClose() {
      this.model.commit();
      this.closeWatcher();
   }

   private void closeWatcher() {
      if (this.watcher != null) {
         try {
            this.watcher.close();
            this.watcher = null;
         } catch (Exception var2) {
         }
      }

   }

   protected void init() {
      HeaderAndFooterLayout var10000 = this.layout;
      Objects.requireNonNull(this.font);
      int var10001 = 4 + 9 + 4;
      Objects.requireNonNull(this.font);
      var10000.setHeaderHeight(var10001 + 9 + 4 + 15 + 4);
      LinearLayout var1 = (LinearLayout)this.layout.addToHeader(LinearLayout.vertical().spacing(4));
      var1.defaultCellSetting().alignHorizontallyCenter();
      var1.addChild(new StringWidget(this.getTitle(), this.font));
      var1.addChild(new StringWidget(DRAG_AND_DROP, this.font));
      this.search = (EditBox)var1.addChild(new EditBox(this.font, 0, 0, 200, 15, Component.empty()));
      this.search.setHint(SEARCH);
      this.search.setResponder(this::updateFilteredEntries);
      this.availablePackList = (TransferableSelectionList)this.layout.addToContents(new TransferableSelectionList(this.minecraft, this, 200, this.height - 66, AVAILABLE_TITLE));
      this.selectedPackList = (TransferableSelectionList)this.layout.addToContents(new TransferableSelectionList(this.minecraft, this, 200, this.height - 66, SELECTED_TITLE));
      LinearLayout var2 = (LinearLayout)this.layout.addToFooter(LinearLayout.horizontal().spacing(8));
      var2.addChild(Button.builder(OPEN_PACK_FOLDER_TITLE, (var1x) -> Util.getPlatform().openPath(this.packDir)).tooltip(Tooltip.create(DIRECTORY_BUTTON_TOOLTIP)).build());
      this.doneButton = (Button)var2.addChild(Button.builder(CommonComponents.GUI_DONE, (var1x) -> this.onClose()).build());
      this.layout.visitWidgets((var1x) -> {
         AbstractWidget var10000 = (AbstractWidget)this.addRenderableWidget(var1x);
      });
      this.repositionElements();
      this.setInitialFocus(this.search);
      this.reload();
   }

   private void updateFilteredEntries(String var1) {
      this.filterEntries(var1, this.model.getSelected(), this.selectedPackList);
      this.filterEntries(var1, this.model.getUnselected(), this.availablePackList);
   }

   private void filterEntries(String var1, Stream<PackSelectionModel.Entry> var2, @Nullable TransferableSelectionList var3) {
      if (var3 != null) {
         String var4 = var1.toLowerCase(Locale.ROOT);
         Stream var5 = var2.filter((var2x) -> var1.isBlank() || var2x.getId().toLowerCase(Locale.ROOT).contains(var4) || var2x.getTitle().getString().toLowerCase(Locale.ROOT).contains(var4) || var2x.getDescription().getString().toLowerCase(Locale.ROOT).contains(var4));
         var3.updateList(var5, (PackSelectionModel.EntryBase)null);
      }
   }

   protected void repositionElements() {
      this.layout.arrangeElements();
      if (this.availablePackList != null) {
         this.availablePackList.updateSizeAndPosition(200, this.layout.getContentHeight(), this.width / 2 - 15 - 200, this.layout.getHeaderHeight());
      }

      if (this.selectedPackList != null) {
         this.selectedPackList.updateSizeAndPosition(200, this.layout.getContentHeight(), this.width / 2 + 15, this.layout.getHeaderHeight());
      }

   }

   public void tick() {
      if (this.watcher != null) {
         try {
            if (this.watcher.pollForChanges()) {
               this.ticksToReload = 20L;
            }
         } catch (IOException var2) {
            LOGGER.warn("Failed to poll for directory {} changes, stopping", this.packDir);
            this.closeWatcher();
         }
      }

      if (this.ticksToReload > 0L && --this.ticksToReload == 0L) {
         this.reload();
      }

   }

   private void populateLists(PackSelectionModel.EntryBase var1) {
      if (this.selectedPackList != null) {
         this.selectedPackList.updateList(this.model.getSelected(), var1);
      }

      if (this.availablePackList != null) {
         this.availablePackList.updateList(this.model.getUnselected(), var1);
      }

      if (this.search != null) {
         this.updateFilteredEntries(this.search.getValue());
      }

      if (this.doneButton != null) {
         this.doneButton.active = !this.selectedPackList.children().isEmpty();
      }

   }

   private void reload() {
      this.model.findNewPacks();
      this.populateLists((PackSelectionModel.EntryBase)null);
      this.ticksToReload = 0L;
      this.packIcons.clear();
   }

   protected static void copyPacks(Minecraft var0, List<Path> var1, Path var2) {
      MutableBoolean var3 = new MutableBoolean();
      var1.forEach((var2x) -> {
         try {
            Stream var3x = Files.walk(var2x);

            try {
               var3x.forEach((var3xx) -> {
                  try {
                     Util.copyBetweenDirs(var2x.getParent(), var2, var3xx);
                  } catch (IOException var5) {
                     LOGGER.warn("Failed to copy datapack file  from {} to {}", new Object[]{var3xx, var2, var5});
                     var3.setTrue();
                  }

               });
            } catch (Throwable var7) {
               if (var3x != null) {
                  try {
                     var3x.close();
                  } catch (Throwable var6) {
                     var7.addSuppressed(var6);
                  }
               }

               throw var7;
            }

            if (var3x != null) {
               var3x.close();
            }
         } catch (IOException var8) {
            LOGGER.warn("Failed to copy datapack file from {} to {}", var2x, var2);
            var3.setTrue();
         }

      });
      if (var3.isTrue()) {
         SystemToast.onPackCopyFailure(var0, var2.toString());
      }

   }

   public void onFilesDrop(List<Path> var1) {
      String var2 = (String)extractPackNames(var1).collect(Collectors.joining(", "));
      this.minecraft.setScreen(new ConfirmScreen((var2x) -> {
         if (var2x) {
            ArrayList var3 = new ArrayList(var1.size());
            HashSet var4 = new HashSet(var1);
            PackDetector var5 = new PackDetector<Path>(this.minecraft.directoryValidator()) {
               protected Path createZipPack(Path var1) {
                  return var1;
               }

               protected Path createDirectoryPack(Path var1) {
                  return var1;
               }

               // $FF: synthetic method
               protected Object createDirectoryPack(final Path var1) throws IOException {
                  return this.createDirectoryPack(var1);
               }

               // $FF: synthetic method
               protected Object createZipPack(final Path var1) throws IOException {
                  return this.createZipPack(var1);
               }
            };
            ArrayList var6 = new ArrayList();

            for(Path var8 : var1) {
               try {
                  Path var9 = (Path)var5.detectPackResources(var8, var6);
                  if (var9 == null) {
                     LOGGER.warn("Path {} does not seem like pack", var8);
                  } else {
                     var3.add(var9);
                     var4.remove(var9);
                  }
               } catch (IOException var10) {
                  LOGGER.warn("Failed to check {} for packs", var8, var10);
               }
            }

            if (!var6.isEmpty()) {
               this.minecraft.setScreen(NoticeWithLinkScreen.createPackSymlinkWarningScreen(() -> this.minecraft.setScreen(this)));
               return;
            }

            if (!var3.isEmpty()) {
               copyPacks(this.minecraft, var3, this.packDir);
               this.reload();
            }

            if (!var4.isEmpty()) {
               String var11 = (String)extractPackNames(var4).collect(Collectors.joining(", "));
               this.minecraft.setScreen(new AlertScreen(() -> this.minecraft.setScreen(this), Component.translatable("pack.dropRejected.title"), Component.translatable("pack.dropRejected.message", var11)));
               return;
            }
         }

         this.minecraft.setScreen(this);
      }, Component.translatable("pack.dropConfirm"), Component.literal(var2)));
   }

   private static Stream<String> extractPackNames(Collection<Path> var0) {
      return var0.stream().map(Path::getFileName).map(Path::toString);
   }

   private ResourceLocation loadPackIcon(TextureManager var1, Pack var2) {
      try {
         ResourceLocation var9;
         try (PackResources var3 = var2.open()) {
            IoSupplier var4 = var3.getRootResource("pack.png");
            if (var4 == null) {
               return DEFAULT_ICON;
            }

            String var5 = var2.getId();
            String var10000 = Util.sanitizeName(var5, ResourceLocation::validPathChar);
            ResourceLocation var6 = ResourceLocation.withDefaultNamespace("pack/" + var10000 + "/" + String.valueOf(Hashing.sha1().hashUnencodedChars(var5)) + "/icon");
            InputStream var7 = (InputStream)var4.get();

            try {
               NativeImage var8 = NativeImage.read(var7);
               Objects.requireNonNull(var6);
               var1.register(var6, new DynamicTexture(var6::toString, var8));
               var9 = var6;
            } catch (Throwable var12) {
               if (var7 != null) {
                  try {
                     var7.close();
                  } catch (Throwable var11) {
                     var12.addSuppressed(var11);
                  }
               }

               throw var12;
            }

            if (var7 != null) {
               var7.close();
            }
         }

         return var9;
      } catch (Exception var14) {
         LOGGER.warn("Failed to load icon from pack {}", var2.getId(), var14);
         return DEFAULT_ICON;
      }
   }

   private ResourceLocation getPackIcon(Pack var1) {
      return (ResourceLocation)this.packIcons.computeIfAbsent(var1.getId(), (var2) -> this.loadPackIcon(this.minecraft.getTextureManager(), var1));
   }

   static {
      SEARCH = Component.translatable("gui.packSelection.search").withStyle(EditBox.SEARCH_HINT_STYLE);
      DRAG_AND_DROP = Component.translatable("pack.dropInfo").withStyle(ChatFormatting.GRAY);
      DIRECTORY_BUTTON_TOOLTIP = Component.translatable("pack.folderInfo");
      DEFAULT_ICON = ResourceLocation.withDefaultNamespace("textures/misc/unknown_pack.png");
   }

   static class Watcher implements AutoCloseable {
      private final WatchService watcher;
      private final Path packPath;

      public Watcher(Path var1) throws IOException {
         super();
         this.packPath = var1;
         this.watcher = var1.getFileSystem().newWatchService();

         try {
            this.watchDir(var1);
            DirectoryStream var2 = Files.newDirectoryStream(var1);

            try {
               for(Path var4 : var2) {
                  if (Files.isDirectory(var4, new LinkOption[]{LinkOption.NOFOLLOW_LINKS})) {
                     this.watchDir(var4);
                  }
               }
            } catch (Throwable var6) {
               if (var2 != null) {
                  try {
                     var2.close();
                  } catch (Throwable var5) {
                     var6.addSuppressed(var5);
                  }
               }

               throw var6;
            }

            if (var2 != null) {
               var2.close();
            }

         } catch (Exception var7) {
            this.watcher.close();
            throw var7;
         }
      }

      public static @Nullable Watcher create(Path var0) {
         try {
            return new Watcher(var0);
         } catch (IOException var2) {
            PackSelectionScreen.LOGGER.warn("Failed to initialize pack directory {} monitoring", var0, var2);
            return null;
         }
      }

      private void watchDir(Path var1) throws IOException {
         var1.register(this.watcher, StandardWatchEventKinds.ENTRY_CREATE, StandardWatchEventKinds.ENTRY_DELETE, StandardWatchEventKinds.ENTRY_MODIFY);
      }

      public boolean pollForChanges() throws IOException {
         boolean var1 = false;

         WatchKey var2;
         while((var2 = this.watcher.poll()) != null) {
            for(WatchEvent var5 : var2.pollEvents()) {
               var1 = true;
               if (var2.watchable() == this.packPath && var5.kind() == StandardWatchEventKinds.ENTRY_CREATE) {
                  Path var6 = this.packPath.resolve((Path)var5.context());
                  if (Files.isDirectory(var6, new LinkOption[]{LinkOption.NOFOLLOW_LINKS})) {
                     this.watchDir(var6);
                  }
               }
            }

            var2.reset();
         }

         return var1;
      }

      public void close() throws IOException {
         this.watcher.close();
      }
   }
}
