package net.minecraft.item;

import javax.annotation.Nullable;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class ItemUseContext {
   protected final EntityPlayer field_196001_b;
   protected final float field_196002_c;
   protected final float field_196003_d;
   protected final float field_196004_e;
   protected final EnumFacing field_196005_f;
   protected final World field_196006_g;
   protected final ItemStack field_196007_h;
   protected final BlockPos field_196008_i;

   public ItemUseContext(EntityPlayer var1, ItemStack var2, BlockPos var3, EnumFacing var4, float var5, float var6, float var7) {
      this(var1.field_70170_p, var1, var2, var3, var4, var5, var6, var7);
   }

   protected ItemUseContext(World var1, @Nullable EntityPlayer var2, ItemStack var3, BlockPos var4, EnumFacing var5, float var6, float var7, float var8) {
      super();
      this.field_196001_b = var2;
      this.field_196005_f = var5;
      this.field_196002_c = var6;
      this.field_196003_d = var7;
      this.field_196004_e = var8;
      this.field_196008_i = var4;
      this.field_196007_h = var3;
      this.field_196006_g = var1;
   }

   public BlockPos func_195995_a() {
      return this.field_196008_i;
   }

   public ItemStack func_195996_i() {
      return this.field_196007_h;
   }

   @Nullable
   public EntityPlayer func_195999_j() {
      return this.field_196001_b;
   }

   public World func_195991_k() {
      return this.field_196006_g;
   }

   public EnumFacing func_196000_l() {
      return this.field_196005_f;
   }

   public float func_195997_m() {
      return this.field_196002_c;
   }

   public float func_195993_n() {
      return this.field_196003_d;
   }

   public float func_195994_o() {
      return this.field_196004_e;
   }

   public EnumFacing func_195992_f() {
      return this.field_196001_b == null ? EnumFacing.NORTH : this.field_196001_b.func_174811_aO();
   }

   public boolean func_195998_g() {
      return this.field_196001_b != null && this.field_196001_b.func_70093_af();
   }

   public float func_195990_h() {
      return this.field_196001_b == null ? 0.0F : this.field_196001_b.field_70177_z;
   }
}
