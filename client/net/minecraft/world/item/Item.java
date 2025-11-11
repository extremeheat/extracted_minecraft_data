package net.minecraft.world.item;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.Maps;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;
import net.minecraft.SharedConstants;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.HolderSet;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.DependantName;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.tags.EntityTypeTags;
import net.minecraft.tags.TagKey;
import net.minecraft.util.Mth;
import net.minecraft.util.Util;
import net.minecraft.world.Difficulty;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.SlotAccess;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.flag.FeatureElement;
import net.minecraft.world.flag.FeatureFlag;
import net.minecraft.world.flag.FeatureFlagSet;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.inventory.ClickAction;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.component.Consumable;
import net.minecraft.world.item.component.Consumables;
import net.minecraft.world.item.component.DamageResistant;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.minecraft.world.item.component.KineticWeapon;
import net.minecraft.world.item.component.PiercingWeapon;
import net.minecraft.world.item.component.ProvidesTrimMaterial;
import net.minecraft.world.item.component.SwingAnimation;
import net.minecraft.world.item.component.Tool;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.item.component.TypedEntityData;
import net.minecraft.world.item.component.UseCooldown;
import net.minecraft.world.item.component.UseEffects;
import net.minecraft.world.item.component.UseRemainder;
import net.minecraft.world.item.component.Weapon;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.item.enchantment.Enchantable;
import net.minecraft.world.item.enchantment.Repairable;
import net.minecraft.world.item.equipment.ArmorMaterial;
import net.minecraft.world.item.equipment.ArmorType;
import net.minecraft.world.item.equipment.Equippable;
import net.minecraft.world.item.equipment.trim.TrimMaterial;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.saveddata.maps.MapId;
import net.minecraft.world.level.saveddata.maps.MapItemSavedData;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import org.jspecify.annotations.Nullable;
import org.slf4j.Logger;

public class Item implements FeatureElement, ItemLike {
   public static final Codec<Holder<Item>> CODEC;
   public static final StreamCodec<RegistryFriendlyByteBuf, Holder<Item>> STREAM_CODEC;
   private static final Logger LOGGER;
   public static final Map<Block, Item> BY_BLOCK;
   public static final Identifier BASE_ATTACK_DAMAGE_ID;
   public static final Identifier BASE_ATTACK_SPEED_ID;
   public static final int DEFAULT_MAX_STACK_SIZE = 64;
   public static final int ABSOLUTE_MAX_STACK_SIZE = 99;
   public static final int MAX_BAR_WIDTH = 13;
   protected static final int APPROXIMATELY_INFINITE_USE_DURATION = 72000;
   private final Holder.Reference<Item> builtInRegistryHolder;
   private final DataComponentMap components;
   private final @Nullable Item craftingRemainingItem;
   protected final String descriptionId;
   private final FeatureFlagSet requiredFeatures;

   public static int getId(Item var0) {
      return var0 == null ? 0 : BuiltInRegistries.ITEM.getId(var0);
   }

   public static Item byId(int var0) {
      return BuiltInRegistries.ITEM.byId(var0);
   }

   /** @deprecated */
   @Deprecated
   public static Item byBlock(Block var0) {
      return (Item)BY_BLOCK.getOrDefault(var0, Items.AIR);
   }

   public Item(Properties var1) {
      super();
      this.builtInRegistryHolder = BuiltInRegistries.ITEM.createIntrusiveHolder(this);
      this.descriptionId = var1.effectiveDescriptionId();
      this.components = var1.buildAndValidateComponents(Component.translatable(this.descriptionId), var1.effectiveModel());
      this.craftingRemainingItem = var1.craftingRemainingItem;
      this.requiredFeatures = var1.requiredFeatures;
      if (SharedConstants.IS_RUNNING_IN_IDE) {
         String var2 = this.getClass().getSimpleName();
         if (!var2.endsWith("Item")) {
            LOGGER.error("Item classes should end with Item and {} doesn't.", var2);
         }
      }

   }

   /** @deprecated */
   @Deprecated
   public Holder.Reference<Item> builtInRegistryHolder() {
      return this.builtInRegistryHolder;
   }

   public DataComponentMap components() {
      return this.components;
   }

   public int getDefaultMaxStackSize() {
      return (Integer)this.components.getOrDefault(DataComponents.MAX_STACK_SIZE, 1);
   }

   public void onUseTick(Level var1, LivingEntity var2, ItemStack var3, int var4) {
   }

   public void onDestroyed(ItemEntity var1) {
   }

   public boolean canDestroyBlock(ItemStack var1, BlockState var2, Level var3, BlockPos var4, LivingEntity var5) {
      Tool var6 = (Tool)var1.get(DataComponents.TOOL);
      if (var6 != null && !var6.canDestroyBlocksInCreative()) {
         boolean var10000;
         if (var5 instanceof Player) {
            Player var7 = (Player)var5;
            if (var7.getAbilities().instabuild) {
               var10000 = false;
               return var10000;
            }
         }

         var10000 = true;
         return var10000;
      } else {
         return true;
      }
   }

   public Item asItem() {
      return this;
   }

   public InteractionResult useOn(UseOnContext var1) {
      return InteractionResult.PASS;
   }

   public float getDestroySpeed(ItemStack var1, BlockState var2) {
      Tool var3 = (Tool)var1.get(DataComponents.TOOL);
      return var3 != null ? var3.getMiningSpeed(var2) : 1.0F;
   }

   public InteractionResult use(Level var1, Player var2, InteractionHand var3) {
      ItemStack var4 = var2.getItemInHand(var3);
      Consumable var5 = (Consumable)var4.get(DataComponents.CONSUMABLE);
      if (var5 != null) {
         return var5.startConsuming(var2, var4, var3);
      } else {
         Equippable var6 = (Equippable)var4.get(DataComponents.EQUIPPABLE);
         if (var6 != null && var6.swappable()) {
            return var6.swapWithEquipmentSlot(var4, var2);
         } else if (var4.has(DataComponents.BLOCKS_ATTACKS)) {
            var2.startUsingItem(var3);
            return InteractionResult.CONSUME;
         } else {
            KineticWeapon var7 = (KineticWeapon)var4.get(DataComponents.KINETIC_WEAPON);
            if (var7 != null) {
               var2.startUsingItem(var3);
               var7.makeSound(var2);
               return InteractionResult.CONSUME;
            } else {
               return InteractionResult.PASS;
            }
         }
      }
   }

   public ItemStack finishUsingItem(ItemStack var1, Level var2, LivingEntity var3) {
      Consumable var4 = (Consumable)var1.get(DataComponents.CONSUMABLE);
      return var4 != null ? var4.onConsume(var2, var3, var1) : var1;
   }

   public boolean isBarVisible(ItemStack var1) {
      return var1.isDamaged();
   }

   public int getBarWidth(ItemStack var1) {
      return Mth.clamp(Math.round(13.0F - (float)var1.getDamageValue() * 13.0F / (float)var1.getMaxDamage()), 0, 13);
   }

   public int getBarColor(ItemStack var1) {
      int var2 = var1.getMaxDamage();
      float var3 = Math.max(0.0F, ((float)var2 - (float)var1.getDamageValue()) / (float)var2);
      return Mth.hsvToRgb(var3 / 3.0F, 1.0F, 1.0F);
   }

   public boolean overrideStackedOnOther(ItemStack var1, Slot var2, ClickAction var3, Player var4) {
      return false;
   }

   public boolean overrideOtherStackedOnMe(ItemStack var1, ItemStack var2, Slot var3, ClickAction var4, Player var5, SlotAccess var6) {
      return false;
   }

   public float getAttackDamageBonus(Entity var1, float var2, DamageSource var3) {
      return 0.0F;
   }

   /** @deprecated */
   @Deprecated
   public @Nullable DamageSource getItemDamageSource(LivingEntity var1) {
      return null;
   }

   public void hurtEnemy(ItemStack var1, LivingEntity var2, LivingEntity var3) {
   }

   public void postHurtEnemy(ItemStack var1, LivingEntity var2, LivingEntity var3) {
   }

   public boolean mineBlock(ItemStack var1, Level var2, BlockState var3, BlockPos var4, LivingEntity var5) {
      Tool var6 = (Tool)var1.get(DataComponents.TOOL);
      if (var6 == null) {
         return false;
      } else {
         if (!var2.isClientSide() && var3.getDestroySpeed(var2, var4) != 0.0F && var6.damagePerBlock() > 0) {
            var1.hurtAndBreak(var6.damagePerBlock(), var5, EquipmentSlot.MAINHAND);
         }

         return true;
      }
   }

   public boolean isCorrectToolForDrops(ItemStack var1, BlockState var2) {
      Tool var3 = (Tool)var1.get(DataComponents.TOOL);
      return var3 != null && var3.isCorrectForDrops(var2);
   }

   public InteractionResult interactLivingEntity(ItemStack var1, Player var2, LivingEntity var3, InteractionHand var4) {
      return InteractionResult.PASS;
   }

   public String toString() {
      return BuiltInRegistries.ITEM.wrapAsHolder(this).getRegisteredName();
   }

   public final ItemStack getCraftingRemainder() {
      return this.craftingRemainingItem == null ? ItemStack.EMPTY : new ItemStack(this.craftingRemainingItem);
   }

   public void inventoryTick(ItemStack var1, ServerLevel var2, Entity var3, @Nullable EquipmentSlot var4) {
   }

   public void onCraftedBy(ItemStack var1, Player var2) {
      this.onCraftedPostProcess(var1, var2.level());
   }

   public void onCraftedPostProcess(ItemStack var1, Level var2) {
   }

   public ItemUseAnimation getUseAnimation(ItemStack var1) {
      Consumable var2 = (Consumable)var1.get(DataComponents.CONSUMABLE);
      if (var2 != null) {
         return var2.animation();
      } else if (var1.has(DataComponents.BLOCKS_ATTACKS)) {
         return ItemUseAnimation.BLOCK;
      } else {
         return var1.has(DataComponents.KINETIC_WEAPON) ? ItemUseAnimation.SPEAR : ItemUseAnimation.NONE;
      }
   }

   public int getUseDuration(ItemStack var1, LivingEntity var2) {
      Consumable var3 = (Consumable)var1.get(DataComponents.CONSUMABLE);
      if (var3 != null) {
         return var3.consumeTicks();
      } else {
         return !var1.has(DataComponents.BLOCKS_ATTACKS) && !var1.has(DataComponents.KINETIC_WEAPON) ? 0 : 72000;
      }
   }

   public boolean releaseUsing(ItemStack var1, Level var2, LivingEntity var3, int var4) {
      return false;
   }

   /** @deprecated */
   @Deprecated
   public void appendHoverText(ItemStack var1, TooltipContext var2, TooltipDisplay var3, Consumer<Component> var4, TooltipFlag var5) {
   }

   public Optional<TooltipComponent> getTooltipImage(ItemStack var1) {
      return Optional.empty();
   }

   @VisibleForTesting
   public final String getDescriptionId() {
      return this.descriptionId;
   }

   public final Component getName() {
      return (Component)this.components.getOrDefault(DataComponents.ITEM_NAME, CommonComponents.EMPTY);
   }

   public Component getName(ItemStack var1) {
      return (Component)var1.getComponents().getOrDefault(DataComponents.ITEM_NAME, CommonComponents.EMPTY);
   }

   public boolean isFoil(ItemStack var1) {
      return var1.isEnchanted();
   }

   protected static BlockHitResult getPlayerPOVHitResult(Level var0, Player var1, ClipContext.Fluid var2) {
      Vec3 var3 = var1.getEyePosition();
      Vec3 var4 = var3.add(var1.calculateViewVector(var1.getXRot(), var1.getYRot()).scale(var1.blockInteractionRange()));
      return var0.clip(new ClipContext(var3, var4, ClipContext.Block.OUTLINE, var2, var1));
   }

   public boolean useOnRelease(ItemStack var1) {
      return false;
   }

   public ItemStack getDefaultInstance() {
      return new ItemStack(this);
   }

   public boolean canFitInsideContainerItems() {
      return true;
   }

   public FeatureFlagSet requiredFeatures() {
      return this.requiredFeatures;
   }

   public boolean shouldPrintOpWarning(ItemStack var1, @Nullable Player var2) {
      return false;
   }

   static {
      CODEC = BuiltInRegistries.ITEM.holderByNameCodec().validate((var0) -> var0.is((Holder)Items.AIR.builtInRegistryHolder()) ? DataResult.error(() -> "Item must not be minecraft:air") : DataResult.success(var0));
      STREAM_CODEC = ByteBufCodecs.holderRegistry(Registries.ITEM);
      LOGGER = LogUtils.getLogger();
      BY_BLOCK = Maps.newHashMap();
      BASE_ATTACK_DAMAGE_ID = Identifier.withDefaultNamespace("base_attack_damage");
      BASE_ATTACK_SPEED_ID = Identifier.withDefaultNamespace("base_attack_speed");
   }

   public static class Properties {
      private static final DependantName<Item, String> BLOCK_DESCRIPTION_ID = (var0) -> Util.makeDescriptionId("block", var0.identifier());
      private static final DependantName<Item, String> ITEM_DESCRIPTION_ID = (var0) -> Util.makeDescriptionId("item", var0.identifier());
      private final DataComponentMap.Builder components;
      @Nullable Item craftingRemainingItem;
      FeatureFlagSet requiredFeatures;
      private @Nullable ResourceKey<Item> id;
      private DependantName<Item, String> descriptionId;
      private final DependantName<Item, Identifier> model;

      public Properties() {
         super();
         this.components = DataComponentMap.builder().addAll(DataComponents.COMMON_ITEM_COMPONENTS);
         this.requiredFeatures = FeatureFlags.VANILLA_SET;
         this.descriptionId = ITEM_DESCRIPTION_ID;
         this.model = ResourceKey::identifier;
      }

      public Properties food(FoodProperties var1) {
         return this.food(var1, Consumables.DEFAULT_FOOD);
      }

      public Properties food(FoodProperties var1, Consumable var2) {
         return this.component(DataComponents.FOOD, var1).component(DataComponents.CONSUMABLE, var2);
      }

      public Properties usingConvertsTo(Item var1) {
         return this.component(DataComponents.USE_REMAINDER, new UseRemainder(new ItemStack(var1)));
      }

      public Properties useCooldown(float var1) {
         return this.component(DataComponents.USE_COOLDOWN, new UseCooldown(var1));
      }

      public Properties stacksTo(int var1) {
         return this.component(DataComponents.MAX_STACK_SIZE, var1);
      }

      public Properties durability(int var1) {
         this.component(DataComponents.MAX_DAMAGE, var1);
         this.component(DataComponents.MAX_STACK_SIZE, 1);
         this.component(DataComponents.DAMAGE, 0);
         return this;
      }

      public Properties craftRemainder(Item var1) {
         this.craftingRemainingItem = var1;
         return this;
      }

      public Properties rarity(Rarity var1) {
         return this.component(DataComponents.RARITY, var1);
      }

      public Properties fireResistant() {
         return this.component(DataComponents.DAMAGE_RESISTANT, new DamageResistant(DamageTypeTags.IS_FIRE));
      }

      public Properties jukeboxPlayable(ResourceKey<JukeboxSong> var1) {
         return this.component(DataComponents.JUKEBOX_PLAYABLE, new JukeboxPlayable(new EitherHolder(var1)));
      }

      public Properties enchantable(int var1) {
         return this.component(DataComponents.ENCHANTABLE, new Enchantable(var1));
      }

      public Properties repairable(Item var1) {
         return this.component(DataComponents.REPAIRABLE, new Repairable(HolderSet.direct(var1.builtInRegistryHolder())));
      }

      public Properties repairable(TagKey<Item> var1) {
         HolderGetter var2 = BuiltInRegistries.acquireBootstrapRegistrationLookup(BuiltInRegistries.ITEM);
         return this.component(DataComponents.REPAIRABLE, new Repairable(var2.getOrThrow(var1)));
      }

      public Properties equippable(EquipmentSlot var1) {
         return this.component(DataComponents.EQUIPPABLE, Equippable.builder(var1).build());
      }

      public Properties equippableUnswappable(EquipmentSlot var1) {
         return this.component(DataComponents.EQUIPPABLE, Equippable.builder(var1).setSwappable(false).build());
      }

      public Properties tool(ToolMaterial var1, TagKey<Block> var2, float var3, float var4, float var5) {
         return var1.applyToolProperties(this, var2, var3, var4, var5);
      }

      public Properties pickaxe(ToolMaterial var1, float var2, float var3) {
         return this.tool(var1, BlockTags.MINEABLE_WITH_PICKAXE, var2, var3, 0.0F);
      }

      public Properties axe(ToolMaterial var1, float var2, float var3) {
         return this.tool(var1, BlockTags.MINEABLE_WITH_AXE, var2, var3, 5.0F);
      }

      public Properties hoe(ToolMaterial var1, float var2, float var3) {
         return this.tool(var1, BlockTags.MINEABLE_WITH_HOE, var2, var3, 0.0F);
      }

      public Properties shovel(ToolMaterial var1, float var2, float var3) {
         return this.tool(var1, BlockTags.MINEABLE_WITH_SHOVEL, var2, var3, 0.0F);
      }

      public Properties sword(ToolMaterial var1, float var2, float var3) {
         return var1.applySwordProperties(this, var2, var3);
      }

      public Properties spear(ToolMaterial var1, float var2, float var3, float var4, float var5, float var6, float var7, float var8, float var9, float var10) {
         return this.durability(var1.durability()).repairable(var1.repairItems()).enchantable(var1.enchantmentValue()).component(DataComponents.DAMAGE_TYPE, new EitherHolder(DamageTypes.SPEAR)).component(DataComponents.KINETIC_WEAPON, new KineticWeapon(2.0F, 4.5F, 0.125F, 10, (int)(var4 * 20.0F), KineticWeapon.Condition.ofAttackerSpeed((int)(var5 * 20.0F), var6), KineticWeapon.Condition.ofAttackerSpeed((int)(var7 * 20.0F), var8), KineticWeapon.Condition.ofRelativeSpeed((int)(var9 * 20.0F), var10), 0.38F, var3, Optional.of(var1 == ToolMaterial.WOOD ? SoundEvents.SPEAR_WOOD_USE : SoundEvents.SPEAR_USE), Optional.of(var1 == ToolMaterial.WOOD ? SoundEvents.SPEAR_WOOD_HIT : SoundEvents.SPEAR_HIT))).component(DataComponents.PIERCING_WEAPON, new PiercingWeapon(2.0F, 4.5F, 0.25F, true, false, Optional.of(var1 == ToolMaterial.WOOD ? SoundEvents.SPEAR_WOOD_ATTACK : SoundEvents.SPEAR_ATTACK), Optional.of(var1 == ToolMaterial.WOOD ? SoundEvents.SPEAR_WOOD_HIT : SoundEvents.SPEAR_HIT))).component(DataComponents.MINIMUM_ATTACK_CHARGE, 1.0F).component(DataComponents.SWING_ANIMATION, new SwingAnimation(SwingAnimationType.STAB, (int)(var2 * 20.0F))).attributes(ItemAttributeModifiers.builder().add(Attributes.ATTACK_DAMAGE, new AttributeModifier(Item.BASE_ATTACK_DAMAGE_ID, (double)(0.0F + var1.attackDamageBonus()), AttributeModifier.Operation.ADD_VALUE), EquipmentSlotGroup.MAINHAND).add(Attributes.ATTACK_SPEED, new AttributeModifier(Item.BASE_ATTACK_SPEED_ID, (double)(1.0F / var2) - 4.0, AttributeModifier.Operation.ADD_VALUE), EquipmentSlotGroup.MAINHAND).build()).component(DataComponents.USE_EFFECTS, new UseEffects(true, false, 1.0F)).component(DataComponents.WEAPON, new Weapon(1));
      }

      public Properties spawnEgg(EntityType<?> var1) {
         return this.component(DataComponents.ENTITY_DATA, TypedEntityData.of(var1, new CompoundTag()));
      }

      public Properties humanoidArmor(ArmorMaterial var1, ArmorType var2) {
         return this.durability(var2.getDurability(var1.durability())).attributes(var1.createAttributes(var2)).enchantable(var1.enchantmentValue()).component(DataComponents.EQUIPPABLE, Equippable.builder(var2.getSlot()).setEquipSound(var1.equipSound()).setAsset(var1.assetId()).build()).repairable(var1.repairIngredient());
      }

      public Properties wolfArmor(ArmorMaterial var1) {
         return this.durability(ArmorType.BODY.getDurability(var1.durability())).attributes(var1.createAttributes(ArmorType.BODY)).repairable(var1.repairIngredient()).component(DataComponents.EQUIPPABLE, Equippable.builder(EquipmentSlot.BODY).setEquipSound(var1.equipSound()).setAsset(var1.assetId()).setAllowedEntities(HolderSet.direct(EntityType.WOLF.builtInRegistryHolder())).setCanBeSheared(true).setShearingSound(BuiltInRegistries.SOUND_EVENT.wrapAsHolder(SoundEvents.ARMOR_UNEQUIP_WOLF)).build()).component(DataComponents.BREAK_SOUND, SoundEvents.WOLF_ARMOR_BREAK).stacksTo(1);
      }

      public Properties horseArmor(ArmorMaterial var1) {
         HolderGetter var2 = BuiltInRegistries.acquireBootstrapRegistrationLookup(BuiltInRegistries.ENTITY_TYPE);
         return this.attributes(var1.createAttributes(ArmorType.BODY)).component(DataComponents.EQUIPPABLE, Equippable.builder(EquipmentSlot.BODY).setEquipSound(SoundEvents.HORSE_ARMOR).setAsset(var1.assetId()).setAllowedEntities(var2.getOrThrow(EntityTypeTags.CAN_WEAR_HORSE_ARMOR)).setDamageOnHurt(false).setCanBeSheared(true).setShearingSound(SoundEvents.HORSE_ARMOR_UNEQUIP).build()).stacksTo(1);
      }

      public Properties nautilusArmor(ArmorMaterial var1) {
         HolderGetter var2 = BuiltInRegistries.acquireBootstrapRegistrationLookup(BuiltInRegistries.ENTITY_TYPE);
         return this.attributes(var1.createAttributes(ArmorType.BODY)).component(DataComponents.EQUIPPABLE, Equippable.builder(EquipmentSlot.BODY).setEquipSound(SoundEvents.ARMOR_EQUIP_NAUTILUS).setAsset(var1.assetId()).setAllowedEntities(var2.getOrThrow(EntityTypeTags.CAN_WEAR_NAUTILUS_ARMOR)).setDamageOnHurt(false).setEquipOnInteract(true).setCanBeSheared(true).setShearingSound(SoundEvents.ARMOR_UNEQUIP_NAUTILUS).build()).stacksTo(1);
      }

      public Properties trimMaterial(ResourceKey<TrimMaterial> var1) {
         return this.component(DataComponents.PROVIDES_TRIM_MATERIAL, new ProvidesTrimMaterial(var1));
      }

      public Properties requiredFeatures(FeatureFlag... var1) {
         this.requiredFeatures = FeatureFlags.REGISTRY.subset(var1);
         return this;
      }

      public Properties setId(ResourceKey<Item> var1) {
         this.id = var1;
         return this;
      }

      public Properties overrideDescription(String var1) {
         this.descriptionId = DependantName.<Item, String>fixed(var1);
         return this;
      }

      public Properties useBlockDescriptionPrefix() {
         this.descriptionId = BLOCK_DESCRIPTION_ID;
         return this;
      }

      public Properties useItemDescriptionPrefix() {
         this.descriptionId = ITEM_DESCRIPTION_ID;
         return this;
      }

      protected String effectiveDescriptionId() {
         return this.descriptionId.get((ResourceKey)Objects.requireNonNull(this.id, "Item id not set"));
      }

      public Identifier effectiveModel() {
         return this.model.get((ResourceKey)Objects.requireNonNull(this.id, "Item id not set"));
      }

      public <T> Properties component(DataComponentType<T> var1, T var2) {
         this.components.set(var1, var2);
         return this;
      }

      public Properties attributes(ItemAttributeModifiers var1) {
         return this.component(DataComponents.ATTRIBUTE_MODIFIERS, var1);
      }

      DataComponentMap buildAndValidateComponents(Component var1, Identifier var2) {
         DataComponentMap var3 = this.components.set(DataComponents.ITEM_NAME, var1).set(DataComponents.ITEM_MODEL, var2).build();
         if (var3.has(DataComponents.DAMAGE) && (Integer)var3.getOrDefault(DataComponents.MAX_STACK_SIZE, 1) > 1) {
            throw new IllegalStateException("Item cannot have both durability and be stackable");
         } else {
            return var3;
         }
      }
   }

   public interface TooltipContext {
      TooltipContext EMPTY = new TooltipContext() {
         public HolderLookup.@Nullable Provider registries() {
            return null;
         }

         public float tickRate() {
            return 20.0F;
         }

         public @Nullable MapItemSavedData mapData(MapId var1) {
            return null;
         }

         public boolean isPeaceful() {
            return false;
         }
      };

      HolderLookup.@Nullable Provider registries();

      float tickRate();

      @Nullable MapItemSavedData mapData(MapId var1);

      boolean isPeaceful();

      static TooltipContext of(final @Nullable Level var0) {
         return var0 == null ? EMPTY : new TooltipContext() {
            public HolderLookup.Provider registries() {
               return var0.registryAccess();
            }

            public float tickRate() {
               return var0.tickRateManager().tickrate();
            }

            public MapItemSavedData mapData(MapId var1) {
               return var0.getMapData(var1);
            }

            public boolean isPeaceful() {
               return var0.getDifficulty() == Difficulty.PEACEFUL;
            }
         };
      }

      static TooltipContext of(final HolderLookup.Provider var0) {
         return new TooltipContext() {
            public HolderLookup.Provider registries() {
               return var0;
            }

            public float tickRate() {
               return 20.0F;
            }

            public @Nullable MapItemSavedData mapData(MapId var1) {
               return null;
            }

            public boolean isPeaceful() {
               return false;
            }
         };
      }
   }
}
