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
import java.util.Iterator;
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
import net.minecraft.client.gui.components.AbstractSelectionList;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.StringWidget;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.components.events.ContainerEventHandler;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.components.toasts.SystemToast;
import net.minecraft.client.gui.layouts.HeaderAndFooterLayout;
import net.minecraft.client.gui.layouts.LinearLayout;
import net.minecraft.client.gui.screens.AlertScreen;
import net.minecraft.client.gui.screens.ConfirmScreen;
import net.minecraft.client.gui.screens.NoticeWithLinkScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.texture.AbstractTexture;
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
   private static final Component DRAG_AND_DROP;
   private static final Component DIRECTORY_BUTTON_TOOLTIP;
   private static final int RELOAD_COOLDOWN = 20;
   private static final ResourceLocation DEFAULT_ICON;
   private final HeaderAndFooterLayout layout = new HeaderAndFooterLayout(this);
   private final PackSelectionModel model;
   @Nullable
   private Watcher watcher;
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
      LinearLayout var1 = (LinearLayout)this.layout.addToHeader(LinearLayout.vertical().spacing(5));
      var1.defaultCellSetting().alignHorizontallyCenter();
      var1.addChild(new StringWidget(this.getTitle(), this.font));
      var1.addChild(new StringWidget(DRAG_AND_DROP, this.font));
      this.availablePackList = (TransferableSelectionList)this.addRenderableWidget(new TransferableSelectionList(this.minecraft, this, 200, this.height - 66, AVAILABLE_TITLE));
      this.selectedPackList = (TransferableSelectionList)this.addRenderableWidget(new TransferableSelectionList(this.minecraft, this, 200, this.height - 66, SELECTED_TITLE));
      LinearLayout var2 = (LinearLayout)this.layout.addToFooter(LinearLayout.horizontal().spacing(8));
      var2.addChild(Button.builder(OPEN_PACK_FOLDER_TITLE, (var1x) -> {
         Util.getPlatform().openPath(this.packDir);
      }).tooltip(Tooltip.create(DIRECTORY_BUTTON_TOOLTIP)).build());
      this.doneButton = (Button)var2.addChild(Button.builder(CommonComponents.GUI_DONE, (var1x) -> {
         this.onClose();
      }).build());
      this.reload();
      this.layout.visitWidgets((var1x) -> {
         AbstractWidget var10000 = (AbstractWidget)this.addRenderableWidget(var1x);
      });
      this.repositionElements();
   }

   protected void repositionElements() {
      this.layout.arrangeElements();
      this.availablePackList.updateSize(200, this.layout);
      this.availablePackList.setX(this.width / 2 - 15 - 200);
      this.selectedPackList.updateSize(200, this.layout);
      this.selectedPackList.setX(this.width / 2 + 15);
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

   private void populateLists() {
      this.updateList(this.selectedPackList, this.model.getSelected());
      this.updateList(this.availablePackList, this.model.getUnselected());
      this.doneButton.active = !this.selectedPackList.children().isEmpty();
   }

   private void updateList(TransferableSelectionList var1, Stream<PackSelectionModel.Entry> var2) {
      var1.children().clear();
      TransferableSelectionList.PackEntry var3 = (TransferableSelectionList.PackEntry)var1.getSelected();
      String var4 = var3 == null ? "" : var3.getPackId();
      var1.setSelected((AbstractSelectionList.Entry)null);
      var2.forEach((var3x) -> {
         TransferableSelectionList.PackEntry var4x = new TransferableSelectionList.PackEntry(this.minecraft, var1, var3x);
         var1.children().add(var4x);
         if (var3x.getId().equals(var4)) {
            var1.setSelected(var4x);
         }

      });
   }

   public void updateFocus(TransferableSelectionList var1) {
      TransferableSelectionList var2 = this.selectedPackList == var1 ? this.availablePackList : this.selectedPackList;
      this.changeFocus(ComponentPath.path((GuiEventListener)var2.getFirstElement(), (ContainerEventHandler[])(var2, this)));
   }

   public void clearSelected() {
      this.selectedPackList.setSelected((AbstractSelectionList.Entry)null);
      this.availablePackList.setSelected((AbstractSelectionList.Entry)null);
   }

   private void reload() {
      this.model.findNewPacks();
      this.populateLists();
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
            PackDetector var5 = new PackDetector<Path>(this, this.minecraft.directoryValidator()) {
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
            Iterator var7 = var1.iterator();

            while(var7.hasNext()) {
               Path var8 = (Path)var7.next();

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
               this.minecraft.setScreen(NoticeWithLinkScreen.createPackSymlinkWarningScreen(() -> {
                  this.minecraft.setScreen(this);
               }));
               return;
            }

            if (!var3.isEmpty()) {
               copyPacks(this.minecraft, var3, this.packDir);
               this.reload();
            }

            if (!var4.isEmpty()) {
               String var11 = (String)extractPackNames(var4).collect(Collectors.joining(", "));
               this.minecraft.setScreen(new AlertScreen(() -> {
                  this.minecraft.setScreen(this);
               }, Component.translatable("pack.dropRejected.title"), Component.translatable("pack.dropRejected.message", var11)));
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
         PackResources var3 = var2.open();

         ResourceLocation var15;
         label70: {
            ResourceLocation var9;
            try {
               IoSupplier var4 = var3.getRootResource("pack.png");
               if (var4 == null) {
                  var15 = DEFAULT_ICON;
                  break label70;
               }

               String var5 = var2.getId();
               String var10000 = Util.sanitizeName(var5, ResourceLocation::validPathChar);
               ResourceLocation var6 = ResourceLocation.withDefaultNamespace("pack/" + var10000 + "/" + String.valueOf(Hashing.sha1().hashUnencodedChars(var5)) + "/icon");
               InputStream var7 = (InputStream)var4.get();

               try {
                  NativeImage var8 = NativeImage.read(var7);
                  var1.register((ResourceLocation)var6, (AbstractTexture)(new DynamicTexture(var8)));
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
            } catch (Throwable var13) {
               if (var3 != null) {
                  try {
                     var3.close();
                  } catch (Throwable var10) {
                     var13.addSuppressed(var10);
                  }
               }

               throw var13;
            }

            if (var3 != null) {
               var3.close();
            }

            return var9;
         }

         if (var3 != null) {
            var3.close();
         }

         return var15;
      } catch (Exception var14) {
         LOGGER.warn("Failed to load icon from pack {}", var2.getId(), var14);
         return DEFAULT_ICON;
      }
   }

   private ResourceLocation getPackIcon(Pack var1) {
      return (ResourceLocation)this.packIcons.computeIfAbsent(var1.getId(), (var2) -> {
         return this.loadPackIcon(this.minecraft.getTextureManager(), var1);
      });
   }

   static {
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
               Iterator var3 = var2.iterator();

               while(var3.hasNext()) {
                  Path var4 = (Path)var3.next();
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

      @Nullable
      public static Watcher create(Path var0) {
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
            List var3 = var2.pollEvents();
            Iterator var4 = var3.iterator();

            while(var4.hasNext()) {
               WatchEvent var5 = (WatchEvent)var4.next();
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
