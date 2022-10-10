package net.minecraft.item;

import com.google.common.collect.Maps;
import java.util.Map;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.passive.EntitySheep;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumHand;

public class ItemDye extends Item {
   private static final Map<EnumDyeColor, ItemDye> field_195963_a = Maps.newEnumMap(EnumDyeColor.class);
   private final EnumDyeColor field_195964_b;

   public ItemDye(EnumDyeColor var1, Item.Properties var2) {
      super(var2);
      this.field_195964_b = var1;
      field_195963_a.put(var1, this);
   }

   public boolean func_111207_a(ItemStack var1, EntityPlayer var2, EntityLivingBase var3, EnumHand var4) {
      if (var3 instanceof EntitySheep) {
         EntitySheep var5 = (EntitySheep)var3;
         if (!var5.func_70892_o() && var5.func_175509_cj() != this.field_195964_b) {
            var5.func_175512_b(this.field_195964_b);
            var1.func_190918_g(1);
         }

         return true;
      } else {
         return false;
      }
   }

   public EnumDyeColor func_195962_g() {
      return this.field_195964_b;
   }

   public static ItemDye func_195961_a(EnumDyeColor var0) {
      return (ItemDye)field_195963_a.get(var0);
   }
}
