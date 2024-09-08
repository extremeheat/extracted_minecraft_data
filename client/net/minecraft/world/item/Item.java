package net.minecraft.world.item;

import com.google.common.collect.Maps;
import com.mojang.logging.LogUtils;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import javax.annotation.Nullable;
import net.minecraft.SharedConstants;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.HolderSet;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.DependantName;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.TagKey;
import net.minecraft.util.Mth;
import net.minecraft.util.Unit;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.SlotAccess;
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
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.minecraft.world.item.component.Tool;
import net.minecraft.world.item.component.UseCooldown;
import net.minecraft.world.item.component.UseRemainder;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.item.enchantment.Enchantable;
import net.minecraft.world.item.enchantment.Repairable;
import net.minecraft.world.item.equipment.Equippable;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.saveddata.maps.MapId;
import net.minecraft.world.level.saveddata.maps.MapItemSavedData;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import org.slf4j.Logger;

public class Item implements FeatureElement, ItemLike {
   private static final Logger LOGGER = LogUtils.getLogger();
   public static final Map<Block, Item> BY_BLOCK = Maps.newHashMap();
   public static final ResourceLocation BASE_ATTACK_DAMAGE_ID = ResourceLocation.withDefaultNamespace("base_attack_damage");
   public static final ResourceLocation BASE_ATTACK_SPEED_ID = ResourceLocation.withDefaultNamespace("base_attack_speed");
   public static final int DEFAULT_MAX_STACK_SIZE = 64;
   public static final int ABSOLUTE_MAX_STACK_SIZE = 99;
   public static final int MAX_BAR_WIDTH = 13;
   private final Holder.Reference<Item> builtInRegistryHolder = BuiltInRegistries.ITEM.createIntrusiveHolder(this);
   private final DataComponentMap components;
   @Nullable
   private final Item craftingRemainingItem;
   protected final String descriptionId;
   private final FeatureFlagSet requiredFeatures;

   public static int getId(Item var0) {
      return var0 == null ? 0 : BuiltInRegistries.ITEM.getId(var0);
   }

   public static Item byId(int var0) {
      return BuiltInRegistries.ITEM.byId(var0);
   }

   @Deprecated
   public static Item byBlock(Block var0) {
      return BY_BLOCK.getOrDefault(var0, Items.AIR);
   }

   public Item(Item.Properties var1) {
      super();
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

   @Deprecated
   public Holder.Reference<Item> builtInRegistryHolder() {
      return this.builtInRegistryHolder;
   }

   public DataComponentMap components() {
      return this.components;
   }

   public int getDefaultMaxStackSize() {
      return this.components.getOrDefault(DataComponents.MAX_STACK_SIZE, 1);
   }

   public void onUseTick(Level var1, LivingEntity var2, ItemStack var3, int var4) {
   }

   public void onDestroyed(ItemEntity var1) {
   }

   public void verifyComponentsAfterLoad(ItemStack var1) {
   }

   public boolean canAttackBlock(BlockState var1, Level var2, BlockPos var3, Player var4) {
      return true;
   }

   @Override
   public Item asItem() {
      return this;
   }

   public InteractionResult useOn(UseOnContext var1) {
      return InteractionResult.PASS;
   }

   public float getDestroySpeed(ItemStack var1, BlockState var2) {
      Tool var3 = var1.get(DataComponents.TOOL);
      return var3 != null ? var3.getMiningSpeed(var2) : 1.0F;
   }

   public InteractionResult use(Level var1, Player var2, InteractionHand var3) {
      ItemStack var4 = var2.getItemInHand(var3);
      Consumable var5 = var4.get(DataComponents.CONSUMABLE);
      if (var5 != null) {
         return var5.startConsuming(var2, var4, var3);
      } else {
         Equippable var6 = var4.get(DataComponents.EQUIPPABLE);
         return (InteractionResult)(var6 != null ? var6.swapWithEquipmentSlot(var4, var2) : InteractionResult.PASS);
      }
   }

   public ItemStack finishUsingItem(ItemStack var1, Level var2, LivingEntity var3) {
      Consumable var4 = var1.get(DataComponents.CONSUMABLE);
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

   public boolean hurtEnemy(ItemStack var1, LivingEntity var2, LivingEntity var3) {
      return false;
   }

   public void postHurtEnemy(ItemStack var1, LivingEntity var2, LivingEntity var3) {
   }

   public boolean mineBlock(ItemStack var1, Level var2, BlockState var3, BlockPos var4, LivingEntity var5) {
      Tool var6 = var1.get(DataComponents.TOOL);
      if (var6 == null) {
         return false;
      } else {
         if (!var2.isClientSide && var3.getDestroySpeed(var2, var4) != 0.0F && var6.damagePerBlock() > 0) {
            var1.hurtAndBreak(var6.damagePerBlock(), var5, EquipmentSlot.MAINHAND);
         }

         return true;
      }
   }

   public boolean isCorrectToolForDrops(ItemStack var1, BlockState var2) {
      Tool var3 = var1.get(DataComponents.TOOL);
      return var3 != null && var3.isCorrectForDrops(var2);
   }

   public InteractionResult interactLivingEntity(ItemStack var1, Player var2, LivingEntity var3, InteractionHand var4) {
      return InteractionResult.PASS;
   }

   @Override
   public String toString() {
      return BuiltInRegistries.ITEM.wrapAsHolder(this).getRegisteredName();
   }

   @Nullable
   public final Item getCraftingRemainingItem() {
      return this.craftingRemainingItem;
   }

   public boolean hasCraftingRemainingItem() {
      return this.craftingRemainingItem != null;
   }

   public void inventoryTick(ItemStack var1, Level var2, Entity var3, int var4, boolean var5) {
   }

   public void onCraftedBy(ItemStack var1, Level var2, Player var3) {
      this.onCraftedPostProcess(var1, var2);
   }

   public void onCraftedPostProcess(ItemStack var1, Level var2) {
   }

   public boolean isComplex() {
      return false;
   }

   public ItemUseAnimation getUseAnimation(ItemStack var1) {
      Consumable var2 = var1.get(DataComponents.CONSUMABLE);
      return var2 != null ? var2.animation() : ItemUseAnimation.NONE;
   }

   public int getUseDuration(ItemStack var1, LivingEntity var2) {
      Consumable var3 = var1.get(DataComponents.CONSUMABLE);
      return var3 != null ? var3.consumeTicks() : 0;
   }

   public boolean releaseUsing(ItemStack var1, Level var2, LivingEntity var3, int var4) {
      return false;
   }

   public void appendHoverText(ItemStack var1, Item.TooltipContext var2, List<Component> var3, TooltipFlag var4) {
   }

   public Optional<TooltipComponent> getTooltipImage(ItemStack var1) {
      return Optional.empty();
   }

   public final String getDescriptionId() {
      return this.descriptionId;
   }

   public final Component getName() {
      return this.components.getOrDefault(DataComponents.ITEM_NAME, CommonComponents.EMPTY);
   }

   public Component getName(ItemStack var1) {
      return var1.getComponents().getOrDefault(DataComponents.ITEM_NAME, CommonComponents.EMPTY);
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

   public SoundEvent getBreakingSound() {
      return SoundEvents.ITEM_BREAK;
   }

   public boolean canFitInsideContainerItems() {
      return true;
   }

   @Override
   public FeatureFlagSet requiredFeatures() {
      return this.requiredFeatures;
   }

   public static class Properties {
      private static final DependantName<Item, String> BLOCK_DESCRIPTION_ID = var0 -> Util.makeDescriptionId("block", var0.location());
      private static final DependantName<Item, String> ITEM_DESCRIPTION_ID = var0 -> Util.makeDescriptionId("item", var0.location());
      private final DataComponentMap.Builder components = DataComponentMap.builder().addAll(DataComponents.COMMON_ITEM_COMPONENTS);
      @Nullable
      Item craftingRemainingItem;
      FeatureFlagSet requiredFeatures = FeatureFlags.VANILLA_SET;
      @Nullable
      private ResourceKey<Item> id;
      private DependantName<Item, String> descriptionId = ITEM_DESCRIPTION_ID;
      private DependantName<Item, ResourceLocation> model = ResourceKey::location;

      public Properties() {
         super();
      }

      public Item.Properties food(FoodProperties var1) {
         return this.food(var1, Consumables.DEFAULT_FOOD);
      }

      public Item.Properties food(FoodProperties var1, Consumable var2) {
         return this.component(DataComponents.FOOD, var1).component(DataComponents.CONSUMABLE, var2);
      }

      public Item.Properties usingConvertsTo(Item var1) {
         return this.component(DataComponents.USE_REMAINDER, new UseRemainder(new ItemStack(var1)));
      }

      public Item.Properties useCooldown(float var1) {
         return this.component(DataComponents.USE_COOLDOWN, new UseCooldown(var1));
      }

      public Item.Properties stacksTo(int var1) {
         return this.component(DataComponents.MAX_STACK_SIZE, var1);
      }

      public Item.Properties durability(int var1) {
         this.component(DataComponents.MAX_DAMAGE, var1);
         this.component(DataComponents.MAX_STACK_SIZE, 1);
         this.component(DataComponents.DAMAGE, 0);
         return this;
      }

      public Item.Properties craftRemainder(Item var1) {
         this.craftingRemainingItem = var1;
         return this;
      }

      public Item.Properties rarity(Rarity var1) {
         return this.component(DataComponents.RARITY, var1);
      }

      public Item.Properties fireResistant() {
         return this.component(DataComponents.FIRE_RESISTANT, Unit.INSTANCE);
      }

      public Item.Properties jukeboxPlayable(ResourceKey<JukeboxSong> var1) {
         return this.component(DataComponents.JUKEBOX_PLAYABLE, new JukeboxPlayable(new EitherHolder<>(var1), true));
      }

      public Item.Properties enchantable(int var1) {
         return this.component(DataComponents.ENCHANTABLE, new Enchantable(var1));
      }

      public Item.Properties repairable(Item var1) {
         return this.component(DataComponents.REPAIRABLE, new Repairable(HolderSet.direct(var1.builtInRegistryHolder())));
      }

      public Item.Properties repairable(TagKey<Item> var1) {
         HolderGetter var2 = BuiltInRegistries.acquireBootstrapRegistrationLookup(BuiltInRegistries.ITEM);
         return this.component(DataComponents.REPAIRABLE, new Repairable(var2.getOrThrow(var1)));
      }

      public Item.Properties equippable(EquipmentSlot var1, Holder<SoundEvent> var2, ResourceLocation var3) {
         return this.component(DataComponents.EQUIPPABLE, new Equippable(var1, var2, Optional.of(var3), Optional.empty(), true));
      }

      public Item.Properties equippable(EquipmentSlot var1) {
         return this.component(DataComponents.EQUIPPABLE, new Equippable(var1, SoundEvents.ARMOR_EQUIP_GENERIC, Optional.empty(), Optional.empty(), true));
      }

      public Item.Properties requiredFeatures(FeatureFlag... var1) {
         this.requiredFeatures = FeatureFlags.REGISTRY.subset(var1);
         return this;
      }

      public Item.Properties setId(ResourceKey<Item> var1) {
         this.id = var1;
         return this;
      }

      public Item.Properties overrideDescription(String var1) {
         this.descriptionId = DependantName.fixed(var1);
         return this;
      }

      public Item.Properties useBlockDescriptionPrefix() {
         this.descriptionId = BLOCK_DESCRIPTION_ID;
         return this;
      }

      public Item.Properties useItemDescriptionPrefix() {
         this.descriptionId = ITEM_DESCRIPTION_ID;
         return this;
      }

      protected String effectiveDescriptionId() {
         return this.descriptionId.get(Objects.requireNonNull(this.id, "Item id not set"));
      }

      public Item.Properties overrideModel(ResourceLocation var1) {
         this.model = DependantName.fixed(var1);
         return this;
      }

      public ResourceLocation effectiveModel() {
         return this.model.get(Objects.requireNonNull(this.id, "Item id not set"));
      }

      public <T> Item.Properties component(DataComponentType<T> var1, T var2) {
         this.components.set(var1, var2);
         return this;
      }

      public Item.Properties attributes(ItemAttributeModifiers var1) {
         return this.component(DataComponents.ATTRIBUTE_MODIFIERS, var1);
      }

      DataComponentMap buildAndValidateComponents(Component var1, ResourceLocation var2) {
         DataComponentMap var3 = this.components.set(DataComponents.ITEM_NAME, var1).set(DataComponents.ITEM_MODEL, var2).build();
         if (var3.has(DataComponents.DAMAGE) && var3.getOrDefault(DataComponents.MAX_STACK_SIZE, 1) > 1) {
            throw new IllegalStateException("Item cannot have both durability and be stackable");
         } else {
            return var3;
         }
      }
   }

   public interface TooltipContext {
      Item.TooltipContext EMPTY = new Item.TooltipContext() {
         @Nullable
         @Override
         public HolderLookup.Provider registries() {
            return null;
         }

         @Override
         public float tickRate() {
            return 20.0F;
         }

         @Nullable
         @Override
         public MapItemSavedData mapData(MapId var1) {
            return null;
         }
      };

      @Nullable
      HolderLookup.Provider registries();

      float tickRate();

      @Nullable
      MapItemSavedData mapData(MapId var1);

      static Item.TooltipContext of(@Nullable final Level var0) {
         return var0 == null ? EMPTY : new Item.TooltipContext() {
            @Override
            public HolderLookup.Provider registries() {
               return var0.registryAccess();
            }

            @Override
            public float tickRate() {
               return var0.tickRateManager().tickrate();
            }

            @Override
            public MapItemSavedData mapData(MapId var1) {
               return var0.getMapData(var1);
            }
         };
      }

      static Item.TooltipContext of(final HolderLookup.Provider var0) {
         return new Item.TooltipContext() {
            @Override
            public HolderLookup.Provider registries() {
               return var0;
            }

            @Override
            public float tickRate() {
               return 20.0F;
            }

            @Nullable
            @Override
            public MapItemSavedData mapData(MapId var1) {
               return null;
            }
         };
      }
   }
}
