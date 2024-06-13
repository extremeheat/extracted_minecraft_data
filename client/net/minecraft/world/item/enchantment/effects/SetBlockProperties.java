package net.minecraft.world.item.enchantment.effects;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.component.BlockItemStateProperties;
import net.minecraft.world.item.enchantment.EnchantedItemInUse;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

public record SetBlockProperties(BlockItemStateProperties properties, Vec3i offset) implements EnchantmentEntityEffect {
   public static final MapCodec<SetBlockProperties> CODEC = RecordCodecBuilder.mapCodec(
      var0 -> var0.group(
               BlockItemStateProperties.CODEC.fieldOf("properties").forGetter(SetBlockProperties::properties),
               Vec3i.CODEC.optionalFieldOf("offset", Vec3i.ZERO).forGetter(SetBlockProperties::offset)
            )
            .apply(var0, SetBlockProperties::new)
   );

   public SetBlockProperties(BlockItemStateProperties var1) {
      this(var1, Vec3i.ZERO);
   }

   public SetBlockProperties(BlockItemStateProperties properties, Vec3i offset) {
      super();
      this.properties = properties;
      this.offset = offset;
   }

   @Override
   public void apply(ServerLevel var1, int var2, EnchantedItemInUse var3, Entity var4, Vec3 var5) {
      BlockPos var6 = BlockPos.containing(var5).offset(this.offset);
      BlockState var7 = var4.level().getBlockState(var6);
      BlockState var8 = this.properties.apply(var7);
      if (!var7.equals(var8)) {
         var4.level().setBlock(var6, var8, 3);
      }
   }

   @Override
   public MapCodec<SetBlockProperties> codec() {
      return CODEC;
   }
}
