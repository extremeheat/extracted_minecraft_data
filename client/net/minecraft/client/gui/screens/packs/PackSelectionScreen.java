package net.minecraft.client.gui.screens.packs;

import com.google.common.collect.Maps;
import com.google.common.hash.Hashing;
import com.mojang.blaze3d.platform.NativeImage;
import com.mojang.blaze3d.vertex.PoseStack;
import java.io.File;
import java.io.FileNotFoundException;
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
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.toasts.SystemToast;
import net.minecraft.client.gui.screens.ConfirmScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.texture.AbstractTexture;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackResources;
import net.minecraft.server.packs.repository.Pack;
import net.minecraft.server.packs.repository.PackRepository;
import org.apache.commons.lang3.mutable.MutableBoolean;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class PackSelectionScreen extends Screen {
   private static final Logger LOGGER = LogManager.getLogger();
   private static final Component DRAG_AND_DROP;
   private static final Component DIRECTORY_BUTTON_TOOLTIP;
   private static final ResourceLocation DEFAULT_ICON;
   private final PackSelectionModel model;
   private final Screen lastScreen;
   @Nullable
   private PackSelectionScreen.Watcher watcher;
   private long ticksToReload;
   private TransferableSelectionList availablePackList;
   private TransferableSelectionList selectedPackList;
   private final File packDir;
   private Button doneButton;
   private final Map<String, ResourceLocation> packIcons = Maps.newHashMap();

   public PackSelectionScreen(Screen var1, PackRepository var2, Consumer<PackRepository> var3, File var4, Component var5) {
      super(var5);
      this.lastScreen = var1;
      this.model = new PackSelectionModel(this::populateLists, this::getPackIcon, var2, var3);
      this.packDir = var4;
      this.watcher = PackSelectionScreen.Watcher.create(var4);
   }

   public void onClose() {
      this.model.commit();
      this.minecraft.setScreen(this.lastScreen);
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
      this.doneButton = (Button)this.addButton(new Button(this.width / 2 + 4, this.height - 48, 150, 20, CommonComponents.GUI_DONE, (var1) -> {
         this.onClose();
      }));
      this.addButton(new Button(this.width / 2 - 154, this.height - 48, 150, 20, new TranslatableComponent("pack.openFolder"), (var1) -> {
         Util.getPlatform().openFile(this.packDir);
      }, (var1, var2, var3, var4) -> {
         this.renderTooltip(var2, DIRECTORY_BUTTON_TOOLTIP, var3, var4);
      }));
      this.availablePackList = new TransferableSelectionList(this.minecraft, 200, this.height, new TranslatableComponent("pack.available.title"));
      this.availablePackList.setLeftPos(this.width / 2 - 4 - 200);
      this.children.add(this.availablePackList);
      this.selectedPackList = new TransferableSelectionList(this.minecraft, 200, this.height, new TranslatableComponent("pack.selected.title"));
      this.selectedPackList.setLeftPos(this.width / 2 + 4);
      this.children.add(this.selectedPackList);
      this.reload();
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
      var2.forEach((var2x) -> {
         var1.children().add(new TransferableSelectionList.PackEntry(this.minecraft, var1, this, var2x));
      });
   }

   private void reload() {
      this.model.findNewPacks();
      this.populateLists();
      this.ticksToReload = 0L;
      this.packIcons.clear();
   }

   public void render(PoseStack var1, int var2, int var3, float var4) {
      this.renderDirtBackground(0);
      this.availablePackList.render(var1, var2, var3, var4);
      this.selectedPackList.render(var1, var2, var3, var4);
      drawCenteredString(var1, this.font, this.title, this.width / 2, 8, 16777215);
      drawCenteredString(var1, this.font, DRAG_AND_DROP, this.width / 2, 20, 16777215);
      super.render(var1, var2, var3, var4);
   }

   protected static void copyPacks(Minecraft var0, List<Path> var1, Path var2) {
      MutableBoolean var3 = new MutableBoolean();
      var1.forEach((var2x) -> {
         try {
            Stream var3x = Files.walk(var2x);
            Throwable var4 = null;

            try {
               var3x.forEach((var3xx) -> {
                  try {
                     Util.copyBetweenDirs(var2x.getParent(), var2, var3xx);
                  } catch (IOException var5) {
                     LOGGER.warn("Failed to copy datapack file  from {} to {}", var3xx, var2, var5);
                     var3.setTrue();
                  }

               });
            } catch (Throwable var14) {
               var4 = var14;
               throw var14;
            } finally {
               if (var3x != null) {
                  if (var4 != null) {
                     try {
                        var3x.close();
                     } catch (Throwable var13) {
                        var4.addSuppressed(var13);
                     }
                  } else {
                     var3x.close();
                  }
               }

            }
         } catch (IOException var16) {
            LOGGER.warn("Failed to copy datapack file from {} to {}", var2x, var2);
            var3.setTrue();
         }

      });
      if (var3.isTrue()) {
         SystemToast.onPackCopyFailure(var0, var2.toString());
      }

   }

   public void onFilesDrop(List<Path> var1) {
      String var2 = (String)var1.stream().map(Path::getFileName).map(Path::toString).collect(Collectors.joining(", "));
      this.minecraft.setScreen(new ConfirmScreen((var2x) -> {
         if (var2x) {
            copyPacks(this.minecraft, var1, this.packDir.toPath());
            this.reload();
         }

         this.minecraft.setScreen(this);
      }, new TranslatableComponent("pack.dropConfirm"), new TextComponent(var2)));
   }

   private ResourceLocation loadPackIcon(TextureManager var1, Pack var2) {
      try {
         PackResources var3 = var2.open();
         Throwable var4 = null;

         Object var7;
         try {
            InputStream var5 = var3.getRootResource("pack.png");
            Throwable var6 = null;

            try {
               if (var5 != null) {
                  var7 = var2.getId();
                  ResourceLocation var8 = new ResourceLocation("minecraft", "pack/" + Util.sanitizeName((String)var7, ResourceLocation::validPathChar) + "/" + Hashing.sha1().hashUnencodedChars((CharSequence)var7) + "/icon");
                  NativeImage var9 = NativeImage.read(var5);
                  var1.register((ResourceLocation)var8, (AbstractTexture)(new DynamicTexture(var9)));
                  ResourceLocation var10 = var8;
                  return var10;
               }

               var7 = DEFAULT_ICON;
            } catch (Throwable var40) {
               var7 = var40;
               var6 = var40;
               throw var40;
            } finally {
               if (var5 != null) {
                  if (var6 != null) {
                     try {
                        var5.close();
                     } catch (Throwable var39) {
                        var6.addSuppressed(var39);
                     }
                  } else {
                     var5.close();
                  }
               }

            }
         } catch (Throwable var42) {
            var4 = var42;
            throw var42;
         } finally {
            if (var3 != null) {
               if (var4 != null) {
                  try {
                     var3.close();
                  } catch (Throwable var38) {
                     var4.addSuppressed(var38);
                  }
               } else {
                  var3.close();
               }
            }

         }

         return (ResourceLocation)var7;
      } catch (FileNotFoundException var44) {
      } catch (Exception var45) {
         LOGGER.warn("Failed to load icon from pack {}", var2.getId(), var45);
      }

      return DEFAULT_ICON;
   }

   private ResourceLocation getPackIcon(Pack var1) {
      return (ResourceLocation)this.packIcons.computeIfAbsent(var1.getId(), (var2) -> {
         return this.loadPackIcon(this.minecraft.getTextureManager(), var1);
      });
   }

   static {
      DRAG_AND_DROP = (new TranslatableComponent("pack.dropInfo")).withStyle(ChatFormatting.GRAY);
      DIRECTORY_BUTTON_TOOLTIP = new TranslatableComponent("pack.folderInfo");
      DEFAULT_ICON = new ResourceLocation("textures/misc/unknown_pack.png");
   }

   static class Watcher implements AutoCloseable {
      private final WatchService watcher;
      private final Path packPath;

      public Watcher(File var1) throws IOException {
         super();
         this.packPath = var1.toPath();
         this.watcher = this.packPath.getFileSystem().newWatchService();

         try {
            this.watchDir(this.packPath);
            DirectoryStream var2 = Files.newDirectoryStream(this.packPath);
            Throwable var3 = null;

            try {
               Iterator var4 = var2.iterator();

               while(var4.hasNext()) {
                  Path var5 = (Path)var4.next();
                  if (Files.isDirectory(var5, new LinkOption[]{LinkOption.NOFOLLOW_LINKS})) {
                     this.watchDir(var5);
                  }
               }
            } catch (Throwable var14) {
               var3 = var14;
               throw var14;
            } finally {
               if (var2 != null) {
                  if (var3 != null) {
                     try {
                        var2.close();
                     } catch (Throwable var13) {
                        var3.addSuppressed(var13);
                     }
                  } else {
                     var2.close();
                  }
               }

            }

         } catch (Exception var16) {
            this.watcher.close();
            throw var16;
         }
      }

      @Nullable
      public static PackSelectionScreen.Watcher create(File var0) {
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
