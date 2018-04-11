package net.darkhax.huntingdim.handler;

import java.util.UUID;

import net.darkhax.bookshelf.data.AttributeOperation;
import net.darkhax.bookshelf.util.MathsUtils;
import net.darkhax.bookshelf.util.PotionUtils;
import net.darkhax.bookshelf.util.WorldUtils;
import net.darkhax.huntingdim.HuntingDimension;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.IAttribute;
import net.minecraft.entity.ai.attributes.ModifiableAttributeInstance;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.potion.PotionEffect;
import net.minecraft.world.DimensionType;
import net.minecraftforge.event.AnvilUpdateEvent;
import net.minecraftforge.event.entity.living.LivingEvent.LivingUpdateEvent;
import net.minecraftforge.event.entity.living.LivingExperienceDropEvent;
import net.minecraftforge.event.entity.living.LivingSpawnEvent.CheckSpawn;
import net.minecraftforge.event.entity.living.LootingLevelEvent;
import net.minecraftforge.event.entity.player.AnvilRepairEvent;
import net.minecraftforge.fml.common.eventhandler.Event.Result;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class DimensionEffectHandler {

    public static final AttributeModifier BUFF_ARMOR = new AttributeModifier(UUID.fromString("054ab076-0a2e-4ea4-b525-b201fcf4a2a7"), "buff_hunting_armor", ConfigurationHandler.buffArmor, AttributeOperation.ADDITIVE.ordinal());
    public static final AttributeModifier BUFF_HEALTH = new AttributeModifier(UUID.fromString("d52efdc2-53fc-42ab-80ed-9fe79d219ff7"), "buff_hunting_health", ConfigurationHandler.buffHealth, AttributeOperation.MULTIPLY.ordinal());
    public static final AttributeModifier BUFF_ATTACK = new AttributeModifier(UUID.fromString("d86f9cdc-8a12-492a-a0ad-e8cdded32ab7"), "buff_hunting_attack", ConfigurationHandler.buffAttack, AttributeOperation.MULTIPLY.ordinal());

    @SubscribeEvent
    public void onAnvilUpdate (AnvilUpdateEvent event) {

        System.out.println("Update: " + event.getCost());
    }

    @SubscribeEvent
    public void onAnvilRepair (AnvilRepairEvent event) {

    }

    @SubscribeEvent
    public void onExpCalculated (LivingExperienceDropEvent event) {

        // Check if entity is in the hunting dimension, and run a % chance
        if (event.getEntityLiving() != null && event.getEntityLiving().dimension == ConfigurationHandler.dimensionId && MathsUtils.tryPercentage(ConfigurationHandler.expChance)) {

            // Increase experience points based on a modifier.
            final int additional = (int) (event.getOriginalExperience() * ConfigurationHandler.expMultiplier / event.getOriginalExperience());
            event.setDroppedExperience(additional);
        }
    }

    @SubscribeEvent
    public void onLootingCalculated (LootingLevelEvent event) {

        // Check if entity is in the hunting dimension, and run a % chance
        if (event.getEntityLiving() != null && event.getEntityLiving().dimension == ConfigurationHandler.dimensionId && MathsUtils.tryPercentage(ConfigurationHandler.lootingChance)) {

            // Increase looting level by one.
            event.setLootingLevel(event.getLootingLevel() + 1);
        }
    }

    @SubscribeEvent
    public void onEntityJoinWorld (LivingUpdateEvent event) {

        if (event.getEntityLiving() instanceof IMob && event.getEntityLiving().dimension == ConfigurationHandler.dimensionId) {

            final EntityLivingBase entity = event.getEntityLiving();

            this.applyModifier(entity, BUFF_ARMOR, SharedMonsterAttributes.ARMOR);
            this.applyModifier(entity, BUFF_HEALTH, SharedMonsterAttributes.MAX_HEALTH);
            this.applyModifier(entity, BUFF_ATTACK, SharedMonsterAttributes.ATTACK_DAMAGE);
        }
    }

    private void applyModifier (EntityLivingBase entity, AttributeModifier modifier, IAttribute attribute) {

        if (modifier.getAmount() > 0) {

            final ModifiableAttributeInstance attrInst = (ModifiableAttributeInstance) entity.getEntityAttribute(attribute);

            if (attrInst != null && !attrInst.hasModifier(modifier)) {

                attrInst.applyModifier(modifier);

                if (attribute == SharedMonsterAttributes.MAX_HEALTH) {

                    entity.heal(entity.getMaxHealth());
                }
            }
        }
    }

    @SubscribeEvent
    public void onSpawnCheck (CheckSpawn event) {

        // Mob spawners will override these conditions.
        if (event.isSpawner()) {

            return;
        }

        // Check if peaceful mobs should spawn in hostile world.
        if (!ConfigurationHandler.allowPeacefulInHunting && event.getEntityLiving() instanceof EntityAnimal && WorldUtils.isDimension(event.getWorld(), HuntingDimension.dimensionType)) {

            event.setResult(Result.DENY);
        }

        else if (!ConfigurationHandler.allowHostileInOverworld && event.getEntityLiving() instanceof IMob && WorldUtils.isDimension(event.getWorld(), DimensionType.OVERWORLD)) {

            event.setResult(Result.DENY);
        }
    }

    @SubscribeEvent
    public void onLivingUpdate (LivingUpdateEvent event) {

        // If entity is a player, and in the hunting dimension, and quick potion wear off is
        // enabled
        if (event.getEntityLiving() instanceof EntityPlayer && !((EntityPlayer) event.getEntityLiving()).capabilities.isCreativeMode && ConfigurationHandler.quickPotionWearOff && event.getEntityLiving().dimension == ConfigurationHandler.dimensionId) {

            for (final PotionEffect effect : event.getEntityLiving().getActivePotionEffects()) {

                // If the effect is positive and not ambient (beacons)
                if (PotionUtils.isBeneficial(effect.getPotion()) && !effect.getIsAmbient()) {

                    // Lower the duration by a second tick.
                    PotionUtils.deincrementDuration(effect);
                }
            }
        }
    }
}