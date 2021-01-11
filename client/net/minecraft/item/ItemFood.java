package net.minecraft.item;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.potion.PotionEffect;
import net.minecraft.stats.StatList;
import net.minecraft.world.World;

public class ItemFood extends Item {
   public final int field_77855_a;
   private final int field_77853_b;
   private final float field_77854_c;
   private final boolean field_77856_bY;
   private boolean field_77852_bZ;
   private int field_77851_ca;
   private int field_77850_cb;
   private int field_77857_cc;
   private float field_77858_cd;

   public ItemFood(int var1, float var2, boolean var3) {
      super();
      this.field_77855_a = 32;
      this.field_77853_b = var1;
      this.field_77856_bY = var3;
      this.field_77854_c = var2;
      this.func_77637_a(CreativeTabs.field_78039_h);
   }

   public ItemFood(int var1, boolean var2) {
      this(var1, 0.6F, var2);
   }

   public ItemStack func_77654_b(ItemStack var1, World var2, EntityPlayer var3) {
      --var1.field_77994_a;
      var3.func_71024_bL().func_151686_a(this, var1);
      var2.func_72956_a(var3, "random.burp", 0.5F, var2.field_73012_v.nextFloat() * 0.1F + 0.9F);
      this.func_77849_c(var1, var2, var3);
      var3.func_71029_a(StatList.field_75929_E[Item.func_150891_b(this)]);
      return var1;
   }

   protected void func_77849_c(ItemStack var1, World var2, EntityPlayer var3) {
      if (!var2.field_72995_K && this.field_77851_ca > 0 && var2.field_73012_v.nextFloat() < this.field_77858_cd) {
         var3.func_70690_d(new PotionEffect(this.field_77851_ca, this.field_77850_cb * 20, this.field_77857_cc));
      }

   }

   public int func_77626_a(ItemStack var1) {
      return 32;
   }

   public EnumAction func_77661_b(ItemStack var1) {
      return EnumAction.EAT;
   }

   public ItemStack func_77659_a(ItemStack var1, World var2, EntityPlayer var3) {
      if (var3.func_71043_e(this.field_77852_bZ)) {
         var3.func_71008_a(var1, this.func_77626_a(var1));
      }

      return var1;
   }

   public int func_150905_g(ItemStack var1) {
      return this.field_77853_b;
   }

   public float func_150906_h(ItemStack var1) {
      return this.field_77854_c;
   }

   public boolean func_77845_h() {
      return this.field_77856_bY;
   }

   public ItemFood func_77844_a(int var1, int var2, int var3, float var4) {
      this.field_77851_ca = var1;
      this.field_77850_cb = var2;
      this.field_77857_cc = var3;
      this.field_77858_cd = var4;
      return this;
   }

   public ItemFood func_77848_i() {
      this.field_77852_bZ = true;
      return this;
   }
}
