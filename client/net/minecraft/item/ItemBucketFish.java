package net.minecraft.item;

import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.passive.AbstractFish;
import net.minecraft.entity.passive.EntityTropicalFish;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.fluid.Fluid;
import net.minecraft.init.SoundEvents;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;

public class ItemBucketFish extends ItemBucket {
   private final EntityType<?> field_203794_a;

   public ItemBucketFish(EntityType<?> var1, Fluid var2, Item.Properties var3) {
      super(var2, var3);
      this.field_203794_a = var1;
   }

   public void func_203792_a(World var1, ItemStack var2, BlockPos var3) {
      if (!var1.field_72995_K) {
         this.func_205357_b(var1, var2, var3);
      }

   }

   protected void func_203791_b(@Nullable EntityPlayer var1, IWorld var2, BlockPos var3) {
      var2.func_184133_a(var1, var3, SoundEvents.field_203819_X, SoundCategory.NEUTRAL, 1.0F, 1.0F);
   }

   private void func_205357_b(World var1, ItemStack var2, BlockPos var3) {
      Entity var4 = this.field_203794_a.func_208049_a(var1, var2, (EntityPlayer)null, var3, true, false);
      if (var4 != null) {
         ((AbstractFish)var4).func_203706_r(true);
      }

   }

   public void func_77624_a(ItemStack var1, @Nullable World var2, List<ITextComponent> var3, ITooltipFlag var4) {
      if (this.field_203794_a == EntityType.field_204262_at) {
         NBTTagCompound var5 = var1.func_77978_p();
         if (var5 != null && var5.func_150297_b("BucketVariantTag", 3)) {
            int var6 = var5.func_74762_e("BucketVariantTag");
            TextFormatting[] var7 = new TextFormatting[]{TextFormatting.ITALIC, TextFormatting.GRAY};
            String var8 = "color.minecraft." + EntityTropicalFish.func_212326_d(var6);
            String var9 = "color.minecraft." + EntityTropicalFish.func_212323_p(var6);

            for(int var10 = 0; var10 < EntityTropicalFish.field_204227_bz.length; ++var10) {
               if (var6 == EntityTropicalFish.field_204227_bz[var10]) {
                  var3.add((new TextComponentTranslation(EntityTropicalFish.func_212324_b(var10), new Object[0])).func_211709_a(var7));
                  return;
               }
            }

            var3.add((new TextComponentTranslation(EntityTropicalFish.func_212327_q(var6), new Object[0])).func_211709_a(var7));
            TextComponentTranslation var11 = new TextComponentTranslation(var8, new Object[0]);
            if (!var8.equals(var9)) {
               var11.func_150258_a(", ").func_150257_a(new TextComponentTranslation(var9, new Object[0]));
            }

            var11.func_211709_a(var7);
            var3.add(var11);
         }
      }

   }
}
