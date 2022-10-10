package net.minecraft.item;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import java.util.List;
import java.util.Map;
import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.block.BlockJukebox;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.stats.StatList;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;

public class ItemRecord extends Item {
   private static final Map<SoundEvent, ItemRecord> field_150928_b = Maps.newHashMap();
   private static final List<ItemRecord> field_195976_b = Lists.newArrayList();
   private final int field_195977_c;
   private final SoundEvent field_185076_b;

   protected ItemRecord(int var1, SoundEvent var2, Item.Properties var3) {
      super(var3);
      this.field_195977_c = var1;
      this.field_185076_b = var2;
      field_150928_b.put(this.field_185076_b, this);
      field_195976_b.add(this);
   }

   public static ItemRecord func_195974_a(Random var0) {
      return (ItemRecord)field_195976_b.get(var0.nextInt(field_195976_b.size()));
   }

   public EnumActionResult func_195939_a(ItemUseContext var1) {
      World var2 = var1.func_195991_k();
      BlockPos var3 = var1.func_195995_a();
      IBlockState var4 = var2.func_180495_p(var3);
      if (var4.func_177230_c() == Blocks.field_150421_aI && !(Boolean)var4.func_177229_b(BlockJukebox.field_176432_a)) {
         ItemStack var5 = var1.func_195996_i();
         if (!var2.field_72995_K) {
            ((BlockJukebox)Blocks.field_150421_aI).func_176431_a(var2, var3, var4, var5);
            var2.func_180498_a((EntityPlayer)null, 1010, var3, Item.func_150891_b(this));
            var5.func_190918_g(1);
            EntityPlayer var6 = var1.func_195999_j();
            if (var6 != null) {
               var6.func_195066_a(StatList.field_188092_Z);
            }
         }

         return EnumActionResult.SUCCESS;
      } else {
         return EnumActionResult.PASS;
      }
   }

   public int func_195975_g() {
      return this.field_195977_c;
   }

   public void func_77624_a(ItemStack var1, @Nullable World var2, List<ITextComponent> var3, ITooltipFlag var4) {
      var3.add(this.func_200299_h().func_211708_a(TextFormatting.GRAY));
   }

   public ITextComponent func_200299_h() {
      return new TextComponentTranslation(this.func_77658_a() + ".desc", new Object[0]);
   }

   @Nullable
   public static ItemRecord func_185074_a(SoundEvent var0) {
      return (ItemRecord)field_150928_b.get(var0);
   }

   public SoundEvent func_185075_h() {
      return this.field_185076_b;
   }
}
