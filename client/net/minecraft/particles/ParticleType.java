package net.minecraft.particles;

import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.IRegistry;

public class ParticleType<T extends IParticleData> {
   private final ResourceLocation field_197579_c;
   private final boolean field_197581_e;
   private final IParticleData.IDeserializer<T> field_197582_f;

   protected ParticleType(ResourceLocation var1, boolean var2, IParticleData.IDeserializer<T> var3) {
      super();
      this.field_197579_c = var1;
      this.field_197581_e = var2;
      this.field_197582_f = var3;
   }

   public static void func_197576_c() {
      func_197572_a("ambient_entity_effect", false);
      func_197572_a("angry_villager", false);
      func_197572_a("barrier", false);
      func_197573_a("block", false, BlockParticleData.field_197585_a);
      func_197572_a("bubble", false);
      func_197572_a("cloud", false);
      func_197572_a("crit", false);
      func_197572_a("damage_indicator", true);
      func_197572_a("dragon_breath", false);
      func_197572_a("dripping_lava", false);
      func_197572_a("dripping_water", false);
      func_197573_a("dust", false, RedstoneParticleData.field_197565_b);
      func_197572_a("effect", false);
      func_197572_a("elder_guardian", true);
      func_197572_a("enchanted_hit", false);
      func_197572_a("enchant", false);
      func_197572_a("end_rod", false);
      func_197572_a("entity_effect", false);
      func_197572_a("explosion_emitter", true);
      func_197572_a("explosion", true);
      func_197573_a("falling_dust", false, BlockParticleData.field_197585_a);
      func_197572_a("firework", false);
      func_197572_a("fishing", false);
      func_197572_a("flame", false);
      func_197572_a("happy_villager", false);
      func_197572_a("heart", false);
      func_197572_a("instant_effect", false);
      func_197573_a("item", false, ItemParticleData.field_197557_a);
      func_197572_a("item_slime", false);
      func_197572_a("item_snowball", false);
      func_197572_a("large_smoke", false);
      func_197572_a("lava", false);
      func_197572_a("mycelium", false);
      func_197572_a("note", false);
      func_197572_a("poof", true);
      func_197572_a("portal", false);
      func_197572_a("rain", false);
      func_197572_a("smoke", false);
      func_197572_a("spit", true);
      func_197572_a("squid_ink", true);
      func_197572_a("sweep_attack", true);
      func_197572_a("totem_of_undying", false);
      func_197572_a("underwater", false);
      func_197572_a("splash", false);
      func_197572_a("witch", false);
      func_197572_a("bubble_pop", false);
      func_197572_a("current_down", false);
      func_197572_a("bubble_column_up", false);
      func_197572_a("nautilus", false);
      func_197572_a("dolphin", false);
   }

   public ResourceLocation func_197570_d() {
      return this.field_197579_c;
   }

   public boolean func_197575_f() {
      return this.field_197581_e;
   }

   public IParticleData.IDeserializer<T> func_197571_g() {
      return this.field_197582_f;
   }

   private static void func_197572_a(String var0, boolean var1) {
      IRegistry.field_212632_u.func_82595_a(new ResourceLocation(var0), new BasicParticleType(new ResourceLocation(var0), var1));
   }

   private static <T extends IParticleData> void func_197573_a(String var0, boolean var1, IParticleData.IDeserializer<T> var2) {
      IRegistry.field_212632_u.func_82595_a(new ResourceLocation(var0), new ParticleType(new ResourceLocation(var0), var1, var2));
   }
}
