package com.player_level_skills.mixin.entity;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.player_level_skills.util.BonusHelper;
import net.minecraft.entity.*;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.World;
import net.minecraft.world.explosion.ExplosionBehavior;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(TntEntity.class)
public abstract class TntEntityMixin extends Entity {

    @Shadow
    @Nullable
    private LazyEntityReference<LivingEntity> causingEntity;

    public TntEntityMixin(EntityType<?> type, World world) {
        super(type, world);
    }

    @WrapOperation(method = "explode", at = @At(value = "INVOKE", target =
            "Lnet/minecraft/world/World;createExplosion(Lnet/minecraft/entity/Entity;Lnet/minecraft/entity/damage/DamageSource;Lnet/minecraft/world/explosion/ExplosionBehavior;DDDFZLnet/minecraft/world/World$ExplosionSourceType;)V"
    ))
    private void explosionMixin(World instance, Entity entity, DamageSource damageSource, ExplosionBehavior behavior, double x, double y, double z, float power, boolean createFire, World.ExplosionSourceType explosionSourceType, Operation<Void> original) {

        LivingEntity causing = LazyEntityReference.getLivingEntity(causingEntity, this.getEntityWorld());

        if (causing instanceof PlayerEntity playerEntity) {
            power += BonusHelper.tntStrengthBonus(playerEntity);
        }
        original.call(instance, entity, damageSource, behavior, x, y, z, power, createFire, explosionSourceType);
    }

}
