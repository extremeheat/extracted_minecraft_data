package net.minecraft.client;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.collect.Queues;
import com.google.common.collect.UnmodifiableIterator;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.exceptions.AuthenticationException;
import com.mojang.authlib.minecraft.BanDetails;
import com.mojang.authlib.minecraft.MinecraftSessionService;
import com.mojang.authlib.minecraft.UserApiService;
import com.mojang.authlib.minecraft.UserApiService.UserFlag;
import com.mojang.authlib.yggdrasil.ProfileActionType;
import com.mojang.authlib.yggdrasil.ProfileResult;
import com.mojang.authlib.yggdrasil.ServicesKeyType;
import com.mojang.authlib.yggdrasil.YggdrasilAuthenticationService;
import com.mojang.blaze3d.TracyFrameCapture;
import com.mojang.blaze3d.pipeline.MainTarget;
import com.mojang.blaze3d.pipeline.RenderTarget;
import com.mojang.blaze3d.platform.ClientShutdownWatchdog;
import com.mojang.blaze3d.platform.DisplayData;
import com.mojang.blaze3d.platform.FramerateLimitTracker;
import com.mojang.blaze3d.platform.GlDebug;
import com.mojang.blaze3d.platform.GlUtil;
import com.mojang.blaze3d.platform.IconSet;
import com.mojang.blaze3d.platform.Window;
import com.mojang.blaze3d.platform.WindowEventHandler;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.systems.TimerQuery;
import com.mojang.blaze3d.vertex.BufferUploader;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.datafixers.DataFixer;
import com.mojang.jtracy.DiscontinuousFrame;
import com.mojang.jtracy.TracyClient;
import com.mojang.logging.LogUtils;
import com.mojang.realmsclient.client.RealmsClient;
import com.mojang.realmsclient.gui.RealmsDataFetcher;
import java.io.File;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.lang.management.ManagementFactory;
import java.net.Proxy;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.Objects;
import java.util.Optional;
import java.util.Queue;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.function.UnaryOperator;
import javax.annotation.Nullable;
import net.minecraft.ChatFormatting;
import net.minecraft.CrashReport;
import net.minecraft.CrashReportCategory;
import net.minecraft.CrashReportDetail;
import net.minecraft.FileUtil;
import net.minecraft.Optionull;
import net.minecraft.ReportType;
import net.minecraft.ReportedException;
import net.minecraft.SharedConstants;
import net.minecraft.SystemReport;
import net.minecraft.Util;
import net.minecraft.client.color.block.BlockColors;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiSpriteManager;
import net.minecraft.client.gui.components.DebugScreenOverlay;
import net.minecraft.client.gui.components.debugchart.ProfilerPieChart;
import net.minecraft.client.gui.components.toasts.SystemToast;
import net.minecraft.client.gui.components.toasts.ToastManager;
import net.minecraft.client.gui.components.toasts.TutorialToast;
import net.minecraft.client.gui.font.FontManager;
import net.minecraft.client.gui.font.providers.FreeTypeUtil;
import net.minecraft.client.gui.screens.AccessibilityOnboardingScreen;
import net.minecraft.client.gui.screens.BanNoticeScreens;
import net.minecraft.client.gui.screens.ChatScreen;
import net.minecraft.client.gui.screens.ConfirmLinkScreen;
import net.minecraft.client.gui.screens.DeathScreen;
import net.minecraft.client.gui.screens.GenericMessageScreen;
import net.minecraft.client.gui.screens.InBedChatScreen;
import net.minecraft.client.gui.screens.LevelLoadingScreen;
import net.minecraft.client.gui.screens.LoadingOverlay;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.gui.screens.OutOfMemoryScreen;
import net.minecraft.client.gui.screens.Overlay;
import net.minecraft.client.gui.screens.PauseScreen;
import net.minecraft.client.gui.screens.ProgressScreen;
import net.minecraft.client.gui.screens.ReceivingLevelScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.TitleScreen;
import net.minecraft.client.gui.screens.advancements.AdvancementsScreen;
import net.minecraft.client.gui.screens.inventory.CreativeModeInventoryScreen;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.client.gui.screens.social.PlayerSocialManager;
import net.minecraft.client.gui.screens.social.SocialInteractionsScreen;
import net.minecraft.client.gui.screens.worldselection.WorldOpenFlows;
import net.minecraft.client.main.GameConfig;
import net.minecraft.client.main.SilentInitException;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.multiplayer.ClientHandshakePacketListenerImpl;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.client.multiplayer.MultiPlayerGameMode;
import net.minecraft.client.multiplayer.ProfileKeyPairManager;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.client.multiplayer.TransferState;
import net.minecraft.client.multiplayer.chat.ChatListener;
import net.minecraft.client.multiplayer.chat.report.ReportEnvironment;
import net.minecraft.client.multiplayer.chat.report.ReportingContext;
import net.minecraft.client.particle.ParticleEngine;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.profiling.ClientMetricsSamplersProvider;
import net.minecraft.client.quickplay.QuickPlay;
import net.minecraft.client.quickplay.QuickPlayLog;
import net.minecraft.client.renderer.FogParameters;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.GpuWarnlistManager;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.MapRenderer;
import net.minecraft.client.renderer.RenderBuffers;
import net.minecraft.client.renderer.ShaderManager;
import net.minecraft.client.renderer.VirtualScreen;
import net.minecraft.client.renderer.block.BlockModelShaper;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderDispatcher;
import net.minecraft.client.renderer.debug.DebugRenderer;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.entity.EntityRenderers;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.item.ItemModelResolver;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.resources.ClientPackSource;
import net.minecraft.client.resources.FoliageColorReloadListener;
import net.minecraft.client.resources.GrassColorReloadListener;
import net.minecraft.client.resources.MapDecorationTextureManager;
import net.minecraft.client.resources.MapTextureManager;
import net.minecraft.client.resources.MobEffectTextureManager;
import net.minecraft.client.resources.PaintingTextureManager;
import net.minecraft.client.resources.SkinManager;
import net.minecraft.client.resources.SplashManager;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.client.resources.language.LanguageManager;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.EquipmentAssetManager;
import net.minecraft.client.resources.model.ModelManager;
import net.minecraft.client.resources.server.DownloadedPackSource;
import net.minecraft.client.server.IntegratedServer;
import net.minecraft.client.sounds.MusicInfo;
import net.minecraft.client.sounds.MusicManager;
import net.minecraft.client.sounds.SoundManager;
import net.minecraft.client.telemetry.ClientTelemetryManager;
import net.minecraft.client.telemetry.TelemetryProperty;
import net.minecraft.client.telemetry.events.GameLoadTimesEvent;
import net.minecraft.client.tutorial.Tutorial;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.Connection;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.contents.KeybindResolver;
import net.minecraft.network.protocol.game.ServerboundClientTickEndPacket;
import net.minecraft.network.protocol.game.ServerboundPlayerActionPacket;
import net.minecraft.network.protocol.login.ServerboundHelloPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.Bootstrap;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.Services;
import net.minecraft.server.WorldStem;
import net.minecraft.server.level.progress.ProcessorChunkProgressListener;
import net.minecraft.server.level.progress.StoringChunkProgressListener;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.VanillaPackResources;
import net.minecraft.server.packs.repository.FolderRepositorySource;
import net.minecraft.server.packs.repository.PackRepository;
import net.minecraft.server.packs.repository.PackSource;
import net.minecraft.server.packs.repository.RepositorySource;
import net.minecraft.server.packs.resources.ReloadInstance;
import net.minecraft.server.packs.resources.ReloadableResourceManager;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.players.GameProfileCache;
import net.minecraft.sounds.Music;
import net.minecraft.sounds.Musics;
import net.minecraft.tags.BiomeTags;
import net.minecraft.util.CommonLinks;
import net.minecraft.util.FileZipper;
import net.minecraft.util.MemoryReserve;
import net.minecraft.util.ModCheck;
import net.minecraft.util.SignatureValidator;
import net.minecraft.util.TimeUtil;
import net.minecraft.util.Unit;
import net.minecraft.util.datafix.DataFixers;
import net.minecraft.util.profiling.ContinuousProfiler;
import net.minecraft.util.profiling.EmptyProfileResults;
import net.minecraft.util.profiling.InactiveProfiler;
import net.minecraft.util.profiling.ProfileResults;
import net.minecraft.util.profiling.Profiler;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.util.profiling.SingleTickProfiler;
import net.minecraft.util.profiling.Zone;
import net.minecraft.util.profiling.metrics.profiling.ActiveMetricsRecorder;
import net.minecraft.util.profiling.metrics.profiling.InactiveMetricsRecorder;
import net.minecraft.util.profiling.metrics.profiling.MetricsRecorder;
import net.minecraft.util.profiling.metrics.storage.MetricsPersister;
import net.minecraft.util.random.SimpleWeightedRandomList;
import net.minecraft.util.thread.ReentrantBlockableEventLoop;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.TickRateManager;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.ChatVisiblity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.SkullBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.LevelStorageSource;
import net.minecraft.world.level.validation.DirectoryValidator;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import org.apache.commons.io.FileUtils;
import org.lwjgl.util.tinyfd.TinyFileDialogs;
import org.slf4j.Logger;

public class Minecraft extends ReentrantBlockableEventLoop<Runnable> implements WindowEventHandler {
   static Minecraft instance;
   private static final Logger LOGGER = LogUtils.getLogger();
   public static final boolean ON_OSX;
   private static final int MAX_TICKS_PER_UPDATE = 10;
   public static final ResourceLocation DEFAULT_FONT;
   public static final ResourceLocation UNIFORM_FONT;
   public static final ResourceLocation ALT_FONT;
   private static final ResourceLocation REGIONAL_COMPLIANCIES;
   private static final CompletableFuture<Unit> RESOURCE_RELOAD_INITIAL_TASK;
   private static final Component SOCIAL_INTERACTIONS_NOT_AVAILABLE;
   public static final String UPDATE_DRIVERS_ADVICE = "Please make sure you have up-to-date drivers (see aka.ms/mcdriver for instructions).";
   private final long canary = Double.doubleToLongBits(3.141592653589793);
   private final Path resourcePackDirectory;
   private final CompletableFuture<ProfileResult> profileFuture;
   private final TextureManager textureManager;
   private final ShaderManager shaderManager;
   private final DataFixer fixerUpper;
   private final VirtualScreen virtualScreen;
   private final Window window;
   private final DeltaTracker.Timer deltaTracker = new DeltaTracker.Timer(20.0F, 0L, this::getTickTargetMillis);
   private final RenderBuffers renderBuffers;
   public final LevelRenderer levelRenderer;
   private final EntityRenderDispatcher entityRenderDispatcher;
   private final ItemModelResolver itemModelResolver;
   private final ItemRenderer itemRenderer;
   private final MapRenderer mapRenderer;
   public final ParticleEngine particleEngine;
   private final User user;
   public final Font font;
   public final Font fontFilterFishy;
   public final GameRenderer gameRenderer;
   public final DebugRenderer debugRenderer;
   private final AtomicReference<StoringChunkProgressListener> progressListener = new AtomicReference();
   public final Gui gui;
   public final Options options;
   private final HotbarManager hotbarManager;
   public final MouseHandler mouseHandler;
   public final KeyboardHandler keyboardHandler;
   private InputType lastInputType;
   public final File gameDirectory;
   private final String launchedVersion;
   private final String versionType;
   private final Proxy proxy;
   private final LevelStorageSource levelSource;
   private final boolean demo;
   private final boolean allowsMultiplayer;
   private final boolean allowsChat;
   private final ReloadableResourceManager resourceManager;
   private final VanillaPackResources vanillaPackResources;
   private final DownloadedPackSource downloadedPackSource;
   private final PackRepository resourcePackRepository;
   private final LanguageManager languageManager;
   private final BlockColors blockColors;
   private final RenderTarget mainRenderTarget;
   @Nullable
   private final TracyFrameCapture tracyFrameCapture;
   private final SoundManager soundManager;
   private final MusicManager musicManager;
   private final FontManager fontManager;
   private final SplashManager splashManager;
   private final GpuWarnlistManager gpuWarnlistManager;
   private final PeriodicNotificationManager regionalCompliancies;
   private final YggdrasilAuthenticationService authenticationService;
   private final MinecraftSessionService minecraftSessionService;
   private final UserApiService userApiService;
   private final CompletableFuture<UserApiService.UserProperties> userPropertiesFuture;
   private final SkinManager skinManager;
   private final ModelManager modelManager;
   private final BlockRenderDispatcher blockRenderer;
   private final PaintingTextureManager paintingTextures;
   private final MobEffectTextureManager mobEffectTextures;
   private final MapTextureManager mapTextureManager;
   private final MapDecorationTextureManager mapDecorationTextures;
   private final GuiSpriteManager guiSprites;
   private final ToastManager toastManager;
   private final Tutorial tutorial;
   private final PlayerSocialManager playerSocialManager;
   private final BlockEntityRenderDispatcher blockEntityRenderDispatcher;
   private final ClientTelemetryManager telemetryManager;
   private final ProfileKeyPairManager profileKeyPairManager;
   private final RealmsDataFetcher realmsDataFetcher;
   private final QuickPlayLog quickPlayLog;
   @Nullable
   public MultiPlayerGameMode gameMode;
   @Nullable
   public ClientLevel level;
   @Nullable
   public LocalPlayer player;
   @Nullable
   private IntegratedServer singleplayerServer;
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
   private volatile boolean pause;
   private long lastNanoTime;
   private long lastTime;
   private int frames;
   public boolean noRender;
   @Nullable
   public Screen screen;
   @Nullable
   private Overlay overlay;
   private boolean clientLevelTeardownInProgress;
   Thread gameThread;
   private volatile boolean running;
   @Nullable
   private Supplier<CrashReport> delayedCrash;
   private static int fps;
   public String fpsString;
   private long frameTimeNs;
   private final FramerateLimitTracker framerateLimitTracker;
   public boolean wireframe;
   public boolean sectionPath;
   public boolean sectionVisibility;
   public boolean smartCull;
   private boolean windowActive;
   private final Queue<Runnable> progressTasks;
   @Nullable
   private CompletableFuture<Void> pendingReload;
   @Nullable
   private TutorialToast socialInteractionsToast;
   private int fpsPieRenderTicks;
   private final ContinuousProfiler fpsPieProfiler;
   private MetricsRecorder metricsRecorder;
   private final ResourceLoadStateTracker reloadStateTracker;
   private long savedCpuDuration;
   private double gpuUtilization;
   @Nullable
   private TimerQuery.FrameProfile currentFrameProfile;
   private final GameNarrator narrator;
   private final ChatListener chatListener;
   private ReportingContext reportingContext;
   private final CommandHistory commandHistory;
   private final DirectoryValidator directoryValidator;
   private boolean gameLoadFinished;
   private final long clientStartTimeMs;
   private long clientTickCount;

   public Minecraft(final GameConfig var1) {
      super("Client");
      this.lastInputType = InputType.NONE;
      this.regionalCompliancies = new PeriodicNotificationManager(REGIONAL_COMPLIANCIES, Minecraft::countryEqualsISO3);
      this.lastNanoTime = Util.getNanos();
      this.fpsString = "";
      this.smartCull = true;
      this.progressTasks = Queues.newConcurrentLinkedQueue();
      this.fpsPieProfiler = new ContinuousProfiler(Util.timeSource, () -> this.fpsPieRenderTicks);
      this.metricsRecorder = InactiveMetricsRecorder.INSTANCE;
      this.reloadStateTracker = new ResourceLoadStateTracker();
      instance = this;
      this.clientStartTimeMs = System.currentTimeMillis();
      this.gameDirectory = var1.location.gameDirectory;
      File var2 = var1.location.assetDirectory;
      this.resourcePackDirectory = var1.location.resourcePackDirectory.toPath();
      this.launchedVersion = var1.game.launchVersion;
      this.versionType = var1.game.versionType;
      Path var3 = this.gameDirectory.toPath();
      this.directoryValidator = LevelStorageSource.parseValidator(var3.resolve("allowed_symlinks.txt"));
      ClientPackSource var4 = new ClientPackSource(var1.location.getExternalAssetSource(), this.directoryValidator);
      this.downloadedPackSource = new DownloadedPackSource(this, var3.resolve("downloads"), var1.user);
      FolderRepositorySource var5 = new FolderRepositorySource(this.resourcePackDirectory, PackType.CLIENT_RESOURCES, PackSource.DEFAULT, this.directoryValidator);
      this.resourcePackRepository = new PackRepository(new RepositorySource[]{var4, this.downloadedPackSource.createRepositorySource(), var5});
      this.vanillaPackResources = var4.getVanillaPack();
      this.proxy = var1.user.proxy;
      this.authenticationService = new YggdrasilAuthenticationService(this.proxy);
      this.minecraftSessionService = this.authenticationService.createMinecraftSessionService();
      this.user = var1.user.user;
      this.profileFuture = CompletableFuture.supplyAsync(() -> this.minecraftSessionService.fetchProfile(this.user.getProfileId(), true), Util.nonCriticalIoPool());
      this.userApiService = this.createUserApiService(this.authenticationService, var1);
      this.userPropertiesFuture = CompletableFuture.supplyAsync(() -> {
         try {
            return this.userApiService.fetchProperties();
         } catch (AuthenticationException var2) {
            LOGGER.error("Failed to fetch user properties", var2);
            return UserApiService.OFFLINE_PROPERTIES;
         }
      }, Util.nonCriticalIoPool());
      LOGGER.info("Setting user: {}", this.user.getName());
      LOGGER.debug("(Session ID is {})", this.user.getSessionId());
      this.demo = var1.game.demo;
      this.allowsMultiplayer = !var1.game.disableMultiplayer;
      this.allowsChat = !var1.game.disableChat;
      this.singleplayerServer = null;
      KeybindResolver.setKeyResolver(KeyMapping::createNameSupplier);
      this.fixerUpper = DataFixers.getDataFixer();
      this.toastManager = new ToastManager(this);
      this.gameThread = Thread.currentThread();
      this.options = new Options(this, this.gameDirectory);
      RenderSystem.setShaderGlintAlpha((Double)this.options.glintStrength().get());
      this.running = true;
      this.tutorial = new Tutorial(this, this.options);
      this.hotbarManager = new HotbarManager(var3, this.fixerUpper);
      LOGGER.info("Backend library: {}", RenderSystem.getBackendDescription());
      DisplayData var6;
      if (this.options.overrideHeight > 0 && this.options.overrideWidth > 0) {
         var6 = new DisplayData(this.options.overrideWidth, this.options.overrideHeight, var1.display.fullscreenWidth, var1.display.fullscreenHeight, var1.display.isFullscreen);
      } else {
         var6 = var1.display;
      }

      Util.timeSource = RenderSystem.initBackendSystem();
      this.virtualScreen = new VirtualScreen(this);
      this.window = this.virtualScreen.newWindow(var6, this.options.fullscreenVideoModeString, this.createTitle());
      this.setWindowActive(true);
      this.window.setWindowCloseCallback(new Runnable() {
         private boolean threadStarted;

         public void run() {
            if (!this.threadStarted) {
               this.threadStarted = true;
               ClientShutdownWatchdog.startShutdownWatchdog(var1.location.gameDirectory, Minecraft.this.gameThread.threadId());
            }

         }
      });
      GameLoadTimesEvent.INSTANCE.endStep(TelemetryProperty.LOAD_TIME_PRE_WINDOW_MS);

      try {
         this.window.setIcon(this.vanillaPackResources, SharedConstants.getCurrentVersion().isStable() ? IconSet.RELEASE : IconSet.SNAPSHOT);
      } catch (IOException var13) {
         LOGGER.error("Couldn't set icon", var13);
      }

      this.mouseHandler = new MouseHandler(this);
      this.mouseHandler.setup(this.window.getWindow());
      this.keyboardHandler = new KeyboardHandler(this);
      this.keyboardHandler.setup(this.window.getWindow());
      RenderSystem.initRenderer(this.options.glDebugVerbosity, false);
      this.mainRenderTarget = new MainTarget(this.window.getWidth(), this.window.getHeight());
      this.mainRenderTarget.setClearColor(0.0F, 0.0F, 0.0F, 0.0F);
      this.mainRenderTarget.clear();
      this.resourceManager = new ReloadableResourceManager(PackType.CLIENT_RESOURCES);
      this.resourcePackRepository.reload();
      this.options.loadSelectedResourcePacks(this.resourcePackRepository);
      this.languageManager = new LanguageManager(this.options.languageCode, (var1x) -> {
         if (this.player != null) {
            this.player.connection.updateSearchTrees();
         }

      });
      this.resourceManager.registerReloadListener(this.languageManager);
      this.textureManager = new TextureManager(this.resourceManager);
      this.resourceManager.registerReloadListener(this.textureManager);
      this.shaderManager = new ShaderManager(this.textureManager, this::triggerResourcePackRecovery);
      this.resourceManager.registerReloadListener(this.shaderManager);
      this.skinManager = new SkinManager(var2.toPath().resolve("skins"), this.minecraftSessionService, this);
      this.levelSource = new LevelStorageSource(var3.resolve("saves"), var3.resolve("backups"), this.directoryValidator, this.fixerUpper);
      this.commandHistory = new CommandHistory(var3);
      this.soundManager = new SoundManager(this.options);
      this.resourceManager.registerReloadListener(this.soundManager);
      this.splashManager = new SplashManager(this.user);
      this.resourceManager.registerReloadListener(this.splashManager);
      this.musicManager = new MusicManager(this);
      this.fontManager = new FontManager(this.textureManager);
      this.font = this.fontManager.createFont();
      this.fontFilterFishy = this.fontManager.createFontFilterFishy();
      this.resourceManager.registerReloadListener(this.fontManager);
      this.updateFontOptions();
      this.resourceManager.registerReloadListener(new GrassColorReloadListener());
      this.resourceManager.registerReloadListener(new FoliageColorReloadListener());
      this.window.setErrorSection("Startup");
      RenderSystem.setupDefaultState(0, 0, this.window.getWidth(), this.window.getHeight());
      this.window.setErrorSection("Post startup");
      this.blockColors = BlockColors.createDefault();
      this.modelManager = new ModelManager(this.textureManager, this.blockColors, (Integer)this.options.mipmapLevels().get());
      this.resourceManager.registerReloadListener(this.modelManager);
      EquipmentAssetManager var7 = new EquipmentAssetManager();
      this.resourceManager.registerReloadListener(var7);
      this.itemModelResolver = new ItemModelResolver(this.modelManager);
      this.itemRenderer = new ItemRenderer(this.itemModelResolver);
      this.mapTextureManager = new MapTextureManager(this.textureManager);
      this.mapDecorationTextures = new MapDecorationTextureManager(this.textureManager);
      this.resourceManager.registerReloadListener(this.mapDecorationTextures);
      this.mapRenderer = new MapRenderer(this.mapDecorationTextures, this.mapTextureManager);

      try {
         int var8 = Runtime.getRuntime().availableProcessors();
         Tesselator.init();
         this.renderBuffers = new RenderBuffers(var8);
      } catch (OutOfMemoryError var12) {
         TinyFileDialogs.tinyfd_messageBox("Minecraft", "Oh no! The game was unable to allocate memory off-heap while trying to start. You may try to free some memory by closing other applications on your computer, check that your system meets the minimum requirements, and try again. If the problem persists, please visit: " + String.valueOf(CommonLinks.GENERAL_HELP), "ok", "error", true);
         throw new SilentInitException("Unable to allocate render buffers", var12);
      }

      this.playerSocialManager = new PlayerSocialManager(this, this.userApiService);
      this.blockRenderer = new BlockRenderDispatcher(this.modelManager.getBlockModelShaper(), this.modelManager.specialBlockModelRenderer(), this.blockColors);
      this.resourceManager.registerReloadListener(this.blockRenderer);
      this.entityRenderDispatcher = new EntityRenderDispatcher(this, this.textureManager, this.itemModelResolver, this.itemRenderer, this.mapRenderer, this.blockRenderer, this.font, this.options, this.modelManager.entityModels(), var7);
      this.resourceManager.registerReloadListener(this.entityRenderDispatcher);
      this.blockEntityRenderDispatcher = new BlockEntityRenderDispatcher(this.font, this.modelManager.entityModels(), this.blockRenderer, this.itemModelResolver, this.itemRenderer, this.entityRenderDispatcher);
      this.resourceManager.registerReloadListener(this.blockEntityRenderDispatcher);
      this.particleEngine = new ParticleEngine(this.level, this.textureManager);
      this.resourceManager.registerReloadListener(this.particleEngine);
      this.paintingTextures = new PaintingTextureManager(this.textureManager);
      this.resourceManager.registerReloadListener(this.paintingTextures);
      this.mobEffectTextures = new MobEffectTextureManager(this.textureManager);
      this.resourceManager.registerReloadListener(this.mobEffectTextures);
      this.guiSprites = new GuiSpriteManager(this.textureManager);
      this.resourceManager.registerReloadListener(this.guiSprites);
      this.gameRenderer = new GameRenderer(this, this.entityRenderDispatcher.getItemInHandRenderer(), this.resourceManager, this.renderBuffers);
      this.levelRenderer = new LevelRenderer(this, this.entityRenderDispatcher, this.blockEntityRenderDispatcher, this.renderBuffers);
      this.resourceManager.registerReloadListener(this.levelRenderer);
      this.resourceManager.registerReloadListener(this.levelRenderer.getCloudRenderer());
      this.gpuWarnlistManager = new GpuWarnlistManager();
      this.resourceManager.registerReloadListener(this.gpuWarnlistManager);
      this.resourceManager.registerReloadListener(this.regionalCompliancies);
      this.gui = new Gui(this);
      this.debugRenderer = new DebugRenderer(this);
      RealmsClient var14 = RealmsClient.create(this);
      this.realmsDataFetcher = new RealmsDataFetcher(var14);
      RenderSystem.setErrorCallback(this::onFullscreenError);
      if (this.mainRenderTarget.width == this.window.getWidth() && this.mainRenderTarget.height == this.window.getHeight()) {
         if ((Boolean)this.options.fullscreen().get() && !this.window.isFullscreen()) {
            this.window.toggleFullScreen();
            this.options.fullscreen().set(this.window.isFullscreen());
         }
      } else {
         int var10002 = this.window.getWidth();
         StringBuilder var9 = new StringBuilder("Recovering from unsupported resolution (" + var10002 + "x" + this.window.getHeight() + ").\nPlease make sure you have up-to-date drivers (see aka.ms/mcdriver for instructions).");
         if (GlDebug.isDebugEnabled()) {
            var9.append("\n\nReported GL debug messages:\n").append(String.join("\n", GlDebug.getLastOpenGlDebugMessages()));
         }

         this.window.setWindowed(this.mainRenderTarget.width, this.mainRenderTarget.height);
         TinyFileDialogs.tinyfd_messageBox("Minecraft", var9.toString(), "ok", "error", false);
      }

      this.window.updateVsync((Boolean)this.options.enableVsync().get());
      this.window.updateRawMouseInput((Boolean)this.options.rawMouseInput().get());
      this.window.setDefaultErrorCallback();
      this.resizeDisplay();
      this.gameRenderer.preloadUiShader(this.vanillaPackResources.asProvider());
      this.telemetryManager = new ClientTelemetryManager(this, this.userApiService, this.user);
      this.profileKeyPairManager = ProfileKeyPairManager.create(this.userApiService, this.user, var3);
      this.narrator = new GameNarrator(this);
      this.narrator.checkStatus(this.options.narrator().get() != NarratorStatus.OFF);
      this.chatListener = new ChatListener(this);
      this.chatListener.setMessageDelay((Double)this.options.chatDelay().get());
      this.reportingContext = ReportingContext.create(ReportEnvironment.local(), this.userApiService);
      TitleScreen.registerTextures(this.textureManager);
      LoadingOverlay.registerTextures(this.textureManager);
      this.setScreen(new GenericMessageScreen(Component.translatable("gui.loadingMinecraft")));
      List var15 = this.resourcePackRepository.openAllSelected();
      this.reloadStateTracker.startReload(ResourceLoadStateTracker.ReloadReason.INITIAL, var15);
      ReloadInstance var10 = this.resourceManager.createReload(Util.backgroundExecutor().forName("resourceLoad"), this, RESOURCE_RELOAD_INITIAL_TASK, var15);
      GameLoadTimesEvent.INSTANCE.beginStep(TelemetryProperty.LOAD_TIME_LOADING_OVERLAY_MS);
      GameLoadCookie var11 = new GameLoadCookie(var14, var1.quickPlay);
      this.setOverlay(new LoadingOverlay(this, var10, (var2x) -> Util.ifElse(var2x, (var2) -> this.rollbackResourcePacks(var2, var11), () -> {
            if (SharedConstants.IS_RUNNING_IN_IDE) {
               this.selfTest();
            }

            this.reloadStateTracker.finishReload();
            this.onResourceLoadFinished(var11);
         }), false));
      this.quickPlayLog = QuickPlayLog.of(var1.quickPlay.path());
      this.framerateLimitTracker = new FramerateLimitTracker(this.options, this);
      if (TracyClient.isAvailable() && var1.game.captureTracyImages) {
         this.tracyFrameCapture = new TracyFrameCapture();
      } else {
         this.tracyFrameCapture = null;
      }

   }

   private void onResourceLoadFinished(@Nullable GameLoadCookie var1) {
      if (!this.gameLoadFinished) {
         this.gameLoadFinished = true;
         this.onGameLoadFinished(var1);
      }

   }

   private void onGameLoadFinished(@Nullable GameLoadCookie var1) {
      Runnable var2 = this.buildInitialScreens(var1);
      GameLoadTimesEvent.INSTANCE.endStep(TelemetryProperty.LOAD_TIME_LOADING_OVERLAY_MS);
      GameLoadTimesEvent.INSTANCE.endStep(TelemetryProperty.LOAD_TIME_TOTAL_TIME_MS);
      GameLoadTimesEvent.INSTANCE.send(this.telemetryManager.getOutsideSessionSender());
      var2.run();
   }

   public boolean isGameLoadFinished() {
      return this.gameLoadFinished;
   }

   private Runnable buildInitialScreens(@Nullable GameLoadCookie var1) {
      ArrayList var2 = new ArrayList();
      this.addInitialScreens(var2);
      Runnable var3 = () -> {
         if (var1 != null && var1.quickPlayData().isEnabled()) {
            QuickPlay.connect(this, var1.quickPlayData(), var1.realmsClient());
         } else {
            this.setScreen(new TitleScreen(true));
         }

      };

      for(Function var5 : Lists.reverse(var2)) {
         Screen var6 = (Screen)var5.apply(var3);
         var3 = () -> this.setScreen(var6);
      }

      return var3;
   }

   private void addInitialScreens(List<Function<Runnable, Screen>> var1) {
      if (this.options.onboardAccessibility) {
         var1.add((Function)(var1x) -> new AccessibilityOnboardingScreen(this.options, var1x));
      }

      BanDetails var2 = this.multiplayerBan();
      if (var2 != null) {
         var1.add((Function)(var1x) -> BanNoticeScreens.create((var1) -> {
               if (var1) {
                  Util.getPlatform().openUri(CommonLinks.SUSPENSION_HELP);
               }

               var1x.run();
            }, var2));
      }

      ProfileResult var3 = (ProfileResult)this.profileFuture.join();
      if (var3 != null) {
         GameProfile var4 = var3.profile();
         Set var5 = var3.actions();
         if (var5.contains(ProfileActionType.FORCED_NAME_CHANGE)) {
            var1.add((Function)(var1x) -> BanNoticeScreens.createNameBan(var4.getName(), var1x));
         }

         if (var5.contains(ProfileActionType.USING_BANNED_SKIN)) {
            var1.add(BanNoticeScreens::createSkinBan);
         }
      }

   }

   private static boolean countryEqualsISO3(Object var0) {
      try {
         return Locale.getDefault().getISO3Country().equals(var0);
      } catch (MissingResourceException var2) {
         return false;
      }
   }

   public void updateTitle() {
      this.window.setTitle(this.createTitle());
   }

   private String createTitle() {
      StringBuilder var1 = new StringBuilder("Minecraft");
      if (checkModStatus().shouldReportAsModified()) {
         var1.append("*");
      }

      var1.append(" ");
      var1.append(SharedConstants.getCurrentVersion().getName());
      ClientPacketListener var2 = this.getConnection();
      if (var2 != null && var2.getConnection().isConnected()) {
         var1.append(" - ");
         ServerData var3 = this.getCurrentServer();
         if (this.singleplayerServer != null && !this.singleplayerServer.isPublished()) {
            var1.append(I18n.get("title.singleplayer"));
         } else if (var3 != null && var3.isRealm()) {
            var1.append(I18n.get("title.multiplayer.realms"));
         } else if (this.singleplayerServer == null && (var3 == null || !var3.isLan())) {
            var1.append(I18n.get("title.multiplayer.other"));
         } else {
            var1.append(I18n.get("title.multiplayer.lan"));
         }
      }

      return var1.toString();
   }

   private UserApiService createUserApiService(YggdrasilAuthenticationService var1, GameConfig var2) {
      return var2.user.user.getType() != User.Type.MSA ? UserApiService.OFFLINE : var1.createUserApiService(var2.user.user.getAccessToken());
   }

   public static ModCheck checkModStatus() {
      return ModCheck.identify("vanilla", ClientBrandRetriever::getClientModName, "Client", Minecraft.class);
   }

   private void rollbackResourcePacks(Throwable var1, @Nullable GameLoadCookie var2) {
      if (this.resourcePackRepository.getSelectedIds().size() > 1) {
         this.clearResourcePacksOnError(var1, (Component)null, var2);
      } else {
         Util.throwAsRuntime(var1);
      }

   }

   public void clearResourcePacksOnError(Throwable var1, @Nullable Component var2, @Nullable GameLoadCookie var3) {
      LOGGER.info("Caught error loading resourcepacks, removing all selected resourcepacks", var1);
      this.reloadStateTracker.startRecovery(var1);
      this.downloadedPackSource.onRecovery();
      this.resourcePackRepository.setSelected(Collections.emptyList());
      this.options.resourcePacks.clear();
      this.options.incompatibleResourcePacks.clear();
      this.options.save();
      this.reloadResourcePacks(true, var3).thenRunAsync(() -> this.addResourcePackLoadFailToast(var2), this);
   }

   private void abortResourcePackRecovery() {
      this.setOverlay((Overlay)null);
      if (this.level != null) {
         this.level.disconnect();
         this.disconnect();
      }

      this.setScreen(new TitleScreen());
      this.addResourcePackLoadFailToast((Component)null);
   }

   private void addResourcePackLoadFailToast(@Nullable Component var1) {
      ToastManager var2 = this.getToastManager();
      SystemToast.addOrUpdate(var2, SystemToast.SystemToastId.PACK_LOAD_FAILURE, Component.translatable("resourcePack.load_fail"), var1);
   }

   public void triggerResourcePackRecovery(Exception var1) {
      if (!this.resourcePackRepository.isAbleToClearAnyPack()) {
         if (this.resourcePackRepository.getSelectedIds().size() <= 1) {
            LOGGER.error(LogUtils.FATAL_MARKER, var1.getMessage(), var1);
            this.emergencySaveAndCrash(new CrashReport(var1.getMessage(), var1));
         } else {
            this.schedule(this::abortResourcePackRecovery);
         }

      } else {
         this.clearResourcePacksOnError(var1, Component.translatable("resourcePack.runtime_failure"), (GameLoadCookie)null);
      }
   }

   public void run() {
      this.gameThread = Thread.currentThread();
      if (Runtime.getRuntime().availableProcessors() > 4) {
         this.gameThread.setPriority(10);
      }

      DiscontinuousFrame var1 = TracyClient.createDiscontinuousFrame("Client Tick");

      try {
         boolean var2 = false;

         while(this.running) {
            this.handleDelayedCrash();

            try {
               SingleTickProfiler var3 = SingleTickProfiler.createTickProfiler("Renderer");
               boolean var4 = this.getDebugOverlay().showProfilerChart();

               try (Profiler.Scope var5 = Profiler.use(this.constructProfiler(var4, var3))) {
                  this.metricsRecorder.startTick();
                  var1.start();
                  this.runTick(!var2);
                  var1.end();
                  this.metricsRecorder.endTick();
               }

               this.finishProfilers(var4, var3);
            } catch (OutOfMemoryError var10) {
               if (var2) {
                  throw var10;
               }

               this.emergencySave();
               this.setScreen(new OutOfMemoryScreen());
               System.gc();
               LOGGER.error(LogUtils.FATAL_MARKER, "Out of memory", var10);
               var2 = true;
            }
         }
      } catch (ReportedException var11) {
         LOGGER.error(LogUtils.FATAL_MARKER, "Reported exception thrown!", var11);
         this.emergencySaveAndCrash(var11.getReport());
      } catch (Throwable var12) {
         LOGGER.error(LogUtils.FATAL_MARKER, "Unreported exception thrown!", var12);
         this.emergencySaveAndCrash(new CrashReport("Unexpected error", var12));
      }

   }

   void updateFontOptions() {
      this.fontManager.updateOptions(this.options);
   }

   private void onFullscreenError(int var1, long var2) {
      this.options.enableVsync().set(false);
      this.options.save();
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

   public void delayCrash(CrashReport var1) {
      this.delayedCrash = () -> this.fillReport(var1);
   }

   public void delayCrashRaw(CrashReport var1) {
      this.delayedCrash = () -> var1;
   }

   private void handleDelayedCrash() {
      if (this.delayedCrash != null) {
         crash(this, this.gameDirectory, (CrashReport)this.delayedCrash.get());
      }

   }

   public void emergencySaveAndCrash(CrashReport var1) {
      MemoryReserve.release();
      CrashReport var2 = this.fillReport(var1);
      this.emergencySave();
      crash(this, this.gameDirectory, var2);
   }

   public static int saveReport(File var0, CrashReport var1) {
      Path var2 = var0.toPath().resolve("crash-reports");
      Path var3 = var2.resolve("crash-" + Util.getFilenameFormattedDateTime() + "-client.txt");
      Bootstrap.realStdoutPrintln(var1.getFriendlyReport(ReportType.CRASH));
      if (var1.getSaveFile() != null) {
         Bootstrap.realStdoutPrintln("#@!@# Game crashed! Crash report saved to: #@!@# " + String.valueOf(var1.getSaveFile().toAbsolutePath()));
         return -1;
      } else if (var1.saveToFile(var3, ReportType.CRASH)) {
         Bootstrap.realStdoutPrintln("#@!@# Game crashed! Crash report saved to: #@!@# " + String.valueOf(var3.toAbsolutePath()));
         return -1;
      } else {
         Bootstrap.realStdoutPrintln("#@?@# Game crashed! Crash report could not be saved. #@?@#");
         return -2;
      }
   }

   public static void crash(@Nullable Minecraft var0, File var1, CrashReport var2) {
      int var3 = saveReport(var1, var2);
      if (var0 != null) {
         var0.soundManager.emergencyShutdown();
      }

      System.exit(var3);
   }

   public boolean isEnforceUnicode() {
      return (Boolean)this.options.forceUnicodeFont().get();
   }

   public CompletableFuture<Void> reloadResourcePacks() {
      return this.reloadResourcePacks(false, (GameLoadCookie)null);
   }

   private CompletableFuture<Void> reloadResourcePacks(boolean var1, @Nullable GameLoadCookie var2) {
      if (this.pendingReload != null) {
         return this.pendingReload;
      } else {
         CompletableFuture var3 = new CompletableFuture();
         if (!var1 && this.overlay instanceof LoadingOverlay) {
            this.pendingReload = var3;
            return var3;
         } else {
            this.resourcePackRepository.reload();
            List var4 = this.resourcePackRepository.openAllSelected();
            if (!var1) {
               this.reloadStateTracker.startReload(ResourceLoadStateTracker.ReloadReason.MANUAL, var4);
            }

            this.setOverlay(new LoadingOverlay(this, this.resourceManager.createReload(Util.backgroundExecutor().forName("resourceLoad"), this, RESOURCE_RELOAD_INITIAL_TASK, var4), (var4x) -> Util.ifElse(var4x, (var3x) -> {
                  if (var1) {
                     this.downloadedPackSource.onRecoveryFailure();
                     this.abortResourcePackRecovery();
                  } else {
                     this.rollbackResourcePacks(var3x, var2);
                  }

               }, () -> {
                  this.levelRenderer.allChanged();
                  this.reloadStateTracker.finishReload();
                  this.downloadedPackSource.onReloadSuccess();
                  var3.complete((Object)null);
                  this.onResourceLoadFinished(var2);
               }), !var1));
            return var3;
         }
      }
   }

   private void selfTest() {
      boolean var1 = false;
      BlockModelShaper var2 = this.getBlockRenderer().getBlockModelShaper();
      BakedModel var3 = var2.getModelManager().getMissingModel();

      for(Block var5 : BuiltInRegistries.BLOCK) {
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

      for(Block var14 : BuiltInRegistries.BLOCK) {
         UnmodifiableIterator var15 = var14.getStateDefinition().getPossibleStates().iterator();

         while(var15.hasNext()) {
            BlockState var16 = (BlockState)var15.next();
            TextureAtlasSprite var9 = var2.getParticleIcon(var16);
            if (!var16.isAir() && var9 == var12) {
               LOGGER.debug("Missing particle icon for: {}", var16);
            }
         }
      }

      BuiltInRegistries.ITEM.listElements().forEach((var0) -> {
         Item var1 = (Item)var0.value();
         String var2 = var1.getDescriptionId();
         String var3 = Component.translatable(var2).getString();
         if (var3.toLowerCase(Locale.ROOT).equals(var1.getDescriptionId())) {
            LOGGER.debug("Missing translation for: {} {} {}", new Object[]{var0.key().location(), var2, var1});
         }

      });
      var1 |= MenuScreens.selfTest();
      var1 |= EntityRenderers.validateRegistrations();
      if (var1) {
         throw new IllegalStateException("Your game data is foobar, fix the errors above!");
      }
   }

   public LevelStorageSource getLevelSource() {
      return this.levelSource;
   }

   private void openChatScreen(String var1) {
      ChatStatus var2 = this.getChatStatus();
      if (!var2.isChatAllowed(this.isLocalServer())) {
         if (this.gui.isShowingChatDisabledByPlayer()) {
            this.gui.setChatDisabledByPlayerShown(false);
            this.setScreen(new ConfirmLinkScreen((var1x) -> {
               if (var1x) {
                  Util.getPlatform().openUri(CommonLinks.ACCOUNT_SETTINGS);
               }

               this.setScreen((Screen)null);
            }, Minecraft.ChatStatus.INFO_DISABLED_BY_PROFILE, CommonLinks.ACCOUNT_SETTINGS, true));
         } else {
            Component var3 = var2.getMessage();
            this.gui.setOverlayMessage(var3, false);
            this.narrator.sayNow(var3);
            this.gui.setChatDisabledByPlayerShown(var2 == Minecraft.ChatStatus.DISABLED_BY_PROFILE);
         }
      } else {
         this.setScreen(new ChatScreen(var1));
      }

   }

   public void setScreen(@Nullable Screen var1) {
      if (SharedConstants.IS_RUNNING_IN_IDE && Thread.currentThread() != this.gameThread) {
         LOGGER.error("setScreen called from non-game thread");
      }

      if (this.screen != null) {
         this.screen.removed();
      } else {
         this.setLastInputType(InputType.NONE);
      }

      if (var1 == null && this.clientLevelTeardownInProgress) {
         throw new IllegalStateException("Trying to return to in-game GUI during disconnection");
      } else {
         if (var1 == null && this.level == null) {
            var1 = new TitleScreen();
         } else if (var1 == null && this.player.isDeadOrDying()) {
            if (this.player.shouldShowDeathScreen()) {
               var1 = new DeathScreen((Component)null, this.level.getLevelData().isHardcore());
            } else {
               this.player.respawn();
            }
         }

         this.screen = (Screen)var1;
         if (this.screen != null) {
            this.screen.added();
         }

         BufferUploader.reset();
         if (var1 != null) {
            this.mouseHandler.releaseMouse();
            KeyMapping.releaseAll();
            ((Screen)var1).init(this, this.window.getGuiScaledWidth(), this.window.getGuiScaledHeight());
            this.noRender = false;
         } else {
            this.soundManager.resume();
            this.mouseHandler.grabMouse();
         }

         this.updateTitle();
      }
   }

   public void setOverlay(@Nullable Overlay var1) {
      this.overlay = var1;
   }

   public void destroy() {
      try {
         LOGGER.info("Stopping!");

         try {
            this.narrator.destroy();
         } catch (Throwable var7) {
         }

         try {
            if (this.level != null) {
               this.level.disconnect();
            }

            this.disconnect();
         } catch (Throwable var6) {
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
      if (this.currentFrameProfile != null) {
         this.currentFrameProfile.cancel();
      }

      try {
         this.telemetryManager.close();
         this.regionalCompliancies.close();
         this.modelManager.close();
         this.fontManager.close();
         this.gameRenderer.close();
         this.shaderManager.close();
         this.levelRenderer.close();
         this.soundManager.destroy();
         this.particleEngine.close();
         this.mobEffectTextures.close();
         this.paintingTextures.close();
         this.mapDecorationTextures.close();
         this.guiSprites.close();
         this.mapTextureManager.close();
         this.textureManager.close();
         this.resourceManager.close();
         if (this.tracyFrameCapture != null) {
            this.tracyFrameCapture.close();
         }

         FreeTypeUtil.destroy();
         Util.shutdownExecutors();
      } catch (Throwable var5) {
         LOGGER.error("Shutdown failure!", var5);
         throw var5;
      } finally {
         this.virtualScreen.close();
         this.window.close();
      }

   }

   private void runTick(boolean var1) {
      this.window.setErrorSection("Pre render");
      if (this.window.shouldClose()) {
         this.stop();
      }

      if (this.pendingReload != null && !(this.overlay instanceof LoadingOverlay)) {
         CompletableFuture var2 = this.pendingReload;
         this.pendingReload = null;
         this.reloadResourcePacks().thenRun(() -> var2.complete((Object)null));
      }

      Runnable var14;
      while((var14 = (Runnable)this.progressTasks.poll()) != null) {
         var14.run();
      }

      int var3 = this.deltaTracker.advanceTime(Util.getMillis(), var1);
      ProfilerFiller var4 = Profiler.get();
      if (var1) {
         var4.push("scheduledExecutables");
         this.runAllTasks();
         var4.pop();
         var4.push("tick");

         for(int var5 = 0; var5 < Math.min(10, var3); ++var5) {
            var4.incrementCounter("clientTick");
            this.tick();
         }

         var4.pop();
      }

      this.window.setErrorSection("Render");
      var4.push("sound");
      this.soundManager.updateSource(this.gameRenderer.getMainCamera());
      var4.popPush("toasts");
      this.toastManager.update();
      var4.popPush("render");
      long var15 = Util.getNanos();
      boolean var7;
      if (!this.getDebugOverlay().showDebugScreen() && !this.metricsRecorder.isRecording()) {
         var7 = false;
         this.gpuUtilization = 0.0;
      } else {
         var7 = this.currentFrameProfile == null || this.currentFrameProfile.isDone();
         if (var7) {
            TimerQuery.getInstance().ifPresent(TimerQuery::beginProfile);
         }
      }

      RenderSystem.clear(16640);
      this.mainRenderTarget.bindWrite(true);
      RenderSystem.setShaderFog(FogParameters.NO_FOG);
      var4.push("display");
      RenderSystem.enableCull();
      var4.popPush("mouse");
      this.mouseHandler.handleAccumulatedMovement();
      var4.pop();
      if (!this.noRender) {
         var4.popPush("gameRenderer");
         this.gameRenderer.render(this.deltaTracker, var1);
         var4.pop();
      }

      var4.push("blit");
      this.mainRenderTarget.unbindWrite();
      this.mainRenderTarget.blitToScreen(this.window.getWidth(), this.window.getHeight());
      this.frameTimeNs = Util.getNanos() - var15;
      if (var7) {
         TimerQuery.getInstance().ifPresent((var1x) -> this.currentFrameProfile = var1x.endProfile());
      }

      var4.popPush("updateDisplay");
      if (this.tracyFrameCapture != null) {
         this.tracyFrameCapture.upload();
         this.tracyFrameCapture.capture(this.mainRenderTarget);
      }

      this.window.updateDisplay(this.tracyFrameCapture);
      int var8 = this.framerateLimitTracker.getFramerateLimit();
      if (var8 < 260) {
         RenderSystem.limitDisplayFPS(var8);
      }

      var4.popPush("yield");
      Thread.yield();
      var4.pop();
      this.window.setErrorSection("Post render");
      ++this.frames;
      this.pause = this.hasSingleplayerServer() && (this.screen != null && this.screen.isPauseScreen() || this.overlay != null && this.overlay.isPauseScreen()) && !this.singleplayerServer.isPublished();
      this.deltaTracker.updatePauseState(this.pause);
      this.deltaTracker.updateFrozenState(!this.isLevelRunningNormally());
      long var9 = Util.getNanos();
      long var11 = var9 - this.lastNanoTime;
      if (var7) {
         this.savedCpuDuration = var11;
      }

      this.getDebugOverlay().logFrameDuration(var11);
      this.lastNanoTime = var9;
      var4.push("fpsUpdate");
      if (this.currentFrameProfile != null && this.currentFrameProfile.isDone()) {
         this.gpuUtilization = (double)this.currentFrameProfile.get() * 100.0 / (double)this.savedCpuDuration;
      }

      while(Util.getMillis() >= this.lastTime + 1000L) {
         String var13;
         if (this.gpuUtilization > 0.0) {
            String var10000 = this.gpuUtilization > 100.0 ? String.valueOf(ChatFormatting.RED) + "100%" : Math.round(this.gpuUtilization) + "%";
            var13 = " GPU: " + var10000;
         } else {
            var13 = "";
         }

         fps = this.frames;
         this.fpsString = String.format(Locale.ROOT, "%d fps T: %s%s%s%s B: %d%s", fps, var8 == 260 ? "inf" : var8, (Boolean)this.options.enableVsync().get() ? " vsync " : " ", this.options.graphicsMode().get(), this.options.cloudStatus().get() == CloudStatus.OFF ? "" : (this.options.cloudStatus().get() == CloudStatus.FAST ? " fast-clouds" : " fancy-clouds"), this.options.biomeBlendRadius().get(), var13);
         this.lastTime += 1000L;
         this.frames = 0;
      }

      var4.pop();
   }

   private ProfilerFiller constructProfiler(boolean var1, @Nullable SingleTickProfiler var2) {
      if (!var1) {
         this.fpsPieProfiler.disable();
         if (!this.metricsRecorder.isRecording() && var2 == null) {
            return InactiveProfiler.INSTANCE;
         }
      }

      Object var3;
      if (var1) {
         if (!this.fpsPieProfiler.isEnabled()) {
            this.fpsPieRenderTicks = 0;
            this.fpsPieProfiler.enable();
         }

         ++this.fpsPieRenderTicks;
         var3 = this.fpsPieProfiler.getFiller();
      } else {
         var3 = InactiveProfiler.INSTANCE;
      }

      if (this.metricsRecorder.isRecording()) {
         var3 = ProfilerFiller.combine((ProfilerFiller)var3, this.metricsRecorder.getProfiler());
      }

      return SingleTickProfiler.decorateFiller((ProfilerFiller)var3, var2);
   }

   private void finishProfilers(boolean var1, @Nullable SingleTickProfiler var2) {
      if (var2 != null) {
         var2.endTick();
      }

      ProfilerPieChart var3 = this.getDebugOverlay().getProfilerPieChart();
      if (var1) {
         var3.setPieChartResults(this.fpsPieProfiler.getResults());
      } else {
         var3.setPieChartResults((ProfileResults)null);
      }

   }

   public void resizeDisplay() {
      int var1 = this.window.calculateScale((Integer)this.options.guiScale().get(), this.isEnforceUnicode());
      this.window.setGuiScale((double)var1);
      if (this.screen != null) {
         this.screen.resize(this, this.window.getGuiScaledWidth(), this.window.getGuiScaledHeight());
      }

      RenderTarget var2 = this.getMainRenderTarget();
      var2.resize(this.window.getWidth(), this.window.getHeight());
      this.gameRenderer.resize(this.window.getWidth(), this.window.getHeight());
      this.mouseHandler.setIgnoreFirstMove();
   }

   public void cursorEntered() {
      this.mouseHandler.cursorEntered();
   }

   public int getFps() {
      return fps;
   }

   public long getFrameTimeNs() {
      return this.frameTimeNs;
   }

   private void emergencySave() {
      MemoryReserve.release();

      try {
         if (this.isLocalServer && this.singleplayerServer != null) {
            this.singleplayerServer.halt(true);
         }

         this.disconnect(new GenericMessageScreen(Component.translatable("menu.savingLevel")));
      } catch (Throwable var2) {
      }

      System.gc();
   }

   public boolean debugClientMetricsStart(Consumer<Component> var1) {
      if (this.metricsRecorder.isRecording()) {
         this.debugClientMetricsStop();
         return false;
      } else {
         Consumer var2 = (var2x) -> {
            if (var2x != EmptyProfileResults.EMPTY) {
               int var3 = var2x.getTickDuration();
               double var4 = (double)var2x.getNanoDuration() / (double)TimeUtil.NANOSECONDS_PER_SECOND;
               this.execute(() -> var1.accept(Component.translatable("commands.debug.stopped", String.format(Locale.ROOT, "%.2f", var4), var3, String.format(Locale.ROOT, "%.2f", (double)var3 / var4))));
            }
         };
         Consumer var3 = (var2x) -> {
            MutableComponent var3 = Component.literal(var2x.toString()).withStyle(ChatFormatting.UNDERLINE).withStyle((UnaryOperator)((var1x) -> var1x.withClickEvent(new ClickEvent(ClickEvent.Action.OPEN_FILE, var2x.toFile().getParent()))));
            this.execute(() -> var1.accept(Component.translatable("debug.profiling.stop", var3)));
         };
         SystemReport var4 = fillSystemReport(new SystemReport(), this, this.languageManager, this.launchedVersion, this.options);
         Consumer var5 = (var3x) -> {
            Path var4x = this.archiveProfilingReport(var4, var3x);
            var3.accept(var4x);
         };
         Consumer var6;
         if (this.singleplayerServer == null) {
            var6 = (var1x) -> var5.accept(ImmutableList.of(var1x));
         } else {
            this.singleplayerServer.fillSystemReport(var4);
            CompletableFuture var7 = new CompletableFuture();
            CompletableFuture var8 = new CompletableFuture();
            CompletableFuture.allOf(var7, var8).thenRunAsync(() -> var5.accept(ImmutableList.of((Path)var7.join(), (Path)var8.join())), Util.ioPool());
            IntegratedServer var10000 = this.singleplayerServer;
            Consumer var10001 = (var0) -> {
            };
            Objects.requireNonNull(var8);
            var10000.startRecordingMetrics(var10001, var8::complete);
            Objects.requireNonNull(var7);
            var6 = var7::complete;
         }

         this.metricsRecorder = ActiveMetricsRecorder.createStarted(new ClientMetricsSamplersProvider(Util.timeSource, this.levelRenderer), Util.timeSource, Util.ioPool(), new MetricsPersister("client"), (var2x) -> {
            this.metricsRecorder = InactiveMetricsRecorder.INSTANCE;
            var2.accept(var2x);
         }, var6);
         return true;
      }
   }

   private void debugClientMetricsStop() {
      this.metricsRecorder.end();
      if (this.singleplayerServer != null) {
         this.singleplayerServer.finishRecordingMetrics();
      }

   }

   private void debugClientMetricsCancel() {
      this.metricsRecorder.cancel();
      if (this.singleplayerServer != null) {
         this.singleplayerServer.cancelRecordingMetrics();
      }

   }

   private Path archiveProfilingReport(SystemReport var1, List<Path> var2) {
      String var4;
      if (this.isLocalServer()) {
         var4 = this.getSingleplayerServer().getWorldData().getLevelName();
      } else {
         ServerData var5 = this.getCurrentServer();
         var4 = var5 != null ? var5.name : "unknown";
      }

      Path var3;
      try {
         String var23 = String.format(Locale.ROOT, "%s-%s-%s", Util.getFilenameFormattedDateTime(), var4, SharedConstants.getCurrentVersion().getId());
         String var6 = FileUtil.findAvailableName(MetricsPersister.PROFILING_RESULTS_DIR, var23, ".zip");
         var3 = MetricsPersister.PROFILING_RESULTS_DIR.resolve(var6);
      } catch (IOException var21) {
         throw new UncheckedIOException(var21);
      }

      try {
         FileZipper var24 = new FileZipper(var3);

         try {
            var24.add(Paths.get("system.txt"), var1.toLineSeparatedString());
            var24.add(Paths.get("client").resolve(this.options.getFile().getName()), this.options.dumpOptionsForReport());
            Objects.requireNonNull(var24);
            var2.forEach(var24::add);
         } catch (Throwable var20) {
            try {
               var24.close();
            } catch (Throwable var19) {
               var20.addSuppressed(var19);
            }

            throw var20;
         }

         var24.close();
      } finally {
         for(Path var10 : var2) {
            try {
               FileUtils.forceDelete(var10.toFile());
            } catch (IOException var18) {
               LOGGER.warn("Failed to delete temporary profiling result {}", var10, var18);
            }
         }

      }

      return var3;
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

   private boolean startAttack() {
      if (this.missTime > 0) {
         return false;
      } else if (this.hitResult == null) {
         LOGGER.error("Null returned as 'hitResult', this shouldn't happen!");
         if (this.gameMode.hasMissTime()) {
            this.missTime = 10;
         }

         return false;
      } else if (this.player.isHandsBusy()) {
         return false;
      } else {
         ItemStack var1 = this.player.getItemInHand(InteractionHand.MAIN_HAND);
         if (!var1.isItemEnabled(this.level.enabledFeatures())) {
            return false;
         } else {
            boolean var2 = false;
            switch (this.hitResult.getType()) {
               case ENTITY:
                  this.gameMode.attack(this.player, ((EntityHitResult)this.hitResult).getEntity());
                  break;
               case BLOCK:
                  BlockHitResult var3 = (BlockHitResult)this.hitResult;
                  BlockPos var4 = var3.getBlockPos();
                  if (!this.level.getBlockState(var4).isAir()) {
                     this.gameMode.startDestroyBlock(var4, var3.getDirection());
                     if (this.level.getBlockState(var4).isAir()) {
                        var2 = true;
                     }
                     break;
                  }
               case MISS:
                  if (this.gameMode.hasMissTime()) {
                     this.missTime = 10;
                  }

                  this.player.resetAttackStrengthTicker();
            }

            this.player.swing(InteractionHand.MAIN_HAND);
            return var2;
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

            for(InteractionHand var4 : InteractionHand.values()) {
               ItemStack var5 = this.player.getItemInHand(var4);
               if (!var5.isItemEnabled(this.level.enabledFeatures())) {
                  return;
               }

               if (this.hitResult != null) {
                  switch (this.hitResult.getType()) {
                     case ENTITY:
                        EntityHitResult var6 = (EntityHitResult)this.hitResult;
                        Entity var7 = var6.getEntity();
                        if (!this.level.getWorldBorder().isWithinBounds(var7.blockPosition())) {
                           return;
                        }

                        InteractionResult var8 = this.gameMode.interactAt(this.player, var7, var6, var4);
                        if (!var8.consumesAction()) {
                           var8 = this.gameMode.interact(this.player, var7, var4);
                        }

                        if (var8 instanceof InteractionResult.Success) {
                           InteractionResult.Success var15 = (InteractionResult.Success)var8;
                           if (var15.swingSource() == InteractionResult.SwingSource.CLIENT) {
                              this.player.swing(var4);
                           }

                           return;
                        }
                        break;
                     case BLOCK:
                        BlockHitResult var9 = (BlockHitResult)this.hitResult;
                        int var10 = var5.getCount();
                        InteractionResult var11 = this.gameMode.useItemOn(this.player, var4, var9);
                        if (var11 instanceof InteractionResult.Success) {
                           InteractionResult.Success var12 = (InteractionResult.Success)var11;
                           if (var12.swingSource() == InteractionResult.SwingSource.CLIENT) {
                              this.player.swing(var4);
                              if (!var5.isEmpty() && (var5.getCount() != var10 || this.gameMode.hasInfiniteItems())) {
                                 this.gameRenderer.itemInHandRenderer.itemUsed(var4);
                              }
                           }

                           return;
                        }

                        if (var11 instanceof InteractionResult.Fail) {
                           return;
                        }
                  }
               }

               if (!var5.isEmpty()) {
                  InteractionResult var13 = this.gameMode.useItem(this.player, var4);
                  if (var13 instanceof InteractionResult.Success) {
                     InteractionResult.Success var14 = (InteractionResult.Success)var13;
                     if (var14.swingSource() == InteractionResult.SwingSource.CLIENT) {
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
      ++this.clientTickCount;
      if (this.level != null && !this.pause) {
         this.level.tickRateManager().tick();
      }

      if (this.rightClickDelay > 0) {
         --this.rightClickDelay;
      }

      ProfilerFiller var1 = Profiler.get();
      var1.push("gui");
      this.chatListener.tick();
      this.gui.tick(this.pause);
      var1.pop();
      this.gameRenderer.pick(1.0F);
      this.tutorial.onLookAt(this.level, this.hitResult);
      var1.push("gameMode");
      if (!this.pause && this.level != null) {
         this.gameMode.tick();
      }

      var1.popPush("textures");
      if (this.isLevelRunningNormally()) {
         this.textureManager.tick();
      }

      if (this.screen == null && this.player != null) {
         if (this.player.isDeadOrDying() && !(this.screen instanceof DeathScreen)) {
            this.setScreen((Screen)null);
         } else if (this.player.isSleeping() && this.level != null) {
            this.setScreen(new InBedChatScreen());
         }
      } else {
         Screen var3 = this.screen;
         if (var3 instanceof InBedChatScreen) {
            InBedChatScreen var2 = (InBedChatScreen)var3;
            if (!this.player.isSleeping()) {
               var2.onPlayerWokeUp();
            }
         }
      }

      if (this.screen != null) {
         this.missTime = 10000;
      }

      if (this.screen != null) {
         try {
            this.screen.tick();
         } catch (Throwable var5) {
            CrashReport var9 = CrashReport.forThrowable(var5, "Ticking screen");
            this.screen.fillCrashDetails(var9);
            throw new ReportedException(var9);
         }
      }

      if (!this.getDebugOverlay().showDebugScreen()) {
         this.gui.clearCache();
      }

      if (this.overlay == null && this.screen == null) {
         var1.popPush("Keybindings");
         this.handleKeybinds();
         if (this.missTime > 0) {
            --this.missTime;
         }
      }

      if (this.level != null) {
         var1.popPush("gameRenderer");
         if (!this.pause) {
            this.gameRenderer.tick();
         }

         var1.popPush("levelRenderer");
         if (!this.pause) {
            this.levelRenderer.tick();
         }

         var1.popPush("level");
         if (!this.pause) {
            this.level.tickEntities();
         }
      } else if (this.gameRenderer.currentPostEffect() != null) {
         this.gameRenderer.clearPostEffect();
      }

      if (!this.pause) {
         this.musicManager.tick();
      }

      this.soundManager.tick(this.pause);
      if (this.level != null) {
         if (!this.pause) {
            if (!this.options.joinedFirstServer && this.isMultiplayerServer()) {
               MutableComponent var7 = Component.translatable("tutorial.socialInteractions.title");
               MutableComponent var10 = Component.translatable("tutorial.socialInteractions.description", Tutorial.key("socialInteractions"));
               this.socialInteractionsToast = new TutorialToast(this.font, TutorialToast.Icons.SOCIAL_INTERACTIONS, var7, var10, true, 8000);
               this.toastManager.addToast(this.socialInteractionsToast);
               this.options.joinedFirstServer = true;
               this.options.save();
            }

            this.tutorial.tick();

            try {
               this.level.tick(() -> true);
            } catch (Throwable var6) {
               CrashReport var11 = CrashReport.forThrowable(var6, "Exception in world tick");
               if (this.level == null) {
                  CrashReportCategory var4 = var11.addCategory("Affected level");
                  var4.setDetail("Problem", "Level is null!");
               } else {
                  this.level.fillReportDetails(var11);
               }

               throw new ReportedException(var11);
            }
         }

         var1.popPush("animateTick");
         if (!this.pause && this.isLevelRunningNormally()) {
            this.level.animateTick(this.player.getBlockX(), this.player.getBlockY(), this.player.getBlockZ());
         }

         var1.popPush("particles");
         if (!this.pause && this.isLevelRunningNormally()) {
            this.particleEngine.tick();
         }

         ClientPacketListener var8 = this.getConnection();
         if (var8 != null && !this.pause) {
            var8.send(ServerboundClientTickEndPacket.INSTANCE);
         }
      } else if (this.pendingConnection != null) {
         var1.popPush("pendingConnection");
         this.pendingConnection.tick();
      }

      var1.popPush("keyboard");
      this.keyboardHandler.tick();
      var1.pop();
   }

   private boolean isLevelRunningNormally() {
      return this.level == null || this.level.tickRateManager().runsNormally();
   }

   private boolean isMultiplayerServer() {
      return !this.isLocalServer || this.singleplayerServer != null && this.singleplayerServer.isPublished();
   }

   private void handleKeybinds() {
      for(; this.options.keyTogglePerspective.consumeClick(); this.levelRenderer.needsUpdate()) {
         CameraType var1 = this.options.getCameraType();
         this.options.setCameraType(this.options.getCameraType().cycle());
         if (var1.isFirstPerson() != this.options.getCameraType().isFirstPerson()) {
            this.gameRenderer.checkEntityPostEffect(this.options.getCameraType().isFirstPerson() ? this.getCameraEntity() : null);
         }
      }

      while(this.options.keySmoothCamera.consumeClick()) {
         this.options.smoothCamera = !this.options.smoothCamera;
      }

      for(int var4 = 0; var4 < 9; ++var4) {
         boolean var2 = this.options.keySaveHotbarActivator.isDown();
         boolean var3 = this.options.keyLoadHotbarActivator.isDown();
         if (this.options.keyHotbarSlots[var4].consumeClick()) {
            if (this.player.isSpectator()) {
               this.gui.getSpectatorGui().onHotbarSelected(var4);
            } else if (!this.player.isCreative() || this.screen != null || !var3 && !var2) {
               this.player.getInventory().selected = var4;
            } else {
               CreativeModeInventoryScreen.handleHotbarLoadOrSave(this, var4, var3, var2);
            }
         }
      }

      while(this.options.keySocialInteractions.consumeClick()) {
         if (!this.isMultiplayerServer()) {
            this.player.displayClientMessage(SOCIAL_INTERACTIONS_NOT_AVAILABLE, true);
            this.narrator.sayNow(SOCIAL_INTERACTIONS_NOT_AVAILABLE);
         } else {
            if (this.socialInteractionsToast != null) {
               this.socialInteractionsToast.hide();
               this.socialInteractionsToast = null;
            }

            this.setScreen(new SocialInteractionsScreen());
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

      while(this.options.keySwapOffhand.consumeClick()) {
         if (!this.player.isSpectator()) {
            this.getConnection().send(new ServerboundPlayerActionPacket(ServerboundPlayerActionPacket.Action.SWAP_ITEM_WITH_OFFHAND, BlockPos.ZERO, Direction.DOWN));
         }
      }

      while(this.options.keyDrop.consumeClick()) {
         if (!this.player.isSpectator() && this.player.drop(Screen.hasControlDown())) {
            this.player.swing(InteractionHand.MAIN_HAND);
         }
      }

      while(this.options.keyChat.consumeClick()) {
         this.openChatScreen("");
      }

      if (this.screen == null && this.overlay == null && this.options.keyCommand.consumeClick()) {
         this.openChatScreen("/");
      }

      boolean var5 = false;
      if (this.player.isUsingItem()) {
         if (!this.options.keyUse.isDown()) {
            this.gameMode.releaseUsingItem(this.player);
         }

         while(this.options.keyAttack.consumeClick()) {
         }

         while(this.options.keyUse.consumeClick()) {
         }

         while(this.options.keyPickItem.consumeClick()) {
         }
      } else {
         while(this.options.keyAttack.consumeClick()) {
            var5 |= this.startAttack();
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

      this.continueAttack(this.screen == null && !var5 && this.options.keyAttack.isDown() && this.mouseHandler.isMouseGrabbed());
   }

   public ClientTelemetryManager getTelemetryManager() {
      return this.telemetryManager;
   }

   public double getGpuUtilization() {
      return this.gpuUtilization;
   }

   public ProfileKeyPairManager getProfileKeyPairManager() {
      return this.profileKeyPairManager;
   }

   public WorldOpenFlows createWorldOpenFlows() {
      return new WorldOpenFlows(this, this.levelSource);
   }

   public void doWorldLoad(LevelStorageSource.LevelStorageAccess var1, PackRepository var2, WorldStem var3, boolean var4) {
      this.disconnect();
      this.progressListener.set((Object)null);
      Instant var5 = Instant.now();

      try {
         var1.saveDataTag(var3.registries().compositeAccess(), var3.worldData());
         Services var6 = Services.create(this.authenticationService, this.gameDirectory);
         var6.profileCache().setExecutor(this);
         SkullBlockEntity.setup(var6, this);
         GameProfileCache.setUsesAuthentication(false);
         this.singleplayerServer = (IntegratedServer)MinecraftServer.spin((var5x) -> new IntegratedServer(var5x, this, var1, var2, var3, var6, (var1x) -> {
               StoringChunkProgressListener var2 = StoringChunkProgressListener.createFromGameruleRadius(var1x + 0);
               this.progressListener.set(var2);
               Queue var10001 = this.progressTasks;
               Objects.requireNonNull(var10001);
               return ProcessorChunkProgressListener.createStarted(var2, var10001::add);
            }));
         this.isLocalServer = true;
         this.updateReportEnvironment(ReportEnvironment.local());
         this.quickPlayLog.setWorldData(QuickPlayLog.Type.SINGLEPLAYER, var1.getLevelId(), var3.worldData().getLevelName());
      } catch (Throwable var12) {
         CrashReport var7 = CrashReport.forThrowable(var12, "Starting integrated server");
         CrashReportCategory var8 = var7.addCategory("Starting integrated server");
         var8.setDetail("Level ID", var1.getLevelId());
         var8.setDetail("Level Name", (CrashReportDetail)(() -> var3.worldData().getLevelName()));
         throw new ReportedException(var7);
      }

      while(this.progressListener.get() == null) {
         Thread.yield();
      }

      LevelLoadingScreen var13 = new LevelLoadingScreen((StoringChunkProgressListener)this.progressListener.get());
      ProfilerFiller var14 = Profiler.get();
      this.setScreen(var13);
      var14.push("waitForServer");

      for(; !this.singleplayerServer.isReady() || this.overlay != null; this.handleDelayedCrash()) {
         var13.tick();
         this.runTick(false);

         try {
            Thread.sleep(16L);
         } catch (InterruptedException var11) {
         }
      }

      var14.pop();
      Duration var15 = Duration.between(var5, Instant.now());
      SocketAddress var9 = this.singleplayerServer.getConnection().startMemoryChannel();
      Connection var10 = Connection.connectToLocalServer(var9);
      var10.initiateServerboundPlayConnection(var9.toString(), 0, new ClientHandshakePacketListenerImpl(var10, this, (ServerData)null, (Screen)null, var4, var15, (var0) -> {
      }, (TransferState)null));
      var10.send(new ServerboundHelloPacket(this.getUser().getName(), this.getUser().getProfileId()));
      this.pendingConnection = var10;
   }

   public void setLevel(ClientLevel var1, ReceivingLevelScreen.Reason var2) {
      this.updateScreenAndTick(new ReceivingLevelScreen(() -> false, var2));
      this.level = var1;
      this.updateLevelInEngines(var1);
      if (!this.isLocalServer) {
         Services var3 = Services.create(this.authenticationService, this.gameDirectory);
         var3.profileCache().setExecutor(this);
         SkullBlockEntity.setup(var3, this);
         GameProfileCache.setUsesAuthentication(false);
      }

   }

   public void disconnect() {
      this.disconnect(new ProgressScreen(true), false);
   }

   public void disconnect(Screen var1) {
      this.disconnect(var1, false);
   }

   public void disconnect(Screen var1, boolean var2) {
      ClientPacketListener var3 = this.getConnection();
      if (var3 != null) {
         this.dropAllTasks();
         var3.close();
         if (!var2) {
            this.clearDownloadedResourcePacks();
         }
      }

      this.playerSocialManager.stopOnlineMode();
      if (this.metricsRecorder.isRecording()) {
         this.debugClientMetricsCancel();
      }

      IntegratedServer var4 = this.singleplayerServer;
      this.singleplayerServer = null;
      this.gameRenderer.resetData();
      this.gameMode = null;
      this.narrator.clear();
      this.clientLevelTeardownInProgress = true;

      try {
         this.updateScreenAndTick(var1);
         if (this.level != null) {
            if (var4 != null) {
               ProfilerFiller var5 = Profiler.get();
               var5.push("waitForServer");

               while(!var4.isShutdown()) {
                  this.runTick(false);
               }

               var5.pop();
            }

            this.gui.onDisconnected();
            this.isLocalServer = false;
         }

         this.level = null;
         this.updateLevelInEngines((ClientLevel)null);
         this.player = null;
      } finally {
         this.clientLevelTeardownInProgress = false;
      }

      SkullBlockEntity.clear();
   }

   public void clearDownloadedResourcePacks() {
      this.downloadedPackSource.cleanupAfterDisconnect();
      this.runAllTasks();
   }

   public void clearClientLevel(Screen var1) {
      ClientPacketListener var2 = this.getConnection();
      if (var2 != null) {
         var2.clearLevel();
      }

      if (this.metricsRecorder.isRecording()) {
         this.debugClientMetricsCancel();
      }

      this.gameRenderer.resetData();
      this.gameMode = null;
      this.narrator.clear();
      this.clientLevelTeardownInProgress = true;

      try {
         this.updateScreenAndTick(var1);
         this.gui.onDisconnected();
         this.level = null;
         this.updateLevelInEngines((ClientLevel)null);
         this.player = null;
      } finally {
         this.clientLevelTeardownInProgress = false;
      }

      SkullBlockEntity.clear();
   }

   private void updateScreenAndTick(Screen var1) {
      ProfilerFiller var2 = Profiler.get();
      var2.push("forcedTick");
      this.soundManager.stop();
      this.cameraEntity = null;
      this.pendingConnection = null;
      this.setScreen(var1);
      this.runTick(false);
      var2.pop();
   }

   public void forceSetScreen(Screen var1) {
      try (Zone var2 = Profiler.get().zone("forcedTick")) {
         this.setScreen(var1);
         this.runTick(false);
      }

   }

   private void updateLevelInEngines(@Nullable ClientLevel var1) {
      this.levelRenderer.setLevel(var1);
      this.particleEngine.setLevel(var1);
      this.blockEntityRenderDispatcher.setLevel(var1);
      this.updateTitle();
   }

   private UserApiService.UserProperties userProperties() {
      return (UserApiService.UserProperties)this.userPropertiesFuture.join();
   }

   public boolean telemetryOptInExtra() {
      return this.extraTelemetryAvailable() && (Boolean)this.options.telemetryOptInExtra().get();
   }

   public boolean extraTelemetryAvailable() {
      return this.allowsTelemetry() && this.userProperties().flag(UserFlag.OPTIONAL_TELEMETRY_AVAILABLE);
   }

   public boolean allowsTelemetry() {
      return SharedConstants.IS_RUNNING_IN_IDE ? false : this.userProperties().flag(UserFlag.TELEMETRY_ENABLED);
   }

   public boolean allowsMultiplayer() {
      return this.allowsMultiplayer && this.userProperties().flag(UserFlag.SERVERS_ALLOWED) && this.multiplayerBan() == null && !this.isNameBanned();
   }

   public boolean allowsRealms() {
      return this.userProperties().flag(UserFlag.REALMS_ALLOWED) && this.multiplayerBan() == null;
   }

   @Nullable
   public BanDetails multiplayerBan() {
      return (BanDetails)this.userProperties().bannedScopes().get("MULTIPLAYER");
   }

   public boolean isNameBanned() {
      ProfileResult var1 = (ProfileResult)this.profileFuture.getNow((Object)null);
      return var1 != null && var1.actions().contains(ProfileActionType.FORCED_NAME_CHANGE);
   }

   public boolean isBlocked(UUID var1) {
      if (this.getChatStatus().isChatAllowed(false)) {
         return this.playerSocialManager.shouldHideMessageFrom(var1);
      } else {
         return (this.player == null || !var1.equals(this.player.getUUID())) && !var1.equals(Util.NIL_UUID);
      }
   }

   public ChatStatus getChatStatus() {
      if (this.options.chatVisibility().get() == ChatVisiblity.HIDDEN) {
         return Minecraft.ChatStatus.DISABLED_BY_OPTIONS;
      } else if (!this.allowsChat) {
         return Minecraft.ChatStatus.DISABLED_BY_LAUNCHER;
      } else {
         return !this.userProperties().flag(UserFlag.CHAT_ALLOWED) ? Minecraft.ChatStatus.DISABLED_BY_PROFILE : Minecraft.ChatStatus.ENABLED;
      }
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
      return ((GraphicsStatus)instance.options.graphicsMode().get()).getId() >= GraphicsStatus.FANCY.getId();
   }

   public static boolean useShaderTransparency() {
      return !instance.gameRenderer.isPanoramicMode() && ((GraphicsStatus)instance.options.graphicsMode().get()).getId() >= GraphicsStatus.FABULOUS.getId();
   }

   public static boolean useAmbientOcclusion() {
      return (Boolean)instance.options.ambientOcclusion().get();
   }

   private void pickBlock() {
      if (this.hitResult != null && this.hitResult.getType() != HitResult.Type.MISS) {
         boolean var1 = Screen.hasControlDown();
         HitResult var10000 = this.hitResult;
         Objects.requireNonNull(var10000);
         HitResult var2 = var10000;
         byte var3 = 0;
         //$FF: var3->value
         //0->net/minecraft/world/phys/BlockHitResult
         //1->net/minecraft/world/phys/EntityHitResult
         switch (var2.typeSwitch<invokedynamic>(var2, var3)) {
            case 0:
               BlockHitResult var4 = (BlockHitResult)var2;
               this.gameMode.handlePickItemFromBlock(var4.getBlockPos(), var1);
               break;
            case 1:
               EntityHitResult var5 = (EntityHitResult)var2;
               this.gameMode.handlePickItemFromEntity(var5.getEntity(), var1);
         }

      }
   }

   public CrashReport fillReport(CrashReport var1) {
      SystemReport var2 = var1.getSystemReport();

      try {
         fillSystemReport(var2, this, this.languageManager, this.launchedVersion, this.options);
         this.fillUptime(var1.addCategory("Uptime"));
         if (this.level != null) {
            this.level.fillReportDetails(var1);
         }

         if (this.singleplayerServer != null) {
            this.singleplayerServer.fillSystemReport(var2);
         }

         this.reloadStateTracker.fillCrashReport(var1);
      } catch (Throwable var4) {
         LOGGER.error("Failed to collect details", var4);
      }

      return var1;
   }

   public static void fillReport(@Nullable Minecraft var0, @Nullable LanguageManager var1, String var2, @Nullable Options var3, CrashReport var4) {
      SystemReport var5 = var4.getSystemReport();
      fillSystemReport(var5, var0, var1, var2, var3);
   }

   private static String formatSeconds(double var0) {
      return String.format(Locale.ROOT, "%.3fs", var0);
   }

   private void fillUptime(CrashReportCategory var1) {
      var1.setDetail("JVM uptime", (CrashReportDetail)(() -> formatSeconds((double)ManagementFactory.getRuntimeMXBean().getUptime() / 1000.0)));
      var1.setDetail("Wall uptime", (CrashReportDetail)(() -> formatSeconds((double)(System.currentTimeMillis() - this.clientStartTimeMs) / 1000.0)));
      var1.setDetail("High-res time", (CrashReportDetail)(() -> formatSeconds((double)Util.getMillis() / 1000.0)));
      var1.setDetail("Client ticks", (CrashReportDetail)(() -> String.format(Locale.ROOT, "%d ticks / %.3fs", this.clientTickCount, (double)this.clientTickCount / 20.0)));
   }

   private static SystemReport fillSystemReport(SystemReport var0, @Nullable Minecraft var1, @Nullable LanguageManager var2, String var3, @Nullable Options var4) {
      var0.setDetail("Launched Version", (Supplier)(() -> var3));
      String var5 = getLauncherBrand();
      if (var5 != null) {
         var0.setDetail("Launcher name", var5);
      }

      var0.setDetail("Backend library", RenderSystem::getBackendDescription);
      var0.setDetail("Backend API", RenderSystem::getApiDescription);
      var0.setDetail("Window size", (Supplier)(() -> var1 != null ? var1.window.getWidth() + "x" + var1.window.getHeight() : "<not initialized>"));
      var0.setDetail("GFLW Platform", Window::getPlatform);
      var0.setDetail("GL Caps", RenderSystem::getCapsString);
      var0.setDetail("GL debug messages", (Supplier)(() -> GlDebug.isDebugEnabled() ? String.join("\n", GlDebug.getLastOpenGlDebugMessages()) : "<disabled>"));
      var0.setDetail("Is Modded", (Supplier)(() -> checkModStatus().fullDescription()));
      var0.setDetail("Universe", (Supplier)(() -> var1 != null ? Long.toHexString(var1.canary) : "404"));
      var0.setDetail("Type", "Client (map_client.txt)");
      if (var4 != null) {
         if (var1 != null) {
            String var6 = var1.getGpuWarnlistManager().getAllWarnings();
            if (var6 != null) {
               var0.setDetail("GPU Warnings", var6);
            }
         }

         var0.setDetail("Graphics mode", ((GraphicsStatus)var4.graphicsMode().get()).toString());
         int var10002 = var4.getEffectiveRenderDistance();
         var0.setDetail("Render Distance", var10002 + "/" + String.valueOf(var4.renderDistance().get()) + " chunks");
      }

      if (var1 != null) {
         var0.setDetail("Resource Packs", (Supplier)(() -> PackRepository.displayPackList(var1.getResourcePackRepository().getSelectedPacks())));
      }

      if (var2 != null) {
         var0.setDetail("Current Language", (Supplier)(() -> var2.getSelected()));
      }

      var0.setDetail("Locale", String.valueOf(Locale.getDefault()));
      var0.setDetail("System encoding", (Supplier)(() -> System.getProperty("sun.jnu.encoding", "<not set>")));
      var0.setDetail("File encoding", (Supplier)(() -> System.getProperty("file.encoding", "<not set>")));
      var0.setDetail("CPU", GlUtil::getCpuInfo);
      return var0;
   }

   public static Minecraft getInstance() {
      return instance;
   }

   public CompletableFuture<Void> delayTextureReload() {
      return this.submit(this::reloadResourcePacks).thenCompose((var0) -> var0);
   }

   public void updateReportEnvironment(ReportEnvironment var1) {
      if (!this.reportingContext.matches(var1)) {
         this.reportingContext = ReportingContext.create(var1, this.userApiService);
      }

   }

   @Nullable
   public ServerData getCurrentServer() {
      return (ServerData)Optionull.map(this.getConnection(), ClientPacketListener::getServerData);
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

   public boolean isSingleplayer() {
      IntegratedServer var1 = this.getSingleplayerServer();
      return var1 != null && !var1.isPublished();
   }

   public boolean isLocalPlayer(UUID var1) {
      return var1.equals(this.getUser().getProfileId());
   }

   public User getUser() {
      return this.user;
   }

   public GameProfile getGameProfile() {
      ProfileResult var1 = (ProfileResult)this.profileFuture.join();
      return var1 != null ? var1.profile() : new GameProfile(this.user.getProfileId(), this.user.getName());
   }

   public Proxy getProxy() {
      return this.proxy;
   }

   public TextureManager getTextureManager() {
      return this.textureManager;
   }

   public ShaderManager getShaderManager() {
      return this.shaderManager;
   }

   public ResourceManager getResourceManager() {
      return this.resourceManager;
   }

   public PackRepository getResourcePackRepository() {
      return this.resourcePackRepository;
   }

   public VanillaPackResources getVanillaPackResources() {
      return this.vanillaPackResources;
   }

   public DownloadedPackSource getDownloadedPackSource() {
      return this.downloadedPackSource;
   }

   public Path getResourcePackDirectory() {
      return this.resourcePackDirectory;
   }

   public LanguageManager getLanguageManager() {
      return this.languageManager;
   }

   public Function<ResourceLocation, TextureAtlasSprite> getTextureAtlas(ResourceLocation var1) {
      TextureAtlas var10000 = this.modelManager.getAtlas(var1);
      Objects.requireNonNull(var10000);
      return var10000::getSprite;
   }

   public boolean isPaused() {
      return this.pause;
   }

   public GpuWarnlistManager getGpuWarnlistManager() {
      return this.gpuWarnlistManager;
   }

   public SoundManager getSoundManager() {
      return this.soundManager;
   }

   public MusicInfo getSituationalMusic() {
      Music var1 = (Music)Optionull.map(this.screen, Screen::getBackgroundMusic);
      if (var1 != null) {
         return new MusicInfo(var1);
      } else if (this.player != null) {
         Level var2 = this.player.level();
         if (var2.dimension() == Level.END) {
            return this.gui.getBossOverlay().shouldPlayMusic() ? new MusicInfo(Musics.END_BOSS) : new MusicInfo(Musics.END);
         } else {
            Holder var3 = var2.getBiome(this.player.blockPosition());
            float var4 = ((Biome)var3.value()).getBackgroundMusicVolume();
            if (!this.musicManager.isPlayingMusic(Musics.UNDER_WATER) && (!this.player.isUnderWater() || !var3.is(BiomeTags.PLAYS_UNDERWATER_MUSIC))) {
               if (var2.dimension() != Level.NETHER && this.player.getAbilities().instabuild && this.player.getAbilities().mayfly) {
                  return new MusicInfo(Musics.CREATIVE, var4);
               } else {
                  Optional var5 = ((Biome)var3.value()).getBackgroundMusic();
                  if (var5.isPresent()) {
                     Optional var6 = ((SimpleWeightedRandomList)var5.get()).getRandomValue(var2.random);
                     return new MusicInfo((Music)var6.orElse((Object)null), var4);
                  } else {
                     return new MusicInfo(Musics.GAME, var4);
                  }
               }
            } else {
               return new MusicInfo(Musics.UNDER_WATER, var4);
            }
         }
      } else {
         return new MusicInfo(Musics.MENU);
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

   public boolean shouldEntityAppearGlowing(Entity var1) {
      return var1.isCurrentlyGlowing() || this.player != null && this.player.isSpectator() && this.options.keySpectatorOutlines.isDown() && var1.getType() == EntityType.PLAYER;
   }

   protected Thread getRunningThread() {
      return this.gameThread;
   }

   public Runnable wrapRunnable(Runnable var1) {
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

   public BlockEntityRenderDispatcher getBlockEntityRenderDispatcher() {
      return this.blockEntityRenderDispatcher;
   }

   public ItemRenderer getItemRenderer() {
      return this.itemRenderer;
   }

   public MapRenderer getMapRenderer() {
      return this.mapRenderer;
   }

   public DataFixer getFixerUpper() {
      return this.fixerUpper;
   }

   public DeltaTracker getDeltaTracker() {
      return this.deltaTracker;
   }

   public BlockColors getBlockColors() {
      return this.blockColors;
   }

   public boolean showOnlyReducedInfo() {
      return this.player != null && this.player.isReducedDebugInfo() || (Boolean)this.options.reducedDebugInfo().get();
   }

   public ToastManager getToastManager() {
      return this.toastManager;
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

   public PaintingTextureManager getPaintingTextures() {
      return this.paintingTextures;
   }

   public MobEffectTextureManager getMobEffectTextures() {
      return this.mobEffectTextures;
   }

   public MapTextureManager getMapTextureManager() {
      return this.mapTextureManager;
   }

   public MapDecorationTextureManager getMapDecorationTextures() {
      return this.mapDecorationTextures;
   }

   public GuiSpriteManager getGuiSprites() {
      return this.guiSprites;
   }

   public void setWindowActive(boolean var1) {
      this.windowActive = var1;
   }

   public Component grabPanoramixScreenshot(File var1, int var2, int var3) {
      int var4 = this.window.getWidth();
      int var5 = this.window.getHeight();
      RenderTarget var6 = this.getMainRenderTarget();
      float var7 = this.player.getXRot();
      float var8 = this.player.getYRot();
      float var9 = this.player.xRotO;
      float var10 = this.player.yRotO;
      this.gameRenderer.setRenderBlockOutline(false);

      MutableComponent var12;
      try {
         this.gameRenderer.setPanoramicMode(true);
         this.window.setWidth(var2);
         this.window.setHeight(var3);
         var6.resize(var2, var3);

         for(int var11 = 0; var11 < 6; ++var11) {
            switch (var11) {
               case 0:
                  this.player.setYRot(var8);
                  this.player.setXRot(0.0F);
                  break;
               case 1:
                  this.player.setYRot((var8 + 90.0F) % 360.0F);
                  this.player.setXRot(0.0F);
                  break;
               case 2:
                  this.player.setYRot((var8 + 180.0F) % 360.0F);
                  this.player.setXRot(0.0F);
                  break;
               case 3:
                  this.player.setYRot((var8 - 90.0F) % 360.0F);
                  this.player.setXRot(0.0F);
                  break;
               case 4:
                  this.player.setYRot(var8);
                  this.player.setXRot(-90.0F);
                  break;
               case 5:
               default:
                  this.player.setYRot(var8);
                  this.player.setXRot(90.0F);
            }

            this.player.yRotO = this.player.getYRot();
            this.player.xRotO = this.player.getXRot();
            var6.bindWrite(true);
            this.gameRenderer.renderLevel(DeltaTracker.ONE);

            try {
               Thread.sleep(10L);
            } catch (InterruptedException var17) {
            }

            Screenshot.grab(var1, "panorama_" + var11 + ".png", var6, (var0) -> {
            });
         }

         MutableComponent var20 = Component.literal(var1.getName()).withStyle(ChatFormatting.UNDERLINE).withStyle((UnaryOperator)((var1x) -> var1x.withClickEvent(new ClickEvent(ClickEvent.Action.OPEN_FILE, var1.getAbsolutePath()))));
         var12 = Component.translatable("screenshot.success", var20);
         return var12;
      } catch (Exception var18) {
         LOGGER.error("Couldn't save image", var18);
         var12 = Component.translatable("screenshot.failure", var18.getMessage());
      } finally {
         this.player.setXRot(var7);
         this.player.setYRot(var8);
         this.player.xRotO = var9;
         this.player.yRotO = var10;
         this.gameRenderer.setRenderBlockOutline(true);
         this.window.setWidth(var4);
         this.window.setHeight(var5);
         var6.resize(var4, var5);
         this.gameRenderer.setPanoramicMode(false);
         this.getMainRenderTarget().bindWrite(true);
      }

      return var12;
   }

   private Component grabHugeScreenshot(File var1, int var2, int var3, int var4, int var5) {
      try {
         ByteBuffer var6 = GlUtil.allocateMemory(var2 * var3 * 3);
         Screenshot var7 = new Screenshot(var1, var4, var5, var3);
         float var8 = (float)var4 / (float)var2;
         float var9 = (float)var5 / (float)var3;
         float var10 = var8 > var9 ? var8 : var9;

         for(int var11 = (var5 - 1) / var3 * var3; var11 >= 0; var11 -= var3) {
            for(int var12 = 0; var12 < var4; var12 += var2) {
               RenderSystem.setShaderTexture(0, TextureAtlas.LOCATION_BLOCKS);
               float var13 = (float)(var4 - var2) / 2.0F * 2.0F - (float)(var12 * 2);
               float var14 = (float)(var5 - var3) / 2.0F * 2.0F - (float)(var11 * 2);
               var13 /= (float)var2;
               var14 /= (float)var3;
               this.gameRenderer.renderZoomed(var10, var13, var14);
               var6.clear();
               RenderSystem.pixelStore(3333, 1);
               RenderSystem.pixelStore(3317, 1);
               RenderSystem.readPixels(0, 0, var2, var3, 32992, 5121, var6);
               var7.addRegion(var6, var12, var11, var2, var3);
            }

            var7.saveRow();
         }

         File var16 = var7.close();
         GlUtil.freeMemory(var6);
         MutableComponent var17 = Component.literal(var16.getName()).withStyle(ChatFormatting.UNDERLINE).withStyle((UnaryOperator)((var1x) -> var1x.withClickEvent(new ClickEvent(ClickEvent.Action.OPEN_FILE, var16.getAbsolutePath()))));
         return Component.translatable("screenshot.success", var17);
      } catch (Exception var15) {
         LOGGER.warn("Couldn't save screenshot", var15);
         return Component.translatable("screenshot.failure", var15.getMessage());
      }
   }

   @Nullable
   public StoringChunkProgressListener getProgressListener() {
      return (StoringChunkProgressListener)this.progressListener.get();
   }

   public SplashManager getSplashManager() {
      return this.splashManager;
   }

   @Nullable
   public Overlay getOverlay() {
      return this.overlay;
   }

   public PlayerSocialManager getPlayerSocialManager() {
      return this.playerSocialManager;
   }

   public Window getWindow() {
      return this.window;
   }

   public FramerateLimitTracker getFramerateLimitTracker() {
      return this.framerateLimitTracker;
   }

   public DebugScreenOverlay getDebugOverlay() {
      return this.gui.getDebugOverlay();
   }

   public RenderBuffers renderBuffers() {
      return this.renderBuffers;
   }

   public void updateMaxMipLevel(int var1) {
      this.modelManager.updateMaxMipLevel(var1);
   }

   public EntityModelSet getEntityModels() {
      return (EntityModelSet)this.modelManager.entityModels().get();
   }

   public boolean isTextFilteringEnabled() {
      return this.userProperties().flag(UserFlag.PROFANITY_FILTER_ENABLED);
   }

   public void prepareForMultiplayer() {
      this.playerSocialManager.startOnlineMode();
      this.getProfileKeyPairManager().prepareKeyPair();
   }

   @Nullable
   public SignatureValidator getProfileKeySignatureValidator() {
      return SignatureValidator.from(this.authenticationService.getServicesKeySet(), ServicesKeyType.PROFILE_KEY);
   }

   public boolean canValidateProfileKeys() {
      return !this.authenticationService.getServicesKeySet().keys(ServicesKeyType.PROFILE_KEY).isEmpty();
   }

   public InputType getLastInputType() {
      return this.lastInputType;
   }

   public void setLastInputType(InputType var1) {
      this.lastInputType = var1;
   }

   public GameNarrator getNarrator() {
      return this.narrator;
   }

   public ChatListener getChatListener() {
      return this.chatListener;
   }

   public ReportingContext getReportingContext() {
      return this.reportingContext;
   }

   public RealmsDataFetcher realmsDataFetcher() {
      return this.realmsDataFetcher;
   }

   public QuickPlayLog quickPlayLog() {
      return this.quickPlayLog;
   }

   public CommandHistory commandHistory() {
      return this.commandHistory;
   }

   public DirectoryValidator directoryValidator() {
      return this.directoryValidator;
   }

   private float getTickTargetMillis(float var1) {
      if (this.level != null) {
         TickRateManager var2 = this.level.tickRateManager();
         if (var2.runsNormally()) {
            return Math.max(var1, var2.millisecondsPerTick());
         }
      }

      return var1;
   }

   public ItemModelResolver getItemModelResolver() {
      return this.itemModelResolver;
   }

   @Nullable
   public static String getLauncherBrand() {
      return System.getProperty("minecraft.launcher.brand");
   }

   static {
      ON_OSX = Util.getPlatform() == Util.OS.OSX;
      DEFAULT_FONT = ResourceLocation.withDefaultNamespace("default");
      UNIFORM_FONT = ResourceLocation.withDefaultNamespace("uniform");
      ALT_FONT = ResourceLocation.withDefaultNamespace("alt");
      REGIONAL_COMPLIANCIES = ResourceLocation.withDefaultNamespace("regional_compliancies.json");
      RESOURCE_RELOAD_INITIAL_TASK = CompletableFuture.completedFuture(Unit.INSTANCE);
      SOCIAL_INTERACTIONS_NOT_AVAILABLE = Component.translatable("multiplayer.socialInteractions.not_available");
   }

   public static enum ChatStatus {
      ENABLED(CommonComponents.EMPTY) {
         public boolean isChatAllowed(boolean var1) {
            return true;
         }
      },
      DISABLED_BY_OPTIONS(Component.translatable("chat.disabled.options").withStyle(ChatFormatting.RED)) {
         public boolean isChatAllowed(boolean var1) {
            return false;
         }
      },
      DISABLED_BY_LAUNCHER(Component.translatable("chat.disabled.launcher").withStyle(ChatFormatting.RED)) {
         public boolean isChatAllowed(boolean var1) {
            return var1;
         }
      },
      DISABLED_BY_PROFILE(Component.translatable("chat.disabled.profile", Component.keybind(Minecraft.instance.options.keyChat.getName())).withStyle(ChatFormatting.RED)) {
         public boolean isChatAllowed(boolean var1) {
            return var1;
         }
      };

      static final Component INFO_DISABLED_BY_PROFILE = Component.translatable("chat.disabled.profile.moreInfo");
      private final Component message;

      ChatStatus(final Component var3) {
         this.message = var3;
      }

      public Component getMessage() {
         return this.message;
      }

      public abstract boolean isChatAllowed(boolean var1);

      // $FF: synthetic method
      private static ChatStatus[] $values() {
         return new ChatStatus[]{ENABLED, DISABLED_BY_OPTIONS, DISABLED_BY_LAUNCHER, DISABLED_BY_PROFILE};
      }
   }

   static record GameLoadCookie(RealmsClient realmsClient, GameConfig.QuickPlayData quickPlayData) {
      GameLoadCookie(RealmsClient var1, GameConfig.QuickPlayData var2) {
         super();
         this.realmsClient = var1;
         this.quickPlayData = var2;
      }
   }
}
