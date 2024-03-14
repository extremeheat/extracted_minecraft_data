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
import java.util.Map;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.ChatFormatting;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ComponentPath;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.StringWidget;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.components.events.GuiEventListener;
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
import org.slf4j.Logger;

public class PackSelectionScreen extends Screen {
   static final Logger LOGGER = LogUtils.getLogger();
   private static final Component AVAILABLE_TITLE = Component.translatable("pack.available.title");
   private static final Component SELECTED_TITLE = Component.translatable("pack.selected.title");
   private static final Component OPEN_PACK_FOLDER_TITLE = Component.translatable("pack.openFolder");
   private static final int LIST_WIDTH = 200;
   private static final Component DRAG_AND_DROP = Component.translatable("pack.dropInfo").withStyle(ChatFormatting.GRAY);
   private static final Component DIRECTORY_BUTTON_TOOLTIP = Component.translatable("pack.folderInfo");
   private static final int RELOAD_COOLDOWN = 20;
   private static final ResourceLocation DEFAULT_ICON = new ResourceLocation("textures/misc/unknown_pack.png");
   private final HeaderAndFooterLayout layout = new HeaderAndFooterLayout(this);
   private final PackSelectionModel model;
   @Nullable
   private PackSelectionScreen.Watcher watcher;
   private long ticksToReload;
   private TransferableSelectionList availablePackList;
   private TransferableSelectionList selectedPackList;
   private final Path packDir;
   private Button doneButton;
   private final Map<String, ResourceLocation> packIcons = Maps.newHashMap();

   public PackSelectionScreen(PackRepository var1, Consumer<PackRepository> var2, Path var3, Component var4) {
      super(var4);
      this.model = new PackSelectionModel(this::populateLists, this::getPackIcon, var1, var2);
      this.packDir = var3;
      this.watcher = PackSelectionScreen.Watcher.create(var3);
   }

   @Override
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

   @Override
   protected void init() {
      LinearLayout var1 = this.layout.addToHeader(LinearLayout.vertical().spacing(5));
      var1.defaultCellSetting().alignHorizontallyCenter();
      var1.addChild(new StringWidget(this.getTitle(), this.font));
      var1.addChild(new StringWidget(DRAG_AND_DROP, this.font));
      this.availablePackList = this.addRenderableWidget(new TransferableSelectionList(this.minecraft, this, 200, this.height - 66, AVAILABLE_TITLE));
      this.selectedPackList = this.addRenderableWidget(new TransferableSelectionList(this.minecraft, this, 200, this.height - 66, SELECTED_TITLE));
      LinearLayout var2 = this.layout.addToFooter(LinearLayout.horizontal().spacing(8));
      var2.addChild(
         Button.builder(OPEN_PACK_FOLDER_TITLE, var1x -> Util.getPlatform().openUri(this.packDir.toUri()))
            .tooltip(Tooltip.create(DIRECTORY_BUTTON_TOOLTIP))
            .build()
      );
      this.doneButton = var2.addChild(Button.builder(CommonComponents.GUI_DONE, var1x -> this.onClose()).build());
      this.reload();
      this.layout.visitWidgets(var1x -> {
      });
      this.repositionElements();
   }

   @Override
   protected void repositionElements() {
      this.layout.arrangeElements();
      this.availablePackList.updateSize(200, this.layout);
      this.availablePackList.setX(this.width / 2 - 15 - 200);
      this.selectedPackList.updateSize(200, this.layout);
      this.selectedPackList.setX(this.width / 2 + 15);
   }

   @Override
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

   private void populateLists() {
      this.updateList(this.selectedPackList, this.model.getSelected());
      this.updateList(this.availablePackList, this.model.getUnselected());
      this.doneButton.active = !this.selectedPackList.children().isEmpty();
   }

   private void updateList(TransferableSelectionList var1, Stream<PackSelectionModel.Entry> var2) {
      var1.children().clear();
      TransferableSelectionList.PackEntry var3 = var1.getSelected();
      String var4 = var3 == null ? "" : var3.getPackId();
      var1.setSelected(null);
      var2.forEach(var3x -> {
         TransferableSelectionList.PackEntry var4xx = new TransferableSelectionList.PackEntry(this.minecraft, var1, var3x);
         var1.children().add(var4xx);
         if (var3x.getId().equals(var4)) {
            var1.setSelected(var4xx);
         }
      });
   }

   public void updateFocus(TransferableSelectionList var1) {
      TransferableSelectionList var2 = this.selectedPackList == var1 ? this.availablePackList : this.selectedPackList;
      this.changeFocus(ComponentPath.path(var2.getFirstElement(), var2, this));
   }

   public void clearSelected() {
      this.selectedPackList.setSelected(null);
      this.availablePackList.setSelected(null);
   }

   private void reload() {
      this.model.findNewPacks();
      this.populateLists();
      this.ticksToReload = 0L;
      this.packIcons.clear();
   }

   protected static void copyPacks(Minecraft var0, List<Path> var1, Path var2) {
      MutableBoolean var3 = new MutableBoolean();
      var1.forEach(var2x -> {
         try (Stream var3xx = Files.walk(var2x)) {
            var3xx.forEach(var3xx -> {
               try {
                  Util.copyBetweenDirs(var2x.getParent(), var2, var3xx);
               } catch (IOException var5) {
                  LOGGER.warn("Failed to copy datapack file  from {} to {}", new Object[]{var3xx, var2, var5});
                  var3.setTrue();
               }
            });
         } catch (IOException var8) {
            LOGGER.warn("Failed to copy datapack file from {} to {}", var2x, var2);
            var3.setTrue();
         }
      });
      if (var3.isTrue()) {
         SystemToast.onPackCopyFailure(var0, var2.toString());
      }
   }

   @Override
   public void onFilesDrop(List<Path> var1) {
      String var2 = extractPackNames(var1).collect(Collectors.joining(", "));
      this.minecraft
         .setScreen(
            new ConfirmScreen(
               var2x -> {
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
                        String var11 = extractPackNames(var4).collect(Collectors.joining(", "));
                        this.minecraft
                           .setScreen(
                              new AlertScreen(
                                 () -> this.minecraft.setScreen(this),
                                 Component.translatable("pack.dropRejected.title"),
                                 Component.translatable("pack.dropRejected.message", var11)
                              )
                           );
                        return;
                     }
                  }
         
                  this.minecraft.setScreen(this);
               },
               Component.translatable("pack.dropConfirm"),
               Component.literal(var2)
            )
         );
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
            ResourceLocation var6 = new ResourceLocation(
               "minecraft", "pack/" + Util.sanitizeName(var5, ResourceLocation::validPathChar) + "/" + Hashing.sha1().hashUnencodedChars(var5) + "/icon"
            );

            try (InputStream var7 = (InputStream)var4.get()) {
               NativeImage var8 = NativeImage.read(var7);
               var1.register(var6, new DynamicTexture(var8));
               var9 = var6;
            }
         }

         return var9;
      } catch (Exception var14) {
         LOGGER.warn("Failed to load icon from pack {}", var2.getId(), var14);
         return DEFAULT_ICON;
      }
   }

   private ResourceLocation getPackIcon(Pack var1) {
      return this.packIcons.computeIfAbsent(var1.getId(), var2 -> this.loadPackIcon(this.minecraft.getTextureManager(), var1));
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

            try (DirectoryStream var2 = Files.newDirectoryStream(var1)) {
               for(Path var4 : var2) {
                  if (Files.isDirectory(var4, LinkOption.NOFOLLOW_LINKS)) {
                     this.watchDir(var4);
                  }
               }
            }
         } catch (Exception var7) {
            this.watcher.close();
            throw var7;
         }
      }

      @Nullable
      public static PackSelectionScreen.Watcher create(Path var0) {
         try {
            return new PackSelectionScreen.Watcher(var0);
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
                  if (Files.isDirectory(var6, LinkOption.NOFOLLOW_LINKS)) {
                     this.watchDir(var6);
                  }
               }
            }

            var2.reset();
         }

         return var1;
      }

      @Override
      public void close() throws IOException {
         this.watcher.close();
      }
   }
}
