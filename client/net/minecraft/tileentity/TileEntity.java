package net.minecraft.tileentity;

import javax.annotation.Nullable;
import net.minecraft.block.state.IBlockState;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.util.Mirror;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.IRegistry;
import net.minecraft.world.World;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public abstract class TileEntity {
   private static final Logger field_145852_a = LogManager.getLogger();
   private final TileEntityType<?> field_200663_e;
   protected World field_145850_b;
   protected BlockPos field_174879_c;
   protected boolean field_145846_f;
   @Nullable
   private IBlockState field_195045_e;

   public TileEntity(TileEntityType<?> var1) {
      super();
      this.field_174879_c = BlockPos.field_177992_a;
      this.field_200663_e = var1;
   }

   @Nullable
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
      this.field_174879_c = new BlockPos(var1.func_74762_e("x"), var1.func_74762_e("y"), var1.func_74762_e("z"));
   }

   public NBTTagCompound func_189515_b(NBTTagCompound var1) {
      return this.func_189516_d(var1);
   }

   private NBTTagCompound func_189516_d(NBTTagCompound var1) {
      ResourceLocation var2 = TileEntityType.func_200969_a(this.func_200662_C());
      if (var2 == null) {
         throw new RuntimeException(this.getClass() + " is missing a mapping! This is a bug!");
      } else {
         var1.func_74778_a("id", var2.toString());
         var1.func_74768_a("x", this.field_174879_c.func_177958_n());
         var1.func_74768_a("y", this.field_174879_c.func_177956_o());
         var1.func_74768_a("z", this.field_174879_c.func_177952_p());
         return var1;
      }
   }

   @Nullable
   public static TileEntity func_203403_c(NBTTagCompound var0) {
      TileEntity var1 = null;
      String var2 = var0.func_74779_i("id");

      try {
         var1 = TileEntityType.func_200967_a(var2);
      } catch (Throwable var5) {
         field_145852_a.error("Failed to create block entity {}", var2, var5);
      }

      if (var1 != null) {
         try {
            var1.func_145839_a(var0);
         } catch (Throwable var4) {
            field_145852_a.error("Failed to load data for block entity {}", var2, var4);
            var1 = null;
         }
      } else {
         field_145852_a.warn("Skipping BlockEntity with id {}", var2);
      }

      return var1;
   }

   public void func_70296_d() {
      if (this.field_145850_b != null) {
         this.field_195045_e = this.field_145850_b.func_180495_p(this.field_174879_c);
         this.field_145850_b.func_175646_b(this.field_174879_c, this);
         if (!this.field_195045_e.func_196958_f()) {
            this.field_145850_b.func_175666_e(this.field_174879_c, this.field_195045_e.func_177230_c());
         }
      }

   }

   public double func_145835_a(double var1, double var3, double var5) {
      double var7 = (double)this.field_174879_c.func_177958_n() + 0.5D - var1;
      double var9 = (double)this.field_174879_c.func_177956_o() + 0.5D - var3;
      double var11 = (double)this.field_174879_c.func_177952_p() + 0.5D - var5;
      return var7 * var7 + var9 * var9 + var11 * var11;
   }

   public double func_145833_n() {
      return 4096.0D;
   }

   public BlockPos func_174877_v() {
      return this.field_174879_c;
   }

   public IBlockState func_195044_w() {
      if (this.field_195045_e == null) {
         this.field_195045_e = this.field_145850_b.func_180495_p(this.field_174879_c);
      }

      return this.field_195045_e;
   }

   @Nullable
   public SPacketUpdateTileEntity func_189518_D_() {
      return null;
   }

   public NBTTagCompound func_189517_E_() {
      return this.func_189516_d(new NBTTagCompound());
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
      this.field_195045_e = null;
   }

   public void func_145828_a(CrashReportCategory var1) {
      var1.func_189529_a("Name", () -> {
         return IRegistry.field_212626_o.func_177774_c(this.func_200662_C()) + " // " + this.getClass().getCanonicalName();
      });
      if (this.field_145850_b != null) {
         CrashReportCategory.func_175750_a(var1, this.field_174879_c, this.func_195044_w());
         CrashReportCategory.func_175750_a(var1, this.field_174879_c, this.field_145850_b.func_180495_p(this.field_174879_c));
      }
   }

   public void func_174878_a(BlockPos var1) {
      this.field_174879_c = var1.func_185334_h();
   }

   public boolean func_183000_F() {
      return false;
   }

   public void func_189667_a(Rotation var1) {
   }

   public void func_189668_a(Mirror var1) {
   }

   public TileEntityType<?> func_200662_C() {
      return this.field_200663_e;
   }
}
