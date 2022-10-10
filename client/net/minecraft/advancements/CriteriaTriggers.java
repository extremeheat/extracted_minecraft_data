package net.minecraft.advancements;

import com.google.common.collect.Maps;
import java.util.Map;
import javax.annotation.Nullable;
import net.minecraft.advancements.criterion.BredAnimalsTrigger;
import net.minecraft.advancements.criterion.BrewedPotionTrigger;
import net.minecraft.advancements.criterion.ChangeDimensionTrigger;
import net.minecraft.advancements.criterion.ChanneledLightningTrigger;
import net.minecraft.advancements.criterion.ConstructBeaconTrigger;
import net.minecraft.advancements.criterion.ConsumeItemTrigger;
import net.minecraft.advancements.criterion.CuredZombieVillagerTrigger;
import net.minecraft.advancements.criterion.EffectsChangedTrigger;
import net.minecraft.advancements.criterion.EnchantedItemTrigger;
import net.minecraft.advancements.criterion.EnterBlockTrigger;
import net.minecraft.advancements.criterion.EntityHurtPlayerTrigger;
import net.minecraft.advancements.criterion.FilledBucketTrigger;
import net.minecraft.advancements.criterion.FishingRodHookedTrigger;
import net.minecraft.advancements.criterion.ImpossibleTrigger;
import net.minecraft.advancements.criterion.InventoryChangeTrigger;
import net.minecraft.advancements.criterion.ItemDurabilityTrigger;
import net.minecraft.advancements.criterion.KilledTrigger;
import net.minecraft.advancements.criterion.LevitationTrigger;
import net.minecraft.advancements.criterion.NetherTravelTrigger;
import net.minecraft.advancements.criterion.PlacedBlockTrigger;
import net.minecraft.advancements.criterion.PlayerHurtEntityTrigger;
import net.minecraft.advancements.criterion.PositionTrigger;
import net.minecraft.advancements.criterion.RecipeUnlockedTrigger;
import net.minecraft.advancements.criterion.SummonedEntityTrigger;
import net.minecraft.advancements.criterion.TameAnimalTrigger;
import net.minecraft.advancements.criterion.TickTrigger;
import net.minecraft.advancements.criterion.UsedEnderEyeTrigger;
import net.minecraft.advancements.criterion.UsedTotemTrigger;
import net.minecraft.advancements.criterion.VillagerTradeTrigger;
import net.minecraft.util.ResourceLocation;

public class CriteriaTriggers {
   private static final Map<ResourceLocation, ICriterionTrigger<?>> field_192139_s = Maps.newHashMap();
   public static final ImpossibleTrigger field_192121_a = (ImpossibleTrigger)func_192118_a(new ImpossibleTrigger());
   public static final KilledTrigger field_192122_b = (KilledTrigger)func_192118_a(new KilledTrigger(new ResourceLocation("player_killed_entity")));
   public static final KilledTrigger field_192123_c = (KilledTrigger)func_192118_a(new KilledTrigger(new ResourceLocation("entity_killed_player")));
   public static final EnterBlockTrigger field_192124_d = (EnterBlockTrigger)func_192118_a(new EnterBlockTrigger());
   public static final InventoryChangeTrigger field_192125_e = (InventoryChangeTrigger)func_192118_a(new InventoryChangeTrigger());
   public static final RecipeUnlockedTrigger field_192126_f = (RecipeUnlockedTrigger)func_192118_a(new RecipeUnlockedTrigger());
   public static final PlayerHurtEntityTrigger field_192127_g = (PlayerHurtEntityTrigger)func_192118_a(new PlayerHurtEntityTrigger());
   public static final EntityHurtPlayerTrigger field_192128_h = (EntityHurtPlayerTrigger)func_192118_a(new EntityHurtPlayerTrigger());
   public static final EnchantedItemTrigger field_192129_i = (EnchantedItemTrigger)func_192118_a(new EnchantedItemTrigger());
   public static final FilledBucketTrigger field_204813_j = (FilledBucketTrigger)func_192118_a(new FilledBucketTrigger());
   public static final BrewedPotionTrigger field_192130_j = (BrewedPotionTrigger)func_192118_a(new BrewedPotionTrigger());
   public static final ConstructBeaconTrigger field_192131_k = (ConstructBeaconTrigger)func_192118_a(new ConstructBeaconTrigger());
   public static final UsedEnderEyeTrigger field_192132_l = (UsedEnderEyeTrigger)func_192118_a(new UsedEnderEyeTrigger());
   public static final SummonedEntityTrigger field_192133_m = (SummonedEntityTrigger)func_192118_a(new SummonedEntityTrigger());
   public static final BredAnimalsTrigger field_192134_n = (BredAnimalsTrigger)func_192118_a(new BredAnimalsTrigger());
   public static final PositionTrigger field_192135_o = (PositionTrigger)func_192118_a(new PositionTrigger(new ResourceLocation("location")));
   public static final PositionTrigger field_192136_p = (PositionTrigger)func_192118_a(new PositionTrigger(new ResourceLocation("slept_in_bed")));
   public static final CuredZombieVillagerTrigger field_192137_q = (CuredZombieVillagerTrigger)func_192118_a(new CuredZombieVillagerTrigger());
   public static final VillagerTradeTrigger field_192138_r = (VillagerTradeTrigger)func_192118_a(new VillagerTradeTrigger());
   public static final ItemDurabilityTrigger field_193132_s = (ItemDurabilityTrigger)func_192118_a(new ItemDurabilityTrigger());
   public static final LevitationTrigger field_193133_t = (LevitationTrigger)func_192118_a(new LevitationTrigger());
   public static final ChangeDimensionTrigger field_193134_u = (ChangeDimensionTrigger)func_192118_a(new ChangeDimensionTrigger());
   public static final TickTrigger field_193135_v = (TickTrigger)func_192118_a(new TickTrigger());
   public static final TameAnimalTrigger field_193136_w = (TameAnimalTrigger)func_192118_a(new TameAnimalTrigger());
   public static final PlacedBlockTrigger field_193137_x = (PlacedBlockTrigger)func_192118_a(new PlacedBlockTrigger());
   public static final ConsumeItemTrigger field_193138_y = (ConsumeItemTrigger)func_192118_a(new ConsumeItemTrigger());
   public static final EffectsChangedTrigger field_193139_z = (EffectsChangedTrigger)func_192118_a(new EffectsChangedTrigger());
   public static final UsedTotemTrigger field_193130_A = (UsedTotemTrigger)func_192118_a(new UsedTotemTrigger());
   public static final NetherTravelTrigger field_193131_B = (NetherTravelTrigger)func_192118_a(new NetherTravelTrigger());
   public static final FishingRodHookedTrigger field_204811_D = (FishingRodHookedTrigger)func_192118_a(new FishingRodHookedTrigger());
   public static final ChanneledLightningTrigger field_204812_E = (ChanneledLightningTrigger)func_192118_a(new ChanneledLightningTrigger());

   private static <T extends ICriterionTrigger<?>> T func_192118_a(T var0) {
      if (field_192139_s.containsKey(var0.func_192163_a())) {
         throw new IllegalArgumentException("Duplicate criterion id " + var0.func_192163_a());
      } else {
         field_192139_s.put(var0.func_192163_a(), var0);
         return var0;
      }
   }

   @Nullable
   public static <T extends ICriterionInstance> ICriterionTrigger<T> func_192119_a(ResourceLocation var0) {
      return (ICriterionTrigger)field_192139_s.get(var0);
   }

   public static Iterable<? extends ICriterionTrigger<?>> func_192120_a() {
      return field_192139_s.values();
   }
}
