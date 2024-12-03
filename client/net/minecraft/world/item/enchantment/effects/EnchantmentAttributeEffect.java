package net.minecraft.world.item.enchantment.effects;

import com.google.common.collect.HashMultimap;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.item.enchantment.EnchantedItemInUse;
import net.minecraft.world.item.enchantment.LevelBasedValue;
import net.minecraft.world.phys.Vec3;

public record EnchantmentAttributeEffect(ResourceLocation id, Holder<Attribute> attribute, LevelBasedValue amount, AttributeModifier.Operation operation) implements EnchantmentLocationBasedEffect {
   public static final MapCodec<EnchantmentAttributeEffect> CODEC = RecordCodecBuilder.mapCodec((var0) -> var0.group(ResourceLocation.CODEC.fieldOf("id").forGetter(EnchantmentAttributeEffect::id), Attribute.CODEC.fieldOf("attribute").forGetter(EnchantmentAttributeEffect::attribute), LevelBasedValue.CODEC.fieldOf("amount").forGetter(EnchantmentAttributeEffect::amount), AttributeModifier.Operation.CODEC.fieldOf("operation").forGetter(EnchantmentAttributeEffect::operation)).apply(var0, EnchantmentAttributeEffect::new));

   public EnchantmentAttributeEffect(ResourceLocation var1, Holder<Attribute> var2, LevelBasedValue var3, AttributeModifier.Operation var4) {
      super();
      this.id = var1;
      this.attribute = var2;
      this.amount = var3;
      this.operation = var4;
   }

   private ResourceLocation idForSlot(StringRepresentable var1) {
      return this.id.withSuffix("/" + var1.getSerializedName());
   }

   public AttributeModifier getModifier(int var1, StringRepresentable var2) {
      return new AttributeModifier(this.idForSlot(var2), (double)this.amount().calculate(var1), this.operation());
   }

   public void onChangedBlock(ServerLevel var1, int var2, EnchantedItemInUse var3, Entity var4, Vec3 var5, boolean var6) {
      if (var6 && var4 instanceof LivingEntity var7) {
         var7.getAttributes().addTransientAttributeModifiers(this.makeAttributeMap(var2, var3.inSlot()));
      }

   }

   public void onDeactivated(EnchantedItemInUse var1, Entity var2, Vec3 var3, int var4) {
      if (var2 instanceof LivingEntity var5) {
         var5.getAttributes().removeAttributeModifiers(this.makeAttributeMap(var4, var1.inSlot()));
      }

   }

   private HashMultimap<Holder<Attribute>, AttributeModifier> makeAttributeMap(int var1, EquipmentSlot var2) {
      HashMultimap var3 = HashMultimap.create();
      var3.put(this.attribute, this.getModifier(var1, var2));
      return var3;
   }

   public MapCodec<EnchantmentAttributeEffect> codec() {
      return CODEC;
   }
}
