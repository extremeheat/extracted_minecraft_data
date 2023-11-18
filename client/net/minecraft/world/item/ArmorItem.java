package net.minecraft.world.item;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import com.google.common.collect.ImmutableMultimap.Builder;
import java.util.EnumMap;
import java.util.List;
import java.util.UUID;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.dispenser.BlockSource;
import net.minecraft.core.dispenser.DefaultDispenseItemBehavior;
import net.minecraft.core.dispenser.DispenseItemBehavior;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.DispenserBlock;
import net.minecraft.world.phys.AABB;

public class ArmorItem extends Item implements Equipable {
   private static final EnumMap<ArmorItem.Type, UUID> ARMOR_MODIFIER_UUID_PER_TYPE = Util.make(new EnumMap<>(ArmorItem.Type.class), var0 -> {
      var0.put(ArmorItem.Type.BOOTS, UUID.fromString("845DB27C-C624-495F-8C9F-6020A9A58B6B"));
      var0.put(ArmorItem.Type.LEGGINGS, UUID.fromString("D8499B04-0E66-4726-AB29-64469D734E0D"));
      var0.put(ArmorItem.Type.CHESTPLATE, UUID.fromString("9F3D476D-C118-4544-8365-64846904B48E"));
      var0.put(ArmorItem.Type.HELMET, UUID.fromString("2AD3F246-FEE1-4E67-B886-69FD380BB150"));
   });
   public static final DispenseItemBehavior DISPENSE_ITEM_BEHAVIOR = new DefaultDispenseItemBehavior() {
      @Override
      protected ItemStack execute(BlockSource var1, ItemStack var2) {
         return ArmorItem.dispenseArmor(var1, var2) ? var2 : super.execute(var1, var2);
      }
   };
   protected final ArmorItem.Type type;
   private final int defense;
   private final float toughness;
   protected final float knockbackResistance;
   protected final ArmorMaterial material;
   private final Multimap<Attribute, AttributeModifier> defaultModifiers;

   public static boolean dispenseArmor(BlockSource var0, ItemStack var1) {
      BlockPos var2 = var0.pos().relative(var0.state().getValue(DispenserBlock.FACING));
      List var3 = var0.level()
         .getEntitiesOfClass(LivingEntity.class, new AABB(var2), EntitySelector.NO_SPECTATORS.and(new EntitySelector.MobCanWearArmorEntitySelector(var1)));
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

   public ArmorItem(ArmorMaterial var1, ArmorItem.Type var2, Item.Properties var3) {
      super(var3.defaultDurability(var1.getDurabilityForType(var2)));
      this.material = var1;
      this.type = var2;
      this.defense = var1.getDefenseForType(var2);
      this.toughness = var1.getToughness();
      this.knockbackResistance = var1.getKnockbackResistance();
      DispenserBlock.registerBehavior(this, DISPENSE_ITEM_BEHAVIOR);
      Builder var4 = ImmutableMultimap.builder();
      UUID var5 = ARMOR_MODIFIER_UUID_PER_TYPE.get(var2);
      var4.put(Attributes.ARMOR, new AttributeModifier(var5, "Armor modifier", (double)this.defense, AttributeModifier.Operation.ADDITION));
      var4.put(Attributes.ARMOR_TOUGHNESS, new AttributeModifier(var5, "Armor toughness", (double)this.toughness, AttributeModifier.Operation.ADDITION));
      if (var1 == ArmorMaterials.NETHERITE) {
         var4.put(
            Attributes.KNOCKBACK_RESISTANCE,
            new AttributeModifier(var5, "Armor knockback resistance", (double)this.knockbackResistance, AttributeModifier.Operation.ADDITION)
         );
      }

      this.defaultModifiers = var4.build();
   }

   public ArmorItem.Type getType() {
      return this.type;
   }

   @Override
   public int getEnchantmentValue() {
      return this.material.getEnchantmentValue();
   }

   public ArmorMaterial getMaterial() {
      return this.material;
   }

   @Override
   public boolean isValidRepairItem(ItemStack var1, ItemStack var2) {
      return this.material.getRepairIngredient().test(var2) || super.isValidRepairItem(var1, var2);
   }

   @Override
   public InteractionResultHolder<ItemStack> use(Level var1, Player var2, InteractionHand var3) {
      return this.swapWithEquipmentSlot(this, var1, var2, var3);
   }

   @Override
   public Multimap<Attribute, AttributeModifier> getDefaultAttributeModifiers(EquipmentSlot var1) {
      return var1 == this.type.getSlot() ? this.defaultModifiers : super.getDefaultAttributeModifiers(var1);
   }

   public int getDefense() {
      return this.defense;
   }

   public float getToughness() {
      return this.toughness;
   }

   @Override
   public EquipmentSlot getEquipmentSlot() {
      return this.type.getSlot();
   }

   @Override
   public SoundEvent getEquipSound() {
      return this.getMaterial().getEquipSound();
   }

   public static enum Type {
      HELMET(EquipmentSlot.HEAD, "helmet"),
      CHESTPLATE(EquipmentSlot.CHEST, "chestplate"),
      LEGGINGS(EquipmentSlot.LEGS, "leggings"),
      BOOTS(EquipmentSlot.FEET, "boots");

      private final EquipmentSlot slot;
      private final String name;

      private Type(EquipmentSlot var3, String var4) {
         this.slot = var3;
         this.name = var4;
      }

      public EquipmentSlot getSlot() {
         return this.slot;
      }

      public String getName() {
         return this.name;
      }
   }
}
