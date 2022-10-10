package net.minecraft.world.storage;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import javax.annotation.Nullable;
import net.minecraft.entity.item.EntityItemFrame;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemMap;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.INBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.SPacketMaps;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.dimension.DimensionType;

public class MapData extends WorldSavedData {
   public int field_76201_a;
   public int field_76199_b;
   public DimensionType field_76200_c;
   public boolean field_186210_e;
   public boolean field_191096_f;
   public byte field_76197_d;
   public byte[] field_76198_e = new byte[16384];
   public List<MapData.MapInfo> field_76196_g = Lists.newArrayList();
   private final Map<EntityPlayer, MapData.MapInfo> field_76202_j = Maps.newHashMap();
   private final Map<String, MapBanner> field_204270_k = Maps.newHashMap();
   public Map<String, MapDecoration> field_76203_h = Maps.newLinkedHashMap();
   private final Map<String, MapFrame> field_212442_l = Maps.newHashMap();

   public MapData(String var1) {
      super(var1);
   }

   public void func_212440_a(int var1, int var2, int var3, boolean var4, boolean var5, DimensionType var6) {
      this.field_76197_d = (byte)var3;
      this.func_176054_a((double)var1, (double)var2, this.field_76197_d);
      this.field_76200_c = var6;
      this.field_186210_e = var4;
      this.field_191096_f = var5;
      this.func_76185_a();
   }

   public void func_176054_a(double var1, double var3, int var5) {
      int var6 = 128 * (1 << var5);
      int var7 = MathHelper.func_76128_c((var1 + 64.0D) / (double)var6);
      int var8 = MathHelper.func_76128_c((var3 + 64.0D) / (double)var6);
      this.field_76201_a = var7 * var6 + var6 / 2 - 64;
      this.field_76199_b = var8 * var6 + var6 / 2 - 64;
   }

   public void func_76184_a(NBTTagCompound var1) {
      this.field_76200_c = DimensionType.func_186069_a(var1.func_74762_e("dimension"));
      this.field_76201_a = var1.func_74762_e("xCenter");
      this.field_76199_b = var1.func_74762_e("zCenter");
      this.field_76197_d = (byte)MathHelper.func_76125_a(var1.func_74771_c("scale"), 0, 4);
      this.field_186210_e = !var1.func_150297_b("trackingPosition", 1) || var1.func_74767_n("trackingPosition");
      this.field_191096_f = var1.func_74767_n("unlimitedTracking");
      this.field_76198_e = var1.func_74770_j("colors");
      if (this.field_76198_e.length != 16384) {
         this.field_76198_e = new byte[16384];
      }

      NBTTagList var2 = var1.func_150295_c("banners", 10);

      for(int var3 = 0; var3 < var2.size(); ++var3) {
         MapBanner var4 = MapBanner.func_204300_a(var2.func_150305_b(var3));
         this.field_204270_k.put(var4.func_204299_f(), var4);
         this.func_191095_a(var4.func_204305_c(), (IWorld)null, var4.func_204299_f(), (double)var4.func_204304_a().func_177958_n(), (double)var4.func_204304_a().func_177952_p(), 180.0D, var4.func_204302_d());
      }

      NBTTagList var6 = var1.func_150295_c("frames", 10);

      for(int var7 = 0; var7 < var6.size(); ++var7) {
         MapFrame var5 = MapFrame.func_212765_a(var6.func_150305_b(var7));
         this.field_212442_l.put(var5.func_212767_e(), var5);
         this.func_191095_a(MapDecoration.Type.FRAME, (IWorld)null, "frame-" + var5.func_212769_d(), (double)var5.func_212764_b().func_177958_n(), (double)var5.func_212764_b().func_177952_p(), (double)var5.func_212768_c(), (ITextComponent)null);
      }

   }

   public NBTTagCompound func_189551_b(NBTTagCompound var1) {
      var1.func_74768_a("dimension", this.field_76200_c.func_186068_a());
      var1.func_74768_a("xCenter", this.field_76201_a);
      var1.func_74768_a("zCenter", this.field_76199_b);
      var1.func_74774_a("scale", this.field_76197_d);
      var1.func_74773_a("colors", this.field_76198_e);
      var1.func_74757_a("trackingPosition", this.field_186210_e);
      var1.func_74757_a("unlimitedTracking", this.field_191096_f);
      NBTTagList var2 = new NBTTagList();
      Iterator var3 = this.field_204270_k.values().iterator();

      while(var3.hasNext()) {
         MapBanner var4 = (MapBanner)var3.next();
         var2.add((INBTBase)var4.func_204303_e());
      }

      var1.func_74782_a("banners", var2);
      NBTTagList var6 = new NBTTagList();
      Iterator var7 = this.field_212442_l.values().iterator();

      while(var7.hasNext()) {
         MapFrame var5 = (MapFrame)var7.next();
         var6.add((INBTBase)var5.func_212770_a());
      }

      var1.func_74782_a("frames", var6);
      return var1;
   }

   public void func_76191_a(EntityPlayer var1, ItemStack var2) {
      if (!this.field_76202_j.containsKey(var1)) {
         MapData.MapInfo var3 = new MapData.MapInfo(var1);
         this.field_76202_j.put(var1, var3);
         this.field_76196_g.add(var3);
      }

      if (!var1.field_71071_by.func_70431_c(var2)) {
         this.field_76203_h.remove(var1.func_200200_C_().getString());
      }

      for(int var7 = 0; var7 < this.field_76196_g.size(); ++var7) {
         MapData.MapInfo var4 = (MapData.MapInfo)this.field_76196_g.get(var7);
         String var5 = var4.field_76211_a.func_200200_C_().getString();
         if (!var4.field_76211_a.field_70128_L && (var4.field_76211_a.field_71071_by.func_70431_c(var2) || var2.func_82839_y())) {
            if (!var2.func_82839_y() && var4.field_76211_a.field_71093_bK == this.field_76200_c && this.field_186210_e) {
               this.func_191095_a(MapDecoration.Type.PLAYER, var4.field_76211_a.field_70170_p, var5, var4.field_76211_a.field_70165_t, var4.field_76211_a.field_70161_v, (double)var4.field_76211_a.field_70177_z, (ITextComponent)null);
            }
         } else {
            this.field_76202_j.remove(var4.field_76211_a);
            this.field_76196_g.remove(var4);
            this.field_76203_h.remove(var5);
         }
      }

      if (var2.func_82839_y() && this.field_186210_e) {
         EntityItemFrame var8 = var2.func_82836_z();
         BlockPos var9 = var8.func_174857_n();
         MapFrame var12 = (MapFrame)this.field_212442_l.get(MapFrame.func_212766_a(var9));
         if (var12 != null && var8.func_145782_y() != var12.func_212769_d() && this.field_212442_l.containsKey(var12.func_212767_e())) {
            this.field_76203_h.remove("frame-" + var12.func_212769_d());
         }

         MapFrame var6 = new MapFrame(var9, var8.field_174860_b.func_176736_b() * 90, var8.func_145782_y());
         this.func_191095_a(MapDecoration.Type.FRAME, var1.field_70170_p, "frame-" + var8.func_145782_y(), (double)var9.func_177958_n(), (double)var9.func_177952_p(), (double)(var8.field_174860_b.func_176736_b() * 90), (ITextComponent)null);
         this.field_212442_l.put(var6.func_212767_e(), var6);
      }

      NBTTagCompound var10 = var2.func_77978_p();
      if (var10 != null && var10.func_150297_b("Decorations", 9)) {
         NBTTagList var11 = var10.func_150295_c("Decorations", 10);

         for(int var13 = 0; var13 < var11.size(); ++var13) {
            NBTTagCompound var14 = var11.func_150305_b(var13);
            if (!this.field_76203_h.containsKey(var14.func_74779_i("id"))) {
               this.func_191095_a(MapDecoration.Type.func_191159_a(var14.func_74771_c("type")), var1.field_70170_p, var14.func_74779_i("id"), var14.func_74769_h("x"), var14.func_74769_h("z"), var14.func_74769_h("rot"), (ITextComponent)null);
            }
         }
      }

   }

   public static void func_191094_a(ItemStack var0, BlockPos var1, String var2, MapDecoration.Type var3) {
      NBTTagList var4;
      if (var0.func_77942_o() && var0.func_77978_p().func_150297_b("Decorations", 9)) {
         var4 = var0.func_77978_p().func_150295_c("Decorations", 10);
      } else {
         var4 = new NBTTagList();
         var0.func_77983_a("Decorations", var4);
      }

      NBTTagCompound var5 = new NBTTagCompound();
      var5.func_74774_a("type", var3.func_191163_a());
      var5.func_74778_a("id", var2);
      var5.func_74780_a("x", (double)var1.func_177958_n());
      var5.func_74780_a("z", (double)var1.func_177952_p());
      var5.func_74780_a("rot", 180.0D);
      var4.add((INBTBase)var5);
      if (var3.func_191162_c()) {
         NBTTagCompound var6 = var0.func_190925_c("display");
         var6.func_74768_a("MapColor", var3.func_191161_d());
      }

   }

   private void func_191095_a(MapDecoration.Type var1, @Nullable IWorld var2, String var3, double var4, double var6, double var8, @Nullable ITextComponent var10) {
      int var11 = 1 << this.field_76197_d;
      float var12 = (float)(var4 - (double)this.field_76201_a) / (float)var11;
      float var13 = (float)(var6 - (double)this.field_76199_b) / (float)var11;
      byte var14 = (byte)((int)((double)(var12 * 2.0F) + 0.5D));
      byte var15 = (byte)((int)((double)(var13 * 2.0F) + 0.5D));
      boolean var17 = true;
      byte var16;
      if (var12 >= -63.0F && var13 >= -63.0F && var12 <= 63.0F && var13 <= 63.0F) {
         var8 += var8 < 0.0D ? -8.0D : 8.0D;
         var16 = (byte)((int)(var8 * 16.0D / 360.0D));
         if (this.field_76200_c == DimensionType.NETHER && var2 != null) {
            int var19 = (int)(var2.func_72912_H().func_76073_f() / 10L);
            var16 = (byte)(var19 * var19 * 34187121 + var19 * 121 >> 15 & 15);
         }
      } else {
         if (var1 != MapDecoration.Type.PLAYER) {
            this.field_76203_h.remove(var3);
            return;
         }

         boolean var18 = true;
         if (Math.abs(var12) < 320.0F && Math.abs(var13) < 320.0F) {
            var1 = MapDecoration.Type.PLAYER_OFF_MAP;
         } else {
            if (!this.field_191096_f) {
               this.field_76203_h.remove(var3);
               return;
            }

            var1 = MapDecoration.Type.PLAYER_OFF_LIMITS;
         }

         var16 = 0;
         if (var12 <= -63.0F) {
            var14 = -128;
         }

         if (var13 <= -63.0F) {
            var15 = -128;
         }

         if (var12 >= 63.0F) {
            var14 = 127;
         }

         if (var13 >= 63.0F) {
            var15 = 127;
         }
      }

      this.field_76203_h.put(var3, new MapDecoration(var1, var14, var15, var16, var10));
   }

   @Nullable
   public Packet<?> func_176052_a(ItemStack var1, IBlockReader var2, EntityPlayer var3) {
      MapData.MapInfo var4 = (MapData.MapInfo)this.field_76202_j.get(var3);
      return var4 == null ? null : var4.func_176101_a(var1);
   }

   public void func_176053_a(int var1, int var2) {
      this.func_76185_a();
      Iterator var3 = this.field_76196_g.iterator();

      while(var3.hasNext()) {
         MapData.MapInfo var4 = (MapData.MapInfo)var3.next();
         var4.func_176102_a(var1, var2);
      }

   }

   public MapData.MapInfo func_82568_a(EntityPlayer var1) {
      MapData.MapInfo var2 = (MapData.MapInfo)this.field_76202_j.get(var1);
      if (var2 == null) {
         var2 = new MapData.MapInfo(var1);
         this.field_76202_j.put(var1, var2);
         this.field_76196_g.add(var2);
      }

      return var2;
   }

   public void func_204269_a(IWorld var1, BlockPos var2) {
      float var3 = (float)var2.func_177958_n() + 0.5F;
      float var4 = (float)var2.func_177952_p() + 0.5F;
      int var5 = 1 << this.field_76197_d;
      float var6 = (var3 - (float)this.field_76201_a) / (float)var5;
      float var7 = (var4 - (float)this.field_76199_b) / (float)var5;
      boolean var8 = true;
      boolean var9 = false;
      if (var6 >= -63.0F && var7 >= -63.0F && var6 <= 63.0F && var7 <= 63.0F) {
         MapBanner var10 = MapBanner.func_204301_a(var1, var2);
         if (var10 == null) {
            return;
         }

         boolean var11 = true;
         if (this.field_204270_k.containsKey(var10.func_204299_f()) && ((MapBanner)this.field_204270_k.get(var10.func_204299_f())).equals(var10)) {
            this.field_204270_k.remove(var10.func_204299_f());
            this.field_76203_h.remove(var10.func_204299_f());
            var11 = false;
            var9 = true;
         }

         if (var11) {
            this.field_204270_k.put(var10.func_204299_f(), var10);
            this.func_191095_a(var10.func_204305_c(), var1, var10.func_204299_f(), (double)var3, (double)var4, 180.0D, var10.func_204302_d());
            var9 = true;
         }

         if (var9) {
            this.func_76185_a();
         }
      }

   }

   public void func_204268_a(IBlockReader var1, int var2, int var3) {
      Iterator var4 = this.field_204270_k.values().iterator();

      while(var4.hasNext()) {
         MapBanner var5 = (MapBanner)var4.next();
         if (var5.func_204304_a().func_177958_n() == var2 && var5.func_204304_a().func_177952_p() == var3) {
            MapBanner var6 = MapBanner.func_204301_a(var1, var5.func_204304_a());
            if (!var5.equals(var6)) {
               var4.remove();
               this.field_76203_h.remove(var5.func_204299_f());
            }
         }
      }

   }

   public void func_212441_a(BlockPos var1, int var2) {
      this.field_76203_h.remove("frame-" + var2);
      this.field_212442_l.remove(MapFrame.func_212766_a(var1));
   }

   public class MapInfo {
      public final EntityPlayer field_76211_a;
      private boolean field_176105_d = true;
      private int field_176106_e;
      private int field_176103_f;
      private int field_176104_g = 127;
      private int field_176108_h = 127;
      private int field_176109_i;
      public int field_82569_d;

      public MapInfo(EntityPlayer var2) {
         super();
         this.field_76211_a = var2;
      }

      @Nullable
      public Packet<?> func_176101_a(ItemStack var1) {
         if (this.field_176105_d) {
            this.field_176105_d = false;
            return new SPacketMaps(ItemMap.func_195949_f(var1), MapData.this.field_76197_d, MapData.this.field_186210_e, MapData.this.field_76203_h.values(), MapData.this.field_76198_e, this.field_176106_e, this.field_176103_f, this.field_176104_g + 1 - this.field_176106_e, this.field_176108_h + 1 - this.field_176103_f);
         } else {
            return this.field_176109_i++ % 5 == 0 ? new SPacketMaps(ItemMap.func_195949_f(var1), MapData.this.field_76197_d, MapData.this.field_186210_e, MapData.this.field_76203_h.values(), MapData.this.field_76198_e, 0, 0, 0, 0) : null;
         }
      }

      public void func_176102_a(int var1, int var2) {
         if (this.field_176105_d) {
            this.field_176106_e = Math.min(this.field_176106_e, var1);
            this.field_176103_f = Math.min(this.field_176103_f, var2);
            this.field_176104_g = Math.max(this.field_176104_g, var1);
            this.field_176108_h = Math.max(this.field_176108_h, var2);
         } else {
            this.field_176105_d = true;
            this.field_176106_e = var1;
            this.field_176103_f = var2;
            this.field_176104_g = var1;
            this.field_176108_h = var2;
         }

      }
   }
}
