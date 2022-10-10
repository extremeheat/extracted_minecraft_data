package net.minecraft.client;

import com.google.common.base.Joiner;
import com.google.common.collect.Lists;
import com.google.common.collect.Queues;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListenableFutureTask;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.GameProfileRepository;
import com.mojang.authlib.minecraft.MinecraftSessionService;
import com.mojang.authlib.properties.PropertyMap;
import com.mojang.authlib.yggdrasil.YggdrasilAuthenticationService;
import com.mojang.datafixers.DataFixer;
import java.io.File;
import java.net.Proxy;
import java.net.SocketAddress;
import java.nio.ByteOrder;
import java.nio.IntBuffer;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Queue;
import java.util.UUID;
import java.util.concurrent.Callable;
import java.util.concurrent.Executors;
import java.util.concurrent.FutureTask;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.audio.MusicTicker;
import net.minecraft.client.audio.SoundHandler;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.client.gui.GuiConnecting;
import net.minecraft.client.gui.GuiDirtMessageScreen;
import net.minecraft.client.gui.GuiGameOver;
import net.minecraft.client.gui.GuiIngame;
import net.minecraft.client.gui.GuiIngameMenu;
import net.minecraft.client.gui.GuiMainMenu;
import net.minecraft.client.gui.GuiMemoryErrorScreen;
import net.minecraft.client.gui.GuiMultiplayer;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiScreenLoading;
import net.minecraft.client.gui.GuiScreenWorking;
import net.minecraft.client.gui.GuiSleepMP;
import net.minecraft.client.gui.GuiWinGame;
import net.minecraft.client.gui.IGuiEventListener;
import net.minecraft.client.gui.IGuiEventListenerDeferred;
import net.minecraft.client.gui.advancements.GuiScreenAdvancements;
import net.minecraft.client.gui.chat.NarratorChatListener;
import net.minecraft.client.gui.fonts.FontResourceManager;
import net.minecraft.client.gui.inventory.GuiContainerCreative;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.client.gui.toasts.GuiToast;
import net.minecraft.client.main.GameConfiguration;
import net.minecraft.client.multiplayer.PlayerControllerMP;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.client.network.NetHandlerLoginClient;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.client.particle.ParticleManager;
import net.minecraft.client.renderer.BlockRendererDispatcher;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.FirstPersonRenderer;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.GlDebugTextUtils;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.VirtualScreen;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.chunk.RenderChunk;
import net.minecraft.client.renderer.color.BlockColors;
import net.minecraft.client.renderer.color.ItemColors;
import net.minecraft.client.renderer.debug.DebugRenderer;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.model.ModelManager;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.resources.DownloadingPackFinder;
import net.minecraft.client.resources.FoliageColorReloadListener;
import net.minecraft.client.resources.GrassColorReloadListener;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.resources.LanguageManager;
import net.minecraft.client.resources.LegacyResourcePackWrapper;
import net.minecraft.client.resources.ResourcePackInfoClient;
import net.minecraft.client.resources.SkinManager;
import net.minecraft.client.settings.CreativeSettings;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.client.shader.Framebuffer;
import net.minecraft.client.tutorial.Tutorial;
import net.minecraft.client.util.ISearchTree;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.client.util.RecipeBookClient;
import net.minecraft.client.util.SearchTree;
import net.minecraft.client.util.SearchTreeManager;
import net.minecraft.crash.CrashReport;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.crash.ReportedException;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLeashKnot;
import net.minecraft.entity.item.EntityArmorStand;
import net.minecraft.entity.item.EntityBoat;
import net.minecraft.entity.item.EntityEnderCrystal;
import net.minecraft.entity.item.EntityItemFrame;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.entity.item.EntityPainting;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.init.Bootstrap;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemSkull;
import net.minecraft.item.ItemSpawnEgg;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.RecipeManager;
import net.minecraft.nbt.INBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.network.EnumConnectionState;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.handshake.client.CPacketHandshake;
import net.minecraft.network.login.client.CPacketLoginStart;
import net.minecraft.network.play.client.CPacketPlayerDigging;
import net.minecraft.profiler.ISnooperInfo;
import net.minecraft.profiler.Profiler;
import net.minecraft.profiler.Snooper;
import net.minecraft.resources.FolderPackFinder;
import net.minecraft.resources.IReloadableResourceManager;
import net.minecraft.resources.IResourceManager;
import net.minecraft.resources.IResourcePack;
import net.minecraft.resources.ResourcePackInfo;
import net.minecraft.resources.ResourcePackList;
import net.minecraft.resources.ResourcePackType;
import net.minecraft.resources.SimpleReloadableResourceManager;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.integrated.IntegratedServer;
import net.minecraft.server.management.PlayerProfileCache;
import net.minecraft.stats.StatisticsManager;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntitySkull;
import net.minecraft.util.DefaultUncaughtExceptionHandler;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.FrameTimer;
import net.minecraft.util.IThreadListener;
import net.minecraft.util.MovementInputFromOptions;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Session;
import net.minecraft.util.Timer;
import net.minecraft.util.Util;
import net.minecraft.util.datafix.DataFixesManager;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.registry.IRegistry;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentKeybind;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.WorldSettings;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.storage.AnvilSaveConverter;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.dimension.EndDimension;
import net.minecraft.world.dimension.NetherDimension;
import net.minecraft.world.storage.ISaveFormat;
import net.minecraft.world.storage.ISaveHandler;
import net.minecraft.world.storage.WorldInfo;
import org.apache.commons.lang3.Validate;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.Version;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWErrorCallback;

public class Minecraft implements IThreadListener, ISnooperInfo, IGuiEventListenerDeferred {
   private static final Logger field_147123_G = LogManager.getLogger();
   public static final boolean field_142025_a;
   public static final ResourceLocation field_211502_b;
   public static final ResourceLocation field_71464_q;
   public static byte[] field_71444_a;
   private static int field_211120_F;
   private final File field_130070_K;
   private final PropertyMap field_181038_N;
   private final GameConfiguration.DisplayInformation field_195556_O;
   private ServerData field_71422_O;
   private TextureManager field_71446_o;
   private static Minecraft field_71432_P;
   private final DataFixer field_184131_U;
   public PlayerControllerMP field_71442_b;
   private VirtualScreen field_195557_T;
   public MainWindow field_195558_d;
   private boolean field_71434_R;
   private CrashReport field_71433_S;
   private boolean field_181541_X;
   private final Timer field_71428_T = new Timer(20.0F, 0L);
   private final Snooper field_71427_U = new Snooper("client", this, Util.func_211177_b());
   public WorldClient field_71441_e;
   public WorldRenderer field_71438_f;
   private RenderManager field_175616_W;
   private ItemRenderer field_175621_X;
   private FirstPersonRenderer field_175620_Y;
   public EntityPlayerSP field_71439_g;
   @Nullable
   private Entity field_175622_Z;
   @Nullable
   public Entity field_147125_j;
   public ParticleManager field_71452_i;
   private final SearchTreeManager field_193995_ae = new SearchTreeManager();
   private final Session field_71449_j;
   private boolean field_71445_n;
   private float field_193996_ah;
   public FontRenderer field_71466_p;
   @Nullable
   public GuiScreen field_71462_r;
   public GameRenderer field_71460_t;
   public DebugRenderer field_184132_p;
   int field_71429_W;
   @Nullable
   private IntegratedServer field_71437_Z;
   public GuiIngame field_71456_v;
   public boolean field_71454_w;
   public RayTraceResult field_71476_x;
   public GameSettings field_71474_y;
   private CreativeSettings field_191950_u;
   public MouseHelper field_71417_B;
   public KeyboardListener field_195559_v;
   public final File field_71412_D;
   private final File field_110446_Y;
   private final String field_110447_Z;
   private final String field_184130_ao;
   private final Proxy field_110453_aa;
   private ISaveFormat field_71469_aa;
   private static int field_71470_ab;
   private int field_71467_ac;
   private String field_71475_ae;
   private int field_71477_af;
   private int field_71457_ai;
   public final FrameTimer field_181542_y = new FrameTimer();
   private long field_181543_z = Util.func_211178_c();
   private final boolean field_147129_ai;
   private final boolean field_71459_aj;
   @Nullable
   private NetworkManager field_71453_ak;
   private boolean field_71455_al;
   public final Profiler field_71424_I = new Profiler();
   private IReloadableResourceManager field_110451_am;
   private final DownloadingPackFinder field_195554_ax;
   private final ResourcePackList<ResourcePackInfoClient> field_110448_aq;
   private LanguageManager field_135017_as;
   private BlockColors field_184127_aH;
   private ItemColors field_184128_aI;
   private Framebuffer field_147124_at;
   private TextureMap field_147128_au;
   private SoundHandler field_147127_av;
   private MusicTicker field_147126_aw;
   private FontResourceManager field_211501_aD;
   private final MinecraftSessionService field_152355_az;
   private SkinManager field_152350_aA;
   private final Queue<FutureTask<?>> field_152351_aB = Queues.newConcurrentLinkedQueue();
   private final Thread field_152352_aC = Thread.currentThread();
   private ModelManager field_175617_aL;
   private BlockRendererDispatcher field_175618_aM;
   private final GuiToast field_193034_aS;
   private volatile boolean field_71425_J = true;
   public String field_71426_K = "";
   public boolean field_175612_E = true;
   private long field_71419_L;
   private int field_71420_M;
   private final Tutorial field_193035_aW;
   boolean field_195555_I;
   private String field_71465_an = "root";

   public Minecraft(GameConfiguration var1) {
      super();
      this.field_195556_O = var1.field_178743_b;
      field_71432_P = this;
      this.field_71412_D = var1.field_178744_c.field_178760_a;
      this.field_110446_Y = var1.field_178744_c.field_178759_c;
      this.field_130070_K = var1.field_178744_c.field_178758_b;
      this.field_110447_Z = var1.field_178741_d.field_178755_b;
      this.field_184130_ao = var1.field_178741_d.field_187053_c;
      this.field_181038_N = var1.field_178745_a.field_181172_c;
      this.field_195554_ax = new DownloadingPackFinder(new File(this.field_71412_D, "server-resource-packs"), var1.field_178744_c.func_187052_a());
      this.field_110448_aq = new ResourcePackList((var0, var1x, var2, var3, var4, var5) -> {
         Supplier var6;
         if (var4.func_198962_b() < 4) {
            var6 = () -> {
               return new LegacyResourcePackWrapper((IResourcePack)var2.get(), LegacyResourcePackWrapper.field_211853_a);
            };
         } else {
            var6 = var2;
         }

         return new ResourcePackInfoClient(var0, var1x, var6, var3, var4, var5);
      });
      this.field_110448_aq.func_198982_a(this.field_195554_ax);
      this.field_110448_aq.func_198982_a(new FolderPackFinder(this.field_130070_K));
      this.field_110453_aa = var1.field_178745_a.field_178751_c == null ? Proxy.NO_PROXY : var1.field_178745_a.field_178751_c;
      this.field_152355_az = (new YggdrasilAuthenticationService(this.field_110453_aa, UUID.randomUUID().toString())).createMinecraftSessionService();
      this.field_71449_j = var1.field_178745_a.field_178752_a;
      field_147123_G.info("Setting user: {}", this.field_71449_j.func_111285_a());
      field_147123_G.debug("(Session ID is {})", this.field_71449_j.func_111286_b());
      this.field_71459_aj = var1.field_178741_d.field_178756_a;
      this.field_147129_ai = func_147122_X();
      this.field_71437_Z = null;
      if (var1.field_178742_e.field_178754_a != null) {
         this.field_71475_ae = var1.field_178742_e.field_178754_a;
         this.field_71477_af = var1.field_178742_e.field_178753_b;
      }

      Bootstrap.func_151354_b();
      TextComponentKeybind.field_193637_b = KeyBinding::func_193626_b;
      this.field_184131_U = DataFixesManager.func_210901_a();
      this.field_193034_aS = new GuiToast(this);
      this.field_193035_aW = new Tutorial(this);
   }

   public void func_99999_d() {
      this.field_71425_J = true;

      CrashReport var2;
      try {
         this.func_71384_a();
      } catch (Throwable var10) {
         var2 = CrashReport.func_85055_a(var10, "Initializing game");
         var2.func_85058_a("Initialization");
         this.func_71377_b(this.func_71396_d(var2));
         return;
      }

      try {
         try {
            while(this.field_71425_J) {
               if (this.field_71434_R && this.field_71433_S != null) {
                  this.func_71377_b(this.field_71433_S);
                  return;
               }

               try {
                  this.func_195542_b(true);
               } catch (OutOfMemoryError var9) {
                  this.func_71398_f();
                  this.func_147108_a(new GuiMemoryErrorScreen());
                  System.gc();
               }
            }

            return;
         } catch (ReportedException var11) {
            this.func_71396_d(var11.func_71575_a());
            this.func_71398_f();
            field_147123_G.fatal("Reported exception thrown!", var11);
            this.func_71377_b(var11.func_71575_a());
         } catch (Throwable var12) {
            var2 = this.func_71396_d(new CrashReport("Unexpected error", var12));
            this.func_71398_f();
            field_147123_G.fatal("Unreported exception thrown!", var12);
            this.func_71377_b(var2);
         }

      } finally {
         this.func_71405_e();
      }
   }

   private void func_71384_a() {
      this.field_71474_y = new GameSettings(this, this.field_71412_D);
      this.field_191950_u = new CreativeSettings(this.field_71412_D, this.field_184131_U);
      this.func_71389_H();
      field_147123_G.info("LWJGL Version: {}", Version.getVersion());
      GameConfiguration.DisplayInformation var1 = this.field_195556_O;
      if (this.field_71474_y.field_92119_C > 0 && this.field_71474_y.field_92118_B > 0) {
         var1 = new GameConfiguration.DisplayInformation(this.field_71474_y.field_92118_B, this.field_71474_y.field_92119_C, var1.field_199045_c, var1.field_199046_d, var1.field_178763_c);
      }

      this.func_211118_al();
      this.field_195557_T = new VirtualScreen(this);
      this.field_195558_d = this.field_195557_T.func_198053_a(var1, this.field_71474_y.field_198019_u);
      OpenGlHelper.func_77474_a();
      GlDebugTextUtils.func_209247_b(this.field_71474_y.field_209231_W);
      this.field_147124_at = new Framebuffer(this.field_195558_d.func_198109_k(), this.field_195558_d.func_198091_l(), true);
      this.field_147124_at.func_147604_a(0.0F, 0.0F, 0.0F, 0.0F);
      this.field_110451_am = new SimpleReloadableResourceManager(ResourcePackType.CLIENT_RESOURCES);
      this.field_135017_as = new LanguageManager(this.field_71474_y.field_74363_ab);
      this.field_110451_am.func_199006_a(this.field_135017_as);
      this.field_71474_y.func_198017_a(this.field_110448_aq);
      this.func_110436_a();
      this.field_71446_o = new TextureManager(this.field_110451_am);
      this.field_110451_am.func_199006_a(this.field_71446_o);
      this.field_195558_d.func_198098_h();
      this.func_147108_a(new GuiScreenLoading());
      this.func_195547_ap();
      this.field_152350_aA = new SkinManager(this.field_71446_o, new File(this.field_110446_Y, "skins"), this.field_152355_az);
      this.field_71469_aa = new AnvilSaveConverter(this.field_71412_D.toPath().resolve("saves"), this.field_71412_D.toPath().resolve("backups"), this.field_184131_U);
      this.field_147127_av = new SoundHandler(this.field_110451_am, this.field_71474_y);
      this.field_110451_am.func_199006_a(this.field_147127_av);
      this.field_147126_aw = new MusicTicker(this);
      this.field_211501_aD = new FontResourceManager(this.field_71446_o, this.func_211821_e());
      this.field_110451_am.func_199006_a(this.field_211501_aD);
      this.field_71466_p = this.field_211501_aD.func_211504_a(field_211502_b);
      if (this.field_71474_y.field_74363_ab != null) {
         this.field_71466_p.func_78275_b(this.field_135017_as.func_135044_b());
      }

      this.field_110451_am.func_199006_a(new GrassColorReloadListener());
      this.field_110451_am.func_199006_a(new FoliageColorReloadListener());
      this.field_195558_d.func_198076_a("Startup");
      GlStateManager.func_179098_w();
      GlStateManager.func_179103_j(7425);
      GlStateManager.func_179151_a(1.0D);
      GlStateManager.func_179126_j();
      GlStateManager.func_179143_c(515);
      GlStateManager.func_179141_d();
      GlStateManager.func_179092_a(516, 0.1F);
      GlStateManager.func_187407_a(GlStateManager.CullFace.BACK);
      GlStateManager.func_179128_n(5889);
      GlStateManager.func_179096_D();
      GlStateManager.func_179128_n(5888);
      this.field_195558_d.func_198076_a("Post startup");
      this.field_147128_au = new TextureMap("textures");
      this.field_147128_au.func_147633_a(this.field_71474_y.field_151442_I);
      this.field_71446_o.func_110580_a(TextureMap.field_110575_b, this.field_147128_au);
      this.field_71446_o.func_110577_a(TextureMap.field_110575_b);
      this.field_147128_au.func_174937_a(false, this.field_71474_y.field_151442_I > 0);
      this.field_175617_aL = new ModelManager(this.field_147128_au);
      this.field_110451_am.func_199006_a(this.field_175617_aL);
      this.field_184127_aH = BlockColors.func_186723_a();
      this.field_184128_aI = ItemColors.func_186729_a(this.field_184127_aH);
      this.field_175621_X = new ItemRenderer(this.field_71446_o, this.field_175617_aL, this.field_184128_aI);
      this.field_175616_W = new RenderManager(this.field_71446_o, this.field_175621_X);
      this.field_175620_Y = new FirstPersonRenderer(this);
      this.field_110451_am.func_199006_a(this.field_175621_X);
      this.field_71460_t = new GameRenderer(this, this.field_110451_am);
      this.field_110451_am.func_199006_a(this.field_71460_t);
      this.field_175618_aM = new BlockRendererDispatcher(this.field_175617_aL.func_174954_c(), this.field_184127_aH);
      this.field_110451_am.func_199006_a(this.field_175618_aM);
      this.field_71438_f = new WorldRenderer(this);
      this.field_110451_am.func_199006_a(this.field_71438_f);
      this.func_193986_ar();
      this.field_110451_am.func_199006_a(this.field_193995_ae);
      GlStateManager.func_179083_b(0, 0, this.field_195558_d.func_198109_k(), this.field_195558_d.func_198091_l());
      this.field_71452_i = new ParticleManager(this.field_71441_e, this.field_71446_o);
      this.field_71456_v = new GuiIngame(this);
      if (this.field_71475_ae != null) {
         this.func_147108_a(new GuiConnecting(new GuiMainMenu(), this, this.field_71475_ae, this.field_71477_af));
      } else {
         this.func_147108_a(new GuiMainMenu());
      }

      this.field_184132_p = new DebugRenderer(this);
      GLFW.glfwSetErrorCallback(this::func_195545_a).free();
      if (this.field_71474_y.field_74353_u && !this.field_195558_d.func_198113_j()) {
         this.field_195558_d.func_198077_g();
      }

      this.field_195558_d.func_209548_c();
      this.field_195558_d.func_198112_b();
      this.field_71438_f.func_174966_b();
   }

   private void func_211118_al() {
      MainWindow.func_211162_a((var0, var1x) -> {
         throw new IllegalStateException(String.format("GLFW error before init: [0x%X]%s", var0, var1x));
      });
      ArrayList var1 = Lists.newArrayList();
      GLFWErrorCallback var2 = GLFW.glfwSetErrorCallback((var1x, var2x) -> {
         var1.add(String.format("GLFW error during init: [0x%X]%s", var1x, var2x));
      });
      if (!GLFW.glfwInit()) {
         throw new IllegalStateException("Failed to initialize GLFW, errors: " + Joiner.on(",").join(var1));
      } else {
         Util.field_211180_a = () -> {
            return (long)(GLFW.glfwGetTime() * 1.0E9D);
         };
         Iterator var3 = var1.iterator();

         while(var3.hasNext()) {
            String var4 = (String)var3.next();
            field_147123_G.error("GLFW error collected during initialization: {}", var4);
         }

         GLFW.glfwSetErrorCallback(var2).free();
      }
   }

   private void func_193986_ar() {
      SearchTree var1 = new SearchTree((var0) -> {
         return (List)var0.func_82840_a((EntityPlayer)null, ITooltipFlag.TooltipFlags.NORMAL).stream().map((var0x) -> {
            return TextFormatting.func_110646_a(var0x.getString()).trim();
         }).filter((var0x) -> {
            return !var0x.isEmpty();
         }).collect(Collectors.toList());
      }, (var0) -> {
         return Collections.singleton(IRegistry.field_212630_s.func_177774_c(var0.func_77973_b()));
      });
      NonNullList var2 = NonNullList.func_191196_a();
      Iterator var3 = IRegistry.field_212630_s.iterator();

      while(var3.hasNext()) {
         Item var4 = (Item)var3.next();
         var4.func_150895_a(ItemGroup.field_78027_g, var2);
      }

      var2.forEach(var1::func_194043_a);
      SearchTree var5 = new SearchTree((var0) -> {
         return (List)var0.func_192711_b().stream().flatMap((var0x) -> {
            return var0x.func_77571_b().func_82840_a((EntityPlayer)null, ITooltipFlag.TooltipFlags.NORMAL).stream();
         }).map((var0x) -> {
            return TextFormatting.func_110646_a(var0x.getString()).trim();
         }).filter((var0x) -> {
            return !var0x.isEmpty();
         }).collect(Collectors.toList());
      }, (var0) -> {
         return (List)var0.func_192711_b().stream().map((var0x) -> {
            return IRegistry.field_212630_s.func_177774_c(var0x.func_77571_b().func_77973_b());
         }).collect(Collectors.toList());
      });
      this.field_193995_ae.func_194009_a(SearchTreeManager.field_194011_a, var1);
      this.field_193995_ae.func_194009_a(SearchTreeManager.field_194012_b, var5);
   }

   private void func_195545_a(int var1, long var2) {
      this.field_71474_y.field_74352_v = false;
      this.field_71474_y.func_74303_b();
   }

   private static boolean func_147122_X() {
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

   public Framebuffer func_147110_a() {
      return this.field_147124_at;
   }

   public String func_175600_c() {
      return this.field_110447_Z;
   }

   public String func_184123_d() {
      return this.field_184130_ao;
   }

   private void func_71389_H() {
      Thread var1 = new Thread("Timer hack thread") {
         public void run() {
            while(Minecraft.this.field_71425_J) {
               try {
                  Thread.sleep(2147483647L);
               } catch (InterruptedException var2) {
               }
            }

         }
      };
      var1.setDaemon(true);
      var1.setUncaughtExceptionHandler(new DefaultUncaughtExceptionHandler(field_147123_G));
      var1.start();
   }

   public void func_71404_a(CrashReport var1) {
      this.field_71434_R = true;
      this.field_71433_S = var1;
   }

   public void func_71377_b(CrashReport var1) {
      File var2 = new File(func_71410_x().field_71412_D, "crash-reports");
      File var3 = new File(var2, "crash-" + (new SimpleDateFormat("yyyy-MM-dd_HH.mm.ss")).format(new Date()) + "-client.txt");
      Bootstrap.func_179870_a(var1.func_71502_e());
      if (var1.func_71497_f() != null) {
         Bootstrap.func_179870_a("#@!@# Game crashed! Crash report saved to: #@!@# " + var1.func_71497_f());
         System.exit(-1);
      } else if (var1.func_147149_a(var3)) {
         Bootstrap.func_179870_a("#@!@# Game crashed! Crash report saved to: #@!@# " + var3.getAbsolutePath());
         System.exit(-1);
      } else {
         Bootstrap.func_179870_a("#@?@# Game crashed! Crash report could not be saved. #@?@#");
         System.exit(-2);
      }

   }

   public boolean func_211821_e() {
      return this.field_71474_y.field_211842_aO;
   }

   public void func_110436_a() {
      this.field_110448_aq.func_198983_a();
      List var1 = (List)this.field_110448_aq.func_198980_d().stream().map(ResourcePackInfo::func_195796_e).collect(Collectors.toList());
      if (this.field_71437_Z != null) {
         this.field_71437_Z.func_193031_aM();
      }

      try {
         this.field_110451_am.func_199005_a(var1);
      } catch (RuntimeException var4) {
         field_147123_G.info("Caught error stitching, removing all assigned resourcepacks", var4);
         this.field_110448_aq.func_198985_a(Collections.emptyList());
         List var3 = (List)this.field_110448_aq.func_198980_d().stream().map(ResourcePackInfo::func_195796_e).collect(Collectors.toList());
         this.field_110451_am.func_199005_a(var3);
         this.field_71474_y.field_151453_l.clear();
         this.field_71474_y.field_183018_l.clear();
         this.field_71474_y.func_74303_b();
      }

      this.field_135017_as.func_135043_a(var1);
      if (this.field_71438_f != null) {
         this.field_71438_f.func_72712_a();
      }

   }

   private void func_195547_ap() {
      this.field_195558_d.func_198094_a();
      this.field_71462_r.func_73863_a(0, 0, 0.0F);
      this.field_195558_d.func_198086_a(false);
   }

   public void func_181536_a(int var1, int var2, int var3, int var4, int var5, int var6, int var7, int var8, int var9, int var10) {
      BufferBuilder var11 = Tessellator.func_178181_a().func_178180_c();
      var11.func_181668_a(7, DefaultVertexFormats.field_181709_i);
      float var12 = 0.00390625F;
      float var13 = 0.00390625F;
      var11.func_181662_b((double)var1, (double)(var2 + var6), 0.0D).func_187315_a((double)((float)var3 * 0.00390625F), (double)((float)(var4 + var6) * 0.00390625F)).func_181669_b(var7, var8, var9, var10).func_181675_d();
      var11.func_181662_b((double)(var1 + var5), (double)(var2 + var6), 0.0D).func_187315_a((double)((float)(var3 + var5) * 0.00390625F), (double)((float)(var4 + var6) * 0.00390625F)).func_181669_b(var7, var8, var9, var10).func_181675_d();
      var11.func_181662_b((double)(var1 + var5), (double)var2, 0.0D).func_187315_a((double)((float)(var3 + var5) * 0.00390625F), (double)((float)var4 * 0.00390625F)).func_181669_b(var7, var8, var9, var10).func_181675_d();
      var11.func_181662_b((double)var1, (double)var2, 0.0D).func_187315_a((double)((float)var3 * 0.00390625F), (double)((float)var4 * 0.00390625F)).func_181669_b(var7, var8, var9, var10).func_181675_d();
      Tessellator.func_178181_a().func_78381_a();
   }

   public ISaveFormat func_71359_d() {
      return this.field_71469_aa;
   }

   @Nullable
   public IGuiEventListener getFocused() {
      return this.field_71462_r;
   }

   public void func_147108_a(@Nullable GuiScreen var1) {
      if (this.field_71462_r != null) {
         this.field_71462_r.func_146281_b();
      }

      if (var1 == null && this.field_71441_e == null) {
         var1 = new GuiMainMenu();
      } else if (var1 == null && this.field_71439_g.func_110143_aJ() <= 0.0F) {
         var1 = new GuiGameOver((ITextComponent)null);
      }

      if (var1 instanceof GuiMainMenu || var1 instanceof GuiMultiplayer) {
         this.field_71474_y.field_74330_P = false;
         this.field_71456_v.func_146158_b().func_146231_a(true);
      }

      this.field_71462_r = (GuiScreen)var1;
      if (var1 != null) {
         this.field_71417_B.func_198032_j();
         KeyBinding.func_74506_a();
         ((GuiScreen)var1).func_146280_a(this, this.field_195558_d.func_198107_o(), this.field_195558_d.func_198087_p());
         this.field_71454_w = false;
      } else {
         this.field_147127_av.func_147687_e();
         this.field_71417_B.func_198034_i();
      }

   }

   public void func_71405_e() {
      try {
         field_147123_G.info("Stopping!");

         try {
            this.func_71403_a((WorldClient)null);
         } catch (Throwable var5) {
         }

         if (this.field_71462_r != null) {
            this.field_71462_r.func_146281_b();
         }

         this.field_147128_au.func_195419_g();
         this.field_71466_p.close();
         this.field_71460_t.close();
         this.field_71438_f.close();
         this.field_147127_av.func_147685_d();
      } finally {
         this.field_195557_T.close();
         this.field_195558_d.close();
         if (!this.field_71434_R) {
            System.exit(0);
         }

      }

      System.gc();
   }

   private void func_195542_b(boolean var1) {
      this.field_195558_d.func_198076_a("Pre render");
      long var2 = Util.func_211178_c();
      this.field_71424_I.func_76320_a("root");
      if (GLFW.glfwWindowShouldClose(this.field_195558_d.func_198092_i())) {
         this.func_71400_g();
      }

      if (var1) {
         this.field_71428_T.func_74275_a(Util.func_211177_b());
         this.field_71424_I.func_76320_a("scheduledExecutables");

         FutureTask var4;
         while((var4 = (FutureTask)this.field_152351_aB.poll()) != null) {
            Util.func_181617_a(var4, field_147123_G);
         }

         this.field_71424_I.func_76319_b();
      }

      long var11 = Util.func_211178_c();
      if (var1) {
         this.field_71424_I.func_76320_a("tick");

         for(int var6 = 0; var6 < Math.min(10, this.field_71428_T.field_74280_b); ++var6) {
            this.func_71407_l();
         }
      }

      this.field_71417_B.func_198028_a();
      this.field_195558_d.func_198076_a("Render");
      GLFW.glfwPollEvents();
      long var12 = Util.func_211178_c() - var11;
      this.field_71424_I.func_76318_c("sound");
      this.field_147127_av.func_147691_a(this.field_71439_g, this.field_71428_T.field_194147_b);
      this.field_71424_I.func_76319_b();
      this.field_71424_I.func_76320_a("render");
      GlStateManager.func_179094_E();
      GlStateManager.func_179086_m(16640);
      this.field_147124_at.func_147610_a(true);
      this.field_71424_I.func_76320_a("display");
      GlStateManager.func_179098_w();
      this.field_71424_I.func_76319_b();
      if (!this.field_71454_w) {
         this.field_71424_I.func_76318_c("gameRenderer");
         this.field_71460_t.func_195458_a(this.field_71445_n ? this.field_193996_ah : this.field_71428_T.field_194147_b, var2, var1);
         this.field_71424_I.func_76318_c("toasts");
         this.field_193034_aS.func_195625_a();
         this.field_71424_I.func_76319_b();
      }

      this.field_71424_I.func_76319_b();
      if (this.field_71474_y.field_74330_P && this.field_71474_y.field_74329_Q && !this.field_71474_y.field_74319_N) {
         this.field_71424_I.func_199095_a(this.field_71428_T.field_74280_b);
         this.func_203410_as();
      } else {
         this.field_71424_I.func_199098_b();
      }

      this.field_147124_at.func_147609_e();
      GlStateManager.func_179121_F();
      GlStateManager.func_179094_E();
      this.field_147124_at.func_147615_c(this.field_195558_d.func_198109_k(), this.field_195558_d.func_198091_l());
      GlStateManager.func_179121_F();
      GlStateManager.func_179094_E();
      this.field_71460_t.func_152430_c(this.field_71428_T.field_194147_b);
      GlStateManager.func_179121_F();
      this.field_71424_I.func_76320_a("root");
      this.field_195558_d.func_198086_a(true);
      Thread.yield();
      this.field_195558_d.func_198076_a("Post render");
      ++this.field_71420_M;
      boolean var8 = this.func_71356_B() && this.field_71462_r != null && this.field_71462_r.func_73868_f() && !this.field_71437_Z.func_71344_c();
      if (this.field_71445_n != var8) {
         if (this.field_71445_n) {
            this.field_193996_ah = this.field_71428_T.field_194147_b;
         } else {
            this.field_71428_T.field_194147_b = this.field_193996_ah;
         }

         this.field_71445_n = var8;
      }

      long var9 = Util.func_211178_c();
      this.field_181542_y.func_181747_a(var9 - this.field_181543_z);
      this.field_181543_z = var9;

      while(Util.func_211177_b() >= this.field_71419_L + 1000L) {
         field_71470_ab = this.field_71420_M;
         this.field_71426_K = String.format("%d fps (%d chunk update%s) T: %s%s%s%s%s", field_71470_ab, RenderChunk.field_178592_a, RenderChunk.field_178592_a == 1 ? "" : "s", (double)this.field_71474_y.field_74350_i == GameSettings.Options.FRAMERATE_LIMIT.func_198009_f() ? "inf" : this.field_71474_y.field_74350_i, this.field_71474_y.field_74352_v ? " vsync" : "", this.field_71474_y.field_74347_j ? "" : " fast", this.field_71474_y.field_74345_l == 0 ? "" : (this.field_71474_y.field_74345_l == 1 ? " fast-clouds" : " fancy-clouds"), OpenGlHelper.func_176075_f() ? " vbo" : "");
         RenderChunk.field_178592_a = 0;
         this.field_71419_L += 1000L;
         this.field_71420_M = 0;
         this.field_71427_U.func_76471_b();
         if (!this.field_71427_U.func_76468_d()) {
            this.field_71427_U.func_76463_a();
         }
      }

      this.field_71424_I.func_76319_b();
   }

   public void func_71398_f() {
      try {
         field_71444_a = new byte[0];
         this.field_71438_f.func_72728_f();
      } catch (Throwable var3) {
      }

      try {
         System.gc();
         this.func_205055_a((WorldClient)null, new GuiDirtMessageScreen(I18n.func_135052_a("menu.savingLevel")));
      } catch (Throwable var2) {
      }

      System.gc();
   }

   void func_71383_b(int var1) {
      List var2 = this.field_71424_I.func_76321_b(this.field_71465_an);
      if (!var2.isEmpty()) {
         Profiler.Result var3 = (Profiler.Result)var2.remove(0);
         if (var1 == 0) {
            if (!var3.field_76331_c.isEmpty()) {
               int var4 = this.field_71465_an.lastIndexOf(46);
               if (var4 >= 0) {
                  this.field_71465_an = this.field_71465_an.substring(0, var4);
               }
            }
         } else {
            --var1;
            if (var1 < var2.size() && !"unspecified".equals(((Profiler.Result)var2.get(var1)).field_76331_c)) {
               if (!this.field_71465_an.isEmpty()) {
                  this.field_71465_an = this.field_71465_an + ".";
               }

               this.field_71465_an = this.field_71465_an + ((Profiler.Result)var2.get(var1)).field_76331_c;
            }
         }

      }
   }

   private void func_203410_as() {
      if (this.field_71424_I.func_199094_a()) {
         List var1 = this.field_71424_I.func_76321_b(this.field_71465_an);
         Profiler.Result var2 = (Profiler.Result)var1.remove(0);
         GlStateManager.func_179086_m(256);
         GlStateManager.func_179128_n(5889);
         GlStateManager.func_179142_g();
         GlStateManager.func_179096_D();
         GlStateManager.func_179130_a(0.0D, (double)this.field_195558_d.func_198109_k(), (double)this.field_195558_d.func_198091_l(), 0.0D, 1000.0D, 3000.0D);
         GlStateManager.func_179128_n(5888);
         GlStateManager.func_179096_D();
         GlStateManager.func_179109_b(0.0F, 0.0F, -2000.0F);
         GlStateManager.func_187441_d(1.0F);
         GlStateManager.func_179090_x();
         Tessellator var3 = Tessellator.func_178181_a();
         BufferBuilder var4 = var3.func_178180_c();
         boolean var5 = true;
         int var6 = this.field_195558_d.func_198109_k() - 160 - 10;
         int var7 = this.field_195558_d.func_198091_l() - 320;
         GlStateManager.func_179147_l();
         var4.func_181668_a(7, DefaultVertexFormats.field_181706_f);
         var4.func_181662_b((double)((float)var6 - 176.0F), (double)((float)var7 - 96.0F - 16.0F), 0.0D).func_181669_b(200, 0, 0, 0).func_181675_d();
         var4.func_181662_b((double)((float)var6 - 176.0F), (double)(var7 + 320), 0.0D).func_181669_b(200, 0, 0, 0).func_181675_d();
         var4.func_181662_b((double)((float)var6 + 176.0F), (double)(var7 + 320), 0.0D).func_181669_b(200, 0, 0, 0).func_181675_d();
         var4.func_181662_b((double)((float)var6 + 176.0F), (double)((float)var7 - 96.0F - 16.0F), 0.0D).func_181669_b(200, 0, 0, 0).func_181675_d();
         var3.func_78381_a();
         GlStateManager.func_179084_k();
         double var8 = 0.0D;

         int var12;
         for(int var10 = 0; var10 < var1.size(); ++var10) {
            Profiler.Result var11 = (Profiler.Result)var1.get(var10);
            var12 = MathHelper.func_76128_c(var11.field_76332_a / 4.0D) + 1;
            var4.func_181668_a(6, DefaultVertexFormats.field_181706_f);
            int var13 = var11.func_76329_a();
            int var14 = var13 >> 16 & 255;
            int var15 = var13 >> 8 & 255;
            int var16 = var13 & 255;
            var4.func_181662_b((double)var6, (double)var7, 0.0D).func_181669_b(var14, var15, var16, 255).func_181675_d();

            int var17;
            float var18;
            float var19;
            float var20;
            for(var17 = var12; var17 >= 0; --var17) {
               var18 = (float)((var8 + var11.field_76332_a * (double)var17 / (double)var12) * 6.2831854820251465D / 100.0D);
               var19 = MathHelper.func_76126_a(var18) * 160.0F;
               var20 = MathHelper.func_76134_b(var18) * 160.0F * 0.5F;
               var4.func_181662_b((double)((float)var6 + var19), (double)((float)var7 - var20), 0.0D).func_181669_b(var14, var15, var16, 255).func_181675_d();
            }

            var3.func_78381_a();
            var4.func_181668_a(5, DefaultVertexFormats.field_181706_f);

            for(var17 = var12; var17 >= 0; --var17) {
               var18 = (float)((var8 + var11.field_76332_a * (double)var17 / (double)var12) * 6.2831854820251465D / 100.0D);
               var19 = MathHelper.func_76126_a(var18) * 160.0F;
               var20 = MathHelper.func_76134_b(var18) * 160.0F * 0.5F;
               var4.func_181662_b((double)((float)var6 + var19), (double)((float)var7 - var20), 0.0D).func_181669_b(var14 >> 1, var15 >> 1, var16 >> 1, 255).func_181675_d();
               var4.func_181662_b((double)((float)var6 + var19), (double)((float)var7 - var20 + 10.0F), 0.0D).func_181669_b(var14 >> 1, var15 >> 1, var16 >> 1, 255).func_181675_d();
            }

            var3.func_78381_a();
            var8 += var11.field_76332_a;
         }

         DecimalFormat var21 = new DecimalFormat("##0.00");
         var21.setDecimalFormatSymbols(DecimalFormatSymbols.getInstance(Locale.ROOT));
         GlStateManager.func_179098_w();
         String var22 = "";
         if (!"unspecified".equals(var2.field_76331_c)) {
            var22 = var22 + "[0] ";
         }

         if (var2.field_76331_c.isEmpty()) {
            var22 = var22 + "ROOT ";
         } else {
            var22 = var22 + var2.field_76331_c + ' ';
         }

         var12 = 16777215;
         this.field_71466_p.func_175063_a(var22, (float)(var6 - 160), (float)(var7 - 80 - 16), 16777215);
         var22 = var21.format(var2.field_76330_b) + "%";
         this.field_71466_p.func_175063_a(var22, (float)(var6 + 160 - this.field_71466_p.func_78256_a(var22)), (float)(var7 - 80 - 16), 16777215);

         for(int var25 = 0; var25 < var1.size(); ++var25) {
            Profiler.Result var23 = (Profiler.Result)var1.get(var25);
            StringBuilder var24 = new StringBuilder();
            if ("unspecified".equals(var23.field_76331_c)) {
               var24.append("[?] ");
            } else {
               var24.append("[").append(var25 + 1).append("] ");
            }

            String var26 = var24.append(var23.field_76331_c).toString();
            this.field_71466_p.func_175063_a(var26, (float)(var6 - 160), (float)(var7 + 80 + var25 * 8 + 20), var23.func_76329_a());
            var26 = var21.format(var23.field_76332_a) + "%";
            this.field_71466_p.func_175063_a(var26, (float)(var6 + 160 - 50 - this.field_71466_p.func_78256_a(var26)), (float)(var7 + 80 + var25 * 8 + 20), var23.func_76329_a());
            var26 = var21.format(var23.field_76330_b) + "%";
            this.field_71466_p.func_175063_a(var26, (float)(var6 + 160 - this.field_71466_p.func_78256_a(var26)), (float)(var7 + 80 + var25 * 8 + 20), var23.func_76329_a());
         }

      }
   }

   public void func_71400_g() {
      this.field_71425_J = false;
   }

   public void func_71385_j() {
      if (this.field_71462_r == null) {
         this.func_147108_a(new GuiIngameMenu());
         if (this.func_71356_B() && !this.field_71437_Z.func_71344_c()) {
            this.field_147127_av.func_147689_b();
         }

      }
   }

   private void func_147115_a(boolean var1) {
      if (!var1) {
         this.field_71429_W = 0;
      }

      if (this.field_71429_W <= 0 && !this.field_71439_g.func_184587_cr()) {
         if (var1 && this.field_71476_x != null && this.field_71476_x.field_72313_a == RayTraceResult.Type.BLOCK) {
            BlockPos var2 = this.field_71476_x.func_178782_a();
            if (!this.field_71441_e.func_180495_p(var2).func_196958_f() && this.field_71442_b.func_180512_c(var2, this.field_71476_x.field_178784_b)) {
               this.field_71452_i.func_180532_a(var2, this.field_71476_x.field_178784_b);
               this.field_71439_g.func_184609_a(EnumHand.MAIN_HAND);
            }

         } else {
            this.field_71442_b.func_78767_c();
         }
      }
   }

   private void func_147116_af() {
      if (this.field_71429_W <= 0) {
         if (this.field_71476_x == null) {
            field_147123_G.error("Null returned as 'hitResult', this shouldn't happen!");
            if (this.field_71442_b.func_78762_g()) {
               this.field_71429_W = 10;
            }

         } else if (!this.field_71439_g.func_184838_M()) {
            switch(this.field_71476_x.field_72313_a) {
            case ENTITY:
               this.field_71442_b.func_78764_a(this.field_71439_g, this.field_71476_x.field_72308_g);
               break;
            case BLOCK:
               BlockPos var1 = this.field_71476_x.func_178782_a();
               if (!this.field_71441_e.func_180495_p(var1).func_196958_f()) {
                  this.field_71442_b.func_180511_b(var1, this.field_71476_x.field_178784_b);
                  break;
               }
            case MISS:
               if (this.field_71442_b.func_78762_g()) {
                  this.field_71429_W = 10;
               }

               this.field_71439_g.func_184821_cY();
            }

            this.field_71439_g.func_184609_a(EnumHand.MAIN_HAND);
         }
      }
   }

   private void func_147121_ag() {
      if (!this.field_71442_b.func_181040_m()) {
         this.field_71467_ac = 4;
         if (!this.field_71439_g.func_184838_M()) {
            if (this.field_71476_x == null) {
               field_147123_G.warn("Null returned as 'hitResult', this shouldn't happen!");
            }

            EnumHand[] var1 = EnumHand.values();
            int var2 = var1.length;

            for(int var3 = 0; var3 < var2; ++var3) {
               EnumHand var4 = var1[var3];
               ItemStack var5 = this.field_71439_g.func_184586_b(var4);
               if (this.field_71476_x != null) {
                  switch(this.field_71476_x.field_72313_a) {
                  case ENTITY:
                     if (this.field_71442_b.func_187102_a(this.field_71439_g, this.field_71476_x.field_72308_g, this.field_71476_x, var4) == EnumActionResult.SUCCESS) {
                        return;
                     }

                     if (this.field_71442_b.func_187097_a(this.field_71439_g, this.field_71476_x.field_72308_g, var4) == EnumActionResult.SUCCESS) {
                        return;
                     }
                     break;
                  case BLOCK:
                     BlockPos var6 = this.field_71476_x.func_178782_a();
                     if (!this.field_71441_e.func_180495_p(var6).func_196958_f()) {
                        int var7 = var5.func_190916_E();
                        EnumActionResult var8 = this.field_71442_b.func_187099_a(this.field_71439_g, this.field_71441_e, var6, this.field_71476_x.field_178784_b, this.field_71476_x.field_72307_f, var4);
                        if (var8 == EnumActionResult.SUCCESS) {
                           this.field_71439_g.func_184609_a(var4);
                           if (!var5.func_190926_b() && (var5.func_190916_E() != var7 || this.field_71442_b.func_78758_h())) {
                              this.field_71460_t.field_78516_c.func_187460_a(var4);
                           }

                           return;
                        }

                        if (var8 == EnumActionResult.FAIL) {
                           return;
                        }
                     }
                  }
               }

               if (!var5.func_190926_b() && this.field_71442_b.func_187101_a(this.field_71439_g, this.field_71441_e, var4) == EnumActionResult.SUCCESS) {
                  this.field_71460_t.field_78516_c.func_187460_a(var4);
                  return;
               }
            }

         }
      }
   }

   public MusicTicker func_181535_r() {
      return this.field_147126_aw;
   }

   public void func_71407_l() {
      if (this.field_71467_ac > 0) {
         --this.field_71467_ac;
      }

      this.field_71424_I.func_76320_a("gui");
      if (!this.field_71445_n) {
         this.field_71456_v.func_73831_a();
      }

      this.field_71424_I.func_76319_b();
      this.field_71460_t.func_78473_a(1.0F);
      this.field_193035_aW.func_193297_a(this.field_71441_e, this.field_71476_x);
      this.field_71424_I.func_76320_a("gameMode");
      if (!this.field_71445_n && this.field_71441_e != null) {
         this.field_71442_b.func_78765_e();
      }

      this.field_71424_I.func_76318_c("textures");
      if (this.field_71441_e != null) {
         this.field_71446_o.func_110550_d();
      }

      if (this.field_71462_r == null && this.field_71439_g != null) {
         if (this.field_71439_g.func_110143_aJ() <= 0.0F && !(this.field_71462_r instanceof GuiGameOver)) {
            this.func_147108_a((GuiScreen)null);
         } else if (this.field_71439_g.func_70608_bn() && this.field_71441_e != null) {
            this.func_147108_a(new GuiSleepMP());
         }
      } else if (this.field_71462_r != null && this.field_71462_r instanceof GuiSleepMP && !this.field_71439_g.func_70608_bn()) {
         this.func_147108_a((GuiScreen)null);
      }

      if (this.field_71462_r != null) {
         this.field_71429_W = 10000;
      }

      if (this.field_71462_r != null) {
         GuiScreen.func_195121_a(() -> {
            this.field_71462_r.func_73876_c();
         }, "Ticking screen", this.field_71462_r.getClass().getCanonicalName());
      }

      if (this.field_71462_r == null || this.field_71462_r.field_146291_p) {
         this.field_71424_I.func_76318_c("GLFW events");
         GLFW.glfwPollEvents();
         this.func_184117_aA();
         if (this.field_71429_W > 0) {
            --this.field_71429_W;
         }
      }

      if (this.field_71441_e != null) {
         if (this.field_71439_g != null) {
            ++this.field_71457_ai;
            if (this.field_71457_ai == 30) {
               this.field_71457_ai = 0;
               this.field_71441_e.func_72897_h(this.field_71439_g);
            }
         }

         this.field_71424_I.func_76318_c("gameRenderer");
         if (!this.field_71445_n) {
            this.field_71460_t.func_78464_a();
         }

         this.field_71424_I.func_76318_c("levelRenderer");
         if (!this.field_71445_n) {
            this.field_71438_f.func_72734_e();
         }

         this.field_71424_I.func_76318_c("level");
         if (!this.field_71445_n) {
            if (this.field_71441_e.func_175658_ac() > 0) {
               this.field_71441_e.func_175702_c(this.field_71441_e.func_175658_ac() - 1);
            }

            this.field_71441_e.func_72939_s();
         }
      } else if (this.field_71460_t.func_147702_a()) {
         this.field_71460_t.func_181022_b();
      }

      if (!this.field_71445_n) {
         this.field_147126_aw.func_73660_a();
         this.field_147127_av.func_73660_a();
      }

      if (this.field_71441_e != null) {
         if (!this.field_71445_n) {
            this.field_71441_e.func_72891_a(this.field_71441_e.func_175659_aa() != EnumDifficulty.PEACEFUL, true);
            this.field_193035_aW.func_193303_d();

            try {
               this.field_71441_e.func_72835_b(() -> {
                  return true;
               });
            } catch (Throwable var4) {
               CrashReport var2 = CrashReport.func_85055_a(var4, "Exception in world tick");
               if (this.field_71441_e == null) {
                  CrashReportCategory var3 = var2.func_85058_a("Affected level");
                  var3.func_71507_a("Problem", "Level is null!");
               } else {
                  this.field_71441_e.func_72914_a(var2);
               }

               throw new ReportedException(var2);
            }
         }

         this.field_71424_I.func_76318_c("animateTick");
         if (!this.field_71445_n && this.field_71441_e != null) {
            this.field_71441_e.func_73029_E(MathHelper.func_76128_c(this.field_71439_g.field_70165_t), MathHelper.func_76128_c(this.field_71439_g.field_70163_u), MathHelper.func_76128_c(this.field_71439_g.field_70161_v));
         }

         this.field_71424_I.func_76318_c("particles");
         if (!this.field_71445_n) {
            this.field_71452_i.func_78868_a();
         }
      } else if (this.field_71453_ak != null) {
         this.field_71424_I.func_76318_c("pendingConnection");
         this.field_71453_ak.func_74428_b();
      }

      this.field_71424_I.func_76318_c("keyboard");
      this.field_195559_v.func_204870_b();
      this.field_71424_I.func_76319_b();
   }

   private void func_184117_aA() {
      for(; this.field_71474_y.field_151457_aa.func_151468_f(); this.field_71438_f.func_174979_m()) {
         ++this.field_71474_y.field_74320_O;
         if (this.field_71474_y.field_74320_O > 2) {
            this.field_71474_y.field_74320_O = 0;
         }

         if (this.field_71474_y.field_74320_O == 0) {
            this.field_71460_t.func_175066_a(this.func_175606_aa());
         } else if (this.field_71474_y.field_74320_O == 1) {
            this.field_71460_t.func_175066_a((Entity)null);
         }
      }

      while(this.field_71474_y.field_151458_ab.func_151468_f()) {
         this.field_71474_y.field_74326_T = !this.field_71474_y.field_74326_T;
      }

      for(int var1 = 0; var1 < 9; ++var1) {
         boolean var2 = this.field_71474_y.field_193629_ap.func_151470_d();
         boolean var3 = this.field_71474_y.field_193630_aq.func_151470_d();
         if (this.field_71474_y.field_151456_ac[var1].func_151468_f()) {
            if (this.field_71439_g.func_175149_v()) {
               this.field_71456_v.func_175187_g().func_175260_a(var1);
            } else if (!this.field_71439_g.func_184812_l_() || this.field_71462_r != null || !var3 && !var2) {
               this.field_71439_g.field_71071_by.field_70461_c = var1;
            } else {
               GuiContainerCreative.func_192044_a(this, var1, var3, var2);
            }
         }
      }

      while(this.field_71474_y.field_151445_Q.func_151468_f()) {
         if (this.field_71442_b.func_110738_j()) {
            this.field_71439_g.func_175163_u();
         } else {
            this.field_193035_aW.func_193296_a();
            this.func_147108_a(new GuiInventory(this.field_71439_g));
         }
      }

      while(this.field_71474_y.field_194146_ao.func_151468_f()) {
         this.func_147108_a(new GuiScreenAdvancements(this.field_71439_g.field_71174_a.func_191982_f()));
      }

      while(this.field_71474_y.field_186718_X.func_151468_f()) {
         if (!this.field_71439_g.func_175149_v()) {
            this.func_147114_u().func_147297_a(new CPacketPlayerDigging(CPacketPlayerDigging.Action.SWAP_HELD_ITEMS, BlockPos.field_177992_a, EnumFacing.DOWN));
         }
      }

      while(this.field_71474_y.field_74316_C.func_151468_f()) {
         if (!this.field_71439_g.func_175149_v()) {
            this.field_71439_g.func_71040_bB(GuiScreen.func_146271_m());
         }
      }

      boolean var4 = this.field_71474_y.field_74343_n != EntityPlayer.EnumChatVisibility.HIDDEN;
      if (var4) {
         while(this.field_71474_y.field_74310_D.func_151468_f()) {
            this.func_147108_a(new GuiChat());
         }

         if (this.field_71462_r == null && this.field_71474_y.field_74323_J.func_151468_f()) {
            this.func_147108_a(new GuiChat("/"));
         }
      }

      if (this.field_71439_g.func_184587_cr()) {
         if (!this.field_71474_y.field_74313_G.func_151470_d()) {
            this.field_71442_b.func_78766_c(this.field_71439_g);
         }

         label109:
         while(true) {
            if (!this.field_71474_y.field_74312_F.func_151468_f()) {
               while(this.field_71474_y.field_74313_G.func_151468_f()) {
               }

               while(true) {
                  if (this.field_71474_y.field_74322_I.func_151468_f()) {
                     continue;
                  }
                  break label109;
               }
            }
         }
      } else {
         while(this.field_71474_y.field_74312_F.func_151468_f()) {
            this.func_147116_af();
         }

         while(this.field_71474_y.field_74313_G.func_151468_f()) {
            this.func_147121_ag();
         }

         while(this.field_71474_y.field_74322_I.func_151468_f()) {
            this.func_147112_ai();
         }
      }

      if (this.field_71474_y.field_74313_G.func_151470_d() && this.field_71467_ac == 0 && !this.field_71439_g.func_184587_cr()) {
         this.func_147121_ag();
      }

      this.func_147115_a(this.field_71462_r == null && this.field_71474_y.field_74312_F.func_151470_d() && this.field_71417_B.func_198035_h());
   }

   public void func_71371_a(String var1, String var2, @Nullable WorldSettings var3) {
      this.func_71403_a((WorldClient)null);
      System.gc();
      ISaveHandler var4 = this.field_71469_aa.func_197715_a(var1, (MinecraftServer)null);
      WorldInfo var5 = var4.func_75757_d();
      if (var5 == null && var3 != null) {
         var5 = new WorldInfo(var3, var1);
         var4.func_75761_a(var5);
      }

      if (var3 == null) {
         var3 = new WorldSettings(var5);
      }

      try {
         YggdrasilAuthenticationService var6 = new YggdrasilAuthenticationService(this.field_110453_aa, UUID.randomUUID().toString());
         MinecraftSessionService var13 = var6.createMinecraftSessionService();
         GameProfileRepository var16 = var6.createProfileRepository();
         PlayerProfileCache var9 = new PlayerProfileCache(var16, new File(this.field_71412_D, MinecraftServer.field_152367_a.getName()));
         TileEntitySkull.func_184293_a(var9);
         TileEntitySkull.func_184294_a(var13);
         PlayerProfileCache.func_187320_a(false);
         this.field_71437_Z = new IntegratedServer(this, var1, var2, var3, var6, var13, var16, var9);
         this.field_71437_Z.func_71256_s();
         this.field_71455_al = true;
      } catch (Throwable var11) {
         CrashReport var7 = CrashReport.func_85055_a(var11, "Starting integrated server");
         CrashReportCategory var8 = var7.func_85058_a("Starting integrated server");
         var8.func_71507_a("Level ID", var1);
         var8.func_71507_a("Level Name", var2);
         throw new ReportedException(var7);
      }

      GuiScreenWorking var12 = new GuiScreenWorking();
      this.func_147108_a(var12);
      var12.func_200210_a(new TextComponentTranslation("menu.loadingLevel", new Object[0]));

      do {
         if (this.field_71437_Z.func_71200_ad()) {
            SocketAddress var15 = this.field_71437_Z.func_147137_ag().func_151270_a();
            NetworkManager var18 = NetworkManager.func_150722_a(var15);
            var18.func_150719_a(new NetHandlerLoginClient(var18, this, (GuiScreen)null, (var0) -> {
            }));
            var18.func_179290_a(new CPacketHandshake(var15.toString(), 0, EnumConnectionState.LOGIN));
            var18.func_179290_a(new CPacketLoginStart(this.func_110432_I().func_148256_e()));
            this.field_71453_ak = var18;
            return;
         }

         ITextComponent var14 = this.field_71437_Z.func_200253_h_();
         if (var14 != null) {
            ITextComponent var17 = this.field_71437_Z.func_200246_aJ();
            if (var17 != null) {
               var12.func_200209_c(var17);
               var12.func_73718_a(this.field_71437_Z.func_195566_aK());
            } else {
               var12.func_200209_c(var14);
            }
         } else {
            var12.func_200209_c(new TextComponentString(""));
         }

         this.func_195542_b(false);

         try {
            Thread.sleep(200L);
         } catch (InterruptedException var10) {
         }
      } while(!this.field_71434_R || this.field_71433_S == null);

      this.func_71377_b(this.field_71433_S);
   }

   public void func_71403_a(@Nullable WorldClient var1) {
      GuiScreenWorking var2 = new GuiScreenWorking();
      if (var1 != null) {
         var2.func_200210_a(new TextComponentTranslation("connect.joining", new Object[0]));
      }

      this.func_205055_a(var1, var2);
   }

   public void func_205055_a(@Nullable WorldClient var1, GuiScreen var2) {
      if (var1 == null) {
         NetHandlerPlayClient var3 = this.func_147114_u();
         if (var3 != null) {
            this.field_152351_aB.clear();
            var3.func_147296_c();
         }

         this.field_71437_Z = null;
         this.field_71460_t.func_190564_k();
         this.field_71442_b = null;
         NarratorChatListener.field_193643_a.func_193642_b();
      }

      this.field_147126_aw.func_209200_a();
      this.field_147127_av.func_147690_c();
      this.field_175622_Z = null;
      this.field_71453_ak = null;
      this.func_147108_a(var2);
      this.func_195542_b(false);
      if (var1 == null && this.field_71441_e != null) {
         this.field_195554_ax.func_195749_c();
         this.field_71456_v.func_181029_i();
         this.func_71351_a((ServerData)null);
         this.field_71455_al = false;
      }

      this.field_71441_e = var1;
      if (this.field_71438_f != null) {
         this.field_71438_f.func_72732_a(var1);
      }

      if (this.field_71452_i != null) {
         this.field_71452_i.func_78870_a(var1);
      }

      TileEntityRendererDispatcher.field_147556_a.func_147543_a(var1);
      if (var1 != null) {
         if (!this.field_71455_al) {
            YggdrasilAuthenticationService var7 = new YggdrasilAuthenticationService(this.field_110453_aa, UUID.randomUUID().toString());
            MinecraftSessionService var4 = var7.createMinecraftSessionService();
            GameProfileRepository var5 = var7.createProfileRepository();
            PlayerProfileCache var6 = new PlayerProfileCache(var5, new File(this.field_71412_D, MinecraftServer.field_152367_a.getName()));
            TileEntitySkull.func_184293_a(var6);
            TileEntitySkull.func_184294_a(var4);
            PlayerProfileCache.func_187320_a(false);
         }

         if (this.field_71439_g == null) {
            this.field_71439_g = this.field_71442_b.func_199681_a(var1, new StatisticsManager(), new RecipeBookClient(var1.func_199532_z()));
            this.field_71442_b.func_78745_b(this.field_71439_g);
            if (this.field_71437_Z != null) {
               this.field_71437_Z.func_211527_b(this.field_71439_g.func_110124_au());
            }
         }

         this.field_71439_g.func_70065_x();
         var1.func_72838_d(this.field_71439_g);
         this.field_71439_g.field_71158_b = new MovementInputFromOptions(this.field_71474_y);
         this.field_71442_b.func_78748_a(this.field_71439_g);
         this.field_175622_Z = this.field_71439_g;
      } else {
         this.field_71439_g = null;
      }

      System.gc();
   }

   public void func_212315_a(DimensionType var1) {
      this.field_71441_e.func_72974_f();
      this.field_71441_e.func_73022_a();
      int var2 = 0;
      String var3 = null;
      if (this.field_71439_g != null) {
         var2 = this.field_71439_g.func_145782_y();
         this.field_71441_e.func_72900_e(this.field_71439_g);
         var3 = this.field_71439_g.func_142021_k();
      }

      this.field_175622_Z = null;
      EntityPlayerSP var4 = this.field_71439_g;
      this.field_71439_g = this.field_71442_b.func_199681_a(this.field_71441_e, this.field_71439_g == null ? new StatisticsManager() : this.field_71439_g.func_146107_m(), this.field_71439_g == null ? new RecipeBookClient(new RecipeManager()) : this.field_71439_g.func_199507_B());
      this.field_71439_g.func_184212_Q().func_187218_a(var4.func_184212_Q().func_187231_c());
      this.field_71439_g.field_71093_bK = var1;
      this.field_175622_Z = this.field_71439_g;
      this.field_71439_g.func_70065_x();
      this.field_71439_g.func_175158_f(var3);
      this.field_71441_e.func_72838_d(this.field_71439_g);
      this.field_71442_b.func_78745_b(this.field_71439_g);
      this.field_71439_g.field_71158_b = new MovementInputFromOptions(this.field_71474_y);
      this.field_71439_g.func_145769_d(var2);
      this.field_71442_b.func_78748_a(this.field_71439_g);
      this.field_71439_g.func_175150_k(var4.func_175140_cp());
      if (this.field_71462_r instanceof GuiGameOver) {
         this.func_147108_a((GuiScreen)null);
      }

   }

   public final boolean func_71355_q() {
      return this.field_71459_aj;
   }

   @Nullable
   public NetHandlerPlayClient func_147114_u() {
      return this.field_71439_g == null ? null : this.field_71439_g.field_71174_a;
   }

   public static boolean func_71382_s() {
      return field_71432_P == null || !field_71432_P.field_71474_y.field_74319_N;
   }

   public static boolean func_71375_t() {
      return field_71432_P != null && field_71432_P.field_71474_y.field_74347_j;
   }

   public static boolean func_71379_u() {
      return field_71432_P != null && field_71432_P.field_71474_y.field_74348_k != 0;
   }

   private void func_147112_ai() {
      if (this.field_71476_x != null && this.field_71476_x.field_72313_a != RayTraceResult.Type.MISS) {
         boolean var1 = this.field_71439_g.field_71075_bZ.field_75098_d;
         TileEntity var2 = null;
         ItemStack var3;
         if (this.field_71476_x.field_72313_a == RayTraceResult.Type.BLOCK) {
            BlockPos var9 = this.field_71476_x.func_178782_a();
            IBlockState var13 = this.field_71441_e.func_180495_p(var9);
            Block var6 = var13.func_177230_c();
            if (var13.func_196958_f()) {
               return;
            }

            var3 = var6.func_185473_a(this.field_71441_e, var9, var13);
            if (var3.func_190926_b()) {
               return;
            }

            if (var1 && GuiScreen.func_146271_m() && var6.func_149716_u()) {
               var2 = this.field_71441_e.func_175625_s(var9);
            }
         } else {
            if (this.field_71476_x.field_72313_a != RayTraceResult.Type.ENTITY || this.field_71476_x.field_72308_g == null || !var1) {
               return;
            }

            if (this.field_71476_x.field_72308_g instanceof EntityPainting) {
               var3 = new ItemStack(Items.field_151159_an);
            } else if (this.field_71476_x.field_72308_g instanceof EntityLeashKnot) {
               var3 = new ItemStack(Items.field_151058_ca);
            } else if (this.field_71476_x.field_72308_g instanceof EntityItemFrame) {
               EntityItemFrame var4 = (EntityItemFrame)this.field_71476_x.field_72308_g;
               ItemStack var5 = var4.func_82335_i();
               if (var5.func_190926_b()) {
                  var3 = new ItemStack(Items.field_151160_bD);
               } else {
                  var3 = var5.func_77946_l();
               }
            } else if (this.field_71476_x.field_72308_g instanceof EntityMinecart) {
               EntityMinecart var7 = (EntityMinecart)this.field_71476_x.field_72308_g;
               Item var11;
               switch(var7.func_184264_v()) {
               case FURNACE:
                  var11 = Items.field_151109_aJ;
                  break;
               case CHEST:
                  var11 = Items.field_151108_aI;
                  break;
               case TNT:
                  var11 = Items.field_151142_bV;
                  break;
               case HOPPER:
                  var11 = Items.field_151140_bW;
                  break;
               case COMMAND_BLOCK:
                  var11 = Items.field_151095_cc;
                  break;
               default:
                  var11 = Items.field_151143_au;
               }

               var3 = new ItemStack(var11);
            } else if (this.field_71476_x.field_72308_g instanceof EntityBoat) {
               var3 = new ItemStack(((EntityBoat)this.field_71476_x.field_72308_g).func_184455_j());
            } else if (this.field_71476_x.field_72308_g instanceof EntityArmorStand) {
               var3 = new ItemStack(Items.field_179565_cj);
            } else if (this.field_71476_x.field_72308_g instanceof EntityEnderCrystal) {
               var3 = new ItemStack(Items.field_185158_cP);
            } else {
               ItemSpawnEgg var8 = ItemSpawnEgg.func_200889_b(this.field_71476_x.field_72308_g.func_200600_R());
               if (var8 == null) {
                  return;
               }

               var3 = new ItemStack(var8);
            }
         }

         if (var3.func_190926_b()) {
            String var12 = "";
            if (this.field_71476_x.field_72313_a == RayTraceResult.Type.BLOCK) {
               var12 = IRegistry.field_212618_g.func_177774_c(this.field_71441_e.func_180495_p(this.field_71476_x.func_178782_a()).func_177230_c()).toString();
            } else if (this.field_71476_x.field_72313_a == RayTraceResult.Type.ENTITY) {
               var12 = IRegistry.field_212629_r.func_177774_c(this.field_71476_x.field_72308_g.func_200600_R()).toString();
            }

            field_147123_G.warn("Picking on: [{}] {} gave null item", this.field_71476_x.field_72313_a, var12);
         } else {
            InventoryPlayer var10 = this.field_71439_g.field_71071_by;
            if (var2 != null) {
               this.func_184119_a(var3, var2);
            }

            int var14 = var10.func_184429_b(var3);
            if (var1) {
               var10.func_184434_a(var3);
               this.field_71442_b.func_78761_a(this.field_71439_g.func_184586_b(EnumHand.MAIN_HAND), 36 + var10.field_70461_c);
            } else if (var14 != -1) {
               if (InventoryPlayer.func_184435_e(var14)) {
                  var10.field_70461_c = var14;
               } else {
                  this.field_71442_b.func_187100_a(var14);
               }
            }

         }
      }
   }

   private ItemStack func_184119_a(ItemStack var1, TileEntity var2) {
      NBTTagCompound var3 = var2.func_189515_b(new NBTTagCompound());
      NBTTagCompound var4;
      if (var1.func_77973_b() instanceof ItemSkull && var3.func_74764_b("Owner")) {
         var4 = var3.func_74775_l("Owner");
         var1.func_196082_o().func_74782_a("SkullOwner", var4);
         return var1;
      } else {
         var1.func_77983_a("BlockEntityTag", var3);
         var4 = new NBTTagCompound();
         NBTTagList var5 = new NBTTagList();
         var5.add((INBTBase)(new NBTTagString("(+NBT)")));
         var4.func_74782_a("Lore", var5);
         var1.func_77983_a("display", var4);
         return var1;
      }
   }

   public CrashReport func_71396_d(CrashReport var1) {
      CrashReportCategory var2 = var1.func_85056_g();
      var2.func_189529_a("Launched Version", () -> {
         return this.field_110447_Z;
      });
      var2.func_189529_a("LWJGL", Version::getVersion);
      var2.func_189529_a("OpenGL", () -> {
         return GLFW.glfwGetCurrentContext() == 0L ? "NO CONTEXT" : GlStateManager.func_187416_u(7937) + " GL version " + GlStateManager.func_187416_u(7938) + ", " + GlStateManager.func_187416_u(7936);
      });
      var2.func_189529_a("GL Caps", OpenGlHelper::func_153172_c);
      var2.func_189529_a("Using VBOs", () -> {
         return this.field_71474_y.field_178881_t ? "Yes" : "No";
      });
      var2.func_189529_a("Is Modded", () -> {
         String var0 = ClientBrandRetriever.getClientModName();
         if (!"vanilla".equals(var0)) {
            return "Definitely; Client brand changed to '" + var0 + "'";
         } else {
            return Minecraft.class.getSigners() == null ? "Very likely; Jar signature invalidated" : "Probably not. Jar signature remains and client brand is untouched.";
         }
      });
      var2.func_71507_a("Type", "Client (map_client.txt)");
      var2.func_189529_a("Resource Packs", () -> {
         StringBuilder var1 = new StringBuilder();
         Iterator var2 = this.field_71474_y.field_151453_l.iterator();

         while(var2.hasNext()) {
            String var3 = (String)var2.next();
            if (var1.length() > 0) {
               var1.append(", ");
            }

            var1.append(var3);
            if (this.field_71474_y.field_183018_l.contains(var3)) {
               var1.append(" (incompatible)");
            }
         }

         return var1.toString();
      });
      var2.func_189529_a("Current Language", () -> {
         return this.field_135017_as.func_135041_c().toString();
      });
      var2.func_189529_a("Profiler Position", () -> {
         return this.field_71424_I.func_199094_a() ? this.field_71424_I.func_76322_c() : "N/A (disabled)";
      });
      var2.func_189529_a("CPU", OpenGlHelper::func_183029_j);
      if (this.field_71441_e != null) {
         this.field_71441_e.func_72914_a(var1);
      }

      return var1;
   }

   public static Minecraft func_71410_x() {
      return field_71432_P;
   }

   public ListenableFuture<Object> func_175603_A() {
      return this.func_152344_a(this::func_110436_a);
   }

   public void func_70000_a(Snooper var1) {
      var1.func_152768_a("fps", field_71470_ab);
      var1.func_152768_a("vsync_enabled", this.field_71474_y.field_74352_v);
      long var2 = GLFW.glfwGetWindowMonitor(this.field_195558_d.func_198092_i());
      if (var2 == 0L) {
         var2 = GLFW.glfwGetPrimaryMonitor();
      }

      var1.func_152768_a("display_frequency", GLFW.glfwGetVideoMode(var2).refreshRate());
      var1.func_152768_a("display_type", this.field_195558_d.func_198113_j() ? "fullscreen" : "windowed");
      var1.func_152768_a("run_time", (Util.func_211177_b() - var1.func_130105_g()) / 60L * 1000L);
      var1.func_152768_a("current_action", this.func_181538_aA());
      var1.func_152768_a("language", this.field_71474_y.field_74363_ab == null ? "en_us" : this.field_71474_y.field_74363_ab);
      String var4 = ByteOrder.nativeOrder() == ByteOrder.LITTLE_ENDIAN ? "little" : "big";
      var1.func_152768_a("endianness", var4);
      var1.func_152768_a("subtitles", this.field_71474_y.field_186717_N);
      var1.func_152768_a("touch", this.field_71474_y.field_85185_A ? "touch" : "mouse");
      int var5 = 0;
      Iterator var6 = this.field_110448_aq.func_198980_d().iterator();

      while(var6.hasNext()) {
         ResourcePackInfoClient var7 = (ResourcePackInfoClient)var6.next();
         if (!var7.func_195797_g() && !var7.func_195798_h()) {
            var1.func_152768_a("resource_pack[" + var5++ + "]", var7.func_195790_f());
         }
      }

      var1.func_152768_a("resource_packs", var5);
      if (this.field_71437_Z != null && this.field_71437_Z.func_80003_ah() != null) {
         var1.func_152768_a("snooper_partner", this.field_71437_Z.func_80003_ah().func_80006_f());
      }

   }

   private String func_181538_aA() {
      if (this.field_71437_Z != null) {
         return this.field_71437_Z.func_71344_c() ? "hosting_lan" : "singleplayer";
      } else if (this.field_71422_O != null) {
         return this.field_71422_O.func_181041_d() ? "playing_lan" : "multiplayer";
      } else {
         return "out_of_game";
      }
   }

   public static int func_71369_N() {
      if (field_211120_F == -1) {
         for(int var0 = 16384; var0 > 0; var0 >>= 1) {
            GlStateManager.func_187419_a(32868, 0, 6408, var0, var0, 0, 6408, 5121, (IntBuffer)null);
            int var1 = GlStateManager.func_187411_c(32868, 0, 4096);
            if (var1 != 0) {
               field_211120_F = var0;
               return var0;
            }
         }
      }

      return field_211120_F;
   }

   public boolean func_70002_Q() {
      return this.field_71474_y.field_74355_t;
   }

   public void func_71351_a(ServerData var1) {
      this.field_71422_O = var1;
   }

   @Nullable
   public ServerData func_147104_D() {
      return this.field_71422_O;
   }

   public boolean func_71387_A() {
      return this.field_71455_al;
   }

   public boolean func_71356_B() {
      return this.field_71455_al && this.field_71437_Z != null;
   }

   @Nullable
   public IntegratedServer func_71401_C() {
      return this.field_71437_Z;
   }

   public static void func_71363_D() {
      if (field_71432_P != null) {
         IntegratedServer var0 = field_71432_P.func_71401_C();
         if (var0 != null) {
            var0.func_71260_j();
         }

      }
   }

   public Snooper func_71378_E() {
      return this.field_71427_U;
   }

   public Session func_110432_I() {
      return this.field_71449_j;
   }

   public PropertyMap func_181037_M() {
      if (this.field_181038_N.isEmpty()) {
         GameProfile var1 = this.func_152347_ac().fillProfileProperties(this.field_71449_j.func_148256_e(), false);
         this.field_181038_N.putAll(var1.getProperties());
      }

      return this.field_181038_N;
   }

   public Proxy func_110437_J() {
      return this.field_110453_aa;
   }

   public TextureManager func_110434_K() {
      return this.field_71446_o;
   }

   public IResourceManager func_195551_G() {
      return this.field_110451_am;
   }

   public ResourcePackList<ResourcePackInfoClient> func_195548_H() {
      return this.field_110448_aq;
   }

   public DownloadingPackFinder func_195541_I() {
      return this.field_195554_ax;
   }

   public File func_195549_J() {
      return this.field_130070_K;
   }

   public LanguageManager func_135016_M() {
      return this.field_135017_as;
   }

   public TextureMap func_147117_R() {
      return this.field_147128_au;
   }

   public boolean func_147111_S() {
      return this.field_147129_ai;
   }

   public boolean func_147113_T() {
      return this.field_71445_n;
   }

   public SoundHandler func_147118_V() {
      return this.field_147127_av;
   }

   public MusicTicker.MusicType func_147109_W() {
      if (this.field_71462_r instanceof GuiWinGame) {
         return MusicTicker.MusicType.CREDITS;
      } else if (this.field_71439_g == null) {
         return MusicTicker.MusicType.MENU;
      } else if (this.field_71439_g.field_70170_p.field_73011_w instanceof NetherDimension) {
         return MusicTicker.MusicType.NETHER;
      } else if (this.field_71439_g.field_70170_p.field_73011_w instanceof EndDimension) {
         return this.field_71456_v.func_184046_j().func_184054_d() ? MusicTicker.MusicType.END_BOSS : MusicTicker.MusicType.END;
      } else {
         Biome.Category var1 = this.field_71439_g.field_70170_p.func_180494_b(new BlockPos(this.field_71439_g.field_70165_t, this.field_71439_g.field_70163_u, this.field_71439_g.field_70161_v)).func_201856_r();
         if (this.field_147126_aw.func_209100_b(MusicTicker.MusicType.UNDER_WATER) || this.field_71439_g.func_204231_K() && !this.field_147126_aw.func_209100_b(MusicTicker.MusicType.GAME) && (var1 == Biome.Category.OCEAN || var1 == Biome.Category.RIVER)) {
            return MusicTicker.MusicType.UNDER_WATER;
         } else {
            return this.field_71439_g.field_71075_bZ.field_75098_d && this.field_71439_g.field_71075_bZ.field_75101_c ? MusicTicker.MusicType.CREATIVE : MusicTicker.MusicType.GAME;
         }
      }
   }

   public MinecraftSessionService func_152347_ac() {
      return this.field_152355_az;
   }

   public SkinManager func_152342_ad() {
      return this.field_152350_aA;
   }

   @Nullable
   public Entity func_175606_aa() {
      return this.field_175622_Z;
   }

   public void func_175607_a(Entity var1) {
      this.field_175622_Z = var1;
      this.field_71460_t.func_175066_a(var1);
   }

   public <V> ListenableFuture<V> func_152343_a(Callable<V> var1) {
      Validate.notNull(var1);
      if (this.func_152345_ab()) {
         try {
            return Futures.immediateFuture(var1.call());
         } catch (Exception var3) {
            return Futures.immediateFailedCheckedFuture(var3);
         }
      } else {
         ListenableFutureTask var2 = ListenableFutureTask.create(var1);
         this.field_152351_aB.add(var2);
         return var2;
      }
   }

   public ListenableFuture<Object> func_152344_a(Runnable var1) {
      Validate.notNull(var1);
      return this.func_152343_a(Executors.callable(var1));
   }

   public boolean func_152345_ab() {
      return Thread.currentThread() == this.field_152352_aC;
   }

   public BlockRendererDispatcher func_175602_ab() {
      return this.field_175618_aM;
   }

   public RenderManager func_175598_ae() {
      return this.field_175616_W;
   }

   public ItemRenderer func_175599_af() {
      return this.field_175621_X;
   }

   public FirstPersonRenderer func_175597_ag() {
      return this.field_175620_Y;
   }

   public <T> ISearchTree<T> func_193987_a(SearchTreeManager.Key<T> var1) {
      return this.field_193995_ae.func_194010_a(var1);
   }

   public static int func_175610_ah() {
      return field_71470_ab;
   }

   public FrameTimer func_181539_aj() {
      return this.field_181542_y;
   }

   public boolean func_181540_al() {
      return this.field_181541_X;
   }

   public void func_181537_a(boolean var1) {
      this.field_181541_X = var1;
   }

   public DataFixer func_184126_aj() {
      return this.field_184131_U;
   }

   public float func_184121_ak() {
      return this.field_71428_T.field_194147_b;
   }

   public float func_193989_ak() {
      return this.field_71428_T.field_194148_c;
   }

   public BlockColors func_184125_al() {
      return this.field_184127_aH;
   }

   public boolean func_189648_am() {
      return this.field_71439_g != null && this.field_71439_g.func_175140_cp() || this.field_71474_y.field_178879_v;
   }

   public GuiToast func_193033_an() {
      return this.field_193034_aS;
   }

   public Tutorial func_193032_ao() {
      return this.field_193035_aW;
   }

   public boolean func_195544_aj() {
      return this.field_195555_I;
   }

   public CreativeSettings func_199403_al() {
      return this.field_191950_u;
   }

   public ModelManager func_209506_al() {
      return this.field_175617_aL;
   }

   public FontResourceManager func_211500_ak() {
      return this.field_211501_aD;
   }

   static {
      field_142025_a = Util.func_110647_a() == Util.EnumOS.OSX;
      field_211502_b = new ResourceLocation("default");
      field_71464_q = new ResourceLocation("alt");
      field_71444_a = new byte[10485760];
      field_211120_F = -1;
   }
}
