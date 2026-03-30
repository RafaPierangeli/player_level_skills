package com.player_level_skills.mixin.block;

import com.player_level_skills.access.LevelManagerAccess;
import com.player_level_skills.config.ConfigInit;
import com.player_level_skills.entity.LevelExperienceOrbEntity;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import net.minecraft.block.entity.AbstractFurnaceBlockEntity;
import net.minecraft.recipe.AbstractCookingRecipe;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeEntry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;


import java.util.List;

@Mixin(AbstractFurnaceBlockEntity.class)
public abstract class AbstractFurnaceBlockEntityMixin {

    @Unique
    @Nullable
    private ServerPlayerEntity serverPlayerEntity = null;

    @Inject(method = "dropExperienceForRecipesUsed", at = @At("HEAD"))
    private void dropExperienceForRecipesUsedMixin(ServerPlayerEntity player, CallbackInfo info) {
        this.serverPlayerEntity = player;
    }

    @Inject(method = "getRecipesUsedAndDropExperience", at = @At("RETURN"))
    private void getRecipesUsedAndDropExperienceMixin(
            ServerWorld world, Vec3d pos, org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable<List<RecipeEntry<?>>> cir
    ) {
        if (ConfigInit.CONFIG.furnaceXPMultiplier <= 0.0F) {
            return;
        }

        List<RecipeEntry<?>> recipes = cir.getReturnValue();
        if (recipes == null || recipes.isEmpty()) {
            return;
        }

        for (RecipeEntry<?> recipeEntry : recipes) {
            if (!(recipeEntry.value() instanceof AbstractCookingRecipe cookingRecipe)) {
                continue;
            }

            float customXp = cookingRecipe.getExperience();
            if (customXp <= 0.0F) {
                continue;
            }

            float multiplier = ConfigInit.CONFIG.furnaceXPMultiplier;

            if (ConfigInit.CONFIG.dropXPbasedOnLvl && serverPlayerEntity != null) {
                multiplier *= 1.0F + ConfigInit.CONFIG.basedOnMultiplier
                        * ((LevelManagerAccess) serverPlayerEntity).getLevelManager().getOverallLevel();
            }

            int finalXp = Math.max(1, Math.round(customXp * multiplier));

            LevelExperienceOrbEntity.spawn(world, pos, finalXp);
        }
    }
}