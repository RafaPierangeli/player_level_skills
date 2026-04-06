package com.player_level_skills.mixin.entity;

import com.player_level_skills.access.LevelManagerAccess;
import com.player_level_skills.entity.LevelExperienceOrbEntity;
import com.player_level_skills.init.ConfigInit;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.boss.dragon.EnderDragonEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(EnderDragonEntity.class)
public abstract class EnderDragonEntityMixin extends MobEntity {

    @Unique
    @Nullable
    ServerPlayerEntity serverPlayerEntity = null;

    public EnderDragonEntityMixin(EntityType<? extends MobEntity> entityType, World world) {
        super(entityType, world);
    }

    @Inject(method = "updatePostDeath", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/ExperienceOrbEntity;spawn(Lnet/minecraft/server/world/ServerWorld;Lnet/minecraft/util/math/Vec3d;I)V", ordinal = 0), locals = LocalCapture.CAPTURE_FAILSOFT)
    protected void updatePostDeathMixin(CallbackInfo ci, int i, ServerWorld serverWorld, World var3) {
        if (ConfigInit.CONFIG.dragonXPMultiplier > 0.0F) {
            LevelExperienceOrbEntity.spawn((ServerWorld) this.getEntityWorld(), this.getEntityPos(),
                    MathHelper.floor((float) i * 0.08f * ConfigInit.CONFIG.dragonXPMultiplier
                            * (ConfigInit.CONFIG.dropXPbasedOnLvl && serverPlayerEntity != null
                            ? 1.0F + ConfigInit.CONFIG.basedOnMultiplier * ((LevelManagerAccess) serverPlayerEntity).getLevelManager().getOverallLevel()
                            : 1.0F)));
        }
    }

    @Inject(method = "updatePostDeath", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/ExperienceOrbEntity;spawn(Lnet/minecraft/server/world/ServerWorld;Lnet/minecraft/util/math/Vec3d;I)V", ordinal = 1), locals = LocalCapture.CAPTURE_FAILSOFT)
    protected void updatePostDeathXPMixin(CallbackInfo ci, int i, Vec3d vec3d, ServerWorld serverWorld2, World var4) {
        if (ConfigInit.CONFIG.dragonXPMultiplier > 0.0F) {
            LevelExperienceOrbEntity.spawn((ServerWorld) this.getEntityWorld(), this.getEntityPos(),
                    MathHelper.floor((float) i * 0.2f * ConfigInit.CONFIG.dragonXPMultiplier
                            * (ConfigInit.CONFIG.dropXPbasedOnLvl && serverPlayerEntity != null
                            ? 1.0F + ConfigInit.CONFIG.basedOnMultiplier * ((LevelManagerAccess) serverPlayerEntity).getLevelManager().getOverallLevel()
                            : 1.0F)));
        }
    }

    @Override
    public void onDeath(DamageSource source) {
        if (!this.getEntityWorld().isClient()) {
            if (source.getSource() instanceof ProjectileEntity projectileEntity) {
                if (projectileEntity.getOwner() instanceof ServerPlayerEntity serverPlayerEntity) {
                    this.serverPlayerEntity = serverPlayerEntity;
                }
            } else if (source.getSource() instanceof ServerPlayerEntity serverPlayerEntity) {
                this.serverPlayerEntity = serverPlayerEntity;
            }
        }
        super.onDeath(source);
    }
}

