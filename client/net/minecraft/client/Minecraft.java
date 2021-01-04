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
import com.mojang.blaze3d.platform.GLX;
import com.mojang.blaze3d.platform.GlDebug;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.platform.Window;
import com.mojang.blaze3d.platform.WindowEventHandler;
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
import java.nio.IntBuffer;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Queue;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.LongSupplier;
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
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.client.multiplayer.MultiPlayerGameMode;
import net.minecraft.client.multiplayer.MultiPlayerLevel;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.client.particle.ParticleEngine;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.ItemInHandRenderer;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.VirtualScreen;
import net.minecraft.client.renderer.block.BlockModelShaper;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderDispatcher;
import net.minecraft.client.renderer.chunk.RenderChunk;
import net.minecraft.client.renderer.debug.DebugRenderer;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.renderer.texture.TickableTextureObject;
import net.minecraft.client.resources.ClientPackSource;
import net.minecraft.client.resources.FoliageColorReloadListener;
import net.minecraft.client.resources.GrassColorReloadListener;
import net.minecraft.client.resources.LegacyResourcePackAdapter;
import net.minecraft.client.resources.MobEffectTextureManager;
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

public class Minecraft extends ReentrantBlockableEventLoop<Runnable> implements SnooperPopulator, WindowEventHandler, AutoCloseable {
   private static final Logger LOGGER = LogManager.getLogger();
   public static final boolean ON_OSX;
   public static final ResourceLocation DEFAULT_FONT;
   public static final ResourceLocation ALT_FONT;
   private static final CompletableFuture<Unit> RESOURCE_RELOAD_INITIAL_TASK;
   public static byte[] reserve;
   private static int MAX_SUPPORTED_TEXTURE_SIZE;
   private final File resourcePackDirectory;
   private final PropertyMap profileProperties;
   private final DisplayData displayData;
   private ServerData currentServer;
   private TextureManager textureManager;
   private static Minecraft instance;
   private final DataFixer fixerUpper;
   public MultiPlayerGameMode gameMode;
   private VirtualScreen virtualScreen;
   public Window window;
   private boolean hasCrashed;
   private CrashReport delayedCrash;
   private boolean connectedToRealms;
   private final Timer timer = new Timer(20.0F, 0L);
   private final Snooper snooper = new Snooper("client", this, Util.getMillis());
   public MultiPlayerLevel level;
   public LevelRenderer levelRenderer;
   private EntityRenderDispatcher entityRenderDispatcher;
   private ItemRenderer itemRenderer;
   private ItemInHandRenderer itemInHandRenderer;
   public LocalPlayer player;
   @Nullable
   public Entity cameraEntity;
   @Nullable
   public Entity crosshairPickEntity;
   public ParticleEngine particleEngine;
   private final SearchRegistry searchRegistry = new SearchRegistry();
   private final User user;
   private boolean pause;
   private float pausePartialTick;
   public Font font;
   @Nullable
   public Screen screen;
   @Nullable
   public Overlay overlay;
   public GameRenderer gameRenderer;
   public DebugRenderer debugRenderer;
   protected int missTime;
   @Nullable
   private IntegratedServer singleplayerServer;
   private final AtomicReference<StoringChunkProgressListener> progressListener = new AtomicReference();
   public Gui gui;
   public boolean noRender;
   public HitResult hitResult;
   public Options options;
   private HotbarManager hotbarManager;
   public MouseHandler mouseHandler;
   public KeyboardHandler keyboardHandler;
   public final File gameDirectory;
   private final File assetsDirectory;
   private final String launchedVersion;
   private final String versionType;
   private final Proxy proxy;
   private LevelStorageSource levelSource;
   private static int fps;
   private int rightClickDelay;
   private String connectToIp;
   private int connectToPort;
   public final FrameTimer frameTimer = new FrameTimer();
   private long lastNanoTime = Util.getNanos();
   private final boolean is64bit;
   private final boolean demo;
   @Nullable
   private Connection pendingConnection;
   private boolean isLocalServer;
   private final GameProfiler profiler = new GameProfiler(() -> {
      return this.timer.ticks;
   });
   private ReloadableResourceManager resourceManager;
   private final ClientPackSource clientPackSource;
   private final PackRepository<UnopenedResourcePack> resourcePackRepository;
   private LanguageManager languageManager;
   private BlockColors blockColors;
   private ItemColors itemColors;
   private RenderTarget mainRenderTarget;
   private TextureAtlas textureAtlas;
   private SoundManager soundManager;
   private MusicManager musicManager;
   private FontManager fontManager;
   private SplashManager splashManager;
   private final MinecraftSessionService minecraftSessionService;
   private SkinManager skinManager;
   private final Thread gameThread = Thread.currentThread();
   private ModelManager modelManager;
   private BlockRenderDispatcher blockRenderer;
   private PaintingTextureManager paintingTextures;
   private MobEffectTextureManager mobEffectTextures;
   private final ToastComponent toast;
   private final Game game = new Game(this);
   private volatile boolean running = true;
   public String fpsString = "";
   public boolean smartCull = true;
   private long lastTime;
   private int frames;
   private final Tutorial tutorial;
   private boolean windowActive;
   private final Queue<Runnable> progressTasks = Queues.newConcurrentLinkedQueue();
   private CompletableFuture<Void> pendingReload;
   private String debugPath = "root";

   public Minecraft(GameConfig var1) {
      super("Client");
      this.displayData = var1.display;
      instance = this;
      this.gameDirectory = var1.location.gameDirectory;
      this.assetsDirectory = var1.location.assetDirectory;
      this.resourcePackDirectory = var1.location.resourcePackDirectory;
      this.launchedVersion = var1.game.launchVersion;
      this.versionType = var1.game.versionType;
      this.profileProperties = var1.user.profileProperties;
      this.clientPackSource = new ClientPackSource(new File(this.gameDirectory, "server-resource-packs"), var1.location.getAssetIndex());
      this.resourcePackRepository = new PackRepository((var0, var1x, var2, var3, var4, var5) -> {
         Supplier var6;
         if (var4.getPackFormat() < SharedConstants.getCurrentVersion().getPackVersion()) {
            var6 = () -> {
               return new LegacyResourcePackAdapter((Pack)var2.get(), LegacyResourcePackAdapter.V3);
            };
         } else {
            var6 = var2;
         }

         return new UnopenedResourcePack(var0, var1x, var6, var3, var4, var5);
      });
      this.resourcePackRepository.addSource(this.clientPackSource);
      this.resourcePackRepository.addSource(new FolderRepositorySource(this.resourcePackDirectory));
      this.proxy = var1.user.proxy == null ? Proxy.NO_PROXY : var1.user.proxy;
      this.minecraftSessionService = (new YggdrasilAuthenticationService(this.proxy, UUID.randomUUID().toString())).createMinecraftSessionService();
      this.user = var1.user.user;
      LOGGER.info("Setting user: {}", this.user.getName());
      LOGGER.debug("(Session ID is {})", this.user.getSessionId());
      this.demo = var1.game.demo;
      this.is64bit = checkIs64Bit();
      this.singleplayerServer = null;
      if (var1.server.hostname != null) {
         this.connectToIp = var1.server.hostname;
         this.connectToPort = var1.server.port;
      }

      Bootstrap.bootStrap();
      Bootstrap.validate();
      KeybindComponent.keyResolver = KeyMapping::createNameSupplier;
      this.fixerUpper = DataFixers.getDataFixer();
      this.toast = new ToastComponent(this);
      this.tutorial = new Tutorial(this);
   }

   public void run() {
      this.running = true;

      CrashReport var2;
      try {
         this.init();
      } catch (Throwable var9) {
         var2 = CrashReport.forThrowable(var9, "Initializing game");
         var2.addCategory("Initialization");
         this.crash(this.fillReport(var2));
         return;
      }

      try {
         boolean var1 = false;

         while(this.running) {
            if (this.hasCrashed && this.delayedCrash != null) {
               this.crash(this.delayedCrash);
               return;
            }

            try {
               this.runTick(!var1);
            } catch (OutOfMemoryError var10) {
               if (var1) {
                  throw var10;
               }

               this.emergencySave();
               this.setScreen(new OutOfMemoryScreen());
               System.gc();
               LOGGER.fatal("Out of memory", var10);
               var1 = true;
            }
         }
      } catch (ReportedException var11) {
         this.fillReport(var11.getReport());
         this.emergencySave();
         LOGGER.fatal("Reported exception thrown!", var11);
         this.crash(var11.getReport());
      } catch (Throwable var12) {
         var2 = this.fillReport(new CrashReport("Unexpected error", var12));
         LOGGER.fatal("Unreported exception thrown!", var12);
         this.emergencySave();
         this.crash(var2);
      } finally {
         this.destroy();
      }

   }

   private void init() {
      this.options = new Options(this, this.gameDirectory);
      this.hotbarManager = new HotbarManager(this.gameDirectory, this.fixerUpper);
      this.startTimerHackThread();
      LOGGER.info("LWJGL Version: {}", GLX.getLWJGLVersion());
      DisplayData var1 = this.displayData;
      if (this.options.overrideHeight > 0 && this.options.overrideWidth > 0) {
         var1 = new DisplayData(this.options.overrideWidth, this.options.overrideHeight, var1.fullscreenWidth, var1.fullscreenHeight, var1.isFullscreen);
      }

      LongSupplier var2 = GLX.initGlfw();
      if (var2 != null) {
         Util.timeSource = var2;
      }

      this.virtualScreen = new VirtualScreen(this);
      this.window = this.virtualScreen.newWindow(var1, this.options.fullscreenVideoModeString, "Minecraft " + SharedConstants.getCurrentVersion().getName());
      this.setWindowActive(true);

      try {
         InputStream var3 = this.getClientPackSource().getVanillaPack().getResource(PackType.CLIENT_RESOURCES, new ResourceLocation("icons/icon_16x16.png"));
         InputStream var4 = this.getClientPackSource().getVanillaPack().getResource(PackType.CLIENT_RESOURCES, new ResourceLocation("icons/icon_32x32.png"));
         this.window.setIcon(var3, var4);
      } catch (IOException var6) {
         LOGGER.error("Couldn't set icon", var6);
      }

      this.window.setFramerateLimit(this.options.framerateLimit);
      this.mouseHandler = new MouseHandler(this);
      this.mouseHandler.setup(this.window.getWindow());
      this.keyboardHandler = new KeyboardHandler(this);
      this.keyboardHandler.setup(this.window.getWindow());
      GLX.init();
      GlDebug.enableDebugCallback(this.options.glDebugVerbosity, false);
      this.mainRenderTarget = new RenderTarget(this.window.getWidth(), this.window.getHeight(), true, ON_OSX);
      this.mainRenderTarget.setClearColor(0.0F, 0.0F, 0.0F, 0.0F);
      this.resourceManager = new SimpleReloadableResourceManager(PackType.CLIENT_RESOURCES, this.gameThread);
      this.options.loadResourcePacks(this.resourcePackRepository);
      this.resourcePackRepository.reload();
      List var7 = (List)this.resourcePackRepository.getSelected().stream().map(UnopenedPack::open).collect(Collectors.toList());
      Iterator var8 = var7.iterator();

      while(var8.hasNext()) {
         Pack var5 = (Pack)var8.next();
         this.resourceManager.add(var5);
      }

      this.languageManager = new LanguageManager(this.options.languageCode);
      this.resourceManager.registerReloadListener(this.languageManager);
      this.languageManager.reload(var7);
      this.textureManager = new TextureManager(this.resourceManager);
      this.resourceManager.registerReloadListener(this.textureManager);
      this.resizeDisplay();
      this.skinManager = new SkinManager(this.textureManager, new File(this.assetsDirectory, "skins"), this.minecraftSessionService);
      this.levelSource = new LevelStorageSource(this.gameDirectory.toPath().resolve("saves"), this.gameDirectory.toPath().resolve("backups"), this.fixerUpper);
      this.soundManager = new SoundManager(this.resourceManager, this.options);
      this.resourceManager.registerReloadListener(this.soundManager);
      this.splashManager = new SplashManager(this.user);
      this.resourceManager.registerReloadListener(this.splashManager);
      this.musicManager = new MusicManager(this);
      this.fontManager = new FontManager(this.textureManager, this.isEnforceUnicode());
      this.resourceManager.registerReloadListener(this.fontManager.getReloadListener());
      this.font = this.fontManager.get(DEFAULT_FONT);
      if (this.options.languageCode != null) {
         this.font.setBidirectional(this.languageManager.isBidirectional());
      }

      this.resourceManager.registerReloadListener(new GrassColorReloadListener());
      this.resourceManager.registerReloadListener(new FoliageColorReloadListener());
      this.window.setGlErrorSection("Startup");
      GlStateManager.enableTexture();
      GlStateManager.shadeModel(7425);
      GlStateManager.clearDepth(1.0D);
      GlStateManager.enableDepthTest();
      GlStateManager.depthFunc(515);
      GlStateManager.enableAlphaTest();
      GlStateManager.alphaFunc(516, 0.1F);
      GlStateManager.cullFace(GlStateManager.CullFace.BACK);
      GlStateManager.matrixMode(5889);
      GlStateManager.loadIdentity();
      GlStateManager.matrixMode(5888);
      this.window.setGlErrorSection("Post startup");
      this.textureAtlas = new TextureAtlas("textures");
      this.textureAtlas.setMaxMipLevel(this.options.mipmapLevels);
      this.textureManager.register((ResourceLocation)TextureAtlas.LOCATION_BLOCKS, (TickableTextureObject)this.textureAtlas);
      this.textureManager.bind(TextureAtlas.LOCATION_BLOCKS);
      this.textureAtlas.setFilter(false, this.options.mipmapLevels > 0);
      this.blockColors = BlockColors.createDefault();
      this.itemColors = ItemColors.createDefault(this.blockColors);
      this.modelManager = new ModelManager(this.textureAtlas, this.blockColors);
      this.resourceManager.registerReloadListener(this.modelManager);
      this.itemRenderer = new ItemRenderer(this.textureManager, this.modelManager, this.itemColors);
      this.entityRenderDispatcher = new EntityRenderDispatcher(this.textureManager, this.itemRenderer, this.resourceManager);
      this.itemInHandRenderer = new ItemInHandRenderer(this);
      this.resourceManager.registerReloadListener(this.itemRenderer);
      this.gameRenderer = new GameRenderer(this, this.resourceManager);
      this.resourceManager.registerReloadListener(this.gameRenderer);
      this.blockRenderer = new BlockRenderDispatcher(this.modelManager.getBlockModelShaper(), this.blockColors);
      this.resourceManager.registerReloadListener(this.blockRenderer);
      this.levelRenderer = new LevelRenderer(this);
      this.resourceManager.registerReloadListener(this.levelRenderer);
      this.createSearchTrees();
      this.resourceManager.registerReloadListener(this.searchRegistry);
      GlStateManager.viewport(0, 0, this.window.getWidth(), this.window.getHeight());
      this.particleEngine = new ParticleEngine(this.level, this.textureManager);
      this.resourceManager.registerReloadListener(this.particleEngine);
      this.paintingTextures = new PaintingTextureManager(this.textureManager);
      this.resourceManager.registerReloadListener(this.paintingTextures);
      this.mobEffectTextures = new MobEffectTextureManager(this.textureManager);
      this.resourceManager.registerReloadListener(this.mobEffectTextures);
      this.gui = new Gui(this);
      this.debugRenderer = new DebugRenderer(this);
      GLX.setGlfwErrorCallback(this::onFullscreenError);
      if (this.options.fullscreen && !this.window.isFullscreen()) {
         this.window.toggleFullScreen();
         this.options.fullscreen = this.window.isFullscreen();
      }

      this.window.updateVsync(this.options.enableVsync);
      this.window.updateRawMouseInput(this.options.rawMouseInput);
      this.window.setDefaultGlErrorCallback();
      if (this.connectToIp != null) {
         this.setScreen(new ConnectScreen(new TitleScreen(), this, this.connectToIp, this.connectToPort));
      } else {
         this.setScreen(new TitleScreen(true));
      }

      LoadingOverlay.registerTextures(this);
      this.setOverlay(new LoadingOverlay(this, this.resourceManager.createQueuedReload(Util.backgroundExecutor(), this, RESOURCE_RELOAD_INITIAL_TASK), () -> {
         if (SharedConstants.IS_RUNNING_IN_IDE) {
            this.selfTest();
         }

      }, false));
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
      this.hasCrashed = true;
      this.delayedCrash = var1;
   }

   public void crash(CrashReport var1) {
      File var2 = new File(getInstance().gameDirectory, "crash-reports");
      File var3 = new File(var2, "crash-" + (new SimpleDateFormat("yyyy-MM-dd_HH.mm.ss")).format(new Date()) + "-client.txt");
      Bootstrap.realStdoutPrintln(var1.getFriendlyReport());
      if (var1.getSaveFile() != null) {
         Bootstrap.realStdoutPrintln("#@!@# Game crashed! Crash report saved to: #@!@# " + var1.getSaveFile());
         System.exit(-1);
      } else if (var1.saveToFile(var3)) {
         Bootstrap.realStdoutPrintln("#@!@# Game crashed! Crash report saved to: #@!@# " + var3.getAbsolutePath());
         System.exit(-1);
      } else {
         Bootstrap.realStdoutPrintln("#@?@# Game crashed! Crash report could not be saved. #@?@#");
         System.exit(-2);
      }

   }

   public boolean isEnforceUnicode() {
      return this.options.forceUnicodeFont;
   }

   public CompletableFuture<Void> reloadResourcePacks() {
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
            this.setOverlay(new LoadingOverlay(this, this.resourceManager.createFullReload(Util.backgroundExecutor(), this, RESOURCE_RELOAD_INITIAL_TASK, var2), () -> {
               this.languageManager.reload(var2);
               if (this.levelRenderer != null) {
                  this.levelRenderer.allChanged();
               }

               var1.complete((Object)null);
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
         var1 = new DeathScreen((Component)null, this.level.getLevelData().isHardcore());
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
         if (!this.hasCrashed) {
            System.exit(0);
         }

      }

   }

   public void close() {
      try {
         this.textureAtlas.clearTextureData();
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
      this.window.setGlErrorSection("Pre render");
      long var2 = Util.getNanos();
      this.profiler.startTick();
      if (GLX.shouldClose(this.window)) {
         this.stop();
      }

      if (this.pendingReload != null && !(this.overlay instanceof LoadingOverlay)) {
         CompletableFuture var4 = this.pendingReload;
         this.pendingReload = null;
         this.reloadResourcePacks().thenRun(() -> {
            var4.complete((Object)null);
         });
      }

      Runnable var12;
      while((var12 = (Runnable)this.progressTasks.poll()) != null) {
         var12.run();
      }

      if (var1) {
         this.timer.advanceTime(Util.getMillis());
         this.profiler.push("scheduledExecutables");
         this.runAllTasks();
         this.profiler.pop();
      }

      long var5 = Util.getNanos();
      this.profiler.push("tick");
      if (var1) {
         for(int var7 = 0; var7 < Math.min(10, this.timer.ticks); ++var7) {
            this.tick();
         }
      }

      this.mouseHandler.turnPlayer();
      this.window.setGlErrorSection("Render");
      GLX.pollEvents();
      long var13 = Util.getNanos() - var5;
      this.profiler.popPush("sound");
      this.soundManager.updateSource(this.gameRenderer.getMainCamera());
      this.profiler.pop();
      this.profiler.push("render");
      GlStateManager.pushMatrix();
      GlStateManager.clear(16640, ON_OSX);
      this.mainRenderTarget.bindWrite(true);
      this.profiler.push("display");
      GlStateManager.enableTexture();
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
      GlStateManager.popMatrix();
      GlStateManager.pushMatrix();
      this.mainRenderTarget.blitToScreen(this.window.getWidth(), this.window.getHeight());
      GlStateManager.popMatrix();
      this.profiler.startTick();
      this.updateDisplay(true);
      Thread.yield();
      this.window.setGlErrorSection("Post render");
      ++this.frames;
      boolean var9 = this.hasSingleplayerServer() && (this.screen != null && this.screen.isPauseScreen() || this.overlay != null && this.overlay.isPauseScreen()) && !this.singleplayerServer.isPublished();
      if (this.pause != var9) {
         if (this.pause) {
            this.pausePartialTick = this.timer.partialTick;
         } else {
            this.timer.partialTick = this.pausePartialTick;
         }

         this.pause = var9;
      }

      long var10 = Util.getNanos();
      this.frameTimer.logFrameDuration(var10 - this.lastNanoTime);
      this.lastNanoTime = var10;

      while(Util.getMillis() >= this.lastTime + 1000L) {
         fps = this.frames;
         this.fpsString = String.format("%d fps (%d chunk update%s) T: %s%s%s%s%s", fps, RenderChunk.updateCounter, RenderChunk.updateCounter == 1 ? "" : "s", (double)this.options.framerateLimit == Option.FRAMERATE_LIMIT.getMaxValue() ? "inf" : this.options.framerateLimit, this.options.enableVsync ? " vsync" : "", this.options.fancyGraphics ? "" : " fast", this.options.renderClouds == CloudStatus.OFF ? "" : (this.options.renderClouds == CloudStatus.FAST ? " fast-clouds" : " fancy-clouds"), GLX.useVbo() ? " vbo" : "");
         RenderChunk.updateCounter = 0;
         this.lastTime += 1000L;
         this.frames = 0;
         this.snooper.prepare();
         if (!this.snooper.isStarted()) {
            this.snooper.start();
         }
      }

      this.profiler.endTick();
   }

   public void updateDisplay(boolean var1) {
      this.profiler.push("display_update");
      this.window.updateDisplay(this.options.fullscreen);
      this.profiler.pop();
      if (var1 && this.isFramerateLimited()) {
         this.profiler.push("fpslimit_wait");
         this.window.limitDisplayFPS();
         this.profiler.pop();
      }

   }

   public void resizeDisplay() {
      int var1 = this.window.calculateScale(this.options.guiScale, this.isEnforceUnicode());
      this.window.setGuiScale((double)var1);
      if (this.screen != null) {
         this.screen.resize(this, this.window.getGuiScaledWidth(), this.window.getGuiScaledHeight());
      }

      RenderTarget var2 = this.getMainRenderTarget();
      if (var2 != null) {
         var2.resize(this.window.getWidth(), this.window.getHeight(), ON_OSX);
      }

      if (this.gameRenderer != null) {
         this.gameRenderer.resize(this.window.getWidth(), this.window.getHeight());
      }

      if (this.mouseHandler != null) {
         this.mouseHandler.setIgnoreFirstMove();
      }

   }

   private int getFramerateLimit() {
      return this.level != null || this.screen == null && this.overlay == null ? this.window.getFramerateLimit() : 60;
   }

   private boolean isFramerateLimited() {
      return (double)this.getFramerateLimit() < Option.FRAMERATE_LIMIT.getMaxValue();
   }

   public void emergencySave() {
      try {
         reserve = new byte[0];
         this.levelRenderer.clear();
      } catch (Throwable var3) {
      }

      try {
         System.gc();
         if (this.hasSingleplayerServer()) {
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
               int var5 = this.debugPath.lastIndexOf(46);
               if (var5 >= 0) {
                  this.debugPath = this.debugPath.substring(0, var5);
               }
            }
         } else {
            --var1;
            if (var1 < var3.size() && !"unspecified".equals(((ResultField)var3.get(var1)).name)) {
               if (!this.debugPath.isEmpty()) {
                  this.debugPath = this.debugPath + ".";
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
         GlStateManager.clear(256, ON_OSX);
         GlStateManager.matrixMode(5889);
         GlStateManager.enableColorMaterial();
         GlStateManager.loadIdentity();
         GlStateManager.ortho(0.0D, (double)this.window.getWidth(), (double)this.window.getHeight(), 0.0D, 1000.0D, 3000.0D);
         GlStateManager.matrixMode(5888);
         GlStateManager.loadIdentity();
         GlStateManager.translatef(0.0F, 0.0F, -2000.0F);
         GlStateManager.lineWidth(1.0F);
         GlStateManager.disableTexture();
         Tesselator var4 = Tesselator.getInstance();
         BufferBuilder var5 = var4.getBuilder();
         boolean var6 = true;
         int var7 = this.window.getWidth() - 160 - 10;
         int var8 = this.window.getHeight() - 320;
         GlStateManager.enableBlend();
         var5.begin(7, DefaultVertexFormat.POSITION_COLOR);
         var5.vertex((double)((float)var7 - 176.0F), (double)((float)var8 - 96.0F - 16.0F), 0.0D).color(200, 0, 0, 0).endVertex();
         var5.vertex((double)((float)var7 - 176.0F), (double)(var8 + 320), 0.0D).color(200, 0, 0, 0).endVertex();
         var5.vertex((double)((float)var7 + 176.0F), (double)(var8 + 320), 0.0D).color(200, 0, 0, 0).endVertex();
         var5.vertex((double)((float)var7 + 176.0F), (double)((float)var8 - 96.0F - 16.0F), 0.0D).color(200, 0, 0, 0).endVertex();
         var4.end();
         GlStateManager.disableBlend();
         double var9 = 0.0D;

         int var13;
         for(int var11 = 0; var11 < var2.size(); ++var11) {
            ResultField var12 = (ResultField)var2.get(var11);
            var13 = Mth.floor(var12.percentage / 4.0D) + 1;
            var5.begin(6, DefaultVertexFormat.POSITION_COLOR);
            int var14 = var12.getColor();
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
               var5.vertex((double)((float)var7 + var20), (double)((float)var8 - var21), 0.0D).color(var15 >> 1, var16 >> 1, var17 >> 1, 255).endVertex();
               var5.vertex((double)((float)var7 + var20), (double)((float)var8 - var21 + 10.0F), 0.0D).color(var15 >> 1, var16 >> 1, var17 >> 1, 255).endVertex();
            }

            var4.end();
            var9 += var12.percentage;
         }

         DecimalFormat var22 = new DecimalFormat("##0.00");
         var22.setDecimalFormatSymbols(DecimalFormatSymbols.getInstance(Locale.ROOT));
         GlStateManager.enableTexture();
         String var23 = "";
         if (!"unspecified".equals(var3.name)) {
            var23 = var23 + "[0] ";
         }

         if (var3.name.isEmpty()) {
            var23 = var23 + "ROOT ";
         } else {
            var23 = var23 + var3.name + ' ';
         }

         var13 = 16777215;
         this.font.drawShadow(var23, (float)(var7 - 160), (float)(var8 - 80 - 16), 16777215);
         var23 = var22.format(var3.globalPercentage) + "%";
         this.font.drawShadow(var23, (float)(var7 + 160 - this.font.width(var23)), (float)(var8 - 80 - 16), 16777215);

         for(int var26 = 0; var26 < var2.size(); ++var26) {
            ResultField var24 = (ResultField)var2.get(var26);
            StringBuilder var25 = new StringBuilder();
            if ("unspecified".equals(var24.name)) {
               var25.append("[?] ");
            } else {
               var25.append("[").append(var26 + 1).append("] ");
            }

            String var27 = var25.append(var24.name).toString();
            this.font.drawShadow(var27, (float)(var7 - 160), (float)(var8 + 80 + var26 * 8 + 20), var24.getColor());
            var27 = var22.format(var24.percentage) + "%";
            this.font.drawShadow(var27, (float)(var7 + 160 - 50 - this.font.width(var27)), (float)(var8 + 80 + var26 * 8 + 20), var24.getColor());
            var27 = var22.format(var24.globalPercentage) + "%";
            this.font.drawShadow(var27, (float)(var7 + 160 - this.font.width(var27)), (float)(var8 + 80 + var26 * 8 + 20), var24.getColor());
         }

      }
   }

   public void stop() {
      this.running = false;
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
                     if (this.gameMode.interactAt(this.player, var7, var6, var4) == InteractionResult.SUCCESS) {
                        return;
                     }

                     if (this.gameMode.interact(this.player, var7, var4) == InteractionResult.SUCCESS) {
                        return;
                     }
                     break;
                  case BLOCK:
                     BlockHitResult var8 = (BlockHitResult)this.hitResult;
                     int var9 = var5.getCount();
                     InteractionResult var10 = this.gameMode.useItemOn(this.player, this.level, var4, var8);
                     if (var10 == InteractionResult.SUCCESS) {
                        this.player.swing(var4);
                        if (!var5.isEmpty() && (var5.getCount() != var9 || this.gameMode.hasInfiniteItems())) {
                           this.gameRenderer.itemInHandRenderer.itemUsed(var4);
                        }

                        return;
                     }

                     if (var10 == InteractionResult.FAIL) {
                        return;
                     }
                  }
               }

               if (!var5.isEmpty() && this.gameMode.useItem(this.player, this.level, var4) == InteractionResult.SUCCESS) {
                  this.gameRenderer.itemInHandRenderer.itemUsed(var4);
                  return;
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
         this.profiler.popPush("GLFW events");
         GLX.pollEvents();
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
      } else if (this.gameRenderer.postEffectActive()) {
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
            this.level.animateTick(Mth.floor(this.player.x), Mth.floor(this.player.y), Mth.floor(this.player.z));
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
         if (!this.player.isSpectator()) {
            this.player.drop(Screen.hasControlDown());
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

         label111:
         while(true) {
            if (!this.options.keyAttack.consumeClick()) {
               while(this.options.keyUse.consumeClick()) {
               }

               while(true) {
                  if (this.options.keyPickItem.consumeClick()) {
                     continue;
                  }
                  break label111;
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

         if (this.hasCrashed && this.delayedCrash != null) {
            this.crash(this.delayedCrash);
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

   public void setLevel(MultiPlayerLevel var1) {
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
         this.setCurrentServer((ServerData)null);
         this.isLocalServer = false;
         this.game.onLeaveGameSession();
      }

      this.level = null;
      this.updateLevelInEngines((MultiPlayerLevel)null);
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

   private void updateLevelInEngines(@Nullable MultiPlayerLevel var1) {
      if (this.levelRenderer != null) {
         this.levelRenderer.setLevel(var1);
      }

      if (this.particleEngine != null) {
         this.particleEngine.setLevel(var1);
      }

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
      return instance == null || !instance.options.hideGui;
   }

   public static boolean useFancyGraphics() {
      return instance != null && instance.options.fancyGraphics;
   }

   public static boolean useAmbientOcclusion() {
      return instance != null && instance.options.ambientOcclusion != AmbientOcclusionStatus.OFF;
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
         var5.add(new StringTag("\"(+NBT)\""));
         var4.put("Lore", var5);
         var1.addTagElement("display", var4);
         return var1;
      }
   }

   public CrashReport fillReport(CrashReport var1) {
      CrashReportCategory var2 = var1.getSystemDetails();
      var2.setDetail("Launched Version", () -> {
         return this.launchedVersion;
      });
      var2.setDetail("LWJGL", GLX::getLWJGLVersion);
      var2.setDetail("OpenGL", GLX::getOpenGLVersionString);
      var2.setDetail("GL Caps", GLX::getCapsString);
      var2.setDetail("Using VBOs", () -> {
         return "Yes";
      });
      var2.setDetail("Is Modded", () -> {
         String var0 = ClientBrandRetriever.getClientModName();
         if (!"vanilla".equals(var0)) {
            return "Definitely; Client brand changed to '" + var0 + "'";
         } else {
            return Minecraft.class.getSigners() == null ? "Very likely; Jar signature invalidated" : "Probably not. Jar signature remains and client brand is untouched.";
         }
      });
      var2.setDetail("Type", (Object)"Client (map_client.txt)");
      var2.setDetail("Resource Packs", () -> {
         StringBuilder var1 = new StringBuilder();
         Iterator var2 = this.options.resourcePacks.iterator();

         while(var2.hasNext()) {
            String var3 = (String)var2.next();
            if (var1.length() > 0) {
               var1.append(", ");
            }

            var1.append(var3);
            if (this.options.incompatibleResourcePacks.contains(var3)) {
               var1.append(" (incompatible)");
            }
         }

         return var1.toString();
      });
      var2.setDetail("Current Language", () -> {
         return this.languageManager.getSelected().toString();
      });
      var2.setDetail("CPU", GLX::getCpuInfo);
      if (this.level != null) {
         this.level.fillReportDetails(var1);
      }

      return var1;
   }

   public static Minecraft getInstance() {
      return instance;
   }

   public CompletableFuture<Void> delayTextureReload() {
      return this.submit(this::reloadResourcePacks).thenCompose((var0) -> {
         return var0;
      });
   }

   public void populateSnooper(Snooper var1) {
      var1.setDynamicData("fps", fps);
      var1.setDynamicData("vsync_enabled", this.options.enableVsync);
      int var2 = GLX.getRefreshRate(this.window);
      var1.setDynamicData("display_frequency", var2);
      var1.setDynamicData("display_type", this.window.isFullscreen() ? "fullscreen" : "windowed");
      var1.setDynamicData("run_time", (Util.getMillis() - var1.getStartupTime()) / 60L * 1000L);
      var1.setDynamicData("current_action", this.getCurrentSnooperAction());
      var1.setDynamicData("language", this.options.languageCode == null ? "en_us" : this.options.languageCode);
      String var3 = ByteOrder.nativeOrder() == ByteOrder.LITTLE_ENDIAN ? "little" : "big";
      var1.setDynamicData("endianness", var3);
      var1.setDynamicData("subtitles", this.options.showSubtitles);
      var1.setDynamicData("touch", this.options.touchscreen ? "touch" : "mouse");
      int var4 = 0;
      Iterator var5 = this.resourcePackRepository.getSelected().iterator();

      while(var5.hasNext()) {
         UnopenedResourcePack var6 = (UnopenedResourcePack)var5.next();
         if (!var6.isRequired() && !var6.isFixedPosition()) {
            var1.setDynamicData("resource_pack[" + var4++ + "]", var6.getId());
         }
      }

      var1.setDynamicData("resource_packs", var4);
      if (this.singleplayerServer != null && this.singleplayerServer.getSnooper() != null) {
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

   public static int maxSupportedTextureSize() {
      if (MAX_SUPPORTED_TEXTURE_SIZE == -1) {
         for(int var0 = 16384; var0 > 0; var0 >>= 1) {
            GlStateManager.texImage2D(32868, 0, 6408, var0, var0, 0, 6408, 5121, (IntBuffer)null);
            int var1 = GlStateManager.getTexLevelParameter(32868, 0, 4096);
            if (var1 != 0) {
               MAX_SUPPORTED_TEXTURE_SIZE = var0;
               return var0;
            }
         }

         MAX_SUPPORTED_TEXTURE_SIZE = Mth.clamp(GlStateManager.getInteger(3379), 1024, 16384);
         LOGGER.info("Failed to determine maximum texture size by probing, trying GL_MAX_TEXTURE_SIZE = {}", MAX_SUPPORTED_TEXTURE_SIZE);
      }

      return MAX_SUPPORTED_TEXTURE_SIZE;
   }

   public void setCurrentServer(ServerData var1) {
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

   public PackRepository<UnopenedResourcePack> getResourcePackRepository() {
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

   public TextureAtlas getTextureAtlas() {
      return this.textureAtlas;
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

   public <T> MutableSearchTree<T> getSearchTree(SearchRegistry.Key<T> var1) {
      return this.searchRegistry.getTree(var1);
   }

   public static int getAverageFps() {
      return fps;
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

   static {
      ON_OSX = Util.getPlatform() == Util.OS.OSX;
      DEFAULT_FONT = new ResourceLocation("default");
      ALT_FONT = new ResourceLocation("alt");
      RESOURCE_RELOAD_INITIAL_TASK = CompletableFuture.completedFuture(Unit.INSTANCE);
      reserve = new byte[10485760];
      MAX_SUPPORTED_TEXTURE_SIZE = -1;
   }
}
