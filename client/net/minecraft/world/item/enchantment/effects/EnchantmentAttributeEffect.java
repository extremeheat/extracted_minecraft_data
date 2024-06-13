package net.minecraft.world.item.enchantment.effects;

import com.google.common.collect.HashMultimap;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.UUID;
import net.minecraft.core.Holder;
import net.minecraft.core.UUIDUtil;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.item.enchantment.EnchantedItemInUse;
import net.minecraft.world.item.enchantment.LevelBasedValue;
import net.minecraft.world.phys.Vec3;

public record EnchantmentAttributeEffect(String name, Holder<Attribute> attribute, LevelBasedValue amount, AttributeModifier.Operation operation, UUID uuid)
   implements EnchantmentLocationBasedEffect {
   public static final MapCodec<EnchantmentAttributeEffect> CODEC = RecordCodecBuilder.mapCodec(
      var0 -> var0.group(
               Codec.STRING.fieldOf("name").forGetter(EnchantmentAttributeEffect::name),
               Attribute.CODEC.fieldOf("attribute").forGetter(EnchantmentAttributeEffect::attribute),
               LevelBasedValue.CODEC.fieldOf("amount").forGetter(EnchantmentAttributeEffect::amount),
               AttributeModifier.Operation.CODEC.fieldOf("operation").forGetter(EnchantmentAttributeEffect::operation),
               UUIDUtil.STRING_CODEC.fieldOf("uuid").forGetter(EnchantmentAttributeEffect::uuid)
            )
            .apply(var0, EnchantmentAttributeEffect::new)
   );

   public EnchantmentAttributeEffect(String name, Holder<Attribute> attribute, LevelBasedValue amount, AttributeModifier.Operation operation, UUID uuid) {
      super();
      this.name = name;
      this.attribute = attribute;
      this.amount = amount;
      this.operation = operation;
      this.uuid = uuid;
   }

   public AttributeModifier getModifier(int var1) {
      return new AttributeModifier(this.uuid(), this.name(), (double)this.amount().calculate(var1), this.operation());
   }

   @Override
   public void onChangedBlock(ServerLevel var1, int var2, EnchantedItemInUse var3, Entity var4, Vec3 var5, boolean var6) {
      if (var6 && var4 instanceof LivingEntity var7) {
         var7.getAttributes().addTransientAttributeModifiers(this.makeAttributeMap(var2));
      }
   }

   @Override
   public void onDeactivated(EnchantedItemInUse var1, Entity var2, Vec3 var3, int var4) {
      if (var2 instanceof LivingEntity var5) {
         var5.getAttributes().removeAttributeModifiers(this.makeAttributeMap(var4));
      }
   }

   private HashMultimap<Holder<Attribute>, AttributeModifier> makeAttributeMap(int var1) {
      HashMultimap var2 = HashMultimap.create();
      var2.put(this.attribute, this.getModifier(var1));
      return var2;
   }

   @Override
   public MapCodec<EnchantmentAttributeEffect> codec() {
      return CODEC;
   }
}
