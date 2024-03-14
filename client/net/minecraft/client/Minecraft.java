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
import com.mojang.authlib.minecraft.UserApiService.UserProperties;
import com.mojang.authlib.yggdrasil.ProfileActionType;
import com.mojang.authlib.yggdrasil.ProfileResult;
import com.mojang.authlib.yggdrasil.ServicesKeyType;
import com.mojang.authlib.yggdrasil.YggdrasilAuthenticationService;
import com.mojang.blaze3d.pipeline.MainTarget;
import com.mojang.blaze3d.pipeline.RenderTarget;
import com.mojang.blaze3d.pipeline.TextureTarget;
import com.mojang.blaze3d.platform.DisplayData;
import com.mojang.blaze3d.platform.GlDebug;
import com.mojang.blaze3d.platform.GlUtil;
import com.mojang.blaze3d.platform.IconSet;
import com.mojang.blaze3d.platform.Window;
import com.mojang.blaze3d.platform.WindowEventHandler;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.systems.TimerQuery;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.BufferUploader;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.mojang.blaze3d.vertex.VertexSorting;
import com.mojang.datafixers.DataFixer;
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
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.Optional;
import java.util.Queue;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.ChatFormatting;
import net.minecraft.CrashReport;
import net.minecraft.CrashReportCategory;
import net.minecraft.FileUtil;
import net.minecraft.Optionull;
import net.minecraft.ReportedException;
import net.minecraft.SharedConstants;
import net.minecraft.SystemReport;
import net.minecraft.Util;
import net.minecraft.client.color.block.BlockColors;
import net.minecraft.client.color.item.ItemColors;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.GuiSpriteManager;
import net.minecraft.client.gui.components.DebugScreenOverlay;
import net.minecraft.client.gui.components.toasts.SystemToast;
import net.minecraft.client.gui.components.toasts.ToastComponent;
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
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.TitleScreen;
import net.minecraft.client.gui.screens.advancements.AdvancementsScreen;
import net.minecraft.client.gui.screens.inventory.CreativeModeInventoryScreen;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.client.gui.screens.recipebook.RecipeCollection;
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
import net.minecraft.client.multiplayer.chat.ChatListener;
import net.minecraft.client.multiplayer.chat.report.ReportEnvironment;
import net.minecraft.client.multiplayer.chat.report.ReportingContext;
import net.minecraft.client.particle.ParticleEngine;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.profiling.ClientMetricsSamplersProvider;
import net.minecraft.client.quickplay.QuickPlay;
import net.minecraft.client.quickplay.QuickPlayLog;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.renderer.FogRenderer;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.GpuWarnlistManager;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.RenderBuffers;
import net.minecraft.client.renderer.VirtualScreen;
import net.minecraft.client.renderer.block.BlockModelShaper;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderDispatcher;
import net.minecraft.client.renderer.debug.DebugRenderer;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.entity.EntityRenderers;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.resources.ClientPackSource;
import net.minecraft.client.resources.FoliageColorReloadListener;
import net.minecraft.client.resources.GrassColorReloadListener;
import net.minecraft.client.resources.MobEffectTextureManager;
import net.minecraft.client.resources.PaintingTextureManager;
import net.minecraft.client.resources.SkinManager;
import net.minecraft.client.resources.SplashManager;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.client.resources.language.LanguageManager;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.ModelManager;
import net.minecraft.client.resources.server.DownloadedPackSource;
import net.minecraft.client.searchtree.FullTextSearchTree;
import net.minecraft.client.searchtree.IdSearchTree;
import net.minecraft.client.searchtree.SearchRegistry;
import net.minecraft.client.searchtree.SearchTree;
import net.minecraft.client.server.IntegratedServer;
import net.minecraft.client.sounds.MusicManager;
import net.minecraft.client.sounds.SoundManager;
import net.minecraft.client.telemetry.ClientTelemetryManager;
import net.minecraft.client.telemetry.TelemetryProperty;
import net.minecraft.client.telemetry.events.GameLoadTimesEvent;
import net.minecraft.client.tutorial.Tutorial;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Connection;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.contents.KeybindResolver;
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
import net.minecraft.server.packs.resources.ReloadInstance;
import net.minecraft.server.packs.resources.ReloadableResourceManager;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.players.GameProfileCache;
import net.minecraft.sounds.Music;
import net.minecraft.sounds.Musics;
import net.minecraft.tags.BiomeTags;
import net.minecraft.tags.TagKey;
import net.minecraft.util.FileZipper;
import net.minecraft.util.MemoryReserve;
import net.minecraft.util.ModCheck;
import net.minecraft.util.Mth;
import net.minecraft.util.SignatureValidator;
import net.minecraft.util.TimeUtil;
import net.minecraft.util.Unit;
import net.minecraft.util.datafix.DataFixers;
import net.minecraft.util.profiling.ContinuousProfiler;
import net.minecraft.util.profiling.EmptyProfileResults;
import net.minecraft.util.profiling.InactiveProfiler;
import net.minecraft.util.profiling.ProfileResults;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.util.profiling.ResultField;
import net.minecraft.util.profiling.SingleTickProfiler;
import net.minecraft.util.profiling.metrics.profiling.ActiveMetricsRecorder;
import net.minecraft.util.profiling.metrics.profiling.InactiveMetricsRecorder;
import net.minecraft.util.profiling.metrics.profiling.MetricsRecorder;
import net.minecraft.util.profiling.metrics.storage.MetricsPersister;
import net.minecraft.util.thread.ReentrantBlockableEventLoop;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.TickRateManager;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.ChatVisiblity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.ItemLore;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.SkullBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.LevelStorageSource;
import net.minecraft.world.level.validation.DirectoryValidator;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import org.apache.commons.io.FileUtils;
import org.joml.Matrix4f;
import org.joml.Matrix4fStack;
import org.lwjgl.util.tinyfd.TinyFileDialogs;
import org.slf4j.Logger;

public class Minecraft extends ReentrantBlockableEventLoop<Runnable> implements WindowEventHandler {
   static Minecraft instance;
   private static final Logger LOGGER = LogUtils.getLogger();
   public static final boolean ON_OSX = Util.getPlatform() == Util.OS.OSX;
   private static final int MAX_TICKS_PER_UPDATE = 10;
   public static final ResourceLocation DEFAULT_FONT = new ResourceLocation("default");
   public static final ResourceLocation UNIFORM_FONT = new ResourceLocation("uniform");
   public static final ResourceLocation ALT_FONT = new ResourceLocation("alt");
   private static final ResourceLocation REGIONAL_COMPLIANCIES = new ResourceLocation("regional_compliancies.json");
   private static final CompletableFuture<Unit> RESOURCE_RELOAD_INITIAL_TASK = CompletableFuture.completedFuture(Unit.INSTANCE);
   private static final Component NBT_TOOLTIP = Component.literal("(+NBT)");
   private static final Component SOCIAL_INTERACTIONS_NOT_AVAILABLE = Component.translatable("multiplayer.socialInteractions.not_available");
   public static final String UPDATE_DRIVERS_ADVICE = "Please make sure you have up-to-date drivers (see aka.ms/mcdriver for instructions).";
   private final long canary = Double.doubleToLongBits(3.141592653589793);
   private final Path resourcePackDirectory;
   private final CompletableFuture<ProfileResult> profileFuture;
   private final TextureManager textureManager;
   private final DataFixer fixerUpper;
   private final VirtualScreen virtualScreen;
   private final Window window;
   private final Timer timer = new Timer(20.0F, 0L, this::getTickTargetMillis);
   private final RenderBuffers renderBuffers;
   public final LevelRenderer levelRenderer;
   private final EntityRenderDispatcher entityRenderDispatcher;
   private final ItemRenderer itemRenderer;
   public final ParticleEngine particleEngine;
   private final SearchRegistry searchRegistry = new SearchRegistry();
   private final User user;
   public final Font font;
   public final Font fontFilterFishy;
   public final GameRenderer gameRenderer;
   public final DebugRenderer debugRenderer;
   private final AtomicReference<StoringChunkProgressListener> progressListener = new AtomicReference<>();
   public final Gui gui;
   public final Options options;
   private final HotbarManager hotbarManager;
   public final MouseHandler mouseHandler;
   public final KeyboardHandler keyboardHandler;
   private InputType lastInputType = InputType.NONE;
   public final File gameDirectory;
   private final String launchedVersion;
   private final String versionType;
   private final Proxy proxy;
   private final LevelStorageSource levelSource;
   private final boolean is64bit;
   private final boolean demo;
   private final boolean allowsMultiplayer;
   private final boolean allowsChat;
   private final ReloadableResourceManager resourceManager;
   private final VanillaPackResources vanillaPackResources;
   private final DownloadedPackSource downloadedPackSource;
   private final PackRepository resourcePackRepository;
   private final LanguageManager languageManager;
   private final BlockColors blockColors;
   private final ItemColors itemColors;
   private final RenderTarget mainRenderTarget;
   private final SoundManager soundManager;
   private final MusicManager musicManager;
   private final FontManager fontManager;
   private final SplashManager splashManager;
   private final GpuWarnlistManager gpuWarnlistManager;
   private final PeriodicNotificationManager regionalCompliancies = new PeriodicNotificationManager(REGIONAL_COMPLIANCIES, Minecraft::countryEqualsISO3);
   private final YggdrasilAuthenticationService authenticationService;
   private final MinecraftSessionService minecraftSessionService;
   private final UserApiService userApiService;
   private final CompletableFuture<UserProperties> userPropertiesFuture;
   private final SkinManager skinManager;
   private final ModelManager modelManager;
   private final BlockRenderDispatcher blockRenderer;
   private final PaintingTextureManager paintingTextures;
   private final MobEffectTextureManager mobEffectTextures;
   private final GuiSpriteManager guiSprites;
   private final ToastComponent toast;
   private final Tutorial tutorial;
   private final PlayerSocialManager playerSocialManager;
   private final EntityModelSet entityModels;
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
   private float pausePartialTick;
   private long lastNanoTime = Util.getNanos();
   private long lastTime;
   private int frames;
   public boolean noRender;
   @Nullable
   public Screen screen;
   @Nullable
   private Overlay overlay;
   private boolean clientLevelTeardownInProgress;
   private Thread gameThread;
   private volatile boolean running;
   @Nullable
   private Supplier<CrashReport> delayedCrash;
   private static int fps;
   public String fpsString = "";
   private long frameTimeNs;
   public boolean wireframe;
   public boolean sectionPath;
   public boolean sectionVisibility;
   public boolean smartCull = true;
   private boolean windowActive;
   private final Queue<Runnable> progressTasks = Queues.newConcurrentLinkedQueue();
   @Nullable
   private CompletableFuture<Void> pendingReload;
   @Nullable
   private TutorialToast socialInteractionsToast;
   private ProfilerFiller profiler = InactiveProfiler.INSTANCE;
   private int fpsPieRenderTicks;
   private final ContinuousProfiler fpsPieProfiler = new ContinuousProfiler(Util.timeSource, () -> this.fpsPieRenderTicks);
   @Nullable
   private ProfileResults fpsPieResults;
   private MetricsRecorder metricsRecorder = InactiveMetricsRecorder.INSTANCE;
   private final ResourceLoadStateTracker reloadStateTracker = new ResourceLoadStateTracker();
   private long savedCpuDuration;
   private double gpuUtilization;
   @Nullable
   private TimerQuery.FrameProfile currentFrameProfile;
   private final Realms32BitWarningStatus realms32BitWarningStatus;
   private final GameNarrator narrator;
   private final ChatListener chatListener;
   private ReportingContext reportingContext;
   private final CommandHistory commandHistory;
   private final DirectoryValidator directoryValidator;
   private boolean gameLoadFinished;
   private final long clientStartTimeMs;
   private long clientTickCount;
   private String debugPath = "root";

   public Minecraft(GameConfig var1) {
      super("Client");
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
      FolderRepositorySource var5 = new FolderRepositorySource(
         this.resourcePackDirectory, PackType.CLIENT_RESOURCES, PackSource.DEFAULT, this.directoryValidator
      );
      this.resourcePackRepository = new PackRepository(var4, this.downloadedPackSource.createRepositorySource(), var5);
      this.vanillaPackResources = var4.getVanillaPack();
      this.proxy = var1.user.proxy;
      this.authenticationService = new YggdrasilAuthenticationService(this.proxy);
      this.minecraftSessionService = this.authenticationService.createMinecraftSessionService();
      this.user = var1.user.user;
      this.profileFuture = CompletableFuture.supplyAsync(
         () -> this.minecraftSessionService.fetchProfile(this.user.getProfileId(), true), Util.nonCriticalIoPool()
      );
      this.userApiService = this.createUserApiService(this.authenticationService, var1);
      this.userPropertiesFuture = CompletableFuture.supplyAsync(() -> {
         try {
            return this.userApiService.fetchProperties();
         } catch (AuthenticationException var2xx) {
            LOGGER.error("Failed to fetch user properties", var2xx);
            return UserApiService.OFFLINE_PROPERTIES;
         }
      }, Util.nonCriticalIoPool());
      LOGGER.info("Setting user: {}", this.user.getName());
      LOGGER.debug("(Session ID is {})", this.user.getSessionId());
      this.demo = var1.game.demo;
      this.allowsMultiplayer = !var1.game.disableMultiplayer;
      this.allowsChat = !var1.game.disableChat;
      this.is64bit = checkIs64Bit();
      this.singleplayerServer = null;
      KeybindResolver.setKeyResolver(KeyMapping::createNameSupplier);
      this.fixerUpper = DataFixers.getDataFixer();
      this.toast = new ToastComponent(this);
      this.gameThread = Thread.currentThread();
      this.options = new Options(this, this.gameDirectory);
      RenderSystem.setShaderGlintAlpha(this.options.glintStrength().get());
      this.running = true;
      this.tutorial = new Tutorial(this, this.options);
      this.hotbarManager = new HotbarManager(var3, this.fixerUpper);
      LOGGER.info("Backend library: {}", RenderSystem.getBackendDescription());
      DisplayData var6;
      if (this.options.overrideHeight > 0 && this.options.overrideWidth > 0) {
         var6 = new DisplayData(
            this.options.overrideWidth, this.options.overrideHeight, var1.display.fullscreenWidth, var1.display.fullscreenHeight, var1.display.isFullscreen
         );
      } else {
         var6 = var1.display;
      }

      Util.timeSource = RenderSystem.initBackendSystem();
      this.virtualScreen = new VirtualScreen(this);
      this.window = this.virtualScreen.newWindow(var6, this.options.fullscreenVideoModeString, this.createTitle());
      this.setWindowActive(true);
      GameLoadTimesEvent.INSTANCE.endStep(TelemetryProperty.LOAD_TIME_PRE_WINDOW_MS);

      try {
         this.window.setIcon(this.vanillaPackResources, SharedConstants.getCurrentVersion().isStable() ? IconSet.RELEASE : IconSet.SNAPSHOT);
      } catch (IOException var13) {
         LOGGER.error("Couldn't set icon", var13);
      }

      this.window.setFramerateLimit(this.options.framerateLimit().get());
      this.mouseHandler = new MouseHandler(this);
      this.mouseHandler.setup(this.window.getWindow());
      this.keyboardHandler = new KeyboardHandler(this);
      this.keyboardHandler.setup(this.window.getWindow());
      RenderSystem.initRenderer(this.options.glDebugVerbosity, false);
      this.mainRenderTarget = new MainTarget(this.window.getWidth(), this.window.getHeight());
      this.mainRenderTarget.setClearColor(0.0F, 0.0F, 0.0F, 0.0F);
      this.mainRenderTarget.clear(ON_OSX);
      this.resourceManager = new ReloadableResourceManager(PackType.CLIENT_RESOURCES);
      this.resourcePackRepository.reload();
      this.options.loadSelectedResourcePacks(this.resourcePackRepository);
      this.languageManager = new LanguageManager(this.options.languageCode);
      this.resourceManager.registerReloadListener(this.languageManager);
      this.textureManager = new TextureManager(this.resourceManager);
      this.resourceManager.registerReloadListener(this.textureManager);
      this.skinManager = new SkinManager(this.textureManager, var2.toPath().resolve("skins"), this.minecraftSessionService, this);
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
      this.itemColors = ItemColors.createDefault(this.blockColors);
      this.modelManager = new ModelManager(this.textureManager, this.blockColors, this.options.mipmapLevels().get());
      this.resourceManager.registerReloadListener(this.modelManager);
      this.entityModels = new EntityModelSet();
      this.resourceManager.registerReloadListener(this.entityModels);
      this.blockEntityRenderDispatcher = new BlockEntityRenderDispatcher(
         this.font, this.entityModels, this::getBlockRenderer, this::getItemRenderer, this::getEntityRenderDispatcher
      );
      this.resourceManager.registerReloadListener(this.blockEntityRenderDispatcher);
      BlockEntityWithoutLevelRenderer var7 = new BlockEntityWithoutLevelRenderer(this.blockEntityRenderDispatcher, this.entityModels);
      this.resourceManager.registerReloadListener(var7);
      this.itemRenderer = new ItemRenderer(this, this.textureManager, this.modelManager, this.itemColors, var7);
      this.resourceManager.registerReloadListener(this.itemRenderer);

      try {
         int var8 = Runtime.getRuntime().availableProcessors();
         int var9 = this.is64Bit() ? var8 : Math.min(var8, 4);
         Tesselator.init();
         this.renderBuffers = new RenderBuffers(var9);
      } catch (OutOfMemoryError var12) {
         TinyFileDialogs.tinyfd_messageBox(
            "Minecraft",
            "Oh no! The game was unable to allocate memory off-heap while trying to start. You may try to free some memory by closing other applications on your computer, check that your system meets the minimum requirements, and try again. If the problem persists, please visit: https://aka.ms/Minecraft-Support",
            "ok",
            "error",
            true
         );
         throw new SilentInitException("Unable to allocate render buffers", var12);
      }

      this.playerSocialManager = new PlayerSocialManager(this, this.userApiService);
      this.blockRenderer = new BlockRenderDispatcher(this.modelManager.getBlockModelShaper(), var7, this.blockColors);
      this.resourceManager.registerReloadListener(this.blockRenderer);
      this.entityRenderDispatcher = new EntityRenderDispatcher(
         this, this.textureManager, this.itemRenderer, this.blockRenderer, this.font, this.options, this.entityModels
      );
      this.resourceManager.registerReloadListener(this.entityRenderDispatcher);
      this.gameRenderer = new GameRenderer(this, this.entityRenderDispatcher.getItemInHandRenderer(), this.resourceManager, this.renderBuffers);
      this.resourceManager.registerReloadListener(this.gameRenderer.createReloadListener());
      this.levelRenderer = new LevelRenderer(this, this.entityRenderDispatcher, this.blockEntityRenderDispatcher, this.renderBuffers);
      this.resourceManager.registerReloadListener(this.levelRenderer);
      this.createSearchTrees();
      this.resourceManager.registerReloadListener(this.searchRegistry);
      this.particleEngine = new ParticleEngine(this.level, this.textureManager);
      this.resourceManager.registerReloadListener(this.particleEngine);
      this.paintingTextures = new PaintingTextureManager(this.textureManager);
      this.resourceManager.registerReloadListener(this.paintingTextures);
      this.mobEffectTextures = new MobEffectTextureManager(this.textureManager);
      this.resourceManager.registerReloadListener(this.mobEffectTextures);
      this.guiSprites = new GuiSpriteManager(this.textureManager);
      this.resourceManager.registerReloadListener(this.guiSprites);
      this.gpuWarnlistManager = new GpuWarnlistManager();
      this.resourceManager.registerReloadListener(this.gpuWarnlistManager);
      this.resourceManager.registerReloadListener(this.regionalCompliancies);
      this.gui = new Gui(this);
      this.debugRenderer = new DebugRenderer(this);
      RealmsClient var14 = RealmsClient.create(this);
      this.realmsDataFetcher = new RealmsDataFetcher(var14);
      RenderSystem.setErrorCallback(this::onFullscreenError);
      if (this.mainRenderTarget.width != this.window.getWidth() || this.mainRenderTarget.height != this.window.getHeight()) {
         StringBuilder var15 = new StringBuilder(
            "Recovering from unsupported resolution ("
               + this.window.getWidth()
               + "x"
               + this.window.getHeight()
               + ").\nPlease make sure you have up-to-date drivers (see aka.ms/mcdriver for instructions)."
         );
         if (GlDebug.isDebugEnabled()) {
            var15.append("\n\nReported GL debug messages:\n").append(String.join("\n", GlDebug.getLastOpenGlDebugMessages()));
         }

         this.window.setWindowed(this.mainRenderTarget.width, this.mainRenderTarget.height);
         TinyFileDialogs.tinyfd_messageBox("Minecraft", var15.toString(), "ok", "error", false);
      } else if (this.options.fullscreen().get() && !this.window.isFullscreen()) {
         this.window.toggleFullScreen();
         this.options.fullscreen().set(this.window.isFullscreen());
      }

      this.window.updateVsync(this.options.enableVsync().get());
      this.window.updateRawMouseInput(this.options.rawMouseInput().get());
      this.window.setDefaultErrorCallback();
      this.resizeDisplay();
      this.gameRenderer.preloadUiShader(this.vanillaPackResources.asProvider());
      this.telemetryManager = new ClientTelemetryManager(this, this.userApiService, this.user);
      this.profileKeyPairManager = ProfileKeyPairManager.create(this.userApiService, this.user, var3);
      this.realms32BitWarningStatus = new Realms32BitWarningStatus(this);
      this.narrator = new GameNarrator(this);
      this.narrator.checkStatus(this.options.narrator().get() != NarratorStatus.OFF);
      this.chatListener = new ChatListener(this);
      this.chatListener.setMessageDelay(this.options.chatDelay().get());
      this.reportingContext = ReportingContext.create(ReportEnvironment.local(), this.userApiService);
      LoadingOverlay.registerTextures(this);
      this.setScreen(new GenericMessageScreen(Component.translatable("gui.loadingMinecraft")));
      List var16 = this.resourcePackRepository.openAllSelected();
      this.reloadStateTracker.startReload(ResourceLoadStateTracker.ReloadReason.INITIAL, var16);
      ReloadInstance var10 = this.resourceManager.createReload(Util.backgroundExecutor(), this, RESOURCE_RELOAD_INITIAL_TASK, var16);
      GameLoadTimesEvent.INSTANCE.beginStep(TelemetryProperty.LOAD_TIME_LOADING_OVERLAY_MS);
      Minecraft.GameLoadCookie var11 = new Minecraft.GameLoadCookie(var14, var1.quickPlay);
      this.setOverlay(new LoadingOverlay(this, var10, var2x -> Util.ifElse(var2x, var2xx -> this.rollbackResourcePacks(var2xx, var11), () -> {
            if (SharedConstants.IS_RUNNING_IN_IDE) {
               this.selfTest();
            }

            this.reloadStateTracker.finishReload();
            this.onResourceLoadFinished(var11);
         }), false));
      this.quickPlayLog = QuickPlayLog.of(var1.quickPlay.path());
   }

   private void onResourceLoadFinished(@Nullable Minecraft.GameLoadCookie var1) {
      if (!this.gameLoadFinished) {
         this.gameLoadFinished = true;
         this.onGameLoadFinished(var1);
      }
   }

   private void onGameLoadFinished(@Nullable Minecraft.GameLoadCookie var1) {
      Runnable var2 = this.buildInitialScreens(var1);
      GameLoadTimesEvent.INSTANCE.endStep(TelemetryProperty.LOAD_TIME_LOADING_OVERLAY_MS);
      GameLoadTimesEvent.INSTANCE.endStep(TelemetryProperty.LOAD_TIME_TOTAL_TIME_MS);
      GameLoadTimesEvent.INSTANCE.send(this.telemetryManager.getOutsideSessionSender());
      var2.run();
   }

   public boolean isGameLoadFinished() {
      return this.gameLoadFinished;
   }

   private Runnable buildInitialScreens(@Nullable Minecraft.GameLoadCookie var1) {
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
         var1.add(var1x -> new AccessibilityOnboardingScreen(this.options, var1x));
      }

      BanDetails var2 = this.multiplayerBan();
      if (var2 != null) {
         var1.add(var1x -> BanNoticeScreens.create(var1xx -> {
               if (var1xx) {
                  Util.getPlatform().openUri("https://aka.ms/mcjavamoderation");
               }

               var1x.run();
            }, var2));
      }

      ProfileResult var3 = (ProfileResult)this.profileFuture.join();
      if (var3 != null) {
         GameProfile var4 = var3.profile();
         Set var5 = var3.actions();
         if (var5.contains(ProfileActionType.FORCED_NAME_CHANGE)) {
            var1.add(var1x -> BanNoticeScreens.createNameBan(var4.getName(), var1x));
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
      return var1.createUserApiService(var2.user.user.getAccessToken());
   }

   public static ModCheck checkModStatus() {
      return ModCheck.identify("vanilla", ClientBrandRetriever::getClientModName, "Client", Minecraft.class);
   }

   private void rollbackResourcePacks(Throwable var1, @Nullable Minecraft.GameLoadCookie var2) {
      if (this.resourcePackRepository.getSelectedIds().size() > 1) {
         this.clearResourcePacksOnError(var1, null, var2);
      } else {
         Util.throwAsRuntime(var1);
      }
   }

   public void clearResourcePacksOnError(Throwable var1, @Nullable Component var2, @Nullable Minecraft.GameLoadCookie var3) {
      LOGGER.info("Caught error loading resourcepacks, removing all selected resourcepacks", var1);
      this.reloadStateTracker.startRecovery(var1);
      this.downloadedPackSource.onRecovery();
      this.resourcePackRepository.setSelected(Collections.emptyList());
      this.options.resourcePacks.clear();
      this.options.incompatibleResourcePacks.clear();
      this.options.save();
      this.reloadResourcePacks(true, var3).thenRun(() -> this.addResourcePackLoadFailToast(var2));
   }

   private void abortResourcePackRecovery() {
      this.setOverlay(null);
      if (this.level != null) {
         this.level.disconnect();
         this.disconnect();
      }

      this.setScreen(new TitleScreen());
      this.addResourcePackLoadFailToast(null);
   }

   private void addResourcePackLoadFailToast(@Nullable Component var1) {
      ToastComponent var2 = this.getToasts();
      SystemToast.addOrUpdate(var2, SystemToast.SystemToastId.PACK_LOAD_FAILURE, Component.translatable("resourcePack.load_fail"), var1);
   }

   public void run() {
      this.gameThread = Thread.currentThread();
      if (Runtime.getRuntime().availableProcessors() > 4) {
         this.gameThread.setPriority(10);
      }

      try {
         boolean var1 = false;

         while(this.running) {
            this.handleDelayedCrash();

            try {
               SingleTickProfiler var2 = SingleTickProfiler.createTickProfiler("Renderer");
               boolean var3 = this.getDebugOverlay().showProfilerChart();
               this.profiler = this.constructProfiler(var3, var2);
               this.profiler.startTick();
               this.metricsRecorder.startTick();
               this.runTick(!var1);
               this.metricsRecorder.endTick();
               this.profiler.endTick();
               this.finishProfilers(var3, var2);
            } catch (OutOfMemoryError var4) {
               if (var1) {
                  throw var4;
               }

               this.emergencySave();
               this.setScreen(new OutOfMemoryScreen());
               System.gc();
               LOGGER.error(LogUtils.FATAL_MARKER, "Out of memory", var4);
               var1 = true;
            }
         }
      } catch (ReportedException var5) {
         LOGGER.error(LogUtils.FATAL_MARKER, "Reported exception thrown!", var5);
         this.emergencySaveAndCrash(var5.getReport());
      } catch (Throwable var6) {
         LOGGER.error(LogUtils.FATAL_MARKER, "Unreported exception thrown!", var6);
         this.emergencySaveAndCrash(new CrashReport("Unexpected error", var6));
      }
   }

   void updateFontOptions() {
      this.fontManager.updateOptions(this.options);
   }

   private void createSearchTrees() {
      this.searchRegistry
         .register(
            SearchRegistry.CREATIVE_NAMES,
            var0 -> new FullTextSearchTree<>(
                  var0x -> var0x.getTooltipLines(null, TooltipFlag.Default.NORMAL.asCreative())
                        .stream()
                        .map(var0xx -> ChatFormatting.stripFormatting(var0xx.getString()).trim())
                        .filter(var0xx -> !var0xx.isEmpty()),
                  var0x -> Stream.of(BuiltInRegistries.ITEM.getKey(var0x.getItem())),
                  var0
               )
         );
      this.searchRegistry.register(SearchRegistry.CREATIVE_TAGS, var0 -> new IdSearchTree<>(var0x -> var0x.getTags().map(TagKey::location), var0));
      this.searchRegistry
         .register(
            SearchRegistry.RECIPE_COLLECTIONS,
            var0 -> new FullTextSearchTree<>(
                  var0x -> var0x.getRecipes()
                        .stream()
                        .flatMap(var1 -> var1.value().getResultItem(var0x.registryAccess()).getTooltipLines(null, TooltipFlag.Default.NORMAL).stream())
                        .map(var0xx -> ChatFormatting.stripFormatting(var0xx.getString()).trim())
                        .filter(var0xx -> !var0xx.isEmpty()),
                  var0x -> var0x.getRecipes().stream().map(var1 -> BuiltInRegistries.ITEM.getKey(var1.value().getResultItem(var0x.registryAccess()).getItem())),
                  var0
               )
         );
      CreativeModeTabs.searchTab().setSearchTreeBuilder(var1 -> {
         this.populateSearchTree(SearchRegistry.CREATIVE_NAMES, var1);
         this.populateSearchTree(SearchRegistry.CREATIVE_TAGS, var1);
      });
   }

   private void onFullscreenError(int var1, long var2) {
      this.options.enableVsync().set(false);
      this.options.save();
   }

   private static boolean checkIs64Bit() {
      String[] var0 = new String[]{"sun.arch.data.model", "com.ibm.vm.bitmode", "os.arch"};

      for(String var4 : var0) {
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

   public void delayCrash(CrashReport var1) {
      this.delayedCrash = () -> this.fillReport(var1);
   }

   public void delayCrashRaw(CrashReport var1) {
      this.delayedCrash = () -> var1;
   }

   private void handleDelayedCrash() {
      if (this.delayedCrash != null) {
         crash(this, this.gameDirectory, this.delayedCrash.get());
      }
   }

   public void emergencySaveAndCrash(CrashReport var1) {
      CrashReport var2 = this.fillReport(var1);
      this.emergencySave();
      crash(this, this.gameDirectory, var2);
   }

   public static void crash(@Nullable Minecraft var0, File var1, CrashReport var2) {
      File var3 = new File(var1, "crash-reports");
      File var4 = new File(var3, "crash-" + Util.getFilenameFormattedDateTime() + "-client.txt");
      Bootstrap.realStdoutPrintln(var2.getFriendlyReport());
      if (var0 != null) {
         var0.soundManager.emergencyShutdown();
      }

      if (var2.getSaveFile() != null) {
         Bootstrap.realStdoutPrintln("#@!@# Game crashed! Crash report saved to: #@!@# " + var2.getSaveFile());
         System.exit(-1);
      } else if (var2.saveToFile(var4)) {
         Bootstrap.realStdoutPrintln("#@!@# Game crashed! Crash report saved to: #@!@# " + var4.getAbsolutePath());
         System.exit(-1);
      } else {
         Bootstrap.realStdoutPrintln("#@?@# Game crashed! Crash report could not be saved. #@?@#");
         System.exit(-2);
      }
   }

   public boolean isEnforceUnicode() {
      return this.options.forceUnicodeFont().get();
   }

   public CompletableFuture<Void> reloadResourcePacks() {
      return this.reloadResourcePacks(false, null);
   }

   private CompletableFuture<Void> reloadResourcePacks(boolean var1, @Nullable Minecraft.GameLoadCookie var2) {
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

            this.setOverlay(
               new LoadingOverlay(
                  this,
                  this.resourceManager.createReload(Util.backgroundExecutor(), this, RESOURCE_RELOAD_INITIAL_TASK, var4),
                  var4x -> Util.ifElse(var4x, var3xx -> {
                        if (var1) {
                           this.downloadedPackSource.onRecoveryFailure();
                           this.abortResourcePackRecovery();
                        } else {
                           this.rollbackResourcePacks(var3xx, var2);
                        }
                     }, () -> {
                        this.levelRenderer.allChanged();
                        this.reloadStateTracker.finishReload();
                        this.downloadedPackSource.onReloadSuccess();
                        var3.complete(null);
                        this.onResourceLoadFinished(var2);
                     }),
                  !var1
               )
            );
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

      for(Block var15 : BuiltInRegistries.BLOCK) {
         UnmodifiableIterator var17 = var15.getStateDefinition().getPossibleStates().iterator();

         while(var17.hasNext()) {
            BlockState var19 = (BlockState)var17.next();
            TextureAtlasSprite var9 = var2.getParticleIcon(var19);
            if (!var19.isAir() && var9 == var12) {
               LOGGER.debug("Missing particle icon for: {}", var19);
            }
         }
      }

      for(Item var16 : BuiltInRegistries.ITEM) {
         ItemStack var18 = var16.getDefaultInstance();
         String var20 = var18.getDescriptionId();
         String var21 = Component.translatable(var20).getString();
         if (var21.toLowerCase(Locale.ROOT).equals(var16.getDescriptionId())) {
            LOGGER.debug("Missing translation for: {} {} {}", new Object[]{var18, var20, var16});
         }
      }

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
      Minecraft.ChatStatus var2 = this.getChatStatus();
      if (!var2.isChatAllowed(this.isLocalServer())) {
         if (this.gui.isShowingChatDisabledByPlayer()) {
            this.gui.setChatDisabledByPlayerShown(false);
            this.setScreen(new ConfirmLinkScreen(var1x -> {
               if (var1x) {
                  Util.getPlatform().openUri("https://aka.ms/JavaAccountSettings");
               }

               this.setScreen(null);
            }, Minecraft.ChatStatus.INFO_DISABLED_BY_PROFILE, "https://aka.ms/JavaAccountSettings", true));
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
               var1 = new DeathScreen(null, this.level.getLevelData().isHardcore());
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

   @Override
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
         this.levelRenderer.close();
         this.soundManager.destroy();
         this.particleEngine.close();
         this.mobEffectTextures.close();
         this.paintingTextures.close();
         this.guiSprites.close();
         this.textureManager.close();
         this.resourceManager.close();
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
      long var2 = Util.getNanos();
      if (this.window.shouldClose()) {
         this.stop();
      }

      if (this.pendingReload != null && !(this.overlay instanceof LoadingOverlay)) {
         CompletableFuture var4 = this.pendingReload;
         this.pendingReload = null;
         this.reloadResourcePacks().thenRun(() -> var4.complete(null));
      }

      Runnable var15;
      while((var15 = this.progressTasks.poll()) != null) {
         var15.run();
      }

      if (var1) {
         int var5 = this.timer.advanceTime(Util.getMillis());
         this.profiler.push("scheduledExecutables");
         this.runAllTasks();
         this.profiler.pop();
         this.profiler.push("tick");

         for(int var6 = 0; var6 < Math.min(10, var5); ++var6) {
            this.profiler.incrementCounter("clientTick");
            this.tick();
         }

         this.profiler.pop();
      }

      this.window.setErrorSection("Render");
      this.profiler.push("sound");
      this.soundManager.updateSource(this.gameRenderer.getMainCamera());
      this.profiler.pop();
      this.profiler.push("render");
      long var16 = Util.getNanos();
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

      RenderSystem.clear(16640, ON_OSX);
      this.mainRenderTarget.bindWrite(true);
      FogRenderer.setupNoFog();
      this.profiler.push("display");
      RenderSystem.enableCull();
      this.profiler.popPush("mouse");
      this.mouseHandler.handleAccumulatedMovement();
      this.profiler.pop();
      if (!this.noRender) {
         this.profiler.popPush("gameRenderer");
         this.gameRenderer.render(this.pause ? this.pausePartialTick : this.timer.partialTick, var2, var1);
         this.profiler.pop();
      }

      if (this.fpsPieResults != null) {
         this.profiler.push("fpsPie");
         GuiGraphics var8 = new GuiGraphics(this, this.renderBuffers.bufferSource());
         this.renderFpsMeter(var8, this.fpsPieResults);
         var8.flush();
         this.profiler.pop();
      }

      this.profiler.push("blit");
      this.mainRenderTarget.unbindWrite();
      this.mainRenderTarget.blitToScreen(this.window.getWidth(), this.window.getHeight());
      this.frameTimeNs = Util.getNanos() - var16;
      if (var7) {
         TimerQuery.getInstance().ifPresent(var1x -> this.currentFrameProfile = var1x.endProfile());
      }

      this.profiler.popPush("updateDisplay");
      this.window.updateDisplay();
      int var17 = this.getFramerateLimit();
      if (var17 < 260) {
         RenderSystem.limitDisplayFPS(var17);
      }

      this.profiler.popPush("yield");
      Thread.yield();
      this.profiler.pop();
      this.window.setErrorSection("Post render");
      ++this.frames;
      boolean var9 = this.hasSingleplayerServer()
         && (this.screen != null && this.screen.isPauseScreen() || this.overlay != null && this.overlay.isPauseScreen())
         && !this.singleplayerServer.isPublished();
      if (this.pause != var9) {
         if (var9) {
            this.pausePartialTick = this.timer.partialTick;
         } else {
            this.timer.partialTick = this.pausePartialTick;
         }

         this.pause = var9;
      }

      long var10 = Util.getNanos();
      long var12 = var10 - this.lastNanoTime;
      if (var7) {
         this.savedCpuDuration = var12;
      }

      this.getDebugOverlay().logFrameDuration(var12);
      this.lastNanoTime = var10;
      this.profiler.push("fpsUpdate");
      if (this.currentFrameProfile != null && this.currentFrameProfile.isDone()) {
         this.gpuUtilization = (double)this.currentFrameProfile.get() * 100.0 / (double)this.savedCpuDuration;
      }

      while(Util.getMillis() >= this.lastTime + 1000L) {
         String var14;
         if (this.gpuUtilization > 0.0) {
            var14 = " GPU: " + (this.gpuUtilization > 100.0 ? ChatFormatting.RED + "100%" : Math.round(this.gpuUtilization) + "%");
         } else {
            var14 = "";
         }

         fps = this.frames;
         this.fpsString = String.format(
            Locale.ROOT,
            "%d fps T: %s%s%s%s B: %d%s",
            fps,
            var17 == 260 ? "inf" : var17,
            this.options.enableVsync().get() ? " vsync " : " ",
            this.options.graphicsMode().get(),
            this.options.cloudStatus().get() == CloudStatus.OFF
               ? ""
               : (this.options.cloudStatus().get() == CloudStatus.FAST ? " fast-clouds" : " fancy-clouds"),
            this.options.biomeBlendRadius().get(),
            var14
         );
         this.lastTime += 1000L;
         this.frames = 0;
      }

      this.profiler.pop();
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
         var3 = ProfilerFiller.tee((ProfilerFiller)var3, this.metricsRecorder.getProfiler());
      }

      return SingleTickProfiler.decorateFiller((ProfilerFiller)var3, var2);
   }

   private void finishProfilers(boolean var1, @Nullable SingleTickProfiler var2) {
      if (var2 != null) {
         var2.endTick();
      }

      if (var1) {
         this.fpsPieResults = this.fpsPieProfiler.getResults();
      } else {
         this.fpsPieResults = null;
      }

      this.profiler = this.fpsPieProfiler.getFiller();
   }

   @Override
   public void resizeDisplay() {
      int var1 = this.window.calculateScale(this.options.guiScale().get(), this.isEnforceUnicode());
      this.window.setGuiScale((double)var1);
      if (this.screen != null) {
         this.screen.resize(this, this.window.getGuiScaledWidth(), this.window.getGuiScaledHeight());
      }

      RenderTarget var2 = this.getMainRenderTarget();
      var2.resize(this.window.getWidth(), this.window.getHeight(), ON_OSX);
      this.gameRenderer.resize(this.window.getWidth(), this.window.getHeight());
      this.mouseHandler.setIgnoreFirstMove();
   }

   @Override
   public void cursorEntered() {
      this.mouseHandler.cursorEntered();
   }

   public int getFps() {
      return fps;
   }

   public long getFrameTimeNs() {
      return this.frameTimeNs;
   }

   private int getFramerateLimit() {
      return this.level != null || this.screen == null && this.overlay == null ? this.window.getFramerateLimit() : 60;
   }

   private void emergencySave() {
      try {
         MemoryReserve.release();
         this.levelRenderer.clear();
      } catch (Throwable var3) {
      }

      try {
         System.gc();
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
         Consumer var2 = var2x -> {
            if (var2x != EmptyProfileResults.EMPTY) {
               int var3xx = var2x.getTickDuration();
               double var4xx = (double)var2x.getNanoDuration() / (double)TimeUtil.NANOSECONDS_PER_SECOND;
               this.execute(
                  () -> var1.accept(
                        Component.translatable(
                           "commands.debug.stopped",
                           String.format(Locale.ROOT, "%.2f", var4x),
                           var3x,
                           String.format(Locale.ROOT, "%.2f", (double)var3x / var4x)
                        )
                     )
               );
            }
         };
         Consumer var3 = var2x -> {
            MutableComponent var3xx = Component.literal(var2x.toString())
               .withStyle(ChatFormatting.UNDERLINE)
               .withStyle(var1xx -> var1xx.withClickEvent(new ClickEvent(ClickEvent.Action.OPEN_FILE, var2x.toFile().getParent())));
            this.execute(() -> var1.accept(Component.translatable("debug.profiling.stop", var3x)));
         };
         SystemReport var4 = fillSystemReport(new SystemReport(), this, this.languageManager, this.launchedVersion, this.options);
         Consumer var5 = var3x -> {
            Path var4xx = this.archiveProfilingReport(var4, var3x);
            var3.accept(var4xx);
         };
         Consumer var6;
         if (this.singleplayerServer == null) {
            var6 = var1x -> var5.accept(ImmutableList.of(var1x));
         } else {
            this.singleplayerServer.fillSystemReport(var4);
            CompletableFuture var7 = new CompletableFuture();
            CompletableFuture var8 = new CompletableFuture();
            CompletableFuture.allOf(var7, var8).thenRunAsync(() -> var5.accept(ImmutableList.of((Path)var7.join(), (Path)var8.join())), Util.ioPool());
            this.singleplayerServer.startRecordingMetrics(var0 -> {
            }, var8::complete);
            var6 = var7::complete;
         }

         this.metricsRecorder = ActiveMetricsRecorder.createStarted(
            new ClientMetricsSamplersProvider(Util.timeSource, this.levelRenderer), Util.timeSource, Util.ioPool(), new MetricsPersister("client"), var2x -> {
               this.metricsRecorder = InactiveMetricsRecorder.INSTANCE;
               var2.accept(var2x);
            }, var6
         );
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

      try (FileZipper var24 = new FileZipper(var3)) {
         var24.add(Paths.get("system.txt"), var1.toLineSeparatedString());
         var24.add(Paths.get("client").resolve(this.options.getFile().getName()), this.options.dumpOptionsForReport());
         var2.forEach(var24::add);
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

   public void debugFpsMeterKeyPress(int var1) {
      if (this.fpsPieResults != null) {
         List var2 = this.fpsPieResults.getTimes(this.debugPath);
         if (!var2.isEmpty()) {
            ResultField var3 = (ResultField)var2.remove(0);
            if (var1 == 0) {
               if (!var3.name.isEmpty()) {
                  int var4 = this.debugPath.lastIndexOf(30);
                  if (var4 >= 0) {
                     this.debugPath = this.debugPath.substring(0, var4);
                  }
               }
            } else {
               --var1;
               if (var1 < var2.size() && !"unspecified".equals(((ResultField)var2.get(var1)).name)) {
                  if (!this.debugPath.isEmpty()) {
                     this.debugPath = this.debugPath + "\u001e";
                  }

                  this.debugPath = this.debugPath + ((ResultField)var2.get(var1)).name;
               }
            }
         }
      }
   }

   private void renderFpsMeter(GuiGraphics var1, ProfileResults var2) {
      List var3 = var2.getTimes(this.debugPath);
      ResultField var4 = (ResultField)var3.remove(0);
      RenderSystem.clear(256, ON_OSX);
      RenderSystem.setShader(GameRenderer::getPositionColorShader);
      Matrix4f var5 = new Matrix4f().setOrtho(0.0F, (float)this.window.getWidth(), (float)this.window.getHeight(), 0.0F, 1000.0F, 3000.0F);
      RenderSystem.setProjectionMatrix(var5, VertexSorting.ORTHOGRAPHIC_Z);
      Matrix4fStack var6 = RenderSystem.getModelViewStack();
      var6.pushMatrix();
      var6.translation(0.0F, 0.0F, -2000.0F);
      RenderSystem.applyModelViewMatrix();
      RenderSystem.lineWidth(1.0F);
      Tesselator var7 = Tesselator.getInstance();
      BufferBuilder var8 = var7.getBuilder();
      boolean var9 = true;
      int var10 = this.window.getWidth() - 160 - 10;
      int var11 = this.window.getHeight() - 320;
      RenderSystem.enableBlend();
      var8.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR);
      var8.vertex((double)((float)var10 - 176.0F), (double)((float)var11 - 96.0F - 16.0F), 0.0).color(200, 0, 0, 0).endVertex();
      var8.vertex((double)((float)var10 - 176.0F), (double)(var11 + 320), 0.0).color(200, 0, 0, 0).endVertex();
      var8.vertex((double)((float)var10 + 176.0F), (double)(var11 + 320), 0.0).color(200, 0, 0, 0).endVertex();
      var8.vertex((double)((float)var10 + 176.0F), (double)((float)var11 - 96.0F - 16.0F), 0.0).color(200, 0, 0, 0).endVertex();
      var7.end();
      RenderSystem.disableBlend();
      double var12 = 0.0;

      for(ResultField var15 : var3) {
         int var16 = Mth.floor(var15.percentage / 4.0) + 1;
         var8.begin(VertexFormat.Mode.TRIANGLE_FAN, DefaultVertexFormat.POSITION_COLOR);
         int var17 = var15.getColor();
         int var18 = var17 >> 16 & 0xFF;
         int var19 = var17 >> 8 & 0xFF;
         int var20 = var17 & 0xFF;
         var8.vertex((double)var10, (double)var11, 0.0).color(var18, var19, var20, 255).endVertex();

         for(int var21 = var16; var21 >= 0; --var21) {
            float var22 = (float)((var12 + var15.percentage * (double)var21 / (double)var16) * 6.2831854820251465 / 100.0);
            float var23 = Mth.sin(var22) * 160.0F;
            float var24 = Mth.cos(var22) * 160.0F * 0.5F;
            var8.vertex((double)((float)var10 + var23), (double)((float)var11 - var24), 0.0).color(var18, var19, var20, 255).endVertex();
         }

         var7.end();
         var8.begin(VertexFormat.Mode.TRIANGLE_STRIP, DefaultVertexFormat.POSITION_COLOR);

         for(int var37 = var16; var37 >= 0; --var37) {
            float var38 = (float)((var12 + var15.percentage * (double)var37 / (double)var16) * 6.2831854820251465 / 100.0);
            float var39 = Mth.sin(var38) * 160.0F;
            float var40 = Mth.cos(var38) * 160.0F * 0.5F;
            if (!(var40 > 0.0F)) {
               var8.vertex((double)((float)var10 + var39), (double)((float)var11 - var40), 0.0).color(var18 >> 1, var19 >> 1, var20 >> 1, 255).endVertex();
               var8.vertex((double)((float)var10 + var39), (double)((float)var11 - var40 + 10.0F), 0.0)
                  .color(var18 >> 1, var19 >> 1, var20 >> 1, 255)
                  .endVertex();
            }
         }

         var7.end();
         var12 += var15.percentage;
      }

      DecimalFormat var25 = new DecimalFormat("##0.00");
      var25.setDecimalFormatSymbols(DecimalFormatSymbols.getInstance(Locale.ROOT));
      String var26 = ProfileResults.demanglePath(var4.name);
      String var28 = "";
      if (!"unspecified".equals(var26)) {
         var28 = var28 + "[0] ";
      }

      if (var26.isEmpty()) {
         var28 = var28 + "ROOT ";
      } else {
         var28 = var28 + var26 + " ";
      }

      int var32 = 16777215;
      var1.drawString(this.font, var28, var10 - 160, var11 - 80 - 16, 16777215);
      var28 = var25.format(var4.globalPercentage) + "%";
      var1.drawString(this.font, var28, var10 + 160 - this.font.width(var28), var11 - 80 - 16, 16777215);

      for(int var27 = 0; var27 < var3.size(); ++var27) {
         ResultField var31 = (ResultField)var3.get(var27);
         StringBuilder var33 = new StringBuilder();
         if ("unspecified".equals(var31.name)) {
            var33.append("[?] ");
         } else {
            var33.append("[").append(var27 + 1).append("] ");
         }

         String var34 = var33.append(var31.name).toString();
         var1.drawString(this.font, var34, var10 - 160, var11 + 80 + var27 * 8 + 20, var31.getColor());
         var34 = var25.format(var31.percentage) + "%";
         var1.drawString(this.font, var34, var10 + 160 - 50 - this.font.width(var34), var11 + 80 + var27 * 8 + 20, var31.getColor());
         var34 = var25.format(var31.globalPercentage) + "%";
         var1.drawString(this.font, var34, var10 + 160 - this.font.width(var34), var11 + 80 + var27 * 8 + 20, var31.getColor());
      }

      var6.popMatrix();
      RenderSystem.applyModelViewMatrix();
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
            switch(this.hitResult.getType()) {
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
                  switch(this.hitResult.getType()) {
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
                        InteractionResult var11 = this.gameMode.useItemOn(this.player, var4, var9);
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
                  InteractionResult var12 = this.gameMode.useItem(this.player, var4);
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

   // $VF: Could not properly define all variable types!
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   public void tick() {
      ++this.clientTickCount;
      if (this.level != null && !this.pause) {
         this.level.tickRateManager().tick();
      }

      if (this.rightClickDelay > 0) {
         --this.rightClickDelay;
      }

      this.profiler.push("gui");
      this.chatListener.tick();
      this.gui.tick(this.pause);
      this.profiler.pop();
      this.gameRenderer.pick(1.0F);
      this.tutorial.onLookAt(this.level, this.hitResult);
      this.profiler.push("gameMode");
      if (!this.pause && this.level != null) {
         this.gameMode.tick();
      }

      this.profiler.popPush("textures");
      boolean var1 = this.level == null || this.level.tickRateManager().runsNormally();
      if (var1) {
         this.textureManager.tick();
      }

      if (this.screen != null || this.player == null) {
         Screen var3 = this.screen;
         if (var3 instanceof InBedChatScreen var2 && !this.player.isSleeping()) {
            var2.onPlayerWokeUp();
         }
      } else if (this.player.isDeadOrDying() && !(this.screen instanceof DeathScreen)) {
         this.setScreen(null);
      } else if (this.player.isSleeping() && this.level != null) {
         this.setScreen(new InBedChatScreen());
      }

      if (this.screen != null) {
         this.missTime = 10000;
      }

      if (this.screen != null) {
         Screen.wrapScreenError(() -> this.screen.tick(), "Ticking screen", this.screen.getClass().getCanonicalName());
      }

      if (!this.getDebugOverlay().showDebugScreen()) {
         this.gui.clearCache();
      }

      if (this.overlay == null && this.screen == null) {
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
            if (!this.options.joinedFirstServer && this.isMultiplayerServer()) {
               MutableComponent var6 = Component.translatable("tutorial.socialInteractions.title");
               MutableComponent var7 = Component.translatable("tutorial.socialInteractions.description", Tutorial.key("socialInteractions"));
               this.socialInteractionsToast = new TutorialToast(TutorialToast.Icons.SOCIAL_INTERACTIONS, var6, var7, true);
               this.tutorial.addTimedToast(this.socialInteractionsToast, 160);
               this.options.joinedFirstServer = true;
               this.options.save();
            }

            this.tutorial.tick();

            try {
               this.level.tick(() -> true);
            } catch (Throwable var5) {
               CrashReport var8 = CrashReport.forThrowable(var5, "Exception in world tick");
               if (this.level == null) {
                  CrashReportCategory var4 = var8.addCategory("Affected level");
                  var4.setDetail("Problem", "Level is null!");
               } else {
                  this.level.fillReportDetails(var8);
               }

               throw new ReportedException(var8);
            }
         }

         this.profiler.popPush("animateTick");
         if (!this.pause && var1) {
            this.level.animateTick(this.player.getBlockX(), this.player.getBlockY(), this.player.getBlockZ());
         }

         this.profiler.popPush("particles");
         if (!this.pause && var1) {
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
               this.tutorial.removeTimedToast(this.socialInteractionsToast);
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
            this.getConnection()
               .send(new ServerboundPlayerActionPacket(ServerboundPlayerActionPacket.Action.SWAP_ITEM_WITH_OFFHAND, BlockPos.ZERO, Direction.DOWN));
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
      this.progressListener.set(null);
      Instant var5 = Instant.now();

      try {
         var1.saveDataTag(var3.registries().compositeAccess(), var3.worldData());
         Services var6 = Services.create(this.authenticationService, this.gameDirectory);
         var6.profileCache().setExecutor(this);
         SkullBlockEntity.setup(var6, this);
         GameProfileCache.setUsesAuthentication(false);
         this.singleplayerServer = MinecraftServer.spin(var5x -> new IntegratedServer(var5x, this, var1, var2, var3, var6, var1xx -> {
               StoringChunkProgressListener var2xxx = StoringChunkProgressListener.createFromGameruleRadius(var1xx + 0);
               this.progressListener.set(var2xxx);
               return ProcessorChunkProgressListener.createStarted(var2xxx, this.progressTasks::add);
            }));
         this.isLocalServer = true;
         this.updateReportEnvironment(ReportEnvironment.local());
         this.quickPlayLog.setWorldData(QuickPlayLog.Type.SINGLEPLAYER, var1.getLevelId(), var3.worldData().getLevelName());
      } catch (Throwable var11) {
         CrashReport var7 = CrashReport.forThrowable(var11, "Starting integrated server");
         CrashReportCategory var8 = var7.addCategory("Starting integrated server");
         var8.setDetail("Level ID", var1.getLevelId());
         var8.setDetail("Level Name", () -> var3.worldData().getLevelName());
         throw new ReportedException(var7);
      }

      while(this.progressListener.get() == null) {
         Thread.yield();
      }

      LevelLoadingScreen var12 = new LevelLoadingScreen(this.progressListener.get());
      this.setScreen(var12);
      this.profiler.push("waitForServer");

      for(; !this.singleplayerServer.isReady() || this.overlay != null; this.handleDelayedCrash()) {
         var12.tick();
         this.runTick(false);

         try {
            Thread.sleep(16L);
         } catch (InterruptedException var10) {
         }
      }

      this.profiler.pop();
      Duration var13 = Duration.between(var5, Instant.now());
      SocketAddress var14 = this.singleplayerServer.getConnection().startMemoryChannel();
      Connection var9 = Connection.connectToLocalServer(var14);
      var9.initiateServerboundPlayConnection(var14.toString(), 0, new ClientHandshakePacketListenerImpl(var9, this, null, null, var4, var13, var0 -> {
      }, null));
      var9.send(new ServerboundHelloPacket(this.getUser().getName(), this.getUser().getProfileId()));
      this.pendingConnection = var9;
   }

   public void setLevel(ClientLevel var1) {
      ProgressScreen var2 = new ProgressScreen(true);
      var2.progressStartNoAbort(Component.translatable("connect.joining"));
      this.updateScreenAndTick(var2);
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
               this.profiler.push("waitForServer");

               while(!var4.isShutdown()) {
                  this.runTick(false);
               }

               this.profiler.pop();
            }

            this.gui.onDisconnected();
            this.isLocalServer = false;
         }

         this.level = null;
         this.updateLevelInEngines(null);
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
         this.updateLevelInEngines(null);
         this.player = null;
      } finally {
         this.clientLevelTeardownInProgress = false;
      }

      SkullBlockEntity.clear();
   }

   private void updateScreenAndTick(Screen var1) {
      this.profiler.push("forcedTick");
      this.soundManager.stop();
      this.cameraEntity = null;
      this.pendingConnection = null;
      this.setScreen(var1);
      this.runTick(false);
      this.profiler.pop();
   }

   public void forceSetScreen(Screen var1) {
      this.profiler.push("forcedTick");
      this.setScreen(var1);
      this.runTick(false);
      this.profiler.pop();
   }

   private void updateLevelInEngines(@Nullable ClientLevel var1) {
      this.levelRenderer.setLevel(var1);
      this.particleEngine.setLevel(var1);
      this.blockEntityRenderDispatcher.setLevel(var1);
      this.updateTitle();
   }

   private UserProperties userProperties() {
      return (UserProperties)this.userPropertiesFuture.join();
   }

   public boolean telemetryOptInExtra() {
      return this.extraTelemetryAvailable() && this.options.telemetryOptInExtra().get();
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
      ProfileResult var1 = (ProfileResult)this.profileFuture.getNow(null);
      return var1 != null && var1.actions().contains(ProfileActionType.FORCED_NAME_CHANGE);
   }

   public boolean isBlocked(UUID var1) {
      if (this.getChatStatus().isChatAllowed(false)) {
         return this.playerSocialManager.shouldHideMessageFrom(var1);
      } else {
         return (this.player == null || !var1.equals(this.player.getUUID())) && !var1.equals(Util.NIL_UUID);
      }
   }

   public Minecraft.ChatStatus getChatStatus() {
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
      return instance.options.graphicsMode().get().getId() >= GraphicsStatus.FANCY.getId();
   }

   public static boolean useShaderTransparency() {
      return !instance.gameRenderer.isPanoramicMode() && instance.options.graphicsMode().get().getId() >= GraphicsStatus.FABULOUS.getId();
   }

   public static boolean useAmbientOcclusion() {
      return instance.options.ambientOcclusion().get();
   }

   private void pickBlock() {
      if (this.hitResult != null && this.hitResult.getType() != HitResult.Type.MISS) {
         boolean var1 = this.player.getAbilities().instabuild;
         BlockEntity var2 = null;
         HitResult.Type var4 = this.hitResult.getType();
         ItemStack var3;
         if (var4 == HitResult.Type.BLOCK) {
            BlockPos var5 = ((BlockHitResult)this.hitResult).getBlockPos();
            BlockState var6 = this.level.getBlockState(var5);
            if (var6.isAir()) {
               return;
            }

            Block var7 = var6.getBlock();
            var3 = var7.getCloneItemStack(this.level, var5, var6);
            if (var3.isEmpty()) {
               return;
            }

            if (var1 && Screen.hasControlDown() && var6.hasBlockEntity()) {
               var2 = this.level.getBlockEntity(var5);
            }
         } else {
            if (var4 != HitResult.Type.ENTITY || !var1) {
               return;
            }

            Entity var8 = ((EntityHitResult)this.hitResult).getEntity();
            var3 = var8.getPickResult();
            if (var3 == null) {
               return;
            }
         }

         if (var3.isEmpty()) {
            String var10 = "";
            if (var4 == HitResult.Type.BLOCK) {
               var10 = BuiltInRegistries.BLOCK.getKey(this.level.getBlockState(((BlockHitResult)this.hitResult).getBlockPos()).getBlock()).toString();
            } else if (var4 == HitResult.Type.ENTITY) {
               var10 = BuiltInRegistries.ENTITY_TYPE.getKey(((EntityHitResult)this.hitResult).getEntity().getType()).toString();
            }

            LOGGER.warn("Picking on: [{}] {} gave null item", var4, var10);
         } else {
            Inventory var9 = this.player.getInventory();
            if (var2 != null) {
               this.addCustomNbtData(var3, var2, this.level.registryAccess());
            }

            int var11 = var9.findSlotMatchingItem(var3);
            if (var1) {
               var9.setPickedItem(var3);
               this.gameMode.handleCreativeModeItemAdd(this.player.getItemInHand(InteractionHand.MAIN_HAND), 36 + var9.selected);
            } else if (var11 != -1) {
               if (Inventory.isHotbarSlot(var11)) {
                  var9.selected = var11;
               } else {
                  this.gameMode.handlePickItem(var11);
               }
            }
         }
      }
   }

   private void addCustomNbtData(ItemStack var1, BlockEntity var2, RegistryAccess var3) {
      CompoundTag var4 = var2.saveWithFullMetadata(var3);
      var2.removeComponentsFromTag(var4);
      BlockItem.setBlockEntityData(var1, var2.getType(), var4);
      var1.applyComponents(var2.collectComponents());
      var1.update(DataComponents.LORE, ItemLore.EMPTY, NBT_TOOLTIP, ItemLore::withLineAdded);
   }

   public CrashReport fillReport(CrashReport var1) {
      SystemReport var2 = var1.getSystemReport();
      fillSystemReport(var2, this, this.languageManager, this.launchedVersion, this.options);
      this.fillUptime(var1.addCategory("Uptime"));
      if (this.level != null) {
         this.level.fillReportDetails(var1);
      }

      if (this.singleplayerServer != null) {
         this.singleplayerServer.fillSystemReport(var2);
      }

      this.reloadStateTracker.fillCrashReport(var1);
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
      var1.setDetail("JVM uptime", () -> formatSeconds((double)ManagementFactory.getRuntimeMXBean().getUptime() / 1000.0));
      var1.setDetail("Wall uptime", () -> formatSeconds((double)(System.currentTimeMillis() - this.clientStartTimeMs) / 1000.0));
      var1.setDetail("High-res time", () -> formatSeconds((double)Util.getMillis() / 1000.0));
      var1.setDetail("Client ticks", () -> String.format(Locale.ROOT, "%d ticks / %.3fs", this.clientTickCount, (double)this.clientTickCount / 20.0));
   }

   private static SystemReport fillSystemReport(
      SystemReport var0, @Nullable Minecraft var1, @Nullable LanguageManager var2, String var3, @Nullable Options var4
   ) {
      var0.setDetail("Launched Version", () -> var3);
      String var5 = getLauncherBrand();
      if (var5 != null) {
         var0.setDetail("Launcher name", var5);
      }

      var0.setDetail("Backend library", RenderSystem::getBackendDescription);
      var0.setDetail("Backend API", RenderSystem::getApiDescription);
      var0.setDetail("Window size", () -> var1 != null ? var1.window.getWidth() + "x" + var1.window.getHeight() : "<not initialized>");
      var0.setDetail("GL Caps", RenderSystem::getCapsString);
      var0.setDetail("GL debug messages", () -> GlDebug.isDebugEnabled() ? String.join("\n", GlDebug.getLastOpenGlDebugMessages()) : "<disabled>");
      var0.setDetail("Using VBOs", () -> "Yes");
      var0.setDetail("Is Modded", () -> checkModStatus().fullDescription());
      var0.setDetail("Universe", () -> var1 != null ? Long.toHexString(var1.canary) : "404");
      var0.setDetail("Type", "Client (map_client.txt)");
      if (var4 != null) {
         if (var1 != null) {
            String var6 = var1.getGpuWarnlistManager().getAllWarnings();
            if (var6 != null) {
               var0.setDetail("GPU Warnings", var6);
            }
         }

         var0.setDetail("Graphics mode", var4.graphicsMode().get().toString());
         var0.setDetail("Render Distance", var4.getEffectiveRenderDistance() + "/" + var4.renderDistance().get() + " chunks");
         var0.setDetail("Resource Packs", () -> {
            StringBuilder var1xx = new StringBuilder();

            for(String var3xx : var4.resourcePacks) {
               if (var1xx.length() > 0) {
                  var1xx.append(", ");
               }

               var1xx.append(var3xx);
               if (var4.incompatibleResourcePacks.contains(var3xx)) {
                  var1xx.append(" (incompatible)");
               }
            }

            return var1xx.toString();
         });
      }

      if (var2 != null) {
         var0.setDetail("Current Language", () -> var2.getSelected());
      }

      var0.setDetail("Locale", String.valueOf(Locale.getDefault()));
      var0.setDetail("CPU", GlUtil::getCpuInfo);
      return var0;
   }

   public static Minecraft getInstance() {
      return instance;
   }

   public CompletableFuture<Void> delayTextureReload() {
      return this.<CompletableFuture<Void>>submit(this::reloadResourcePacks).thenCompose(var0 -> var0);
   }

   public void updateReportEnvironment(ReportEnvironment var1) {
      if (!this.reportingContext.matches(var1)) {
         this.reportingContext = ReportingContext.create(var1, this.userApiService);
      }
   }

   @Nullable
   public ServerData getCurrentServer() {
      return Optionull.map(this.getConnection(), ClientPacketListener::getServerData);
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
      return this.modelManager.getAtlas(var1)::getSprite;
   }

   public boolean is64Bit() {
      return this.is64bit;
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

   public Music getSituationalMusic() {
      Music var1 = Optionull.map(this.screen, Screen::getBackgroundMusic);
      if (var1 != null) {
         return var1;
      } else if (this.player != null) {
         if (this.player.level().dimension() == Level.END) {
            return this.gui.getBossOverlay().shouldPlayMusic() ? Musics.END_BOSS : Musics.END;
         } else {
            Holder var2 = this.player.level().getBiome(this.player.blockPosition());
            if (!this.musicManager.isPlayingMusic(Musics.UNDER_WATER) && (!this.player.isUnderWater() || !var2.is(BiomeTags.PLAYS_UNDERWATER_MUSIC))) {
               return this.player.level().dimension() != Level.NETHER && this.player.getAbilities().instabuild && this.player.getAbilities().mayfly
                  ? Musics.CREATIVE
                  : ((Biome)var2.value()).getBackgroundMusic().orElse(Musics.GAME);
            } else {
               return Musics.UNDER_WATER;
            }
         }
      } else {
         return Musics.MENU;
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
      return var1.isCurrentlyGlowing()
         || this.player != null && this.player.isSpectator() && this.options.keySpectatorOutlines.isDown() && var1.getType() == EntityType.PLAYER;
   }

   @Override
   protected Thread getRunningThread() {
      return this.gameThread;
   }

   @Override
   protected Runnable wrapRunnable(Runnable var1) {
      return var1;
   }

   @Override
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

   public <T> SearchTree<T> getSearchTree(SearchRegistry.Key<T> var1) {
      return this.searchRegistry.getTree(var1);
   }

   public <T> void populateSearchTree(SearchRegistry.Key<T> var1, List<T> var2) {
      this.searchRegistry.populate(var1, var2);
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
      return this.player != null && this.player.isReducedDebugInfo() || this.options.reducedDebugInfo().get();
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

   public PaintingTextureManager getPaintingTextures() {
      return this.paintingTextures;
   }

   public MobEffectTextureManager getMobEffectTextures() {
      return this.mobEffectTextures;
   }

   public GuiSpriteManager getGuiSprites() {
      return this.guiSprites;
   }

   @Override
   public void setWindowActive(boolean var1) {
      this.windowActive = var1;
   }

   public Component grabPanoramixScreenshot(File var1, int var2, int var3) {
      int var4 = this.window.getWidth();
      int var5 = this.window.getHeight();
      TextureTarget var6 = new TextureTarget(var2, var3, true, ON_OSX);
      float var7 = this.player.getXRot();
      float var8 = this.player.getYRot();
      float var9 = this.player.xRotO;
      float var10 = this.player.yRotO;
      this.gameRenderer.setRenderBlockOutline(false);

      MutableComponent var12;
      try {
         this.gameRenderer.setPanoramicMode(true);
         this.levelRenderer.graphicsChanged();
         this.window.setWidth(var2);
         this.window.setHeight(var3);

         for(int var11 = 0; var11 < 6; ++var11) {
            switch(var11) {
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
            this.gameRenderer.renderLevel(1.0F, 0L);

            try {
               Thread.sleep(10L);
            } catch (InterruptedException var17) {
            }

            Screenshot.grab(var1, "panorama_" + var11 + ".png", var6, var0 -> {
            });
         }

         MutableComponent var20 = Component.literal(var1.getName())
            .withStyle(ChatFormatting.UNDERLINE)
            .withStyle(var1x -> var1x.withClickEvent(new ClickEvent(ClickEvent.Action.OPEN_FILE, var1.getAbsolutePath())));
         return Component.translatable("screenshot.success", var20);
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
         var6.destroyBuffers();
         this.gameRenderer.setPanoramicMode(false);
         this.levelRenderer.graphicsChanged();
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
         MutableComponent var17 = Component.literal(var16.getName())
            .withStyle(ChatFormatting.UNDERLINE)
            .withStyle(var1x -> var1x.withClickEvent(new ClickEvent(ClickEvent.Action.OPEN_FILE, var16.getAbsolutePath())));
         return Component.translatable("screenshot.success", var17);
      } catch (Exception var15) {
         LOGGER.warn("Couldn't save screenshot", var15);
         return Component.translatable("screenshot.failure", var15.getMessage());
      }
   }

   public ProfilerFiller getProfiler() {
      return this.profiler;
   }

   @Nullable
   public StoringChunkProgressListener getProgressListener() {
      return this.progressListener.get();
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

   public boolean renderOnThread() {
      return false;
   }

   public Window getWindow() {
      return this.window;
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
      return this.entityModels;
   }

   public boolean isTextFilteringEnabled() {
      return this.userProperties().flag(UserFlag.PROFANITY_FILTER_ENABLED);
   }

   public void prepareForMultiplayer() {
      this.playerSocialManager.startOnlineMode();
      this.getProfileKeyPairManager().prepareKeyPair();
   }

   public Realms32BitWarningStatus getRealms32BitWarningStatus() {
      return this.realms32BitWarningStatus;
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

   @Nullable
   public static String getLauncherBrand() {
      return System.getProperty("minecraft.launcher.brand");
   }

   public static enum ChatStatus {
      ENABLED(CommonComponents.EMPTY) {
         @Override
         public boolean isChatAllowed(boolean var1) {
            return true;
         }
      },
      DISABLED_BY_OPTIONS(Component.translatable("chat.disabled.options").withStyle(ChatFormatting.RED)) {
         @Override
         public boolean isChatAllowed(boolean var1) {
            return false;
         }
      },
      DISABLED_BY_LAUNCHER(Component.translatable("chat.disabled.launcher").withStyle(ChatFormatting.RED)) {
         @Override
         public boolean isChatAllowed(boolean var1) {
            return var1;
         }
      },
      DISABLED_BY_PROFILE(
         Component.translatable("chat.disabled.profile", Component.keybind(Minecraft.instance.options.keyChat.getName())).withStyle(ChatFormatting.RED)
      ) {
         @Override
         public boolean isChatAllowed(boolean var1) {
            return var1;
         }
      };

      static final Component INFO_DISABLED_BY_PROFILE = Component.translatable("chat.disabled.profile.moreInfo");
      private final Component message;

      ChatStatus(Component var3) {
         this.message = var3;
      }

      public Component getMessage() {
         return this.message;
      }

      public abstract boolean isChatAllowed(boolean var1);
   }

   static record GameLoadCookie(RealmsClient a, GameConfig.QuickPlayData b) {
      private final RealmsClient realmsClient;
      private final GameConfig.QuickPlayData quickPlayData;

      GameLoadCookie(RealmsClient var1, GameConfig.QuickPlayData var2) {
         super();
         this.realmsClient = var1;
         this.quickPlayData = var2;
      }
   }
}
