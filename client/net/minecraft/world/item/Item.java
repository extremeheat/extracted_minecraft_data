package net.minecraft.world.item;

import com.google.common.collect.Interner;
import com.google.common.collect.Interners;
import com.google.common.collect.Maps;
import com.mojang.logging.LogUtils;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.SharedConstants;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.util.Unit;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
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
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.minecraft.world.item.component.Tool;
import net.minecraft.world.item.context.UseOnContext;
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
   public static final UUID BASE_ATTACK_DAMAGE_UUID = UUID.fromString("CB3F55D3-645C-4F38-A497-9C13A33DB5CF");
   public static final UUID BASE_ATTACK_SPEED_UUID = UUID.fromString("FA233E1C-4180-4865-B01B-BCCE9785ACA3");
   public static final int DEFAULT_MAX_STACK_SIZE = 64;
   public static final int ABSOLUTE_MAX_STACK_SIZE = 99;
   public static final int MAX_BAR_WIDTH = 13;
   private final Holder.Reference<Item> builtInRegistryHolder;
   private final DataComponentMap components;
   @Nullable
   private final Item craftingRemainingItem;
   @Nullable
   private String descriptionId;
   private final FeatureFlagSet requiredFeatures;

   public static int getId(Item var0) {
      return var0 == null ? 0 : BuiltInRegistries.ITEM.getId(var0);
   }

   public static Item byId(int var0) {
      return (Item)BuiltInRegistries.ITEM.byId(var0);
   }

   /** @deprecated */
   @Deprecated
   public static Item byBlock(Block var0) {
      return (Item)BY_BLOCK.getOrDefault(var0, Items.AIR);
   }

   public Item(Properties var1) {
      super();
      this.builtInRegistryHolder = BuiltInRegistries.ITEM.createIntrusiveHolder(this);
      this.components = var1.buildAndValidateComponents();
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

   public void verifyComponentsAfterLoad(ItemStack var1) {
   }

   public boolean canAttackBlock(BlockState var1, Level var2, BlockPos var3, Player var4) {
      return true;
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

   public InteractionResultHolder<ItemStack> use(Level var1, Player var2, InteractionHand var3) {
      ItemStack var4 = var2.getItemInHand(var3);
      FoodProperties var5 = (FoodProperties)var4.get(DataComponents.FOOD);
      if (var5 != null) {
         if (var2.canEat(var5.canAlwaysEat())) {
            var2.startUsingItem(var3);
            return InteractionResultHolder.consume(var4);
         } else {
            return InteractionResultHolder.fail(var4);
         }
      } else {
         return InteractionResultHolder.pass(var2.getItemInHand(var3));
      }
   }

   public ItemStack finishUsingItem(ItemStack var1, Level var2, LivingEntity var3) {
      return var1.has(DataComponents.FOOD) ? var3.eat(var2, var1) : var1;
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
      Tool var6 = (Tool)var1.get(DataComponents.TOOL);
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
      Tool var3 = (Tool)var1.get(DataComponents.TOOL);
      return var3 != null && var3.isCorrectForDrops(var2);
   }

   public InteractionResult interactLivingEntity(ItemStack var1, Player var2, LivingEntity var3, InteractionHand var4) {
      return InteractionResult.PASS;
   }

   public Component getDescription() {
      return Component.translatable(this.getDescriptionId());
   }

   public String toString() {
      return BuiltInRegistries.ITEM.getKey(this).getPath();
   }

   protected String getOrCreateDescriptionId() {
      if (this.descriptionId == null) {
         this.descriptionId = Util.makeDescriptionId("item", BuiltInRegistries.ITEM.getKey(this));
      }

      return this.descriptionId;
   }

   public String getDescriptionId() {
      return this.getOrCreateDescriptionId();
   }

   public String getDescriptionId(ItemStack var1) {
      return this.getDescriptionId();
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

   public UseAnim getUseAnimation(ItemStack var1) {
      return var1.has(DataComponents.FOOD) ? UseAnim.EAT : UseAnim.NONE;
   }

   public int getUseDuration(ItemStack var1, LivingEntity var2) {
      FoodProperties var3 = (FoodProperties)var1.get(DataComponents.FOOD);
      return var3 != null ? var3.eatDurationTicks() : 0;
   }

   public void releaseUsing(ItemStack var1, Level var2, LivingEntity var3, int var4) {
   }

   public void appendHoverText(ItemStack var1, TooltipContext var2, List<Component> var3, TooltipFlag var4) {
   }

   public Optional<TooltipComponent> getTooltipImage(ItemStack var1) {
      return Optional.empty();
   }

   public Component getName(ItemStack var1) {
      return Component.translatable(this.getDescriptionId(var1));
   }

   public boolean isFoil(ItemStack var1) {
      return var1.isEnchanted();
   }

   public boolean isEnchantable(ItemStack var1) {
      return var1.getMaxStackSize() == 1 && var1.has(DataComponents.MAX_DAMAGE);
   }

   protected static BlockHitResult getPlayerPOVHitResult(Level var0, Player var1, ClipContext.Fluid var2) {
      Vec3 var3 = var1.getEyePosition();
      Vec3 var4 = var3.add(var1.calculateViewVector(var1.getXRot(), var1.getYRot()).scale(var1.blockInteractionRange()));
      return var0.clip(new ClipContext(var3, var4, ClipContext.Block.OUTLINE, var2, var1));
   }

   public int getEnchantmentValue() {
      return 0;
   }

   public boolean isValidRepairItem(ItemStack var1, ItemStack var2) {
      return false;
   }

   /** @deprecated */
   @Deprecated
   public ItemAttributeModifiers getDefaultAttributeModifiers() {
      return ItemAttributeModifiers.EMPTY;
   }

   public boolean useOnRelease(ItemStack var1) {
      return false;
   }

   public ItemStack getDefaultInstance() {
      return new ItemStack(this);
   }

   public SoundEvent getDrinkingSound() {
      return SoundEvents.GENERIC_DRINK;
   }

   public SoundEvent getEatingSound() {
      return SoundEvents.GENERIC_EAT;
   }

   public SoundEvent getBreakingSound() {
      return SoundEvents.ITEM_BREAK;
   }

   public boolean canFitInsideContainerItems() {
      return true;
   }

   public FeatureFlagSet requiredFeatures() {
      return this.requiredFeatures;
   }

   public static class Properties {
      private static final Interner<DataComponentMap> COMPONENT_INTERNER = Interners.newStrongInterner();
      @Nullable
      private DataComponentMap.Builder components;
      @Nullable
      Item craftingRemainingItem;
      FeatureFlagSet requiredFeatures;

      public Properties() {
         super();
         this.requiredFeatures = FeatureFlags.VANILLA_SET;
      }

      public Properties food(FoodProperties var1) {
         return this.component(DataComponents.FOOD, var1);
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
         return this.component(DataComponents.FIRE_RESISTANT, Unit.INSTANCE);
      }

      public Properties requiredFeatures(FeatureFlag... var1) {
         this.requiredFeatures = FeatureFlags.REGISTRY.subset(var1);
         return this;
      }

      public <T> Properties component(DataComponentType<T> var1, T var2) {
         if (this.components == null) {
            this.components = DataComponentMap.builder().addAll(DataComponents.COMMON_ITEM_COMPONENTS);
         }

         this.components.set(var1, var2);
         return this;
      }

      public Properties attributes(ItemAttributeModifiers var1) {
         return this.component(DataComponents.ATTRIBUTE_MODIFIERS, var1);
      }

      DataComponentMap buildAndValidateComponents() {
         DataComponentMap var1 = this.buildComponents();
         if (var1.has(DataComponents.DAMAGE) && (Integer)var1.getOrDefault(DataComponents.MAX_STACK_SIZE, 1) > 1) {
            throw new IllegalStateException("Item cannot have both durability and be stackable");
         } else {
            return var1;
         }
      }

      private DataComponentMap buildComponents() {
         return this.components == null ? DataComponents.COMMON_ITEM_COMPONENTS : (DataComponentMap)COMPONENT_INTERNER.intern(this.components.build());
      }
   }

   public interface TooltipContext {
      TooltipContext EMPTY = new TooltipContext() {
         @Nullable
         public HolderLookup.Provider registries() {
            return null;
         }

         public float tickRate() {
            return 20.0F;
         }

         @Nullable
         public MapItemSavedData mapData(MapId var1) {
            return null;
         }
      };

      @Nullable
      HolderLookup.Provider registries();

      float tickRate();

      @Nullable
      MapItemSavedData mapData(MapId var1);

      static TooltipContext of(@Nullable final Level var0) {
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

            @Nullable
            public MapItemSavedData mapData(MapId var1) {
               return null;
            }
         };
      }
   }
}
