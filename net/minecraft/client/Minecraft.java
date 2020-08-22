package net.minecraft.client;

import com.google.common.collect.Queues;
import com.google.common.collect.UnmodifiableIterator;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.GameProfileRepository;
import com.mojang.authlib.minecraft.MinecraftSessionService;
import com.mojang.authlib.properties.PropertyMap;
import com.mojang.authlib.yggdrasil.YggdrasilAuthenticationService;
import com.mojang.blaze3d.pipeline.RenderTarget;
import com.mojang.blaze3d.platform.DisplayData;
import com.mojang.blaze3d.platform.GlUtil;
import com.mojang.blaze3d.platform.Window;
import com.mojang.blaze3d.platform.WindowEventHandler;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.datafixers.DataFixer;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.Proxy;
import java.net.SocketAddress;
import java.nio.ByteOrder;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Queue;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.ChatFormatting;
import net.minecraft.CrashReport;
import net.minecraft.CrashReportCategory;
import net.minecraft.DefaultUncaughtExceptionHandler;
import net.minecraft.ReportedException;
import net.minecraft.SharedConstants;
import net.minecraft.Util;
import net.minecraft.client.color.block.BlockColors;
import net.minecraft.client.color.item.ItemColors;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.chat.NarratorChatListener;
import net.minecraft.client.gui.components.toasts.SystemToast;
import net.minecraft.client.gui.components.toasts.ToastComponent;
import net.minecraft.client.gui.font.FontManager;
import net.minecraft.client.gui.screens.ChatScreen;
import net.minecraft.client.gui.screens.ConnectScreen;
import net.minecraft.client.gui.screens.DeathScreen;
import net.minecraft.client.gui.screens.GenericDirtMessageScreen;
import net.minecraft.client.gui.screens.InBedChatScreen;
import net.minecraft.client.gui.screens.LevelLoadingScreen;
import net.minecraft.client.gui.screens.LoadingOverlay;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.gui.screens.OutOfMemoryScreen;
import net.minecraft.client.gui.screens.Overlay;
import net.minecraft.client.gui.screens.PauseScreen;
import net.minecraft.client.gui.screens.ProgressScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.TitleScreen;
import net.minecraft.client.gui.screens.WinScreen;
import net.minecraft.client.gui.screens.advancements.AdvancementsScreen;
import net.minecraft.client.gui.screens.inventory.CreativeModeInventoryScreen;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.client.gui.screens.multiplayer.JoinMultiplayerScreen;
import net.minecraft.client.main.GameConfig;
import net.minecraft.client.multiplayer.ClientHandshakePacketListenerImpl;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.client.multiplayer.MultiPlayerGameMode;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.client.particle.ParticleEngine;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.FogRenderer;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.ItemInHandRenderer;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.RenderBuffers;
import net.minecraft.client.renderer.VirtualScreen;
import net.minecraft.client.renderer.block.BlockModelShaper;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderDispatcher;
import net.minecraft.client.renderer.debug.DebugRenderer;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.resources.ClientPackSource;
import net.minecraft.client.resources.FoliageColorReloadListener;
import net.minecraft.client.resources.GrassColorReloadListener;
import net.minecraft.client.resources.LegacyResourcePackAdapter;
import net.minecraft.client.resources.MobEffectTextureManager;
import net.minecraft.client.resources.PackAdapterV4;
import net.minecraft.client.resources.PaintingTextureManager;
import net.minecraft.client.resources.SkinManager;
import net.minecraft.client.resources.SplashManager;
import net.minecraft.client.resources.UnopenedResourcePack;
import net.minecraft.client.resources.language.LanguageManager;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.ModelManager;
import net.minecraft.client.searchtree.MutableSearchTree;
import net.minecraft.client.searchtree.ReloadableIdSearchTree;
import net.minecraft.client.searchtree.ReloadableSearchTree;
import net.minecraft.client.searchtree.SearchRegistry;
import net.minecraft.client.server.IntegratedServer;
import net.minecraft.client.sounds.MusicManager;
import net.minecraft.client.sounds.SoundManager;
import net.minecraft.client.tutorial.Tutorial;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.core.Registry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.network.Connection;
import net.minecraft.network.ConnectionProtocol;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.KeybindComponent;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ServerboundPlayerActionPacket;
import net.minecraft.network.protocol.handshake.ClientIntentionPacket;
import net.minecraft.network.protocol.login.ServerboundHelloPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.Bootstrap;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.progress.ProcessorChunkProgressListener;
import net.minecraft.server.level.progress.StoringChunkProgressListener;
import net.minecraft.server.packs.Pack;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.metadata.pack.PackMetadataSection;
import net.minecraft.server.packs.repository.FolderRepositorySource;
import net.minecraft.server.packs.repository.PackRepository;
import net.minecraft.server.packs.repository.UnopenedPack;
import net.minecraft.server.packs.resources.ReloadableResourceManager;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleReloadableResourceManager;
import net.minecraft.server.players.GameProfileCache;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.FrameTimer;
import net.minecraft.util.Mth;
import net.minecraft.util.Unit;
import net.minecraft.util.datafix.DataFixers;
import net.minecraft.util.profiling.GameProfiler;
import net.minecraft.util.profiling.ProfileResults;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.util.profiling.ResultField;
import net.minecraft.util.thread.ReentrantBlockableEventLoop;
import net.minecraft.world.Difficulty;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.Snooper;
import net.minecraft.world.SnooperPopulator;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.boss.enderdragon.EndCrystal;
import net.minecraft.world.entity.decoration.ArmorStand;
import net.minecraft.world.entity.decoration.ItemFrame;
import net.minecraft.world.entity.decoration.LeashFenceKnotEntity;
import net.minecraft.world.entity.decoration.Painting;
import net.minecraft.world.entity.player.ChatVisiblity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.vehicle.AbstractMinecart;
import net.minecraft.world.entity.vehicle.Boat;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.PlayerHeadItem;
import net.minecraft.world.item.SpawnEggItem;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.LevelSettings;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.SkullBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.dimension.NetherDimension;
import net.minecraft.world.level.dimension.end.TheEndDimension;
import net.minecraft.world.level.storage.LevelData;
import net.minecraft.world.level.storage.LevelStorage;
import net.minecraft.world.level.storage.LevelStorageSource;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Minecraft extends ReentrantBlockableEventLoop implements SnooperPopulator, WindowEventHandler {
   private static Minecraft instance;
   private static final Logger LOGGER = LogManager.getLogger();
   public static final boolean ON_OSX;
   public static final ResourceLocation DEFAULT_FONT;
   public static final ResourceLocation ALT_FONT;
   private static final CompletableFuture RESOURCE_RELOAD_INITIAL_TASK;
   private final File resourcePackDirectory;
   private final PropertyMap profileProperties;
   private final TextureManager textureManager;
   private final DataFixer fixerUpper;
   private final VirtualScreen virtualScreen;
   private final Window window;
   private final Timer timer = new Timer(20.0F, 0L);
   private final Snooper snooper = new Snooper("client", this, Util.getMillis());
   private final RenderBuffers renderBuffers;
   public final LevelRenderer levelRenderer;
   private final EntityRenderDispatcher entityRenderDispatcher;
   private final ItemRenderer itemRenderer;
   private final ItemInHandRenderer itemInHandRenderer;
   public final ParticleEngine particleEngine;
   private final SearchRegistry searchRegistry = new SearchRegistry();
   private final User user;
   public final Font font;
   public final GameRenderer gameRenderer;
   public final DebugRenderer debugRenderer;
   private final AtomicReference progressListener = new AtomicReference();
   public final Gui gui;
   public final Options options;
   private final HotbarManager hotbarManager;
   public final MouseHandler mouseHandler;
   public final KeyboardHandler keyboardHandler;
   public final File gameDirectory;
   private final String launchedVersion;
   private final String versionType;
   private final Proxy proxy;
   private final LevelStorageSource levelSource;
   public final FrameTimer frameTimer = new FrameTimer();
   private final boolean is64bit;
   private final boolean demo;
   private final GameProfiler profiler = new GameProfiler(() -> {
      return this.timer.ticks;
   });
   private final ReloadableResourceManager resourceManager;
   private final ClientPackSource clientPackSource;
   private final PackRepository resourcePackRepository;
   private final LanguageManager languageManager;
   private final BlockColors blockColors;
   private final ItemColors itemColors;
   private final RenderTarget mainRenderTarget;
   private final SoundManager soundManager;
   private final MusicManager musicManager;
   private final FontManager fontManager;
   private final SplashManager splashManager;
   private final MinecraftSessionService minecraftSessionService;
   private final SkinManager skinManager;
   private final ModelManager modelManager;
   private final BlockRenderDispatcher blockRenderer;
   private final PaintingTextureManager paintingTextures;
   private final MobEffectTextureManager mobEffectTextures;
   private final ToastComponent toast;
   private final Game game = new Game(this);
   private final Tutorial tutorial;
   public static byte[] reserve;
   @Nullable
   public MultiPlayerGameMode gameMode;
   @Nullable
   public ClientLevel level;
   @Nullable
   public LocalPlayer player;
   @Nullable
   private IntegratedServer singleplayerServer;
   @Nullable
   private ServerData currentServer;
   @Nullable
   private Connection pendingConnection;
   private boolean isLocalServer;
   @Nullable
   public Entity cameraEntity;
   @Nullable
   public Entity crosshairPickEntity;
   @Nullable
   public HitResult hitResult;
   private int rightClickDelay;
   protected int missTime;
   private boolean pause;
   private float pausePartialTick;
   private long lastNanoTime = Util.getNanos();
   private long lastTime;
   private int frames;
   public boolean noRender;
   @Nullable
   public Screen screen;
   @Nullable
   public Overlay overlay;
   private boolean connectedToRealms;
   private Thread gameThread;
   private volatile boolean running = true;
   @Nullable
   private CrashReport delayedCrash;
   private static int fps;
   public String fpsString = "";
   public boolean chunkPath;
   public boolean chunkVisibility;
   public boolean smartCull = true;
   private boolean windowActive;
   private final Queue progressTasks = Queues.newConcurrentLinkedQueue();
   @Nullable
   private CompletableFuture pendingReload;
   private String debugPath = "root";

   public Minecraft(GameConfig var1) {
      super("Client");
      instance = this;
      this.gameDirectory = var1.location.gameDirectory;
      File var2 = var1.location.assetDirectory;
      this.resourcePackDirectory = var1.location.resourcePackDirectory;
      this.launchedVersion = var1.game.launchVersion;
      this.versionType = var1.game.versionType;
      this.profileProperties = var1.user.profileProperties;
      this.clientPackSource = new ClientPackSource(new File(this.gameDirectory, "server-resource-packs"), var1.location.getAssetIndex());
      this.resourcePackRepository = new PackRepository(Minecraft::createClientPackAdapter);
      this.resourcePackRepository.addSource(this.clientPackSource);
      this.resourcePackRepository.addSource(new FolderRepositorySource(this.resourcePackDirectory));
      this.proxy = var1.user.proxy;
      this.minecraftSessionService = (new YggdrasilAuthenticationService(this.proxy, UUID.randomUUID().toString())).createMinecraftSessionService();
      this.user = var1.user.user;
      LOGGER.info("Setting user: {}", this.user.getName());
      LOGGER.debug("(Session ID is {})", this.user.getSessionId());
      this.demo = var1.game.demo;
      this.is64bit = checkIs64Bit();
      this.singleplayerServer = null;
      String var3;
      int var4;
      if (var1.server.hostname != null) {
         var3 = var1.server.hostname;
         var4 = var1.server.port;
      } else {
         var3 = null;
         var4 = 0;
      }

      Bootstrap.bootStrap();
      Bootstrap.validate();
      KeybindComponent.keyResolver = KeyMapping::createNameSupplier;
      this.fixerUpper = DataFixers.getDataFixer();
      this.toast = new ToastComponent(this);
      this.tutorial = new Tutorial(this);
      this.gameThread = Thread.currentThread();
      this.options = new Options(this, this.gameDirectory);
      this.hotbarManager = new HotbarManager(this.gameDirectory, this.fixerUpper);
      this.startTimerHackThread();
      LOGGER.info("Backend library: {}", RenderSystem.getBackendDescription());
      DisplayData var5;
      if (this.options.overrideHeight > 0 && this.options.overrideWidth > 0) {
         var5 = new DisplayData(this.options.overrideWidth, this.options.overrideHeight, var1.display.fullscreenWidth, var1.display.fullscreenHeight, var1.display.isFullscreen);
      } else {
         var5 = var1.display;
      }

      Util.timeSource = RenderSystem.initBackendSystem();
      this.virtualScreen = new VirtualScreen(this);
      this.window = this.virtualScreen.newWindow(var5, this.options.fullscreenVideoModeString, "Minecraft " + SharedConstants.getCurrentVersion().getName());
      this.setWindowActive(true);

      try {
         InputStream var6 = this.getClientPackSource().getVanillaPack().getResource(PackType.CLIENT_RESOURCES, new ResourceLocation("icons/icon_16x16.png"));
         InputStream var7 = this.getClientPackSource().getVanillaPack().getResource(PackType.CLIENT_RESOURCES, new ResourceLocation("icons/icon_32x32.png"));
         this.window.setIcon(var6, var7);
      } catch (IOException var8) {
         LOGGER.error("Couldn't set icon", var8);
      }

      this.window.setFramerateLimit(this.options.framerateLimit);
      this.mouseHandler = new MouseHandler(this);
      this.mouseHandler.setup(this.window.getWindow());
      this.keyboardHandler = new KeyboardHandler(this);
      this.keyboardHandler.setup(this.window.getWindow());
      RenderSystem.initRenderer(this.options.glDebugVerbosity, false);
      this.mainRenderTarget = new RenderTarget(this.window.getWidth(), this.window.getHeight(), true, ON_OSX);
      this.mainRenderTarget.setClearColor(0.0F, 0.0F, 0.0F, 0.0F);
      this.resourceManager = new SimpleReloadableResourceManager(PackType.CLIENT_RESOURCES, this.gameThread);
      this.options.loadResourcePacks(this.resourcePackRepository);
      this.resourcePackRepository.reload();
      this.languageManager = new LanguageManager(this.options.languageCode);
      this.resourceManager.registerReloadListener(this.languageManager);
      this.textureManager = new TextureManager(this.resourceManager);
      this.resourceManager.registerReloadListener(this.textureManager);
      this.skinManager = new SkinManager(this.textureManager, new File(var2, "skins"), this.minecraftSessionService);
      this.levelSource = new LevelStorageSource(this.gameDirectory.toPath().resolve("saves"), this.gameDirectory.toPath().resolve("backups"), this.fixerUpper);
      this.soundManager = new SoundManager(this.resourceManager, this.options);
      this.resourceManager.registerReloadListener(this.soundManager);
      this.splashManager = new SplashManager(this.user);
      this.resourceManager.registerReloadListener(this.splashManager);
      this.musicManager = new MusicManager(this);
      this.fontManager = new FontManager(this.textureManager, this.isEnforceUnicode());
      this.resourceManager.registerReloadListener(this.fontManager.getReloadListener());
      Font var9 = this.fontManager.get(DEFAULT_FONT);
      if (var9 == null) {
         throw new IllegalStateException("Default font is null");
      } else {
         this.font = var9;
         this.font.setBidirectional(this.languageManager.isBidirectional());
         this.resourceManager.registerReloadListener(new GrassColorReloadListener());
         this.resourceManager.registerReloadListener(new FoliageColorReloadListener());
         this.window.setErrorSection("Startup");
         RenderSystem.setupDefaultState(0, 0, this.window.getWidth(), this.window.getHeight());
         this.window.setErrorSection("Post startup");
         this.blockColors = BlockColors.createDefault();
         this.itemColors = ItemColors.createDefault(this.blockColors);
         this.modelManager = new ModelManager(this.textureManager, this.blockColors, this.options.mipmapLevels);
         this.resourceManager.registerReloadListener(this.modelManager);
         this.itemRenderer = new ItemRenderer(this.textureManager, this.modelManager, this.itemColors);
         this.entityRenderDispatcher = new EntityRenderDispatcher(this.textureManager, this.itemRenderer, this.resourceManager, this.font, this.options);
         this.itemInHandRenderer = new ItemInHandRenderer(this);
         this.resourceManager.registerReloadListener(this.itemRenderer);
         this.renderBuffers = new RenderBuffers();
         this.gameRenderer = new GameRenderer(this, this.resourceManager, this.renderBuffers);
         this.resourceManager.registerReloadListener(this.gameRenderer);
         this.blockRenderer = new BlockRenderDispatcher(this.modelManager.getBlockModelShaper(), this.blockColors);
         this.resourceManager.registerReloadListener(this.blockRenderer);
         this.levelRenderer = new LevelRenderer(this, this.renderBuffers);
         this.resourceManager.registerReloadListener(this.levelRenderer);
         this.createSearchTrees();
         this.resourceManager.registerReloadListener(this.searchRegistry);
         this.particleEngine = new ParticleEngine(this.level, this.textureManager);
         this.resourceManager.registerReloadListener(this.particleEngine);
         this.paintingTextures = new PaintingTextureManager(this.textureManager);
         this.resourceManager.registerReloadListener(this.paintingTextures);
         this.mobEffectTextures = new MobEffectTextureManager(this.textureManager);
         this.resourceManager.registerReloadListener(this.mobEffectTextures);
         this.gui = new Gui(this);
         this.debugRenderer = new DebugRenderer(this);
         RenderSystem.setErrorCallback(this::onFullscreenError);
         if (this.options.fullscreen && !this.window.isFullscreen()) {
            this.window.toggleFullScreen();
            this.options.fullscreen = this.window.isFullscreen();
         }

         this.window.updateVsync(this.options.enableVsync);
         this.window.updateRawMouseInput(this.options.rawMouseInput);
         this.window.setDefaultErrorCallback();
         this.resizeDisplay();
         if (var3 != null) {
            this.setScreen(new ConnectScreen(new TitleScreen(), this, var3, var4));
         } else {
            this.setScreen(new TitleScreen(true));
         }

         LoadingOverlay.registerTextures(this);
         List var10 = (List)this.resourcePackRepository.getSelected().stream().map(UnopenedPack::open).collect(Collectors.toList());
         this.setOverlay(new LoadingOverlay(this, this.resourceManager.createFullReload(Util.backgroundExecutor(), this, RESOURCE_RELOAD_INITIAL_TASK, var10), (var2x) -> {
            Util.ifElse(var2x, this::rollbackResourcePacks, () -> {
               this.languageManager.reload(var10);
               if (SharedConstants.IS_RUNNING_IN_IDE) {
                  this.selfTest();
               }

            });
         }, false));
      }
   }

   private void rollbackResourcePacks(Throwable var1) {
      if (this.resourcePackRepository.getSelected().size() > 1) {
         TextComponent var2;
         if (var1 instanceof SimpleReloadableResourceManager.ResourcePackLoadingFailure) {
            var2 = new TextComponent(((SimpleReloadableResourceManager.ResourcePackLoadingFailure)var1).getPack().getName());
         } else {
            var2 = null;
         }

         LOGGER.info("Caught error loading resourcepacks, removing all selected resourcepacks", var1);
         this.resourcePackRepository.setSelected(Collections.emptyList());
         this.options.resourcePacks.clear();
         this.options.incompatibleResourcePacks.clear();
         this.options.save();
         this.reloadResourcePacks().thenRun(() -> {
            ToastComponent var2x = this.getToasts();
            SystemToast.addOrUpdate(var2x, SystemToast.SystemToastIds.PACK_LOAD_FAILURE, new TranslatableComponent("resourcePack.load_fail", new Object[0]), var2);
         });
      } else {
         Util.throwAsRuntime(var1);
      }

   }

   public void run() {
      this.gameThread = Thread.currentThread();

      try {
         boolean var1 = false;

         while(this.running) {
            if (this.delayedCrash != null) {
               crash(this.delayedCrash);
               return;
            }

            try {
               this.runTick(!var1);
            } catch (OutOfMemoryError var3) {
               if (var1) {
                  throw var3;
               }

               this.emergencySave();
               this.setScreen(new OutOfMemoryScreen());
               System.gc();
               LOGGER.fatal("Out of memory", var3);
               var1 = true;
            }
         }
      } catch (ReportedException var4) {
         this.fillReport(var4.getReport());
         this.emergencySave();
         LOGGER.fatal("Reported exception thrown!", var4);
         crash(var4.getReport());
      } catch (Throwable var5) {
         CrashReport var2 = this.fillReport(new CrashReport("Unexpected error", var5));
         LOGGER.fatal("Unreported exception thrown!", var5);
         this.emergencySave();
         crash(var2);
      }

   }

   private void createSearchTrees() {
      ReloadableSearchTree var1 = new ReloadableSearchTree((var0) -> {
         return var0.getTooltipLines((Player)null, TooltipFlag.Default.NORMAL).stream().map((var0x) -> {
            return ChatFormatting.stripFormatting(var0x.getString()).trim();
         }).filter((var0x) -> {
            return !var0x.isEmpty();
         });
      }, (var0) -> {
         return Stream.of(Registry.ITEM.getKey(var0.getItem()));
      });
      ReloadableIdSearchTree var2 = new ReloadableIdSearchTree((var0) -> {
         return ItemTags.getAllTags().getMatchingTags(var0.getItem()).stream();
      });
      NonNullList var3 = NonNullList.create();
      Iterator var4 = Registry.ITEM.iterator();

      while(var4.hasNext()) {
         Item var5 = (Item)var4.next();
         var5.fillItemCategory(CreativeModeTab.TAB_SEARCH, var3);
      }

      var3.forEach((var2x) -> {
         var1.add(var2x);
         var2.add(var2x);
      });
      ReloadableSearchTree var6 = new ReloadableSearchTree((var0) -> {
         return var0.getRecipes().stream().flatMap((var0x) -> {
            return var0x.getResultItem().getTooltipLines((Player)null, TooltipFlag.Default.NORMAL).stream();
         }).map((var0x) -> {
            return ChatFormatting.stripFormatting(var0x.getString()).trim();
         }).filter((var0x) -> {
            return !var0x.isEmpty();
         });
      }, (var0) -> {
         return var0.getRecipes().stream().map((var0x) -> {
            return Registry.ITEM.getKey(var0x.getResultItem().getItem());
         });
      });
      this.searchRegistry.register(SearchRegistry.CREATIVE_NAMES, var1);
      this.searchRegistry.register(SearchRegistry.CREATIVE_TAGS, var2);
      this.searchRegistry.register(SearchRegistry.RECIPE_COLLECTIONS, var6);
   }

   private void onFullscreenError(int var1, long var2) {
      this.options.enableVsync = false;
      this.options.save();
   }

   private static boolean checkIs64Bit() {
      String[] var0 = new String[]{"sun.arch.data.model", "com.ibm.vm.bitmode", "os.arch"};
      String[] var1 = var0;
      int var2 = var0.length;

      for(int var3 = 0; var3 < var2; ++var3) {
         String var4 = var1[var3];
         String var5 = System.getProperty(var4);
         if (var5 != null && var5.contains("64")) {
            return true;
         }
      }

      return false;
   }

   public RenderTarget getMainRenderTarget() {
      return this.mainRenderTarget;
   }

   public String getLaunchedVersion() {
      return this.launchedVersion;
   }

   public String getVersionType() {
      return this.versionType;
   }

   private void startTimerHackThread() {
      Thread var1 = new Thread("Timer hack thread") {
         public void run() {
            while(Minecraft.this.running) {
               try {
                  Thread.sleep(2147483647L);
               } catch (InterruptedException var2) {
               }
            }

         }
      };
      var1.setDaemon(true);
      var1.setUncaughtExceptionHandler(new DefaultUncaughtExceptionHandler(LOGGER));
      var1.start();
   }

   public void delayCrash(CrashReport var1) {
      this.delayedCrash = var1;
   }

   public static void crash(CrashReport var0) {
      File var1 = new File(getInstance().gameDirectory, "crash-reports");
      File var2 = new File(var1, "crash-" + (new SimpleDateFormat("yyyy-MM-dd_HH.mm.ss")).format(new Date()) + "-client.txt");
      Bootstrap.realStdoutPrintln(var0.getFriendlyReport());
      if (var0.getSaveFile() != null) {
         Bootstrap.realStdoutPrintln("#@!@# Game crashed! Crash report saved to: #@!@# " + var0.getSaveFile());
         System.exit(-1);
      } else if (var0.saveToFile(var2)) {
         Bootstrap.realStdoutPrintln("#@!@# Game crashed! Crash report saved to: #@!@# " + var2.getAbsolutePath());
         System.exit(-1);
      } else {
         Bootstrap.realStdoutPrintln("#@?@# Game crashed! Crash report could not be saved. #@?@#");
         System.exit(-2);
      }

   }

   public boolean isEnforceUnicode() {
      return this.options.forceUnicodeFont;
   }

   public CompletableFuture reloadResourcePacks() {
      if (this.pendingReload != null) {
         return this.pendingReload;
      } else {
         CompletableFuture var1 = new CompletableFuture();
         if (this.overlay instanceof LoadingOverlay) {
            this.pendingReload = var1;
            return var1;
         } else {
            this.resourcePackRepository.reload();
            List var2 = (List)this.resourcePackRepository.getSelected().stream().map(UnopenedPack::open).collect(Collectors.toList());
            this.setOverlay(new LoadingOverlay(this, this.resourceManager.createFullReload(Util.backgroundExecutor(), this, RESOURCE_RELOAD_INITIAL_TASK, var2), (var3) -> {
               Util.ifElse(var3, this::rollbackResourcePacks, () -> {
                  this.languageManager.reload(var2);
                  this.levelRenderer.allChanged();
                  var1.complete((Object)null);
               });
            }, true));
            return var1;
         }
      }
   }

   private void selfTest() {
      boolean var1 = false;
      BlockModelShaper var2 = this.getBlockRenderer().getBlockModelShaper();
      BakedModel var3 = var2.getModelManager().getMissingModel();
      Iterator var4 = Registry.BLOCK.iterator();

      while(var4.hasNext()) {
         Block var5 = (Block)var4.next();
         UnmodifiableIterator var6 = var5.getStateDefinition().getPossibleStates().iterator();

         while(var6.hasNext()) {
            BlockState var7 = (BlockState)var6.next();
            if (var7.getRenderShape() == RenderShape.MODEL) {
               BakedModel var8 = var2.getBlockModel(var7);
               if (var8 == var3) {
                  LOGGER.debug("Missing model for: {}", var7);
                  var1 = true;
               }
            }
         }
      }

      TextureAtlasSprite var12 = var3.getParticleIcon();
      Iterator var13 = Registry.BLOCK.iterator();

      while(var13.hasNext()) {
         Block var15 = (Block)var13.next();
         UnmodifiableIterator var17 = var15.getStateDefinition().getPossibleStates().iterator();

         while(var17.hasNext()) {
            BlockState var19 = (BlockState)var17.next();
            TextureAtlasSprite var9 = var2.getParticleIcon(var19);
            if (!var19.isAir() && var9 == var12) {
               LOGGER.debug("Missing particle icon for: {}", var19);
               var1 = true;
            }
         }
      }

      NonNullList var14 = NonNullList.create();
      Iterator var16 = Registry.ITEM.iterator();

      while(var16.hasNext()) {
         Item var18 = (Item)var16.next();
         var14.clear();
         var18.fillItemCategory(CreativeModeTab.TAB_SEARCH, var14);
         Iterator var20 = var14.iterator();

         while(var20.hasNext()) {
            ItemStack var21 = (ItemStack)var20.next();
            String var10 = var21.getDescriptionId();
            String var11 = (new TranslatableComponent(var10, new Object[0])).getString();
            if (var11.toLowerCase(Locale.ROOT).equals(var18.getDescriptionId())) {
               LOGGER.debug("Missing translation for: {} {} {}", var21, var10, var21.getItem());
            }
         }
      }

      var1 |= MenuScreens.selfTest();
      if (var1) {
         throw new IllegalStateException("Your game data is foobar, fix the errors above!");
      }
   }

   public LevelStorageSource getLevelSource() {
      return this.levelSource;
   }

   public void setScreen(@Nullable Screen var1) {
      if (this.screen != null) {
         this.screen.removed();
      }

      if (var1 == null && this.level == null) {
         var1 = new TitleScreen();
      } else if (var1 == null && this.player.getHealth() <= 0.0F) {
         if (this.player.shouldShowDeathScreen()) {
            var1 = new DeathScreen((Component)null, this.level.getLevelData().isHardcore());
         } else {
            this.player.respawn();
         }
      }

      if (var1 instanceof TitleScreen || var1 instanceof JoinMultiplayerScreen) {
         this.options.renderDebug = false;
         this.gui.getChat().clearMessages(true);
      }

      this.screen = (Screen)var1;
      if (var1 != null) {
         this.mouseHandler.releaseMouse();
         KeyMapping.releaseAll();
         ((Screen)var1).init(this, this.window.getGuiScaledWidth(), this.window.getGuiScaledHeight());
         this.noRender = false;
         NarratorChatListener.INSTANCE.sayNow(((Screen)var1).getNarrationMessage());
      } else {
         this.soundManager.resume();
         this.mouseHandler.grabMouse();
      }

   }

   public void setOverlay(@Nullable Overlay var1) {
      this.overlay = var1;
   }

   public void destroy() {
      try {
         LOGGER.info("Stopping!");
         NarratorChatListener.INSTANCE.destroy();

         try {
            if (this.level != null) {
               this.level.disconnect();
            }

            this.clearLevel();
         } catch (Throwable var5) {
         }

         if (this.screen != null) {
            this.screen.removed();
         }

         this.close();
      } finally {
         Util.timeSource = System::nanoTime;
         if (this.delayedCrash == null) {
            System.exit(0);
         }

      }

   }

   public void close() {
      try {
         this.modelManager.close();
         this.font.close();
         this.fontManager.close();
         this.gameRenderer.close();
         this.levelRenderer.close();
         this.soundManager.destroy();
         this.resourcePackRepository.close();
         this.particleEngine.close();
         this.mobEffectTextures.close();
         this.paintingTextures.close();
         Util.shutdownBackgroundExecutor();
      } finally {
         this.virtualScreen.close();
         this.window.close();
      }

   }

   private void runTick(boolean var1) {
      this.window.setErrorSection("Pre render");
      long var2 = Util.getNanos();
      this.profiler.startTick();
      if (this.window.shouldClose()) {
         this.stop();
      }

      if (this.pendingReload != null && !(this.overlay instanceof LoadingOverlay)) {
         CompletableFuture var4 = this.pendingReload;
         this.pendingReload = null;
         this.reloadResourcePacks().thenRun(() -> {
            var4.complete((Object)null);
         });
      }

      Runnable var9;
      while((var9 = (Runnable)this.progressTasks.poll()) != null) {
         var9.run();
      }

      if (var1) {
         this.timer.advanceTime(Util.getMillis());
         this.profiler.push("scheduledExecutables");
         this.runAllTasks();
         this.profiler.pop();
      }

      this.profiler.push("tick");
      int var5;
      if (var1) {
         for(var5 = 0; var5 < Math.min(10, this.timer.ticks); ++var5) {
            this.tick();
         }
      }

      this.mouseHandler.turnPlayer();
      this.window.setErrorSection("Render");
      this.profiler.popPush("sound");
      this.soundManager.updateSource(this.gameRenderer.getMainCamera());
      this.profiler.pop();
      this.profiler.push("render");
      RenderSystem.pushMatrix();
      RenderSystem.clear(16640, ON_OSX);
      this.mainRenderTarget.bindWrite(true);
      FogRenderer.setupNoFog();
      this.profiler.push("display");
      RenderSystem.enableTexture();
      this.profiler.pop();
      if (!this.noRender) {
         this.profiler.popPush("gameRenderer");
         this.gameRenderer.render(this.pause ? this.pausePartialTick : this.timer.partialTick, var2, var1);
         this.profiler.popPush("toasts");
         this.toast.render();
         this.profiler.pop();
      }

      this.profiler.endTick();
      if (this.options.renderDebug && this.options.renderDebugCharts && !this.options.hideGui) {
         this.profiler.continuous().enable();
         this.renderFpsMeter();
      } else {
         this.profiler.continuous().disable();
      }

      this.mainRenderTarget.unbindWrite();
      RenderSystem.popMatrix();
      RenderSystem.pushMatrix();
      this.mainRenderTarget.blitToScreen(this.window.getWidth(), this.window.getHeight());
      RenderSystem.popMatrix();
      this.profiler.startTick();
      this.profiler.push("updateDisplay");
      this.window.updateDisplay();
      var5 = this.getFramerateLimit();
      if ((double)var5 < Option.FRAMERATE_LIMIT.getMaxValue()) {
         RenderSystem.limitDisplayFPS(var5);
      }

      this.profiler.popPush("yield");
      Thread.yield();
      this.profiler.pop();
      this.window.setErrorSection("Post render");
      ++this.frames;
      boolean var6 = this.hasSingleplayerServer() && (this.screen != null && this.screen.isPauseScreen() || this.overlay != null && this.overlay.isPauseScreen()) && !this.singleplayerServer.isPublished();
      if (this.pause != var6) {
         if (this.pause) {
            this.pausePartialTick = this.timer.partialTick;
         } else {
            this.timer.partialTick = this.pausePartialTick;
         }

         this.pause = var6;
      }

      long var7 = Util.getNanos();
      this.frameTimer.logFrameDuration(var7 - this.lastNanoTime);
      this.lastNanoTime = var7;

      while(Util.getMillis() >= this.lastTime + 1000L) {
         fps = this.frames;
         this.fpsString = String.format("%d fps T: %s%s%s%s B: %d", fps, (double)this.options.framerateLimit == Option.FRAMERATE_LIMIT.getMaxValue() ? "inf" : this.options.framerateLimit, this.options.enableVsync ? " vsync" : "", this.options.fancyGraphics ? "" : " fast", this.options.renderClouds == CloudStatus.OFF ? "" : (this.options.renderClouds == CloudStatus.FAST ? " fast-clouds" : " fancy-clouds"), this.options.biomeBlendRadius);
         this.lastTime += 1000L;
         this.frames = 0;
         this.snooper.prepare();
         if (!this.snooper.isStarted()) {
            this.snooper.start();
         }
      }

      this.profiler.endTick();
   }

   public void resizeDisplay() {
      int var1 = this.window.calculateScale(this.options.guiScale, this.isEnforceUnicode());
      this.window.setGuiScale((double)var1);
      if (this.screen != null) {
         this.screen.resize(this, this.window.getGuiScaledWidth(), this.window.getGuiScaledHeight());
      }

      RenderTarget var2 = this.getMainRenderTarget();
      var2.resize(this.window.getWidth(), this.window.getHeight(), ON_OSX);
      this.gameRenderer.resize(this.window.getWidth(), this.window.getHeight());
      this.mouseHandler.setIgnoreFirstMove();
   }

   private int getFramerateLimit() {
      return this.level != null || this.screen == null && this.overlay == null ? this.window.getFramerateLimit() : 60;
   }

   public void emergencySave() {
      try {
         reserve = new byte[0];
         this.levelRenderer.clear();
      } catch (Throwable var3) {
      }

      try {
         System.gc();
         if (this.isLocalServer && this.singleplayerServer != null) {
            this.singleplayerServer.halt(true);
         }

         this.clearLevel(new GenericDirtMessageScreen(new TranslatableComponent("menu.savingLevel", new Object[0])));
      } catch (Throwable var2) {
      }

      System.gc();
   }

   void debugFpsMeterKeyPress(int var1) {
      ProfileResults var2 = this.profiler.continuous().getResults();
      List var3 = var2.getTimes(this.debugPath);
      if (!var3.isEmpty()) {
         ResultField var4 = (ResultField)var3.remove(0);
         if (var1 == 0) {
            if (!var4.name.isEmpty()) {
               int var5 = this.debugPath.lastIndexOf(30);
               if (var5 >= 0) {
                  this.debugPath = this.debugPath.substring(0, var5);
               }
            }
         } else {
            --var1;
            if (var1 < var3.size() && !"unspecified".equals(((ResultField)var3.get(var1)).name)) {
               if (!this.debugPath.isEmpty()) {
                  this.debugPath = this.debugPath + '\u001e';
               }

               this.debugPath = this.debugPath + ((ResultField)var3.get(var1)).name;
            }
         }

      }
   }

   private void renderFpsMeter() {
      if (this.profiler.continuous().isEnabled()) {
         ProfileResults var1 = this.profiler.continuous().getResults();
         List var2 = var1.getTimes(this.debugPath);
         ResultField var3 = (ResultField)var2.remove(0);
         RenderSystem.clear(256, ON_OSX);
         RenderSystem.matrixMode(5889);
         RenderSystem.loadIdentity();
         RenderSystem.ortho(0.0D, (double)this.window.getWidth(), (double)this.window.getHeight(), 0.0D, 1000.0D, 3000.0D);
         RenderSystem.matrixMode(5888);
         RenderSystem.loadIdentity();
         RenderSystem.translatef(0.0F, 0.0F, -2000.0F);
         RenderSystem.lineWidth(1.0F);
         RenderSystem.disableTexture();
         Tesselator var4 = Tesselator.getInstance();
         BufferBuilder var5 = var4.getBuilder();
         boolean var6 = true;
         int var7 = this.window.getWidth() - 160 - 10;
         int var8 = this.window.getHeight() - 320;
         RenderSystem.enableBlend();
         var5.begin(7, DefaultVertexFormat.POSITION_COLOR);
         var5.vertex((double)((float)var7 - 176.0F), (double)((float)var8 - 96.0F - 16.0F), 0.0D).color(200, 0, 0, 0).endVertex();
         var5.vertex((double)((float)var7 - 176.0F), (double)(var8 + 320), 0.0D).color(200, 0, 0, 0).endVertex();
         var5.vertex((double)((float)var7 + 176.0F), (double)(var8 + 320), 0.0D).color(200, 0, 0, 0).endVertex();
         var5.vertex((double)((float)var7 + 176.0F), (double)((float)var8 - 96.0F - 16.0F), 0.0D).color(200, 0, 0, 0).endVertex();
         var4.end();
         RenderSystem.disableBlend();
         double var9 = 0.0D;

         ResultField var12;
         int var14;
         for(Iterator var11 = var2.iterator(); var11.hasNext(); var9 += var12.percentage) {
            var12 = (ResultField)var11.next();
            int var13 = Mth.floor(var12.percentage / 4.0D) + 1;
            var5.begin(6, DefaultVertexFormat.POSITION_COLOR);
            var14 = var12.getColor();
            int var15 = var14 >> 16 & 255;
            int var16 = var14 >> 8 & 255;
            int var17 = var14 & 255;
            var5.vertex((double)var7, (double)var8, 0.0D).color(var15, var16, var17, 255).endVertex();

            int var18;
            float var19;
            float var20;
            float var21;
            for(var18 = var13; var18 >= 0; --var18) {
               var19 = (float)((var9 + var12.percentage * (double)var18 / (double)var13) * 6.2831854820251465D / 100.0D);
               var20 = Mth.sin(var19) * 160.0F;
               var21 = Mth.cos(var19) * 160.0F * 0.5F;
               var5.vertex((double)((float)var7 + var20), (double)((float)var8 - var21), 0.0D).color(var15, var16, var17, 255).endVertex();
            }

            var4.end();
            var5.begin(5, DefaultVertexFormat.POSITION_COLOR);

            for(var18 = var13; var18 >= 0; --var18) {
               var19 = (float)((var9 + var12.percentage * (double)var18 / (double)var13) * 6.2831854820251465D / 100.0D);
               var20 = Mth.sin(var19) * 160.0F;
               var21 = Mth.cos(var19) * 160.0F * 0.5F;
               if (var21 <= 0.0F) {
                  var5.vertex((double)((float)var7 + var20), (double)((float)var8 - var21), 0.0D).color(var15 >> 1, var16 >> 1, var17 >> 1, 255).endVertex();
                  var5.vertex((double)((float)var7 + var20), (double)((float)var8 - var21 + 10.0F), 0.0D).color(var15 >> 1, var16 >> 1, var17 >> 1, 255).endVertex();
               }
            }

            var4.end();
         }

         DecimalFormat var22 = new DecimalFormat("##0.00");
         var22.setDecimalFormatSymbols(DecimalFormatSymbols.getInstance(Locale.ROOT));
         RenderSystem.enableTexture();
         String var23 = ProfileResults.demanglePath(var3.name);
         String var25 = "";
         if (!"unspecified".equals(var23)) {
            var25 = var25 + "[0] ";
         }

         if (var23.isEmpty()) {
            var25 = var25 + "ROOT ";
         } else {
            var25 = var25 + var23 + ' ';
         }

         var14 = 16777215;
         this.font.drawShadow(var25, (float)(var7 - 160), (float)(var8 - 80 - 16), 16777215);
         var25 = var22.format(var3.globalPercentage) + "%";
         this.font.drawShadow(var25, (float)(var7 + 160 - this.font.width(var25)), (float)(var8 - 80 - 16), 16777215);

         for(int var24 = 0; var24 < var2.size(); ++var24) {
            ResultField var28 = (ResultField)var2.get(var24);
            StringBuilder var26 = new StringBuilder();
            if ("unspecified".equals(var28.name)) {
               var26.append("[?] ");
            } else {
               var26.append("[").append(var24 + 1).append("] ");
            }

            String var27 = var26.append(var28.name).toString();
            this.font.drawShadow(var27, (float)(var7 - 160), (float)(var8 + 80 + var24 * 8 + 20), var28.getColor());
            var27 = var22.format(var28.percentage) + "%";
            this.font.drawShadow(var27, (float)(var7 + 160 - 50 - this.font.width(var27)), (float)(var8 + 80 + var24 * 8 + 20), var28.getColor());
            var27 = var22.format(var28.globalPercentage) + "%";
            this.font.drawShadow(var27, (float)(var7 + 160 - this.font.width(var27)), (float)(var8 + 80 + var24 * 8 + 20), var28.getColor());
         }

      }
   }

   public void stop() {
      this.running = false;
   }

   public boolean isRunning() {
      return this.running;
   }

   public void pauseGame(boolean var1) {
      if (this.screen == null) {
         boolean var2 = this.hasSingleplayerServer() && !this.singleplayerServer.isPublished();
         if (var2) {
            this.setScreen(new PauseScreen(!var1));
            this.soundManager.pause();
         } else {
            this.setScreen(new PauseScreen(true));
         }

      }
   }

   private void continueAttack(boolean var1) {
      if (!var1) {
         this.missTime = 0;
      }

      if (this.missTime <= 0 && !this.player.isUsingItem()) {
         if (var1 && this.hitResult != null && this.hitResult.getType() == HitResult.Type.BLOCK) {
            BlockHitResult var2 = (BlockHitResult)this.hitResult;
            BlockPos var3 = var2.getBlockPos();
            if (!this.level.getBlockState(var3).isAir()) {
               Direction var4 = var2.getDirection();
               if (this.gameMode.continueDestroyBlock(var3, var4)) {
                  this.particleEngine.crack(var3, var4);
                  this.player.swing(InteractionHand.MAIN_HAND);
               }
            }

         } else {
            this.gameMode.stopDestroyBlock();
         }
      }
   }

   private void startAttack() {
      if (this.missTime <= 0) {
         if (this.hitResult == null) {
            LOGGER.error("Null returned as 'hitResult', this shouldn't happen!");
            if (this.gameMode.hasMissTime()) {
               this.missTime = 10;
            }

         } else if (!this.player.isHandsBusy()) {
            switch(this.hitResult.getType()) {
            case ENTITY:
               this.gameMode.attack(this.player, ((EntityHitResult)this.hitResult).getEntity());
               break;
            case BLOCK:
               BlockHitResult var1 = (BlockHitResult)this.hitResult;
               BlockPos var2 = var1.getBlockPos();
               if (!this.level.getBlockState(var2).isAir()) {
                  this.gameMode.startDestroyBlock(var2, var1.getDirection());
                  break;
               }
            case MISS:
               if (this.gameMode.hasMissTime()) {
                  this.missTime = 10;
               }

               this.player.resetAttackStrengthTicker();
            }

            this.player.swing(InteractionHand.MAIN_HAND);
         }
      }
   }

   private void startUseItem() {
      if (!this.gameMode.isDestroying()) {
         this.rightClickDelay = 4;
         if (!this.player.isHandsBusy()) {
            if (this.hitResult == null) {
               LOGGER.warn("Null returned as 'hitResult', this shouldn't happen!");
            }

            InteractionHand[] var1 = InteractionHand.values();
            int var2 = var1.length;

            for(int var3 = 0; var3 < var2; ++var3) {
               InteractionHand var4 = var1[var3];
               ItemStack var5 = this.player.getItemInHand(var4);
               if (this.hitResult != null) {
                  switch(this.hitResult.getType()) {
                  case ENTITY:
                     EntityHitResult var6 = (EntityHitResult)this.hitResult;
                     Entity var7 = var6.getEntity();
                     InteractionResult var8 = this.gameMode.interactAt(this.player, var7, var6, var4);
                     if (!var8.consumesAction()) {
                        var8 = this.gameMode.interact(this.player, var7, var4);
                     }

                     if (var8.consumesAction()) {
                        if (var8.shouldSwing()) {
                           this.player.swing(var4);
                        }

                        return;
                     }
                     break;
                  case BLOCK:
                     BlockHitResult var9 = (BlockHitResult)this.hitResult;
                     int var10 = var5.getCount();
                     InteractionResult var11 = this.gameMode.useItemOn(this.player, this.level, var4, var9);
                     if (var11.consumesAction()) {
                        if (var11.shouldSwing()) {
                           this.player.swing(var4);
                           if (!var5.isEmpty() && (var5.getCount() != var10 || this.gameMode.hasInfiniteItems())) {
                              this.gameRenderer.itemInHandRenderer.itemUsed(var4);
                           }
                        }

                        return;
                     }

                     if (var11 == InteractionResult.FAIL) {
                        return;
                     }
                  }
               }

               if (!var5.isEmpty()) {
                  InteractionResult var12 = this.gameMode.useItem(this.player, this.level, var4);
                  if (var12.consumesAction()) {
                     if (var12.shouldSwing()) {
                        this.player.swing(var4);
                     }

                     this.gameRenderer.itemInHandRenderer.itemUsed(var4);
                     return;
                  }
               }
            }

         }
      }
   }

   public MusicManager getMusicManager() {
      return this.musicManager;
   }

   public void tick() {
      if (this.rightClickDelay > 0) {
         --this.rightClickDelay;
      }

      this.profiler.push("gui");
      if (!this.pause) {
         this.gui.tick();
      }

      this.profiler.pop();
      this.gameRenderer.pick(1.0F);
      this.tutorial.onLookAt(this.level, this.hitResult);
      this.profiler.push("gameMode");
      if (!this.pause && this.level != null) {
         this.gameMode.tick();
      }

      this.profiler.popPush("textures");
      if (this.level != null) {
         this.textureManager.tick();
      }

      if (this.screen == null && this.player != null) {
         if (this.player.getHealth() <= 0.0F && !(this.screen instanceof DeathScreen)) {
            this.setScreen((Screen)null);
         } else if (this.player.isSleeping() && this.level != null) {
            this.setScreen(new InBedChatScreen());
         }
      } else if (this.screen != null && this.screen instanceof InBedChatScreen && !this.player.isSleeping()) {
         this.setScreen((Screen)null);
      }

      if (this.screen != null) {
         this.missTime = 10000;
      }

      if (this.screen != null) {
         Screen.wrapScreenError(() -> {
            this.screen.tick();
         }, "Ticking screen", this.screen.getClass().getCanonicalName());
      }

      if (!this.options.renderDebug) {
         this.gui.clearCache();
      }

      if (this.overlay == null && (this.screen == null || this.screen.passEvents)) {
         this.profiler.popPush("Keybindings");
         this.handleKeybinds();
         if (this.missTime > 0) {
            --this.missTime;
         }
      }

      if (this.level != null) {
         this.profiler.popPush("gameRenderer");
         if (!this.pause) {
            this.gameRenderer.tick();
         }

         this.profiler.popPush("levelRenderer");
         if (!this.pause) {
            this.levelRenderer.tick();
         }

         this.profiler.popPush("level");
         if (!this.pause) {
            if (this.level.getSkyFlashTime() > 0) {
               this.level.setSkyFlashTime(this.level.getSkyFlashTime() - 1);
            }

            this.level.tickEntities();
         }
      } else if (this.gameRenderer.currentEffect() != null) {
         this.gameRenderer.shutdownEffect();
      }

      if (!this.pause) {
         this.musicManager.tick();
      }

      this.soundManager.tick(this.pause);
      if (this.level != null) {
         if (!this.pause) {
            this.level.setSpawnSettings(this.level.getDifficulty() != Difficulty.PEACEFUL, true);
            this.tutorial.tick();

            try {
               this.level.tick(() -> {
                  return true;
               });
            } catch (Throwable var4) {
               CrashReport var2 = CrashReport.forThrowable(var4, "Exception in world tick");
               if (this.level == null) {
                  CrashReportCategory var3 = var2.addCategory("Affected level");
                  var3.setDetail("Problem", (Object)"Level is null!");
               } else {
                  this.level.fillReportDetails(var2);
               }

               throw new ReportedException(var2);
            }
         }

         this.profiler.popPush("animateTick");
         if (!this.pause && this.level != null) {
            this.level.animateTick(Mth.floor(this.player.getX()), Mth.floor(this.player.getY()), Mth.floor(this.player.getZ()));
         }

         this.profiler.popPush("particles");
         if (!this.pause) {
            this.particleEngine.tick();
         }
      } else if (this.pendingConnection != null) {
         this.profiler.popPush("pendingConnection");
         this.pendingConnection.tick();
      }

      this.profiler.popPush("keyboard");
      this.keyboardHandler.tick();
      this.profiler.pop();
   }

   private void handleKeybinds() {
      for(; this.options.keyTogglePerspective.consumeClick(); this.levelRenderer.needsUpdate()) {
         ++this.options.thirdPersonView;
         if (this.options.thirdPersonView > 2) {
            this.options.thirdPersonView = 0;
         }

         if (this.options.thirdPersonView == 0) {
            this.gameRenderer.checkEntityPostEffect(this.getCameraEntity());
         } else if (this.options.thirdPersonView == 1) {
            this.gameRenderer.checkEntityPostEffect((Entity)null);
         }
      }

      while(this.options.keySmoothCamera.consumeClick()) {
         this.options.smoothCamera = !this.options.smoothCamera;
      }

      for(int var1 = 0; var1 < 9; ++var1) {
         boolean var2 = this.options.keySaveHotbarActivator.isDown();
         boolean var3 = this.options.keyLoadHotbarActivator.isDown();
         if (this.options.keyHotbarSlots[var1].consumeClick()) {
            if (this.player.isSpectator()) {
               this.gui.getSpectatorGui().onHotbarSelected(var1);
            } else if (!this.player.isCreative() || this.screen != null || !var3 && !var2) {
               this.player.inventory.selected = var1;
            } else {
               CreativeModeInventoryScreen.handleHotbarLoadOrSave(this, var1, var3, var2);
            }
         }
      }

      while(this.options.keyInventory.consumeClick()) {
         if (this.gameMode.isServerControlledInventory()) {
            this.player.sendOpenInventory();
         } else {
            this.tutorial.onOpenInventory();
            this.setScreen(new InventoryScreen(this.player));
         }
      }

      while(this.options.keyAdvancements.consumeClick()) {
         this.setScreen(new AdvancementsScreen(this.player.connection.getAdvancements()));
      }

      while(this.options.keySwapHands.consumeClick()) {
         if (!this.player.isSpectator()) {
            this.getConnection().send((Packet)(new ServerboundPlayerActionPacket(ServerboundPlayerActionPacket.Action.SWAP_HELD_ITEMS, BlockPos.ZERO, Direction.DOWN)));
         }
      }

      while(this.options.keyDrop.consumeClick()) {
         if (!this.player.isSpectator() && this.player.drop(Screen.hasControlDown())) {
            this.player.swing(InteractionHand.MAIN_HAND);
         }
      }

      boolean var4 = this.options.chatVisibility != ChatVisiblity.HIDDEN;
      if (var4) {
         while(this.options.keyChat.consumeClick()) {
            this.setScreen(new ChatScreen(""));
         }

         if (this.screen == null && this.overlay == null && this.options.keyCommand.consumeClick()) {
            this.setScreen(new ChatScreen("/"));
         }
      }

      if (this.player.isUsingItem()) {
         if (!this.options.keyUse.isDown()) {
            this.gameMode.releaseUsingItem(this.player);
         }

         label113:
         while(true) {
            if (!this.options.keyAttack.consumeClick()) {
               while(this.options.keyUse.consumeClick()) {
               }

               while(true) {
                  if (this.options.keyPickItem.consumeClick()) {
                     continue;
                  }
                  break label113;
               }
            }
         }
      } else {
         while(this.options.keyAttack.consumeClick()) {
            this.startAttack();
         }

         while(this.options.keyUse.consumeClick()) {
            this.startUseItem();
         }

         while(this.options.keyPickItem.consumeClick()) {
            this.pickBlock();
         }
      }

      if (this.options.keyUse.isDown() && this.rightClickDelay == 0 && !this.player.isUsingItem()) {
         this.startUseItem();
      }

      this.continueAttack(this.screen == null && this.options.keyAttack.isDown() && this.mouseHandler.isMouseGrabbed());
   }

   public void selectLevel(String var1, String var2, @Nullable LevelSettings var3) {
      this.clearLevel();
      LevelStorage var4 = this.levelSource.selectLevel(var1, (MinecraftServer)null);
      LevelData var5 = var4.prepareLevel();
      if (var5 == null && var3 != null) {
         var5 = new LevelData(var3, var1);
         var4.saveLevelData(var5);
      }

      if (var3 == null) {
         var3 = new LevelSettings(var5);
      }

      this.progressListener.set((Object)null);

      try {
         YggdrasilAuthenticationService var6 = new YggdrasilAuthenticationService(this.proxy, UUID.randomUUID().toString());
         MinecraftSessionService var13 = var6.createMinecraftSessionService();
         GameProfileRepository var15 = var6.createProfileRepository();
         GameProfileCache var9 = new GameProfileCache(var15, new File(this.gameDirectory, MinecraftServer.USERID_CACHE_FILE.getName()));
         SkullBlockEntity.setProfileCache(var9);
         SkullBlockEntity.setSessionService(var13);
         GameProfileCache.setUsesAuthentication(false);
         this.singleplayerServer = new IntegratedServer(this, var1, var2, var3, var6, var13, var15, var9, (var1x) -> {
            StoringChunkProgressListener var2 = new StoringChunkProgressListener(var1x + 0);
            var2.start();
            this.progressListener.set(var2);
            Queue var10003 = this.progressTasks;
            var10003.getClass();
            return new ProcessorChunkProgressListener(var2, var10003::add);
         });
         this.singleplayerServer.forkAndRun();
         this.isLocalServer = true;
      } catch (Throwable var11) {
         CrashReport var7 = CrashReport.forThrowable(var11, "Starting integrated server");
         CrashReportCategory var8 = var7.addCategory("Starting integrated server");
         var8.setDetail("Level ID", (Object)var1);
         var8.setDetail("Level Name", (Object)var2);
         throw new ReportedException(var7);
      }

      while(this.progressListener.get() == null) {
         Thread.yield();
      }

      LevelLoadingScreen var12 = new LevelLoadingScreen((StoringChunkProgressListener)this.progressListener.get());
      this.setScreen(var12);

      while(!this.singleplayerServer.isReady()) {
         var12.tick();
         this.runTick(false);

         try {
            Thread.sleep(16L);
         } catch (InterruptedException var10) {
         }

         if (this.delayedCrash != null) {
            crash(this.delayedCrash);
            return;
         }
      }

      SocketAddress var14 = this.singleplayerServer.getConnection().startMemoryChannel();
      Connection var16 = Connection.connectToLocalServer(var14);
      var16.setListener(new ClientHandshakePacketListenerImpl(var16, this, (Screen)null, (var0) -> {
      }));
      var16.send(new ClientIntentionPacket(var14.toString(), 0, ConnectionProtocol.LOGIN));
      var16.send(new ServerboundHelloPacket(this.getUser().getGameProfile()));
      this.pendingConnection = var16;
   }

   public void setLevel(ClientLevel var1) {
      ProgressScreen var2 = new ProgressScreen();
      var2.progressStartNoAbort(new TranslatableComponent("connect.joining", new Object[0]));
      this.updateScreenAndTick(var2);
      this.level = var1;
      this.updateLevelInEngines(var1);
      if (!this.isLocalServer) {
         YggdrasilAuthenticationService var3 = new YggdrasilAuthenticationService(this.proxy, UUID.randomUUID().toString());
         MinecraftSessionService var4 = var3.createMinecraftSessionService();
         GameProfileRepository var5 = var3.createProfileRepository();
         GameProfileCache var6 = new GameProfileCache(var5, new File(this.gameDirectory, MinecraftServer.USERID_CACHE_FILE.getName()));
         SkullBlockEntity.setProfileCache(var6);
         SkullBlockEntity.setSessionService(var4);
         GameProfileCache.setUsesAuthentication(false);
      }

   }

   public void clearLevel() {
      this.clearLevel(new ProgressScreen());
   }

   public void clearLevel(Screen var1) {
      ClientPacketListener var2 = this.getConnection();
      if (var2 != null) {
         this.dropAllTasks();
         var2.cleanup();
      }

      IntegratedServer var3 = this.singleplayerServer;
      this.singleplayerServer = null;
      this.gameRenderer.resetData();
      this.gameMode = null;
      NarratorChatListener.INSTANCE.clear();
      this.updateScreenAndTick(var1);
      if (this.level != null) {
         if (var3 != null) {
            while(!var3.isShutdown()) {
               this.runTick(false);
            }
         }

         this.clientPackSource.clearServerPack();
         this.gui.onDisconnected();
         this.currentServer = null;
         this.isLocalServer = false;
         this.game.onLeaveGameSession();
      }

      this.level = null;
      this.updateLevelInEngines((ClientLevel)null);
      this.player = null;
   }

   private void updateScreenAndTick(Screen var1) {
      this.musicManager.stopPlaying();
      this.soundManager.stop();
      this.cameraEntity = null;
      this.pendingConnection = null;
      this.setScreen(var1);
      this.runTick(false);
   }

   private void updateLevelInEngines(@Nullable ClientLevel var1) {
      this.levelRenderer.setLevel(var1);
      this.particleEngine.setLevel(var1);
      BlockEntityRenderDispatcher.instance.setLevel(var1);
   }

   public final boolean isDemo() {
      return this.demo;
   }

   @Nullable
   public ClientPacketListener getConnection() {
      return this.player == null ? null : this.player.connection;
   }

   public static boolean renderNames() {
      return !instance.options.hideGui;
   }

   public static boolean useFancyGraphics() {
      return instance.options.fancyGraphics;
   }

   public static boolean useAmbientOcclusion() {
      return instance.options.ambientOcclusion != AmbientOcclusionStatus.OFF;
   }

   private void pickBlock() {
      if (this.hitResult != null && this.hitResult.getType() != HitResult.Type.MISS) {
         boolean var1 = this.player.abilities.instabuild;
         BlockEntity var2 = null;
         HitResult.Type var4 = this.hitResult.getType();
         ItemStack var3;
         if (var4 == HitResult.Type.BLOCK) {
            BlockPos var8 = ((BlockHitResult)this.hitResult).getBlockPos();
            BlockState var13 = this.level.getBlockState(var8);
            Block var16 = var13.getBlock();
            if (var13.isAir()) {
               return;
            }

            var3 = var16.getCloneItemStack(this.level, var8, var13);
            if (var3.isEmpty()) {
               return;
            }

            if (var1 && Screen.hasControlDown() && var16.isEntityBlock()) {
               var2 = this.level.getBlockEntity(var8);
            }
         } else {
            if (var4 != HitResult.Type.ENTITY || !var1) {
               return;
            }

            Entity var5 = ((EntityHitResult)this.hitResult).getEntity();
            if (var5 instanceof Painting) {
               var3 = new ItemStack(Items.PAINTING);
            } else if (var5 instanceof LeashFenceKnotEntity) {
               var3 = new ItemStack(Items.LEAD);
            } else if (var5 instanceof ItemFrame) {
               ItemFrame var6 = (ItemFrame)var5;
               ItemStack var7 = var6.getItem();
               if (var7.isEmpty()) {
                  var3 = new ItemStack(Items.ITEM_FRAME);
               } else {
                  var3 = var7.copy();
               }
            } else if (var5 instanceof AbstractMinecart) {
               AbstractMinecart var11 = (AbstractMinecart)var5;
               Item var15;
               switch(var11.getMinecartType()) {
               case FURNACE:
                  var15 = Items.FURNACE_MINECART;
                  break;
               case CHEST:
                  var15 = Items.CHEST_MINECART;
                  break;
               case TNT:
                  var15 = Items.TNT_MINECART;
                  break;
               case HOPPER:
                  var15 = Items.HOPPER_MINECART;
                  break;
               case COMMAND_BLOCK:
                  var15 = Items.COMMAND_BLOCK_MINECART;
                  break;
               default:
                  var15 = Items.MINECART;
               }

               var3 = new ItemStack(var15);
            } else if (var5 instanceof Boat) {
               var3 = new ItemStack(((Boat)var5).getDropItem());
            } else if (var5 instanceof ArmorStand) {
               var3 = new ItemStack(Items.ARMOR_STAND);
            } else if (var5 instanceof EndCrystal) {
               var3 = new ItemStack(Items.END_CRYSTAL);
            } else {
               SpawnEggItem var12 = SpawnEggItem.byId(var5.getType());
               if (var12 == null) {
                  return;
               }

               var3 = new ItemStack(var12);
            }
         }

         if (var3.isEmpty()) {
            String var10 = "";
            if (var4 == HitResult.Type.BLOCK) {
               var10 = Registry.BLOCK.getKey(this.level.getBlockState(((BlockHitResult)this.hitResult).getBlockPos()).getBlock()).toString();
            } else if (var4 == HitResult.Type.ENTITY) {
               var10 = Registry.ENTITY_TYPE.getKey(((EntityHitResult)this.hitResult).getEntity().getType()).toString();
            }

            LOGGER.warn("Picking on: [{}] {} gave null item", var4, var10);
         } else {
            Inventory var9 = this.player.inventory;
            if (var2 != null) {
               this.addCustomNbtData(var3, var2);
            }

            int var14 = var9.findSlotMatchingItem(var3);
            if (var1) {
               var9.setPickedItem(var3);
               this.gameMode.handleCreativeModeItemAdd(this.player.getItemInHand(InteractionHand.MAIN_HAND), 36 + var9.selected);
            } else if (var14 != -1) {
               if (Inventory.isHotbarSlot(var14)) {
                  var9.selected = var14;
               } else {
                  this.gameMode.handlePickItem(var14);
               }
            }

         }
      }
   }

   private ItemStack addCustomNbtData(ItemStack var1, BlockEntity var2) {
      CompoundTag var3 = var2.save(new CompoundTag());
      CompoundTag var4;
      if (var1.getItem() instanceof PlayerHeadItem && var3.contains("Owner")) {
         var4 = var3.getCompound("Owner");
         var1.getOrCreateTag().put("SkullOwner", var4);
         return var1;
      } else {
         var1.addTagElement("BlockEntityTag", var3);
         var4 = new CompoundTag();
         ListTag var5 = new ListTag();
         var5.add(StringTag.valueOf("\"(+NBT)\""));
         var4.put("Lore", var5);
         var1.addTagElement("display", var4);
         return var1;
      }
   }

   public CrashReport fillReport(CrashReport var1) {
      fillReport(this.languageManager, this.launchedVersion, this.options, var1);
      if (this.level != null) {
         this.level.fillReportDetails(var1);
      }

      return var1;
   }

   public static void fillReport(@Nullable LanguageManager var0, String var1, @Nullable Options var2, CrashReport var3) {
      CrashReportCategory var4 = var3.getSystemDetails();
      var4.setDetail("Launched Version", () -> {
         return var1;
      });
      var4.setDetail("Backend library", RenderSystem::getBackendDescription);
      var4.setDetail("Backend API", RenderSystem::getApiDescription);
      var4.setDetail("GL Caps", RenderSystem::getCapsString);
      var4.setDetail("Using VBOs", () -> {
         return "Yes";
      });
      var4.setDetail("Is Modded", () -> {
         String var0 = ClientBrandRetriever.getClientModName();
         if (!"vanilla".equals(var0)) {
            return "Definitely; Client brand changed to '" + var0 + "'";
         } else {
            return Minecraft.class.getSigners() == null ? "Very likely; Jar signature invalidated" : "Probably not. Jar signature remains and client brand is untouched.";
         }
      });
      var4.setDetail("Type", (Object)"Client (map_client.txt)");
      if (var2 != null) {
         var4.setDetail("Resource Packs", () -> {
            StringBuilder var1 = new StringBuilder();
            Iterator var2x = var2.resourcePacks.iterator();

            while(var2x.hasNext()) {
               String var3 = (String)var2x.next();
               if (var1.length() > 0) {
                  var1.append(", ");
               }

               var1.append(var3);
               if (var2.incompatibleResourcePacks.contains(var3)) {
                  var1.append(" (incompatible)");
               }
            }

            return var1.toString();
         });
      }

      if (var0 != null) {
         var4.setDetail("Current Language", () -> {
            return var0.getSelected().toString();
         });
      }

      var4.setDetail("CPU", GlUtil::getCpuInfo);
   }

   public static Minecraft getInstance() {
      return instance;
   }

   public CompletableFuture delayTextureReload() {
      return this.submit(this::reloadResourcePacks).thenCompose((var0) -> {
         return var0;
      });
   }

   public void populateSnooper(Snooper var1) {
      var1.setDynamicData("fps", fps);
      var1.setDynamicData("vsync_enabled", this.options.enableVsync);
      var1.setDynamicData("display_frequency", this.window.getRefreshRate());
      var1.setDynamicData("display_type", this.window.isFullscreen() ? "fullscreen" : "windowed");
      var1.setDynamicData("run_time", (Util.getMillis() - var1.getStartupTime()) / 60L * 1000L);
      var1.setDynamicData("current_action", this.getCurrentSnooperAction());
      var1.setDynamicData("language", this.options.languageCode == null ? "en_us" : this.options.languageCode);
      String var2 = ByteOrder.nativeOrder() == ByteOrder.LITTLE_ENDIAN ? "little" : "big";
      var1.setDynamicData("endianness", var2);
      var1.setDynamicData("subtitles", this.options.showSubtitles);
      var1.setDynamicData("touch", this.options.touchscreen ? "touch" : "mouse");
      int var3 = 0;
      Iterator var4 = this.resourcePackRepository.getSelected().iterator();

      while(var4.hasNext()) {
         UnopenedResourcePack var5 = (UnopenedResourcePack)var4.next();
         if (!var5.isRequired() && !var5.isFixedPosition()) {
            var1.setDynamicData("resource_pack[" + var3++ + "]", var5.getId());
         }
      }

      var1.setDynamicData("resource_packs", var3);
      if (this.singleplayerServer != null) {
         var1.setDynamicData("snooper_partner", this.singleplayerServer.getSnooper().getToken());
      }

   }

   private String getCurrentSnooperAction() {
      if (this.singleplayerServer != null) {
         return this.singleplayerServer.isPublished() ? "hosting_lan" : "singleplayer";
      } else if (this.currentServer != null) {
         return this.currentServer.isLan() ? "playing_lan" : "multiplayer";
      } else {
         return "out_of_game";
      }
   }

   public void setCurrentServer(@Nullable ServerData var1) {
      this.currentServer = var1;
   }

   @Nullable
   public ServerData getCurrentServer() {
      return this.currentServer;
   }

   public boolean isLocalServer() {
      return this.isLocalServer;
   }

   public boolean hasSingleplayerServer() {
      return this.isLocalServer && this.singleplayerServer != null;
   }

   @Nullable
   public IntegratedServer getSingleplayerServer() {
      return this.singleplayerServer;
   }

   public Snooper getSnooper() {
      return this.snooper;
   }

   public User getUser() {
      return this.user;
   }

   public PropertyMap getProfileProperties() {
      if (this.profileProperties.isEmpty()) {
         GameProfile var1 = this.getMinecraftSessionService().fillProfileProperties(this.user.getGameProfile(), false);
         this.profileProperties.putAll(var1.getProperties());
      }

      return this.profileProperties;
   }

   public Proxy getProxy() {
      return this.proxy;
   }

   public TextureManager getTextureManager() {
      return this.textureManager;
   }

   public ResourceManager getResourceManager() {
      return this.resourceManager;
   }

   public PackRepository getResourcePackRepository() {
      return this.resourcePackRepository;
   }

   public ClientPackSource getClientPackSource() {
      return this.clientPackSource;
   }

   public File getResourcePackDirectory() {
      return this.resourcePackDirectory;
   }

   public LanguageManager getLanguageManager() {
      return this.languageManager;
   }

   public Function getTextureAtlas(ResourceLocation var1) {
      TextureAtlas var10000 = this.modelManager.getAtlas(var1);
      return var10000::getSprite;
   }

   public boolean is64Bit() {
      return this.is64bit;
   }

   public boolean isPaused() {
      return this.pause;
   }

   public SoundManager getSoundManager() {
      return this.soundManager;
   }

   public MusicManager.Music getSituationalMusic() {
      if (this.screen instanceof WinScreen) {
         return MusicManager.Music.CREDITS;
      } else if (this.player == null) {
         return MusicManager.Music.MENU;
      } else if (this.player.level.dimension instanceof NetherDimension) {
         return MusicManager.Music.NETHER;
      } else if (this.player.level.dimension instanceof TheEndDimension) {
         return this.gui.getBossOverlay().shouldPlayMusic() ? MusicManager.Music.END_BOSS : MusicManager.Music.END;
      } else {
         Biome.BiomeCategory var1 = this.player.level.getBiome(new BlockPos(this.player)).getBiomeCategory();
         if (this.musicManager.isPlayingMusic(MusicManager.Music.UNDER_WATER) || this.player.isUnderWater() && !this.musicManager.isPlayingMusic(MusicManager.Music.GAME) && (var1 == Biome.BiomeCategory.OCEAN || var1 == Biome.BiomeCategory.RIVER)) {
            return MusicManager.Music.UNDER_WATER;
         } else {
            return this.player.abilities.instabuild && this.player.abilities.mayfly ? MusicManager.Music.CREATIVE : MusicManager.Music.GAME;
         }
      }
   }

   public MinecraftSessionService getMinecraftSessionService() {
      return this.minecraftSessionService;
   }

   public SkinManager getSkinManager() {
      return this.skinManager;
   }

   @Nullable
   public Entity getCameraEntity() {
      return this.cameraEntity;
   }

   public void setCameraEntity(Entity var1) {
      this.cameraEntity = var1;
      this.gameRenderer.checkEntityPostEffect(var1);
   }

   protected Thread getRunningThread() {
      return this.gameThread;
   }

   protected Runnable wrapRunnable(Runnable var1) {
      return var1;
   }

   protected boolean shouldRun(Runnable var1) {
      return true;
   }

   public BlockRenderDispatcher getBlockRenderer() {
      return this.blockRenderer;
   }

   public EntityRenderDispatcher getEntityRenderDispatcher() {
      return this.entityRenderDispatcher;
   }

   public ItemRenderer getItemRenderer() {
      return this.itemRenderer;
   }

   public ItemInHandRenderer getItemInHandRenderer() {
      return this.itemInHandRenderer;
   }

   public MutableSearchTree getSearchTree(SearchRegistry.Key var1) {
      return this.searchRegistry.getTree(var1);
   }

   public FrameTimer getFrameTimer() {
      return this.frameTimer;
   }

   public boolean isConnectedToRealms() {
      return this.connectedToRealms;
   }

   public void setConnectedToRealms(boolean var1) {
      this.connectedToRealms = var1;
   }

   public DataFixer getFixerUpper() {
      return this.fixerUpper;
   }

   public float getFrameTime() {
      return this.timer.partialTick;
   }

   public float getDeltaFrameTime() {
      return this.timer.tickDelta;
   }

   public BlockColors getBlockColors() {
      return this.blockColors;
   }

   public boolean showOnlyReducedInfo() {
      return this.player != null && this.player.isReducedDebugInfo() || this.options.reducedDebugInfo;
   }

   public ToastComponent getToasts() {
      return this.toast;
   }

   public Tutorial getTutorial() {
      return this.tutorial;
   }

   public boolean isWindowActive() {
      return this.windowActive;
   }

   public HotbarManager getHotbarManager() {
      return this.hotbarManager;
   }

   public ModelManager getModelManager() {
      return this.modelManager;
   }

   public FontManager getFontManager() {
      return this.fontManager;
   }

   public PaintingTextureManager getPaintingTextures() {
      return this.paintingTextures;
   }

   public MobEffectTextureManager getMobEffectTextures() {
      return this.mobEffectTextures;
   }

   public void setWindowActive(boolean var1) {
      this.windowActive = var1;
   }

   public ProfilerFiller getProfiler() {
      return this.profiler;
   }

   public Game getGame() {
      return this.game;
   }

   public SplashManager getSplashManager() {
      return this.splashManager;
   }

   @Nullable
   public Overlay getOverlay() {
      return this.overlay;
   }

   public boolean renderOnThread() {
      return false;
   }

   public Window getWindow() {
      return this.window;
   }

   public RenderBuffers renderBuffers() {
      return this.renderBuffers;
   }

   private static UnopenedResourcePack createClientPackAdapter(String var0, boolean var1, Supplier var2, Pack var3, PackMetadataSection var4, UnopenedPack.Position var5) {
      int var6 = var4.getPackFormat();
      Supplier var7 = var2;
      if (var6 <= 3) {
         var7 = adaptV3(var2);
      }

      if (var6 <= 4) {
         var7 = adaptV4(var7);
      }

      return new UnopenedResourcePack(var0, var1, var7, var3, var4, var5);
   }

   private static Supplier adaptV3(Supplier var0) {
      return () -> {
         return new LegacyResourcePackAdapter((Pack)var0.get(), LegacyResourcePackAdapter.V3);
      };
   }

   private static Supplier adaptV4(Supplier var0) {
      return () -> {
         return new PackAdapterV4((Pack)var0.get());
      };
   }

   public void updateMaxMipLevel(int var1) {
      this.modelManager.updateMaxMipLevel(var1);
   }

   static {
      ON_OSX = Util.getPlatform() == Util.OS.OSX;
      DEFAULT_FONT = new ResourceLocation("default");
      ALT_FONT = new ResourceLocation("alt");
      RESOURCE_RELOAD_INITIAL_TASK = CompletableFuture.completedFuture(Unit.INSTANCE);
      reserve = new byte[10485760];
   }
}
