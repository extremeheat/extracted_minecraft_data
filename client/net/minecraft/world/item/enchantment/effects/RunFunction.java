package net.minecraft.world.item.enchantment.effects;

import com.mojang.logging.LogUtils;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Optional;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.functions.CommandFunction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.ServerFunctionManager;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.enchantment.EnchantedItemInUse;
import net.minecraft.world.phys.Vec3;
import org.slf4j.Logger;

public record RunFunction(ResourceLocation function) implements EnchantmentEntityEffect {
   private static final Logger LOGGER = LogUtils.getLogger();
   public static final MapCodec<RunFunction> CODEC = RecordCodecBuilder.mapCodec((var0) -> var0.group(ResourceLocation.CODEC.fieldOf("function").forGetter(RunFunction::function)).apply(var0, RunFunction::new));

   public RunFunction(ResourceLocation var1) {
      super();
      this.function = var1;
   }

   public void apply(ServerLevel var1, int var2, EnchantedItemInUse var3, Entity var4, Vec3 var5) {
      MinecraftServer var6 = var1.getServer();
      ServerFunctionManager var7 = var6.getFunctions();
      Optional var8 = var7.get(this.function);
      if (var8.isPresent()) {
         CommandSourceStack var9 = var6.createCommandSourceStack().withPermission(2).withSuppressedOutput().withEntity(var4).withLevel(var1).withPosition(var5).withRotation(var4.getRotationVector());
         var7.execute((CommandFunction)var8.get(), var9);
      } else {
         LOGGER.error("Enchantment run_function effect failed for non-existent function {}", this.function);
      }

   }

   public MapCodec<RunFunction> codec() {
      return CODEC;
   }
}
