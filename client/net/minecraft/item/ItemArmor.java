package net.minecraft.item;

import com.google.common.collect.Multimap;
import java.util.List;
import java.util.UUID;
import net.minecraft.block.BlockDispenser;
import net.minecraft.dispenser.BehaviorDefaultDispenseItem;
import net.minecraft.dispenser.IBehaviorDispenseItem;
import net.minecraft.dispenser.IBlockSource;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EntitySelectors;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class ItemArmor extends Item {
   private static final UUID[] field_185084_n = new UUID[]{UUID.fromString("845DB27C-C624-495F-8C9F-6020A9A58B6B"), UUID.fromString("D8499B04-0E66-4726-AB29-64469D734E0D"), UUID.fromString("9F3D476D-C118-4544-8365-64846904B48E"), UUID.fromString("2AD3F246-FEE1-4E67-B886-69FD380BB150")};
   public static final IBehaviorDispenseItem field_96605_cw = new BehaviorDefaultDispenseItem() {
      protected ItemStack func_82487_b(IBlockSource var1, ItemStack var2) {
         ItemStack var3 = ItemArmor.func_185082_a(var1, var2);
         return var3.func_190926_b() ? super.func_82487_b(var1, var2) : var3;
      }
   };
   protected final EntityEquipmentSlot field_77881_a;
   protected final int field_77879_b;
   protected final float field_189415_e;
   protected final IArmorMaterial field_200882_e;

   public static ItemStack func_185082_a(IBlockSource var0, ItemStack var1) {
      BlockPos var2 = var0.func_180699_d().func_177972_a((EnumFacing)var0.func_189992_e().func_177229_b(BlockDispenser.field_176441_a));
      List var3 = var0.func_197524_h().func_175647_a(EntityLivingBase.class, new AxisAlignedBB(var2), EntitySelectors.field_180132_d.and(new EntitySelectors.ArmoredMob(var1)));
      if (var3.isEmpty()) {
         return ItemStack.field_190927_a;
      } else {
         EntityLivingBase var4 = (EntityLivingBase)var3.get(0);
         EntityEquipmentSlot var5 = EntityLiving.func_184640_d(var1);
         ItemStack var6 = var1.func_77979_a(1);
         var4.func_184201_a(var5, var6);
         if (var4 instanceof EntityLiving) {
            ((EntityLiving)var4).func_184642_a(var5, 2.0F);
            ((EntityLiving)var4).func_110163_bv();
         }

         return var1;
      }
   }

   public ItemArmor(IArmorMaterial var1, EntityEquipmentSlot var2, Item.Properties var3) {
      super(var3.func_200915_b(var1.func_200896_a(var2)));
      this.field_200882_e = var1;
      this.field_77881_a = var2;
      this.field_77879_b = var1.func_200902_b(var2);
      this.field_189415_e = var1.func_200901_e();
      BlockDispenser.func_199774_a(this, field_96605_cw);
   }

   public EntityEquipmentSlot func_185083_B_() {
      return this.field_77881_a;
   }

   public int func_77619_b() {
      return this.field_200882_e.func_200900_a();
   }

   public IArmorMaterial func_200880_d() {
      return this.field_200882_e;
   }

   public boolean func_82789_a(ItemStack var1, ItemStack var2) {
      return this.field_200882_e.func_200898_c().test(var2) || super.func_82789_a(var1, var2);
   }

   public ActionResult<ItemStack> func_77659_a(World var1, EntityPlayer var2, EnumHand var3) {
      ItemStack var4 = var2.func_184586_b(var3);
      EntityEquipmentSlot var5 = EntityLiving.func_184640_d(var4);
      ItemStack var6 = var2.func_184582_a(var5);
      if (var6.func_190926_b()) {
         var2.func_184201_a(var5, var4.func_77946_l());
         var4.func_190920_e(0);
         return new ActionResult(EnumActionResult.SUCCESS, var4);
      } else {
         return new ActionResult(EnumActionResult.FAIL, var4);
      }
   }

   public Multimap<String, AttributeModifier> func_111205_h(EntityEquipmentSlot var1) {
      Multimap var2 = super.func_111205_h(var1);
      if (var1 == this.field_77881_a) {
         var2.put(SharedMonsterAttributes.field_188791_g.func_111108_a(), new AttributeModifier(field_185084_n[var1.func_188454_b()], "Armor modifier", (double)this.field_77879_b, 0));
         var2.put(SharedMonsterAttributes.field_189429_h.func_111108_a(), new AttributeModifier(field_185084_n[var1.func_188454_b()], "Armor toughness", (double)this.field_189415_e, 0));
      }

      return var2;
   }

   public int func_200881_e() {
      return this.field_77879_b;
   }
}
