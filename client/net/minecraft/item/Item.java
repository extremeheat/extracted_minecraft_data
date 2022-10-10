package net.minecraft.item;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.item.EntityBoat;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.entity.item.EntityPainting;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Fluids;
import net.minecraft.init.Items;
import net.minecraft.init.MobEffects;
import net.minecraft.init.SoundEvents;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.potion.PotionEffect;
import net.minecraft.tags.Tag;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.EnumHandSide;
import net.minecraft.util.IItemProvider;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceFluidMode;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.registry.IRegistry;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.World;

public class Item implements IItemProvider {
   public static final Map<Block, Item> field_179220_a = Maps.newHashMap();
   private static final IItemPropertyGetter field_185046_b = (var0, var1, var2) -> {
      return var0.func_77951_h() ? 1.0F : 0.0F;
   };
   private static final IItemPropertyGetter field_185047_c = (var0, var1, var2) -> {
      return MathHelper.func_76131_a((float)var0.func_77952_i() / (float)var0.func_77958_k(), 0.0F, 1.0F);
   };
   private static final IItemPropertyGetter field_185048_d = (var0, var1, var2) -> {
      return var2 != null && var2.func_184591_cq() != EnumHandSide.RIGHT ? 1.0F : 0.0F;
   };
   private static final IItemPropertyGetter field_185049_e = (var0, var1, var2) -> {
      return var2 instanceof EntityPlayer ? ((EntityPlayer)var2).func_184811_cZ().func_185143_a(var0.func_77973_b(), 0.0F) : 0.0F;
   };
   protected static final UUID field_111210_e = UUID.fromString("CB3F55D3-645C-4F38-A497-9C13A33DB5CF");
   protected static final UUID field_185050_h = UUID.fromString("FA233E1C-4180-4865-B01B-BCCE9785ACA3");
   protected static Random field_77697_d = new Random();
   private final Map<ResourceLocation, IItemPropertyGetter> field_185051_m = Maps.newHashMap();
   protected final ItemGroup field_77701_a;
   private final EnumRarity field_208075_l;
   private final int field_77777_bU;
   private final int field_77699_b;
   private final Item field_77700_c;
   @Nullable
   private String field_77774_bZ;

   public static int func_150891_b(Item var0) {
      return var0 == null ? 0 : IRegistry.field_212630_s.func_148757_b(var0);
   }

   public static Item func_150899_d(int var0) {
      return (Item)IRegistry.field_212630_s.func_148754_a(var0);
   }

   @Deprecated
   public static Item func_150898_a(Block var0) {
      Item var1 = (Item)field_179220_a.get(var0);
      return var1 == null ? Items.field_190931_a : var1;
   }

   public Item(Item.Properties var1) {
      super();
      this.func_185043_a(new ResourceLocation("lefthanded"), field_185048_d);
      this.func_185043_a(new ResourceLocation("cooldown"), field_185049_e);
      this.field_77701_a = var1.field_200923_d;
      this.field_208075_l = var1.field_208104_e;
      this.field_77700_c = var1.field_200922_c;
      this.field_77699_b = var1.field_200921_b;
      this.field_77777_bU = var1.field_200920_a;
      if (this.field_77699_b > 0) {
         this.func_185043_a(new ResourceLocation("damaged"), field_185046_b);
         this.func_185043_a(new ResourceLocation("damage"), field_185047_c);
      }

   }

   @Nullable
   public IItemPropertyGetter func_185045_a(ResourceLocation var1) {
      return (IItemPropertyGetter)this.field_185051_m.get(var1);
   }

   public boolean func_185040_i() {
      return !this.field_185051_m.isEmpty();
   }

   public boolean func_179215_a(NBTTagCompound var1) {
      return false;
   }

   public boolean func_195938_a(IBlockState var1, World var2, BlockPos var3, EntityPlayer var4) {
      return true;
   }

   public Item func_199767_j() {
      return this;
   }

   public final void func_185043_a(ResourceLocation var1, IItemPropertyGetter var2) {
      this.field_185051_m.put(var1, var2);
   }

   public EnumActionResult func_195939_a(ItemUseContext var1) {
      return EnumActionResult.PASS;
   }

   public float func_150893_a(ItemStack var1, IBlockState var2) {
      return 1.0F;
   }

   public ActionResult<ItemStack> func_77659_a(World var1, EntityPlayer var2, EnumHand var3) {
      return new ActionResult(EnumActionResult.PASS, var2.func_184586_b(var3));
   }

   public ItemStack func_77654_b(ItemStack var1, World var2, EntityLivingBase var3) {
      return var1;
   }

   public final int func_77639_j() {
      return this.field_77777_bU;
   }

   public final int func_77612_l() {
      return this.field_77699_b;
   }

   public boolean func_77645_m() {
      return this.field_77699_b > 0;
   }

   public boolean func_77644_a(ItemStack var1, EntityLivingBase var2, EntityLivingBase var3) {
      return false;
   }

   public boolean func_179218_a(ItemStack var1, World var2, IBlockState var3, BlockPos var4, EntityLivingBase var5) {
      return false;
   }

   public boolean func_150897_b(IBlockState var1) {
      return false;
   }

   public boolean func_111207_a(ItemStack var1, EntityPlayer var2, EntityLivingBase var3, EnumHand var4) {
      return false;
   }

   public ITextComponent func_200296_o() {
      return new TextComponentTranslation(this.func_77658_a(), new Object[0]);
   }

   protected String func_195935_o() {
      if (this.field_77774_bZ == null) {
         this.field_77774_bZ = Util.func_200697_a("item", IRegistry.field_212630_s.func_177774_c(this));
      }

      return this.field_77774_bZ;
   }

   public String func_77658_a() {
      return this.func_195935_o();
   }

   public String func_77667_c(ItemStack var1) {
      return this.func_77658_a();
   }

   public boolean func_77651_p() {
      return true;
   }

   @Nullable
   public final Item func_77668_q() {
      return this.field_77700_c;
   }

   public boolean func_77634_r() {
      return this.field_77700_c != null;
   }

   public void func_77663_a(ItemStack var1, World var2, Entity var3, int var4, boolean var5) {
   }

   public void func_77622_d(ItemStack var1, World var2, EntityPlayer var3) {
   }

   public boolean func_77643_m_() {
      return false;
   }

   public EnumAction func_77661_b(ItemStack var1) {
      return EnumAction.NONE;
   }

   public int func_77626_a(ItemStack var1) {
      return 0;
   }

   public void func_77615_a(ItemStack var1, World var2, EntityLivingBase var3, int var4) {
   }

   public void func_77624_a(ItemStack var1, @Nullable World var2, List<ITextComponent> var3, ITooltipFlag var4) {
   }

   public ITextComponent func_200295_i(ItemStack var1) {
      return new TextComponentTranslation(this.func_77667_c(var1), new Object[0]);
   }

   public boolean func_77636_d(ItemStack var1) {
      return var1.func_77948_v();
   }

   public EnumRarity func_77613_e(ItemStack var1) {
      if (!var1.func_77948_v()) {
         return this.field_208075_l;
      } else {
         switch(this.field_208075_l) {
         case COMMON:
         case UNCOMMON:
            return EnumRarity.RARE;
         case RARE:
            return EnumRarity.EPIC;
         case EPIC:
         default:
            return this.field_208075_l;
         }
      }
   }

   public boolean func_77616_k(ItemStack var1) {
      return this.func_77639_j() == 1 && this.func_77645_m();
   }

   @Nullable
   protected RayTraceResult func_77621_a(World var1, EntityPlayer var2, boolean var3) {
      float var4 = var2.field_70125_A;
      float var5 = var2.field_70177_z;
      double var6 = var2.field_70165_t;
      double var8 = var2.field_70163_u + (double)var2.func_70047_e();
      double var10 = var2.field_70161_v;
      Vec3d var12 = new Vec3d(var6, var8, var10);
      float var13 = MathHelper.func_76134_b(-var5 * 0.017453292F - 3.1415927F);
      float var14 = MathHelper.func_76126_a(-var5 * 0.017453292F - 3.1415927F);
      float var15 = -MathHelper.func_76134_b(-var4 * 0.017453292F);
      float var16 = MathHelper.func_76126_a(-var4 * 0.017453292F);
      float var17 = var14 * var15;
      float var19 = var13 * var15;
      double var20 = 5.0D;
      Vec3d var22 = var12.func_72441_c((double)var17 * 5.0D, (double)var16 * 5.0D, (double)var19 * 5.0D);
      return var1.func_200259_a(var12, var22, var3 ? RayTraceFluidMode.SOURCE_ONLY : RayTraceFluidMode.NEVER, false, false);
   }

   public int func_77619_b() {
      return 0;
   }

   public void func_150895_a(ItemGroup var1, NonNullList<ItemStack> var2) {
      if (this.func_194125_a(var1)) {
         var2.add(new ItemStack(this));
      }

   }

   protected boolean func_194125_a(ItemGroup var1) {
      ItemGroup var2 = this.func_77640_w();
      return var2 != null && (var1 == ItemGroup.field_78027_g || var1 == var2);
   }

   @Nullable
   public final ItemGroup func_77640_w() {
      return this.field_77701_a;
   }

   public boolean func_82789_a(ItemStack var1, ItemStack var2) {
      return false;
   }

   public Multimap<String, AttributeModifier> func_111205_h(EntityEquipmentSlot var1) {
      return HashMultimap.create();
   }

   public static void func_150900_l() {
      func_179214_a(Blocks.field_150350_a, new ItemAir(Blocks.field_150350_a, new Item.Properties()));
      func_200879_a(Blocks.field_150348_b, ItemGroup.field_78030_b);
      func_200879_a(Blocks.field_196650_c, ItemGroup.field_78030_b);
      func_200879_a(Blocks.field_196652_d, ItemGroup.field_78030_b);
      func_200879_a(Blocks.field_196654_e, ItemGroup.field_78030_b);
      func_200879_a(Blocks.field_196655_f, ItemGroup.field_78030_b);
      func_200879_a(Blocks.field_196656_g, ItemGroup.field_78030_b);
      func_200879_a(Blocks.field_196657_h, ItemGroup.field_78030_b);
      func_200879_a(Blocks.field_196658_i, ItemGroup.field_78030_b);
      func_200879_a(Blocks.field_150346_d, ItemGroup.field_78030_b);
      func_200879_a(Blocks.field_196660_k, ItemGroup.field_78030_b);
      func_200879_a(Blocks.field_196661_l, ItemGroup.field_78030_b);
      func_200879_a(Blocks.field_150347_e, ItemGroup.field_78030_b);
      func_200879_a(Blocks.field_196662_n, ItemGroup.field_78030_b);
      func_200879_a(Blocks.field_196664_o, ItemGroup.field_78030_b);
      func_200879_a(Blocks.field_196666_p, ItemGroup.field_78030_b);
      func_200879_a(Blocks.field_196668_q, ItemGroup.field_78030_b);
      func_200879_a(Blocks.field_196670_r, ItemGroup.field_78030_b);
      func_200879_a(Blocks.field_196672_s, ItemGroup.field_78030_b);
      func_200879_a(Blocks.field_196674_t, ItemGroup.field_78031_c);
      func_200879_a(Blocks.field_196675_u, ItemGroup.field_78031_c);
      func_200879_a(Blocks.field_196676_v, ItemGroup.field_78031_c);
      func_200879_a(Blocks.field_196678_w, ItemGroup.field_78031_c);
      func_200879_a(Blocks.field_196679_x, ItemGroup.field_78031_c);
      func_200879_a(Blocks.field_196680_y, ItemGroup.field_78031_c);
      func_200879_a(Blocks.field_150357_h, ItemGroup.field_78030_b);
      func_200879_a(Blocks.field_150354_m, ItemGroup.field_78030_b);
      func_200879_a(Blocks.field_196611_F, ItemGroup.field_78030_b);
      func_200879_a(Blocks.field_150351_n, ItemGroup.field_78030_b);
      func_200879_a(Blocks.field_150352_o, ItemGroup.field_78030_b);
      func_200879_a(Blocks.field_150366_p, ItemGroup.field_78030_b);
      func_200879_a(Blocks.field_150365_q, ItemGroup.field_78030_b);
      func_200879_a(Blocks.field_196617_K, ItemGroup.field_78030_b);
      func_200879_a(Blocks.field_196618_L, ItemGroup.field_78030_b);
      func_200879_a(Blocks.field_196619_M, ItemGroup.field_78030_b);
      func_200879_a(Blocks.field_196620_N, ItemGroup.field_78030_b);
      func_200879_a(Blocks.field_196621_O, ItemGroup.field_78030_b);
      func_200879_a(Blocks.field_196623_P, ItemGroup.field_78030_b);
      func_200879_a(Blocks.field_203204_R, ItemGroup.field_78030_b);
      func_200879_a(Blocks.field_203205_S, ItemGroup.field_78030_b);
      func_200879_a(Blocks.field_203206_T, ItemGroup.field_78030_b);
      func_200879_a(Blocks.field_203207_U, ItemGroup.field_78030_b);
      func_200879_a(Blocks.field_203208_V, ItemGroup.field_78030_b);
      func_200879_a(Blocks.field_203209_W, ItemGroup.field_78030_b);
      func_200879_a(Blocks.field_209389_ab, ItemGroup.field_78030_b);
      func_200879_a(Blocks.field_209390_ac, ItemGroup.field_78030_b);
      func_200879_a(Blocks.field_209391_ad, ItemGroup.field_78030_b);
      func_200879_a(Blocks.field_209392_ae, ItemGroup.field_78030_b);
      func_200879_a(Blocks.field_209393_af, ItemGroup.field_78030_b);
      func_200879_a(Blocks.field_209394_ag, ItemGroup.field_78030_b);
      func_200879_a(Blocks.field_196626_Q, ItemGroup.field_78030_b);
      func_200879_a(Blocks.field_196629_R, ItemGroup.field_78030_b);
      func_200879_a(Blocks.field_196631_S, ItemGroup.field_78030_b);
      func_200879_a(Blocks.field_196634_T, ItemGroup.field_78030_b);
      func_200879_a(Blocks.field_196637_U, ItemGroup.field_78030_b);
      func_200879_a(Blocks.field_196639_V, ItemGroup.field_78030_b);
      func_200879_a(Blocks.field_196642_W, ItemGroup.field_78031_c);
      func_200879_a(Blocks.field_196645_X, ItemGroup.field_78031_c);
      func_200879_a(Blocks.field_196647_Y, ItemGroup.field_78031_c);
      func_200879_a(Blocks.field_196648_Z, ItemGroup.field_78031_c);
      func_200879_a(Blocks.field_196572_aa, ItemGroup.field_78031_c);
      func_200879_a(Blocks.field_196574_ab, ItemGroup.field_78031_c);
      func_200879_a(Blocks.field_150360_v, ItemGroup.field_78030_b);
      func_200879_a(Blocks.field_196577_ad, ItemGroup.field_78030_b);
      func_200879_a(Blocks.field_150359_w, ItemGroup.field_78030_b);
      func_200879_a(Blocks.field_150369_x, ItemGroup.field_78030_b);
      func_200879_a(Blocks.field_150368_y, ItemGroup.field_78030_b);
      func_200879_a(Blocks.field_150367_z, ItemGroup.field_78028_d);
      func_200879_a(Blocks.field_150322_A, ItemGroup.field_78030_b);
      func_200879_a(Blocks.field_196583_aj, ItemGroup.field_78030_b);
      func_200879_a(Blocks.field_196585_ak, ItemGroup.field_78030_b);
      func_200879_a(Blocks.field_196586_al, ItemGroup.field_78028_d);
      func_200879_a(Blocks.field_196552_aC, ItemGroup.field_78029_e);
      func_200879_a(Blocks.field_150319_E, ItemGroup.field_78029_e);
      func_200879_a(Blocks.field_150320_F, ItemGroup.field_78028_d);
      func_200879_a(Blocks.field_196553_aF, ItemGroup.field_78031_c);
      func_200879_a(Blocks.field_150349_c, ItemGroup.field_78031_c);
      func_200879_a(Blocks.field_196554_aH, ItemGroup.field_78031_c);
      func_200879_a(Blocks.field_196555_aI, ItemGroup.field_78031_c);
      func_200879_a(Blocks.field_203198_aQ, ItemGroup.field_78031_c);
      func_200879_a(Blocks.field_204913_jW, ItemGroup.field_78031_c);
      func_200879_a(Blocks.field_150331_J, ItemGroup.field_78028_d);
      func_200879_a(Blocks.field_196556_aL, ItemGroup.field_78030_b);
      func_200879_a(Blocks.field_196557_aM, ItemGroup.field_78030_b);
      func_200879_a(Blocks.field_196558_aN, ItemGroup.field_78030_b);
      func_200879_a(Blocks.field_196559_aO, ItemGroup.field_78030_b);
      func_200879_a(Blocks.field_196560_aP, ItemGroup.field_78030_b);
      func_200879_a(Blocks.field_196561_aQ, ItemGroup.field_78030_b);
      func_200879_a(Blocks.field_196562_aR, ItemGroup.field_78030_b);
      func_200879_a(Blocks.field_196563_aS, ItemGroup.field_78030_b);
      func_200879_a(Blocks.field_196564_aT, ItemGroup.field_78030_b);
      func_200879_a(Blocks.field_196565_aU, ItemGroup.field_78030_b);
      func_200879_a(Blocks.field_196566_aV, ItemGroup.field_78030_b);
      func_200879_a(Blocks.field_196567_aW, ItemGroup.field_78030_b);
      func_200879_a(Blocks.field_196568_aX, ItemGroup.field_78030_b);
      func_200879_a(Blocks.field_196569_aY, ItemGroup.field_78030_b);
      func_200879_a(Blocks.field_196570_aZ, ItemGroup.field_78030_b);
      func_200879_a(Blocks.field_196602_ba, ItemGroup.field_78030_b);
      func_200879_a(Blocks.field_196605_bc, ItemGroup.field_78031_c);
      func_200879_a(Blocks.field_196606_bd, ItemGroup.field_78031_c);
      func_200879_a(Blocks.field_196607_be, ItemGroup.field_78031_c);
      func_200879_a(Blocks.field_196609_bf, ItemGroup.field_78031_c);
      func_200879_a(Blocks.field_196610_bg, ItemGroup.field_78031_c);
      func_200879_a(Blocks.field_196612_bh, ItemGroup.field_78031_c);
      func_200879_a(Blocks.field_196613_bi, ItemGroup.field_78031_c);
      func_200879_a(Blocks.field_196614_bj, ItemGroup.field_78031_c);
      func_200879_a(Blocks.field_196615_bk, ItemGroup.field_78031_c);
      func_200879_a(Blocks.field_196616_bl, ItemGroup.field_78031_c);
      func_200879_a(Blocks.field_150338_P, ItemGroup.field_78031_c);
      func_200879_a(Blocks.field_150337_Q, ItemGroup.field_78031_c);
      func_200879_a(Blocks.field_150340_R, ItemGroup.field_78030_b);
      func_200879_a(Blocks.field_150339_S, ItemGroup.field_78030_b);
      func_200879_a(Blocks.field_196622_bq, ItemGroup.field_78030_b);
      func_200879_a(Blocks.field_196624_br, ItemGroup.field_78030_b);
      func_200879_a(Blocks.field_196627_bs, ItemGroup.field_78030_b);
      func_200879_a(Blocks.field_196630_bt, ItemGroup.field_78030_b);
      func_200879_a(Blocks.field_196632_bu, ItemGroup.field_78030_b);
      func_200879_a(Blocks.field_196635_bv, ItemGroup.field_78030_b);
      func_200879_a(Blocks.field_150333_U, ItemGroup.field_78030_b);
      func_200879_a(Blocks.field_196640_bx, ItemGroup.field_78030_b);
      func_200879_a(Blocks.field_196643_by, ItemGroup.field_78030_b);
      func_200879_a(Blocks.field_196646_bz, ItemGroup.field_78030_b);
      func_200879_a(Blocks.field_196571_bA, ItemGroup.field_78030_b);
      func_200879_a(Blocks.field_196573_bB, ItemGroup.field_78030_b);
      func_200879_a(Blocks.field_196575_bC, ItemGroup.field_78030_b);
      func_200879_a(Blocks.field_196576_bD, ItemGroup.field_78030_b);
      func_200879_a(Blocks.field_196578_bE, ItemGroup.field_78030_b);
      func_200879_a(Blocks.field_185771_cX, ItemGroup.field_78030_b);
      func_200879_a(Blocks.field_203200_bP, ItemGroup.field_78030_b);
      func_200879_a(Blocks.field_203201_bQ, ItemGroup.field_78030_b);
      func_200879_a(Blocks.field_203202_bR, ItemGroup.field_78030_b);
      func_200879_a(Blocks.field_196581_bI, ItemGroup.field_78030_b);
      func_200879_a(Blocks.field_196582_bJ, ItemGroup.field_78030_b);
      func_200879_a(Blocks.field_196580_bH, ItemGroup.field_78030_b);
      func_200879_a(Blocks.field_196579_bG, ItemGroup.field_78030_b);
      func_200879_a(Blocks.field_196584_bK, ItemGroup.field_78030_b);
      func_200879_a(Blocks.field_150335_W, ItemGroup.field_78028_d);
      func_200879_a(Blocks.field_150342_X, ItemGroup.field_78030_b);
      func_200879_a(Blocks.field_150341_Y, ItemGroup.field_78030_b);
      func_200879_a(Blocks.field_150343_Z, ItemGroup.field_78030_b);
      func_200126_a(new ItemWallOrFloor(Blocks.field_150478_aa, Blocks.field_196591_bQ, (new Item.Properties()).func_200916_a(ItemGroup.field_78031_c)));
      func_200879_a(Blocks.field_185764_cQ, ItemGroup.field_78031_c);
      func_200879_a(Blocks.field_185765_cR, ItemGroup.field_78031_c);
      func_200879_a(Blocks.field_185766_cS, ItemGroup.field_78031_c);
      func_200879_a(Blocks.field_185767_cT, ItemGroup.field_78030_b);
      func_200879_a(Blocks.field_185768_cU, ItemGroup.field_78030_b);
      func_200879_a(Blocks.field_185769_cV, ItemGroup.field_78030_b);
      func_179216_c(Blocks.field_150474_ac);
      func_200879_a(Blocks.field_150476_ad, ItemGroup.field_78030_b);
      func_200879_a(Blocks.field_150486_ae, ItemGroup.field_78031_c);
      func_200879_a(Blocks.field_150482_ag, ItemGroup.field_78030_b);
      func_200879_a(Blocks.field_150484_ah, ItemGroup.field_78030_b);
      func_200879_a(Blocks.field_150462_ai, ItemGroup.field_78031_c);
      func_200879_a(Blocks.field_150458_ak, ItemGroup.field_78031_c);
      func_200879_a(Blocks.field_150460_al, ItemGroup.field_78031_c);
      func_200879_a(Blocks.field_150468_ap, ItemGroup.field_78031_c);
      func_200879_a(Blocks.field_150448_aq, ItemGroup.field_78029_e);
      func_200879_a(Blocks.field_196659_cl, ItemGroup.field_78030_b);
      func_200879_a(Blocks.field_150442_at, ItemGroup.field_78028_d);
      func_200879_a(Blocks.field_150456_au, ItemGroup.field_78028_d);
      func_200879_a(Blocks.field_196663_cq, ItemGroup.field_78028_d);
      func_200879_a(Blocks.field_196665_cr, ItemGroup.field_78028_d);
      func_200879_a(Blocks.field_196667_cs, ItemGroup.field_78028_d);
      func_200879_a(Blocks.field_196669_ct, ItemGroup.field_78028_d);
      func_200879_a(Blocks.field_196671_cu, ItemGroup.field_78028_d);
      func_200879_a(Blocks.field_196673_cv, ItemGroup.field_78028_d);
      func_200879_a(Blocks.field_150450_ax, ItemGroup.field_78030_b);
      func_200126_a(new ItemWallOrFloor(Blocks.field_150429_aA, Blocks.field_196677_cy, (new Item.Properties()).func_200916_a(ItemGroup.field_78028_d)));
      func_200879_a(Blocks.field_150430_aB, ItemGroup.field_78028_d);
      func_200879_a(Blocks.field_150433_aE, ItemGroup.field_78031_c);
      func_200879_a(Blocks.field_150432_aD, ItemGroup.field_78030_b);
      func_200879_a(Blocks.field_196604_cC, ItemGroup.field_78030_b);
      func_200879_a(Blocks.field_150434_aF, ItemGroup.field_78031_c);
      func_200879_a(Blocks.field_150435_aG, ItemGroup.field_78030_b);
      func_200879_a(Blocks.field_150421_aI, ItemGroup.field_78031_c);
      func_200879_a(Blocks.field_180407_aO, ItemGroup.field_78031_c);
      func_200879_a(Blocks.field_180408_aP, ItemGroup.field_78031_c);
      func_200879_a(Blocks.field_180404_aQ, ItemGroup.field_78031_c);
      func_200879_a(Blocks.field_180403_aR, ItemGroup.field_78031_c);
      func_200879_a(Blocks.field_180405_aT, ItemGroup.field_78031_c);
      func_200879_a(Blocks.field_180406_aS, ItemGroup.field_78031_c);
      func_200879_a(Blocks.field_150423_aK, ItemGroup.field_78030_b);
      func_200879_a(Blocks.field_196625_cS, ItemGroup.field_78030_b);
      func_200879_a(Blocks.field_150424_aL, ItemGroup.field_78030_b);
      func_200879_a(Blocks.field_150425_aM, ItemGroup.field_78030_b);
      func_200879_a(Blocks.field_150426_aN, ItemGroup.field_78030_b);
      func_200879_a(Blocks.field_196628_cT, ItemGroup.field_78030_b);
      func_200879_a(Blocks.field_196636_cW, ItemGroup.field_78028_d);
      func_200879_a(Blocks.field_196638_cX, ItemGroup.field_78028_d);
      func_200879_a(Blocks.field_196641_cY, ItemGroup.field_78028_d);
      func_200879_a(Blocks.field_196644_cZ, ItemGroup.field_78028_d);
      func_200879_a(Blocks.field_196682_da, ItemGroup.field_78028_d);
      func_200879_a(Blocks.field_196684_db, ItemGroup.field_78028_d);
      func_200879_a(Blocks.field_196686_dc, ItemGroup.field_78031_c);
      func_200879_a(Blocks.field_196687_dd, ItemGroup.field_78031_c);
      func_200879_a(Blocks.field_196688_de, ItemGroup.field_78031_c);
      func_200879_a(Blocks.field_196690_df, ItemGroup.field_78031_c);
      func_200879_a(Blocks.field_196692_dg, ItemGroup.field_78031_c);
      func_200879_a(Blocks.field_196694_dh, ItemGroup.field_78031_c);
      func_200879_a(Blocks.field_196696_di, ItemGroup.field_78030_b);
      func_200879_a(Blocks.field_196698_dj, ItemGroup.field_78030_b);
      func_200879_a(Blocks.field_196700_dk, ItemGroup.field_78030_b);
      func_200879_a(Blocks.field_196702_dl, ItemGroup.field_78030_b);
      func_200879_a(Blocks.field_150420_aW, ItemGroup.field_78031_c);
      func_200879_a(Blocks.field_150419_aX, ItemGroup.field_78031_c);
      func_200879_a(Blocks.field_196706_do, ItemGroup.field_78031_c);
      func_200879_a(Blocks.field_150411_aY, ItemGroup.field_78031_c);
      func_200879_a(Blocks.field_150410_aZ, ItemGroup.field_78031_c);
      func_200879_a(Blocks.field_150440_ba, ItemGroup.field_78030_b);
      func_200879_a(Blocks.field_150395_bd, ItemGroup.field_78031_c);
      func_200879_a(Blocks.field_180390_bo, ItemGroup.field_78028_d);
      func_200879_a(Blocks.field_180391_bp, ItemGroup.field_78028_d);
      func_200879_a(Blocks.field_180392_bq, ItemGroup.field_78028_d);
      func_200879_a(Blocks.field_180386_br, ItemGroup.field_78028_d);
      func_200879_a(Blocks.field_180387_bt, ItemGroup.field_78028_d);
      func_200879_a(Blocks.field_180385_bs, ItemGroup.field_78028_d);
      func_200879_a(Blocks.field_150389_bf, ItemGroup.field_78030_b);
      func_200879_a(Blocks.field_150390_bg, ItemGroup.field_78030_b);
      func_200879_a(Blocks.field_150391_bh, ItemGroup.field_78030_b);
      func_200126_a(new ItemLilyPad(Blocks.field_196651_dG, (new Item.Properties()).func_200916_a(ItemGroup.field_78031_c)));
      func_200879_a(Blocks.field_196653_dH, ItemGroup.field_78030_b);
      func_200879_a(Blocks.field_150386_bk, ItemGroup.field_78031_c);
      func_200879_a(Blocks.field_150387_bl, ItemGroup.field_78030_b);
      func_200879_a(Blocks.field_150381_bn, ItemGroup.field_78031_c);
      func_200879_a(Blocks.field_150378_br, ItemGroup.field_78031_c);
      func_200879_a(Blocks.field_150377_bs, ItemGroup.field_78030_b);
      func_200879_a(Blocks.field_196806_hJ, ItemGroup.field_78030_b);
      func_200126_a(new ItemBlock(Blocks.field_150380_bt, (new Item.Properties()).func_208103_a(EnumRarity.EPIC)));
      func_200879_a(Blocks.field_150379_bu, ItemGroup.field_78028_d);
      func_200879_a(Blocks.field_150372_bz, ItemGroup.field_78030_b);
      func_200879_a(Blocks.field_150412_bA, ItemGroup.field_78030_b);
      func_200879_a(Blocks.field_150477_bB, ItemGroup.field_78031_c);
      func_200879_a(Blocks.field_150479_bC, ItemGroup.field_78028_d);
      func_200879_a(Blocks.field_150475_bE, ItemGroup.field_78030_b);
      func_200879_a(Blocks.field_150485_bF, ItemGroup.field_78030_b);
      func_200879_a(Blocks.field_150487_bG, ItemGroup.field_78030_b);
      func_200879_a(Blocks.field_150481_bH, ItemGroup.field_78030_b);
      func_200126_a(new ItemGMOnly(Blocks.field_150483_bI, (new Item.Properties()).func_208103_a(EnumRarity.EPIC)));
      func_200126_a(new ItemBlock(Blocks.field_150461_bJ, (new Item.Properties()).func_200916_a(ItemGroup.field_78026_f).func_208103_a(EnumRarity.RARE)));
      func_200879_a(Blocks.field_150463_bK, ItemGroup.field_78031_c);
      func_200879_a(Blocks.field_196723_eg, ItemGroup.field_78031_c);
      func_200879_a(Blocks.field_196689_eF, ItemGroup.field_78028_d);
      func_200879_a(Blocks.field_196691_eG, ItemGroup.field_78028_d);
      func_200879_a(Blocks.field_196693_eH, ItemGroup.field_78028_d);
      func_200879_a(Blocks.field_196695_eI, ItemGroup.field_78028_d);
      func_200879_a(Blocks.field_196697_eJ, ItemGroup.field_78028_d);
      func_200879_a(Blocks.field_196699_eK, ItemGroup.field_78028_d);
      func_200879_a(Blocks.field_150467_bQ, ItemGroup.field_78031_c);
      func_200879_a(Blocks.field_196717_eY, ItemGroup.field_78031_c);
      func_200879_a(Blocks.field_196718_eZ, ItemGroup.field_78031_c);
      func_200879_a(Blocks.field_150447_bR, ItemGroup.field_78028_d);
      func_200879_a(Blocks.field_150445_bS, ItemGroup.field_78028_d);
      func_200879_a(Blocks.field_150443_bT, ItemGroup.field_78028_d);
      func_200879_a(Blocks.field_150453_bW, ItemGroup.field_78028_d);
      func_200879_a(Blocks.field_150451_bX, ItemGroup.field_78028_d);
      func_200879_a(Blocks.field_196766_fg, ItemGroup.field_78030_b);
      func_200879_a(Blocks.field_150438_bZ, ItemGroup.field_78028_d);
      func_200879_a(Blocks.field_196772_fk, ItemGroup.field_78030_b);
      func_200879_a(Blocks.field_150371_ca, ItemGroup.field_78030_b);
      func_200879_a(Blocks.field_196770_fj, ItemGroup.field_78030_b);
      func_200879_a(Blocks.field_150370_cb, ItemGroup.field_78030_b);
      func_200879_a(Blocks.field_150408_cc, ItemGroup.field_78029_e);
      func_200879_a(Blocks.field_150409_cd, ItemGroup.field_78028_d);
      func_200879_a(Blocks.field_196777_fo, ItemGroup.field_78030_b);
      func_200879_a(Blocks.field_196778_fp, ItemGroup.field_78030_b);
      func_200879_a(Blocks.field_196780_fq, ItemGroup.field_78030_b);
      func_200879_a(Blocks.field_196782_fr, ItemGroup.field_78030_b);
      func_200879_a(Blocks.field_196783_fs, ItemGroup.field_78030_b);
      func_200879_a(Blocks.field_196785_ft, ItemGroup.field_78030_b);
      func_200879_a(Blocks.field_196787_fu, ItemGroup.field_78030_b);
      func_200879_a(Blocks.field_196789_fv, ItemGroup.field_78030_b);
      func_200879_a(Blocks.field_196791_fw, ItemGroup.field_78030_b);
      func_200879_a(Blocks.field_196793_fx, ItemGroup.field_78030_b);
      func_200879_a(Blocks.field_196795_fy, ItemGroup.field_78030_b);
      func_200879_a(Blocks.field_196797_fz, ItemGroup.field_78030_b);
      func_200879_a(Blocks.field_196719_fA, ItemGroup.field_78030_b);
      func_200879_a(Blocks.field_196720_fB, ItemGroup.field_78030_b);
      func_200879_a(Blocks.field_196721_fC, ItemGroup.field_78030_b);
      func_200879_a(Blocks.field_196722_fD, ItemGroup.field_78030_b);
      func_179216_c(Blocks.field_180401_cv);
      func_200879_a(Blocks.field_180400_cw, ItemGroup.field_78028_d);
      func_200879_a(Blocks.field_150407_cf, ItemGroup.field_78030_b);
      func_200879_a(Blocks.field_196724_fH, ItemGroup.field_78031_c);
      func_200879_a(Blocks.field_196725_fI, ItemGroup.field_78031_c);
      func_200879_a(Blocks.field_196727_fJ, ItemGroup.field_78031_c);
      func_200879_a(Blocks.field_196729_fK, ItemGroup.field_78031_c);
      func_200879_a(Blocks.field_196731_fL, ItemGroup.field_78031_c);
      func_200879_a(Blocks.field_196733_fM, ItemGroup.field_78031_c);
      func_200879_a(Blocks.field_196735_fN, ItemGroup.field_78031_c);
      func_200879_a(Blocks.field_196737_fO, ItemGroup.field_78031_c);
      func_200879_a(Blocks.field_196739_fP, ItemGroup.field_78031_c);
      func_200879_a(Blocks.field_196741_fQ, ItemGroup.field_78031_c);
      func_200879_a(Blocks.field_196743_fR, ItemGroup.field_78031_c);
      func_200879_a(Blocks.field_196745_fS, ItemGroup.field_78031_c);
      func_200879_a(Blocks.field_196747_fT, ItemGroup.field_78031_c);
      func_200879_a(Blocks.field_196749_fU, ItemGroup.field_78031_c);
      func_200879_a(Blocks.field_196751_fV, ItemGroup.field_78031_c);
      func_200879_a(Blocks.field_196753_fW, ItemGroup.field_78031_c);
      func_200879_a(Blocks.field_150405_ch, ItemGroup.field_78030_b);
      func_200879_a(Blocks.field_150402_ci, ItemGroup.field_78030_b);
      func_200879_a(Blocks.field_150403_cj, ItemGroup.field_78030_b);
      func_200879_a(Blocks.field_150400_ck, ItemGroup.field_78030_b);
      func_200879_a(Blocks.field_150401_cl, ItemGroup.field_78030_b);
      func_200879_a(Blocks.field_180399_cE, ItemGroup.field_78031_c);
      func_200879_a(Blocks.field_185774_da, ItemGroup.field_78031_c);
      func_200126_a(new ItemBlockTall(Blocks.field_196800_gd, (new Item.Properties()).func_200916_a(ItemGroup.field_78031_c)));
      func_200126_a(new ItemBlockTall(Blocks.field_196801_ge, (new Item.Properties()).func_200916_a(ItemGroup.field_78031_c)));
      func_200126_a(new ItemBlockTall(Blocks.field_196802_gf, (new Item.Properties()).func_200916_a(ItemGroup.field_78031_c)));
      func_200126_a(new ItemBlockTall(Blocks.field_196803_gg, (new Item.Properties()).func_200916_a(ItemGroup.field_78031_c)));
      func_200126_a(new ItemBlockTall(Blocks.field_196804_gh, (new Item.Properties()).func_200916_a(ItemGroup.field_78031_c)));
      func_200126_a(new ItemBlockTall(Blocks.field_196805_gi, (new Item.Properties()).func_200916_a(ItemGroup.field_78031_c)));
      func_200879_a(Blocks.field_196807_gj, ItemGroup.field_78030_b);
      func_200879_a(Blocks.field_196808_gk, ItemGroup.field_78030_b);
      func_200879_a(Blocks.field_196809_gl, ItemGroup.field_78030_b);
      func_200879_a(Blocks.field_196810_gm, ItemGroup.field_78030_b);
      func_200879_a(Blocks.field_196811_gn, ItemGroup.field_78030_b);
      func_200879_a(Blocks.field_196812_go, ItemGroup.field_78030_b);
      func_200879_a(Blocks.field_196813_gp, ItemGroup.field_78030_b);
      func_200879_a(Blocks.field_196815_gq, ItemGroup.field_78030_b);
      func_200879_a(Blocks.field_196816_gr, ItemGroup.field_78030_b);
      func_200879_a(Blocks.field_196818_gs, ItemGroup.field_78030_b);
      func_200879_a(Blocks.field_196819_gt, ItemGroup.field_78030_b);
      func_200879_a(Blocks.field_196820_gu, ItemGroup.field_78030_b);
      func_200879_a(Blocks.field_196821_gv, ItemGroup.field_78030_b);
      func_200879_a(Blocks.field_196822_gw, ItemGroup.field_78030_b);
      func_200879_a(Blocks.field_196823_gx, ItemGroup.field_78030_b);
      func_200879_a(Blocks.field_196824_gy, ItemGroup.field_78030_b);
      func_200879_a(Blocks.field_196825_gz, ItemGroup.field_78031_c);
      func_200879_a(Blocks.field_196758_gA, ItemGroup.field_78031_c);
      func_200879_a(Blocks.field_196759_gB, ItemGroup.field_78031_c);
      func_200879_a(Blocks.field_196760_gC, ItemGroup.field_78031_c);
      func_200879_a(Blocks.field_196761_gD, ItemGroup.field_78031_c);
      func_200879_a(Blocks.field_196763_gE, ItemGroup.field_78031_c);
      func_200879_a(Blocks.field_196764_gF, ItemGroup.field_78031_c);
      func_200879_a(Blocks.field_196765_gG, ItemGroup.field_78031_c);
      func_200879_a(Blocks.field_196767_gH, ItemGroup.field_78031_c);
      func_200879_a(Blocks.field_196768_gI, ItemGroup.field_78031_c);
      func_200879_a(Blocks.field_196769_gJ, ItemGroup.field_78031_c);
      func_200879_a(Blocks.field_196771_gK, ItemGroup.field_78031_c);
      func_200879_a(Blocks.field_196773_gL, ItemGroup.field_78031_c);
      func_200879_a(Blocks.field_196774_gM, ItemGroup.field_78031_c);
      func_200879_a(Blocks.field_196775_gN, ItemGroup.field_78031_c);
      func_200879_a(Blocks.field_196776_gO, ItemGroup.field_78031_c);
      func_200879_a(Blocks.field_180397_cI, ItemGroup.field_78030_b);
      func_200879_a(Blocks.field_196779_gQ, ItemGroup.field_78030_b);
      func_200879_a(Blocks.field_196781_gR, ItemGroup.field_78030_b);
      func_200879_a(Blocks.field_203210_he, ItemGroup.field_78030_b);
      func_200879_a(Blocks.field_203211_hf, ItemGroup.field_78030_b);
      func_200879_a(Blocks.field_203212_hg, ItemGroup.field_78030_b);
      func_200879_a(Blocks.field_180398_cJ, ItemGroup.field_78030_b);
      func_200879_a(Blocks.field_180395_cM, ItemGroup.field_78030_b);
      func_200879_a(Blocks.field_196798_hA, ItemGroup.field_78030_b);
      func_200879_a(Blocks.field_196799_hB, ItemGroup.field_78030_b);
      func_200879_a(Blocks.field_180396_cN, ItemGroup.field_78030_b);
      func_200126_a(new ItemGMOnly(Blocks.field_185776_dc, (new Item.Properties()).func_208103_a(EnumRarity.EPIC)));
      func_200126_a(new ItemGMOnly(Blocks.field_185777_dd, (new Item.Properties()).func_208103_a(EnumRarity.EPIC)));
      func_200879_a(Blocks.field_196814_hQ, ItemGroup.field_78030_b);
      func_200879_a(Blocks.field_189878_dg, ItemGroup.field_78030_b);
      func_200879_a(Blocks.field_196817_hS, ItemGroup.field_78030_b);
      func_200879_a(Blocks.field_189880_di, ItemGroup.field_78030_b);
      func_179216_c(Blocks.field_189881_dj);
      func_200879_a(Blocks.field_190976_dk, ItemGroup.field_78028_d);
      func_200126_a(new ItemBlock(Blocks.field_204409_il, (new Item.Properties()).func_200917_a(1).func_200916_a(ItemGroup.field_78031_c)));
      func_200126_a(new ItemBlock(Blocks.field_190977_dl, (new Item.Properties()).func_200917_a(1).func_200916_a(ItemGroup.field_78031_c)));
      func_200126_a(new ItemBlock(Blocks.field_190978_dm, (new Item.Properties()).func_200917_a(1).func_200916_a(ItemGroup.field_78031_c)));
      func_200126_a(new ItemBlock(Blocks.field_190979_dn, (new Item.Properties()).func_200917_a(1).func_200916_a(ItemGroup.field_78031_c)));
      func_200126_a(new ItemBlock(Blocks.field_190980_do, (new Item.Properties()).func_200917_a(1).func_200916_a(ItemGroup.field_78031_c)));
      func_200126_a(new ItemBlock(Blocks.field_190981_dp, (new Item.Properties()).func_200917_a(1).func_200916_a(ItemGroup.field_78031_c)));
      func_200126_a(new ItemBlock(Blocks.field_190982_dq, (new Item.Properties()).func_200917_a(1).func_200916_a(ItemGroup.field_78031_c)));
      func_200126_a(new ItemBlock(Blocks.field_190983_dr, (new Item.Properties()).func_200917_a(1).func_200916_a(ItemGroup.field_78031_c)));
      func_200126_a(new ItemBlock(Blocks.field_190984_ds, (new Item.Properties()).func_200917_a(1).func_200916_a(ItemGroup.field_78031_c)));
      func_200126_a(new ItemBlock(Blocks.field_196875_ie, (new Item.Properties()).func_200917_a(1).func_200916_a(ItemGroup.field_78031_c)));
      func_200126_a(new ItemBlock(Blocks.field_190986_du, (new Item.Properties()).func_200917_a(1).func_200916_a(ItemGroup.field_78031_c)));
      func_200126_a(new ItemBlock(Blocks.field_190987_dv, (new Item.Properties()).func_200917_a(1).func_200916_a(ItemGroup.field_78031_c)));
      func_200126_a(new ItemBlock(Blocks.field_190988_dw, (new Item.Properties()).func_200917_a(1).func_200916_a(ItemGroup.field_78031_c)));
      func_200126_a(new ItemBlock(Blocks.field_190989_dx, (new Item.Properties()).func_200917_a(1).func_200916_a(ItemGroup.field_78031_c)));
      func_200126_a(new ItemBlock(Blocks.field_190990_dy, (new Item.Properties()).func_200917_a(1).func_200916_a(ItemGroup.field_78031_c)));
      func_200126_a(new ItemBlock(Blocks.field_190991_dz, (new Item.Properties()).func_200917_a(1).func_200916_a(ItemGroup.field_78031_c)));
      func_200126_a(new ItemBlock(Blocks.field_190975_dA, (new Item.Properties()).func_200917_a(1).func_200916_a(ItemGroup.field_78031_c)));
      func_200879_a(Blocks.field_192427_dB, ItemGroup.field_78031_c);
      func_200879_a(Blocks.field_192428_dC, ItemGroup.field_78031_c);
      func_200879_a(Blocks.field_192429_dD, ItemGroup.field_78031_c);
      func_200879_a(Blocks.field_192430_dE, ItemGroup.field_78031_c);
      func_200879_a(Blocks.field_192431_dF, ItemGroup.field_78031_c);
      func_200879_a(Blocks.field_192432_dG, ItemGroup.field_78031_c);
      func_200879_a(Blocks.field_192433_dH, ItemGroup.field_78031_c);
      func_200879_a(Blocks.field_192434_dI, ItemGroup.field_78031_c);
      func_200879_a(Blocks.field_196876_iu, ItemGroup.field_78031_c);
      func_200879_a(Blocks.field_192436_dK, ItemGroup.field_78031_c);
      func_200879_a(Blocks.field_192437_dL, ItemGroup.field_78031_c);
      func_200879_a(Blocks.field_192438_dM, ItemGroup.field_78031_c);
      func_200879_a(Blocks.field_192439_dN, ItemGroup.field_78031_c);
      func_200879_a(Blocks.field_192440_dO, ItemGroup.field_78031_c);
      func_200879_a(Blocks.field_192441_dP, ItemGroup.field_78031_c);
      func_200879_a(Blocks.field_192442_dQ, ItemGroup.field_78031_c);
      func_200879_a(Blocks.field_196828_iC, ItemGroup.field_78030_b);
      func_200879_a(Blocks.field_196830_iD, ItemGroup.field_78030_b);
      func_200879_a(Blocks.field_196832_iE, ItemGroup.field_78030_b);
      func_200879_a(Blocks.field_196834_iF, ItemGroup.field_78030_b);
      func_200879_a(Blocks.field_196836_iG, ItemGroup.field_78030_b);
      func_200879_a(Blocks.field_196838_iH, ItemGroup.field_78030_b);
      func_200879_a(Blocks.field_196840_iI, ItemGroup.field_78030_b);
      func_200879_a(Blocks.field_196842_iJ, ItemGroup.field_78030_b);
      func_200879_a(Blocks.field_196844_iK, ItemGroup.field_78030_b);
      func_200879_a(Blocks.field_196846_iL, ItemGroup.field_78030_b);
      func_200879_a(Blocks.field_196848_iM, ItemGroup.field_78030_b);
      func_200879_a(Blocks.field_196850_iN, ItemGroup.field_78030_b);
      func_200879_a(Blocks.field_196852_iO, ItemGroup.field_78030_b);
      func_200879_a(Blocks.field_196854_iP, ItemGroup.field_78030_b);
      func_200879_a(Blocks.field_196856_iQ, ItemGroup.field_78030_b);
      func_200879_a(Blocks.field_196858_iR, ItemGroup.field_78030_b);
      func_200879_a(Blocks.field_196860_iS, ItemGroup.field_78030_b);
      func_200879_a(Blocks.field_196862_iT, ItemGroup.field_78030_b);
      func_200879_a(Blocks.field_196864_iU, ItemGroup.field_78030_b);
      func_200879_a(Blocks.field_196866_iV, ItemGroup.field_78030_b);
      func_200879_a(Blocks.field_196868_iW, ItemGroup.field_78030_b);
      func_200879_a(Blocks.field_196870_iX, ItemGroup.field_78030_b);
      func_200879_a(Blocks.field_196872_iY, ItemGroup.field_78030_b);
      func_200879_a(Blocks.field_196874_iZ, ItemGroup.field_78030_b);
      func_200879_a(Blocks.field_196877_ja, ItemGroup.field_78030_b);
      func_200879_a(Blocks.field_196878_jb, ItemGroup.field_78030_b);
      func_200879_a(Blocks.field_196879_jc, ItemGroup.field_78030_b);
      func_200879_a(Blocks.field_196880_jd, ItemGroup.field_78030_b);
      func_200879_a(Blocks.field_196881_je, ItemGroup.field_78030_b);
      func_200879_a(Blocks.field_196882_jf, ItemGroup.field_78030_b);
      func_200879_a(Blocks.field_196883_jg, ItemGroup.field_78030_b);
      func_200879_a(Blocks.field_196884_jh, ItemGroup.field_78030_b);
      func_200879_a(Blocks.field_203213_jA, ItemGroup.field_78026_f);
      func_200879_a(Blocks.field_204404_jE, ItemGroup.field_78030_b);
      func_200879_a(Blocks.field_204405_jF, ItemGroup.field_78030_b);
      func_200879_a(Blocks.field_204406_jG, ItemGroup.field_78030_b);
      func_200879_a(Blocks.field_204407_jH, ItemGroup.field_78030_b);
      func_200879_a(Blocks.field_204408_jI, ItemGroup.field_78030_b);
      func_200879_a(Blocks.field_203963_jE, ItemGroup.field_78030_b);
      func_200879_a(Blocks.field_203964_jF, ItemGroup.field_78030_b);
      func_200879_a(Blocks.field_203965_jG, ItemGroup.field_78030_b);
      func_200879_a(Blocks.field_203966_jH, ItemGroup.field_78030_b);
      func_200879_a(Blocks.field_203967_jI, ItemGroup.field_78030_b);
      func_200879_a(Blocks.field_204278_jJ, ItemGroup.field_78031_c);
      func_200879_a(Blocks.field_204279_jK, ItemGroup.field_78031_c);
      func_200879_a(Blocks.field_204280_jL, ItemGroup.field_78031_c);
      func_200879_a(Blocks.field_204281_jM, ItemGroup.field_78031_c);
      func_200879_a(Blocks.field_204282_jN, ItemGroup.field_78031_c);
      func_200879_a(Blocks.field_212586_jZ, ItemGroup.field_78031_c);
      func_200879_a(Blocks.field_212587_ka, ItemGroup.field_78031_c);
      func_200879_a(Blocks.field_212588_kb, ItemGroup.field_78031_c);
      func_200879_a(Blocks.field_212589_kc, ItemGroup.field_78031_c);
      func_200879_a(Blocks.field_212585_jY, ItemGroup.field_78031_c);
      func_200126_a(new ItemWallOrFloor(Blocks.field_204743_jR, Blocks.field_211891_jY, (new Item.Properties()).func_200916_a(ItemGroup.field_78031_c)));
      func_200126_a(new ItemWallOrFloor(Blocks.field_204744_jS, Blocks.field_211892_jZ, (new Item.Properties()).func_200916_a(ItemGroup.field_78031_c)));
      func_200126_a(new ItemWallOrFloor(Blocks.field_204745_jT, Blocks.field_211893_ka, (new Item.Properties()).func_200916_a(ItemGroup.field_78031_c)));
      func_200126_a(new ItemWallOrFloor(Blocks.field_204746_jU, Blocks.field_211894_kb, (new Item.Properties()).func_200916_a(ItemGroup.field_78031_c)));
      func_200126_a(new ItemWallOrFloor(Blocks.field_204747_jV, Blocks.field_211895_kc, (new Item.Properties()).func_200916_a(ItemGroup.field_78031_c)));
      func_200126_a(new ItemWallOrFloor(Blocks.field_211901_kp, Blocks.field_211896_kk, (new Item.Properties()).func_200916_a(ItemGroup.field_78031_c)));
      func_200126_a(new ItemWallOrFloor(Blocks.field_211902_kq, Blocks.field_211897_kl, (new Item.Properties()).func_200916_a(ItemGroup.field_78031_c)));
      func_200126_a(new ItemWallOrFloor(Blocks.field_211903_kr, Blocks.field_211898_km, (new Item.Properties()).func_200916_a(ItemGroup.field_78031_c)));
      func_200126_a(new ItemWallOrFloor(Blocks.field_211904_ks, Blocks.field_211899_kn, (new Item.Properties()).func_200916_a(ItemGroup.field_78031_c)));
      func_200126_a(new ItemWallOrFloor(Blocks.field_211905_kt, Blocks.field_211900_ko, (new Item.Properties()).func_200916_a(ItemGroup.field_78031_c)));
      func_200879_a(Blocks.field_205164_gk, ItemGroup.field_78030_b);
      func_200126_a(new ItemBlock(Blocks.field_205165_jY, (new Item.Properties()).func_200916_a(ItemGroup.field_78026_f).func_208103_a(EnumRarity.RARE)));
      func_200126_a(new ItemBlockTall(Blocks.field_150454_av, (new Item.Properties()).func_200916_a(ItemGroup.field_78028_d)));
      func_200126_a(new ItemBlockTall(Blocks.field_180413_ao, (new Item.Properties()).func_200916_a(ItemGroup.field_78028_d)));
      func_200126_a(new ItemBlockTall(Blocks.field_180414_ap, (new Item.Properties()).func_200916_a(ItemGroup.field_78028_d)));
      func_200126_a(new ItemBlockTall(Blocks.field_180412_aq, (new Item.Properties()).func_200916_a(ItemGroup.field_78028_d)));
      func_200126_a(new ItemBlockTall(Blocks.field_180411_ar, (new Item.Properties()).func_200916_a(ItemGroup.field_78028_d)));
      func_200126_a(new ItemBlockTall(Blocks.field_180410_as, (new Item.Properties()).func_200916_a(ItemGroup.field_78028_d)));
      func_200126_a(new ItemBlockTall(Blocks.field_180409_at, (new Item.Properties()).func_200916_a(ItemGroup.field_78028_d)));
      func_200879_a(Blocks.field_196633_cV, ItemGroup.field_78028_d);
      func_200879_a(Blocks.field_196762_fd, ItemGroup.field_78028_d);
      func_200126_a(new ItemGMOnly(Blocks.field_185779_df, (new Item.Properties()).func_208103_a(EnumRarity.EPIC)));
      func_195936_a("turtle_helmet", new ItemArmor(ArmorMaterial.TURTLE, EntityEquipmentSlot.HEAD, (new Item.Properties()).func_200916_a(ItemGroup.field_78037_j)));
      func_195936_a("scute", new Item((new Item.Properties()).func_200916_a(ItemGroup.field_78035_l)));
      func_195936_a("iron_shovel", new ItemSpade(ItemTier.IRON, 1.5F, -3.0F, (new Item.Properties()).func_200916_a(ItemGroup.field_78040_i)));
      func_195936_a("iron_pickaxe", new ItemPickaxe(ItemTier.IRON, 1, -2.8F, (new Item.Properties()).func_200916_a(ItemGroup.field_78040_i)));
      func_195936_a("iron_axe", new ItemAxe(ItemTier.IRON, 6.0F, -3.1F, (new Item.Properties()).func_200916_a(ItemGroup.field_78040_i)));
      func_195936_a("flint_and_steel", new ItemFlintAndSteel((new Item.Properties()).func_200918_c(64).func_200916_a(ItemGroup.field_78040_i)));
      func_195936_a("apple", new ItemFood(4, 0.3F, false, (new Item.Properties()).func_200916_a(ItemGroup.field_78039_h)));
      func_195936_a("bow", new ItemBow((new Item.Properties()).func_200918_c(384).func_200916_a(ItemGroup.field_78037_j)));
      func_195936_a("arrow", new ItemArrow((new Item.Properties()).func_200916_a(ItemGroup.field_78037_j)));
      func_195936_a("coal", new Item((new Item.Properties()).func_200916_a(ItemGroup.field_78035_l)));
      func_195936_a("charcoal", new Item((new Item.Properties()).func_200916_a(ItemGroup.field_78035_l)));
      func_195936_a("diamond", new Item((new Item.Properties()).func_200916_a(ItemGroup.field_78035_l)));
      func_195936_a("iron_ingot", new Item((new Item.Properties()).func_200916_a(ItemGroup.field_78035_l)));
      func_195936_a("gold_ingot", new Item((new Item.Properties()).func_200916_a(ItemGroup.field_78035_l)));
      func_195936_a("iron_sword", new ItemSword(ItemTier.IRON, 3, -2.4F, (new Item.Properties()).func_200916_a(ItemGroup.field_78037_j)));
      func_195936_a("wooden_sword", new ItemSword(ItemTier.WOOD, 3, -2.4F, (new Item.Properties()).func_200916_a(ItemGroup.field_78037_j)));
      func_195936_a("wooden_shovel", new ItemSpade(ItemTier.WOOD, 1.5F, -3.0F, (new Item.Properties()).func_200916_a(ItemGroup.field_78040_i)));
      func_195936_a("wooden_pickaxe", new ItemPickaxe(ItemTier.WOOD, 1, -2.8F, (new Item.Properties()).func_200916_a(ItemGroup.field_78040_i)));
      func_195936_a("wooden_axe", new ItemAxe(ItemTier.WOOD, 6.0F, -3.2F, (new Item.Properties()).func_200916_a(ItemGroup.field_78040_i)));
      func_195936_a("stone_sword", new ItemSword(ItemTier.STONE, 3, -2.4F, (new Item.Properties()).func_200916_a(ItemGroup.field_78037_j)));
      func_195936_a("stone_shovel", new ItemSpade(ItemTier.STONE, 1.5F, -3.0F, (new Item.Properties()).func_200916_a(ItemGroup.field_78040_i)));
      func_195936_a("stone_pickaxe", new ItemPickaxe(ItemTier.STONE, 1, -2.8F, (new Item.Properties()).func_200916_a(ItemGroup.field_78040_i)));
      func_195936_a("stone_axe", new ItemAxe(ItemTier.STONE, 7.0F, -3.2F, (new Item.Properties()).func_200916_a(ItemGroup.field_78040_i)));
      func_195936_a("diamond_sword", new ItemSword(ItemTier.DIAMOND, 3, -2.4F, (new Item.Properties()).func_200916_a(ItemGroup.field_78037_j)));
      func_195936_a("diamond_shovel", new ItemSpade(ItemTier.DIAMOND, 1.5F, -3.0F, (new Item.Properties()).func_200916_a(ItemGroup.field_78040_i)));
      func_195936_a("diamond_pickaxe", new ItemPickaxe(ItemTier.DIAMOND, 1, -2.8F, (new Item.Properties()).func_200916_a(ItemGroup.field_78040_i)));
      func_195936_a("diamond_axe", new ItemAxe(ItemTier.DIAMOND, 5.0F, -3.0F, (new Item.Properties()).func_200916_a(ItemGroup.field_78040_i)));
      func_195936_a("stick", new Item((new Item.Properties()).func_200916_a(ItemGroup.field_78035_l)));
      func_195936_a("bowl", new Item((new Item.Properties()).func_200916_a(ItemGroup.field_78035_l)));
      func_195936_a("mushroom_stew", new ItemSoup(6, (new Item.Properties()).func_200917_a(1).func_200916_a(ItemGroup.field_78039_h)));
      func_195936_a("golden_sword", new ItemSword(ItemTier.GOLD, 3, -2.4F, (new Item.Properties()).func_200916_a(ItemGroup.field_78037_j)));
      func_195936_a("golden_shovel", new ItemSpade(ItemTier.GOLD, 1.5F, -3.0F, (new Item.Properties()).func_200916_a(ItemGroup.field_78040_i)));
      func_195936_a("golden_pickaxe", new ItemPickaxe(ItemTier.GOLD, 1, -2.8F, (new Item.Properties()).func_200916_a(ItemGroup.field_78040_i)));
      func_195936_a("golden_axe", new ItemAxe(ItemTier.GOLD, 6.0F, -3.0F, (new Item.Properties()).func_200916_a(ItemGroup.field_78040_i)));
      func_195936_a("string", new ItemString((new Item.Properties()).func_200916_a(ItemGroup.field_78026_f)));
      func_195936_a("feather", new Item((new Item.Properties()).func_200916_a(ItemGroup.field_78035_l)));
      func_195936_a("gunpowder", new Item((new Item.Properties()).func_200916_a(ItemGroup.field_78035_l)));
      func_195936_a("wooden_hoe", new ItemHoe(ItemTier.WOOD, -3.0F, (new Item.Properties()).func_200916_a(ItemGroup.field_78040_i)));
      func_195936_a("stone_hoe", new ItemHoe(ItemTier.STONE, -2.0F, (new Item.Properties()).func_200916_a(ItemGroup.field_78040_i)));
      func_195936_a("iron_hoe", new ItemHoe(ItemTier.IRON, -1.0F, (new Item.Properties()).func_200916_a(ItemGroup.field_78040_i)));
      func_195936_a("diamond_hoe", new ItemHoe(ItemTier.DIAMOND, 0.0F, (new Item.Properties()).func_200916_a(ItemGroup.field_78040_i)));
      func_195936_a("golden_hoe", new ItemHoe(ItemTier.GOLD, -3.0F, (new Item.Properties()).func_200916_a(ItemGroup.field_78040_i)));
      func_195936_a("wheat_seeds", new ItemSeeds(Blocks.field_150464_aj, (new Item.Properties()).func_200916_a(ItemGroup.field_78035_l)));
      func_195936_a("wheat", new Item((new Item.Properties()).func_200916_a(ItemGroup.field_78035_l)));
      func_195936_a("bread", new ItemFood(5, 0.6F, false, (new Item.Properties()).func_200916_a(ItemGroup.field_78039_h)));
      func_195936_a("leather_helmet", new ItemArmorDyeable(ArmorMaterial.LEATHER, EntityEquipmentSlot.HEAD, (new Item.Properties()).func_200916_a(ItemGroup.field_78037_j)));
      func_195936_a("leather_chestplate", new ItemArmorDyeable(ArmorMaterial.LEATHER, EntityEquipmentSlot.CHEST, (new Item.Properties()).func_200916_a(ItemGroup.field_78037_j)));
      func_195936_a("leather_leggings", new ItemArmorDyeable(ArmorMaterial.LEATHER, EntityEquipmentSlot.LEGS, (new Item.Properties()).func_200916_a(ItemGroup.field_78037_j)));
      func_195936_a("leather_boots", new ItemArmorDyeable(ArmorMaterial.LEATHER, EntityEquipmentSlot.FEET, (new Item.Properties()).func_200916_a(ItemGroup.field_78037_j)));
      func_195936_a("chainmail_helmet", new ItemArmor(ArmorMaterial.CHAIN, EntityEquipmentSlot.HEAD, (new Item.Properties()).func_200916_a(ItemGroup.field_78037_j)));
      func_195936_a("chainmail_chestplate", new ItemArmor(ArmorMaterial.CHAIN, EntityEquipmentSlot.CHEST, (new Item.Properties()).func_200916_a(ItemGroup.field_78037_j)));
      func_195936_a("chainmail_leggings", new ItemArmor(ArmorMaterial.CHAIN, EntityEquipmentSlot.LEGS, (new Item.Properties()).func_200916_a(ItemGroup.field_78037_j)));
      func_195936_a("chainmail_boots", new ItemArmor(ArmorMaterial.CHAIN, EntityEquipmentSlot.FEET, (new Item.Properties()).func_200916_a(ItemGroup.field_78037_j)));
      func_195936_a("iron_helmet", new ItemArmor(ArmorMaterial.IRON, EntityEquipmentSlot.HEAD, (new Item.Properties()).func_200916_a(ItemGroup.field_78037_j)));
      func_195936_a("iron_chestplate", new ItemArmor(ArmorMaterial.IRON, EntityEquipmentSlot.CHEST, (new Item.Properties()).func_200916_a(ItemGroup.field_78037_j)));
      func_195936_a("iron_leggings", new ItemArmor(ArmorMaterial.IRON, EntityEquipmentSlot.LEGS, (new Item.Properties()).func_200916_a(ItemGroup.field_78037_j)));
      func_195936_a("iron_boots", new ItemArmor(ArmorMaterial.IRON, EntityEquipmentSlot.FEET, (new Item.Properties()).func_200916_a(ItemGroup.field_78037_j)));
      func_195936_a("diamond_helmet", new ItemArmor(ArmorMaterial.DIAMOND, EntityEquipmentSlot.HEAD, (new Item.Properties()).func_200916_a(ItemGroup.field_78037_j)));
      func_195936_a("diamond_chestplate", new ItemArmor(ArmorMaterial.DIAMOND, EntityEquipmentSlot.CHEST, (new Item.Properties()).func_200916_a(ItemGroup.field_78037_j)));
      func_195936_a("diamond_leggings", new ItemArmor(ArmorMaterial.DIAMOND, EntityEquipmentSlot.LEGS, (new Item.Properties()).func_200916_a(ItemGroup.field_78037_j)));
      func_195936_a("diamond_boots", new ItemArmor(ArmorMaterial.DIAMOND, EntityEquipmentSlot.FEET, (new Item.Properties()).func_200916_a(ItemGroup.field_78037_j)));
      func_195936_a("golden_helmet", new ItemArmor(ArmorMaterial.GOLD, EntityEquipmentSlot.HEAD, (new Item.Properties()).func_200916_a(ItemGroup.field_78037_j)));
      func_195936_a("golden_chestplate", new ItemArmor(ArmorMaterial.GOLD, EntityEquipmentSlot.CHEST, (new Item.Properties()).func_200916_a(ItemGroup.field_78037_j)));
      func_195936_a("golden_leggings", new ItemArmor(ArmorMaterial.GOLD, EntityEquipmentSlot.LEGS, (new Item.Properties()).func_200916_a(ItemGroup.field_78037_j)));
      func_195936_a("golden_boots", new ItemArmor(ArmorMaterial.GOLD, EntityEquipmentSlot.FEET, (new Item.Properties()).func_200916_a(ItemGroup.field_78037_j)));
      func_195936_a("flint", new Item((new Item.Properties()).func_200916_a(ItemGroup.field_78035_l)));
      func_195936_a("porkchop", new ItemFood(3, 0.3F, true, (new Item.Properties()).func_200916_a(ItemGroup.field_78039_h)));
      func_195936_a("cooked_porkchop", new ItemFood(8, 0.8F, true, (new Item.Properties()).func_200916_a(ItemGroup.field_78039_h)));
      func_195936_a("painting", new ItemHangingEntity(EntityPainting.class, (new Item.Properties()).func_200916_a(ItemGroup.field_78031_c)));
      func_195936_a("golden_apple", (new ItemAppleGold(4, 1.2F, false, (new Item.Properties()).func_200916_a(ItemGroup.field_78039_h).func_208103_a(EnumRarity.RARE))).func_77848_i());
      func_195936_a("enchanted_golden_apple", (new ItemAppleGoldEnchanted(4, 1.2F, false, (new Item.Properties()).func_200916_a(ItemGroup.field_78039_h).func_208103_a(EnumRarity.EPIC))).func_77848_i());
      func_195936_a("sign", new ItemSign((new Item.Properties()).func_200917_a(16).func_200916_a(ItemGroup.field_78031_c)));
      ItemBucket var0 = new ItemBucket(Fluids.field_204541_a, (new Item.Properties()).func_200917_a(16).func_200916_a(ItemGroup.field_78026_f));
      func_195936_a("bucket", var0);
      func_195936_a("water_bucket", new ItemBucket(Fluids.field_204546_a, (new Item.Properties()).func_200919_a(var0).func_200917_a(1).func_200916_a(ItemGroup.field_78026_f)));
      func_195936_a("lava_bucket", new ItemBucket(Fluids.field_204547_b, (new Item.Properties()).func_200919_a(var0).func_200917_a(1).func_200916_a(ItemGroup.field_78026_f)));
      func_195936_a("minecart", new ItemMinecart(EntityMinecart.Type.RIDEABLE, (new Item.Properties()).func_200917_a(1).func_200916_a(ItemGroup.field_78029_e)));
      func_195936_a("saddle", new ItemSaddle((new Item.Properties()).func_200917_a(1).func_200916_a(ItemGroup.field_78029_e)));
      func_195936_a("redstone", new ItemBlock(Blocks.field_150488_af, (new Item.Properties()).func_200916_a(ItemGroup.field_78028_d)));
      func_195936_a("snowball", new ItemSnowball((new Item.Properties()).func_200917_a(16).func_200916_a(ItemGroup.field_78026_f)));
      func_195936_a("oak_boat", new ItemBoat(EntityBoat.Type.OAK, (new Item.Properties()).func_200917_a(1).func_200916_a(ItemGroup.field_78029_e)));
      func_195936_a("leather", new Item((new Item.Properties()).func_200916_a(ItemGroup.field_78035_l)));
      func_195936_a("milk_bucket", new ItemBucketMilk((new Item.Properties()).func_200919_a(var0).func_200917_a(1).func_200916_a(ItemGroup.field_78026_f)));
      func_195936_a("pufferfish_bucket", new ItemBucketFish(EntityType.field_203779_Z, Fluids.field_204546_a, (new Item.Properties()).func_200917_a(1).func_200916_a(ItemGroup.field_78026_f)));
      func_195936_a("salmon_bucket", new ItemBucketFish(EntityType.field_203778_ae, Fluids.field_204546_a, (new Item.Properties()).func_200917_a(1).func_200916_a(ItemGroup.field_78026_f)));
      func_195936_a("cod_bucket", new ItemBucketFish(EntityType.field_203780_j, Fluids.field_204546_a, (new Item.Properties()).func_200917_a(1).func_200916_a(ItemGroup.field_78026_f)));
      func_195936_a("tropical_fish_bucket", new ItemBucketFish(EntityType.field_204262_at, Fluids.field_204546_a, (new Item.Properties()).func_200917_a(1).func_200916_a(ItemGroup.field_78026_f)));
      func_195936_a("brick", new Item((new Item.Properties()).func_200916_a(ItemGroup.field_78035_l)));
      func_195936_a("clay_ball", new Item((new Item.Properties()).func_200916_a(ItemGroup.field_78035_l)));
      func_200879_a(Blocks.field_196608_cF, ItemGroup.field_78026_f);
      func_200879_a(Blocks.field_203214_jx, ItemGroup.field_78026_f);
      func_200879_a(Blocks.field_203216_jz, ItemGroup.field_78030_b);
      func_195936_a("paper", new Item((new Item.Properties()).func_200916_a(ItemGroup.field_78026_f)));
      func_195936_a("book", new ItemBook((new Item.Properties()).func_200916_a(ItemGroup.field_78026_f)));
      func_195936_a("slime_ball", new Item((new Item.Properties()).func_200916_a(ItemGroup.field_78026_f)));
      func_195936_a("chest_minecart", new ItemMinecart(EntityMinecart.Type.CHEST, (new Item.Properties()).func_200917_a(1).func_200916_a(ItemGroup.field_78029_e)));
      func_195936_a("furnace_minecart", new ItemMinecart(EntityMinecart.Type.FURNACE, (new Item.Properties()).func_200917_a(1).func_200916_a(ItemGroup.field_78029_e)));
      func_195936_a("egg", new ItemEgg((new Item.Properties()).func_200917_a(16).func_200916_a(ItemGroup.field_78035_l)));
      func_195936_a("compass", new ItemCompass((new Item.Properties()).func_200916_a(ItemGroup.field_78040_i)));
      func_195936_a("fishing_rod", new ItemFishingRod((new Item.Properties()).func_200918_c(64).func_200916_a(ItemGroup.field_78040_i)));
      func_195936_a("clock", new ItemClock((new Item.Properties()).func_200916_a(ItemGroup.field_78040_i)));
      func_195936_a("glowstone_dust", new Item((new Item.Properties()).func_200916_a(ItemGroup.field_78035_l)));
      func_195936_a("cod", new ItemFishFood(ItemFishFood.FishType.COD, false, (new Item.Properties()).func_200916_a(ItemGroup.field_78039_h)));
      func_195936_a("salmon", new ItemFishFood(ItemFishFood.FishType.SALMON, false, (new Item.Properties()).func_200916_a(ItemGroup.field_78039_h)));
      func_195936_a("tropical_fish", new ItemFishFood(ItemFishFood.FishType.TROPICAL_FISH, false, (new Item.Properties()).func_200916_a(ItemGroup.field_78039_h)));
      func_195936_a("pufferfish", new ItemFishFood(ItemFishFood.FishType.PUFFERFISH, false, (new Item.Properties()).func_200916_a(ItemGroup.field_78039_h)));
      func_195936_a("cooked_cod", new ItemFishFood(ItemFishFood.FishType.COD, true, (new Item.Properties()).func_200916_a(ItemGroup.field_78039_h)));
      func_195936_a("cooked_salmon", new ItemFishFood(ItemFishFood.FishType.SALMON, true, (new Item.Properties()).func_200916_a(ItemGroup.field_78039_h)));
      func_195936_a("ink_sac", new ItemDye(EnumDyeColor.BLACK, (new Item.Properties()).func_200916_a(ItemGroup.field_78035_l)));
      func_195936_a("rose_red", new ItemDye(EnumDyeColor.RED, (new Item.Properties()).func_200916_a(ItemGroup.field_78035_l)));
      func_195936_a("cactus_green", new ItemDye(EnumDyeColor.GREEN, (new Item.Properties()).func_200916_a(ItemGroup.field_78035_l)));
      func_195936_a("cocoa_beans", new ItemCocoa(EnumDyeColor.BROWN, (new Item.Properties()).func_200916_a(ItemGroup.field_78035_l)));
      func_195936_a("lapis_lazuli", new ItemDye(EnumDyeColor.BLUE, (new Item.Properties()).func_200916_a(ItemGroup.field_78035_l)));
      func_195936_a("purple_dye", new ItemDye(EnumDyeColor.PURPLE, (new Item.Properties()).func_200916_a(ItemGroup.field_78035_l)));
      func_195936_a("cyan_dye", new ItemDye(EnumDyeColor.CYAN, (new Item.Properties()).func_200916_a(ItemGroup.field_78035_l)));
      func_195936_a("light_gray_dye", new ItemDye(EnumDyeColor.LIGHT_GRAY, (new Item.Properties()).func_200916_a(ItemGroup.field_78035_l)));
      func_195936_a("gray_dye", new ItemDye(EnumDyeColor.GRAY, (new Item.Properties()).func_200916_a(ItemGroup.field_78035_l)));
      func_195936_a("pink_dye", new ItemDye(EnumDyeColor.PINK, (new Item.Properties()).func_200916_a(ItemGroup.field_78035_l)));
      func_195936_a("lime_dye", new ItemDye(EnumDyeColor.LIME, (new Item.Properties()).func_200916_a(ItemGroup.field_78035_l)));
      func_195936_a("dandelion_yellow", new ItemDye(EnumDyeColor.YELLOW, (new Item.Properties()).func_200916_a(ItemGroup.field_78035_l)));
      func_195936_a("light_blue_dye", new ItemDye(EnumDyeColor.LIGHT_BLUE, (new Item.Properties()).func_200916_a(ItemGroup.field_78035_l)));
      func_195936_a("magenta_dye", new ItemDye(EnumDyeColor.MAGENTA, (new Item.Properties()).func_200916_a(ItemGroup.field_78035_l)));
      func_195936_a("orange_dye", new ItemDye(EnumDyeColor.ORANGE, (new Item.Properties()).func_200916_a(ItemGroup.field_78035_l)));
      func_195936_a("bone_meal", new ItemBoneMeal(EnumDyeColor.WHITE, (new Item.Properties()).func_200916_a(ItemGroup.field_78035_l)));
      func_195936_a("bone", new Item((new Item.Properties()).func_200916_a(ItemGroup.field_78026_f)));
      func_195936_a("sugar", new Item((new Item.Properties()).func_200916_a(ItemGroup.field_78035_l)));
      func_200126_a(new ItemBlock(Blocks.field_150414_aQ, (new Item.Properties()).func_200917_a(1).func_200916_a(ItemGroup.field_78039_h)));
      func_200126_a(new ItemBed(Blocks.field_196587_am, (new Item.Properties()).func_200917_a(1).func_200916_a(ItemGroup.field_78031_c)));
      func_200126_a(new ItemBed(Blocks.field_196588_an, (new Item.Properties()).func_200917_a(1).func_200916_a(ItemGroup.field_78031_c)));
      func_200126_a(new ItemBed(Blocks.field_196589_ao, (new Item.Properties()).func_200917_a(1).func_200916_a(ItemGroup.field_78031_c)));
      func_200126_a(new ItemBed(Blocks.field_196590_ap, (new Item.Properties()).func_200917_a(1).func_200916_a(ItemGroup.field_78031_c)));
      func_200126_a(new ItemBed(Blocks.field_196592_aq, (new Item.Properties()).func_200917_a(1).func_200916_a(ItemGroup.field_78031_c)));
      func_200126_a(new ItemBed(Blocks.field_196593_ar, (new Item.Properties()).func_200917_a(1).func_200916_a(ItemGroup.field_78031_c)));
      func_200126_a(new ItemBed(Blocks.field_196594_as, (new Item.Properties()).func_200917_a(1).func_200916_a(ItemGroup.field_78031_c)));
      func_200126_a(new ItemBed(Blocks.field_196595_at, (new Item.Properties()).func_200917_a(1).func_200916_a(ItemGroup.field_78031_c)));
      func_200126_a(new ItemBed(Blocks.field_196596_au, (new Item.Properties()).func_200917_a(1).func_200916_a(ItemGroup.field_78031_c)));
      func_200126_a(new ItemBed(Blocks.field_196597_av, (new Item.Properties()).func_200917_a(1).func_200916_a(ItemGroup.field_78031_c)));
      func_200126_a(new ItemBed(Blocks.field_196598_aw, (new Item.Properties()).func_200917_a(1).func_200916_a(ItemGroup.field_78031_c)));
      func_200126_a(new ItemBed(Blocks.field_196599_ax, (new Item.Properties()).func_200917_a(1).func_200916_a(ItemGroup.field_78031_c)));
      func_200126_a(new ItemBed(Blocks.field_196600_ay, (new Item.Properties()).func_200917_a(1).func_200916_a(ItemGroup.field_78031_c)));
      func_200126_a(new ItemBed(Blocks.field_196601_az, (new Item.Properties()).func_200917_a(1).func_200916_a(ItemGroup.field_78031_c)));
      func_200126_a(new ItemBed(Blocks.field_196550_aA, (new Item.Properties()).func_200917_a(1).func_200916_a(ItemGroup.field_78031_c)));
      func_200126_a(new ItemBed(Blocks.field_196551_aB, (new Item.Properties()).func_200917_a(1).func_200916_a(ItemGroup.field_78031_c)));
      func_195936_a("cookie", new ItemFood(2, 0.1F, false, (new Item.Properties()).func_200916_a(ItemGroup.field_78039_h)));
      func_195936_a("filled_map", new ItemMap(new Item.Properties()));
      func_195936_a("shears", new ItemShears((new Item.Properties()).func_200918_c(238).func_200916_a(ItemGroup.field_78040_i)));
      func_195936_a("melon_slice", new ItemFood(2, 0.3F, false, (new Item.Properties()).func_200916_a(ItemGroup.field_78039_h)));
      func_195936_a("dried_kelp", (new ItemFood(1, 0.3F, false, (new Item.Properties()).func_200916_a(ItemGroup.field_78039_h))).func_203174_f());
      func_195936_a("pumpkin_seeds", new ItemSeeds(Blocks.field_150393_bb, (new Item.Properties()).func_200916_a(ItemGroup.field_78035_l)));
      func_195936_a("melon_seeds", new ItemSeeds(Blocks.field_150394_bc, (new Item.Properties()).func_200916_a(ItemGroup.field_78035_l)));
      func_195936_a("beef", new ItemFood(3, 0.3F, true, (new Item.Properties()).func_200916_a(ItemGroup.field_78039_h)));
      func_195936_a("cooked_beef", new ItemFood(8, 0.8F, true, (new Item.Properties()).func_200916_a(ItemGroup.field_78039_h)));
      func_195936_a("chicken", (new ItemFood(2, 0.3F, true, (new Item.Properties()).func_200916_a(ItemGroup.field_78039_h))).func_185070_a(new PotionEffect(MobEffects.field_76438_s, 600, 0), 0.3F));
      func_195936_a("cooked_chicken", new ItemFood(6, 0.6F, true, (new Item.Properties()).func_200916_a(ItemGroup.field_78039_h)));
      func_195936_a("rotten_flesh", (new ItemFood(4, 0.1F, true, (new Item.Properties()).func_200916_a(ItemGroup.field_78039_h))).func_185070_a(new PotionEffect(MobEffects.field_76438_s, 600, 0), 0.8F));
      func_195936_a("ender_pearl", new ItemEnderPearl((new Item.Properties()).func_200917_a(16).func_200916_a(ItemGroup.field_78026_f)));
      func_195936_a("blaze_rod", new Item((new Item.Properties()).func_200916_a(ItemGroup.field_78035_l)));
      func_195936_a("ghast_tear", new Item((new Item.Properties()).func_200916_a(ItemGroup.field_78038_k)));
      func_195936_a("gold_nugget", new Item((new Item.Properties()).func_200916_a(ItemGroup.field_78035_l)));
      func_195936_a("nether_wart", new ItemSeeds(Blocks.field_150388_bm, (new Item.Properties()).func_200916_a(ItemGroup.field_78035_l)));
      func_195936_a("potion", new ItemPotion((new Item.Properties()).func_200917_a(1).func_200916_a(ItemGroup.field_78038_k)));
      ItemGlassBottle var1 = new ItemGlassBottle((new Item.Properties()).func_200916_a(ItemGroup.field_78038_k));
      func_195936_a("glass_bottle", var1);
      func_195936_a("spider_eye", (new ItemFood(2, 0.8F, false, (new Item.Properties()).func_200916_a(ItemGroup.field_78039_h))).func_185070_a(new PotionEffect(MobEffects.field_76436_u, 100, 0), 1.0F));
      func_195936_a("fermented_spider_eye", new Item((new Item.Properties()).func_200916_a(ItemGroup.field_78038_k)));
      func_195936_a("blaze_powder", new Item((new Item.Properties()).func_200916_a(ItemGroup.field_78038_k)));
      func_195936_a("magma_cream", new Item((new Item.Properties()).func_200916_a(ItemGroup.field_78038_k)));
      func_200879_a(Blocks.field_150382_bo, ItemGroup.field_78038_k);
      func_200879_a(Blocks.field_150383_bp, ItemGroup.field_78038_k);
      func_195936_a("ender_eye", new ItemEnderEye((new Item.Properties()).func_200916_a(ItemGroup.field_78026_f)));
      func_195936_a("glistering_melon_slice", new Item((new Item.Properties()).func_200916_a(ItemGroup.field_78038_k)));
      func_195936_a("bat_spawn_egg", new ItemSpawnEgg(EntityType.field_200791_e, 4996656, 986895, (new Item.Properties()).func_200916_a(ItemGroup.field_78026_f)));
      func_195936_a("blaze_spawn_egg", new ItemSpawnEgg(EntityType.field_200792_f, 16167425, 16775294, (new Item.Properties()).func_200916_a(ItemGroup.field_78026_f)));
      func_195936_a("cave_spider_spawn_egg", new ItemSpawnEgg(EntityType.field_200794_h, 803406, 11013646, (new Item.Properties()).func_200916_a(ItemGroup.field_78026_f)));
      func_195936_a("chicken_spawn_egg", new ItemSpawnEgg(EntityType.field_200795_i, 10592673, 16711680, (new Item.Properties()).func_200916_a(ItemGroup.field_78026_f)));
      func_195936_a("cod_spawn_egg", new ItemSpawnEgg(EntityType.field_203780_j, 12691306, 15058059, (new Item.Properties()).func_200916_a(ItemGroup.field_78026_f)));
      func_195936_a("cow_spawn_egg", new ItemSpawnEgg(EntityType.field_200796_j, 4470310, 10592673, (new Item.Properties()).func_200916_a(ItemGroup.field_78026_f)));
      func_195936_a("creeper_spawn_egg", new ItemSpawnEgg(EntityType.field_200797_k, 894731, 0, (new Item.Properties()).func_200916_a(ItemGroup.field_78026_f)));
      func_195936_a("dolphin_spawn_egg", new ItemSpawnEgg(EntityType.field_205137_n, 2243405, 16382457, (new Item.Properties()).func_200916_a(ItemGroup.field_78026_f)));
      func_195936_a("donkey_spawn_egg", new ItemSpawnEgg(EntityType.field_200798_l, 5457209, 8811878, (new Item.Properties()).func_200916_a(ItemGroup.field_78026_f)));
      func_195936_a("drowned_spawn_egg", new ItemSpawnEgg(EntityType.field_204724_o, 9433559, 7969893, (new Item.Properties()).func_200916_a(ItemGroup.field_78026_f)));
      func_195936_a("elder_guardian_spawn_egg", new ItemSpawnEgg(EntityType.field_200800_n, 13552826, 7632531, (new Item.Properties()).func_200916_a(ItemGroup.field_78026_f)));
      func_195936_a("enderman_spawn_egg", new ItemSpawnEgg(EntityType.field_200803_q, 1447446, 0, (new Item.Properties()).func_200916_a(ItemGroup.field_78026_f)));
      func_195936_a("endermite_spawn_egg", new ItemSpawnEgg(EntityType.field_200804_r, 1447446, 7237230, (new Item.Properties()).func_200916_a(ItemGroup.field_78026_f)));
      func_195936_a("evoker_spawn_egg", new ItemSpawnEgg(EntityType.field_200806_t, 9804699, 1973274, (new Item.Properties()).func_200916_a(ItemGroup.field_78026_f)));
      func_195936_a("ghast_spawn_egg", new ItemSpawnEgg(EntityType.field_200811_y, 16382457, 12369084, (new Item.Properties()).func_200916_a(ItemGroup.field_78026_f)));
      func_195936_a("guardian_spawn_egg", new ItemSpawnEgg(EntityType.field_200761_A, 5931634, 15826224, (new Item.Properties()).func_200916_a(ItemGroup.field_78026_f)));
      func_195936_a("horse_spawn_egg", new ItemSpawnEgg(EntityType.field_200762_B, 12623485, 15656192, (new Item.Properties()).func_200916_a(ItemGroup.field_78026_f)));
      func_195936_a("husk_spawn_egg", new ItemSpawnEgg(EntityType.field_200763_C, 7958625, 15125652, (new Item.Properties()).func_200916_a(ItemGroup.field_78026_f)));
      func_195936_a("llama_spawn_egg", new ItemSpawnEgg(EntityType.field_200769_I, 12623485, 10051392, (new Item.Properties()).func_200916_a(ItemGroup.field_78026_f)));
      func_195936_a("magma_cube_spawn_egg", new ItemSpawnEgg(EntityType.field_200771_K, 3407872, 16579584, (new Item.Properties()).func_200916_a(ItemGroup.field_78026_f)));
      func_195936_a("mooshroom_spawn_egg", new ItemSpawnEgg(EntityType.field_200780_T, 10489616, 12040119, (new Item.Properties()).func_200916_a(ItemGroup.field_78026_f)));
      func_195936_a("mule_spawn_egg", new ItemSpawnEgg(EntityType.field_200779_S, 1769984, 5321501, (new Item.Properties()).func_200916_a(ItemGroup.field_78026_f)));
      func_195936_a("ocelot_spawn_egg", new ItemSpawnEgg(EntityType.field_200781_U, 15720061, 5653556, (new Item.Properties()).func_200916_a(ItemGroup.field_78026_f)));
      func_195936_a("parrot_spawn_egg", new ItemSpawnEgg(EntityType.field_200783_W, 894731, 16711680, (new Item.Properties()).func_200916_a(ItemGroup.field_78026_f)));
      func_195936_a("phantom_spawn_egg", new ItemSpawnEgg(EntityType.field_203097_aH, 4411786, 8978176, (new Item.Properties()).func_200916_a(ItemGroup.field_78026_f)));
      func_195936_a("pig_spawn_egg", new ItemSpawnEgg(EntityType.field_200784_X, 15771042, 14377823, (new Item.Properties()).func_200916_a(ItemGroup.field_78026_f)));
      func_195936_a("polar_bear_spawn_egg", new ItemSpawnEgg(EntityType.field_200786_Z, 15921906, 9803152, (new Item.Properties()).func_200916_a(ItemGroup.field_78026_f)));
      func_195936_a("pufferfish_spawn_egg", new ItemSpawnEgg(EntityType.field_203779_Z, 16167425, 3654642, (new Item.Properties()).func_200916_a(ItemGroup.field_78026_f)));
      func_195936_a("rabbit_spawn_egg", new ItemSpawnEgg(EntityType.field_200736_ab, 10051392, 7555121, (new Item.Properties()).func_200916_a(ItemGroup.field_78026_f)));
      func_195936_a("salmon_spawn_egg", new ItemSpawnEgg(EntityType.field_203778_ae, 10489616, 951412, (new Item.Properties()).func_200916_a(ItemGroup.field_78026_f)));
      func_195936_a("sheep_spawn_egg", new ItemSpawnEgg(EntityType.field_200737_ac, 15198183, 16758197, (new Item.Properties()).func_200916_a(ItemGroup.field_78026_f)));
      func_195936_a("shulker_spawn_egg", new ItemSpawnEgg(EntityType.field_200738_ad, 9725844, 5060690, (new Item.Properties()).func_200916_a(ItemGroup.field_78026_f)));
      func_195936_a("silverfish_spawn_egg", new ItemSpawnEgg(EntityType.field_200740_af, 7237230, 3158064, (new Item.Properties()).func_200916_a(ItemGroup.field_78026_f)));
      func_195936_a("skeleton_spawn_egg", new ItemSpawnEgg(EntityType.field_200741_ag, 12698049, 4802889, (new Item.Properties()).func_200916_a(ItemGroup.field_78026_f)));
      func_195936_a("skeleton_horse_spawn_egg", new ItemSpawnEgg(EntityType.field_200742_ah, 6842447, 15066584, (new Item.Properties()).func_200916_a(ItemGroup.field_78026_f)));
      func_195936_a("slime_spawn_egg", new ItemSpawnEgg(EntityType.field_200743_ai, 5349438, 8306542, (new Item.Properties()).func_200916_a(ItemGroup.field_78026_f)));
      func_195936_a("spider_spawn_egg", new ItemSpawnEgg(EntityType.field_200748_an, 3419431, 11013646, (new Item.Properties()).func_200916_a(ItemGroup.field_78026_f)));
      func_195936_a("squid_spawn_egg", new ItemSpawnEgg(EntityType.field_200749_ao, 2243405, 7375001, (new Item.Properties()).func_200916_a(ItemGroup.field_78026_f)));
      func_195936_a("stray_spawn_egg", new ItemSpawnEgg(EntityType.field_200750_ap, 6387319, 14543594, (new Item.Properties()).func_200916_a(ItemGroup.field_78026_f)));
      func_195936_a("tropical_fish_spawn_egg", new ItemSpawnEgg(EntityType.field_204262_at, 15690005, 16775663, (new Item.Properties()).func_200916_a(ItemGroup.field_78026_f)));
      func_195936_a("turtle_spawn_egg", new ItemSpawnEgg(EntityType.field_203099_aq, 15198183, 44975, (new Item.Properties()).func_200916_a(ItemGroup.field_78026_f)));
      func_195936_a("vex_spawn_egg", new ItemSpawnEgg(EntityType.field_200755_au, 8032420, 15265265, (new Item.Properties()).func_200916_a(ItemGroup.field_78026_f)));
      func_195936_a("villager_spawn_egg", new ItemSpawnEgg(EntityType.field_200756_av, 5651507, 12422002, (new Item.Properties()).func_200916_a(ItemGroup.field_78026_f)));
      func_195936_a("vindicator_spawn_egg", new ItemSpawnEgg(EntityType.field_200758_ax, 9804699, 2580065, (new Item.Properties()).func_200916_a(ItemGroup.field_78026_f)));
      func_195936_a("witch_spawn_egg", new ItemSpawnEgg(EntityType.field_200759_ay, 3407872, 5349438, (new Item.Properties()).func_200916_a(ItemGroup.field_78026_f)));
      func_195936_a("wither_skeleton_spawn_egg", new ItemSpawnEgg(EntityType.field_200722_aA, 1315860, 4672845, (new Item.Properties()).func_200916_a(ItemGroup.field_78026_f)));
      func_195936_a("wolf_spawn_egg", new ItemSpawnEgg(EntityType.field_200724_aC, 14144467, 13545366, (new Item.Properties()).func_200916_a(ItemGroup.field_78026_f)));
      func_195936_a("zombie_spawn_egg", new ItemSpawnEgg(EntityType.field_200725_aD, 44975, 7969893, (new Item.Properties()).func_200916_a(ItemGroup.field_78026_f)));
      func_195936_a("zombie_horse_spawn_egg", new ItemSpawnEgg(EntityType.field_200726_aE, 3232308, 9945732, (new Item.Properties()).func_200916_a(ItemGroup.field_78026_f)));
      func_195936_a("zombie_pigman_spawn_egg", new ItemSpawnEgg(EntityType.field_200785_Y, 15373203, 5009705, (new Item.Properties()).func_200916_a(ItemGroup.field_78026_f)));
      func_195936_a("zombie_villager_spawn_egg", new ItemSpawnEgg(EntityType.field_200727_aF, 5651507, 7969893, (new Item.Properties()).func_200916_a(ItemGroup.field_78026_f)));
      func_195936_a("experience_bottle", new ItemExpBottle((new Item.Properties()).func_200916_a(ItemGroup.field_78026_f).func_208103_a(EnumRarity.UNCOMMON)));
      func_195936_a("fire_charge", new ItemFireCharge((new Item.Properties()).func_200916_a(ItemGroup.field_78026_f)));
      func_195936_a("writable_book", new ItemWritableBook((new Item.Properties()).func_200917_a(1).func_200916_a(ItemGroup.field_78026_f)));
      func_195936_a("written_book", new ItemWrittenBook((new Item.Properties()).func_200917_a(16)));
      func_195936_a("emerald", new Item((new Item.Properties()).func_200916_a(ItemGroup.field_78035_l)));
      func_195936_a("item_frame", new ItemItemFrame((new Item.Properties()).func_200916_a(ItemGroup.field_78031_c)));
      func_200879_a(Blocks.field_150457_bL, ItemGroup.field_78031_c);
      func_195936_a("carrot", new ItemSeedFood(3, 0.6F, Blocks.field_150459_bM, (new Item.Properties()).func_200916_a(ItemGroup.field_78039_h)));
      func_195936_a("potato", new ItemSeedFood(1, 0.3F, Blocks.field_150469_bN, (new Item.Properties()).func_200916_a(ItemGroup.field_78039_h)));
      func_195936_a("baked_potato", new ItemFood(5, 0.6F, false, (new Item.Properties()).func_200916_a(ItemGroup.field_78039_h)));
      func_195936_a("poisonous_potato", (new ItemFood(2, 0.3F, false, (new Item.Properties()).func_200916_a(ItemGroup.field_78039_h))).func_185070_a(new PotionEffect(MobEffects.field_76436_u, 100, 0), 0.6F));
      func_195936_a("map", new ItemEmptyMap((new Item.Properties()).func_200916_a(ItemGroup.field_78026_f)));
      func_195936_a("golden_carrot", new ItemFood(6, 1.2F, false, (new Item.Properties()).func_200916_a(ItemGroup.field_78038_k)));
      func_200126_a(new ItemWallOrFloor(Blocks.field_196703_eM, Blocks.field_196701_eL, (new Item.Properties()).func_200916_a(ItemGroup.field_78031_c).func_208103_a(EnumRarity.UNCOMMON)));
      func_200126_a(new ItemWallOrFloor(Blocks.field_196705_eO, Blocks.field_196704_eN, (new Item.Properties()).func_200916_a(ItemGroup.field_78031_c).func_208103_a(EnumRarity.UNCOMMON)));
      func_200126_a(new ItemSkull(Blocks.field_196710_eS, Blocks.field_196709_eR, (new Item.Properties()).func_200916_a(ItemGroup.field_78031_c).func_208103_a(EnumRarity.UNCOMMON)));
      func_200126_a(new ItemWallOrFloor(Blocks.field_196708_eQ, Blocks.field_196707_eP, (new Item.Properties()).func_200916_a(ItemGroup.field_78031_c).func_208103_a(EnumRarity.UNCOMMON)));
      func_200126_a(new ItemWallOrFloor(Blocks.field_196714_eU, Blocks.field_196712_eT, (new Item.Properties()).func_200916_a(ItemGroup.field_78031_c).func_208103_a(EnumRarity.UNCOMMON)));
      func_200126_a(new ItemWallOrFloor(Blocks.field_196716_eW, Blocks.field_196715_eV, (new Item.Properties()).func_200916_a(ItemGroup.field_78031_c).func_208103_a(EnumRarity.UNCOMMON)));
      func_195936_a("carrot_on_a_stick", new ItemCarrotOnAStick((new Item.Properties()).func_200918_c(25).func_200916_a(ItemGroup.field_78029_e)));
      func_195936_a("nether_star", new ItemSimpleFoiled((new Item.Properties()).func_200916_a(ItemGroup.field_78035_l).func_208103_a(EnumRarity.UNCOMMON)));
      func_195936_a("pumpkin_pie", new ItemFood(8, 0.3F, false, (new Item.Properties()).func_200916_a(ItemGroup.field_78039_h)));
      func_195936_a("firework_rocket", new ItemFireworkRocket((new Item.Properties()).func_200916_a(ItemGroup.field_78026_f)));
      func_195936_a("firework_star", new ItemFireworkStar((new Item.Properties()).func_200916_a(ItemGroup.field_78026_f)));
      func_195936_a("enchanted_book", new ItemEnchantedBook((new Item.Properties()).func_200917_a(1).func_208103_a(EnumRarity.UNCOMMON)));
      func_195936_a("nether_brick", new Item((new Item.Properties()).func_200916_a(ItemGroup.field_78035_l)));
      func_195936_a("quartz", new Item((new Item.Properties()).func_200916_a(ItemGroup.field_78035_l)));
      func_195936_a("tnt_minecart", new ItemMinecart(EntityMinecart.Type.TNT, (new Item.Properties()).func_200917_a(1).func_200916_a(ItemGroup.field_78029_e)));
      func_195936_a("hopper_minecart", new ItemMinecart(EntityMinecart.Type.HOPPER, (new Item.Properties()).func_200917_a(1).func_200916_a(ItemGroup.field_78029_e)));
      func_195936_a("prismarine_shard", new Item((new Item.Properties()).func_200916_a(ItemGroup.field_78035_l)));
      func_195936_a("prismarine_crystals", new Item((new Item.Properties()).func_200916_a(ItemGroup.field_78035_l)));
      func_195936_a("rabbit", new ItemFood(3, 0.3F, true, (new Item.Properties()).func_200916_a(ItemGroup.field_78039_h)));
      func_195936_a("cooked_rabbit", new ItemFood(5, 0.6F, true, (new Item.Properties()).func_200916_a(ItemGroup.field_78039_h)));
      func_195936_a("rabbit_stew", new ItemSoup(10, (new Item.Properties()).func_200917_a(1).func_200916_a(ItemGroup.field_78039_h)));
      func_195936_a("rabbit_foot", new Item((new Item.Properties()).func_200916_a(ItemGroup.field_78038_k)));
      func_195936_a("rabbit_hide", new Item((new Item.Properties()).func_200916_a(ItemGroup.field_78035_l)));
      func_195936_a("armor_stand", new ItemArmorStand((new Item.Properties()).func_200917_a(16).func_200916_a(ItemGroup.field_78031_c)));
      func_195936_a("iron_horse_armor", new Item((new Item.Properties()).func_200917_a(1).func_200916_a(ItemGroup.field_78026_f)));
      func_195936_a("golden_horse_armor", new Item((new Item.Properties()).func_200917_a(1).func_200916_a(ItemGroup.field_78026_f)));
      func_195936_a("diamond_horse_armor", new Item((new Item.Properties()).func_200917_a(1).func_200916_a(ItemGroup.field_78026_f)));
      func_195936_a("lead", new ItemLead((new Item.Properties()).func_200916_a(ItemGroup.field_78040_i)));
      func_195936_a("name_tag", new ItemNameTag((new Item.Properties()).func_200916_a(ItemGroup.field_78040_i)));
      func_195936_a("command_block_minecart", new ItemMinecart(EntityMinecart.Type.COMMAND_BLOCK, (new Item.Properties()).func_200917_a(1)));
      func_195936_a("mutton", new ItemFood(2, 0.3F, true, (new Item.Properties()).func_200916_a(ItemGroup.field_78039_h)));
      func_195936_a("cooked_mutton", new ItemFood(6, 0.8F, true, (new Item.Properties()).func_200916_a(ItemGroup.field_78039_h)));
      func_195936_a("white_banner", new ItemBanner(Blocks.field_196784_gT, Blocks.field_196843_hj, (new Item.Properties()).func_200917_a(16).func_200916_a(ItemGroup.field_78031_c)));
      func_195936_a("orange_banner", new ItemBanner(Blocks.field_196786_gU, Blocks.field_196845_hk, (new Item.Properties()).func_200917_a(16).func_200916_a(ItemGroup.field_78031_c)));
      func_195936_a("magenta_banner", new ItemBanner(Blocks.field_196788_gV, Blocks.field_196847_hl, (new Item.Properties()).func_200917_a(16).func_200916_a(ItemGroup.field_78031_c)));
      func_195936_a("light_blue_banner", new ItemBanner(Blocks.field_196790_gW, Blocks.field_196849_hm, (new Item.Properties()).func_200917_a(16).func_200916_a(ItemGroup.field_78031_c)));
      func_195936_a("yellow_banner", new ItemBanner(Blocks.field_196792_gX, Blocks.field_196851_hn, (new Item.Properties()).func_200917_a(16).func_200916_a(ItemGroup.field_78031_c)));
      func_195936_a("lime_banner", new ItemBanner(Blocks.field_196794_gY, Blocks.field_196853_ho, (new Item.Properties()).func_200917_a(16).func_200916_a(ItemGroup.field_78031_c)));
      func_195936_a("pink_banner", new ItemBanner(Blocks.field_196796_gZ, Blocks.field_196855_hp, (new Item.Properties()).func_200917_a(16).func_200916_a(ItemGroup.field_78031_c)));
      func_195936_a("gray_banner", new ItemBanner(Blocks.field_196826_ha, Blocks.field_196857_hq, (new Item.Properties()).func_200917_a(16).func_200916_a(ItemGroup.field_78031_c)));
      func_195936_a("light_gray_banner", new ItemBanner(Blocks.field_196827_hb, Blocks.field_196859_hr, (new Item.Properties()).func_200917_a(16).func_200916_a(ItemGroup.field_78031_c)));
      func_195936_a("cyan_banner", new ItemBanner(Blocks.field_196829_hc, Blocks.field_196861_hs, (new Item.Properties()).func_200917_a(16).func_200916_a(ItemGroup.field_78031_c)));
      func_195936_a("purple_banner", new ItemBanner(Blocks.field_196831_hd, Blocks.field_196863_ht, (new Item.Properties()).func_200917_a(16).func_200916_a(ItemGroup.field_78031_c)));
      func_195936_a("blue_banner", new ItemBanner(Blocks.field_196833_he, Blocks.field_196865_hu, (new Item.Properties()).func_200917_a(16).func_200916_a(ItemGroup.field_78031_c)));
      func_195936_a("brown_banner", new ItemBanner(Blocks.field_196835_hf, Blocks.field_196867_hv, (new Item.Properties()).func_200917_a(16).func_200916_a(ItemGroup.field_78031_c)));
      func_195936_a("green_banner", new ItemBanner(Blocks.field_196837_hg, Blocks.field_196869_hw, (new Item.Properties()).func_200917_a(16).func_200916_a(ItemGroup.field_78031_c)));
      func_195936_a("red_banner", new ItemBanner(Blocks.field_196839_hh, Blocks.field_196871_hx, (new Item.Properties()).func_200917_a(16).func_200916_a(ItemGroup.field_78031_c)));
      func_195936_a("black_banner", new ItemBanner(Blocks.field_196841_hi, Blocks.field_196873_hy, (new Item.Properties()).func_200917_a(16).func_200916_a(ItemGroup.field_78031_c)));
      func_195936_a("end_crystal", new ItemEndCrystal((new Item.Properties()).func_200916_a(ItemGroup.field_78031_c).func_208103_a(EnumRarity.RARE)));
      func_195936_a("chorus_fruit", (new ItemChorusFruit(4, 0.3F, (new Item.Properties()).func_200916_a(ItemGroup.field_78035_l))).func_77848_i());
      func_195936_a("popped_chorus_fruit", new Item((new Item.Properties()).func_200916_a(ItemGroup.field_78035_l)));
      func_195936_a("beetroot", new ItemFood(1, 0.6F, false, (new Item.Properties()).func_200916_a(ItemGroup.field_78039_h)));
      func_195936_a("beetroot_seeds", new ItemSeeds(Blocks.field_185773_cZ, (new Item.Properties()).func_200916_a(ItemGroup.field_78035_l)));
      func_195936_a("beetroot_soup", new ItemSoup(6, (new Item.Properties()).func_200917_a(1).func_200916_a(ItemGroup.field_78039_h)));
      func_195936_a("dragon_breath", new Item((new Item.Properties()).func_200919_a(var1).func_200916_a(ItemGroup.field_78038_k).func_208103_a(EnumRarity.UNCOMMON)));
      func_195936_a("splash_potion", new ItemSplashPotion((new Item.Properties()).func_200917_a(1).func_200916_a(ItemGroup.field_78038_k)));
      func_195936_a("spectral_arrow", new ItemSpectralArrow((new Item.Properties()).func_200916_a(ItemGroup.field_78037_j)));
      func_195936_a("tipped_arrow", new ItemTippedArrow((new Item.Properties()).func_200916_a(ItemGroup.field_78037_j)));
      func_195936_a("lingering_potion", new ItemLingeringPotion((new Item.Properties()).func_200917_a(1).func_200916_a(ItemGroup.field_78038_k)));
      func_195936_a("shield", new ItemShield((new Item.Properties()).func_200918_c(336).func_200916_a(ItemGroup.field_78037_j)));
      func_195936_a("elytra", new ItemElytra((new Item.Properties()).func_200918_c(432).func_200916_a(ItemGroup.field_78029_e).func_208103_a(EnumRarity.UNCOMMON)));
      func_195936_a("spruce_boat", new ItemBoat(EntityBoat.Type.SPRUCE, (new Item.Properties()).func_200917_a(1).func_200916_a(ItemGroup.field_78029_e)));
      func_195936_a("birch_boat", new ItemBoat(EntityBoat.Type.BIRCH, (new Item.Properties()).func_200917_a(1).func_200916_a(ItemGroup.field_78029_e)));
      func_195936_a("jungle_boat", new ItemBoat(EntityBoat.Type.JUNGLE, (new Item.Properties()).func_200917_a(1).func_200916_a(ItemGroup.field_78029_e)));
      func_195936_a("acacia_boat", new ItemBoat(EntityBoat.Type.ACACIA, (new Item.Properties()).func_200917_a(1).func_200916_a(ItemGroup.field_78029_e)));
      func_195936_a("dark_oak_boat", new ItemBoat(EntityBoat.Type.DARK_OAK, (new Item.Properties()).func_200917_a(1).func_200916_a(ItemGroup.field_78029_e)));
      func_195936_a("totem_of_undying", new Item((new Item.Properties()).func_200917_a(1).func_200916_a(ItemGroup.field_78037_j).func_208103_a(EnumRarity.UNCOMMON)));
      func_195936_a("shulker_shell", new Item((new Item.Properties()).func_200916_a(ItemGroup.field_78035_l)));
      func_195936_a("iron_nugget", new Item((new Item.Properties()).func_200916_a(ItemGroup.field_78035_l)));
      func_195936_a("knowledge_book", new ItemKnowledgeBook((new Item.Properties()).func_200917_a(1)));
      func_195936_a("debug_stick", new ItemDebugStick((new Item.Properties()).func_200917_a(1)));
      func_195936_a("music_disc_13", new ItemRecord(1, SoundEvents.field_187828_ep, (new Item.Properties()).func_200917_a(1).func_200916_a(ItemGroup.field_78026_f).func_208103_a(EnumRarity.RARE)));
      func_195936_a("music_disc_cat", new ItemRecord(2, SoundEvents.field_187832_er, (new Item.Properties()).func_200917_a(1).func_200916_a(ItemGroup.field_78026_f).func_208103_a(EnumRarity.RARE)));
      func_195936_a("music_disc_blocks", new ItemRecord(3, SoundEvents.field_187830_eq, (new Item.Properties()).func_200917_a(1).func_200916_a(ItemGroup.field_78026_f).func_208103_a(EnumRarity.RARE)));
      func_195936_a("music_disc_chirp", new ItemRecord(4, SoundEvents.field_187834_es, (new Item.Properties()).func_200917_a(1).func_200916_a(ItemGroup.field_78026_f).func_208103_a(EnumRarity.RARE)));
      func_195936_a("music_disc_far", new ItemRecord(5, SoundEvents.field_187836_et, (new Item.Properties()).func_200917_a(1).func_200916_a(ItemGroup.field_78026_f).func_208103_a(EnumRarity.RARE)));
      func_195936_a("music_disc_mall", new ItemRecord(6, SoundEvents.field_187838_eu, (new Item.Properties()).func_200917_a(1).func_200916_a(ItemGroup.field_78026_f).func_208103_a(EnumRarity.RARE)));
      func_195936_a("music_disc_mellohi", new ItemRecord(7, SoundEvents.field_187840_ev, (new Item.Properties()).func_200917_a(1).func_200916_a(ItemGroup.field_78026_f).func_208103_a(EnumRarity.RARE)));
      func_195936_a("music_disc_stal", new ItemRecord(8, SoundEvents.field_187842_ew, (new Item.Properties()).func_200917_a(1).func_200916_a(ItemGroup.field_78026_f).func_208103_a(EnumRarity.RARE)));
      func_195936_a("music_disc_strad", new ItemRecord(9, SoundEvents.field_187844_ex, (new Item.Properties()).func_200917_a(1).func_200916_a(ItemGroup.field_78026_f).func_208103_a(EnumRarity.RARE)));
      func_195936_a("music_disc_ward", new ItemRecord(10, SoundEvents.field_187848_ez, (new Item.Properties()).func_200917_a(1).func_200916_a(ItemGroup.field_78026_f).func_208103_a(EnumRarity.RARE)));
      func_195936_a("music_disc_11", new ItemRecord(11, SoundEvents.field_187826_eo, (new Item.Properties()).func_200917_a(1).func_200916_a(ItemGroup.field_78026_f).func_208103_a(EnumRarity.RARE)));
      func_195936_a("music_disc_wait", new ItemRecord(12, SoundEvents.field_187846_ey, (new Item.Properties()).func_200917_a(1).func_200916_a(ItemGroup.field_78026_f).func_208103_a(EnumRarity.RARE)));
      func_195936_a("trident", new ItemTrident((new Item.Properties()).func_200918_c(250).func_200916_a(ItemGroup.field_78037_j)));
      func_195936_a("phantom_membrane", new Item((new Item.Properties()).func_200916_a(ItemGroup.field_78038_k)));
      func_195936_a("nautilus_shell", new Item((new Item.Properties()).func_200916_a(ItemGroup.field_78035_l)));
      func_195936_a("heart_of_the_sea", new Item((new Item.Properties()).func_200916_a(ItemGroup.field_78035_l).func_208103_a(EnumRarity.UNCOMMON)));
   }

   private static void func_179216_c(Block var0) {
      func_200126_a(new ItemBlock(var0, new Item.Properties()));
   }

   private static void func_200879_a(Block var0, ItemGroup var1) {
      func_200126_a(new ItemBlock(var0, (new Item.Properties()).func_200916_a(var1)));
   }

   private static void func_200126_a(ItemBlock var0) {
      func_179214_a(var0.func_179223_d(), var0);
   }

   protected static void func_179214_a(Block var0, Item var1) {
      func_195940_a(IRegistry.field_212618_g.func_177774_c(var0), var1);
   }

   private static void func_195936_a(String var0, Item var1) {
      func_195940_a(new ResourceLocation(var0), var1);
   }

   private static void func_195940_a(ResourceLocation var0, Item var1) {
      if (var1 instanceof ItemBlock) {
         ((ItemBlock)var1).func_195946_a(field_179220_a, var1);
      }

      IRegistry.field_212630_s.func_82595_a(var0, var1);
   }

   public ItemStack func_190903_i() {
      return new ItemStack(this);
   }

   public boolean func_206844_a(Tag<Item> var1) {
      return var1.func_199685_a_(this);
   }

   public static class Properties {
      private int field_200920_a = 64;
      private int field_200921_b;
      private Item field_200922_c;
      private ItemGroup field_200923_d;
      private EnumRarity field_208104_e;

      public Properties() {
         super();
         this.field_208104_e = EnumRarity.COMMON;
      }

      public Item.Properties func_200917_a(int var1) {
         if (this.field_200921_b > 0) {
            throw new RuntimeException("Unable to have damage AND stack.");
         } else {
            this.field_200920_a = var1;
            return this;
         }
      }

      public Item.Properties func_200915_b(int var1) {
         return this.field_200921_b == 0 ? this.func_200918_c(var1) : this;
      }

      private Item.Properties func_200918_c(int var1) {
         this.field_200921_b = var1;
         this.field_200920_a = 1;
         return this;
      }

      public Item.Properties func_200919_a(Item var1) {
         this.field_200922_c = var1;
         return this;
      }

      public Item.Properties func_200916_a(ItemGroup var1) {
         this.field_200923_d = var1;
         return this;
      }

      public Item.Properties func_208103_a(EnumRarity var1) {
         this.field_208104_e = var1;
         return this;
      }
   }
}
