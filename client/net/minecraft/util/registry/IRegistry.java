package net.minecraft.util.registry;

import java.util.Iterator;
import java.util.Random;
import java.util.Set;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.item.PaintingType;
import net.minecraft.fluid.Fluid;
import net.minecraft.item.Item;
import net.minecraft.particles.IParticleData;
import net.minecraft.particles.ParticleType;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionType;
import net.minecraft.stats.StatType;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.IObjectIntIterable;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SharedConstants;
import net.minecraft.util.SoundEvent;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.provider.BiomeProviderType;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.gen.ChunkGeneratorType;
import org.apache.commons.lang3.Validate;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public interface IRegistry<T> extends IObjectIntIterable<T> {
   Logger field_212616_e = LogManager.getLogger();
   IRegistry<IRegistry<?>> field_212617_f = new RegistryNamespaced();
   IRegistry<Block> field_212618_g = func_212610_a("block", new RegistryNamespacedDefaultedByKey(new ResourceLocation("air")));
   IRegistry<Fluid> field_212619_h = func_212610_a("fluid", new RegistryNamespacedDefaultedByKey(new ResourceLocation("empty")));
   IRegistry<PaintingType> field_212620_i = func_212610_a("motive", new RegistryNamespacedDefaultedByKey(new ResourceLocation("kebab")));
   IRegistry<PotionType> field_212621_j = func_212610_a("potion", new RegistryNamespacedDefaultedByKey(new ResourceLocation("empty")));
   IRegistry<DimensionType> field_212622_k = func_212610_a("dimension_type", new RegistryNamespaced());
   IRegistry<ResourceLocation> field_212623_l = func_212610_a("custom_stat", new RegistryNamespaced());
   IRegistry<Biome> field_212624_m = func_212610_a("biome", new RegistryNamespaced());
   IRegistry<BiomeProviderType<?, ?>> field_212625_n = func_212610_a("biome_source_type", new RegistryNamespaced());
   IRegistry<TileEntityType<?>> field_212626_o = func_212610_a("block_entity_type", new RegistryNamespaced());
   IRegistry<ChunkGeneratorType<?, ?>> field_212627_p = func_212610_a("chunk_generator_type", new RegistryNamespaced());
   IRegistry<Enchantment> field_212628_q = func_212610_a("enchantment", new RegistryNamespaced());
   IRegistry<EntityType<?>> field_212629_r = func_212610_a("entity_type", new RegistryNamespaced());
   IRegistry<Item> field_212630_s = func_212610_a("item", new RegistryNamespaced());
   IRegistry<Potion> field_212631_t = func_212610_a("mob_effect", new RegistryNamespaced());
   IRegistry<ParticleType<? extends IParticleData>> field_212632_u = func_212610_a("particle_type", new RegistryNamespaced());
   IRegistry<SoundEvent> field_212633_v = func_212610_a("sound_event", new RegistryNamespaced());
   IRegistry<StatType<?>> field_212634_w = func_212610_a("stats", new RegistryNamespaced());

   static <T> IRegistry<T> func_212610_a(String var0, IRegistry<T> var1) {
      field_212617_f.func_82595_a(new ResourceLocation(var0), var1);
      return var1;
   }

   static void func_212613_e() {
      field_212617_f.forEach((var0) -> {
         if (var0.func_195866_d()) {
            field_212616_e.error("Registry '{}' was empty after loading", field_212617_f.func_177774_c(var0));
            if (SharedConstants.field_206244_b) {
               throw new IllegalStateException("Registry: '" + field_212617_f.func_177774_c(var0) + "' is empty, not allowed, fix me!");
            }
         }

         if (var0 instanceof RegistryNamespacedDefaultedByKey) {
            ResourceLocation var1 = var0.func_212609_b();
            Validate.notNull(var0.func_212608_b(var1), "Missing default of DefaultedMappedRegistry: " + var1, new Object[0]);
         }

      });
   }

   @Nullable
   ResourceLocation func_177774_c(T var1);

   T func_82594_a(@Nullable ResourceLocation var1);

   ResourceLocation func_212609_b();

   int func_148757_b(@Nullable T var1);

   @Nullable
   T func_148754_a(int var1);

   Iterator<T> iterator();

   @Nullable
   T func_212608_b(@Nullable ResourceLocation var1);

   void func_177775_a(int var1, ResourceLocation var2, T var3);

   void func_82595_a(ResourceLocation var1, T var2);

   Set<ResourceLocation> func_148742_b();

   boolean func_195866_d();

   @Nullable
   T func_186801_a(Random var1);

   default Stream<T> func_201756_e() {
      return StreamSupport.stream(this.spliterator(), false);
   }

   boolean func_212607_c(ResourceLocation var1);
}
