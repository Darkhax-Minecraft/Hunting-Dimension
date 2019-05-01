package net.darkhax.huntingdim.handler;

import java.awt.Color;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

import org.apache.commons.io.FileUtils;

import net.darkhax.bookshelf.lib.MCColor;
import net.darkhax.huntingdim.HuntingDimension;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.common.config.Configuration;

public class ConfigurationHandler {

    public static File directory = new File("config/huntingdim/");
    public static File configFile = new File(directory, "huntingdim.cfg");
    public static File worldPreset = new File(directory, "worldgen.json");

    public static Configuration config;

    public static String generatorPreset = "";

    public static int dimensionId = 28885;
    public static boolean quickPotionWearOff = true;
    public static double lootingChance = 0.25;
    public static double expChance = 0.2;
    public static double expMultiplier = 2.5f;

    public static int buffArmor = 0;
    public static float buffHealth = 0;
    public static float buffAttack = 0;

    public static boolean mimicSurfaceWorld = false;
    public static String worldType = "default";
    public static String defaultBiome = "minecraft:plains";

    public static boolean allowHostileInOverworld = true;
    public static boolean allowPeacefulInHunting = false;

    public static boolean allowRespawn = false;

    public static int chanceSound = 100;
    public static int chanceSpawn = 2000;

    public static MCColor defaultColor = new MCColor(59, 162, 73);
    public static int defaultColorPacked = defaultColor.getRGB();
    public static Vec3d defaultColorVector = new Vec3d(defaultColor.getRedF(), defaultColor.getGreenF(), defaultColor.getBlueF());

    public static int returnDimension = 0;

    public static boolean isVoidWorld = false;

    public ConfigurationHandler () {

        if (!directory.exists()) {

            HuntingDimension.LOG.info("Generating config folder");
            directory.mkdirs();
        }

        config = new Configuration(configFile);
        this.generatePresets();
        this.syncConfigData();
    }

    private void generatePresets () {

        final File preset = new File(directory, "world_generator_settings.json");

        if (!preset.exists()) {

            HuntingDimension.LOG.info("World generator settings does not exist. Generating a new one.");

            try {

                FileUtils.copyURLToFile(HuntingDimension.class.getResource("/assets/huntingdim/presets/hunting_dimension_generator_settings.json"), preset);
                HuntingDimension.LOG.info("Finished generating world generator settings.");
            }

            catch (final IOException e) {

                HuntingDimension.LOG.warn(e, "Could not generate world generator settings!");
            }
        }

        HuntingDimension.LOG.info("Reading world generator settings.");

        try {

            generatorPreset = FileUtils.readFileToString(preset, StandardCharsets.UTF_8);
            HuntingDimension.LOG.info("World settings loaded: " + generatorPreset.replaceAll("\\R", "").replaceAll("\\s", " "));
        }

        catch (final IOException e) {

            HuntingDimension.LOG.warn(e, "Could not read world generator settings! Default will be used!");
        }
    }

    private void syncConfigData () {

        HuntingDimension.LOG.info("Reading config file.");
        dimensionId = config.getInt("dimensionId", Configuration.CATEGORY_GENERAL, 28885, Integer.MIN_VALUE, Integer.MAX_VALUE, "The id for the hunting dimension.");
        quickPotionWearOff = config.getBoolean("quickPotionWearOff", Configuration.CATEGORY_GENERAL, true, "While true, beneficial potion effects on players will wear off twice as fast.");
        lootingChance = config.getFloat("lootingChance", Configuration.CATEGORY_GENERAL, 0.25f, 0f, 1f, "Whenever a mob dies in the hunting dimension, there is a chance that it will have +1 levels of looting applied to it's drop. Set to 0 to disable.");
        expChance = config.getFloat("expChance", Configuration.CATEGORY_GENERAL, 0.2f, 0f, 1f, "Whenever a mob dies in the hunting dimension, there is a chance that it will drop additional exp.");
        expMultiplier = config.getFloat("expModifier", Configuration.CATEGORY_GENERAL, 2.5f, 0f, 128f, "When a mob dies, it can drop additional exp. This changes how much more exp it will drop. Default is 2.5x.");

        buffArmor = config.getInt("buffArmor", Configuration.CATEGORY_GENERAL, 4, 0, 30, "The amount of additional armor to give mobs that spawn in the hunting dimension.");
        buffHealth = config.getFloat("buffHealth", Configuration.CATEGORY_GENERAL, 0.3f, 0, 1000f, "The percentage of additional health to give mobs that spawn in the hunting dimension.");
        buffAttack = config.getFloat("buffAttack", Configuration.CATEGORY_GENERAL, 0.3f, 0, 1000f, "The percentage of additional attack damage to give mobs that spawn in the hunting dimension.");

        mimicSurfaceWorld = config.getBoolean("mimicSurfaceWorld", Configuration.CATEGORY_GENERAL, false, "If true, the mining dimension will use the same world generator as the surface world. For example, if the surface is flat so will the dimension.");
        worldType = config.getString("worldType", Configuration.CATEGORY_GENERAL, "default", "The type of world to use for the hunting dimension. Vanilla values include default, flat, largeBiomes, amplified, customized, debug_all_block_states, and default_1_1 Keep in mind that this will be ignored if you use mimicSurfaceWorld!");
        
        if (worldType == null) {
            
            HuntingDimension.LOG.error("worldType was configured to null, this is not allowed. Please resolve this in your config.");
            worldType = "default";
        }
        
        defaultBiome = config.getString("initialBiome", Configuration.CATEGORY_GENERAL, "minecraft:plains", "The biome to use when generating the hunting dimension. If an invalid id is used, plains will be defaulted.");

        allowHostileInOverworld = config.getBoolean("mobsInSurface", Configuration.CATEGORY_GENERAL, true, "Should hostile mobs be allowed to spawn in the overworld?");
        allowPeacefulInHunting = config.getBoolean("allowPeacefulMobs", Configuration.CATEGORY_GENERAL, false, "Should peaceful mobs be allowed to spawn in the hunting dimension?");

        allowRespawn = config.getBoolean("allowRespawn", Configuration.CATEGORY_GENERAL, false, "Should players respawn inside of the hunting dimension?");

        chanceSound = config.getInt("chanceSound", Configuration.CATEGORY_GENERAL, 100, 0, 10000, "The chance that the portal will play a sound. Default is a 1 in 100 chance.");
        chanceSpawn = config.getInt("chanceSpawn", Configuration.CATEGORY_GENERAL, 2000, 0, 10000, "The chance that the portal will spawn a mob. Peaceful, easy, normal and hard have a 0, 1, 2, and 3 in X chance of spawning a mob, where X is the configured value.");

        returnDimension = config.getInt("returnDimension", Configuration.CATEGORY_GENERAL, 0, Integer.MIN_VALUE, Integer.MAX_VALUE, "The dimension to go to when you return from the hunting dimension.");

        defaultColor = this.getColor("defaultColor", "colors", new Color(59, 162, 73));
        defaultColorPacked = defaultColor.getRGB();
        defaultColorVector = new Vec3d(defaultColor.getRedF(), defaultColor.getGreenF(), defaultColor.getBlueF());

        isVoidWorld = config.getBoolean("useVoidWorld", Configuration.CATEGORY_GENERAL, false, "Whether or not the hunting dimension should be an empty void world.");
        if (config.hasChanged()) {

            HuntingDimension.LOG.info("Saving config file.");
            config.save();
        }
    }

    private MCColor getColor (String type, String category, Color initial) {

        final int red = config.getInt(type + "Red", category, initial.getRed(), 0, 255, "The red color value for " + type);
        final int green = config.getInt(type + "Green", category, initial.getGreen(), 0, 255, "The green color value for " + type);
        final int blue = config.getInt(type + "Blue", category, initial.getBlue(), 0, 255, "The blue color value for " + type);

        return new MCColor(red, green, blue);
    }
}