package net.minecraft.tileentity;

import java.util.HashMap;
import java.util.Map;
import net.minecraft.block.Block;
import net.minecraft.block.BlockJukebox$TileEntityJukebox;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.Packet;
import net.minecraft.world.World;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class TileEntity {
   private static final Logger field_145852_a = LogManager.getLogger();
   private static Map field_145855_i = new HashMap();
   private static Map field_145853_j = new HashMap();
   protected World field_145850_b;
   public int field_145851_c;
   public int field_145848_d;
   public int field_145849_e;
   protected boolean field_145846_f;
   public int field_145847_g = -1;
   public Block field_145854_h;

   public TileEntity() {
      super();
   }

   private static void func_145826_a(Class var0, String var1) {
      if (field_145855_i.containsKey(var1)) {
         throw new IllegalArgumentException("Duplicate id: " + var1);
      } else {
         field_145855_i.put(var1, var0);
         field_145853_j.put(var0, var1);
      }
   }

   public World func_145831_w() {
      return this.field_145850_b;
   }

   public void func_145834_a(World var1) {
      this.field_145850_b = var1;
   }

   public boolean func_145830_o() {
      return this.field_145850_b != null;
   }

   public void func_145839_a(NBTTagCompound var1) {
      this.field_145851_c = var1.func_74762_e("x");
      this.field_145848_d = var1.func_74762_e("y");
      this.field_145849_e = var1.func_74762_e("z");
   }

   public void func_145841_b(NBTTagCompound var1) {
      String var2 = (String)field_145853_j.get(this.getClass());
      if (var2 == null) {
         throw new RuntimeException(this.getClass() + " is missing a mapping! This is a bug!");
      } else {
         var1.func_74778_a("id", var2);
         var1.func_74768_a("x", this.field_145851_c);
         var1.func_74768_a("y", this.field_145848_d);
         var1.func_74768_a("z", this.field_145849_e);
      }
   }

   public void func_145845_h() {
   }

   public static TileEntity func_145827_c(NBTTagCompound var0) {
      TileEntity var1 = null;

      try {
         Class var2 = (Class)field_145855_i.get(var0.func_74779_i("id"));
         if (var2 != null) {
            var1 = (TileEntity)var2.newInstance();
         }
      } catch (Exception var3) {
         var3.printStackTrace();
      }

      if (var1 != null) {
         var1.func_145839_a(var0);
      } else {
         field_145852_a.warn("Skipping BlockEntity with id " + var0.func_74779_i("id"));
      }

      return var1;
   }

   public int func_145832_p() {
      if (this.field_145847_g == -1) {
         this.field_145847_g = this.field_145850_b.func_72805_g(this.field_145851_c, this.field_145848_d, this.field_145849_e);
      }

      return this.field_145847_g;
   }

   public void func_70296_d() {
      if (this.field_145850_b != null) {
         this.field_145847_g = this.field_145850_b.func_72805_g(this.field_145851_c, this.field_145848_d, this.field_145849_e);
         this.field_145850_b.func_147476_b(this.field_145851_c, this.field_145848_d, this.field_145849_e, this);
         if (this.func_145838_q() != Blocks.field_150350_a) {
            this.field_145850_b.func_147453_f(this.field_145851_c, this.field_145848_d, this.field_145849_e, this.func_145838_q());
         }
      }
   }

   public double func_145835_a(double var1, double var3, double var5) {
      double var7 = (double)this.field_145851_c + 0.5 - var1;
      double var9 = (double)this.field_145848_d + 0.5 - var3;
      double var11 = (double)this.field_145849_e + 0.5 - var5;
      return var7 * var7 + var9 * var9 + var11 * var11;
   }

   public double func_145833_n() {
      return 4096.0;
   }

   public Block func_145838_q() {
      if (this.field_145854_h == null) {
         this.field_145854_h = this.field_145850_b.func_147439_a(this.field_145851_c, this.field_145848_d, this.field_145849_e);
      }

      return this.field_145854_h;
   }

   public Packet func_145844_m() {
      return null;
   }

   public boolean func_145837_r() {
      return this.field_145846_f;
   }

   public void func_145843_s() {
      this.field_145846_f = true;
   }

   public void func_145829_t() {
      this.field_145846_f = false;
   }

   public boolean func_145842_c(int var1, int var2) {
      return false;
   }

   public void func_145836_u() {
      this.field_145854_h = null;
      this.field_145847_g = -1;
   }

   public void func_145828_a(CrashReportCategory var1) {
      var1.func_71500_a("Name", new TileEntity$1(this));
      CrashReportCategory.func_147153_a(var1, this.field_145851_c, this.field_145848_d, this.field_145849_e, this.func_145838_q(), this.func_145832_p());
      var1.func_71500_a("Actual block type", new TileEntity$2(this));
      var1.func_71500_a("Actual block data value", new TileEntity$3(this));
   }

   static {
      func_145826_a(TileEntityFurnace.class, "Furnace");
      func_145826_a(TileEntityChest.class, "Chest");
      func_145826_a(TileEntityEnderChest.class, "EnderChest");
      func_145826_a(BlockJukebox$TileEntityJukebox.class, "RecordPlayer");
      func_145826_a(TileEntityDispenser.class, "Trap");
      func_145826_a(TileEntityDropper.class, "Dropper");
      func_145826_a(TileEntitySign.class, "Sign");
      func_145826_a(TileEntityMobSpawner.class, "MobSpawner");
      func_145826_a(TileEntityNote.class, "Music");
      func_145826_a(TileEntityPiston.class, "Piston");
      func_145826_a(TileEntityBrewingStand.class, "Cauldron");
      func_145826_a(TileEntityEnchantmentTable.class, "EnchantTable");
      func_145826_a(TileEntityEndPortal.class, "Airportal");
      func_145826_a(TileEntityCommandBlock.class, "Control");
      func_145826_a(TileEntityBeacon.class, "Beacon");
      func_145826_a(TileEntitySkull.class, "Skull");
      func_145826_a(TileEntityDaylightDetector.class, "DLDetector");
      func_145826_a(TileEntityHopper.class, "Hopper");
      func_145826_a(TileEntityComparator.class, "Comparator");
      func_145826_a(TileEntityFlowerPot.class, "FlowerPot");
   }
}
