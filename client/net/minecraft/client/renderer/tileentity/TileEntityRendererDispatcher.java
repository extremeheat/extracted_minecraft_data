package net.minecraft.client.renderer.tileentity;

import com.google.common.collect.Maps;
import java.util.Iterator;
import java.util.Map;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.crash.CrashReport;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.entity.Entity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityBanner;
import net.minecraft.tileentity.TileEntityBeacon;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraft.tileentity.TileEntityEnchantmentTable;
import net.minecraft.tileentity.TileEntityEndPortal;
import net.minecraft.tileentity.TileEntityEnderChest;
import net.minecraft.tileentity.TileEntityMobSpawner;
import net.minecraft.tileentity.TileEntityPiston;
import net.minecraft.tileentity.TileEntitySign;
import net.minecraft.tileentity.TileEntitySkull;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ReportedException;
import net.minecraft.world.World;

public class TileEntityRendererDispatcher {
   private Map<Class<? extends TileEntity>, TileEntitySpecialRenderer<? extends TileEntity>> field_147559_m = Maps.newHashMap();
   public static TileEntityRendererDispatcher field_147556_a = new TileEntityRendererDispatcher();
   private FontRenderer field_147557_n;
   public static double field_147554_b;
   public static double field_147555_c;
   public static double field_147552_d;
   public TextureManager field_147553_e;
   public World field_147550_f;
   public Entity field_147551_g;
   public float field_147562_h;
   public float field_147563_i;
   public double field_147560_j;
   public double field_147561_k;
   public double field_147558_l;

   private TileEntityRendererDispatcher() {
      super();
      this.field_147559_m.put(TileEntitySign.class, new TileEntitySignRenderer());
      this.field_147559_m.put(TileEntityMobSpawner.class, new TileEntityMobSpawnerRenderer());
      this.field_147559_m.put(TileEntityPiston.class, new TileEntityPistonRenderer());
      this.field_147559_m.put(TileEntityChest.class, new TileEntityChestRenderer());
      this.field_147559_m.put(TileEntityEnderChest.class, new TileEntityEnderChestRenderer());
      this.field_147559_m.put(TileEntityEnchantmentTable.class, new TileEntityEnchantmentTableRenderer());
      this.field_147559_m.put(TileEntityEndPortal.class, new TileEntityEndPortalRenderer());
      this.field_147559_m.put(TileEntityBeacon.class, new TileEntityBeaconRenderer());
      this.field_147559_m.put(TileEntitySkull.class, new TileEntitySkullRenderer());
      this.field_147559_m.put(TileEntityBanner.class, new TileEntityBannerRenderer());
      Iterator var1 = this.field_147559_m.values().iterator();

      while(var1.hasNext()) {
         TileEntitySpecialRenderer var2 = (TileEntitySpecialRenderer)var1.next();
         var2.func_147497_a(this);
      }

   }

   public <T extends TileEntity> TileEntitySpecialRenderer<T> func_147546_a(Class<? extends TileEntity> var1) {
      TileEntitySpecialRenderer var2 = (TileEntitySpecialRenderer)this.field_147559_m.get(var1);
      if (var2 == null && var1 != TileEntity.class) {
         var2 = this.func_147546_a(var1.getSuperclass());
         this.field_147559_m.put(var1, var2);
      }

      return var2;
   }

   public <T extends TileEntity> TileEntitySpecialRenderer<T> func_147547_b(TileEntity var1) {
      return var1 == null ? null : this.func_147546_a(var1.getClass());
   }

   public void func_178470_a(World var1, TextureManager var2, FontRenderer var3, Entity var4, float var5) {
      if (this.field_147550_f != var1) {
         this.func_147543_a(var1);
      }

      this.field_147553_e = var2;
      this.field_147551_g = var4;
      this.field_147557_n = var3;
      this.field_147562_h = var4.field_70126_B + (var4.field_70177_z - var4.field_70126_B) * var5;
      this.field_147563_i = var4.field_70127_C + (var4.field_70125_A - var4.field_70127_C) * var5;
      this.field_147560_j = var4.field_70142_S + (var4.field_70165_t - var4.field_70142_S) * (double)var5;
      this.field_147561_k = var4.field_70137_T + (var4.field_70163_u - var4.field_70137_T) * (double)var5;
      this.field_147558_l = var4.field_70136_U + (var4.field_70161_v - var4.field_70136_U) * (double)var5;
   }

   public void func_180546_a(TileEntity var1, float var2, int var3) {
      if (var1.func_145835_a(this.field_147560_j, this.field_147561_k, this.field_147558_l) < var1.func_145833_n()) {
         int var4 = this.field_147550_f.func_175626_b(var1.func_174877_v(), 0);
         int var5 = var4 % 65536;
         int var6 = var4 / 65536;
         OpenGlHelper.func_77475_a(OpenGlHelper.field_77476_b, (float)var5 / 1.0F, (float)var6 / 1.0F);
         GlStateManager.func_179131_c(1.0F, 1.0F, 1.0F, 1.0F);
         BlockPos var7 = var1.func_174877_v();
         this.func_178469_a(var1, (double)var7.func_177958_n() - field_147554_b, (double)var7.func_177956_o() - field_147555_c, (double)var7.func_177952_p() - field_147552_d, var2, var3);
      }

   }

   public void func_147549_a(TileEntity var1, double var2, double var4, double var6, float var8) {
      this.func_178469_a(var1, var2, var4, var6, var8, -1);
   }

   public void func_178469_a(TileEntity var1, double var2, double var4, double var6, float var8, int var9) {
      TileEntitySpecialRenderer var10 = this.func_147547_b(var1);
      if (var10 != null) {
         try {
            var10.func_180535_a(var1, var2, var4, var6, var8, var9);
         } catch (Throwable var14) {
            CrashReport var12 = CrashReport.func_85055_a(var14, "Rendering Block Entity");
            CrashReportCategory var13 = var12.func_85058_a("Block Entity Details");
            var1.func_145828_a(var13);
            throw new ReportedException(var12);
         }
      }

   }

   public void func_147543_a(World var1) {
      this.field_147550_f = var1;
   }

   public FontRenderer func_147548_a() {
      return this.field_147557_n;
   }
}
