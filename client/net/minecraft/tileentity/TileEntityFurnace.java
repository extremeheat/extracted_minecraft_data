package net.minecraft.tileentity;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import javax.annotation.Nullable;
import net.minecraft.block.BlockFurnace;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.ContainerFurnace;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.IRecipeHelperPopulator;
import net.minecraft.inventory.IRecipeHolder;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.inventory.ItemStackHelper;
import net.minecraft.inventory.SlotFurnaceFuel;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.FurnaceRecipe;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.RecipeItemHelper;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.Tag;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.IItemProvider;
import net.minecraft.util.ITickable;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.World;

public class TileEntityFurnace extends TileEntityLockable implements ISidedInventory, IRecipeHolder, IRecipeHelperPopulator, ITickable {
   private static final int[] field_145962_k = new int[]{0};
   private static final int[] field_145959_l = new int[]{2, 1};
   private static final int[] field_145960_m = new int[]{1};
   private NonNullList<ItemStack> field_145957_n;
   private int field_145956_a;
   private int field_145963_i;
   private int field_174906_k;
   private int field_174905_l;
   private ITextComponent field_145958_o;
   private final Map<ResourceLocation, Integer> field_203901_m;

   private static void func_201563_a(Map<Item, Integer> var0, Tag<Item> var1, int var2) {
      Iterator var3 = var1.func_199885_a().iterator();

      while(var3.hasNext()) {
         Item var4 = (Item)var3.next();
         var0.put(var4, var2);
      }

   }

   private static void func_203065_a(Map<Item, Integer> var0, IItemProvider var1, int var2) {
      var0.put(var1.func_199767_j(), var2);
   }

   public static Map<Item, Integer> func_201564_p() {
      LinkedHashMap var0 = Maps.newLinkedHashMap();
      func_203065_a(var0, Items.field_151129_at, 20000);
      func_203065_a(var0, Blocks.field_150402_ci, 16000);
      func_203065_a(var0, Items.field_151072_bj, 2400);
      func_203065_a(var0, Items.field_151044_h, 1600);
      func_203065_a(var0, Items.field_196155_l, 1600);
      func_201563_a(var0, ItemTags.field_200038_h, 300);
      func_201563_a(var0, ItemTags.field_199905_b, 300);
      func_201563_a(var0, ItemTags.field_202898_h, 300);
      func_201563_a(var0, ItemTags.field_202899_i, 150);
      func_201563_a(var0, ItemTags.field_212188_k, 300);
      func_201563_a(var0, ItemTags.field_202900_j, 300);
      func_203065_a(var0, Blocks.field_180407_aO, 300);
      func_203065_a(var0, Blocks.field_180404_aQ, 300);
      func_203065_a(var0, Blocks.field_180408_aP, 300);
      func_203065_a(var0, Blocks.field_180403_aR, 300);
      func_203065_a(var0, Blocks.field_180406_aS, 300);
      func_203065_a(var0, Blocks.field_180405_aT, 300);
      func_203065_a(var0, Blocks.field_180390_bo, 300);
      func_203065_a(var0, Blocks.field_180392_bq, 300);
      func_203065_a(var0, Blocks.field_180391_bp, 300);
      func_203065_a(var0, Blocks.field_180386_br, 300);
      func_203065_a(var0, Blocks.field_180385_bs, 300);
      func_203065_a(var0, Blocks.field_180387_bt, 300);
      func_203065_a(var0, Blocks.field_196586_al, 300);
      func_203065_a(var0, Blocks.field_150342_X, 300);
      func_203065_a(var0, Blocks.field_150421_aI, 300);
      func_203065_a(var0, Blocks.field_150486_ae, 300);
      func_203065_a(var0, Blocks.field_150447_bR, 300);
      func_203065_a(var0, Blocks.field_150462_ai, 300);
      func_203065_a(var0, Blocks.field_150453_bW, 300);
      func_201563_a(var0, ItemTags.field_202901_n, 300);
      func_203065_a(var0, Items.field_151031_f, 300);
      func_203065_a(var0, Items.field_151112_aM, 300);
      func_203065_a(var0, Blocks.field_150468_ap, 300);
      func_203065_a(var0, Items.field_151155_ap, 200);
      func_203065_a(var0, Items.field_151038_n, 200);
      func_203065_a(var0, Items.field_151041_m, 200);
      func_203065_a(var0, Items.field_151017_I, 200);
      func_203065_a(var0, Items.field_151053_p, 200);
      func_203065_a(var0, Items.field_151039_o, 200);
      func_201563_a(var0, ItemTags.field_200154_g, 200);
      func_201563_a(var0, ItemTags.field_202902_o, 200);
      func_201563_a(var0, ItemTags.field_199904_a, 100);
      func_201563_a(var0, ItemTags.field_200153_d, 100);
      func_203065_a(var0, Items.field_151055_y, 100);
      func_201563_a(var0, ItemTags.field_200037_g, 100);
      func_203065_a(var0, Items.field_151054_z, 100);
      func_201563_a(var0, ItemTags.field_200035_e, 67);
      func_203065_a(var0, Blocks.field_203216_jz, 4001);
      return var0;
   }

   public TileEntityFurnace() {
      super(TileEntityType.field_200971_b);
      this.field_145957_n = NonNullList.func_191197_a(3, ItemStack.field_190927_a);
      this.field_203901_m = Maps.newHashMap();
   }

   public int func_70302_i_() {
      return this.field_145957_n.size();
   }

   public boolean func_191420_l() {
      Iterator var1 = this.field_145957_n.iterator();

      ItemStack var2;
      do {
         if (!var1.hasNext()) {
            return true;
         }

         var2 = (ItemStack)var1.next();
      } while(var2.func_190926_b());

      return false;
   }

   public ItemStack func_70301_a(int var1) {
      return (ItemStack)this.field_145957_n.get(var1);
   }

   public ItemStack func_70298_a(int var1, int var2) {
      return ItemStackHelper.func_188382_a(this.field_145957_n, var1, var2);
   }

   public ItemStack func_70304_b(int var1) {
      return ItemStackHelper.func_188383_a(this.field_145957_n, var1);
   }

   public void func_70299_a(int var1, ItemStack var2) {
      ItemStack var3 = (ItemStack)this.field_145957_n.get(var1);
      boolean var4 = !var2.func_190926_b() && var2.func_77969_a(var3) && ItemStack.func_77970_a(var2, var3);
      this.field_145957_n.set(var1, var2);
      if (var2.func_190916_E() > this.func_70297_j_()) {
         var2.func_190920_e(this.func_70297_j_());
      }

      if (var1 == 0 && !var4) {
         this.field_174905_l = this.func_201562_r();
         this.field_174906_k = 0;
         this.func_70296_d();
      }

   }

   public ITextComponent func_200200_C_() {
      return (ITextComponent)(this.field_145958_o != null ? this.field_145958_o : new TextComponentTranslation("container.furnace", new Object[0]));
   }

   public boolean func_145818_k_() {
      return this.field_145958_o != null;
   }

   @Nullable
   public ITextComponent func_200201_e() {
      return this.field_145958_o;
   }

   public void func_200225_a(@Nullable ITextComponent var1) {
      this.field_145958_o = var1;
   }

   public void func_145839_a(NBTTagCompound var1) {
      super.func_145839_a(var1);
      this.field_145957_n = NonNullList.func_191197_a(this.func_70302_i_(), ItemStack.field_190927_a);
      ItemStackHelper.func_191283_b(var1, this.field_145957_n);
      this.field_145956_a = var1.func_74765_d("BurnTime");
      this.field_174906_k = var1.func_74765_d("CookTime");
      this.field_174905_l = var1.func_74765_d("CookTimeTotal");
      this.field_145963_i = func_145952_a((ItemStack)this.field_145957_n.get(1));
      short var2 = var1.func_74765_d("RecipesUsedSize");

      for(int var3 = 0; var3 < var2; ++var3) {
         ResourceLocation var4 = new ResourceLocation(var1.func_74779_i("RecipeLocation" + var3));
         int var5 = var1.func_74762_e("RecipeAmount" + var3);
         this.field_203901_m.put(var4, var5);
      }

      if (var1.func_150297_b("CustomName", 8)) {
         this.field_145958_o = ITextComponent.Serializer.func_150699_a(var1.func_74779_i("CustomName"));
      }

   }

   public NBTTagCompound func_189515_b(NBTTagCompound var1) {
      super.func_189515_b(var1);
      var1.func_74777_a("BurnTime", (short)this.field_145956_a);
      var1.func_74777_a("CookTime", (short)this.field_174906_k);
      var1.func_74777_a("CookTimeTotal", (short)this.field_174905_l);
      ItemStackHelper.func_191282_a(var1, this.field_145957_n);
      var1.func_74777_a("RecipesUsedSize", (short)this.field_203901_m.size());
      int var2 = 0;

      for(Iterator var3 = this.field_203901_m.entrySet().iterator(); var3.hasNext(); ++var2) {
         Entry var4 = (Entry)var3.next();
         var1.func_74778_a("RecipeLocation" + var2, ((ResourceLocation)var4.getKey()).toString());
         var1.func_74768_a("RecipeAmount" + var2, (Integer)var4.getValue());
      }

      if (this.field_145958_o != null) {
         var1.func_74778_a("CustomName", ITextComponent.Serializer.func_150696_a(this.field_145958_o));
      }

      return var1;
   }

   public int func_70297_j_() {
      return 64;
   }

   private boolean func_145950_i() {
      return this.field_145956_a > 0;
   }

   public static boolean func_174903_a(IInventory var0) {
      return var0.func_174887_a_(0) > 0;
   }

   public void func_73660_a() {
      boolean var1 = this.func_145950_i();
      boolean var2 = false;
      if (this.func_145950_i()) {
         --this.field_145956_a;
      }

      if (!this.field_145850_b.field_72995_K) {
         ItemStack var3 = (ItemStack)this.field_145957_n.get(1);
         if (!this.func_145950_i() && (var3.func_190926_b() || ((ItemStack)this.field_145957_n.get(0)).func_190926_b())) {
            if (!this.func_145950_i() && this.field_174906_k > 0) {
               this.field_174906_k = MathHelper.func_76125_a(this.field_174906_k - 2, 0, this.field_174905_l);
            }
         } else {
            IRecipe var4 = this.field_145850_b.func_199532_z().func_199515_b(this, this.field_145850_b);
            if (!this.func_145950_i() && this.func_201566_b(var4)) {
               this.field_145956_a = func_145952_a(var3);
               this.field_145963_i = this.field_145956_a;
               if (this.func_145950_i()) {
                  var2 = true;
                  if (!var3.func_190926_b()) {
                     Item var5 = var3.func_77973_b();
                     var3.func_190918_g(1);
                     if (var3.func_190926_b()) {
                        Item var6 = var5.func_77668_q();
                        this.field_145957_n.set(1, var6 == null ? ItemStack.field_190927_a : new ItemStack(var6));
                     }
                  }
               }
            }

            if (this.func_145950_i() && this.func_201566_b(var4)) {
               ++this.field_174906_k;
               if (this.field_174906_k == this.field_174905_l) {
                  this.field_174906_k = 0;
                  this.field_174905_l = this.func_201562_r();
                  this.func_201565_c(var4);
                  var2 = true;
               }
            } else {
               this.field_174906_k = 0;
            }
         }

         if (var1 != this.func_145950_i()) {
            var2 = true;
            this.field_145850_b.func_180501_a(this.field_174879_c, (IBlockState)this.field_145850_b.func_180495_p(this.field_174879_c).func_206870_a(BlockFurnace.field_196325_b, this.func_145950_i()), 3);
         }
      }

      if (var2) {
         this.func_70296_d();
      }

   }

   private int func_201562_r() {
      FurnaceRecipe var1 = (FurnaceRecipe)this.field_145850_b.func_199532_z().func_199515_b(this, this.field_145850_b);
      return var1 != null ? var1.func_201830_h() : 200;
   }

   private boolean func_201566_b(@Nullable IRecipe var1) {
      if (!((ItemStack)this.field_145957_n.get(0)).func_190926_b() && var1 != null) {
         ItemStack var2 = var1.func_77571_b();
         if (var2.func_190926_b()) {
            return false;
         } else {
            ItemStack var3 = (ItemStack)this.field_145957_n.get(2);
            if (var3.func_190926_b()) {
               return true;
            } else if (!var3.func_77969_a(var2)) {
               return false;
            } else if (var3.func_190916_E() < this.func_70297_j_() && var3.func_190916_E() < var3.func_77976_d()) {
               return true;
            } else {
               return var3.func_190916_E() < var2.func_77976_d();
            }
         }
      } else {
         return false;
      }
   }

   private void func_201565_c(@Nullable IRecipe var1) {
      if (var1 != null && this.func_201566_b(var1)) {
         ItemStack var2 = (ItemStack)this.field_145957_n.get(0);
         ItemStack var3 = var1.func_77571_b();
         ItemStack var4 = (ItemStack)this.field_145957_n.get(2);
         if (var4.func_190926_b()) {
            this.field_145957_n.set(2, var3.func_77946_l());
         } else if (var4.func_77973_b() == var3.func_77973_b()) {
            var4.func_190917_f(1);
         }

         if (!this.field_145850_b.field_72995_K) {
            this.func_201561_a(this.field_145850_b, (EntityPlayerMP)null, var1);
         }

         if (var2.func_77973_b() == Blocks.field_196577_ad.func_199767_j() && !((ItemStack)this.field_145957_n.get(1)).func_190926_b() && ((ItemStack)this.field_145957_n.get(1)).func_77973_b() == Items.field_151133_ar) {
            this.field_145957_n.set(1, new ItemStack(Items.field_151131_as));
         }

         var2.func_190918_g(1);
      }
   }

   private static int func_145952_a(ItemStack var0) {
      if (var0.func_190926_b()) {
         return 0;
      } else {
         Item var1 = var0.func_77973_b();
         return (Integer)func_201564_p().getOrDefault(var1, 0);
      }
   }

   public static boolean func_145954_b(ItemStack var0) {
      return func_201564_p().containsKey(var0.func_77973_b());
   }

   public boolean func_70300_a(EntityPlayer var1) {
      if (this.field_145850_b.func_175625_s(this.field_174879_c) != this) {
         return false;
      } else {
         return var1.func_70092_e((double)this.field_174879_c.func_177958_n() + 0.5D, (double)this.field_174879_c.func_177956_o() + 0.5D, (double)this.field_174879_c.func_177952_p() + 0.5D) <= 64.0D;
      }
   }

   public void func_174889_b(EntityPlayer var1) {
   }

   public void func_174886_c(EntityPlayer var1) {
   }

   public boolean func_94041_b(int var1, ItemStack var2) {
      if (var1 == 2) {
         return false;
      } else if (var1 != 1) {
         return true;
      } else {
         ItemStack var3 = (ItemStack)this.field_145957_n.get(1);
         return func_145954_b(var2) || SlotFurnaceFuel.func_178173_c_(var2) && var3.func_77973_b() != Items.field_151133_ar;
      }
   }

   public int[] func_180463_a(EnumFacing var1) {
      if (var1 == EnumFacing.DOWN) {
         return field_145959_l;
      } else {
         return var1 == EnumFacing.UP ? field_145962_k : field_145960_m;
      }
   }

   public boolean func_180462_a(int var1, ItemStack var2, @Nullable EnumFacing var3) {
      return this.func_94041_b(var1, var2);
   }

   public boolean func_180461_b(int var1, ItemStack var2, EnumFacing var3) {
      if (var3 == EnumFacing.DOWN && var1 == 1) {
         Item var4 = var2.func_77973_b();
         if (var4 != Items.field_151131_as && var4 != Items.field_151133_ar) {
            return false;
         }
      }

      return true;
   }

   public String func_174875_k() {
      return "minecraft:furnace";
   }

   public Container func_174876_a(InventoryPlayer var1, EntityPlayer var2) {
      return new ContainerFurnace(var1, this);
   }

   public int func_174887_a_(int var1) {
      switch(var1) {
      case 0:
         return this.field_145956_a;
      case 1:
         return this.field_145963_i;
      case 2:
         return this.field_174906_k;
      case 3:
         return this.field_174905_l;
      default:
         return 0;
      }
   }

   public void func_174885_b(int var1, int var2) {
      switch(var1) {
      case 0:
         this.field_145956_a = var2;
         break;
      case 1:
         this.field_145963_i = var2;
         break;
      case 2:
         this.field_174906_k = var2;
         break;
      case 3:
         this.field_174905_l = var2;
      }

   }

   public int func_174890_g() {
      return 4;
   }

   public void func_174888_l() {
      this.field_145957_n.clear();
   }

   public void func_194018_a(RecipeItemHelper var1) {
      Iterator var2 = this.field_145957_n.iterator();

      while(var2.hasNext()) {
         ItemStack var3 = (ItemStack)var2.next();
         var1.func_194112_a(var3);
      }

   }

   public void func_193056_a(IRecipe var1) {
      if (this.field_203901_m.containsKey(var1.func_199560_c())) {
         this.field_203901_m.put(var1.func_199560_c(), (Integer)this.field_203901_m.get(var1.func_199560_c()) + 1);
      } else {
         this.field_203901_m.put(var1.func_199560_c(), 1);
      }

   }

   @Nullable
   public IRecipe func_193055_i() {
      return null;
   }

   public Map<ResourceLocation, Integer> func_203900_q() {
      return this.field_203901_m;
   }

   public boolean func_201561_a(World var1, EntityPlayerMP var2, @Nullable IRecipe var3) {
      if (var3 != null) {
         this.func_193056_a(var3);
         return true;
      } else {
         return false;
      }
   }

   public void func_201560_d(EntityPlayer var1) {
      if (!this.field_145850_b.func_82736_K().func_82766_b("doLimitedCrafting")) {
         ArrayList var2 = Lists.newArrayList();
         Iterator var3 = this.field_203901_m.keySet().iterator();

         while(var3.hasNext()) {
            ResourceLocation var4 = (ResourceLocation)var3.next();
            IRecipe var5 = var1.field_70170_p.func_199532_z().func_199517_a(var4);
            if (var5 != null) {
               var2.add(var5);
            }
         }

         var1.func_195065_a(var2);
      }

      this.field_203901_m.clear();
   }
}
