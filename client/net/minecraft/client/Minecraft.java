package net.minecraft.client;

import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Queues;
import com.google.common.collect.Sets;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListenableFutureTask;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.minecraft.MinecraftSessionService;
import com.mojang.authlib.properties.Property;
import com.mojang.authlib.properties.PropertyMap;
import com.mojang.authlib.yggdrasil.YggdrasilAuthenticationService;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.Proxy;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.UUID;
import java.util.concurrent.Callable;
import java.util.concurrent.Executors;
import java.util.concurrent.FutureTask;
import javax.imageio.ImageIO;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.audio.MusicTicker;
import net.minecraft.client.audio.SoundHandler;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.client.gui.GuiControls;
import net.minecraft.client.gui.GuiGameOver;
import net.minecraft.client.gui.GuiIngame;
import net.minecraft.client.gui.GuiIngameMenu;
import net.minecraft.client.gui.GuiMainMenu;
import net.minecraft.client.gui.GuiMemoryErrorScreen;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiSleepMP;
import net.minecraft.client.gui.GuiYesNo;
import net.minecraft.client.gui.GuiYesNoCallback;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.gui.achievement.GuiAchievement;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.client.gui.stream.GuiStreamUnavailable;
import net.minecraft.client.main.GameConfiguration;
import net.minecraft.client.multiplayer.GuiConnecting;
import net.minecraft.client.multiplayer.PlayerControllerMP;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.client.network.NetHandlerLoginClient;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.client.particle.EffectRenderer;
import net.minecraft.client.renderer.BlockRendererDispatcher;
import net.minecraft.client.renderer.EntityRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RenderGlobal;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.chunk.RenderChunk;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.resources.DefaultResourcePack;
import net.minecraft.client.resources.FoliageColorReloadListener;
import net.minecraft.client.resources.GrassColorReloadListener;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.resources.IReloadableResourceManager;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.client.resources.IResourcePack;
import net.minecraft.client.resources.LanguageManager;
import net.minecraft.client.resources.ResourceIndex;
import net.minecraft.client.resources.ResourcePackRepository;
import net.minecraft.client.resources.SimpleReloadableResourceManager;
import net.minecraft.client.resources.SkinManager;
import net.minecraft.client.resources.data.AnimationMetadataSection;
import net.minecraft.client.resources.data.AnimationMetadataSectionSerializer;
import net.minecraft.client.resources.data.FontMetadataSection;
import net.minecraft.client.resources.data.FontMetadataSectionSerializer;
import net.minecraft.client.resources.data.IMetadataSerializer;
import net.minecraft.client.resources.data.LanguageMetadataSection;
import net.minecraft.client.resources.data.LanguageMetadataSectionSerializer;
import net.minecraft.client.resources.data.PackMetadataSection;
import net.minecraft.client.resources.data.PackMetadataSectionSerializer;
import net.minecraft.client.resources.data.TextureMetadataSection;
import net.minecraft.client.resources.data.TextureMetadataSectionSerializer;
import net.minecraft.client.resources.model.ModelManager;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.client.shader.Framebuffer;
import net.minecraft.client.stream.IStream;
import net.minecraft.client.stream.NullStream;
import net.minecraft.client.stream.TwitchStream;
import net.minecraft.crash.CrashReport;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLeashKnot;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.boss.BossStatus;
import net.minecraft.entity.item.EntityArmorStand;
import net.minecraft.entity.item.EntityBoat;
import net.minecraft.entity.item.EntityItemFrame;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.entity.item.EntityPainting;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.init.Bootstrap;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.network.EnumConnectionState;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.handshake.client.C00Handshake;
import net.minecraft.network.login.client.C00PacketLoginStart;
import net.minecraft.network.play.client.C16PacketClientStatus;
import net.minecraft.profiler.IPlayerUsage;
import net.minecraft.profiler.PlayerUsageSnooper;
import net.minecraft.profiler.Profiler;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.integrated.IntegratedServer;
import net.minecraft.stats.AchievementList;
import net.minecraft.stats.IStatStringFormat;
import net.minecraft.stats.StatFileWriter;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.FrameTimer;
import net.minecraft.util.IThreadListener;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MinecraftError;
import net.minecraft.util.MouseHelper;
import net.minecraft.util.MovementInputFromOptions;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.ReportedException;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.ScreenShotHelper;
import net.minecraft.util.Session;
import net.minecraft.util.Timer;
import net.minecraft.util.Util;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.WorldProviderEnd;
import net.minecraft.world.WorldProviderHell;
import net.minecraft.world.WorldSettings;
import net.minecraft.world.chunk.storage.AnvilSaveConverter;
import net.minecraft.world.storage.ISaveFormat;
import net.minecraft.world.storage.ISaveHandler;
import net.minecraft.world.storage.WorldInfo;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.Validate;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.LWJGLException;
import org.lwjgl.Sys;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.ContextCapabilities;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GLContext;
import org.lwjgl.opengl.OpenGLException;
import org.lwjgl.opengl.PixelFormat;
import org.lwjgl.util.glu.GLU;

public class Minecraft implements IThreadListener, IPlayerUsage {
   private static final Logger field_147123_G = LogManager.getLogger();
   private static final ResourceLocation field_110444_H = new ResourceLocation("textures/gui/title/mojang.png");
   public static final boolean field_142025_a;
   public static byte[] field_71444_a;
   private static final List<DisplayMode> field_110445_I;
   private final File field_130070_K;
   private final PropertyMap field_152356_J;
   private final PropertyMap field_181038_N;
   private ServerData field_71422_O;
   private TextureManager field_71446_o;
   private static Minecraft field_71432_P;
   public PlayerControllerMP field_71442_b;
   private boolean field_71431_Q;
   private boolean field_175619_R = true;
   private boolean field_71434_R;
   private CrashReport field_71433_S;
   public int field_71443_c;
   public int field_71440_d;
   private boolean field_181541_X = false;
   private Timer field_71428_T = new Timer(20.0F);
   private PlayerUsageSnooper field_71427_U = new PlayerUsageSnooper("client", this, MinecraftServer.func_130071_aq());
   public WorldClient field_71441_e;
   public RenderGlobal field_71438_f;
   private RenderManager field_175616_W;
   private RenderItem field_175621_X;
   private ItemRenderer field_175620_Y;
   public EntityPlayerSP field_71439_g;
   private Entity field_175622_Z;
   public Entity field_147125_j;
   public EffectRenderer field_71452_i;
   private final Session field_71449_j;
   private boolean field_71445_n;
   public FontRenderer field_71466_p;
   public FontRenderer field_71464_q;
   public GuiScreen field_71462_r;
   public LoadingScreenRenderer field_71461_s;
   public EntityRenderer field_71460_t;
   private int field_71429_W;
   private int field_71436_X;
   private int field_71435_Y;
   private IntegratedServer field_71437_Z;
   public GuiAchievement field_71458_u;
   public GuiIngame field_71456_v;
   public boolean field_71454_w;
   public MovingObjectPosition field_71476_x;
   public GameSettings field_71474_y;
   public MouseHelper field_71417_B;
   public final File field_71412_D;
   private final File field_110446_Y;
   private final String field_110447_Z;
   private final Proxy field_110453_aa;
   private ISaveFormat field_71469_aa;
   private static int field_71470_ab;
   private int field_71467_ac;
   private String field_71475_ae;
   private int field_71477_af;
   public boolean field_71415_G;
   long field_71423_H = func_71386_F();
   private int field_71457_ai;
   public final FrameTimer field_181542_y = new FrameTimer();
   long field_181543_z = System.nanoTime();
   private final boolean field_147129_ai;
   private final boolean field_71459_aj;
   private NetworkManager field_71453_ak;
   private boolean field_71455_al;
   public final Profiler field_71424_I = new Profiler();
   private long field_83002_am = -1L;
   private IReloadableResourceManager field_110451_am;
   private final IMetadataSerializer field_110452_an = new IMetadataSerializer();
   private final List<IResourcePack> field_110449_ao = Lists.newArrayList();
   private final DefaultResourcePack field_110450_ap;
   private ResourcePackRepository field_110448_aq;
   private LanguageManager field_135017_as;
   private IStream field_152353_at;
   private Framebuffer field_147124_at;
   private TextureMap field_147128_au;
   private SoundHandler field_147127_av;
   private MusicTicker field_147126_aw;
   private ResourceLocation field_152354_ay;
   private final MinecraftSessionService field_152355_az;
   private SkinManager field_152350_aA;
   private final Queue<FutureTask<?>> field_152351_aB = Queues.newArrayDeque();
   private long field_175615_aJ = 0L;
   private final Thread field_152352_aC = Thread.currentThread();
   private ModelManager field_175617_aL;
   private BlockRendererDispatcher field_175618_aM;
   volatile boolean field_71425_J = true;
   public String field_71426_K = "";
   public boolean field_175613_B = false;
   public boolean field_175614_C = false;
   public boolean field_175611_D = false;
   public boolean field_175612_E = true;
   long field_71419_L = func_71386_F();
   int field_71420_M;
   long field_71421_N = -1L;
   private String field_71465_an = "root";

   public Minecraft(GameConfiguration var1) {
      super();
      field_71432_P = this;
      this.field_71412_D = var1.field_178744_c.field_178760_a;
      this.field_110446_Y = var1.field_178744_c.field_178759_c;
      this.field_130070_K = var1.field_178744_c.field_178758_b;
      this.field_110447_Z = var1.field_178741_d.field_178755_b;
      this.field_152356_J = var1.field_178745_a.field_178750_b;
      this.field_181038_N = var1.field_178745_a.field_181172_c;
      this.field_110450_ap = new DefaultResourcePack((new ResourceIndex(var1.field_178744_c.field_178759_c, var1.field_178744_c.field_178757_d)).func_152782_a());
      this.field_110453_aa = var1.field_178745_a.field_178751_c == null ? Proxy.NO_PROXY : var1.field_178745_a.field_178751_c;
      this.field_152355_az = (new YggdrasilAuthenticationService(var1.field_178745_a.field_178751_c, UUID.randomUUID().toString())).createMinecraftSessionService();
      this.field_71449_j = var1.field_178745_a.field_178752_a;
      field_147123_G.info("Setting user: " + this.field_71449_j.func_111285_a());
      field_147123_G.info("(Session ID is " + this.field_71449_j.func_111286_b() + ")");
      this.field_71459_aj = var1.field_178741_d.field_178756_a;
      this.field_71443_c = var1.field_178743_b.field_178764_a > 0 ? var1.field_178743_b.field_178764_a : 1;
      this.field_71440_d = var1.field_178743_b.field_178762_b > 0 ? var1.field_178743_b.field_178762_b : 1;
      this.field_71436_X = var1.field_178743_b.field_178764_a;
      this.field_71435_Y = var1.field_178743_b.field_178762_b;
      this.field_71431_Q = var1.field_178743_b.field_178763_c;
      this.field_147129_ai = func_147122_X();
      this.field_71437_Z = new IntegratedServer(this);
      if (var1.field_178742_e.field_178754_a != null) {
         this.field_71475_ae = var1.field_178742_e.field_178754_a;
         this.field_71477_af = var1.field_178742_e.field_178753_b;
      }

      ImageIO.setUseCache(false);
      Bootstrap.func_151354_b();
   }

   public void func_99999_d() {
      this.field_71425_J = true;

      CrashReport var2;
      try {
         this.func_71384_a();
      } catch (Throwable var11) {
         var2 = CrashReport.func_85055_a(var11, "Initializing game");
         var2.func_85058_a("Initialization");
         this.func_71377_b(this.func_71396_d(var2));
         return;
      }

      while(true) {
         try {
            if (this.field_71425_J) {
               if (!this.field_71434_R || this.field_71433_S == null) {
                  try {
                     this.func_71411_J();
                  } catch (OutOfMemoryError var10) {
                     this.func_71398_f();
                     this.func_147108_a(new GuiMemoryErrorScreen());
                     System.gc();
                  }
                  continue;
               }

               this.func_71377_b(this.field_71433_S);
               return;
            }
         } catch (MinecraftError var12) {
         } catch (ReportedException var13) {
            this.func_71396_d(var13.func_71575_a());
            this.func_71398_f();
            field_147123_G.fatal("Reported exception thrown!", var13);
            this.func_71377_b(var13.func_71575_a());
         } catch (Throwable var14) {
            var2 = this.func_71396_d(new CrashReport("Unexpected error", var14));
            this.func_71398_f();
            field_147123_G.fatal("Unreported exception thrown!", var14);
            this.func_71377_b(var2);
         } finally {
            this.func_71405_e();
         }

         return;
      }
   }

   private void func_71384_a() throws LWJGLException, IOException {
      this.field_71474_y = new GameSettings(this, this.field_71412_D);
      this.field_110449_ao.add(this.field_110450_ap);
      this.func_71389_H();
      if (this.field_71474_y.field_92119_C > 0 && this.field_71474_y.field_92118_B > 0) {
         this.field_71443_c = this.field_71474_y.field_92118_B;
         this.field_71440_d = this.field_71474_y.field_92119_C;
      }

      field_147123_G.info("LWJGL Version: " + Sys.getVersion());
      this.func_175594_ao();
      this.func_175605_an();
      this.func_175609_am();
      OpenGlHelper.func_77474_a();
      this.field_147124_at = new Framebuffer(this.field_71443_c, this.field_71440_d, true);
      this.field_147124_at.func_147604_a(0.0F, 0.0F, 0.0F, 0.0F);
      this.func_175608_ak();
      this.field_110448_aq = new ResourcePackRepository(this.field_130070_K, new File(this.field_71412_D, "server-resource-packs"), this.field_110450_ap, this.field_110452_an, this.field_71474_y);
      this.field_110451_am = new SimpleReloadableResourceManager(this.field_110452_an);
      this.field_135017_as = new LanguageManager(this.field_110452_an, this.field_71474_y.field_74363_ab);
      this.field_110451_am.func_110542_a(this.field_135017_as);
      this.func_110436_a();
      this.field_71446_o = new TextureManager(this.field_110451_am);
      this.field_110451_am.func_110542_a(this.field_71446_o);
      this.func_180510_a(this.field_71446_o);
      this.func_175595_al();
      this.field_152350_aA = new SkinManager(this.field_71446_o, new File(this.field_110446_Y, "skins"), this.field_152355_az);
      this.field_71469_aa = new AnvilSaveConverter(new File(this.field_71412_D, "saves"));
      this.field_147127_av = new SoundHandler(this.field_110451_am, this.field_71474_y);
      this.field_110451_am.func_110542_a(this.field_147127_av);
      this.field_147126_aw = new MusicTicker(this);
      this.field_71466_p = new FontRenderer(this.field_71474_y, new ResourceLocation("textures/font/ascii.png"), this.field_71446_o, false);
      if (this.field_71474_y.field_74363_ab != null) {
         this.field_71466_p.func_78264_a(this.func_152349_b());
         this.field_71466_p.func_78275_b(this.field_135017_as.func_135044_b());
      }

      this.field_71464_q = new FontRenderer(this.field_71474_y, new ResourceLocation("textures/font/ascii_sga.png"), this.field_71446_o, false);
      this.field_110451_am.func_110542_a(this.field_71466_p);
      this.field_110451_am.func_110542_a(this.field_71464_q);
      this.field_110451_am.func_110542_a(new GrassColorReloadListener());
      this.field_110451_am.func_110542_a(new FoliageColorReloadListener());
      AchievementList.field_76004_f.func_75988_a(new IStatStringFormat() {
         public String func_74535_a(String var1) {
            try {
               return String.format(var1, GameSettings.func_74298_c(Minecraft.this.field_71474_y.field_151445_Q.func_151463_i()));
            } catch (Exception var3) {
               return "Error: " + var3.getLocalizedMessage();
            }
         }
      });
      this.field_71417_B = new MouseHelper();
      this.func_71361_d("Pre startup");
      GlStateManager.func_179098_w();
      GlStateManager.func_179103_j(7425);
      GlStateManager.func_179151_a(1.0D);
      GlStateManager.func_179126_j();
      GlStateManager.func_179143_c(515);
      GlStateManager.func_179141_d();
      GlStateManager.func_179092_a(516, 0.1F);
      GlStateManager.func_179107_e(1029);
      GlStateManager.func_179128_n(5889);
      GlStateManager.func_179096_D();
      GlStateManager.func_179128_n(5888);
      this.func_71361_d("Startup");
      this.field_147128_au = new TextureMap("textures");
      this.field_147128_au.func_147633_a(this.field_71474_y.field_151442_I);
      this.field_71446_o.func_110580_a(TextureMap.field_110575_b, this.field_147128_au);
      this.field_71446_o.func_110577_a(TextureMap.field_110575_b);
      this.field_147128_au.func_174937_a(false, this.field_71474_y.field_151442_I > 0);
      this.field_175617_aL = new ModelManager(this.field_147128_au);
      this.field_110451_am.func_110542_a(this.field_175617_aL);
      this.field_175621_X = new RenderItem(this.field_71446_o, this.field_175617_aL);
      this.field_175616_W = new RenderManager(this.field_71446_o, this.field_175621_X);
      this.field_175620_Y = new ItemRenderer(this);
      this.field_110451_am.func_110542_a(this.field_175621_X);
      this.field_71460_t = new EntityRenderer(this, this.field_110451_am);
      this.field_110451_am.func_110542_a(this.field_71460_t);
      this.field_175618_aM = new BlockRendererDispatcher(this.field_175617_aL.func_174954_c(), this.field_71474_y);
      this.field_110451_am.func_110542_a(this.field_175618_aM);
      this.field_71438_f = new RenderGlobal(this);
      this.field_110451_am.func_110542_a(this.field_71438_f);
      this.field_71458_u = new GuiAchievement(this);
      GlStateManager.func_179083_b(0, 0, this.field_71443_c, this.field_71440_d);
      this.field_71452_i = new EffectRenderer(this.field_71441_e, this.field_71446_o);
      this.func_71361_d("Post startup");
      this.field_71456_v = new GuiIngame(this);
      if (this.field_71475_ae != null) {
         this.func_147108_a(new GuiConnecting(new GuiMainMenu(), this, this.field_71475_ae, this.field_71477_af));
      } else {
         this.func_147108_a(new GuiMainMenu());
      }

      this.field_71446_o.func_147645_c(this.field_152354_ay);
      this.field_152354_ay = null;
      this.field_71461_s = new LoadingScreenRenderer(this);
      if (this.field_71474_y.field_74353_u && !this.field_71431_Q) {
         this.func_71352_k();
      }

      try {
         Display.setVSyncEnabled(this.field_71474_y.field_74352_v);
      } catch (OpenGLException var2) {
         this.field_71474_y.field_74352_v = false;
         this.field_71474_y.func_74303_b();
      }

      this.field_71438_f.func_174966_b();
   }

   private void func_175608_ak() {
      this.field_110452_an.func_110504_a(new TextureMetadataSectionSerializer(), TextureMetadataSection.class);
      this.field_110452_an.func_110504_a(new FontMetadataSectionSerializer(), FontMetadataSection.class);
      this.field_110452_an.func_110504_a(new AnimationMetadataSectionSerializer(), AnimationMetadataSection.class);
      this.field_110452_an.func_110504_a(new PackMetadataSectionSerializer(), PackMetadataSection.class);
      this.field_110452_an.func_110504_a(new LanguageMetadataSectionSerializer(), LanguageMetadataSection.class);
   }

   private void func_175595_al() {
      try {
         this.field_152353_at = new TwitchStream(this, (Property)Iterables.getFirst(this.field_152356_J.get("twitch_access_token"), (Object)null));
      } catch (Throwable var2) {
         this.field_152353_at = new NullStream(var2);
         field_147123_G.error("Couldn't initialize twitch stream");
      }

   }

   private void func_175609_am() throws LWJGLException {
      Display.setResizable(true);
      Display.setTitle("Minecraft 1.8.9");

      try {
         Display.create((new PixelFormat()).withDepthBits(24));
      } catch (LWJGLException var4) {
         field_147123_G.error("Couldn't set pixel format", var4);

         try {
            Thread.sleep(1000L);
         } catch (InterruptedException var3) {
         }

         if (this.field_71431_Q) {
            this.func_110441_Q();
         }

         Display.create();
      }

   }

   private void func_175605_an() throws LWJGLException {
      if (this.field_71431_Q) {
         Display.setFullscreen(true);
         DisplayMode var1 = Display.getDisplayMode();
         this.field_71443_c = Math.max(1, var1.getWidth());
         this.field_71440_d = Math.max(1, var1.getHeight());
      } else {
         Display.setDisplayMode(new DisplayMode(this.field_71443_c, this.field_71440_d));
      }

   }

   private void func_175594_ao() {
      Util.EnumOS var1 = Util.func_110647_a();
      if (var1 != Util.EnumOS.OSX) {
         InputStream var2 = null;
         InputStream var3 = null;

         try {
            var2 = this.field_110450_ap.func_152780_c(new ResourceLocation("icons/icon_16x16.png"));
            var3 = this.field_110450_ap.func_152780_c(new ResourceLocation("icons/icon_32x32.png"));
            if (var2 != null && var3 != null) {
               Display.setIcon(new ByteBuffer[]{this.func_152340_a(var2), this.func_152340_a(var3)});
            }
         } catch (IOException var8) {
            field_147123_G.error("Couldn't set icon", var8);
         } finally {
            IOUtils.closeQuietly(var2);
            IOUtils.closeQuietly(var3);
         }
      }

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

   public boolean func_152349_b() {
      return this.field_135017_as.func_135042_a() || this.field_71474_y.field_151455_aw;
   }

   public void func_110436_a() {
      ArrayList var1 = Lists.newArrayList(this.field_110449_ao);
      Iterator var2 = this.field_110448_aq.func_110613_c().iterator();

      while(var2.hasNext()) {
         ResourcePackRepository.Entry var3 = (ResourcePackRepository.Entry)var2.next();
         var1.add(var3.func_110514_c());
      }

      if (this.field_110448_aq.func_148530_e() != null) {
         var1.add(this.field_110448_aq.func_148530_e());
      }

      try {
         this.field_110451_am.func_110541_a(var1);
      } catch (RuntimeException var4) {
         field_147123_G.info("Caught error stitching, removing all assigned resourcepacks", var4);
         var1.clear();
         var1.addAll(this.field_110449_ao);
         this.field_110448_aq.func_148527_a(Collections.emptyList());
         this.field_110451_am.func_110541_a(var1);
         this.field_71474_y.field_151453_l.clear();
         this.field_71474_y.field_183018_l.clear();
         this.field_71474_y.func_74303_b();
      }

      this.field_135017_as.func_135043_a(var1);
      if (this.field_71438_f != null) {
         this.field_71438_f.func_72712_a();
      }

   }

   private ByteBuffer func_152340_a(InputStream var1) throws IOException {
      BufferedImage var2 = ImageIO.read(var1);
      int[] var3 = var2.getRGB(0, 0, var2.getWidth(), var2.getHeight(), (int[])null, 0, var2.getWidth());
      ByteBuffer var4 = ByteBuffer.allocate(4 * var3.length);
      int[] var5 = var3;
      int var6 = var3.length;

      for(int var7 = 0; var7 < var6; ++var7) {
         int var8 = var5[var7];
         var4.putInt(var8 << 8 | var8 >> 24 & 255);
      }

      var4.flip();
      return var4;
   }

   private void func_110441_Q() throws LWJGLException {
      HashSet var1 = Sets.newHashSet();
      Collections.addAll(var1, Display.getAvailableDisplayModes());
      DisplayMode var2 = Display.getDesktopDisplayMode();
      if (!var1.contains(var2) && Util.func_110647_a() == Util.EnumOS.OSX) {
         Iterator var3 = field_110445_I.iterator();

         label52:
         while(true) {
            while(true) {
               DisplayMode var4;
               boolean var5;
               Iterator var6;
               DisplayMode var7;
               do {
                  if (!var3.hasNext()) {
                     break label52;
                  }

                  var4 = (DisplayMode)var3.next();
                  var5 = true;
                  var6 = var1.iterator();

                  while(var6.hasNext()) {
                     var7 = (DisplayMode)var6.next();
                     if (var7.getBitsPerPixel() == 32 && var7.getWidth() == var4.getWidth() && var7.getHeight() == var4.getHeight()) {
                        var5 = false;
                        break;
                     }
                  }
               } while(var5);

               var6 = var1.iterator();

               while(var6.hasNext()) {
                  var7 = (DisplayMode)var6.next();
                  if (var7.getBitsPerPixel() == 32 && var7.getWidth() == var4.getWidth() / 2 && var7.getHeight() == var4.getHeight() / 2) {
                     var2 = var7;
                     break;
                  }
               }
            }
         }
      }

      Display.setDisplayMode(var2);
      this.field_71443_c = var2.getWidth();
      this.field_71440_d = var2.getHeight();
   }

   private void func_180510_a(TextureManager var1) throws LWJGLException {
      ScaledResolution var2 = new ScaledResolution(this);
      int var3 = var2.func_78325_e();
      Framebuffer var4 = new Framebuffer(var2.func_78326_a() * var3, var2.func_78328_b() * var3, true);
      var4.func_147610_a(false);
      GlStateManager.func_179128_n(5889);
      GlStateManager.func_179096_D();
      GlStateManager.func_179130_a(0.0D, (double)var2.func_78326_a(), (double)var2.func_78328_b(), 0.0D, 1000.0D, 3000.0D);
      GlStateManager.func_179128_n(5888);
      GlStateManager.func_179096_D();
      GlStateManager.func_179109_b(0.0F, 0.0F, -2000.0F);
      GlStateManager.func_179140_f();
      GlStateManager.func_179106_n();
      GlStateManager.func_179097_i();
      GlStateManager.func_179098_w();
      InputStream var5 = null;

      try {
         var5 = this.field_110450_ap.func_110590_a(field_110444_H);
         this.field_152354_ay = var1.func_110578_a("logo", new DynamicTexture(ImageIO.read(var5)));
         var1.func_110577_a(this.field_152354_ay);
      } catch (IOException var12) {
         field_147123_G.error("Unable to load logo: " + field_110444_H, var12);
      } finally {
         IOUtils.closeQuietly(var5);
      }

      Tessellator var6 = Tessellator.func_178181_a();
      WorldRenderer var7 = var6.func_178180_c();
      var7.func_181668_a(7, DefaultVertexFormats.field_181709_i);
      var7.func_181662_b(0.0D, (double)this.field_71440_d, 0.0D).func_181673_a(0.0D, 0.0D).func_181669_b(255, 255, 255, 255).func_181675_d();
      var7.func_181662_b((double)this.field_71443_c, (double)this.field_71440_d, 0.0D).func_181673_a(0.0D, 0.0D).func_181669_b(255, 255, 255, 255).func_181675_d();
      var7.func_181662_b((double)this.field_71443_c, 0.0D, 0.0D).func_181673_a(0.0D, 0.0D).func_181669_b(255, 255, 255, 255).func_181675_d();
      var7.func_181662_b(0.0D, 0.0D, 0.0D).func_181673_a(0.0D, 0.0D).func_181669_b(255, 255, 255, 255).func_181675_d();
      var6.func_78381_a();
      GlStateManager.func_179131_c(1.0F, 1.0F, 1.0F, 1.0F);
      short var8 = 256;
      short var9 = 256;
      this.func_181536_a((var2.func_78326_a() - var8) / 2, (var2.func_78328_b() - var9) / 2, 0, 0, var8, var9, 255, 255, 255, 255);
      GlStateManager.func_179140_f();
      GlStateManager.func_179106_n();
      var4.func_147609_e();
      var4.func_147615_c(var2.func_78326_a() * var3, var2.func_78328_b() * var3);
      GlStateManager.func_179141_d();
      GlStateManager.func_179092_a(516, 0.1F);
      this.func_175601_h();
   }

   public void func_181536_a(int var1, int var2, int var3, int var4, int var5, int var6, int var7, int var8, int var9, int var10) {
      float var11 = 0.00390625F;
      float var12 = 0.00390625F;
      WorldRenderer var13 = Tessellator.func_178181_a().func_178180_c();
      var13.func_181668_a(7, DefaultVertexFormats.field_181709_i);
      var13.func_181662_b((double)var1, (double)(var2 + var6), 0.0D).func_181673_a((double)((float)var3 * var11), (double)((float)(var4 + var6) * var12)).func_181669_b(var7, var8, var9, var10).func_181675_d();
      var13.func_181662_b((double)(var1 + var5), (double)(var2 + var6), 0.0D).func_181673_a((double)((float)(var3 + var5) * var11), (double)((float)(var4 + var6) * var12)).func_181669_b(var7, var8, var9, var10).func_181675_d();
      var13.func_181662_b((double)(var1 + var5), (double)var2, 0.0D).func_181673_a((double)((float)(var3 + var5) * var11), (double)((float)var4 * var12)).func_181669_b(var7, var8, var9, var10).func_181675_d();
      var13.func_181662_b((double)var1, (double)var2, 0.0D).func_181673_a((double)((float)var3 * var11), (double)((float)var4 * var12)).func_181669_b(var7, var8, var9, var10).func_181675_d();
      Tessellator.func_178181_a().func_78381_a();
   }

   public ISaveFormat func_71359_d() {
      return this.field_71469_aa;
   }

   public void func_147108_a(GuiScreen var1) {
      if (this.field_71462_r != null) {
         this.field_71462_r.func_146281_b();
      }

      if (var1 == null && this.field_71441_e == null) {
         var1 = new GuiMainMenu();
      } else if (var1 == null && this.field_71439_g.func_110143_aJ() <= 0.0F) {
         var1 = new GuiGameOver();
      }

      if (var1 instanceof GuiMainMenu) {
         this.field_71474_y.field_74330_P = false;
         this.field_71456_v.func_146158_b().func_146231_a();
      }

      this.field_71462_r = (GuiScreen)var1;
      if (var1 != null) {
         this.func_71364_i();
         ScaledResolution var2 = new ScaledResolution(this);
         int var3 = var2.func_78326_a();
         int var4 = var2.func_78328_b();
         ((GuiScreen)var1).func_146280_a(this, var3, var4);
         this.field_71454_w = false;
      } else {
         this.field_147127_av.func_147687_e();
         this.func_71381_h();
      }

   }

   private void func_71361_d(String var1) {
      if (this.field_175619_R) {
         int var2 = GL11.glGetError();
         if (var2 != 0) {
            String var3 = GLU.gluErrorString(var2);
            field_147123_G.error("########## GL ERROR ##########");
            field_147123_G.error("@ " + var1);
            field_147123_G.error(var2 + ": " + var3);
         }

      }
   }

   public void func_71405_e() {
      try {
         this.field_152353_at.func_152923_i();
         field_147123_G.info("Stopping!");

         try {
            this.func_71403_a((WorldClient)null);
         } catch (Throwable var5) {
         }

         this.field_147127_av.func_147685_d();
      } finally {
         Display.destroy();
         if (!this.field_71434_R) {
            System.exit(0);
         }

      }

      System.gc();
   }

   private void func_71411_J() {
      long var1 = System.nanoTime();
      this.field_71424_I.func_76320_a("root");
      if (Display.isCreated() && Display.isCloseRequested()) {
         this.func_71400_g();
      }

      if (this.field_71445_n && this.field_71441_e != null) {
         float var3 = this.field_71428_T.field_74281_c;
         this.field_71428_T.func_74275_a();
         this.field_71428_T.field_74281_c = var3;
      } else {
         this.field_71428_T.func_74275_a();
      }

      this.field_71424_I.func_76320_a("scheduledExecutables");
      synchronized(this.field_152351_aB) {
         while(!this.field_152351_aB.isEmpty()) {
            Util.func_181617_a((FutureTask)this.field_152351_aB.poll(), field_147123_G);
         }
      }

      this.field_71424_I.func_76319_b();
      long var10 = System.nanoTime();
      this.field_71424_I.func_76320_a("tick");

      for(int var5 = 0; var5 < this.field_71428_T.field_74280_b; ++var5) {
         this.func_71407_l();
      }

      this.field_71424_I.func_76318_c("preRenderErrors");
      long var11 = System.nanoTime() - var10;
      this.func_71361_d("Pre render");
      this.field_71424_I.func_76318_c("sound");
      this.field_147127_av.func_147691_a(this.field_71439_g, this.field_71428_T.field_74281_c);
      this.field_71424_I.func_76319_b();
      this.field_71424_I.func_76320_a("render");
      GlStateManager.func_179094_E();
      GlStateManager.func_179086_m(16640);
      this.field_147124_at.func_147610_a(true);
      this.field_71424_I.func_76320_a("display");
      GlStateManager.func_179098_w();
      if (this.field_71439_g != null && this.field_71439_g.func_70094_T()) {
         this.field_71474_y.field_74320_O = 0;
      }

      this.field_71424_I.func_76319_b();
      if (!this.field_71454_w) {
         this.field_71424_I.func_76318_c("gameRenderer");
         this.field_71460_t.func_181560_a(this.field_71428_T.field_74281_c, var1);
         this.field_71424_I.func_76319_b();
      }

      this.field_71424_I.func_76319_b();
      if (this.field_71474_y.field_74330_P && this.field_71474_y.field_74329_Q && !this.field_71474_y.field_74319_N) {
         if (!this.field_71424_I.field_76327_a) {
            this.field_71424_I.func_76317_a();
         }

         this.field_71424_I.field_76327_a = true;
         this.func_71366_a(var11);
      } else {
         this.field_71424_I.field_76327_a = false;
         this.field_71421_N = System.nanoTime();
      }

      this.field_71458_u.func_146254_a();
      this.field_147124_at.func_147609_e();
      GlStateManager.func_179121_F();
      GlStateManager.func_179094_E();
      this.field_147124_at.func_147615_c(this.field_71443_c, this.field_71440_d);
      GlStateManager.func_179121_F();
      GlStateManager.func_179094_E();
      this.field_71460_t.func_152430_c(this.field_71428_T.field_74281_c);
      GlStateManager.func_179121_F();
      this.field_71424_I.func_76320_a("root");
      this.func_175601_h();
      Thread.yield();
      this.field_71424_I.func_76320_a("stream");
      this.field_71424_I.func_76320_a("update");
      this.field_152353_at.func_152935_j();
      this.field_71424_I.func_76318_c("submit");
      this.field_152353_at.func_152922_k();
      this.field_71424_I.func_76319_b();
      this.field_71424_I.func_76319_b();
      this.func_71361_d("Post render");
      ++this.field_71420_M;
      this.field_71445_n = this.func_71356_B() && this.field_71462_r != null && this.field_71462_r.func_73868_f() && !this.field_71437_Z.func_71344_c();
      long var7 = System.nanoTime();
      this.field_181542_y.func_181747_a(var7 - this.field_181543_z);
      this.field_181543_z = var7;

      while(func_71386_F() >= this.field_71419_L + 1000L) {
         field_71470_ab = this.field_71420_M;
         this.field_71426_K = String.format("%d fps (%d chunk update%s) T: %s%s%s%s%s", field_71470_ab, RenderChunk.field_178592_a, RenderChunk.field_178592_a != 1 ? "s" : "", (float)this.field_71474_y.field_74350_i == GameSettings.Options.FRAMERATE_LIMIT.func_148267_f() ? "inf" : this.field_71474_y.field_74350_i, this.field_71474_y.field_74352_v ? " vsync" : "", this.field_71474_y.field_74347_j ? "" : " fast", this.field_71474_y.field_74345_l == 0 ? "" : (this.field_71474_y.field_74345_l == 1 ? " fast-clouds" : " fancy-clouds"), OpenGlHelper.func_176075_f() ? " vbo" : "");
         RenderChunk.field_178592_a = 0;
         this.field_71419_L += 1000L;
         this.field_71420_M = 0;
         this.field_71427_U.func_76471_b();
         if (!this.field_71427_U.func_76468_d()) {
            this.field_71427_U.func_76463_a();
         }
      }

      if (this.func_147107_h()) {
         this.field_71424_I.func_76320_a("fpslimit_wait");
         Display.sync(this.func_90020_K());
         this.field_71424_I.func_76319_b();
      }

      this.field_71424_I.func_76319_b();
   }

   public void func_175601_h() {
      this.field_71424_I.func_76320_a("display_update");
      Display.update();
      this.field_71424_I.func_76319_b();
      this.func_175604_i();
   }

   protected void func_175604_i() {
      if (!this.field_71431_Q && Display.wasResized()) {
         int var1 = this.field_71443_c;
         int var2 = this.field_71440_d;
         this.field_71443_c = Display.getWidth();
         this.field_71440_d = Display.getHeight();
         if (this.field_71443_c != var1 || this.field_71440_d != var2) {
            if (this.field_71443_c <= 0) {
               this.field_71443_c = 1;
            }

            if (this.field_71440_d <= 0) {
               this.field_71440_d = 1;
            }

            this.func_71370_a(this.field_71443_c, this.field_71440_d);
         }
      }

   }

   public int func_90020_K() {
      return this.field_71441_e == null && this.field_71462_r != null ? 30 : this.field_71474_y.field_74350_i;
   }

   public boolean func_147107_h() {
      return (float)this.func_90020_K() < GameSettings.Options.FRAMERATE_LIMIT.func_148267_f();
   }

   public void func_71398_f() {
      try {
         field_71444_a = new byte[0];
         this.field_71438_f.func_72728_f();
      } catch (Throwable var3) {
      }

      try {
         System.gc();
         this.func_71403_a((WorldClient)null);
      } catch (Throwable var2) {
      }

      System.gc();
   }

   private void func_71383_b(int var1) {
      List var2 = this.field_71424_I.func_76321_b(this.field_71465_an);
      if (var2 != null && !var2.isEmpty()) {
         Profiler.Result var3 = (Profiler.Result)var2.remove(0);
         if (var1 == 0) {
            if (var3.field_76331_c.length() > 0) {
               int var4 = this.field_71465_an.lastIndexOf(".");
               if (var4 >= 0) {
                  this.field_71465_an = this.field_71465_an.substring(0, var4);
               }
            }
         } else {
            --var1;
            if (var1 < var2.size() && !((Profiler.Result)var2.get(var1)).field_76331_c.equals("unspecified")) {
               if (this.field_71465_an.length() > 0) {
                  this.field_71465_an = this.field_71465_an + ".";
               }

               this.field_71465_an = this.field_71465_an + ((Profiler.Result)var2.get(var1)).field_76331_c;
            }
         }

      }
   }

   private void func_71366_a(long var1) {
      if (this.field_71424_I.field_76327_a) {
         List var3 = this.field_71424_I.func_76321_b(this.field_71465_an);
         Profiler.Result var4 = (Profiler.Result)var3.remove(0);
         GlStateManager.func_179086_m(256);
         GlStateManager.func_179128_n(5889);
         GlStateManager.func_179142_g();
         GlStateManager.func_179096_D();
         GlStateManager.func_179130_a(0.0D, (double)this.field_71443_c, (double)this.field_71440_d, 0.0D, 1000.0D, 3000.0D);
         GlStateManager.func_179128_n(5888);
         GlStateManager.func_179096_D();
         GlStateManager.func_179109_b(0.0F, 0.0F, -2000.0F);
         GL11.glLineWidth(1.0F);
         GlStateManager.func_179090_x();
         Tessellator var5 = Tessellator.func_178181_a();
         WorldRenderer var6 = var5.func_178180_c();
         short var7 = 160;
         int var8 = this.field_71443_c - var7 - 10;
         int var9 = this.field_71440_d - var7 * 2;
         GlStateManager.func_179147_l();
         var6.func_181668_a(7, DefaultVertexFormats.field_181706_f);
         var6.func_181662_b((double)((float)var8 - (float)var7 * 1.1F), (double)((float)var9 - (float)var7 * 0.6F - 16.0F), 0.0D).func_181669_b(200, 0, 0, 0).func_181675_d();
         var6.func_181662_b((double)((float)var8 - (float)var7 * 1.1F), (double)(var9 + var7 * 2), 0.0D).func_181669_b(200, 0, 0, 0).func_181675_d();
         var6.func_181662_b((double)((float)var8 + (float)var7 * 1.1F), (double)(var9 + var7 * 2), 0.0D).func_181669_b(200, 0, 0, 0).func_181675_d();
         var6.func_181662_b((double)((float)var8 + (float)var7 * 1.1F), (double)((float)var9 - (float)var7 * 0.6F - 16.0F), 0.0D).func_181669_b(200, 0, 0, 0).func_181675_d();
         var5.func_78381_a();
         GlStateManager.func_179084_k();
         double var10 = 0.0D;

         int var14;
         for(int var12 = 0; var12 < var3.size(); ++var12) {
            Profiler.Result var13 = (Profiler.Result)var3.get(var12);
            var14 = MathHelper.func_76128_c(var13.field_76332_a / 4.0D) + 1;
            var6.func_181668_a(6, DefaultVertexFormats.field_181706_f);
            int var15 = var13.func_76329_a();
            int var16 = var15 >> 16 & 255;
            int var17 = var15 >> 8 & 255;
            int var18 = var15 & 255;
            var6.func_181662_b((double)var8, (double)var9, 0.0D).func_181669_b(var16, var17, var18, 255).func_181675_d();

            int var19;
            float var20;
            float var21;
            float var22;
            for(var19 = var14; var19 >= 0; --var19) {
               var20 = (float)((var10 + var13.field_76332_a * (double)var19 / (double)var14) * 3.1415927410125732D * 2.0D / 100.0D);
               var21 = MathHelper.func_76126_a(var20) * (float)var7;
               var22 = MathHelper.func_76134_b(var20) * (float)var7 * 0.5F;
               var6.func_181662_b((double)((float)var8 + var21), (double)((float)var9 - var22), 0.0D).func_181669_b(var16, var17, var18, 255).func_181675_d();
            }

            var5.func_78381_a();
            var6.func_181668_a(5, DefaultVertexFormats.field_181706_f);

            for(var19 = var14; var19 >= 0; --var19) {
               var20 = (float)((var10 + var13.field_76332_a * (double)var19 / (double)var14) * 3.1415927410125732D * 2.0D / 100.0D);
               var21 = MathHelper.func_76126_a(var20) * (float)var7;
               var22 = MathHelper.func_76134_b(var20) * (float)var7 * 0.5F;
               var6.func_181662_b((double)((float)var8 + var21), (double)((float)var9 - var22), 0.0D).func_181669_b(var16 >> 1, var17 >> 1, var18 >> 1, 255).func_181675_d();
               var6.func_181662_b((double)((float)var8 + var21), (double)((float)var9 - var22 + 10.0F), 0.0D).func_181669_b(var16 >> 1, var17 >> 1, var18 >> 1, 255).func_181675_d();
            }

            var5.func_78381_a();
            var10 += var13.field_76332_a;
         }

         DecimalFormat var23 = new DecimalFormat("##0.00");
         GlStateManager.func_179098_w();
         String var24 = "";
         if (!var4.field_76331_c.equals("unspecified")) {
            var24 = var24 + "[0] ";
         }

         if (var4.field_76331_c.length() == 0) {
            var24 = var24 + "ROOT ";
         } else {
            var24 = var24 + var4.field_76331_c + " ";
         }

         var14 = 16777215;
         this.field_71466_p.func_175063_a(var24, (float)(var8 - var7), (float)(var9 - var7 / 2 - 16), var14);
         this.field_71466_p.func_175063_a(var24 = var23.format(var4.field_76330_b) + "%", (float)(var8 + var7 - this.field_71466_p.func_78256_a(var24)), (float)(var9 - var7 / 2 - 16), var14);

         for(int var27 = 0; var27 < var3.size(); ++var27) {
            Profiler.Result var25 = (Profiler.Result)var3.get(var27);
            String var26 = "";
            if (var25.field_76331_c.equals("unspecified")) {
               var26 = var26 + "[?] ";
            } else {
               var26 = var26 + "[" + (var27 + 1) + "] ";
            }

            var26 = var26 + var25.field_76331_c;
            this.field_71466_p.func_175063_a(var26, (float)(var8 - var7), (float)(var9 + var7 / 2 + var27 * 8 + 20), var25.func_76329_a());
            this.field_71466_p.func_175063_a(var26 = var23.format(var25.field_76332_a) + "%", (float)(var8 + var7 - 50 - this.field_71466_p.func_78256_a(var26)), (float)(var9 + var7 / 2 + var27 * 8 + 20), var25.func_76329_a());
            this.field_71466_p.func_175063_a(var26 = var23.format(var25.field_76330_b) + "%", (float)(var8 + var7 - this.field_71466_p.func_78256_a(var26)), (float)(var9 + var7 / 2 + var27 * 8 + 20), var25.func_76329_a());
         }

      }
   }

   public void func_71400_g() {
      this.field_71425_J = false;
   }

   public void func_71381_h() {
      if (Display.isActive()) {
         if (!this.field_71415_G) {
            this.field_71415_G = true;
            this.field_71417_B.func_74372_a();
            this.func_147108_a((GuiScreen)null);
            this.field_71429_W = 10000;
         }
      }
   }

   public void func_71364_i() {
      if (this.field_71415_G) {
         KeyBinding.func_74506_a();
         this.field_71415_G = false;
         this.field_71417_B.func_74373_b();
      }
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

      if (this.field_71429_W <= 0 && !this.field_71439_g.func_71039_bw()) {
         if (var1 && this.field_71476_x != null && this.field_71476_x.field_72313_a == MovingObjectPosition.MovingObjectType.BLOCK) {
            BlockPos var2 = this.field_71476_x.func_178782_a();
            if (this.field_71441_e.func_180495_p(var2).func_177230_c().func_149688_o() != Material.field_151579_a && this.field_71442_b.func_180512_c(var2, this.field_71476_x.field_178784_b)) {
               this.field_71452_i.func_180532_a(var2, this.field_71476_x.field_178784_b);
               this.field_71439_g.func_71038_i();
            }

         } else {
            this.field_71442_b.func_78767_c();
         }
      }
   }

   private void func_147116_af() {
      if (this.field_71429_W <= 0) {
         this.field_71439_g.func_71038_i();
         if (this.field_71476_x == null) {
            field_147123_G.error("Null returned as 'hitResult', this shouldn't happen!");
            if (this.field_71442_b.func_78762_g()) {
               this.field_71429_W = 10;
            }

         } else {
            switch(this.field_71476_x.field_72313_a) {
            case ENTITY:
               this.field_71442_b.func_78764_a(this.field_71439_g, this.field_71476_x.field_72308_g);
               break;
            case BLOCK:
               BlockPos var1 = this.field_71476_x.func_178782_a();
               if (this.field_71441_e.func_180495_p(var1).func_177230_c().func_149688_o() != Material.field_151579_a) {
                  this.field_71442_b.func_180511_b(var1, this.field_71476_x.field_178784_b);
                  break;
               }
            case MISS:
            default:
               if (this.field_71442_b.func_78762_g()) {
                  this.field_71429_W = 10;
               }
            }

         }
      }
   }

   private void func_147121_ag() {
      if (!this.field_71442_b.func_181040_m()) {
         this.field_71467_ac = 4;
         boolean var1 = true;
         ItemStack var2 = this.field_71439_g.field_71071_by.func_70448_g();
         if (this.field_71476_x == null) {
            field_147123_G.warn("Null returned as 'hitResult', this shouldn't happen!");
         } else {
            switch(this.field_71476_x.field_72313_a) {
            case ENTITY:
               if (this.field_71442_b.func_178894_a(this.field_71439_g, this.field_71476_x.field_72308_g, this.field_71476_x)) {
                  var1 = false;
               } else if (this.field_71442_b.func_78768_b(this.field_71439_g, this.field_71476_x.field_72308_g)) {
                  var1 = false;
               }
               break;
            case BLOCK:
               BlockPos var3 = this.field_71476_x.func_178782_a();
               if (this.field_71441_e.func_180495_p(var3).func_177230_c().func_149688_o() != Material.field_151579_a) {
                  int var4 = var2 != null ? var2.field_77994_a : 0;
                  if (this.field_71442_b.func_178890_a(this.field_71439_g, this.field_71441_e, var2, var3, this.field_71476_x.field_178784_b, this.field_71476_x.field_72307_f)) {
                     var1 = false;
                     this.field_71439_g.func_71038_i();
                  }

                  if (var2 == null) {
                     return;
                  }

                  if (var2.field_77994_a == 0) {
                     this.field_71439_g.field_71071_by.field_70462_a[this.field_71439_g.field_71071_by.field_70461_c] = null;
                  } else if (var2.field_77994_a != var4 || this.field_71442_b.func_78758_h()) {
                     this.field_71460_t.field_78516_c.func_78444_b();
                  }
               }
            }
         }

         if (var1) {
            ItemStack var5 = this.field_71439_g.field_71071_by.func_70448_g();
            if (var5 != null && this.field_71442_b.func_78769_a(this.field_71439_g, this.field_71441_e, var5)) {
               this.field_71460_t.field_78516_c.func_78445_c();
            }
         }

      }
   }

   public void func_71352_k() {
      try {
         this.field_71431_Q = !this.field_71431_Q;
         this.field_71474_y.field_74353_u = this.field_71431_Q;
         if (this.field_71431_Q) {
            this.func_110441_Q();
            this.field_71443_c = Display.getDisplayMode().getWidth();
            this.field_71440_d = Display.getDisplayMode().getHeight();
            if (this.field_71443_c <= 0) {
               this.field_71443_c = 1;
            }

            if (this.field_71440_d <= 0) {
               this.field_71440_d = 1;
            }
         } else {
            Display.setDisplayMode(new DisplayMode(this.field_71436_X, this.field_71435_Y));
            this.field_71443_c = this.field_71436_X;
            this.field_71440_d = this.field_71435_Y;
            if (this.field_71443_c <= 0) {
               this.field_71443_c = 1;
            }

            if (this.field_71440_d <= 0) {
               this.field_71440_d = 1;
            }
         }

         if (this.field_71462_r != null) {
            this.func_71370_a(this.field_71443_c, this.field_71440_d);
         } else {
            this.func_147119_ah();
         }

         Display.setFullscreen(this.field_71431_Q);
         Display.setVSyncEnabled(this.field_71474_y.field_74352_v);
         this.func_175601_h();
      } catch (Exception var2) {
         field_147123_G.error("Couldn't toggle fullscreen", var2);
      }

   }

   private void func_71370_a(int var1, int var2) {
      this.field_71443_c = Math.max(1, var1);
      this.field_71440_d = Math.max(1, var2);
      if (this.field_71462_r != null) {
         ScaledResolution var3 = new ScaledResolution(this);
         this.field_71462_r.func_175273_b(this, var3.func_78326_a(), var3.func_78328_b());
      }

      this.field_71461_s = new LoadingScreenRenderer(this);
      this.func_147119_ah();
   }

   private void func_147119_ah() {
      this.field_147124_at.func_147613_a(this.field_71443_c, this.field_71440_d);
      if (this.field_71460_t != null) {
         this.field_71460_t.func_147704_a(this.field_71443_c, this.field_71440_d);
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
      this.field_71424_I.func_76320_a("gameMode");
      if (!this.field_71445_n && this.field_71441_e != null) {
         this.field_71442_b.func_78765_e();
      }

      this.field_71424_I.func_76318_c("textures");
      if (!this.field_71445_n) {
         this.field_71446_o.func_110550_d();
      }

      if (this.field_71462_r == null && this.field_71439_g != null) {
         if (this.field_71439_g.func_110143_aJ() <= 0.0F) {
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

      CrashReport var2;
      CrashReportCategory var3;
      if (this.field_71462_r != null) {
         try {
            this.field_71462_r.func_146269_k();
         } catch (Throwable var7) {
            var2 = CrashReport.func_85055_a(var7, "Updating screen events");
            var3 = var2.func_85058_a("Affected screen");
            var3.func_71500_a("Screen name", new Callable<String>() {
               public String call() throws Exception {
                  return Minecraft.this.field_71462_r.getClass().getCanonicalName();
               }

               // $FF: synthetic method
               public Object call() throws Exception {
                  return this.call();
               }
            });
            throw new ReportedException(var2);
         }

         if (this.field_71462_r != null) {
            try {
               this.field_71462_r.func_73876_c();
            } catch (Throwable var6) {
               var2 = CrashReport.func_85055_a(var6, "Ticking screen");
               var3 = var2.func_85058_a("Affected screen");
               var3.func_71500_a("Screen name", new Callable<String>() {
                  public String call() throws Exception {
                     return Minecraft.this.field_71462_r.getClass().getCanonicalName();
                  }

                  // $FF: synthetic method
                  public Object call() throws Exception {
                     return this.call();
                  }
               });
               throw new ReportedException(var2);
            }
         }
      }

      if (this.field_71462_r == null || this.field_71462_r.field_146291_p) {
         this.field_71424_I.func_76318_c("mouse");

         int var1;
         while(Mouse.next()) {
            var1 = Mouse.getEventButton();
            KeyBinding.func_74510_a(var1 - 100, Mouse.getEventButtonState());
            if (Mouse.getEventButtonState()) {
               if (this.field_71439_g.func_175149_v() && var1 == 2) {
                  this.field_71456_v.func_175187_g().func_175261_b();
               } else {
                  KeyBinding.func_74507_a(var1 - 100);
               }
            }

            long var10 = func_71386_F() - this.field_71423_H;
            if (var10 <= 200L) {
               int var4 = Mouse.getEventDWheel();
               if (var4 != 0) {
                  if (this.field_71439_g.func_175149_v()) {
                     var4 = var4 < 0 ? -1 : 1;
                     if (this.field_71456_v.func_175187_g().func_175262_a()) {
                        this.field_71456_v.func_175187_g().func_175259_b(-var4);
                     } else {
                        float var5 = MathHelper.func_76131_a(this.field_71439_g.field_71075_bZ.func_75093_a() + (float)var4 * 0.005F, 0.0F, 0.2F);
                        this.field_71439_g.field_71075_bZ.func_75092_a(var5);
                     }
                  } else {
                     this.field_71439_g.field_71071_by.func_70453_c(var4);
                  }
               }

               if (this.field_71462_r == null) {
                  if (!this.field_71415_G && Mouse.getEventButtonState()) {
                     this.func_71381_h();
                  }
               } else if (this.field_71462_r != null) {
                  this.field_71462_r.func_146274_d();
               }
            }
         }

         if (this.field_71429_W > 0) {
            --this.field_71429_W;
         }

         this.field_71424_I.func_76318_c("keyboard");

         label504:
         while(true) {
            do {
               do {
                  do {
                     if (!Keyboard.next()) {
                        for(var1 = 0; var1 < 9; ++var1) {
                           if (this.field_71474_y.field_151456_ac[var1].func_151468_f()) {
                              if (this.field_71439_g.func_175149_v()) {
                                 this.field_71456_v.func_175187_g().func_175260_a(var1);
                              } else {
                                 this.field_71439_g.field_71071_by.field_70461_c = var1;
                              }
                           }
                        }

                        boolean var9 = this.field_71474_y.field_74343_n != EntityPlayer.EnumChatVisibility.HIDDEN;

                        while(this.field_71474_y.field_151445_Q.func_151468_f()) {
                           if (this.field_71442_b.func_110738_j()) {
                              this.field_71439_g.func_175163_u();
                           } else {
                              this.func_147114_u().func_147297_a(new C16PacketClientStatus(C16PacketClientStatus.EnumState.OPEN_INVENTORY_ACHIEVEMENT));
                              this.func_147108_a(new GuiInventory(this.field_71439_g));
                           }
                        }

                        while(this.field_71474_y.field_74316_C.func_151468_f()) {
                           if (!this.field_71439_g.func_175149_v()) {
                              this.field_71439_g.func_71040_bB(GuiScreen.func_146271_m());
                           }
                        }

                        while(this.field_71474_y.field_74310_D.func_151468_f() && var9) {
                           this.func_147108_a(new GuiChat());
                        }

                        if (this.field_71462_r == null && this.field_71474_y.field_74323_J.func_151468_f() && var9) {
                           this.func_147108_a(new GuiChat("/"));
                        }

                        if (this.field_71439_g.func_71039_bw()) {
                           if (!this.field_71474_y.field_74313_G.func_151470_d()) {
                              this.field_71442_b.func_78766_c(this.field_71439_g);
                           }

                           while(true) {
                              if (!this.field_71474_y.field_74312_F.func_151468_f()) {
                                 while(this.field_71474_y.field_74313_G.func_151468_f()) {
                                 }

                                 while(this.field_71474_y.field_74322_I.func_151468_f()) {
                                 }
                                 break;
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

                        if (this.field_71474_y.field_74313_G.func_151470_d() && this.field_71467_ac == 0 && !this.field_71439_g.func_71039_bw()) {
                           this.func_147121_ag();
                        }

                        this.func_147115_a(this.field_71462_r == null && this.field_71474_y.field_74312_F.func_151470_d() && this.field_71415_G);
                        break label504;
                     }

                     var1 = Keyboard.getEventKey() == 0 ? Keyboard.getEventCharacter() + 256 : Keyboard.getEventKey();
                     KeyBinding.func_74510_a(var1, Keyboard.getEventKeyState());
                     if (Keyboard.getEventKeyState()) {
                        KeyBinding.func_74507_a(var1);
                     }

                     if (this.field_83002_am > 0L) {
                        if (func_71386_F() - this.field_83002_am >= 6000L) {
                           throw new ReportedException(new CrashReport("Manually triggered debug crash", new Throwable()));
                        }

                        if (!Keyboard.isKeyDown(46) || !Keyboard.isKeyDown(61)) {
                           this.field_83002_am = -1L;
                        }
                     } else if (Keyboard.isKeyDown(46) && Keyboard.isKeyDown(61)) {
                        this.field_83002_am = func_71386_F();
                     }

                     this.func_152348_aa();
                  } while(!Keyboard.getEventKeyState());

                  if (var1 == 62 && this.field_71460_t != null) {
                     this.field_71460_t.func_175071_c();
                  }

                  if (this.field_71462_r != null) {
                     this.field_71462_r.func_146282_l();
                  } else {
                     if (var1 == 1) {
                        this.func_71385_j();
                     }

                     if (var1 == 32 && Keyboard.isKeyDown(61) && this.field_71456_v != null) {
                        this.field_71456_v.func_146158_b().func_146231_a();
                     }

                     if (var1 == 31 && Keyboard.isKeyDown(61)) {
                        this.func_110436_a();
                     }

                     if (var1 == 17 && Keyboard.isKeyDown(61)) {
                     }

                     if (var1 == 18 && Keyboard.isKeyDown(61)) {
                     }

                     if (var1 == 47 && Keyboard.isKeyDown(61)) {
                     }

                     if (var1 == 38 && Keyboard.isKeyDown(61)) {
                     }

                     if (var1 == 22 && Keyboard.isKeyDown(61)) {
                     }

                     if (var1 == 20 && Keyboard.isKeyDown(61)) {
                        this.func_110436_a();
                     }

                     if (var1 == 33 && Keyboard.isKeyDown(61)) {
                        this.field_71474_y.func_74306_a(GameSettings.Options.RENDER_DISTANCE, GuiScreen.func_146272_n() ? -1 : 1);
                     }

                     if (var1 == 30 && Keyboard.isKeyDown(61)) {
                        this.field_71438_f.func_72712_a();
                     }

                     if (var1 == 35 && Keyboard.isKeyDown(61)) {
                        this.field_71474_y.field_82882_x = !this.field_71474_y.field_82882_x;
                        this.field_71474_y.func_74303_b();
                     }

                     if (var1 == 48 && Keyboard.isKeyDown(61)) {
                        this.field_175616_W.func_178629_b(!this.field_175616_W.func_178634_b());
                     }

                     if (var1 == 25 && Keyboard.isKeyDown(61)) {
                        this.field_71474_y.field_82881_y = !this.field_71474_y.field_82881_y;
                        this.field_71474_y.func_74303_b();
                     }

                     if (var1 == 59) {
                        this.field_71474_y.field_74319_N = !this.field_71474_y.field_74319_N;
                     }

                     if (var1 == 61) {
                        this.field_71474_y.field_74330_P = !this.field_71474_y.field_74330_P;
                        this.field_71474_y.field_74329_Q = GuiScreen.func_146272_n();
                        this.field_71474_y.field_181657_aC = GuiScreen.func_175283_s();
                     }

                     if (this.field_71474_y.field_151457_aa.func_151468_f()) {
                        ++this.field_71474_y.field_74320_O;
                        if (this.field_71474_y.field_74320_O > 2) {
                           this.field_71474_y.field_74320_O = 0;
                        }

                        if (this.field_71474_y.field_74320_O == 0) {
                           this.field_71460_t.func_175066_a(this.func_175606_aa());
                        } else if (this.field_71474_y.field_74320_O == 1) {
                           this.field_71460_t.func_175066_a((Entity)null);
                        }

                        this.field_71438_f.func_174979_m();
                     }

                     if (this.field_71474_y.field_151458_ab.func_151468_f()) {
                        this.field_71474_y.field_74326_T = !this.field_71474_y.field_74326_T;
                     }
                  }
               } while(!this.field_71474_y.field_74330_P);
            } while(!this.field_71474_y.field_74329_Q);

            if (var1 == 11) {
               this.func_71383_b(0);
            }

            for(int var11 = 0; var11 < 9; ++var11) {
               if (var1 == 2 + var11) {
                  this.func_71383_b(var11 + 1);
               }
            }
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

            try {
               this.field_71441_e.func_72835_b();
            } catch (Throwable var8) {
               var2 = CrashReport.func_85055_a(var8, "Exception in world tick");
               if (this.field_71441_e == null) {
                  var3 = var2.func_85058_a("Affected level");
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

      this.field_71424_I.func_76319_b();
      this.field_71423_H = func_71386_F();
   }

   public void func_71371_a(String var1, String var2, WorldSettings var3) {
      this.func_71403_a((WorldClient)null);
      System.gc();
      ISaveHandler var4 = this.field_71469_aa.func_75804_a(var1, false);
      WorldInfo var5 = var4.func_75757_d();
      if (var5 == null && var3 != null) {
         var5 = new WorldInfo(var3, var1);
         var4.func_75761_a(var5);
      }

      if (var3 == null) {
         var3 = new WorldSettings(var5);
      }

      try {
         this.field_71437_Z = new IntegratedServer(this, var1, var2, var3);
         this.field_71437_Z.func_71256_s();
         this.field_71455_al = true;
      } catch (Throwable var10) {
         CrashReport var7 = CrashReport.func_85055_a(var10, "Starting integrated server");
         CrashReportCategory var8 = var7.func_85058_a("Starting integrated server");
         var8.func_71507_a("Level ID", var1);
         var8.func_71507_a("Level Name", var2);
         throw new ReportedException(var7);
      }

      this.field_71461_s.func_73720_a(I18n.func_135052_a("menu.loadingLevel"));

      while(!this.field_71437_Z.func_71200_ad()) {
         String var6 = this.field_71437_Z.func_71195_b_();
         if (var6 != null) {
            this.field_71461_s.func_73719_c(I18n.func_135052_a(var6));
         } else {
            this.field_71461_s.func_73719_c("");
         }

         try {
            Thread.sleep(200L);
         } catch (InterruptedException var9) {
         }
      }

      this.func_147108_a((GuiScreen)null);
      SocketAddress var11 = this.field_71437_Z.func_147137_ag().func_151270_a();
      NetworkManager var12 = NetworkManager.func_150722_a(var11);
      var12.func_150719_a(new NetHandlerLoginClient(var12, this, (GuiScreen)null));
      var12.func_179290_a(new C00Handshake(47, var11.toString(), 0, EnumConnectionState.LOGIN));
      var12.func_179290_a(new C00PacketLoginStart(this.func_110432_I().func_148256_e()));
      this.field_71453_ak = var12;
   }

   public void func_71403_a(WorldClient var1) {
      this.func_71353_a(var1, "");
   }

   public void func_71353_a(WorldClient var1, String var2) {
      if (var1 == null) {
         NetHandlerPlayClient var3 = this.func_147114_u();
         if (var3 != null) {
            var3.func_147296_c();
         }

         if (this.field_71437_Z != null && this.field_71437_Z.func_175578_N()) {
            this.field_71437_Z.func_71263_m();
            this.field_71437_Z.func_175592_a();
         }

         this.field_71437_Z = null;
         this.field_71458_u.func_146257_b();
         this.field_71460_t.func_147701_i().func_148249_a();
      }

      this.field_175622_Z = null;
      this.field_71453_ak = null;
      if (this.field_71461_s != null) {
         this.field_71461_s.func_73721_b(var2);
         this.field_71461_s.func_73719_c("");
      }

      if (var1 == null && this.field_71441_e != null) {
         this.field_110448_aq.func_148529_f();
         this.field_71456_v.func_181029_i();
         this.func_71351_a((ServerData)null);
         this.field_71455_al = false;
      }

      this.field_147127_av.func_147690_c();
      this.field_71441_e = var1;
      if (var1 != null) {
         if (this.field_71438_f != null) {
            this.field_71438_f.func_72732_a(var1);
         }

         if (this.field_71452_i != null) {
            this.field_71452_i.func_78870_a(var1);
         }

         if (this.field_71439_g == null) {
            this.field_71439_g = this.field_71442_b.func_178892_a(var1, new StatFileWriter());
            this.field_71442_b.func_78745_b(this.field_71439_g);
         }

         this.field_71439_g.func_70065_x();
         var1.func_72838_d(this.field_71439_g);
         this.field_71439_g.field_71158_b = new MovementInputFromOptions(this.field_71474_y);
         this.field_71442_b.func_78748_a(this.field_71439_g);
         this.field_175622_Z = this.field_71439_g;
      } else {
         this.field_71469_aa.func_75800_d();
         this.field_71439_g = null;
      }

      System.gc();
      this.field_71423_H = 0L;
   }

   public void func_71354_a(int var1) {
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
      this.field_71439_g = this.field_71442_b.func_178892_a(this.field_71441_e, this.field_71439_g == null ? new StatFileWriter() : this.field_71439_g.func_146107_m());
      this.field_71439_g.func_70096_w().func_75687_a(var4.func_70096_w().func_75685_c());
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

   public NetHandlerPlayClient func_147114_u() {
      return this.field_71439_g != null ? this.field_71439_g.field_71174_a : null;
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
      if (this.field_71476_x != null) {
         boolean var1 = this.field_71439_g.field_71075_bZ.field_75098_d;
         int var3 = 0;
         boolean var4 = false;
         TileEntity var5 = null;
         Object var2;
         ItemStack var7;
         if (this.field_71476_x.field_72313_a == MovingObjectPosition.MovingObjectType.BLOCK) {
            BlockPos var10 = this.field_71476_x.func_178782_a();
            Block var12 = this.field_71441_e.func_180495_p(var10).func_177230_c();
            if (var12.func_149688_o() == Material.field_151579_a) {
               return;
            }

            var2 = var12.func_180665_b(this.field_71441_e, var10);
            if (var2 == null) {
               return;
            }

            if (var1 && GuiScreen.func_146271_m()) {
               var5 = this.field_71441_e.func_175625_s(var10);
            }

            Block var8 = var2 instanceof ItemBlock && !var12.func_149648_K() ? Block.func_149634_a((Item)var2) : var12;
            var3 = var8.func_176222_j(this.field_71441_e, var10);
            var4 = ((Item)var2).func_77614_k();
         } else {
            if (this.field_71476_x.field_72313_a != MovingObjectPosition.MovingObjectType.ENTITY || this.field_71476_x.field_72308_g == null || !var1) {
               return;
            }

            if (this.field_71476_x.field_72308_g instanceof EntityPainting) {
               var2 = Items.field_151159_an;
            } else if (this.field_71476_x.field_72308_g instanceof EntityLeashKnot) {
               var2 = Items.field_151058_ca;
            } else if (this.field_71476_x.field_72308_g instanceof EntityItemFrame) {
               EntityItemFrame var6 = (EntityItemFrame)this.field_71476_x.field_72308_g;
               var7 = var6.func_82335_i();
               if (var7 == null) {
                  var2 = Items.field_151160_bD;
               } else {
                  var2 = var7.func_77973_b();
                  var3 = var7.func_77960_j();
                  var4 = true;
               }
            } else if (this.field_71476_x.field_72308_g instanceof EntityMinecart) {
               EntityMinecart var9 = (EntityMinecart)this.field_71476_x.field_72308_g;
               switch(var9.func_180456_s()) {
               case FURNACE:
                  var2 = Items.field_151109_aJ;
                  break;
               case CHEST:
                  var2 = Items.field_151108_aI;
                  break;
               case TNT:
                  var2 = Items.field_151142_bV;
                  break;
               case HOPPER:
                  var2 = Items.field_151140_bW;
                  break;
               case COMMAND_BLOCK:
                  var2 = Items.field_151095_cc;
                  break;
               default:
                  var2 = Items.field_151143_au;
               }
            } else if (this.field_71476_x.field_72308_g instanceof EntityBoat) {
               var2 = Items.field_151124_az;
            } else if (this.field_71476_x.field_72308_g instanceof EntityArmorStand) {
               var2 = Items.field_179565_cj;
            } else {
               var2 = Items.field_151063_bx;
               var3 = EntityList.func_75619_a(this.field_71476_x.field_72308_g);
               var4 = true;
               if (!EntityList.field_75627_a.containsKey(var3)) {
                  return;
               }
            }
         }

         InventoryPlayer var11 = this.field_71439_g.field_71071_by;
         if (var5 == null) {
            var11.func_146030_a((Item)var2, var3, var4, var1);
         } else {
            var7 = this.func_181036_a((Item)var2, var3, var5);
            var11.func_70299_a(var11.field_70461_c, var7);
         }

         if (var1) {
            int var13 = this.field_71439_g.field_71069_bz.field_75151_b.size() - 9 + var11.field_70461_c;
            this.field_71442_b.func_78761_a(var11.func_70301_a(var11.field_70461_c), var13);
         }

      }
   }

   private ItemStack func_181036_a(Item var1, int var2, TileEntity var3) {
      ItemStack var4 = new ItemStack(var1, 1, var2);
      NBTTagCompound var5 = new NBTTagCompound();
      var3.func_145841_b(var5);
      NBTTagCompound var6;
      if (var1 == Items.field_151144_bL && var5.func_74764_b("Owner")) {
         var6 = var5.func_74775_l("Owner");
         NBTTagCompound var8 = new NBTTagCompound();
         var8.func_74782_a("SkullOwner", var6);
         var4.func_77982_d(var8);
         return var4;
      } else {
         var4.func_77983_a("BlockEntityTag", var5);
         var6 = new NBTTagCompound();
         NBTTagList var7 = new NBTTagList();
         var7.func_74742_a(new NBTTagString("(+NBT)"));
         var6.func_74782_a("Lore", var7);
         var4.func_77983_a("display", var6);
         return var4;
      }
   }

   public CrashReport func_71396_d(CrashReport var1) {
      var1.func_85056_g().func_71500_a("Launched Version", new Callable<String>() {
         public String call() {
            return Minecraft.this.field_110447_Z;
         }

         // $FF: synthetic method
         public Object call() throws Exception {
            return this.call();
         }
      });
      var1.func_85056_g().func_71500_a("LWJGL", new Callable<String>() {
         public String call() {
            return Sys.getVersion();
         }

         // $FF: synthetic method
         public Object call() throws Exception {
            return this.call();
         }
      });
      var1.func_85056_g().func_71500_a("OpenGL", new Callable<String>() {
         public String call() {
            return GL11.glGetString(7937) + " GL version " + GL11.glGetString(7938) + ", " + GL11.glGetString(7936);
         }

         // $FF: synthetic method
         public Object call() throws Exception {
            return this.call();
         }
      });
      var1.func_85056_g().func_71500_a("GL Caps", new Callable<String>() {
         public String call() {
            return OpenGlHelper.func_153172_c();
         }

         // $FF: synthetic method
         public Object call() throws Exception {
            return this.call();
         }
      });
      var1.func_85056_g().func_71500_a("Using VBOs", new Callable<String>() {
         public String call() {
            return Minecraft.this.field_71474_y.field_178881_t ? "Yes" : "No";
         }

         // $FF: synthetic method
         public Object call() throws Exception {
            return this.call();
         }
      });
      var1.func_85056_g().func_71500_a("Is Modded", new Callable<String>() {
         public String call() throws Exception {
            String var1 = ClientBrandRetriever.getClientModName();
            if (!var1.equals("vanilla")) {
               return "Definitely; Client brand changed to '" + var1 + "'";
            } else {
               return Minecraft.class.getSigners() == null ? "Very likely; Jar signature invalidated" : "Probably not. Jar signature remains and client brand is untouched.";
            }
         }

         // $FF: synthetic method
         public Object call() throws Exception {
            return this.call();
         }
      });
      var1.func_85056_g().func_71500_a("Type", new Callable<String>() {
         public String call() throws Exception {
            return "Client (map_client.txt)";
         }

         // $FF: synthetic method
         public Object call() throws Exception {
            return this.call();
         }
      });
      var1.func_85056_g().func_71500_a("Resource Packs", new Callable<String>() {
         public String call() throws Exception {
            StringBuilder var1 = new StringBuilder();
            Iterator var2 = Minecraft.this.field_71474_y.field_151453_l.iterator();

            while(var2.hasNext()) {
               String var3 = (String)var2.next();
               if (var1.length() > 0) {
                  var1.append(", ");
               }

               var1.append(var3);
               if (Minecraft.this.field_71474_y.field_183018_l.contains(var3)) {
                  var1.append(" (incompatible)");
               }
            }

            return var1.toString();
         }

         // $FF: synthetic method
         public Object call() throws Exception {
            return this.call();
         }
      });
      var1.func_85056_g().func_71500_a("Current Language", new Callable<String>() {
         public String call() throws Exception {
            return Minecraft.this.field_135017_as.func_135041_c().toString();
         }

         // $FF: synthetic method
         public Object call() throws Exception {
            return this.call();
         }
      });
      var1.func_85056_g().func_71500_a("Profiler Position", new Callable<String>() {
         public String call() throws Exception {
            return Minecraft.this.field_71424_I.field_76327_a ? Minecraft.this.field_71424_I.func_76322_c() : "N/A (disabled)";
         }

         // $FF: synthetic method
         public Object call() throws Exception {
            return this.call();
         }
      });
      var1.func_85056_g().func_71500_a("CPU", new Callable<String>() {
         public String call() {
            return OpenGlHelper.func_183029_j();
         }

         // $FF: synthetic method
         public Object call() throws Exception {
            return this.call();
         }
      });
      if (this.field_71441_e != null) {
         this.field_71441_e.func_72914_a(var1);
      }

      return var1;
   }

   public static Minecraft func_71410_x() {
      return field_71432_P;
   }

   public ListenableFuture<Object> func_175603_A() {
      return this.func_152344_a(new Runnable() {
         public void run() {
            Minecraft.this.func_110436_a();
         }
      });
   }

   public void func_70000_a(PlayerUsageSnooper var1) {
      var1.func_152768_a("fps", field_71470_ab);
      var1.func_152768_a("vsync_enabled", this.field_71474_y.field_74352_v);
      var1.func_152768_a("display_frequency", Display.getDisplayMode().getFrequency());
      var1.func_152768_a("display_type", this.field_71431_Q ? "fullscreen" : "windowed");
      var1.func_152768_a("run_time", (MinecraftServer.func_130071_aq() - var1.func_130105_g()) / 60L * 1000L);
      var1.func_152768_a("current_action", this.func_181538_aA());
      String var2 = ByteOrder.nativeOrder() == ByteOrder.LITTLE_ENDIAN ? "little" : "big";
      var1.func_152768_a("endianness", var2);
      var1.func_152768_a("resource_packs", this.field_110448_aq.func_110613_c().size());
      int var3 = 0;
      Iterator var4 = this.field_110448_aq.func_110613_c().iterator();

      while(var4.hasNext()) {
         ResourcePackRepository.Entry var5 = (ResourcePackRepository.Entry)var4.next();
         var1.func_152768_a("resource_pack[" + var3++ + "]", var5.func_110515_d());
      }

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

   public void func_70001_b(PlayerUsageSnooper var1) {
      var1.func_152767_b("opengl_version", GL11.glGetString(7938));
      var1.func_152767_b("opengl_vendor", GL11.glGetString(7936));
      var1.func_152767_b("client_brand", ClientBrandRetriever.getClientModName());
      var1.func_152767_b("launched_version", this.field_110447_Z);
      ContextCapabilities var2 = GLContext.getCapabilities();
      var1.func_152767_b("gl_caps[ARB_arrays_of_arrays]", var2.GL_ARB_arrays_of_arrays);
      var1.func_152767_b("gl_caps[ARB_base_instance]", var2.GL_ARB_base_instance);
      var1.func_152767_b("gl_caps[ARB_blend_func_extended]", var2.GL_ARB_blend_func_extended);
      var1.func_152767_b("gl_caps[ARB_clear_buffer_object]", var2.GL_ARB_clear_buffer_object);
      var1.func_152767_b("gl_caps[ARB_color_buffer_float]", var2.GL_ARB_color_buffer_float);
      var1.func_152767_b("gl_caps[ARB_compatibility]", var2.GL_ARB_compatibility);
      var1.func_152767_b("gl_caps[ARB_compressed_texture_pixel_storage]", var2.GL_ARB_compressed_texture_pixel_storage);
      var1.func_152767_b("gl_caps[ARB_compute_shader]", var2.GL_ARB_compute_shader);
      var1.func_152767_b("gl_caps[ARB_copy_buffer]", var2.GL_ARB_copy_buffer);
      var1.func_152767_b("gl_caps[ARB_copy_image]", var2.GL_ARB_copy_image);
      var1.func_152767_b("gl_caps[ARB_depth_buffer_float]", var2.GL_ARB_depth_buffer_float);
      var1.func_152767_b("gl_caps[ARB_compute_shader]", var2.GL_ARB_compute_shader);
      var1.func_152767_b("gl_caps[ARB_copy_buffer]", var2.GL_ARB_copy_buffer);
      var1.func_152767_b("gl_caps[ARB_copy_image]", var2.GL_ARB_copy_image);
      var1.func_152767_b("gl_caps[ARB_depth_buffer_float]", var2.GL_ARB_depth_buffer_float);
      var1.func_152767_b("gl_caps[ARB_depth_clamp]", var2.GL_ARB_depth_clamp);
      var1.func_152767_b("gl_caps[ARB_depth_texture]", var2.GL_ARB_depth_texture);
      var1.func_152767_b("gl_caps[ARB_draw_buffers]", var2.GL_ARB_draw_buffers);
      var1.func_152767_b("gl_caps[ARB_draw_buffers_blend]", var2.GL_ARB_draw_buffers_blend);
      var1.func_152767_b("gl_caps[ARB_draw_elements_base_vertex]", var2.GL_ARB_draw_elements_base_vertex);
      var1.func_152767_b("gl_caps[ARB_draw_indirect]", var2.GL_ARB_draw_indirect);
      var1.func_152767_b("gl_caps[ARB_draw_instanced]", var2.GL_ARB_draw_instanced);
      var1.func_152767_b("gl_caps[ARB_explicit_attrib_location]", var2.GL_ARB_explicit_attrib_location);
      var1.func_152767_b("gl_caps[ARB_explicit_uniform_location]", var2.GL_ARB_explicit_uniform_location);
      var1.func_152767_b("gl_caps[ARB_fragment_layer_viewport]", var2.GL_ARB_fragment_layer_viewport);
      var1.func_152767_b("gl_caps[ARB_fragment_program]", var2.GL_ARB_fragment_program);
      var1.func_152767_b("gl_caps[ARB_fragment_shader]", var2.GL_ARB_fragment_shader);
      var1.func_152767_b("gl_caps[ARB_fragment_program_shadow]", var2.GL_ARB_fragment_program_shadow);
      var1.func_152767_b("gl_caps[ARB_framebuffer_object]", var2.GL_ARB_framebuffer_object);
      var1.func_152767_b("gl_caps[ARB_framebuffer_sRGB]", var2.GL_ARB_framebuffer_sRGB);
      var1.func_152767_b("gl_caps[ARB_geometry_shader4]", var2.GL_ARB_geometry_shader4);
      var1.func_152767_b("gl_caps[ARB_gpu_shader5]", var2.GL_ARB_gpu_shader5);
      var1.func_152767_b("gl_caps[ARB_half_float_pixel]", var2.GL_ARB_half_float_pixel);
      var1.func_152767_b("gl_caps[ARB_half_float_vertex]", var2.GL_ARB_half_float_vertex);
      var1.func_152767_b("gl_caps[ARB_instanced_arrays]", var2.GL_ARB_instanced_arrays);
      var1.func_152767_b("gl_caps[ARB_map_buffer_alignment]", var2.GL_ARB_map_buffer_alignment);
      var1.func_152767_b("gl_caps[ARB_map_buffer_range]", var2.GL_ARB_map_buffer_range);
      var1.func_152767_b("gl_caps[ARB_multisample]", var2.GL_ARB_multisample);
      var1.func_152767_b("gl_caps[ARB_multitexture]", var2.GL_ARB_multitexture);
      var1.func_152767_b("gl_caps[ARB_occlusion_query2]", var2.GL_ARB_occlusion_query2);
      var1.func_152767_b("gl_caps[ARB_pixel_buffer_object]", var2.GL_ARB_pixel_buffer_object);
      var1.func_152767_b("gl_caps[ARB_seamless_cube_map]", var2.GL_ARB_seamless_cube_map);
      var1.func_152767_b("gl_caps[ARB_shader_objects]", var2.GL_ARB_shader_objects);
      var1.func_152767_b("gl_caps[ARB_shader_stencil_export]", var2.GL_ARB_shader_stencil_export);
      var1.func_152767_b("gl_caps[ARB_shader_texture_lod]", var2.GL_ARB_shader_texture_lod);
      var1.func_152767_b("gl_caps[ARB_shadow]", var2.GL_ARB_shadow);
      var1.func_152767_b("gl_caps[ARB_shadow_ambient]", var2.GL_ARB_shadow_ambient);
      var1.func_152767_b("gl_caps[ARB_stencil_texturing]", var2.GL_ARB_stencil_texturing);
      var1.func_152767_b("gl_caps[ARB_sync]", var2.GL_ARB_sync);
      var1.func_152767_b("gl_caps[ARB_tessellation_shader]", var2.GL_ARB_tessellation_shader);
      var1.func_152767_b("gl_caps[ARB_texture_border_clamp]", var2.GL_ARB_texture_border_clamp);
      var1.func_152767_b("gl_caps[ARB_texture_buffer_object]", var2.GL_ARB_texture_buffer_object);
      var1.func_152767_b("gl_caps[ARB_texture_cube_map]", var2.GL_ARB_texture_cube_map);
      var1.func_152767_b("gl_caps[ARB_texture_cube_map_array]", var2.GL_ARB_texture_cube_map_array);
      var1.func_152767_b("gl_caps[ARB_texture_non_power_of_two]", var2.GL_ARB_texture_non_power_of_two);
      var1.func_152767_b("gl_caps[ARB_uniform_buffer_object]", var2.GL_ARB_uniform_buffer_object);
      var1.func_152767_b("gl_caps[ARB_vertex_blend]", var2.GL_ARB_vertex_blend);
      var1.func_152767_b("gl_caps[ARB_vertex_buffer_object]", var2.GL_ARB_vertex_buffer_object);
      var1.func_152767_b("gl_caps[ARB_vertex_program]", var2.GL_ARB_vertex_program);
      var1.func_152767_b("gl_caps[ARB_vertex_shader]", var2.GL_ARB_vertex_shader);
      var1.func_152767_b("gl_caps[EXT_bindable_uniform]", var2.GL_EXT_bindable_uniform);
      var1.func_152767_b("gl_caps[EXT_blend_equation_separate]", var2.GL_EXT_blend_equation_separate);
      var1.func_152767_b("gl_caps[EXT_blend_func_separate]", var2.GL_EXT_blend_func_separate);
      var1.func_152767_b("gl_caps[EXT_blend_minmax]", var2.GL_EXT_blend_minmax);
      var1.func_152767_b("gl_caps[EXT_blend_subtract]", var2.GL_EXT_blend_subtract);
      var1.func_152767_b("gl_caps[EXT_draw_instanced]", var2.GL_EXT_draw_instanced);
      var1.func_152767_b("gl_caps[EXT_framebuffer_multisample]", var2.GL_EXT_framebuffer_multisample);
      var1.func_152767_b("gl_caps[EXT_framebuffer_object]", var2.GL_EXT_framebuffer_object);
      var1.func_152767_b("gl_caps[EXT_framebuffer_sRGB]", var2.GL_EXT_framebuffer_sRGB);
      var1.func_152767_b("gl_caps[EXT_geometry_shader4]", var2.GL_EXT_geometry_shader4);
      var1.func_152767_b("gl_caps[EXT_gpu_program_parameters]", var2.GL_EXT_gpu_program_parameters);
      var1.func_152767_b("gl_caps[EXT_gpu_shader4]", var2.GL_EXT_gpu_shader4);
      var1.func_152767_b("gl_caps[EXT_multi_draw_arrays]", var2.GL_EXT_multi_draw_arrays);
      var1.func_152767_b("gl_caps[EXT_packed_depth_stencil]", var2.GL_EXT_packed_depth_stencil);
      var1.func_152767_b("gl_caps[EXT_paletted_texture]", var2.GL_EXT_paletted_texture);
      var1.func_152767_b("gl_caps[EXT_rescale_normal]", var2.GL_EXT_rescale_normal);
      var1.func_152767_b("gl_caps[EXT_separate_shader_objects]", var2.GL_EXT_separate_shader_objects);
      var1.func_152767_b("gl_caps[EXT_shader_image_load_store]", var2.GL_EXT_shader_image_load_store);
      var1.func_152767_b("gl_caps[EXT_shadow_funcs]", var2.GL_EXT_shadow_funcs);
      var1.func_152767_b("gl_caps[EXT_shared_texture_palette]", var2.GL_EXT_shared_texture_palette);
      var1.func_152767_b("gl_caps[EXT_stencil_clear_tag]", var2.GL_EXT_stencil_clear_tag);
      var1.func_152767_b("gl_caps[EXT_stencil_two_side]", var2.GL_EXT_stencil_two_side);
      var1.func_152767_b("gl_caps[EXT_stencil_wrap]", var2.GL_EXT_stencil_wrap);
      var1.func_152767_b("gl_caps[EXT_texture_3d]", var2.GL_EXT_texture_3d);
      var1.func_152767_b("gl_caps[EXT_texture_array]", var2.GL_EXT_texture_array);
      var1.func_152767_b("gl_caps[EXT_texture_buffer_object]", var2.GL_EXT_texture_buffer_object);
      var1.func_152767_b("gl_caps[EXT_texture_integer]", var2.GL_EXT_texture_integer);
      var1.func_152767_b("gl_caps[EXT_texture_lod_bias]", var2.GL_EXT_texture_lod_bias);
      var1.func_152767_b("gl_caps[EXT_texture_sRGB]", var2.GL_EXT_texture_sRGB);
      var1.func_152767_b("gl_caps[EXT_vertex_shader]", var2.GL_EXT_vertex_shader);
      var1.func_152767_b("gl_caps[EXT_vertex_weighting]", var2.GL_EXT_vertex_weighting);
      var1.func_152767_b("gl_caps[gl_max_vertex_uniforms]", GL11.glGetInteger(35658));
      GL11.glGetError();
      var1.func_152767_b("gl_caps[gl_max_fragment_uniforms]", GL11.glGetInteger(35657));
      GL11.glGetError();
      var1.func_152767_b("gl_caps[gl_max_vertex_attribs]", GL11.glGetInteger(34921));
      GL11.glGetError();
      var1.func_152767_b("gl_caps[gl_max_vertex_texture_image_units]", GL11.glGetInteger(35660));
      GL11.glGetError();
      var1.func_152767_b("gl_caps[gl_max_texture_image_units]", GL11.glGetInteger(34930));
      GL11.glGetError();
      var1.func_152767_b("gl_caps[gl_max_texture_image_units]", GL11.glGetInteger(35071));
      GL11.glGetError();
      var1.func_152767_b("gl_max_texture_size", func_71369_N());
   }

   public static int func_71369_N() {
      for(int var0 = 16384; var0 > 0; var0 >>= 1) {
         GL11.glTexImage2D(32868, 0, 6408, var0, var0, 0, 6408, 5121, (ByteBuffer)null);
         int var1 = GL11.glGetTexLevelParameteri(32868, 0, 4096);
         if (var1 != 0) {
            return var0;
         }
      }

      return -1;
   }

   public boolean func_70002_Q() {
      return this.field_71474_y.field_74355_t;
   }

   public void func_71351_a(ServerData var1) {
      this.field_71422_O = var1;
   }

   public ServerData func_147104_D() {
      return this.field_71422_O;
   }

   public boolean func_71387_A() {
      return this.field_71455_al;
   }

   public boolean func_71356_B() {
      return this.field_71455_al && this.field_71437_Z != null;
   }

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

   public PlayerUsageSnooper func_71378_E() {
      return this.field_71427_U;
   }

   public static long func_71386_F() {
      return Sys.getTime() * 1000L / Sys.getTimerResolution();
   }

   public boolean func_71372_G() {
      return this.field_71431_Q;
   }

   public Session func_110432_I() {
      return this.field_71449_j;
   }

   public PropertyMap func_180509_L() {
      return this.field_152356_J;
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

   public IResourceManager func_110442_L() {
      return this.field_110451_am;
   }

   public ResourcePackRepository func_110438_M() {
      return this.field_110448_aq;
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
      if (this.field_71439_g != null) {
         if (this.field_71439_g.field_70170_p.field_73011_w instanceof WorldProviderHell) {
            return MusicTicker.MusicType.NETHER;
         } else if (this.field_71439_g.field_70170_p.field_73011_w instanceof WorldProviderEnd) {
            return BossStatus.field_82827_c != null && BossStatus.field_82826_b > 0 ? MusicTicker.MusicType.END_BOSS : MusicTicker.MusicType.END;
         } else {
            return this.field_71439_g.field_71075_bZ.field_75098_d && this.field_71439_g.field_71075_bZ.field_75101_c ? MusicTicker.MusicType.CREATIVE : MusicTicker.MusicType.GAME;
         }
      } else {
         return MusicTicker.MusicType.MENU;
      }
   }

   public IStream func_152346_Z() {
      return this.field_152353_at;
   }

   public void func_152348_aa() {
      int var1 = Keyboard.getEventKey() == 0 ? Keyboard.getEventCharacter() : Keyboard.getEventKey();
      if (var1 != 0 && !Keyboard.isRepeatEvent()) {
         if (!(this.field_71462_r instanceof GuiControls) || ((GuiControls)this.field_71462_r).field_152177_g <= func_71386_F() - 20L) {
            if (Keyboard.getEventKeyState()) {
               if (var1 == this.field_71474_y.field_152396_an.func_151463_i()) {
                  if (this.func_152346_Z().func_152934_n()) {
                     this.func_152346_Z().func_152914_u();
                  } else if (this.func_152346_Z().func_152924_m()) {
                     this.func_147108_a(new GuiYesNo(new GuiYesNoCallback() {
                        public void func_73878_a(boolean var1, int var2) {
                           if (var1) {
                              Minecraft.this.func_152346_Z().func_152930_t();
                           }

                           Minecraft.this.func_147108_a((GuiScreen)null);
                        }
                     }, I18n.func_135052_a("stream.confirm_start"), "", 0));
                  } else if (this.func_152346_Z().func_152928_D() && this.func_152346_Z().func_152936_l()) {
                     if (this.field_71441_e != null) {
                        this.field_71456_v.func_146158_b().func_146227_a(new ChatComponentText("Not ready to start streaming yet!"));
                     }
                  } else {
                     GuiStreamUnavailable.func_152321_a(this.field_71462_r);
                  }
               } else if (var1 == this.field_71474_y.field_152397_ao.func_151463_i()) {
                  if (this.func_152346_Z().func_152934_n()) {
                     if (this.func_152346_Z().func_152919_o()) {
                        this.func_152346_Z().func_152933_r();
                     } else {
                        this.func_152346_Z().func_152916_q();
                     }
                  }
               } else if (var1 == this.field_71474_y.field_152398_ap.func_151463_i()) {
                  if (this.func_152346_Z().func_152934_n()) {
                     this.func_152346_Z().func_152931_p();
                  }
               } else if (var1 == this.field_71474_y.field_152399_aq.func_151463_i()) {
                  this.field_152353_at.func_152910_a(true);
               } else if (var1 == this.field_71474_y.field_152395_am.func_151463_i()) {
                  this.func_71352_k();
               } else if (var1 == this.field_71474_y.field_151447_Z.func_151463_i()) {
                  this.field_71456_v.func_146158_b().func_146227_a(ScreenShotHelper.func_148260_a(this.field_71412_D, this.field_71443_c, this.field_71440_d, this.field_147124_at));
               }
            } else if (var1 == this.field_71474_y.field_152399_aq.func_151463_i()) {
               this.field_152353_at.func_152910_a(false);
            }

         }
      }
   }

   public MinecraftSessionService func_152347_ac() {
      return this.field_152355_az;
   }

   public SkinManager func_152342_ad() {
      return this.field_152350_aA;
   }

   public Entity func_175606_aa() {
      return this.field_175622_Z;
   }

   public void func_175607_a(Entity var1) {
      this.field_175622_Z = var1;
      this.field_71460_t.func_175066_a(var1);
   }

   public <V> ListenableFuture<V> func_152343_a(Callable<V> var1) {
      Validate.notNull(var1);
      if (!this.func_152345_ab()) {
         ListenableFutureTask var2 = ListenableFutureTask.create(var1);
         synchronized(this.field_152351_aB) {
            this.field_152351_aB.add(var2);
            return var2;
         }
      } else {
         try {
            return Futures.immediateFuture(var1.call());
         } catch (Exception var6) {
            return Futures.immediateFailedCheckedFuture(var6);
         }
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

   public RenderItem func_175599_af() {
      return this.field_175621_X;
   }

   public ItemRenderer func_175597_ag() {
      return this.field_175620_Y;
   }

   public static int func_175610_ah() {
      return field_71470_ab;
   }

   public FrameTimer func_181539_aj() {
      return this.field_181542_y;
   }

   public static Map<String, String> func_175596_ai() {
      HashMap var0 = Maps.newHashMap();
      var0.put("X-Minecraft-Username", func_71410_x().func_110432_I().func_111285_a());
      var0.put("X-Minecraft-UUID", func_71410_x().func_110432_I().func_148255_b());
      var0.put("X-Minecraft-Version", "1.8.9");
      return var0;
   }

   public boolean func_181540_al() {
      return this.field_181541_X;
   }

   public void func_181537_a(boolean var1) {
      this.field_181541_X = var1;
   }

   static {
      field_142025_a = Util.func_110647_a() == Util.EnumOS.OSX;
      field_71444_a = new byte[10485760];
      field_110445_I = Lists.newArrayList(new DisplayMode[]{new DisplayMode(2560, 1600), new DisplayMode(2880, 1800)});
   }
}
