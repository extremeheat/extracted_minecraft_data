package net.minecraft.client.renderer.tileentity;

import java.util.HashMap;
import java.util.Map;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.entity.RenderEnchantmentTable;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.crash.CrashReport;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityBeacon;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraft.tileentity.TileEntityEnchantmentTable;
import net.minecraft.tileentity.TileEntityEndPortal;
import net.minecraft.tileentity.TileEntityEnderChest;
import net.minecraft.tileentity.TileEntityMobSpawner;
import net.minecraft.tileentity.TileEntityPiston;
import net.minecraft.tileentity.TileEntitySign;
import net.minecraft.tileentity.TileEntitySkull;
import net.minecraft.util.ReportedException;
import net.minecraft.world.World;
import org.lwjgl.opengl.GL11;

public class TileEntityRendererDispatcher {
   private Map field_147559_m = new HashMap();
   public static TileEntityRendererDispatcher field_147556_a = new TileEntityRendererDispatcher();
   private FontRenderer field_147557_n;
   public static double field_147554_b;
   public static double field_147555_c;
   public static double field_147552_d;
   public TextureManager field_147553_e;
   public World field_147550_f;
   public EntityLivingBase field_147551_g;
   public float field_147562_h;
   public float field_147563_i;
   public double field_147560_j;
   public double field_147561_k;
   public double field_147558_l;

   private TileEntityRendererDispatcher() {
      super();
      this.field_147559_m.put(TileEntitySign.class, new TileEntitySignRenderer());
      this.field_147559_m.put(TileEntityMobSpawner.class, new TileEntityMobSpawnerRenderer());
      this.field_147559_m.put(TileEntityPiston.class, new TileEntityRendererPiston());
      this.field_147559_m.put(TileEntityChest.class, new TileEntityChestRenderer());
      this.field_147559_m.put(TileEntityEnderChest.class, new TileEntityEnderChestRenderer());
      this.field_147559_m.put(TileEntityEnchantmentTable.class, new RenderEnchantmentTable());
      this.field_147559_m.put(TileEntityEndPortal.class, new RenderEndPortal());
      this.field_147559_m.put(TileEntityBeacon.class, new TileEntityBeaconRenderer());
      this.field_147559_m.put(TileEntitySkull.class, new TileEntitySkullRenderer());

      for(TileEntitySpecialRenderer var2 : this.field_147559_m.values()) {
         var2.func_147497_a(this);
      }
   }

   public TileEntitySpecialRenderer func_147546_a(Class var1) {
      TileEntitySpecialRenderer var2 = (TileEntitySpecialRenderer)this.field_147559_m.get(var1);
      if (var2 == null && var1 != TileEntity.class) {
         var2 = this.func_147546_a(var1.getSuperclass());
         this.field_147559_m.put(var1, var2);
      }

      return var2;
   }

   public boolean func_147545_a(TileEntity var1) {
      return this.func_147547_b(var1) != null;
   }

   public TileEntitySpecialRenderer func_147547_b(TileEntity var1) {
      return var1 == null ? null : this.func_147546_a(var1.getClass());
   }

   public void func_147542_a(World var1, TextureManager var2, FontRenderer var3, EntityLivingBase var4, float var5) {
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

   public void func_147544_a(TileEntity var1, float var2) {
      if (var1.func_145835_a(this.field_147560_j, this.field_147561_k, this.field_147558_l) < var1.func_145833_n()) {
         int var3 = this.field_147550_f.func_72802_i(var1.field_145851_c, var1.field_145848_d, var1.field_145849_e, 0);
         int var4 = var3 % 65536;
         int var5 = var3 / 65536;
         OpenGlHelper.func_77475_a(OpenGlHelper.field_77476_b, (float)var4 / 1.0F, (float)var5 / 1.0F);
         GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
         this.func_147549_a(
            var1,
            (double)var1.field_145851_c - field_147554_b,
            (double)var1.field_145848_d - field_147555_c,
            (double)var1.field_145849_e - field_147552_d,
            var2
         );
      }
   }

   public void func_147549_a(TileEntity var1, double var2, double var4, double var6, float var8) {
      TileEntitySpecialRenderer var9 = this.func_147547_b(var1);
      if (var9 != null) {
         try {
            var9.func_147500_a(var1, var2, var4, var6, var8);
         } catch (Throwable var13) {
            CrashReport var11 = CrashReport.func_85055_a(var13, "Rendering Block Entity");
            CrashReportCategory var12 = var11.func_85058_a("Block Entity Details");
            var1.func_145828_a(var12);
            throw new ReportedException(var11);
         }
      }
   }

   public void func_147543_a(World var1) {
      this.field_147550_f = var1;

      for(TileEntitySpecialRenderer var3 : this.field_147559_m.values()) {
         if (var3 != null) {
            var3.func_147496_a(var1);
         }
      }
   }

   public FontRenderer func_147548_a() {
      return this.field_147557_n;
   }
}
