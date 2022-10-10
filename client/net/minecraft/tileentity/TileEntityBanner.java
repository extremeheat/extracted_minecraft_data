package net.minecraft.tileentity;

import com.google.common.collect.Lists;
import java.util.List;
import java.util.function.Supplier;
import javax.annotation.Nullable;
import net.minecraft.block.BlockAbstractBanner;
import net.minecraft.block.BlockBanner;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.util.INameable;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentTranslation;

public class TileEntityBanner extends TileEntity implements INameable {
   private ITextComponent field_190617_a;
   private EnumDyeColor field_175120_a;
   private NBTTagList field_175118_f;
   private boolean field_175119_g;
   private List<BannerPattern> field_175122_h;
   private List<EnumDyeColor> field_175123_i;
   private String field_175121_j;

   public TileEntityBanner() {
      super(TileEntityType.field_200989_t);
      this.field_175120_a = EnumDyeColor.WHITE;
   }

   public TileEntityBanner(EnumDyeColor var1) {
      this();
      this.field_175120_a = var1;
   }

   public void func_195534_a(ItemStack var1, EnumDyeColor var2) {
      this.field_175118_f = null;
      NBTTagCompound var3 = var1.func_179543_a("BlockEntityTag");
      if (var3 != null && var3.func_150297_b("Patterns", 9)) {
         this.field_175118_f = var3.func_150295_c("Patterns", 10).func_74737_b();
      }

      this.field_175120_a = var2;
      this.field_175122_h = null;
      this.field_175123_i = null;
      this.field_175121_j = "";
      this.field_175119_g = true;
      this.field_190617_a = var1.func_82837_s() ? var1.func_200301_q() : null;
   }

   public ITextComponent func_200200_C_() {
      return (ITextComponent)(this.field_190617_a != null ? this.field_190617_a : new TextComponentTranslation("block.minecraft.banner", new Object[0]));
   }

   public boolean func_145818_k_() {
      return this.field_190617_a != null;
   }

   @Nullable
   public ITextComponent func_200201_e() {
      return this.field_190617_a;
   }

   public NBTTagCompound func_189515_b(NBTTagCompound var1) {
      super.func_189515_b(var1);
      if (this.field_175118_f != null) {
         var1.func_74782_a("Patterns", this.field_175118_f);
      }

      if (this.field_190617_a != null) {
         var1.func_74778_a("CustomName", ITextComponent.Serializer.func_150696_a(this.field_190617_a));
      }

      return var1;
   }

   public void func_145839_a(NBTTagCompound var1) {
      super.func_145839_a(var1);
      if (var1.func_150297_b("CustomName", 8)) {
         this.field_190617_a = ITextComponent.Serializer.func_150699_a(var1.func_74779_i("CustomName"));
      }

      if (this.func_145830_o()) {
         this.field_175120_a = ((BlockAbstractBanner)this.func_195044_w().func_177230_c()).func_196285_M_();
      } else {
         this.field_175120_a = null;
      }

      this.field_175118_f = var1.func_150295_c("Patterns", 10);
      this.field_175122_h = null;
      this.field_175123_i = null;
      this.field_175121_j = null;
      this.field_175119_g = true;
   }

   @Nullable
   public SPacketUpdateTileEntity func_189518_D_() {
      return new SPacketUpdateTileEntity(this.field_174879_c, 6, this.func_189517_E_());
   }

   public NBTTagCompound func_189517_E_() {
      return this.func_189515_b(new NBTTagCompound());
   }

   public static int func_175113_c(ItemStack var0) {
      NBTTagCompound var1 = var0.func_179543_a("BlockEntityTag");
      return var1 != null && var1.func_74764_b("Patterns") ? var1.func_150295_c("Patterns", 10).size() : 0;
   }

   public List<BannerPattern> func_175114_c() {
      this.func_175109_g();
      return this.field_175122_h;
   }

   public List<EnumDyeColor> func_175110_d() {
      this.func_175109_g();
      return this.field_175123_i;
   }

   public String func_175116_e() {
      this.func_175109_g();
      return this.field_175121_j;
   }

   private void func_175109_g() {
      if (this.field_175122_h == null || this.field_175123_i == null || this.field_175121_j == null) {
         if (!this.field_175119_g) {
            this.field_175121_j = "";
         } else {
            this.field_175122_h = Lists.newArrayList();
            this.field_175123_i = Lists.newArrayList();
            EnumDyeColor var1 = this.func_195533_l(this::func_195044_w);
            if (var1 == null) {
               this.field_175121_j = "banner_missing";
            } else {
               this.field_175122_h.add(BannerPattern.BASE);
               this.field_175123_i.add(var1);
               this.field_175121_j = "b" + var1.func_196059_a();
               if (this.field_175118_f != null) {
                  for(int var2 = 0; var2 < this.field_175118_f.size(); ++var2) {
                     NBTTagCompound var3 = this.field_175118_f.func_150305_b(var2);
                     BannerPattern var4 = BannerPattern.func_190994_a(var3.func_74779_i("Pattern"));
                     if (var4 != null) {
                        this.field_175122_h.add(var4);
                        int var5 = var3.func_74762_e("Color");
                        this.field_175123_i.add(EnumDyeColor.func_196056_a(var5));
                        this.field_175121_j = this.field_175121_j + var4.func_190993_b() + var5;
                     }
                  }
               }
            }

         }
      }
   }

   public static void func_175117_e(ItemStack var0) {
      NBTTagCompound var1 = var0.func_179543_a("BlockEntityTag");
      if (var1 != null && var1.func_150297_b("Patterns", 9)) {
         NBTTagList var2 = var1.func_150295_c("Patterns", 10);
         if (!var2.isEmpty()) {
            var2.remove(var2.size() - 1);
            if (var2.isEmpty()) {
               var0.func_196083_e("BlockEntityTag");
            }

         }
      }
   }

   public ItemStack func_190615_l(IBlockState var1) {
      ItemStack var2 = new ItemStack(BlockBanner.func_196287_a(this.func_195533_l(() -> {
         return var1;
      })));
      if (this.field_175118_f != null && !this.field_175118_f.isEmpty()) {
         var2.func_190925_c("BlockEntityTag").func_74782_a("Patterns", this.field_175118_f.func_74737_b());
      }

      if (this.field_190617_a != null) {
         var2.func_200302_a(this.field_190617_a);
      }

      return var2;
   }

   public EnumDyeColor func_195533_l(Supplier<IBlockState> var1) {
      if (this.field_175120_a == null) {
         this.field_175120_a = ((BlockAbstractBanner)((IBlockState)var1.get()).func_177230_c()).func_196285_M_();
      }

      return this.field_175120_a;
   }
}
