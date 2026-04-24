package com.player_level_skills.item;

import net.minecraft.client.sound.Sound;
import net.minecraft.component.type.ConsumableComponent;
import net.minecraft.component.type.ConsumableComponents;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.component.type.FoodComponent;
import net.minecraft.item.consume.ApplyEffectsConsumeEffect;
import net.minecraft.item.consume.UseAction;
import net.minecraft.sound.SoundEvents;

public class ModFoodComponent {

    public static final FoodComponent RARE_CANDY = new FoodComponent.Builder().nutrition(1).saturationModifier(0.0f).build();

    public static final ConsumableComponent RARE_CANDY_EFFECT = ConsumableComponents.food()
            .consumeEffect(new ApplyEffectsConsumeEffect(new StatusEffectInstance(StatusEffects.HEALTH_BOOST, 200), 0.95f)).build();

    public static final FoodComponent STRANGE_POTION = new FoodComponent.Builder().nutrition(0).saturationModifier(0.0f).alwaysEdible().build();

    public static final ConsumableComponent STRANGE_POTION_EFFECT = ConsumableComponents.drink()
            .consumeSeconds(4.0f)
            .useAction(UseAction.DRINK)
            .sound(SoundEvents.ENTITY_GENERIC_DRINK)
            .consumeParticles(false)
            .consumeEffect(new ApplyEffectsConsumeEffect(new StatusEffectInstance(StatusEffects.ABSORPTION, 200,0), 1.0f)).build();
}
