package net.minecraft.world.item;

import com.google.common.collect.Multimap;
import java.util.List;
import java.util.UUID;
import net.minecraft.core.BlockPos;
import net.minecraft.core.BlockSource;
import net.minecraft.core.Direction;
import net.minecraft.core.dispenser.DefaultDispenseItemBehavior;
import net.minecraft.core.dispenser.DispenseItemBehavior;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.monster.SharedMonsterAttributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.DispenserBlock;
import net.minecraft.world.phys.AABB;

public class ArmorItem extends Item {
   private static final UUID[] ARMOR_MODIFIER_UUID_PER_SLOT = new UUID[]{UUID.fromString("845DB27C-C624-495F-8C9F-6020A9A58B6B"), UUID.fromString("D8499B04-0E66-4726-AB29-64469D734E0D"), UUID.fromString("9F3D476D-C118-4544-8365-64846904B48E"), UUID.fromString("2AD3F246-FEE1-4E67-B886-69FD380BB150")};
   public static final DispenseItemBehavior DISPENSE_ITEM_BEHAVIOR = new DefaultDispenseItemBehavior() {
      protected ItemStack execute(BlockSource var1, ItemStack var2) {
         return ArmorItem.dispenseArmor(var1, var2) ? var2 : super.execute(var1, var2);
      }
   };
   protected final EquipmentSlot slot;
   protected final int defense;
   protected final float toughness;
   protected final ArmorMaterial material;

   public static boolean dispenseArmor(BlockSource var0, ItemStack var1) {
      BlockPos var2 = var0.getPos().relative((Direction)var0.getBlockState().getValue(DispenserBlock.FACING));
      List var3 = var0.getLevel().getEntitiesOfClass(LivingEntity.class, new AABB(var2), EntitySelector.NO_SPECTATORS.and(new EntitySelector.MobCanWearArmourEntitySelector(var1)));
      if (var3.isEmpty()) {
         return false;
      } else {
         LivingEntity var4 = (LivingEntity)var3.get(0);
         EquipmentSlot var5 = Mob.getEquipmentSlotForItem(var1);
         ItemStack var6 = var1.split(1);
         var4.setItemSlot(var5, var6);
         if (var4 instanceof Mob) {
            ((Mob)var4).setDropChance(var5, 2.0F);
            ((Mob)var4).setPersistenceRequired();
         }

         return true;
      }
   }

   public ArmorItem(ArmorMaterial var1, EquipmentSlot var2, Item.Properties var3) {
      super(var3.defaultDurability(var1.getDurabilityForSlot(var2)));
      this.material = var1;
      this.slot = var2;
      this.defense = var1.getDefenseForSlot(var2);
      this.toughness = var1.getToughness();
      DispenserBlock.registerBehavior(this, DISPENSE_ITEM_BEHAVIOR);
   }

   public EquipmentSlot getSlot() {
      return this.slot;
   }

   public int getEnchantmentValue() {
      return this.material.getEnchantmentValue();
   }

   public ArmorMaterial getMaterial() {
      return this.material;
   }

   public boolean isValidRepairItem(ItemStack var1, ItemStack var2) {
      return this.material.getRepairIngredient().test(var2) || super.isValidRepairItem(var1, var2);
   }

   public InteractionResultHolder use(Level var1, Player var2, InteractionHand var3) {
      ItemStack var4 = var2.getItemInHand(var3);
      EquipmentSlot var5 = Mob.getEquipmentSlotForItem(var4);
      ItemStack var6 = var2.getItemBySlot(var5);
      if (var6.isEmpty()) {
         var2.setItemSlot(var5, var4.copy());
         var4.setCount(0);
         return InteractionResultHolder.success(var4);
      } else {
         return InteractionResultHolder.fail(var4);
      }
   }

   public Multimap getDefaultAttributeModifiers(EquipmentSlot var1) {
      Multimap var2 = super.getDefaultAttributeModifiers(var1);
      if (var1 == this.slot) {
         var2.put(SharedMonsterAttributes.ARMOR.getName(), new AttributeModifier(ARMOR_MODIFIER_UUID_PER_SLOT[var1.getIndex()], "Armor modifier", (double)this.defense, AttributeModifier.Operation.ADDITION));
         var2.put(SharedMonsterAttributes.ARMOR_TOUGHNESS.getName(), new AttributeModifier(ARMOR_MODIFIER_UUID_PER_SLOT[var1.getIndex()], "Armor toughness", (double)this.toughness, AttributeModifier.Operation.ADDITION));
      }

      return var2;
   }

   public int getDefense() {
      return this.defense;
   }
}
