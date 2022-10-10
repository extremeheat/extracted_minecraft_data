package net.minecraft.client.gui;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.google.common.collect.UnmodifiableIterator;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map.Entry;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.ClientBrandRetriever;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.fluid.IFluidState;
import net.minecraft.network.NetworkManager;
import net.minecraft.server.integrated.IntegratedServer;
import net.minecraft.state.IProperty;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.FrameTimer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceFluidMode;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.registry.IRegistry;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.EnumLightType;
import net.minecraft.world.ForcedChunksSaveData;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.dimension.DimensionType;

public class GuiOverlayDebug extends Gui {
   private final Minecraft field_175242_a;
   private final FontRenderer field_175241_f;
   private RayTraceResult field_211537_g;
   private RayTraceResult field_211538_h;

   public GuiOverlayDebug(Minecraft var1) {
      super();
      this.field_175242_a = var1;
      this.field_175241_f = var1.field_71466_p;
   }

   public void func_194818_a() {
      this.field_175242_a.field_71424_I.func_76320_a("debug");
      GlStateManager.func_179094_E();
      Entity var1 = this.field_175242_a.func_175606_aa();
      this.field_211537_g = var1.func_174822_a(20.0D, 0.0F, RayTraceFluidMode.NEVER);
      this.field_211538_h = var1.func_174822_a(20.0D, 0.0F, RayTraceFluidMode.ALWAYS);
      this.func_180798_a();
      this.func_194819_c();
      GlStateManager.func_179121_F();
      if (this.field_175242_a.field_71474_y.field_181657_aC) {
         this.func_181554_e();
      }

      this.field_175242_a.field_71424_I.func_76319_b();
   }

   protected void func_180798_a() {
      List var1 = this.func_209011_c();
      var1.add("");
      var1.add("Debug: Pie [shift]: " + (this.field_175242_a.field_71474_y.field_74329_Q ? "visible" : "hidden") + " FPS [alt]: " + (this.field_175242_a.field_71474_y.field_181657_aC ? "visible" : "hidden"));
      var1.add("For help: press F3 + Q");

      for(int var2 = 0; var2 < var1.size(); ++var2) {
         String var3 = (String)var1.get(var2);
         if (!Strings.isNullOrEmpty(var3)) {
            int var4 = this.field_175241_f.field_78288_b;
            int var5 = this.field_175241_f.func_78256_a(var3);
            boolean var6 = true;
            int var7 = 2 + var4 * var2;
            func_73734_a(1, var7 - 1, 2 + var5 + 1, var7 + var4 - 1, -1873784752);
            this.field_175241_f.func_211126_b(var3, 2.0F, (float)var7, 14737632);
         }
      }

   }

   protected void func_194819_c() {
      List var1 = this.func_175238_c();

      for(int var2 = 0; var2 < var1.size(); ++var2) {
         String var3 = (String)var1.get(var2);
         if (!Strings.isNullOrEmpty(var3)) {
            int var4 = this.field_175241_f.field_78288_b;
            int var5 = this.field_175241_f.func_78256_a(var3);
            int var6 = this.field_175242_a.field_195558_d.func_198107_o() - 2 - var5;
            int var7 = 2 + var4 * var2;
            func_73734_a(var6 - 1, var7 - 1, var6 + var5 + 1, var7 + var4 - 1, -1873784752);
            this.field_175241_f.func_211126_b(var3, (float)var6, (float)var7, 14737632);
         }
      }

   }

   protected List<String> func_209011_c() {
      IntegratedServer var2 = this.field_175242_a.func_71401_C();
      NetworkManager var3 = this.field_175242_a.func_147114_u().func_147298_b();
      float var4 = var3.func_211390_n();
      float var5 = var3.func_211393_m();
      String var1;
      if (var2 != null) {
         var1 = String.format("Integrated server @ %.0f ms ticks, %.0f tx, %.0f rx", var2.func_211149_aT(), var4, var5);
      } else {
         var1 = String.format("\"%s\" server, %.0f tx, %.0f rx", this.field_175242_a.field_71439_g.func_142021_k(), var4, var5);
      }

      BlockPos var6 = new BlockPos(this.field_175242_a.func_175606_aa().field_70165_t, this.field_175242_a.func_175606_aa().func_174813_aQ().field_72338_b, this.field_175242_a.func_175606_aa().field_70161_v);
      if (this.field_175242_a.func_189648_am()) {
         return Lists.newArrayList(new String[]{"Minecraft 1.13.2 (" + this.field_175242_a.func_175600_c() + "/" + ClientBrandRetriever.getClientModName() + ")", this.field_175242_a.field_71426_K, var1, this.field_175242_a.field_71438_f.func_72735_c(), this.field_175242_a.field_71438_f.func_72723_d(), "P: " + this.field_175242_a.field_71452_i.func_78869_b() + ". T: " + this.field_175242_a.field_71441_e.func_72981_t(), this.field_175242_a.field_71441_e.func_72827_u(), "", String.format("Chunk-relative: %d %d %d", var6.func_177958_n() & 15, var6.func_177956_o() & 15, var6.func_177952_p() & 15)});
      } else {
         Entity var7 = this.field_175242_a.func_175606_aa();
         EnumFacing var8 = var7.func_174811_aO();
         String var9 = "Invalid";
         switch(var8) {
         case NORTH:
            var9 = "Towards negative Z";
            break;
         case SOUTH:
            var9 = "Towards positive Z";
            break;
         case WEST:
            var9 = "Towards negative X";
            break;
         case EAST:
            var9 = "Towards positive X";
         }

         DimensionType var10 = this.field_175242_a.field_71441_e.field_73011_w.func_186058_p();
         Object var11;
         if (var2 != null && var2.func_71218_a(var10) != null) {
            var11 = var2.func_71218_a(var10);
         } else {
            var11 = this.field_175242_a.field_71441_e;
         }

         ForcedChunksSaveData var12 = (ForcedChunksSaveData)((World)var11).func_212411_a(var10, ForcedChunksSaveData::new, "chunks");
         ArrayList var13 = Lists.newArrayList(new String[]{"Minecraft 1.13.2 (" + this.field_175242_a.func_175600_c() + "/" + ClientBrandRetriever.getClientModName() + ("release".equalsIgnoreCase(this.field_175242_a.func_184123_d()) ? "" : "/" + this.field_175242_a.func_184123_d()) + ")", this.field_175242_a.field_71426_K, var1, this.field_175242_a.field_71438_f.func_72735_c(), this.field_175242_a.field_71438_f.func_72723_d(), "P: " + this.field_175242_a.field_71452_i.func_78869_b() + ". T: " + this.field_175242_a.field_71441_e.func_72981_t(), this.field_175242_a.field_71441_e.func_72827_u(), DimensionType.func_212678_a(var10).toString() + " FC: " + (var12 == null ? "n/a" : Integer.toString(var12.func_212438_a().size())), "", String.format(Locale.ROOT, "XYZ: %.3f / %.5f / %.3f", this.field_175242_a.func_175606_aa().field_70165_t, this.field_175242_a.func_175606_aa().func_174813_aQ().field_72338_b, this.field_175242_a.func_175606_aa().field_70161_v), String.format("Block: %d %d %d", var6.func_177958_n(), var6.func_177956_o(), var6.func_177952_p()), String.format("Chunk: %d %d %d in %d %d %d", var6.func_177958_n() & 15, var6.func_177956_o() & 15, var6.func_177952_p() & 15, var6.func_177958_n() >> 4, var6.func_177956_o() >> 4, var6.func_177952_p() >> 4), String.format(Locale.ROOT, "Facing: %s (%s) (%.1f / %.1f)", var8, var9, MathHelper.func_76142_g(var7.field_70177_z), MathHelper.func_76142_g(var7.field_70125_A))});
         if (this.field_175242_a.field_71441_e != null) {
            Chunk var14 = this.field_175242_a.field_71441_e.func_175726_f(var6);
            if (this.field_175242_a.field_71441_e.func_175667_e(var6) && var6.func_177956_o() >= 0 && var6.func_177956_o() < 256) {
               if (!var14.func_76621_g()) {
                  var13.add("Biome: " + IRegistry.field_212624_m.func_177774_c(var14.func_201600_k(var6)));
                  var13.add("Light: " + var14.func_201586_a(var6, 0, var14.func_177412_p().field_73011_w.func_191066_m()) + " (" + var14.func_201587_a(EnumLightType.SKY, var6, var14.func_177412_p().field_73011_w.func_191066_m()) + " sky, " + var14.func_201587_a(EnumLightType.BLOCK, var6, var14.func_177412_p().field_73011_w.func_191066_m()) + " block)");
                  DifficultyInstance var15 = this.field_175242_a.field_71441_e.func_175649_E(var6);
                  if (this.field_175242_a.func_71387_A() && var2 != null) {
                     EntityPlayerMP var16 = var2.func_184103_al().func_177451_a(this.field_175242_a.field_71439_g.func_110124_au());
                     if (var16 != null) {
                        var15 = var16.field_70170_p.func_175649_E(new BlockPos(var16));
                     }
                  }

                  var13.add(String.format(Locale.ROOT, "Local Difficulty: %.2f // %.2f (Day %d)", var15.func_180168_b(), var15.func_180170_c(), this.field_175242_a.field_71441_e.func_72820_D() / 24000L));
               } else {
                  var13.add("Waiting for chunk...");
               }
            } else {
               var13.add("Outside of world...");
            }
         }

         if (this.field_175242_a.field_71460_t != null && this.field_175242_a.field_71460_t.func_147702_a()) {
            var13.add("Shader: " + this.field_175242_a.field_71460_t.func_147706_e().func_148022_b());
         }

         BlockPos var17;
         if (this.field_211537_g != null && this.field_211537_g.field_72313_a == RayTraceResult.Type.BLOCK) {
            var17 = this.field_211537_g.func_178782_a();
            var13.add(String.format("Looking at block: %d %d %d", var17.func_177958_n(), var17.func_177956_o(), var17.func_177952_p()));
         }

         if (this.field_211538_h != null && this.field_211538_h.field_72313_a == RayTraceResult.Type.BLOCK) {
            var17 = this.field_211538_h.func_178782_a();
            var13.add(String.format("Looking at liquid: %d %d %d", var17.func_177958_n(), var17.func_177956_o(), var17.func_177952_p()));
         }

         return var13;
      }
   }

   protected List<String> func_175238_c() {
      long var1 = Runtime.getRuntime().maxMemory();
      long var3 = Runtime.getRuntime().totalMemory();
      long var5 = Runtime.getRuntime().freeMemory();
      long var7 = var3 - var5;
      ArrayList var9 = Lists.newArrayList(new String[]{String.format("Java: %s %dbit", System.getProperty("java.version"), this.field_175242_a.func_147111_S() ? 64 : 32), String.format("Mem: % 2d%% %03d/%03dMB", var7 * 100L / var1, func_175240_a(var7), func_175240_a(var1)), String.format("Allocated: % 2d%% %03dMB", var3 * 100L / var1, func_175240_a(var3)), "", String.format("CPU: %s", OpenGlHelper.func_183029_j()), "", String.format("Display: %dx%d (%s)", Minecraft.func_71410_x().field_195558_d.func_198109_k(), Minecraft.func_71410_x().field_195558_d.func_198091_l(), GlStateManager.func_187416_u(7936)), GlStateManager.func_187416_u(7937), GlStateManager.func_187416_u(7938)});
      if (this.field_175242_a.func_189648_am()) {
         return var9;
      } else {
         BlockPos var10;
         UnmodifiableIterator var12;
         Entry var13;
         Iterator var16;
         ResourceLocation var17;
         if (this.field_211537_g != null && this.field_211537_g.field_72313_a == RayTraceResult.Type.BLOCK) {
            var10 = this.field_211537_g.func_178782_a();
            IBlockState var11 = this.field_175242_a.field_71441_e.func_180495_p(var10);
            var9.add("");
            var9.add(TextFormatting.UNDERLINE + "Targeted Block");
            var9.add(String.valueOf(IRegistry.field_212618_g.func_177774_c(var11.func_177230_c())));
            var12 = var11.func_206871_b().entrySet().iterator();

            while(var12.hasNext()) {
               var13 = (Entry)var12.next();
               var9.add(this.func_211534_a(var13));
            }

            var16 = this.field_175242_a.func_147114_u().func_199724_l().func_199717_a().func_199913_a(var11.func_177230_c()).iterator();

            while(var16.hasNext()) {
               var17 = (ResourceLocation)var16.next();
               var9.add("#" + var17);
            }
         }

         if (this.field_211538_h != null && this.field_211538_h.field_72313_a == RayTraceResult.Type.BLOCK) {
            var10 = this.field_211538_h.func_178782_a();
            IFluidState var15 = this.field_175242_a.field_71441_e.func_204610_c(var10);
            var9.add("");
            var9.add(TextFormatting.UNDERLINE + "Targeted Fluid");
            var9.add(String.valueOf(IRegistry.field_212619_h.func_177774_c(var15.func_206886_c())));
            var12 = var15.func_206871_b().entrySet().iterator();

            while(var12.hasNext()) {
               var13 = (Entry)var12.next();
               var9.add(this.func_211534_a(var13));
            }

            var16 = this.field_175242_a.func_147114_u().func_199724_l().func_205704_c().func_199913_a(var15.func_206886_c()).iterator();

            while(var16.hasNext()) {
               var17 = (ResourceLocation)var16.next();
               var9.add("#" + var17);
            }
         }

         Entity var14 = this.field_175242_a.field_147125_j;
         if (var14 != null) {
            var9.add("");
            var9.add(TextFormatting.UNDERLINE + "Targeted Entity");
            var9.add(String.valueOf(IRegistry.field_212629_r.func_177774_c(var14.func_200600_R())));
         }

         return var9;
      }
   }

   private String func_211534_a(Entry<IProperty<?>, Comparable<?>> var1) {
      IProperty var2 = (IProperty)var1.getKey();
      Comparable var3 = (Comparable)var1.getValue();
      String var4 = Util.func_200269_a(var2, var3);
      if (Boolean.TRUE.equals(var3)) {
         var4 = TextFormatting.GREEN + var4;
      } else if (Boolean.FALSE.equals(var3)) {
         var4 = TextFormatting.RED + var4;
      }

      return var2.func_177701_a() + ": " + var4;
   }

   private void func_181554_e() {
      GlStateManager.func_179097_i();
      FrameTimer var1 = this.field_175242_a.func_181539_aj();
      int var2 = var1.func_181749_a();
      int var3 = var1.func_181750_b();
      long[] var4 = var1.func_181746_c();
      int var5 = var2;
      int var6 = 0;
      int var7 = this.field_175242_a.field_195558_d.func_198087_p();
      func_73734_a(0, var7 - 60, 240, var7, -1873784752);

      while(var5 != var3) {
         int var8 = var1.func_181748_a(var4[var5], 30);
         int var9 = this.func_181552_c(MathHelper.func_76125_a(var8, 0, 60), 0, 30, 60);
         this.func_73728_b(var6, var7, var7 - var8, var9);
         ++var6;
         var5 = var1.func_181751_b(var5 + 1);
      }

      func_73734_a(1, var7 - 30 + 1, 14, var7 - 30 + 10, -1873784752);
      this.field_175241_f.func_211126_b("60", 2.0F, (float)(var7 - 30 + 2), 14737632);
      this.func_73730_a(0, 239, var7 - 30, -1);
      func_73734_a(1, var7 - 60 + 1, 14, var7 - 60 + 10, -1873784752);
      this.field_175241_f.func_211126_b("30", 2.0F, (float)(var7 - 60 + 2), 14737632);
      this.func_73730_a(0, 239, var7 - 60, -1);
      this.func_73730_a(0, 239, var7 - 1, -1);
      this.func_73728_b(0, var7 - 60, var7, -1);
      this.func_73728_b(239, var7 - 60, var7, -1);
      if (this.field_175242_a.field_71474_y.field_74350_i <= 120) {
         this.func_73730_a(0, 239, var7 - 60 + this.field_175242_a.field_71474_y.field_74350_i / 2, -16711681);
      }

      GlStateManager.func_179126_j();
   }

   private int func_181552_c(int var1, int var2, int var3, int var4) {
      return var1 < var3 ? this.func_181553_a(-16711936, -256, (float)var1 / (float)var3) : this.func_181553_a(-256, -65536, (float)(var1 - var3) / (float)(var4 - var3));
   }

   private int func_181553_a(int var1, int var2, float var3) {
      int var4 = var1 >> 24 & 255;
      int var5 = var1 >> 16 & 255;
      int var6 = var1 >> 8 & 255;
      int var7 = var1 & 255;
      int var8 = var2 >> 24 & 255;
      int var9 = var2 >> 16 & 255;
      int var10 = var2 >> 8 & 255;
      int var11 = var2 & 255;
      int var12 = MathHelper.func_76125_a((int)((float)var4 + (float)(var8 - var4) * var3), 0, 255);
      int var13 = MathHelper.func_76125_a((int)((float)var5 + (float)(var9 - var5) * var3), 0, 255);
      int var14 = MathHelper.func_76125_a((int)((float)var6 + (float)(var10 - var6) * var3), 0, 255);
      int var15 = MathHelper.func_76125_a((int)((float)var7 + (float)(var11 - var7) * var3), 0, 255);
      return var12 << 24 | var13 << 16 | var14 << 8 | var15;
   }

   private static long func_175240_a(long var0) {
      return var0 / 1024L / 1024L;
   }
}
