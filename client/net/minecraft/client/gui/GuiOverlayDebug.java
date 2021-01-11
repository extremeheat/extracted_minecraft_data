package net.minecraft.client.gui;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import net.minecraft.block.Block;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.ClientBrandRetriever;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.FrameTimer;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.EnumSkyBlock;
import net.minecraft.world.WorldType;
import net.minecraft.world.chunk.Chunk;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;

public class GuiOverlayDebug extends Gui {
   private final Minecraft field_175242_a;
   private final FontRenderer field_175241_f;

   public GuiOverlayDebug(Minecraft var1) {
      super();
      this.field_175242_a = var1;
      this.field_175241_f = var1.field_71466_p;
   }

   public void func_175237_a(ScaledResolution var1) {
      this.field_175242_a.field_71424_I.func_76320_a("debug");
      GlStateManager.func_179094_E();
      this.func_180798_a();
      this.func_175239_b(var1);
      GlStateManager.func_179121_F();
      if (this.field_175242_a.field_71474_y.field_181657_aC) {
         this.func_181554_e();
      }

      this.field_175242_a.field_71424_I.func_76319_b();
   }

   private boolean func_175236_d() {
      return this.field_175242_a.field_71439_g.func_175140_cp() || this.field_175242_a.field_71474_y.field_178879_v;
   }

   protected void func_180798_a() {
      List var1 = this.call();

      for(int var2 = 0; var2 < var1.size(); ++var2) {
         String var3 = (String)var1.get(var2);
         if (!Strings.isNullOrEmpty(var3)) {
            int var4 = this.field_175241_f.field_78288_b;
            int var5 = this.field_175241_f.func_78256_a(var3);
            boolean var6 = true;
            int var7 = 2 + var4 * var2;
            func_73734_a(1, var7 - 1, 2 + var5 + 1, var7 + var4 - 1, -1873784752);
            this.field_175241_f.func_78276_b(var3, 2, var7, 14737632);
         }
      }

   }

   protected void func_175239_b(ScaledResolution var1) {
      List var2 = this.func_175238_c();

      for(int var3 = 0; var3 < var2.size(); ++var3) {
         String var4 = (String)var2.get(var3);
         if (!Strings.isNullOrEmpty(var4)) {
            int var5 = this.field_175241_f.field_78288_b;
            int var6 = this.field_175241_f.func_78256_a(var4);
            int var7 = var1.func_78326_a() - 2 - var6;
            int var8 = 2 + var5 * var3;
            func_73734_a(var7 - 1, var8 - 1, var7 + var6 + 1, var8 + var5 - 1, -1873784752);
            this.field_175241_f.func_78276_b(var4, var7, var8, 14737632);
         }
      }

   }

   protected List<String> call() {
      BlockPos var1 = new BlockPos(this.field_175242_a.func_175606_aa().field_70165_t, this.field_175242_a.func_175606_aa().func_174813_aQ().field_72338_b, this.field_175242_a.func_175606_aa().field_70161_v);
      if (this.func_175236_d()) {
         return Lists.newArrayList(new String[]{"Minecraft 1.8.9 (" + this.field_175242_a.func_175600_c() + "/" + ClientBrandRetriever.getClientModName() + ")", this.field_175242_a.field_71426_K, this.field_175242_a.field_71438_f.func_72735_c(), this.field_175242_a.field_71438_f.func_72723_d(), "P: " + this.field_175242_a.field_71452_i.func_78869_b() + ". T: " + this.field_175242_a.field_71441_e.func_72981_t(), this.field_175242_a.field_71441_e.func_72827_u(), "", String.format("Chunk-relative: %d %d %d", var1.func_177958_n() & 15, var1.func_177956_o() & 15, var1.func_177952_p() & 15)});
      } else {
         Entity var2 = this.field_175242_a.func_175606_aa();
         EnumFacing var3 = var2.func_174811_aO();
         String var4 = "Invalid";
         switch(var3) {
         case NORTH:
            var4 = "Towards negative Z";
            break;
         case SOUTH:
            var4 = "Towards positive Z";
            break;
         case WEST:
            var4 = "Towards negative X";
            break;
         case EAST:
            var4 = "Towards positive X";
         }

         ArrayList var5 = Lists.newArrayList(new String[]{"Minecraft 1.8.9 (" + this.field_175242_a.func_175600_c() + "/" + ClientBrandRetriever.getClientModName() + ")", this.field_175242_a.field_71426_K, this.field_175242_a.field_71438_f.func_72735_c(), this.field_175242_a.field_71438_f.func_72723_d(), "P: " + this.field_175242_a.field_71452_i.func_78869_b() + ". T: " + this.field_175242_a.field_71441_e.func_72981_t(), this.field_175242_a.field_71441_e.func_72827_u(), "", String.format("XYZ: %.3f / %.5f / %.3f", this.field_175242_a.func_175606_aa().field_70165_t, this.field_175242_a.func_175606_aa().func_174813_aQ().field_72338_b, this.field_175242_a.func_175606_aa().field_70161_v), String.format("Block: %d %d %d", var1.func_177958_n(), var1.func_177956_o(), var1.func_177952_p()), String.format("Chunk: %d %d %d in %d %d %d", var1.func_177958_n() & 15, var1.func_177956_o() & 15, var1.func_177952_p() & 15, var1.func_177958_n() >> 4, var1.func_177956_o() >> 4, var1.func_177952_p() >> 4), String.format("Facing: %s (%s) (%.1f / %.1f)", var3, var4, MathHelper.func_76142_g(var2.field_70177_z), MathHelper.func_76142_g(var2.field_70125_A))});
         if (this.field_175242_a.field_71441_e != null && this.field_175242_a.field_71441_e.func_175667_e(var1)) {
            Chunk var6 = this.field_175242_a.field_71441_e.func_175726_f(var1);
            var5.add("Biome: " + var6.func_177411_a(var1, this.field_175242_a.field_71441_e.func_72959_q()).field_76791_y);
            var5.add("Light: " + var6.func_177443_a(var1, 0) + " (" + var6.func_177413_a(EnumSkyBlock.SKY, var1) + " sky, " + var6.func_177413_a(EnumSkyBlock.BLOCK, var1) + " block)");
            DifficultyInstance var7 = this.field_175242_a.field_71441_e.func_175649_E(var1);
            if (this.field_175242_a.func_71387_A() && this.field_175242_a.func_71401_C() != null) {
               EntityPlayerMP var8 = this.field_175242_a.func_71401_C().func_71203_ab().func_177451_a(this.field_175242_a.field_71439_g.func_110124_au());
               if (var8 != null) {
                  var7 = var8.field_70170_p.func_175649_E(new BlockPos(var8));
               }
            }

            var5.add(String.format("Local Difficulty: %.2f (Day %d)", var7.func_180168_b(), this.field_175242_a.field_71441_e.func_72820_D() / 24000L));
         }

         if (this.field_175242_a.field_71460_t != null && this.field_175242_a.field_71460_t.func_147702_a()) {
            var5.add("Shader: " + this.field_175242_a.field_71460_t.func_147706_e().func_148022_b());
         }

         if (this.field_175242_a.field_71476_x != null && this.field_175242_a.field_71476_x.field_72313_a == MovingObjectPosition.MovingObjectType.BLOCK && this.field_175242_a.field_71476_x.func_178782_a() != null) {
            BlockPos var9 = this.field_175242_a.field_71476_x.func_178782_a();
            var5.add(String.format("Looking at: %d %d %d", var9.func_177958_n(), var9.func_177956_o(), var9.func_177952_p()));
         }

         return var5;
      }
   }

   protected List<String> func_175238_c() {
      long var1 = Runtime.getRuntime().maxMemory();
      long var3 = Runtime.getRuntime().totalMemory();
      long var5 = Runtime.getRuntime().freeMemory();
      long var7 = var3 - var5;
      ArrayList var9 = Lists.newArrayList(new String[]{String.format("Java: %s %dbit", System.getProperty("java.version"), this.field_175242_a.func_147111_S() ? 64 : 32), String.format("Mem: % 2d%% %03d/%03dMB", var7 * 100L / var1, func_175240_a(var7), func_175240_a(var1)), String.format("Allocated: % 2d%% %03dMB", var3 * 100L / var1, func_175240_a(var3)), "", String.format("CPU: %s", OpenGlHelper.func_183029_j()), "", String.format("Display: %dx%d (%s)", Display.getWidth(), Display.getHeight(), GL11.glGetString(7936)), GL11.glGetString(7937), GL11.glGetString(7938)});
      if (this.func_175236_d()) {
         return var9;
      } else {
         if (this.field_175242_a.field_71476_x != null && this.field_175242_a.field_71476_x.field_72313_a == MovingObjectPosition.MovingObjectType.BLOCK && this.field_175242_a.field_71476_x.func_178782_a() != null) {
            BlockPos var10 = this.field_175242_a.field_71476_x.func_178782_a();
            IBlockState var11 = this.field_175242_a.field_71441_e.func_180495_p(var10);
            if (this.field_175242_a.field_71441_e.func_175624_G() != WorldType.field_180272_g) {
               var11 = var11.func_177230_c().func_176221_a(var11, this.field_175242_a.field_71441_e, var10);
            }

            var9.add("");
            var9.add(String.valueOf(Block.field_149771_c.func_177774_c(var11.func_177230_c())));

            Entry var13;
            String var14;
            for(Iterator var12 = var11.func_177228_b().entrySet().iterator(); var12.hasNext(); var9.add(((IProperty)var13.getKey()).func_177701_a() + ": " + var14)) {
               var13 = (Entry)var12.next();
               var14 = ((Comparable)var13.getValue()).toString();
               if (var13.getValue() == Boolean.TRUE) {
                  var14 = EnumChatFormatting.GREEN + var14;
               } else if (var13.getValue() == Boolean.FALSE) {
                  var14 = EnumChatFormatting.RED + var14;
               }
            }
         }

         return var9;
      }
   }

   private void func_181554_e() {
      GlStateManager.func_179097_i();
      FrameTimer var1 = this.field_175242_a.func_181539_aj();
      int var2 = var1.func_181749_a();
      int var3 = var1.func_181750_b();
      long[] var4 = var1.func_181746_c();
      ScaledResolution var5 = new ScaledResolution(this.field_175242_a);
      int var6 = var2;
      int var7 = 0;
      func_73734_a(0, var5.func_78328_b() - 60, 240, var5.func_78328_b(), -1873784752);

      while(var6 != var3) {
         int var8 = var1.func_181748_a(var4[var6], 30);
         int var9 = this.func_181552_c(MathHelper.func_76125_a(var8, 0, 60), 0, 30, 60);
         this.func_73728_b(var7, var5.func_78328_b(), var5.func_78328_b() - var8, var9);
         ++var7;
         var6 = var1.func_181751_b(var6 + 1);
      }

      func_73734_a(1, var5.func_78328_b() - 30 + 1, 14, var5.func_78328_b() - 30 + 10, -1873784752);
      this.field_175241_f.func_78276_b("60", 2, var5.func_78328_b() - 30 + 2, 14737632);
      this.func_73730_a(0, 239, var5.func_78328_b() - 30, -1);
      func_73734_a(1, var5.func_78328_b() - 60 + 1, 14, var5.func_78328_b() - 60 + 10, -1873784752);
      this.field_175241_f.func_78276_b("30", 2, var5.func_78328_b() - 60 + 2, 14737632);
      this.func_73730_a(0, 239, var5.func_78328_b() - 60, -1);
      this.func_73730_a(0, 239, var5.func_78328_b() - 1, -1);
      this.func_73728_b(0, var5.func_78328_b() - 60, var5.func_78328_b(), -1);
      this.func_73728_b(239, var5.func_78328_b() - 60, var5.func_78328_b(), -1);
      if (this.field_175242_a.field_71474_y.field_74350_i <= 120) {
         this.func_73730_a(0, 239, var5.func_78328_b() - 60 + this.field_175242_a.field_71474_y.field_74350_i / 2, -16711681);
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
