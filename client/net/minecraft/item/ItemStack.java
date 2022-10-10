package net.minecraft.item;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;
import com.google.gson.JsonParseException;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Random;
import java.util.Map.Entry;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.block.Block;
import net.minecraft.block.state.BlockWorldState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.command.arguments.BlockPredicateArgument;
import net.minecraft.command.arguments.BlockStateParser;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentDurability;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.CreatureAttribute;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.item.EntityItemFrame;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Enchantments;
import net.minecraft.init.Items;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.nbt.INBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.stats.StatList;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.NetworkTagManager;
import net.minecraft.tags.Tag;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.IItemProvider;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.IRegistry;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextComponentUtils;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.event.HoverEvent;
import net.minecraft.world.World;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public final class ItemStack {
   private static final Logger field_199558_c = LogManager.getLogger();
   public static final ItemStack field_190927_a = new ItemStack((Item)null);
   public static final DecimalFormat field_111284_a = func_208306_D();
   private int field_77994_a;
   private int field_77992_b;
   @Deprecated
   private final Item field_151002_e;
   private NBTTagCompound field_77990_d;
   private boolean field_190928_g;
   private EntityItemFrame field_82843_f;
   private BlockWorldState field_179552_h;
   private boolean field_179553_i;
   private BlockWorldState field_179550_j;
   private boolean field_179551_k;

   private static DecimalFormat func_208306_D() {
      DecimalFormat var0 = new DecimalFormat("#.##");
      var0.setDecimalFormatSymbols(DecimalFormatSymbols.getInstance(Locale.ROOT));
      return var0;
   }

   public ItemStack(IItemProvider var1) {
      this(var1, 1);
   }

   public ItemStack(IItemProvider var1, int var2) {
      super();
      this.field_151002_e = var1 == null ? null : var1.func_199767_j();
      this.field_77994_a = var2;
      this.func_190923_F();
   }

   private void func_190923_F() {
      this.field_190928_g = false;
      this.field_190928_g = this.func_190926_b();
   }

   private ItemStack(NBTTagCompound var1) {
      super();
      Item var2 = (Item)IRegistry.field_212630_s.func_212608_b(new ResourceLocation(var1.func_74779_i("id")));
      this.field_151002_e = var2 == null ? Items.field_190931_a : var2;
      this.field_77994_a = var1.func_74771_c("Count");
      if (var1.func_150297_b("tag", 10)) {
         this.field_77990_d = var1.func_74775_l("tag");
         this.func_77973_b().func_179215_a(var1);
      }

      if (this.func_77973_b().func_77645_m()) {
         this.func_196085_b(this.func_77952_i());
      }

      this.func_190923_F();
   }

   public static ItemStack func_199557_a(NBTTagCompound var0) {
      try {
         return new ItemStack(var0);
      } catch (RuntimeException var2) {
         field_199558_c.debug("Tried to load invalid item: {}", var0, var2);
         return field_190927_a;
      }
   }

   public boolean func_190926_b() {
      if (this == field_190927_a) {
         return true;
      } else if (this.func_77973_b() != null && this.func_77973_b() != Items.field_190931_a) {
         return this.field_77994_a <= 0;
      } else {
         return true;
      }
   }

   public ItemStack func_77979_a(int var1) {
      int var2 = Math.min(var1, this.field_77994_a);
      ItemStack var3 = this.func_77946_l();
      var3.func_190920_e(var2);
      this.func_190918_g(var2);
      return var3;
   }

   public Item func_77973_b() {
      return this.field_190928_g ? Items.field_190931_a : this.field_151002_e;
   }

   public EnumActionResult func_196084_a(ItemUseContext var1) {
      EntityPlayer var2 = var1.func_195999_j();
      BlockPos var3 = var1.func_195995_a();
      BlockWorldState var4 = new BlockWorldState(var1.func_195991_k(), var3, false);
      if (var2 != null && !var2.field_71075_bZ.field_75099_e && !this.func_206847_b(var1.func_195991_k().func_205772_D(), var4)) {
         return EnumActionResult.PASS;
      } else {
         Item var5 = this.func_77973_b();
         EnumActionResult var6 = var5.func_195939_a(var1);
         if (var2 != null && var6 == EnumActionResult.SUCCESS) {
            var2.func_71029_a(StatList.field_75929_E.func_199076_b(var5));
         }

         return var6;
      }
   }

   public float func_150997_a(IBlockState var1) {
      return this.func_77973_b().func_150893_a(this, var1);
   }

   public ActionResult<ItemStack> func_77957_a(World var1, EntityPlayer var2, EnumHand var3) {
      return this.func_77973_b().func_77659_a(var1, var2, var3);
   }

   public ItemStack func_77950_b(World var1, EntityLivingBase var2) {
      return this.func_77973_b().func_77654_b(this, var1, var2);
   }

   public NBTTagCompound func_77955_b(NBTTagCompound var1) {
      ResourceLocation var2 = IRegistry.field_212630_s.func_177774_c(this.func_77973_b());
      var1.func_74778_a("id", var2 == null ? "minecraft:air" : var2.toString());
      var1.func_74774_a("Count", (byte)this.field_77994_a);
      if (this.field_77990_d != null) {
         var1.func_74782_a("tag", this.field_77990_d);
      }

      return var1;
   }

   public int func_77976_d() {
      return this.func_77973_b().func_77639_j();
   }

   public boolean func_77985_e() {
      return this.func_77976_d() > 1 && (!this.func_77984_f() || !this.func_77951_h());
   }

   public boolean func_77984_f() {
      if (!this.field_190928_g && this.func_77973_b().func_77612_l() > 0) {
         NBTTagCompound var1 = this.func_77978_p();
         return var1 == null || !var1.func_74767_n("Unbreakable");
      } else {
         return false;
      }
   }

   public boolean func_77951_h() {
      return this.func_77984_f() && this.func_77952_i() > 0;
   }

   public int func_77952_i() {
      return this.field_77990_d == null ? 0 : this.field_77990_d.func_74762_e("Damage");
   }

   public void func_196085_b(int var1) {
      this.func_196082_o().func_74768_a("Damage", Math.max(0, var1));
   }

   public int func_77958_k() {
      return this.func_77973_b().func_77612_l();
   }

   public boolean func_96631_a(int var1, Random var2, @Nullable EntityPlayerMP var3) {
      if (!this.func_77984_f()) {
         return false;
      } else {
         int var4;
         if (var1 > 0) {
            var4 = EnchantmentHelper.func_77506_a(Enchantments.field_185307_s, this);
            int var5 = 0;

            for(int var6 = 0; var4 > 0 && var6 < var1; ++var6) {
               if (EnchantmentDurability.func_92097_a(this, var4, var2)) {
                  ++var5;
               }
            }

            var1 -= var5;
            if (var1 <= 0) {
               return false;
            }
         }

         if (var3 != null && var1 != 0) {
            CriteriaTriggers.field_193132_s.func_193158_a(var3, this, this.func_77952_i() + var1);
         }

         var4 = this.func_77952_i() + var1;
         this.func_196085_b(var4);
         return var4 >= this.func_77958_k();
      }
   }

   public void func_77972_a(int var1, EntityLivingBase var2) {
      if (!(var2 instanceof EntityPlayer) || !((EntityPlayer)var2).field_71075_bZ.field_75098_d) {
         if (this.func_77984_f()) {
            if (this.func_96631_a(var1, var2.func_70681_au(), var2 instanceof EntityPlayerMP ? (EntityPlayerMP)var2 : null)) {
               var2.func_70669_a(this);
               Item var3 = this.func_77973_b();
               this.func_190918_g(1);
               if (var2 instanceof EntityPlayer) {
                  ((EntityPlayer)var2).func_71029_a(StatList.field_199088_e.func_199076_b(var3));
               }

               this.func_196085_b(0);
            }

         }
      }
   }

   public void func_77961_a(EntityLivingBase var1, EntityPlayer var2) {
      Item var3 = this.func_77973_b();
      if (var3.func_77644_a(this, var1, var2)) {
         var2.func_71029_a(StatList.field_75929_E.func_199076_b(var3));
      }

   }

   public void func_179548_a(World var1, IBlockState var2, BlockPos var3, EntityPlayer var4) {
      Item var5 = this.func_77973_b();
      if (var5.func_179218_a(this, var1, var2, var3, var4)) {
         var4.func_71029_a(StatList.field_75929_E.func_199076_b(var5));
      }

   }

   public boolean func_150998_b(IBlockState var1) {
      return this.func_77973_b().func_150897_b(var1);
   }

   public boolean func_111282_a(EntityPlayer var1, EntityLivingBase var2, EnumHand var3) {
      return this.func_77973_b().func_111207_a(this, var1, var2, var3);
   }

   public ItemStack func_77946_l() {
      ItemStack var1 = new ItemStack(this.func_77973_b(), this.field_77994_a);
      var1.func_190915_d(this.func_190921_D());
      if (this.field_77990_d != null) {
         var1.field_77990_d = this.field_77990_d.func_74737_b();
      }

      return var1;
   }

   public static boolean func_77970_a(ItemStack var0, ItemStack var1) {
      if (var0.func_190926_b() && var1.func_190926_b()) {
         return true;
      } else if (!var0.func_190926_b() && !var1.func_190926_b()) {
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
      if (var0.func_190926_b() && var1.func_190926_b()) {
         return true;
      } else {
         return !var0.func_190926_b() && !var1.func_190926_b() ? var0.func_77959_d(var1) : false;
      }
   }

   private boolean func_77959_d(ItemStack var1) {
      if (this.field_77994_a != var1.field_77994_a) {
         return false;
      } else if (this.func_77973_b() != var1.func_77973_b()) {
         return false;
      } else if (this.field_77990_d == null && var1.field_77990_d != null) {
         return false;
      } else {
         return this.field_77990_d == null || this.field_77990_d.equals(var1.field_77990_d);
      }
   }

   public static boolean func_179545_c(ItemStack var0, ItemStack var1) {
      if (var0 == var1) {
         return true;
      } else {
         return !var0.func_190926_b() && !var1.func_190926_b() ? var0.func_77969_a(var1) : false;
      }
   }

   public static boolean func_185132_d(ItemStack var0, ItemStack var1) {
      if (var0 == var1) {
         return true;
      } else {
         return !var0.func_190926_b() && !var1.func_190926_b() ? var0.func_185136_b(var1) : false;
      }
   }

   public boolean func_77969_a(ItemStack var1) {
      return !var1.func_190926_b() && this.func_77973_b() == var1.func_77973_b();
   }

   public boolean func_185136_b(ItemStack var1) {
      if (!this.func_77984_f()) {
         return this.func_77969_a(var1);
      } else {
         return !var1.func_190926_b() && this.func_77973_b() == var1.func_77973_b();
      }
   }

   public String func_77977_a() {
      return this.func_77973_b().func_77667_c(this);
   }

   public String toString() {
      return this.field_77994_a + "x" + this.func_77973_b().func_77658_a();
   }

   public void func_77945_a(World var1, Entity var2, int var3, boolean var4) {
      if (this.field_77992_b > 0) {
         --this.field_77992_b;
      }

      if (this.func_77973_b() != null) {
         this.func_77973_b().func_77663_a(this, var1, var2, var3, var4);
      }

   }

   public void func_77980_a(World var1, EntityPlayer var2, int var3) {
      var2.func_71064_a(StatList.field_188066_af.func_199076_b(this.func_77973_b()), var3);
      this.func_77973_b().func_77622_d(this, var1, var2);
   }

   public int func_77988_m() {
      return this.func_77973_b().func_77626_a(this);
   }

   public EnumAction func_77975_n() {
      return this.func_77973_b().func_77661_b(this);
   }

   public void func_77974_b(World var1, EntityLivingBase var2, int var3) {
      this.func_77973_b().func_77615_a(this, var1, var2, var3);
   }

   public boolean func_77942_o() {
      return !this.field_190928_g && this.field_77990_d != null && !this.field_77990_d.isEmpty();
   }

   @Nullable
   public NBTTagCompound func_77978_p() {
      return this.field_77990_d;
   }

   public NBTTagCompound func_196082_o() {
      if (this.field_77990_d == null) {
         this.func_77982_d(new NBTTagCompound());
      }

      return this.field_77990_d;
   }

   public NBTTagCompound func_190925_c(String var1) {
      if (this.field_77990_d != null && this.field_77990_d.func_150297_b(var1, 10)) {
         return this.field_77990_d.func_74775_l(var1);
      } else {
         NBTTagCompound var2 = new NBTTagCompound();
         this.func_77983_a(var1, var2);
         return var2;
      }
   }

   @Nullable
   public NBTTagCompound func_179543_a(String var1) {
      return this.field_77990_d != null && this.field_77990_d.func_150297_b(var1, 10) ? this.field_77990_d.func_74775_l(var1) : null;
   }

   public void func_196083_e(String var1) {
      if (this.field_77990_d != null && this.field_77990_d.func_74764_b(var1)) {
         this.field_77990_d.func_82580_o(var1);
         if (this.field_77990_d.isEmpty()) {
            this.field_77990_d = null;
         }
      }

   }

   public NBTTagList func_77986_q() {
      return this.field_77990_d != null ? this.field_77990_d.func_150295_c("Enchantments", 10) : new NBTTagList();
   }

   public void func_77982_d(@Nullable NBTTagCompound var1) {
      this.field_77990_d = var1;
   }

   public ITextComponent func_200301_q() {
      NBTTagCompound var1 = this.func_179543_a("display");
      if (var1 != null && var1.func_150297_b("Name", 8)) {
         try {
            ITextComponent var2 = ITextComponent.Serializer.func_150699_a(var1.func_74779_i("Name"));
            if (var2 != null) {
               return var2;
            }

            var1.func_82580_o("Name");
         } catch (JsonParseException var3) {
            var1.func_82580_o("Name");
         }
      }

      return this.func_77973_b().func_200295_i(this);
   }

   public ItemStack func_200302_a(@Nullable ITextComponent var1) {
      NBTTagCompound var2 = this.func_190925_c("display");
      if (var1 != null) {
         var2.func_74778_a("Name", ITextComponent.Serializer.func_150696_a(var1));
      } else {
         var2.func_82580_o("Name");
      }

      return this;
   }

   public void func_135074_t() {
      NBTTagCompound var1 = this.func_179543_a("display");
      if (var1 != null) {
         var1.func_82580_o("Name");
         if (var1.isEmpty()) {
            this.func_196083_e("display");
         }
      }

      if (this.field_77990_d != null && this.field_77990_d.isEmpty()) {
         this.field_77990_d = null;
      }

   }

   public boolean func_82837_s() {
      NBTTagCompound var1 = this.func_179543_a("display");
      return var1 != null && var1.func_150297_b("Name", 8);
   }

   public List<ITextComponent> func_82840_a(@Nullable EntityPlayer var1, ITooltipFlag var2) {
      ArrayList var3 = Lists.newArrayList();
      ITextComponent var4 = (new TextComponentString("")).func_150257_a(this.func_200301_q()).func_211708_a(this.func_77953_t().field_77937_e);
      if (this.func_82837_s()) {
         var4.func_211708_a(TextFormatting.ITALIC);
      }

      var3.add(var4);
      if (!var2.func_194127_a() && !this.func_82837_s() && this.func_77973_b() == Items.field_151098_aY) {
         var3.add((new TextComponentString("#" + ItemMap.func_195949_f(this))).func_211708_a(TextFormatting.GRAY));
      }

      int var5 = 0;
      if (this.func_77942_o() && this.field_77990_d.func_150297_b("HideFlags", 99)) {
         var5 = this.field_77990_d.func_74762_e("HideFlags");
      }

      if ((var5 & 32) == 0) {
         this.func_77973_b().func_77624_a(this, var1 == null ? null : var1.field_70170_p, var3, var2);
      }

      NBTTagList var6;
      int var7;
      int var22;
      if (this.func_77942_o()) {
         if ((var5 & 1) == 0) {
            var6 = this.func_77986_q();

            for(var7 = 0; var7 < var6.size(); ++var7) {
               NBTTagCompound var8 = var6.func_150305_b(var7);
               Enchantment var9 = (Enchantment)IRegistry.field_212628_q.func_212608_b(ResourceLocation.func_208304_a(var8.func_74779_i("id")));
               if (var9 != null) {
                  var3.add(var9.func_200305_d(var8.func_74762_e("lvl")));
               }
            }
         }

         if (this.field_77990_d.func_150297_b("display", 10)) {
            NBTTagCompound var19 = this.field_77990_d.func_74775_l("display");
            if (var19.func_150297_b("color", 3)) {
               if (var2.func_194127_a()) {
                  var3.add((new TextComponentTranslation("item.color", new Object[]{String.format("#%06X", var19.func_74762_e("color"))})).func_211708_a(TextFormatting.GRAY));
               } else {
                  var3.add((new TextComponentTranslation("item.dyed", new Object[0])).func_211709_a(new TextFormatting[]{TextFormatting.GRAY, TextFormatting.ITALIC}));
               }
            }

            if (var19.func_150299_b("Lore") == 9) {
               NBTTagList var21 = var19.func_150295_c("Lore", 8);

               for(var22 = 0; var22 < var21.size(); ++var22) {
                  var3.add((new TextComponentString(var21.func_150307_f(var22))).func_211709_a(new TextFormatting[]{TextFormatting.DARK_PURPLE, TextFormatting.ITALIC}));
               }
            }
         }
      }

      EntityEquipmentSlot[] var20 = EntityEquipmentSlot.values();
      var7 = var20.length;

      for(var22 = 0; var22 < var7; ++var22) {
         EntityEquipmentSlot var23 = var20[var22];
         Multimap var10 = this.func_111283_C(var23);
         if (!var10.isEmpty() && (var5 & 2) == 0) {
            var3.add(new TextComponentString(""));
            var3.add((new TextComponentTranslation("item.modifiers." + var23.func_188450_d(), new Object[0])).func_211708_a(TextFormatting.GRAY));
            Iterator var11 = var10.entries().iterator();

            while(var11.hasNext()) {
               Entry var12 = (Entry)var11.next();
               AttributeModifier var13 = (AttributeModifier)var12.getValue();
               double var14 = var13.func_111164_d();
               boolean var18 = false;
               if (var1 != null) {
                  if (var13.func_111167_a() == Item.field_111210_e) {
                     var14 += var1.func_110148_a(SharedMonsterAttributes.field_111264_e).func_111125_b();
                     var14 += (double)EnchantmentHelper.func_152377_a(this, CreatureAttribute.UNDEFINED);
                     var18 = true;
                  } else if (var13.func_111167_a() == Item.field_185050_h) {
                     var14 += var1.func_110148_a(SharedMonsterAttributes.field_188790_f).func_111125_b();
                     var18 = true;
                  }
               }

               double var16;
               if (var13.func_111169_c() != 1 && var13.func_111169_c() != 2) {
                  var16 = var14;
               } else {
                  var16 = var14 * 100.0D;
               }

               if (var18) {
                  var3.add((new TextComponentString(" ")).func_150257_a(new TextComponentTranslation("attribute.modifier.equals." + var13.func_111169_c(), new Object[]{field_111284_a.format(var16), new TextComponentTranslation("attribute.name." + (String)var12.getKey(), new Object[0])})).func_211708_a(TextFormatting.DARK_GREEN));
               } else if (var14 > 0.0D) {
                  var3.add((new TextComponentTranslation("attribute.modifier.plus." + var13.func_111169_c(), new Object[]{field_111284_a.format(var16), new TextComponentTranslation("attribute.name." + (String)var12.getKey(), new Object[0])})).func_211708_a(TextFormatting.BLUE));
               } else if (var14 < 0.0D) {
                  var16 *= -1.0D;
                  var3.add((new TextComponentTranslation("attribute.modifier.take." + var13.func_111169_c(), new Object[]{field_111284_a.format(var16), new TextComponentTranslation("attribute.name." + (String)var12.getKey(), new Object[0])})).func_211708_a(TextFormatting.RED));
               }
            }
         }
      }

      if (this.func_77942_o() && this.func_77978_p().func_74767_n("Unbreakable") && (var5 & 4) == 0) {
         var3.add((new TextComponentTranslation("item.unbreakable", new Object[0])).func_211708_a(TextFormatting.BLUE));
      }

      if (this.func_77942_o() && this.field_77990_d.func_150297_b("CanDestroy", 9) && (var5 & 8) == 0) {
         var6 = this.field_77990_d.func_150295_c("CanDestroy", 8);
         if (!var6.isEmpty()) {
            var3.add(new TextComponentString(""));
            var3.add((new TextComponentTranslation("item.canBreak", new Object[0])).func_211708_a(TextFormatting.GRAY));

            for(var7 = 0; var7 < var6.size(); ++var7) {
               var3.addAll(func_206845_f(var6.func_150307_f(var7)));
            }
         }
      }

      if (this.func_77942_o() && this.field_77990_d.func_150297_b("CanPlaceOn", 9) && (var5 & 16) == 0) {
         var6 = this.field_77990_d.func_150295_c("CanPlaceOn", 8);
         if (!var6.isEmpty()) {
            var3.add(new TextComponentString(""));
            var3.add((new TextComponentTranslation("item.canPlace", new Object[0])).func_211708_a(TextFormatting.GRAY));

            for(var7 = 0; var7 < var6.size(); ++var7) {
               var3.addAll(func_206845_f(var6.func_150307_f(var7)));
            }
         }
      }

      if (var2.func_194127_a()) {
         if (this.func_77951_h()) {
            var3.add(new TextComponentTranslation("item.durability", new Object[]{this.func_77958_k() - this.func_77952_i(), this.func_77958_k()}));
         }

         var3.add((new TextComponentString(IRegistry.field_212630_s.func_177774_c(this.func_77973_b()).toString())).func_211708_a(TextFormatting.DARK_GRAY));
         if (this.func_77942_o()) {
            var3.add((new TextComponentTranslation("item.nbt_tags", new Object[]{this.func_77978_p().func_150296_c().size()})).func_211708_a(TextFormatting.DARK_GRAY));
         }
      }

      return var3;
   }

   private static Collection<ITextComponent> func_206845_f(String var0) {
      try {
         BlockStateParser var1 = (new BlockStateParser(new StringReader(var0), true)).func_197243_a(true);
         IBlockState var2 = var1.func_197249_b();
         ResourceLocation var3 = var1.func_199829_d();
         boolean var4 = var2 != null;
         boolean var5 = var3 != null;
         if (var4 || var5) {
            if (var4) {
               return Lists.newArrayList(var2.func_177230_c().func_200291_n().func_211708_a(TextFormatting.DARK_GRAY));
            }

            Tag var6 = BlockTags.func_199896_a().func_199910_a(var3);
            if (var6 != null) {
               Collection var7 = var6.func_199885_a();
               if (!var7.isEmpty()) {
                  return (Collection)var7.stream().map(Block::func_200291_n).map((var0x) -> {
                     return var0x.func_211708_a(TextFormatting.DARK_GRAY);
                  }).collect(Collectors.toList());
               }
            }
         }
      } catch (CommandSyntaxException var8) {
      }

      return Lists.newArrayList((new TextComponentString("missingno")).func_211708_a(TextFormatting.DARK_GRAY));
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
      this.func_196082_o();
      if (!this.field_77990_d.func_150297_b("Enchantments", 9)) {
         this.field_77990_d.func_74782_a("Enchantments", new NBTTagList());
      }

      NBTTagList var3 = this.field_77990_d.func_150295_c("Enchantments", 10);
      NBTTagCompound var4 = new NBTTagCompound();
      var4.func_74778_a("id", String.valueOf(IRegistry.field_212628_q.func_177774_c(var1)));
      var4.func_74777_a("lvl", (short)((byte)var2));
      var3.add((INBTBase)var4);
   }

   public boolean func_77948_v() {
      if (this.field_77990_d != null && this.field_77990_d.func_150297_b("Enchantments", 9)) {
         return !this.field_77990_d.func_150295_c("Enchantments", 10).isEmpty();
      } else {
         return false;
      }
   }

   public void func_77983_a(String var1, INBTBase var2) {
      this.func_196082_o().func_74782_a(var1, var2);
   }

   public boolean func_82839_y() {
      return this.field_82843_f != null;
   }

   public void func_82842_a(@Nullable EntityItemFrame var1) {
      this.field_82843_f = var1;
   }

   @Nullable
   public EntityItemFrame func_82836_z() {
      return this.field_190928_g ? null : this.field_82843_f;
   }

   public int func_82838_A() {
      return this.func_77942_o() && this.field_77990_d.func_150297_b("RepairCost", 3) ? this.field_77990_d.func_74762_e("RepairCost") : 0;
   }

   public void func_82841_c(int var1) {
      this.func_196082_o().func_74768_a("RepairCost", var1);
   }

   public Multimap<String, AttributeModifier> func_111283_C(EntityEquipmentSlot var1) {
      Object var2;
      if (this.func_77942_o() && this.field_77990_d.func_150297_b("AttributeModifiers", 9)) {
         var2 = HashMultimap.create();
         NBTTagList var3 = this.field_77990_d.func_150295_c("AttributeModifiers", 10);

         for(int var4 = 0; var4 < var3.size(); ++var4) {
            NBTTagCompound var5 = var3.func_150305_b(var4);
            AttributeModifier var6 = SharedMonsterAttributes.func_111259_a(var5);
            if (var6 != null && (!var5.func_150297_b("Slot", 8) || var5.func_74779_i("Slot").equals(var1.func_188450_d())) && var6.func_111167_a().getLeastSignificantBits() != 0L && var6.func_111167_a().getMostSignificantBits() != 0L) {
               ((Multimap)var2).put(var5.func_74779_i("AttributeName"), var6);
            }
         }
      } else {
         var2 = this.func_77973_b().func_111205_h(var1);
      }

      return (Multimap)var2;
   }

   public void func_185129_a(String var1, AttributeModifier var2, @Nullable EntityEquipmentSlot var3) {
      this.func_196082_o();
      if (!this.field_77990_d.func_150297_b("AttributeModifiers", 9)) {
         this.field_77990_d.func_74782_a("AttributeModifiers", new NBTTagList());
      }

      NBTTagList var4 = this.field_77990_d.func_150295_c("AttributeModifiers", 10);
      NBTTagCompound var5 = SharedMonsterAttributes.func_111262_a(var2);
      var5.func_74778_a("AttributeName", var1);
      if (var3 != null) {
         var5.func_74778_a("Slot", var3.func_188450_d());
      }

      var4.add((INBTBase)var5);
   }

   public ITextComponent func_151000_E() {
      ITextComponent var1 = (new TextComponentString("")).func_150257_a(this.func_200301_q());
      if (this.func_82837_s()) {
         var1.func_211708_a(TextFormatting.ITALIC);
      }

      ITextComponent var2 = TextComponentUtils.func_197676_a(var1);
      if (!this.field_190928_g) {
         NBTTagCompound var3 = this.func_77955_b(new NBTTagCompound());
         var2.func_211708_a(this.func_77953_t().field_77937_e).func_211710_a((var1x) -> {
            var1x.func_150209_a(new HoverEvent(HoverEvent.Action.SHOW_ITEM, new TextComponentString(var3.toString())));
         });
      }

      return var2;
   }

   private static boolean func_206846_a(BlockWorldState var0, @Nullable BlockWorldState var1) {
      if (var1 != null && var0.func_177509_a() == var1.func_177509_a()) {
         if (var0.func_177507_b() == null && var1.func_177507_b() == null) {
            return true;
         } else {
            return var0.func_177507_b() != null && var1.func_177507_b() != null ? Objects.equals(var0.func_177507_b().func_189515_b(new NBTTagCompound()), var1.func_177507_b().func_189515_b(new NBTTagCompound())) : false;
         }
      } else {
         return false;
      }
   }

   public boolean func_206848_a(NetworkTagManager var1, BlockWorldState var2) {
      if (func_206846_a(var2, this.field_179552_h)) {
         return this.field_179553_i;
      } else {
         this.field_179552_h = var2;
         if (this.func_77942_o() && this.field_77990_d.func_150297_b("CanDestroy", 9)) {
            NBTTagList var3 = this.field_77990_d.func_150295_c("CanDestroy", 8);

            for(int var4 = 0; var4 < var3.size(); ++var4) {
               String var5 = var3.func_150307_f(var4);

               try {
                  Predicate var6 = BlockPredicateArgument.func_199824_a().parse(new StringReader(var5)).create(var1);
                  if (var6.test(var2)) {
                     this.field_179553_i = true;
                     return true;
                  }
               } catch (CommandSyntaxException var7) {
               }
            }
         }

         this.field_179553_i = false;
         return false;
      }
   }

   public boolean func_206847_b(NetworkTagManager var1, BlockWorldState var2) {
      if (func_206846_a(var2, this.field_179550_j)) {
         return this.field_179551_k;
      } else {
         this.field_179550_j = var2;
         if (this.func_77942_o() && this.field_77990_d.func_150297_b("CanPlaceOn", 9)) {
            NBTTagList var3 = this.field_77990_d.func_150295_c("CanPlaceOn", 8);

            for(int var4 = 0; var4 < var3.size(); ++var4) {
               String var5 = var3.func_150307_f(var4);

               try {
                  Predicate var6 = BlockPredicateArgument.func_199824_a().parse(new StringReader(var5)).create(var1);
                  if (var6.test(var2)) {
                     this.field_179551_k = true;
                     return true;
                  }
               } catch (CommandSyntaxException var7) {
               }
            }
         }

         this.field_179551_k = false;
         return false;
      }
   }

   public int func_190921_D() {
      return this.field_77992_b;
   }

   public void func_190915_d(int var1) {
      this.field_77992_b = var1;
   }

   public int func_190916_E() {
      return this.field_190928_g ? 0 : this.field_77994_a;
   }

   public void func_190920_e(int var1) {
      this.field_77994_a = var1;
      this.func_190923_F();
   }

   public void func_190917_f(int var1) {
      this.func_190920_e(this.field_77994_a + var1);
   }

   public void func_190918_g(int var1) {
      this.func_190917_f(-var1);
   }
}
