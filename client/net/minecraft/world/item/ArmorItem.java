package net.minecraft.world.item;

import com.google.common.base.Suppliers;
import com.mojang.serialization.Codec;
import java.util.List;
import java.util.function.Supplier;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.core.dispenser.BlockSource;
import net.minecraft.core.dispenser.DefaultDispenseItemBehavior;
import net.minecraft.core.dispenser.DispenseItemBehavior;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.DispenserBlock;
import net.minecraft.world.phys.AABB;

public class ArmorItem extends Item implements Equipable {
   public static final DispenseItemBehavior DISPENSE_ITEM_BEHAVIOR = new DefaultDispenseItemBehavior() {
      protected ItemStack execute(BlockSource var1, ItemStack var2) {
         return ArmorItem.dispenseArmor(var1, var2) ? var2 : super.execute(var1, var2);
      }
   };
   protected final Type type;
   protected final Holder<ArmorMaterial> material;
   private final Supplier<ItemAttributeModifiers> defaultModifiers;

   public static boolean dispenseArmor(BlockSource var0, ItemStack var1) {
      BlockPos var2 = var0.pos().relative((Direction)var0.state().getValue(DispenserBlock.FACING));
      List var3 = var0.level().getEntitiesOfClass(LivingEntity.class, new AABB(var2), EntitySelector.NO_SPECTATORS.and(new EntitySelector.MobCanWearArmorEntitySelector(var1)));
      if (var3.isEmpty()) {
         return false;
      } else {
         LivingEntity var4 = (LivingEntity)var3.get(0);
         EquipmentSlot var5 = var4.getEquipmentSlotForItem(var1);
         ItemStack var6 = var1.split(1);
         var4.setItemSlot(var5, var6);
         if (var4 instanceof Mob) {
            ((Mob)var4).setDropChance(var5, 2.0F);
            ((Mob)var4).setPersistenceRequired();
         }

         return true;
      }
   }

   public ArmorItem(Holder<ArmorMaterial> var1, Type var2, Item.Properties var3) {
      super(var3);
      this.material = var1;
      this.type = var2;
      DispenserBlock.registerBehavior(this, DISPENSE_ITEM_BEHAVIOR);
      this.defaultModifiers = Suppliers.memoize(() -> {
         int var2x = ((ArmorMaterial)var1.value()).getDefense(var2);
         float var3 = ((ArmorMaterial)var1.value()).toughness();
         ItemAttributeModifiers.Builder var4 = ItemAttributeModifiers.builder();
         EquipmentSlotGroup var5 = EquipmentSlotGroup.bySlot(var2.getSlot());
         ResourceLocation var6 = ResourceLocation.withDefaultNamespace("armor." + var2.getName());
         var4.add(Attributes.ARMOR, new AttributeModifier(var6, (double)var2x, AttributeModifier.Operation.ADD_VALUE), var5);
         var4.add(Attributes.ARMOR_TOUGHNESS, new AttributeModifier(var6, (double)var3, AttributeModifier.Operation.ADD_VALUE), var5);
         float var7 = ((ArmorMaterial)var1.value()).knockbackResistance();
         if (var7 > 0.0F) {
            var4.add(Attributes.KNOCKBACK_RESISTANCE, new AttributeModifier(var6, (double)var7, AttributeModifier.Operation.ADD_VALUE), var5);
         }

         return var4.build();
      });
   }

   public Type getType() {
      return this.type;
   }

   public int getEnchantmentValue() {
      return ((ArmorMaterial)this.material.value()).enchantmentValue();
   }

   public Holder<ArmorMaterial> getMaterial() {
      return this.material;
   }

   public boolean isValidRepairItem(ItemStack var1, ItemStack var2) {
      return ((Ingredient)((ArmorMaterial)this.material.value()).repairIngredient().get()).test(var2) || super.isValidRepairItem(var1, var2);
   }

   public InteractionResultHolder<ItemStack> use(Level var1, Player var2, InteractionHand var3) {
      return this.swapWithEquipmentSlot(this, var1, var2, var3);
   }

   public ItemAttributeModifiers getDefaultAttributeModifiers() {
      return (ItemAttributeModifiers)this.defaultModifiers.get();
   }

   public int getDefense() {
      return ((ArmorMaterial)this.material.value()).getDefense(this.type);
   }

   public float getToughness() {
      return ((ArmorMaterial)this.material.value()).toughness();
   }

   public EquipmentSlot getEquipmentSlot() {
      return this.type.getSlot();
   }

   public Holder<SoundEvent> getEquipSound() {
      return ((ArmorMaterial)this.getMaterial().value()).equipSound();
   }

   public static enum Type implements StringRepresentable {
      HELMET(EquipmentSlot.HEAD, 11, "helmet"),
      CHESTPLATE(EquipmentSlot.CHEST, 16, "chestplate"),
      LEGGINGS(EquipmentSlot.LEGS, 15, "leggings"),
      BOOTS(EquipmentSlot.FEET, 13, "boots"),
      BODY(EquipmentSlot.BODY, 16, "body");

      public static final Codec<Type> CODEC = StringRepresentable.fromValues(Type::values);
      private final EquipmentSlot slot;
      private final String name;
      private final int durability;

      private Type(final EquipmentSlot var3, final int var4, final String var5) {
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

      public String getSerializedName() {
         return this.name;
      }

      // $FF: synthetic method
      private static Type[] $values() {
         return new Type[]{HELMET, CHESTPLATE, LEGGINGS, BOOTS, BODY};
      }
   }
}
