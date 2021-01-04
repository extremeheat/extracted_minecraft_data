package net.minecraft.world.item;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.core.Registry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.Tag;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

public class Item implements ItemLike {
   public static final Map<Block, Item> BY_BLOCK = Maps.newHashMap();
   private static final ItemPropertyFunction PROPERTY_DAMAGED = (var0, var1, var2) -> {
      return var0.isDamaged() ? 1.0F : 0.0F;
   };
   private static final ItemPropertyFunction PROPERTY_DAMAGE = (var0, var1, var2) -> {
      return Mth.clamp((float)var0.getDamageValue() / (float)var0.getMaxDamage(), 0.0F, 1.0F);
   };
   private static final ItemPropertyFunction PROPERTY_LEFTHANDED = (var0, var1, var2) -> {
      return var2 != null && var2.getMainArm() != HumanoidArm.RIGHT ? 1.0F : 0.0F;
   };
   private static final ItemPropertyFunction PROPERTY_COOLDOWN = (var0, var1, var2) -> {
      return var2 instanceof Player ? ((Player)var2).getCooldowns().getCooldownPercent(var0.getItem(), 0.0F) : 0.0F;
   };
   private static final ItemPropertyFunction PROPERTY_CUSTOM_MODEL_DATA = (var0, var1, var2) -> {
      return var0.hasTag() ? (float)var0.getTag().getInt("CustomModelData") : 0.0F;
   };
   protected static final UUID BASE_ATTACK_DAMAGE_UUID = UUID.fromString("CB3F55D3-645C-4F38-A497-9C13A33DB5CF");
   protected static final UUID BASE_ATTACK_SPEED_UUID = UUID.fromString("FA233E1C-4180-4865-B01B-BCCE9785ACA3");
   protected static final Random random = new Random();
   private final Map<ResourceLocation, ItemPropertyFunction> properties = Maps.newHashMap();
   protected final CreativeModeTab category;
   private final Rarity rarity;
   private final int maxStackSize;
   private final int maxDamage;
   private final Item craftingRemainingItem;
   @Nullable
   private String descriptionId;
   @Nullable
   private final FoodProperties foodProperties;

   public static int getId(Item var0) {
      return var0 == null ? 0 : Registry.ITEM.getId(var0);
   }

   public static Item byId(int var0) {
      return (Item)Registry.ITEM.byId(var0);
   }

   @Deprecated
   public static Item byBlock(Block var0) {
      return (Item)BY_BLOCK.getOrDefault(var0, Items.AIR);
   }

   public Item(Item.Properties var1) {
      super();
      this.addProperty(new ResourceLocation("lefthanded"), PROPERTY_LEFTHANDED);
      this.addProperty(new ResourceLocation("cooldown"), PROPERTY_COOLDOWN);
      this.addProperty(new ResourceLocation("custom_model_data"), PROPERTY_CUSTOM_MODEL_DATA);
      this.category = var1.category;
      this.rarity = var1.rarity;
      this.craftingRemainingItem = var1.craftingRemainingItem;
      this.maxDamage = var1.maxDamage;
      this.maxStackSize = var1.maxStackSize;
      this.foodProperties = var1.foodProperties;
      if (this.maxDamage > 0) {
         this.addProperty(new ResourceLocation("damaged"), PROPERTY_DAMAGED);
         this.addProperty(new ResourceLocation("damage"), PROPERTY_DAMAGE);
      }

   }

   public void onUseTick(Level var1, LivingEntity var2, ItemStack var3, int var4) {
   }

   @Nullable
   public ItemPropertyFunction getProperty(ResourceLocation var1) {
      return (ItemPropertyFunction)this.properties.get(var1);
   }

   public boolean hasProperties() {
      return !this.properties.isEmpty();
   }

   public boolean verifyTagAfterLoad(CompoundTag var1) {
      return false;
   }

   public boolean canAttackBlock(BlockState var1, Level var2, BlockPos var3, Player var4) {
      return true;
   }

   public Item asItem() {
      return this;
   }

   public final void addProperty(ResourceLocation var1, ItemPropertyFunction var2) {
      this.properties.put(var1, var2);
   }

   public InteractionResult useOn(UseOnContext var1) {
      return InteractionResult.PASS;
   }

   public float getDestroySpeed(ItemStack var1, BlockState var2) {
      return 1.0F;
   }

   public InteractionResultHolder<ItemStack> use(Level var1, Player var2, InteractionHand var3) {
      if (this.isEdible()) {
         ItemStack var4 = var2.getItemInHand(var3);
         if (var2.canEat(this.getFoodProperties().canAlwaysEat())) {
            var2.startUsingItem(var3);
            return new InteractionResultHolder(InteractionResult.SUCCESS, var4);
         } else {
            return new InteractionResultHolder(InteractionResult.FAIL, var4);
         }
      } else {
         return new InteractionResultHolder(InteractionResult.PASS, var2.getItemInHand(var3));
      }
   }

   public ItemStack finishUsingItem(ItemStack var1, Level var2, LivingEntity var3) {
      return this.isEdible() ? var3.eat(var2, var1) : var1;
   }

   public final int getMaxStackSize() {
      return this.maxStackSize;
   }

   public final int getMaxDamage() {
      return this.maxDamage;
   }

   public boolean canBeDepleted() {
      return this.maxDamage > 0;
   }

   public boolean hurtEnemy(ItemStack var1, LivingEntity var2, LivingEntity var3) {
      return false;
   }

   public boolean mineBlock(ItemStack var1, Level var2, BlockState var3, BlockPos var4, LivingEntity var5) {
      return false;
   }

   public boolean canDestroySpecial(BlockState var1) {
      return false;
   }

   public boolean interactEnemy(ItemStack var1, Player var2, LivingEntity var3, InteractionHand var4) {
      return false;
   }

   public Component getDescription() {
      return new TranslatableComponent(this.getDescriptionId(), new Object[0]);
   }

   public String toString() {
      return Registry.ITEM.getKey(this).getPath();
   }

   protected String getOrCreateDescriptionId() {
      if (this.descriptionId == null) {
         this.descriptionId = Util.makeDescriptionId("item", Registry.ITEM.getKey(this));
      }

      return this.descriptionId;
   }

   public String getDescriptionId() {
      return this.getOrCreateDescriptionId();
   }

   public String getDescriptionId(ItemStack var1) {
      return this.getDescriptionId();
   }

   public boolean shouldOverrideMultiplayerNbt() {
      return true;
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
   }

   public boolean isComplex() {
      return false;
   }

   public UseAnim getUseAnimation(ItemStack var1) {
      return var1.getItem().isEdible() ? UseAnim.EAT : UseAnim.NONE;
   }

   public int getUseDuration(ItemStack var1) {
      if (var1.getItem().isEdible()) {
         return this.getFoodProperties().isFastFood() ? 16 : 32;
      } else {
         return 0;
      }
   }

   public void releaseUsing(ItemStack var1, Level var2, LivingEntity var3, int var4) {
   }

   public void appendHoverText(ItemStack var1, @Nullable Level var2, List<Component> var3, TooltipFlag var4) {
   }

   public Component getName(ItemStack var1) {
      return new TranslatableComponent(this.getDescriptionId(var1), new Object[0]);
   }

   public boolean isFoil(ItemStack var1) {
      return var1.isEnchanted();
   }

   public Rarity getRarity(ItemStack var1) {
      if (!var1.isEnchanted()) {
         return this.rarity;
      } else {
         switch(this.rarity) {
         case COMMON:
         case UNCOMMON:
            return Rarity.RARE;
         case RARE:
            return Rarity.EPIC;
         case EPIC:
         default:
            return this.rarity;
         }
      }
   }

   public boolean isEnchantable(ItemStack var1) {
      return this.getMaxStackSize() == 1 && this.canBeDepleted();
   }

   protected static HitResult getPlayerPOVHitResult(Level var0, Player var1, ClipContext.Fluid var2) {
      float var3 = var1.xRot;
      float var4 = var1.yRot;
      Vec3 var5 = var1.getEyePosition(1.0F);
      float var6 = Mth.cos(-var4 * 0.017453292F - 3.1415927F);
      float var7 = Mth.sin(-var4 * 0.017453292F - 3.1415927F);
      float var8 = -Mth.cos(-var3 * 0.017453292F);
      float var9 = Mth.sin(-var3 * 0.017453292F);
      float var10 = var7 * var8;
      float var12 = var6 * var8;
      double var13 = 5.0D;
      Vec3 var15 = var5.add((double)var10 * 5.0D, (double)var9 * 5.0D, (double)var12 * 5.0D);
      return var0.clip(new ClipContext(var5, var15, ClipContext.Block.OUTLINE, var2, var1));
   }

   public int getEnchantmentValue() {
      return 0;
   }

   public void fillItemCategory(CreativeModeTab var1, NonNullList<ItemStack> var2) {
      if (this.allowdedIn(var1)) {
         var2.add(new ItemStack(this));
      }

   }

   protected boolean allowdedIn(CreativeModeTab var1) {
      CreativeModeTab var2 = this.getItemCategory();
      return var2 != null && (var1 == CreativeModeTab.TAB_SEARCH || var1 == var2);
   }

   @Nullable
   public final CreativeModeTab getItemCategory() {
      return this.category;
   }

   public boolean isValidRepairItem(ItemStack var1, ItemStack var2) {
      return false;
   }

   public Multimap<String, AttributeModifier> getDefaultAttributeModifiers(EquipmentSlot var1) {
      return HashMultimap.create();
   }

   public boolean useOnRelease(ItemStack var1) {
      return var1.getItem() == Items.CROSSBOW;
   }

   public ItemStack getDefaultInstance() {
      return new ItemStack(this);
   }

   public boolean is(Tag<Item> var1) {
      return var1.contains(this);
   }

   public boolean isEdible() {
      return this.foodProperties != null;
   }

   @Nullable
   public FoodProperties getFoodProperties() {
      return this.foodProperties;
   }

   public static class Properties {
      private int maxStackSize = 64;
      private int maxDamage;
      private Item craftingRemainingItem;
      private CreativeModeTab category;
      private Rarity rarity;
      private FoodProperties foodProperties;

      public Properties() {
         super();
         this.rarity = Rarity.COMMON;
      }

      public Item.Properties food(FoodProperties var1) {
         this.foodProperties = var1;
         return this;
      }

      public Item.Properties stacksTo(int var1) {
         if (this.maxDamage > 0) {
            throw new RuntimeException("Unable to have damage AND stack.");
         } else {
            this.maxStackSize = var1;
            return this;
         }
      }

      public Item.Properties defaultDurability(int var1) {
         return this.maxDamage == 0 ? this.durability(var1) : this;
      }

      public Item.Properties durability(int var1) {
         this.maxDamage = var1;
         this.maxStackSize = 1;
         return this;
      }

      public Item.Properties craftRemainder(Item var1) {
         this.craftingRemainingItem = var1;
         return this;
      }

      public Item.Properties tab(CreativeModeTab var1) {
         this.category = var1;
         return this;
      }

      public Item.Properties rarity(Rarity var1) {
         this.rarity = var1;
         return this;
      }
   }
}
