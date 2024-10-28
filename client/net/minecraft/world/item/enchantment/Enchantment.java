package net.minecraft.world.item.enchantment;

import com.google.common.collect.Maps;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.objects.ObjectArraySet;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;
import net.minecraft.ChatFormatting;
import net.minecraft.Util;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.RegistryCodecs;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.network.chat.ComponentUtils;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.RegistryFixedCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.EnchantmentTags;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.util.Unit;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.effects.EnchantmentAttributeEffect;
import net.minecraft.world.item.enchantment.effects.EnchantmentEntityEffect;
import net.minecraft.world.item.enchantment.effects.EnchantmentLocationBasedEffect;
import net.minecraft.world.item.enchantment.effects.EnchantmentValueEffect;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.phys.Vec3;
import org.apache.commons.lang3.mutable.MutableFloat;

public record Enchantment(Component description, EnchantmentDefinition definition, HolderSet<Enchantment> exclusiveSet, DataComponentMap effects) {
   public static final Codec<Enchantment> DIRECT_CODEC = RecordCodecBuilder.create((var0) -> {
      return var0.group(ComponentSerialization.CODEC.fieldOf("description").forGetter(Enchantment::description), Enchantment.EnchantmentDefinition.CODEC.forGetter(Enchantment::definition), RegistryCodecs.homogeneousList(Registries.ENCHANTMENT).optionalFieldOf("exclusive_set", HolderSet.direct()).forGetter(Enchantment::exclusiveSet), EnchantmentEffectComponents.CODEC.optionalFieldOf("effects", DataComponentMap.EMPTY).forGetter(Enchantment::effects)).apply(var0, Enchantment::new);
   });
   public static final Codec<Holder<Enchantment>> CODEC;

   public Enchantment(Component description, EnchantmentDefinition definition, HolderSet<Enchantment> exclusiveSet, DataComponentMap effects) {
      super();
      this.description = description;
      this.definition = definition;
      this.exclusiveSet = exclusiveSet;
      this.effects = effects;
   }

   public static Cost constantCost(int var0) {
      return new Cost(var0, 0);
   }

   public static Cost dynamicCost(int var0, int var1) {
      return new Cost(var0, var1);
   }

   public static EnchantmentDefinition definition(HolderSet<Item> var0, HolderSet<Item> var1, int var2, int var3, Cost var4, Cost var5, int var6, EquipmentSlotGroup... var7) {
      return new EnchantmentDefinition(var0, Optional.of(var1), var2, var3, var4, var5, var6, List.of(var7));
   }

   public static EnchantmentDefinition definition(HolderSet<Item> var0, int var1, int var2, Cost var3, Cost var4, int var5, EquipmentSlotGroup... var6) {
      return new EnchantmentDefinition(var0, Optional.empty(), var1, var2, var3, var4, var5, List.of(var6));
   }

   public Map<EquipmentSlot, ItemStack> getSlotItems(LivingEntity var1) {
      EnumMap var2 = Maps.newEnumMap(EquipmentSlot.class);
      EquipmentSlot[] var3 = EquipmentSlot.values();
      int var4 = var3.length;

      for(int var5 = 0; var5 < var4; ++var5) {
         EquipmentSlot var6 = var3[var5];
         if (this.matchingSlot(var6)) {
            ItemStack var7 = var1.getItemBySlot(var6);
            if (!var7.isEmpty()) {
               var2.put(var6, var7);
            }
         }
      }

      return var2;
   }

   public HolderSet<Item> getSupportedItems() {
      return this.definition.supportedItems();
   }

   public boolean matchingSlot(EquipmentSlot var1) {
      return this.definition.slots().stream().anyMatch((var1x) -> {
         return var1x.test(var1);
      });
   }

   public boolean isPrimaryItem(ItemStack var1) {
      return this.isSupportedItem(var1) && (this.definition.primaryItems.isEmpty() || var1.is((HolderSet)this.definition.primaryItems.get()));
   }

   public boolean isSupportedItem(ItemStack var1) {
      return var1.is(this.definition.supportedItems);
   }

   public int getWeight() {
      return this.definition.weight();
   }

   public int getAnvilCost() {
      return this.definition.anvilCost();
   }

   public int getMinLevel() {
      return 1;
   }

   public int getMaxLevel() {
      return this.definition.maxLevel();
   }

   public int getMinCost(int var1) {
      return this.definition.minCost().calculate(var1);
   }

   public int getMaxCost(int var1) {
      return this.definition.maxCost().calculate(var1);
   }

   public String toString() {
      return "Enchantment " + this.description.getString();
   }

   public static boolean areCompatible(Holder<Enchantment> var0, Holder<Enchantment> var1) {
      return !var0.equals(var1) && !((Enchantment)var0.value()).exclusiveSet.contains(var1) && !((Enchantment)var1.value()).exclusiveSet.contains(var0);
   }

   public static Component getFullname(Holder<Enchantment> var0, int var1) {
      MutableComponent var2 = ((Enchantment)var0.value()).description.copy();
      if (var0.is(EnchantmentTags.CURSE)) {
         ComponentUtils.mergeStyles(var2, Style.EMPTY.withColor(ChatFormatting.RED));
      } else {
         ComponentUtils.mergeStyles(var2, Style.EMPTY.withColor(ChatFormatting.GRAY));
      }

      if (var1 != 1 || ((Enchantment)var0.value()).getMaxLevel() != 1) {
         var2.append(CommonComponents.SPACE).append((Component)Component.translatable("enchantment.level." + var1));
      }

      return var2;
   }

   public boolean canEnchant(ItemStack var1) {
      return this.definition.supportedItems().contains(var1.getItemHolder());
   }

   public <T> List<T> getEffects(DataComponentType<List<T>> var1) {
      return (List)this.effects.getOrDefault(var1, List.of());
   }

   public boolean isImmuneToDamage(ServerLevel var1, int var2, Entity var3, DamageSource var4) {
      LootContext var5 = damageContext(var1, var2, var3, var4);
      Iterator var6 = this.getEffects(EnchantmentEffectComponents.DAMAGE_IMMUNITY).iterator();

      ConditionalEffect var7;
      do {
         if (!var6.hasNext()) {
            return false;
         }

         var7 = (ConditionalEffect)var6.next();
      } while(!var7.matches(var5));

      return true;
   }

   public void modifyDamageProtection(ServerLevel var1, int var2, ItemStack var3, Entity var4, DamageSource var5, MutableFloat var6) {
      LootContext var7 = damageContext(var1, var2, var4, var5);
      Iterator var8 = this.getEffects(EnchantmentEffectComponents.DAMAGE_PROTECTION).iterator();

      while(var8.hasNext()) {
         ConditionalEffect var9 = (ConditionalEffect)var8.next();
         if (var9.matches(var7)) {
            var6.setValue(((EnchantmentValueEffect)var9.effect()).process(var3, var2, var4.getRandom(), var6.floatValue()));
         }
      }

   }

   public void modifyDurabilityChange(ServerLevel var1, int var2, ItemStack var3, MutableFloat var4) {
      this.modifyItemFilteredCount(EnchantmentEffectComponents.ITEM_DAMAGE, var1, var2, var3, var4);
   }

   public void modifyAmmoCount(ServerLevel var1, int var2, ItemStack var3, MutableFloat var4) {
      this.modifyItemFilteredCount(EnchantmentEffectComponents.AMMO_USE, var1, var2, var3, var4);
   }

   public void modifyPiercingCount(ServerLevel var1, int var2, ItemStack var3, MutableFloat var4) {
      this.modifyItemFilteredCount(EnchantmentEffectComponents.PROJECTILE_PIERCING, var1, var2, var3, var4);
   }

   public void modifyBlockExperience(ServerLevel var1, int var2, ItemStack var3, MutableFloat var4) {
      this.modifyItemFilteredCount(EnchantmentEffectComponents.BLOCK_EXPERIENCE, var1, var2, var3, var4);
   }

   public void modifyMobExperience(ServerLevel var1, int var2, ItemStack var3, Entity var4, MutableFloat var5) {
      this.modifyEntityFilteredValue(EnchantmentEffectComponents.MOB_EXPERIENCE, var1, var2, var3, var4, var5);
   }

   public void modifyDurabilityToRepairFromXp(ServerLevel var1, int var2, ItemStack var3, MutableFloat var4) {
      this.modifyItemFilteredCount(EnchantmentEffectComponents.REPAIR_WITH_XP, var1, var2, var3, var4);
   }

   public void modifyTridentReturnToOwnerAcceleration(ServerLevel var1, int var2, ItemStack var3, Entity var4, MutableFloat var5) {
      this.modifyEntityFilteredValue(EnchantmentEffectComponents.TRIDENT_RETURN_ACCELERATION, var1, var2, var3, var4, var5);
   }

   public void modifyTridentSpinAttackStrength(ServerLevel var1, int var2, ItemStack var3, Entity var4, MutableFloat var5) {
      this.modifyEntityFilteredValue(EnchantmentEffectComponents.TRIDENT_SPIN_ATTACK_STRENGTH, var1, var2, var3, var4, var5);
   }

   public void modifyFishingTimeReduction(ServerLevel var1, int var2, ItemStack var3, Entity var4, MutableFloat var5) {
      this.modifyEntityFilteredValue(EnchantmentEffectComponents.FISHING_TIME_REDUCTION, var1, var2, var3, var4, var5);
   }

   public void modifyFishingLuckBonus(ServerLevel var1, int var2, ItemStack var3, Entity var4, MutableFloat var5) {
      this.modifyEntityFilteredValue(EnchantmentEffectComponents.FISHING_LUCK_BONUS, var1, var2, var3, var4, var5);
   }

   public void modifyDamage(ServerLevel var1, int var2, ItemStack var3, Entity var4, DamageSource var5, MutableFloat var6) {
      this.modifyDamageFilteredValue(EnchantmentEffectComponents.DAMAGE, var1, var2, var3, var4, var5, var6);
   }

   public void modifyFallBasedDamage(ServerLevel var1, int var2, ItemStack var3, Entity var4, DamageSource var5, MutableFloat var6) {
      this.modifyDamageFilteredValue(EnchantmentEffectComponents.SMASH_DAMAGE_PER_FALLEN_BLOCK, var1, var2, var3, var4, var5, var6);
   }

   public void modifyKnockback(ServerLevel var1, int var2, ItemStack var3, Entity var4, DamageSource var5, MutableFloat var6) {
      this.modifyDamageFilteredValue(EnchantmentEffectComponents.KNOCKBACK, var1, var2, var3, var4, var5, var6);
   }

   public void modifyArmorEffectivness(ServerLevel var1, int var2, ItemStack var3, Entity var4, DamageSource var5, MutableFloat var6) {
      this.modifyDamageFilteredValue(EnchantmentEffectComponents.ARMOR_EFFECTIVENESS, var1, var2, var3, var4, var5, var6);
   }

   public static void doPostAttack(TargetedConditionalEffect<EnchantmentEntityEffect> var0, ServerLevel var1, int var2, EnchantedItemInUse var3, Entity var4, DamageSource var5) {
      if (var0.matches(damageContext(var1, var2, var4, var5))) {
         Entity var10000;
         switch (var0.affected()) {
            case ATTACKER -> var10000 = var5.getEntity();
            case DAMAGING_ENTITY -> var10000 = var5.getDirectEntity();
            case VICTIM -> var10000 = var4;
            default -> throw new MatchException((String)null, (Throwable)null);
         }

         Entity var6 = var10000;
         if (var6 != null) {
            ((EnchantmentEntityEffect)var0.effect()).apply(var1, var2, var3, var6, var6.position());
         }
      }

   }

   public void doPostAttack(ServerLevel var1, int var2, EnchantedItemInUse var3, EnchantmentTarget var4, Entity var5, DamageSource var6) {
      Iterator var7 = this.getEffects(EnchantmentEffectComponents.POST_ATTACK).iterator();

      while(var7.hasNext()) {
         TargetedConditionalEffect var8 = (TargetedConditionalEffect)var7.next();
         if (var4 == var8.enchanted()) {
            doPostAttack(var8, var1, var2, var3, var5, var6);
         }
      }

   }

   public void modifyProjectileCount(ServerLevel var1, int var2, ItemStack var3, Entity var4, MutableFloat var5) {
      this.modifyEntityFilteredValue(EnchantmentEffectComponents.PROJECTILE_COUNT, var1, var2, var3, var4, var5);
   }

   public void modifyProjectileSpread(ServerLevel var1, int var2, ItemStack var3, Entity var4, MutableFloat var5) {
      this.modifyEntityFilteredValue(EnchantmentEffectComponents.PROJECTILE_SPREAD, var1, var2, var3, var4, var5);
   }

   public void modifyCrossbowChargeTime(ServerLevel var1, int var2, ItemStack var3, MutableFloat var4) {
      this.modifyItemFilteredCount(EnchantmentEffectComponents.CROSSBOW_CHARGE_TIME, var1, var2, var3, var4);
   }

   public void tick(ServerLevel var1, int var2, EnchantedItemInUse var3, Entity var4) {
      applyEffects(this.getEffects(EnchantmentEffectComponents.TICK), entityContext(var1, var2, var4, var4.position()), (var4x) -> {
         var4x.apply(var1, var2, var3, var4, var4.position());
      });
   }

   public void onProjectileSpawned(ServerLevel var1, int var2, EnchantedItemInUse var3, Entity var4) {
      applyEffects(this.getEffects(EnchantmentEffectComponents.PROJECTILE_SPAWNED), entityContext(var1, var2, var4, var4.position()), (var4x) -> {
         var4x.apply(var1, var2, var3, var4, var4.position());
      });
   }

   public void onHitBlock(ServerLevel var1, int var2, EnchantedItemInUse var3, Entity var4, Vec3 var5) {
      applyEffects(this.getEffects(EnchantmentEffectComponents.HIT_BLOCK), entityContext(var1, var2, var4, var5), (var5x) -> {
         var5x.apply(var1, var2, var3, var4, var5);
      });
   }

   private void modifyItemFilteredCount(DataComponentType<List<ConditionalEffect<EnchantmentValueEffect>>> var1, ServerLevel var2, int var3, ItemStack var4, MutableFloat var5) {
      applyEffects(this.getEffects(var1), itemContext(var2, var3, var4), (var4x) -> {
         var5.setValue(var4x.process(var4, var3, var2.getRandom(), var5.getValue()));
      });
   }

   private void modifyEntityFilteredValue(DataComponentType<List<ConditionalEffect<EnchantmentValueEffect>>> var1, ServerLevel var2, int var3, ItemStack var4, Entity var5, MutableFloat var6) {
      applyEffects(this.getEffects(var1), entityContext(var2, var3, var5, var5.position()), (var4x) -> {
         var6.setValue(var4x.process(var4, var3, var5.getRandom(), var6.floatValue()));
      });
   }

   private void modifyDamageFilteredValue(DataComponentType<List<ConditionalEffect<EnchantmentValueEffect>>> var1, ServerLevel var2, int var3, ItemStack var4, Entity var5, DamageSource var6, MutableFloat var7) {
      applyEffects(this.getEffects(var1), damageContext(var2, var3, var5, var6), (var4x) -> {
         var7.setValue(var4x.process(var4, var3, var5.getRandom(), var7.floatValue()));
      });
   }

   public static LootContext damageContext(ServerLevel var0, int var1, Entity var2, DamageSource var3) {
      LootParams var4 = (new LootParams.Builder(var0)).withParameter(LootContextParams.THIS_ENTITY, var2).withParameter(LootContextParams.ENCHANTMENT_LEVEL, var1).withParameter(LootContextParams.ORIGIN, var2.position()).withParameter(LootContextParams.DAMAGE_SOURCE, var3).withOptionalParameter(LootContextParams.ATTACKING_ENTITY, var3.getEntity()).withOptionalParameter(LootContextParams.DIRECT_ATTACKING_ENTITY, var3.getDirectEntity()).create(LootContextParamSets.ENCHANTED_DAMAGE);
      return (new LootContext.Builder(var4)).create(Optional.empty());
   }

   private static LootContext itemContext(ServerLevel var0, int var1, ItemStack var2) {
      LootParams var3 = (new LootParams.Builder(var0)).withParameter(LootContextParams.TOOL, var2).withParameter(LootContextParams.ENCHANTMENT_LEVEL, var1).create(LootContextParamSets.ENCHANTED_ITEM);
      return (new LootContext.Builder(var3)).create(Optional.empty());
   }

   private static LootContext locationContext(ServerLevel var0, int var1, Entity var2, boolean var3) {
      LootParams var4 = (new LootParams.Builder(var0)).withParameter(LootContextParams.THIS_ENTITY, var2).withParameter(LootContextParams.ENCHANTMENT_LEVEL, var1).withParameter(LootContextParams.ORIGIN, var2.position()).withParameter(LootContextParams.ENCHANTMENT_ACTIVE, var3).create(LootContextParamSets.ENCHANTED_LOCATION);
      return (new LootContext.Builder(var4)).create(Optional.empty());
   }

   private static LootContext entityContext(ServerLevel var0, int var1, Entity var2, Vec3 var3) {
      LootParams var4 = (new LootParams.Builder(var0)).withParameter(LootContextParams.THIS_ENTITY, var2).withParameter(LootContextParams.ENCHANTMENT_LEVEL, var1).withParameter(LootContextParams.ORIGIN, var3).create(LootContextParamSets.ENCHANTED_ENTITY);
      return (new LootContext.Builder(var4)).create(Optional.empty());
   }

   private static <T> void applyEffects(List<ConditionalEffect<T>> var0, LootContext var1, Consumer<T> var2) {
      Iterator var3 = var0.iterator();

      while(var3.hasNext()) {
         ConditionalEffect var4 = (ConditionalEffect)var3.next();
         if (var4.matches(var1)) {
            var2.accept(var4.effect());
         }
      }

   }

   public void runLocationChangedEffects(ServerLevel var1, int var2, EnchantedItemInUse var3, LivingEntity var4) {
      if (var3.inSlot() != null && !this.matchingSlot(var3.inSlot())) {
         Set var10 = (Set)var4.activeLocationDependentEnchantments().remove(this);
         if (var10 != null) {
            var10.forEach((var3x) -> {
               var3x.onDeactivated(var3, var4, var4.position(), var2);
            });
         }

      } else {
         Object var5 = (Set)var4.activeLocationDependentEnchantments().get(this);
         Iterator var6 = this.getEffects(EnchantmentEffectComponents.LOCATION_CHANGED).iterator();

         while(var6.hasNext()) {
            ConditionalEffect var7 = (ConditionalEffect)var6.next();
            EnchantmentLocationBasedEffect var8 = (EnchantmentLocationBasedEffect)var7.effect();
            boolean var9 = var5 != null && ((Set)var5).contains(var8);
            if (var7.matches(locationContext(var1, var2, var4, var9))) {
               if (!var9) {
                  if (var5 == null) {
                     var5 = new ObjectArraySet();
                     var4.activeLocationDependentEnchantments().put(this, var5);
                  }

                  ((Set)var5).add(var8);
               }

               var8.onChangedBlock(var1, var2, var3, var4, var4.position(), !var9);
            } else if (var5 != null && ((Set)var5).remove(var8)) {
               var8.onDeactivated(var3, var4, var4.position(), var2);
            }
         }

         if (var5 != null && ((Set)var5).isEmpty()) {
            var4.activeLocationDependentEnchantments().remove(this);
         }

      }
   }

   public void stopLocationBasedEffects(int var1, EnchantedItemInUse var2, LivingEntity var3) {
      Set var4 = (Set)var3.activeLocationDependentEnchantments().remove(this);
      if (var4 != null) {
         Iterator var5 = var4.iterator();

         while(var5.hasNext()) {
            EnchantmentLocationBasedEffect var6 = (EnchantmentLocationBasedEffect)var5.next();
            var6.onDeactivated(var2, var3, var3.position(), var1);
         }

      }
   }

   public static Builder enchantment(EnchantmentDefinition var0) {
      return new Builder(var0);
   }

   public Component description() {
      return this.description;
   }

   public EnchantmentDefinition definition() {
      return this.definition;
   }

   public HolderSet<Enchantment> exclusiveSet() {
      return this.exclusiveSet;
   }

   public DataComponentMap effects() {
      return this.effects;
   }

   static {
      CODEC = RegistryFixedCodec.create(Registries.ENCHANTMENT);
   }

   public static record EnchantmentDefinition(HolderSet<Item> supportedItems, Optional<HolderSet<Item>> primaryItems, int weight, int maxLevel, Cost minCost, Cost maxCost, int anvilCost, List<EquipmentSlotGroup> slots) {
      final HolderSet<Item> supportedItems;
      final Optional<HolderSet<Item>> primaryItems;
      public static final MapCodec<EnchantmentDefinition> CODEC = RecordCodecBuilder.mapCodec((var0) -> {
         return var0.group(RegistryCodecs.homogeneousList(Registries.ITEM).fieldOf("supported_items").forGetter(EnchantmentDefinition::supportedItems), RegistryCodecs.homogeneousList(Registries.ITEM).optionalFieldOf("primary_items").forGetter(EnchantmentDefinition::primaryItems), ExtraCodecs.POSITIVE_INT.fieldOf("weight").forGetter(EnchantmentDefinition::weight), ExtraCodecs.POSITIVE_INT.fieldOf("max_level").forGetter(EnchantmentDefinition::maxLevel), Enchantment.Cost.CODEC.fieldOf("min_cost").forGetter(EnchantmentDefinition::minCost), Enchantment.Cost.CODEC.fieldOf("max_cost").forGetter(EnchantmentDefinition::maxCost), ExtraCodecs.NON_NEGATIVE_INT.fieldOf("anvil_cost").forGetter(EnchantmentDefinition::anvilCost), EquipmentSlotGroup.CODEC.listOf().fieldOf("slots").forGetter(EnchantmentDefinition::slots)).apply(var0, EnchantmentDefinition::new);
      });

      public EnchantmentDefinition(HolderSet<Item> supportedItems, Optional<HolderSet<Item>> primaryItems, int weight, int maxLevel, Cost minCost, Cost maxCost, int anvilCost, List<EquipmentSlotGroup> slots) {
         super();
         this.supportedItems = supportedItems;
         this.primaryItems = primaryItems;
         this.weight = weight;
         this.maxLevel = maxLevel;
         this.minCost = minCost;
         this.maxCost = maxCost;
         this.anvilCost = anvilCost;
         this.slots = slots;
      }

      public HolderSet<Item> supportedItems() {
         return this.supportedItems;
      }

      public Optional<HolderSet<Item>> primaryItems() {
         return this.primaryItems;
      }

      public int weight() {
         return this.weight;
      }

      public int maxLevel() {
         return this.maxLevel;
      }

      public Cost minCost() {
         return this.minCost;
      }

      public Cost maxCost() {
         return this.maxCost;
      }

      public int anvilCost() {
         return this.anvilCost;
      }

      public List<EquipmentSlotGroup> slots() {
         return this.slots;
      }
   }

   public static record Cost(int base, int perLevelAboveFirst) {
      public static final Codec<Cost> CODEC = RecordCodecBuilder.create((var0) -> {
         return var0.group(Codec.INT.fieldOf("base").forGetter(Cost::base), Codec.INT.fieldOf("per_level_above_first").forGetter(Cost::perLevelAboveFirst)).apply(var0, Cost::new);
      });

      public Cost(int base, int perLevelAboveFirst) {
         super();
         this.base = base;
         this.perLevelAboveFirst = perLevelAboveFirst;
      }

      public int calculate(int var1) {
         return this.base + this.perLevelAboveFirst * (var1 - 1);
      }

      public int base() {
         return this.base;
      }

      public int perLevelAboveFirst() {
         return this.perLevelAboveFirst;
      }
   }

   public static class Builder {
      private final EnchantmentDefinition definition;
      private HolderSet<Enchantment> exclusiveSet = HolderSet.direct();
      private final Map<DataComponentType<?>, List<?>> effectLists = new HashMap();
      private final DataComponentMap.Builder effectMapBuilder = DataComponentMap.builder();

      public Builder(EnchantmentDefinition var1) {
         super();
         this.definition = var1;
      }

      public Builder exclusiveWith(HolderSet<Enchantment> var1) {
         this.exclusiveSet = var1;
         return this;
      }

      public <E> Builder withEffect(DataComponentType<List<ConditionalEffect<E>>> var1, E var2, LootItemCondition.Builder var3) {
         this.getEffectsList(var1).add(new ConditionalEffect(var2, Optional.of(var3.build())));
         return this;
      }

      public <E> Builder withEffect(DataComponentType<List<ConditionalEffect<E>>> var1, E var2) {
         this.getEffectsList(var1).add(new ConditionalEffect(var2, Optional.empty()));
         return this;
      }

      public <E> Builder withEffect(DataComponentType<List<TargetedConditionalEffect<E>>> var1, EnchantmentTarget var2, EnchantmentTarget var3, E var4, LootItemCondition.Builder var5) {
         this.getEffectsList(var1).add(new TargetedConditionalEffect(var2, var3, var4, Optional.of(var5.build())));
         return this;
      }

      public <E> Builder withEffect(DataComponentType<List<TargetedConditionalEffect<E>>> var1, EnchantmentTarget var2, EnchantmentTarget var3, E var4) {
         this.getEffectsList(var1).add(new TargetedConditionalEffect(var2, var3, var4, Optional.empty()));
         return this;
      }

      public Builder withEffect(DataComponentType<List<EnchantmentAttributeEffect>> var1, EnchantmentAttributeEffect var2) {
         this.getEffectsList(var1).add(var2);
         return this;
      }

      public <E> Builder withSpecialEffect(DataComponentType<E> var1, E var2) {
         this.effectMapBuilder.set(var1, var2);
         return this;
      }

      public Builder withEffect(DataComponentType<Unit> var1) {
         this.effectMapBuilder.set(var1, Unit.INSTANCE);
         return this;
      }

      private <E> List<E> getEffectsList(DataComponentType<List<E>> var1) {
         return (List)this.effectLists.computeIfAbsent(var1, (var2) -> {
            ArrayList var3 = new ArrayList();
            this.effectMapBuilder.set(var1, var3);
            return var3;
         });
      }

      public Enchantment build(ResourceLocation var1) {
         return new Enchantment(Component.translatable(Util.makeDescriptionId("enchantment", var1)), this.definition, this.exclusiveSet, this.effectMapBuilder.build());
      }
   }
}
