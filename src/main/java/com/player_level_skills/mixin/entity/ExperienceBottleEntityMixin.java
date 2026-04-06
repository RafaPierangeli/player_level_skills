package com.player_level_skills.mixin.entity;

import com.player_level_skills.access.LevelManagerAccess;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.player_level_skills.entity.LevelExperienceOrbEntity;
import com.player_level_skills.init.ConfigInit;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.projectile.thrown.ExperienceBottleEntity;
import net.minecraft.entity.projectile.thrown.ThrownItemEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.hit.HitResult;
import net.minecraft.world.World;

@Mixin(ExperienceBottleEntity.class)
public abstract class ExperienceBottleEntityMixin extends ThrownItemEntity {

    public ExperienceBottleEntityMixin(EntityType<? extends ThrownItemEntity> entityType, World world) {
        super(entityType, world);
    }

    @Inject(method = "onCollision", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/projectile/thrown/ExperienceBottleEntity;discard()V"))
    protected void onCollisionMixin(HitResult hitResult, CallbackInfo ci) {
        if (ConfigInit.CONFIG.bottleXPMultiplier > 0.0F) {
            LevelExperienceOrbEntity.spawn((ServerWorld) this.getEntityWorld(), this.getEntityPos().add(0.0D, 0.5D, 0.0D),
                    (int) (5 * ConfigInit.CONFIG.bottleXPMultiplier
                            * (ConfigInit.CONFIG.dropXPbasedOnLvl && this.getOwner() != null && this.getOwner() instanceof ServerPlayerEntity serverPlayerEntity
                            ? 1.0F + ConfigInit.CONFIG.basedOnMultiplier * ((LevelManagerAccess) serverPlayerEntity).getLevelManager().getOverallLevel()
                            : 1.0F)));
        }
    }
}
