package com.player_level_skills.mixin.entity;

import com.player_level_skills.access.LevelManagerAccess;
import com.player_level_skills.access.MobEntityAccess;
import com.player_level_skills.access.PlayerDropAccess;
import com.player_level_skills.config.ConfigInit;
import com.player_level_skills.entity.LevelExperienceOrbEntity;
import com.player_level_skills.level.LevelManager;
import com.player_level_skills.util.BonusHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.damage.DamageTypes;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin extends Entity {

    @Shadow
    protected int playerHitTimer;

    @Shadow
    public abstract int getExperienceToDrop(ServerWorld world, @org.jspecify.annotations.Nullable Entity attacker);

    @Shadow
    public abstract @org.jspecify.annotations.Nullable PlayerEntity getAttackingPlayer();

    public LivingEntityMixin(EntityType<?> type, World world) {
        super(type, world);
    }

    @ModifyVariable(
            method = "modifyAppliedDamage",
            at = @At(
                    value = "INVOKE_ASSIGN",
                    target = "Lnet/minecraft/enchantment/EnchantmentHelper;getProtectionAmount(Lnet/minecraft/server/world/ServerWorld;Lnet/minecraft/entity/LivingEntity;Lnet/minecraft/entity/damage/DamageSource;)F",
                    shift = At.Shift.AFTER
            ),
            ordinal = 1
    )
    private float modifyAppliedDamageMixin(float original, DamageSource source, float amount) {
        if (source.isOf(DamageTypes.FALL) && (Object) this instanceof PlayerEntity playerEntity) {
            return original + BonusHelper.fallDamageReductionBonus(playerEntity);
        } else {
            return original;
        }
    }

    @ModifyVariable(
            method = "tryUseDeathProtector",
            at = @At(
                    value = "INVOKE_ASSIGN",
                    target = "Lnet/minecraft/entity/LivingEntity;getStackInHand(Lnet/minecraft/util/Hand;)Lnet/minecraft/item/ItemStack;",
                    ordinal = 0
            )
    )
    private ItemStack tryUseTotemMixin(ItemStack original) {
        if ((Object) this instanceof PlayerEntity playerEntity && original.isOf(Items.TOTEM_OF_UNDYING)) {
            if (playerEntity.isCreative()) {
                return original;
            }

            LevelManager levelManager = ((LevelManagerAccess) playerEntity).getLevelManager();
            if (!levelManager.hasRequiredItemLevel(original.getItem())) {
                return ItemStack.EMPTY;
            }
        }

        return original;
    }

    @Inject(
            method = "tryUseDeathProtector",
            at = @At(
                    value = "INVOKE_ASSIGN",
                    target = "Lnet/minecraft/util/Hand;values()[Lnet/minecraft/util/Hand;"
            ),
            cancellable = true
    )
    private void tryUseTotemMixin(DamageSource source, CallbackInfoReturnable<Boolean> info) {
        if ((Object) this instanceof PlayerEntity playerEntity && BonusHelper.deathGraceChanceBonus(playerEntity)) {
            info.setReturnValue(true);
        }
    }

    @Inject(method = "drop", at = @At("HEAD"), cancellable = true)
    protected void dropMixin(ServerWorld world, DamageSource damageSource, CallbackInfo info) {
        if (!((Object) this instanceof PlayerEntity) && this.playerHitTimer > 0 && ConfigInit.CONFIG.disableMobFarms) {
            PlayerEntity attacker = this.getAttackingPlayer();
            if (attacker != null && !((PlayerDropAccess) attacker).allowMobDrop()) {
                info.cancel();
            }
        }
    }

    @Inject(
            method = "onDeath",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/entity/LivingEntity;drop(Lnet/minecraft/server/world/ServerWorld;Lnet/minecraft/entity/damage/DamageSource;)V"
            )
    )
    private void onDeathMixin(DamageSource source, CallbackInfo info) {
        PlayerEntity attacker = this.getAttackingPlayer();
        if (attacker != null && this.playerHitTimer > 0 && ConfigInit.CONFIG.disableMobFarms) {
            ((PlayerDropAccess) attacker).increaseKilledMobStat(this.getEntityWorld().getChunk(this.getBlockPos()));
        }
    }

    @Inject(method = "dropExperience", at = @At("TAIL"))
    protected void dropXpMixin(ServerWorld world, @Nullable Entity attacker, CallbackInfo info) {
        if (ConfigInit.CONFIG.mobXPMultiplier <= 0.0F) {
            return;
        }

        if ((Object) this instanceof MobEntity mobEntity && !ConfigInit.CONFIG.spawnerMobXP && ((MobEntityAccess) mobEntity).isSpawnerMob()) {
            return;
        }

        int baseXp = this.getExperienceToDrop(world, attacker);

        float customXp = baseXp * ConfigInit.CONFIG.mobXPMultiplier;

        if (ConfigInit.CONFIG.dropXPbasedOnLvl && attacker instanceof PlayerEntity playerEntity) {
            customXp *= 1.0F + ConfigInit.CONFIG.basedOnMultiplier
                    * ((LevelManagerAccess) playerEntity).getLevelManager().getOverallLevel();
        }

        int finalXp = Math.max(1, Math.round(customXp));

        LevelExperienceOrbEntity.spawn((ServerWorld) this.getEntityWorld(), this.getEntityPos(), finalXp);
    }
}