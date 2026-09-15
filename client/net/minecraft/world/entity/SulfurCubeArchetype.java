package net.minecraft.world.entity;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import java.util.Optional;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.registries.Registries;
import net.minecraft.core.registries.codec.RegistryCodecs;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.util.valueproviders.FloatProvider;
import net.minecraft.util.valueproviders.FloatProviders;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.item.Item;

public record SulfurCubeArchetype(HolderSet<Item> items, List<AttributeEntry> attributeModifiers, boolean buoyant, Optional<ExplosionData> explosion, Optional<ContactDamage> contactDamage, KnockbackModifiers knockbackModifiers, SoundSettings soundSettings) {
   public static final Codec<SulfurCubeArchetype> DIRECT_CODEC = RecordCodecBuilder.create((i) -> i.group(RegistryCodecs.holderSet(Registries.ITEM).fieldOf("items").forGetter(SulfurCubeArchetype::items), SulfurCubeArchetype.AttributeEntry.CODEC.listOf().fieldOf("attribute_modifiers").forGetter(SulfurCubeArchetype::attributeModifiers), Codec.BOOL.optionalFieldOf("buoyant", false).forGetter(SulfurCubeArchetype::buoyant), SulfurCubeArchetype.ExplosionData.CODEC.optionalFieldOf("explosion").forGetter(SulfurCubeArchetype::explosion), SulfurCubeArchetype.ContactDamage.CODEC.optionalFieldOf("contact_damage").forGetter(SulfurCubeArchetype::contactDamage), SulfurCubeArchetype.KnockbackModifiers.CODEC.fieldOf("knockback_modifiers").forGetter(SulfurCubeArchetype::knockbackModifiers), SulfurCubeArchetype.SoundSettings.CODEC.fieldOf("sound_settings").forGetter(SulfurCubeArchetype::soundSettings)).apply(i, SulfurCubeArchetype::new));
   public static KnockbackModifiers DEFAULT_KNOCKBACK_MODIFIERS = new KnockbackModifiers(0.33F, 0.06F);
   public static SoundSettings DEFAULT_SOUND_SETTINGS;

   public SulfurCubeArchetype {
      super();
   }

   static {
      DEFAULT_SOUND_SETTINGS = new SoundSettings(SoundEvents.SULFUR_CUBE_REGULAR_HIT, SoundEvents.SULFUR_CUBE_REGULAR_PUSH, 0.2F, 0.5F);
   }

   public static record AttributeEntry(Holder<Attribute> attribute, AttributeModifier modifier) {
      public static final Codec<AttributeEntry> CODEC = RecordCodecBuilder.create((i) -> i.group(Attribute.CODEC.fieldOf("attribute").forGetter(AttributeEntry::attribute), AttributeModifier.MAP_CODEC.forGetter(AttributeEntry::modifier)).apply(i, AttributeEntry::new));

      public AttributeEntry {
         super();
      }

      public static AttributeEntry add(final Holder<Attribute> attribute, final double amount, final ResourceKey<SulfurCubeArchetype> archetype) {
         String var10005 = archetype.identifier().getPath();
         return new AttributeEntry(attribute, new AttributeModifier(Identifier.withDefaultNamespace(var10005 + "_add_" + ((ResourceKey)attribute.unwrapKey().get()).identifier().getPath()), amount, AttributeModifier.Operation.ADD_VALUE));
      }

      public static AttributeEntry multiply(final Holder<Attribute> attribute, final double amount, final ResourceKey<SulfurCubeArchetype> archetype) {
         String var10005 = archetype.identifier().getPath();
         return new AttributeEntry(attribute, new AttributeModifier(Identifier.withDefaultNamespace(var10005 + "_mul_" + ((ResourceKey)attribute.unwrapKey().get()).identifier().getPath()), amount - 1.0, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL));
      }
   }

   public static record ExplosionData(int power, boolean causesFire, int fuse) {
      public static final Codec<ExplosionData> CODEC = RecordCodecBuilder.create((i) -> i.group(ExtraCodecs.NON_NEGATIVE_INT.fieldOf("power").forGetter(ExplosionData::power), Codec.BOOL.fieldOf("causes_fire").forGetter(ExplosionData::causesFire), ExtraCodecs.POSITIVE_INT.fieldOf("fuse").forGetter(ExplosionData::fuse)).apply(i, ExplosionData::new));

      public ExplosionData {
         super();
      }
   }

   public static record ContactDamage(Holder<DamageType> damageType, FloatProvider amount, boolean attributeToSource) {
      public static final Codec<ContactDamage> CODEC = RecordCodecBuilder.create((i) -> i.group(DamageType.CODEC.fieldOf("damage_type").forGetter(ContactDamage::damageType), FloatProviders.codec(0.0F).fieldOf("amount").forGetter(ContactDamage::amount), Codec.BOOL.fieldOf("attribute_to_source").forGetter(ContactDamage::attributeToSource)).apply(i, ContactDamage::new));

      public ContactDamage {
         super();
      }
   }

   public static record KnockbackModifiers(float horizontalPower, float verticalPower) {
      public static final Codec<KnockbackModifiers> CODEC = RecordCodecBuilder.create((i) -> i.group(Codec.FLOAT.fieldOf("horizontal_power").forGetter(KnockbackModifiers::horizontalPower), Codec.FLOAT.fieldOf("vertical_power").forGetter(KnockbackModifiers::verticalPower)).apply(i, KnockbackModifiers::new));

      public KnockbackModifiers {
         super();
      }
   }

   public static record SoundSettings(Holder<SoundEvent> hitSound, Holder<SoundEvent> pushSound, float pushSoundImpulseThreshold, float pushSoundCooldown) {
      public static final Codec<SoundSettings> CODEC = RecordCodecBuilder.create((i) -> i.group(SoundEvent.CODEC.fieldOf("hit_sound").forGetter(SoundSettings::hitSound), SoundEvent.CODEC.fieldOf("push_sound").forGetter(SoundSettings::pushSound), Codec.FLOAT.fieldOf("push_sound_impulse_threshold").forGetter(SoundSettings::pushSoundImpulseThreshold), Codec.FLOAT.fieldOf("push_sound_cooldown").forGetter(SoundSettings::pushSoundCooldown)).apply(i, SoundSettings::new));

      public SoundSettings {
         super();
      }
   }
}
