package net.minecraft.item;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.Map.Entry;
import net.minecraft.block.Block;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentDurability;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.EnumCreatureAttribute;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.item.EntityItemFrame;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.event.HoverEvent;
import net.minecraft.init.Items;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.stats.StatList;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.IChatComponent;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;

public final class ItemStack {
   public static final DecimalFormat field_111284_a = new DecimalFormat("#.###");
   public int field_77994_a;
   public int field_77992_b;
   private Item field_151002_e;
   private NBTTagCompound field_77990_d;
   private int field_77991_e;
   private EntityItemFrame field_82843_f;
   private Block field_179552_h;
   private boolean field_179553_i;
   private Block field_179550_j;
   private boolean field_179551_k;

   public ItemStack(Block var1) {
      this((Block)var1, 1);
   }

   public ItemStack(Block var1, int var2) {
      this((Block)var1, var2, 0);
   }

   public ItemStack(Block var1, int var2, int var3) {
      this(Item.func_150898_a(var1), var2, var3);
   }

   public ItemStack(Item var1) {
      this((Item)var1, 1);
   }

   public ItemStack(Item var1, int var2) {
      this((Item)var1, var2, 0);
   }

   public ItemStack(Item var1, int var2, int var3) {
      super();
      this.field_179552_h = null;
      this.field_179553_i = false;
      this.field_179550_j = null;
      this.field_179551_k = false;
      this.field_151002_e = var1;
      this.field_77994_a = var2;
      this.field_77991_e = var3;
      if (this.field_77991_e < 0) {
         this.field_77991_e = 0;
      }

   }

   public static ItemStack func_77949_a(NBTTagCompound var0) {
      ItemStack var1 = new ItemStack();
      var1.func_77963_c(var0);
      return var1.func_77973_b() != null ? var1 : null;
   }

   private ItemStack() {
      super();
      this.field_179552_h = null;
      this.field_179553_i = false;
      this.field_179550_j = null;
      this.field_179551_k = false;
   }

   public ItemStack func_77979_a(int var1) {
      ItemStack var2 = new ItemStack(this.field_151002_e, var1, this.field_77991_e);
      if (this.field_77990_d != null) {
         var2.field_77990_d = (NBTTagCompound)this.field_77990_d.func_74737_b();
      }

      this.field_77994_a -= var1;
      return var2;
   }

   public Item func_77973_b() {
      return this.field_151002_e;
   }

   public boolean func_179546_a(EntityPlayer var1, World var2, BlockPos var3, EnumFacing var4, float var5, float var6, float var7) {
      boolean var8 = this.func_77973_b().func_180614_a(this, var1, var2, var3, var4, var5, var6, var7);
      if (var8) {
         var1.func_71029_a(StatList.field_75929_E[Item.func_150891_b(this.field_151002_e)]);
      }

      return var8;
   }

   public float func_150997_a(Block var1) {
      return this.func_77973_b().func_150893_a(this, var1);
   }

   public ItemStack func_77957_a(World var1, EntityPlayer var2) {
      return this.func_77973_b().func_77659_a(this, var1, var2);
   }

   public ItemStack func_77950_b(World var1, EntityPlayer var2) {
      return this.func_77973_b().func_77654_b(this, var1, var2);
   }

   public NBTTagCompound func_77955_b(NBTTagCompound var1) {
      ResourceLocation var2 = (ResourceLocation)Item.field_150901_e.func_177774_c(this.field_151002_e);
      var1.func_74778_a("id", var2 == null ? "minecraft:air" : var2.toString());
      var1.func_74774_a("Count", (byte)this.field_77994_a);
      var1.func_74777_a("Damage", (short)this.field_77991_e);
      if (this.field_77990_d != null) {
         var1.func_74782_a("tag", this.field_77990_d);
      }

      return var1;
   }

   public void func_77963_c(NBTTagCompound var1) {
      if (var1.func_150297_b("id", 8)) {
         this.field_151002_e = Item.func_111206_d(var1.func_74779_i("id"));
      } else {
         this.field_151002_e = Item.func_150899_d(var1.func_74765_d("id"));
      }

      this.field_77994_a = var1.func_74771_c("Count");
      this.field_77991_e = var1.func_74765_d("Damage");
      if (this.field_77991_e < 0) {
         this.field_77991_e = 0;
      }

      if (var1.func_150297_b("tag", 10)) {
         this.field_77990_d = var1.func_74775_l("tag");
         if (this.field_151002_e != null) {
            this.field_151002_e.func_179215_a(this.field_77990_d);
         }
      }

   }

   public int func_77976_d() {
      return this.func_77973_b().func_77639_j();
   }

   public boolean func_77985_e() {
      return this.func_77976_d() > 1 && (!this.func_77984_f() || !this.func_77951_h());
   }

   public boolean func_77984_f() {
      if (this.field_151002_e == null) {
         return false;
      } else if (this.field_151002_e.func_77612_l() <= 0) {
         return false;
      } else {
         return !this.func_77942_o() || !this.func_77978_p().func_74767_n("Unbreakable");
      }
   }

   public boolean func_77981_g() {
      return this.field_151002_e.func_77614_k();
   }

   public boolean func_77951_h() {
      return this.func_77984_f() && this.field_77991_e > 0;
   }

   public int func_77952_i() {
      return this.field_77991_e;
   }

   public int func_77960_j() {
      return this.field_77991_e;
   }

   public void func_77964_b(int var1) {
      this.field_77991_e = var1;
      if (this.field_77991_e < 0) {
         this.field_77991_e = 0;
      }

   }

   public int func_77958_k() {
      return this.field_151002_e.func_77612_l();
   }

   public boolean func_96631_a(int var1, Random var2) {
      if (!this.func_77984_f()) {
         return false;
      } else {
         if (var1 > 0) {
            int var3 = EnchantmentHelper.func_77506_a(Enchantment.field_77347_r.field_77352_x, this);
            int var4 = 0;

            for(int var5 = 0; var3 > 0 && var5 < var1; ++var5) {
               if (EnchantmentDurability.func_92097_a(this, var3, var2)) {
                  ++var4;
               }
            }

            var1 -= var4;
            if (var1 <= 0) {
               return false;
            }
         }

         this.field_77991_e += var1;
         return this.field_77991_e > this.func_77958_k();
      }
   }

   public void func_77972_a(int var1, EntityLivingBase var2) {
      if (!(var2 instanceof EntityPlayer) || !((EntityPlayer)var2).field_71075_bZ.field_75098_d) {
         if (this.func_77984_f()) {
            if (this.func_96631_a(var1, var2.func_70681_au())) {
               var2.func_70669_a(this);
               --this.field_77994_a;
               if (var2 instanceof EntityPlayer) {
                  EntityPlayer var3 = (EntityPlayer)var2;
                  var3.func_71029_a(StatList.field_75930_F[Item.func_150891_b(this.field_151002_e)]);
                  if (this.field_77994_a == 0 && this.func_77973_b() instanceof ItemBow) {
                     var3.func_71028_bD();
                  }
               }

               if (this.field_77994_a < 0) {
                  this.field_77994_a = 0;
               }

               this.field_77991_e = 0;
            }

         }
      }
   }

   public void func_77961_a(EntityLivingBase var1, EntityPlayer var2) {
      boolean var3 = this.field_151002_e.func_77644_a(this, var1, var2);
      if (var3) {
         var2.func_71029_a(StatList.field_75929_E[Item.func_150891_b(this.field_151002_e)]);
      }

   }

   public void func_179548_a(World var1, Block var2, BlockPos var3, EntityPlayer var4) {
      boolean var5 = this.field_151002_e.func_179218_a(this, var1, var2, var3, var4);
      if (var5) {
         var4.func_71029_a(StatList.field_75929_E[Item.func_150891_b(this.field_151002_e)]);
      }

   }

   public boolean func_150998_b(Block var1) {
      return this.field_151002_e.func_150897_b(var1);
   }

   public boolean func_111282_a(EntityPlayer var1, EntityLivingBase var2) {
      return this.field_151002_e.func_111207_a(this, var1, var2);
   }

   public ItemStack func_77946_l() {
      ItemStack var1 = new ItemStack(this.field_151002_e, this.field_77994_a, this.field_77991_e);
      if (this.field_77990_d != null) {
         var1.field_77990_d = (NBTTagCompound)this.field_77990_d.func_74737_b();
      }

      return var1;
   }

   public static boolean func_77970_a(ItemStack var0, ItemStack var1) {
      if (var0 == null && var1 == null) {
         return true;
      } else if (var0 != null && var1 != null) {
         if (var0.field_77990_d == null && var1.field_77990_d != null) {
            return false;
         } else {
            return var0.field_77990_d == null || var0.field_77990_d.equals(var1.field_77990_d);
         }
      } else {
         return false;
      }
   }

   public static boolean func_77989_b(ItemStack var0, ItemStack var1) {
      if (var0 == null && var1 == null) {
         return true;
      } else {
         return var0 != null && var1 != null ? var0.func_77959_d(var1) : false;
      }
   }

   private boolean func_77959_d(ItemStack var1) {
      if (this.field_77994_a != var1.field_77994_a) {
         return false;
      } else if (this.field_151002_e != var1.field_151002_e) {
         return false;
      } else if (this.field_77991_e != var1.field_77991_e) {
         return false;
      } else if (this.field_77990_d == null && var1.field_77990_d != null) {
         return false;
      } else {
         return this.field_77990_d == null || this.field_77990_d.equals(var1.field_77990_d);
      }
   }

   public static boolean func_179545_c(ItemStack var0, ItemStack var1) {
      if (var0 == null && var1 == null) {
         return true;
      } else {
         return var0 != null && var1 != null ? var0.func_77969_a(var1) : false;
      }
   }

   public boolean func_77969_a(ItemStack var1) {
      return var1 != null && this.field_151002_e == var1.field_151002_e && this.field_77991_e == var1.field_77991_e;
   }

   public String func_77977_a() {
      return this.field_151002_e.func_77667_c(this);
   }

   public static ItemStack func_77944_b(ItemStack var0) {
      return var0 == null ? null : var0.func_77946_l();
   }

   public String toString() {
      return this.field_77994_a + "x" + this.field_151002_e.func_77658_a() + "@" + this.field_77991_e;
   }

   public void func_77945_a(World var1, Entity var2, int var3, boolean var4) {
      if (this.field_77992_b > 0) {
         --this.field_77992_b;
      }

      this.field_151002_e.func_77663_a(this, var1, var2, var3, var4);
   }

   public void func_77980_a(World var1, EntityPlayer var2, int var3) {
      var2.func_71064_a(StatList.field_75928_D[Item.func_150891_b(this.field_151002_e)], var3);
      this.field_151002_e.func_77622_d(this, var1, var2);
   }

   public boolean func_179549_c(ItemStack var1) {
      return this.func_77959_d(var1);
   }

   public int func_77988_m() {
      return this.func_77973_b().func_77626_a(this);
   }

   public EnumAction func_77975_n() {
      return this.func_77973_b().func_77661_b(this);
   }

   public void func_77974_b(World var1, EntityPlayer var2, int var3) {
      this.func_77973_b().func_77615_a(this, var1, var2, var3);
   }

   public boolean func_77942_o() {
      return this.field_77990_d != null;
   }

   public NBTTagCompound func_77978_p() {
      return this.field_77990_d;
   }

   public NBTTagCompound func_179543_a(String var1, boolean var2) {
      if (this.field_77990_d != null && this.field_77990_d.func_150297_b(var1, 10)) {
         return this.field_77990_d.func_74775_l(var1);
      } else if (var2) {
         NBTTagCompound var3 = new NBTTagCompound();
         this.func_77983_a(var1, var3);
         return var3;
      } else {
         return null;
      }
   }

   public NBTTagList func_77986_q() {
      return this.field_77990_d == null ? null : this.field_77990_d.func_150295_c("ench", 10);
   }

   public void func_77982_d(NBTTagCompound var1) {
      this.field_77990_d = var1;
   }

   public String func_82833_r() {
      String var1 = this.func_77973_b().func_77653_i(this);
      if (this.field_77990_d != null && this.field_77990_d.func_150297_b("display", 10)) {
         NBTTagCompound var2 = this.field_77990_d.func_74775_l("display");
         if (var2.func_150297_b("Name", 8)) {
            var1 = var2.func_74779_i("Name");
         }
      }

      return var1;
   }

   public ItemStack func_151001_c(String var1) {
      if (this.field_77990_d == null) {
         this.field_77990_d = new NBTTagCompound();
      }

      if (!this.field_77990_d.func_150297_b("display", 10)) {
         this.field_77990_d.func_74782_a("display", new NBTTagCompound());
      }

      this.field_77990_d.func_74775_l("display").func_74778_a("Name", var1);
      return this;
   }

   public void func_135074_t() {
      if (this.field_77990_d != null) {
         if (this.field_77990_d.func_150297_b("display", 10)) {
            NBTTagCompound var1 = this.field_77990_d.func_74775_l("display");
            var1.func_82580_o("Name");
            if (var1.func_82582_d()) {
               this.field_77990_d.func_82580_o("display");
               if (this.field_77990_d.func_82582_d()) {
                  this.func_77982_d((NBTTagCompound)null);
               }
            }

         }
      }
   }

   public boolean func_82837_s() {
      if (this.field_77990_d == null) {
         return false;
      } else {
         return !this.field_77990_d.func_150297_b("display", 10) ? false : this.field_77990_d.func_74775_l("display").func_150297_b("Name", 8);
      }
   }

   public List<String> func_82840_a(EntityPlayer var1, boolean var2) {
      ArrayList var3 = Lists.newArrayList();
      String var4 = this.func_82833_r();
      if (this.func_82837_s()) {
         var4 = EnumChatFormatting.ITALIC + var4;
      }

      var4 = var4 + EnumChatFormatting.RESET;
      if (var2) {
         String var5 = "";
         if (var4.length() > 0) {
            var4 = var4 + " (";
            var5 = ")";
         }

         int var6 = Item.func_150891_b(this.field_151002_e);
         if (this.func_77981_g()) {
            var4 = var4 + String.format("#%04d/%d%s", var6, this.field_77991_e, var5);
         } else {
            var4 = var4 + String.format("#%04d%s", var6, var5);
         }
      } else if (!this.func_82837_s() && this.field_151002_e == Items.field_151098_aY) {
         var4 = var4 + " #" + this.field_77991_e;
      }

      var3.add(var4);
      int var14 = 0;
      if (this.func_77942_o() && this.field_77990_d.func_150297_b("HideFlags", 99)) {
         var14 = this.field_77990_d.func_74762_e("HideFlags");
      }

      if ((var14 & 32) == 0) {
         this.field_151002_e.func_77624_a(this, var1, var3, var2);
      }

      NBTTagList var18;
      int var20;
      if (this.func_77942_o()) {
         if ((var14 & 1) == 0) {
            NBTTagList var15 = this.func_77986_q();
            if (var15 != null) {
               for(int var7 = 0; var7 < var15.func_74745_c(); ++var7) {
                  short var8 = var15.func_150305_b(var7).func_74765_d("id");
                  short var9 = var15.func_150305_b(var7).func_74765_d("lvl");
                  if (Enchantment.func_180306_c(var8) != null) {
                     var3.add(Enchantment.func_180306_c(var8).func_77316_c(var9));
                  }
               }
            }
         }

         if (this.field_77990_d.func_150297_b("display", 10)) {
            NBTTagCompound var16 = this.field_77990_d.func_74775_l("display");
            if (var16.func_150297_b("color", 3)) {
               if (var2) {
                  var3.add("Color: #" + Integer.toHexString(var16.func_74762_e("color")).toUpperCase());
               } else {
                  var3.add(EnumChatFormatting.ITALIC + StatCollector.func_74838_a("item.dyed"));
               }
            }

            if (var16.func_150299_b("Lore") == 9) {
               var18 = var16.func_150295_c("Lore", 8);
               if (var18.func_74745_c() > 0) {
                  for(var20 = 0; var20 < var18.func_74745_c(); ++var20) {
                     var3.add(EnumChatFormatting.DARK_PURPLE + "" + EnumChatFormatting.ITALIC + var18.func_150307_f(var20));
                  }
               }
            }
         }
      }

      Multimap var17 = this.func_111283_C();
      if (!var17.isEmpty() && (var14 & 2) == 0) {
         var3.add("");
         Iterator var19 = var17.entries().iterator();

         while(var19.hasNext()) {
            Entry var21 = (Entry)var19.next();
            AttributeModifier var22 = (AttributeModifier)var21.getValue();
            double var10 = var22.func_111164_d();
            if (var22.func_111167_a() == Item.field_111210_e) {
               var10 += (double)EnchantmentHelper.func_152377_a(this, EnumCreatureAttribute.UNDEFINED);
            }

            double var12;
            if (var22.func_111169_c() != 1 && var22.func_111169_c() != 2) {
               var12 = var10;
            } else {
               var12 = var10 * 100.0D;
            }

            if (var10 > 0.0D) {
               var3.add(EnumChatFormatting.BLUE + StatCollector.func_74837_a("attribute.modifier.plus." + var22.func_111169_c(), field_111284_a.format(var12), StatCollector.func_74838_a("attribute.name." + (String)var21.getKey())));
            } else if (var10 < 0.0D) {
               var12 *= -1.0D;
               var3.add(EnumChatFormatting.RED + StatCollector.func_74837_a("attribute.modifier.take." + var22.func_111169_c(), field_111284_a.format(var12), StatCollector.func_74838_a("attribute.name." + (String)var21.getKey())));
            }
         }
      }

      if (this.func_77942_o() && this.func_77978_p().func_74767_n("Unbreakable") && (var14 & 4) == 0) {
         var3.add(EnumChatFormatting.BLUE + StatCollector.func_74838_a("item.unbreakable"));
      }

      Block var23;
      if (this.func_77942_o() && this.field_77990_d.func_150297_b("CanDestroy", 9) && (var14 & 8) == 0) {
         var18 = this.field_77990_d.func_150295_c("CanDestroy", 8);
         if (var18.func_74745_c() > 0) {
            var3.add("");
            var3.add(EnumChatFormatting.GRAY + StatCollector.func_74838_a("item.canBreak"));

            for(var20 = 0; var20 < var18.func_74745_c(); ++var20) {
               var23 = Block.func_149684_b(var18.func_150307_f(var20));
               if (var23 != null) {
                  var3.add(EnumChatFormatting.DARK_GRAY + var23.func_149732_F());
               } else {
                  var3.add(EnumChatFormatting.DARK_GRAY + "missingno");
               }
            }
         }
      }

      if (this.func_77942_o() && this.field_77990_d.func_150297_b("CanPlaceOn", 9) && (var14 & 16) == 0) {
         var18 = this.field_77990_d.func_150295_c("CanPlaceOn", 8);
         if (var18.func_74745_c() > 0) {
            var3.add("");
            var3.add(EnumChatFormatting.GRAY + StatCollector.func_74838_a("item.canPlace"));

            for(var20 = 0; var20 < var18.func_74745_c(); ++var20) {
               var23 = Block.func_149684_b(var18.func_150307_f(var20));
               if (var23 != null) {
                  var3.add(EnumChatFormatting.DARK_GRAY + var23.func_149732_F());
               } else {
                  var3.add(EnumChatFormatting.DARK_GRAY + "missingno");
               }
            }
         }
      }

      if (var2) {
         if (this.func_77951_h()) {
            var3.add("Durability: " + (this.func_77958_k() - this.func_77952_i()) + " / " + this.func_77958_k());
         }

         var3.add(EnumChatFormatting.DARK_GRAY + ((ResourceLocation)Item.field_150901_e.func_177774_c(this.field_151002_e)).toString());
         if (this.func_77942_o()) {
            var3.add(EnumChatFormatting.DARK_GRAY + "NBT: " + this.func_77978_p().func_150296_c().size() + " tag(s)");
         }
      }

      return var3;
   }

   public boolean func_77962_s() {
      return this.func_77973_b().func_77636_d(this);
   }

   public EnumRarity func_77953_t() {
      return this.func_77973_b().func_77613_e(this);
   }

   public boolean func_77956_u() {
      if (!this.func_77973_b().func_77616_k(this)) {
         return false;
      } else {
         return !this.func_77948_v();
      }
   }

   public void func_77966_a(Enchantment var1, int var2) {
      if (this.field_77990_d == null) {
         this.func_77982_d(new NBTTagCompound());
      }

      if (!this.field_77990_d.func_150297_b("ench", 9)) {
         this.field_77990_d.func_74782_a("ench", new NBTTagList());
      }

      NBTTagList var3 = this.field_77990_d.func_150295_c("ench", 10);
      NBTTagCompound var4 = new NBTTagCompound();
      var4.func_74777_a("id", (short)var1.field_77352_x);
      var4.func_74777_a("lvl", (short)((byte)var2));
      var3.func_74742_a(var4);
   }

   public boolean func_77948_v() {
      return this.field_77990_d != null && this.field_77990_d.func_150297_b("ench", 9);
   }

   public void func_77983_a(String var1, NBTBase var2) {
      if (this.field_77990_d == null) {
         this.func_77982_d(new NBTTagCompound());
      }

      this.field_77990_d.func_74782_a(var1, var2);
   }

   public boolean func_82835_x() {
      return this.func_77973_b().func_82788_x();
   }

   public boolean func_82839_y() {
      return this.field_82843_f != null;
   }

   public void func_82842_a(EntityItemFrame var1) {
      this.field_82843_f = var1;
   }

   public EntityItemFrame func_82836_z() {
      return this.field_82843_f;
   }

   public int func_82838_A() {
      return this.func_77942_o() && this.field_77990_d.func_150297_b("RepairCost", 3) ? this.field_77990_d.func_74762_e("RepairCost") : 0;
   }

   public void func_82841_c(int var1) {
      if (!this.func_77942_o()) {
         this.field_77990_d = new NBTTagCompound();
      }

      this.field_77990_d.func_74768_a("RepairCost", var1);
   }

   public Multimap<String, AttributeModifier> func_111283_C() {
      Object var1;
      if (this.func_77942_o() && this.field_77990_d.func_150297_b("AttributeModifiers", 9)) {
         var1 = HashMultimap.create();
         NBTTagList var2 = this.field_77990_d.func_150295_c("AttributeModifiers", 10);

         for(int var3 = 0; var3 < var2.func_74745_c(); ++var3) {
            NBTTagCompound var4 = var2.func_150305_b(var3);
            AttributeModifier var5 = SharedMonsterAttributes.func_111259_a(var4);
            if (var5 != null && var5.func_111167_a().getLeastSignificantBits() != 0L && var5.func_111167_a().getMostSignificantBits() != 0L) {
               ((Multimap)var1).put(var4.func_74779_i("AttributeName"), var5);
            }
         }
      } else {
         var1 = this.func_77973_b().func_111205_h();
      }

      return (Multimap)var1;
   }

   public void func_150996_a(Item var1) {
      this.field_151002_e = var1;
   }

   public IChatComponent func_151000_E() {
      ChatComponentText var1 = new ChatComponentText(this.func_82833_r());
      if (this.func_82837_s()) {
         var1.func_150256_b().func_150217_b(true);
      }

      IChatComponent var2 = (new ChatComponentText("[")).func_150257_a(var1).func_150258_a("]");
      if (this.field_151002_e != null) {
         NBTTagCompound var3 = new NBTTagCompound();
         this.func_77955_b(var3);
         var2.func_150256_b().func_150209_a(new HoverEvent(HoverEvent.Action.SHOW_ITEM, new ChatComponentText(var3.toString())));
         var2.func_150256_b().func_150238_a(this.func_77953_t().field_77937_e);
      }

      return var2;
   }

   public boolean func_179544_c(Block var1) {
      if (var1 == this.field_179552_h) {
         return this.field_179553_i;
      } else {
         this.field_179552_h = var1;
         if (this.func_77942_o() && this.field_77990_d.func_150297_b("CanDestroy", 9)) {
            NBTTagList var2 = this.field_77990_d.func_150295_c("CanDestroy", 8);

            for(int var3 = 0; var3 < var2.func_74745_c(); ++var3) {
               Block var4 = Block.func_149684_b(var2.func_150307_f(var3));
               if (var4 == var1) {
                  this.field_179553_i = true;
                  return true;
               }
            }
         }

         this.field_179553_i = false;
         return false;
      }
   }

   public boolean func_179547_d(Block var1) {
      if (var1 == this.field_179550_j) {
         return this.field_179551_k;
      } else {
         this.field_179550_j = var1;
         if (this.func_77942_o() && this.field_77990_d.func_150297_b("CanPlaceOn", 9)) {
            NBTTagList var2 = this.field_77990_d.func_150295_c("CanPlaceOn", 8);

            for(int var3 = 0; var3 < var2.func_74745_c(); ++var3) {
               Block var4 = Block.func_149684_b(var2.func_150307_f(var3));
               if (var4 == var1) {
                  this.field_179551_k = true;
                  return true;
               }
            }
         }

         this.field_179551_k = false;
         return false;
      }
   }
}
