package net.minecraft.advancements;

import com.google.common.collect.Maps;
import java.util.Map;
import javax.annotation.Nullable;
import net.minecraft.advancements.critereon.BeeNestDestroyedTrigger;
import net.minecraft.advancements.critereon.BredAnimalsTrigger;
import net.minecraft.advancements.critereon.BrewedPotionTrigger;
import net.minecraft.advancements.critereon.ChangeDimensionTrigger;
import net.minecraft.advancements.critereon.ChanneledLightningTrigger;
import net.minecraft.advancements.critereon.ConstructBeaconTrigger;
import net.minecraft.advancements.critereon.ConsumeItemTrigger;
import net.minecraft.advancements.critereon.CuredZombieVillagerTrigger;
import net.minecraft.advancements.critereon.EffectsChangedTrigger;
import net.minecraft.advancements.critereon.EnchantedItemTrigger;
import net.minecraft.advancements.critereon.EnterBlockTrigger;
import net.minecraft.advancements.critereon.EntityHurtPlayerTrigger;
import net.minecraft.advancements.critereon.FilledBucketTrigger;
import net.minecraft.advancements.critereon.FishingRodHookedTrigger;
import net.minecraft.advancements.critereon.ImpossibleTrigger;
import net.minecraft.advancements.critereon.InventoryChangeTrigger;
import net.minecraft.advancements.critereon.ItemDurabilityTrigger;
import net.minecraft.advancements.critereon.ItemUsedOnBlockTrigger;
import net.minecraft.advancements.critereon.KilledByCrossbowTrigger;
import net.minecraft.advancements.critereon.KilledTrigger;
import net.minecraft.advancements.critereon.LevitationTrigger;
import net.minecraft.advancements.critereon.LocationTrigger;
import net.minecraft.advancements.critereon.NetherTravelTrigger;
import net.minecraft.advancements.critereon.PlacedBlockTrigger;
import net.minecraft.advancements.critereon.PlayerHurtEntityTrigger;
import net.minecraft.advancements.critereon.RecipeUnlockedTrigger;
import net.minecraft.advancements.critereon.ShotCrossbowTrigger;
import net.minecraft.advancements.critereon.SlideDownBlockTrigger;
import net.minecraft.advancements.critereon.SummonedEntityTrigger;
import net.minecraft.advancements.critereon.TameAnimalTrigger;
import net.minecraft.advancements.critereon.TickTrigger;
import net.minecraft.advancements.critereon.TradeTrigger;
import net.minecraft.advancements.critereon.UsedEnderEyeTrigger;
import net.minecraft.advancements.critereon.UsedTotemTrigger;
import net.minecraft.resources.ResourceLocation;

public class CriteriaTriggers {
   private static final Map CRITERIA = Maps.newHashMap();
   public static final ImpossibleTrigger IMPOSSIBLE = (ImpossibleTrigger)register(new ImpossibleTrigger());
   public static final KilledTrigger PLAYER_KILLED_ENTITY = (KilledTrigger)register(new KilledTrigger(new ResourceLocation("player_killed_entity")));
   public static final KilledTrigger ENTITY_KILLED_PLAYER = (KilledTrigger)register(new KilledTrigger(new ResourceLocation("entity_killed_player")));
   public static final EnterBlockTrigger ENTER_BLOCK = (EnterBlockTrigger)register(new EnterBlockTrigger());
   public static final InventoryChangeTrigger INVENTORY_CHANGED = (InventoryChangeTrigger)register(new InventoryChangeTrigger());
   public static final RecipeUnlockedTrigger RECIPE_UNLOCKED = (RecipeUnlockedTrigger)register(new RecipeUnlockedTrigger());
   public static final PlayerHurtEntityTrigger PLAYER_HURT_ENTITY = (PlayerHurtEntityTrigger)register(new PlayerHurtEntityTrigger());
   public static final EntityHurtPlayerTrigger ENTITY_HURT_PLAYER = (EntityHurtPlayerTrigger)register(new EntityHurtPlayerTrigger());
   public static final EnchantedItemTrigger ENCHANTED_ITEM = (EnchantedItemTrigger)register(new EnchantedItemTrigger());
   public static final FilledBucketTrigger FILLED_BUCKET = (FilledBucketTrigger)register(new FilledBucketTrigger());
   public static final BrewedPotionTrigger BREWED_POTION = (BrewedPotionTrigger)register(new BrewedPotionTrigger());
   public static final ConstructBeaconTrigger CONSTRUCT_BEACON = (ConstructBeaconTrigger)register(new ConstructBeaconTrigger());
   public static final UsedEnderEyeTrigger USED_ENDER_EYE = (UsedEnderEyeTrigger)register(new UsedEnderEyeTrigger());
   public static final SummonedEntityTrigger SUMMONED_ENTITY = (SummonedEntityTrigger)register(new SummonedEntityTrigger());
   public static final BredAnimalsTrigger BRED_ANIMALS = (BredAnimalsTrigger)register(new BredAnimalsTrigger());
   public static final LocationTrigger LOCATION = (LocationTrigger)register(new LocationTrigger(new ResourceLocation("location")));
   public static final LocationTrigger SLEPT_IN_BED = (LocationTrigger)register(new LocationTrigger(new ResourceLocation("slept_in_bed")));
   public static final CuredZombieVillagerTrigger CURED_ZOMBIE_VILLAGER = (CuredZombieVillagerTrigger)register(new CuredZombieVillagerTrigger());
   public static final TradeTrigger TRADE = (TradeTrigger)register(new TradeTrigger());
   public static final ItemDurabilityTrigger ITEM_DURABILITY_CHANGED = (ItemDurabilityTrigger)register(new ItemDurabilityTrigger());
   public static final LevitationTrigger LEVITATION = (LevitationTrigger)register(new LevitationTrigger());
   public static final ChangeDimensionTrigger CHANGED_DIMENSION = (ChangeDimensionTrigger)register(new ChangeDimensionTrigger());
   public static final TickTrigger TICK = (TickTrigger)register(new TickTrigger());
   public static final TameAnimalTrigger TAME_ANIMAL = (TameAnimalTrigger)register(new TameAnimalTrigger());
   public static final PlacedBlockTrigger PLACED_BLOCK = (PlacedBlockTrigger)register(new PlacedBlockTrigger());
   public static final ConsumeItemTrigger CONSUME_ITEM = (ConsumeItemTrigger)register(new ConsumeItemTrigger());
   public static final EffectsChangedTrigger EFFECTS_CHANGED = (EffectsChangedTrigger)register(new EffectsChangedTrigger());
   public static final UsedTotemTrigger USED_TOTEM = (UsedTotemTrigger)register(new UsedTotemTrigger());
   public static final NetherTravelTrigger NETHER_TRAVEL = (NetherTravelTrigger)register(new NetherTravelTrigger());
   public static final FishingRodHookedTrigger FISHING_ROD_HOOKED = (FishingRodHookedTrigger)register(new FishingRodHookedTrigger());
   public static final ChanneledLightningTrigger CHANNELED_LIGHTNING = (ChanneledLightningTrigger)register(new ChanneledLightningTrigger());
   public static final ShotCrossbowTrigger SHOT_CROSSBOW = (ShotCrossbowTrigger)register(new ShotCrossbowTrigger());
   public static final KilledByCrossbowTrigger KILLED_BY_CROSSBOW = (KilledByCrossbowTrigger)register(new KilledByCrossbowTrigger());
   public static final LocationTrigger RAID_WIN = (LocationTrigger)register(new LocationTrigger(new ResourceLocation("hero_of_the_village")));
   public static final LocationTrigger BAD_OMEN = (LocationTrigger)register(new LocationTrigger(new ResourceLocation("voluntary_exile")));
   public static final ItemUsedOnBlockTrigger SAFELY_HARVEST_HONEY = (ItemUsedOnBlockTrigger)register(new ItemUsedOnBlockTrigger(new ResourceLocation("safely_harvest_honey")));
   public static final SlideDownBlockTrigger HONEY_BLOCK_SLIDE = (SlideDownBlockTrigger)register(new SlideDownBlockTrigger());
   public static final BeeNestDestroyedTrigger BEE_NEST_DESTROYED = (BeeNestDestroyedTrigger)register(new BeeNestDestroyedTrigger());

   private static CriterionTrigger register(CriterionTrigger var0) {
      if (CRITERIA.containsKey(var0.getId())) {
         throw new IllegalArgumentException("Duplicate criterion id " + var0.getId());
      } else {
         CRITERIA.put(var0.getId(), var0);
         return var0;
      }
   }

   @Nullable
   public static CriterionTrigger getCriterion(ResourceLocation var0) {
      return (CriterionTrigger)CRITERIA.get(var0);
   }

   public static Iterable all() {
      return CRITERIA.values();
   }
}
