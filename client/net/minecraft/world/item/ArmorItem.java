package net.minecraft.world.item;

import com.google.common.base.Suppliers;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import com.google.common.collect.ImmutableMultimap.Builder;
import com.mojang.serialization.Codec;
import java.util.EnumMap;
import java.util.List;
import java.util.UUID;
import java.util.function.Supplier;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.dispenser.BlockSource;
import net.minecraft.core.dispenser.DefaultDispenseItemBehavior;
import net.minecraft.core.dispenser.DispenseItemBehavior;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.StringRepresentable;
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
      var0.put(ArmorItem.Type.BODY, UUID.fromString("C1C72771-8B8E-BA4A-ACE0-81A93C8928B2"));
   });
   public static final DispenseItemBehavior DISPENSE_ITEM_BEHAVIOR = new DefaultDispenseItemBehavior() {
      @Override
      protected ItemStack execute(BlockSource var1, ItemStack var2) {
         return ArmorItem.dispenseArmor(var1, var2) ? var2 : super.execute(var1, var2);
      }
   };
   protected final ArmorItem.Type type;
   protected final Holder<ArmorMaterial> material;
   private final Supplier<Multimap<Holder<Attribute>, AttributeModifier>> defaultModifiers;

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

   public ArmorItem(Holder<ArmorMaterial> var1, ArmorItem.Type var2, Item.Properties var3) {
      super(var3);
      this.material = var1;
      this.type = var2;
      DispenserBlock.registerBehavior(this, DISPENSE_ITEM_BEHAVIOR);
      this.defaultModifiers = Suppliers.memoize(
         () -> {
            int var2xx = ((ArmorMaterial)var1.value()).getDefense(var2);
            float var3xx = ((ArmorMaterial)var1.value()).toughness();
            Builder var4 = ImmutableMultimap.builder();
            UUID var5 = ARMOR_MODIFIER_UUID_PER_TYPE.get(var2);
            var4.put(Attributes.ARMOR, new AttributeModifier(var5, "Armor modifier", (double)var2xx, AttributeModifier.Operation.ADD_VALUE));
            var4.put(Attributes.ARMOR_TOUGHNESS, new AttributeModifier(var5, "Armor toughness", (double)var3xx, AttributeModifier.Operation.ADD_VALUE));
            float var6 = ((ArmorMaterial)var1.value()).knockbackResistance();
            if (var6 > 0.0F) {
               var4.put(
                  Attributes.KNOCKBACK_RESISTANCE,
                  new AttributeModifier(var5, "Armor knockback resistance", (double)var6, AttributeModifier.Operation.ADD_VALUE)
               );
            }
   
            return var4.build();
         }
      );
   }

   public ArmorItem.Type getType() {
      return this.type;
   }

   @Override
   public int getEnchantmentValue() {
      return ((ArmorMaterial)this.material.value()).enchantmentValue();
   }

   public Holder<ArmorMaterial> getMaterial() {
      return this.material;
   }

   @Override
   public boolean isValidRepairItem(ItemStack var1, ItemStack var2) {
      return ((ArmorMaterial)this.material.value()).repairIngredient().get().test(var2) || super.isValidRepairItem(var1, var2);
   }

   @Override
   public InteractionResultHolder<ItemStack> use(Level var1, Player var2, InteractionHand var3) {
      return this.swapWithEquipmentSlot(this, var1, var2, var3);
   }

   @Override
   public Multimap<Holder<Attribute>, AttributeModifier> getDefaultAttributeModifiers(EquipmentSlot var1) {
      return var1 == this.type.getSlot() ? (Multimap)this.defaultModifiers.get() : super.getDefaultAttributeModifiers(var1);
   }

   public int getDefense() {
      return ((ArmorMaterial)this.material.value()).getDefense(this.type);
   }

   public float getToughness() {
      return ((ArmorMaterial)this.material.value()).toughness();
   }

   @Override
   public EquipmentSlot getEquipmentSlot() {
      return this.type.getSlot();
   }

   @Override
   public Holder<SoundEvent> getEquipSound() {
      return ((ArmorMaterial)this.getMaterial().value()).equipSound();
   }

   public static enum Type implements StringRepresentable {
      HELMET(EquipmentSlot.HEAD, 11, "helmet"),
      CHESTPLATE(EquipmentSlot.CHEST, 16, "chestplate"),
      LEGGINGS(EquipmentSlot.LEGS, 15, "leggings"),
      BOOTS(EquipmentSlot.FEET, 13, "boots"),
      BODY(EquipmentSlot.BODY, 16, "body");

      public static final Codec<ArmorItem.Type> CODEC = StringRepresentable.fromValues(ArmorItem.Type::values);
      private final EquipmentSlot slot;
      private final String name;
      private final int durability;

      private Type(EquipmentSlot var3, int var4, String var5) {
         this.slot = var3;
         this.name = var5;
         this.durability = var4;
      }

      public int getDurability(int var1) {
         return this.durability * var1;
      }

      public EquipmentSlot getSlot() {
         return this.slot;
      }

      public String getName() {
         return this.name;
      }

      public boolean hasTrims() {
         return this == HELMET || this == CHESTPLATE || this == LEGGINGS || this == BOOTS;
      }

      @Override
      public String getSerializedName() {
         return this.name;
      }
   }
}