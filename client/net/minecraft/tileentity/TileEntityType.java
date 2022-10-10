package net.minecraft.tileentity;

import com.mojang.datafixers.DataFixUtils;
import com.mojang.datafixers.types.Type;
import java.util.function.Supplier;
import javax.annotation.Nullable;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SharedConstants;
import net.minecraft.util.datafix.DataFixesManager;
import net.minecraft.util.datafix.TypeReferences;
import net.minecraft.util.registry.IRegistry;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class TileEntityType<T extends TileEntity> {
   private static final Logger field_206866_A = LogManager.getLogger();
   public static final TileEntityType<TileEntityFurnace> field_200971_b = func_200966_a("furnace", TileEntityType.Builder.func_200963_a(TileEntityFurnace::new));
   public static final TileEntityType<TileEntityChest> field_200972_c = func_200966_a("chest", TileEntityType.Builder.func_200963_a(TileEntityChest::new));
   public static final TileEntityType<TileEntityTrappedChest> field_200973_d = func_200966_a("trapped_chest", TileEntityType.Builder.func_200963_a(TileEntityTrappedChest::new));
   public static final TileEntityType<TileEntityEnderChest> field_200974_e = func_200966_a("ender_chest", TileEntityType.Builder.func_200963_a(TileEntityEnderChest::new));
   public static final TileEntityType<TileEntityJukebox> field_200975_f = func_200966_a("jukebox", TileEntityType.Builder.func_200963_a(TileEntityJukebox::new));
   public static final TileEntityType<TileEntityDispenser> field_200976_g = func_200966_a("dispenser", TileEntityType.Builder.func_200963_a(TileEntityDispenser::new));
   public static final TileEntityType<TileEntityDropper> field_200977_h = func_200966_a("dropper", TileEntityType.Builder.func_200963_a(TileEntityDropper::new));
   public static final TileEntityType<TileEntitySign> field_200978_i = func_200966_a("sign", TileEntityType.Builder.func_200963_a(TileEntitySign::new));
   public static final TileEntityType<TileEntityMobSpawner> field_200979_j = func_200966_a("mob_spawner", TileEntityType.Builder.func_200963_a(TileEntityMobSpawner::new));
   public static final TileEntityType<TileEntityPiston> field_200980_k = func_200966_a("piston", TileEntityType.Builder.func_200963_a(TileEntityPiston::new));
   public static final TileEntityType<TileEntityBrewingStand> field_200981_l = func_200966_a("brewing_stand", TileEntityType.Builder.func_200963_a(TileEntityBrewingStand::new));
   public static final TileEntityType<TileEntityEnchantmentTable> field_200982_m = func_200966_a("enchanting_table", TileEntityType.Builder.func_200963_a(TileEntityEnchantmentTable::new));
   public static final TileEntityType<TileEntityEndPortal> field_200983_n = func_200966_a("end_portal", TileEntityType.Builder.func_200963_a(TileEntityEndPortal::new));
   public static final TileEntityType<TileEntityBeacon> field_200984_o = func_200966_a("beacon", TileEntityType.Builder.func_200963_a(TileEntityBeacon::new));
   public static final TileEntityType<TileEntitySkull> field_200985_p = func_200966_a("skull", TileEntityType.Builder.func_200963_a(TileEntitySkull::new));
   public static final TileEntityType<TileEntityDaylightDetector> field_200986_q = func_200966_a("daylight_detector", TileEntityType.Builder.func_200963_a(TileEntityDaylightDetector::new));
   public static final TileEntityType<TileEntityHopper> field_200987_r = func_200966_a("hopper", TileEntityType.Builder.func_200963_a(TileEntityHopper::new));
   public static final TileEntityType<TileEntityComparator> field_200988_s = func_200966_a("comparator", TileEntityType.Builder.func_200963_a(TileEntityComparator::new));
   public static final TileEntityType<TileEntityBanner> field_200989_t = func_200966_a("banner", TileEntityType.Builder.func_200963_a(TileEntityBanner::new));
   public static final TileEntityType<TileEntityStructure> field_200990_u = func_200966_a("structure_block", TileEntityType.Builder.func_200963_a(TileEntityStructure::new));
   public static final TileEntityType<TileEntityEndGateway> field_200991_v = func_200966_a("end_gateway", TileEntityType.Builder.func_200963_a(TileEntityEndGateway::new));
   public static final TileEntityType<TileEntityCommandBlock> field_200992_w = func_200966_a("command_block", TileEntityType.Builder.func_200963_a(TileEntityCommandBlock::new));
   public static final TileEntityType<TileEntityShulkerBox> field_200993_x = func_200966_a("shulker_box", TileEntityType.Builder.func_200963_a(TileEntityShulkerBox::new));
   public static final TileEntityType<TileEntityBed> field_200994_y = func_200966_a("bed", TileEntityType.Builder.func_200963_a(TileEntityBed::new));
   public static final TileEntityType<TileEntityConduit> field_205166_z = func_200966_a("conduit", TileEntityType.Builder.func_200963_a(TileEntityConduit::new));
   private final Supplier<? extends T> field_200995_z;
   private final Type<?> field_206867_C;

   @Nullable
   public static ResourceLocation func_200969_a(TileEntityType<?> var0) {
      return IRegistry.field_212626_o.func_177774_c(var0);
   }

   public static <T extends TileEntity> TileEntityType<T> func_200966_a(String var0, TileEntityType.Builder<T> var1) {
      Type var2 = null;

      try {
         var2 = DataFixesManager.func_210901_a().getSchema(DataFixUtils.makeKey(1631)).getChoiceType(TypeReferences.field_211294_j, var0);
      } catch (IllegalStateException var4) {
         if (SharedConstants.field_206244_b) {
            throw var4;
         }

         field_206866_A.warn("No data fixer registered for block entity {}", var0);
      }

      TileEntityType var3 = var1.func_206865_a(var2);
      IRegistry.field_212626_o.func_82595_a(new ResourceLocation(var0), var3);
      return var3;
   }

   public static void func_212641_a() {
   }

   public TileEntityType(Supplier<? extends T> var1, Type<?> var2) {
      super();
      this.field_200995_z = var1;
      this.field_206867_C = var2;
   }

   @Nullable
   public T func_200968_a() {
      return (TileEntity)this.field_200995_z.get();
   }

   @Nullable
   static TileEntity func_200967_a(String var0) {
      TileEntityType var1 = (TileEntityType)IRegistry.field_212626_o.func_212608_b(new ResourceLocation(var0));
      return var1 == null ? null : var1.func_200968_a();
   }

   public static final class Builder<T extends TileEntity> {
      private final Supplier<? extends T> field_200965_a;

      private Builder(Supplier<? extends T> var1) {
         super();
         this.field_200965_a = var1;
      }

      public static <T extends TileEntity> TileEntityType.Builder<T> func_200963_a(Supplier<? extends T> var0) {
         return new TileEntityType.Builder(var0);
      }

      public TileEntityType<T> func_206865_a(Type<?> var1) {
         return new TileEntityType(this.field_200965_a, var1);
      }
   }
}
