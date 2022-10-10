package net.minecraft.tileentity;

import com.google.common.collect.Lists;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.BlockStructure;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.state.properties.StructureMode;
import net.minecraft.util.Mirror;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.ResourceLocationException;
import net.minecraft.util.Rotation;
import net.minecraft.util.StringUtils;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.MutableBoundingBox;
import net.minecraft.world.WorldServer;
import net.minecraft.world.gen.feature.template.PlacementSettings;
import net.minecraft.world.gen.feature.template.Template;
import net.minecraft.world.gen.feature.template.TemplateManager;

public class TileEntityStructure extends TileEntity {
   private ResourceLocation field_184420_a;
   private String field_184421_f = "";
   private String field_184422_g = "";
   private BlockPos field_184423_h = new BlockPos(0, 1, 0);
   private BlockPos field_184424_i;
   private Mirror field_184425_j;
   private Rotation field_184426_k;
   private StructureMode field_184427_l;
   private boolean field_184428_m;
   private boolean field_189727_n;
   private boolean field_189728_o;
   private boolean field_189729_p;
   private float field_189730_q;
   private long field_189731_r;

   public TileEntityStructure() {
      super(TileEntityType.field_200990_u);
      this.field_184424_i = BlockPos.field_177992_a;
      this.field_184425_j = Mirror.NONE;
      this.field_184426_k = Rotation.NONE;
      this.field_184427_l = StructureMode.DATA;
      this.field_184428_m = true;
      this.field_189729_p = true;
      this.field_189730_q = 1.0F;
   }

   public NBTTagCompound func_189515_b(NBTTagCompound var1) {
      super.func_189515_b(var1);
      var1.func_74778_a("name", this.func_189715_d());
      var1.func_74778_a("author", this.field_184421_f);
      var1.func_74778_a("metadata", this.field_184422_g);
      var1.func_74768_a("posX", this.field_184423_h.func_177958_n());
      var1.func_74768_a("posY", this.field_184423_h.func_177956_o());
      var1.func_74768_a("posZ", this.field_184423_h.func_177952_p());
      var1.func_74768_a("sizeX", this.field_184424_i.func_177958_n());
      var1.func_74768_a("sizeY", this.field_184424_i.func_177956_o());
      var1.func_74768_a("sizeZ", this.field_184424_i.func_177952_p());
      var1.func_74778_a("rotation", this.field_184426_k.toString());
      var1.func_74778_a("mirror", this.field_184425_j.toString());
      var1.func_74778_a("mode", this.field_184427_l.toString());
      var1.func_74757_a("ignoreEntities", this.field_184428_m);
      var1.func_74757_a("powered", this.field_189727_n);
      var1.func_74757_a("showair", this.field_189728_o);
      var1.func_74757_a("showboundingbox", this.field_189729_p);
      var1.func_74776_a("integrity", this.field_189730_q);
      var1.func_74772_a("seed", this.field_189731_r);
      return var1;
   }

   public void func_145839_a(NBTTagCompound var1) {
      super.func_145839_a(var1);
      this.func_184404_a(var1.func_74779_i("name"));
      this.field_184421_f = var1.func_74779_i("author");
      this.field_184422_g = var1.func_74779_i("metadata");
      int var2 = MathHelper.func_76125_a(var1.func_74762_e("posX"), -32, 32);
      int var3 = MathHelper.func_76125_a(var1.func_74762_e("posY"), -32, 32);
      int var4 = MathHelper.func_76125_a(var1.func_74762_e("posZ"), -32, 32);
      this.field_184423_h = new BlockPos(var2, var3, var4);
      int var5 = MathHelper.func_76125_a(var1.func_74762_e("sizeX"), 0, 32);
      int var6 = MathHelper.func_76125_a(var1.func_74762_e("sizeY"), 0, 32);
      int var7 = MathHelper.func_76125_a(var1.func_74762_e("sizeZ"), 0, 32);
      this.field_184424_i = new BlockPos(var5, var6, var7);

      try {
         this.field_184426_k = Rotation.valueOf(var1.func_74779_i("rotation"));
      } catch (IllegalArgumentException var11) {
         this.field_184426_k = Rotation.NONE;
      }

      try {
         this.field_184425_j = Mirror.valueOf(var1.func_74779_i("mirror"));
      } catch (IllegalArgumentException var10) {
         this.field_184425_j = Mirror.NONE;
      }

      try {
         this.field_184427_l = StructureMode.valueOf(var1.func_74779_i("mode"));
      } catch (IllegalArgumentException var9) {
         this.field_184427_l = StructureMode.DATA;
      }

      this.field_184428_m = var1.func_74767_n("ignoreEntities");
      this.field_189727_n = var1.func_74767_n("powered");
      this.field_189728_o = var1.func_74767_n("showair");
      this.field_189729_p = var1.func_74767_n("showboundingbox");
      if (var1.func_74764_b("integrity")) {
         this.field_189730_q = var1.func_74760_g("integrity");
      } else {
         this.field_189730_q = 1.0F;
      }

      this.field_189731_r = var1.func_74763_f("seed");
      this.func_189704_J();
   }

   private void func_189704_J() {
      if (this.field_145850_b != null) {
         BlockPos var1 = this.func_174877_v();
         IBlockState var2 = this.field_145850_b.func_180495_p(var1);
         if (var2.func_177230_c() == Blocks.field_185779_df) {
            this.field_145850_b.func_180501_a(var1, (IBlockState)var2.func_206870_a(BlockStructure.field_185587_a, this.field_184427_l), 2);
         }

      }
   }

   @Nullable
   public SPacketUpdateTileEntity func_189518_D_() {
      return new SPacketUpdateTileEntity(this.field_174879_c, 7, this.func_189517_E_());
   }

   public NBTTagCompound func_189517_E_() {
      return this.func_189515_b(new NBTTagCompound());
   }

   public boolean func_189701_a(EntityPlayer var1) {
      if (!var1.func_195070_dx()) {
         return false;
      } else {
         if (var1.func_130014_f_().field_72995_K) {
            var1.func_189807_a(this);
         }

         return true;
      }
   }

   public String func_189715_d() {
      return this.field_184420_a == null ? "" : this.field_184420_a.toString();
   }

   public boolean func_208404_d() {
      return this.field_184420_a != null;
   }

   public void func_184404_a(@Nullable String var1) {
      this.func_210163_a(StringUtils.func_151246_b(var1) ? null : ResourceLocation.func_208304_a(var1));
   }

   public void func_210163_a(@Nullable ResourceLocation var1) {
      this.field_184420_a = var1;
   }

   public void func_189720_a(EntityLivingBase var1) {
      this.field_184421_f = var1.func_200200_C_().getString();
   }

   public BlockPos func_189711_e() {
      return this.field_184423_h;
   }

   public void func_184414_b(BlockPos var1) {
      this.field_184423_h = var1;
   }

   public BlockPos func_189717_g() {
      return this.field_184424_i;
   }

   public void func_184409_c(BlockPos var1) {
      this.field_184424_i = var1;
   }

   public Mirror func_189716_h() {
      return this.field_184425_j;
   }

   public void func_184411_a(Mirror var1) {
      this.field_184425_j = var1;
   }

   public Rotation func_189726_i() {
      return this.field_184426_k;
   }

   public void func_184408_a(Rotation var1) {
      this.field_184426_k = var1;
   }

   public String func_189708_j() {
      return this.field_184422_g;
   }

   public void func_184410_b(String var1) {
      this.field_184422_g = var1;
   }

   public StructureMode func_189700_k() {
      return this.field_184427_l;
   }

   public void func_184405_a(StructureMode var1) {
      this.field_184427_l = var1;
      IBlockState var2 = this.field_145850_b.func_180495_p(this.func_174877_v());
      if (var2.func_177230_c() == Blocks.field_185779_df) {
         this.field_145850_b.func_180501_a(this.func_174877_v(), (IBlockState)var2.func_206870_a(BlockStructure.field_185587_a, var1), 2);
      }

   }

   public void func_189724_l() {
      switch(this.func_189700_k()) {
      case SAVE:
         this.func_184405_a(StructureMode.LOAD);
         break;
      case LOAD:
         this.func_184405_a(StructureMode.CORNER);
         break;
      case CORNER:
         this.func_184405_a(StructureMode.DATA);
         break;
      case DATA:
         this.func_184405_a(StructureMode.SAVE);
      }

   }

   public boolean func_189713_m() {
      return this.field_184428_m;
   }

   public void func_184406_a(boolean var1) {
      this.field_184428_m = var1;
   }

   public float func_189702_n() {
      return this.field_189730_q;
   }

   public void func_189718_a(float var1) {
      this.field_189730_q = var1;
   }

   public long func_189719_o() {
      return this.field_189731_r;
   }

   public void func_189725_a(long var1) {
      this.field_189731_r = var1;
   }

   public boolean func_184417_l() {
      if (this.field_184427_l != StructureMode.SAVE) {
         return false;
      } else {
         BlockPos var1 = this.func_174877_v();
         boolean var2 = true;
         BlockPos var3 = new BlockPos(var1.func_177958_n() - 80, 0, var1.func_177952_p() - 80);
         BlockPos var4 = new BlockPos(var1.func_177958_n() + 80, 255, var1.func_177952_p() + 80);
         List var5 = this.func_184418_a(var3, var4);
         List var6 = this.func_184415_a(var5);
         if (var6.size() < 1) {
            return false;
         } else {
            MutableBoundingBox var7 = this.func_184416_a(var1, var6);
            if (var7.field_78893_d - var7.field_78897_a > 1 && var7.field_78894_e - var7.field_78895_b > 1 && var7.field_78892_f - var7.field_78896_c > 1) {
               this.field_184423_h = new BlockPos(var7.field_78897_a - var1.func_177958_n() + 1, var7.field_78895_b - var1.func_177956_o() + 1, var7.field_78896_c - var1.func_177952_p() + 1);
               this.field_184424_i = new BlockPos(var7.field_78893_d - var7.field_78897_a - 1, var7.field_78894_e - var7.field_78895_b - 1, var7.field_78892_f - var7.field_78896_c - 1);
               this.func_70296_d();
               IBlockState var8 = this.field_145850_b.func_180495_p(var1);
               this.field_145850_b.func_184138_a(var1, var8, var8, 3);
               return true;
            } else {
               return false;
            }
         }
      }
   }

   private List<TileEntityStructure> func_184415_a(List<TileEntityStructure> var1) {
      Predicate var2 = (var1x) -> {
         return var1x.field_184427_l == StructureMode.CORNER && Objects.equals(this.field_184420_a, var1x.field_184420_a);
      };
      return (List)var1.stream().filter(var2).collect(Collectors.toList());
   }

   private List<TileEntityStructure> func_184418_a(BlockPos var1, BlockPos var2) {
      ArrayList var3 = Lists.newArrayList();
      Iterator var4 = BlockPos.func_177975_b(var1, var2).iterator();

      while(var4.hasNext()) {
         BlockPos.MutableBlockPos var5 = (BlockPos.MutableBlockPos)var4.next();
         IBlockState var6 = this.field_145850_b.func_180495_p(var5);
         if (var6.func_177230_c() == Blocks.field_185779_df) {
            TileEntity var7 = this.field_145850_b.func_175625_s(var5);
            if (var7 != null && var7 instanceof TileEntityStructure) {
               var3.add((TileEntityStructure)var7);
            }
         }
      }

      return var3;
   }

   private MutableBoundingBox func_184416_a(BlockPos var1, List<TileEntityStructure> var2) {
      MutableBoundingBox var3;
      if (var2.size() > 1) {
         BlockPos var4 = ((TileEntityStructure)var2.get(0)).func_174877_v();
         var3 = new MutableBoundingBox(var4, var4);
      } else {
         var3 = new MutableBoundingBox(var1, var1);
      }

      Iterator var7 = var2.iterator();

      while(var7.hasNext()) {
         TileEntityStructure var5 = (TileEntityStructure)var7.next();
         BlockPos var6 = var5.func_174877_v();
         if (var6.func_177958_n() < var3.field_78897_a) {
            var3.field_78897_a = var6.func_177958_n();
         } else if (var6.func_177958_n() > var3.field_78893_d) {
            var3.field_78893_d = var6.func_177958_n();
         }

         if (var6.func_177956_o() < var3.field_78895_b) {
            var3.field_78895_b = var6.func_177956_o();
         } else if (var6.func_177956_o() > var3.field_78894_e) {
            var3.field_78894_e = var6.func_177956_o();
         }

         if (var6.func_177952_p() < var3.field_78896_c) {
            var3.field_78896_c = var6.func_177952_p();
         } else if (var6.func_177952_p() > var3.field_78892_f) {
            var3.field_78892_f = var6.func_177952_p();
         }
      }

      return var3;
   }

   public boolean func_184419_m() {
      return this.func_189712_b(true);
   }

   public boolean func_189712_b(boolean var1) {
      if (this.field_184427_l == StructureMode.SAVE && !this.field_145850_b.field_72995_K && this.field_184420_a != null) {
         BlockPos var2 = this.func_174877_v().func_177971_a(this.field_184423_h);
         WorldServer var3 = (WorldServer)this.field_145850_b;
         TemplateManager var4 = var3.func_184163_y();

         Template var5;
         try {
            var5 = var4.func_200220_a(this.field_184420_a);
         } catch (ResourceLocationException var8) {
            return false;
         }

         var5.func_186254_a(this.field_145850_b, var2, this.field_184424_i, !this.field_184428_m, Blocks.field_189881_dj);
         var5.func_186252_a(this.field_184421_f);
         if (var1) {
            try {
               return var4.func_195429_b(this.field_184420_a);
            } catch (ResourceLocationException var7) {
               return false;
            }
         } else {
            return true;
         }
      } else {
         return false;
      }
   }

   public boolean func_184412_n() {
      return this.func_189714_c(true);
   }

   public boolean func_189714_c(boolean var1) {
      if (this.field_184427_l == StructureMode.LOAD && !this.field_145850_b.field_72995_K && this.field_184420_a != null) {
         BlockPos var2 = this.func_174877_v();
         BlockPos var3 = var2.func_177971_a(this.field_184423_h);
         WorldServer var4 = (WorldServer)this.field_145850_b;
         TemplateManager var5 = var4.func_184163_y();

         Template var6;
         try {
            var6 = var5.func_200219_b(this.field_184420_a);
         } catch (ResourceLocationException var10) {
            return false;
         }

         if (var6 == null) {
            return false;
         } else {
            if (!StringUtils.func_151246_b(var6.func_186261_b())) {
               this.field_184421_f = var6.func_186261_b();
            }

            BlockPos var7 = var6.func_186259_a();
            boolean var8 = this.field_184424_i.equals(var7);
            if (!var8) {
               this.field_184424_i = var7;
               this.func_70296_d();
               IBlockState var9 = this.field_145850_b.func_180495_p(var2);
               this.field_145850_b.func_184138_a(var2, var9, var9, 3);
            }

            if (var1 && !var8) {
               return false;
            } else {
               PlacementSettings var11 = (new PlacementSettings()).func_186214_a(this.field_184425_j).func_186220_a(this.field_184426_k).func_186222_a(this.field_184428_m).func_186218_a((ChunkPos)null).func_186225_a((Block)null).func_186226_b(false);
               if (this.field_189730_q < 1.0F) {
                  var11.func_189946_a(MathHelper.func_76131_a(this.field_189730_q, 0.0F, 1.0F)).func_189949_a(this.field_189731_r);
               }

               var6.func_186260_a(this.field_145850_b, var3, var11);
               return true;
            }
         }
      } else {
         return false;
      }
   }

   public void func_189706_E() {
      if (this.field_184420_a != null) {
         WorldServer var1 = (WorldServer)this.field_145850_b;
         TemplateManager var2 = var1.func_184163_y();
         var2.func_189941_a(this.field_184420_a);
      }
   }

   public boolean func_189709_F() {
      if (this.field_184427_l == StructureMode.LOAD && !this.field_145850_b.field_72995_K && this.field_184420_a != null) {
         WorldServer var1 = (WorldServer)this.field_145850_b;
         TemplateManager var2 = var1.func_184163_y();

         try {
            return var2.func_200219_b(this.field_184420_a) != null;
         } catch (ResourceLocationException var4) {
            return false;
         }
      } else {
         return false;
      }
   }

   public boolean func_189722_G() {
      return this.field_189727_n;
   }

   public void func_189723_d(boolean var1) {
      this.field_189727_n = var1;
   }

   public boolean func_189707_H() {
      return this.field_189728_o;
   }

   public void func_189703_e(boolean var1) {
      this.field_189728_o = var1;
   }

   public boolean func_189721_I() {
      return this.field_189729_p;
   }

   public void func_189710_f(boolean var1) {
      this.field_189729_p = var1;
   }

   public static enum UpdateCommand {
      UPDATE_DATA,
      SAVE_AREA,
      LOAD_AREA,
      SCAN_AREA;

      private UpdateCommand() {
      }
   }
}
