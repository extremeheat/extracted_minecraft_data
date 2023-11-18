package net.minecraft.advancements.critereon;

import com.google.gson.JsonArray;
import com.google.gson.JsonParseException;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.JsonOps;
import java.util.List;
import net.minecraft.Util;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.storage.loot.LootDataManager;
import net.minecraft.world.level.storage.loot.ValidationContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSet;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemConditions;
import org.slf4j.Logger;

public class DeserializationContext {
   private static final Logger LOGGER = LogUtils.getLogger();
   private final ResourceLocation id;
   private final LootDataManager lootData;

   public DeserializationContext(ResourceLocation var1, LootDataManager var2) {
      super();
      this.id = var1;
      this.lootData = var2;
   }

   public final List<LootItemCondition> deserializeConditions(JsonArray var1, String var2, LootContextParamSet var3) {
      List var4 = Util.getOrThrow(LootItemConditions.CODEC.listOf().parse(JsonOps.INSTANCE, var1), JsonParseException::new);
      ValidationContext var5 = new ValidationContext(var3, this.lootData);

      for(LootItemCondition var7 : var4) {
         var7.validate(var5);
         var5.getProblems()
            .forEach((var1x, var2x) -> LOGGER.warn("Found validation problem in advancement trigger {}/{}: {}", new Object[]{var2, var1x, var2x}));
      }

      return var4;
   }

   public ResourceLocation getAdvancementId() {
      return this.id;
   }
}
