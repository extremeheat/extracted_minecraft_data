package net.minecraft.item.crafting;

import com.google.common.collect.Maps;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import net.minecraft.block.Block;
import net.minecraft.block.BlockStoneBrick;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.Item;
import net.minecraft.item.ItemFishFood;
import net.minecraft.item.ItemStack;

public class FurnaceRecipes {
   private static final FurnaceRecipes field_77606_a = new FurnaceRecipes();
   private Map<ItemStack, ItemStack> field_77604_b = Maps.newHashMap();
   private Map<ItemStack, Float> field_77605_c = Maps.newHashMap();

   public static FurnaceRecipes func_77602_a() {
      return field_77606_a;
   }

   private FurnaceRecipes() {
      super();
      this.func_151393_a(Blocks.field_150366_p, new ItemStack(Items.field_151042_j), 0.7F);
      this.func_151393_a(Blocks.field_150352_o, new ItemStack(Items.field_151043_k), 1.0F);
      this.func_151393_a(Blocks.field_150482_ag, new ItemStack(Items.field_151045_i), 1.0F);
      this.func_151393_a(Blocks.field_150354_m, new ItemStack(Blocks.field_150359_w), 0.1F);
      this.func_151396_a(Items.field_151147_al, new ItemStack(Items.field_151157_am), 0.35F);
      this.func_151396_a(Items.field_151082_bd, new ItemStack(Items.field_151083_be), 0.35F);
      this.func_151396_a(Items.field_151076_bf, new ItemStack(Items.field_151077_bg), 0.35F);
      this.func_151396_a(Items.field_179558_bo, new ItemStack(Items.field_179559_bp), 0.35F);
      this.func_151396_a(Items.field_179561_bm, new ItemStack(Items.field_179557_bn), 0.35F);
      this.func_151393_a(Blocks.field_150347_e, new ItemStack(Blocks.field_150348_b), 0.1F);
      this.func_151394_a(new ItemStack(Blocks.field_150417_aV, 1, BlockStoneBrick.field_176248_b), new ItemStack(Blocks.field_150417_aV, 1, BlockStoneBrick.field_176251_N), 0.1F);
      this.func_151396_a(Items.field_151119_aD, new ItemStack(Items.field_151118_aC), 0.3F);
      this.func_151393_a(Blocks.field_150435_aG, new ItemStack(Blocks.field_150405_ch), 0.35F);
      this.func_151393_a(Blocks.field_150434_aF, new ItemStack(Items.field_151100_aR, 1, EnumDyeColor.GREEN.func_176767_b()), 0.2F);
      this.func_151393_a(Blocks.field_150364_r, new ItemStack(Items.field_151044_h, 1, 1), 0.15F);
      this.func_151393_a(Blocks.field_150363_s, new ItemStack(Items.field_151044_h, 1, 1), 0.15F);
      this.func_151393_a(Blocks.field_150412_bA, new ItemStack(Items.field_151166_bC), 1.0F);
      this.func_151396_a(Items.field_151174_bG, new ItemStack(Items.field_151168_bH), 0.35F);
      this.func_151393_a(Blocks.field_150424_aL, new ItemStack(Items.field_151130_bT), 0.1F);
      this.func_151394_a(new ItemStack(Blocks.field_150360_v, 1, 1), new ItemStack(Blocks.field_150360_v, 1, 0), 0.15F);
      ItemFishFood.FishType[] var1 = ItemFishFood.FishType.values();
      int var2 = var1.length;

      for(int var3 = 0; var3 < var2; ++var3) {
         ItemFishFood.FishType var4 = var1[var3];
         if (var4.func_150973_i()) {
            this.func_151394_a(new ItemStack(Items.field_151115_aP, 1, var4.func_150976_a()), new ItemStack(Items.field_179566_aV, 1, var4.func_150976_a()), 0.35F);
         }
      }

      this.func_151393_a(Blocks.field_150365_q, new ItemStack(Items.field_151044_h), 0.1F);
      this.func_151393_a(Blocks.field_150450_ax, new ItemStack(Items.field_151137_ax), 0.7F);
      this.func_151393_a(Blocks.field_150369_x, new ItemStack(Items.field_151100_aR, 1, EnumDyeColor.BLUE.func_176767_b()), 0.2F);
      this.func_151393_a(Blocks.field_150449_bY, new ItemStack(Items.field_151128_bU), 0.2F);
   }

   public void func_151393_a(Block var1, ItemStack var2, float var3) {
      this.func_151396_a(Item.func_150898_a(var1), var2, var3);
   }

   public void func_151396_a(Item var1, ItemStack var2, float var3) {
      this.func_151394_a(new ItemStack(var1, 1, 32767), var2, var3);
   }

   public void func_151394_a(ItemStack var1, ItemStack var2, float var3) {
      this.field_77604_b.put(var1, var2);
      this.field_77605_c.put(var2, var3);
   }

   public ItemStack func_151395_a(ItemStack var1) {
      Iterator var2 = this.field_77604_b.entrySet().iterator();

      Entry var3;
      do {
         if (!var2.hasNext()) {
            return null;
         }

         var3 = (Entry)var2.next();
      } while(!this.func_151397_a(var1, (ItemStack)var3.getKey()));

      return (ItemStack)var3.getValue();
   }

   private boolean func_151397_a(ItemStack var1, ItemStack var2) {
      return var2.func_77973_b() == var1.func_77973_b() && (var2.func_77960_j() == 32767 || var2.func_77960_j() == var1.func_77960_j());
   }

   public Map<ItemStack, ItemStack> func_77599_b() {
      return this.field_77604_b;
   }

   public float func_151398_b(ItemStack var1) {
      Iterator var2 = this.field_77605_c.entrySet().iterator();

      Entry var3;
      do {
         if (!var2.hasNext()) {
            return 0.0F;
         }

         var3 = (Entry)var2.next();
      } while(!this.func_151397_a(var1, (ItemStack)var3.getKey()));

      return (Float)var3.getValue();
   }
}
